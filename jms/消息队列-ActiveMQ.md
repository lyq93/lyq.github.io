## 消息队列-ActiveMQ

### 背景

- 技术出现的原因

> 考虑个实际场景：
>
> 比如在课堂上学生向老师提问，每个学生都有一个问题，老师只有一个。当老师在回答第一个学生的问题的时候，可能需要花费5分钟，然后第二、第三、第n个学生就依次排队等待提问。这种方式有什么问题？
>
> 1、对于学生来说，效率很低。排队在越后面的，解决一个问题所需要花费的时间越多，这些时间都是浪费在排队上的
>
> 2、对于老师来说，压力很大。在当前这节课上，老师要处理这么多学生的问题，可能需要花费整节课的时间，甚至都处理不完，老师可能直接崩溃
>
> 所以，这里映射到我们的系统之间的服务调用的话，老师相当于是服务提供方，学生相当于是客户端，提问相当于是请求，问题相当于是消息。
>
> 客户端如果拿不到请求的机会，就应该把请求以一种格式留言给服务端，服务端处理完之后响应（解耦）
>
> 作为服务端，也不需要非得在特定时间点处理完所有的请求，只需要在空闲时多处理一点，忙碌时少处理一点。也影响不了客户端

- 消息中间件特点

> 所以消息队列出现的原因就是提供这种技术支撑：
>
> 1、解耦两个服务之间的依赖，通过消息中间件
>
> 2、削峰，削减服务器特定时间点的请求压力
>
> 3、异步调用，客户端把请求封装好发送给消息中间件，而后做自己的事情去了。服务端处理完再获取结果即可

### JMS简单介绍

> JMS--java message service，java消息服务，主要四个元素组成：
>
> 1、provider
>
> 可以理解为存放消息的主机
>
> 2、producer
>
> 生产消息的服务
>
> 3、consumer
>
> 消费消息的服务
>
> 4、message
>
> 消息，消息包括三部分：
>
> 1、消息头
>
> 有一系列可以设置的属性，包括优先级，消息头id等。举个例子，例如messageId
>
> 如何保证消息不被重复的消费？
>
> 每条消息都带着一个全局的唯一id，这个id可以放到第三方服务中去，比如redis。消费消息的时候，通过messageId去redis查就能知道是否重复消费
>
> 2、消息体
>
> 消息体就是存放消息正文内容的地方。
>
> 消息有五种结构：
>
> - Text 字符串
> - MAP 键值对
> - BYTE 字节
> - OBJECT 对象
> - STREAM 流
>
> 3、消息属性
>
> > 例如发100条消息，如何标识某一条或者某几条消息比较关键？
> >
> > 消息属性的作用就是这个，可以标识出关键的消息区别于其他消息

### 消息中间件的两种模式

- 点对点（队列）

> 消息生产者发送消息到queue中，消息消费者去消费该消息
>
> 特点：
>
> 1、如果没有消费者，消息也不会丢失
>
> 2、如果存在多个消费者，轮训的方式发给每一个消费者，每个消费者得到部分数据
>
> 3、默认情况下，queue的消息会以文件的形式在mq服务器上保存
>
> 4、由于是点对点，所以即使消费者再多，对于性能也没有什么影响

- 一对多（发布/订阅 Topic）

> 1、如果没有订阅者，消息会被丢失
>
> 2、如果有多个订阅者，每个订阅者都会收到全量消息
>
> 3、消息没有状态
>
> 4、如果存在多个订阅者，性能会有所下降。因为要复制数据发送给每一个消费者

### 消息可靠性保证

- 持久化

> 在ActiveMQ中，消息的持久化功能有两种模式：
>
> 1、持久化
>
> 对于队列而言，生产者设置消息持久化，会在产生消息后即使服务挂掉了，重启之后消息依然存在待消费队列中，消费者依然可以消费。
>
> 对于topic而言，消息持久化的方式需要消费者向mq服务器注册订阅，这个步骤执行后，发布订阅模式的消息都是持久化的
>
> 2、非持久化
>
> 对于队列而言，设置非持久化，生产消息后重启服务，消费者就无法消费到消息
>
> 对于topic而言，默认消息是非持久化的，如果不存在消费者，那么消息就等于丢失
>
> - 简单的来说，如果想要解决这样一个问题：
>
> 生产者发送消息之后，消费者没有及时消费，这个时候mq服务器挂了，消息不能丢失
>
> 这个时候就需要设置持久化

- 事务

> 对于生产者而言，开启事务之后，消息要直到commit操作才会真正提交消息。执行rollback方法，之前的消息会被回滚。
>
> 对于消费者而言，开启事务之后，直到执行commit操作，消息才算是真正被消费。如果消费者不执行commit操作，mq服务器不认为消息被消费，那么消息就存在多次消费的情况

- 签收机制

> 签收的几种方式：
>
> 1、自动签收，这种方式是默认方式，也即是不管做什么操作，框架都会自动签收消息
>
> 2、手动签收，这种方式需要手动调用方法执行签收操作，否则消息会重复消费
>
> 3、允许重复消息，多个线程或者多个消费者同时消费到一个消息时，因为线程不安全，可能会重复消费。这种方式很少用
>
> 4、事务下的签收，开启事务的情况下可以使用该方式

### ActiveMQ的传输协议

> tcp、nio、amqp、stomp、mqtt协议
>
> - tcp协议：
>
> 1. Transmission Control Protocol(TCP)是默认的。TCP的Client监听端口61616
> 2. 在网络传输数据前，必须要先序列化数据，消息是通过一个叫wire protocol的来序列化成字节流。
> 3. TCP连接的URI形式如：tcp://HostName:port?key=value&key=value，后面的参数是可选的。
> 4. TCP传输的的优点：
>
> TCP协议传输可靠性高，稳定性强
>
> 高效率：字节流方式传递，效率很高
>
> 有效性、可用性：应用广泛，支持任何平台
>
> 1. 关于Transport协议的可选配置参数可以参考官网http://activemq.apache.org/tcp-transport-reference
>
> - nio协议
>
> 1. New I/O API Protocol(NIO)
> 2. NIO协议和TCP协议类似，但NIO更侧重于底层的访问操作。它允许开发人员对同一资源可有更多的client调用和服务器端有更多的负载。
> 3. 适合使用NIO协议的场景：
>
> 可能有大量的Client去连接到Broker上，一般情况下，大量的Client去连接Broker是被操作系统的线程所限制的。因此，NIO的实现比TCP需要更少的线程去运行，所以建议使用NIO协议。
>
> 可能对于Broker有一个很迟钝的网络传输，NIO比TCP提供更好的性能。
>
> 1. NIO连接的URI形式：nio://hostname:port?key=value&key=value
> 2. 关于Transport协议的可选配置参数可以参考官网http://activemq.apache.org/configuring-version-5-transports.html
>
> 其他协议不做过多了解
>
> 在activemq.xml文件中，transportConnectors标签中，增加对应一项nio协议即可
>
> <transportConnector name="nio" uri="nio://0.0.0.0:61618?trace=true" />
>
> 协议增强：
>
> <transportConnector name="auto+nio" uri="auto+nio://0.0.0.0:61618?xxx" />
>
> 支持所有协议使用nio网络io模型

### ActiveMQ消息存储和持久化

> 上面提到的消息可靠性保证中的三个特性持久化、事务、签收机制都是MQ服务器内部的实现机制。这里介绍的持久化，是借用第三方硬件，把数据存到外部系统中去。例如，把消息存到mysql数据库中做数据持久化
>
> ActiveMQ的消息持久化机制有JDBC，AMQ，KahaDB和LevelDB
>
> - AMQ
>
> > 基于文件的存储机制，是以前默认的实现（5.3），现在已经不使用该实现了
>
> - KahaDB
>
> > ActiveMQ5.4开始的默认实现，基于日志文件的持久化机制。
> >
> > 在activemq.xml配置文件中有如下一段配置：
> >
> > <persistenceAdapter>
> >
> > ​      <kahaDB directory="${activemq.data}/kahadb"/>
> >
> > </persistenceAdapter>
> >
> > 存储原理：
> >
> > kahaDB使用索引文件+消息日志文件的方式来进行数据持久化。索引文件存储所有数据的地址，消息日志文件存储所有消息。
> >
> > 进入到kahadb，有对应如下几个文件：
> >
> > 1、db<number>.log
> >
> > 该文件是一个预定义大小的数据文件，例如32M。文件命名为db-number.log，当文件已满时，一个新的文件会创建，number会进行递增。当文件消息不再有被引用时，文件会被删除或者归档
> >
> > 2、db.data
> >
> > 该文件包含了持久化的BTree索引，索引了消息数据记录中的消息，它是索引文件
> >
> > 3、db.redo
> >
> > 用来进行消息恢复
> >
> > 4、db.free
> >
> > 当前使用的db.data有哪些是空闲的，文件具体内容是空闲页的ID。这个类似于是JVM中的对象分配的空闲列表分配方式，规整的分配内存
> >
> > 5、lock
> >
> > 文件锁，表示当前获得kahaDB读写权限的broker
>
> - JDBC消息存储
>
> > 1、添加mysql数据驱动包到lib文件夹
> >
> > > 这个就不多赘述，activeMQ下lib目录就是存放jar的目录
> >
> > 2、修改persistenceAdapter配置
> >
> > > 默认使用的是kahaDB：
> > >
> > > <persistenceAdapter>
> > >
> > > ​      <kahaDB directory="${activemq.data}/kahadb"/>
> > >
> > > </persistenceAdapter>
> > >
> > > 修改为：
> > >
> > > <persistenceAdapter>
> > >
> > > ​      <jdbcPersistenceAdapter dataSource="#mysql-ds" createTableOnStartup="true" />
> > >
> > > </persistenceAdapter>
> > >
> > > dataSource指定将要引用的持久化数据库的bean名称
> > >
> > > createTableOnStartup指定是否启动的时候创建数据库表，默认是true。一般的处理操作是第一次启动设置为true，之后改为false
> >
> > 3、数据库连接池配置
> >
> > > 在</broker>标签和<import>标签之间插入数据库连接池配置
> > >
> > > <bean id="mysql-ds" class="org.apache.commons.dbcp2.BasicDataSource" destroy-method="close">
> > >
> > > ​	<property name="driverClassName" value="com.mysql.jdbc.Driver"/>
> > >
> > > ​	<property name="url" value="jdbc:mysql://xxx:3306/activemq?relaxAutoCommit=true"/>
> > >
> > > ​	<property name="userName" value="xxx"/>
> > >
> > > ​	<property name="password" value="xxx"/>
> > >
> > > ​	<property name="maxTotal" value="200"/>	
> > >
> > > ​	<property name="poolPreparedStatements" value="true"/>
> > >
> > > </bean>
> > >
> > > 之后建立一个activemq的数据库
> >
> > 4、重启服务，自动建表
> >
> > 数据库表有三张：
> >
> > 1、activemq_msgs:
> >
> > 消息表，字段如下
> >
> > id 自增主键
> >
> > container 消息的destination
> >
> > msgid_prod 消息发送者的主键
> >
> > Msg_seq 发送消息的顺序
> >
> > expiration 消息的过期时间
> >
> > msg 消息本体的java序列化对象的二进制数据
> >
> > priority 优先级，从0-9，数值越大优先级越高
> >
> > 2、activemq_acks:
> >
> > 存储持久化订阅的信息和最后一个持久订阅接收的消息ID
> >
> > 字段如下：
> >
> > container 消息的destination
> >
> > sub_dest 如果是使用static集群，这个字段会有集群其他系统的信息
> >
> > client_id 每个订阅者都必须有一个唯一的客户端ID用以区分
> >
> > sub_name 订阅者的名称
> >
> > selector 选择器，可以选择只消费满足条件的消息。条件可以用自定义属性实现，支持多属性and和or操作
> >
> > last_acked_id 记录消费过的消息的ID
> >
> > 3、activemq_lock:
> >
> > 该表在集群环境中才有用，只有一个broker可以获得消息，成为master broker，其他的只能作为备份等待master broker不可用，才可能成为下一个master broker。这个表用于记录哪个broker是当前的master broker
> >
> > #### 配置遇到的问题
> >
> > 1、数据库的驱动jar包问题
> >
> > 需要把jar放到activemq安装路径下的lib目录
> >
> > 2、下划线问题
> >
> > “java.lang.illegalStateException:BeanFactory not initialized or already closed"
> >
> > 操作系统机器名有"_"符号，更改机器名重启后解决
> >
> > 3、可能存在的问题
> >
> > 数据持久化到mysql这种方式，不断的读写mysql不合理。
> >
> > 使用jdbc message store with activemq journal的方式，加上activeMQ journal，使用高速缓存写入技术，提高性能。举个例子，假如生产者产生了1000条消息，这1000条消息会写入到journal文件中，如果消费者的消费速度很快的情况下，那么在journal文件还没同步到数据库之前，消费者已经消费了90%以上的消息，那么这个时候只需要批量的同步10%的消息到DB中
> >
> > 所以，为了提高性能，这种方式使用了日志文件存储+数据库存储
> >
> > 对应的配置修改如下：
> >
> > 原配置：
> >
> > <persistenceAdapter>
> >
> > ​      <jdbcPersistenceAdapter dataSource="#mysql-ds" createTableOnStartup="true" />
> >
> > </persistenceAdapter>
> >
> > 新配置：
> >
> > <persistenceFactory>
> >
> > ​	<journalPersistenceAdapterFactory 
> >
> > ​		journalLogFiles="4"
> >
> > ​		journalLogFileSize="32768"
> >
> > ​		useJournal="true"
> >
> > ​		useQuickJournal="true"
> >
> > ​		dataSource="#mysql-ds"
> >
> > ​		dataDirectory="activemq-data" />
> >
> > </persistenceFactory>
>
> 

### ActiveMQ多节点集群

> 待补充

### 高级特性

- 异步投递
- 延迟投递和定时投递
- 消息消费的重试机制
- 死信队列
- 消息不被重复消费（幂等性）

