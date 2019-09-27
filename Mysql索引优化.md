# Mysql 性能调优之sql和索引优化

------

​													主讲老师：fox



![1563945809178](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563945809178.png)



# Mysql索引优化

## 创建test表（测试表）

```sql
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `c1` varchar(10) DEFAULT NULL,
  `c2` varchar(10) DEFAULT NULL,
  `c3` varchar(10) DEFAULT NULL,
  `c4` varchar(10) DEFAULT NULL,
  `c5` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into test(c1,c2,c3,c4,c5) values('a1','a2','a3','a4','a5');
insert into test(c1,c2,c3,c4,c5) values('b1','b2','b3','b4','b5');
insert into test(c1,c2,c3,c4,c5) values('c1','c2','c3','c4','c5');
insert into test(c1,c2,c3,c4,c5) values('d1','d2','d3','d4','d5');
insert into test(c1,c2,c3,c4,c5) values('e1','e2','e3','e4','e5');
```

## **创建索引**

```sql
create index idx_test_c1234 on test(c1,c2,c3,c4);
show index from test;
```

![图片](https://uploader.shimo.im/f/zEoFv09NoRsXPSDF.png!thumbnail)

## **分析以下案例索引使用情况**

### Case 1：

```sql
explain select * from test where c1='a1' and c2='a2' and c3='a3' and c4='a4';

explain select * from test where c1='a1' and c4='a4' and c2='a2' and c3='a3' ;
```

![1563776112464](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776112464.png)

分析：
①创建复合索引的顺序为c1,c2,c3,c4。
②上述explain执行的结果都一样：type=ref，key_len=132，ref=const,const,const,const。
结论：在执行常量等值查询时，改变索引列的顺序并不会更改explain的执行结果，因为mysql底层优化器会进行优化，但是推荐按照索引顺序列编写sql语句。

### Case 2：

```sql
explain select * from test where c1='a1' and c2='a2';
```

![1563776305036](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776305036.png)

```sql
explain select * from test where c1='a1' and c2='a2' and c3>'a3' and c4='a4';
```

![1563776386460](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776386460.png)

分析：
当出现范围的时候，type=range，key_len=99，比不用范围key_len=66增加了，说明使用上了索引，但对比Case1中执行结果，说明c4上索引失效。
结论：范围右边索引列失效，但是范围当前位置（c3）的索引是有效的，从key_len=99可证明。

#### Case 2.1：

```sql
explain select * from test where c1='a1' and c2='a2' and c4>'a4' and c3='a3' ;
```

![1563776600233](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776600233.png)

分析：
与上面explain执行结果对比，key_len=132说明索引用到了4个，因为对此sql语句mysql底层优化器会进行优化：范围右边索引列失效（c4右边已经没有索引列了），注意索引的顺序（c1,c2,c3,c4），所以c4右边不会出现失效的索引列，因此4个索引全部用上。
结论：范围右边索引列失效，是有顺序的：c1,c2,c3,c4，如果c3有范围，则c4失效；如果c4有范围，则没有失效的索引列，从而会使用全部索引。

#### Case 2.2：

```sql
explain select * from test where c1>'a1' and c2='a2' and c3='a3' and c4='a4';
```

![1563776737273](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776737273.png)

分析：
如果在c1处使用范围，则type=ALL，key=Null，索引失效，全表扫描，这里违背了最左前缀法则，带头大哥已死，因为c1主要用于范围，而不是查询。
解决方式使用覆盖索引。

![1564057586409](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1564057586409.png)



![1563776871338](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563776871338.png)结论：在最左前缀法则中，如果最左前列（带头大哥）的索引失效，则后面的索引都失效。

### Case 3：

```sql
explain select * from test where c1='a1' and c2='a2'  and c4='a4' order by c3;
```

![1563777128061](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563777128061.png)

分析：
利用最左前缀法则：中间兄弟不能断，因此用到了c1和c2索引（查找），从key_len=66，ref=const,const，c3索引列用在排序过程中。

#### Case 3.1：

```sql
explain select * from test where c1='a1' and c2='a2' order by c3;
```

![1563777242893](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563777242893.png)

分析：
从explain的执行结果来看：key_len=66，ref=const,const，从而查找只用到c1和c2索引，c3索引用于排序。

#### Case 3.2：

```sql
explain select * from test where c1='a1' and c2='a2' order by c4;
```

![1563777434493](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563777434493.png)

分析：
从explain的执行结果来看：key_len=66，ref=const,const，查询使用了c1和c2索引，由于用了c4进行排序，跳过了c3，出现了Using filesort。

### Case 4：

```sql
explain select * from test where c1='a1' and c5='a5' order by c2,c3;
```

![1563777540668](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563777540668.png)

分析：
查找只用到索引c1，c2和c3用于排序，无Using filesort。

#### Case 4.1：

![1563777645937](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563777645937.png)

分析：
和Case 4中explain的执行结果一样，但是出现了Using filesort，因为索引的创建顺序为c1,c2,c3,c4，但是排序的时候c2和c3颠倒位置了。

#### Case 4.2：

```sql
explain select * from test where c1='a1' and c2='a2' order by c2,c3;
```

![1563778376108](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563778376108.png)

![1563778411496](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563778411496.png)

分析：
在查询时增加了c5，但是explain的执行结果一样，因为c5并未创建索引。

#### Case 4.3：

```sql
explain select * from test where c1='a1' and c2='a2' and c5='a5' order by c3,c2;
```

![1563778549902](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563778549902.png)

分析：
与Case 4.1对比，在Extra中并未出现Using filesort，因为c2为常量，在排序中被优化，所以索引未颠倒，不会出现Using filesort。

### Case 5：

```sql
explain select * from test where c1='a1' and c4='a4' group by c2,c3;
```

![1563778974365](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563778974365.png)

分析：
只用到c1上的索引，因为c4中间间断了，根据最左前缀法则，所以key_len=33，ref=const，表示只用到一个索引。

#### Case 5.1：

```sql
explain select * from test where c1='a1' and c4='a4' group by c3,c2;
```

![1563778789731](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563778789731.png)

分析：
对比Case 5，在group by时交换了c2和c3的位置，结果出现Using temporary和Using filesort，极度恶劣。原因：c3和c2与索引创建顺序相反。

### Case 6：

```sql
explain select * from test where c1>'a1' order by c1;
```

![1563779176739](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563779176739.png)

分析：
①在c1,c2,c3,c4上创建了索引，直接在c1上使用范围，导致了索引失效，全表扫描：type=ALL，ref=Null。因为此时c1主要用于排序，并不是查询。
②使用c1进行排序，出现了Using filesort。
③解决方法：使用覆盖索引。

![1563780204203](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563780204203.png)

![1564059569458](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1564059569458.png)

### Case 7：

```sql
explain select c1 from test order by c1 asc,c2 desc;
```

![1563780505952](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563780505952.png)

分析：
虽然排序的字段列与索引顺序一样，且order by默认升序，这里c2 desc变成了降序，导致与索引的排序方式不同，从而产生Using filesort。

### Case 8：

```sql
EXPLAIN extended select c1 from test where c1 in ('a1','b1') ORDER BY c2,c3;
```

![1563780908199](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563780908199.png)

分析：
对于排序来说，多个相等条件也是范围查询

## **总结：**

①MySQL支持两种方式的排序filesort和index，Using index是指MySQL扫描索引本身完成排序。index效率高，filesort效率低。
②order by满足两种情况会使用Using index。

- order by语句使用索引最左前列。
- 使用where子句与order by子句条件列组合满足索引最左前列。

③尽量在索引列上完成排序，遵循索引建立（索引创建的顺序）时的最左前缀法则。
④如果order by的条件不在索引列上，就会产生Using filesort。
⑤group by与order by很类似，其实质是先排序后分组，遵照索引创建顺序的最左前缀法则。注意where高于having，能写在where中的限定条件就不要去having限定了。

group by:    Using temporary ;Using filesort

order by:   Using filesort

思考：如何选择合适的列建立索引？

1.在where从句，group by从句，order by从句，on从句中出现的列
2.索引字段越小越好    
3.离散度大的列放到联合索引的前面

```sql
explain select * from payment where staff_id=2 and customer_id=584; 

思考： index(staff_id,customer_id)好？还是index(customer_id,staff_id)好？

select count(distinct customer_id),count(distinct staff_id) from payment;
+-----------------------------+--------------------------+
| count(distinct customer_id) | count(distinct staff_id) |
+-----------------------------+--------------------------+
|                         599 |                        2 |
+-----------------------------+--------------------------+

由于customer_id的离散度更大(重复率小,可选择性更大)，所以应该使用index(customer_id,staff_id)
```

![1562591850965](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562591850965.png)



## 索引的维护

### 查找重复及冗余索引

```sql
#冗余索引是指多个索引的前缀列相同，或是在联合索引中包含了主键的索引。如下：key(name,id)就是一个冗余索引
#可以删除冗余索引，达到优化效果。
create table test(
id int not null primary key,
name varchar(10) not null,
key(name,id)
)engine=innodb;

```

使用pt-duplicate-key-checker工具检查重复及冗余索引

安装

```bash
wget http://www.percona.com/downloads/percona-toolkit/2.2.4/percona-toolkit-2.2.4.tar.gz
tar -xzvf percona-toolkit-2.2.4.tar.gz
cd percona-toolkit-2.2.4
perl Makefile.PL
make && make install
#如果报错(Can't locate Time/HiRes.pm in @INC (@INC contains....)
yum -y install perl-Time-HiRes

#如果报错： Cannot connect to MySQL because the Perl DBD::mysql module is not installed or not found.
yum -y install perl-DBD-mysql
```

使用

```bash
pt-duplicate-key-checker -h127.0.0.1 -uroot -proot 
#指定数据库
pt-duplicate-key-checker -h127.0.0.1 -uroot -proot -dsakila
```

![1562594225181](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562594225181.png)



### 删除不用索引

在mysql中可以通过慢查日志配合pt-index-usage工具来进行索引使用情况分析。

```sql
pt-index-usage -h127.0.0.1 -uroot -proot  /data/mysql/hive-slow.log
```

![1562594843386](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562594843386.png)



# SQL优化

演示数据库：

Mysql官方提供的sakila数据库
下载地址： https://dev.mysql.com/doc/index-other.html

安装步骤： https://dev.mysql.com/doc/sakila/en/sakila-installation.html



## 慢查询

思考：如何发现有问题的SQL？

使用Mysql慢查询日志对有效率问题的SQL进行监控

```sql
# 查看包含log的参数
show variables like '%log%';

#查看慢查询日志是否开启
show variables like 'slow_query_log';

#查看慢查询日志存储位置
show variables like 'slow_query_log_file';

#开启慢查询日志
set global slow_query_log=on;

#指定慢查询日志存储位置
set global show_query_log_file='/data/mysql/hive-slow.log';

#记录没有使用索引的sql  开启慢查询日志
set global log_queries_not_using_indexes=on;

# 查看慢查询设置的时间  超过此时间记录到慢查询日志中
show variables like 'long_query_time';
#记录查询超过1s的sql
set global long_query_time=1;
```

测试

```sql
#执行sql
select sleep(3);
```

慢查询日志所包含的内容

```bash
#查看日志
[root@hive ~]# tail -f /data/mysql/hive-slow.log
/root/mysql/bin/mysqld, Version: 5.6.38 (MySQL Community Server (GPL)). started with:
Tcp port: 3306  Unix socket: /tmp/mysql.sock
Time                 Id Command    Argument
# Time: 190708  9:38:47     
# User@Host: root[root] @  [192.168.3.36]  Id:    40       //执行sql的主机信息
# Query_time: 3.000991  Lock_time: 0.000000 Rows_sent: 1  Rows_examined: 0  //sql的执行信息
SET timestamp=1562593127;   //sql执行时间
select sleep(3);     //sql的内容

```



### 慢查日志分析工具

#### mysqldumpslow

安装完MySQL后，默认就带了mysqldumpslow，mysql官方提供的一个常用工具。

```bash
#查看参数列表
mysqldumpslow -h

#分析慢查询日志中前三条比较慢的sql
mysqldumpslow -t 3 /data/mysql/hive-slow.log | more 
```

输出效果

```bash
[root@hive ~]# mysqldumpslow -t 3 /data/mysql/hive-slow.log | more

Reading mysql slow query log from /data/mysql/hive-slow.log
Died at /root/mysql/bin/mysqldumpslow line 161, <> chunk 1.
Count: 1  Time=3.00s (3s)  Lock=0.00s (0s)  Rows=1.0 (1), root[root]@[192.168.3.36]
  select sleep(N)

```

#### pt-query-digest

分析结果比mysqldumpslow更详细全面，它可以分析binlog、General log、slowlog，

安装

```bash
wget percona.com/get/pt-query-digest
chmod u+x pt-query-digest
mv /root/pt-query-digest /usr/bin/

#如果出现Can't locate Time/HiRes.pm in @INC 错误
yum -y install perl-Time-HiRes
```

使用

参考https://blog.csdn.net/a12345678n/article/details/81983951

```bash
#查看参数列表
pt-query-digest --help


[root@hive bin]# pt-query-digest /data/mysql/hive-slow.log | more 

# 50ms user time, 50ms system time, 20.09M rss, 165.33M vsz
# Current date: Mon Jul  8 10:35:26 2019
# Hostname: hive
# Files: /data/mysql/hive-slow.log
# Overall: 1 total, 1 unique, 0 QPS, 0x concurrency ______________________
# Time range: all events occurred at 2019-07-08 09:38:47
# Attribute          total     min     max     avg     95%  stddev  median
# ============     ======= ======= ======= ======= ======= ======= =======
# Exec time             3s      3s      3s      3s      3s       0      3s
# Lock time              0       0       0       0       0       0       0
# Rows sent              1       1       1       1       1       0       1
# Rows examine           0       0       0       0       0       0       0
# Query size            15      15      15      15      15       0      15

# Profile
# Rank Query ID                           Response time Calls R/Call V/M  
# ==== ================================== ============= ===== ====== =====
#    1 0x59A74D08D407B5EDF9A57DD5A41825CA 3.0010 100.0%     1 3.0010  0.00 SELECT

# Query 1: 0 QPS, 0x concurrency, ID 0x59A74D08D407B5EDF9A57DD5A41825CA at byte 0
# This item is included in the report because it matches --limit.
# Scores: V/M = 0.00
# Time range: all events occurred at 2019-07-08 09:38:47
# Attribute    pct   total     min     max     avg     95%  stddev  median
# ============ === ======= ======= ======= ======= ======= ======= =======
# Count        100       1
# Exec time    100      3s      3s      3s      3s      3s       0      3s
# Lock time      0       0       0       0       0       0       0       0
# Rows sent    100       1       1       1       1       1       0       1
# Rows examine   0       0       0       0       0       0       0       0
# Query size   100      15      15      15      15      15       0      15
# String:
# Hosts        192.168.3.36
# Users        root
# Query_time distribution
#   1us
#  10us
# 100us
#   1ms
#  10ms
# 100ms
#    1s  ################################################################
#  10s+
# EXPLAIN /*!50100 PARTITIONS*/
select sleep(3)\G

```

输出分为三部分：

1.显示除了日志的时间范围，以及总的sql数量和不同的sql数量
2.Response Time:响应时间占比 Calls:sql执行次数
3.sql的具体日志

思考：如何通过慢查询日志发现有问题的SQL？

```
1.查询次数多且每次查询占用时间长的SQL
通常为pt-query-digest分析的前几个查询

2.IO大的SQL（数据库主要瓶颈出现在IO层次）
注意pt-query-digest分析中的Rows examine项

3.未命中索引的SQL
注意pt-query-digest分析中的Rows examine和Rows Sent的对比
```



## 案例

### Count()和Max()的优化

```sql
#查询最后支付时间--优化max()函数
explain select max(payment_date) from payment;
#给payment_date建立索引(覆盖索引)
create index idx_paydate on payment(payment_date);
#删除索引
drop index  idx_release_year  on payment;
#显示索引
show index from payment;

#在一条SQL中同时查出2006年和2007年电影的数量--优化Count()函数
# count('任意内容')都会统计出所有记录数，因为count只有在遇见null时不计数，即count(null)==0
explain select count(release_year='2006' or null) as '2006年电影数量',count(release_year='2007' or null) as '2007年电影数量' from film;

#优化，为release_year列设置索引
create index idx_release_year on film(release_year);

```

**![1562576955825](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562576955825.png)**

![1562585792645](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562585792645.png)



### 子查询优化

通常情况下，需要把子查询优化为join查询，但在优化时要注意关联键是否有一对多的关系，要注意重复数据。

```sql
#查询sandra出演的所有影片
explain select title,release_year,length from film
where film_id in (
select film_id from film_actor where actor_id in (
select actor_id from actor where first_name='sandra'));

#优化之后
explain select title,release_year,length from film f
join film_actor fa on fa.film_id=f.film_id
join actor a on fa.actor_id = a.actor_id 
where a.first_name='sandra';

```

![1562587874179](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562587874179.png)

继续优化，将first_name设为索引

```sql
create index idx_first_name on actor(first_name);
```

![1562588119089](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562588119089.png)



### group by的优化

优化策略：

​     先给分组字段建索引

​     再对该表分组、分组后再和其他表关联查询

```sql
#每个演员参与影片的数量  
explain select a.first_name,a.last_name,count(*) from film_actor fa
inner join actor a using(actor_id)
group by fa.actor_id;

#优化后     子查询 索引
explain select a.first_name,a.last_name,c.cnt from actor a
inner join (
select actor_id,count(*) as cnt from film_actor group by actor_id) as c USING(actor_id)
```

![1562589195541](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562589195541.png)



### limit优化

limit常用于分页处理，时常会伴随order by 从句使用，因此大多时候会使用Filesorts这样会造成大量的IO问题。

避免数据量大时扫描过多的记录

```sql
# 分页查询影片描述信息
explain select film_id,description from film order by title limit 50,5;

# 优化1：使用有索引的列或主键进行order by操作（order by film_id）
# 页数越大，rows越大
explain select film_id,description from film order by film_id limit 50,5;

# 优化2:记录上次返回的主键，在下次查询的时候用主键过滤，避免了数据量大时扫描过多的记录
# 注意要求有序主键 或者建立有序辅助索引列
explain select film_id,description from film where film_id>55 and film_id<=60 order by film_id limit 1,5; 

```

![1562590326917](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562590326917.png)



### in和exsits优化

原则：**小表驱动大表**，即小的数据集驱动大的数据集

- in：当B表的数据集必须小于A表的数据集时，in优于exists 

  select * from A where id in (select id from B)

  ```sql
  explain select * from film where id in(select film_id from film_actor);
  ```

  ![1563788817761](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563788817761.png)

- exists：当A表的数据集小于B表的数据集时，exists优于in
  将主查询A的数据，放到子查询B中做条件验证，根据验证结果（true或false）来决定主查询的数据是否保留
  select * from A where exists (select 1 from B where B.id = A.id) #A表与B表的ID字段应建立索引

  ```sql
  explain select * from film where exists (select 1 from film_actor where film_actor.film_id = film.id)
  ```

  ![1563788922698](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563788922698.png)

1、EXISTS (subquery)只返回TRUE或FALSE,因此子查询中的SELECT * 也可以是SELECT 1或select X,官方说法是实际执行时会忽略SELECT清单,因此没有区别
2、EXISTS子查询的实际执行过程可能经过了优化而不是我们理解上的逐条对比
3、EXISTS子查询往往也可以用JOIN来代替，何种最优需要具体问题具体分析



### join 

对连接属性进行排序时，应当选择驱动表的属性作为排序表中的条件

```sql
explain select * from film join film_actor on film_actor.film_id=film.id order by film.id;
```

![1563892920716](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563892920716.png)

```sql
explain select * from film join film_actor on film_actor.film_id=film.id order by film_actor.film_id;
```

![1563892979123](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563892979123.png)

```sql
explain select name from film join film_actor on film_actor.film_id=film.id  order by film_actor.film_id;
```

![1563893106611](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1563893106611.png)

