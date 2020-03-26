## Maven引入本地jar

本地jar的相对位置，定义版本和生命周期

```xml
<dependency>
    <groupId>com.eos.foundation</groupId>
    <artifactId>eos6</artifactId>
    <version>6.1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/src/lib/com.eos.foundation.jar</systemPath>
</dependency>
```

启动类的pom文件

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <includeSystemScope>true</includeSystemScope>
            </configuration>
        </plugin>
    </plugins>
</build>
```

