## CentOS7



### CentOS7查看和关闭防火墙

CentOS 7.0默认使用的是firewall作为防火墙

查看防火墙状态

```java
firewall-cmd --state1
```

停止firewall

```java
systemctl stop firewalld.service1
```

禁止firewall开机启动

```java
systemctl disable firewalld.service 
```



