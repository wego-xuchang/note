# 深入理解Mysql锁与事务隔离级别

​																主讲老师： fox

​																

### 1 锁的定义

锁是计算机协调多个进程或线程并发访问某一资源的机制。

在数据库中，除了传统的计算资源（如CPU、RAM、I/O等）的争用以外，数据也是一种供需要用户共享的资源。如何保证数据并发访问的一致性、有效性是所有数据库必须解决的一个问题，锁冲突也是影响数据库并发访问性能的一个重要因素。从这个角度来说，锁对数据库而言显得尤其重要，也更加复杂。



### 2 锁的分类

- 从性能上分为乐观锁(用版本对比来实现)和悲观锁

  update ticket  set count=count-1,version=version+1   where id=1  and version=2

- 从对数据库操作的类型分，分为读锁和写锁(都属于悲观锁)

  - 读锁（共享锁）：针对同一份数据，多个读操作可以同时进行而不会互相影响

  - 写锁（排它锁）：当前写操作没有完成前，它会阻断其他写锁和读锁

- 从对数据操作的粒度分，分为表锁，行锁和页面锁
  - 表级锁：开销小，加锁快；不会出现死锁；锁定粒度大，发生锁冲突的概率最高，并发度最低。
  - 行级锁：开销大，加锁慢；会出现死锁；锁定粒度最小，发生锁冲突的概率最低，并发度也最高。
  - 页面锁：开销和加锁时间界于表锁和行锁之间；会出现死锁；锁定粒度界于表锁和行锁之间，并发度一般

存储引擎支持情况：

![img](https://img2018.cnblogs.com/blog/1044429/201811/1044429-20181102154150537-672525342.png)

死锁: 多个进程互相等待对方锁的释放

锁冲突：一个进程等待另一个进程释放需要的锁



### 3 MyISAM的表锁（偏读）

mysql5.5之前默认支持的存储引擎

MySQL的表级锁有两种模式：

- 表共享读锁（Table Read Lock)
- 表独占写锁（Table Write Lock)

ＭySQL中的表锁兼容性：

![1561886358123](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1561886358123.png)

对ＭyISAM表的读操作，不会阻塞其他用户对同一表的读请求，但会阻塞对同一表的写请求；对ＭyISAM表的写操作，则会阻塞其他用户对同一表的读和写请求；ＭyISAM表的读和写操作之间，以及写和写操作之间是**串行**的！（**当一线程获得对一个表的写锁后，只有持有锁的线程可以对表进行更新操作。其他线程的读、写操作都会等待，直到锁被释放为止。**）

#### 3.1 基本操作

- 建表SQL

```sql
CREATE TABLE `mylock` (
`id` INT (11) NOT NULL AUTO_INCREMENT,
`NAME` VARCHAR (20) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE = MyISAM DEFAULT CHARSET = utf8;
```

- 插入数据

```sql
INSERT INTO `mylock` (`id`, `NAME`) VALUES ('1', 'a');
INSERT INTO `mylock` (`id`, `NAME`) VALUES ('2', 'b');
INSERT INTO `mylock` (`id`, `NAME`) VALUES ('3', 'c');
INSERT INTO `mylock` (`id`, `NAME`) VALUES ('4', 'd');
```

- 手动增加表锁

lock table 表名称 read(write),表名称2 read(write);

```sql
lock table mylock read;
```

- 查看表上加过的锁

```sql
show open tables;
show open tables where in_use >=1;
show open tables where `table`='mylock';
```

​	show open tables; 会返回以下字段：

​    **DataBase**：含有该表的数据库

​    **Table** ：表名称

​    **In_use**：表当前被查询使用的次数。如果该数为零，则表是打开的，但是当前没有被使用。

​    **Name_locked**：表名称是否被锁定。名称锁定用于取消表或对表进行重命名等操作。

- 删除表锁

```sql
unlock tables;
```



#### 3.2 案例分析

**加读锁**

- 当前session和其他session都可以读该表
- 当前session中增删改该表都会报错，其他session增删改则会等待
- 当前session中增删改查其他表会报错，其他session可以对其他表增删改查

![1563368127751](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563368127751.png)

![1563368481540](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563368481540.png)

**加写锁**

- 当前session对该表的增删改查都没有问题，其他session对该表的所有操作被阻塞
- 当前session中增删改查其他表会报错，其他session可以对其他表增删改查

![1563369051411](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563369051411.png)



#### 3.3 结论

MyISAM在执行查询语句(SELECT)前,会自动给涉及的所有表加读锁,在执行增删改操作前,会自动给涉及的表加写锁。

1、对MyISAM表的读操作(加读锁) ,不会阻寒其他进程对同一表的读请求,但会阻赛对同一表的写请求。只有当读锁释放后,才会执行其它进程的写操作。

2、对MylSAM表的写操作(加写锁) ,会阻塞其他进程对同一表的读和写操作,只有当写锁释放后,才会执行其它进程的读写操作

简而言之，就是**读锁会阻塞写，但是不会阻塞读。而写锁则会把读和写都阻塞**。



### 4 InnoDb的行锁(偏写)

- InnoDb支持表锁和行锁，**开启一个新的事务会释放表锁**。

- InnoDB支持**事务**（TRANSACTION）。   

思考： 修改表字段，因为表数据量大，导致大量用户阻塞，无法访问，如何解决？

 innodb 在 ddl 的时候所执行的操作：

```
1. 按照原始表 (original_table) 的表结构和 ddl 语句，新建一个不可见的临时表 (temporary_table)

2. 在原表上面加上 WRITE LOCK 阻塞所有的更新操作 (insert、delete、update等操作)

3. 执行 insert into tmp_table select * from original_table

4. rename original_table 和 tmp_table 最后 drop original_table

5. 最后释放掉 write lock
```

 **pt-online-schema-change** 可以在不阻塞写入的情况下改动数据表：

```
1. 首先创建一个和你要执行的 alter 操作的表一样的空的表结构。

2. 执行我们赋予的表结构的修改，然后 copy 原表中的数据到新表里面。

3. 在原表上创建一个触发器在数据 copy 的过程中，将原表的更新数据的操作全部更新到新的表中来。 这里特别注意一下，如果原表中已经定义了触发器那么工具就不能工作了，因为 pt 使用到了数据库的触发器。

4. copy 完成之后，用 rename table 新表代替原表，默认删除原表。
```



#### 4.1 行锁支持事务

##### 4.1.1 事务ACID特性

事务是由一组SQL语句组成的逻辑处理单元,事务具有以下4个特性,通常简称为事务的ACID特性。

- **A: 原子性(atomicity)**

  ​	一个事务（transaction）中的所有操作，要么全部完成，要么全部不完成，不会结束在中间某个环节。事务在执行过程中发生错误，会被回滚（Rollback）到事务开始前的状态，就像这个事务从来没有执行过一样。

- **C: 一致性(consistency)**

  ​	在事务开始之前和事务结束以后，数据库的完整性没有被破坏。这表示写入的资料必须完全符合所有的预设规则，这包含资料的精确度、串联性以及后续数据库可以自发性地完成预定的工作。

- **I:  隔离性(isolation)**

  ​	数据库允许多个并发事务同时对其数据进行读写和修改的能力，隔离性可以防止多个事务并发执行时由于交叉执行而导致数据的不一致。事务隔离分为不同级别，包括读未提交（Read uncommitted）、读已提交（read committed）、可重复读（repeatable read）和串行化（Serializable）。

- **D: 持久性(durability)**

  ​	事务处理结束后，对数据的修改就是永久的，即便系统故障也不会丢失。

##### 4.1.2 并发事务处理带来的问题

- **更新丢失（Lost Update）**　　

  当两个或多个事务选择同一行，然后基于最初选定的值更新该行时，由于每个事务都不知道其他事务的存在，就会发生丢失更新问题–最后的更新覆盖了由其他事务所做的更新。

- **脏读（Dirty Reads）**　　

  一个事务正在对一条记录做修改，在这个事务完成并提交前，这条记录的数据就处于不一致的状态；这时，另一个事务也来读取同一条记录，如果不加控制，第二个事务读取了这些“脏”数据，并据此作进一步的处理，就会产生未提交的数据依赖关系。这种现象被形象的叫做“脏读”。

  一句话：事务A读取到了事务B已经修改但尚未提交的数据，还在这个数据基础上做了操作。此时，如果B事务回滚，A读取的数据无效，不符合一致性要求。

- **不可重读（Non-Repeatable Reads）** 

  一个事务在读取某些数据后的某个时间，再次读取以前读过的数据，却发现其读出的数据已经发生了改变、或某些记录已经被删除了！这种现象就叫做“不可重复读”。　　

  一句话：事务A读取到了事务B已经提交的修改数据，不符合隔离性

- **幻读（Phantom Reads）**

  一个事务按相同的查询条件重新读取以前检索过的数据，却发现其他事务插入了满足其查询条件的新数据，这种现象就称为“幻读”。

  一句话：事务A读取到了事务B提交的新增数据，不符合隔离性

  **脏读是事务B里面修改了数据**

  **幻读是事务B里面新增了数据**

##### 4.1.3 事务隔离级别

脏读”、“不可重复读”和“幻读”,其实都是数据库读一致性问题,必须由数据库提供一定的事务隔离机制来解决。

![1562035600118](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562035600118.png)

数据库的事务隔离越严格,并发副作用越小,但付出的代价也就越大,因为事务隔离实质上就是使事务在一定程度上“串行化”进行,这显然与“并发”是矛盾的。

同时,不同的应用对读一致性和事务隔离程度的要求也是不同的,比如许多应用对“不可重复读"和“幻读”并不敏感,可能更关心数据并发访问的能力。

```sql
#常看当前数据库的事务隔离级别
show variables like 'tx_isolation';

#查看当前会话隔离级别
select @@tx_isolation;

#查看系统当前隔离级别
select @@global.tx_isolation;

#设置为读未提交
set tx_isolation='read-uncommitted';  

#设置为读已提交
set tx_isolation='read-committed';  

#设置为可重复读
set tx_isolation='REPEATABLE-READ';   

#设置为串行化
set tx_isolation='SERIALIZABLE';


```

#### 4.2 案例分析

```sql
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `balance` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `account` (`name`, `balance`) VALUES ('fox', '600');
INSERT INTO `account` (`name`, `balance`) VALUES ('monkey', '1000');
```

##### 4.2.1 读未提交

（1）打开一个客户端A，并设置当前事务模式为read uncommitted（未提交读），查询表account的初始值：

```
 set tx_isolation='read-uncommitted';
```

![1563370846072](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563370846072.png)
　　　　

（2）在客户端A的事务提交之前，打开另一个客户端B，更新表account： 

```sql
update account set balance=balance-50 where id=1;
```

![1563370976115](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563370976115.png)
 　　　　

（3）这时，虽然客户端B的事务还没提交，但是客户端A就可以查询到B已经更新的数据： 
![1563371081365](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563371081365.png)
　　　　

（4）一旦客户端B的事务因为某种原因回滚，所有的操作都将会被撤销，那客户端A查询到的数据其实就是脏数据： 

![1563371326636](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563371326636.png)
 　　　　

（5）在客户端A执行更新语句update account set balance = balance - 50 where id =1，

更新后结果没变，产生了脏读问题。

![1563371488411](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563371488411.png).



##### 4.2.2 读已提交

（1）打开一个客户端A，并设置当前事务模式为read committed（未提交读），查询表account的所有记录：

```sql
 set tx_isolation='read-committed';
```

![1563371932473](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563371932473.png)
　　　　

（2）在客户端A的事务提交之前，打开另一个客户端B，更新表account： 

![1563371994964](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563371994964.png).


　　　　

（3）这时，客户端B的事务还没提交，客户端A不能查询到B已经更新的数据，解决了脏读问题：

 ![1563372042833](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372042833.png)

　　　　

（4）客户端B的事务提交

![1563372111339](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372111339.png)
　　　　

（5）客户端A执行与上一步相同的查询，结果 与上一步不一致，即产生了不可重复读的问题

![1563372179832](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372179832.png).



##### 4.2.3  可重复读

（1）打开一个客户端A，并设置当前事务模式为repeatable read，查询表account的所有记录

```sql
set tx_isolation='repeatable-read';
```

![1563372716043](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372716043.png)
　　　　

（2）在客户端A的事务提交之前，打开另一个客户端B，更新表account并提交

![1563372678804](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372678804.png).

　　　　

（3）在客户端A查询表account的所有记录，与步骤（1）查询结果一致，没有出现不可重复读的问题

![1563372816856](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372816856.png)
　　　　

（4）在客户端A，接着执行update balance = balance - 50 where id = 1，balance没有变成500-50=450，fox的balance值用的是步骤（2）中的450来算的，所以是400，数据的一致性倒是没有被破坏。可重复读的隔离级别下使用了MVCC机制，select操作不会更新版本号，是快照读（历史版本）；insert、update和delete会更新版本号，是当前读（当前版本）。

![1563372972364](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563372972364.png).

（5）重新打开客户端B，插入一条新数据后提交

```sql
INSERT INTO `account` (`name`, `balance`) VALUES ('lion', '1000');
```

![1563373167224](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563373167224.png).

（6）在客户端A查询表account的所有记录，没有 查出 新增数据，所以没有出现幻读

![1563373239886](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563373239886.png).


 (7)验证幻读

在客户端A执行update account set balance=balance-50 where id = 3;能更新成功，再次查询能查到客户端B新增的数据

幻读前提：更新操作包含了事务B新插入的数据

![1563373452334](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563373452334.png).



##### 4.2.4 串行化

（1）打开一个客户端A，并设置当前事务模式为serializable，查询表account的初始值：

```sql
set tx_isolation='serializable';
```

![1563373548604](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563373548604.png).

（2）打开一个客户端B，并设置当前事务模式为serializable，插入一条记录报错，表被锁了插入失败，mysql中事务隔离级别为serializable时会锁表，因此不会出现幻读的情况，这种隔离级别并发性极低，开发中很少会用到。         读锁-----写操作---写锁

```sql
INSERT INTO `account` (`name`, `balance`) VALUES ('panda', '1000');
```

![1563373721886](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563373721886.png).

思考 ：Mysql默认级别是repeatable-read，有办法解决幻读问题吗？

**间隙锁在某些情况下可以解决幻读问题**

要避免幻读可以用间隙锁，在Session_1下面执行

```sql
update account set name = 'duck' where id > 10 and id <=20;
```

则其他Session没法插入这个范围内的数据

![1563374048928](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563374048928.png)

#### 4.3 结论　　

Innodb存储引擎由于实现了行级锁定，虽然在锁定机制的实现方面所带来的性能损耗可能比表级锁定会要更高一下，但是在整体并发处理能力方面要远远优于MYISAM的表级锁定的。当系统并发量高的时候，Innodb的整体性能和MYISAM相比就会有比较明显的优势了。　　

但是，Innodb的行级锁定同样也有其脆弱的一面，当我们使用不当的时候，可能会让Innodb的整体性能表现不仅不能比MYISAM高，甚至可能会更差。



#### 4.4 行锁分析

通过检查InnoDB_row_lock状态变量来分析系统上的行锁的争夺情况

```sql
show status like 'innodb_row_lock%';
```

对各个状态量的说明如下：

```
Innodb_row_lock_current_waits: 当前正在等待锁定的数量

Innodb_row_lock_time: 从系统启动到现在锁定总时间长度

Innodb_row_lock_time_avg: 每次等待所花平均时间

Innodb_row_lock_time_max：从系统启动到现在等待最长的一次所花时间

Innodb_row_lock_waits:系统启动后到现在总共等待的次数
```

对于这5个状态变量，比较重要的主要是：

```
Innodb_row_lock_time_avg （等待平均时长）

Innodb_row_lock_waits （等待总次数）

Innodb_row_lock_time（等待总时长）
```

尤其是当等待次数很高，而且每次等待时长也不小的时候，我们就需要分析系统中为什么会有如此多的等待，然后根据分析结果着手制定优化计划。



### 5 for update

行锁---表锁       对当前数据加锁

```
思考：如果没查到记录会锁表吗？
基于是否有索引，是否是all range
```

存在高并发并且对于数据的准确性很有要求的场景

- for update 仅适用于InnoDB，并且必须开启事务，在begin与commit之间才生效。

- 当有明确指定的主键时候，是行级锁。否则是表级锁

  ```sql
  例1: (明确指定主键，并且有此记录，行级锁) 
  SELECT * FROM account WHERE id=1 FOR UPDATE; 
  
  例2: (明确指定主键/索引，若查无此记录，无锁) 
  SELECT * FROM account WHERE id=-1 FOR UPDATE;
  
  例3: (无主键/索引，表级锁)   all
  SELECT * FROM account WHERE name='fox' FOR UPDATE;
  
  例4: (主键/索引不明确，表级锁)    range
  SELECT * FROM account WHERE id<>'3' FOR UPDATE; 
  SELECT * FROM account WHERE id LIKE '3' FOR UPDATE;
  
  ```

  ![1563457413164](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563457413164.png)

死锁演示

```sql
set tx_isolation='repeatable-read';

Session_1执行：select * from account where id=1 for update;

Session_2执行：select * from account where id=2 for update;

Session_1执行：select * from account where id=2 for update;

Session_2执行：select * from account where id=1 for update;

查看近期死锁日志信息：show engine innodb status\G; 
```

大多数情况mysql可以自动检测死锁并回滚产生死锁的那个事务，但是有些情况mysql没法自动检测死锁

![1563458248314](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563458248314.png)



![1563459603648](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563459603648.png)

### 6 优化建议

- 尽可能让所有数据检索都通过索引来完成，避免无索引行锁升级为表锁

- 合理设计索引，尽量缩小锁的范围

- 尽可能减少检索条件，避免间隙锁

- 尽量控制事务大小，减少锁定资源量和时间长度

- 尽可能低级别事务隔离