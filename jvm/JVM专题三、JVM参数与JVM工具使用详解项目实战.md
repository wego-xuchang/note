# JVM参数与JVM工具使用详解项目实战

## 日志分析

如何生产日志？如何阅读日志?通过分析我们能得出什么结论来?

### 日志怎么生成？

-XX:+PrintGC 输出简要GC日志 

-XX:+PrintGCDetails 输出详细GC日志 

-Xloggc:gc.log  输出GC日志到文件

-XX:+PrintGCTimeStamps 输出GC的时间戳（以JVM启动到当期的总时长的时间戳形式） 

-XX:+PrintGCDateStamps 输出GC的时间戳（以日期的形式，如 2013-05-04T21:53:59.234+0800） 

-XX:+PrintHeapAtGC 在进行GC的前后打印出堆的信息

-verbose:gc

-XX:+PrintReferenceGC 打印年轻代各个引用的数量以及时长

### 日志怎么看？

  2020-01-07T20:27:21.065+0800: [GC (Allocation Failure) [PSYoungGen: 44539K->5880K(45056K)] 74243K->73473K(122880K), 0.0267773 secs] [Times: user=0.06 sys=0.05, real=0.03 secs] 

Minor gc：[GC (Allocation Failure) 

PSYoungGen：类型新生代gc

44539K->5880K(45056K)] ：

0.0267773 secs] [Times: user=0.06 sys=0.05, real=0.03 secs]：

 

2020-01-07T20:27:21.092+0800: [Full GC (Ergonomics) [PSYoungGen: 5880K->0K(45056K)] [ParOldGen: 67593K->73366K(153600K)] 73473K->73366K(198656K), [Metaspace: 3491K->3491K

Full gc：

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps30.jpg) 

### 可视化工具

Gceasy

https://gceasy.io/

## JVM参数详解：

官网JVM参数：

https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html

 

-X

-XX

 

布尔类型

-XX:+PrintGCDetails  打开 输出详细GC日志 

-XX:-PrintGCDetails  关闭 输出详细GC日志 

-XX:PretenureSizeThreshold=1M 赋值  6

-XX:*=“” 指定文件路径

=============

-X

-Xmx200M -Xmn50m 

-XX:+PrintGC 

 

| 配置参数                        | 功能                                                         |
| ------------------------------- | ------------------------------------------------------------ |
| -Xms                            | 初始堆大小。如：-Xms256m                                     |
| -Xmx                            | 最大堆大小。如：-Xmx512m                                     |
| -Xmn                            | 新生代大小。通常为 Xmx 的 1/3 或 1/4。新生代 = Eden + 2 个 Survivor 空间。实际可用空间为 = Eden + 1 个 Survivor，即 90% |
| -Xss                            | JDK1.5+ 每个线程堆栈大小为 1M，一般来说如果栈不是很深的话， 1M 是绝对够用了的。 |
| -XX:NewRatio                    | 新生代与老年代的比例，如 –XX:NewRatio=2，则新生代占整个堆空间的1/3，老年代占2/3 |
| -XX:SurvivorRatio               | 新生代中 Eden 与 Survivor 的比值。默认值为 8。即 Eden 占新生代空间的 8/10，另外两个 Survivor 各占 1/10 |
| -XX:+PrintGCDetails             | 打印 GC 信息                                                 |
| -XX:+HeapDumpOnOutOfMemoryError | 让虚拟机在发生内存溢出时 Dump 出当前的内存堆转储快照，以便分析用 |

 

### Serial 收集器参数

| 配置参数                            | 功能                                                         |
| ----------------------------------- | ------------------------------------------------------------ |
| -XX:+UseSerialGC                    | 这个参数就是可以指定使用新生代串行收集器和老年代串行收集器， “+” 号的意思是ture，开启，反之，如果是 “-”号，则是关闭。 |
| -XX:+UseParNewGC                    | 新生代使用 ParNew 回收器，老年代使用串行收集器。             |
| -XX:+UseParallelGC                  | 新生代私用 ParallelGC 回收器，老年代使用串行收集器           |
| 而 Serial 收集器出现的日志为 DefNew |                                                              |

 

### ParNew 收集器参数

| 配置参数                                | 功能                                                         |
| --------------------------------------- | ------------------------------------------------------------ |
| -XX:+UseParNewGC                        | 上面说过了，新生代使用 ParNew 收集器，老年代使用串行收集器   |
| -XX:+UseConcMarkSweepGC                 | 新生代使用 ParNew 回收器，老年代使用 CMS                     |
| -XX:ParallelGCThreads={value}           | 这个参数是指定并行 GC 线程的数量，一般最好和 CPU 核心数量相当。默认情况下，当 CPU 数量小于8， ParallelGCThreads 的值等于 CPU 数量，当 CPU 数量大于 8 时，则使用公式：3+（（5*CPU）/ 8）；同时这个参数只要是并行 GC 都可以使用，不只是 ParNew。 |
| 而 ParNew 的 GC 日志则表吸纳出 ParNew。 |                                                              |

### PS 收集器参数

| -XX:MaxGCPauseMillis      | 设置最大垃圾收集停顿时间，他的值是一个大于0的整数。ParallelGC 工作时，会调整 Java 堆大小或者其他的一些参数，尽可能的把停顿时间控制在 MaxGCPauseMillis 以内。如果为了将停顿时间设置的很小，将此值也设置的很小，那么 PS 将会把堆设置的也很小，这将会到值频繁 GC ，虽然系统停顿时间小了，但总吞吐量下降了。。 |
| ------------------------- | ------------------------------------------------------------ |
| -XX:GCTimeRatio           | 置吞吐量大小，他的值是一个0 到100之间的整数，假设 GCTimeRatio 的值是 n ，那么系统将花费不超过 1/(1+n) 的时间用于垃圾收集，默认 n 是99，即不超过1% 的时间用于垃圾收集。 |
| -XX:+UseParallelGC        | 新生代使用 ParallelGC 回收器，老年代使用串行回收器。         |
| -XX:+UseParallelOldGC     | 新生代使用 ParallelGC 回收器，老年代使用 ParallelOldGC 回收器 |
| -XX:UseAdaptiveSizePolicy | 打开[自适应](http://msd.misuland.com/pd/3255818135034404050)策略。在这种模式下，新生代的大小，eden 和 Survivor 的比例，晋升老年代的对象年龄等参数会被自动调整。以达到堆大小，吞吐量，停顿时间的平衡点。 |

### CMS 收集器参数

| -XX:-CMSPrecleaningEnabled             | 不进行预清理，度过我们之前的文章的都知道，CMS 在并发标记和重新标记的这段时间内，会有一个预清理的工作，而这个通过会尝试5秒之内等待来一次 YGC。以免在后面的重新标记阶段耗费大量时间来标记新生代的对象。 |
| -------------------------------------- | ------------------------------------------------------------ |
| -XX:+UseConcMarkSweepGC                | 此参数将启动 CMS 回收器。默认新生代是 ParNew，也可以设置 Serial 为新生代收集器。该参数等价于 -Xconcgc。 |
| -XX:ParallelGCThreads                  | 由于是并行处理器，当然也可以指定线程数。默认并发线程数是：（ParallelGCThreads + 3）/ 4）。 |
| -XX:ConcGCThreads                      | 或者 -XX:ParallelCMSThreads ；除了上面设置线程的方式，你也可以通过这个两个参数任意一个手工设定 CMS 并发线程数 |
| -XX:CMSInitiatingOccupancyFraction     | 由于 CMS 回收器不是独占式的，在垃圾回收的时候应用程序仍在工作，所以需要留出足够的内存给应用程序，否则会触发 FGC。而什么时候运行 CMS GC 呢？通过该参数即可设置，该参数表示的是老年代的内存使用百分比。当达到这个阈值就会执行 CMS。默认是68。 如果老年代内存增长很快，建议降低阈值，避免 FGC，如果增长慢，则可以加大阈值，减少 CMS GC 次数。提高吞吐量。 |
| -XX：+UseCMSCompactAtFullCollection    | 由于 CMS 使用标记清理算法，内存碎片无法避免。该参数指定每次 CMS 后进行一次碎片整理。 |
| -XX:CMSFullGCsBeforeCompaction         | 由于每次进行碎片整理将会影响性能，你可以使用该参数设定多少次 CMS 后才进行一次碎片整理，也就是内存压缩。 |
| -XX:+CMSClassUnloadingEnabled          | 允许对类元数据进行回收。                                     |
| -XX:CMSInitiatingPermOccupancyFraction | 当永久区占用率达到这一百分比时，启动 CMS 回收（前提是 -XX:+CMSClassUnloadingEnabled 激活了）。 |
| -XX:UseCMSInitiatingOccupancyOnly      | 表示只在到达阈值的时候才进行 CMS 回收。                      |
| XX:CMSWaitDuration=2000                | 由于CMS GC 条件比较简单，JVM有一个线程定时扫描Old区，时间间隔可以通过该参数指定（毫秒单位），默认是2s。 |
| CMS 的 GC 日志 就是 CMS。              |                                                              |

### G1 收集器参数

| -XX:+UseG1GC                       | 开启 G1 收集器。                                             |
| ---------------------------------- | ------------------------------------------------------------ |
| -XX:MaxGCPauseMillis               | 用于指定最大停顿时间，如果任何一次停顿超过这个设置值时，G1 就会尝试调整新生代和老年代的比例，调整堆大小，调整晋升年龄的手段，试图达到目标。和 PS 一样，停顿时间小了，对应的吞吐量也会变小。这点值得注意。 |
| -XX:ParallelGCThreads              | 由于是并行并发的，可以指定GC 工作线程数量                    |
| -XX:InitiatingHeapOccupancyPercent | 该参数可以指定当整个堆使用率达到多少时，触发并发标记周期的执行。默认值时45，即当堆的使用率达到45%，执行并发标记周期，该值一旦设置，始终都不会被 G1修改。也就是说，G1 就算为了满足 MaxGCPauseMillis 也不会修改此值。如果该值设置的很大，导致并发周期迟迟得不到启动，那么引起 FGC 的几率将会变大。如果过小，则会频繁标记，GC 线程抢占应用程序CPU 资源，性能将会下降。 |
| -XX:GCPauseIntervalMillis          | 设置停顿时间间隔                                             |

### JVM通用参数：

| -XX:-+DisableExplicitGC             | 禁用 System.gc()，由于该方法默认会触发 FGC，并且忽略参数中的 UseG1GC 和 UseConcMarkSweepGC，因此必要时可以禁用该方法。 |
| ----------------------------------- | ------------------------------------------------------------ |
| -XX:+ExplicitGCInvokesConcurrent    | 该参数可以改变上面的行为，也就是说，System.gc() 后不使用 FGC ，而是使用配置的并发收集器进行并发收集。注意：使用此选项就不要 使用 上面的选项。 |
| -XX:-ScavengeBeforeFullGC           | 由于大部分 FGC 之前都会 YGC，减轻了 FGC 的压力，缩短了 FGC 的停顿时间，但也可能你不需要这个特性，那么你可以使用这个参数关闭，默认是 ture 开启。 |
| -XX:MaxTenuringThreshold={value}    | 新生代 to 区的对象在经过多次 GC 后，如果还没有死亡，则认为他是一个老对象，则可以晋升到老年代，而这个年龄（GC 次数）是可以设置的，有就是这个参数。默认值时15。超过15 则认为是无限大(因为age变量时4个 bit，超过15无法表达)。但该参数不是唯一决定对象晋升的条件。当 to 区不够或者改对象年龄已经达到了平均晋升值或者大对象等等条件。 |
| -XX:TargetSurvivorRatio={value}     | 决定对何时晋升的不仅只有 XX:MaxTenuringThreshold 参数，如果在 Survivor 空间中相同年龄所有对象大小的总和大鱼 Survivor 空间的一半（默认50%），年龄大于或等于该年龄的对象就可以直接进入老年代。无需在乎 XX:MaxTenuringThreshold参数。因此，MaxTenuringThreshold 只是对象晋升的最大年龄。如果将 TargetSurvivorRatio 设置的很小，对象将晋升的很快。 |
| -XX:PretenureSizeThresholds={value} | 除了年龄外，对象的体积也是影响晋升的一个关键，也就是大对象。如果一个对象新生代放不下，只能直接通过分配担保机制进入老年代。该参数是设置对象直接晋升到老年代的阈值，单位是字节。只要对象的大小大于此阈值，就会直接绕过新生代，直接进入老年代。注意： 这个参数只对 Serial 和 ParNew 有效，ParallelGC 无效 ，默认情况下该值为0，也就是不指定最大的晋升大小，一切有运行情况决定。 |
| -XX:-UseTLAB                        | 禁用线程本地分配缓存。TLAB 的全称是 Thread LocalAllocation Buffer ，即线程本地线程分配缓存，是一个线程私有的内存区域。该设计是为了加速对象分配速度。由于对象一般都是分配在堆上，而对是线程共享的。因此肯定有锁，虽然使用 CAS 的操作，但性能仍有优化空间。通过为每一个线程分配一个 TLAB 的空间（在 eden 区），可以消除多个线程同步的开销。默认开启。 |
| -XX:TLABSize                        | 指定 TLAB 的大小                                             |
| -XX:+PrintTLAB                      | 跟踪 TLAB 的使用情况。用以确定是用多大的 TLABSize。          |
| -XX:+ResizeTLAB                     | 自动调整 TLAB 大小。                                         |

### JVM工具参数：

| -XX:+PrintGCDateStamps            | 打印 GC 日志时间戳                      |
| --------------------------------- | --------------------------------------- |
| -XX:+PrintGCDetails               | 打印 GC 详情                            |
| -XX:+PrintGCTimeStamps            | 印此次垃圾回收距离jvm开始运行的所耗时间 |
| -Xloggc:<filename>                | 将垃圾回收信息输出到指定文件            |
| -verbose:gc                       | 打印 GC 日志                            |
| -XX:+PrintGCApplicationStopedTime | 查看 gc 造成的应用暂停时间              |
| XX:+PrintTenuringDistribution     | 对象晋升的日志                          |
| -XX:+HeapDumpOnOutOfMemoryError   | 内存溢出时输出 dump 文件                |

# ![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps31.jpg) 

## 工具使用

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps32.jpg) 

## 工具分类：

Main Tools

Language Shell

Security Tools

RMI Tools

Java IDL & RMI-IIOP Tools

Java Deployment Tools

Java Web Start

Java Web Services Tools

Scripting Tools

**Monitoring Tools（监控）**

**Troubleshooting Tools （故障排除）**

## 监控工具：

### JPS:JVM进程状态工具

jps的功能和unix/liunx中的ps命令是类似。只不过它是打印出正在运行的虚拟机进程，并显示虚拟机执行主类的名称以及这些进程的本地虚拟机唯一ID(Local Virtual Machine Identifier, LVMID,通常是系统进程ID)。

-q 不输出类名、Jar名和传入main方法的参数
-m 输出传入main方法的参数
-l 输出main类或Jar的全限名
-v 输出传入JVM的参数

### jinfo –实时查看和调整JVM配置参数

jinfo的作用是实时的查看和调整虚拟机各项参数。

jinfo - Configuration Info

jinfo -flag <name> PID

jinfo -flag [+|-]<name> PID – 布尔型参数

jinfo -flag <name>=<value> PID – 数字或字符型参数

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps33.jpg) 

### Jstat:JVM统计信息监视工具

查看Java进程状态信息、jstat是用于监视虚拟机各种运行状态信息的工具。它可以显示本地或远程虚拟机进程中类load,内存gc.jit等运行参数

jstat - Java Virtual Machine statistics monitoring tool

jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

支持的选项

class (类加载器) 

 compiler (JIT) 

 gc (GC堆状态)

 gccapacity (各区大小) 

 gccause (最近一次GC统计和原因) 

 gcnew (新区统计)

 gcnewcapacity (新区大小)

 gcold (老区统计)

 gcoldcapacity (老区大小)

 gcpermcapacity (永久区大小)

 gcutil (GC统计汇总)l

 printcompilation (HotSpot编译统计)

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps34.jpg) 

 

S0C、S1C、S0U、S1U：Survivor 0/1区容量（Capacity）和使用量（Used） EC、EU：Eden区容量和使用量 OC、OU：年老代容量和使用量 PC、PU：永久代容量和使用量 YGC、YGT：年轻代GC次数和GC耗时 FGC、FGCT：Full GC次数和Full GC耗时 GCT：GC总耗时

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps35.jpg) 

### jstatd–JVM的jstat守护进程

jstatd - Virtual Machine jstat Daemon

-在需要被监控的服务器上面，通过jstatd来启动RMI服务 

jstatd -J-Djava.security.policy=jstatd.all.policy 

或者

jstatd -J-Djava.security.policy=jstatd.all.policy -J-Djava.rmi.server.hostname=服务器IP

## Jmap：Java内存映射工具

jmap命令可以用于生产堆存储快照（dump文件）。它还可以查下finalize队列（自我拯救队列）、java堆和代码区的详细信息。

### jconsole -监视与管理

### Visualvm多合一故障处理工具

 

## 项目实战：

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps36.jpg) 

### 生产调优&总结：

 

 

调试：CPU、内存、IO

JVM》内存

分代的内存大小：

 

项目：

交易系统：400-600 》20万  800

调优>内存分配的问题》full gc

 

![img](file:///C:\Users\xuchang\AppData\Local\Temp\ksohtml612\wps37.jpg) 

 

 

 