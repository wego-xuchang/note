## Java8中的Stream

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

#### map(转换)

<R> Stream<R> map(Function<? super T, ? extends R> mapper);



#### distinct(去重)

Stream<T> distinct();



#### sorted(排序)

Stream<T> sorted();

Stream<T> sorted(Comparator<? super T> comparator);



#### limit（限制返回个数）



#### skip(删除元素)



#### reduce(聚合)



#### min(求最小值)



#### anyMatch/allMatch/noneMatch（匹配）

anyMatch：Stream 中任意一个元素符合传入的 predicate，返回 true

allMatch：Stream 中全部元素符合传入的 predicate，返回 true

noneMatch：Stream 中没有一个元素符合传入的 predicate，返回 true