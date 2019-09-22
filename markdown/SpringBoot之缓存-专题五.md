## SpringBoot之缓存-专题五



### 一、JSR-107

> 本文JSR-107部分借鉴[JSR107](https://jcp.org/aboutJava/communityprocess/final/jsr107/index.html)官方文档

#### 1、介绍

JSR是Java Specification Requests的缩写，意思是Java 规范提案。是指向[JCP](https://baike.baidu.com/item/JCP)(Java Community Process)提出新增一个标准化技术规范的正式请求。任何人都可以提交JSR，以向Java平台增添新的API和服务。JSR已成为Java界的一个重要标准。

##### **1.1 JSR 107**

2012年10月26日JSR规范委员会发布了JSR 107（JCache API）的首个早期草案。自该JSR启动以来，已经过去近12年时间，因此该规范颇为Java社区所诟病，但由于目前对缓存需求越来越多，因此专家组加快了这一进度。



##### **1.2 规范**

JCache规范定义了一种对Java对象临时在内存中进行缓存的方法，包括对象的创建、共享访问、假脱机（spooling）、失效、各JVM的一致性等，可被用于缓存JSP内最经常读取的数据，如产品目录和价格列表。利用JCACHE，多数查询的反应时间会因为有缓存的数据而加快（内部测试表明反应时间大约快15倍）。



##### **1.3 发展方向**

JSR 107将成为2013年第2季度发布的JavaEE 7的一部分。

> 这里只是对jsr107基础了解

#### 2、Java Caching

Java Caching定义了5个核心接口，分别是**CachingProvider**, **CacheManager**, **Cache**, **Entry** 和 **Expiry**。

**CachingProvider**定义了创建、配置、获取、管理和控制多个**CacheManager**。一个应用可以在运行期访问多个CachingProvider。

**CacheManager**定义了创建、配置、获取、管理和控制多个唯一命名的**Cache**，这些Cache存在于CacheManager的上下文中。一个CacheManager仅被一个CachingProvider所拥有。

**Cache**是一个类似Map的数据结构并临时存储以Key为索引的值。一个Cache仅被一个CacheManager所拥有。

**Entry**是一个存储在Cache中的key-value对。

**Expiry** 每一个存储在Cache中的条目有一个定义的有效期。一旦超过这个时间，条目为过期的状态。一旦过期，条目将不可访问、更新和删除。缓存有效期可以通过ExpiryPolicy设置。

### 二、Spring缓存抽象

#### 1、介绍

Spring从3.1开始定义了org.springframework.cache.Cache

和org.springframework.cache.CacheManager接口来统一不同的缓存技术；

并支持使用JCache（JSR-107）注解简化我们开发；

![1561518655577](C:\Users\No\AppData\Roaming\Typora\typora-user-images\1561518655577.png)

Cache接口为缓存的组件规范定义，包含缓存的各种操作集合；

Cache接口下Spring提供了各种xxxCache的实现；如RedisCache，EhCacheCache , ConcurrentMapCache等；



每次调用需要缓存功能的方法时，Spring会检查检查指定参数的指定的目标方法是否已经被调用过；如果有就直接从缓存中获取方法调用后的结果，如果没有就调用方法并缓存结果后返回给用户。下次调用直接从缓存中获取。

使用Spring缓存抽象时我们需要关注以下两点；

1、确定方法需要被缓存以及他们的缓存策略

2、从缓存中读取之前缓存存储的数据



将方法的运行结果进行缓存；以后再要相同的数据，直接从缓存中获取，不用调用方法； CacheManager管理多个Cache组件的，对缓存的真正CRUD操作在Cache组件中，每一个缓存组件有自己唯一一个名字；

#### 2、缓存注解

##### 2.1 **Cache**

缓存接口，定义缓存操作。实现有：RedisCache、EhCacheCache、ConcurrentMapCache等

##### 2.2 **CacheManager**

缓存管理器，管理各种缓存（Cache）组件

##### 2.3 **@Cacheable**

**主要针对方法配置，能够根据方法的请求参数对其结果进行缓存**

运行流程：
1、方法运行之前，先去查询Cache（缓存组件），按照cacheNames指定的名字获取；
（CacheManager先获取相应的缓存），第一次获取缓存如果没有Cache组件会自动创建。
2、去Cache中查找缓存的内容，使用一个key，默认就是方法的参数；
		 key是按照某种策略生成的；默认是使用keyGenerator生成的，默认使用SimpleKeyGenerator生成key；
 		SimpleKeyGenerator生成key的默认策略；
		如果没有参数；key=new SimpleKey()；
		如果有一个参数：key=参数的值
		如果有多个参数：key=new SimpleKey(params)；
3、没有查到缓存就调用目标方法；
 4、将目标方法返回的结果，放进缓存中

核心：

1）、使用CacheManager【ConcurrentMapCacheManager】按照名字得到Cache【ConcurrentMapCache】组件

2）、key使用keyGenerator生成的，默认是SimpleKeyGenerator

```java
属性：
cacheNames/value：指定缓存组件的名字;将方法的返回结果放在哪个缓存中，是数组的方式，可以指定多个缓存；
key：缓存数据使用的key；可以用它来指定。默认是使用方法参数的值  1-方法的返回值
                   编写SpEL； #i d;参数id的值   #a0  #p0  #root.args[0]
                  getEmp[2]
keyGenerator：key的生成器；可以自己指定key的生成器的组件id
                 key/keyGenerator：二选一使用;

cacheManager：指定缓存管理器；或者cacheResolver指定获取解析器

condition：指定符合条件的情况下才缓存；
		condition = "#id>0"
		condition = "#a0>1"：第一个参数的值》1的时候才进行缓存
unless:否定缓存；当unless指定的条件为true，方法的返回值就不会被缓存；可以获取到结果进行判断
		unless = "#result == null"
 		unless = "#a0==2":如果第一个参数的值是2，结果不缓存；
sync：是否使用异步模式

@Cacheable(value = {"emp"},keyGenerator = "myKeyGenerator",condition = "#a0>1",unless = "#a0==2")
```

2.4 @CachePut

**保证方法被调用，又希望结果被缓存。**



既调用方法，又更新缓存数据；同步更新缓存；修改了数据库的某个数据，同时更新缓存；

运行时机：

1、先调用目标方法

2、将目标方法的结果缓存起来

```java
@CachePut(value = "emp",key = "#result.id")
```



2.5 @CacheEvict

**清空缓存**

key：指定要清除的数据

allEntries = true：指定清除这个缓存中所有的数据

beforeInvocation = false：缓存的清除是否在方法之前执行;默认代表缓存清除操作是在方法执行之后执行;如果出现异常缓存就不会清除

 beforeInvocation = true：代表清除缓存操作是在方法运行之前执行，无论方法是否出现异常，缓存都清除



```java
@CacheEvict(value="emp",beforeInvocation = truekey = "#id",)
```



2.6 @EnableCaching

**开启基于注解的缓存**

2.7 @Caching

定义复杂的缓存规则

```java
@Caching(
         cacheable = {
             @Cacheable(value="emp",key = "#lastName")
         },
         put = {
             @CachePut(value="emp",key = "#result.id"),
             @CachePut(value="emp",key = "#result.email")
         }
    )
```



#### 3、Cache原理

1、自动配置类；CacheAutoConfiguration
2、缓存的配置类

```java
org.springframework.boot.autoconfigure.cache.GenericCacheConfiguration
org.springframework.boot.autoconfigure.cache.JCacheCacheConfiguration
org.springframework.boot.autoconfigure.cache.EhCacheCacheConfiguration
org.springframework.boot.autoconfigure.cache.HazelcastCacheConfiguration
org.springframework.boot.autoconfigure.cache.InfinispanCacheConfiguration
org.springframework.boot.autoconfigure.cache.CouchbaseCacheConfiguration
org.springframework.boot.autoconfigure.cache.RedisCacheConfiguration
org.springframework.boot.autoconfigure.cache.CaffeineCacheConfiguration
org.springframework.boot.autoconfigure.cache.GuavaCacheConfiguration
org.springframework.boot.autoconfigure.cache.SimpleCacheConfiguration【默认】
org.springframework.boot.autoconfigure.cache.NoOpCacheConfiguration
```

3、配置类默认生效：SimpleCacheConfiguration；

4、给容器中注册了一个CacheManager：ConcurrentMapCacheManager
5、可以获取和创建ConcurrentMapCache类型的缓存组件；他的作用将数据保存在ConcurrentMap中；



#### 4、Cache的使用

新建工程的什么的都不说了，直接上代码。

pom.xml文件依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
</parent>

<dependencies>
		<!--cache缓存 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		<!--Redis -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool2</artifactId>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.10</version>
		</dependency>
		<!-- 测试 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.1.1</version>
		</dependency>
		<!-- mysql 依赖 -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!-- springboot-web组件 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- springboot 整合 pagehelper -->
		<dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper-spring-boot-starter</artifactId>
			<version>1.2.5</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>3.7</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<optional>true</optional>
			<scope>true</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
		</dependency>

		<!-- spring boot start -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
			<exclusions>
				<!-- 排除自带的logback依赖 -->
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<!-- springboot-log4j -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j</artifactId>
			<version>1.3.8.RELEASE</version>
		</dependency>
		<!-- aop -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
	</dependencies>
```

实体类

```java
package com.gitboy.redis.pojo;

import java.io.Serializable;

public class Department implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String departmentName;
	
	
	public Department() {
		super();
	}
	public Department(Integer id, String departmentName) {
		super();
		this.id = id;
		this.departmentName = departmentName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	@Override
	public String toString() {
		return "Department [id=" + id + ", departmentName=" + departmentName + "]";
	}
	

}

```

```java
package com.gitboy.redis.pojo;

import java.io.Serializable;

public class Employee implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String lastName;
	private String email;
	private Integer gender; //性别 1男  0女
	private Integer dId;
	
	
	public Employee() {
		super();
	}

	
	public Employee(Integer id, String lastName, String email, Integer gender, Integer dId) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.dId = dId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getdId() {
		return dId;
	}
	public void setdId(Integer dId) {
		this.dId = dId;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", dId="
				+ dId + "]";
	}


}

```

配置类

```java
package com.gitboy.redis.conf;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

@Configuration
public class MyCacheConfig {

    @Bean("myKeyGenerator")
    public KeyGenerator keyGenerator(){
        return new KeyGenerator(){

            @Override
            public Object generate(Object target, Method method, Object... params) {
                return method.getName()+"["+ Arrays.asList(params).toString()+"]";
            }
        };
    }
}
```

mapper

```java
package com.gitboy.redis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.gitboy.redis.pojo.Department;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT * FROM department WHERE id = #{id}")
    Department getDeptById(Integer id);
}

```

```java
package com.gitboy.redis.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gitboy.redis.pojo.Employee;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE id = #{id}")
    public Employee getEmpById(Integer id);

    @Update("UPDATE employee SET lastName=#{lastName},email=#{email},gender=#{gender},d_id=#{dId} WHERE id=#{id}")
    public void updateEmp(Employee employee);

    @Delete("DELETE FROM employee WHERE id=#{id}")
    public void deleteEmpById(Integer id);

    @Insert("INSERT INTO employee(lastName,email,gender,d_id) VALUES(#{lastName},#{email},#{gender},#{dId})")
    public void insertEmployee(Employee employee);

    @Select("SELECT * FROM employee WHERE lastName = #{lastName}")
    Employee getEmpByLastName(String lastName);
}

```



service

```java
package com.gitboy.redis.service;

import com.gitboy.redis.pojo.Department;

public interface DeptService {

	Department getDeptById(Integer id);

}

package com.gitboy.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitboy.redis.mapper.DepartmentMapper;
import com.gitboy.redis.pojo.Department;
import com.gitboy.redis.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService {

	@Autowired
	private DepartmentMapper departmentMapper;

	@Override
	public Department getDeptById(Integer id) {

		System.out.println("查询部门"+id);
        Department department = departmentMapper.getDeptById(id);
        return department;
	}
}

```

```java
package com.gitboy.redis.service;

import com.gitboy.redis.pojo.Employee;

public interface EmployeeService {

	Employee getEmp(Integer id);

	Employee updateEmp(Employee employee);

	void deleteEmp(Integer id);

	Employee getEmpByLastName(String lastName);


}
package com.gitboy.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gitboy.redis.mapper.DepartmentMapper;
import com.gitboy.redis.pojo.Department;
import com.gitboy.redis.service.DeptService;

@Service
public class DeptServiceImpl implements DeptService {

	@Autowired
	private DepartmentMapper departmentMapper;

	@Override
	public Department getDeptById(Integer id) {

		System.out.println("查询部门"+id);
        Department department = departmentMapper.getDeptById(id);
        return department;
	}
}

```

controller

```java
package com.gitboy.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gitboy.redis.pojo.Department;
import com.gitboy.redis.service.DeptService;

@RestController
public class DeptController {

	@Autowired
	private DeptService deptService;
	
	
    @GetMapping("/dept/{id}")
    public Department getDept(@PathVariable("id") Integer id){
        return deptService.getDeptById(id);
    }
}
```

```java
package com.gitboy.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gitboy.redis.pojo.Employee;
import com.gitboy.redis.service.EmployeeService;

/**
 * @author No
 *  缓存采用cache
 */
@RestController
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;
	
	@GetMapping("/emp/{id}")
    public Employee getEmployee(@PathVariable("id") Integer id){
        Employee employee = employeeService.getEmp(id);
        return employee;
    }

    @GetMapping("/emp")
    public Employee update(Employee employee){
        Employee emp = employeeService.updateEmp(employee);

        return emp;
    }

    @GetMapping("/delemp")
    public String deleteEmp(Integer id){
        employeeService.deleteEmp(id);
        return "success";
    }

    @GetMapping("/emp/lastname/{lastName}")
    public Employee getEmpByLastName(@PathVariable("lastName") String lastName){
       return employeeService.getEmpByLastName(lastName);
    }
	
}


```



yml文件

```yml
#端口
server:
  port: 8081
  
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springboot?characterEncoding=utf8&amp;useSSL=false
    username: root
    password: root
    type: com.alibaba.druid.pool.DruidDataSource
  thymeleaf:
    cache: false
    
  redis:
    host: 134.175.30.90
mybatis:
  configuration:
    map-underscore-to-camel-case: true
    
#分页插件
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
  page-size-zero: true
```

日志文件

```properties
#log4j.rootLogger=CONSOLE,info,error,DEBUG
log4j.rootLogger=info,error,CONSOLE,DEBUG
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender     
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout     
log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n     
log4j.logger.info=info
log4j.appender.info=org.apache.log4j.DailyRollingFileAppender
log4j.appender.info.layout=org.apache.log4j.PatternLayout     
log4j.appender.info.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.info.datePattern='.'yyyy-MM-dd
log4j.appender.info.Threshold = info   
log4j.appender.info.append=true   
#log4j.appender.info.File=/home/admin/pms-api-services/logs/info/api_services_info
log4j.appender.info.File=C:logs/info/api_services_info
log4j.logger.error=error  
log4j.appender.error=org.apache.log4j.DailyRollingFileAppender
log4j.appender.error.layout=org.apache.log4j.PatternLayout     
log4j.appender.error.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.error.datePattern='.'yyyy-MM-dd
log4j.appender.error.Threshold = error   
log4j.appender.error.append=true   
#log4j.appender.error.File=/home/admin/pms-api-services/logs/error/api_services_error
log4j.appender.error.File=C:logs/error/api_services_error
log4j.logger.DEBUG=DEBUG
log4j.appender.DEBUG=org.apache.log4j.DailyRollingFileAppender
log4j.appender.DEBUG.layout=org.apache.log4j.PatternLayout     
log4j.appender.DEBUG.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.DEBUG.datePattern='.'yyyy-MM-dd
log4j.appender.DEBUG.Threshold = DEBUG   
log4j.appender.DEBUG.append=true   
#log4j.appender.DEBUG.File=/home/admin/pms-api-services/logs/debug/api_services_debug
log4j.appender.DEBUG.File=C:logs/debug/api_services_debug
```



启动类

```java
package com.gitboy.redis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages="com.gitboy.redis.mapper")
@EnableScheduling
@EnableCaching
public class AppRedis {
    public static void main(String[] args) {

        SpringApplication.run(AppRedis.class, args);
    }
}

```





### 三、SpringBoot整合Redis缓存中间件

> 想要了解redis的基础入门可以查看往期的[Redis初识](https://juejin.im/post/5d123e6c6fb9a07f0870a472)



#### 1、Redis常见的五大数据类型

String（字符串）、List（列表）、Set（集合）、Hash（散列）、ZSet（有序集合）

SpringBoot整合封装：

stringRedisTemplate.opsForValue()[String（字符串）]

stringRedisTemplate.opsForList()[List（列表）]

stringRedisTemplate.opsForSet()[Set（集合）]

stringRedisTemplate.opsForHash()[Hash（散列）]

stringRedisTemplate.opsForZSet()[ZSet（有序集合）]

#### 2、RedisCacheConfiguration使用

新建工程的什么的都不说了，直接上代码。

pom.xml文件依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.0.0.RELEASE</version>
</parent>

<dependencies>
		<!--cache缓存 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		<!--Redis -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-pool2</artifactId>
		</dependency>

		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.1.10</version>
		</dependency>
		<!-- 测试 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>1.1.1</version>
		</dependency>
		<!-- mysql 依赖 -->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!-- springboot-web组件 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<!-- springboot 整合 pagehelper -->
		<dependency>
			<groupId>com.github.pagehelper</groupId>
			<artifactId>pagehelper-spring-boot-starter</artifactId>
			<version>1.2.5</version>
		</dependency>
		<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-lang3</artifactId>
			<version>3.7</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<optional>true</optional>
			<scope>true</scope>
		</dependency>
		<dependency>
			<groupId>org.apache.tomcat.embed</groupId>
			<artifactId>tomcat-embed-jasper</artifactId>
		</dependency>

		<!-- spring boot start -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
			<exclusions>
				<!-- 排除自带的logback依赖 -->
				<exclusion>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>

		<!-- springboot-log4j -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j</artifactId>
			<version>1.3.8.RELEASE</version>
		</dependency>
		<!-- aop -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
	</dependencies>
```

实体类

```java
package com.gitboy.redis.pojo;

import java.io.Serializable;

public class Department implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String departmentName;
	
	
	public Department() {
		super();
	}
	public Department(Integer id, String departmentName) {
		super();
		this.id = id;
		this.departmentName = departmentName;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	@Override
	public String toString() {
		return "Department [id=" + id + ", departmentName=" + departmentName + "]";
	}
	

}

```

```java
package com.gitboy.redis.pojo;

import java.io.Serializable;

public class Employee implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String lastName;
	private String email;
	private Integer gender; //性别 1男  0女
	private Integer dId;
	
	
	public Employee() {
		super();
	}

	
	public Employee(Integer id, String lastName, String email, Integer gender, Integer dId) {
		super();
		this.id = id;
		this.lastName = lastName;
		this.email = email;
		this.gender = gender;
		this.dId = dId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getGender() {
		return gender;
	}
	public void setGender(Integer gender) {
		this.gender = gender;
	}
	public Integer getdId() {
		return dId;
	}
	public void setdId(Integer dId) {
		this.dId = dId;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", lastName=" + lastName + ", email=" + email + ", gender=" + gender + ", dId="
				+ dId + "]";
	}


}

```

Redis的配置

```java
package com.gitboy.redis.conf;
 
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
 
import java.time.Duration;
 
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
 
    /**
     * 配置自定义redisTemplate
     * 
     *org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
     * @param 
     * @return
     */
   @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
 
        RedisTemplate<String, Object> template = new RedisTemplate<>();
 
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
 
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
 
        template.setConnectionFactory(factory);
        //key序列化方式
        template.setKeySerializer(redisSerializer);
        //value序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        //value hashmap序列化
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
 
        return template;
    }
 
    /**
     * json序列化
     * org.springframework.data.redis.serializer.RedisSerializer<T>
     * org.springframework.data.redis.serializer.RedisSerializer.serialize(T)
     * @return
     */
    @Bean
    public RedisSerializer<Object> jackson2JsonRedisSerializer() {
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
 
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        return serializer;
    }
 
 
    /**
     * 配置缓存管理器
     * org.springframework.cache.support.AbstractCacheManager
     * org.springframework.cache.CacheManager
     * @param
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
 
        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
 
        // 配置序列化（解决乱码的问题）,过期时间
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
                .disableCachingNullValues();
 
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}

```

mapper

```java
package com.gitboy.redis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.gitboy.redis.pojo.Department;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT * FROM department WHERE id = #{id}")
    Department getDeptById(Integer id);
}

```

```java
package com.gitboy.redis.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gitboy.redis.pojo.Employee;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employee WHERE id = #{id}")
    public Employee getEmpById(Integer id);

    @Update("UPDATE employee SET lastName=#{lastName},email=#{email},gender=#{gender},d_id=#{dId} WHERE id=#{id}")
    public void updateEmp(Employee employee);

    @Delete("DELETE FROM employee WHERE id=#{id}")
    public void deleteEmpById(Integer id);

    @Insert("INSERT INTO employee(lastName,email,gender,d_id) VALUES(#{lastName},#{email},#{gender},#{dId})")
    public void insertEmployee(Employee employee);

    @Select("SELECT * FROM employee WHERE lastName = #{lastName}")
    Employee getEmpByLastName(String lastName);
}

```

service

```java
package com.gitboy.redis.service;

import com.gitboy.redis.pojo.Employee;

public interface EmployeeRedisService {

	Employee getEmp(Integer id);

	Employee updateEmp(Employee employee);

	void deleteEmp(Integer id);

	Employee getEmpByLastName(String lastName);


}




package com.gitboy.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.gitboy.redis.mapper.EmployeeMapper;
import com.gitboy.redis.pojo.Employee;
import com.gitboy.redis.service.EmployeeRedisService;

@CacheConfig(cacheNames= {"empRedis"})
@Service
public class EmployeeRedisServiceImpl implements EmployeeRedisService {

	@Autowired
	private EmployeeMapper employeeMapper;
	
	
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Cacheable(value="empRedis"/*,keyGenerator = "myKeyGenerator",unless="#result == null"key = "#id"*/)
	@Override
	public Employee getEmp(Integer id) {
		System.out.println("查询"+id+"号员工");
        Employee emp = employeeMapper.getEmpById(id);
        
        //empRedisTemplate.opsForValue().set(id, emp);
        
        
        return emp;
	}

	@CachePut(key = "#result.id")
	@Override
	public Employee updateEmp(Employee employee) {
		System.out.println("updateEmp:"+employee);
        employeeMapper.updateEmp(employee);
        return employee;
	}

	@CacheEvict(beforeInvocation = true/*key = "#id",*/)
	@Override
	public void deleteEmp(Integer id) {
		System.out.println("deleteEmp:"+id);
        employeeMapper.deleteEmpById(id);
        //int i = 10/0;
	}

	 @Caching(
	         cacheable = {
	             @Cacheable(key = "#lastName")
	         },
	         put = {
	             @CachePut(key = "#result.id"),
	             @CachePut(key = "#result.email")
	         }
	    )
	@Override
	public Employee getEmpByLastName(String lastName) {
		return employeeMapper.getEmpByLastName(lastName);
	}

}

```

```java
package com.gitboy.redis.service;

import com.gitboy.redis.pojo.Department;

public interface DeptRedisService {

	Department getDeptById(Integer id);

}


package com.gitboy.redis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.gitboy.redis.mapper.DepartmentMapper;
import com.gitboy.redis.pojo.Department;
import com.gitboy.redis.service.DeptRedisService;

@Service
@CacheConfig(cacheNames= {"deptRedis"})
public class DeptRedisServiceImpl implements DeptRedisService {

	@Autowired
	private DepartmentMapper departmentMapper;

	@Cacheable(key="#id")
	@Override
	public Department getDeptById(Integer id) {

		System.out.println("查询部门"+id);
        Department department = departmentMapper.getDeptById(id);
        return department;
	}
}

```

comtroller

```java
package com.gitboy.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gitboy.redis.pojo.Department;
import com.gitboy.redis.service.DeptRedisService;

@RestController
public class DeptRedisController {

	@Autowired
	private DeptRedisService deptRedisService;
	
	
    @GetMapping("/deptRedis/{id}")
    public Department getDept(@PathVariable("id") Integer id){
        return deptRedisService.getDeptById(id);
    }
}
```



```java
package com.gitboy.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gitboy.redis.pojo.Employee;
import com.gitboy.redis.service.EmployeeRedisService;

/**
 * @author No
 * 缓存采用redis中间件
 */
@RestController
public class EmployeeRedisController {

	@Autowired
	private EmployeeRedisService employeeRedisService;
	
	@GetMapping("/empRedis/{id}")
    public Employee getEmployee(@PathVariable("id") Integer id){
        Employee employee = employeeRedisService.getEmp(id);
        return employee;
    }

    @GetMapping("/empRedis")
    public Employee update(Employee employee){
        Employee emp = employeeRedisService.updateEmp(employee);

        return emp;
    }

    @GetMapping("/delEmpRedis")
    public String deleteEmp(Integer id){
    	employeeRedisService.deleteEmp(id);
        return "success";
    }

    @GetMapping("/empRedis/lastname/{lastName}")
    public Employee getEmpByLastName(@PathVariable("lastName") String lastName){
       return employeeRedisService.getEmpByLastName(lastName);
    }
	
}


```





yml文件

```yml
#端口
server:
  port: 8081
  
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springboot?characterEncoding=utf8&amp;useSSL=false
    username: root
    password: root
    type: com.alibaba.druid.pool.DruidDataSource
  thymeleaf:
    cache: false
    
  redis:
    host: 134.175.30.90
mybatis:
  configuration:
    map-underscore-to-camel-case: true
    
#分页插件
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
  page-size-zero: true
```

日志文件

```properties
#log4j.rootLogger=CONSOLE,info,error,DEBUG
log4j.rootLogger=info,error,CONSOLE,DEBUG
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender     
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout     
log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n     
log4j.logger.info=info
log4j.appender.info=org.apache.log4j.DailyRollingFileAppender
log4j.appender.info.layout=org.apache.log4j.PatternLayout     
log4j.appender.info.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.info.datePattern='.'yyyy-MM-dd
log4j.appender.info.Threshold = info   
log4j.appender.info.append=true   
#log4j.appender.info.File=/home/admin/pms-api-services/logs/info/api_services_info
log4j.appender.info.File=C:logs/info/api_services_info
log4j.logger.error=error  
log4j.appender.error=org.apache.log4j.DailyRollingFileAppender
log4j.appender.error.layout=org.apache.log4j.PatternLayout     
log4j.appender.error.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.error.datePattern='.'yyyy-MM-dd
log4j.appender.error.Threshold = error   
log4j.appender.error.append=true   
#log4j.appender.error.File=/home/admin/pms-api-services/logs/error/api_services_error
log4j.appender.error.File=C:logs/error/api_services_error
log4j.logger.DEBUG=DEBUG
log4j.appender.DEBUG=org.apache.log4j.DailyRollingFileAppender
log4j.appender.DEBUG.layout=org.apache.log4j.PatternLayout     
log4j.appender.DEBUG.layout.ConversionPattern=%d{yyyy-MM-dd-HH-mm} [%t] [%c] [%p] - %m%n  
log4j.appender.DEBUG.datePattern='.'yyyy-MM-dd
log4j.appender.DEBUG.Threshold = DEBUG   
log4j.appender.DEBUG.append=true   
#log4j.appender.DEBUG.File=/home/admin/pms-api-services/logs/debug/api_services_debug
log4j.appender.DEBUG.File=C:logs/debug/api_services_debug
```



启动类

```java
package com.gitboy.redis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages="com.gitboy.redis.mapper")
@EnableScheduling
@EnableCaching
public class AppRedis {
    public static void main(String[] args) {

        SpringApplication.run(AppRedis.class, args);
    }
}

```

