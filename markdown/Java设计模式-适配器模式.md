## Java设计模式-适配器模式

适配器模式将某个类的接口转换成客户端期望的另一个接口表示，目的是消除由于接口不匹配所造成的类的兼容性问题。主要分为三类：类的适配器模式、对象的适配器模式、接口的适配器模式。

### 1、类的适配器模式



核心思想就是：有一个 Source 类，拥有一个方法，待适配，目标接口是 Targetable，通过 Adapter 类，将 Source 的功能扩展到 Targetable 里。

![](E:\markdown\类的适配器模式.PNG)

实现代码：

Adapter

```java
package com.gitboy.adapter;

public class Adapter extends Source implements Targetable {

	@Override
	public void method2() {
		System.out.println("this is Targetable method2");

	}

}

```

Source

```java
package com.gitboy.adapter;

public class Source {

	public void method1() {
		System.out.println("this is original method!");
	}
}
```

Targetable

```java
package com.gitboy.adapter;

public interface Targetable {

	public void method1();
	public void method2();
}

```

AdapterTest

```java
package com.gitboy.adapter;

public class AdapterTest {

	public static void main(String[] args) {
		Targetable target = new Adapter();
		target.method1();
		target.method2();
	}
}

```

运行效果：

```java
this is original method!
this is Targetable method2
```



### 2、对象的适配器模式

基本思路和类的适配器模式相同，只是将 Adapter 类作修改，这次不继承 Source 类，而是持有 Source
类的实例，以达到解决兼容性的问题。看图：

![](E:\markdown\对象的适配模式.PNG)

修改 Adapter 类

```java
package com.gitboy.adapter;

public class Wrapper implements Targetable {

	private Source source;


	public Wrapper(Source source) {
		super();
		this.source = source;
	}



	@Override
	public void method1() {
		source.method1();
	}

	@Override
	public void method2() {
		System.out.println("this is the targetable method!");
	}

}

```

AdapterTest2

```java
package com.gitboy.adapter;

public class AdapterTest2 {

	public static void main(String[] args) {
		Source source = new Source();
		Targetable target = new Wrapper(source);
		target.method1();
		target.method2();
	}
}

```

输出

```java
this is original method!
this is Targetable method2
```

### 3、接口的适配器模式

第三种适配器模式是接口的适配器模式，接口的适配器是这样的：有时我们写的一个接口中有多个抽象方法，当我们写该接口的实现类时，必须实现该接口的所有方法，这明显有时比较浪费，因为并不是所有的方法都是我们需要的，有时只需要某一些，此处为了解决这个问题，我们引入了接口的适配器模式，借助于一个抽象类，该抽象类实现了该接口，实现了所有的方法，而我们不和原始的接口打交道，只和
该抽象类取得联系，所以我们写一个类，继承该抽象类，重写我们需要的方法就行。看一下类图：

![](E:\markdown\接口适配器模式.PNG)

代码：

Sourceable

```java
package com.gitboy.adapter;

public interface Sourceable {

	public void method1();
	public void method2();
}

```

SourceSub1

```java
package com.gitboy.adapter;

public class SourceSub1 extends Wrapper2 {
	@Override
	public void method1() {
		System.out.println("the sourceable i Sub1!");
	}
}

```



SourceSub2

```java
package com.gitboy.adapter;

public class SourceSub2 extends Wrapper2 {
	@Override
	public void method2() {
		System.out.println("the sourceable i Sub2!");
	}
}

```



Wrapper2

```java
package com.gitboy.adapter;

public abstract class Wrapper2 implements Sourceable{
	public void method1(){}
	public void method2(){}
}

```

```java
package com.gitboy.adapter;

public class WrapperTest {

	public static void main(String[] args) {
		Sourceable source1 = new SourceSub1();
		Sourceable source2 = new SourceSub2();
		source1.method1();
		source1.method2();
		source2.method1();
		source2.method2();
	}

}

```

输出

```java
the sourceable i Sub1!
the sourceable i Sub2!

```





### 4、总结

#### 4.1 类的适配器模式

当希望将一个类转换成满足另一个新接口的类时，可以使用类的适配器模式，创建一
个新类，继承原有的类，实现新的接口即可。

#### 4.2 对象的适配器模式

当希望将一个对象转换成满足另一个新接口的对象时，可以创建一个 Wrapper 类，
持有原类的一个实例，在 Wrapper 类的方法中，调用实例的方法就行。

#### 4.3 接口的适配器模式

当不希望实现一个接口中所有的方法时，可以创建一个抽象类 Wrapper，实现所
有方法，我们写别的类的时候，继承抽象类即可。