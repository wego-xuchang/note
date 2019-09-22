## 基本使用-ElasticSearch



> 说明：本篇文章主要是通过springboot整合es的基本使用基础，详细了解的可以看我上一篇文章：[全文搜索-ElasticSearch](https://juejin.im/post/5d05c9a9f265da1b7401fb61)
>
> 有朋友私信我上一篇没有环境搭建方面的，这里给你细说一下。

### 一、ElasticSearch

[官方文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/intro.html)

#### 1.介绍

Elasticsearch是一个开源的搜索引擎，建立在一个全文搜索引擎库[Apache Lucene™](https://lucene.apache.org/core/)基础之上。 Lucene可以说是当下最先进，高性能，全功能的搜索引擎库 - 无论是开源还是私有。

但是Lucene仅仅是一个库。为了充分发挥其功能，你需要使用Java并将Lucene直接集成到应用程序中。更糟糕的是，您可能需要获得信息检索学位才能了解其工作原理.Lucene *非常*复杂。

Elasticsearch也是使用Java编写的，它的内部使用Lucene做索引与搜索，但是它的目的是使用全文检索变得简单，通过隐藏Lucene的复杂性，取而代之的提供一套简单一致的RESTful API。

然而，Elasticsearch不仅仅是Lucene，并且也不仅仅是一个全文搜索引擎。 它可以被下面这样准确的形容：

- 一个分布式的实时文档存储，*每个字段*可以被索引与搜索
- 一个分布式实时分析搜索引擎
- 能胜任上百个服务节点的扩展，并支持PB级别的结构化或者非结构化数据

Elasticsearch将所有的功能打包成一个单独的服务，这样你可以通过程序与它提供的简单的RESTful API进行通信，可以使用自己喜欢的编程语言充当web客户端，甚至可以使用命令行（去充当这个客户端）。

就Elasticsearch而言，起步很简单。对于初学者来说，它预设了一些适当的默认值，并隐藏了复杂的搜索理论知识。它*开箱即用*。只需最少的理解，你很快就能具有生产力。

#### 2.应用场景

- 日志分析系统ELK  elasticsearch（存储日志）与logstash(收集日志)+kibana(展示数据)
- 大型电商项目搜索系统
- 网盘搜索引
- 结合爬虫等数据收集
- 大数据方面等等

#### 3.存储结构

Elasticsearch是文件存储，Elasticsearch是面向文档型数据库，一条数据在这里就是一个文档，用JSON作为文档序列化的格式，比如下面这条用户数据：

```java
{
    "name" :     "hello world",
    "sex" :      0,
    "email":	 "10086@qq.com"
    "age" :      20
}

```



关系数据库     ⇒ 数据库(database) ⇒ 表(table)    ⇒ 行 (row)   ⇒ 列(Columns)

Elasticsearch    ⇒索引(index)   ⇒类型(type)  ⇒文档(docments)  ⇒字段(fields)



#### 4.优势

Elasticsearch可以存放大量数据，Elasticsearch集群非常容易横向扩展，而且检索性能非常好，在大数据量的情况下相比于mysql等性能上有很大的优势，下面的几个优势：

**横向可扩展性:**只需要增加台服务器或者添加节点，在分布式配置中心更新配置，启动Elasticsearch的服务器就可以并入集群。

 **分片机制提供更好的分布性:**同一个索引分成多个分片(sharding), 这点类似于分布式文件系统的块机制;分而治之的方式可提升处理效率。


 **高可用:**提供复制( replica) 机制，一个分片可以设置多个复制，使得某台服务器在宕机的情况下，集群仍旧可以照常运行，并会把服务器宕机丢失的数据信息复制恢复到其他可用节点上。
 使用简单:只需一条命令就可以下载文件，然后很快就能搭建一一个站内搜索引擎。



#### 5.与solr对比

|      | Elasticsearch                                                | solr                                                         |
| :--- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 优点 | Elasticsearch是分布式的。不需要其他组件，分发是实时的，被叫做”Push replication”。Elasticsearch 完全支持 Apache Lucene 的接近实时的搜索。处理多租户不需要特殊配置，而Solr则需要更多的高级设置。Elasticsearch 采用 Gateway 的概念，使得完备份更加简单。各节点组成对等的网络结构，某些节点出现故障时会自动分配其他节点代替其进行工作。 | Solr有一个更大、更成熟的用户、开发和贡献者社区。支持添加多种格式的索引，如：HTML、PDF、微软 Office 系列软件格式以及 JSON、XML、CSV 等纯文本格式。Solr比较成熟、稳定。不考虑建索引的同时进行搜索，速度更快。 |
| 缺点 | 只有一名开发者（当前Elasticsearch GitHub组织已经不只如此，已经有了相当活跃的维护者）还不够自动（不适合当前新的Index Warmup API） | 建立索引时，搜索效率下降，实时索引搜索效率不高。             |
|      |                                                              |                                                              |

> 对于两个全文索引的框架，得考虑系统架构、技术选型、人员等等方面的因素进行对比使用
>
> 后期会更新关于solr的文章

### 二、环境搭建

#### 1.Docker搭建

> elasticsearch默认运行内存为1G,最好设置初始化内存和最大内存
>
> 安装JDK1.8以上环境

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





#### 2.Linux搭建

##### 1、安装Elasticsearch

1、  安装JDK环境变量

```
export JAVA_HOME=/usr/local/jdk1.8.0_181
export PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar

source /etc/profile

```

2、  下载elasticsearch安装包

 下载elasticsearch安装包

[官方文档](https://www.elastic.co/downloads/elasticsearch)

注意：linux安装内存建议1g内存以上

 

3、  上传elasticsearch安装包（这里使用xshell 与 xshell ftp）



4、  解压elasticsearch

```java
tar -zxvf elasticsearch-6.4.3.tar.gz
```

5、  修改elasticsearch.yml

network.host: 对应本地ip

http.port: 9200

6、启动elasticsearch报错

```java
cd  /usr/local/elasticsearch-6.4.3/bin

./elasticsearch
```

可能出现的错误：

这里出现的错误借鉴网络

```java
报错1：can not run elasticsearch as root

解决方案:

因为安全问题elasticsearch 不让用root用户直接运行，所以要创建新用户

第一步：liunx创建新用户，然后给创建的用户加密码，输入两次密码。

第二步：切换刚才创建的用户，然后执行elasticsearch，会显示Permission denied 权限不足。

第三步：给新建的用户赋权限，chmod 777 *  这个不行，因为这个用户本身就没有权限，肯定自己不能给自己付权限。所以要用root用户登录付权限。

第四步：root给用户赋权限，chown -R XXX /你的elasticsearch安装目录。

然后执行成功。

 

下面一些操作：

groupadd user_group

useradd username-g user_group -p 123456

chown -R username:user_group elasticsearch-6.4.3

 
 
chown -R username:user_group 

 

切换用户：
su username 

./elasticsearch

报错2：

bootstrap checks failed max virtual memory areas vm.max_map_count [65530] is

 

vi /etc/sysctl.conf

vm.max_map_count=655360


sysctl  -p

 
./elasticsearch


报错3：
max file descriptors [4096] for elasticsearch process is too low, increase to at least [65536]

vi /etc/security/limits.conf

* soft nofile 65536

* hard nofile 131072

* soft nproc 2048

* hard nproc 4096


重启服务器即可 记得，这个很重要。

./elasticsearch
```

7、访问elasticsearch

关闭防火墙，可以的话设置永久关闭防火墙，自行百度

```java
systemctl stop firewalld.service
```



> 中途可能会出现很多坑，记得小心,我搭建倒是没有这么多问题，我朋友的就出现了挺多的

http://ip:9200

##### 2、安装Kibana

1、下载

2、上传，解压

```java
tar  -zxvf  kibana-6.4.3-linux-x86_64.tar.gz
```

3、配置

```java
vim config/kibana.yml

# 将默认配置改成如下：

server.port: 5601

server.host: "你的ip"
    
#elasticsearch的服务
elasticsearch.url: "http:// 你的ip:9200"
```

4、启动Kibana

./bin/kibana



 5、运行

http://ip:5601









### 三、整合使用

#### 1、Kibana基本使用

```java
GET _search
{
  "query": {
    "match_all": {}
  }
}
# 索引index  类型type 文档document 属性field
PUT /gitboy/user/1
{
  
  "name":"hello",
  "sex":1,
  "age":22
}

PUT /gitboy/user/2
{
  
  "name":"hello2",
  "sex":1,
  "age":22
}

GET  /gitboy/user/1

```



![1561458956430](C:\Users\No\AppData\Roaming\Typora\typora-user-images\1561458956430.png)

> 基本索引的命令以及操作可以借鉴我上一篇文章

#### 2、Springboot整合搭建

新建工程什么的就不说了，直接来吧！

##### 2.1引入pom文件的依赖

```java
<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.0.RELEASE</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-elasticsearch</artifactId>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
	</dependencies>

```

##### 2.2  application.yml

```xml
spring:
  data:
    elasticsearch:
    ####集群名称
     cluster-name: elasticsearch
    ####地址 
     cluster-nodes: 139.196.81.0:9300

```



##### 2.3 pojo

```java
@Document(indexName = "cluster_name索引", type = "user")//注意cluster_name索引
@Data
public class UserEntity {
	@Id
	private String id;
	private String name;
	private int sex;
	private int age;
}

```

##### 2.4 dao

```java
public interface UserReposiory extends CrudRepository<UserEntity, String> {

}

```

##### 2.5 service

```java

public interface UserService {
    
	public UserEntity addUser( UserEntity user);
    
    public Optional<UserEntity> findUser(String id);
}


@Service
public class UserServiceImpl implements UserService {
    @Autowired
	private UserReposiory userReposiory;
    
    @Override
    public UserEntity addUser(UserEntity user) {
		return userReposiory.save(user);
	}

	@Override
	public Optional<UserEntity> findUser(String id) {
		return userReposiory.findById(id);
	}
}
```



##### 2.6 controller

```java
@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping("/addUser")
	public UserEntity addUser(@RequestBody UserEntity user) {
		return userService.save(user);
	}

	@RequestMapping("/findUser")
	public Optional<UserEntity> findUser(String id) {
		return userService.findById(id);
	}

}

```



##### 2.7启动

```java
@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.repository")
public class AppEs {

	public static void main(String[] args) {
		SpringApplication.run(AppEs.class, args);
	}
}

```



### 四、分词

#### 1、什么是分词器

因为Elasticsearch中默认的标准分词器分词器对中文分词不是很友好，会将中文词语拆分成一个一个中文的汉子。因此引入中文分词器-es-ik插件

#### 2、下载与配置

下载地址: <https://github.com/medcl/elasticsearch-analysis-ik/releases>

注意: es-ik分词插件版本一定要和es安装的版本对应

```java
第一步：下载es的IK插件（资料中有）命名改为ik插件
第二步: 上传到/usr/local/elasticsearch-6.4.3/plugins
第三步: 重启elasticsearch即可

```

#### 3、自定义扩展字典

在/usr/local/elasticsearch-6.4.3/plugins/ik/config目录下

#####  3.1 新建文件

```java
vi custom/new_word.dic

吃鸡
王者荣耀
码云
马云

```

3.2 配置

vi IKAnalyzer.cfg.xml

```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
    <comment>IK Analyzer 扩展配置</comment>
    <!--用户可以在这里配置自己的扩展字典 -->
    <entry key="ext_dict">custom/new_word.dic</entry>
     <!--用户可以在这里配置自己的扩展停止词字典-->
    <entry key="ext_stopwords"></entry>
    <!--用户可以在这里配置远程扩展字典 -->
    <!-- <entry key="remote_ext_dict">words_location</entry> -->
    <!--用户可以在这里配置远程扩展停止词字典-->
    <!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>

```







### 五、文档映射

已经把ElasticSearch的核心概念和关系数据库做了一个对比，索引（index）相当于数据库，类型(type)相当于数据表，映射(Mapping)相当于数据表的表结构。ElasticSearch中的映射（Mapping）用来定义一个文档，可以定义所包含的字段以及字段的类型、分词器及属性等等。

**文档映射就是给文档中的字段指定字段类型、分词器。**

使用GET /gitboy/user/_mapping

 

#### 1、映射的分类

##### 1.1 动态映射

我们知道，在关系数据库中，需要事先创建数据库，然后在该数据库实例下创建数据表，然后才能在该数据表中插入数据。而ElasticSearch中不需要事先定义映射（Mapping），文档写入ElasticSearch时，会根据文档字段自动识别类型，这种机制称之为动态映射。

##### 1.2 静态映射

在ElasticSearch中也可以事先定义好映射，包含文档的各个字段及其类型等，这种方式称之为静态映射。

#### 2、ES类型支持

##### 2.1 基本类型

符串：string，string类型包含 text 和 keyword。

text：该类型被用来索引长文本，在创建索引前会将这些文本进行分词，转化为词的组合，建立索引；允许es来检索这些词，text类型不能用来排序和聚合。

keyword：该类型不需要进行分词，可以被用来检索过滤、排序和聚合，keyword类型自读那只能用本身来进行检索（不可用text分词后的模糊检索）。

**注意: keyword类型不能分词，Text类型可以分词查询**

数指型：long、integer、short、byte、double、float

日期型：date

布尔型：boolean

二进制型：binary

数组类型（Array datatype）

##### 2.2 复杂类型

###### 地理位置类型（Geo datatypes）

地理坐标类型（Geo-point datatype）：geo_point 用于经纬度坐标

地理形状类型（Geo-Shape datatype）：geo_shape 用于类似于多边形的复杂形状

 

###### 2.3 特定类型（Specialised datatypes）

Pv4 类型（IPv4 datatype）：ip 用于IPv4 地址

Completion 类型（Completion datatype）：completion 提供自动补全建议

Token count 类型（Token count datatype）：token_count 用于统计做子标记的字段的index数目，该值会一直增加，不会因为过滤条件而减少

mapper-murmur3 类型：通过插件，可以通过_murmur3_来计算index的哈希值

附加类型（Attachment datatype）：采用mapper-attachments插件，可支持_attachments_索引，例如 Microsoft office 格式，Open Documnet 格式， ePub，HTML等

 

 Analyzer 索引分词器，索引创建的时候使用的分词器 比如ik_smart

Search_analyzer 搜索字段的值时，指定的分词器



### 六 、倒排索引

#### 1、正向索引

正排表是以文档的ID为关键字，表中记录文档中每个字的位置信息，查找时扫描表中每个文档中字的信息直到找出所有包含查询关键字的文档。

这种组织方法在建立索引的时候结构比较简单，建立比较方便且易于维护;因为索引是基于文档建立的，若是有新的文档加入，直接为该文档建立一个新的索引块，挂接在原来索引文件的后面。若是有文档删除，则直接找到该文档号文档对应的索引信息，将其直接删除。但是在查询的时候需对所有的文档进行扫描以确保没有遗漏，这样就使得检索时间大大延长，检索效率低下。     

尽管正排表的工作原理非常的简单，但是由于其检索效率太低，除非在特定情况下，否则实用性价值不大。

 

#### 2、倒排索引

倒排表以字或词为关键字进行索引，表中关键字所对应的记录表项记录了出现这个字或词的所有文档，一个表项就是一个字表段，它记录该文档的ID和字符在该文档中出现的位置情况。

由于每个字或词对应的文档数量在动态变化，所以倒排表的建立和维护都较为复杂，但是在查询的时候由于可以一次得到查询关键字所对应的所有文档，所以效率高于正排表。在全文检索中，检索的快速响应是一个最为关键的性能，而索引建立由于在后台进行，尽管效率相对低一些，但不会影响整个搜索引擎的效率。



>  正排索引是从文档到关键字的映射（已知文档求关键字），倒排索引是从关键字到文档的映射（已知关键字求文档）。



