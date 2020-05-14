## Java解决大量if-else

### 反射动态调用相应

```java
/***
 *定义每种类型所对应的方法
*/
public class ReflectTest {
    public void methodOne() {
        System.out.println("one");
    }

    public void methodTwo() {
        System.out.println("two");
    }

    public void methodThree() {
        System.out.println("three");
    }

    public void methodFour() {
        System.out.println("four");
    }

}
```



```java
/***
     *
     * 通过反射，动态调用方法。采用了Guava的工具类。
     * */
    @Test
    public void testReflect() throws Exception {
        //首字母大写，根据类型拼接方法
        String methodName = "method" + LOWER_CAMEL.to(UPPER_CAMEL, input);
        Method method = ReflectTest.class.getDeclaredMethod(methodName);
        Invokable<ReflectTest, Object> invokable =
                (Invokable<ReflectTest, Object>) Invokable.from(method);
        invokable.invoke(new ReflectTest());
    }
```

### 利用Java8提供的Function函数式编程

通过function接口提供的compose,andThen方法可以灵活地加入其它处理逻辑,

```java
public class CalcFunction {
    private String calcOrdinary(Bill bill) {
        return "处理普通用户的账单计费:" + bill.getTotal();
    }
 
    private String calcVip1(Bill bill) {
        return "处理vip1级用户的账单计费:" + bill.getTotal();
    }
 
    private String calcVip2(Bill bill) {
        return "处理vip2级用户的账单计费:" + bill.getTotal();
    }
 
    public Function<Bill, String> getFunction(Integer type) {
        Function<Bill, String> ordinary = bill -> calcOrdinary(bill);
        Function<Bill, String> vip1 = bill -> calcVip1(bill);
        Function<Bill, String> vip2 = bill -> calcVip2(bill);
        Supplier<Map<Integer, Function<Bill, String>>> supplier = () -> {
            Map<Integer, Function<Bill, String>> map = new HashMap<>(3);
            map.put(BillType.ORDINARY.getType(), ordinary);
            map.put(BillType.VIP1.getType(), vip1);
            map.put(BillType.VIP2.getType(), vip2);
            return map;
        };
        return supplier.get().get(type);
    }
}
```

```java
    @Test
    public void testFunctional() {
        CalcFunction calcFunction = new CalcFunction();
 
        Bill bill1 = new Bill(BillTypeEnum.ORDINARY.getType(), new BigDecimal(500));
        calcFunction.getFunction(bill1.getType()).apply(bill1);
 
        Bill bill2 = new Bill(BillTypeEnum.VIP1.getType(), new BigDecimal(400));
        calcFunction.getFunction(bill2.getType()).apply(bill2);
 
        Bill bill3 = new Bill(BillTypeEnum.VIP2.getType(), new BigDecimal(300));
        calcFunction.getFunction(bill3.getType()).apply(bill3);
    }
```

### 策略模式+自定义注解+Springboot监听

- 定义统一的账单处理接口

```java
public interface CalcService {
    /**
     * 处理账单计算
     */
    void handleCalc(BigDecimal total);
}
```

- 不同的计费逻辑分别去实现该接口中的handleCalc方法:

```java
@BillTypeHandler(BillType.ORDINARY)
public class OridinaryCalcServiceImpl implements CalcService {
    @Override
    public void handleCalc(BigDecimal total) {
        System.out.println("处理普通用户的账单计费:" + total);
    }
}
```

```java
@BillTypeHandler(BillType.VIP1)
public class Vip1CalcServiceImpl implements CalcService {
    @Override
    public void handleCalc(BigDecimal total) {
        System.out.println("处理vip1级用户的账单计费:"+total);
    }
}

@BillTypeHandler(BillType.VIP2)
public class Vip1CalcServiceImpl implements CalcService {
    @Override
    public void handleCalc(BigDecimal total) {
        System.out.println("处理vip2级用户的账单计费:"+total);
    }
}

@BillTypeHandler(BillType.VIP3)
public class Vip1CalcServiceImpl implements CalcService {
    @Override
    public void handleCalc(BigDecimal total) {
        System.out.println("处理vip3级用户的账单计费:"+total);
    }
}
```

- 定义自定义注解

```java
@Service
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BillTypeHandler {
    BillType value();
 
    enum BillType {
        ORDINARY(0, "普通用户"),
        VIP1(1, "一级会员"),
        VIP2(2, "二级会员");
        private Integer type;
        private String desc;
 
        BillType(Integer type, String desc) {
            this.type = type;
            this.desc = desc;
        }
 
        public Integer getType() {
            return type;
        }
    }
}
```

- 创建自定义的上下文环境:

此上下文环境中维护了一个Map,用来存放CalcService的实现类,然后通过@Component交由Spring容器管理

```java
@Component
public class BillServiceContext {
    @Getter
    private final static Map<Integer, CalcService> calcServiceMap;
 
    static {
        calcServiceMap = new HashMap<>();
    }
 
    public CalcService get(Integer type) {
        return calcServiceMap.get(type);
    }
 
    public void put(Integer type, CalcService calcService) {
        calcServiceMap.put(type, calcService);
    }
}
```

- 定义Springboot的监听器:

```java
@Component
public class BillTypeListener implements ApplicationListener<ContextRefreshedEvent> {
    @Resource
    private BillServiceContext billServiceContext;
 
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, CalcService> beans = contextRefreshedEvent.getApplicationContext().getBeansOfType(
            CalcService.class);
        beans.forEach((k, calcService) -> {
            Class clazz = calcService.getClass();
            BillTypeHandler billTypeHandler = (BillTypeHandler)clazz.getAnnotation(BillTypeHandler.class);
            billServiceContext.put(billTypeHandler.value().getType(), calcService);
        });
    }
}
```

- 在需要调用的地方通过@Resource或者@Autowired注解将自定义的上下文环境注入,然后直接调用即可:

```java
@SpringBootTest
@RunWith(SpringRunner.class)
public class BillTest {
    @Autowired
    BillServiceContext billServiceContext;
 
    @Test
    public void test() {
        Bill bill1 = new Bill(BillType.ORDINARY.getType(), new BigDecimal(500));
        billServiceContext.get(bill1.getType()).handleCalc(bill1.getTotal());
 
        Bill bill2 = new Bill(BillType.VIP1.getType(), new BigDecimal(400));
        billServiceContext.get(bill2.getType()).handleCalc(bill2.getTotal());
 
        Bill bill3 = new Bill(BillType.VIP2.getType(), new BigDecimal(300));
        billServiceContext.get(bill3.getType()).handleCalc(bill3.getTotal());
    }
}
```

