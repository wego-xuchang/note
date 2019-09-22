## ElasticSearch

[官方文档](https://www.elastic.co/guide/cn/elasticsearch/guide/current/intro.html)



Elasticsearch是一个开源的搜索引擎，建立在一个全文搜索引擎库[Apache Lucene™](https://lucene.apache.org/core/)基础之上。 Lucene可以说是当下最先进，高性能，全功能的搜索引擎库 - 无论是开源还是私有。

但是Lucene仅仅是一个库。为了充分发挥其功能，你需要使用Java并将Lucene直接集成到应用程序中。更糟糕的是，您可能需要获得信息检索学位才能了解其工作原理.Lucene *非常*复杂。

Elasticsearch也是使用Java编写的，它的内部使用Lucene做索引与搜索，但是它的目的是使用全文检索变得简单，通过隐藏Lucene的复杂性，取而代之的提供一套简单一致的RESTful API。

然而，Elasticsearch不仅仅是Lucene，并且也不仅仅是一个全文搜索引擎。 它可以被下面这样准确的形容：

- 一个分布式的实时文档存储，*每个字段*可以被索引与搜索
- 一个分布式实时分析搜索引擎
- 能胜任上百个服务节点的扩展，并支持PB级别的结构化或者非结构化数据

Elasticsearch将所有的功能打包成一个单独的服务，这样你可以通过程序与它提供的简单的RESTful API进行通信，可以使用自己喜欢的编程语言充当web客户端，甚至可以使用命令行（去充当这个客户端）。

就Elasticsearch而言，起步很简单。对于初学者来说，它预设了一些适当的默认值，并隐藏了复杂的搜索理论知识。它*开箱即用*。只需最少的理解，你很快就能具有生产力。



### ElasticSearch交互

java api 

如果你正在使用 Java，在代码中你可以使用Elasticsearch内置的两个客户端：

节点客户端（节点客户端）节点客户端作为一个非数据节点加入到本地集群中。换句话说，它本身不保存任何数据，但是它知道数据在集群中的哪个节点中，并且可以把请求转发到正确的节点。传输客户端（Transport client）轻量级的传输客户端可以将请求发送到远程集群。它本身不加入集群，但是它可以将请求转发到集群中的一个节点上。

两个Java客户端都是通过*9300*端口并使用Elasticsearch原的生*传输*协议状语从句：集群交互。集群中的节点通过端口9300彼此通信。如果这个端口没有打开，节点将无法形成一个集群。

Java 客户端作为节点必须和 Elasticsearch 有相同的 *主要* 版本；否则，它们之间将无法互相理解。

使用 Json over http Restfull api

所有其他语言可以使用 RESTful API 通过端口 *9200* 和 Elasticsearch 进行通信，你可以用你最喜爱的 web 客户端访问 Elasticsearch 。事实上，正如你所看到的，你甚至可以使用curl命令来和 Elasticsearch 交互。



Elasticsearch 为以下语言提供了官方客户端 --Groovy、JavaScript、.NET、 PHP、 Perl、 Python 和 Ruby--还有很多社区提供的客户端和插件，所有这些都可以在 [Elasticsearch Clients](https://www.elastic.co/guide/en/elasticsearch/client/index.html) 中找到。





一个 Elasticsearch 请求和任何 HTTP 请求一样由若干相同的部件组成：

```
curl -X<VERB> '<PROTOCOL>://<HOST>:<PORT>/<PATH>?<QUERY_STRING>' -d '<BODY>'
```

标签

| VERB         | 适当的 HTTP *方法* 或 *谓词* : GET`、 `POST`、 `PUT`、 `HEAD 或者 `DELETE`。 |
| ------------ | ------------------------------------------------------------ |
| PROTOCOL     | http 或者 https`（如果你在 Elasticsearch 前面有一个 `https 代理） |
| HOST         | Elasticsearch 集群中任意节点的主机名，或者用 localhost 代表本地机器上的节点。 |
| PORT         | 运行 Elasticsearch HTTP 服务的端口号，默认是 9200 。         |
| PATH         | API 的终端路径（例如 _count 将返回集群中文档数量）。Path 可能包含多个组件，例如：_cluster/stats 和 _nodes/stats/jvm 。 |
| QUERY_STRING | 任意可选的查询字符串参数 (例如 ?pretty 将格式化地输出 JSON 返回值，使其更容易阅读) |
| BODY         | 一个 JSON 格式的请求体 (如果请求需要的话)                    |

### 面向文档

在应用程序中对象很少只是一个简单的键和值的列表。通常，它们拥有更复杂的数据结构，可能包括日期、地理信息、其他对象或者数组等。

也许有一天你想把这些对象存储在数据库中。使用关系型数据库的行和列存储，这相当于是把一个表现力丰富的对象挤压到一个非常大的电子表格中：你必须将这个对象扁平化来适应表结构--通常一个字段>对应一列--而且又不得不在每次查询时重新构造对象。

Elasticsearch 是 *面向文档* 的，意味着它存储整个对象或 *文档_。Elasticsearch 不仅存储文档，而且 _索引*每个文档的内容使之可以被检索。在 Elasticsearch 中，你 对文档进行索引、检索、排序和过滤--而不是对行列数据。这是一种完全不同的思考数据的方式，也是 Elasticsearch 能支持复杂全文检索的原因。



#### json

Elasticsearch 使用 JavaScript Object Notation 或者 *JSON* 作为文档的序列化格式。JSON 序列化被大多数编程语言所支持，并且已经成为 NoSQL 领域的标准格式。 它简单、简洁、易于阅读。



#### 索引（添加）文档

put http://ip端口/索引名称/类型名称/特定的ID



```
{
    "first_name" : "John",
    "last_name" :  "Smith",
    "age" :        25,
    "about" :      "I love to go rock climbing",
    "interests": [ "sports", "music" ]
}
```



#### 检索文档



目前我们已经在Elasticsearch中存储了一些数据， 接下来就能专注于实现应用的业务需求了。第一个需求是可以检索到单个雇员的数据。

这在Elasticsearch中很简单。简单地执行 一个HTTP GET请求并指定文档的地址 - 索引库，类型和ID。 使用这三个信息可以返回原始的JSON文档：

get http://ip端口/索引名称/类型名称/特定的ID



```
{
  "_index" :   "megacorp",
  "_type" :    "employee",
  "_id" :      "1",
  "_version" : 1,
  "found" :    true,
  "_source" :  {
      "first_name" :  "John",
      "last_name" :   "Smith",
      "age" :         25,
      "about" :       "I love to go rock climbing",
      "interests":  [ "sports", "music" ]
  }
}
```





将HTTP命令由PUT改为GET可以用来检索文档，同样的，可以使用DELETE命令来删除文档，以及使用HEAD指令来检查文档是否存在。如果想更新已存在的文档，只需再次PUT。





#### 轻量搜索

_search 用于检索文档的所有数据

```
GET 索引名称/类型名称/特定的ID/_search
```

可以看到，我们仍然使用索引库 以及类型 ，但与指定一个文档 ID 不同，这次使用 `_search 。返回结果包括了所有三个文档，放在数组 hits 中。一个搜索默认返回十条结果。



```
{
    "took": 5,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits": {
        "total": 3,
        "max_score": 1,
        "hits": [
            {
                "_index": "gitboy",
                "_type": "employee",
                "_id": "2",
                "_score": 1,
                "_source": {
                    "first_name": "Jane",
                    "last_name": "Smith",
                    "age": 32,
                    "about": "I like to collect rock albums",
                    "interests": [
                        "music"
                    ]
                }
            }  
        ]
    }
}
```



通过一个URL参数来传递查询信息给搜索接口过滤筛选条件



```
GET 索引名称/类型名称/_search?q=last_name:Smith
```



通过查询表达式使用一个json请求

*领域特定语言*（DSL）， 指定了使用一个JSON请求

```
GET 索引名称/类型名称/_search
{
    "query" : {
        "match" : {
            "last_name" : "Smith"
        }
    }
}
```



#### 复杂的搜索

使用过滤器filter，its支持高效地执行一个结构化查询

```
GET 索引名称/类型名称/_search
{
    "query" : {
        "bool": {
            "must": {
                "match" : {
                    "last_name" : "smith" 
                }
            },
            "filter": {
                "range" : {
                    "age" : { "gt" : 30 } 
                }
            }
        }
    }
}
```





这部分与我们之前使用的 match 查询 一样。

这部分是一个 range 过滤器 ， 它能找到年龄大于 30 的文档，其中 gt 表示_大于(_great than)。



#### 全文搜索



搜索下所有喜欢攀岩（rock climbing）的雇员：



```
GET 索引名称/类型名称/_search
{
    "query" : {
        "match" : {
            "about" : "rock climbing"
        }
    }
}
```



Elasticsearch 默认按照相关性得分排序，即每个文档跟查询的匹配程度。第一个最高得分的结果很明显：John Smith 的 about 属性清楚地写着 “rock climbing” 。



但为什么 Jane Smith 也作为结果返回了呢？原因是她的 about 属性里提到了 “rock” 。因为只有 “rock” 而没有 “climbing” ，所以她的相关性得分低于 John 的。



这是一个很好的案例，阐明了 Elasticsearch 如何 在 全文属性上搜索并返回相关性最强的结果。Elasticsearch中的 相关性 概念非常重要，也是完全区别于传统关系型数据库的一个概念，数据库中的一条记录要么匹配要么不匹配。





#### 短语搜索

找出一个属性中的独立单词是没有问题的，但有时候想要精确匹配一系列单词或者短语 。 比如， 我们想执行这样一个查询，仅匹配同时包含 “rock” 和 “climbing” ，并且 二者以短语 “rock climbing” 的形式紧挨着的雇员记录。



为此对 match 查询稍作调整，使用一个叫做 match_phrase 的查询：



```
GET 索引名称/类型名称/_search
{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    }
}
```

#### 高亮搜索

许多应用都倾向于在每个搜索结果中 *高亮* 部分文本片段，以便让用户知道为何该文档符合查询条件。在 Elasticsearch 中检索出高亮片段也很容易。





```
GET 索引名称/类型名称/_search
{
    "query" : {
        "match_phrase" : {
            "about" : "rock climbing"
        }
    },
    "highlight": {
        "fields" : {
            "about" : {}
        }
    }
}
```



分析

终于到了最后一个业务需求：支持管理者对雇员目录做分析。 Elasticsearch有一个功能叫聚合（聚合），允许我们基于数据生成一些精细的分析结果。聚合与SQL中的GROUP BY类似但更强大。





```
GET 索引名称/类型名称/_search
{
  "query": {
    "match": {
      "last_name": "Smith"
    }
  },
  "aggs": {
    "all_interests": {
      "terms": {
        "field": "interests"
      }
    }
  }
}
```

















​	