## Java8中的LocalDateTime

在项目开发过程中经常遇到时间处理，但是你真的用对了吗，理解阿里巴巴开发手册中禁用 static 修饰 SimpleDateFormat 吗

通过阅读本篇文章你将了解到：

- 为什么需要 LocalDate、LocalTime、LocalDateTime【java8 新提供的类】
- java8 新的时间 API 的使用方式，包括创建、格式化、解析、计算、修改

## 为什么需要 LocalDate、LocalTime、LocalDateTime

Date 如果不格式化，打印出的日期可读性差

```java
TueSep1009:34:04 CST 2019
```

使用 SimpleDateFormat 对时间进行格式化，但 SimpleDateFormat 是线程不安全的 SimpleDateFormat 的 format 方法最终调用代码：

```java
privateStringBuffer format(Date date, StringBuffer toAppendTo,FieldDelegatedelegate) {        calendar.setTime(date);boolean useDateFormatSymbols = useDateFormatSymbols();for(int i = 0; i < compiledPattern.length; ) {int tag = compiledPattern[i] >>> 8;int count = compiledPattern[i++] & 0xff;if(count == 255) {                count = compiledPattern[i++] << 16;                count |= compiledPattern[i++];}switch(tag) {case TAG_QUOTE_ASCII_CHAR:                toAppendTo.append((char)count);break;case TAG_QUOTE_CHARS:                toAppendTo.append(compiledPattern, i, count);                i += count;break;default:                subFormat(tag, count, delegate, toAppendTo, useDateFormatSymbols);break;}}return toAppendTo;}
```

calendar 是共享变量，并且这个共享变量没有做线程安全控制。当多个线程同时使用相同的 SimpleDateFormat 对象【如用 static 修饰的 SimpleDateFormat】调用 format 方法时，多个线程会同时调用 calendar.setTime 方法，可能一个线程刚设置好 time 值另外的一个线程马上把设置的 time 值给修改了导致返回的格式化时间可能是错误的。

在多并发情况下使用 SimpleDateFormat 需格外注意
SimpleDateFormat 除了 format 是线程不安全以外，parse 方法也是线程不安全的。parse 方法实际调用 alb.establish(calendar).getTime() 方法来解析，alb.establish(calendar) 方法里主要完成了

- 重置日期对象 cal 的属性值
- 使用 calb 中中属性设置 cal
- 返回设置好的 cal 对象
  但是这三步不是原子操作

### 多线程并发如何保证线程安全

- 避免线程之间共享一个 SimpleDateFormat 对象，每个线程使用时都创建一次 SimpleDateFormat 对象 => 创建和销毁对象的开销大
- 对使用 format 和 parse 方法的地方进行加锁 => 线程阻塞性能差
- 使用 ThreadLocal 保证每个线程最多只创建一次 SimpleDateFormat 对象 => 较好的方法

Date 对时间处理比较麻烦，比如想获取某年、某月、某星期，以及 n 天以后的时间，如果用 Date 来处理的话真是太难了，你可能会说 Date 类不是有 getYear、getMonth 这些方法吗，获取年月日很 Easy，但都被弃用了啊

### java8 全新的日期和时间 API

**LocalDate**

### 只会获取年月日

创建 LocalDate

```java
LocalDate localDate = LocalDate.now();LocalDate localDate1 = LocalDate.of(2019, 9, 10);
```

获取年、月、日、星期几

```java
int year = localDate.getYear();int year1 = localDate.get(ChronoField.YEAR);Month month = localDate.getMonth();int month1 = localDate.get(ChronoField.MONTH_OF_YEAR);int day = localDate.getDayOfMonth();int day1 = localDate.get(ChronoField.DAY_OF_MONTH);DayOfWeek dayOfWeek = localDate.getDayOfWeek();int dayOfWeek1 = localDate.get(ChronoField.DAY_OF_WEEK);
```

**LocalTime**

### 只会获取几点几分几秒

创建 LocalTime

```java
LocalTime localTime = LocalTime.of(13, 51, 10);LocalTime localTime1 = LocalTime.now();
```

获取时分秒

```java
int hour = localTime.getHour();int hour1 = localTime.get(ChronoField.HOUR_OF_DAY);int minute = localTime.getMinute();int minute1 = localTime.get(ChronoField.MINUTE_OF_HOUR);int second = localTime.getMinute();int second1 = localTime.get(ChronoField.SECOND_OF_MINUTE);
```

### LocalDateTime

#### 获取年月日时分秒，等于 LocalDate+LocalTime

创建 LocalDateTime

```java
LocalDateTime localDateTime = LocalDateTime.now();LocalDateTime localDateTime1 = LocalDateTime.of(2019, Month.SEPTEMBER, 10, 14, 46, 56);LocalDateTime localDateTime2 = LocalDateTime.of(localDate, localTime);LocalDateTime localDateTime3 = localDate.atTime(localTime);LocalDateTime localDateTime4 = localTime.atDate(localDate);
```

获取 LocalDate

```java
LocalDate localDate2 = localDateTime.toLocalDate();
```

获取 LocalTime

```java
LocalTime localTime2 = localDateTime.toLocalTime();
```

### Instant

**获取秒数**

创建 Instant 对象

```java
Instant instant = Instant.now();
```

获取秒数

```java
long currentSecond = instant.getEpochSecond();
```

获取毫秒数

```java
long currentMilli = instant.toEpochMilli();
```

个人觉得如果只是为了获取秒数或者毫秒数，使用 System.currentTimeMillis() 来得更为方便

### 修改 LocalDate、LocalTime、LocalDateTime、Instant

LocalDate、LocalTime、LocalDateTime、Instant 为不可变对象，修改这些对象对象会返回一个副本

增加、减少年数、月数、天数等，以 LocalDateTime 为例

```
LocalDateTime localDateTime = LocalDateTime.of(2019, Month.SEPTEMBER, 10,14, 46, 56);localDateTime = localDateTime.plusYears(1);localDateTime = localDateTime.plus(1, ChronoUnit.YEARS);localDateTime = localDateTime.minusMonths(1);localDateTime = localDateTime.minus(1, ChronoUnit.MONTHS);
```

通过 with 修改某些值

```
localDateTime = localDateTime.withYear(2020);localDateTime = localDateTime.with(ChronoField.YEAR, 2022);
```

还可以修改月、日

#### 时间计算

比如有些时候想知道这个月的最后一天是几号、下个周末是几号，通过提供的时间和日期 API 可以很快得到答案

```java
LocalDate localDate = LocalDate.now();LocalDate localDate1 = localDate.with(firstDayOfYear());比如通过firstDayOfYear()返回了当前日期的第一天日期，还有很多方法这里不在举例说明
```

格式化时间

```java
LocalDate localDate = LocalDate.of(2019, 9, 10);String s1 = localDate.format(DateTimeFormatter.BASIC_ISO_DATE);String s2 = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE);DateTimeFormatter dateTimeFormatter =   DateTimeFormatter.ofPattern("dd/MM/yyyy");String s3 = localDate.format(dateTimeFormatter);DateTimeFormatter默认提供了多种格式化方式，如果默认提供的不能满足要求，可以通过DateTimeFormatter的ofPattern方法创建自定义格式化方式
```

#### 解析时间

```java
LocalDate localDate1 = LocalDate.parse("20190910", DateTimeFormatter.BASIC_ISO_DATE);LocalDate localDate2 = LocalDate.parse("2019-09-10", DateTimeFormatter.ISO_LOCAL_DATE);
```

和 SimpleDateFormat 相比，DateTimeFormatter 是线程安全的

### LocalDateTime获取毫秒数


//获取秒数
Long second = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
//获取毫秒数
Long milliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();

 

### LocalDateTime与String互转

```java
//时间转字符串格式化
 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
 String dateTime = LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);

//字符串转时间
String dateTimeStr = "2018-07-28 14:11:15";
DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, df);
```



### Date与LocalDateTime互转




```java
//将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
public static LocalDateTime dateConvertToLocalDateTime(Date date) {
    return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
}
```

   

```java
//将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
    return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
}
```

 

```java
/**
 * 测试转换是否正确
 */
@Test
public void testDateConvertToLocalDateTime() {
    Date date = DateUtils.parseDate("2018-08-01 21:22:22", DateUtils.DATE_YMDHMS);
    LocalDateTime localDateTime = DateUtils.dateConvertToLocalDateTime(date);
    Long localDateTimeSecond = localDateTime.toEpochSecond(ZoneOffset.of("+8"));
    Long dateSecond = date.toInstant().atOffset(ZoneOffset.of("+8")).toEpochSecond();
    Assert.assertTrue(dateSecond.equals(localDateTimeSecond));
}
```
