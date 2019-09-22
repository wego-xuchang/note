## Redis

### 一、redis的介绍

#### 1、什么是NoSql

​	为了解决高并发、高可扩展（集群）、高可用（不能宕机）、大数据存储问题而产生的数据库解决方案，就是NoSql数据库。

​	NoSql  ：全称 not only sql ,非关系型数据库。可以作为关系型数据库的一个很好的补充。不能替代。

#### 2、NoSql数据库分类

##### 2.1 键值(Key-Value)存储数据库

​	相关产品： Tokyo Cabinet/Tyrant、**Redis**、Voldemort、Berkeley DB。

​	典型应用：内容缓存，主要用于处理大量数据的高访问负载。

​	数据模型：一系列键值对

​	优势：快速查询

​	劣势：存储的数据缺少结构化



##### 2.2 列存储数据库

​	相关产品：Cassandra, **HBase**, Riak

​	典型应用：分布式的文件系统

​	数据模型：以列簇式存储，将同一列数据存在一起

​	优势：查找速度快，可扩展性强，更容易进行分布式扩展

​	劣势：功能相对局限

##### 2.3 文档型数据库

​	相关产品：CouchDB、**MongoDB**

​	典型应用：Web应用（与Key-Value类似，Value是结构化的）

​	数据模型：一系列键值对

​	优势：数据结构要求不严格

​	劣势：查询性能不高，而且缺乏统一的查询语法



##### 2.4  图形(Graph)数据库

​	相关数据库：Neo4J、InfoGrid、Infinite Graph

​	典型应用：社交网络

​	数据模型：图结构

​	优势：利用图结构相关算法。

​	劣势：需要对整个图做计算才能得出结果，不容易做分布式的集群方案。



#### 3、什么是redis

​	Redis是用C语言开发的一个开源的高性能键值对（**key-value**）数据库（nosql），应用在缓存。它通过提供多种键值数据类型来适应不同场景下的存储需求，目前为止Redis支持的键值数据类型有5种。

如下：

字符串类型 (String) 

散列类型（hash）

列表类型(List)

集合类型(set)

有序集合类型(SortedSet)

#### 4、redis的应用场景

缓存

分布式集群架构中的session分离

任务队列（秒杀、抢购、12306等等）

应用排行榜（SortedSet）

网站访问统计

数据过期处理(expire)



### 二、Redis的基本操作



##### 1、Redis五种数据类型

##### 1.1 字符串类型 (String) 

String：key-value	

redis命令不区分大小写，但是key区分的 

redis中的数据都是字符串。

redis是单线程，（不适合存储比较大的数据）

使用incr  命令，如果key 不存在，会自动创建key 并自动+1.

 redis中所有的数据都是字符串。

 set key value 设置值

 get key 获取值

 incr key 加一

 decr key 减一



##### 1.2 散列类型（hash）

Hash: key-field-value

​    相当于一个key 对应一个map (map中又是key- value)，

​    应用归类

​	hset  key field value

​    hget  key field 

​	hincrby key field num

##### 1.3 列表类型(List)

List

​	List是有顺序可重复(数据结构中的：双链表，队列)

​	可作为链表 ，从左添加元素  也可以从右添加元素。

​    lpush list a b c d    (从左添加元素)

​    rpush list 1 2 3 4    (从右边添加元素)

​    lrange list 0 -1 (从0 到 -1 元素查看：也就表示查看所有)

​    lpop list （从左边取，删除）

​    rpop list  (从右边取，删除)

##### 1.4 集合类型(set)

Set

  Set无顺序，不能重复

  sadd set1 a b c d d (向set1中添加元素) 元素不重复

  smembers set1 （查询元素）

  srem set1 a （删除元素）

 SortedSet（zset）

​	**有顺序，不能重复**

​	适合做排行榜 排序需要一个分数属性

##### 1.5 有序集合类型(SortedSet)

zadd zset1 9 a 8 c 10 d 1 e   （添加元素 zadd key score member ）

(ZRANGE key start stop [WITHSCORES])(查看所有元素：zrange key  0  -1  withscores) 

如果要查看分数，加上withscores.

zrange zset1 0 -1 (从小到大)

zrevrange zset1 0 -1 (从大到小)

zincrby zset2 score member (对元素member 增加 score)

##### 1.6 key 命令

expire key second  (设置key的过期时间)

ttl key (查看剩余时间)（-2 表示不存在，-1 表示已被持久化，正数表示剩余的时间）

persist key (清除过期时间，也即是持久化 持久化成功体提示 1 不成功0)。

del key: 删除key  

EXISTS key

若key存在，返回1，否则返回0。

select 0 表示：选择0号数据库。默认是0号数据库

### 三、持久化方案

#### 1、Redis持久化方案

​	

​	Redis 数据都放在内存中。如果机器挂掉，内存的数据就不存在。

​	需要做持久化，讲内存中的数据保存在磁盘，下一次启动的时候就可以恢复数据到内存中。



##### 	2.1 RDB   快照形式  

定期将当前时刻的数据保存磁盘中，会产生一个dump.rdb文件

特点：会存在数据丢失，性能较好，数据备份。

redis 默认开启RDB

如下图：redis.conf中默认设置了保存规则及时间间隔



![img](https://qqadapt.qpic.cn/txdocpic/0/6c2b1054c6fd29c534e71a244ba0e557/0?_type=png)



##### 	2.2 AOF  

 append only file  (所有对redis的操作命令记录在aof文件中)，恢复数据，重新执行一遍即可。

特点：每秒保存，数据比较完整，耗费性能。	

AOF开启设置：

修改 redis.conf 文件  如下图：

将appendonly 设置为yes

![img](https://qqadapt.qpic.cn/txdocpic/0/17292e455d0dae368a4e36937b7a021e/0?_type=png)



同时开启两个持久化方案，则按照 AOF的持久化放案恢复数据。

>  默认是按照rdb的方式恢复数据，如果开启了AOF，就是用AOF恢复数据，数据是存在于/usr/local/redis/bin/appendonly.aof文件中



### 四、环境搭建

#### 1、单机版

##### 1.1 docker搭建

###### 查找镜像

```java
[root@VM_0_13_centos ~]# docker search redis
NAME                             DESCRIPTION                                     STARS               OFFICIAL            AUTOMATED
redis                            Redis is an open source key-value store that…   7039                [OK]                
bitnami/redis                    Bitnami Redis Docker Image                      114                                     [OK]
sameersbn/redis                                                                  75                                      [OK]
grokzen/redis-cluster            Redis cluster 3.0, 3.2, 4.0 & 5.0               50                                      
...

```

###### 下载镜像

```
docker pull redis:需要的版本
```

等待下载完成后，我们就可以在本地镜像列表里查到REPOSITORY为redis

###### 查看镜像

```java
[root@VM_0_13_centos ~]# docker images redis
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
redis               latest              a4fe14ff1981        6 weeks ago         95MB

```

###### 运行容器

```java
docker run -d -p 6379:6379 --name 自定义名字 镜像id
```

###### 查看容器启动情况

```java
[root@VM_0_13_centos ~]#docker ps
CONTAINER ID   IMAGE        COMMAND                 ...   PORTS                      NAMES
43f7a65ec7f8   redis:3.2    "docker-entrypoint.sh"  ...   0.0.0.0:6379->6379/tcp     agitated_cray
```

###### 连接、查看容器

使用redis镜像执行redis-cli命令连接到刚启动的容器

```java
docker exec -it 43f7a65ec7f8 redis-cli
localhost:6379> info
# Server
redis_version:3.2.0
redis_git_sha1:00000000
redis_git_dirty:0
redis_build_id:f449541256e7d446
redis_mode:standalone
os:Linux 4.2.0-16-generic x86_64
arch_bits:64
multiplexing_api:epoll
```



##### 1.2 Linux搭建

###### Redis的下载

​       官网地址：http://redis.io/

​       下载地址：http://download.redis.io/releases/redis-3.0.0.tar.gz

###### Redis的安装

 安装redis需要c语言的编译环境,如果没有gcc需要在线安装。如下命令：

```java
 yum -y install gcc-c++ 
```



如果有GCC环境，只需输入命令：

```java
[root@localhost ~]# gcc   

出现 ：gcc: no input files  表示安装成功。
```





安装步骤：

第一步：将redis的源码包上传到linux系统。

第二步：解压缩redis的源码包。

第三步：进行编译。 cd到解压后的目录 输入命令：make   

第四步：进行安装。 输入命令：make install PREFIX=/usr/local/redis

PREFIX 必须是大写的。

![img](file:///C:/Users/No/AppData/Local/Temp/msohtmlclip1/01/clip_image001.png)

第五步：检查目录是否存在。

在/usr/local/redis 下 有bin 说明安装成功。

 

######  连接redis

######  redis服务端启动

 前端启动

[root@localhost bin]# ./redis-server 

后台启动：

第一步：把/root/redis-3.0.0/redis.conf复制到/usr/local/redis/bin目录下

  [root@localhost redis-3.0.0]# cp redis.conf /usr/local/redis/bin/

第二步：使用vim命令修改redis.conf配置文件 将daemonize no修改为daemonize yes 

第三步：输入启动命令 

   [root@localhost bin]# ./redis-server redis.conf

 第四步：检查redis进程：

  [root@localhost bin]# ps -ef|grep redis

前端启动，不能更换终端，影响下一步操作。而后台启动，只在进程中悄悄启动。

推荐使用后台启动。

 

###### 客户端Redis-cli连接redis

 使用Redis-cli建立连接：

 [root@localhost bin]# ./redis-cli 

 默认连接localhost运行在6379端口的redis服务。

[root@localhost bin]# ./redis-cli -h 192.168.25.153 -p 6379

-h：连接的服务器的地址

-p：服务的端口号



 

使用redis的桌面程序建立连接

先安装：

![1561475211721](C:\Users\No\AppData\Roaming\Typora\typora-user-images\1561475211721.png)

###### 退出连接：

​	第一种：

​       [root@localhost bin]# ./redis-cli 

​       127.0.0.1:6379> quit

​       第二种：

​       [root@localhost bin]# ./redis-cli 

​       127.0.0.1:6379> exit

​       第三种：CTR+C

​       [root@localhost bin]#

###### 关闭Redis服务 

 第一种：通过连接上客户端进行关闭，使用shutdown 命令。

 或者：cd 到redis的bin 目录  再执行以下：

[root@localhost bin]# ./redis-cli shutdown

 第二种：使用 kill 命令。

找到对应的redis的进程id 然后使用命令：(pid为进程id)

kill -9 pid

#### 2、集群版

> 集群后期文章再总结

