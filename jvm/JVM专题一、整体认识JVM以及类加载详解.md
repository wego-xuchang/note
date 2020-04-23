## JVM专题一、整体认识JVM以及类加载详解

### JVM虚拟机体系构成
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5OGMwMGFkODEyZA?x-oss-process=image/format,png)![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5OTUwNjc2OTYzOA?x-oss-process=image/format,png)

 JVM的体系结构：四大块，类装载子系统（class loader subsystem），执行引擎子系统（Executionengine子系统），垃圾回收系统（gc），运行时数据区（JVM内存）。

### Java虚拟机产品：

  Sun HotSpot VM、BEA JRockit VM、IBM J9 VM、Azul VM、Apache Harmony、Google Dalvik VM、Microsoft JVM...

 

### 类加载机制详解:

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5Y2Y2ZTllMjA1ZQ?x-oss-process=image/format,png)

加载》验证》准备》解析》初始化



#### 加载

主要是将.class文件中二进制字节流加载到jvm中

- 1.通过类的全限定名获取改类的二进制字节流
- 2.将字节流所代表的静态存储结构转化为方法区的运行时数据结构
- 3.在内存中生成一个该类的**java.lang.Class**对象，作为方法区这个类的各种数据的访问入口

#### 连接

##### **验证**

验证第一步加载阶段获得的二进制字节流.class文件是否符合jvm规范

- 1.文件格式验证
- 2.元数据验证，是否符合java规范
- 3.字节码验证，确保程序语义合法，符合逻辑
- 4.符号引用验证，确保下一步的解析能正常执行

##### 准备

准备阶段主要是给static变量分配内存(方法区中)，并设置初始值。

##### 解析

虚拟机将常量池内的符号引用替换为直接引用。



**在JVM中类加载过程中，在解析阶段，Java虚拟机会把类的二级制数据中的符号引用替换为直接引用。**

1.符号引用（Symbolic References）：

　　符号引用以一组符号来描述所引用的目标，符号可以是任何形式的字面量，只要使用时能够无歧义的定位到目标即可。例如，在Class文件中它以CONSTANT_Class_info、CONSTANT_Fieldref_info、CONSTANT_Methodref_info等类型的常量出现。符号引用与虚拟机的内存布局无关，引用的目标并不一定加载到内存中。在Java中，一个java类将会编译成一个class文件。在编译时，java类并不知道所引用的类的实际地址，因此只能使用符号引用来代替。比如org.simple.People类引用了org.simple.Language类，在编译时People类并不知道Language类的实际内存地址，因此只能使用符号org.simple.Language（假设是这个，当然实际中是由类似于CONSTANT_Class_info的常量来表示的）来表示Language类的地址。各种虚拟机实现的内存布局可能有所不同，但是它们能接受的符号引用都是一致的，因为符号引用的字面量形式明确定义在Java虚拟机规范的Class文件格式中。

2.直接引用：

 直接引用可以是

（1）直接指向目标的指针（比如，指向“类型”【Class对象】、类变量、类方法的直接引用可能是指向方法区的指针）

（2）相对偏移量（比如，指向实例变量、实例方法的直接引用都是偏移量）

（3）一个能间接定位到目标的句柄

直接引用是和虚拟机的布局相关的，同一个符号引用在不同的虚拟机实例上翻译出来的直接引用一般不会相同。如果有了直接引用，那引用的目标必定已经被加载入内存中了。

#### 初始化

根据程序中的赋值语句主动为类变量赋值。

这一块其实就是调用类的构造方法,注意是类的构造方法，不是实例构造函数，实例构造函数就是我们通常写的构造方法，类的构造方法是自动生成的，生成规则:
static变量的赋值操作+static代码块
按照出现的先后顺序来组装。
**注意:**

- 1.static变量的内存分配和初始化是在准备阶段.

- 2 .一个类可以是很多个线程同时并发执行，JVM会加锁保证单一性，所以不要在static代码块中搞一些耗时操作。避免线程阻塞。

#####  什么时候需要初始化

- 1.使用new该实例化对象的时候
- 2.读取或者设置类静态字段是时候，但被final修饰的字段，在编译器时就被放入常量池的静态字段除外static、final
- 3.调用类静态方法的时候
- 4.使用反射**Class.forName("xxx")**对类进行反射调用的时候
- 5.初始化一个类的时候，有父类，先初始化父类（注：1.接口除外，父接口调用的时候才会被初始化；2.自类引用父类静态字段，只会引发父类初始化）
- 6.被注明为启动类（包含main方法）
- 7.当使用JDK1.7的动态语言支持时，如果一个**java.invoke.MethodHandle**实例最后的解析结果REF_getStatic、REF_putStatic、REF_invokeStatic的方法句柄，并且这个方法句柄所对应的类没有进行过实例化，则需要先触发其实例化

##### 初始化顺序

- 1.父类的静态变量和静态块赋值（按声明顺序）
- 2.自身的静态变量和静态块赋值（按声明顺序）
- 3.父类成员变量和块赋值（按声明顺序）
- 4.父类构造器赋值（如果父类中包含有构造器，却没有无参构造器，则在子类的构造器中一定使用**super**指定调用父类的有参构造器，不然就报错）
- 5.自身成员变量和块赋值（按照声明顺序）
- 6.自身构造器赋值

### 加载器分类：

**启动类加载器（Bootstrap ClassLoader）：**

最顶层的类加载器，负责加载 JAVA_HOME\lib 目录中的，或通过-Xbootclasspath参数指定路径中的，且被虚拟机认可（按文件名识别，如rt.jar）的类

 

**扩展类加载器(Extension ClassLoader)：**

负责加载 JAVA_HOME\lib\ext 目录中的，或通过java.ext.dirs系统变量指定路径中的类库

 

**应用程序类加载器(Application ClassLoader)：**

也叫做系统类加载器，可以通过getSystemClassLoader()获取，负责加载用户路径（classpath）上的类库。如果没有自定义类加载器，一般这个就是默认的类加载器。

### 加载基本步骤：

（1）AppClassLoader查找资源时，不是首先查看自己的地盘是否有这个字节码文件，而是直接委托给父加载器ExtClassLoader。当然，这里有一个假定，就是在AppClassLoader的缓存中，没有找到目标class。比方说，第一次加载一个目标类，这个类是不会在缓存的。

（2）ExtClassLoader查找资源时，也不是首先查看自己的地盘是否有这个字节码文件，而是直接委托给父加载器BootstrapClassLoader。

（3）如果父加载器BootstrapClassLoader在其地盘找到，并且加载成功，则直接返回了；反过来，如果在JVM的核心地盘——%sun.boot.class.path% 中没有找到。则回到ExtClassLoader查找其地盘。

（4）如果父加载器ExtClassLoader在自己的地盘找到，并且加载成功，也直接返回了；反过来，如果在ExtClassLoader的地盘——%java.ext.dirs% 中没有找到。则回到AppClassLoader自己的地盘。

（5）于是乎，逗了一大圈，终于回到了自己的地盘。还附带了两条件，就是前面的老大们没有搞定，否则也没有AppClassLoader啥事情了。

（6）AppClassLoader在自己的地盘找到，这个地盘就是%java.class.path%路径下查找。找到就返回。

（7）最终，如果没有找到，就抛出异常了。

这个过程，就是一个典型的双亲委托机制的一次执行流程。

> 最主要的就是getClass和findClass这两个方法

## 双亲委派模型

### 双亲委派模型的的原理是：

如果一个类加载器收到了类加载的请求，它首先不会自己去尝试加载这个类，而是把这个请求委派给父类加载器去完成，每一个层次的类加载器都是如此，因此所有的加载请求最终都应该传送到顶层的启动类加载器中。只有当父加载器反馈自己无法完全这个加载请求时，子加载器才会尝试自己去加载。

### 为什么要使用这种双亲委托模式呢？Tomcat下运行多个项目如何避免重复加载？

因为这样可以避免重复加载，当父亲已经加载了该类的时候，就没有必要子ClassLoader再加载一次。

双亲委托机制，也就构成了JVM 的类的沙箱机制。

沙箱机制是由基于双亲委派机制上采取的一种JVM的自我保护机制，假设你要写一个java.lang.String 的类，由于双亲委派机制的原理，此请求会先交给Bootstrap试图进行加载，但是Bootstrap在加载类时首先通过包和类名查找rt.jar中有没有该类，有则优先加载rt.jar包中的类，因此就保证了java的运行机制不会被破坏。

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YTJiZjBhZDE1OA?x-oss-process=image/format,png)

 我们看到，前面3个类加载和默认的一致，CommonClassLoader、CatalinaClassLoader、SharedClassLoader、WebappClassLoader 和JasperClassLoader则是Tomcat自己定义的类加载器，它们分别加载/common/*、/server/*、/shared/*（在tomcat 6之后已经合并到根目录下的lib目录下）和/WebApp/WEB-INF/*中的Java类库。其中WebApp类加载器和Jsp类加载器通常会存在多个实例，每一个Web应用程序对应一个WebApp类加载器，每一个JSP文件对应一个Jsp类加载器。

commonLoader：Tomcat最基本的类加载器，加载路径中的class可以被Tomcat容器本身以及各个Webapp访问；
catalinaLoader：Tomcat容器私有的类加载器，加载路径中的class对于Webapp不可见；
sharedLoader：各个Webapp共享的类加载器，加载路径中的class对于所有Webapp可见，但是对于Tomcat容器不可见；
WebappClassLoader：各个Webapp私有的类加载器，加载路径中的class只对当前Webapp可见；

JasperClassLoader：jsp私有的类加载器，加载路径中的class只对当前Webapp可见；



tomcat 为了实现隔离性，没有遵守这个约定，每个webappClassLoader加载自己的目录下的class文件，不会传递给父类加载器。

### 类字节码详解:



![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YWU4ODMwZWM2MA?x-oss-process=image/format,png)

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YjFiNzI0YjNhYQ?x-oss-process=image/format,png)

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YmE2NzFlZDQxYg?x-oss-process=image/format,png)

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YmVmMjg4ODA5MQ?x-oss-process=image/format,png)



### JVM运行：

![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly91c2VyLWdvbGQtY2RuLnhpdHUuaW8vMjAyMC80LzQvMTcxNDU5YzMyZDlhOTMyYg?x-oss-process=image/format,png)

**本地方法栈(线程私有)**：登记native方法，在Execution Engine执行时加载本地方法库

 

**程序计数器（线程私有）**：就是一个指针，指向方法区中的方法字节码（用来存储指向下一条指令的地址,也即将要执行的指令代码），    由执行引擎读取下一条指令，是一个非常小的内存空间，几乎可以忽略不记。

**方法区(线程共享)**：类的所有字段和方法字节码，以及一些特殊方法如构造函数，接口代码也在此定义。简单说，所有定义的方法的信息都保存在该区域，静态变量+常量+类信息(构造方法/接口定义)+运行时常量池都存在方法区中，虽然Java虚拟机规范把方法区描述为堆的一个逻辑部分，但是它却有一个别名叫做 Non-Heap（非堆），目的应该是与 Java 堆区分开来。

**Java（虚拟）栈（线程私有）**： Java线程执行方法的内存模型，一个线程对应一个栈，每个方法在执行的同时都会创建一个栈帧（用于存储局部变量表，操作数栈，动态链接，方法出口等信息）不存在垃圾回收问题，只要线程一结束该栈就释放，生命周期和线程一致

### JDK版本差异

**元数据区**：元数据区取代了永久代(jdk1.8以前)，本质和永久代类似，都是对JVM规范中方法区的实现，区别在于元数据区并不在虚拟机中，而是使用本地物理内存，永久代在虚拟机中，永久代逻辑结构上属于堆，但是物理上不属于堆，堆大小=新生代+老年代。元数据区也有可能发生OutOfMemory异常。

Jdk1.6及之前： 有永久代, 常量池在方法区

Jdk1.7：       有永久代，但已经逐步“去永久代”，常量池在堆

Jdk1.8及之后： 无永久代，常量池在元空间

元数据区的动态扩展，默认–XX:MetaspaceSize值为21MB的高水位线。一旦触及则Full GC将被触发并卸载没有用的类（类对应的类加载器不再存活），然后高水位线将会重置。新的高水位线的值取决于GC后释放的元空间。如果释放的空间少，这个高水位线则上升。如果释放空间过多，则高水位线下降。

为什么jdk1.8用元数据区取代了永久代？

官方解释：移除永久代是为融合HotSpot JVM与 JRockit VM而做出的努力，因为JRockit没有永久代，不需要配置永久代

## JVM执行原理：

### JVM指令集详解：

**变量到操作数栈：**

iload,iload_,lload,lload_,fload,fload_,dload,dload_,aload,aload_

**操作数栈到变量：**

**Ø**istore,istore_,lstore,lstore_,fstore,fstore_,dstore,dstor_,astore,astore

**常数到操作数栈**

bipush,sipush,ldc,ldc_w,ldc2_w,aconst_null,iconst_ml,iconst_,lconst_,fconst_,dconst_

**把数据装载到操作数栈**

baload,caload,saload,iaload,laload,faload,daload,aaload

**从操作数栈存存储到数组：**

bastore, castore,sastore,iastore,lastore,fastore,dastore,aastore

**操作数栈管理**

pop,pop2,dup,dup2,dup_xl,dup2_xl,dup_x2,dup2_x2,swap

**运算与转换：**

• 加：iadd,ladd,fadd,dadd

• 减：is ,ls ,fs ,ds

• 乘：imul,lmul,fmul,dmul

• 除：idiv,ldiv,fdiv,ddiv

• 余数：irem,lrem,frem,drem

• 取负：ineg,lneg,fneg,dneg

• 移位：ishl,lshr,iushr,lshl,lshr,lushr

• 按位或：ior,lor

**按位与：iand,land**

• 按位异或：ixor,lxor

**类型转换：**

i2l,i2f,i2d,l2f,l2d,f2d(放宽数值转换)

i2b,i2c,i2s,l2i,f2i,f2l,d2i,d2l,d2f(缩窄数值转换)

**有条件转移**

ifeq,iflt,ifle,ifne,ifgt,ifge,ifnull,ifnonnull,if_icmpeq,if_icmpene,

if_icmplt,if_icmpgt,if_icmple,if_icmpge,if_acmpeq,if_acmpne,lcmp,fc mpl,fcmpg,dcmpl,dcmpg

**复合条件转移：**

tableswitch,lookupswitch

**无条件转移：**

goto,goto_w,jsr,jsr_w,ret

 

 

 

### JVM运行原理详解：

### 栈：

#### 局部变量表

· 局部变量表是变量值的存储空间，用于存放方法参数和方法内部定义的局部变量。在java编译成class文件的时候，就在方法的Code属性的max_locals数据项中确定该方法需要分配的最大局部变量表的容量。

· 局部变量表的容量以变量槽（Slot）为最小单位，32位虚拟机中一个Slot可以存放32位（4 字节）以内的数据类型（ boolean、byte、char、short、int、float、reference和returnAddress八种）

· 对于64位长度的数据类型（long，double），虚拟机会以高位对齐方式为其分配两个连续的Slot空间，也就是相当于把一次long和double数据类型读写分割成为两次32位读写。

· reference类型虚拟机规范没有明确说明它的长度，但一般来说，虚拟机实现至少都应当能从此引用中直接或者间接地查找到对象在Java堆中的起始地址索引和方法区中的对象类型数据。

· Slot是可以重用的，当Slot中的变量超出了作用域，那么下一次分配Slot的时候，将会覆盖原来的数据。Slot对对象的引用会影响GC（要是被引用，将不会被回收）。  系统不会为局部变量赋予初始值（实例变量和类变量都会被赋予初始值）。也就是说不存在类变量那样的准备阶段。

· 系统不会为局部变量赋予初始值（实例变量和类变量都会被赋予初始值）。也就是说不存在类变量那样的准备阶段。

**操作数栈**

· 操作数栈和局部变量表一样，在编译时期就已经确定了该方法所需要分配的局部变量表的最大容量。

· 操作数栈的每一个元素可用是任意的Java数据类型，包括long和double。32位数据类型所占的栈容量为1，64位数据类型占用的栈容量为2。

· 当一个方法刚刚开始执行的时候，这个方法的操作数栈是空的，在方法执行的过程中，会有各种字节码指令往操作数栈中写入和提取内容，也就是出栈 / 入栈操作（例如：在做算术运算的时候是通过操作数栈来进行的，又或者在调用其它方法的时候是通过操作数栈来进行参数传递的）。

· 在概念模型里，栈帧之间是应该是相互独立的，不过大多数虚拟机都会做一些优化处理，使局部变量表和操作数栈之间有部分重叠，这样在进行方法调用的时候可以直接共用参数，而不需要做额外的参数复制等工作。重叠过程如图所示：

#### 动态连接

每个栈帧都包含一个指向运行时常量池中该栈帧所属方法的引用，Class文件的常量池中存有大量的符号引用，字节码中的方法调用指令就以常量池中方法的符号引用为参数。这些符号引用一部分会在类加载阶段或者第一次使用的时候就转化为直接引用（静态方法，私有方法等），这种转化称为静态解析，另一部分将在每一次运行期间转化为直接引用，这部分称为动态连接。由于篇幅有限这里不再继续讨论解析与分派的过程，这里只需要知道静态解析与动态连接的区别就好。

#### 方法返回地址

当一个方法开始执行后，只有两种方式可以退出这个方法:

执行引擎遇到任意一个方法返回的字节码指令:传递给上层的方法调用者，是否有返回值和返回值类型将根据遇到何种方法来返回指令决定，这种退出的方法称为正常完成出口。

方法执行过程中遇到异常： 无论是java虚拟机内部产生的异常还是代码中throw出的异常，只要在本方法的异常表中没有搜索到匹配的异常处理器，就会导致方法退出，这种退出的方式称为异常完成出口，一个方法若使用该方式退出，是不会给上层调用者任何返回值的。无论使用那种方式退出方法，都要返回到方法被调用的位置，程序才能继续执行。方法返回时可能会在栈帧中保存一些信息，用来恢复上层方法的执行状态。一般方法正常退出的时候，调用者的pc计数器的值可以作为返回地址，帧栈中很有可能会保存这个计数器的值作为返回地址。方法退出的过程就是栈帧在虚拟机栈上的出栈过程，因此退出时的操作可能有：恢复上层方法的局部变量表和操作数栈，把返回值压入调用者的操作数栈每条整pc计数器的值指向调用该方法的后一条指令。



 