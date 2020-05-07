## Redis分布式锁

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

