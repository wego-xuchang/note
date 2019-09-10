## java8使用流的filter来筛选数据



```java
package chapter1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

public class stream {
    //比如说 你需要在一个列表中 筛选出所以重量大于150的苹果，然后按照颜色分组
    //按常规的筛选写法 就是在循环里面 迭代筛选

    public static void main(String[] args) {
        List<Apple> appleList = new ArrayList<>();

        //常规写法
        Map<String, List<Apple>> AppMap = new HashMap<>();
        for (Apple apple : appleList) {
            if (apple.getWeight() > 150) { //如果重量大于150
                if (AppMap.get(apple.getColor()) == null) { //该颜色还没分类
                    List<Apple> list = new ArrayList<>(); //新建该颜色的列表
                    list.add(apple);//将苹果放进去列表
                    AppMap.put(apple.getColor(),list);//将列表放到map中
                }else { //该颜色分类已存在
                    AppMap.get(apple.getColor()).add(apple);//该颜色分类已存在，则直接放进去即可
                }
            }
        }

        //如上方式 就可以筛选出来所有的150克大小以上的苹果，并按颜色分类



        //方式二 使用java8提供的流api实现 这种叫内部迭代
        Map<String, List<Apple>> AppMap2=appleList.stream().filter((Apple a)->a.getWeight()>150) //筛选出大于150的
                .collect(groupingBy(Apple::getColor)); //按颜色分组  最后得到map
        

    }


    class Apple {

        private String color;//颜色
        private Integer weight; //重量

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Integer getWeight() {
            return weight;
        }

        public void setWeight(Integer weight) {
            this.weight = weight;
        }


    }
}


```

## java8的Stream

使用java8的Stream，根据Object某些属性对List进行筛选。

对对象集合进行处理时，整天for循环筛选，感觉跟不上时代。java8的Stream，并用来替代for循环筛选。

public class StreamTest {

```java
public static void main (String[] args) {
    List<Person> persons = Arrays.asList(
            new Person("e1", "l1"),
            new Person("e2", "l1"),
            new Person("e3", "l2"),
            new Person("e4", "l2"),
            new Person("e5", "l3"),
            new Person("e6", "l3"),
            new Person("e7", "l3")
 
    );
    //.filter（过滤条件）
    //.collect(Collectors.toList()) 把结果搜集到List之中。
    persons = persons.stream().filter(person -> person.getLastName().equals("l3")).collect(Collectors.toList());
    persons.forEach(person -> {
        System.out.println(person.getName());
    });
```

 

    }
}
public class Person {


```java
private String name;
private String lastName;
 
public Person(String name, String lastName) {
    this.name = name;
    this.lastName = lastName;
}
 
public String getName() {
    return name;
}
 
public void setName(String name) {
    this.name = name;
}
 
public String getLastName() {
    return lastName;
}
 
public void setLastName(String lastName) {
    this.lastName = lastName;
}
```
}

结果：
e5
e6

e7



---------------------
```java
//模糊检索代码
List<user> filter= users.stream()
                          .filter(user -> user.getName().indexOf("月") > -1 || user.getEmail().indexOf("mu") > -1)
                          .collect(Collectors.toList());
collect.stream().forEach(user -> {
    System.out.println(user.getName()+"======"+user.getEmail());
});
//多个年龄匹配代码
List<Integer> ages=new ArrayList<>();
ages.add(20);
ages.add(30);
List<user> filterAges = users.stream().filter(user->ages.contains(user.getAge())).collect(Collectors.toList());
filterAges.stream().forEach(user -> {
    System.out.println(user.getName()+"======"+user.getEmail());
});
```

