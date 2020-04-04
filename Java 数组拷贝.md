# Java 数组拷贝

方法重载：方法名称相同，参数列表不同。
不能有两个名字相同、参数类型相同，返回值不同的方法。
在进行方法重载时，方法的返回值一定相同！！！
方法递归特点：
1.必须有结束条件
2.每次递归处理时，一定要有变更
数组
1.动态初始化：
数据类型【】 数组名称 = new 数据类型 【长度】
2.静态初始化：
数据类型【】数组名称 = {值，值，值…}
3.二维数组
数据类型【】【】 数组名称 = new 数据类型 【行个数】【列个数】`
例：

```java
import java.util.Arrays;
public class TestDeno1 {
    public static void main(String[] args) {
        int[] array2 = {1,1,3,5,5,};
        int diff=0;
        for (int i=0;i<array2.length;i++) {
           diff=diff^array2[i];
        }
        System.out.println(diff);//相同数字异或后为0
        //  int[] array = new int [10];
       // int[][] array3 =  {{1,2},{3,2,5},{6}};
      //  System.out.println(Arrays.toString(array2));//打印一位数组
        // System.out.println(Arrays.deepToString(array3));//打印二维数组
	}
}
```


## 数组的拷贝

### 1.for循环；

```java
public class TestDemo3 {
    public static void main(String[] args) {
        int [] array1 = {1,2,3,4,5,6};
        int [] array2 = new int [6];
        for(int i=0;i<6;i++)
        {
            array2[i] = array1[i];
            System.out.println(array2[i]);
        }
    }
}
```



### 2.clone；

```java
import java.util.Arrays;

public class TestDemo4 {
    public static void main(String[] args) {
        int [] array1 = {1,2,3,4,5,6};
        int [] array2 = new int [6];
        array2 = array1.clone();
        System.out.println(Arrays.toString(array2));
    }
}
```

### 3.System.arraycopy()；

System.arraycopy(源数组名称，源数组开始点，目标数组名称，目标数组开始点，拷贝长度)
这种方法拷贝速度最快，没有返回值

```java
import java.util.Arrays;

public class TestDemo5 {
    public static void main(String[] args) {
        int [] array1 = {1,2,3,4,5,6};
        int [] array2 = new int [6];
        System.arraycopy(array1,0,array2,0,6);
        System.out.println(Arrays.toString(array2));
    }
}
```

### 4.Arrays.copyOf；

Arrays.copyOf(源数组名称，新数组长度)
这种方法有返回值

```java
import java.util.Arrays;

public class TestDemo6 {
    public static void main(String[] args) {
        int [] array1 = {1,2,3,4,5,6};
        int [] array2 = new int [6];
        array2 = Arrays.copyOf(array1,6);
        System.out.println(Arrays.toString(array2));
    }
}

```

注：以上四种方式对于基本类型来说都是深拷贝，对于引用类型来说都是浅拷贝
