package com.counselor.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.counselor.common.constant.CommonConstant;
import com.counselor.common.exception.BusinessException;
import com.counselor.common.result.ResultCode;
import com.counselor.service.entity.Counselor;
import com.counselor.service.mapper.CounselorMapper;
import com.counselor.service.query.CounselorQuery;
import com.counselor.service.service.CounselorService;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CounselorServiceImpl extends ServiceImpl<CounselorMapper, Counselor>
        implements CounselorService {

    @Resource
    private Cache<String, Object> counselorCache;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private static final String REDIS_KEY_PREFIX = "counselor:list:";

    @Override
    public IPage<Counselor> pageQuery(CounselorQuery query) {
        // 缓存 key：按查询条件拼
        String cacheKey = "page:" + query.getPageNum() + ":" + query.getPageSize()
                + ":" + (query.getSpecialty() == null ? "" : query.getSpecialty());

        // 1. Caffeine 查
        Object cached = counselorCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.info("Caffeine 命中: {}", cacheKey);
            return (IPage<Counselor>) cached;
        }

        // 2. Redis 查
        String redisKey = REDIS_KEY_PREFIX + cacheKey;
        Object redisCached = redisTemplate.opsForValue().get(redisKey);
        if (redisCached != null) {
            log.info("Redis 命中: {}", redisKey);
            counselorCache.put(cacheKey, redisCached);   // 回填 Caffeine
            return (IPage<Counselor>) redisCached;
        }

        // 3. MySQL 查
        log.info("走 MySQL: {}", cacheKey);
        Page<Counselor> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Counselor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Counselor::getStatus, CommonConstant.COUNSELOR_ONLINE);
        wrapper.like(StringUtils.isNotBlank(query.getSpecialty()),
                Counselor::getSpecialties, query.getSpecialty());
        wrapper.like(StringUtils.isNotBlank(query.getName()),
                Counselor::getName, query.getName());
        wrapper.orderByDesc(Counselor::getSortOrder).orderByAsc(Counselor::getId);

        IPage<Counselor> result = this.page(page, wrapper);

        // 4. 回填 Redis+ Caffeine
        redisTemplate.opsForValue().set(redisKey, result, 30, TimeUnit.MINUTES);
        counselorCache.put(cacheKey, result);

        return result;
    }

    @Override
    public Counselor getDetail(Long id) {
        String cacheKey = "detail:" + id;
        String redisKey = REDIS_KEY_PREFIX + cacheKey;

        // 1. Caffeine
        Object cached = counselorCache.getIfPresent(cacheKey);
        if (cached != null) {
            log.info("【Caffeine 命中】key={}", cacheKey);    // ← 加这行
            return (Counselor) cached;
        }

        // 2. Redis
        Object redisCached = redisTemplate.opsForValue().get(redisKey);
        if (redisCached != null) {
            log.info("【Redis 命中】key={}", redisKey);       // ← 加这行
            counselorCache.put(cacheKey, redisCached);
            return (Counselor) redisCached;
        }

        // 3. 分布式锁 + MySQL
        RLock lock = redissonClient.getLock("lock:counselor:detail:" + id);
        try {
            if (!lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                log.warn("【获取锁失败，降级查库】id={}", id);
                return this.getById(id);
            }
            try {
                // 双重检查
                redisCached = redisTemplate.opsForValue().get(redisKey);
                if (redisCached != null) {
                    log.info("【双重检查 Redis 命中】key={}", redisKey);
                    counselorCache.put(cacheKey, redisCached);
                    return (Counselor) redisCached;
                }

                log.info("【走 MySQL】id={}", id);              // ← 加这行
                Counselor counselor = this.getById(id);
                if (counselor == null) {
                    throw new BusinessException(ResultCode.COUNSELOR_NOT_FOUND);
                }
                redisTemplate.opsForValue().set(redisKey, counselor, 30, TimeUnit.MINUTES);
                counselorCache.put(cacheKey, counselor);
                log.info("【回填缓存完成】key={}", cacheKey);
                return counselor;
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("系统繁忙，请稍后重试");
        }
    }

    //清掉缓存
    public void evictCache() {
        // 清 Caffeine
        counselorCache.invalidateAll();
        // 清 Redis
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
