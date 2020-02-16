# Zuul构建微服务网关-Header与文件上传和过滤器

一、Header**

**1.1、敏感header的设置**

一般来说，可在同一个系统中的服务之间共享Header，不过应尽量防止让一些敏感的Header外泄。

```pl
zuul:
  routes:
    provide-user: 
     sensitive-headers: Cookie,Set-Cookie
```



说明：敏感的header不会传播到下游去，也就是说此处的Cookie,Set-Cookie不会传播的其它的微服务中去

**1.2、忽略的Header**

可以使用zuul.ignored-headers属性丢弃一些Header，如：

```pl
zuul:
  routes:
    provide-user: 
     sensitive-headers: Cookie,Set-Cookie
  ignored-headers: Authorization
```



说明：忽略的header不会传播到下游去，也就是说此处的Authorization不会传播的其它的微服务中去，作用与上面敏感的Header差不多，事实上sensitive-headers会被添加到ignored-headers中。



注意：

 1、默认情况下zuul.ignored-headers是空的

 2、如果Spring Security在项目的classpath中，那么zuul.ignored-headers的默认值就是Pragma,Cache-Control,X-Frame-Options,X-Content-Type-Options,X-XSS-Protection,Expires，所以，当Spring Security在项目的classpath中，同时又需要使用下游微服务的Spring Security的Header时，可以将zuul.ignoreSecurity-Headers设置为false

**二、使用Zuul上传文件**

1、对于小文件（1M以内）上传，无须任何处理，即可正常上传。

2、对于大文件（10M以上）上传，需要为上传路径添加/zuul前缀。

注意：这里的上传路径添加/zuul前缀无须配置



zuul的配置：

```pl
zuul:
  routes:
    file-upload: /file/**
```





测试上传：上传文件200多M

  [![wKioL1mdJ8ODXYe_AACJcUHRmwQ578.png](https://s1.51cto.com/wyfs02/M02/9F/75/wKioL1mdJ8ODXYe_AACJcUHRmwQ578.png)](https://s1.51cto.com/wyfs02/M02/9F/75/wKioL1mdJ8ODXYe_AACJcUHRmwQ578.png)

zuul端抛出超时异常：

```pl
Caused by: com.netflix.hystrix.exception.HystrixRuntimeException: file-upload timed-out and no fallback available.
```



解决：在zuul的yml配置文件中加入如下配置：

```pl
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds: 60000
ribbon:
  ConnectTimeout: 3000
  ReadTimeout: 60000
```



再次测试上传：上传文件200多M   

 [![wKiom1mdLLfSebcyAABnqzPW_58519.png](https://s2.51cto.com/wyfs02/M01/00/C6/wKiom1mdLLfSebcyAABnqzPW_58519.png)](https://s2.51cto.com/wyfs02/M01/00/C6/wKiom1mdLLfSebcyAABnqzPW_58519.png)

**三、过滤器**

**3.1、过滤器类型与请求生命周期**

Zuul大部分功能都是通过过滤器来实现的。Zuul中定义了4种标准过滤器类型，这些过滤器类型对应于请求的典型生命周期。

PRE

 这种过滤器在请求被路由之前调用。可利用这种过滤实现身份验证、在集群中选择请求的微服务、记录调试信息等。

ROUTING

 这种过滤器将请求路由到微服务。这种过滤器用于构建发送给微服务的请求，并使用Apache HttpClient或Netfilx Ribbon请求微服务。

POST

 这种过滤器在路由到微服务以后执行。这种过滤器可用来为响应添加标准的HTTP Header、收集统计信息和指标、将响应从微服务发送给客户端等。

ERROR

 在其它阶段发送错误时执行该过滤器。



Zuul请求的生命周期如图:

  [![wKiom1mdM13i3jJcAACmQj_aSBA350.png](https://s5.51cto.com/wyfs02/M00/00/C6/wKiom1mdM13i3jJcAACmQj_aSBA350.png)](https://s5.51cto.com/wyfs02/M00/00/C6/wKiom1mdM13i3jJcAACmQj_aSBA350.png)

**3.2、自定义过滤器**

Zuul还允许创建自定义的过滤器类型。继承抽象类ZuulFilter即可。

自定义过滤器需要实现以下方法：

 a、filterType：返回过滤器的类型。有pre、route、post、error等，分别对应着上面的过滤器，详细可以参考com.netflix.zuul.ZuulFilter.filterType() 中的注释。

 b、filterOrder：返回一个int值来指定过滤器执行的顺序，不同的过滤器允许返回相同的顺序。

 c、shouldFilter：返回一个boolean值来判断该过滤器是否要执行，true表示执行。

 d、run：过滤器的具体逻辑。



这里编写一个记录请求日志的过滤器。

1、新建一个类LogFilter继承抽象类ZuulFilter

```java
package com.liuy.filter;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * 打印请求日志
 * 记录请求日志的过滤器
 * @description 记录请求日志的过滤器
 * @author luis
 * @version 1.0
 * @date:2017年8月23日下午3:58:55
 */
public class LogFilter extends ZuulFilter {
	private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		LogFilter.LOG.info(String.format("打印日志 ： send %s request to %s", request.getMethod(), request.getRequestURL().toString()));
		return null;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	@Override
	public String filterType() {
		return "pre";
	}
}
```



2、修改启动类

```java
package com.liuy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import com.liuy.filter.LogFilter;

/**
 * Zuul微服务网关 - 过滤器
 * @description Zuul微服务网关 - 过滤器
 * @author luis
 * @version 1.0
 * @date:2017年8月22日下午2:54:04
 */
@SpringBootApplication
@EnableZuulProxy
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	/**自定义过滤器*/
	@Bean
	public LogFilter preRequestLogFilter() {
	 return new LogFilter();
	}
}
```



3、测试

 a、依次启动eureka-server（4010）、provide-user（4011）、hystrix-consumer-movie（5012）、microservice-gateway-zuul-filter（5017）

 b、访问http://localhost:5017/hystrix-consumer-movie/user/1 

 效果：

  [![wKioL1meO4iRTgcsAABhrBNVfco050.jpg](https://s2.51cto.com/wyfs02/M00/9F/86/wKioL1meO4iRTgcsAABhrBNVfco050.jpg)](https://s2.51cto.com/wyfs02/M00/9F/86/wKioL1meO4iRTgcsAABhrBNVfco050.jpg)

**3.3、禁用过滤器**

Spring Cloud默认为Zuul编写了一些过滤器，如DebugFilter、FormBodyWrap-perFilter等，这些过滤器都放在spring-cloud-netflix-core这个jar包的org.springframework.cloud.netflix.zuul.filters包中。在某些场景下，我们可能需要禁用某些过滤器。



禁用方法非常简单，只需设置zuul.<SimpleClassName>.<filterType>.disable=true即可禁用SimpleClassName对应的过滤器。



以org.springframework.cloud.netflix.zuul.filters.pre.DebugFilter为例，如：

```pl
zuul.DebugFilter.post.disable=true
```



以前面我们自定义的com.liuy.filter.LogFilter为例，如：

```pl
zuul.LogFilter.pre.disable=true
```