# 心理咨询预约平台

基于 Spring Cloud Alibaba 的心理咨询预约系统，支持咨询师时段发布、用户预约、异步通知等完整业务流程，重点解决高并发场景下的重复预约、缓存击穿、接口限流等问题。

---

## 一、项目简介

本项目是一个前后端分离的心理咨询预约平台，用户可以在线浏览咨询师、查看可预约时段、下单预约，并管理自己的预约订单；管理员可以维护咨询师信息和查看所有订单。

项目从真实的"预约抢单"场景出发，实践了微服务架构、高并发抢单、多级缓存、消息队列、分布式锁等核心技术，是一套完整的生产级高并发解决方案。

**核心业务流程**：

用户浏览咨询师 → 查看可预约时段 → 选择时段下单 → 系统扣减库存 → 创建订单 → 异步通知

---

## 二、技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | Web 服务框架 |
| Spring Cloud | 2023.0.1 | 微服务框架 |
| Spring Cloud Alibaba | 2023.0.1.0 | Nacos / Sentinel |
| Spring Cloud Gateway | - | API 网关 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.0 | 分布式缓存 |
| Redisson | 3.27.2 | 分布式锁 |
| Caffeine | - | 本地缓存 |
| Kafka | 3.x | 消息队列 |
| Nacos | 2.x | 注册中心 |
| Sentinel | 1.8.7 | 限流熔断 |
| JWT | 0.12.5 | 无状态鉴权 |
| Hutool | 5.8.27 | 工具类库 |

### 前端

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Vue Router | 路由 |
| Axios | HTTP 客户端 |
| SCSS | 样式预处理 |

### 开发工具

- 后端 IDE：IntelliJ IDEA
- 前端 IDE：VS Code
- 接口调试：Apifox
- Redis 客户端：TinyRDM
- 数据库工具：Navicat
- 压测工具：JMeter

---

## 三、系统架构

### 模块划分

counselor-platform/
├── counselor-common/ # 公共模块：Result、全局异常、JWT、拦截器
├── counselor-service/ # 咨询师服务：咨询师 CRUD + 多级缓存
├── counselor-order-service/ # 订单服务：时段 + 预约 + Lua 扣库存 + Kafka
└── counselor-gateway/ # 网关：路由转发 + 跨域


### 架构图

┌─────────────┐
│ 前端 Vue3 │
└──────┬──────┘
│ http://localhost:8088
▼
┌─────────────────────┐
│ counselor-gateway │ (8088)
│ - 路由 /api/** │
│ - 跨域 CORS │
└──┬──────────────┬────┘
│ │
▼ ▼
┌──────────────┐ ┌──────────────────────┐
│counselor- │ │counselor-order- │
│service (8081)│ │service (8082) │
│ │ │ │
│- 咨询师 │ │- 时段 │
│- 多级缓存 │ │- 预约（Lua） │
│- Redisson │ │- 取消 │
│ │ │- JWT │
│ │ │- Kafka │
│ │ │- Sentinel │
└──────┬───────┘ └──────────┬───────────┘
│ │
└──────┬──────────────┘
│
┌──────▼──────┐
│ MySQL │
│ Redis │
│ Kafka │
│ Nacos │
│ Sentinel │
└─────────────┘

---

## 四、核心功能

### 1. 咨询师管理

- 咨询师分页查询（支持按擅长领域筛选）
- 咨询师详情（含简介、资质、价格）
- 多级缓存加速查询

### 2. 时段管理

- 查询某咨询师的可预约时段（按日期分组）
- 定时任务每日自动生成未来 7 天时段
- 已预约时段显示为灰色不可点击

### 3. 预约管理

- 创建预约（Redis Lua 原子扣库存）
- 取消预约（释放库存）
- 我的预约列表（分页）
- 幂等校验（防止重复提交）

### 4. 用户认证

- JWT 无状态登录
- 拦截器解析 Token 存入 ThreadLocal
- 未登录自动跳转登录页

### 5. 异步通知

- 预约成功后通过 Kafka 异步发送通知
- 主流程立即返回，通知异步处理

---

## 五、效果展示

### 1. 咨询师管理

<img width="703" height="395" alt="屏幕截图 2026-10-05 120256" src="https://github.com/user-attachments/assets/28c953d7-ecb2-416f-b18e-6dcdae376a68" />

### 2. 时段管理

<img width="893" height="704" alt="屏幕截图 2026-10-05 121910" src="https://github.com/user-attachments/assets/12972332-2159-45bb-b599-49abd34771b7" />

### 3. 预约管理

<img width="545" height="343" alt="屏幕截图 2026-10-05 121816" src="https://github.com/user-attachments/assets/503eac89-1aa3-4aa5-8b1b-32632f8f7059" />

<img width="928" height="382" alt="屏幕截图 2026-10-05 121847" src="https://github.com/user-attachments/assets/6ced0155-5139-413e-86b1-740c7b825383" />

---

## 六、解决的核心问题

| 问题 | 解决方案 |
|------|------|
|高并发下重复预约 | Redis Lua 原子扣库存 + 数据库唯一索引 |
| 缓存击穿 | Redisson 分布式锁 + 双重检查 |
| 缓存穿透 | 空值缓存 + 布隆过滤器 |
| 缓存雪崩 | 状态管理 |
| 接口被恶意刷单 | 路由 |
| 通知拖慢主流程 | HTTP 客户端 |
| 逻辑删除与唯一索引冲突 | 样式预处理 |
| 服务间通信 | 空值缓存 + 布隆过滤器 |
| 微服务循环依赖 | 状态管理 |

---
