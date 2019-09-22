## SpringBoot

### **一、Spring介绍**

#### **1.1、SpringBoot简介**

在初次学习Spring整合各个第三方框架构建项目的时候，往往会有一大堆的XML文件的配置，众多的dtd或者schema约束，资源文件的管理也是很凌乱，而且还有个更蛋疼的地方就是版本的迭代问题，并且tomcat的启动加载速度真的10s等的难受。SpringBoot 是一个快速开发的框架,能够快速的整合第三方框架，简化XML配置，全部采用注解形式，内置Tomcat容器,帮助开发者能够实现快速开发，SpringBoot的Web组件 默认集成的是SpringMVC框架。Spring Boot让我们的Spring应用变的更轻量化。比如：你可以仅仅依靠一个Java类来运行一个Spring引用。你也可以打包你的应用为jar并通过使用java -jar来运行你的Spring Web应用。

##### Spring Boot的主要优点：

为所有Spring开发者更快的入门

开箱即用，提供各种默认配置来简化项目配置

内嵌式容器简化Web项目

没有冗余代码生成和XML配置的要求

本章主要目标完成Spring Boot基础项目的构建，并且实现一个简单的Http请求处理，通过这个例子对Spring Boot有一个初步的了解，并体验其结构简单、开发快速的特性。





#### **1.2、系统要求：**

JavaJDK1.8及以上

Spring Framework 4.1.5及以上

Eclipse

**本文采用**Java JDK 1.8、**Spring Boot 2.0版本**调试通过。



#### **1.3、SpringBoot和SpringMVC区别**

SpringBoot 是一个快速开发的框架,能够快速的整合第三方框架，简化XML配置，全部采用注解形式，内置Tomcat容器,帮助开发者能够实现快速开发，SpringBoot的Web组件 默认集成的是SpringMVC框架。

SpringMVC是控制层。





#### **1.4、SpringBoot和SpringCloud区别**

SpringBoot 是一个快速开发的框架,能够快速的整合第三方框架，简化XML配置，全部采用注解形式，内置Tomcat容器,帮助开发者能够实现快速开发，SpringBoot的Web组件 默认集成的是SpringMVC框架。

SpringMVC是控制层。

SpringCloud依赖与SpringBoot组件，使用SpringMVC编写Http协议接口，同时SpringCloud是一套完整的微服务解决框架。



#### **1.5常见错误**

Eclipse 下载SpringBoot2.0以上版本,pom文件报错解决办法

org.apache.maven.archiver.MavenArchiver.getManifest(org.apache.maven.project.MavenProject, org.apache.maven.archiver.MavenArchiveConfiguration)

相关网址: http://bbs.itmayiedu.com/article/1527749194015



### **二、快速入门**



#### **2.1、创建一个Maven工程**

**名为”springboot-helloworld” 类型为Jar工程项目**



#### **2.2、pom文件引入依赖**



```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies> 
```

> **spring-boot-starter-parent作用**
>
> **在pom.xml中引入spring-boot-start-parent,spring官方的解释叫什么stater poms,它可以提供dependency management,也就是说依赖管理，引入以后在申明其它dependency的时候就不需要version了，后面可以看到。**
>
> **spring-boot-starter-web作用**
>
> **springweb 核心组件**
>
> **spring-boot-maven-plugin作用**
>
>  **如果我们要直接Main启动spring，那么以下plugin必须要添加，否则是无法启动的。如果使用maven 的spring-boot:run的话是不需要此配置的。（我在测试的时候，如果不配置下面的plugin也是直接在Main中运行的。）**



#### **2.3、编写HelloWorld服务**

创建package命名为com.example.controller（根据实际情况修改）

创建HelloController类，内容如下

```java
@RestController
@EnableAutoConfiguration
public class HelloController {
	@RequestMapping("/hello")
	public String index() {
		return "Hello World";
	}	
public static void main(String[] args) {
		SpringApplication.run(HelloController.class, args);
	}
}
```



#### **2.4、@RestController**

**在上加上RestController 表示修饰该Controller所有的方法返回JSON格式,直接可以编写**

**Restful接口**

#### **2.5、@EnableAutoConfiguration**

注解:作用在于让 Spring Boot   根据应用所声明的依赖来对 Spring 框架进行自动配置

这个注解告诉Spring Boot根据添加的jar依赖猜测你想如何配置Spring。由于spring-boot-starter-web添加了Tomcat和Spring MVC，所以auto-configuration将假定你正在开发一个web应用并相应地对Spring进行设置。

#### 2.6 SpringApplication.run(HelloController.class, args);

   标识为启动类

#### **2.7、SpringBoot启动方式1**

Springboot默认端口号为8080

```java
@RestController
@EnableAutoConfiguration
public class HelloController {
	@RequestMapping("/hello")
	public String index() {
		return "Hello World";
	}	
public static void main(String[] args) {
		SpringApplication.run(HelloController.class, args);
	}
}
```





启动主程序，打开浏览器访问http://localhost:8080/index，可以看到页面输出Hello World

#### **2.8、SpringBoot启动方式2**

@ComponentScan(basePackages = "com.example.controller")---控制器扫包范围

```java
@ComponentScan(basePackages = "com.example.controller")
@EnableAutoConfiguration
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
```



#### **2.9、SpringBoot启动方式3**

@SpringBootApplication

```java
@SpringBootApplication
public class SpringBootApp {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApp.class, args);
	}

}
```



@SpringBootApplication 被 @Configuration、@EnableAutoConfiguration、@ComponentScan 注解所修饰，换言之 Springboot 提供了统一的注解来替代以上三个注解

扫包范围：在启动类上加上@SpringBootApplication注解,当前包下或者子包下所有的类都可以扫到。