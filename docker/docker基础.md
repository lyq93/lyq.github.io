## Docker基础

### 简介

- Docker背景

> 一门技术的出现，总有其原因，为什么会出现docker
>
> 开发工程师与运维工程师最常扯皮的问题是什么？开发交付的war在运维部署的时候起不来
>
> 那么问题来了，开发人员认为我在本机没问题，是环境问题。运维人员认为开发提供的war包有问题，环境没问题。就此扯皮不清。
>
> docker的出现，就是为了解决这个问题，出现“在我的机器上正常工作”在运维部署却失败的问题，基本上归类于环境和配置等问题。那么如果开发直接交付一整套环境呢？运维只需要把环境复制一份就部署就可以避免类似的问题。这就是docker容器出现的原因

- docker的组成

> 1、镜像
>
> 镜像是开发人员打包的整套环境的模版，包括代码、配置及运行环境
>
> 2、容器
>
> 容器是镜像的实例，类比Java里面的对象。类相当于是镜像，对象相当于是容器。所以一个镜像可以产生多个容器
>
> 3、仓库
>
> 仓库是镜像存放的地方，docker从仓库把镜像拿到生成容器部署

- docker的安装

> ```
> yum install -y yum-utils
> 
> yum-config-manager \
>     --add-repo \
>     https://download.docker.com/linux/centos/docker-ce.repo
>     
> yum install docker-ce docker-ce-cli containerd.io
> 
> 然后运行docker version检查是否安装成功
> [root@lyq1 ~]# docker version
> Client: Docker Engine - Community
>  Version:           20.10.5
>  API version:       1.41
>  Go version:        go1.13.15
>  Git commit:        55c4c88
>  Built:             --
>  OS/Arch:           linux/amd64
>  Context:           default
>  Experimental:      true
> ```

- docker国内镜像配置

> 配置阿里云镜像：
>
> 1、注册阿里云账号，登陆
>
> 2、找到镜像中心-->镜像加速器
>
> 3、根据给出的操作指令执行相应命令
>
> sudo mkdir -p /etc/docker 
>
> sudo tee /etc/docker/daemon.json <<-'EOF' 
>
> {  
>
> "registry-mirrors": ["https://qnemhwwh.mirror.aliyuncs.com"] 
>
> } 
>
> EOF 
>
> sudo systemctl daemon-reload 
>
> sudo systemctl restart docker

- docker底层原理

> 1、docker run一个镜像，内部做了什么？
>
> 客户端会先去找运行docker的主机是否有对应的镜像，如果没有就从远程仓库中下载镜像到本地docker主机，然后创建容器运行
>
> 2、docker比虚拟机快的原因
>
> - docker比虚拟机更少抽象层，没有虚拟化硬件资源
> - docker用的是宿主机的内核，而不像虚拟机可以安装不同的操作系统

### 常用命令

- 帮助命令

> ```
> docker Version docker的版本信息
> 
> docker info docker的详细信息
> 
> docker --help docker的帮助文档
> ```

- 镜像命令

> docker images 列出本机上的镜像
>
> ```
> -a 列出本地所有的镜像(含中间映射层)
> -q 只显示镜像ID
> --digests 显示镜像的摘要信息
> --no-trunc 显示完整的镜像信息
> ```

> docker search xx 搜索某镜像
>
> > ```
> > --no-trun 显示完整的镜像描述
> > -s 列出收藏数不小于指定值的镜像
> > --automated 只列出 automated build类型的镜像
> > ```

> Docker pull xx 从仓库拉取镜像
>
> Docker rmi xx 删除镜像
>
> > docker rm -f 镜像ID --删除单个镜像
> >
> > docker rm -f 镜像名1:TAG 镜像名2:TAG --删除多个镜像
> >
> > docker rmi -f ${docker images -qa} --删除多个镜像

- 容器命令

> docker run [OPTIONS] xx 新建并运行容器
>
> > ```
> > --name="容器新名字":为容器指定一个名称;
> > -d:后台运行容器，并返回容器ID， 也即启动守护式容器;
> > -i:以交互模式运行容器，通常与-t同时使用;
> > -t:为容器重新分配一个伪输入终端，通常与-i同时使用;
> > -P:随机端口映射;
> > -p:指定端口映射，有以下四种格式
> > ip:hostPort:containerPort
> > ip::containerPort
> > hostPort:containerPort
> > containerPort
> > ```

> dockers ps [OPTIONS] 列出当前运行的容器
>
> > ```
> > -a :列出当前所有正在运行的容器+历史上运行过的
> > -|:显示最近创建的容器
> > -n:显示最近n个创建的容器
> > -q :静默模式，只显示容器编号
> > --no-trunc :不截断输出
> > ```

> #### 退出容器
>
> 两种退出方式
>
>  exit 容器停止退出
>
>  ctrl+P+Q 容器不停止退出
>
> #### 启动容器
>
> docker start 容器ID或容器签名
>
> #### 重启容器
>
> docker restart 容器ID或容器签名
>
> #### 停止容器
>
> docker stop 容器ID或容器签名
>
> #### 强制停止容器
>
> docker kill 容器ID或容器签名
>
> #### 删除已停止的容器
>
> docker rm 容器ID -f
>
>  一次性删除多个容器
>
>  docker rm -f $(docker ps -a -q)
>
>  docker ps -a -q | xargs docker rm
>
> ####  查看容器日志
>
> docker logs -f -t --tail 容器ID
>
>  -t 是加入时间戳
>
>  -f 跟随最新的日志打印
>
>  --tail 数字显示最后多少条
>
> #### 查看容器内的进程
>
> docker top 容器ID
>
> #### 查看容器内部细节
>
> docker inspect 容器ID
>
> #### 进入正在运行的容器并以命令行交互
>
> docker exec -it 容器ID bashShell

### docker镜像

- 介绍

> 镜像就是一个可执行的软件包，包括了运行软件所需要的环境以及软件本身。

- unionFS--联合文件系统

> 联合文件系统是docker的基础，文件系统支持修改，提交后相当于是文件系统的层层叠加，可以自定义出基于底层文件的自定义文件。
>
> 也就是说，功能是从下到上逐级继承

- 镜像分层

> bootfs
>
> docker的底层镜像，包含bootloader和kernel，负责加载内核。
>
> rootfs
>
> 包含的就是典型的linux系统中的/etc之类的目录

- 镜像的操作

> docker commit 提交容器副本使之称为一个新的镜像
>
> docker commit -m="提交的描述信息" -a="作者" 容器ID 要创建的目标镜像名:[标签名]

### docker容器数据卷

- 背景

> 为什么有这个东西？核心的需求是容器的数据需要做持久化，否则关闭容器数据就没了

- 实现

> 1、直接命令
>
> docker run -it -v /宿主机绝对路径目录:/容器内目录 镜像名
>
> 这种方式移植性不好，每台主机的路径目录有可能不一样
>
> 2、数据卷容器
>
> 1、使用dockerfile的volume指令给镜像添加一个或多个数据卷
>
> 2、构建生成镜像
>
> 3、新建容器并运行
>
> 4、其余容器使用--volumes -from命令继承父容器
>
> 这种方式实现容器间的传递共享

### dockerfile介绍

- 保留字指令

> 1、FROM
>
> 基础镜像，可以理解为当前新镜像继承的父镜像
>
> 2、MAINTAINER
>
> 镜像维护者的名字和邮箱
>
> 3、RUN
>
> 容器构建时需要运行的命令
>
> 4、EXPOSE
>
> 容器对外暴露的端口
>
> 5、ADD
>
> 将宿主机目录下的文件拷贝进镜像，ADD包含解压tar功能
>
> 6、COPY
>
> 类似于ADD命令，但不具备解压功能。
>
> 7、VOLUME
>
> 容器数据卷，用于数据保存、持久化
>
> 8、CMD
>
> 指定容器启动时需要运行的命令，dockerfile中可以有多个CMD指令，但是只有最后一个会生效。而且dockerfile中的CMD指令会被docker run中的参数替代
>
> 9、ENTRYPOINT
>
> 指定容器启动时需要运行的命令，同CMD一样。但是不同点是，ENTRYPOINT对于运行参数是追加方式，CMD是替换/覆盖。
>
> 10、ONBUILD
>
> 当构建一个被继承的dockerfile时，父镜像在被子镜像集成后，父镜像的ONBUILD会被触发
>
> 11、WORKDIR
>
> 在创建容器后，终端默认登陆进行来的目录
>
> 12、ENV
>
> 构建过程中设置环境变量

- 一个简单的dockerfile文件

> ```
> FROM centos
> MAINTAINER lyq<407539854@qq.com>
> 
> ENV MYPATH /usr/local
> WORKDIR $MYPATH
> 
> RUN yum -y install vim
> RUN yum -y install net-tools
> 
> EXPOSE 80
> 
> CMD echo $MYPATH
> CMD echo "success--------------ok"
> CMD /bin/bash
> ```
>
> 从仓库拉下来的centos镜像没有vim和net-tools，我们需要让启动的容器包含这两个功能的话，使用FROM保留关键字继承现有的centos镜像，然后通过RUN指令执行对面的命令，完成一个新的镜像文件。
>
> 所以，镜像文件是层层叠加的
>
> 构建镜像：docker build -t 新镜像名字:TAG
>
> 运行：docker run -it 新镜像名字:TAG