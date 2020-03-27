# Java Bean 属性复制

JavaBean，包括POJO、BO、VO、PO、DTO等，而Java的应用非常讲究分层的架构，因此就会存在对象在各个层次之间作为参数或者输出传递的过程，这里转换的工作往往非常繁琐。

为此业界有很多开源的解决方案，列出一些常见的如下：

- [Apache org.apache.commons.beanutils.PropertyUtils.copyProperties](https://link.jianshu.com?t=http://commons.apache.org/proper/commons-beanutils/)
- [Apache org.apache.commons.beanutils.BeanUtils.copyProperties](https://link.jianshu.com?t=http://commons.apache.org/proper/commons-beanutils/)
- [Spring org.springframework.beans.BeanUtils.copyProperties](https://link.jianshu.com?t=http://spring.io/)
- [Cglib BeanCopier](https://link.jianshu.com?t=https://github.com/cglib/cglib)
- [Dozer](https://link.jianshu.com?t=http://dozer.sourceforge.net/)



```xml
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.3</version>
</dependency>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-beans</artifactId>
    <version>4.3.6.RELEASE</version>
</dependency>

<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>3.2.5</version>
</dependency>

<dependency>
    <groupId>net.sf.dozer</groupId>
    <artifactId>dozer</artifactId>
    <version>5.5.1</version>
</dependency>
```

测试代码：

```java
package com.bytebeats.bean.copy;

import com.bytebeats.bean.copy.model.Address;
import com.bytebeats.bean.copy.model.Customer;
import com.bytebeats.bean.copy.model.Employee;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class BeanCopyDemo {

    private int count = 100000;

    public static void main( String[] args ) throws Exception {

        new BeanCopyDemo().testTime("ApacheBeanUtils");
    }

    public void testTime(String name) throws Exception {
        Customer customer = getCustomer();
        Employee employee = new Employee();

        long start = System.currentTimeMillis();
        switch (name){
            case "ApacheBeanUtils":
                testApacheBeanUtils(customer, employee);
                break;
            case "ApachePropertyUtils":
                testApachePropertyUtils(customer, employee);
                break;
            case "SpringBeanUtils":
                testSpringBeanUtils(customer, employee);
                break;
            case "CglibBeanCopier":
                testCglibBeanCopier(customer, employee);
                break;
        }

        System.out.println(name+" count: "+count+" cost: "+(System.currentTimeMillis() - start)+" ms");
    }

    public void testCglibBeanCopier(Object src, Object target){

        BeanCopier copier = BeanCopier.create(src.getClass(), target.getClass(), false);
        copier.copy(src, target, null);
    }

    public void testSpringBeanUtils(Object src, Object target){
        for(int i=0; i<count; i++){
            org.springframework.beans.BeanUtils.copyProperties(src, target);
        }
    }

    public void testApacheBeanUtils(Object src, Object dest) throws InvocationTargetException, IllegalAccessException {
        for(int i=0; i<count; i++){
            BeanUtils.copyProperties(dest, src);
        }
    }

    public void testApachePropertyUtils(Object src, Object dest) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        for(int i=0; i<count; i++){
            PropertyUtils.copyProperties(dest, src);
        }
    }

    private Customer getCustomer(){

        Customer customer = new Customer();
        customer.setId(15L);
        customer.setName("ricky");
        customer.setAge(28);

        List<String> hobbies = new ArrayList<>(4);
        hobbies.add("汽车");
        hobbies.add("旅游");
        hobbies.add("体育");
        hobbies.add("NBA");
        customer.setHobbies(hobbies);

        Address address = new Address();
        address.setProvince("湖北省");
        address.setCity("武汉市");
        address.setDistrict("武昌区");
        address.setDetail("欢乐大道28号");
        customer.setAddress(address);

        return customer;
    }
}

```





分别测试10万次拷贝，耗时如下：

#### 表1

| 框架                                                      | 耗时    |
| --------------------------------------------------------- | ------- |
| org.apache.commons.beanutils.BeanUtils.copyProperties     | 1804 ms |
| org.apache.commons.beanutils.PropertyUtils.copyProperties | 1171 ms |
| org.springframework.beans.BeanUtils.copyProperties        | 770 ms  |
| net.sf.cglib.beans.BeanCopier.copy                        | 147 ms  |