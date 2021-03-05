## Redis分布式锁

### Redis分布式锁实现原理

#### 单机版Redis实现

> 单台Redis服务器实现分布式锁会存在哪些问题？
>
> 1、获取到锁的客户端挂了，死锁场景
>
> redis本地方法setnx+timeout的方式解决该问题
>
> 2、某一个客户端拿到锁之后，其他客户端需要做什么？
>
> - 其他客户端通过IO发送setnx做自旋
>
>   > 这种方式意味着多台客户端有可能一直在做无效的网络请求，这本身是一种资源浪费，可以考虑发布订阅模式来规避这种情况。
>
> - 拿到锁的客户端通过发布订阅模式来通知其他等待锁的客户端
>
>   > 这种方式规避了其他未获得锁的客户端在做自旋，浪费网络资源。但是存在问题如果拿到锁的客户端挂了怎么办？
>   >
>   > 这种情况下靠超时来解决，也就是说最终的实现是通过发布订阅+超时的方式，来减少其他客户端的自旋频率
>
> 3、获取到锁的客户端执行时间超过了timeout，导致同时存在多个客户端持有锁
>
> 开辟一个监控线程，监控持有锁的客户端线程，如果线程正常执行且执行时间超过了timeout时间，监控线程负责加时间
>
> 4、redis挂了怎么办？
>
> > 采用redis集群，redis的集群分两类
> >
> > - 主从复制
> >
> >   > 一个主节点，一个从节点。主要解决的问题是HA，单点故障问题
> >   >
> >   > 所以主从数据之间需要进行数据同步以保证节点间的数据是全量的
> >
> > - cluster模式
> >
> >   > 这种模式需要解决的问题就是容量、压力、瓶颈问题。核心思路是分治、分片。每一个节点存储的数据是不一样的
> >   >
> >   > 如果给节点加上从节点，相当于又回到了主从集群模式
>
> 单台redis服务器实现分布式锁应该考虑的问题：
>
> 1、业务剥离，该台服务器只做分布式锁，其他redis服务器做其他业务
>
> 2、硬件支撑，冗余设备（多电源多网卡）

#### 主从集群模式分布式锁

> 针对单台redis挂了的问题，考虑主从集群下分布式锁的可行性。
>
> redis主从模式满足CAP(一致性、可用性、分区容错性)原则的AP，意味着同步不精准。
>
> 假设有主从2台redis服务器，某一客户端从主服务器上获取锁后，主服务器挂了。这个时候，其他客户端存在可能从从节点服务器中获取到锁，又出现了相同的问题。所以使用集群模式并没有解决实际问题。
>
> 对于实现分布式锁而言，redis主从集群模式没有太大作用。

#### Redlock场景

> - 解决问题
>
>   > redis单机挂了之后带来的一系列问题
>
> - 实现原理
>
>   > 多台独立的redis服务器，每个客户端都与多台redis服务器通信取锁，客户端自身维护一个计数器，拿到超过半数的redis服务器的锁，即正常获取到锁。这种情况下，当一台redis挂掉之后就不会存在影响
>   >
>   > 但是带来另外一个问题，有可能存在每个客户端都没获取到超过半数的锁，这个时候需要如何处理？
>   >
>   > 随机重试

## redis锁的简单demo

### 获取锁

```java
/**
 * redis分布式锁
 * 获取锁
 * 存在问题：
 * 假设有一个服务获取了锁之后，因为其他原因服务挂掉了
 * 然后锁就不会释放了，这个时候导致其他服务不能获取到锁
 * @param conn
 * @param lockName
 * @param acquireTimeOut
 * @return
 */
public String acquireLock(Jedis conn, String lockName, Long acquireTimeOut) {
    
    String identify = UUID.randomUUID().toString();

    long acquireEnd = System.currentTimeMillis() + acquireTimeOut;

    while (System.currentTimeMillis() < acquireEnd) {
        if(conn.setnx("lock:" + lockName, identify) == 1) {
            return identify;
        }
        try{
            Thread.sleep(1);
        }catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }

    return null;
}
```

> 对锁增加过期时间，解决服务异常情况下未释放锁的问题

### 第二个版本获取锁

```java
/**
 * 分布式锁，第二个版本
 * 在获取的锁的时候增加锁的过期时间
 * 解决在获取锁之后出现服务异常的情况未释放锁导致其他服务不能获取锁的问题
 * @param conn
 * @param lockName
 * @param acquireTimeOut
 * @param timeOut
 * @return
 */
public String acquireLockByTimeOut(Jedis conn, String lockName, Long acquireTimeOut, int timeOut) {

    String lockKey = "lock:" + lockName;
    // 生成锁的唯一标识
    String identify = UUID.randomUUID().toString();
    // 循环获取锁的条件
    long acquireEnd = System.currentTimeMillis() + acquireTimeOut;

    while (System.currentTimeMillis() < acquireEnd) {
        // 设置锁
        if(conn.setnx(lockKey,identify) == 1) {
            // 设置锁的过期时间
            conn.expire(lockKey, timeOut);
            return identify;
        }
        // 如果锁是存在的，但是没有设置过期时间
        if(conn.ttl(lockKey) == -1) {
            // 增加过期时间
            conn.expire(lockKey,timeOut);
        }

        try{
            Thread.sleep(1);
        }catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
    return null;
}
```

### 释放锁

```java
/**
 * 分布式锁实现
 * 释放锁方法
 * @param conn
 * @param lockName
 * @param identify
 * @return
 */
public boolean releaseLock(Jedis conn, String lockName, String identify) {

    String lockKey = "lock:" + lockName;

    while (true) {
        conn.watch(lockKey);
        if(identify.equals(conn.get(lockKey))) {
            Transaction transaction = conn.multi();
            transaction.del(lockKey);
            List<Object> results = transaction.exec();
            if(results == null) {
                continue;
            }
            return true;
        }

        conn.unwatch();
        break;
    }

    return false;
}
```

