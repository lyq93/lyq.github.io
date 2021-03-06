## redis场景分析

### redis数据类型

- String类型

> redis中String类型可以分为三种场景来考虑。值得注意的一点是String类型在redis中存储的是二进制字节数组。可以使用Object encoding查看String类型
>
> 1、字符串
>
> String作为字符串的场景，可以用于存储session
>
> 由于底层是二进制字节数组，还可以存图片、文件等。针对一些热点文件，文件大小不大的情况，可以避免请求落到数据库。
>
> 2、数值
>
> String类型可以存数值并做计算。数值的使用场景需要结合redis单线程的特点，比如用于计数。在并发情况下，对于共享数值的计数正确要额外处理，且在分布式情况下新服务器起来还需要考虑数据同步。如果把状态（计算）迁移到redis，既不需要额外处理也不需要数据同步，所有服务器都是无状态的，新服务器直接调用redis即可取到正确数值。
>
> 3、bitmap
>
> 这里需要用到二进制与和或的知识点。应用场景的话，参考linux的文件权限，例如chmod 777 redis.log，是用1表示有对应的权限，0表示没有对应的权限。再抽象点来说，二进制的场景包括二进制与或适用场景，都可以是bitmap应用范围

- List

> List值得注意的是它具有放入顺序，例如命令lpush和rpush。
>
> lpush k1 a b c d，数据是d,c,b,a。也就是说lpop第一个数据是d。这符合先进后出，栈的特点
>
> rpush k2 a b c d，数据是a,b,c,d。也就是说lpop第一个数据是a。这符合先进先出，队列的特点
>
> 也就是说redis中List类型可以取代JVM中栈和队列的数据结构的数据。
>
> 典型应用场景是数据共享，在Java里集合是存在JVM中的。如果单台服务器的话，操作JVM中的集合不需要额外的处理，在分布式或者负载均衡等用到多服务器的情况下意味着存在多个JVM，这种情况就需要状态管理。那么如果把这个数据迁移到redis中，这个问题就不需要考虑了。

- Hash

> Hash最典型的就是聚合数据缓存场景。举个例子，详情页面（比如商品）的数据。一个商品的详情，数据可能是多维度的，意味着来源于不同的库表。如果请求直接落到数据库意味着多表联查，性能也是需要考虑的一方面。redis的hash就可以处理聚合数据的问题。

- Set

> Set的特点：
>
> 1、无序
>
> 2、去重
>
> 3、满足集合的操作
>
> 通过Object encoding查看Set会发现实现是hashTable，hash实现都会有一个特点是元素变多会rehash，这有可能导致Set不但是无序的，还可能存在原来的元素位置会发生变化。
>
> 场景：
>
> 1、抽奖等随机事件
>
> SRANDMEMBER可以支持，正数是没有重复数据，负数是支持重复数据。
>
> 2、推荐系统（集合操作）
>
> 两个集合的交集、并集、差集。比如说两个人的好友列表，共同好友是交集，推荐好友是差集等

- Zset

> 要知道集合是否是已排序的，一定得知道排序的维度。也就是说，排序集合有两个要素：
>
> 1、集合存储元素
>
> 2、集合元素按某一维度排序
>
> 所以zset命令会要求设置一个分值（排序的维度）和一个元素本身
>
> 排序集合的场景：
>
> 1、排行榜
>
> 例如小说、商品的排行
>
> 2、评论
>
> 大多数的网站的评论，一般都是按评论时间来进行排序的