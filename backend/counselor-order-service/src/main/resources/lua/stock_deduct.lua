-- KEYS[1] = 库存 key，例如 counselor:schedule:stock:100
-- ARGV[1] = 要扣减的数量（一般传 1）
-- 返回值：
--   >=0  扣减成功，返回剩余库存
--   -1   库存不足
--   -2   库存 key 不存在（时段无效）

local stock = redis.call('GET', KEYS[1])
if not stock then
    return -2
end

stock = tonumber(stock)
local num = tonumber(ARGV[1])

if stock < num then
    return -1
end

return redis.call('DECRBY', KEYS[1], num)