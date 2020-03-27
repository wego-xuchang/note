## Java8中的Stream

[菜鸟教程](https://www.runoob.com/java/java8-streams.html)

### 什么是 Stream？

Stream（流）是一个来自数据源的元素队列并支持聚合操作

- 元素是特定类型的对象，形成一个队列。 Java中的Stream并不会存储元素，而是按需计算。
- **数据源** 流的来源。 可以是集合，数组，I/O channel， 产生器generator 等。
- **聚合操作** 类似SQL语句一样的操作， 比如filter, map, reduce, find, match, sorted等。

和以前的Collection操作不同， Stream操作还有两个基础的特征：

- **Pipelining**: 中间操作都会返回流对象本身。 这样多个操作可以串联成一个管道， 如同流式风格（fluent style）。 这样做可以对操作进行优化， 比如延迟执行(laziness)和短路( short-circuiting)。
- **内部迭代**： 以前对集合遍历都是通过Iterator或者For-Each的方式, 显式的在集合外部进行迭代， 这叫做外部迭代。 Stream提供了内部迭代的方式， 通过访问者模式(Visitor)实现。

lambda表达式，函数式接口，Date API等特性

Stream作为java8的新特性，基于lambda表达式，是对集合对象功能的增强，它专注于对集合对象进行各种高效、便利的聚合操作或者大批量的数据操作，提高了编程效率和代码可读性。

Stream的原理：将要处理的元素看做一种流，流在管道中传输，并且可以在管道的节点上处理，包括过滤筛选、去重、排序、聚合等。元素流在管道中经过中间操作的处理，最后由最终操作得到前面处理的结果。

集合有两种方式生成流：

- stream() − 为集合创建串行流
- parallelStream() - 为集合创建并行流

```java

public interface Stream<T> extends BaseStream<T, Stream<T>> {

    Stream<T> filter(Predicate<? super T> predicate);

    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    IntStream mapToInt(ToIntFunction<? super T> mapper);

    LongStream mapToLong(ToLongFunction<? super T> mapper);

    DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

    LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

    DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

    Stream<T> distinct();

    Stream<T> sorted();

    Stream<T> sorted(Comparator<? super T> comparator);

    Stream<T> peek(Consumer<? super T> action);

    Stream<T> skip(long n);
    
    void forEach(Consumer<? super T> action);

    void forEachOrdered(Consumer<? super T> action);

    Object[] toArray();

    <A> A[] toArray(IntFunction<A[]> generator);

    T reduce(T identity, BinaryOperator<T> accumulator);
    
    Optional<T> reduce(BinaryOperator<T> accumulator);

    <U> U reduce(U identity,
                 BiFunction<U, ? super T, U> accumulator,
                 BinaryOperator<U> combiner);
    
    <R> R collect(Supplier<R> supplier,
                  BiConsumer<R, ? super T> accumulator,
                  BiConsumer<R, R> combiner);

    <R, A> R collect(Collector<? super T, A, R> collector);

    Optional<T> min(Comparator<? super T> comparator);

    Optional<T> max(Comparator<? super T> comparator);

    long count();

    boolean anyMatch(Predicate<? super T> predicate);

    boolean allMatch(Predicate<? super T> predicate);

    boolean noneMatch(Predicate<? super T> predicate);
    Optional<T> findFirst();

    Optional<T> findAny();
    
    public static<T> Builder<T> builder() {
        return new Streams.StreamBuilderImpl<>();
    }
    
    public static<T> Stream<T> empty() {
        return StreamSupport.stream(Spliterators.<T>emptySpliterator(), false);
    }
    public static<T> Stream<T> of(T t) {
        return StreamSupport.stream(new Streams.StreamBuilderImpl<>(t), false);
    }
    
    @SafeVarargs
    @SuppressWarnings("varargs") 
    public static<T> Stream<T> of(T... values) {
        return Arrays.stream(values);
    }

    public static<T> Stream<T> iterate(final T seed, final UnaryOperator<T> f) {
        Objects.requireNonNull(f);
        final Iterator<T> iterator = new Iterator<T>() {
            @SuppressWarnings("unchecked")
            T t = (T) Streams.NONE;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public T next() {
                return t = (t == Streams.NONE) ? seed : f.apply(t);
            }
        };
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    public static<T> Stream<T> generate(Supplier<T> s) {
        Objects.requireNonNull(s);
        return StreamSupport.stream(
                new StreamSpliterators.InfiniteSupplyingSpliterator.OfRef<>(Long.MAX_VALUE, s), false);
    }

    public static <T> Stream<T> concat(Stream<? extends T> a, Stream<? extends T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        @SuppressWarnings("unchecked")
        Spliterator<T> split = new Streams.ConcatSpliterator.OfRef<>(
                (Spliterator<T>) a.spliterator(), (Spliterator<T>) b.spliterator());
        Stream<T> stream = StreamSupport.stream(split, a.isParallel() || b.isParallel());
        return stream.onClose(Streams.composedClose(a, b));
    }
    
    public interface Builder<T> extends Consumer<T> {
        @Override
        void accept(T t);
        default Builder<T> add(T t) {
            accept(t);
            return this;
        }
        Stream<T> build();

    }
}

```



图中是Stream类的类结构图，里面包含了大部分的中间和终止操作。

中间操作主要有以下方法（此类型方法返回的都是Stream）：map (mapToInt, flatMap 等)、 filter、 distinct、 sorted、 peek、 limit、 skip、 parallel、 sequential、 unordered

终止操作主要有以下方法：forEach、 forEachOrdered、 toArray、 reduce、 collect、 min、 max、 count、 anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 iterator

### 接口说明：

#### filter（筛选）

Stream<T> filter(Predicate<? super T> predicate);

```java
lists.stream().filter(list -> "name".equals(list.getName())).collect(Collectors.toList())
```

从集合中过滤出来符合条件的元素：

//过滤出符合条件的数据

```java
List<Apple> filterList = appleList.stream().filter(a -> a.getName().equals("香蕉")).collect(Collectors.toList());
```



#### 分组

List里面的对象元素，以某个属性来分组，例如，以id分组，将id相同的放在一起：

List 以ID分组 Map<Integer,List<Apple>>

```java
Map<Integer, List<Apple>> groupBy = appleList.stream().collect(Collectors.groupingBy(Apple::getId));
```

#### 求和

将集合中的数据按照某个属性求和:

计算 总金额

```java
BigDecimal totalMoney = appleList.stream().map(Apple::getMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
System.err.println("totalMoney:"+totalMoney);  //totalMoney:17.48
```

#### 查找流中最大 最小值

Collectors.maxBy 和 Collectors.minBy 来计算流中的最大或最小值。


```java
Optional<Dish> maxDish = Dish.menu.stream().
      collect(Collectors.maxBy(Comparator.comparing(Dish::getCalories)));
maxDish.ifPresent(System.out::println);
 
Optional<Dish> minDish = Dish.menu.stream().
      collect(Collectors.minBy(Comparator.comparing(Dish::getCalories)));
minDish.ifPresent(System.out::println);
```

#### map(转换)

<R> Stream<R> map(Function<? super T, ? extends R> mapper);

ArraryList转HashMap

VALUE为对象

```java
Map<Long, User> maps = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (key1, key2) -> key2));

```

VALUE为属性

```JAVA
Map<Long, String> maps = userList.stream().collect(Collectors.toMap(User::getId, User::getAge, (key1, key2) -> key2));

```

使用JDK1.8

```java
Map<Long, User> maps = userList.stream().collect(Collectors.toMap(User::getId,Function.identity()));
```

看来还是使用JDK 1.8方便一些。另外，转换成`map`的时候，可能出现`key`一样的情况，如果不指定一个覆盖规则，上面的代码是会报错的。转成`map`的时候，最好使用下面的方式：

```java
Map<Long, User> maps = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (key1, key2) -> key2));
```

有时候，希望得到的map的值不是对象，而是对象的某个属性，那么可以用下面的方式：

```java
Map<Long, String> maps = userList.stream().collect(Collectors.toMap(User::getId, User::getAge, (key1, key2) -> key2));
```

#### distinct(去重)

Stream<T> distinct();

```java
List<String> list = Arrays.asList("AA", "BB", "CC", "BB", "CC", "AA", "AA");
        long l = list.stream().distinct().count();
        System.out.println("No. of distinct elements:"+l);
        String output = list.stream().distinct().collect(Collectors.joining(","));
        System.out.println(output);

```

Output 

```java
No. of distinct elements:3
AA,BB,CC 
```



#### sorted(排序)

Stream<T> sorted();

Stream<T> sorted(Comparator<? super T> comparator);

这篇文章将会讲解Java 8 Stream sorted()示例， 我们能够以自然序或着用Comparator 接口定义的排序规则来排序一个流。Comparator 能用用lambada表达式来初始化， 我们还能够逆序一个已经排序的流。
接下来我们将会使用java 8 的流式sorted排序List 、Map 、 Set
**1、sorted() 默认使用自然序排序， 其中的元素必须实现Comparable 接口**

**2、sorted(Comparator<? super T> comparator) ：我们可以使用lambada 来创建一个Comparator 实例。可以按照升序或着降序来排序元素。**

下面代码以自然序排序一个list

```java
list.stream().sorted() 
```


自然序逆序元素，使用Comparator 提供的reverseOrder() 方法

```java
list.stream().sorted(Comparator.reverseOrder()) 
```


使用Comparator 来排序一个list

```java
list.stream().sorted(Comparator.comparing(Student::getAge)) 

```


把上面的元素逆序

```java
list.stream().sorted(Comparator.comparing(Student::getAge).reversed()) 
```

```java
SortList.java

package com.concretepage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
public class SortList {
    public static void main(String[] args) {
        List<Student> list = new ArrayList<Student>();
        list.add(new Student(1, "Mahesh", 12));
        list.add(new Student(2, "Suresh", 15));
        list.add(new Student(3, "Nilesh", 10));

        System.out.println("---Natural Sorting by Name---");
        List<Student> slist = list.stream().sorted().collect(Collectors.toList());
        slist.forEach(e -> System.out.println("Id:"+ e.getId()+", Name: "+e.getName()+", Age:"+e.getAge()));

        System.out.println("---Natural Sorting by Name in reverse order---");
        slist = list.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        slist.forEach(e -> System.out.println("Id:"+ e.getId()+", Name: "+e.getName()+", Age:"+e.getAge()));        

        System.out.println("---Sorting using Comparator by Age---");
        slist = list.stream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());
        slist.forEach(e -> System.out.println("Id:"+ e.getId()+", Name: "+e.getName()+", Age:"+e.getAge()));

        System.out.println("---Sorting using Comparator by Age with reverse order---");
        slist = list.stream().sorted(Comparator.comparing(Student::getAge).reversed()).collect(Collectors.toList());
        slist.forEach(e -> System.out.println("Id:"+ e.getId()+", Name: "+e.getName()+", Age:"+e.getAge()));
    }
} 

```



#### limit（限制返回个数）

```
List<Integer> result = Stream.of(1, 2, 3, 4, 5, 6)
  .limit(4)
  .collect(Collectors.toList());
List<Integer> expected = asList(1, 2, 3, 4);
assertEquals(expected, result);
```

方法`limit()`的参数n的四种情况：

（1）当`n<0`时，抛`IllegalArgumentException`异常；

（2）当`n=0`时，不取元素，返回空流；

（3）当`0<n<length`时，取前n个元素，返回新的流；

（4）当`n>=length`时，取所有元素，原封不动、完璧归赵。

#### skip(删除元素)

```java
List<Integer> result = Stream.of(1, 2, 3, 4, 5, 6)
  .skip(4)
  .collect(Collectors.toList());
List<Integer> expected = asList(5, 6);
assertEquals(expected, result);
```

方法`skip()`的参数n的四种情况：

（1）当`n<0`时，抛`IllegalArgumentException`异常；

（2）当`n=0`时，相当没有跳过任何元素，原封不动、完璧归赵；

（3）当`0<n<length`时，跳过n个元素后，返回含有剩下的元素的流；

（4）当`n>=length`时，跳过所有元素，返回空流。



#### 分页

```
int pageSize = 10;
int pageIndex = 7;

List<Integer> expected = asList(61, 62, 63, 64, 65, 66, 67, 68, 69, 70);
List<Integer> result = Stream.iterate(1, i -> i + 1)
  .skip((pageIndex - 1) * pageSize)
  .limit(pageSize)
  .collect(Collectors.toList());

assertEquals(expected, result);
```

上面代码例子是获取了第七页数据，每页大小为10。

#### reduce(聚合)

1.第一个参数是我们给出的初值，

2.第二个参数是累加器，可以自己用实现接口完成想要的操作，这里使用Bigdecimal的add方法 

3.最后reduce会返回计算后的结果

```java
BigDecimal qty = Order.getOrderItems().stream().map(p -> p.getProductQty()).reduce(BigDecimal.ZERO,BigDecimal::add);
```





#### min(求最小值) max(求最大值)

```java
int a = Stream.of(2,1,4,5,3).max(Integer::compare).get();------5
int b = Stream.of(2,1,4,5,3).min(Integer::compare).get();------1
```

用Integer::compare即可。
也可以直接：

```java
int a = Stream.of(1,2,4,5,3).mapToInt(i -> i).max().getAsInt();
```

```java
 List<String> strs = Arrays.asList("d", "b", "a", "c", "a");
        Optional<String> min = strs.stream().min(Comparator.comparing(Function.identity()));
        Optional<String> max = strs.stream().max((o1, o2) -> o1.compareTo(o2));
        System.out.println(String.format("min:%s; max:%s", min.get(), max.get()));// min:a; max:d
```



#### anyMatch/allMatch/noneMatch（匹配）

anyMatch：Stream 中任意一个元素符合传入的 predicate，返回 true

allMatch：Stream 中全部元素符合传入的 predicate，返回 true

noneMatch：Stream 中没有一个元素符合传入的 predicate，返回 true



#### Collectors 类的静态工厂方法

toList    List<T>    把流中所有项目收集到一个 List
toSet    Set<T>    把流中所有项目收集到一个 Set，删除重复项
toCollection    Collection<T>    把流中所有项目收集到给定的供应源创建的集合menuStream.collect(toCollection(), ArrayList::new)
counting    Long    计算流中元素的个数
sumInt    Integer    对流中项目的一个整数属性求和
averagingInt    Double    计算流中项目 Integer 属性的平均值
summarizingInt    IntSummaryStatistics    收集关于流中项目 Integer 属性的统计值，例如最大、最小、 总和与平均值
joining    String    连接对流中每个项目调用 toString 方法所生成的字符串collect(joining(", "))
maxBy    Optional<T>    一个包裹了流中按照给定比较器选出的最大元素的 Optional， 或如果流为空则为 Optional.empty()
minBy    Optional<T>    一个包裹了流中按照给定比较器选出的最小元素的 Optional， 或如果流为空则为 Optional.empty()
reducing    归约操作产生的类型    从一个作为累加器的初始值开始，利用 BinaryOperator 与流 中的元素逐个结合，从而将流归约为单个值累加int totalCalories = menuStream.collect(reducing(0, Dish::getCalories, Integer::sum));
collectingAndThen    转换函数返回的类型    包裹另一个收集器，对其结果应用转换函数int howManyDishes = menuStream.collect(collectingAndThen(toList(), List::size))
groupingBy    Map<K, List<T>>    根据项目的一个属性的值对流中的项目作问组，并将属性值作 为结果 Map 的键
partitioningBy    Map<Boolean,List<T>>    根据对流中每个项目应用谓词的结果来对项目进行分区