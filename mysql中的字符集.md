# mysql中的字符集



### **一、字符集(Character set)**

　　字符集是多个字符(英文字符，汉字字符，或者其他国家语言字符)的集合，字符集种类较多，每个字符集包含的字符个数不同。

1）特点：

①字符编码方式是用一个或多个字节表示字符集中的一个字符

②每种字符集都有自己特有的编码方式，因此同一个字符，在不同字符集的编码方式下，会产生不同的二进制

2）常见字符集：

ASCII字符集：基于罗马字母表的一套字符集，它采用1个字节的低7位表示字符，高位始终为0。

LATIN1字符集：相对于ASCII字符集做了扩展，仍然使用一个字节表示字符，但启用了高位，扩展了字符集的表示范围。

GBK字符集：支持中文，字符有一字节编码和两字节编码方式。

UTF8字符集：Unicode字符集的一种，是计算机科学领域里的一项业界标准，支持了所有国家的文字字符，utf8采用1-4个字节表示字符。

### **二、校对规则**

校对规则(collation)：是在字符集内用于字符比较和排序的一套规则，比如有的规则区分大小写，有的则无视。

### **三 、utf8_bin、utf8_general_ci与utf8_unicode_ci的区别。**

ci是 case insensitive, 即 "大小写不敏感", a 和 A 会在字符判断中会被当做一样的;

bin 是二进制, a 和 A 会别区别对待.

例如你运行: 

​                 SELECT * FROM table WHERE txt = 'a';

那么在utf8_bin中你就找不到 txt = 'A' 的那一行, 而 utf8_general_ci 则可以。

1) utf8_general_ci 不区分大小写，这个你在注册用户名和邮箱的时候就要使用。

2) utf8_general_cs 区分大小写，如果用户名和邮箱用这个 就会照成不良后果。

3) utf8_bin:字符串每个字符串用二进制数据编译存储。 区分大小写，而且可以存二进制的内容。

4) utf8_unicode_ci和utf8_general_ci对中、英文来说没有实质的差别。

5) utf8_general_ci校对速度快，但准确度稍差。（准确度够用，一般建库选择这个）

6) utf8_unicode_ci准确度高，但校对速度稍慢。

### 四 、utf8_general_ci与utf8_unicode_ci的区别

一般的解说是 utf8_general_ci 速度较快，utf8_unicode_ci 准确性较好 … 但快在哪里，又好在什么地方呢？

首先就其准确性说，这世界上有无数种文字，除了通用的英文使用我们较熟悉的 A-Z 字符外，还有很多种近似的写法用于法文、德文、俄文等等。



![img](https:////upload-images.jianshu.io/upload_images/13553304-724a395180c124a3.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/662/format/webp)



光是一个 A 字就有数十种不同表现

为什么不都用同一种 A 呢，几十种多麻烦啊。事实上，它们在所属的文字上都各有意义，可能代表不同的发音，也可能是其他东西。在某些语言上，同一个单词不同发音可能就代表两个意思。

**校对规则**

utf8_unicode_ci 及 utf8_general_ci 的用途就是对那些看起来不一样的字符进行转换，令我们进行排序比对时更方便准确。

以下面为例，直接看起来是不相等的，但在实际应用上是成立的，这就是 utf8_unicode_ci 及 utf8_general_ci 工作的结果。

Ä = A

**Ö = O**

**Ü = U**

**utf8_unicode_ci的最主要的特色是支持扩展，将一个特别字符转换成多于一个英文字符即当把一个字母看作其它字母组合相等**。以德文中的 s 例:

在 utf8_unicode_ci 下面等式是成立

**ß = ss**

在 utf8_general_ci 只有这样才成立

**ß = s**

而 且utf8_unicode_ci 准确性较好还表现在它有更完整的字元对照表上。因为 utf8_unicode_ci 有更完整字符表及转换规则，所以在排序的准确性上也比 utf8_general_ci 高一些。





![img](https:////upload-images.jianshu.io/upload_images/13553304-72461a1ec57cfde5.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/662/format/webp)



以上utf8_general_ci 字符表的一部份





![img](https:////upload-images.jianshu.io/upload_images/13553304-9d1f9e5a94dc9245.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/662/format/webp)



utf8_unicode_ci 有更完整的字符表

**效能：**同样因为 utf8_unicode_ci 的字符表及转换规则更复杂，所以在效能上比 utf8_general_ci 慢。

**总结：**如果你的应用是德文、俄文等等，或者需要精确处理国际化的内容，请用 utf8_unicode_ci 。否则的话用 utf8_general_ci 就可以了。

**备注：**虽然说 utf8_unicode_ci 的字集比较完整，但其实还是不全的，所以 MySQL 还提供了很多种其他语言的专用字集，用于特别的本地应用，详细可以看看下面的官网连结

### 五 、utf8与utf8mb4（utf8 most bytes 4）

- MySQL 5.5.3之后增加了utfmb4字符编码
- 支持BMP（Basic Multilingual Plane，基本多文种平面）和补充字符
- 最多使用四个字节存储字符

utf8mb4是utf8的超集并完全兼容utf8，能够用四个字节存储更多的字符。

标准的UTF-8字符集编码是可以使用1-4个字节去编码21位字符，这几乎包含了世界上所有能看见的语言。
**MySQL里面实现的utf8最长使用3个字符**，包含了大多数字符但并不是所有。例如emoji和一些不常用的汉字，如“墅”，这些需要四个字节才能编码的就不支持。

### 六 、字符集、连接字符集、排序字符集

utf8mb4对应的排序字符集有utf8mb4_unicode_ci、utf8mb4_general_ci.

utf8mb4_unicode_ci和utf8mb4_general_ci的对比：

- 准确性：
  - utf8mb4_unicode_ci是基于标准的Unicode来排序和比较，能够在各种语言之间精确排序
  - utf8mb4_general_ci没有实现Unicode排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
  - 但是，在绝大多数情况下，这些特殊字符的顺序并不需要那么精确。
- 性能
  - utf8mb4_general_ci在比较和排序的时候更快
  - utf8mb4_unicode_ci在特殊情况下，Unicode排序规则为了能够处理特殊字符的情况，实现了略微复杂的排序算法。
  - 但是在绝大多数情况下发，不会发生此类复杂比较。相比选择哪一种collation，使用者更应该关心字符集与排序规则在db里需要统一。



utf8_general_ci 不区分大小写，这个你在注册用户名和邮箱的时候就要使用。

utf8_general_cs 区分大小写，如果用户名和邮箱用这个就会照成不良后果。

utf8_bin: compare strings by the binary value of each character in the string 将字符串每个字符串用二进制数据编译存储，区分大小写，而且可以存二进制的内容。

今天在创建新的数据库的时候，在“整理”选项选择的时候，通常就直接选择utf_bin ,但是有时候也用utf_general_ci,用了这么长时间，发现自己竟然不知道这两者到底有什么区别。

ci是 case insensitive, 即 "大小写不敏感", a 和 A 会在字符判断中会被当做一样的。

bin 是二进制, a 和 A 会别区别对待。

例如你运行:

SELECT * FROM table WHERE txt = 'a'

那么在utf8_bin中你就找不到 txt = 'A' 的那一行, 而 utf8_general_ci 则可以。

我们知道utf8下面还有很多 选项，我们没有必要去一一掌握，但是需要了解。

utf8_unicode_ci校对规则仅部分支持Unicode校对规则算法,一些字符还是不能支持。

utf8_unicode_ci不能完全支持组合的记号。

utf8_general_ci是一个遗留的 校对规则，不支持扩展，它仅能够在字符之间进行逐个比较。这意味着utf8_general_ci校对规则进行的比较速度很快，但是与使用 utf8_unicode_ci的校对规则相比，比较正确性较差。

应用上的差别

1、对于一种语言仅当使用utf8_unicode_ci排序做的不好时，才执行与具体语言相关的utf8字符集校对规则。例如，对于德语和法语，utf8_unicode_ci工作的很好，因此不再需要为这两种语言创建特殊的utf8校对规则。

2、utf8_general_ci也适用与德语和法语，除了‘?’等于‘s’，而不是‘ss’之外。如果你的应用能够接受这些，那么应该使用 utf8_general_ci，因为它速度快。否则，使用utf8_unicode_ci，因为它比较准确。

用一句话概况上面这段话：utf8_unicode_ci比较准确，utf8_general_ci速度比较快。通常情况下 utf8_general_ci的准确性就够我们用的了，在我看过很多程序源码后，发现它们大多数也用的是utf8_general_ci，所以新建数据 库时一般选用utf8_general_ci就可以了。