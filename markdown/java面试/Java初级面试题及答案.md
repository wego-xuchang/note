### Java初级面试题及答案



#### **1、Java中的重载与重写有什么区别**

重载（Overload）是让类以统一的方式处理不同类型数据的一种手段，实质表现就是多个具有不同的参数个数或者类型的同名函数（返回值类型可随意，不能以返回类型作为重载函数的区分标准）同时存在于同一个类中，是一个类中多态性的一种表现（调用方法时通过传递不同参数个数和参数类型来决定具体使用哪个方法的多态性）。

重写（Override）是父类与子类之间的多态性，实质是对父类的函数进行重新定义，如果在子类中定义某方法与其父类有相同的名称和参数则该方法被重写，不过子类函数的访问修饰权限不能小于父类的；若子类中的方法与父类中的某一方法具有相同的方法名、返回类型和参数表，则新方法将覆盖原有的方法，如需父类中原有的方法则可使用 super 关键字。

**重载：**

必须具有不同的参数列表；

可以有不同的返回类型；

可以有不同的访问修饰符；

可以抛出不同的异常。

**重写：**

参数列表必须完全与被重写的方法相同，否则不能称其为重写而是重载；

返回类型必须一直与被重写的方法相同，否则不能称其为重写而是重载；

访问修饰符的限制一定要大于等于被重写方法的访问修饰符；

重写方法一定不能抛出新的检查异常或者比被重写方法申明更加宽泛的检查型异常。



> 重载与重写是 Java 多态性的不同表现，重写是父类与子类之间多态性的表现，在运行时起作用（动态多态性，譬如实现动态绑定），而重载是一个类中多态性的表现，在编译时起作用（静态多态性，譬如实现静态绑定）。



#### **2、 Java 中 final、finally、finalize 的区别**

final 是一个修饰符，如果一个类被声明为 final 则其不能再派生出新的子类，所以一个类不能既被声明为 abstract 又被声明为 final 的；将变量或方法声明为 final 可以保证它们在使用中不被改变（对于对象变量来说其引用不可变，即不能再指向其他的对象，但是对象的值可变），被声明为 final 的变量必须在声明时给定初值，而在以后的引用中只能读取不可修改，被声明为 final 的方法也同样只能使用不能重载。

使用 final 关键字如果编译器能够在编译阶段确定某变量的值则编译器就会把该变量当做编译期常量来使用，如果需要在运行时确定（譬如方法调用）则编译器就不会优化相关代码；将类、方法、变量声明为 final 能够提高性能，这样 JVM 就有机会进行估计并进行优化；接口中的变量都是 public static final 的。

finally 用来在异常处理时提供块来执行任何清除操作，如果抛出一个异常，则相匹配的 catch 子句就会执行，然后控制就会进入 finally 块。

finalize 是一个方法名，Java 允许使用 finalize() 方法在垃圾收集器将对象从内存中清除出去之前做必要的清理工作，这个方法是由垃圾收集器在确定这个对象没有被引用时对这个对象调用的，它是在 Object 类中定义的，因此所有的类都继承了它，子类覆盖 finalize() 方法以整理系统资源或者执行其他清理工作，finalize() 方法在垃圾收集器删除对象之前对这个对象调用的。

参考：

[你以为你真的了解final吗？](https://www.javazhiyin.com/935.html)

[Java final关键字](https://www.javazhiyin.com/251.html)



#### **3、Java 中 hashCode() 的作用**

hashCode() 的作用是为了提高在散列结构存储中查找的效率，在线性表中没有作用；

只有每个对象的 hash 码尽可能不同才能保证散列的存取性能，事实上 Object 类提供的默认实现确实保证每个对象的 hash 码不同（在对象的内存地址基础上经过特定算法返回一个 hash 码）。

在 Java 有些集合类（HashSet）中要想保证元素不重复可以在每增加一个元素就通过对象的 equals 方法比较一次，那么当元素很多时后添加到集合中的元素比较的次数就非常多了，会大大降低效率。

于是 Java 采用了哈希表的原理，这样当集合要添加新的元素时会先调用这个元素的 hashCode 方法就一下子能定位到它应该放置的物理位置上（实际可能并不是），如果这个位置上没有元素则它就可以直接存储在这个位置上而不用再进行任何比较了，如果这个位置上已经有元素了则就调用它的 equals 方法与新元素进行比较，相同的话就不存，不相同就散列其它的地址，这样一来实际调用 equals 方法的次数就大大降低了，几乎只需要一两次，而 hashCode 的值对于每个对象实例来说是一个固定值。

参考：

[详解equals()方法和hashCode()方法](https://www.javazhiyin.com/2010.html)

[浅谈Java中的hashcode方法](https://www.javazhiyin.com/2224.html)



#### **4、抽象类（abstract class）和接口（interface）有什么区别**

含有 abstract 修饰符的 class 为抽象类，abstract 类不能创建实例对象，含有 abstract 方法的类必须定义为 abstract class，abstract class 类中的方法不必是抽象的。

abstract class 类中定义的抽象方法必须在具体的子类中实现，所以不能有抽象构造方法或抽象静态方法，如果子类没有实现抽象父类中的所有抽象方法则子类也必须定义为 abstract 类型。

对于接口可以说是抽象类的一种特例，接口中的所有方法都必须是抽象的（接口中的方法定义默认为 public abstract 类型，接口中的成员变量类型默认为 public static final）。

**具体的区别如下：**

- 抽象类可以有构造方法；接口中不能有构造方法。
- 抽象类中可以有普通成员变量或者常量或者静态变量；接口中没有普通成员变量和静态变量，只能是常量（默认修饰符为 publci static final）。
- 抽象类中可以包含非抽象的普通方法和抽象方法及静态方法；接口中的所有方法必须都是抽象的，不能有非抽象的普通方法和静态方法（默认修饰符为 public abstract）。
- 抽象类中的抽象方法访问类型可以是 public、protected 的；接口中的抽象方法只能是 public 的（默认修饰符为 public abstract）。一个子类可以实现多个接口，但只能继承一个抽象类。

参考：

[深入理解Java的接口和抽象类](https://www.javazhiyin.com/2251.html)

[Java 内部类详解](https://www.javazhiyin.com/253.html)

[详解匿名内部类](https://www.javazhiyin.com/2771.html)



#### **5、为什么 ArrayList 的增加或删除操作相对来说效率比较低**

ArrayList 在小于扩容容量的情况下其实增加操作效率是非常高的，在涉及扩容的情况下添加操作效率确实低，删除操作需要移位拷贝，效率是低点。

因为 ArrayList 中增加（扩容）或者是删除元素要调用 System.arrayCopy 这种效率很低的方法进行处理，所以如果遇到了数据量略大且需要频繁插入或删除的操作效率就比较低了，具体可查看 ArrayList 的 add 和 remove 方法实现，但是 ArrayList 频繁访问元素的效率是非常高的，因此遇到类似场景我们应该尽可能使用 LinkedList 进行替代效率会高一些。

参考：

[Java集合系列[1]ArrayList源码分析](https://www.javazhiyin.com/181.html)

[ArrayList和LinkedList的区别](https://www.javazhiyin.com/2493.html)

**6、 LinkedList 工作原理和实现**

LinkedList 是以双向链表实现，链表无容量限制（但是双向链表本身需要消耗额外的链表指针空间来操作），其内部主要成员为 first 和 last 两个 Node 节点，在每次修改列表时用来指引当前双向链表的首尾部位。

所以 LinkedList 不仅仅实现了 List 接口，还实现了 Deque 双端队列接口（该接口是 Queue 队列的子接口），故 LinkedList 自动具备双端队列的特性，当我们使用下标方式调用列表的 get(index)、set(index, e) 方法时需要遍历链表将指针移动到位进行访问（会判断 index 是否大于链表长度的一半决定是首部遍历还是尾部遍历，访问的复杂度为 O(N/2)），无法像 ArrayList 那样进行随机访问。

(如果i>数组大小的一半，会从末尾移起)，只有在链表两头的操作（譬如 add()、addFirst()、removeLast() 或用在 iterator() 上的 remove() 操作）才不需要进行遍历寻找定位。具体感兴趣可以去看下 LinkedList 的源码。

参考：

[Java集合系列[2]LinkedList源码分析](https://www.javazhiyin.com/183.html)

[ArrayList和LinkedList的区别](https://www.javazhiyin.com/2493.html)

[Java 集合类详解](https://www.javazhiyin.com/283.html)

**7、介绍 HashMap 的底层原理**

当我们往 HashMap 中 put 元素时，先根据 key 的 hash 值得到这个 Entry 元素在数组中的位置（即下标），然后把这个 Entry 元素放到对应的位置中，如果这个 Entry 元素所在的位子上已经存放有其他元素就在同一个位子上的 Entry 元素以链表的形式存放，新加入的放在链头，从 HashMap 中 get Entry 元素时先计算 key 的 hashcode，找到数组中对应位置的某一 Entry 元素，然后通过 key 的 equals 方法在对应位置的链表中找到需要的 Entry 元素。

所以 HashMap 的数据结构是数组和链表的结合，此外 HashMap 中 key 和 value 都允许为 null，key 为 null 的键值对永远都放在以 table[0] 为头结点的链表中。

之所以 HashMap 这么设计的实质是由于数组存储区间是连续的，占用内存严重，故空间复杂度大，但二分查找时间复杂度小（O(1)），所以寻址容易而插入和删除困难；而链表存储区间离散，占用内存比较宽松，故空间复杂度小，但时间复杂度大（O(N)），所以寻址困难而插入和删除容易；

所以就产生了一种新的数据结构叫做哈希表，哈希表既满足数据的查找方便，同时不占用太多的内容空间，使用也十分方便，哈希表有多种不同的实现方法，HashMap 采用的是链表的数组实现方式。

对于 JDK 1.8 开始 HashMap 实现原理变成了数组+链表+红黑树的结构，数组链表部分基本不变，红黑树是为了解决哈希碰撞后链表索引效率的问题，所以在 JDK 1.8 中当链表的节点大于 8 个时就会将链表变为红黑树。

区别是 JDK 1.8 以前碰撞节点会在链表头部插入，而 JDK 1.8 开始碰撞节点会在链表尾部插入，对于扩容操作后的节点转移 JDK 1.8 以前转移前后链表顺序会倒置，而 JDK 1.8 中依然保持原序。

参考：

[Java集合系列[3]HashMap源码分析](https://www.javazhiyin.com/188.html)

[LinkedHashMap和HashMap的比较使用](https://www.javazhiyin.com/1598.html)

[Java集合系列[4]LinkedHashMap源码分析](https://www.javazhiyin.com/193.html)

**8、Hashtable 与 HashMap 的区别**

Hashtable 算是一个过时的集合类，因为 JDK1.5 中提供的 ConcurrentHashMap 是 HashTable 的替代品，其扩展性比 HashTable 更好。由于 HashMap 和 Hashtable 都实现了 Map 接口，所以其主要的区别如下：

1. HashMap 是非 synchronized 的，而 Hashtable 是 synchronized 的。
2. HashMap 可以接受 null 的键和值，而 Hashtable 的 key 与 value 均不能为 null 值。
3. HashMap 的迭代器 Iterator 是 fail-fast 机制的，而 Hashtable 的 Enumerator 迭代器不是 fail-fast 机制的（历史原因）。
4. 单线程情况下使用 HashMap 性能要比 Hashtable 好，因为 HashMap 是没有同步操作的。
5. Hashtable 继承自 Dictionary 类且实现了 Map 接口，而 HashMap 继承自 AbstractMap 类且实现了 Map 接口。
6. HashTable 的默认容量为11，而 HashMap 为 16（安卓中为 4）。
7. Hashtable 不要求底层数组的容量一定是 2 的整数次幂，而 HashMap 则要求一定为 2 的整数次幂。
8. Hashtable 扩容时将容量变为原来的 2 倍加 1，而 HashMap 扩容时将容量变为原来的 2 倍。
9. Hashtable 有 contains 方法，而 HashMap 有 containsKey 和 containsValue 方法。

参考：

[HashMap和HashTable到底哪不同？](https://www.javazhiyin.com/2501.html)

**9、 java 类加载器的理解及加载机制**

通过 java 命令运行 java 程序的步骤就是指定包含 main 方法的完整类名以及一个 classpath 类路径，类路径可以有多个，对于直接的 class 文件路径就是 class 文件的根目录，对于 jar 包文件路径是 jar 包的完整路径，包含 jar 包名字；

java 运行时会根据类的完全限定名寻找并加载，寻找的方式基本就是在系统类和指定的路径中寻找，如果是 class 文件的根目录则直接查看是否有对应的子目录及文件，如果是 jar 包则首先在内存中解压文件，然后再查看是否有对应的类；

负责类加载的类就是 ClassLoader 类加载器，它的输入是完全限定的类名，输出是 Class 对象，java 虚拟机中可以安装多个类加载器，系统默认主要有三个类加载器，每个类负责加载特定位置的类，也可以自定义类加载器，自定义的加载器必须继承 ClassLoader，如下：

启动类加载器(Bootstrap ClassLoader)：此加载器为虚拟机实现的一部分，不是 java 语言上层实现的，一般为 C++ 实现，主要负责加载 java 基础类（譬如<JAVA_HOME>/lib/rt.jar，常用的 String、List 等都位于此包下），启动类加载器无法被 java 程序直接引用。

扩展类加载器(Extension ClassLoader)：此加载器实现类为 sun.misc.Launcher$ExtClassLoader，负责加载 java 的一些扩展类（一般为<JAVA_HOME>/lib/ext目录下的 jar 包），开发者可直接使用。

应用程序类加载器(Application ClassLoader)：此加载器实现类为 sun.misc.Launcher$AppClassLoader，负责加载应用程序的类，包括自己写的和引用的第三方类库，即 classpath 类路径中指定的类，开发者可直接使用，一个程序运行时会创建一个这个加载器，程序中用到加载器的地方如果没有特殊指定一般都是这个加载器，所以也被称为 System 系统类加载器。

这三个加载器具备父子委派关系（非继承父子关系），在 java 中每个类都是由某个类加载器的实体来载入的，所以在 Class 类的实体中都会有字段记录载入它的类加载器的实体（当为 null 时，其指 Bootstrap ClassLoader），在 java 类加载器中除了引导类加载器（既 Bootstrap ClassLoader）。

所有的类加载器都有一个父类加载器（因为他们本身自己就是 java 类），子 ClassLoader 有一个变量 parent 指向父 ClassLoader，在子 ClassLoader 加载类时一般会先通过父 ClassLoader 加载，所以在加载一个 class 文件时首先会判断是否已经加载过了，加载过则直接返回 Class 对象（一个类只会被一个 ClassLoader 加载一次）。

没加载过则先让父 ClassLoader 去加载，如果加载成功返回得到的 Class 对象，父没有加载成功则尝试自己加载，自己加载不成功则抛出 ClassNotFoundException，整个加载流程就是双亲委派模型，即优先让父 ClassLoader 加载；双亲委派可以从优先级的策略上避免 Java 类库被覆盖的问题。

例如类 java.long.Object 存放在 rt.jar 中，无论哪个类加载器要加载这个类最终都会委派给启动类加载器进行加载，因此 Object 类在程序的各种类加载器环境中都是同一个类，相反如果我们自己写了一个类名为 java.long.Object 且放在了程序的 classpath 中，那系统中将会出现多个不同的 Object 类，java 类型体系中最基础的行为也无法保证，所以一般遵循双亲委派的加载器就不会存在这个问题。

类加载机制中的双亲委派模型只是一般情况下的机制，有些时候我们可以自定义加载顺序（不建议）就不用遵守双亲委派模型了，同时以 java 开头的类也不能被自定义类加载器加载，这是 java 安全机制保证的；

ClassLoader 一般是系统提供的，不需要自己实现，不过通过自定义 ClassLoader 可以实现一些灵活强大的功能，譬如热部署（不重启 Java 程序的情况下动态替换类实现）、应用的模块化和隔离化（不同 ClassLoader 可以加载相同的类，但是互相隔离互不影响，tomcat 就是利用这个特性管理多 web 应用的）、灵活加载等，通过自定义类加载器我们可以加载其它位置的类或 jar，自定义类加载器主要步骤为继承 java.lang.ClassLoader 然后重写父类的 findClass 方法。

之所以一般只重写这一个方法是因为 JDK 已经在 loadClass 方法中帮我们实现了 ClassLoader 搜索类的算法，当在 loadClass 方法中搜索不到类时 loadClass 方法会主动调用 findClass 方法来搜索类，所以我们只需重写该方法即可，如没有特殊的要求，一般不建议重写 loadClass 搜索类的算法。

JVM 在判定两个 Class 是否相同时不仅会判断两个类名是否相同而且会判断是否由同一个类加载器实例加载的，只有两者同时满足的情况下 JVM 才认为这两个 Class 是相同的，就算两个 Class 是同一份 class 字节码文件，如果被两个不同的 ClassLoader 实例所加载 JVM 也会认为它们是两个不同 Class。

而对于 JVM 来说它们是两个不同的实例对象，但它们确实是同一份字节码文件，当试图将这个 Class 实例生成具体的对象进行转换时就会抛运行时异常 java.lang.ClassCaseException 提示这是两个不同的类型。此外一个 ClassLoader 创建时如果没有指定 parent 则 parent 默认就是 AppClassLoader。

参考：

[类加载器详解](https://www.javazhiyin.com/2391.html)

**10、Java 中 sleep() 与 wait() 方法的区别**

sleep() 方法使当前线程进入停滞状态（阻塞当前线程），让出 CUP 的使用，目的是不让当前线程独自霸占该进程所获的 CPU 资源。该方法是 Thread 类的静态方法，当在一个 synchronized 块中调用 sleep() 方法时，线程虽然休眠了，但是其占用的锁并没有被释放；当 sleep() 休眠时间期满后，该线程不一定会立即执行，因为其它线程可能正在运行而且没有被调度为放弃执行，除非此线程具有更高的优先级。

wait() 方法是 Object 类的，当一个线程执行到 wait() 方法时就进入到一个和该对象相关的等待池中，同时释放对象的锁（对于 wait(long timeout) 方法来说是暂时释放锁，因为超时时间到后还需要返还对象锁），其他线程可以访问。wait() 使用 notify() 或 notifyAll() 或者指定睡眠时间来唤醒当前等待池中的线程。wait() 必须放在 synchronized 块中使用，否则会在运行时抛出 IllegalMonitorStateException 异常。

由此可以看出它们之间的区别如下：

1. sleep() 不释放同步锁，wait() 释放同步锁。
2. sleep(milliseconds) 可以用时间指定来使他自动醒过来，如果时间没到则只能调用 interreput() 方法来强行打断（不建议，会抛出 InterruptedException），而 wait() 可以用 notify() 直接唤起。
3. sleep() 是 Thread 的静态方法，而 wait() 是 Object 的方法。
4. wait()、notify()、notifyAll() 方法只能在同步控制方法或者同步控制块里面使用，而 sleep() 方法可以在任何地方使用。

多线程与并发参考：

[Java 并发编程简介](https://www.javazhiyin.com/199.html)

[并发编程的优缺点](https://www.javazhiyin.com/840.html)

[线程的状态转换以及基本操作](https://www.javazhiyin.com/847.html)

[Java并发专栏](https://www.javazhiyin.com/category/thread)

[Java多线程和线程池](https://www.javazhiyin.com/2149.html)

[多线程的优点](https://www.javazhiyin.com/2448.html)

[JAVA多线程实现和应用总结](https://www.javazhiyin.com/2457.html)

**11、对 ClassLoader 的理解**

ClassLoader 的作用是根据一个指定的类名称找到或者生成其对应的字节代码，然后把字节码转换成一个 Java 类（即 java.lang.Class 实例），除此之外还负责加载 Java 应用所需的资源、Native lib 库等。

Java 的类加载器大致可以分成系统类加载器和应用开发自定义类加载器。系统类加载器主要有如下几个：

1. 引导类加载器（bootstrap class loader）：用来加载 Java 核心库，是虚拟机中用原生代码实现的，没有继承自 ClassLoader。
2. 扩展类加载器（extensions class loader）：用来加载 Java 的扩展库，虚拟机的实现会提供一个默认的扩展库目录，该类加载器在此目录里面查找并加载 Java 类。
3. 系统类加载器（system class loader）：用来加载应用类路径（CLASSPATH）下的 class，一般来说 Java 应用的类都是由它来完成加载的，可以通过 ClassLoader.getSystemClassLoader() 来获取它。

除了引导类加载器之外，所有的其他类加载器都有一个父类加载器（可以通过 ClassLoader 的 getParent() 方法得到）。系统类加载器的父类加载器是扩展类加载器，而扩展类加载器的父类加载器是引导类加载器。

开发自定义的类加载器的父类加载器是加载此类加载器的 Java 类的类加载器。所以类加载器在尝试自己去加载某个类时会先通过 getParent() 代理给其父类加载器，由父类加载器先去尝试加载这个类，依次类推，从而形成了双亲委派模式。类加载机制是通过 loadClass 方法触发的，查找类有没有被加载和该代理给哪个层级的加载器加载是由 findClass 方法实现的，而真正完成类加载工作是 defineClass 方法实现的。

**12、ArrayList和Vector有何异同点？**

ArrayList和Vector在很多时候都很类似。  
（1）两者都是基于索引的，内部由一个数组支持。  
（2）两者维护插入的顺序，我们可以根据插入顺序来获取元素。  
（3）ArrayList和Vector的迭代器实现都是fail-fast的。  
（4）ArrayList和Vector两者允许null值，也可以使用索引值对元素进行随机访问。

以下是ArrayList和Vector的不同点。  
（1）Vector是同步的，而ArrayList不是。然而，如果你寻求在迭代的时候对列表进行改变，你应该使用CopyOnWriteArrayList。  
（2）ArrayList比Vector快，它因为有同步，不会过载。  
（3）ArrayList更加通用，因为我们可以使用Collections工具类轻易地获取同步列表和只读列表。

**13、SpringMVC运行原理**

1. 客户端请求提交到DispatcherServlet
2. 由DispatcherServlet控制器查询HandlerMapping，找到并分发到指定的Controller中。
3. Controller调用业务逻辑处理后，返回ModelAndView
4. DispatcherServlet查询一个或多个ViewResoler视图解析器，找到ModelAndView指定的视图
5. 视图负责将结果显示到客户端

参考：

[SpringMVC架构浅析](https://www.javazhiyin.com/1642.html)

[SpringMVC工作原理](https://www.javazhiyin.com/1422.html)

[自己手写一个SpringMVC框架](https://www.javazhiyin.com/1922.html)

[手写spring+springmvc+mybatis框架篇【springmvc】](https://www.javazhiyin.com/558.html)

**14、说说熟悉的排序算法（很可能手写伪代码）**

[基数排序](https://www.javazhiyin.com/1215.html)

[堆排序](https://www.javazhiyin.com/1200.html)

[归并排序](https://www.javazhiyin.com/1222.html)

[选择排序](https://www.javazhiyin.com/1249.html)

[拓扑排序之Java详解](https://www.javazhiyin.com/1255.html)

[希尔排序](https://www.javazhiyin.com/1272.html)

[直接插入排序](https://www.javazhiyin.com/1307.html)

[快速排序](https://www.javazhiyin.com/1313.html)

[冒泡排序](https://www.javazhiyin.com/1326.html)

**15、项目中用到了什么设计模式（也可能伪代码）**

设计模式专栏：[https://www.javazhiyin.com/category/sjms](https://www.javazhiyin.com/category/sjms)
