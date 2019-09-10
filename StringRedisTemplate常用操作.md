

### **StringRedisTemplate常用操作**

```java
stringRedisTemplate.opsForValue().set("test", "100",60*10,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间  

stringRedisTemplate.boundValueOps("test").increment(-1);//val做-1操作

stringRedisTemplate.opsForValue().get("test")//根据key获取缓存中的val

stringRedisTemplate.boundValueOps("test").increment(1);//val +1

stringRedisTemplate.getExpire("test")//根据key获取过期时间

stringRedisTemplate.getExpire("test",TimeUnit.SECONDS)//根据key获取过期时间并换算成指定单位 

stringRedisTemplate.delete("test");//根据key删除缓存

stringRedisTemplate.hasKey("546545");//检查key是否存在，返回boolean值 

stringRedisTemplate.opsForSet().add("red_123", "1","2","3");//向指定key中存放set集合

stringRedisTemplate.expire("red_123",1000 , TimeUnit.MILLISECONDS);//设置过期时间

stringRedisTemplate.opsForSet().isMember("red_123", "1")//根据key查看集合中是否存在指定数据

stringRedisTemplate.opsForSet().members("red_123");//根据key获取set集合

//赋值value
stringTemplate.set("1");
//设置 key的缓存时间
// 可以先添加对应的value,然后在设置缓存时间
Boolean expire = stringTemplate.expire(1, TimeUnit.DAYS);
System.out.println(key+"设置缓存时间是否成功:"+expire);
//获取缓存时间
Long expire1 = stringTemplate.getExpire();
System.out.println(expire1);
//设置具体的到期时间,并且返回是否设置成功,例如,设定当天8点,8点后就会自动销毁
Boolean expireTime = stringTemplate.expireAt(new Date());
System.out.println("是否设置成功:"+expireTime);
//获取剩余过期时间
Long expire3 = stringTemplate.getExpire();
System.out.println("剩余到期时间:"+expire3);
//删除key的到期时间,并且返回是否删除成功
Boolean persist = stringTemplate.persist();
System.out.println("删除key的缓存时间是否成功:"+persist);
//获取剩余过期时间
Long expire2 = stringTemplate.getExpire();
System.out.println("key的缓存时间为"+expire2);
String value = stringTemplate.get();
System.out.println("原来的value="+value+",长度为:"+value.length());
//在value值的基础上进行追加添加,并且返回新的字符串长度
Integer addComment = stringTemplate.append("2");
String s = stringTemplate.get();
System.out.println("新的value="+s+",新的字符串长度为"+addComment);
String value1 = stringTemplate.get();
System.out.println("原来的value="+value1);
//如果value 是number类型,增加value的值,如 12变为 12+param,并且返回新的value
// 方法重载,可以传递2中参数,一个是 long还有一个是double,分别返回对应参数的类型 value
Long increment = stringTemplate.increment(1);
System.out.println("value增加参数后的值为:"+increment);
String s1 = stringTemplate.get();
System.out.println("新的value="+s1);
//获取value的长度
Long size = stringTemplate.size();
System.out.println("value的长度为"+size);
//删除key
redisTemplate.delete(key);
//如果key没有设置value的话,那么参数设置为value
Boolean asd = stringTemplate.setIfAbsent("123");
System.out.println("新的value="+stringTemplate.get());
System.out.println("旧的key为:"+stringTemplate.getKey());
//替换key的名称
stringTemplate.rename("newhexiaowu");
System.out.println("新的key为:"+stringTemplate.getKey());
```







| 方法名                                         | 方法描述                                                     |
| ---------------------------------------------- | ------------------------------------------------------------ |
| void set(V value)                              | 设定key对应的vlaue值                                         |
| void set(V value,long offset)                  | 将value值从第offset位开始替换                                |
| void set(V value, long timeout, TimeUnit unit) | 设置value的超时时间,timeout为数字,unit为单位,例如天,小时等   |
| Boolean setIfAbsent(V value)                   | 判断key是否有对应的value,如果有,则返回false,如果没有,添加,返回true |
| V get()                                        | 返回key对应的value                                           |
| String get(long start, long end)               | 从start开始,到end结束,截取value的值                          |
| V getAndSet(V value)                           | 替换value的值,并且返回value的旧值                            |
| Long increment(long delta)                     | 如果value是数字类型的字符串,那么增加delta,并且返回新的value  |
| Double increment(double delta)                 | 如果value是数字类型的字符串,那么增加delta,并且返回新的value  |
| Integer append(String value)                   | 在value值后面进行添加,并且返回新value的长度                  |
| Long size()                                    | 返回value的长度                                              |
| Boolean expire(long var1, TimeUnit var3)       | 设置key的缓存时间,var1为数字,unit为单位,例如天,小时等,返回是否设置成功 |
| Boolean expireAt(Date var1)                    | 设置key的具体到期时间,并且返回是否设置成功                   |
| Long getExpire()                               | 返回key的剩余缓存时间,单位:秒                                |
| K getKey()                                     | 返回key的名称                                                |
| DataType getType()                             | 获取key的类型                                                |
| Boolean persist()                              | 删除key的缓存时间                                            |
| void rename(K var1)                            | 修改key的名称                                                |





失败代码

```java
//        Map<String ,Object>  paramMap = new HashMap<>();
//        List<String> ulistIds = Arrays.asList(userIdList);
//
//        paramMap.put("userIdList",ulistIds);
//        paramMap.put("startDate",startDate);
//        paramMap.put("endDate",endDate);
//        stringRedisTemplate.opsForHash().putAll("paramMap",paramMap);

//        stringRedisTemplate.opsForList().rightPushAll("",Array)
//        stringRedisTemplate.opsForValue().set("startDate",startDate);
//        stringRedisTemplate.opsForValue().set("endDate",endDate);
```