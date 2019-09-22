## Maven

[Maven](USER_CANCEL)项目对象模型(POM)，可以通过一小段描述信息来管理项目的构建，报告和文档的[项目管理工具](USER_CANCEL)软件。

![img](https://qqadapt.qpic.cn/txdocpic/0/4b77a433220eab6b361c95a558bd5d04/0)

Maven 除了以程序构建能力为特色之外，还提供高级项目管理工具。由于 Maven 的缺省构建规则有较高的可重用性，所以常常用两三行 Maven 构建脚本就可以构建简单的项目。由于 Maven 的面向项目的方法，许多 Apache Jakarta 项目发文时使用 Maven，而且公司项目采用 Maven 的比例在持续增长。

Maven这个单词来自于意第绪语（犹太语），意为知识的积累，最初在Jakata Turbine项目中用来简化构建过程。当时有一些项目（有各自Ant build文件），仅有细微的差别，而JAR文件都由[CVS](USER_CANCEL)来维护。于是希望有一种标准化的方式构建项目，一个清晰的方式定义项目的组成，一个容易的方式发布项目的信息，以及一种简单的方式在多个项目中共享JARs。

### maven的优点

1．依赖的管理

Maven是如何找jar包的？



![img](https://qqadapt.qpic.cn/txdocpic/0/632f86f55874dd106df3ed3c4d19d7d6/0)



说白了，其实就是对jar 包的管理并给出坐标的过程。

2．一键构建

什么是构建？

指的是项目从编译-----测试-----运行----打包-------安装整个过程都交给maven进行管理，这个过程称为构建。

![img](https://qqadapt.qpic.cn/txdocpic/0/589576c2093eb237b88adab6e63a742c/0)

### Maven命令

1．clean

clean是maven工程的清理命令，执行

clean会删除target目录及其目录下所有内容

2．Compile

compile是maven工程的编译命令，作用是将src/main/java下的java源文件编译为class文件并输出到target下的classes目录下。

cmd进入命令状态，执行mvn compile，如下图提示成功：

![img](https://qqadapt.qpic.cn/txdocpic/0/8ea1632ca52df1bb2257c1daa5627eff/0)

查看 target目录classes下，class文件已生成，编译完成

![img](https://qqadapt.qpic.cn/txdocpic/0/708ea1d4c325096c8078a50ebb039ff3/0)

3．test

test是maven工程的测试命令 mvn test,会执行src/test/java下的单元测试类。

cmd执行mvn test执行src/test/java下单元测试类,下图为测试结果,运行1个测试用例,全部成功。

![img](https://qqadapt.qpic.cn/txdocpic/0/6783cc4e3c62b4c9b8b2f4b1107d3f4e/0)

4．package

package是maven工程的打包命令, 对于java工程执行package打成jar包,对于web工程打成war包

工程目录下执行 mvn

package

![img](https://qqadapt.qpic.cn/txdocpic/0/5cb5c795c0f221483d82c36507558df3/0)

5．Install

install是maven工程的安装命令，执行install将maven打成jar包或war包发布到本地仓库

![img](https://qqadapt.qpic.cn/txdocpic/0/fab0dddea869482465416745e96d5a2e/0)



从运行结果中，可以看出：

当后面的命令执行时，前面的操作过程也都会自动执行

在cmd中的命令：

a. mvn eclipse:clean 清除Project中以前的编译的东西，重新再来

b. mvn eclipse:eclipse 开始编译Maven的Project

在eclipse中的操作：

a. 选中Maven Project 右击 在Run As中选择Maven clean

b. 在Myeclipse中，Project—Clean 开始编译

c. 选中Maven Project 右击 在Run As中选择Maven install

执行完这几步，如果没发生异常，会在project里生成一个target文件夹，这个文件夹里的东西，就是Maven打包发布的东西。



```
mvn archetype:generate 创建Maven项目
mvn compile 编译源代码
mvn deploy 发布项目
mvn test-compile 编译测试源代码
mvn test 运行应用程序中的单元测试
mvn site 生成项目相关信息的网站
mvn clean 清除项目目录中的生成结果
mvn package 根据项目生成的jar
mvn install 在本地Repository中安装jar
mvn eclipse:eclipse 生成eclipse项目文件
mvnjetty:run 启动jetty服务
mvntomcat:run 启动tomcat服务
mvn clean package -Dmaven.test.skip=true:清除以前的包后重新打包，跳过测试类
```







### 三套生命周期

Maven对项目构建过程分为三套相互独立的生命周期，请注意这里说的是“三套”，而且“相互独立”，这三套生命周期分别是：



\1.        Clean Lifecycle: 在进行真正的构建之前进行一些清理工作。

\2.        Default Lifecycle: 构建的核心部分：编译、测试、打包、部署等等。

\3.        Site Lifecycle: 生成项目报告、站点、发布站点。



每一个阶段都有一个对应的命令，且有相应的插件来支持命令的运行。



注：属于同一个指令周期内的指令，当后面的命令执行时，前面的命令会自动执行。

Jar包的作用范围scope属性

添加jar包的坐标时，还可以指定这个jar包将来的作用范围

依赖范围包括

 **compile**：编译范围，批A在编译时依赖B，此范围为默认依赖范围。编译范围的依赖会用在编译、测试、运行，由于运行时需要所以编译范围的依赖会被打包。



**provided**：provided依赖只有在当JDK或者一个容器已提供该依赖之后才使用，provided依赖在编译和测试时需要，在运行时不需要，比如：servlet api被tomcat容器提供。



 **runtime**: runtime依赖在运行和测试系统时候需要，但在编译的时候不需要，比如：jdbc的驱动包。由于运行时需要，所以runtime范围的依赖会被打包



**test**：test范围依赖在编译和运行时都不需要，它们只有在测试编译和测试运行阶段可用，比如：junit。由于运行时不需要，所以test范围依赖不会被打包。



**system**：system范围依赖与provided类似，但是你必须显式的提供一个对于本地系统中jar文件的路径，需要指定systemPath磁盘路径，system依赖不推荐使用。

![img](https://qqadapt.qpic.cn/txdocpic/0/5f66484ca675873bb0e1c3a0fec39f86/0)

修改pom.xml，给这两个坐标都加上scope为provided 如下：

![img](https://qqadapt.qpic.cn/txdocpic/0/363bc0eac0ea77d0dfe1e0dc67a1a32d/0)



标准目录布局简介

拥有一个通用的目录布局将允许熟悉一个Maven项目的用户立即在另一个Maven项目中感到宾至如归。优点类似于采用站点范围的外观。

下一节将介绍Maven所期望的目录布局以及Maven创建的目录布局。请尽量遵守这种结构; 但是，如果您不能通过项目描述符覆盖这些设置。

| src/main/java      | 应用程序/库源                      |
| ------------------ | ---------------------------------- |
| src/main/resources | 应用程序/库资源                    |
| src/main/filters   | 资源过滤文件                       |
| src/main/webapp    | Web应用程序源                      |
| src/test/java      | 测试来源                           |
| src/test/resources | 测试资源                           |
| src/test/filters   | 测试资源过滤器文件                 |
| src/it             | 集成测试（主要用于插件）           |
| src/assembly       | 程序集描述符                       |
| src/site           | 现场                               |
| LICENSE.TXT        | 项目许可证                         |
| NOTICE.txt         | 项目所依赖的图书馆所需的通知和归属 |
| README.txt         | 项目自述                           |

的src / main /资源在顶层，描述项目的文件：pom.xml文件。此外，还有一些文本文档供用户在接收源时立即读取：README.txt，LICENSE.txt等。

这个结构只有两个子目录：src和target。这里预期的唯一其他目录是CVS，.git或.svn等元数据，以及多项目构建中的任何子项目（每个子项目都将按上面的方式布局）。

在目标目录用于容纳生成的所有输出。

该SRC目录包含了所有的源材料的建设项目，其网站等等。它包含每种类型的子目录：main用于主构建工件，测试单元测试代码和资源，站点等。

在工件生成源目录（即main和test）中，有一个用于语言java的目录（在其下存在正常的包层次结构），还有一个用于资源（在给定默认资源定义的情况下复制到目标类路径的结构） ）。

如果工件构建还有其他贡献源，则它们将位于其他子目录下：例如，src / main / antlr将包含Antlr语法定义文件。



### Maven工程的拆分与聚合

工程的拆分

为什么要拆分？

面对当今互联网+的行业，软件项目变得越来越庞大，复杂程度越来越高，ddd提高了开发与管理的成本。工程的拆分可以实现分模块开发与测试，可实现多线程开发与管理，提高工程代码复用度的同时也提高软件的开发速度与效率。



一个完整的早期开发好的crm项目，现在要使用maven工程对它进行拆分，这时候就可以将dao拆解出来，形成独立的工程，同样service,action也都进行这样的拆分

![img](https://qqadapt.qpic.cn/txdocpic/0/0a7b9c83ae5a66630ca6a7f655e4fc65/0)

把工程拆分成独立的工程，将来要用到的时候就把它们的坐标给引进来就行了，这就有点类似于搭积木一样

工程的聚合

我们的crm项目拆成多个子模块后，独立运行各个模块是无法完成软件项目的要求的，只有把它们都整合起来，分工合作才能完成工作。因此需要父工程来管理各个子模块，把它们聚合在一起运行，把crm06_dao, crm06_service, crm06_web打成一个独立的可运行的war包。



比如：把汽车的各个零部件组装成起来，变成一辆可以行驶的车

继承的理解

类似java类的继承，都是为了消除重复。子类继承父类，父类里有的方法和属性在子类中就不需要再定义和实现了，使用的时候直接调用父类就可以。我们把crm拆分后，有一个父工程，子工程(crm06_dao,crm06_service, crm06_web)要用到的依赖都可以在父工程(crm06)的pom.xml先定义好，将来子工程在开发的时候就不需要再引坐标了。



为了达到聚合的目标，所以今天会引入

父工程（maven project）

子模块(maven module) dao, service, web



### 依赖关系

1．添加模块之间的依赖关系

1)       Service依赖于dao

我们的crm06_service将来要把数据存储到数据库，需要依赖crm06_dao，需要在crm06_service的pom.xml中添加crm06_dao的依赖，即添加crm06_dao的工程坐标



![img](https://qqadapt.qpic.cn/txdocpic/0/44f7d7d3adf7c00bb5e9a4583e9cbd95/0)



保存后查看pom.xml的变化，dependencies下添加了crm06_dao工程的坐标

![img](https://qqadapt.qpic.cn/txdocpic/0/5326cb0c745ce0cb2f6f57a3d6b22e4a/0)

查看工程左右显示的Maven Dependencies

![img](https://qqadapt.qpic.cn/txdocpic/0/16837cb70f89ee145ce406ff51dc135a/0)

2)       Web action依赖于service

同理，crm06_web模块需要调用业务，就需要添加crm06_service的依赖，需要在crm06_web的pom.xml中添加crm06_service的工程坐标

方法同上

![img](https://qqadapt.qpic.cn/txdocpic/0/7be4ad0bbaf8b69b4c06c3a8bfa9f2d6/0)

2．依赖具有传递性

查看crm06_web左边工程显示maven dependencies，此时可以看到：把crm06_dao也依赖进来了，这是因为：**依赖具有传递性**

![img](https://qqadapt.qpic.cn/txdocpic/0/ba49bda99d960ba685c8d2ba2bb89729/0)

3．依赖传递也是有范围的（了解）

子模块crm06_dao中添加junit的依赖，scope为test，但在crm06_service中并不能使用junit

![img](https://qqadapt.qpic.cn/txdocpic/0/0a39cd2fb528e2e6a2a32a8cd75b5d85/0)

1、纵坐标：直接依赖

A 依赖 B，B 是 A 的直接依赖。

在

A 的 pom.xml 中添加 B 的坐标。

2、横坐标：传递依赖

B 依赖 C，C 是 A 的传递依赖。

3、中间部分：传递依赖的范围，A 依赖 C 的范围。



### **依赖冲突解决方法：**

**如果在依赖传递过程中，导致**jar包丢失，我们的做法很简单，就是再导入一次坐标

冲突问题的解决

1．通过添加<exclusion>标签来解决冲突

在父工程中引入了struts-core,hibernate-core，就发现jar包是有冲突的Javassist存在版本上冲突问题

![img](https://qqadapt.qpic.cn/txdocpic/0/107e36625e4a57620ca7192afccb80ba/0)

进入下图：

![img](https://qqadapt.qpic.cn/txdocpic/0/5713f7724f41b1cbf274ebfa7c2d97e2/0)

背后的父工程的pom.xml文件中，添加的内容 

![img](https://qqadapt.qpic.cn/txdocpic/0/f09f5017986da51e4b7051ebf39aa3ea/0)

2．依赖调解的原则

1)       第一声明者优先原则

谁先申明，就用谁的。跟坐标代码的顺序有关

测试：

添加struts2-spring-plugin(2.3.24)在前，spring-context(4.2.3.RELEASE)在后。

![img](https://qqadapt.qpic.cn/txdocpic/0/56926030c5a6e5dda031c12ac731b826/0)

结果

![img](https://qqadapt.qpic.cn/txdocpic/0/3d97e3ee9fbca69ef02e8b1d71670452/0)

如果将上边struts-spring-plugins和spring-context顺序颠倒，系统将导入spring-beans-4.2.3。

![img](https://qqadapt.qpic.cn/txdocpic/0/4ddf6a9f8abc40b13126797fe945fe63/0)

结果：

![img](https://qqadapt.qpic.cn/txdocpic/0/5b66c54ab5d6fe25673d80b114fbcdfe/0)

分析：

由于spring-context在前边以spring-context依赖的spring-beans-4.2.3为准，所以最终spring-beans-4.2.3添加到了工程中。



2)       路径近者优先原则

struts2-spring-plugin(2.3.24)跟spring-context(4.2.3.RELEASE)都会添加spring-beans的依赖。Strut2-spring-plugin会引入spring-beans.3.0.5, 而spring-context会引入spring-beans.4.2.3。如果这时我们直接加入新的依赖叫spring-beans.4.2.4.RELEASE

![img](https://qqadapt.qpic.cn/txdocpic/0/0a8ce5b24335a8957a0855119ca9c54f/0)

结果：

![img](https://qqadapt.qpic.cn/txdocpic/0/4b7261b0478bbb1bf3331b0f18fed6d3/0)

分析：系统中如果要引入spring-beans，可以有以下方法

Ø  通过引入struts2-spring-plugin，它会引入spring-beans.3.0.5的版本。需要经过的路径为2个节点

Ø  通过引入srping-beans-4.2.3，它会引入spring-beans.4.2.3的版本，需要经过的路径为2个节点

Ø  如果直接引入spring-beans-4.2.4，只需要经过1个节点

因此不管spring-beans.4.2.4的位置在哪，系统始终都是引入spring-beans.4.2.4的版本。

3．使用版本锁定实现冲突解决

1)       使用dependencyManagement父工程锁定

首先父工程中pom.xml文件添加：

![img](https://qqadapt.qpic.cn/txdocpic/0/36ce513f71050c42bd577c24b4660f9a/0)

子工程中的pom.xml文件添加junit坐标时就不需要版本的信息了：

![img](https://qqadapt.qpic.cn/txdocpic/0/b68d9832bbffa1e84b3214208841cd4a/0)



2)       定义版本常量

在使用坐标时，对于同一个框架，引入多次时，它的版本信息就会多次出现，所以可以借用常量的思想，将这些版本号提取出来，在需要用到的时候，直接写版本的常量名称就可以了。

![img](https://qqadapt.qpic.cn/txdocpic/0/d5efb6876df5eaa1829e69c275d767d5/0)

引用上面的常量

![img](https://qqadapt.qpic.cn/txdocpic/0/f52ba201ccb3dd957e27ce4084446dce/0)