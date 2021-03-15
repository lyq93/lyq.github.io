## Redis内存淘汰机制

### 背景

> 几个实操redis的常见问题：
>
> 1、生产上，redis内存设置多少比较合适
>
> > 一般为物理机的四分之三，另外默认情况下redis是没有设置内存大小的，这个时候就会有多少物理机内存就用多少
>
> 2、如何设置redis的内存大小
>
> > 两种方式：
> >
> > 1、修改redis.conf配置文件的maxmemory
> >
> > 2、使用config set maxmemory 1
> >
> > 注意这里是bytes单位，需要转换
>
> 3、如何查看redis的内存大小
>
> > config get maxmemory
>
> 4、redis的内存满了之后，会发生什么
>
> > 这里很好重现，把redis内存设置为1字节，自己重现一下就可以了。这里会报oom
>
> 5、redis的内存淘汰机制了解过么
>
> > redis对过期数据的处理有3种方式：
> >
> > 1、定时删除
> >
> > 针对设置了过期时间的数据，CPU会检测数据是否已经达到过期时间，如果是，则删除。这种方式会产生大量的性能消耗，CPU需要不断的去查询数据是否已经过期。相当于是以时间换空间
> >
> > 2、惰性删除
> >
> > 到达过期时间的数据，会留在内存中，直到下一次被调用的时候删除。这种方式下，CPU性能得到释放，但是牺牲了内存空间。意味着内存中可能会存在较多无用数据。相当于是以空间换时间
> >
> > 3、定期删除
> >
> > 针对上述两种方式的优劣采取的一种策略。每隔一定的频率，随机选取部分数据进行删除
> >
> > 即使如此，内存有可能还会打满，因为有可能就是一直都有一些数据，它既不会被随机抽取到，又不会被调用到，这种数据可能会在内存中越积越多，导致内存满了。所以这个时候就引入了redis的内存淘汰机制。
> >
> > 
> >
> > redis内存淘汰机制相关配置：
> >
> > \# volatile-lru -> Evict using approximated LRU, only keys with an expire set.
> >
> > \# allkeys-lru -> Evict any key using approximated LRU.
> >
> > \# volatile-lfu -> Evict using approximated LFU, only keys with an expire set.
> >
> > \# allkeys-lfu -> Evict any key using approximated LFU.
> >
> > \# volatile-random -> Remove a random key having an expire set.
> >
> > \# allkeys-random -> Remove a random key, any key.
> >
> > \# volatile-ttl -> Remove the key with the nearest expire time (minor TTL)
> >
> > \# noeviction -> Don't evict anything, just return an error on write operations.
> >
> > \#
> >
> > \# LRU means Least Recently Used
> >
> > \# LFU means Least Frequently Used
> >
> > \#
> >
> > \# Both LRU, LFU and volatile-ttl are implemented using approximated
> >
> > \# randomized algorithms.
> >
> > \#
> >
> > \# Note: with any of the above policies, Redis will return an error on write
> >
> > \#    operations, when there are no suitable keys for eviction.
> >
> > \#
> >
> > \#    At the date of writing these commands are: set setnx setex append
> >
> > \#    incr decr rpush lpush rpushx lpushx linsert lset rpoplpush sadd
> >
> > \#    sinter sinterstore sunion sunionstore sdiff sdiffstore zadd zincrby
> >
> > \#    zunionstore zinterstore hset hsetnx hmset hincrby incrby decrby
> >
> > \#    getset mset msetnx exec sort
> >
> > \#
> >
> > \# The default is:
> >
> > \#
> >
> > \# maxmemory-policy noeviction
> >
> > 由配置可以看出，内存淘汰机制总共有8种，redis默认采用的是noeviction，不处理等着内存爆掉。
> >
> > 这里内存淘汰机制我分为2个维度，一个是针对所有key，一个是随机key。内存淘汰机制的选择肯定不会选择处理随机key的情况，一般来说最常用的就是最近最少使用LRU算法
>
> 6、你们生产上内存淘汰机制用的是哪个，怎么设置
>
> > 跟设置内存大小一样有两种方式
>
> 7、简单介绍下LRU，能手写么
>
> > LRU 最近最少使用，Java的LinkedHashMap就是最好的LRU实现。这里说一下，实现LRU算法可能存在很多种实现以及用各种数据结构实现。但需要考虑的是时间复杂度和空间复杂度问题。读写O(1)链表+hashMap

