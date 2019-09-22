# Spring 通读官方文档

这部分参考文档涵盖了Spring Framework绝对不可或缺的所有技术。

其中最重要的是Spring Framework的控制反转（IoC）容器。Spring框架的IoC容器的全面处理紧随其后，全面覆盖了Spring的面向方面编程（AOP）技术。Spring Framework有自己的AOP框架，它在概念上易于理解，并且成功地解决了Java企业编程中AOP要求的80％最佳点。

还提供了Spring与AspectJ集成的覆盖范围（目前最丰富的 - 在功能方面 - 当然也是Java企业领域中最成熟的AOP实现）。

## 1. IoC容器

先从Spring的控制反转（IoC）容器开始。

### 1.1 Spring IoC容器和Bean简介

本章介绍了控制反转（IoC）原理的Spring Framework实现。**IoC也称为依赖注入（DI）**。这是一个过程，通过这个过程，对象只能通过构造函数参数，工厂方法的参数或在构造或从工厂方法返回后在对象实例上设置的属性来定义它们的依赖关系（即，它们使用的其他对象）。 。然后容器在创建bean时注入这些依赖项。此过程基本上是bean本身的逆（因此名称，控制反转），通过使用类的直接构造或诸如服务定位器模式的机制来控制其依赖关系的实例化或位置。

在`org.springframework.beans`和`org.springframework.context`包是Spring框架的IoC容器的基础。该 BeanFactory 接口提供了一种能够管理任何类型对象的高级配置机制。 ApplicationContext 是一个子界面BeanFactory。它补充说：

- 更容易与Spring的AOP功能集成

- 消息资源处理（用于国际化）

- 活动出版

- 特定WebApplicationContext 于应用程序层的上下文，例如在Web应用程序中使用的上下文。

简而言之，它BeanFactory提供了配置框架和基本功能，并ApplicationContext添加了更多特定于企业的功能。它ApplicationContext是完整的超集，BeanFactory在本章中仅用于Spring的IoC容器的描述。有关使用BeanFactory而不是ApplicationContext,看到 的BeanFactory更多信息。

在Spring中，构成应用程序主干并由Spring IoC容器管理的对象称为bean。bean是一个由Spring IoC容器实例化，组装和管理的对象。否则，bean只是应用程序中许多对象之一。Bean及其之间的依赖关系反映在容器使用的配置元数据中。

## 1.2 集装箱概览

该`org.springframework.context.ApplicationContext`接口代表Spring IoC容器，负责实例化，配置和组装bean。容器通过读取配置元数据获取有关要实例化，配置和组装的对象的指令。配置元数据以XML，Java注释或Java代码表示。它允许您表达组成应用程序的对象以及这些对象之间丰富的相互依赖性。

ApplicationContextSpring提供了几种接口实现。在独立应用程序中，通常会创建一个ClassPathXmlApplicationContext 或的实例 FileSystemXmlApplicationContext。虽然XML是定义配置元数据的传统格式，但您可以通过提供少量XML配置来声明容器使用Java注释或代码作为元数据格式，以声明方式启用对这些其他元数据格式的支持。

在大多数应用程序方案中，不需要显式用户代码来实例化Spring IoC容器的一个或多个实例。例如，在Web应用程序场景中，应用程序文件中的简单八行（左右）样板Web描述符XML web.xml通常就足够了（请参阅Web应用程序的便捷ApplicationContext实例）。如果您使用 Spring Tool Suite（基于Eclipse的开发环境），只需点击几下鼠标或按键即可轻松创建此样板配置。

下图显示了Spring如何工作的高级视图。您的应用程序类与配置元数据相结合，以便在ApplicationContext创建和初始化之后，您拥有完全配置且可执行的系统或应用程序。
![Spring IoC容器-图1](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/images/container-magic.png)

### 1.2.1 配置元数据

如上图所示，Spring IoC容器使用一种配置元数据。此配置元数据表示您作为应用程序开发人员如何告诉Spring容器在应用程序中实例化，配置和组装对象。

传统上，配置元数据以简单直观的XML格式提供，本章的大部分内容用于传达Spring IoC容器的关键概念和功能。

> **基于XML的元数据不是唯一允许的配置元数据形式。Spring IoC容器本身完全与实际编写此配置元数据的格式分离。目前，许多开发人员为其Spring应用程序选择 [基于Java的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java)。**

有关在Spring容器中使用其他形式的元数据的信息，请参阅：

- [基于注释的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-annotation-config)：Spring 2.5引入了对基于注释的配置元数据的支持。（我这里不细写）

- [基于Java的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java)：从Spring 3.0开始，Spring JavaConfig项目提供的许多功能成为核心Spring Framework的一部分。因此，您可以使用Java而不是XML文件在应用程序类外部定义bean。要使用这些新功能，请参阅 @Configuration， @Bean， @Import，和@DependsOn注释。

Spring配置包含容器必须管理的至少一个且通常不止一个bean定义。基于XML的配置元数据将这些bean配置为<bean/>顶级元素内的<beans/>元素。Java配置通常@Bean在@Configuration类中使用注释方法。

这些bean定义对应于构成应用程序的实际对象。通常，您定义服务层对象，数据访问对象（DAO），表示对象（如Struts Action实例），基础结构对象（如Hibernate SessionFactories，JMS Queues等）。通常，不会在容器中配置细粒度域对象，因为DAO和业务逻辑通常负责创建和加载域对象。但是，您可以使用Spring与AspectJ的集成来配置在IoC容器控制之外创建的对象。请参阅使用AspectJ[使用Spring依赖注入域对象](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#aop-atconfigurable)。

以下示例显示了基于XML的配置元数据的基本结构：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="..." class="...">   
        <!-- collaborators and configuration for this bean go here
        id属性是一个标识单个bean定义的字符串
        该class属性定义bean的类型并使用完全限定的类名
     -->
    </bean>

    <bean id="..." class="...">
        <!-- collaborators and configuration for this bean go here -->
    </bean>

    <!-- more bean definitions go here -->

</beans>
```

### 1.2.2 实例化容器

提供给ApplicationContext构造函数的位置路径是资源字符串，它允许容器从各种外部资源（如本地文件系统，Java等）加载配置元数据CLASSPATH。

    ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

在了解了Spring的IoC容器之后，您可能想要了解有关Spring Resource抽象的更多信息 （如参考资料中所述），它提供了一种从URI语法中定义的位置读取InputStream的便捷机制。特别是， Resource路径用于构建应用程序上下文，如应用程序上下文和资源路径中所述。

以下示例显示了服务层对象(services.xml)配置文件：

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <!-- services -->
    
        <bean id="petStore" class="org.springframework.samples.jpetstore.services.PetStoreServiceImpl">
            <property name="accountDao" ref="accountDao"/>
            <property name="itemDao" ref="itemDao"/>
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>
    
        <!-- more bean definitions for services go here -->
    
    </beans>

以下示例显示了数据访问对象daos.xml文件：

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="accountDao"
           class="org.springframework.samples.jpetstore.dao.jpa.JpaAccountDao">
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>
    
        <bean id="itemDao" class="org.springframework.samples.jpetstore.dao.jpa.JpaItemDao">
            <!-- additional collaborators and configuration for this bean go here -->
        </bean>
    
        <!-- more bean definitions for data access objects go here -->
    
    </beans>

在前面的示例中，服务层由的PetStoreServiceImpl类和类型的两个数据访问对象JpaAccountDao和JpaItemDao（基于JPA对象关系映射标准）。该property name元素是指JavaBean属性的名称，以及ref元素指的是另一个bean定义的名称。元素id和ref元素之间的这种联系表达了协作对象之间的依赖关系。有关配置对象的依赖关系的详细信息，请参阅 依赖关系。

编写基于XML的配置元数据
让bean定义跨越多个XML文件会很有用。通常，每个单独的XML配置文件都代表架构中的逻辑层或模块。

您可以使用应用程序上下文构造函数从所有这些XML片段加载bean定义。此构造函数采用多个Resource位置，如上一节中所示 。或者，使用一个或多个<import/>元素来从另一个或多个文件加载bean定义。以下示例显示了如何执行此操作：

    <beans>
        <import resource="services.xml"/>
        <import resource="resources/messageSource.xml"/>
        <import resource="/resources/themeSource.xml"/>
    
        <bean id="bean1" class="..."/>
        <bean id="bean2" class="..."/>
    </beans>

在前面的例子中，外部豆定义是从三个文件加载： services.xml，messageSource.xml，和themeSource.xml。所有位置路径都与执行导入的定义文件相关，因此services.xml必须与执行导入的文件位于相同的目录或类路径位置， messageSource.xml而且themeSource.xml必须位于resources导入文件位置下方的位置。如您所见，忽略前导斜杠。但是，鉴于这些路径是相对的，最好不要使用斜杠。<beans/>根据Spring Schema，正在导入的文件的内容（包括顶级元素）必须是有效的XML bean定义。

> 可以（但不建议）使用相对“../”路径引用父目录中的文件。这样做会对当前应用程序之外的文件创建依赖关系。特别是，不建议对classpath:URL（例如，classpath:../services.xml）使用此引用，其中运行时解析过程选择“最近的”类路径根，然后查看其父目录。类路径配置更改可能导致选择不同的，不正确的目录。
> 
> 您始终可以使用完全限定的资源位置而不是相对路径：例如，file:C:/config/services.xml或classpath:/config/services.xml。但是，请注意您将应用程序的配置与特定的绝对位置耦合。通常最好为这些绝对位置保持间接 - 例如，通过在运行时针对JVM系统属性解析的“$ {...}”占位符。

命名空间本身提供了导入指令功能。Spring提供的一系列XML命名空间中提供了除普通bean定义之外的其他配置功能 - 例如，context和util名称空间。

Groovy Bean定义DSL
作为外化配置元数据的另一个示例，bean定义也可以在Spring的Groovy Bean定义DSL中表示，如Grails框架中所知。通常，此类配置位于“.groovy”文件中，其结构如下例所示：

    beans {
        dataSource(BasicDataSource) {
            driverClassName = "org.hsqldb.jdbcDriver"
            url = "jdbc:hsqldb:mem:grailsDB"
            username = "sa"
            password = ""
            settings = [mynew:"setting"]
        }
        sessionFactory(SessionFactory) {
            dataSource = dataSource
        }
        myService(MyService) {
            nestedBean = { AnotherBean bean ->
                dataSource = dataSource
            }
        }
    }

### 1.2.3 使用容器

它ApplicationContext是高级工厂的接口，能够维护不同bean及其依赖项的注册表。通过使用该方法T getBean(String name, Class<T> requiredType)，您可以检索Bean的实例。

将ApplicationContext让你读bean定义和访问它们，如下例所示：

```java
// create and configure beans
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

// retrieve configured instance
PetStoreService service = context.getBean("petStore", PetStoreService.class);

// use configured instance
List<String> userList = service.getUsernameList();
```

使用Groovy配置，bootstrapping看起来非常相似。它有一个不同的上下文实现类，它是Groovy-aware（但也理解XML bean定义）。以下示例显示了Groovy配置：

```java
ApplicationContext context = new GenericGroovyApplicationContext("services.groovy", "daos.groovy");
```

最灵活的变体GenericApplicationContext与读者委托相结合 - 例如，XmlBeanDefinitionReader对于XML文件，如以下示例所示

```java
GenericApplicationContext context = new GenericApplicationContext();
new XmlBeanDefinitionReader(context).loadBeanDefinitions("services.xml", "daos.xml");
context.refresh();
```

您还可以使用GroovyBeanDefinitionReaderfor Groovy文件，如以下示例所示：

```java
    GenericApplicationContext context = new GenericApplicationContext();
new GroovyBeanDefinitionReader(context).loadBeanDefinitions("services.groovy", "daos.groovy");
context.refresh();
```

您可以ApplicationContext在不同的配置源中读取和匹配此类读取器委托，读取bean定义。

然后，您可以使用它getBean来检索Bean的实例。该ApplicationContext 接口还有一些其他方法可用于检索bean，但理想情况下，应用程序代码永远不应使用它们。实际上，您的应用程序代码根本不应该调用该 getBean()方法，因此根本不依赖于Spring API。例如，Spring与Web框架的集成为各种Web框架组件（如控制器和JSF托管bean）提供依赖注入，允许您通过元数据（例如自动装配注释）声明对特定bean的依赖性。

## 1.3 Bean概述

Spring IoC容器管理一个或多个bean。这些bean是使用您提供给容器的配置元数据创建的（例如，以XML <bean/>定义的形式 ）。

在容器本身内，这些bean定义表示为BeanDefinition 对象，其中包含（以及其他信息）以下元数据：

- 包限定的类名：通常是正在定义的bean的实际实现类。

- Bean行为配置元素，说明bean在容器中的行为方式（范围，生命周期回调等）。

- 引用bean执行其工作所需的其他bean。这些引用也称为协作者或依赖项。

- 要在新创建的对象中设置的其他配置设置 - 例如，池的大小限制或在管理连接池的Bean中使用的连接数。

此元数据转换为构成每个bean定义的一组属性。下表描述了这些属性：

| 属性      | 解释在......                                                                                                                               |
| ------- |:---------------------------------------------------------------------------------------------------------------------------------------:|
| 类       | [实例化bean​​类](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-class)                 |
| 名称      | [命名bean](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-beanname)                          |
| 范围      | [Bean范围](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-scopes)                    |
| 构造函数参数  | [依赖注入](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators)               |
| 属性      | [依赖注入](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-collaborators)               |
| 自动装配模式  | [自动化协作者](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-autowire)                  |
| 延迟初始化模式 | [懒惰初始化的豆类](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lazy-init)               |
| 初始化方法   | [初始化回调](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean) |
| 破坏方法    | [毁灭回调](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean)    |

除了包含有关如何创建特定bean的信息的bean定义之外，这些`ApplicationContext`实现还允许注册在容器外部（由用户）创建的现有对象。这是通过方法访问ApplicationContext的BeanFactory来完成的`getBeanFactory()`，该方法返回BeanFactory `DefaultListableBeanFactory`实现。`DefaultListableBeanFactory` 通过`registerSingleton(..)`和 `registerBeanDefinition(..)`方法支持此注册。但是，典型的应用程序仅使用通过常规bean定义元数据定义的bean。

> 需要尽早注册Bean元数据和手动提供的单例实例，以便容器在自动装配和其他内省步骤期间正确推理它们。虽然在某种程度上支持覆盖现有元数据和现有单例实例，但是在运行时注册新bean（与对工厂的实时访问同时）并未得到官方支持，并且可能导致并发访问异常，bean容器中的状态不一致，或者都。

### 1.3.1 命名bean

每个bean都有一个或多个标识符。这些标识符在托管bean的容器中必须是唯一的。bean通常只有一个标识符。但是，如果它需要多个，则额外的可以被视为别名。

在基于XML的配置元数据中，您可以使用id属性，name属性或两者来指定bean标识符。该id属性允许您指定一个id。通常，这些名称是字母数字（'myBean'，'someService'等），但它们也可以包含特殊字符。如果要为bean引入其他别名，还可以在name 属性中指定它们，用逗号（,），分号（;）或空格分隔。作为历史记录，在Spring 3.1之前的版本中，该id属性被定义为一种xsd:ID类型，它约束了可能的字符。从3.1开始，它被定义为一种xsd:string类型。请注意，id容器仍然强制实施bean 唯一性，但不再是XML解析器。

您不需要提供bean name或idbean。如果您不提供 name或id显式提供，则容器会为该bean生成唯一的名称。但是，如果要按名称引用该bean，则通过使用ref元素或 Service Locator样式查找，必须提供名称。不提供名称的动机与使用内部bean和自动装配协作者有关。

> Bean命名约定
> 惯例是在命名bean时使用标准Java约定作为实例字段名称。也就是说，bean名称以小写字母开头，并从那里开始驼峰。这样的名字的例子包括accountManager， accountService，userDao，loginController，等等。
> 
> 命名bean始终使您的配置更易于阅读和理解。此外，如果您使用Spring AOP，那么在将建议应用于与名称相关的一组bean时，它会有很大帮助。

*通过类路径中的组件扫描，Spring按照前面描述的规则为未命名的组件生成bean名称：实质上，采用简单的类名并将其初始字符转换为小写。但是，在（不常见的）特殊情况下，当有多个字符且第一个和第二个字符都是大写字母时，原始外壳将被保留。这些规则与java.beans.Introspector.decapitalize（Spring在此处使用）定义的规则相同。*

在Bean定义之外别名Bean
在bean定义本身中，您可以为bean提供多个名称，方法是使用id属性指定的最多一个名称和属性中的任意数量的其他名称name。这些名称可以是同一个bean的等效别名，对某些情况很有用，例如让应用程序中的每个组件通过使用特定于该组件本身的bean名称来引用公共依赖项。

但是，指定实际定义bean的所有别名并不总是足够的。有时需要为其他地方定义的bean引入别名。在大型系统中通常就是这种情况，其中配置在每个子系统之间分配，每个子系统具有其自己的一组对象定义。在基于XML的配置元数据中，您可以使用该<alias/>元素来完成此任务。以下示例显示了如何执行此操作：

    <alias name="fromName" alias="toName"/>

> Java的配置
> 如果使用Javaconfiguration，则@Bean可以使用注释来提供别名。有关详细信息，请参阅使用[@Bean注释](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-bean-annotation "1.12.3")。

### 1.3.2 实例化bean​​类

bean定义本质上是用于创建一个或多个对象的配方。容器在被询问时查看命名bean的配方，并使用由该bean定义封装的配置元数据来创建（或获取）实际对象。

如果使用基于XML的配置元数据，则指定要在元素的class属性中实例化的对象的类型（或类）<bean/>。此 class属性（在内部，是 实例Class上的属性BeanDefinition）通常是必需的。（有关例外，请参阅 使用实例工厂方法和Bean定义继承进行实例化。）您可以通过以下Class两种方式之一使用该属性：

- 通常，在容器本身通过反向调用其构造函数直接创建bean的情况下指定要构造的bean类，稍微等同于使用new运算符的Java代码。

- 要指定包含static为创建对象而调用的工厂方法的实际类，在不太常见的情况下，容器static在类上调用 工厂方法来创建bean。从调用static工厂方法返回的对象类型可以完全是同一个类或另一个类。

> 内部类名
> 如果要为static嵌套类配置bean定义，则必须使用嵌套类的二进制名称。
> 
> 例如，如果您SomeThing在com.example包中调用了一个类，并且此类 SomeThing具有一个static被调用的嵌套类OtherThing，则class bean定义中的属性值将为com.example.SomeThing$OtherThing。
> 
> 请注意，使用$名称中的字符将嵌套类名与外部类名分开。

使用构造函数实例化
当您通过构造方法创建bean时，所有普通类都可以使用并与Spring兼容。也就是说，正在开发的类不需要实现任何特定接口或以特定方式编码。简单地指定bean类就足够了。但是，根据您为该特定bean使用的IoC类型，您可能需要一个默认（空）构造函数。

Spring IoC容器几乎可以管理您希望它管理的任何类。它不仅限于管理真正的JavaBeans。大多数Spring用户更喜欢实际的JavaBeans，只有一个默认（无参数）构造函数，并且在容器中的属性之后建模了适当的setter和getter。您还可以在容器中拥有更多异国情调的非bean样式类。例如，如果您需要使用绝对不符合JavaBean规范的旧连接池，那么Spring也可以对其进行管理。

使用基于XML的配置元数据，您可以按如下方式指定bean类：

    <bean id="exampleBean" class="examples.ExampleBean"/>
    
    <bean name="anotherExample" class="examples.ExampleBeanTwo"/>

有关为构造函数提供参数的机制（如果需要）以及在构造对象后设置对象实例属性的详细信息，请参阅 注入依赖项。

**使用静态工厂方法实例化**

定义使用静态工厂方法创建的bean时，请使用该class 属性指定包含static工厂方法的类，并使用`factory-method`名称的属性**指定工厂方法本身的名称**。您应该能够调用此方法（使用可选参数，如稍后所述）并返回一个活动对象，随后将其视为通过构造函数创建的对象。这种bean定义的一个用途是static在遗留代码中调用工厂。

以下bean定义指定通过调用工厂方法来创建bean。该定义未指定返回对象的类型（类），仅指定包含工厂方法的类。在此示例中，该createInstance() 方法必须是静态方法。以下示例显示如何指定工厂方法：

    <bean id="clientService"
    class="examples.ClientService"
    factory-method="createInstance"/>

以下示例显示了一个可以使用前面的bean定义的类：
    public class ClientService {
        private static ClientService clientService = new ClientService();
        private ClientService() {}

```java
    public static ClientService createInstance() {
        return clientService;
    }
}
```

**使用实例工厂方法实例化**

与通过静态工厂方法实例化类似，使用实例工厂方法进行实例化会从容器调用现有bean的非静态方法来创建新bean。要使用此机制，请将该class属性保留为空，并在factory-bean属性中指定当前（或父或祖先）容器中bean的名称，该容器包含要调用以创建对象的实例方法。使用factory-method属性设置工厂方法本身的名称。以下示例显示如何配置此类bean：

        <!-- the factory bean, which contains a method called createInstance() -->
    <bean id="serviceLocator" class="examples.DefaultServiceLocator">
        <!-- inject any dependencies required by this locator bean -->
    </bean>
    
    <!-- the bean to be created via the factory bean -->
    <bean id="clientService"
        factory-bean="serviceLocator"
        factory-method="createClientServiceInstance"/>

以下示例显示了相应的Java类：

```java
public class DefaultServiceLocator {

    private static ClientService clientService = new ClientServiceImpl();

    public ClientService createClientServiceInstance() {
        return clientService;
    }
}
```

一个工厂类也可以包含多个工厂方法，如以下示例所示：

    <bean id="serviceLocator" class="examples.DefaultServiceLocator">
        <!-- inject any dependencies required by this locator bean -->
    </bean>
    
    <bean id="clientService"
        factory-bean="serviceLocator"
        factory-method="createClientServiceInstance"/>
    
    <bean id="accountService"
        factory-bean="serviceLocator"
        factory-method="createAccountServiceInstance"/>

以下示例显示了相应的Java类：
    public class DefaultServiceLocator {

        private static ClientService clientService = new ClientServiceImpl();
    
        private static AccountService accountService = new AccountServiceImpl();
    
        public ClientService createClientServiceInstance() {
            return clientService;
        }
    
        public AccountService createAccountServiceInstance() {
            return accountService;
        }
    }

这种方法表明工厂bean本身可以通过依赖注入（DI）进行管理和配置。请参阅详细信息中的依赖关系和配置。

> 在Spring文档中，“工厂bean”是指在Spring容器中配置并通过实例或 静态工厂方法创建对象的bean 。相比之下， FactoryBean（注意大写）指的是特定于Spring的 FactoryBean。

## 1.4 依赖

典型的企业应用程序不包含单个对象（或Spring用法中的bean）。即使是最简单的应用程序也有一些对象可以协同工作，以呈现最终用户所看到的连贯应用程序。下一节将介绍如何定义多个独立的bean定义，以及对象协作实现目标的完全实现的应用程序。

### 1.4.1 依赖注入

依赖注入（DI）是一个过程，通过这个过程，对象只能通过构造函数参数，工厂方法的参数或在构造对象实例后在对象实例上设置的属性来定义它们的依赖关系（即，它们使用的其他对象）。从工厂方法返回。然后容器在创建bean时注入这些依赖项。这个过程基本上是bean本身的反向（因此名称，控制反转），它通过使用类的直接构造或服务定位器模式来控制其依赖项的实例化或位置。

使用DI原则的代码更清晰，当对象提供其依赖项时，解耦更有效。该对象不查找其依赖项，也不知道依赖项的位置或类。因此，您的类变得更容易测试，特别是当依赖关系在接口或抽象基类上时，这允许在单元测试中使用存根或模拟实现。

DI存在两个主要变体：基于构造函数的依赖注入和基于Setter的依赖注入。

#### 基于构造函数的依赖注入

基于构造函数的DI由容器调用具有多个参数的构造函数来完成，每个参数表示一个依赖项。调用static具有特定参数的工厂方法来构造bean几乎是等效的，本讨论同样处理构造函数和static工厂方法的参数。以下示例显示了一个只能通过构造函数注入进行依赖注入的类：
    public class SimpleMovieLister {

        // the SimpleMovieLister has a dependency on a MovieFinder
        private MovieFinder movieFinder;
    
        // a constructor so that the Spring container can inject a MovieFinder
        public SimpleMovieLister(MovieFinder movieFinder) {
            this.movieFinder = movieFinder;
        }
    
        // business logic that actually uses the injected MovieFinder is omitted...
    }

请注意，这个类没有什么特别之处。它是一个POJO，它不依赖于容器特定的接口，基类或注释。

####构造函数参数解析
通过使用参数的类型进行构造函数参数解析匹配。如果bean定义的构造函数参数中不存在潜在的歧义，那么在bean定义中定义构造函数参数的顺序是在实例化bean时将这些参数提供给适当的构造函数的顺序。考虑以下课程：

    package x.y;
    
    public class ThingOne {
    
        public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
            // ...
        }
    }

假设ThingTwo并且ThingThree类与继承无关，则不存在潜在的歧义。因此，以下配置工作正常，您不需要在<constructor-arg/> 元素中显式指定构造函数参数索引或类型。

    <beans>
        <bean id="beanOne" class="x.y.ThingOne">
            <constructor-arg ref="beanTwo"/>
            <constructor-arg ref="beanThree"/>
        </bean>
    
        <bean id="beanTwo" class="x.y.ThingTwo"/>
    
        <bean id="beanThree" class="x.y.ThingThree"/>
    </beans>

当引用另一个bean时，类型是已知的，并且可以发生匹配（与前面的示例一样）。当使用简单类型时，例如 <value>true</value>，Spring无法确定值的类型，因此无法在没有帮助的情况下按类型进行匹配。考虑以下课程：

    package examples;
    
    public class ExampleBean {
    
        // Number of years to calculate the Ultimate Answer
        private int years;
    
        // The Answer to Life, the Universe, and Everything
        private String ultimateAnswer;
    
        public ExampleBean(int years, String ultimateAnswer) {
            this.years = years;
            this.ultimateAnswer = ultimateAnswer;
        }
    }

####构造函数参数类型匹配
在前面的场景中，如果使用type属性显式指定构造函数参数的类型，则容器可以使用与简单类型的类型匹配。如下例所示：

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg type="int" value="7500000"/>
        <constructor-arg type="java.lang.String" value="42"/>
    </bean>

####构造函数参数索引
您可以使用该index属性显式指定构造函数参数的索引，如以下示例所示：

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg index="0" value="7500000"/>
        <constructor-arg index="1" value="42"/>

>     </bean>
> 
> 除了解决多个简单值的歧义之外，指定索引还可以解决构造函数具有相同类型的两个参数的歧义。

####构造函数参数名称
您还可以使用构造函数参数名称进行值消歧，如以下示例所示：

    <bean id="exampleBean" class="examples.ExampleBean">
        <constructor-arg name="years" value="7500000"/>
        <constructor-arg name="ultimateAnswer" value="42"/>
    </bean>

请记住，为了使这项工作开箱即用，必须在启用调试标志的情况下编译代码，以便Spring可以从构造函数中查找参数名称。如果您不能或不想使用debug标志编译代码，则可以使用 @ConstructorProperties JDK批注显式命名构造函数参数。然后，示例类必须如下所示：

    package examples;
    
    public class ExampleBean {
    
        // Fields omitted
    
        @ConstructorProperties({"years", "ultimateAnswer"})
        public ExampleBean(int years, String ultimateAnswer) {
            this.years = years;
            this.ultimateAnswer = ultimateAnswer;
        }
    }

基于Setter的依赖注入
在调用无参数构造函数或无参数static工厂方法来实例化bean之后，基于setter的DI由bean上的容器调用setter方法完成。

以下示例显示了一个只能通过使用纯setter注入进行依赖注入的类。这个类是传统的Java。它是一个POJO，它不依赖于容器特定的接口，基类或注释。

public class SimpleMovieLister {

    // the SimpleMovieLister has a dependency on the MovieFinder
    private MovieFinder movieFinder;
    
    // a setter method so that the Spring container can inject a MovieFinder
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
    
    // business logic that actually uses the injected MovieFinder is omitted...

}
它ApplicationContext支持它管理的bean的基于构造函数和基于setter的DI。在通过构造函数方法注入了一些依赖项之后，它还支持基于setter的DI。您可以以a的形式配置依赖项，并将BeanDefinition其与PropertyEditor实例结合使用，以将属性从一种格式转换为另一种格式。然而，大多数Spring用户不直接与这些类（即，编程），而是用XML bean 定义注释的组件（也就是带注释类@Component， @Controller等），或者@Bean方法在基于Java的@Configuration类。然后，这些源在内部转换为实例BeanDefinition并用于加载整个Spring IoC容器实例。

####基于构造函数或基于setter的DI？
由于您可以混合基于构造函数和基于setter的DI，因此将构造函数用于强制依赖项和setter方法或可选依赖项的配置方法是一个很好的经验法则。请注意， 在setter方法上使用@Required注释可用于使属性成为必需的依赖项; 但是，最好使用编程验证参数的构造函数注入。

Spring团队通常提倡构造函数注入，因为它允许您将应用程序组件实现为不可变对象，并确保所需的依赖项不是null。此外，构造函数注入的组件始终以完全初始化的状态返回到客户端（调用）代码。作为旁注，大量的构造函数参数是一个糟糕的代码气味，暗示该类可能有太多的责任，应该重构以更好地解决关注点的正确分离。

Setter注入应主要仅用于可在类中指定合理默认值的可选依赖项。否则，必须在代码使用依赖项的任何位置执行非空检查。setter注入的一个好处是setter方法使该类的对象可以在以后重新配置或重新注入。因此，通过JMX MBean进行管理是二次注入的一个引人注目的用例。

使用对特定类最有意义的DI样式。有时，在处理您没有源的第三方类时，会选择您。例如，如果第三方类没有公开任何setter方法，那么构造函数注入可能是唯一可用的DI形式。

####依赖性解决过程
容器执行bean依赖性解析，如下所示：

使用ApplicationContext描述所有bean的配置元数据创建和初始化。配置元数据可以由XML，Java代码或注释指定。

对于每个bean，它的依赖关系以属性，构造函数参数或static-factory方法的参数的形式表示（如果使用它而不是普通的构造函数）。实际创建bean时，会将这些依赖项提供给bean。

每个属性或构造函数参数都是要设置的值的实际定义，或者是对容器中另一个bean的引用。

作为值的每个属性或构造函数参数都从其指定的格式转换为该属性或构造函数参数的实际类型。默认情况下，Spring能够转换成字符串格式提供给所有内置类型的值，例如int， long，String，boolean，等等。

Spring容器在创建容器时验证每个bean的配置。但是，在实际创建bean之前，不会设置bean属性本身。创建容器时会创建单例作用域并设置为预先实例化（默认值）的Bean。范围在Bean范围中定义。否则，仅在请求时才创建bean。创建bean可能会导致创建bean的图形，因为bean的依赖关系及其依赖关系（依此类推）被创建和分配。请注意，这些依赖项之间的解决方案不匹配可能会显示较晚 - 也就是说，首次创建受影响的bean时。

####循环依赖
如果您主要使用构造函数注入，则可以创建无法解析的循环依赖关系场景。

例如：类A通过构造函数注入需要类B的实例，而类B通过构造函数注入需要类A的实例。如果将A类和B类的bean配置为相互注入，则Spring IoC容器会在运行时检测到此循环引用，并抛出a BeanCurrentlyInCreationException。

一种可能的解决方案是编辑由setter而不是构造函数配置的某些类的源代码。或者，避免构造函数注入并仅使用setter注入。换句话说，尽管不推荐使用，但您可以使用setter注入配置循环依赖项。

与典型情况（没有循环依赖）不同，bean A和bean B之间的循环依赖强制其中一个bean在完全初始化之前被注入另一个bean（一个经典的鸡与蛋的场景）。

你通常可以相信Spring做正确的事。它在容器加载时检测配置问题，例如对不存在的bean和循环依赖关系的引用。当实际创建bean时，Spring会尽可能晚地设置属性并解析依赖关系。这意味着，如果在创建该对象或其中一个依赖项时出现问题，则在请求对象时，正确加载的Spring容器可以在以后生成异常 - 例如，bean因缺失或无效而抛出异常属性。这可能会延迟一些配置问题的可见性ApplicationContext默认情况下实现预实例化单例bean。以实际需要之前创建这些bean的一些前期时间和内存为代价，您ApplicationContext会在创建时发现配置问题，而不是更晚。您仍然可以覆盖此默认行为，以便单例bean可以懒惰地初始化，而不是预先实例化。

如果不存在循环依赖关系，当一个或多个协作bean被注入依赖bean时，每个协作bean在被注入依赖bean之前完全配置。这意味着，如果bean A依赖于bean B，则Spring IoC容器在调用bean A上的setter方法之前完全配置bean B.换句话说，bean被实例化（如果它不是预先实例化的单例），设置其依赖项，并调用相关的生命周期方法（如配置的init方法 或InitializingBean回调方法）。

依赖注入的示例
以下示例将基于XML的配置元数据用于基于setter的DI。Spring XML配置文件的一小部分指定了一些bean定义，如下所示：

    <bean id="exampleBean" class="examples.ExampleBean">
        <!-- setter injection using the nested ref element -->
        <property name="beanOne">
            <ref bean="anotherExampleBean"/>
        </property>
    
        <!-- setter injection using the neater ref attribute -->
        <property name="beanTwo" ref="yetAnotherBean"/>
        <property name="integerProperty" value="1"/>
    </bean>
    
    <bean id="anotherExampleBean" class="examples.AnotherBean"/>
    <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

以下示例显示了相应的ExampleBean类：

    public class ExampleBean {
    
        private AnotherBean beanOne;
    
        private YetAnotherBean beanTwo;
    
        private int i;
    
        public void setBeanOne(AnotherBean beanOne) {
            this.beanOne = beanOne;
        }
    
        public void setBeanTwo(YetAnotherBean beanTwo) {
            this.beanTwo = beanTwo;
        }
    
        public void setIntegerProperty(int i) {
            this.i = i;
        }
    }

在前面的示例中，声明setter与XML文件中指定的属性匹配。以下示例使用基于构造函数的DI：

    <bean id="exampleBean" class="examples.ExampleBean">
        <!-- constructor injection using the nested ref element -->
        <constructor-arg>
            <ref bean="anotherExampleBean"/>
        </constructor-arg>
    
        <!-- constructor injection using the neater ref attribute -->
        <constructor-arg ref="yetAnotherBean"/>
    
        <constructor-arg type="int" value="1"/>
    </bean>
    
    <bean id="anotherExampleBean" class="examples.AnotherBean"/>
    <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

以下示例显示了相应的ExampleBean类：

    public class ExampleBean {
    
        private AnotherBean beanOne;
    
        private YetAnotherBean beanTwo;
    
        private int i;
    
        public ExampleBean(
            AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
            this.beanOne = anotherBean;
            this.beanTwo = yetAnotherBean;
            this.i = i;
        }
    }

bean定义中指定的构造函数参数用作构造函数的参数ExampleBean。

现在考虑这个示例的变体，其中，不使用构造函数，而是告诉Spring调用static工厂方法来返回对象的实例：

    <bean id="exampleBean" class="examples.ExampleBean" factory-method="createInstance">
        <constructor-arg ref="anotherExampleBean"/>
        <constructor-arg ref="yetAnotherBean"/>
        <constructor-arg value="1"/>
    </bean>
    
    <bean id="anotherExampleBean" class="examples.AnotherBean"/>
    <bean id="yetAnotherBean" class="examples.YetAnotherBean"/>

以下示例显示了相应的ExampleBean类：

public class ExampleBean {

    // a private constructor
    private ExampleBean(...) {
        ...
    }
    
    // a static factory method; the arguments to this method can be
    // considered the dependencies of the bean that is returned,
    // regardless of how those arguments are actually used.
    public static ExampleBean createInstance (
        AnotherBean anotherBean, YetAnotherBean yetAnotherBean, int i) {
    
        ExampleBean eb = new ExampleBean (...);
        // some other operations...
        return eb;
    }

}
static工厂方法的参数由<constructor-arg/>元素提供，与实际使用的构造函数完全相同。工厂方法返回的类的类型不必与包含static工厂方法的类相同（尽管在本例中，它是）。实例（非静态）工厂方法可以以基本相同的方式使用（除了使用factory-bean属性而不是class属性），因此我们不在此讨论这些细节。

### 1.4.2 详细信息的依赖关系和配置

如上一节所述，您可以将bean属性和构造函数参数定义为对其他托管bean（协作者）的引用，也可以将其定义为内联定义的值。Spring的基于XML的配置元数据为此目的支持其元素<property/>和<constructor-arg/>元素中的子元素类型。

直值（基元，字符串等）
在value所述的属性<property/>元素指定属性或构造器参数的人类可读的字符串表示。Spring的 转换服务用于将这些值从a转换String为属性或参数的实际类型。以下示例显示了要设置的各种值：

    <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close">
        <!-- results in a setDriverClassName(String) call -->
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="masterkaoli"/>
    </bean>

以下示例使用p命名空间进行更简洁的XML配置：

    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="myDataSource" class="org.apache.commons.dbcp.BasicDataSource"
            destroy-method="close"
            p:driverClassName="com.mysql.jdbc.Driver"
            p:url="jdbc:mysql://localhost:3306/mydb"
            p:username="root"
            p:password="masterkaoli"/>
    
    </beans>

前面的XML更简洁。但是，除非您在创建bean定义时使用支持自动属性完成的IDE（例如IntelliJ IDEA或Spring Tool Suite），否则会在运行时而不是设计时发现拼写错误。强烈建议使用此类IDE帮助。

您还可以配置java.util.Properties实例，如下所示：

    <bean id="mappings"
        class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    
        <!-- typed as a java.util.Properties -->
        <property name="properties">
            <value>
                jdbc.driver.className=com.mysql.jdbc.Driver
                jdbc.url=jdbc:mysql://localhost:3306/mydb
            </value>
        </property>
    </bean>

Spring容器通过使用JavaBeans 机制将<value/>元素内的文本转换为 java.util.Properties实例PropertyEditor。这是一个很好的快捷方式，也是Spring团队支持<value/>在value属性样式上使用嵌套元素的少数几个地方之一。

该idref元素
该idref元素只是一种防错方法，可以将id容器中另一个bean 的（字符串值 - 而不是引用）传递给<constructor-arg/>or或<property/> element。以下示例显示了如何使用它：

    <bean id="theTargetBean" class="..."/>
    
    <bean id="theClientBean" class="...">
        <property name="targetName">
            <idref bean="theTargetBean"/>
        </property>
    </bean>

前面的bean定义代码段与以下代码段完全等效（在运行时）：

    <bean id="theTargetBean" class="..." />
    
    <bean id="client" class="...">
        <property name="targetName" value="theTargetBean"/>
    </bean>

第一种形式优于第二种形式，因为使用idref标记允许容器在部署时验证引用的命名bean实际存在。在第二个变体中，不对传递给bean 的targetName属性的值执行验证client。只有在client实际实例化bean 时才会发现错别字（很可能是致命的结果）。如果client bean是原型 bean，则只能在部署容器后很长时间才能发现此错误和产生的异常。

> 4.0 beans XSD不再支持local该idref元素 的属性，因为它不再提供常规bean引用的值。升级到4.0架构时，将现有idref local引用更改idref bean为。

其中一个共同的地方（至少在早期比Spring 2.0版本）<idref/>元素带来的值在配置AOP拦截在 ProxyFactoryBeanbean定义。<idref/>指定拦截器名称时使用元素可防止拼写错误的拦截器ID。

####参考其他bean类（合作者）
所述ref元件是内部的最终元件<constructor-arg/>或<property/> 定义元素。在这里，您将bean的指定属性的值设置为对容器管理的另一个bean（协作者）的引用。引用的bean是要设置其属性的bean的依赖项，并且在设置该属性之前根据需要对其进行初始化。（如果协作者是单例bean，它可能已经被容器初始化。）所有引用最终都是对另一个对象的引用。划定范围和有效性取决于是否通过指定其他对象的ID或名称bean，local,或parent属性。

通过标记的bean属性指定目标bean <ref/>是最常用的形式，并允许创建对同一容器或父容器中的任何bean的引用，而不管它是否在同一XML文件中。bean属性的值 可以id与目标bean 的属性相同，或者与目标bean的name属性中的值之一相同。以下示例显示如何使用ref元素：

    <ref bean="someBean"/>

通过该parent属性指定目标bean 会创建对当前容器的父容器中的bean的引用。parent 属性的值可以id与目标bean 的属性或目标bean的name属性中的值之一相同。目标bean必须位于当前bean的父容器中。您应该使用此bean引用变体，主要是当您有容器层次结构并且希望将现有bean包装在父容器中时，该容器具有与父bean同名的代理。以下一对列表显示了如何使用该parent属性：

    <!-- in the parent context -->
    <bean id="accountService" class="com.something.SimpleAccountService">
        <!-- insert dependencies as required as here -->
    </bean>
    <!-- in the child (descendant) context -->
    <bean id="accountService" <!-- bean name is the same as the parent bean -->
        class="org.springframework.aop.framework.ProxyFactoryBean">
        <property name="target">
            <ref parent="accountService"/> <!-- notice how we refer to the parent bean -->
        </property>
        <!-- insert other configuration and dependencies as required here -->
    </bean>

> 4.0 beans XSD不再支持local该ref元素 的属性，因为它不再提供常规bean引用的值。升级到4.0架构时，将现有ref local引用更改ref bean为。

####内bean
<bean/>内部的元件<property/>或<constructor-arg/>元件限定内部豆，如下面的示例所示：

    <bean id="outer" class="...">
        <!-- instead of using a reference to a target bean, simply define the target bean inline -->
        <property name="target">
            <bean class="com.example.Person"> <!-- this is the inner bean -->
                <property name="name" value="Fiona Apple"/>
                <property name="age" value="25"/>
            </bean>
        </property>
    </bean>

内部bean定义不需要定义的ID或名称。如果指定，则容器不使用此类值作为标识符。容器还会scope在创建时忽略标志，因为内部bean始终是匿名的，并且始终使用外部bean创建。不可能独立访问内部bean或将它们注入协作bean而不是封闭bean。

内部bean定义不需要定义的ID或名称。如果指定，则容器不使用此类值作为标识符。容器还会scope在创建时忽略标志，因为内部bean始终是匿名的，并且始终使用外部bean创建。不可能独立访问内部bean或将它们注入协作bean而不是封闭bean。

作为一个极端情况，可以从自定义范围接收销毁回调 - 例如，对于包含在单例bean中的请求范围内部bean。内部bean实例的创建与其包含bean相关联，但是销毁回调允许它参与请求范围的生命周期。这不是常见的情况。内部bean通常只是共享其包含bean的范围。

####集合

集合的<list/>，<set/>，<map/>，和<props/>元件设置Java的属性和参数Collection类型List，Set，Map，和Properties，分别。以下示例显示了如何使用它们：

    <bean id="moreComplexObject" class="example.ComplexObject">
        <!-- results in a setAdminEmails(java.util.Properties) call -->
        <property name="adminEmails">
            <props>
                <prop key="administrator">administrator@example.org</prop>
                <prop key="support">support@example.org</prop>
                <prop key="development">development@example.org</prop>
            </props>
        </property>
        <!-- results in a setSomeList(java.util.List) call -->
        <property name="someList">
            <list>
                <value>a list element followed by a reference</value>
                <ref bean="myDataSource" />
            </list>
        </property>
        <!-- results in a setSomeMap(java.util.Map) call -->
        <property name="someMap">
            <map>
                <entry key="an entry" value="just some string"/>
                <entry key ="a ref" value-ref="myDataSource"/>
            </map>
        </property>
        <!-- results in a setSomeSet(java.util.Set) call -->
        <property name="someSet">
            <set>
                <value>just some string</value>
                <ref bean="myDataSource" />
            </set>
        </property>
    </bean>

映射键或值的值或设置值也可以是以下任何元素：

    bean | ref | idref | list | set | map | props | value | null

#####集合合并
Spring容器还支持合并集合。应用程序开发人员可以定义父<list/>，<map/>，<set/>或<props/>元素，并有孩子<list/>，<map/>，<set/>或<props/>元素继承和父集合覆盖值。也就是说，子集合的值是合并父集合和子集合的元素的结果，子集合的元素覆盖父集合中指定的值。

关于合并的这一部分讨论了父子bean机制。不熟悉父母和子bean定义的读者可能希望在继续之前阅读 相关部分。

以下示例演示了集合合并：

    <beans>
        <bean id="parent" abstract="true" class="example.ComplexObject">
            <property name="adminEmails">
                <props>
                    <prop key="administrator">administrator@example.com</prop>
                    <prop key="support">support@example.com</prop>
                </props>
            </property>
        </bean>
        <bean id="child" parent="parent">
            <property name="adminEmails">
                <!-- the merge is specified on the child collection definition -->
                <props merge="true">
                    <prop key="sales">sales@example.com</prop>
                    <prop key="support">support@example.co.uk</prop>
                </props>
            </property>
        </bean>
    <beans>

注意使用的merge=true上属性<props/>的元素 adminEmails的财产childbean定义。当child容器解析并实例化bean时，生成的实例有一个adminEmails Properties集合，其中包含将子集合adminEmails与父adminEmails集合合并的结果 。以下清单显示了结果：

administrator=administrator@example.com 
sales=sales@example.com 
support=support@example.co.uk
孩子Properties集合的值设置继承父所有属性元素<props/>，和孩子的为值support值将覆盖父集合的价值。

这一合并行为同样适用于<list/>，<map/>和<set/> 集合类型。在<list/>元素的特定情况下，保持与List集合类型（即，ordered 值集合的概念）相关联的语义。父级的值位于所有子级列表的值之前。在的情况下Map，Set和Properties集合类型，没有顺序存在。因此，没有排序的语义在背后的关联的集合类型的效果Map，Set以及Properties该容器内部使用实现类型。

收集合并的局限性

您无法合并不同的集合类型（例如a Map和a List）。如果您尝试这样做，Exception则会引发相应的操作。merge必须在较低的继承子定义上指定该属性。merge在父集合定义上指定属性是多余的，并且不会导致所需的合并。

强烈的收藏品

通过在Java 5中引入泛型类型，您可以使用强类型集合。也就是说，可以声明一种Collection类型，使得它只能包含（例如）String元素。如果使用Spring将强类型依赖注入Collection到bean中，则可以利用Spring的类型转换支持，以便强类型Collection 实例的元素在添加到之前转换为适当的类型Collection。以下Java类和bean定义显示了如何执行此操作：

    public class SomeClass {
    
        private Map<String, Float> accounts;
    
        public void setAccounts(Map<String, Float> accounts) {
            this.accounts = accounts;
        }
    }
    <beans>
        <bean id="something" class="x.y.SomeClass">
            <property name="accounts">
                <map>
                    <entry key="one" value="9.99"/>
                    <entry key="two" value="2.75"/>
                    <entry key="six" value="3.99"/>
                </map>
            </property>
        </bean>
    </beans>

当为注入准备bean 的accounts属性时，通过反射可获得something关于强类型的元素类型的泛型信息Map<String, Float>。因此，Spring的类型转换基础结构将各种值元素识别为类型Float，并将字符串值（9.99, 2.75，和 3.99）转换为实际Float类型。

空字符串值和空字符串值
Spring将属性等的空参数视为空Strings。以下基于XML的配置元数据片段将email属性设置为空 String值（“”）。

    <bean class="ExampleBean">
        <property name="email" value=""/>
    </bean>

上面的示例等效于以下Java代码：

    exampleBean.setEmail("");

该<null/>元素处理null值。以下清单显示了一个示例：

    <bean class="ExampleBean">
        <property name="email">
            <null/>
        </property>
    </bean>

上述配置等同于以下Java代码：

    exampleBean.setEmail(null);

####带有p命名空间的XML快捷方式
p-namespace允许您使用bean元素的属性（而不是嵌套 <property/>元素）来描述属性值协作bean，或两者。

Spring支持具有命名空间的可扩展配置格式，这些命名空间基于XML Schema定义。beans本章中讨论的配置格式在XML Schema文档中定义。但是，p-namespace未在XSD文件中定义，仅存在于Spring的核心中。

以下示例显示了两个XML片段（第一个使用标准XML格式，第二个使用p命名空间）解析为相同的结果：

    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean name="classic" class="com.example.ExampleBean">
            <property name="email" value="someone@somewhere.com"/>
        </bean>
    
        <bean name="p-namespace" class="com.example.ExampleBean"
            p:email="someone@somewhere.com"/>
    </beans>

该示例显示email了bean定义中调用的p命名空间中的属性。这告诉Spring包含一个属性声明。如前所述，p命名空间没有架构定义，因此您可以将属性的名称设置为属性名称。

下一个示例包括另外两个bean定义，它们都引用了另一个bean：

    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:p="http://www.springframework.org/schema/p"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean name="john-classic" class="com.example.Person">
            <property name="name" value="John Doe"/>
            <property name="spouse" ref="jane"/>
        </bean>
    
        <bean name="john-modern"
            class="com.example.Person"
            p:name="John Doe"
            p:spouse-ref="jane"/>
    
        <bean name="jane" class="com.example.Person">
            <property name="name" value="Jane Doe"/>
        </bean>
    </beans>

此示例不仅包含使用p命名空间的属性值，还使用特殊格式来声明属性引用。第一个bean定义用于<property name="spouse" ref="jane"/>创建从bean john到bean 的引用 jane，而第二个bean定义p:spouse-ref="jane"用作属性来执行完全相同的操作。在这种情况下，spouse是属性名称，而该-ref部分表示这不是直接值，而是对另一个bean的引用。

> p命名空间不如标准XML格式灵活。例如，声明属性引用的格式与最终的属性冲突Ref，而标准XML格式则不然。我们建议您仔细选择您的方法并将其传达给您的团队成员，以避免生成同时使用所有三种方法的XML文档。

####带有c命名空间的XML快捷方式
与带有p-namespace的XML Shortcut类似，Spring 3.1中引入的c-namespace允许使用内联属性来配置构造函数参数，而不是嵌套constructor-arg元素。

以下示例使用c:命名空间执行与 基于构造函数的依赖注入相同的操作：

    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:c="http://www.springframework.org/schema/c"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd">
    
        <bean id="beanTwo" class="x.y.ThingTwo"/>
        <bean id="beanThree" class="x.y.ThingThree"/>
    
        <!-- traditional declaration with optional argument names -->
        <bean id="beanOne" class="x.y.ThingOne">
            <constructor-arg name="thingTwo" ref="beanTwo"/>
            <constructor-arg name="thingThree" ref="beanThree"/>
            <constructor-arg name="email" value="something@somewhere.com"/>
        </bean>
    
        <!-- c-namespace declaration with argument names -->
        <bean id="beanOne" class="x.y.ThingOne" c:thingTwo-ref="beanTwo"
            c:thingThree-ref="beanThree" c:email="something@somewhere.com"/>
    
    </beans>

该c:命名空间使用相同的约定作为p:一个（尾部-ref的bean引用），供他们的名字设置构造函数的参数。类似地，它需要在XML文件中声明，即使它没有在XSD模式中定义（它存在于Spring核心内部）。

对于构造函数参数名称不可用的罕见情况（通常在没有调试信息的情况下编译字节码），您可以使用回退到参数索引，如下所示：

    <!-- c-namespace index declaration -->
    <bean id="beanOne" class="x.y.ThingOne" c:_0-ref="beanTwo" c:_1-ref="beanThree"
        c:_2="something@somewhere.com"/>

> 由于XML语法，索引表示法要求存在前导_，因为XML属性名称不能以数字开头（即使某些IDE允许）。对于<constructor-arg>元素也可以使用相应的索引符号，但不常用，因为通常的声明顺序通常就足够了。

实际上，构造函数解析 机制在匹配参数方面非常有效，因此除非您确实需要，否则我们建议在整个配置中使用名称表示法。

复合属性名称
设置bean属性时，可以使用复合或嵌套属性名称，只要除最终属性名称之外的路径的所有组件都不是null。考虑以下bean定义：

    <bean id="something" class="things.ThingOne">
        <property name="fred.bob.sammy" value="123" />
    </bean>

该somethingbean具有一个fred属性，该属性具有属性，该bob属性具有sammy 属性，并且最终sammy属性的值设置为123。为了使其工作，在构造bean之后，fred属性something和bob属性fred不得为null。否则，NullPointerException抛出一个。

### 1.4.3运用depends-on

如果bean是另一个bean的依赖项，那通常意味着将一个bean设置为另一个bean的属性。通常，您可以使用基于XML的配置元数据中的<ref/> 元素来完成此操作。但是，有时bean之间的依赖关系不那么直接。例如，需要触发类中的静态初始化程序，例如数据库驱动程序注册。depends-on在初始化使用此元素的bean之前，该属性可以显式强制初始化一个或多个bean。以下示例使用该depends-on属性表示对单个bean的依赖关系：

    <bean id="beanOne" class="ExampleBean" depends-on="manager"/>
    <bean id="manager" class="ManagerBean" />

要表示对多个bean的依赖关系，请提供bean名称列表作为depends-on属性的值（逗号，空格和分号是有效的分隔符）：

    <bean id="beanOne" class="ExampleBean" depends-on="manager,accountDao">
        <property name="manager" ref="manager" />
    </bean>
    
    <bean id="manager" class="ManagerBean" />
    <bean id="accountDao" class="x.y.jdbc.JdbcAccountDao" />

> 该depends-on属性既可以指定初始化时间依赖性，也可以指定单独的 bean，相应的销毁时间依赖性。depends-on在给定的bean本身被销毁之前，首先销毁定义与给定bean 的关系的从属bean 。这样，depends-on也可以控制关​​机顺序。

###1.4.4 懒惰初始化的bean类
默认情况下，ApplicationContext实现会急切地创建和配置所有 单例 bean，作为初始化过程的一部分。通常，这种预先实例化是可取的，因为配置或周围环境中的错误是立即发现的，而不是几小时甚至几天后。当不希望出现这种情况时，可以通过将bean定义标记为延迟初始化来阻止单例bean的预实例化。延迟初始化的bean告诉IoC容器在第一次请求时创建bean实例，而不是在启动时。

在XML中，此行为由 元素lazy-init上的属性控制<bean/>，如以下示例所示：

```
<bean id="lazy" class="com.something.ExpensiveToCreateBean" lazy-init="true"/>
 <bean name="not.lazy" class="com.something.AnotherBean"/>
```

当前面的配置被a使用时ApplicationContext，lazybean在ApplicationContext启动时不会急切地预先实例化，而not.lazybean被急切地预先实例化。

但是，当延迟初始化的bean是未进行延迟初始化的单例bean的依赖项时，ApplicationContext会在启动时创建延迟初始化的bean，因为它必须满足单例的依赖关系。惰性初始化的bean被注入到其他地方的单独的bean中，而这个bean并不是惰性初始化的。

您还可以通过使用元素`default-lazy-init`上的属性来控制容器级别的延迟初始化， 

```
<beans/>以下示例显示：
 <beans default-lazy-init="true">
 <!-- no beans will be pre-instantiated... -->
 </beans>
```

### 1.4.5 自动化协作者

Spring容器可以自动连接协作bean之间的关系。您可以让Spring通过检查bean的内容自动为您的bean解析协作者（其他bean）ApplicationContext。自动装配具有以下优点：

- 自动装配可以显着减少指定属性或构造函数参数的需要。（在本章其他地方讨论的其他机制，如bean模板 ，在这方面也很有价值。）

- 自动装配可以随着对象的发展更新配置。例如，如果需要向类添加依赖项，则可以自动满足该依赖项，而无需修改配置。因此，自动装配在开发期间尤其有用，而不会在代码库变得更稳定时否定切换到显式布线的选项。

使用基于XML的配置元数据（请参阅依赖注入）时，可以使用元素的autowire属性为 bean定义指定autowire模式<bean/>。自动装配功能有四种模式。您指定每个bean的自动装配，因此可以选择要自动装配的那些。下表描述了四种自动装配模式：

自动装配模式

模式    说明

no

（默认）无自动装配。Bean引用必须由ref元素定义。不建议对较大的部署更改默认设置，因为明确指定协作者可以提供更好的控制和清晰度。在某种程度上，它记录了系统的结构。

byName

按属性名称自动装配。Spring查找与需要自动装配的属性同名的bean。例如，如果bean定义按名称设置为autowire并且它包含一个master属性（即，它有一个 setMaster(..)方法），则Spring会查找名为bean的定义master并使用它来设置属性。

byType

如果容器中只存在一个属性类型的bean，则允许属性自动装配。如果存在多个，则抛出致命异常，这表示您可能不会byType对该bean 使用自动装配。如果没有匹配的bean，则不会发生任何事情（该属性未设置）。

constructor

类似byType但适用于构造函数参数。如果容器中没有构造函数参数类型的一个bean，则会引发致命错误。

使用byType或constructor自动装配模式，您可以连接阵列和键入的集合。在这种情况下，提供容器内与预期类型匹配的所有autowire候选者以满足依赖性。Map如果预期的键类型是，则可以自动装配强类型实例String。自动装配Map 实例的值由与预期类型匹配的所有bean实例组成， Map实例的键包含相应的bean名称。

#### 自动装配的局限和缺点

当在整个项目中一致地使用自动装配时，自动装配效果最佳。如果一般不使用自动装配，那么开发人员使用它来连接一个或两个bean定义可能会让人感到困惑。

##### 考虑自动装配的局限和缺点：

- 显式依赖项property和constructor-arg设置始终覆盖自动装配。您不能自动装配简单属性，例如基元 Strings，和Classes（以及此类简单属性的数组）。这种限制是按设计的。

- 自动装配不如显式布线精确。虽然如前面的表中所述，但Spring会谨慎地避免在可能产生意外结果的模糊性的情况下进行猜测。您不再明确记录Spring管理对象之间的关系。

- 可能无法为可能从Spring容器生成文档的工具提供接线信息。

容器中的多个bean定义可以匹配setter方法或构造函数参数指定的类型以进行自动装配。对于数组，集合或 Map实例，这不一定是个问题。但是，对于期望单个值的依赖关系，这种模糊性不是任意解决的。如果没有可用的唯一bean定义，则抛出异常。

在后一种情况下，您有几种选择：

- 放弃自动装配，支持显式布线。

- 通过将其autowire-candidate属性设置为bean，可以避免对bean定义进行自动装配false，如下一节所述。

- 通过将primary其<bean/>元素的属性设置为，将单个bean定义指定为主要候选者 true。

- 实现基于注释的配置可用的更细粒度的控件，如[基于注释的容器配置中所述](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-annotation-config)。

从自动装配中排除Bean
在每个bean的基础上，您可以从自动装配中排除bean。在Spring的XML格式中，将元素的autowire-candidate属性设置<bean/>为false。容器使特定的bean定义对自动装配基础结构不可用（包括注释样式配置等@Autowired）。

> 该autowire-candidate属性旨在仅影响基于类型的自动装配。它不会影响名称的显式引用，即使指定的bean未标记为autowire候选，也会解析它。因此，如果名称匹配，则按名称自动装配会注入bean。

您还可以根据与bean名称的模式匹配来限制autowire候选者。顶级<beans/>元素在其default-autowire-candidates属性中接受一个或多个模式 。例如，要将autowire候选状态限制为名称以其结尾的任何bean Repository，请提供值*Repository。要提供多个模式，请在逗号分隔的列表中定义它们。bean定义的属性的显式值 true或优先级始终优先。对于此类bean，模式匹配规则不适用。falseautowire-candidate

这些技术对于您永远不希望通过自动装配注入其他bean的bean非常有用。这并不意味着排除的bean本身不能使用自动装配进行配置。相反，bean本身不是自动装配其他bean的候选者。

### 1.4.6 方法注入

在大多数应用程序场景中，容器中的大多数bean都是 单例。当单例bean需要与另一个单例bean协作或非单例bean需要与另一个非单例bean协作时，通常通过将一个bean定义为另一个bean的属性来处理依赖关系。当bean生命周期不同时会出现问题。假设单例bean A需要使用非单例（原型）bean B，可能是在A上的每个方法调用上。容器只创建一次单例bean A，因此只有一次机会来设置属性。每次需要时，容器都不能为bean A提供bean B的新实例。

解决方案是放弃一些控制反转。你可以做一个豆意识到容器通过实现ApplicationContextAware接口，并通过制作getBean("B")到容器调用请求（典型新）bean B实例的实例每次豆A需要它。以下示例显示了此方法：

    // a class that uses a stateful Command-style class to perform some processing
    package fiona.apple;
    
    // Spring-API imports
    import org.springframework.beans.BeansException;
    import org.springframework.context.ApplicationContext;
    import org.springframework.context.ApplicationContextAware;
    
    public class CommandManager implements ApplicationContextAware {
    
        private ApplicationContext applicationContext;
    
        public Object process(Map commandState) {
            // grab a new instance of the appropriate Command
            Command command = createCommand();
            // set the state on the (hopefully brand new) Command instance
            command.setState(commandState);
            return command.execute();
        }
    
        protected Command createCommand() {
            // notice the Spring API dependency!
            return this.applicationContext.getBean("command", Command.class);
        }
    
        public void setApplicationContext(
                ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }

前面的内容是不可取的，因为业务代码知道并耦合到Spring Framework。方法注入是Spring IoC容器的一个高级功能，可以让您干净地处理这个用例。

您可以在此博客条目中阅读有关Method Injection的动机的更多信息 。

前面的内容是不可取的，因为业务代码知道并耦合到Spring Framework。方法注入是Spring IoC容器的一个高级功能，可以让您干净地处理这个用例。

您可以在此[博客条目](https://spring.io/blog/2004/08/06/method-injection/)中阅读有关Method Injection的动机的更多信息 。

##### 查找方法注入

查找方法注入是容器覆盖容器管理的bean上的方法并返回容器中另一个命名bean的查找结果的能力。查找通常涉及原型bean，如上一节中描述的场景。Spring Framework通过使用CGLIB库中的字节码生成来动态生成覆盖该方法的子类来实现此方法注入。

> - 要使这个动态子类工作，Spring bean容器子类不能成为的类final，以及要重写的方法也不能final。
> 
> - 对具有abstract方法的类进行单元测试需要您自己对类进行子类化并提供该abstract方法的存根实现。
> 
> - 组件扫描也需要具体的方法，这需要具体的类来获取。
> 
> - 另一个关键限制是查找方法不适用于工厂方法，特别是@Bean配置类中的方法，因为在这种情况下，容器不负责创建实例，因此无法创建运行时生成的子类苍蝇

对于CommandManager前面代码片段中的类，Spring容器动态地覆盖createCommand() 方法的实现。该CommandManager班没有任何Spring的依赖，因为返工例所示：

    package fiona.apple;
    
    // no more Spring imports!
    
    public abstract class CommandManager {
    
        public Object process(Object commandState) {
            // grab a new instance of the appropriate Command interface
            Command command = createCommand();
            // set the state on the (hopefully brand new) Command instance
            command.setState(commandState);
            return command.execute();
        }
    
        // okay... but where is the implementation of this method?
        protected abstract Command createCommand();
    }

在包含要注入的方法的客户端类中（CommandManager在本例中），要注入的方法需要以下形式的签名：

    <public|protected> [abstract] <return-type> theMethodName(no-arguments);

如果方法是abstract，则动态生成的子类实现该方法。否则，动态生成的子类将覆盖原始类中定义的具体方法。请考虑以下示例：

    <!-- a stateful bean deployed as a prototype (non-singleton) -->
    <bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
        <!-- inject dependencies here as required -->
    </bean>
    
    <!-- commandProcessor uses statefulCommandHelper -->
    <bean id="commandManager" class="fiona.apple.CommandManager">
        <lookup-method name="createCommand" bean="myCommand"/>
    </bean>

只要需要bean 的新实例，标识为bean的bean 就会commandManager调用自己的createCommand()方法myCommand。myCommand如果实际需要，您必须小心将bean 部署为原型。如果它是单例，myCommand 则每次返回相同的bean 实例。
或者，在基于注释的组件模型中，您可以通过@Lookup注释声明查找方法，如以下示例所示：
    public abstract class CommandManager {

        public Object process(Object commandState) {
            Command command = createCommand();
            command.setState(commandState);
            return command.execute();
        }
    
        @Lookup("myCommand")
        protected abstract Command createCommand();
    }

或者，更具惯用性，您可以依赖于针对查找方法的声明返回类型解析目标bean：

    public abstract class CommandManager {
    
        public Object process(Object commandState) {
            MyCommand command = createCommand();
            command.setState(commandState);
            return command.execute();
        }
    
        @Lookup
        protected abstract MyCommand createCommand();
    }

请注意，您通常应该使用具体的存根实现来声明这种带注释的查找方法，以使它们与Spring的组件扫描规则兼容，其中默认情况下抽象类被忽略。此限制不适用于显式注册或显式导入的bean类。

> 访问不同范围的目标bean的另一种方法是ObjectFactory/ Provider注入点。请参阅Scoped Beans作为依赖关系。
> 
> 您可能还会发现ServiceLocatorFactoryBean（在 org.springframework.beans.factory.config包中）有用。

#### 任意方法替换

与查找方法注入相比，一种不太有用的方法注入形式是能够使用另一个方法实现替换托管bean中的任意方法。您可以安全地跳过本节的其余部分，直到您确实需要此功能。

使用基于XML的配置元数据，您可以使用该replaced-method元素将已存在的方法实现替换为已部署的bean。考虑以下类，它有一个computeValue我们想要覆盖的方法：

    public class MyValueCalculator {
    
        public String computeValue(String input) {
            // some real code...
        }
    
        // some other methods...
    }

实现`org.springframework.beans.factory.support.MethodReplacer`接口的类提供新的方法定义，如以下示例所示：

    /**
     * meant to be used to override the existing computeValue(String)
     * implementation in MyValueCalculator
     */
    public class ReplacementComputeValue implements MethodReplacer {
    
        public Object reimplement(Object o, Method m, Object[] args) throws Throwable {
            // get the input value, work with it, and return a computed result
            String input = (String) args[0];
            ...
            return ...;
        }
    }

部署原始类并指定方法覆盖的bean定义类似于以下示例：

    <bean id="myValueCalculator" class="x.y.z.MyValueCalculator">
        <!-- arbitrary method replacement -->
        <replaced-method name="computeValue" replacer="replacementComputeValue">
            <arg-type>String</arg-type>
        </replaced-method>
    </bean>
    
    <bean id="replacementComputeValue" class="a.b.c.ReplacementComputeValue"/>

您可以使用<arg-type/>元素中的一个或多个元素<replaced-method/> 来指示被覆盖的方法的方法签名。仅当方法重载且类中存在多个变体时，才需要参数的签名。为方便起见，参数的类型字符串可以是完全限定类型名称的子字符串。例如，以下所有匹配 java.lang.String：

    java.lang.String
    String
    Str

因为参数的数量通常足以区分每个可能的选择，所以通过让您只键入与参数类型匹配的最短字符串，此快捷方式可以节省大量的输入。

## 1.5 Bean范围

创建bean定义时，可以创建用于创建由该bean定义定义的类的实际实例的配方。bean定义是一个配方的想法很重要，因为它意味着，与一个类一样，您可以从一个配方创建许多对象实例。

您不仅可以控制要插入到从特定bean定义创建的对象中的各种依赖项和配置值，还可以控制从特定bean定义创建的对象的范围。这种方法功能强大且灵活，因为您可以选择通过配置创建的对象的范围，而不必在Java类级别烘焙对象的范围。可以将Bean定义为部署在多个范围之一中。Spring Framework支持六个范围，其中四个范围仅在您使用Web感知时才可用ApplicationContext。您还可以创建 自定义范围。

Bean范围描述

- 单例 `singleton`
  
  （默认）将单个bean定
  
  义范围限定为每个Spring IoC容器的单个对象实例。

- 原型`prototype`    将单个bean定义范围限定为任意数量的对象实例。

- 请求`request`    将单个bean定义范围限定为单个HTTP请求的生命周期。也就是说，每个HTTP请求都有自己的bean实例，它是在单个bean定义的后面创建的。仅在具有Web感知功能的Spring环境中有效ApplicationContext。

- 会话 `session`将单个bean定义范围限定为HTTP的生命周期Session。仅在具有Web感知功能的Spring环境中有效ApplicationContext。

- 应用`application`将单个bean定义范围限定为a的生命周期ServletContext。仅在具有Web感知功能的Spring环境中有效ApplicationContext。

- WebSocket    将单个bean定义范围限定为a的生命周期WebSocket。仅在具有Web感知功能的Spring环境中有效ApplicationContext。

> 从Spring 3.0开始，线程范围可用，但默认情况下未注册：请参阅SimpleThreadScope。从Spring 4.2开始，还有一个事务范围： SimpleTransactionScope。有关如何注册这些或任何其他自定义作用域的说明，请参阅 使用自定义作用域。

### 1.5.1单例 `singleton`

只管理单个bean的一个共享实例，并且对具有与该bean定义匹配的ID或ID的bean的所有请求都会导致Spring容器返回一个特定的bean实例。

换句话说，当您定义bean定义并将其作为单一作用域时，Spring IoC容器只创建该bean定义定义的对象的一个​​实例。此单个实例存储在此类单例bean的缓存中，并且该命名Bean的所有后续请求和引用都将返回缓存对象。下图显示了单例范围的工作原理：
![](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/images/singleton.png)

Spring的单例bean概念不同于Gang of Four（GoF）模式书中定义的单例模式。GoF单例对一个对象的范围进行硬编码，使得每个ClassLoader创建一个且只有一个特定类的实例。Spring单例的范围最好描述为每容器和每个bean。这意味着，如果在单个Spring容器中为特定类定义一个bean，则Spring容器将创建该bean定义所定义的类的一个且仅一个实例。单例范围是Spring中的默认范围。要将bean定义为XML中的单例，您可以定义一个bean，如以下示例所示：

    <bean id="accountService" class="com.something.DefaultAccountService"/>
    <!-- the following is equivalent, though redundant (singleton scope is the default) -->
    <bean id="accountService" class="com.something.DefaultAccountService" scope="singleton"/>

### 1.5.2 原型`prototype`

bean部署的非单例原型范围导致每次发出对该特定bean的请求时都创建新的bean实例。也就是说，bean被注入另一个bean，或者通过getBean()对容器的方法调用来请求它。通常，您应该对所有有状态bean使用原型范围，对无状态bean使用单例范围。

下图说明了Spring原型范围：
![](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/images/prototype.png)

（数据访问对象（DAO）通常不配置为原型，因为典型的DAO不会保持任何会话状态。我们更容易重用单例图的核心。）

以下示例将bean定义为XML中的原型

```
<bean id="accountService" class="com.something.DefaultAccountService" scope="prototype"/>
```

> 与其他范围相比，Spring不管理原型bean的完整生命周期。容器实例化，配置和组装原型对象并将其交给客户端，而没有该原型实例的进一步记录。因此，尽管无论范围如何都在所有对象上调用初始化生命周期回调方法，但在原型的情况下，不会调用已配置的销毁生命周期回调。客户端代码必须清理原型范围的对象并释放原型bean所拥有的昂贵资源。要使Spring容器释放原型范围的bean所拥有的资源，请尝试使用自定义[bean后处理器](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-extension-bpp)，它包含对需要清理的bean的引用。

在某些方面，Spring容器关于原型范围bean的角色是Java new运算符的替代品。超过该点的所有生命周期管理必须由客户端处理。（有关Spring容器中bean的生命周期的详细信息，请参阅Lifecycle Callbacks。）

### 1.5.3 具有原型bean依赖关系的单例Bean

当您使用具有依赖于原型bean的单例作用域bean时，请注意在实例化时解析依赖项。因此，如果依赖项将原型范围的bean注入到单例范围的bean中，则会实例化一个新的原型bean，然后将依赖注入到单例bean中。原型实例是唯一提供给单例范围bean的实例。

但是，假设您希望单例范围的bean在运行时重复获取原型范围的bean的新实例。您不能将原型范围的bean依赖注入到您的单例bean中，因为当Spring容器实例化单例bean并解析并注入其依赖项时，该注入只发生一次。如果您需要在运行时多次使用原型bean的新实例，请参阅[方法注入](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-method-injection)

### 1.5.4 请求，会话，应用程序和WebSocket范围

在request，session，application，和websocket范围只有当你使用一个基于web的Spring可ApplicationContext实现（例如 XmlWebApplicationContext）。如果将这些范围与常规的Spring IoC容器一起使用，例如ClassPathXmlApplicationContext，IllegalStateException则会引发抱怨未知Bean范围的问题。

##### 初始Web配置

为了支持豆的范围界定在request，session，application，和 websocket（即具有web作用域bean），需要做少量的初始配置定义你的豆之前。（标准范围不需要此初始设置：singleton和prototype。）

如何完成此初始设置取决于您的特定Servlet环境。

如果您在Spring Web MVC中访问scoped bean，实际上是在Spring处理的请求中，则DispatcherServlet无需进行特殊设置。 DispatcherServlet已暴露所有相关国家。

如果您使用Servlet 2.5 Web容器，并且在Spring之外处理请求 DispatcherServlet（例如，使用JSF或Struts时），则需要注册 `org.springframework.web.context.request.RequestContextListener ServletRequestListener`。对于Servlet 3.0+，可以使用该`WebApplicationInitializer` 接口以编程方式完成。或者，或者对于旧容器，将以下声明添加到Web应用程序的web.xml文件中：

```
<web-app>
 ...
 <listener>
 <listener-class>
 org.springframework.web.context.request.RequestContextListener
 </listener-class>
 </listener>
 ...
 </web-app>
```

或者，如果您的侦听器设置存在问题，请考虑使用Spring RequestContextFilter。过滤器映射取决于周围的Web应用程序配置，因此您必须根据需要进行更改。以下清单显示了Web应用程序的过滤器部分：

    <web-app>
        ...
        <filter>
            <filter-name>requestContextFilter</filter-name>
            <filter-class>org.springframework.web.filter.RequestContextFilter</filter-class>
        </filter>
        <filter-mapping>
            <filter-name>requestContextFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        ...
    </web-app>

DispatcherServlet，RequestContextListener和RequestContextFilter所有做同样的事情，即将HTTP请求对象绑定到Thread为该请求提供服务的对象。这使得请求和会话范围的bean可以在调用链的下游进一步使用。

`DispatcherServlet，RequestContextListener`和`RequestContextFilter`所有做同样的事情，即将HTTP请求对象绑定到Thread为该请求提供服务的对象。这使得请求和会话范围的bean可以在调用链的下游进一步使用。

##### 请求范围

考虑bean定义的以下XML配置：

    <bean id="loginAction" class="com.something.LoginAction" scope="request"/>

Spring容器LoginAction通过loginAction对每个HTTP请求使用bean定义来创建bean 的新实例。也就是说， loginActionbean的范围是HTTP请求级别。您可以根据需要更改创建的实例的内部状态，因为从同一loginActionbean定义创建的其他实例在状态中看不到这些更改。它们特别针对个人要求。当请求完成处理时，将放弃作用于请求的bean。

使用注释驱动的组件或Java配置时，@RequestScope注释可用于将组件分配给request范围。以下示例显示了如何执行此操作：

    @RequestScope
    @Component
    public class LoginAction {
        // ...
    }

##### 会话范围

考虑bean定义的以下XML配置：

    <bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>

Spring容器UserPreferences通过在userPreferences单个HTTP的生存期内使用bean定义来创建bean 的新实例Session。换句话说，userPreferencesbean在HTTP Session级别上有效地作用域。与请求范围的bean一样，您可以根据需要更改创建的实例的内部状态，因为知道Session同样使用从同一userPreferencesbean定义创建的实例的其他HTTP 实例在状态中看不到这些更改，因为它们特定于单个HTTP Session。当Session最终丢弃HTTP时Session，也将丢弃作用于该特定HTTP的bean 。

使用注释驱动的组件或Java配置时，可以使用 @SessionScope注释将组件分配给session范围。

    @SessionScope
    @Component
    public class UserPreferences {
        // ...
    }

适用范围
考虑bean定义的以下XML配置：

    <bean id="appPreferences" class="com.something.AppPreferences" scope="application"/>

Spring容器AppPreferences通过appPreferences对整个Web应用程序使用一次bean定义来创建bean 的新实例。也就是说， appPreferencesbean在该ServletContext级别作用域并存储为常规 ServletContext属性。这有点类似于Spring单例bean，但在两个重要方面有所不同：它是一个单独的ServletContext，不是每个Spring的'ApplicationContext'（在任何给定的Web应用程序中可能有几个），它实际上是暴露的，因此是可见的作为一个ServletContext属性。

使用注释驱动的组件或Java配置时，可以使用 @ApplicationScope注释将组件分配给application范围。以下示例显示了如何执行此操作：

    @ApplicationScope
    @Component
    public class AppPreferences {
        // ...
    }

作为依赖关系的Scoped Bean

Spring IoC容器不仅管理对象（bean）的实例化，还管理协作者（或依赖关系）的连接。如果要将（例如）HTTP请求范围的bean注入到寿命较长范围的另一个bean中，您可以选择注入AOP代理来代替范围内的bean。也就是说，您需要注入一个代理对象，该对象公开与范围对象相同的公共接口，但也可以从相关范围（例如HTTP请求）中检索真实目标对象，并将方法调用委托给真实对象。

> 您还可以<aop:scoped-proxy/>在作用域的bean之间使用singleton，然后通过引用然后通过可序列化的中间代理，从而能够在反序列化时重新获取目标单例bean。
> 
> 当声明<aop:scoped-proxy/>范围的bean时prototype，共享代理上的每个方法调用都会导致创建一个新的目标实例，然后转发该调用。
> 
> 此外，范围代理不是以生命周期安全的方式从较短范围访问bean的唯一方法。您还可以将注入点（即构造函数或setter参数或autowired字段）声明为ObjectFactory<MyTargetBean>允许getObject()调用，以便在每次需要时按需检索当前实例 - 无需保留实例或单独存储它。
> 
> 作为扩展变体，您可以声明ObjectProvider<MyTargetBean>，它提供了几个额外的访问变体，包括getIfAvailable和getIfUnique。
> 
> 调用它的JSR-330变体，Provider并与每次检索尝试的Provider<MyTargetBean声明和相应get()调用一起使用。有关JSR-330整体的更多详细信息，请参见此处。

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/aop
            https://www.springframework.org/schema/aop/spring-aop.xsd">
    
        <!-- an HTTP Session-scoped bean exposed as a proxy -->
        <bean id="userPreferences" class="com.something.UserPreferences" scope="session">
            <!-- instructs the container to proxy the surrounding bean 定义代理的行。 -->
            <aop:scoped-proxy/> 
        </bean>
    
        <!-- a singleton-scoped bean injected with a proxy to the above bean -->
        <bean id="userService" class="com.something.SimpleUserService">
            <!-- a reference to the proxied userPreferences bean -->
            <property name="userPreferences" ref="userPreferences"/>
        </bean>
    </beans>

要创建此类代理，请将子<aop:scoped-proxy/>元素插入到作用域bean定义中（请参阅选择要创建的代理类型和 基于XML架构的配置）。豆类的定义为何作用域的request，session和自定义范围水平要求<aop:scoped-proxy/>元素？考虑以下单例bean定义，并将其与您需要为上述范围定义的内容进行对比（请注意，以下 userPreferencesbean定义不完整）：

    <bean id="userPreferences" class="com.something.UserPreferences" scope="session"/>
    
    <bean id="userManager" class="com.something.UserManager">
        <property name="userPreferences" ref="userPreferences"/>
    </bean>

在前面的示例中，singleton bean（userManager）注入了对HTTP Session-scoped bean（userPreferences）的引用。这里的重点是 userManagerbean是一个单例：它每个容器只实例化一次，它的依赖关系（在这种情况下只有一个，userPreferencesbean）也只注入一次。这意味着userManagerbean只在完全相同的userPreferences对象（即最初注入它的对象）上运行。

当将一个寿命较短的scoped bean注入一个寿命较长的scoped bean时，这不是你想要的行为（例如，将一个HTTP Session-scoped协作bean作为依赖注入到singleton bean中）。相反，您需要一个userManager 对象，并且，在HTTP的生命周期中Session，您需要一个userPreferences特定于HTTP 的对象Session。因此，容器创建一个对象，该对象公开与UserPreferences该类完全相同的公共接口（理想情况下是一个UserPreferences实例的对象），该UserPreferences对象可以从作用域机制（HTTP请求Session等）中获取真实 对象。容器将此代理对象注入到userManagerbean中，该bean不知道此UserPreferences引用是代理。在这个例子中，当一个 UserManager实例在依赖注入的UserPreferences 对象上调用一个方法，它实际上是在代理上调用一个方法。然后，代理 UserPreferences从（在这种情况下）HTTP中Session获取真实UserPreferences对象，并将方法调用委托给检索到的真实对象。

因此，在将bean request-和session-scopedbean注入协作对象时，您需要以下（正确和完整）配置 ，如以下示例所示：

```<bean
<bean id="userPreferences" class="com.something.UserPreferences" scope="session">
 <aop:scoped-proxy/>
 </bean>
```

    <bean id="userManager" class="com.something.UserManager">
        <property name="userPreferences" ref="userPreferences"/>
    </bean>

选择要创建的代理类型
默认情况下，当Spring容器为使用该<aop:scoped-proxy/>元素标记的bean创建代理时，将创建基于CGLIB的类代理。

> CGLIB代理只拦截公共方法调用！不要在这样的代理上调用非公共方法。它们不会委托给实际的作用域目标对象。

或者，您可以通过指定元素属性false的值，将Spring容器配置为为此类作用域bean创建基于JDK接口的标准代理。使用基于JDK接口的代理意味着您不需要在应用程序类路径中使用其他库来影响此类代理。但是，这也意味着作用域bean的类必须至少实现一个接口，并且注入了作用域bean的所有协作者必须通过其中一个接口引用bean。以下示例显示了基于接口的代理：proxy-target-class<aop:scoped-proxy/>

    <!-- DefaultUserPreferences implements the UserPreferences interface -->
    <bean id="userPreferences" class="com.stuff.DefaultUserPreferences" scope="session">
        <aop:scoped-proxy proxy-target-class="false"/>
    </bean>
    
    <bean id="userManager" class="com.stuff.UserManager">
        <property name="userPreferences" ref="userPreferences"/>
    </bean>

有关选择基于类或基于接口的代理的更多详细信息，请参阅[代理机制](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#aop-proxying)。

### 1.5.5 自定义范围

bean范围机制是可扩展的。您可以定义自己的范围，甚至可以重新定义现有范围，尽管后者被认为是不好的做法，您无法覆盖内置singleton和prototype范围。

#####创建自定义范围
要将自定义作用域集成到Spring容器中，需要实现`org.springframework.beans.factory.config.Scope`本节中描述的 接口。有关如何实现自己的作用域的想法，请参阅Scope Spring Framework本身和Scopejavadoc 提供的实现 ，它们解释了您需要更详细地实现的方法。

该Scope接口有四种方法可以从作用域中获取对象，将其从作用域中删除，然后将其销毁。

例如，会话范围实现返回会话范围的bean（如果它不存在，则该方法在将其绑定到会话以供将来参考之后返回该bean的新实例）。以下方法从基础范围返回对象：
    Object get(String name, ObjectFactory objectFactory)

例如，会话范围实现从基础会话中删除会话范围的bean。应返回该对象，但如果找不到具有指定名称的对象，则可以返回null。以下方法从基础范围中删除对象：

    Object remove(String name)

以下方法记录范围在销毁时或范围中指定对象被销毁时应执行的回调：

    void registerDestructionCallback(String name, Runnable destructionCallback)

有关 销毁回调的更多信息，请参阅javadoc或Spring作用域实现。

以下方法获取基础范围的对话标识符：

    String getConversationId()

每个范围的标识符都不同。对于会话范围的实现，该标识符可以是会话标识符。

##### 使用自定义范围

在编写并测试一个或多个自定义Scope实现之后，您需要让Spring容器知道您的新范围。以下方法是Scope使用Spring容器注册new的核心方法：

    void registerScope(String scopeName, Scope scope);

此方法在ConfigurableBeanFactory接口上声明，该接口可通过 Spring随附的BeanFactory大多数具体ApplicationContext实现的属性获得。

- 该registerScope(..)方法的第一个参数是与范围关联的唯一名称。Spring容器本身中的这些名称的示例是singleton和 prototype。
- 该registerScope(..)方法的第二个参数是Scope您希望注册和使用的自定义实现的实际实例。

假设您编写自定义Scope实现，然后注册它，如下一个示例所示。

> 下一个示例使用SimpleThreadScope，它包含在Spring中，但默认情况下未注册。您自己的自定义Scope 实现的说明是相同的。

    Scope threadScope = new SimpleThreadScope();
    beanFactory.registerScope("thread", threadScope);

然后，您可以创建符合自定义的作用域规则的bean定义， Scope如下所示：

    <bean id="..." class="..." scope="thread">

使用自定义Scope实现，您不仅限于范围的编程注册。您还可以Scope使用CustomScopeConfigurer该类以声明方式进行注册 ，如以下示例所示：

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:aop="http://www.springframework.org/schema/aop"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/aop
            https://www.springframework.org/schema/aop/spring-aop.xsd">
    
        <bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
            <property name="scopes">
                <map>
                    <entry key="thread">
                        <bean class="org.springframework.context.support.SimpleThreadScope"/>
                    </entry>
                </map>
            </property>
        </bean>
    
        <bean id="thing2" class="x.y.Thing2" scope="thread">
            <property name="name" value="Rick"/>
            <aop:scoped-proxy/>
        </bean>
    
        <bean id="thing1" class="x.y.Thing1">
            <property name="thing2" ref="thing2"/>
        </bean>
    
    </beans>

> 放置<aop:scoped-proxy/>在FactoryBean实现中时，工厂bean本身是作用域的，而不是从中返回的对象getObject()。

## 1.6 定制Bean的本质

Spring Framework提供了许多可用于自定义bean特性的接口。本节将它们分组如下：

- 生命周期回调

- `ApplicationContextAware` 和 `BeanNameAware`

- 其他Aware接口

### 1.6.1 生命周期回调(Lifecycle Callbacks)

要与容器的bean生命周期管理进行交互，可以实现Spring InitializingBean和DisposableBean接口。容器调用 afterPropertiesSet()前者，destroy()后者让bean在初始化和销毁​​bean时执行某些操作。

> JSR-250 @PostConstruct和@PreDestroy注释通常被认为是在现代Spring应用程序中接收生命周期回调的最佳实践。使用这些注释意味着您的bean不会耦合到特定于Spring的接口。有关详细信息，请参阅使用@PostConstruct和@PreDestroy。
> 
> 如果您不想使用JSR-250注释但仍想删除耦合，请考虑init-method和destroy-methodbean定义元数据。

在内部，Spring Framework使用BeanPostProcessor实现来处理它可以找到的任何回调接口并调用适当的方法。如果您需要自定义功能或其他生命周期行为Spring默认不提供，您可以BeanPostProcessor自己实现。有关更多信息，请参阅 容器扩展点。

除了初始化和销毁​​回调之外，Spring管理的对象还可以实现Lifecycle接口，以便这些对象可以参与启动和关闭过程，这是由容器自身的生命周期驱动的。

本节描述了生命周期回调接口。

##### 初始化回调

该`org.springframework.beans.factory.InitializingBean`接口允许在容器上设置bean的所有必要属性后，一个bean进行初始化工作。的InitializingBean接口规定了一个方法：

    void afterPropertiesSet() throws Exception;

我们建议您不要使用该InitializingBean接口，因为它会不必要地将代码耦合到Spring。或者，我们建议使用@PostConstruct注释或指定POJO初始化方法。对于基于XML的配置元数据，您可以使用该init-method属性指定具有void无参数签名的方法的名称。使用Java配置，您可以使用。的initMethod属性 @Bean。请参阅[接收生命周期回调](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-lifecycle-callbacks)。请考虑以下示例：

    <bean id="exampleInitBean" class="examples.ExampleBean" init-method="init"/>

​    
​    public class ExampleBean {
​    
​        public void init() {
​            // do some initialization work
​        }
​    }

前面的示例与以下示例几乎完全相同（包含两个列表）：

    <bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
    
    public class AnotherExampleBean implements InitializingBean {
    
        public void afterPropertiesSet() {
            // do some initialization work
        }
    }

但是，前面两个示例中的第一个没有将代码耦合到Spring。

##### 毁灭回调

实现`org.springframework.beans.factory.DisposableBean`接口允许bean在包含它的容器被销毁时获得回调。的 `DisposableBean`接口规定了一个方法：

    void destroy() throws Exception;

我们建议您不要使用`DisposableBean`回调接口，因为它会不必要地将代码耦合到Spring。或者，我们建议使用`@PreDestroy`注释或指定bean定义支持的泛型方法。使用基于XML的配置元数据，您可以使用该d`estroy-method`属性`<bean/>`。使用Java配置，您可以使用。的`destroyMethod`属性`@Bean`。请参阅 [接收生命周期回调](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-lifecycle-callbacks)。考虑以下定义：

    <bean id="exampleInitBean" class="examples.ExampleBean" destroy-method="cleanup"/>
    
    public class ExampleBean {
    
        public void cleanup() {
            // do some destruction work (like releasing pooled connections)
        }
    }

前面的定义与以下定义几乎完全相同：

    <bean id="exampleInitBean" class="examples.AnotherExampleBean"/>
    
    public class AnotherExampleBean implements DisposableBean {
    
        public void destroy() {
            // do some destruction work (like releasing pooled connections)
        }
    }

但是，前面两个定义中的第一个没有将代码耦合到Spring。

> 您可以destroy-method为<bean>元素的属性指定一个特殊 (inferred)值，该值指示Spring自动检测特定bean类的公共close或 shutdown方法。（任何实现 java.lang.AutoCloseable或java.io.Closeable因此匹配的类。）您还可以(inferred)在元素的default-destroy-method属性 上设置此特殊值，<beans>以将此行为应用于整组bean（请参阅 [默认初始化和销毁​​方法](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-default-init-destroy-methods)）。请注意，这是Java配置的默认行为。

##### 默认初始化和销毁​​方法

当你写的初始化和销毁不使用Spring的具体方法回调InitializingBean和DisposableBean回调接口，你通常写有名字，如方法init()，initialize()，dispose()，等等。理想情况下，此类生命周期回调方法的名称在项目中是标准化的，以便所有开发人员使用相同的方法名称并确保一致性。

您可以将Spring容器配置为“查找”命名初始化并销毁每个bean上的回调方法名称。这意味着，作为应用程序开发人员，您可以编写应用程序类并使用调用的初始化回调 init()，而无需为init-method="init"每个bean定义配置属性。Spring IoC容器在创建bean时调用该方法（并且符合前面描述的标准生命周期回调协定）。此功能还强制执行初始化和销毁​​方法回调的一致命名约定。

假设您的初始化回调方法已命名，init()并且您的destroy回调方法已命名destroy()。然后，您的类类似于以下示例中的类：

    public class DefaultBlogService implements BlogService {
    
        private BlogDao blogDao;
    
        public void setBlogDao(BlogDao blogDao) {
            this.blogDao = blogDao;
        }
    
        // this is (unsurprisingly) the initialization callback method
        public void init() {
            if (this.blogDao == null) {
                throw new IllegalStateException("The [blogDao] property must be set.");
            }
        }
    }

然后，您可以在类似于以下内容的bean中使用该类：
    <beans default-init-method="init">

        <bean id="blogService" class="com.something.DefaultBlogService">
            <property name="blogDao" ref="blogDao" />
        </bean>
    
    </beans>

`default-init-method`顶级`<beans/>`元素属性上存在属性会导致Spring IoC容器init将bean类上调用的方法识别为初始化方法回调。当bean被创建和组装时，如果bean类具有这样的方法，则在适当的时候调用它。

您可以通过使用`default-destroy-method`顶级`<beans/>`元素上的属性来类似地配置`destroy`方法回调（在XML中） 。

如果现有的bean类已经具有与约定一致的回调方法，则可以通过使用 自身的init-method和destroy-method属性指定（在XML中，即方法名称）来覆盖默认值<bean/>。

Spring容器保证在为bean提供所有依赖项后立即调用已配置的初始化回调。因此，在原始bean引用上调用初始化回调，这意味着AOP拦截器等尚未应用于bean。首先完全创建目标bean，然后应用带有拦截器链的AOP代理（例如）。如果目标bean和代理是分开定义的，那么您的代码甚至可以绕过代理与原始目标bean交互。因此，将拦截器应用于init方法是不一致的，因为这样做会将目标bean的生命周期耦合到其代理或拦截器，并在代码直接与原始目标bean交互时留下奇怪的语义。

##### 结合生命周期机制

从Spring 2.5开始，您有三个控制bean生命周期行为的选项：

- 在InitializingBean和 DisposableBean回调接口

- 定制init()和destroy()方法

- 在@PostConstruct和@PreDestroy 注释。您可以组合这些机制来控制给定的bean。

> 如果为bean配置了多个生命周期机制，并且每个机制都配置了不同的方法名称，则每个配置的方法都按照此注释后列出的顺序执行。但是，如果init()为多个这些生命周期机制配置了相同的方法名称（例如， 对于初始化方法），则该方法将执行一次，如上 一节中所述。

为同一个bean配置的多个生命周期机制具有不同的初始化方法，如下所示：

- 用注释方法注释 @PostConstruct

- mafterPropertiesSet()由InitializingBean回调接口定义

- 自定义配置的init()方法

Destroy方法以相同的顺序调用：

- 用注释方法注释 @PreDestroy

- destroy()由DisposableBean回调接口定义

- 自定义配置的destroy()方法

##### 启动和关闭回调

该`Lifecycle`接口为任何具有自己的生命周期要求的对象（例如启动和停止某些后台进程）定义了基本方法：

    public interface Lifecycle {
    
        void start();
    
        void stop();
    
        boolean isRunning();
    }

##### Default Initialization and Destroy Methods

当你写的初始化和销毁不使用Spring的具体方法回调InitializingBean和DisposableBean回调接口，你通常写有名字，如方法init()，initialize()，dispose()，等等。理想情况下，此类生命周期回调方法的名称在项目中是标准化的，以便所有开发人员使用相同的方法名称并确保一致性。

您可以将Spring容器配置为“查找”命名初始化并销毁每个bean上的回调方法名称。这意味着，作为应用程序开发人员，您可以编写应用程序类并使用调用的初始化回调 init()，而无需为init-method="init"每个bean定义配置属性。Spring IoC容器在创建bean时调用该方法（并且符合前面描述的标准生命周期回调协定）。此功能还强制执行初始化和销毁​​方法回调的一致命名约定。

假设您的初始化回调方法已命名，init()并且您的destroy回调方法已命名destroy()。然后，您的类类似于以下示例中的类：

    public class DefaultBlogService implements BlogService {
    
        private BlogDao blogDao;
    
        public void setBlogDao(BlogDao blogDao) {
            this.blogDao = blogDao;
        }
    
        // this is (unsurprisingly) the initialization callback method
        public void init() {
            if (this.blogDao == null) {
                throw new IllegalStateException("The [blogDao] property must be set.");
            }
        }
    }

然后，您可以在类似于以下内容的bean中使用该类：

    <beans default-init-method="init">
    
        <bean id="blogService" class="com.something.DefaultBlogService">
            <property name="blogDao" ref="blogDao" />
        </bean>
    
    </beans>

default-init-method顶级<beans/>元素属性上存在属性会导致Spring IoC容器init将bean类上调用的方法识别为初始化方法回调。当bean被创建和组装时，如果bean类具有这样的方法，则在适当的时候调用它。

您可以通过使用default-destroy-method顶级<beans/>元素上的属性来类似地配置destroy方法回调（在XML中） 。

如果现有的bean类已经具有与约定一致的回调方法，则可以通过使用 自身的init-method和destroy-method属性指定（在XML中，即方法名称）来覆盖默认值<bean/>。

Spring容器保证在为bean提供所有依赖项后立即调用已配置的初始化回调。因此，在原始bean引用上调用初始化回调，这意味着AOP拦截器等尚未应用于bean。首先完全创建目标bean，然后应用带有拦截器链的AOP代理（例如）。如果目标bean和代理是分开定义的，那么您的代码甚至可以绕过代理与原始目标bean交互。因此，将拦截器应用于init方法是不一致的，因为这样做会将目标bean的生命周期耦合到其代理或拦截器，并在代码直接与原始目标bean交互时留下奇怪的语义。

结合生命周期机制
从Spring 2.5开始，您有三个控制bean生命周期行为的选项：

- 在InitializingBean和 DisposableBean回调接口

- 定制init()和destroy()方法

- 在@PostConstruct和@PreDestroy 注释。您可以组合这些机制来控制给定的bean。

> 如果为bean配置了多个生命周期机制，并且每个机制都配置了不同的方法名称，则每个配置的方法都按照此注释后列出的顺序执行。但是，如果init()为多个这些生命周期机制配置了相同的方法名称（例如， 对于初始化方法），则该方法将执行一次，如上 一节中所述。

为同一个bean配置的多个生命周期机制具有不同的初始化方法，如下所示：

- 用注释方法注释 @PostConstruct

- afterPropertiesSet()由InitializingBean回调接口定义

- 自定义配置的init()方法

Destroy方法以相同的顺序调用：

- 用注释方法注释 @PreDestroy

- destroy()由DisposableBean回调接口定义

- 自定义配置的destroy()方法

##### 启动和关闭回调

该Lifecycle接口为任何具有自己的生命周期要求的对象（例如启动和停止某些后台进程）定义了基本方法：

    public interface LifecycleProcessor extends Lifecycle {
    
        void onRefresh();
    
        void onClose();
    }

> 请注意，LifecycleProcessor它本身是Lifecycle 接口的扩展。它还添加了另外两种方法来响应刷新和关闭的上下文。
> 
> 请注意，常规org.springframework.context.Lifecycle接口是显式启动和停止通知的简单合约，并不意味着在上下文刷新时自动启动。要对特定bean的自动启动（包括启动阶段）进行细粒度控制，请考虑实现org.springframework.context.SmartLifecycle。

此外，请注意，在销毁之前不保证停止通知。在常规关闭时，所有Lifecyclebean在传播一般销毁回调之前首先收到停止通知。但是，在上下文生命周期中的热刷新或中止刷新尝试时，仅调用destroy方法。

启动和关闭调用的顺序非常重要。如果任何两个对象之间存在“依赖”关系，则依赖方在其依赖之后开始，并且在其依赖之前停止。但是，有时，直接依赖性是未知的。您可能只知道某种类型的对象应该在另一种类型的对象之前开始。在这些情况下，SmartLifecycle接口定义了另一个选项，即getPhase()在其超级接口上定义的方法 Phased。以下清单显示了Phased界面的定义：

    public interface Phased {
    
        int getPhase();
    }

下面的列表显示了SmartLifecycle接口的定义：

    public interface SmartLifecycle extends Lifecycle, Phased {
    
        boolean isAutoStartup();
    
        void stop(Runnable callback);
    }

启动时，具有最低相位的对象首先启动。停车时，按相反顺序行驶。因此，实现SmartLifecycle且其getPhase（）方法返回integer.min_值的对象将是第一个开始和最后一个停止的对象。在频谱的另一端，integer.max_值的相位值将指示对象应在最后启动并首先停止（可能是因为它取决于要运行的其他进程）。在考虑阶段值时，还必须知道不实现SmartLifecycle的任何“正常”生命周期对象的默认阶段是0。因此，任何负相位值都表示一个对象应该在这些标准组件之前开始（并在它们之后停止）。对于任何正相位值，反向为真。

定义的stop方法SmartLifecycle接受回调。任何实现必须run()在该实现的关闭过程完成后调用该回调的方法。这样就可以在必要时启用异步关闭，因为LifecycleProcessor接口 的默认实现DefaultLifecycleProcessor等待每个阶段内的对象组的超时值来调用该回调。默认的每阶段超时为30秒。您可以通过定义lifecycleProcessor在上下文中命名的bean来覆盖缺省生命周期处理器实例 。如果您只想修改超时，则定义以下内容就足够了：

    <bean id="lifecycleProcessor" class="org.springframework.context.support.DefaultLifecycleProcessor">
        <!-- timeout value in milliseconds -->
        <property name="timeoutPerShutdownPhase" value="10000"/>
    </bean>

如前所述，该LifecycleProcessor接口还定义了用于刷新和关闭上下文的回调方法。后者驱动关闭过程就好像stop()已经显式调用一样，但它在上下文关闭时发生。另一方面，'refresh'回调启用了SmartLifecyclebean的另一个功能 。刷新上下文时（在实例化并初始化所有对象之后），将调用该回调。此时，默认生命周期处理器检查每个SmartLifecycle对象的isAutoStartup()方法返回的布尔值 。如果true，那个对象是在那个点开始的，而不是等待显式调用上下文或它自己的对象start()方法（与上下文刷新不同，上下文启动不会自动发生在标准上下文实现中）。该phase值与任何“依赖式”的关系确定为前面所述的启动顺序。

##### 在非Web应用程序中优雅地关闭Spring IoC容器

> 本节仅适用于非Web应用程序。Spring的基于Web的 ApplicationContext实现已经具有代码，可以在相关Web应用程序关闭时正常关闭Spring IoC容器。

如果在非Web应用程序环境中使用Spring的IoC容器（例如，在富客户机桌面环境中），请使用JVM注册关闭挂钩。这样做可确保正常关闭并在单例bean上调用相关的destroy方法，以便释放所有资源。您仍然必须正确配置和实现这些destroy回调。

要注册关闭挂钩，请调用接口registerShutdownHook()上声明的方法ConfigurableApplicationContext，如以下示例所示：

    import org.springframework.context.ConfigurableApplicationContext;
    import org.springframework.context.support.ClassPathXmlApplicationContext;
    
    public final class Boot {
    
        public static void main(final String[] args) throws Exception {
            ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
    
            // add a shutdown hook for the above context...
            ctx.registerShutdownHook();
    
            // app runs here...
    
            // main method exits, hook is called prior to the app shutting down...
        }
    }

### 1.6.2 ApplicationContextAware和BeanNameAware

当ApplicationContext创建实现org.springframework.context.ApplicationContextAware接口的对象实例时，将 为该实例提供对该实例的引用ApplicationContext。以下清单显示了ApplicationContextAware界面的定义：

    public interface ApplicationContextAware {
    
        void setApplicationContext(ApplicationContext applicationContext) throws BeansException;
    }

因此，bean可以ApplicationContext通过ApplicationContext接口或通过将引用转换为此接口的已知子类（例如ConfigurableApplicationContext，公开其他功能）以编程方式操纵创建它们的方法。一种用途是对其他bean进行编程检索。有时这种能力很有用。但是，一般情况下，您应该避免使用它，因为它将代码耦合到Spring并且不遵循Inversion of Control样式，其中协作者作为属性提供给bean。其他方法 ApplicationContext提供对文件资源的访问，发布应用程序事件和访问MessageSource。这些附加功能在附加ApplicationContext功能中描述 。

从Spring 2.5开始，自动装配是另一种获取参考的方法 ApplicationContext。“传统” constructor和byType自动装配模式（如自动装配协作者中所述）可以分别为ApplicationContext构造函数参数或setter方法参数提供类型的依赖性 。为了获得更大的灵活性，包括自动装配字段和多参数方法的能力，请使用基于注释的新自动装配功能。如果这样做，ApplicationContext则自动装入一个字段，构造函数参数或方法参数，ApplicationContext如果相关的字段，构造函数或方法带有@Autowired注释，则该参数需要该类型。有关更多信息，请参阅 使用@Autowired。

当applicationContext创建一个实现org.springframework.beans.factory.beannameaware接口的类时，将向该类提供对其关联对象定义中定义的名称的引用。下面的列表显示了beannameaware接口的定义：

    public interface BeanNameAware {
    
        void setBeanName(String name) throws BeansException;
    }

在填充普通bean属性之后，但在初始化回调（如initializingbean、afterpropertiesset或自定义init方法）之前调用回调。

### 1.6.3 其他Aware接口

除了ApplicationContextAware和BeanNameAware讨论（早期），Spring提供了广泛的Aware，让豆子指示，他们需要一定的基础设施的依赖容器回调接口。作为一般规则，名称表示依赖关系类型。下表总结了最重要的Aware接口：

| 名称                               | 注入依赖                                                             | 解释在......                                                                                                                                                |
| -------------------------------- | ---------------------------------------------------------------- |:--------------------------------------------------------------------------------------------------------------------------------------------------------:|
| `ApplicationContextAware`        | 宣布`ApplicationContext`。                                          | [`ApplicationContextAware` 和 `BeanNameAware`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-aware) |
| `ApplicationEventPublisherAware` | 封闭的事件发布者`ApplicationContext`。                                    | [附加功能 `ApplicationContext`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#context-introduction)                  |
| `BeanClassLoaderAware`           | 用于加载bean类的类加载器。                                                  | [实例化豆​​类](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-class)                                     |
| `BeanFactoryAware`               | 宣布`BeanFactory`。                                                 | [`ApplicationContextAware` 和 `BeanNameAware`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-aware) |
| `BeanNameAware`                  | 声明bean的名称。                                                       | [`ApplicationContextAware` 和 `BeanNameAware`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-aware) |
| `BootstrapContextAware`          | `BootstrapContext`容器运行的资源适配器。通常仅在JCA感知`ApplicationContext`实例中可用。 | [JCA CCI](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/integration.html#cci)                                              |
| `LoadTimeWeaverAware`            | 定义的weaver用于在加载时处理类定义。                                            | [在Spring框架中使用AspectJ进行加载时编织](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#aop-aj-ltw)                           |
| `MessageSourceAware`             | 用于解析消息的已配置策略（支持参数化和国际化）。                                         | [附加功能 `ApplicationContext`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#context-introduction)                  |
| `NotificationPublisherAware`     | Spring JMX通知发布者。                                                 | [通知](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/integration.html#jmx-notifications)                                     |
| `ResourceLoaderAware`            | 配置的加载程序，用于对资源进行低级访问。                                             | [资源](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#resources)                                                    |
| `ServletConfigAware`             | 当前`ServletConfig`容器运行。仅在Web感知弹簧中有效 `ApplicationContext`。         | [Spring MVC](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/web.html#mvc)                                                   |
| `ServletContextAware`            | 当前`ServletContext`容器运行。仅在Web感知弹簧中有效 `ApplicationContext`。        | [Spring MVC](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/web.html#mvc)                                                   |

请再次注意，使用这些接口会将您的代码绑定到Spring API，而不会遵循Inversion of Control样式。因此，我们建议将它们用于需要以编程方式访问容器的基础架构bean。

---



## 1.7 Bean定义继承

bean定义可以包含许多配置信息，包括构造函数参数，属性值和特定于容器的信息，例如初始化方法，静态工厂方法名称等。子bean定义从父定义继承配置数据。子定义可以覆盖某些值或根据需要添加其他值。使用父bean和子bean定义可以节省大量的输入。实际上，这是一种模板形式。

如果以`ApplicationContext`编程方式使用接口，则子bean定义由`ChildBeanDefinition`类表示。大多数用户不在此级别上使用它们。相反，它们在类中以声明方式配置bean定义`ClassPathXmlApplicationContext`。使用基于XML的配置元数据时，可以使用该`parent`属性指定子bean定义，并将父bean指定为此属性的值。以下示例显示了如何执行此操作：

```xml
<bean id="inheritedTestBean" abstract="true"
        class="org.springframework.beans.TestBean">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithDifferentClass"
        class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBean" init-method="initialize">  
    <property name="name" value="override"/>
    <!-- the age property value of 1 will be inherited from parent -->
</bean>
```

> 注意`parent`属性。

如果没有指定，则bean bean定义使用父定义中的bean类，但也可以覆盖它。在后一种情况下，子bean类必须与父类兼容（即，它必须接受父类的属性值）。

子bean定义从父级继承范围，构造函数参数值，属性值和方法覆盖，并带有添加新值的选项。`static`您指定的任何范围，初始化方法，销毁方法或工厂方法设置都会覆盖相应的父设置。

其余设置始终取自子定义：取决于，autowire模式，依赖性检查，单例和惰性初始化。

前面的示例通过使用该`abstract`属性将父bean定义显式标记为abstract 。如果父定义未指定类，`abstract`则根据需要显式标记父bean定义，如以下示例所示：

```xml
<bean id="inheritedTestBeanWithoutClass" abstract="true">
    <property name="name" value="parent"/>
    <property name="age" value="1"/>
</bean>

<bean id="inheritsWithClass" class="org.springframework.beans.DerivedTestBean"
        parent="inheritedTestBeanWithoutClass" init-method="initialize">
    <property name="name" value="override"/>
    <!-- age will inherit the value of 1 from the parent bean definition-->
</bean>
```

父bean不能单独实例化，因为它不完整，并且也明确标记为`abstract`。定义时`abstract`，它仅可用作纯模板bean定义，用作子定义的父定义。尝试使用这样的`abstract`父bean，通过将其称为另一个bean的ref属性或`getBean()`使用父bean ID 进行显式调用，将返回错误。类似地，容器的内部 `preInstantiateSingletons()`方法忽略定义为abstract的bean定义。

> `ApplicationContext`默认情况下预先实例化所有单例。因此，重要的是（至少对于单例bean），如果你有一个（父）bean定义，你只打算用作模板，并且这个定义指定了一个类，你必须确保将*abstract*属性设置为*true*否则应用程序上下文将实际（尝试）预先实例化`abstract`bean。

## 1.8 集装箱扩建点

通常，应用程序开发人员不需要子类化`ApplicationContext` 实现类。相反，可以通过插入特殊集成接口的实现来扩展Spring IoC容器。接下来的几节将介绍这些集成接口。

### 1.8.1 使用定制Bean   `BeanPostProcessor`

该`BeanPostProcessor`接口定义了您可以实现的回调方法，以提供您自己的（或覆盖容器的默认）实例化逻辑，依赖关系解析逻辑等。如果要在Spring容器完成实例化，配置和初始化bean之后实现某些自定义逻辑，则可以插入一个或多个自定义`BeanPostProcessor`实现。

您可以配置多个`BeanPostProcessor`实例，并且可以`BeanPostProcessor`通过设置`order`属性来控制这些实例的执行顺序。只有在`BeanPostProcessor`实现`Ordered` 接口时才能设置此属性。如果你自己编写`BeanPostProcessor`，你也应该考虑实现这个`Ordered`接口。有关更多详细信息，请参阅[`BeanPostProcessor`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html) 和[`Ordered`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/core/Ordered.html)接口的javadoc 。另见关于[实例的](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-programmatically-registering-beanpostprocessors)[程序化登记`BeanPostProcessor`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-programmatically-registering-beanpostprocessors)的说明。

> BeanPostProcessor`实例在bean（或对象）实例上运行。也就是说，Spring IoC容器实例化一个bean实例，然后`BeanPostProcessor` 实例执行它们的工作。`BeanPostProcessor`实例的范围是每个容器。仅当您使用容器层次结构时，这才是相关的。如果`BeanPostProcessor`在一个容器中定义一个容器，它只会对该容器中的bean进行后处理。换句话说，`BeanPostProcessor`即使两个容器都是同一层次结构的一部分，在一个容器中定义的bean也不会被另一个容器中定义的bean进行后处理。要更改实际的bean定义（即定义bean的蓝图），您需要使用a `BeanFactoryPostProcessor`，如 使用。[`定制配置元数据中所述`BeanFactoryPostProcessor](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-extension-factory-postprocessors)

该`org.springframework.beans.factory.config.BeanPostProcessor`接口由两个回调方法组成。当这样的类被注册为具有容器的后处理器时，对于由容器创建的每个bean实例，后处理器在容器初始化方法（例如`InitializingBean.afterPropertiesSet()`或任何声明的`init`方法）之前都从容器获得回调。调用，并在任何bean初始化后回调。后处理器可以对bean实例执行任何操作，包括完全忽略回调。bean后处理器通常检查回调接口，或者它可以用代理包装bean。一些Spring AOP基础结构类实现为bean后处理器，以便提供代理包装逻辑。

的`ApplicationContext`自动检测中的实现的配置的元数据中定义的任何豆`BeanPostProcessor`接口。将 `ApplicationContext`这些bean注册为后处理器，以便在创建bean时可以稍后调用它们。Bean后处理器可以以与任何其他bean相同的方式部署在容器中。

请注意，在配置类上`BeanPostProcessor`使用`@Bean`工厂方法声明a时，工厂方法的返回类型应该是实现类本身或至少是`org.springframework.beans.factory.config.BeanPostProcessor` 接口，清楚地表明该bean的后处理器性质。否则，`ApplicationContext`在完全创建之前， 无法按类型自动检测它。由于`BeanPostProcessor`需要尽早实例化以便应用于上下文中其他bean的初始化，因此这种早期类型检测至关重要。

> 以编程方式注册`BeanPostProcessor`实例虽然推荐的`BeanPostProcessor`注册方法是通过 `ApplicationContext`自动检测（如前所述），但您可以`ConfigurableBeanFactory`使用该`addBeanPostProcessor` 方法以编程方式对其进行注册。当您需要在注册前评估条件逻辑或甚至跨层次结构中的上下文复制Bean post处理器时，这非常有用。但请注意，以`BeanPostProcessor`编程方式添加的实例不尊重`Ordered`接口。这里，注册的顺序决定了执行的顺序。另请注意，以`BeanPostProcessor`编程方式注册的实例始终在通过自动检测注册的实例之前处理，而不管任何显式排序。

> BeanPostProcessor` 实例和AOP自动代理实现`BeanPostProcessor`接口的类是特殊的，容器会对它们进行不同的处理。`BeanPostProcessor`他们直接引用的所有实例和bean都会在启动时实例化，作为特殊启动阶段的一部分`ApplicationContext`。接下来，所有`BeanPostProcessor`实例都以排序方式注册，并应用于容器中的所有其他bean。因为AOP自动代理是作为一个`BeanPostProcessor`自身实现的，所以`BeanPostProcessor` 实例和它们直接引用的bean都不符合自动代理的条件，因此没有编织方面。对于任何此类bean，您应该看到一条信息性日志消息：`Bean someBean is not eligible for getting processed by all BeanPostProcessor interfaces (for example: not eligible for auto-proxying)`。如果您`BeanPostProcessor`通过使用自动装配或`@Resource`（可能回退到自动装配）将bean连接到您的 ，则Spring可能会在搜索类型匹配依赖项候选项时访问意外的bean，因此，使它们不符合自动代理或其他类型的bean post -处理。例如，如果您有一个依赖项，`@Resource`其中字段或setter名称与bean的声明名称没有直接对应，并且没有使用name属性，则Spring会访问其他bean以按类型匹配它们。

以下示例显示如何在中编写，注册和使用`BeanPostProcessor`实例`ApplicationContext`。

##### 示例：Hello World，`BeanPostProcessor`-style

第一个例子说明了基本用法。该示例显示了一个自定义 `BeanPostProcessor`实现，该实现调用`toString()`容器创建的每个bean 的方法，并将生成的字符串输出到系统控制台。

以下清单显示了自定义`BeanPostProcessor`实现类定义：

```java
package scripting;

import org.springframework.beans.factory.config.BeanPostProcessor;

public class InstantiationTracingBeanPostProcessor implements BeanPostProcessor {

    // simply return the instantiated bean as-is
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean; // we could potentially return any object reference here...
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("Bean '" + beanName + "' created : " + bean.toString());
        return bean;
    }
}
```

以下`beans`元素使用`InstantiationTracingBeanPostProcessor`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:lang="http://www.springframework.org/schema/lang"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/lang
        https://www.springframework.org/schema/lang/spring-lang.xsd">

    <lang:groovy id="messenger"
            script-source="classpath:org/springframework/scripting/groovy/Messenger.groovy">
        <lang:property name="message" value="Fiona Apple Is Just So Dreamy."/>
    </lang:groovy>

    <!--
    when the above bean (messenger) is instantiated, this custom
    BeanPostProcessor implementation will output the fact to the system console
    -->
    <bean class="scripting.InstantiationTracingBeanPostProcessor"/>

</beans>
```

请注意它`InstantiationTracingBeanPostProcessor`是如何定义的。它甚至没有名称，并且，因为它是一个bean，它可以像任何其他bean一样依赖注入。（前面的配置还定义了一个由Groovy脚本支持的bean。在动态语言支持一章中详细介绍了Spring [动态语言支持](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/languages.html#dynamic-language)。）

以下Java应用程序运行上述代码和配置：

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scripting.Messenger;

public final class Boot {

    public static void main(final String[] args) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("scripting/beans.xml");
        Messenger messenger = (Messenger) ctx.getBean("messenger");
        System.out.println(messenger);
    }

}
```

上述应用程序的输出类似于以下内容：

```
Bean'sensenger'创建：org.springframework.scripting.groovy.GroovyMessenger@272961 
org.springframework.scripting.groovy.GroovyMessenger@272961
```

##### 示例： `RequiredAnnotationBeanPostProcessor`

将回调接口或注释与自定义`BeanPostProcessor`实现结合使用 是扩展Spring IoC容器的常用方法。一个例子是Spring `RequiredAnnotationBeanPostProcessor` - 一个 `BeanPostProcessor`随Spring发行版一起提供的实现，它确保标记有（任意）注释的bean上的JavaBean属性实际上（配置为）依赖注入值。

### 1.8.2使用自定义配置元数据`BeanFactoryPostProcessor`

我们看到的下一个扩展点是 `org.springframework.beans.factory.config.BeanFactoryPostProcessor`。这个接口的语义类似于`BeanPostProcessor`它的一个主要区别：`BeanFactoryPostProcessor`操作bean配置元数据。也就是说，Spring IoC容器允许`BeanFactoryPostProcessor`读取配置元数据，并可能在容器实例化除实例之外的任何bean *之前*更改它`BeanFactoryPostProcessor`。

您可以配置多个`BeanFactoryPostProcessor`实例，并且可以`BeanFactoryPostProcessor`通过设置`order`属性来控制这些实例的运行顺序。但是，如果`BeanFactoryPostProcessor`实现 `Ordered`接口，则只能设置此属性。如果你自己编写`BeanFactoryPostProcessor`，你也应该考虑实现这个`Ordered`接口。有关更多详细信息，请参阅[`BeanFactoryPostProcessor`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/beans/factory/config/BeanFactoryPostProcessor.html)和[`Ordered`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/core/Ordered.html)接口的javadoc 。

> 如果要更改实际的bean实例（即，从配置元数据创建的对象），则需要使用a `BeanPostProcessor` （前面在[使用a定制Bean中进行了描述`BeanPostProcessor`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-extension-bpp)）。虽然技术上可以在a中使用bean实例`BeanFactoryPostProcessor`（例如，通过使用 `BeanFactory.getBean()`），但这样做会导致过早的bean实例化，从而违反标准的容器生命周期。这可能会导致负面影响，例如绕过bean后期处理。此外，`BeanFactoryPostProcessor`实例的范围是每个容器的范围。仅当您使用容器层次结构时，这才有意义。如果`BeanFactoryPostProcessor`在一个容器中定义一个容器，则它仅应用于该容器中的bean定义。`BeanFactoryPostProcessor`即使两个容器都是同一层次结构的一部分，一个容器中的Bean定义也不会被另一个容器中的实例进行后处理。



Bean工厂后处理器在其内部声明时会自动执行 `ApplicationContext`，以便将更改应用于定义容器的配置元数据。Spring包含许多预定义的bean工厂后处理器，例如`PropertyOverrideConfigurer`和 `PropertyPlaceholderConfigurer`。您还可以使用自定义`BeanFactoryPostProcessor` - 例如，注册自定义属性编辑器。

一个`ApplicationContext`自动检测部署在它实现了任何豆`BeanFactoryPostProcessor`接口。它在适当的时候使用这些bean作为bean工厂后处理器。您可以像处理任何其他bean一样部署这些后处理器bean。

> 与`BeanPostProcessor`s一样，您通常不希望`BeanFactoryPostProcessor`为延迟初始化配置 s。如果没有其他bean引用a `Bean(Factory)PostProcessor`，则该后处理器根本不会被实例化。因此，将其标记为延迟初始化将被忽略，`Bean(Factory)PostProcessor`会急切地实例化，即使你设定的 `default-lazy-init`属性`true`对你的声明`<beans />`元素。



##### 示例：类名替换 `PropertyPlaceholderConfigurer`

您可以使用`PropertyPlaceholderConfigurer`标准Java `Properties`格式在单独的文件中使用bean定义中的外部化属性值。这样做可以使部署应用程序的人员自定义特定于环境的属性，例如数据库URL和密码，而不会出现修改主XML定义文件或容器文件的复杂性或风险。

请考虑以下基于XML的配置元数据片段，其中`DataSource` 定义了占位符值：

```xml
<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
    <property name="locations" value="classpath:com/something/jdbc.properties"/>
</bean>

<bean id="dataSource" destroy-method="close"
        class="org.apache.commons.dbcp.BasicDataSource">
    <property name="driverClassName" value="${jdbc.driverClassName}"/>
    <property name="url" value="${jdbc.url}"/>
    <property name="username" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>
```

该示例显示了从外部`Properties`文件配置的属性。在运行时，a `PropertyPlaceholderConfigurer`将应用于替换DataSource的某些属性的元数据。要替换的值被指定为表单的占位符`${property-name}`，它遵循Ant和log4j以及JSP EL样式。

实际值来自标准Java `Properties`格式的另一个文件：

```xml
jdbc.driverClassName = org.hsqldb.jdbcDriver
jdbc.url = JDBC：HSQLDB：HSQL：//localhost：9002
jdbc.username = root
jdbc.password =root
```

因此，`${jdbc.username}`在运行时使用值“sa”替换字符串，这同样适用于与属性文件中的键匹配的其他占位符值。在`PropertyPlaceholderConfigurer`为大多数属性和bean定义的属性占位符检查。此外，您可以自定义占位符前缀和后缀。

使用`context`Spring 2.5中引入的命名空间，您可以使用专用配置元素配置属性占位符。您可以在`location`属性中提供一个或多个位置作为逗号分隔列表，如以下示例所示：

```xml
<context:property-placeholder location="classpath:com/something/jdbc.properties"/>
```

在`PropertyPlaceholderConfigurer`不仅将查找在属性`Properties` 指定的文件。默认情况下，如果它在指定的属性文件中找不到属性，它还会检查Java `System`属性。您可以通过`systemPropertiesMode`使用以下三个受支持的整数值之一设置configurer 的属性来自定义此行为：

- `never` （0）：从不检查系统属性。
- `fallback`（1）：如果在指定的属性文件中无法解析，则检查系统属性。这是默认值。
- `override`（2）：在尝试指定的属性文件之前，首先检查系统属性。这使系统属性可以覆盖任何其他属性源。

有关[`PropertyPlaceholderConfigurer`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/beans/factory/config/PropertyPlaceholderConfigurer.html)更多信息，请参阅javadoc。

> 您可以使用`PropertyPlaceholderConfigurer`替换类名称，这在您必须在运行时选择特定实现类时有时很有用。以下示例显示了如何执行此操作：
>
> ```xml
> <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
>     <property name="locations">
>         <value>classpath:com/something/strategy.properties</value>
>     </property>
>     <property name="properties">
>         <value>custom.strategy.class=com.something.DefaultStrategy</value>
>     </property>
> </bean>
> 
> <bean id="serviceStrategy" class="${custom.strategy.class}"/><bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">     <property name="locations">         <value>classpath:com/something/strategy.properties</value>     </property>     <property name="properties">         <value>custom.strategy.class=com.something.DefaultStrategy</value>     </property> </bean>  <bean id="serviceStrategy" class="${custom.strategy.class}"/>
> ```
>
> 如果类不能在分辨率，当它即将被创造，这是在失败`preInstantiateSingletons()` 的阶段`ApplicationContext`对非延迟实例化的bean。

##### 示例： `PropertyOverrideConfigurer`

在`PropertyOverrideConfigurer`另一个bean工厂后置处理器，类似 `PropertyPlaceholderConfigurer`，但不同的是后者，原来的定义可以有缺省值或者根本没有值的bean属性。如果覆盖 `Properties`文件没有某个bean属性的条目，则使用默认上下文定义。

请注意，bean定义不知道被覆盖，因此从XML定义文件中可以立即看出正在使用覆盖配置器。如果多个`PropertyOverrideConfigurer`实例为同一个bean属性定义了不同的值，则由于覆盖机制，最后一个实例会获胜。

属性文件配置行采用以下格式：

```
beanName.property=value
```

以下清单显示了格式的示例：

```
dataSource.driverClassName=com.mysql.jdbc.Driver
dataSource.url=jdbc:mysql:mydb
```

此示例文件可以与包含名为`dataSource`has has `driver`和`url`properties 的bean的容器定义一起使用 。

也支持复合属性名称，只要路径的每个组件（重写的最终属性除外）都已经非空（可能由构造函数初始化）。在下面的例子中，`sammy`所述的属性`bob`的财产`fred`的财产`tom`豆被设置为标量值`123`：

```
tom.fred.bob.sammy = 123
```

> 指定的覆盖值始终是文字值。它们不会被翻译成bean引用。当XML bean定义中的原始值指定bean引用时，此约定也适用。

使用`context`Spring 2.5中引入的命名空间，可以使用专用配置元素配置属性覆盖，如以下示例所示：

```
<context:property-override location="classpath:override.properties"/>
```

### 1.8.3使用自定义实例化逻辑`FactoryBean`

您可以`org.springframework.beans.factory.FactoryBean`为自己工厂的对象实现接口。

该`FactoryBean`接口是Spring IoC容器实例化逻辑的可插拔点。如果你有一个复杂的初始化代码，用Java表示，而不是（可能）冗长的XML，你可以创建自己的 `FactoryBean`，在该类中编写复杂的初始化，然后将自定义`FactoryBean`插入容器。

该`FactoryBean`接口提供了三种方法：

- `Object getObject()`：返回此工厂创建的对象的实例。可以共享实例，具体取决于此工厂是返回单例还是原型。
- `boolean isSingleton()`：`true`如果`FactoryBean`返回单例或`false`其他方式返回 。
- `Class getObjectType()`：返回`getObject()`方法返回的对象类型，或者`null`如果事先不知道类型。

该`FactoryBean`概念和接口被一些Spring框架内的场所。超过50个`FactoryBean`接口的实现随Spring一起提供。

当你需要向一个容器询问一个实际的`FactoryBean`实例本身而不是它生成的bean 时，在调用the的方法时`id`，用strersand符号（`&`）作为前缀。因此，对于给定 与的，调用在容器上返回的产品，而调用返回的 实例本身。`getBean()``ApplicationContext``FactoryBean``id``myBean``getBean("myBean")``FactoryBean``getBean("&myBean")``FactoryBean`



## 1.9 基于注释的容器配置

基于注释的配置的引入引发了这种方法是否比XML“更好”的问题。简短的回答是“它取决于。”长期的答案是每种方法都有其优点和缺点，通常，由开发人员决定哪种策略更适合他们。由于它们的定义方式，注释在其声明中提供了大量上下文，从而导致更短更简洁的配置。但是，XML擅长在不触及源代码或重新编译它们的情况下连接组件。一些开发人员更喜欢将布线靠近源，而另一些开发人员则认为注释类不再是POJO，而且配置变得分散且难以控制。

无论选择如何，Spring都可以兼顾两种风格，甚至可以将它们混合在一起。值得指出的是，通过其JavaConfig选项，Spring允许以非侵入方式使用注释，而无需触及目标组件源代码，并且在工具方面，Spring Tool Suite支持所有配置样式 。

基于注释的配置提供了XML设置的替代方案，该配置依赖于字节码元数据来连接组件而不是角括号声明。开发人员不是使用XML来描述bean连接，而是通过在相关的类，方法或字段声明上使用注释将配置移动到组件类本身。如示例中所述：RequiredAnnotationBeanPostProcessor使用BeanPostProcessor与注释结合使用是扩展Spring IoC容器的常用方法。例如，Spring 2.0引入了使用@Required注释强制执行所需属性的可能性。Spring 2.5使得有可能采用相同的通用方法来驱动Spring的依赖注入。基本上，@Autowired注释提供与自动装配协作者中描述的相同的功能，但具有更细粒度的控制和更广泛的适用性。Spring 2.5还增加了对JSR-250注释的支持，例如 @PostConstruct和@PreDestroy。Spring 3.0增加了对javax.inject包中包含的JSR-330（Java的依赖注入）注释的支持，例如@Inject 和@Named。有关这些注释的详细信息，请参阅 相关章节。

> 注释注入在XML注入之前执行。因此，XML配置会覆盖通过这两种方法连接的属性的注释。

与往常一样，您可以将它们注册为单独的bean定义，但也可以通过在基于XML的Spring配置中包含以下标记来隐式注册它们（请注意包含context命名空间）：

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            https://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            https://www.springframework.org/schema/context/spring-context.xsd">
    
        <context:annotation-config/>
    
    </beans>

在隐式注册后处理器包括 AutowiredAnnotationBeanPostProcessor， CommonAnnotationBeanPostProcessor， PersistenceAnnotationBeanPostProcessor，和前面提到的 RequiredAnnotationBeanPostProcessor。）

> <context:annotation-config/>仅查找在定义它的同一应用程序上下文中的bean上的注释。这意味着，如果你 <context:annotation-config/>输入一个WebApplicationContextfor DispatcherServlet，它只会检查@Autowired你的控制器中的bean，而不是你的服务。有关更多信息，请参阅 DispatcherServlet。

### 1.9.1 @Required

该@Required注释适用于bean属性setter方法，如下面的例子：

public class SimpleMovieLister {

    private MovieFinder movieFinder;
    
    @Required
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
    
    // ...
    }

此批注指示必须在配置时通过bean定义中的显式属性值或通过自动装配填充受影响的bean属性。如果尚未填充受影响的bean属性，则容器将引发异常。这允许急切和明确的失败，以后避免NullPointerException 实例等。我们仍然建议您将断言放入bean类本身（例如，转换为init方法）。即使您在容器外部使用类，这样做也会强制执行那些必需的引用和值。

> 从@RequiredSpring Framework 5.1开始，注释正式被弃用，支持使用构造函数注入所需的设置（或者InitializingBean.afterPropertiesSet()bean属性setter方法的自定义实现 ）。

> 在本节包含的示例中，JSR 330的@inject注释可以代替spring的@autowired注释。有关详细信息，请参阅此处。

可以将@autowired注释应用于构造函数，如下例所示：

    public class MovieRecommender {
    
        private final CustomerPreferenceDao customerPreferenceDao;
    
        @Autowired
        public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
            this.customerPreferenceDao = customerPreferenceDao;
        }
    
        // ...
    }

> 从Spring Framework 4.3开始，@Autowired如果目标bean只定义了一个开头的构造函数，则不再需要对这样的构造函数进行注释。但是，如果有几个构造器可用，则必须注释至少一个构造器以教导容器使用哪一个。

您还可以将@Autowired注释应用于“传统”setter方法，如以下示例所示：

    public class SimpleMovieLister {
    
        private MovieFinder movieFinder;
    
        @Autowired
        public void setMovieFinder(MovieFinder movieFinder) {
            this.movieFinder = movieFinder;
        }
    
        // ...
    }

您还可以将注释应用于具有任意名称和多个参数的方法，如以下示例所示：

    public class MovieRecommender {
    
        private MovieCatalog movieCatalog;
    
        private CustomerPreferenceDao customerPreferenceDao;
    
        @Autowired
        public void prepare(MovieCatalog movieCatalog,
                CustomerPreferenceDao customerPreferenceDao) {
            this.movieCatalog = movieCatalog;
            this.customerPreferenceDao = customerPreferenceDao;
        }
    
        // ...
    }

您也可以应用于@Autowired字段，甚至可以将它与构造函数混合使用，如下例所示：

    public class MovieRecommender {
    
        private final CustomerPreferenceDao customerPreferenceDao;
    
        @Autowired
        private MovieCatalog movieCatalog;
    
        @Autowired
        public MovieRecommender(CustomerPreferenceDao customerPreferenceDao) {
            this.customerPreferenceDao = customerPreferenceDao;
        }
    
        // ...
    }

您还可以ApplicationContext 通过将注释添加到需要该类型数组的字段或方法来提供特定类型的所有bean ，如以下示例所示：

    public class MovieRecommender {
    
        @Autowired
        private MovieCatalog[] movieCatalogs;
    
        // ...
    }

> 您的目标bean可以实现org.springframework.core.ordered接口，或者如果您希望数组或列表中的项按特定顺序排序，可以使用@order或standard@priority注释。否则，它们的顺序遵循容器中相应目标bean定义的注册顺序。
> 您可以@Order在目标类级别和@Bean方法上声明注释，可能是通过单个bean定义（在多个定义使用相同bean类的情况下）。@Order值可能会影响注入点的优先级，但要注意它们不会影响单例启动顺序，这是由依赖关系和@DependsOn声明确定的正交关注点。
> 
> 请注意，标准javax.annotation.Priority注释在该@Bean级别不可用 ，因为它无法在方法上声明。它的语义可以通过@Order值与@Primary每种类型的单个bean 相结合来建模。

Map只要预期的密钥类型是，即使是类型化的实例也可以自动装配String。Map值包含所有期望类型的bean，并且键包含相应的bean名称，如以下示例所示：

    public class MovieRecommender {
    
        private Map<String, MovieCatalog> movieCatalogs;
    
        @Autowired
        public void setMovieCatalogs(Map<String, MovieCatalog> movieCatalogs) {
            this.movieCatalogs = movieCatalogs;
        }
    
        // ...
    }

默认情况下，当没有匹配的候选bean可用于给定的注入点时，自动布线将失败。对于声明的数组、集合或映射，至少需要一个匹配元素。

默认行为是将带注释的方法和字段视为指示所需的依赖项。您可以更改此行为，如以下示例所示，使框架能够通过将其标记为非必需来跳过不可满足的注入点：

    public class SimpleMovieLister {
    
        private MovieFinder movieFinder;
    
        @Autowired(required = false)
        public void setMovieFinder(MovieFinder movieFinder) {
            this.movieFinder = movieFinder;
        }
    
        // ...
    }

如果不可用的依赖项（或多个参数情况下的依赖项之一），则根本不会调用非必需的方法。在这种情况下，根本不会填充非必填字段，保留其默认值。

注入的构造函数和工厂方法参数是一种特殊情况，因为@Autowired由于Spring的构造函数解析算法可能涉及多个构造函数，因此'required'标志的含义有些不同。默认情况下有效地需要构造函数和工厂方法参数，但在单构造函数场景中有一些特殊规则，例如，如果没有匹配的bean可用，则解析为空实例的多元素注入点（数组，集合，映射）。这允许一个通用的实现模式，其中所有依赖项都可以在一个唯一的多参数构造函数中声明，例如声明为没有@Autowired注释的单个公共构造函数。

> 每个类只能标记一个带注释的构造函数，但可以注释多个非必需的构造函数。在这种情况下，每个都被认为是候选者之一，Spring使用最贪婪的构造函数，其依赖性可以得到满足 - 也就是说，具有最多参数的构造函数。构造函数解析算法与具有重载构造函数的非注释类相同，只是将候选者缩小到带注释的构造函数。
> 
> 建议使用“必需”属性而@Autowired不是@Requiredsetter方法的注释。“required”属性表示该属性不是自动装配所必需的。如果无法自动装配，则会忽略该属性。@Required另一方面，它更强大，因为它强制通过容器支持的任何方式设置属性。如果未定义任何值，则会引发相应的异常。

或者，您可以通过Java 8表达特定依赖关系的非必需特性java.util.Optional，如以下示例所示：

    public class SimpleMovieLister {
    
        @Autowired
        public void setMovieFinder(Optional<MovieFinder> movieFinder) {
            ...
        }
    }

从Spring Framework 5.0开始，您还可以使用@Nullable注释（任何包中的任何类型的注释 - 例如，javax.annotation.Nullable来自JSR-305）：

    public class SimpleMovieLister {
    
        @Autowired
        public void setMovieFinder(@Nullable MovieFinder movieFinder) {
            ...
        }
    }

您还可以使用@Autowired对于那些众所周知的解析依赖接口：BeanFactory，ApplicationContext，Environment，ResourceLoader， ApplicationEventPublisher，和MessageSource。这些接口及其扩展接口（如ConfigurableApplicationContext或ResourcePatternResolver）会自动解析，无需特殊设置。以下示例自动装配一个ApplicationContext对象：

    public class MovieRecommender {
    
        @Autowired
        private ApplicationContext context;
    
        public MovieRecommender() {
        }
    
        // ...
    }

> 在`@Autowired`，`@Inject`，`@Value`，和`@Resource`注释由Spring处理 `BeanPostProcessor`实现。这意味着您无法在自己的类型`BeanPostProcessor`或`BeanFactoryPostProcessor`类型（如果有）中应用这些注释。必须使用XML或Spring `@Bean`方法显式地“连接”这些类型。 

### 1.9.3 微调基于注释的自动装配`@Primary`

由于按类型自动装配可能会导致多个候选人，因此通常需要对选择过程进行更多控制。实现这一目标的一种方法是使用Spring的`@Primary`注释。`@Primary`表示当多个bean可以自动装配到单值依赖项时，应该优先选择特定的bean。如果候选者中只存在一个主bean，则它将成为自动装配的值。

请考虑以下定义`firstMovieCatalog`为主要的配置`MovieCatalog`：

```
@Configuration
public class MovieConfiguration {

    @Bean
    @Primary
    public MovieCatalog firstMovieCatalog() { ... }

    @Bean
    public MovieCatalog secondMovieCatalog() { ... }

    // ...
}
```

使用上述配置，以下`MovieRecommender`内容自动装配 `firstMovieCatalog`：

```
public class MovieRecommender {

    @Autowired
    private MovieCatalog movieCatalog;

    // ...
}
```

相应的bean定义如下：

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog" primary="true">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>
```

### 1.9.4。使用限定符微调基于注释的自动装配

`@Primary`当可以确定一个主要候选者时，是通过具有多个实例的类型使用自动装配的有效方式。当您需要更多控制选择过程时，可以使用Spring的`@Qualifier`注释。您可以将限定符值与特定参数相关联，缩小类型匹配集，以便为每个参数选择特定的bean。在最简单的情况下，这可以是一个简单的描述性值，如以下示例所示：

```
public class MovieRecommender {

    @Autowired
    @Qualifier("main")
    private MovieCatalog movieCatalog;

    // ...
}
```

您还可以`@Qualifier`在各个构造函数参数或方法参数上指定注释，如以下示例所示：

```
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(@Qualifier("main") MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

### 1.9.4。使用限定符微调基于注释的自动装配

`@Primary`当可以确定一个主要候选者时，是通过具有多个实例的类型使用自动装配的有效方式。当您需要更多控制选择过程时，可以使用Spring的`@Qualifier`注释。您可以将限定符值与特定参数相关联，缩小类型匹配集，以便为每个参数选择特定的bean。在最简单的情况下，这可以是一个简单的描述性值，如以下示例所示：

```
public class MovieRecommender {

    @Autowired
    @Qualifier("main")
    private MovieCatalog movieCatalog;

    // ...
}
```

您还可以`@Qualifier`在各个构造函数参数或方法参数上指定注释，如以下示例所示：

```
public class MovieRecommender {

    private MovieCatalog movieCatalog;

    private CustomerPreferenceDao customerPreferenceDao;

    @Autowired
    public void prepare(@Qualifier("main") MovieCatalog movieCatalog,
            CustomerPreferenceDao customerPreferenceDao) {
        this.movieCatalog = movieCatalog;
        this.customerPreferenceDao = customerPreferenceDao;
    }

    // ...
}
```

以下示例显示了相应的bean定义。

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier value="main"/> 

        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier value="action"/> 

        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

|     | 具有`main`限定符值的bean与使用相同值限定的构造函数参数连接。   |
| --- | ------------------------------------- |
|     | 具有`action`限定符值的bean与使用相同值限定的构造函数参数连接。 |

对于回退匹配，bean名称被视为默认限定符值。因此，可以用一个定义bean `id`的`main`代替嵌套限定符元素，导致相同的匹配结果。但是，尽管您可以使用此约定来按名称引用特定bean，但`@Autowired`基本上是关于具有可选语义限定符的类型驱动注入。这意味着即使使用bean名称回退，限定符值在类型匹配集中也总是具有缩小的语义。它们在语义上不表示对唯一bean的引用`id`。良好限定的值是`main`或`EMEA`或`persistent`，表达独立于从所述豆的特定部件的特性`id`，在匿名bean定义的情况下可以自动生成，例如前面例子中的定义。

限定符也适用于类型集合，如前所述 - 例如，to `Set<MovieCatalog>`。在这种情况下，根据声明的限定符，所有匹配的bean都作为集合注入。这意味着限定符不必是唯一的。相反，它们构成了过滤标准。例如，您可以`MovieCatalog`使用相同的限定符值“action” 定义多个bean，所有这些bean都注入带`Set<MovieCatalog>`注释的注释中`@Qualifier("action")`。

> 在类型匹配候选项中，根据目标bean名称选择限定符值，不需要`@Qualifier`注入点处的注释。如果没有其他解析指示符（例如限定符或主要标记），则对于非唯一依赖性情况，Spring会将注入点名称（即字段名称或参数名称）与目标bean名称进行匹配，然后选择同名的候选人，如果有的话。
> 
> 也就是说，如果您打算按名称表达注释驱动的注入，请不要主要使用`@Autowired`，即使它能够在类型匹配候选项中通过bean名称进行选择。相反，使用JSR-250 `@Resource`注释，该注释在语义上定义为通过其唯一名称标识特定目标组件，声明的类型与匹配过程无关。`@Autowired`具有相当不同的语义：在按类型选择候选bean之后，`String` 仅在那些类型选择的候选中考虑指定的限定符值（例如，将`account`限定符与标记有相同限定符标签的bean 匹配）。
> 
> 对于自身定义为集合`Map`或数组类型的bean来说，这`@Resource` 是一个很好的解决方案，它通过唯一名称引用特定的集合或数组bean。也就是说，从4.3开始，只要在返回类型签名或集合继承层次结构中保留元素类型信息，就可以`Map`通过Spring的`@Autowired`类型匹配算法匹配和数组类型 `@Bean`。在这种情况下，您可以使用限定符值在相同类型的集合中进行选择，如上一段所述。
> 
> 从4.3开始，`@Autowired`还考虑了自引用注入（即，引用回到当前注入的bean）。请注意，自我注入是一种后备。对其他组件的常规依赖性始终具有优先权。从这个意义上说，自我引用并不参与常规的候选人选择，因此特别是不是主要的。相反，它们总是最低优先级。在实践中，您应该仅使用自引用作为最后的手段（例如，通过bean的事务代理调用同一实例上的其他方法）。考虑在这种情况下将受影响的方法分解为单独的委托bean。或者，您可以使用`@Resource`，它可以通过其唯一名称获取代理回到当前bean。
> 
> `@Autowired`适用于字段，构造函数和多参数方法，允许在参数级别通过限定符注释缩小范围。相比之下，`@Resource` 仅支持具有单个参数的字段和bean属性setter方法。因此，如果注射目标是构造函数或多参数方法，则应该使用限定符。

您可以创建自己的自定义限定符注释。为此，请定义注释并`@Qualifier`在定义中提供注释，如以下示例所示：

```
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Genre {

    String value();
}
```

然后，您可以在自动装配的字段和参数上提供自定义限定符，如以下示例所示：

```
public class MovieRecommender {

    @Autowired
    @Genre("Action")
    private MovieCatalog actionCatalog;

    private MovieCatalog comedyCatalog;

    @Autowired
    public void setComedyCatalog(@Genre("Comedy") MovieCatalog comedyCatalog) {
        this.comedyCatalog = comedyCatalog;
    }

    // ...
}
```

接下来，您可以提供候选bean定义的信息。您可以将`<qualifier/>`标记添加为 标记的子元素，`<bean/>`然后指定`type`和 `value`匹配自定义限定符注释。类型与注释的完全限定类名匹配。或者，为方便起见，如果不存在冲突名称的风险，您可以使用短类名称。以下示例演示了这两种方法：

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="Genre" value="Action"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="example.Genre" value="Comedy"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean id="movieRecommender" class="example.MovieRecommender"/>

</beans>
```

在[类路径扫描和托管组件中](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-classpath-scanning)，您可以看到基于注释的替代方法，即在XML中提供限定符元数据。具体来说，请参阅[使用注释提供限定符元数据](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-scanning-qualifiers)。

在某些情况下，使用没有值的注释可能就足够了。当注释用于更通用的目的并且可以应用于多种不同类型的依赖项时，这可能很有用。例如，您可以提供可在没有Internet连接时搜索的脱机目录。首先，定义简单注释，如以下示例所示：

```
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface Offline {

}
```

然后将注释添加到要自动装配的字段或属性中，如以下示例所示：

```
public class MovieRecommender {

    @Autowired
    @Offline 
    private MovieCatalog offlineCatalog;

    // ...
}
```

|     | 此行添加`@Offline`注释。 |
| --- | ----------------- |

现在bean定义只需要一个限定符`type`，如下例所示：

```
<bean class="example.SimpleMovieCatalog">
    <qualifier type="Offline"/> 
    <!-- inject any dependencies required by this bean -->
</bean>
```

|     | 此元素指定限定符。 |
| --- | --------- |

您还可以定义除简单`value`属性之外或代替简单属性接受命名属性的自定义限定符注释。如果随后在要自动装配的字段或参数上指定了多个属性值，则bean定义必须匹配所有此类属性值才能被视为自动装配候选。例如，请考虑以下注释定义：

```
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface MovieQualifier {

    String genre();

    Format format();
}
```

在这种情况下`Format`是一个枚举，定义如下：

```
public enum Format {
    VHS, DVD, BLURAY
}
```

要自动装配的字段使用自定义限定符进行注释，并包含两个属性的值：`genre`并且`format`，如以下示例所示：

```
public class MovieRecommender {

    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Action")
    private MovieCatalog actionVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.VHS, genre="Comedy")
    private MovieCatalog comedyVhsCatalog;

    @Autowired
    @MovieQualifier(format=Format.DVD, genre="Action")
    private MovieCatalog actionDvdCatalog;

    @Autowired
    @MovieQualifier(format=Format.BLURAY, genre="Comedy")
    private MovieCatalog comedyBluRayCatalog;

    // ...
}
```

最后，bean定义应包含匹配的限定符值。此示例还演示了您可以使用bean元属性而不是 `<qualifier/>`元素。如果可用，则`<qualifier/>`元素及其属性优先，但`<meta/>`如果不存在此类限定符，则自动装配机制将回退到标记内提供的值 ，如以下示例中的最后两个bean定义：

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="MovieQualifier">
            <attribute key="format" value="VHS"/>
            <attribute key="genre" value="Action"/>
        </qualifier>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <qualifier type="MovieQualifier">
            <attribute key="format" value="VHS"/>
            <attribute key="genre" value="Comedy"/>
        </qualifier>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <meta key="format" value="DVD"/>
        <meta key="genre" value="Action"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

    <bean class="example.SimpleMovieCatalog">
        <meta key="format" value="BLURAY"/>
        <meta key="genre" value="Comedy"/>
        <!-- inject any dependencies required by this bean -->
    </bean>

</beans>
```

#### 1.9.5。使用泛型作为自动装配限定符

除了`@Qualifier`注释之外，您还可以使用Java泛型类型作为隐式的限定形式。例如，假设您具有以下配置：

```
@Configuration
public class MyConfiguration {

    @Bean
    public StringStore stringStore() {
        return new StringStore();
    }

    @Bean
    public IntegerStore integerStore() {
        return new IntegerStore();
    }
}
```

假设前面的bean实现了一个通用接口（即`Store<String>`和， `Store<Integer>`），您可以`@Autowire`将`Store`接口和泛型用作限定符，如下例所示：

```java
@Autowired
private Store<String> s1; // <String> qualifier, injects the stringStore bean

@Autowired
private Store<Integer> s2; // <Integer> qualifier, injects the integerStore bean
```

### 1.9.5。使用泛型作为自动装配限定符

除了`@Qualifier`注释之外，您还可以使用Java泛型类型作为隐式的限定形式。例如，假设您具有以下配置：

```java
@Configuration
public class MyConfiguration {

    @Bean
    public StringStore stringStore() {
        return new StringStore();
    }

    @Bean
    public IntegerStore integerStore() {
        return new IntegerStore();
    }
}
```

假设前面的bean实现了一个通用接口（即`Store<String>`和， `Store<Integer>`），您可以`@Autowire`将`Store`接口和泛型用作限定符，如下例所示：

```java
@Autowired
private Store<String> s1; // <String> qualifier, injects the stringStore bean

@Autowired
private Store<Integer> s2; // <Integer> qualifier, injects the integerStore bean
```

通用限定符也适用于自动装配列表，`Map`实例和数组。以下示例自动装配通用`List`：

```java
// Inject all Store beans as long as they have an <Integer> generic
// Store<String> beans will not appear in this list
@Autowired
private List<Store<Integer>> s;
```

### 1.9.6。运用`CustomAutowireConfigurer`

[`CustomAutowireConfigurer`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/beans/factory/annotation/CustomAutowireConfigurer.html) 是一个`BeanFactoryPostProcessor`允许您注册自己的自定义限定符注释类型的，即使它们没有使用Spring的`@Qualifier`注释进行注释。以下示例显示如何使用`CustomAutowireConfigurer`：

```textile
<bean id="customAutowireConfigurer"
        class="org.springframework.beans.factory.annotation.CustomAutowireConfigurer">
    <property name="customQualifierTypes">
        <set>
            <value>example.CustomQualifier</value>
        </set>
    </property>
</bean>
```

通过以下方式`AutowireCandidateResolver`确定autowire候选人：

- `autowire-candidate`每个bean定义的值

- 元素`default-autowire-candidates`上可用的任何模式`<beans/>`

- `@Qualifier`注释的存在以及注册的任何自定义注释`CustomAutowireConfigurer`

当多个bean有资格作为autowire候选者时，“primary”的确定如下：如果候选者中只有一个bean定义具有`primary` 设置为的属性`true`，则选择它。

### 1.9.7。注射用`@Resource`

Spring还通过在字段或bean属性setter方法上使用JSR-250 `@Resource`annotation（`javax.annotation.Resource`）来支持注入。这是Java EE中的常见模式：例如，在JSF管理的bean和JAX-WS端点中。Spring也支持Spring管理对象的这种模式。

`@Resource`采用名称属性。默认情况下，Spring将该值解释为要注入的bean名称。换句话说，它遵循按名称语义，如以下示例所示：

```
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource(name="myMovieFinder") 
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

这条线注入了一个`@Resource`。

如果未明确指定名称，则默认名称是从字段名称或setter方法派生的。如果是字段，则采用字段名称。在setter方法的情况下，它采用bean属性名称。下面的例子将把bean `movieFinder`注入其setter方法：

```
public class SimpleMovieLister {

    private MovieFinder movieFinder;

    @Resource
    public void setMovieFinder(MovieFinder movieFinder) {
        this.movieFinder = movieFinder;
    }
}
```

> 提供注解的名称解析由一个bean的名称 `ApplicationContext`，其中的`CommonAnnotationBeanPostProcessor`知道。如果您[`SimpleJndiBeanFactory`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/jndi/support/SimpleJndiBeanFactory.html) 明确配置Spring，则可以通过JNDI解析名称 。但是，我们建议您依赖于默认行为并使用Spring的JNDI查找功能来保留间接级别。

在专属情况下，`@Resource`不指定明确的名称，以及类似的使用`@Autowired`，`@Resource`发现的主要类型的比赛，而不是一个具体的bean并解决众所周知的解析依存关系：`BeanFactory`， `ApplicationContext`，`ResourceLoader`，`ApplicationEventPublisher`，和`MessageSource` 接口。

因此，在以下示例中，`customerPreferenceDao`字段首先查找名为“customerPreferenceDao”的bean，然后返回到该类型的主要类型匹配 `CustomerPreferenceDao`：

```java
public class MovieRecommender {

    @Resource
    private CustomerPreferenceDao customerPreferenceDao;

    @Resource
    private ApplicationContext context; 

    public MovieRecommender() {
    }

    // ...
}
```

`context`根据已知的可解析依赖类型注入该字段： `ApplicationContext`。

### 1.9.8。使用`@PostConstruct`和`@PreDestroy`

将`CommonAnnotationBeanPostProcessor`不仅承认了`@Resource`注解也是JSR-250的生命周期注解：`javax.annotation.PostConstruct`和 `javax.annotation.PreDestroy`。在Spring 2.5中引入，对这些注释的支持提供了[初始化回调](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean)和 [销毁回调中](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean)描述的生命周期回调机制的替代 [方法](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean)。如果 `CommonAnnotationBeanPostProcessor`在Spring中注册`ApplicationContext`，则在生命周期的同一点调用带有这些注释之一的方法，作为相应的Spring生命周期接口方法或显式声明的回调方法。在以下示例中，缓存在初始化时预填充并在销毁时清除：

```
public class CachingMovieLister {

    @PostConstruct
    public void populateMovieCache() {
        // populates the movie cache upon initialization...
    }

    @PreDestroy
    public void clearMovieCache() {
        // clears the movie cache upon destruction...
    }
}
```

有关组合各种生命周期机制的效果的详细信息，请参阅 [组合生命周期机制](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-combined-effects)。

> 例如`@Resource`，`@PostConstruct`和`@PreDestroy`注释类型是JDK 6到8的标准Java库的一部分。但是，整个`javax.annotation` 包与JDK 9中的核心Java模块分离，最终在JDK 11中删除。如果需要，`javax.annotation-api`工件需要是现在通过Maven Central获得，只需像任何其他库一样添加到应用程序的类路径中。

---

1.10 1.11再补

---

## 1.12 基于Java的容器配置

本节介绍如何在Java代码中使用注释来配置Spring容器。它包括以下主题：

- [基本概念：`@Bean`和`@Configuration`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-basic-concepts)

- [使用。实例化Spring容器 `AnnotationConfigApplicationContext`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-instantiating-container)

- [使用`@Bean`注释](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-bean-annotation)

- [使用`@Configuration`注释](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-configuration-annotation)

- [编写基于Java的配置](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-composing-configuration-classes)

- [Bean定义配置文件](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-definition-profiles)

- [`PropertySource` 抽象化](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-property-source-abstraction)

- [运用 `@PropertySource`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-using-propertysource)

- [占位符决议在声明中](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-placeholder-resolution-in-statements)

### 1.12.1。基本概念：`@Bean`和`@Configuration`

Spring的新Java配置支持中的中心工件是 `@Configuration`注释类和`@Bean`注释方法。

该`@Bean`注释被用于指示一个方法实例，配置和初始化为通过Spring IoC容器进行管理的新对象。对于那些熟悉Spring的`<beans/>`XML配置的人来说，`@Bean`注释与`<bean/>`元素扮演的角色相同。你可以`@Bean`在任何Spring中使用-annotated方法 `@Component`。但是，它们最常用于`@Configuration `   bean类。

对类进行注释`@Configuration`表明其主要目的是作为bean定义的来源。此外，`@Configuration`类允许通过调用`@Bean`同一类中的其他方法来定义bean间依赖关系。最简单的`@Configuration`类如下：

```java
@Configuration
public class AppConfig {

    @Bean
    public MyService myService() {
        return new MyServiceImpl();
    }
}
```

上面的`AppConfig`类等效于以下Spring `<beans/>`XML：

```
<beans>
    <bean id="myService" class="com.acme.services.MyServiceImpl"/>
</beans>
```

#### 完整@Configuration vs“lite”@Bean模式？

当`@Bean`在未注释的类中声明方法时 `@Configuration`，它们被称为以“精简”模式处理。在一个`@Component`或甚至在一个普通的旧类中声明的Bean方法被认为是“精简”，包含类的主要目的不同，并且`@Bean`方法在那里是一种奖励。例如，服务组件可以通过`@Bean`每个适用组件类的附加方法将管理视图公开给容器。在这种情况下，`@Bean`方法是通用的工厂方法机制。

与full不同`@Configuration`，lite `@Bean`方法不能声明bean间依赖关系。相反，它们对其包含组件的内部状态进行操作，并且可选地，对它们可以声明的参数进行操作。`@Bean`因此，这种方法不应该引用其他 `@Bean`方法。每个这样的方法实际上只是特定bean引用的工厂方法，没有任何特殊的运行时语义。这里的积极副作用是不必在运行时应用CGLIB子类，因此在类设计方面没有限制（也就是说，包含类可能是`final`等等）。

在常见的场景中，`@Bean`方法将在`@Configuration`类中声明，确保始终使用“完整”模式，并因此将交叉方法引用重定向到容器的生命周期管理。这可以防止`@Bean`通过常规Java调用意外地调用相同的 方法，这有助于减少在“精简”模式下操作时难以跟踪的细微错误。

的`@Bean`和`@Configuration`注解的深度在以下章节中讨论。首先，我们将介绍使用基于Java的配置创建弹簧容器的各种方法。

> `@Bean`和`@Configuration`注解的深度在以下章节中讨论。首先，我们将介绍使用基于Java的配置创建弹簧容器的各种方法。

### 1.12.2 使用实例化Spring容器`AnnotationConfigApplicationContext`

以下部分`AnnotationConfigApplicationContext`介绍了在Spring 3.0中引入的Spring。这种通用`ApplicationContext`实现不仅能够接受`@Configuration`类作为输入，还能接受 `@Component`使用JSR-330元数据注释的普通类和类。

当`@Configuration`提供类作为输入时，`@Configuration`类本身被注册为bean定义，并且`@Bean`类中的所有声明的方法也被注册为bean定义。

当`@Component`提供JSR-330类时，它们被注册为bean定义，并且假定DI元数据例如`@Autowired`或`@Inject`在必要时在这些类中使用。

简单的施工

与实例化a时Spring XML文件用作输入的方式大致相同 `ClassPathXmlApplicationContext`，可以`@Configuration`在实例化时使用类作为输入`AnnotationConfigApplicationContext`。这允许完全无XML使用Spring容器，如以下示例所示：

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

如前所述，`AnnotationConfigApplicationContext`并不仅限于使用`@Configuration`类。`@Component`可以将任何或JSR-330带注释的类作为输入提供给构造函数，如以下示例所示：

```java
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(MyServiceImpl.class, Dependency1.class, Dependency2.class);
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

前面的例子中假定`MyServiceImpl`，`Dependency1`以及`Dependency2`使用Spring依赖注入注解，例如`@Autowired`。

##### 用编程方式构建容器 `register(Class<?>…​)`

您可以`AnnotationConfigApplicationContext`使用no-arg构造函数实例化一个，然后使用该`register()`方法对其进行配置。这种方法在以编程方式构建时特别有用`AnnotationConfigApplicationContext`。以下示例显示了如何执行此操作：

```java
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class, OtherConfig.class);
    ctx.register(AdditionalConfig.class);
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
    myService.doStuff();
}
```

##### 使用启用组件扫描 `scan(String…​)`

要启用组件扫描，您可以`@Configuration`按如下方式注释您的类：

```
@Configuration
@ComponentScan(basePackages = "com.acme") 
public class AppConfig  {
    ...
}
```

此注释可启用组件扫描。

有经验的Spring用户可能熟悉与Spring `context:`命名空间等效的XML声明，如下例所示：

```
<beans>
    <context:component-scan base-package="com.acme"/>
</beans>
```

在前面的示例中，`com.acme`扫描包以查找任何已 `@Component`注释的类，并将这些类注册为容器中的Spring bean定义。`AnnotationConfigApplicationContext`公开 `scan(String…​)`方法以允许相同的组件扫描功能，如以下示例所示：

```
public static void main(String[] args) {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.scan("com.acme");
    ctx.refresh();
    MyService myService = ctx.getBean(MyService.class);
}
```

> 请记住，`@Configuration`类是[元注释](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-meta-annotations) 的`@Component`，因此它们是组件扫描的候选者。在前面的示例中，假设`AppConfig`在`com.acme`包（或下面的任何包）中声明了它，它在调用期间被拾取`scan()`。之后`refresh()`，它的所有`@Bean` 方法都被处理并在容器中注册为bean定义。

##### 支持Web应用程序 `AnnotationConfigWebApplicationContext`

可用的`WebApplicationContext`变体。在配置Spring servlet侦听器，Spring MVC等时 ，可以使用此实现。以下代码段配置典型的Spring MVC Web应用程序（请注意context-param和init-param的使用）：`AnnotationConfigApplicationContext``AnnotationConfigWebApplicationContext``ContextLoaderListener``DispatcherServlet``web.xml``contextClass`

```yaml
    <!-- Configure ContextLoaderListener to use AnnotationConfigWebApplicationContext
        instead of the default XmlWebApplicationContext -->
    <context-param>
        <param-name>contextClass</param-name>
        <param-value>
            org.springframework.web.context.support.AnnotationConfigWebApplicationContext
        </param-value>
    </context-param>

    <!-- Configuration locations must consist of one or more comma- or space-delimited
        fully-qualified @Configuration classes. Fully-qualified packages may also be
        specified for component-scanning -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>com.acme.AppConfig</param-value>
    </context-param>

    <!-- Bootstrap the root application context as usual using ContextLoaderListener -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- Declare a Spring MVC DispatcherServlet as usual -->
    <servlet>
        <servlet-name>dispatcher</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <!-- Configure DispatcherServlet to use AnnotationConfigWebApplicationContext
            instead of the default XmlWebApplicationContext -->
        <init-param>
            <param-name>contextClass</param-name>
            <param-value>
                org.springframework.web.context.support.AnnotationConfigWebApplicationContext
            </param-value>
        </init-param>
        <!-- Again, config locations must consist of one or more comma- or space-delimited
            and fully-qualified @Configuration classes -->
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>com.acme.web.MvcConfig</param-value>
        </init-param>
    </servlet>

    <!-- map all requests for /app/* to the dispatcher servlet -->
    <servlet-mapping>
        <servlet-name>dispatcher</servlet-name>
        <url-pattern>/app/*</url-pattern>
    </servlet-mapping>
</web-app>
```

### 1.12.3。使用`@Bean`注释

`@Bean`是方法级注释和XML `<bean/>`元素的直接模拟。注释支持一些提供的属性`<bean/>`，例如：* [init-method](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-initializingbean) * [destroy-method](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-lifecycle-disposablebean) * [autowiring](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-autowire) * `name`。

您可以在带`@Bean`注释的类`@Configuration`或带 注释的类中使用注释`@Component`。

##### 声明一个Bean

要声明bean，可以使用注释注释方法`@Bean`。您可以使用此方法在`ApplicationContext`指定为方法的返回值的类型中注册bean定义。默认情况下，bean名称与方法名称相同。以下示例显示了`@Bean`方法声明：

```
@Configuration
public class AppConfig {

    @Bean
    public TransferServiceImpl transferService() {
        return new TransferServiceImpl();
    }
}
```

上述配置与以下Spring XML完全等效：

```
<beans>
    <bean id="transferService" class="com.acme.TransferServiceImpl"/>
</beans>
```

这两个声明都将一个名为`transferService`available 的bean命名为`ApplicationContext`绑定到类型的对象实例，`TransferServiceImpl`如下图所示：

transferService  - > com.acme.TransferServiceImpl

您还可以`@Bean`使用接口（或基类）返回类型声明您的方法，如以下示例所示：

```
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl();
    }
}
```

但是，这会将高级类型预测的可见性限制为指定的接口类型（`TransferService`）。然后，`TransferServiceImpl`只有容器已知的完整类型（）一次，已经实例化了受影响的单例bean。非延迟单例bean根据其声明顺序进行实例化，因此您可能会看到不同的类型匹配结果，具体取决于另一个组件何时尝试通过非声明类型进行匹配（例如`@Autowired TransferServiceImpl`，只有`transferService`在实例化bean之后才会解析）。

> 如果您始终通过声明的服务接口引用您的类型，则您的 `@Bean`返回类型可以安全地加入该设计决策。但是，对于实现多个接口的组件或可能由其实现类型引用的组件，更可能声明可能的最具体的返回类型（至少与引用您的bean的注入点所需的具体相同）。

##### Bean依赖项

带`@Bean`注释的方法可以有任意数量的参数来描述构建该bean所需的依赖关系。例如，如果我们`TransferService` 需要a `AccountRepository`，我们可以使用方法参数来实现该依赖关系，如下例所示：

```
@Configuration
public class AppConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}
```

解析机制与基于构造函数的依赖注入非常相似。[有关](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-constructor-injection)详细信息，请参阅[相关部分](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-constructor-injection)。

##### 接收生命周期回调

使用`@Bean`注释定义的任何类都支持常规生命周期回调，并且可以使用JSR-250中的注释`@PostConstruct`和`@PreDestroy`注释。有关更多详细信息，请参阅 [JSR-250注释](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-postconstruct-and-predestroy-annotations)。

完全支持常规的Spring [生命周期](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-nature)回调。如果bean实现`InitializingBean`，`DisposableBean`或者`Lifecycle`它们各自的方法由容器调用。

还完全支持标准`*Aware`接口集（例如[BeanFactoryAware](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-beanfactory)， [BeanNameAware](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-aware)， [MessageSourceAware](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#context-functionality-messagesource)， [ApplicationContextAware](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-aware)等）。

该`@Bean`注释支持指定任意初始化和销毁回调方法，就像春天XML的`init-method`，并`destroy-method`在属性上的`bean`元素，如下例所示：

```
public class BeanOne {

    public void init() {
        // initialization logic
    }
}

public class BeanTwo {

    public void cleanup() {
        // destruction logic
    }
}

@Configuration
public class AppConfig {

    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }

    @Bean(destroyMethod = "cleanup")
    public BeanTwo beanTwo() {
        return new BeanTwo();
    }
}
```

> 默认情况下，使用Java配置定义的具有public `close`或`shutdown` method的bean 会自动使用销毁回调登记。如果您有公共 `close`或`shutdown`方法，并且您不希望在容器关闭时调用它，则可以添加`@Bean(destroyMethod="")`到bean定义以禁用默认`(inferred)`模式。
> 
> 对于使用JNDI获取的资源，您可能希望默认执行此操作，因为其生命周期在应用程序之外进行管理。特别是，确保始终为a执行此操作`DataSource`，因为已知它在Java EE应用程序服务器上存在问题。
> 
> 以下示例显示如何防止自动销毁回调 `DataSource`：
> 
> ```
> @Bean(destroyMethod="")
> public DataSource dataSource() throws NamingException {
>     return (DataSource) jndiTemplate.lookup("MyDS");
> }
> ```
> 
> 此外，使用`@Bean`方法，您通常使用编程JNDI查找，通过使用Spring `JndiTemplate`或`JndiLocatorDelegate`帮助程序或直接JNDI `InitialContext`用法但不使用`JndiObjectFactoryBean`变量（这将强制您将返回类型声明为`FactoryBean`类型而不是实际目标类型，使得更难以用于其他`@Bean`打算在此处引用所提供资源的方法中的交叉引用调用。

在`BeanOne`前面注释中的示例的情况下，`init()` 在构造期间直接调用该方法同样有效，如以下示例所示：

```
@Configuration
public class AppConfig {

    @Bean
    public BeanOne beanOne() {
        BeanOne beanOne = new BeanOne();
        beanOne.init();
        return beanOne;
    }

    // ...
}
```

> 当您直接使用Java工作时，您可以使用对象执行任何您喜欢的操作，并且不必总是依赖于容器生命周期。

##### 指定Bean范围

Spring包含`@Scope`注释，以便您可以指定bean的范围。

###### 使用`@Scope`注释

您可以指定使用`@Bean`注释定义的bean 应具有特定范围。您可以使用[Bean Scopes](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-scopes)部分中指定的任何标准作用域 。

默认范围是`singleton`，但您可以使用`@Scope`注释覆盖它，如以下示例所示：

```java
@Configuration
public class MyConfiguration {

    @Bean
    @Scope("prototype")
    public Encryptor encryptor() {
        // ...
    }
}
```

###### `@Scope` 和 `scoped-proxy`

Spring提供了一种通过[作用域代理](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-scopes-other-injection)处理作用域依赖项的便捷方法 。使用XML配置时创建此类代理的最简单方法是`<aop:scoped-proxy/>`元素。使用`@Scope`注释在Java中配置bean 提供了对该`proxyMode`属性的等效支持。默认值为no proxy（`ScopedProxyMode.NO`），但您可以指定`ScopedProxyMode.TARGET_CLASS`或`ScopedProxyMode.INTERFACES`。

如果将scoped代理示例从XML参考文档（请参阅[范围代理](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-scopes-other-injection)）移植 到`@Bean`使用Java，它类似于以下内容：

```java
// an HTTP Session-scoped bean exposed as a proxy
@Bean
@SessionScope
public UserPreferences userPreferences() {
    return new UserPreferences();
}

@Bean
public Service userService() {
    UserService service = new SimpleUserService();
    // a reference to the proxied userPreferences bean
    service.setUserPreferences(userPreferences());
    return service;
}
```

##### 自定义Bean命名

默认情况下，配置类使用`@Bean`方法的名称作为结果bean的名称。但是，可以使用`name`属性覆盖此功能，如以下示例所示：

```
@Configuration
public class AppConfig {

    @Bean(name = "myThing")
    public Thing thing() {
        return new Thing();
    }
}
```

##### Bean Aliasing 别名

正如[Naming Beans中](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-beanname)所讨论的，有时需要为单个bean提供多个名称，也称为bean别名。 为此目的`name`，`@Bean`注释的属性接受String数组。以下示例显示如何为bean设置多个别名：

```java
@Configuration
public class AppConfig {

    @Bean({"dataSource", "subsystemA-dataSource", "subsystemB-dataSource"})
    public DataSource dataSource() {
        // instantiate, configure and return DataSource bean...
    }
}
```

##### Bean描述

有时，提供更详细的bean文本描述会很有帮助。当bean（可能通过JMX）进行监视时，这可能特别有用。

要向a添加描述`@Bean`，可以使用 [`@Description`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/context/annotation/Description.html) 注释，如以下示例所示：

```
@Configuration
public class AppConfig {

    @Bean
    @Description("Provides a basic example of a bean")
    public Thing thing() {
        return new Thing();
    }
}
```

### 1.12.4。使用`@Configuration`注释

`@Configuration`是一个类级别的注释，指示对象是bean定义的来源。`@Configuration`classes通过公共`@Bean`注释方法声明bean 。`@Bean`对`@Configuration`类上的方法的调用也可用于定义bean间依赖项。请参阅[基本概念：`@Bean`和`@Configuration`](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-basic-concepts)一般性介绍。

##### 注入bean间依赖关系

当bean彼此依赖时，表达该依赖关系就像让一个bean方法调用另一个bean一样简单，如下例所示：

```java
@Configuration
public class AppConfig {

    @Bean
    public BeanOne beanOne() {
        return new BeanOne(beanTwo());
    }

    @Bean
    public BeanTwo beanTwo() {
        return new BeanTwo();
    }
}
```

在前面的示例中，`beanOne`接收对`beanTwo`构造函数注入的引用。

|     | 这种声明bean间依赖关系的`@Bean`方法只有在`@Configuration`类中声明方法时才有效。您不能使用普通`@Component`类声明bean间依赖项。 |
| --- | ------------------------------------------------------------------------------------ |
|     |                                                                                      |

> 这种声明bean间依赖关系的`@Bean`方法只有在`@Configuration`类中声明方法时才有效。您不能使用普通`@Component`类声明bean间依赖项。

##### 查找方法注入

如前所述，[查找方法注入](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-factory-method-injection)是一项很少使用的高级功能。在单例范围的bean依赖于原型范围的bean的情况下，它很有用。将Java用于此类配置提供了实现此模式的自然方法。以下示例显示如何使用查找方法注入：

```
public abstract class CommandManager {
    public Object process(Object commandState) {
        // grab a new instance of the appropriate Command interface
        Command command = createCommand();
        // set the state on the (hopefully brand new) Command instance
        command.setState(commandState);
        return command.execute();
    }

    // okay... but where is the implementation of this method?
    protected abstract Command createCommand();
}
```

通过使用Java配置，您可以创建一个子类，`CommandManager`其中抽象`createCommand()`方法被覆盖，以便查找新的（原型）命令对象。以下示例显示了如何执行此操作：

```
@Bean
@Scope("prototype")
public AsyncCommand asyncCommand() {
    AsyncCommand command = new AsyncCommand();
    // inject dependencies here as required
    return command;
}

@Bean
public CommandManager commandManager() {
    // return new anonymous implementation of CommandManager with createCommand()
    // overridden to return a new prototype Command object
    return new CommandManager() {
        protected Command createCommand() {
            return asyncCommand();
        }
    }
}
```

##### 有关基于Java的配置如何在内部工作的更多信息

请考虑以下示例，该示例显示了`@Bean`两次调用的带注释的方法：

```
@Configuration
public class AppConfig {

    @Bean
    public ClientService clientService1() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientService clientService2() {
        ClientServiceImpl clientService = new ClientServiceImpl();
        clientService.setClientDao(clientDao());
        return clientService;
    }

    @Bean
    public ClientDao clientDao() {
        return new ClientDaoImpl();
    }
}
```

`clientDao()`被称为一次进入`clientService1()`和进入一次`clientService2()`。由于此方法创建了一个新实例`ClientDaoImpl`并将其返回，因此通常需要两个实例（每个服务一个）。这肯定会有问题：在Spring中，实例化的bean `singleton`默认具有范围。这就是魔术的用武之地：所有`@Configuration`类都在启动时被子类化`CGLIB`。在子类中，子方法在调用父方法并创建新实例之前，首先检查容器是否有任何缓存（作用域）bean。

|     | 根据bean的范围，行为可能会有所不同。我们在这里谈论单身人士。 |
| --- | -------------------------------- |

> 从Spring 3.2开始，不再需要将CGLIB添加到类路径中，因为CGLIB类已经重新打包`org.springframework.cglib`并直接包含在spring-core JAR中。

> 由于CGLIB在启动时动态添加功能，因此存在一些限制。特别是，配置类不能是最终的。但是，从4.3开始，配置类允许使用任何构造函数，包括使用 `@Autowired`默认注入的单个非默认构造函数声明。

> 如果您希望避免任何CGLIB强加的限制，请考虑`@Bean` 在非`@Configuration`类上声明您的方法（例如，在普通`@Component`类上）。`@Bean`然后拦截方法之间的跨方法调用，因此您必须完全依赖于构造函数或方法级别的依赖注入。

### 1.12.5。编写基于Java的配置

```java
@Configuration
public class ConfigA {

    @Bean
    public A a() {
        return new A();
    }
}

@Configuration
@Import(ConfigA.class)
public class ConfigB {

    @Bean
    public B b() {
        return new B();
    }
}
```

现在，不需要同时指定`ConfigA.class`和`ConfigB.class`实例化上下文，只`ConfigB`需要显式提供，如下例所示：

```
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(ConfigB.class);

    // now both beans A and B will be available...
    A a = ctx.getBean(A.class);
    B b = ctx.getBean(B.class);
}
```

这种方法简化了容器实例化，因为只需要处理一个类，而不是要求您`@Configuration`在构造期间记住可能大量的 类。

> 从Spring Framework 4.2开始，`@Import`还支持引用常规组件类，类似于`AnnotationConfigApplicationContext.register`方法。如果要避免组件扫描，这一点特别有用，可以使用一些配置类作为明确定义所有组件的入口点。

##### 注入对导入`@Bean`定义的依赖性

前面的例子有效，但很简单。在大多数实际情况中，bean跨配置类彼此依赖。使用XML时，这不是问题，因为不涉及编译器，并且您可以声明 `ref="someBean"`并信任Spring在容器初始化期间解决它。使用`@Configuration`类时，Java编译器会对配置模型施加约束，因为对其他bean的引用必须是有效的Java语法。

幸运的是，解决这个问题很简单。正如[我们已经讨论过的](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-java-dependencies)，一个`@Bean`方法可以有任意数量的参数来描述bean的依赖关系。考虑以下更多真实场景​​，其中包含几个`@Configuration` 类，每个类都依赖于其他类中声明的bean：

```java
@Configuration
public class ServiceConfig {

    @Bean
    public TransferService transferService(AccountRepository accountRepository) {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    @Bean
    public AccountRepository accountRepository(DataSource dataSource) {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

> 还有另一种方法可以达到相同的效果。请记住，`@Configuration`类最终只是容器中的另一个bean：这意味着它们可以利用 `@Autowired`和`@Value`注入以及与任何其他bean相同的其他功能。

> 确保以这种方式注入的依赖项只是最简单的类型。`@Configuration` 在上下文初始化期间很早就处理了类，并且强制以这种方式注入依赖项可能会导致意外的早期初始化。尽可能采用基于参数的注入，如前面的示例所示。

> 另外，要特别注意`BeanPostProcessor`和`BeanFactoryPostProcessor`定义`@Bean`。这些通常应该声明为`static @Bean`方法，而不是触发其包含配置类的实例化。否则，`@Autowired`而`@Value`不要在配置类本身的工作，因为它是被作为一个bean实例创建为时尚

以下示例显示了如何将一个bean自动连接到另一个bean：

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private AccountRepository accountRepository;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(accountRepository);
    }
}

@Configuration
public class RepositoryConfig {

    private final DataSource dataSource;

    @Autowired
    public RepositoryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }
}

@Configuration
@Import({ServiceConfig.class, RepositoryConfig.class})
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return new DataSource
    }
}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    // everything wires up across configuration classes...
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

> `@Configuration`仅在Spring Framework 4.3中支持类中的 构造函数注入。另请注意，无需指定`@Autowired`目标bean是否仅定义了一个构造函数。在前面的示例中，构造函数`@Autowired`上没有必要`RepositoryConfig`。

完全符合条件的进口豆类，便于导航

在前面的场景中，使用`@Autowired`效果很好并提供了所需的模块性，但确定声明自动装配的bean定义的确切位置仍然有些模棱两可。例如，作为开发人员`ServiceConfig`，您如何确切地知道`@Autowired AccountRepository`bean的声明位置？它在代码中并不明确，这可能就好了。请记住， [Spring Tool Suite](https://spring.io/tools/sts)提供的工具可以呈现图形，显示所有内容的连线方式，这可能就是您所需要的。此外，您的Java IDE可以轻松找到该`AccountRepository`类型的所有声明和用法，并快速显示`@Bean`返回该类型的方法的位置。

如果这种歧义是不可接受的，并且您希望从IDE中直接从一个`@Configuration`类导航到另一个类，请考虑自行装配配置类本身。以下示例显示了如何执行此操作：

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        // navigate 'through' the config class to the @Bean method!
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}
```

在前面的情况中，`AccountRepository`定义的位置是完全明确的。但是，`ServiceConfig`现在紧紧联系在一起`RepositoryConfig`。这是权衡。通过使用基于接口的或基于`@Configuration`类的抽象类，可以在某种程度上减轻这种紧密耦合。请考虑以下示例：

```java
@Configuration
public class ServiceConfig {

    @Autowired
    private RepositoryConfig repositoryConfig;

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl(repositoryConfig.accountRepository());
    }
}

@Configuration
public interface RepositoryConfig {

    @Bean
    AccountRepository accountRepository();
}

@Configuration
public class DefaultRepositoryConfig implements RepositoryConfig {

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(...);
    }
}

@Configuration
@Import({ServiceConfig.class, DefaultRepositoryConfig.class})  // import the concrete config!
public class SystemTestConfig {

    @Bean
    public DataSource dataSource() {
        // return DataSource
    }

}

public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(SystemTestConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    transferService.transfer(100.00, "A123", "C456");
}
```

现在`ServiceConfig`与具体的松散耦合 `DefaultRepositoryConfig`，内置的IDE工具仍然有用：您可以轻松获得实现的类型层次结构`RepositoryConfig`。通过这种方式，导航`@Configuration`类及其依赖关系与导航基于接口的代码的常规过程没有什么不同。

> 如果要影响某些bean的启动创建顺序，可以考虑将它们中的一些声明为`@Lazy`（用于在第一次访问时创建而不是在启动时）或`@DependsOn`某些其他bean（确保在当前bean之前创建特定的其他bean，超出后者的直接依赖意味着什么）。

##### 有条件地包括`@Configuration`类或`@Bean`方法

基于某些任意系统状态，有条件地启用或禁用完整`@Configuration`类或甚至单个`@Bean`方法通常很有用。一个常见的例子是`@Profile`只有在Spring中启用了特定的配置文件时才使用注释激活bean `Environment`（ 有关详细[信息](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-definition-profiles)，请参阅[Bean定义配置文件](https://docs.spring.io/spring/docs/5.1.7.RELEASE/spring-framework-reference/core.html#beans-definition-profiles)）。

该`@Profile`注释是通过使用一种称为更灵活的注释实际执行[`@Conditional`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/context/annotation/Conditional.html)。该`@Conditional`注释指示特定`org.springframework.context.annotation.Condition`前应谘询的实施`@Bean`是注册。

`Condition`接口的实现提供了一个`matches(…​)` 返回`true`或的方法`false`。例如，以下清单显示了`Condition`用于的实际 实现`@Profile`：

```java
@Override
public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
    if (context.getEnvironment() != null) {
        // Read the @Profile annotation attributes
        MultiValueMap<String, Object> attrs = metadata.getAllAnnotationAttributes(Profile.class.getName());
        if (attrs != null) {
            for (Object value : attrs.get("value")) {
                if (context.getEnvironment().acceptsProfiles(((String[]) value))) {
                    return true;
                }
            }
            return false;
        }
    }
    return true;
}
```

有关[`@Conditional`](https://docs.spring.io/spring-framework/docs/5.1.7.RELEASE/javadoc-api/org/springframework/context/annotation/Conditional.html) 更多详细信息，请参阅javadoc。

##### 结合Java和XML配置

Spring的`@Configuration`类支持并非旨在成为Spring XML的100％完全替代品。某些工具（如Spring XML命名空间）仍然是配置容器的理想方法。在XML方便或必要的情况下，您可以选择：例如，通过使用“以XML为中心”的方式实例化容器`ClassPathXmlApplicationContext`，或者通过使用`AnnotationConfigApplicationContext`和`@ImportResource`注释以“以Java为中心”的方式实例化它。 根据需要导入XML。

###### 以XML为中心的`@Configuration`类的使用

最好从XML引导Spring容器并`@Configuration`以ad-hoc方式包含 类。例如，在使用Spring XML的大型现有代码库中，可以`@Configuration`根据需要更轻松地创建类，并将其包含在现有XML文件中。在本节的后面部分，我们将介绍`@Configuration`在这种“以XML为中心”的情况下使用类的选项。

将`@Configuration`类声明为普通的Spring `<bean/>`元素

请记住，`@Configuration`类最终是容器中的bean定义。在本系列示例中，我们创建了一个`@Configuration`名为的类，`AppConfig`并将其`system-test-config.xml`作为`<bean/>`定义包含在其中。因为 `<context:annotation-config/>`已打开，容器会识别`@Configuration`注释并 正确处理`@Bean`声明的方法`AppConfig`。

以下示例显示了Java中的普通配置类：

```java
@Configuration
public class AppConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public AccountRepository accountRepository() {
        return new JdbcAccountRepository(dataSource);
    }

    @Bean
    public TransferService transferService() {
        return new TransferService(accountRepository());
    }
}
```

以下示例显示了示例`system-test-config.xml`文件的一部分：

```
<beans>
    <!-- enable processing of annotations such as @Autowired and @Configuration -->
    <context:annotation-config/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="com.acme.AppConfig"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

以下示例显示了一个可能的`jdbc.properties`文件：

jdbc.url = JDBC：HSQLDB：HSQL：//本地主机/ XDB jdbc.username = SA jdbc.password =

```
public static void main(String[] args) {
    ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:/com/acme/system-test-config.xml");
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```

|     | 在`system-test-config.xml`文件中，`AppConfig`  `<bean/>`不声明`id` 元素。虽然这样做是可以接受的，但是没有必要，因为没有其他bean引用它，并且不太可能通过名称从容器中明确地获取它。类似地，`DataSource`bean只是按类型自动装配，因此`id` 不严格要求显式bean 。 |
| --- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |

使用<context：component-scan />来获取`@Configuration`类

因为`@Configuration`带有元注释`@Component`，注释`@Configuration`类自动成为组件扫描的候选者。使用与前一个示例中描述的相同的方案，我们可以重新定义`system-test-config.xml`以利用组件扫描。请注意，在这种情况下，我们不需要显式声明`<context:annotation-config/>`，因为`<context:component-scan/>`启用相同的功能。

以下示例显示了已修改的`system-test-config.xml`文件：

```
<beans>
    <!-- picks up and registers AppConfig as a bean definition -->
    <context:component-scan base-package="com.acme"/>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>

    <bean class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="url" value="${jdbc.url}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
    </bean>
</beans>
```

###### `@Configuration` 以类为中心的XML使用 `@ImportResource`

在`@Configuration`类是配置容器的主要机制的应用程序中，仍然可能需要使用至少一些XML。在这些场景中，您可以`@ImportResource`根据需要使用和定义尽可能多的XML。这样做可以实现“以Java为中心”的方法来配置容器并将XML保持在最低限度。以下示例（包括配置类，定义bean的XML文件，属性文件和`main`类）显示了如何使用`@ImportResource`注释来实现根据需要使用XML的“以Java为中心”的配置：

```
@Configuration
@ImportResource("classpath:/com/acme/properties-config.xml")
public class AppConfig {

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return new DriverManagerDataSource(url, username, password);
    }
}
```

```
properties-config.xml
<beans>
    <context:property-placeholder location="classpath:/com/acme/jdbc.properties"/>
</beans>
```

jdbc.properties jdbc.url = JDBC：HSQLDB：HSQL：//本地主机/ XDB jdbc.username = SA jdbc.password =

```
public static void main(String[] args) {
    ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
    TransferService transferService = ctx.getBean(TransferService.class);
    // ...
}
```

1.13后面再更新
