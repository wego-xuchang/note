## Zuul

zuul[文档](https://github.com/Netflix/zuul/wiki/How-It-Works-2.0)

### 什么是Zuul？

Zuul是从设备和网站到Netflix流应用程序后端的所有请求的前门。作为边缘服务应用程序，Zuul旨在实现动态路由，监控，弹性和安全性。它还能够根据需要将请求路由到多个Amazon Auto Scaling组。

直接进入：[入门2.0](https://github.com/Netflix/zuul/wiki/Getting-Started-2.0)

### 我们为什么要建造Zuul？

Netflix API流量的数量和多样性有时会导致生产问题迅速而且没有任何警告。我们需要一个允许我们快速改变行为的系统，以便对这些情况做出反应。

Zuul使用一系列不同类型的过滤器，使我们能够快速灵活地将功能应用于我们的边缘服务。这些过滤器可帮助我们执行以下功能：

- 身份验证和安全性 - 确定每个资源的身份验证要求并拒绝不满足这些要求的请求。
- 洞察和监控 - 在边缘跟踪有意义的数据和统计数据，以便为我们提供准确的生产视图。
- 动态路由 - 根据需要动态地将请求路由到不同的后端群集。
- 压力测试 - 逐渐增加群集的流量以衡量性能。
- Load Shedding - 为每种类型的请求分配容量并删除超过限制的请求。
- 静态响应处理 - 直接在边缘构建一些响应，而不是将它们转发到内部集群
- 多区域弹性 - 跨AWS区域路由请求，以使我们的ELB使用多样化，并使我们的优势更接近我们的成员

有关详细信息：[我们如何在Netflix上使用Zuul](https://github.com/Netflix/zuul/wiki/How-We-Use-Zuul-At-Netflix)

## Zuul组件

Zuul 2.x组件：

- [zuul-core](https://github.com/Netflix/zuul/tree/2.1/zuul-core) - Zuul 2.0的核心功能
- [zuul-sample](https://github.com/Netflix/zuul/tree/2.1/zuul-sample) - Zuul 2.0的示例驱动程序应用程序

Zuul 1.x组件：

- [zuul-core](https://github.com/Netflix/zuul/tree/1.x/zuul-core) - 包含编译和执行[Filters](https://github.com/Netflix/zuul/wiki/Filters)的核心功能的库
- [zuul-simple-webapp](https://github.com/Netflix/zuul/tree/1.x/zuul-simple-webapp) - webapp，它显示了如何使用zuul-core构建应用程序的简单示例
- [zuul-netflix](https://github.com/Netflix/zuul/tree/1.x/zuul-netflix) - 为Zuul添加其他NetflixOSS组件的库 - 例如，使用Ribbon进行路由请求。
- [zuul-netflix-webapp](https://github.com/Netflix/zuul/tree/1.x/zuul-netflix-webapp) - 将zuul-core和zuul-netflix打包成一个易于使用的包的webapp



## 入门2.0



Arthur Gonigberg编辑了这个页面 on 16 Jul · [6次修订](https://github.com/Netflix/zuul/wiki/Getting-Started-2.0/_history)

### 获取二进制文件

可以在[http://search.maven.org](http://search.maven.org/#search|ga|1|com.netflix.zuul)找到Maven，Ivy，Gradle等的二进制文件和依赖信息。

**最新版本：** [![下载](https://camo.githubusercontent.com/f810b8ad996bc6c97c8438b9b696c282228859e4/68747470733a2f2f6170692e62696e747261792e636f6d2f7061636b616765732f6e6574666c69786f73732f6d6176656e2f7a75756c2f696d616765732f646f776e6c6f61642e7376673f73616e6974697a653d74727565)](https://bintray.com/netflixoss/maven/zuul/_latestVersion)

Maven的示例：

```
< dependency >
    < groupId > com.netflix.zuul </ groupId >
    < artifactId > zuul-core </ artifactId >
    < version > 2.1.5 </ version >
</ dependency >
```

对于Gradle：

```
compile "com.netflix.zuul:zuul-core:2.1.5"
```

### 建造

要检查源和构建：

```
$ git clone git@github.com:Netflix/zuul.git
$ cd zuul/
$ ./gradlew build
```

要做一个干净的构建：

```
$ ./gradlew clean build
```

### 运行

您可以使用以下命令运行[示例应用程序](https://github.com/Netflix/zuul/tree/2.1/zuul-sample)：

```
$ ./gradlew run
```

## 工作原理2.0



Arthur Gonigberg编辑了这个页面 on 18 May 2018 · [10次修订](https://github.com/Netflix/zuul/wiki/How-It-Works-2.0/_history)

### 建筑概述

在高级视图中，Zuul 2.0是运行预过滤器（入站过滤器）的Netty服务器，然后使用Netty客户端代理请求，然后在运行后过滤器（出站过滤器）后返回响应。

![img](https://camo.githubusercontent.com/263a4e85f8b9a9e76eb0b61c4cff2b142f9344ec/68747470733a2f2f692e696d6775722e636f6d2f6b5453543948562e706e67)

### 过滤器

过滤器是Zuul业务逻辑的核心。他们有能力执行大范围的操作，并且可以在请求 - 响应生命周期的不同部分运行，如上图所示。

- **入站过滤器**在路由到源之前执行，可用于身份验证，路由和装饰请求。
- **端点过滤器**可用于返回静态响应，否则内置`ProxyEndpoint`过滤器会将请求路由到原点。
- **出站过滤器**在从原点获取响应后执行，可用于度量标准，装饰对用户的响应或添加自定义标头。

还有两种类型的过滤器：同步和异步。因为我们在一个事件循环运行，这是**关键**，以从来没有在过滤器阻塞。如果您要阻止，请在异步过滤器中，在单独的线程池上执行此操作 - 否则您可以使用同步过滤器。

有关更多信息，请查看“ [过滤器”](https://github.com/Netflix/zuul/wiki/Filters)页面。

