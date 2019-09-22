## Docker -基本使用

> 具体安装步骤请参考阿里云[docker安装](https://yq.aliyun.com/articles/110806?spm=5176.8351553.0.0.655e1991fULRE7) 

### 一、Docker

#### 1、介绍：

Docker 是一个开源的应用容器引擎，基于 [Go 语言](https://www.runoob.com/go/go-tutorial.html) 并遵从Apache2.0协议开源。

Docker 可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中，然后发布到任何流行的 Linux 机器上，也可以实现虚拟化。

容器是完全使用沙箱机制，相互之间不会有任何接口（类似 iPhone 的 app）,更重要的是容器性能开销极低。

Docker 从 17.03 版本之后分为 CE（Community Edition: 社区版） 和 EE（Enterprise Edition: 企业版），我们用社区版就可以了。



#### 2、Docker的应用场景

Web 应用的自动化打包和发布。

自动化测试和持续集成、发布。

在服务型环境中部署和调整数据库或其他的后台应用。

从头编译或者扩展现有的OpenShift或Cloud Foundry平台来搭建自己的PaaS环境。



#### 3、为什么要使用Docker

容器除了运行其中应用外，基本不消耗额外的系统资源，使得应用的性能很高，同时系统的开销尽量小。传统虚拟机方式运行 10 个不同的应用就要起 10 个虚拟机，而Docker 只需要启动 10 个隔离的应用即可。

具体说来，Docker 在如下几个方面具有较大的优势。

**3.1  更快速的交付和部署**

对开发和运维（devop）人员来说，最希望的就是一次创建或配置，可以在任意地方正常运行。

开发者可以使用一个标准的镜像来构建一套开发容器，开发完成之后，运维人员可以直接使用这个容器来部署代码。 Docker 可以快速创建容器，快速迭代应用程序，并让整个过程全程可见，使团队中的其他成员更容易理解应用程序是如何创建和工作的。 Docker 容器很轻很快！容器的启动时间是秒级的，大量地节约开发、测试、部署的时间。

**3.2 更高效的虚拟化**

Docker 容器的运行不需要额外的 hypervisor 支持，它是内核级的虚拟化，因此可以实现更高的性能和效率。

**3.3  更轻松的迁移和扩展**

Docker 容器几乎可以在任意的平台上运行，包括物理机、虚拟机、公有云、私有云、个人电脑、服务器等。 这种兼容性可以让用户把一个应用程序从一个平台直接迁移到另外一个。

**3.4  更简单的管理**

使用 Docker，只需要小小的修改，就可以替代以往大量的更新工作。所有的修改都以增量的方式被分发和更新，从而实现自动化并且高效的管理。

#### 4、Docker vs VM

从下图可以看出，VM是一个运行在宿主机之上的完整的操作系统，VM运行自身操作系统会占用较多的CPU、内存、硬盘资源。Docker不同于VM，只包含应用程序以及依赖库，基于libcontainer运行在宿主机上，并处于一个隔离的环境中，这使得Docker更加轻量高效，启动容器只需几秒钟之内完成。由于Docker轻量、资源占用少，使得Docker可以轻易的应用到构建标准化的应用中。但Docker目前还不够完善，比如隔离效果不如VM，共享宿主机操作系统的一些基础库等；网络配置功能相对简单，主要以桥接方式为主；查看日志也不够方便灵活。

![img](https://qqadapt.qpic.cn/txdocpic/0/f81d864895401914cfd5d711f3829b5d/0)

Docker 在容器的基础上，进行了进一步的封装，从文件系统、网络互联到进程隔离等等，极大的简化了容器的创建和维护。使得 Docker 技术比虚拟机技术更为轻便、快捷。

作为一种新兴的虚拟化方式，Docker 跟传统的虚拟化方式相比具有众多的优势。Docker 容器的启动可以在秒级实现，这相比传统的虚拟机方式要快得多；Docker 对系统资源的利用率很高，一台主机上可以同时运行数千个 Docker 容器。



#### 5、Docker 架构

Docker 使用客户端-服务器 (C/S) 架构模式，使用远程API来管理和创建Docker容器。

Docker 容器通过 Docker 镜像来创建。

容器与镜像的关系类似于面向对象编程中的对象与类。

![img](https://qqadapt.qpic.cn/txdocpic/0/089b5a1c073fd1cfa8be3cabf32cee19/0)



| Docker 镜像(Images)    | Docker 镜像是用于创建 Docker 容器的模板。                    |
| ---------------------- | ------------------------------------------------------------ |
| Docker 容器(Container) | 容器是独立运行的一个或一组应用。                             |
| Docker 客户端(Client)  | Docker 客户端通过命令行或者其他工具使用 Docker API (<https://docs.docker.com/reference/api/docker_remote_api>) 与 Docker 的守护进程通信。 |
| Docker 主机(Host)      | 一个物理或者虚拟的机器用于执行 Docker 守护进程和容器。       |
| Docker 仓库(Registry)  | Docker 仓库用来保存镜像，可以理解为代码控制中的代码仓库。Docker Hub(<https://hub.docker.com>) 提供了庞大的镜像集合供使用。 |
| Docker Machine         | Docker Machine是一个简化Docker安装的命令行工具，通过一个简单的命令行即可在相应的平台上安装Docker，比如VirtualBox、 Digital Ocean、Microsoft Azure。 |





### 二、Dockerfile 构建

Dockerfile由一行行命令语句组成，并且支持以#开头的注释行。

一般的，Dockerfile分为四部分：基础镜像信息，维护者信息，镜像操作指令和容器启动时执行指令。

> dockerfile可以说是docker学习部分的精华所在。我这里总结的dockerfile的命令，具体操作可以参照docker hub

#### 1、 指令

指令的一般格式为 INSTRUCTION arguments，指令包括 FROM、MAINTAINER、RUN 等。

##### 1.1 FROM

格式为 FROM <image>或FROM <image>:<tag>。

第一条指令必须为 FROM 指令。并且，如果在同一个Dockerfile中创建多个镜像时，可以使用多个 FROM 指令（每个镜像一次）。

MAINTAINER

格式为 MAINTAINER <name>，指定维护者信息。

#####  1.2 RUN

格式为 RUN <command> 或 RUN ["executable", "param1", "param2"]。



前者将在 shell 终端中运行命令，即 /bin/sh -c；后者则使用 exec 执行。指定使用其它终端可以通过第二种方式实现，例如 RUN ["/bin/bash", "-c", "echo hello"]。



每条 RUN 指令将在当前镜像基础上执行指定命令，并提交为新的镜像。当命令较长时可以使用 \ 来换行。

##### 1.3 CMD

支持三种格式

CMD ["executable","param1","param2"] 使用 exec 执行，推荐方式；

CMD command param1 param2 在 /bin/sh 中执行，提供给需要交互的应用；

CMD ["param1","param2"] 提供给 ENTRYPOINT 的默认参数；

指定启动容器时执行的命令，每个 Dockerfile 只能有一条 CMD 命令。如果指定了多条命令，只有最后一条会被执行。

如果用户启动容器时候指定了运行的命令，则会覆盖掉 CMD 指定的命令。



##### 1.4 EXPOSE

格式为 EXPOSE <port> [<port>...]。

告诉 Docker 服务端容器暴露的端口号，供互联系统使用。在启动容器时需要通过 -P，Docker 主机会自动分配一个端口转发到指定的端口。



#####  1.5 ENV

格式为 ENV <key> <value>。 指定一个环境变量，会被后续 RUN 指令使用，并在容器运行时保持。

例如

ENV PG_MAJOR 9.3

ENV PG_VERSION 9.3.4

RUN curl -SL <http://example.com/postgres-$PG_VERSION.tar.xz> | tar -xJC /usr/src/postgress && …

ENV PATH /usr/local/postgres-$PG_MAJOR/bin:$PATH

##### 1.6 ADD

格式为 ADD <src> <dest>。

该命令将复制指定的 <src> 到容器中的 <dest>。 其中 <src> 可以是Dockerfile所在目录的一个相对路径；也可以是一个 URL；还可以是一个 tar 文件（自动解压为目录）。



##### 1.7  COPY

格式为 COPY <src> <dest>。

复制本地主机的 <src>（为 Dockerfile 所在目录的相对路径）到容器中的 <dest>。

当使用本地目录为源目录时，推荐使用 COPY。



##### 1.8 ENTRYPOINT

两种格式：

ENTRYPOINT ["executable", "param1", "param2"]

ENTRYPOINT command param1 param2（shell中执行）。

配置容器启动后执行的命令，并且不可被 docker run 提供的参数覆盖。

每个 Dockerfile 中只能有一个 ENTRYPOINT，当指定多个时，只有最后一个起效。



##### 1.9 VOLUME

格式为 VOLUME ["/data"]。

创建一个可以从本地主机或其他容器挂载的挂载点，一般用来存放数据库和需要保持的数据等。



##### 1.10 USER

格式为 USER daemon。

指定运行容器时的用户名或 UID，后续的 RUN 也会使用指定用户。



当服务不需要管理员权限时，可以通过该命令指定运行用户。并且可以在之前创建所需要的用户，例如：RUN groupadd -r postgres && useradd -r -g postgres postgres。要临时获取管理员权限可以使用 gosu，而不推荐 sudo。



##### 1.11  WORKDIR

格式为 WORKDIR /path/to/workdir。

为后续的 RUN、CMD、ENTRYPOINT 指令配置工作目录。

可以使用多个 WORKDIR 指令，后续命令如果参数是相对路径，则会基于之前命令指定的路径。例如

WORKDIR /a

WORKDIR b

WORKDIR c

RUN pwd

则最终路径为 /a/b/c。



##### 1.12 ONBUILD

格式为 ONBUILD [INSTRUCTION]。

配置当所创建的镜像作为其它新创建镜像的基础镜像时，所执行的操作指令。

例如，Dockerfile 使用如下的内容创建了镜像 image-A。

[...]

ONBUILD ADD . /app/src

ONBUILD RUN /usr/local/bin/python-build --dir /app/src

[...]

如果基于 image-A 创建新的镜像时，新的Dockerfile中使用 FROM image-A指定基础镜像时，会自动执行ONBUILD 指令内容，等价于在后面添加了两条指令。



FROM image-A



Automatically run the following

ADD . /app/src

RUN /usr/local/bin/python-build --dir /app/src

使用 ONBUILD 指令的镜像，推荐在标签中注明，例如 ruby:1.9-onbuild。

### 三、常用命令



#### 1、docker-server操作

```java
systemctl start docker
systemctl stauts docker
systemctl restart docker
systemctl stop docker
```

#### 2、镜像仓库

##### 2.1 查找docker镜像

```
docker search image_name
```

##### 2.2 拉取docker镜像

```
docker pull image_name:版本
```

##### 2.3查看宿主机上的镜像

Docker镜像保存在/var/lib/docker目录下:

```
docker images
```

执行docker pull centos会将Centos这个仓库下面的所有镜像下载到本地repository。

##### 2.4 删除镜像

```
docker rmi  docker.io/tomcat:7.0.77-jre7   
或者  docker rmi b39c68b7af30
```



#### 3、登陆/登出Docker镜像仓库

##### 3.1 docker login 

登陆到一个Docker镜像仓库，如果未指定镜像仓库地址，默认为官方仓库 Docker Hub

##### 3.2 docker logout 

 登出一个Docker镜像仓库，如果未指定镜像仓库地址，默认为官方仓库 Docker Hub

语法

```
docker login [OPTIONS] [SERVER]
docker logout [OPTIONS] [SERVER]
```

OPTIONS说明：

- **-u :**登陆的用户名
- **-p :**登陆的密码

实例

登陆到Docker Hub

```
docker login -u 用户名 -p 密码
```

登出Docker Hub

```
docker logout
```



#### 4、本地上传镜像

##### **4.1 docker push ** 

将本地的镜像上传到镜像仓库,要先登陆到镜像仓库

语法

```
docker push [OPTIONS] NAME[:TAG]
```

OPTIONS说明：

- **--disable-content-trust :**忽略镜像的校验,默认开启

实例

上传本地镜像myapache:v1到镜像仓库中。

```
docker push myapache:v1
```



#### 5、容器操作

##### 5.1 查看当前运行容器

```
docker ps
```

##### 5.2 查看所有容器

```
docker ps -a
```



#### 6、容器生命周期管理

##### 6.1 创建运行新的容器

docker run ：创建一个新的容器并运行一个命令



```
docker run [OPTIONS] IMAGE [COMMAND] [ARG...]
```

OPTIONS说明：

- **-a stdin:** 指定标准输入输出内容类型，可选 STDIN/STDOUT/STDERR 三项；
- **-d:** 后台运行容器，并返回容器ID；
- **-i:** 以交互模式运行容器，通常与 -t 同时使用；
- **-p:** 端口映射，格式为：**主机(宿主)端口:容器端口**
- **-t:** 为容器重新分配一个伪输入终端，通常与 -i 同时使用；
- **-****-name="nginx-lb":** 为容器指定一个名称；
- **--dns 8.8.8.8:** 指定容器使用的DNS服务器，默认和宿主一致；
- **--dns-search example.com:** 指定容器DNS搜索域名，默认和宿主一致；
- **-h "mars":** 指定容器的hostname；
- **-e username="ritchie":** 设置环境变量；
- **--env-file=[]:** 从指定文件读入环境变量；
- **--cpuset="0-2" or --cpuset="0,1,2":** 绑定容器到指定CPU运行；
- **-m :**设置容器使用内存最大值；
- **--net="bridge":** 指定容器的网络连接类型，支持 bridge/host/none/container: 四种类型；
- **--link=[]:** 添加链接到另一个容器；
- **--expose=[]:** 开放一个端口或一组端口；



使用docker镜像nginx:latest以后台模式启动一个容器,并将容器命名为mynginx。

```
docker run --name mynginx -d nginx:latest
```



##### 6.2 创建新的容器

docker create ：创建一个新的容器但不启动它

用法同 [docker run](https://www.runoob.com/docker/docker-run-command.html)

语法

```
docker create [OPTIONS] IMAGE [COMMAND] [ARG...]
```



##### 6.3 启动、停止、重启容器

```
docker start container_name/container_id
docker stop container_name/container_id
docker restart container_name/container_id
```



##### 6.4 杀掉运行中的容器

docker kill :杀掉一个运行中的容器。

```
docker kill [OPTIONS] CONTAINER [CONTAINER...]
```



##### 6.5 后台启动容器并进入容器

后台启动一个容器后，如果想进入到这个容器，可以使用attach命令：

```
docker attach container_name/container_id
```

##### 6.6 删除容器

```
docker rm container_name/container_id
```



##### 6.7 暂停/恢复容器中所有的进程

**docker pause** :暂停容器中所有的进程。

**docker unpause** :恢复容器中所有的进程。

语法

```
docker pause [OPTIONS] CONTAINER [CONTAINER...]
docker unpause [OPTIONS] CONTAINER [CONTAINER...]
```

实例

暂停数据库容器db01提供服务。

```
docker pause db01
```

恢复数据库容器db01提供服务。

```
docker unpause db01
```



##### 6.8 运行的容器中执行命令

**docker exec ：**在运行的容器中执行命令

语法

```
docker exec [OPTIONS] CONTAINER COMMAND [ARG...]
```

OPTIONS说明：

- **-d :**分离模式: 在后台运行
- **-i :**即使没有附加也保持STDIN 打开
- **-t :**分配一个伪终端

实例

在容器 mynginx 中以交互模式执行容器内 /root/runoob.sh 脚本:

```
runoob@runoob:~$ docker exec -it mynginx 
  /bin/sh /root/runoob.shhttp://www.runoob.com/
```

在容器 mynginx 中开启一个交互模式的终端:

```
runoob@runoob:~$ docker exec -i -t  
mynginx /bin/bashroot@b1a0703e41e7:/#
```

#### 6、查看Docker信息

info|version

查看当前系统Docker信息

```
docker info
```

显示 Docker 版本信息

docker version :显示 Docker 版本信息。

语法

```
docker version [OPTIONS]
```

OPTIONS说明：

- **-f :**指定返回值的模板文件。



#### 7、容器rootfs命令

##### 7.1 容器创建新的镜像

**docker commit :**从容器创建一个新的镜像。

语法

```
docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]
```

OPTIONS说明：

- **-a :**提交的镜像作者；
- **-c :**使用Dockerfile指令来创建镜像；
- **-m :**提交时的说明文字；
- **-p :**在commit时，将容器暂停。

实例

将容器a404c6c174a2 保存为新的镜像,并添加提交人信息和说明信息。

```
runoob@runoob:~$ docker commit -a "runoob.com" -m "my apache" a404c6c174a2  mymysql:v1 
sha256:37af1236adef1544e8886be23010b66577647a40bc02c0885a6600b33ee28057
runoob@runoob:~$ docker images mymysql:v1
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZEmy
mysql             v1                  37af1236adef        15 seconds ago      329 MB
```



##### 7.2 容器与主机之间的数据拷贝

**docker cp :用于容器与主机之间的数据拷贝。**

语法

```
docker cp [OPTIONS] CONTAINER:SRC_PATH DEST_PATH|-
docker cp [OPTIONS] SRC_PATH- CONTAINER:DEST_PATH
```

**OPTIONS说明：**

- **-L :保持源目标中的链接**

**docker cp :**用于容器与主机之间的数据拷贝。

检查容器里文件结构

**docker diff :** 检查容器里文件结构的更改。

语法

```
docker diff [OPTIONS] CONTAINER
```

#### 8、本地镜像管理

##### 8.1 列出本地镜像

**docker images :** 列出本地镜像。

语法

```
docker images [OPTIONS] [REPOSITORY[:TAG]]
```

OPTIONS说明：

- **-a :**列出本地所有的镜像（含中间映像层，默认情况下，过滤掉中间映像层）；
- **--digests :**显示镜像的摘要信息；
- **-f :**显示满足条件的镜像；
- **--format :**指定返回值的模板文件；
- **--no-trunc :**显示完整的镜像信息；
- **-q :**只显示镜像ID。



##### 8.2 删除本地镜像

**docker rmi :** 删除本地一个或多少镜像。

语法

```
docker rmi [OPTIONS] IMAGE [IMAGE...]
```

OPTIONS说明：

- **-f :**强制删除；
- **--no-prune :**不移除该镜像的过程镜像，默认移除；



##### 8.3 归档文件中创建镜像

**docker import :** 从归档文件中创建镜像。

语法

```
docker import [OPTIONS] file|URL|- [REPOSITORY[:TAG]]
```

OPTIONS说明：

- **-c :**应用docker 指令创建镜像；
- **-m :**提交时的说明文字

##### 8.4 镜像保存成 tar 归档文件

**docker save :** 将指定镜像保存成 tar 归档文件。

语法

```
docker save [OPTIONS] IMAGE [IMAGE...]
```

OPTIONS 说明：

- **-o :**输出到的文件。



##### 8.5 导入使用 [docker save](https://www.runoob.com/docker/docker-save-command.html) 命令导出的镜像

**docker load :** 导入使用 [docker save](https://www.runoob.com/docker/docker-save-command.html) 命令导出的镜像。

语法

```
docker load [OPTIONS]
```

OPTIONS 说明：

- **-i :**指定导出的文件。
- **-q :**精简输出信息。





### 四、运行常用的容器命令

> 这些都是我平时有用到的容器，可以参考一下，出现问题可以留言互相学习

#### 1、elasticsearch

##### 1.1下载镜像：

这里使用Elasticsearch的版本是6.4.3，同时使用kibana可视化工具使用，对elasticsearch-head感兴趣的可以download使用

```java
	docker pull elasticsearch:6.4.3
    docker pull mobz/elasticsearch-head:5
    docker pull kibana:6.4.3
```

##### 1.2 运行容器 run:

```java
 docker run -it --name elasticsearch -d -p 9200:9200 -p 9300:9300 -p 5601:5601 -e ES_JAVA_OPTS="-Xms256m -Xmx256m" elasticsearch:6.4.3
     
```

>  注意事项
>
>  kibana的container共用elasticsearch的网络
>
> elasticsearch服务有跨域问题，导致elasticsearch-head无法连接到ES，因此需要进入ES容器修改配置

##### 1.3 运行的容器中执行命令:

```java
docker exec -it elasticsearch /bin/bash

vi config/elasticsearch.yml

加入跨域配置

http.cors.enabled: true

http.cors.allow-origin: "*"
    
```

退出容器：exit

 docker restart elasticsearch



```java
docker run -it -d -e ELASTICSEARCH_URL=http://ip:9200 --name kibana --network=container:elasticsearch kibana:6.4.3
```

```java
docker run -it --name elasticsearch-head -d -p 9100:9100 docker.io/mobz/elasticsearch-head:5 
```



​    

#### 2、redis



```
docker run -d -p 6379:6379 --name 自定义名字 镜像id
```

#### 3、rabbitmq



```
docker run -d -p 5672:5672 -p 15672:15672 --name 自定义名字 镜像id
```



#### 4、mysql



```
docker run -p 3306:3306 --name mysql -v /zzyyuse/mysql/conf:/etc/mysql/conf.d -v /zzyyuse/mysql/logs:/logs -v /zzyyuse/mysql/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql:5.6
```

命令说明：

-p 12345:3306：将主机的12345端口映射到docker容器的3306端口。

--name mysql：运行服务名字

-v /zzyyuse/mysql/conf:/etc/mysql/conf.d ：将主机/zzyyuse/mysql录下的conf/my.cnf 挂载到容器的 /etc/mysql/conf.d

-v /zzyyuse/mysql/logs:/logs：将主机/zzyyuse/mysql目录下的 logs 目录挂载到容器的 /logs。

-v /zzyyuse/mysql/data:/var/lib/mysql ：将主机/zzyyuse/mysql目录下的data目录挂载到容器的 /var/lib/mysql 

-e MYSQL_ROOT_PASSWORD=123456：初始化 root 用户的密码。

-d mysql:5.6 : 后台程序运行mysql5.6



```
docker exec -it MySQL运行成功后的容器ID     /bin/bash
```

或者

```
#启动
docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=Lzslov123! -d mysql

#进入容器
docker exec -it mysql bash

#登录mysql
mysql -u root -p
ALTER USER 'root'@'localhost' IDENTIFIED BY 'Lzslov123!';

#添加远程登录用户
CREATE USER 'liaozesong'@'%' IDENTIFIED WITH mysql_native_password BY 'Lzslov123!';
GRANT ALL PRIVILEGES ON *.* TO 'liaozesong'@'%';
```

**数据备份小测试(可以不做)**

docker exec myql服务容器ID sh -c ' exec mysqldump --all-databases -uroot -p"123456" ' > /zzyyuse/all-databases.sql

#### 5、nginx



```
docker run --name 容器名称 -p 8081:80 -d nginx
```

创建目录 nginx, 用于存放后面的相关东西。



```
mkdir -p ~/nginx/www ~/nginx/logs ~/nginx/conf
```

拷贝容器内 Nginx 默认配置文件到本地当前目录下的 conf 目录，容器 ID 可以查看 **docker ps** 命令输入中的第一列：



```
docker cp 6dd4380ba708:/etc/nginx/nginx.conf ~/nginx/conf
```

- **www**: 目录将映射为 nginx 容器配置的虚拟目录。
- **logs**: 目录将映射为 nginx 容器的日志目录。
- **conf**: 目录里的配置文件将映射为 nginx 容器的配置文件。

部署命令



```
$ docker run -d -p 8082:80 --name 容器名称 
-v ~/nginx/www:/usr/share/nginx/html 
-v ~/nginx/conf/nginx.conf:/etc/nginx/nginx.conf 
-v ~/nginx/logs:/var/log/nginx nginx
```

- **-p 8082:80：** 将容器的 80 端口映射到主机的 8082 端口。
- **--name ：**将容器命名。
- **~/nginx/www:/usr/share/nginx/html：**将我们自己创建的 www 目录挂载到容器的 /usr/share/nginx/html。
- **-v ~/nginx/conf/nginx.conf:/etc/nginx/nginx.conf：**将我们自己创建的 nginx.conf 挂载到容器的 /etc/nginx/nginx.conf。
- **-v ~/nginx/logs:/var/log/nginx：**将我们自己创建的 logs 挂载到容器的 /var/log/nginx。

启动以上命令后进入 ~/nginx/www 目录：



```
cd ~/nginx/www
```

创建 index.html 文件，内容如下：

```
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title></title>
</head>
<body>
    <h1>hello world</h1>
</body>
</html>
```







```
docker run -d -p 8082:80 --name nginx-8082 -v /usr/local/nginx/www:/usr/share/nginx/html -v /usr/local/nginx/conf/nginx.conf:/etc/nginx/nginx.conf -v /usr/local/nginx/logs:/var/log/nginx nginx
```





#### 6、fastdfs-nginx

使用docker镜像构建tracker容器（跟踪服务器，起到调度的作用）



```
docker run -d --network=host --name tracker -v /var/fdfs/tracker:/var/fdfs delron/fastdfs tracker
```



使用docker镜像构建storage容器（存储服务器，提供容量和备份服务）



```
docker run -d --network=host --name storage -e TRACKER_SERVER=134.175.30.90:22122 -v /var/fdfs/storage:/var/fdfs -e GROUP_NAME=group1 delron/fastdfs storage
```

docker run -d --network=host --name storage -e TRACKER_SERVER=172.16.102.58:22122 -v /var/fdfs/storage:/var/fdfs -e GROUP_NAME=group1 delron/fastdfs storage



上传一个文件



```
[root@VM_0_13_centos fdfs]# vi  a.txt
[root@VM_0_13_centos fdfs]# /usr/bin/fdfs_upload_file /etc/fdfs/client.conf a.txt
group1/M00/00/00/rBAADVzgLcCALwIQAAAADfGNUoI054.txt
[root@VM_0_13_centos fdfs]# 
```



