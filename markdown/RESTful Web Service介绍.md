##  RESTful Web Service介绍

 Roy Thomas Fielding博士2000年提出的

 REST是英文Representational

State Transfer的缩写

表象化状态转变  或者 表述性状态转移

 REST是Web服务的一种架构风格

 使用HTTP、URI等广泛流行的标准和协议

 轻量级、跨平台、跨语言的架构设计。



![img](https://qqadapt.qpic.cn/txdocpic/0/db79856d1d4e0ee4b61c9174eb3f4c87/0)

### REST到底是什么？

REST是一种设计风格。它不是一种标准，也不是一种软件，而是一种思想。

 REST通常基于使用HTTP，URI，和XML，JSON以及HTML这些现有的广泛流行的协议和标准。



### RESTful是什么

RESTful对应的中文是 REST式的。

 RESTful Web Service是一种常见的REST的应用，是遵守了REST风格的web服务。



#### 两种主要的web服务

JAX-RS    

RESTful Web Service

JAX-WS Web Service



####  REST 架构的主要原则

网络上的所有事物都可被抽象为资源（Resource）

每个资源都有一个唯一的资源标识符（Resource

Identifier）

同一资源具有多种表现形式(xml、json等)

对资源的各种操作不会改变资源标识符

所有的操作都是无状态的（Stateless）

 符合REST原则的架构方式即可称为RESTful



 无状态性

无状态性使得客户端和服务器端不必保存对方的详细信息，服务器只需要处理当前

Request，而不必了解前面

Request 的历史。

 从而可以更容易地释放资源。

让服务器充分利用

Pool 技术来提高稳定性和性能。



#### 资源操作

http://taotao.com/item/

GET：           获取一个资源 

POST：         创建一个新的资源 

PUT：           修改一个资源的状态 

DELETE ：删除一个资源 

资源展现

XML

JSON

……



**原来的方式**

[http://127.0.0.1/user/queryUser/](http://127.0.0.1/user/queryUser/{id})[{id}](http://127.0.0.1/user/queryUser/{id})                  **GET****方法，根据用户****id****获取数据**

<http://127.0.0.1/user/update>User                       **POST****方法，用户修改**

<http://127.0.0.1/user/save>User                           **POST****方法，用户新增**

[http://127.0.0.1/user/deleteUser/](http://127.0.0.1/user/delete/{id})[{id}](http://127.0.0.1/user/delete/{id})                 **GET/POST****方法，用户根据****id****删除**



**RESTful**

[http://127.0.0.1/user/](http://127.0.0.1/user/ {id})[ {id}](http://127.0.0.1/user/ {id})                                  **GET****方法，根据用户****id****获取数据**

[http://127.0.0.1/user/](http://127.0.0.1/user/update)                                          **PUT****方法，用户修改**

[http://127.0.0.1/user/](http://127.0.0.1/user/save)                                          **POST****方法，用户新增**

http://127.0.0.1/user/{id}                             **DELETE****方法，用户根据****id****删除**



开发的接口，web服务更加的简洁

REST接口定义

![img](https://qqadapt.qpic.cn/txdocpic/0/7c418e3aaa02680f6300669993589604/0)

幂等性：对同一REST接口的多次访问，得到的资源状态是相同的。

安全性：对该REST接口访问，不会使服务器端资源状态发生改变。



### 最佳实践

 最佳实践：REST接口设计

•      URL的组成

–     网络协议（http、https）

–     服务器地址

–     接口名称

–     参数列表

•      URL定义限定

–     不要使用大写字母

–     使用中线 - 代替下划线 _

–     参数列表应该被encode过



 最佳实践：响应设计

Content body 仅仅用来传输数据

 数据要做到拿来就可用的原则，不需要“拆箱”的过程

用来描述数据或者请求的元数据放Header中

![img](https://qqadapt.qpic.cn/txdocpic/0/754acd5488b0587e0a4e306e2198d4bf/0)

![img](https://qqadapt.qpic.cn/txdocpic/0/d01ffa1c9ca9ab430f502a212fbe63fa/0)



http响应状态码

![img](https://qqadapt.qpic.cn/txdocpic/0/5c52fa872f1b2c26e2564b87f621792e/0)



### SpringMVC实现RESTful服务

SpringMVC原生态的支持了REST风格的架构设计。

所涉及到的注解：

@RequestMapping

@PathVariable

@ResponseBody

ResponseEntity

……



####  RESTful风格开发商品接口

根据RESTful风格，编写商品功能的增删改查接口

 编写商品查询接口

在ItemInterfaceController编写

```java
@Controller
@RequestMapping("item/interface")
public class
ItemInterfaceController {

    @Autowired
    private ItemService itemService;

    // http://manager.taotao.com/rest/item/interface/{id}
    /**
     * 根据id查询用户
     * 
     * @param id
     * @return 返回的类型是ResponseEntity，泛型声明为需要返回的数据类型
     */
    @RequestMapping(value = "{id}", method =
RequestMethod.GET) 
// 返回的是ResponseEntity或者加上@ResponseBody注解的效果是一样的，任选其一即可，也可以都设置。
// @ResponseBody
    public
ResponseEntity<Item> queryItemById(@PathVariable("id") Long id) {
       try {
           Item item = this.itemService.queryById(id);
           // 查询成功，响应的状态码应为200
           // 可以设置HttpStatus枚举的OK
           // return
ResponseEntity.status(HttpStatus.OK).body(Item);
           // 也可以使用ok()方法，效果和上面是一样的
           return
ResponseEntity.ok().body(item);
       } catch (Exception e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       // 如果有异常，设置状态码为500
       return
ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}
```



#### 使用谷歌浏览器测试工具测试

![img](https://qqadapt.qpic.cn/txdocpic/0/815db97465f5091ae4bd62edae3b2a69/0)

![img](https://qqadapt.qpic.cn/txdocpic/0/62ab4045c6457771ae689f1907010e89/0)