# Mysql 性能调优之sql语句和索引优化

​																主讲老师：fox



### Mysql使用

连接远程mysql

> mysql -h192.168.3.36 -uroot -proot

授权远程登录

>  mysql> grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;

其他操作

```sql
# 查询mysql版本   
mysql> select @@version;
# 查看端口
mysql> show global variables like 'port';
# 查看所有数据库
mysql> show databases;
# 使用数据库
mysql> use test;
# 查看当前数据库所有表
mysql> show tables;
```



### SQL语句优化

演示数据库：

Mysql官方提供的sakila数据库
下载地址： https://dev.mysql.com/doc/index-other.html

安装步骤： https://dev.mysql.com/doc/sakila/en/sakila-installation.html



#### 慢查询

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



#### 慢查日志分析工具

##### mysqldumpslow

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

##### pt-query-digest

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



#### 执行计划

思考： 如何分析sql查询？

通过explain查询和分析SQL的执行计划

使用EXPLAIN关键字可以模拟优化器执行SQL语句，从而知道MySQL是 如何处理你的SQL语句的，分析你的查询语句或者表结构的性能瓶颈。 

语法 ：Explain + SQL

 在 select 语句之前增加 explain 关键字，MySQL 会在查询上设置一个标记，执行查询时，会返回执行计划的信息，而不是执行这条SQL（如果 from 中包含子查询，仍会执行该子查询，将结果放入临时表中）

```sql
mysql> explain select customer_id,first_name,last_name from customer;
```

![1562575572384](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562575572384.png)

执行计划作用:

- 表的读取顺序数据
- 读取操作的操作类型
- 哪些索引可以使用
- 哪些索引被实际使用
- 表之间的引用
- 每张表有多少行被优化器查询



explain详情

| 列名              | 说明                                                         |
| ----------------- | :----------------------------------------------------------- |
| **id**            | 执行编号，标识select所属的行。如果在语句中没子查询或关联查询，只有唯一的select，每行都将显示1。id列越大执行优先级越高，id相同则从上往下执行，id为NULL最后执行 |
| **select_type**   | SELECT类型,可以为以下任何一种:                                                                                   - **SIMPLE**:简单SELECT(不使用UNION或子查询)                                                          **PRIMARY**:最外面的SELECT                                                                                    **UNION**:UNION中的第二个或后面的SELECT语句                                                       **DEPENDENT UNION**:UNION中的第二个或后面的SELECT语句,取决于外面的查询    **UNION RESULT**:UNION 的结果                                                                                                 **SUBQUERY**:子查询中的第一个SELECT                                                                       **DEPENDENT SUBQUERY**:子查询中的第一个SELECT,取决于外面的查询                **DERIVED**:导出表的SELECT(FROM子句的子查询)                                             **MATERIALIZED**：被物化的子查询 |
| **table**         | 输出的行所引用的表                                           |
| **type**          | 联接类型。下面给出各种联接类型,按照从最佳类型到最坏类型进行排序:                       **Null**:   意味说mysql能在优化阶段分解查询语句，在执行阶段甚至用不到访问表或索引（高效）                                                                                                                                    **system**:表仅有一行(=系统表)。这是const联接类型的一个特例。                                                     **const**:表最多有一个匹配行,它将在查询开始时被读取。因为仅有一行,在这行的列值可被优化器剩余部分认为是常数。const表很快,因为它们只读取一次!                                                     **eq_ref**:对于每个来自于前面的表的行组合,从该表中读取一行。这可能是最好的联接类型,除了const类型。                                                                                                                           **ref**:对于每个来自于前面的表的行组合,所有有匹配索引值的行将从这张表中读取。**ref_or_null**:该联接类型如同ref,但是添加了MySQL可以专门搜索包含NULL值的行。**index_merge**:该联接类型表示使用了索引合并优化方法。                        **unique_subquery**:该类型替换了下面形式的IN子查询的ref: value IN (SELECT primary_key FROM single_table WHERE some_expr) unique_subquery是一个索引查找函数,可以完全替换子查询,效率更高。                                                              **index_subquery**:该联接类型类似于unique_subquery。可以替换IN子查询,但只适合下列形式的子查询中的非唯一索引: value IN (SELECT key_column FROM single_table WHERE some_expr)                                                                                                                             **range**:范围扫描，一个有限制的索引扫描。key 列显示使用了哪个索引。当使用=、 <>、>、>=、<、<=、IS NULL、<=>、BETWEEN 或者 IN 操作符,用常量比较关键字列时,可以使用 range                                                                                                                                **index**:该联接类型与ALL相同,除了只有索引树被扫描。这通常比ALL快,因为索引文件通常比数据文件小。                                                                                                                       **ALL**:对于每个来自于先前的表的行组合,进行完整的表扫描。 |
| **possible_keys** | 指出MySQL能使用哪个索引在该表中找到行。 如果为null，没有可能的索引 |
| **key**           | 显示MySQL实际决定使用的键(索引)。如果没有选择索引,键是NULL。 |
| **key_len**       | 显示MySQL决定使用的键长度。如果键是NULL,则长度为NULL。 在不损失精度的情况下，越短越好 |
| **ref**           | 显示使用哪个列或常数。与key一起从表中选择行。                |
| **rows**          | 显示MySQL认为它执行查询时必须检查的行数。                    |
| **filtered**      | 显示了通过条件过滤出的行数的百分比估计值。                   |
| **Extra**         | 该列包含MySQL解决查询的详细信息                                                                   **Distinct**:MySQL发现第1个匹配行后,停止为当前的行组合搜索更多的行。                           **Not exists**:MySQL能够对查询进行LEFT JOIN优化,发现1个匹配LEFT JOIN标准的行后,不再为前面的的行组合在该表内检查更多的行。                                                                          **range checked for each record (index map: #)**:MySQL没有发现好的可以使用的索引,但发现如果来自前面的表的列值已知,可能部分索引可以使用。                                           **Using filesort**:MySQL需要额外的一次传递,以找出如何按排序顺序检索行。                           **Using index**:从只使用索引树中的信息而不需要进一步搜索读取实际的行来检索表中的列信息。如果同时出现using where，表明索引被用来执行索引键值的查找，没有using where，表明索引用来读取数据而非执行查找动作。这是MySQL服务层完成的，但无需再回表查询记录。                                                                                                                                          **Using index condition**： 这是MySQL 5.6出来的新特性，叫做“索引条件推送”。简单说一点就是MySQL原来在索引上是不能执行如like这样的操作的，但是现在可以了，这样减少了不必要的IO操作，但是只能用在二级索引上。                                                                                                                       **Using temporary**:为了解决查询,MySQL需要创建一个临时表来容纳结果。                  **Using where**:WHERE 子句用于限制哪一个行匹配下一个表或发送到客户。                   **Using sort_union(...), Using union(...), Using intersect(...)**:这些函数说明如何为index_merge联接类型合并索引扫描。                                                                                      **Using index for group-by**:类似于访问表的Using index方式,Using index for group-by表示MySQL发现了一个索引,可以用来查 询GROUP BY或DISTINCT查询的所有列,而不要额外搜索硬盘访问实际的表。                                                                                                              **select tables optimized away**：在没有GROUP BY子句的情况下，基于索引优化MIN/MAX操作，或者对于MyISAM存储引擎优化COUNT(*)操作，不必等到执行阶段再进行计算，查询执行计划生成的阶段即完成优化。                                                                     **impossible where**：where子句的值总是false，不能用来获取任何元组                      **Using join buffer**： 使用了连接缓存：Block Nested Loop，连接算法是块嵌套循环连接;Batched Key Access，连接算法是批量索引连接 |



**extra列需要注意的返回值**

**Using filesort: 看到这个的时候，查询就需要优化了。mysql需要进行额外的步骤来发现如何对返回的行排序。它根据连接类型已经存储排序键值和匹配条件的全部行的行指针来排序全部行。无法利用索引完成排序，建议添加适当的索引**

**Using temporary：看到这个的时候，查询需要优化了。这里mysql需要创建一个临时表来存储结果，这通常发生在对不同的列集进行order by上，而不是group by上 。order by的列没有索引，建议添加适当的索引**

**Using where：通常是因为全表扫描或全索引扫描时（type 列显示为 ALL 或 index），又加上了WHERE条件，建议添加适当的索引。**



#### **Count()和Max()的优化**

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



#### 子查询优化

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



#### group by的优化

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



#### limit优化

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



#### in和exsits优化

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



### 索引优化

#### 如何选择合适的列建立索引？

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



#### 索引优化SQL的方法

**索引的维护及优化--重复及冗余索引**

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



**索引维护的方法--删除不用索引**

目前mysql中还没有记录索引的使用情况，但是在PerconMySQL和MariaDB中可通过INDEX_STATISTICS表来查看哪些索引未使用，但在mysql中目前只能通过慢查日志配合pt-index-usage工具来进行索引使用情况分析。

```sql
pt-index-usage -h127.0.0.1 -uroot -proot  /data/mysql/hive-slow.log
```

![1562594843386](C:\Users\chaos\AppData\Roaming\Typora\typora-user-images\1562594843386.png)



