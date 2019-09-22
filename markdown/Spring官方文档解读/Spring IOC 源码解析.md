# Spring IOC 源码解析

### 控制反转（IoC）

控制反转（IoC）原理的Spring Framework实现。**IoC也称为依赖注入（DI）**。这是一个过程，通过这个过程，对象只能通过构造函数参数，工厂方法的参数或在构造或从工厂方法返回后在对象实例上设置的属性来定义它们的依赖关系（即，它们使用的其他对象）。 。然后容器在创建bean时注入这些依赖项。此过程基本上是bean本身的逆（因此名称，控制反转），通过使用类的直接构造或诸如服务定位器模式的机制来控制其依赖关系的实例化或位置。（官方文档5.x说明）。所谓IoC，对于spring框架来说，就是由spring来负责控制对象的生命周期和对象间的关系。

> **基于XML的元数据不是唯一允许的配置元数据形式。Spring IoC容器本身完全与实际编写此配置元数据的格式分离。目前，许多开发人员为其Spring应用程序选择 [基于Java的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java)。**

有关在Spring容器中使用其他形式的元数据的信息，请参阅：

- [基于注释的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-annotation-config)：Spring 2.5引入了对基于注释的配置元数据的支持。（我这里不解释）

- [基于Java的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java)：从Spring 3.0开始，Spring JavaConfig项目提供的许多功能成为核心Spring Framework的一部分。因此，您可以使用Java而不是XML文件在应用程序类外部定义bean。要使用这些新功能，请参阅 @Configuration， @Bean， @Import，和@DependsOn注释。

首先了解什么是spring Bean？spring的bean是具有完整的spring生命周期的对象。

那spring生命周期的经历了什么呢？

初始化、属性赋值、实例化、销毁。

spring容器可以称为spring上下文或者spring环境。

spring容器包含各种组件，如：bean工厂、单例池、读取器、扫描器、处理器、后置处理器等

这里将会解释一下：

##### bean的初始化和实例化：

bean的初始化：一个对象被new出来，完成属性方法填充，最后生命周期回调方法执行

bean的实例化：一个类变成bean的过程

Spring的生命周期

Spring的9个后置处理器

### 流传的大部分博客对spring生命周期的看法

不知道Spring官方对Bean的生命问题是否有明确的定义或者解析，但是Spring In Action以及市面上流传的大部分博客是这样的：

1. 实例化Bean对象，这个时候Bean的对象是非常低级的，基本不能够被我们使用，因为连最基本的属性都没有设置，可以理解为连Autowired注解都是没有解析的；
2. 填充属性，当做完这一步，Bean对象基本是完整的了，可以理解为Autowired注解已经解析完毕，依赖注入完成了；
3. 如果Bean实现了BeanNameAware接口，则调用setBeanName方法；
4. 如果Bean实现了BeanClassLoaderAware接口，则调用setBeanClassLoader方法；
5. 如果Bean实现了BeanFactoryAware接口，则调用setBeanFactory方法；
6. 调用BeanPostProcessor的postProcessBeforeInitialization方法；
7. 如果Bean实现了InitializingBean接口，调用afterPropertiesSet方法；
8. 如果Bean定义了init-method方法，则调用Bean的init-method方法；
9. 调用BeanPostProcessor的postProcessAfterInitialization方法；当进行到这一步，Bean已经被准备就绪了，一直停留在应用的上下文中，直到被销毁；
10. 如果应用的上下文被销毁了，如果Bean实现了DisposableBean接口，则调用destroy方法，如果Bean定义了destory-method声明了销毁方法也会被调用。



### 基于Java的配置

从这里出发：

    AnnotationConfigApplicationContext context=
        new AnnotationConfigApplicationContext(AppConfig.class);

AnnotationConfigApplicationContext的结构关系：

![](E:\markdown\Spring官方文档解读\AnnotationConfigApplicationContext联系.PNG)

创建AnnotationConfigApplicationContext对象

```java
//根据参数类型可以知道，其实可以传入多个annotatedClasses，但是这种情况出现的比较少
    public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        //调用无参构造函数，会先调用父类GenericApplicationContext的构造函数
        //父类的构造函数里面就是初始化DefaultListableBeanFactory，并且赋值给beanFactory
        //本类的构造函数里面，初始化了一个读取器：AnnotatedBeanDefinitionReader read，一个扫描器ClassPathBeanDefinitionScanner scanner
        //scanner的用处不是很大，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
        this();
        //把传入的类进行注册，这里有两个情况，
        //传入传统的配置类
        //传入bean（虽然一般没有人会这么做
        //看到后面会知道spring把传统的带上@Configuration的配置类称之为FULL配置类，不带@Configuration的称之为Lite配置类
        //但是我们这里先把带上@Configuration的配置类称之为传统配置类，不带的称之为普通bean
        register(annotatedClasses);
        //刷新
        refresh();
    }
```

> 我们先来为构造方法做一个简单的说明：

1. 这是一个有参的构造方法，可以接收多个配置类，不过一般情况下，只会传入一个配置类。

2. 这个配置类有两种情况，一种是传统意义上的带上@Configuration注解的配置类，还有一种是没有带上@Configuration，但是带有@Component，@Import，@ImportResouce，@Service，@ComponentScan等注解的配置类，在Spring内部把前者称为Full配置类，把后者称之为Lite配置类。在本源码分析中，有些地方也把Lite配置类称为**普通Bean**。

使用断点调试，通过this()调用此类无参的构造方法，代码到下面：

```java
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

    //注解bean定义读取器，主要作用是用来读取被注解的了bean
    private final AnnotatedBeanDefinitionReader reader;

    //扫描器，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
    private final ClassPathBeanDefinitionScanner scanner;

    /**
     * Create a new AnnotationConfigApplicationContext that needs to be populated
     * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
     */
    public AnnotationConfigApplicationContext() {
        //会隐式调用父类的构造方法，初始化DefaultListableBeanFactory

        //初始化一个Bean读取器
        this.reader = new AnnotatedBeanDefinitionReader(this);

        //初始化一个扫描器，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
        this.scanner = new ClassPathBeanDefinitionScanner(this);
    }
}
```

首先无参构造方法中就是对读取器`reader`和扫描器`scanner`进行了实例化，reader的类型是`AnnotatedBeanDefinitionReader`，可以看出它是一个 “打了注解的Bean定义读取器”，scanner的类型是`ClassPathBeanDefinitionScanner`，它仅仅是在外面手动调用.scan方法，或者调用参数为String的构造方法，传入需要扫描的包名才会用到，像这样方式传入的配置类是不会用到这个scanner对象的。

AnnotationConfigApplicationContext类是有继承关系的，会隐式调用父类的构造方法：

下面代码，初始化DefaultListableBeanFactory

```java
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

    private final DefaultListableBeanFactory beanFactory;

    @Nullable
    private ResourceLoader resourceLoader;

    private boolean customClassLoader = false;

    private final AtomicBoolean refreshed = new AtomicBoolean();


    /**
     * Create a new GenericApplicationContext.
     * @see #registerBeanDefinition
     * @see #refresh
     */
    public GenericApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }
}
```

DefaultListableBeanFactory的关系图

![](E:\markdown\Spring官方文档解读\DefaultListableBeanFactory的关系图.PNG)

> DefaultListableBeanFactory是相当重要的，从字面意思就可以看出它是一个Bean的工厂，什么是Bean的工厂？当然就是用来生产和获得Bean的。

让我们把目光回到AnnotationConfigApplicationContext的无参构造方法，让我们看看Spring在初始化AnnotatedBeanDefinitionReader的时候做了什么：

```java
 public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, getOrCreateEnvironment(registry));
    }
```

这里的BeanDefinitionRegistry当然就是AnnotationConfigApplicationContext的实例了，这里又直接调用了此类其他的构造方法：

```java
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }
```

让我们把目光移动到这个方法的最后一行，进入registerAnnotationConfigProcessors方法：

```java
    public static void registerAnnotationConfigProcessors(BeanDefinitionRegistry registry) {
        registerAnnotationConfigProcessors(registry, null);
    }
```

这又是一个门面方法，再点进去，这个方法的返回值Set，但是上游方法并没有去接收这个返回值，所以这个方法的返回值也不是很重要了，当然方法内部给这个返回值赋值也不重要了。由于这个方法内容比较多，这里就把最核心的贴出来，这个方法的核心就是注册Spring内置的多个Bean：

```java
if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
            def.setSource(source);
            beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
}
```

1. 判断容器中是否已经存在了`ConfigurationClassPostProcessor` Bean
2. 如果不存在（当然这里肯定是不存在的），就通过RootBeanDefinition的构造方法获得`ConfigurationClassPostProcessor`的`BeanDefinition`，`RootBeanDefinition`是`BeanDefinition`的子类：
3. 执行registerPostProcessor方法，registerPostProcessor方法内部就是注册Bean，当然这里注册其他Bean也是一样的流程。

#### BeanDefinition是什么？

BeanDefinition联系图

![](E:\markdown\Spring官方文档解读\BeanDefinition联系图.PNG)

### 

它是用来描述Bean的，里面存放着关于Bean的一系列信息，比如Bean的作用域，Bean所对应的Class，是否懒加载，是否Primary等等，这个BeanDefinition也相当重要，我们以后会常常和它打交道。**

registerPostProcessor方法：

```java
    private static BeanDefinitionHolder registerPostProcessor(
            BeanDefinitionRegistry registry, RootBeanDefinition definition, String beanName) {

        definition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        registry.registerBeanDefinition(beanName, definition);
        return new BeanDefinitionHolder(definition, beanName);
    }
```

这方法为BeanDefinition设置了一个Role，ROLE_INFRASTRUCTURE代表这是spring内部的，并非用户定义的，然后又调用了registerBeanDefinition方法，再点进去，Oh No，你会发现它是一个接口，没办法直接点进去了，首先要知道registry实现类是什么，那么它的实现是什么呢？答案是DefaultListableBeanFactory：

```java
public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }
```

这又是一个门面方法，再点进去，核心在于下面两行代码：

```java
//beanDefinitionMap是Map<String, BeanDefinition>，
//这里就是把beanName作为key，ScopedProxyMode作为value，推到map里面
this.beanDefinitionMap.put(beanName, beanDefinition);

//beanDefinitionNames就是一个List<String>,这里就是把beanName放到List中去
this.beanDefinitionNames.add(beanName);
```

从这里可以看出DefaultListableBeanFactory就是我们所说的容器了，里面放着beanDefinitionMap，beanDefinitionNames，beanDefinitionMap是一个hashMap，beanName作为Key,beanDefinition作为Value，beanDefinitionNames是一个集合，里面存放了beanName。打个断点，第一次运行到这里，监视这两个变量：

![](E:\markdown\Spring官方文档解读\1240)

![](E:\markdown\Spring官方文档解读\1241)

**DefaultListableBeanFactory中的beanDefinitionMap，beanDefinitionNames也是相当重要的，以后会经常看到它，最好看到它，第一时间就可以反应出它里面放了什么数据**

这里仅仅是注册，可以简单的理解为把一些原料放入工厂，工厂还没有真正的去生产。

上面已经介绍过，这里会一连串注册好几个Bean，在这其中最重要的一个Bean（没有之一）就是BeanDefinitionRegistryPostProcessor Bean。

**ConfigurationClassPostProcessor实现BeanDefinitionRegistryPostProcessor接口，BeanDefinitionRegistryPostProcessor接口又扩展了BeanFactoryPostProcessor接口，BeanFactoryPostProcessor是Spring的扩展点之一，ConfigurationClassPostProcessor是Spring极为重要的一个类，必须牢牢的记住上面所说的这个类和它的继承关系。**

![](E:\markdown\Spring官方文档解读\1243)

除了注册了ConfigurationClassPostProcessor，还注册了其他Bean，其他Bean也都实现了其他接口，比如BeanPostProcessor等。

**BeanPostProcessor接口也是Spring的扩展点之一。**

至此，实例化AnnotatedBeanDefinitionReader reader分析完毕。

由于常规使用方式是不会用到AnnotationConfigApplicationContext里面的scanner的，所以这里就不看scanner是如何被实例化的了。

把目光回到最开始，再分析第二行代码：

```
register(annotatedClasses);
```

这里传进去的是一个数组，最终会循环调用如下方法：

```java
    <T> void doRegisterBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
            @Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {
        //AnnotatedGenericBeanDefinition可以理解为一种数据结构，是用来描述Bean的，这里的作用就是把传入的标记了注解的类
        //转为AnnotatedGenericBeanDefinition数据结构，里面有一个getMetadata方法，可以拿到类上的注解
        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);

        //判断是否需要跳过注解，spring中有一个@Condition注解，当不满足条件，这个bean就不会被解析
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }

        abd.setInstanceSupplier(instanceSupplier);

        //解析bean的作用域，如果没有设置的话，默认为单例
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());

        //获得beanName
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

        //解析通用注解，填充到AnnotatedGenericBeanDefinition，解析的注解为Lazy，Primary，DependsOn，Role，Description
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

        //限定符处理，不是特指@Qualifier注解，也有可能是Primary,或者是Lazy，或者是其他（理论上是任何注解，这里没有判断注解的有效性），如果我们在外面，以类似这种
        //AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(Appconfig.class);常规方式去初始化spring，
        //qualifiers永远都是空的，包括上面的name和instanceSupplier都是同样的道理
        //但是spring提供了其他方式去注册bean，就可能会传入了
        if (qualifiers != null) {
            //可以传入qualifier数组，所以需要循环处理
            for (Class<? extends Annotation> qualifier : qualifiers) {
                //Primary注解优先
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                }
                //Lazy注解
                else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                }
                //其他，AnnotatedGenericBeanDefinition有个Map<String,AutowireCandidateQualifier>属性，直接push进去
                else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }

        for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
            customizer.customize(abd);
        }

        //这个方法用处不大，就是把AnnotatedGenericBeanDefinition数据结构和beanName封装到一个对象中
        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);

        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);

        //注册，最终会调用DefaultListableBeanFactory中的registerBeanDefinition方法去注册，
        //DefaultListableBeanFactory维护着一系列信息，比如beanDefinitionNames，beanDefinitionMap
        //beanDefinitionNames是一个List<String>,用来保存beanName
        //beanDefinitionMap是一个Map,用来保存beanName和beanDefinition
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }
```

在这里又要说明下，以常规方式去注册配置类，此方法中除了第一个参数，其他参数都是默认值。

1. 通过AnnotatedGenericBeanDefinition的构造方法，获得配置类的BeanDefinition，这里是不是似曾相似，在注册ConfigurationClassPostProcessor类的时候，也是通过构造方法去获得BeanDefinition的，只不过当时是通过RootBeanDefinition去获得，现在是通过AnnotatedGenericBeanDefinition去获得。
   
   ![image.png](https://upload-images.jianshu.io/upload_images/15100432-7ecdc09d020a36a3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

2. 判断需不需要跳过注册，Spring中有一个@Condition注解，如果不满足条件，就会跳过这个类的注册。

3. 然后是解析作用域，如果没有设置的话，默认为单例。

4. 获得BeanName。

5. 解析通用注解，填充到AnnotatedGenericBeanDefinition，解析的注解为Lazy，Primary，DependsOn，Role，Description。

6. 限定符处理，不是特指@Qualifier注解，也有可能是Primary，或者是Lazy，或者是其他（理论上是任何注解，这里没有判断注解的有效性）。

7. 把AnnotatedGenericBeanDefinition数据结构和beanName封装到一个对象中（这个不是很重要，可以简单的理解为方便传参）。

8. 注册，最终会调用DefaultListableBeanFactory中的registerBeanDefinition方法去注册：

```java
    public static void registerBeanDefinition(
            BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry)
            throws BeanDefinitionStoreException {

        //获取beanName
        // Register bean definition under primary name.
        String beanName = definitionHolder.getBeanName();

        //注册bean
        registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());

                //Spring支持别名
        // Register aliases for bean name, if any.
        String[] aliases = definitionHolder.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                registry.registerAlias(beanName, alias);
            }
        }
    }
```

这个registerBeanDefinition是不是又有一种似曾相似的感觉，没错，在上面注册Spring内置的Bean的时候，已经解析过这个方法了，这里就不重复了，此时，让我们再观察下beanDefinitionMap beanDefinitionNames两个变量，除了Spring内置的Bean，还有我们传进来的Bean，这里的Bean当然就是我们的配置类了：

![image.png](https://upload-images.jianshu.io/upload_images/15100432-4d9a67b99e104c9b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](https://upload-images.jianshu.io/upload_images/15100432-f379c34313b2f0a8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

到这里注册配置类也分析完毕了。

大家可以看到其实到这里，Spring还没有进行扫描，只是实例化了一个工厂，注册了一些内置的Bean和我们传进去的配置类，真正的大头是在第三行代码：

```java
refresh();
```



为了验证上面的逻辑，可以做个试验：

首先定义了一个Bean，里面有各种回调和钩子，其中需要注意下，我在SpringBean的构造方法中打印了studentService，看SpringBean被new的出来的时候，studentService是否被注入了；又在setBeanName中打印了studentService，看此时studentService是否被注入了，以此来验证，Bean是何时完成的自动注入的（这个StudentServiceImpl 类的代码就不贴出来了，无非就是一个最普通的Bean）：

```java
public class SpringBean implements InitializingBean, DisposableBean, BeanNameAware, BeanFactoryAware, BeanClassLoaderAware {

    public SpringBean() {
        System.out.println("SpringBean构造方法:" + studentService);
        System.out.println("SpringBean构造方法");
    }

    @Autowired
    StudentServiceImpl studentService;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("setBeanClassLoader");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("setBeanFactory");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("setBeanName:" + studentService);
        System.out.println("setBeanName");
    }

    public void initMethod() {
        System.out.println("initMethod");
    }

    public void destroyMethod() {
        System.out.println("destroyMethod");
    }
}
```

再定义一个BeanPostProcessor，在重写的两个方法中进行了判断，如果传进来的beanName是springBean才进行打印：

```java
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("springBean")) {
            System.out.println("postProcessBeforeInitialization");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("springBean")) {
            System.out.println("postProcessAfterInitialization");
        }
        return bean;
    }
}
```

定义一个配置类，完成自动扫描，但是SpringBean是手动注册的，并且声明了initMethod和destroyMethod：

```java
@Configuration
@ComponentScan
public class AppConfig {
    @Bean(initMethod = "initMethod",destroyMethod = "destroyMethod")
    public SpringBean springBean() {
        return new SpringBean();
    }
}
```

最后就是启动类了：

```java
    public static void main(String[] args) {
        AnnotationConfigApplicationContext annotationConfigApplicationContext =
                new AnnotationConfigApplicationContext(AppConfig.class);
        annotationConfigApplicationContext.destroy();
    }
```

运行结果：

```java
SpringBean构造方法:null
SpringBean构造方法
setBeanName:com.codebear.StudentServiceImpl@31190526
setBeanName
setBeanClassLoader
setBeanFactory
postProcessBeforeInitialization
afterPropertiesSet
initMethod
postProcessAfterInitialization
destroy
destroyMethod
```

可以看到，试验结果和上面分析的完全一致。

这就是广为流传的Spring生命周期。

也许你在应付面试的时候，是死记硬背这些结论的，现在我带着你找到这些方法，跟我来。

首先我们来到AnnotationConfigApplicationContext的构造方法：

```java
    //根据参数类型可以知道，其实可以传入多个annotatedClasses，但是这种情况出现的比较少
    public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
        //调用无参构造函数，会先调用父类GenericApplicationContext的构造函数
        //父类的构造函数里面就是初始化DefaultListableBeanFactory，并且赋值给beanFactory
        //本类的构造函数里面，初始化了一个读取器：AnnotatedBeanDefinitionReader read，一个扫描器ClassPathBeanDefinitionScanner scanner
        //scanner的用处不是很大，它仅仅是在我们外部手动调用 .scan 等方法才有用，常规方式是不会用到scanner对象的
        this();
        //把传入的类进行注册，这里有两个情况，
        //传入传统的配置类
        //传入bean（虽然一般没有人会这么做
        //看到后面会知道spring把传统的带上@Configuration的配置类称之为FULL配置类，不带@Configuration的称之为Lite配置类
        //但是我们这里先把带上@Configuration的配置类称之为传统配置类，不带的称之为普通bean
        register(annotatedClasses);
        //刷新
        refresh();
    }
```

进入refresh方法，refresh方法中有一个finishBeanFactoryInitialization小方法，这个方法是用来实例化懒加载单例Bean的，也就是我们的Bean都是在这里被创建出来的（当然我这里说的的是绝大部分情况是这样的）：

```java
finishBeanFactoryInitialization(beanFactory);
```

我们再进入finishBeanFactoryInitialization这方法，里面有一个beanFactory.preInstantiateSingletons()方法：

```java
        //初始化所有的非懒加载单例
        beanFactory.preInstantiateSingletons();
```

我们尝试再点进去，这个时候你会发现这是一个接口，好在它只有一个实现类，所以可以我们来到了他的唯一实现，实现类就是org.springframework.beans.factory.support.DefaultListableBeanFactory，这里面是一个循环，我们的Bean就是循环被创建出来的，我们找到其中的getBean方法：

```java
getBean(beanName);
```

这里有一个分支，如果Bean是FactoryBean，如何如何，如果Bean不是FactoryBean如何如何，好在不管是不是FactoryBean，最终还是会调用getBean方法，所以我们可以毫不犹豫的点进去，点进去之后，你会发现，这是一个门面方法，直接调用了doGetBean方法：

```java
    return doGetBean(name, null, null, false);
```

再进去，不断的深入，接近我们要寻找的东西。
这里面的比较复杂，但是有我在，我可以直接告诉你，下一步我们要进入哪里，我们要进入

```java
if (mbd.isSingleton()) {

                    //getSingleton中的第二个参数类型是ObjectFactory<?>，是一个函数式接口，不会立刻执行，而是在
                    //getSingleton方法中，调用ObjectFactory的getObject，才会执行createBean
                    sharedInstance = getSingleton(beanName, () -> {
                        try {
                            return createBean(beanName, mbd, args);
                        }
                        catch (BeansException ex) {
                            destroySingleton(beanName);
                            throw ex;
                        }
                    });
                    bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
                }
```

这里面的createBean方法，再点进去啊，但是又点不进去了，这是接口啊，但是别慌，这个接口又只有一个实现类，所以说 没事，就是干，这个实现类为org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory。

这个实现的方法里面又做了很多事情，我们就不去看了，我就是带着大家找到那几个生命周期的回调到底定义在哪里就OK了。

```java
    Object beanInstance = doCreateBean(beanName, mbdToUse, args);//创建bean，核心
            if (logger.isDebugEnabled()) {
                logger.debug("Finished creating instance of bean '" + beanName + "'");
            }
            return beanInstance;
```

再继续深入doCreateBean方法，这个方法又做了一堆一堆的事情，但是值得开心的事情就是 我们已经找到了我们要寻找的东西了。

### 创建实例

首先是创建实例，位于：

```
instanceWrapper = createBeanInstance(beanName, mbd, args);//创建bean的实例。核心
```

### 填充属性

其次是填充属性，位于：

```
populateBean(beanName, mbd, instanceWrapper);//填充属性，炒鸡重要
```

在填充属性下面有一行代码：

```
    exposedObject = initializeBean(beanName, exposedObject, mbd);
```

继续深入进去。

### aware系列接口的回调

aware系列接口的回调位于initializeBean中的invokeAwareMethods方法：

```java
invokeAwareMethods(beanName, bean);
private void invokeAwareMethods(final String beanName, final Object bean) {
        if (bean instanceof Aware) {
            if (bean instanceof BeanNameAware) {
                ((BeanNameAware) bean).setBeanName(beanName);
            }
            if (bean instanceof BeanClassLoaderAware) {
                ClassLoader bcl = getBeanClassLoader();
                if (bcl != null) {
                    ((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
                }
            }
            if (bean instanceof BeanFactoryAware) {
                ((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
            }
        }
    }
```

### BeanPostProcessor的postProcessBeforeInitialization方法

BeanPostProcessor的postProcessBeforeInitialization方法位于initializeBean的

```java
if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
        }
    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
```

### afterPropertiesSet init-method

afterPropertiesSet init-method位于initializeBean中的

```
    invokeInitMethods(beanName, wrappedBean, mbd);
```

这里面调用了两个方法，一个是afterPropertiesSet方法，一个是init-method方法：

```java
    ((InitializingBean) bean).afterPropertiesSet();
invokeCustomInitMethod(beanName, bean, mbd);
```

### BeanPostProcessor的postProcessAfterInitialization方法

BeanPostProcessor的postProcessAfterInitialization方法位于initializeBean的

```java
if (mbd == null || !mbd.isSynthetic()) {
            wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
        }
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }
```

当然在实际的开发中，应该没人会去销毁Spring的应用上下文把，所以剩余的两个销毁的回调就不去找了。

这就是广为流传的Spring Bean的生命周期，我也带着大家找到了各种回调和钩子，但是我认为这并非是Spring Bean完整的生命周期，只是经过简化的，那么我认为的完整的生命周期是如何的呢，请听下回分解。