## redis多机多节点集群搭建

#### 安装包下载

> ```
> wget http://download.redis.io/releases/redis-3.0.7.tar.gz
> ```

#### 解压安装

> tar -zxvf redis-3.0.7.tar.gz /home/

> mv redis-3.0.7 redis // 重新命名文件夹

> cd redis 
>
> Make && make install

> 然后把src目录下的redis-trib.rb拷贝到/usr/local/bin/
>
> cp redis-trib.rb /usr/local/bin/
>
> 这个是redis官方提供的工具用来创建集群

#### 多机多节点集群信息

> 集群搭建使用3台服务器，每台服务器3个节点的方式。
>
> 公网IP：
>
> 第一台服务器 47.106.171.169，包括节点7000、7001、7002 
>
> 第二台服务器 120.24.65.72，包括节点7003、7004、7005
>
> 第三台服务器 120.25.251.133， 包括节点7006、7007、7008

#### 创建节点、修改配置

> mkdir redis_cluster
>
> Mkdir redis_cluster/7000 7001 7002
>
> cp /home/redis/redis.conf /home/redis/redis_cluster/7000/
>
> vi redis.conf
>
> 主要修改以下几个配置：
>
> port 7000 // 端口号 7000 7001 7002
>
> bind 172.18.227.228 // 本机地址
>
> demonize yes // redis后台启动
>
> pidfile /var/run/redis_7000.pid // pidfile文件对应端口号
>
> cluster-enabled yes // 开启集群
>
> cluster-config-file nodes_7000.conf // 集群的配置 配置文件在首次启动自动生成
>
> cluster-node-timeout 15000 // 请求超时设置
>
> appendonly yes // aof日志开启

#### 启动节点

> redis-server redis_cluster/7000/redis.conf
>
> redis-server redis_cluster/7001/redis.conf
>
> redis-server redis_cluster/7002/redis.conf
>
> ...

#### 检查启动情况

> ps -ef | grep redis
>
> root   4530   1 0 13:37 ?    00:00:01 **redis**-server 172.18.227.228:7000 [cluster]
>
> root   4534   1 0 13:37 ?    00:00:01 **redis**-server 172.18.227.228:7001 [cluster]
>
> root   4538   1 0 13:37 ?    00:00:01 **redis**-server 172.18.227.228:7002 [cluster]
>
> netstat -tnlp | grep redis
>
> 29139/**redis**-server  
>
> tcp    0   0 172.18.227.228:7000     0.0.0.0:*        LISTEN   4530/**redis**-server 1 
>
> tcp    0   0 172.18.227.228:7001     0.0.0.0:*        LISTEN   4534/**redis**-server 1 
>
> tcp    0   0 172.18.227.228:7002     0.0.0.0:*        LISTEN   4538/**redis**-server 1 
>
> tcp    0   0 172.18.227.228:17000     0.0.0.0:*        LISTEN   4530/**redis**-server 1 
>
> tcp    0   0 172.18.227.228:17001     0.0.0.0:*        LISTEN   4534/**redis**-server 1 
>
> tcp    0   0 172.18.227.228:17002     0.0.0.0:*        LISTEN   4538/**redis**-server 1 

#### 创建集群

> redis-trib.rb create --replicas 1 172.18.227.228:7000 172.18.227.228:7001 172.18.227.228:7002 120.24.65.72:7003 120.24.65.72:7004 120.24.65.72:7005 120.25.251.133:7006 120.25.251.133:7007 120.25.251.133:7008
>
> /usr/bin/env: ruby: 没有那个文件或目录
>
> 这个工具是ruby实现的，需要安装ruby
>
> 1、安装curl
>
> > yum install curl
>
> 2、安装rvm
>
> > curl -L get.rvm.io | bash -s stable
>
> 安装有可能会遇到以下错误
>
> Downloading https://github.com/rvm/rvm/archive/1.29.8.tar.gz
> Downloading https://github.com/rvm/rvm/releases/download/1.29.8/1.29.8.tar.gz.asc
> gpg: 于 2019年05月08日 星期三 22时14分49秒 CST 创建的签名，使用 RSA，钥匙号 39499BDB
> gpg: 无法检查签名：没有公钥
> GPG signature verification failed for '/usr/local/rvm/archives/rvm-1.29.8.tgz' - 'https://github.com/rvm/rvm/releases/download/1.29.8/1.29.8.tar.gz.asc'! Try to install GPG v2 and then fetch the public key:
>
>     gpg2 --keyserver hkp://pool.sks-keyservers.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3 7D2BAF1CF37B13E2069D6956105BD0E739499BDB
>
> or if it fails:
>
>     command curl -sSL https://rvm.io/mpapis.asc | gpg2 --import -
>     command curl -sSL https://rvm.io/pkuczynski.asc | gpg2 --import -
>
> ————————————————
> 版权声明：本文为CSDN博主「lalifeier」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
> 原文链接：https://blog.csdn.net/Gushiyuta/article/details/90770681

> 根据提示执行命令：
>
> > gpg2 --keyserver hkp://pool.sks-keyservers.net --recv-keys 409B6B1796C275462A1703113804BB82D39DC0E3 7D2BAF1CF37B13E2069D6956105BD0E739499BDB
>
> 再重新执行安装，安装完成之后
>
> source /usr/local/rvm/scripts/rvm
>
> rvm install 2.3.3
>
> ruby --version
>
> gem install redis

#### 集群搭建问题记录

> 1、多机多节点集群创建，一直卡在waiting for the cluster to join
>
> 1、一开始怀疑是端口问题，查资料说是除了redis正常的监听端口需要开放防火墙之外，还需要开放总线端口，把三台服务器对应的总线端口都开放，问题依然存在。
>
> 2、然后仔细查看了一下提示信息
>
> > \>>> Nodes configuration updated
> >
> > \>>> Assign a different config epoch to each node
> >
> > \>>> Sending CLUSTER MEET messages to join the cluster
> >
> > Waiting for the cluster to join....
>
> 看到这句话sending cluster meet messages to join the cluster，似乎是要在其他服务器节点上发送一个指令来使节点加入集群，通过查询资料，问题解决如下：
>
> > 登陆其他服务器的每个节点，执行cluster meet 47.106.171.169 7000该命令使节点加入集群。
> >
> > 执行完毕后，创建集群的服务就正常完成了。
> >
> > \>>> Performing Cluster Check (using node 172.18.227.228:7000)
> >
> > M: e149285ee4d12163246957c44a61f2b69c7a9f84 172.18.227.228:7000
> >
> >   slots:0-4095 (4096 slots) master
> >
> > M: e7b1c3d6246dfa3c80b4757d68013d961049adf6 172.18.227.228:7001
> >
> >   slots:12288-16383 (4096 slots) master
> >
> > M: c1bfad3e38fb373876760c1a3d80de187c8babf8 172.18.227.228:7002
> >
> >   slots: (0 slots) master
> >
> >   replicates 6dd555df63bca08798b875beb262416a224ef5da
> >
> > M: fcb64f09ebdeecc920f11b5b1f40e2d3a8f707d9 120.24.65.72:7003
> >
> >   slots:4096-8191 (4096 slots) master
> >
> > M: b25cefeee9dcbed0f45d91170b43e5dacc09d74f 120.24.65.72:7004
> >
> >   slots: (0 slots) master
> >
> >   replicates e149285ee4d12163246957c44a61f2b69c7a9f84
> >
> > M: 1068f989b246c75e2843d57da99bc40cf9fcc690 120.24.65.72:7005
> >
> >   slots: (0 slots) master
> >
> >   replicates e7b1c3d6246dfa3c80b4757d68013d961049adf6
> >
> > M: 6dd555df63bca08798b875beb262416a224ef5da 120.25.251.133:7006
> >
> >   slots:8192-12287 (4096 slots) master
> >
> > M: fcd514823f8ce7349db5f3dd92bece36905f0cd1 120.25.251.133:7007
> >
> >   slots: (0 slots) master
> >
> >   replicates fcb64f09ebdeecc920f11b5b1f40e2d3a8f707d9
> >
> > M: 64da681163d187c5aa616b2767619ba200cb77e6 120.25.251.133:7008
> >
> >   slots: (0 slots) master
> >
> >   replicates e149285ee4d12163246957c44a61f2b69c7a9f84
> >
> > [OK] All nodes agree about slots configuration.
> >
> > \>>> Check for open slots...
> >
> > \>>> Check slots coverage...
> >
> > [OK] All 16384 slots covered.
>
> 然后查看下集群节点：
>
> > [root@lyq1 ~]# redis-cli -h 172.18.227.228 -c -p 7000
> >
> > 172.18.227.228:7000> cluster nodes
> >
> > 6dd555df63bca08798b875beb262416a224ef5da 120.25.251.133:7006 master - 0 1614742281009 7 connected 8192-12287
> >
> > 1068f989b246c75e2843d57da99bc40cf9fcc690 120.24.65.72:7005 slave e7b1c3d6246dfa3c80b4757d68013d961049adf6 0 1614742280509 6 connected
> >
> > fcd514823f8ce7349db5f3dd92bece36905f0cd1 120.25.251.133:7007 slave fcb64f09ebdeecc920f11b5b1f40e2d3a8f707d9 0 1614742277000 8 connected
> >
> > e149285ee4d12163246957c44a61f2b69c7a9f84 172.18.227.228:7000 myself,master - 0 0 1 connected 0-4095
> >
> > 64da681163d187c5aa616b2767619ba200cb77e6 120.25.251.133:7008 slave e149285ee4d12163246957c44a61f2b69c7a9f84 0 1614742282012 1 connected
> >
> > e7b1c3d6246dfa3c80b4757d68013d961049adf6 172.18.227.228:7001 master - 0 1614742280004 2 connected 12288-16383
> >
> > fcb64f09ebdeecc920f11b5b1f40e2d3a8f707d9 120.24.65.72:7003 master - 0 1614742278002 4 connected 4096-8191
> >
> > c1bfad3e38fb373876760c1a3d80de187c8babf8 172.18.227.228:7002 slave 6dd555df63bca08798b875beb262416a224ef5da 0 1614742283011 7 connected
> >
> > b25cefeee9dcbed0f45d91170b43e5dacc09d74f 120.24.65.72:7004 slave e149285ee4d12163246957c44a61f2b69c7a9f84 0 1614742279506 5 connected
>
> 因为总共有3台服务器，9个节点，这里是4个master 5个slave
>
> 2、重新创建redis集群报错：ERR Slot 0 is already busy（redis::CommandError）
>
> 这个是因为删除集群配置重新创建时没清除干净
>
> 连上每个节点执行flushall和cluster reset命令，然后重新创建
>
> 3、使用check检查集群状态时报错：[ERR] Not all 16384 slots are covered by nodes.
>
> 这个是有节点挂掉了，slot分布不均导致。
>
> 通过cluster nodes查看节点状态，找出异常节点
>
> 通过官方推荐的redis-trib.rb fix来修复相关节点