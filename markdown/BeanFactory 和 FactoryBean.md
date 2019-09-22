## BeanFactory 和 FactoryBean

BeanFactory 和 FactoryBean 都是Spring Beans模块下的接口

BeanFactory是spring简单工厂模式的接口类，spring IOC特性核心类，提供从工厂类中获取bean的各种方法，是所有bean的容器。见以下结构视图

![img](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=4081542871,2734504682&fm=173&app=49&f=JPEG?w=246&h=300&s=AE50E913119FC5CE4E7404DE0000D0B3)



FactoryBean仍然是一个bean，但不同于普通bean，它的实现类最终也需要注册到BeanFactory中。它也是一种简单工厂模式的接口类，但是生产的是单一类型的对象，与BeanFactory生产多种类型对象不同。

FactoryBean是一个接口，实现了这个接口的类，在注册到spring BeanFactory后，并不像其它类注册后暴露的是自己，它暴露的是FactoryBean

中getObject方法的返回值。这一点可在FactoryBean的类注释上找到证据。



/** * Interface to be implemented by objects used within a {@link BeanFactory} which * are themselves factories for individual objects. If a bean implements this * interface, it is used as a factory for an object to expose, not directly as a * bean instance that will be exposed itself. * * <p><b>NB: A bean that implements this interface cannot be used as a normal bean.</b> * A FactoryBean is defined in a bean style, but the object exposed for bean * references ({@link #getObject()}) is always the object that it creates.

比如：在spring配置文件中配置了名字 myFcBean 的一个类型，该类型是 FactoryBean 的实现类。那么通过

BeanFactory.getBean(“myFcBean”) 返回的并不是这个类型本身的对象，而是调用这个对象的getObject方法的返回值。



FactoryBean的结构见下图



![img](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=1218780934,3491530618&fm=173&app=49&f=JPEG?w=300&h=228&s=8670E9320B5A644D16D5C5DA0000C0B3)