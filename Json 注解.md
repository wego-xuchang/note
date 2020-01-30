# Json 注解

Jackson JSON 框架中包含了大量的注解来让我们可以干预 Jackson 的 JSON 处理过程，
例如我们可以通过注解指定 java pojo 的某些属性在生成 json 时被忽略。

这里主要介绍如何使用 Jackson 提供的注解。
Jackson注解主要分成三类，一是只在序列化时生效的注解；二是只在反序列化时候生效的注解；三是两种情况下都生效的注解。

### 1.@JsonIgnoreProperties

在类上注解哪些属性不用参与序列化和反序列化

```java
@Data
@JsonIgnoreProperties(value = { "word" })  
public class Person { 
  private String hello;
  private String word;
}
```

### 2.@JsonIgnore

此注解用于属性或者方法上（最好是属性上），作用和上面的@JsonIgnoreProperties一样。

```java
@Data
public class Person { 
  private String hello;
  @JsonIgnore
  private String word;
}
```

### 3.@JsonFormat

格式化序列化后的字符串

```java
@Data
public class Person{
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+8")
  private Date time;
  private String hello;
  private String word;
}
pattern:
yyyy-MM-dd
yyyy-MM-dd HH:mm:ss
HH:mm:ss
yyyy-MM-dd HH:mm:ss
```

### 4.@JsonSerialize

序列化的时候通过重写的方法，可以加在get方法上，也可以直接加在属性上

```java
@Data
public class Person { 
  private String hello;
  private String word;
  @JsonSerialize(using = CustomDoubleSerialize.class)
  private Double money;
}

public class CustomDoubleSerialize extends JsonSerializer<Double> {  
   private DecimalFormat df = new DecimalFormat("#.##"); 
   @Override    
   public void serialize(Double value, JsonGenerator jgen, SerializerProvider provider) throws IOException,            JsonProcessingException { 
       jgen.writeString(df.format(value));    
   }
}




@Data
public class CourseQuziResult {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long courseQuziId;
}

```

### 5.@JsonDeserialize

反序列化的时候通过重写的方法，可以加在set方法上，也可以直接加在属性上

```java
@Data
public class Person { 
  private String hello;
  private String word; 
  @JsonDeserialize(using = CustomDateDeserialize.class)
  private Date time;
}

public class CustomDateDeserialize extends JsonDeserializer<Date> {  
  
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
  
    @Override  
    public Date deserialize(JsonParser jp, DeserializationContext ctxt)  
            throws IOException, JsonProcessingException {  
  
        Date date = null;  
        try {  
            date = sdf.parse(jp.getText());  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }  
        return date;  
    }  
} 
```

### 6.@JsonInclude

Include.Include.ALWAYS 默认
Include.NON_DEFAULT 属性为默认值不序列化
Include.NON_EMPTY 属性为 空（“”） 或者为 NULL 都不序列化
Include.NON_NULL 属性为NULL 不序列化

```java
@Data
public class OrderProcessTime { 
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Date plan;
}
```

### 7.@JsonProperty

用于表示Json序列化和反序列化时用到的名字，例如一些不符合编程规范的变量命名

```java
@Data
public class Person { 
  private String hello;
  private String word; 
  private Date time;
  @JsonProperty(value = "DeliveryTime")
  private Integer deliveryTime;
}
```

### 8.@Transient

如果一个属性并非数据库表的字段映射，就务必将其标示为@Transient，否则ORM框架默认其注解为@Basic；

```java
//表示该字段在数据库表中没有

@Transient

public int getAge() {

　return 1+1;

}
```

### 9.@JsonIgnoreType:

标注在类上，当其他类有该类作为属性时，该属性将被忽略。

## 只在序列化情况下生效的注解

### 1. @JsonPropertyOrder

在将 java pojo 对象序列化成为 json 字符串时，使用 @JsonPropertyOrder 可以指定属性在 json 字符串中的顺序。

### 2. @JsonInclude

在将 java pojo 对象序列化成为 json 字符串时，使用 @JsonInclude 注解可以控制在哪些情况下才将被注解的属性转换成 json，例如只有属性不为 null 时。

**@JsonInclude(JsonInclude.Include.NON_NULL)**
这个注解放在类头上，返给前端的json里就没有null类型的字段，即实体类与json互转的时候 属性值为null的不参与序列化。
另外还有很多其它的范围，例如 NON_EMPTY、NON_DEFAULT等

 

## 在反序列化情况下生效的注解

### 1.@JsonSetter

@JsonSetter 标注于 setter 方法上，类似 @JsonProperty ，也可以解决 json 键名称和 java pojo 字段名称不匹配的问题。