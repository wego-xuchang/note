## MYSQL中文排序

### utf-8编码的mysql数据库 按照 中文来对 名称进行排序

如果存储姓名的字段采用的是GBK字符集，那就好办了，因为GBK内码编码时本身就采用了拼音排序的方法（常用一级汉字3755个采用拼音排序，二级汉字就不是了，但考虑到人名等都是常用汉字，因此只是针对一级汉字能正确排序也够用了）。

直接在查询语句后面 添加 order by name asc; 查询结果按照姓氏的升序排序；

但是如果在 utf-8的数据库中，如果我们针对名称进行排序，实际是无序的，例如

```sql
ORDER BY ENTERPRISE_NAME asc
```



解决方法

如果存储姓名的字段采用的是 utf8字符集，需要在排序的时候对字段进行转码；对于的代码是

order by convert(你的字段名 using gbk) asc; 同样，查询的结果也是按照姓氏的升序排序；

```sql
ORDER BY convert(ENTERPRISE_NAME using gbk) collate gbk_chinese_ci asc
```



### 其他排序规则

```java
//这个是可以 有 1-9 a-z 汉字 排序
SELECT * from bb where ischeck = 1 ORDER BY convert(zz using gbk) collate gbk_chinese_ci asc

//bb 表名
//zz 字段名
//这个只针对汉字
order by
      ELT(INTERVAL(CONV(HEX(left(CONVERT(storesname USING gbk),1)),16,10),
      0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,          
      0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,
      0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1),
      'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P',
       'Q','R','S','T','W','X','Y','Z') asc;

//如果要排除 非汉字 的 直接按 此条件 排序
group by
      ELT(INTERVAL(CONV(HEX(left(CONVERT(storesname USING gbk),1)),16,10),
      0xB0A1,0xB0C5,0xB2C1,0xB4EE,0xB6EA,0xB7A2,0xB8C1,0xB9FE,0xBBF7,          
      0xBFA6,0xC0AC,0xC2E8,0xC4C3,0xC5B6,0xC5BE,0xC6DA,0xC8BB,0xC8F6,
      0xCBFA,0xCDDA,0xCEF4,0xD1B9,0xD4D1),
      'A','B','C','D','E','F','G','H','J','K','L','M','N','O','P',
       'Q','R','S','T','W','X','Y','Z')

```

