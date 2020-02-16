目录

- [上节内容回顾：](https://www.cnblogs.com/progor/p/10331531.html#上节内容回顾)
- npm
  - [介绍](https://www.cnblogs.com/progor/p/10331531.html#介绍)
  - [安装](https://www.cnblogs.com/progor/p/10331531.html#安装)
  - [常用命令：](https://www.cnblogs.com/progor/p/10331531.html#常用命令)
  - [补充：](https://www.cnblogs.com/progor/p/10331531.html#补充)
- [基于npm的Hello World](https://www.cnblogs.com/progor/p/10331531.html#基于npm的hello-world)
- [项目结构分析](https://www.cnblogs.com/progor/p/10331531.html#项目结构分析)
- [用法迁移](https://www.cnblogs.com/progor/p/10331531.html#用法迁移)
- 小提醒
  - [ES6语法](https://www.cnblogs.com/progor/p/10331531.html#es6语法)
  - [知识补充](https://www.cnblogs.com/progor/p/10331531.html#知识补充)
- 单文件组件
  - [使用注意：](https://www.cnblogs.com/progor/p/10331531.html#使用注意)
- 路由
  - [开启路由](https://www.cnblogs.com/progor/p/10331531.html#开启路由)
  - [定义路由](https://www.cnblogs.com/progor/p/10331531.html#定义路由)
  - 使用路由
    - [数据显示](https://www.cnblogs.com/progor/p/10331531.html#数据显示)
    - [路由跳转](https://www.cnblogs.com/progor/p/10331531.html#路由跳转)
  - 带参路由
    - [动态路径参数](https://www.cnblogs.com/progor/p/10331531.html#动态路径参数)
    - [查询参数](https://www.cnblogs.com/progor/p/10331531.html#查询参数)
    - [监听路由参数变化](https://www.cnblogs.com/progor/p/10331531.html#监听路由参数变化)
  - [嵌套路由](https://www.cnblogs.com/progor/p/10331531.html#嵌套路由)
  - [命名视图](https://www.cnblogs.com/progor/p/10331531.html#命名视图)
  - [编程式路由](https://www.cnblogs.com/progor/p/10331531.html#编程式路由)
  - [404路由](https://www.cnblogs.com/progor/p/10331531.html#路由-1)
  - [补充：](https://www.cnblogs.com/progor/p/10331531.html#补充-1)
- vuex
  - [开启vuex并创建store](https://www.cnblogs.com/progor/p/10331531.html#开启vuex并创建store)
  - 使用vuex
    - [获取数据](https://www.cnblogs.com/progor/p/10331531.html#获取数据)
    - [改变数据](https://www.cnblogs.com/progor/p/10331531.html#改变数据)
  - [项目结构](https://www.cnblogs.com/progor/p/10331531.html#项目结构)
  - [补充：](https://www.cnblogs.com/progor/p/10331531.html#补充-2)

首发日期：2019-01-28
修改：

1. 2019-01-29：增加404路由

------

# 上节内容回顾：

- 组件的注册（全局，局部）
- 组件的数据传输（父子组件，非父子组件）
- props（数据校验）
- 给组件绑定原生事件
- template
- 插槽（插槽的作用，命名插槽，插槽的默认内容，作用域插槽）
- 动态组件
- $refs

------

# npm





## 介绍

> npm是什么?
> npm 是 JavaScript 世界的包管理工具。js也是可以建成一个项目的，很多时候前端的功能不可能是纯粹的自己写的，我们会去用别人造好的轮子（日历插件，图表插件之类的），如果我们自己去找轮子，那么首先要去那个轮子的官网下载（有时候这个步骤会很麻烦）。而npm的服务器上会收集了很多常用的js相关的文件（不仅仅是js，更准确的说是代码模块，通常都是集成一个包），我们可以利用npm来下载项目的依赖模块（包）。
> 除了依赖，npm还允许用户从服务器下载并安装别人编写的命令行程序到本地使用。【比如vue-cli,安装了vue-cli,就可以使用这个程序来快速建立一个基础的vue项目】



## 安装

- 首先，你需要到[node.js 官网](https://nodejs.org/en/download/)下载适合版本的node
- 安装node 【省略安装过程，实在不懂的，可以自行百度】
- 测试node是否正常，在命令行输入下面**两条**命令：`node -v`和`npm -v`，如果输出了各自的版本号，那么就是安装成功了。



## 常用命令：

这些命令可以先暂时不记，后面用到的时候还会提一下。

- 安装指定模块：`npm install 包名` 【如果要全局安装，那么要加上参数-g】【一些第三方的包也是程序，安装的时候也是使用`npm install 程序名`】
- 安装项目中package.json所包含的模块：`npm install`
- 卸载模块：`npm uninstall 包名` 【如果还想从package.json中删除模块，那么还需要加上参数--save,即`npm uninstall --save 包名`】
- 启动项目：`npm start`
- 查看已经安装的模块：`npm ls`
- 升级npm版本:`npm install -g npm`



## 补充：

- npm下载模块的时候需要从服务器上下载，如果你嫌慢，可以考虑更换源，使用国内的cnpm：`npm install -g cnpm --registry=http://registry.npm.taobao.org`



------

# 基于npm的Hello World



1.进入命令行，键入命令`npm install -g vue-cli`【这一步是安装vue脚手架vue-cli，vue-cli能够快速构建一个vue的基础项目出来】
2.使用脚手架来创建一个vue项目：`vue init webpack my-project` 【webpack是创建vue项目的方式，my-project是vue项目的名称】
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127002636.png)



3.等待下载完模块后，使用cd命令进入项目文件夹后，输入`npm run dev`来运行项目。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127125957.png)
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127130142.png)



4.访问控制台提示的url，就可以看到基于npm的hello world了。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127130201.png)



5.访问localhost:8080,可以看到如下的界面，说明一个基础的vue项目已经初始化完成了：
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127130240.png)



> 为什么不使用静态导入来演示后面的例子了？
> 在后面，我们将讲到多个组件之间的复合使用，在有多个组件的时候，使用单文件来注册组件是一个比较好的处理，而且在项目扩大的时候会涉及到方方面面的问题（路由啊，数据管理啊等等），这时候静态导入就不太适合了，开发效率太低了。



------

# 项目结构分析



项目构建完之后，让我们来分析一下“该在哪个目录写下哪些代码”
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127130536.png)

- node_modules目录：node_modules是项目依赖模块的目录，里面的一个个文件夹都是项目依赖的模块。一般不需要理会。我们使用npm install来安装依赖模块的时候就是把模块下载到这里。
- build目录：build目录是关于项目构建信息的目录，里面主要是webpack构建项目的一些配置。现在暂时不讲，后面再讲。
- config目录：
  - config目录是存放项目的配置文件的目录。
  - 基础配置信息在index.js
  - 开发环境下的配置信息：dev.env.js
  - 线上产品环境下的配置信息：prod.env.js
- static目录：存放项目的静态资源文件，这个目录是开放访问的。
- package.json和package-lock.json文件：
  - package.json文件包含了项目依赖模块的列表，使用npm 来安装项目依赖模块的时候都是从package.json来判断需要什么依赖的。
  - package-lock.json文件包含了项目依赖模块的一些限制信息（限制版本号，限制下载地址，限制哈希值）
- .eslintrc.js和.eslintignore文件：
  - .eslintrc.js是关于eslint的语法规则的文件。
  - .eslintignore是指定哪些文件或文件夹忽略eslint语法检测。
- index.html文件：是当前项目的首页文件。
- .babelrc文件：是一个关于vue语法与js语法转换的文件【一般不需要理会】
- src目录：是项目的源代码目录
  - main.js：是项目的入口文件，包含了新建根实例等操作。（以后可能还会有全局注册组件等操作。）
  - router目录：用于设置vue路由信息的目录
  - store目录：用于设置vuex数据管理的目录
  - App.vue文件：是项目的根组件。
  - components目录：组件相关的目录，单文件组件一般都存放在这里
  - assets目录：静态资源存放处【与static的区别是，static会原封不动地构建起来，而assets会被webpack进行处理，路径什么的webpack会自动管理好的】



**上面的文件具体不需要太理解，我们只需要关注src目录。我们只需要在src目录下写代码就好。下面将介绍怎么在src目录下写代码。**



------

# 用法迁移



项目换到了npm上后，先了解一下前面学习过的内容怎么在npm创建的vue项目中使用吧。



> 1.还需不需要手动创建根实例？
> 已经不需要了，在main.js文件会自动创建一个根实例。
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127135140.png)



> 2.根实例管理区域在哪里？
> 从main.js文件中，可以看到根实例所对应的区域的id还是app,而这个app在index.html中【你可以尝试修改index.html，确定一下是这个区域，比如说可以修改一些页面的title】
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127135219.png)



> 3.是如何渲染出上面的Hello World页面的？
> 可以看到出来main.js是核心的入口文件，它创建了一个根实例来管理了index.html的app区域，并且声明了内部使用的template,所以在渲染的时候，app区域里面会渲染成`<App/>`,而App是一个组件（这是单文件组件，App.vue），所以也就是把App组件中的内容显示到app区域中。



> 4.怎么修改首页？
> 从上面来看，首页就是index.html,核心的显示区域是app区域，而app区域显示的是组件App的内容，如果我们修改App组件(App.vue)中的内容就可以修改首页了。



> 5.怎么注册组件？
> 使用npm来构建项目后，一个组件可以定义在一个单独的vue中。
> 除了以往的那些全局注册，局部注册，你还可以使用单个vue文件来注册一个组件（实质相当于局部注册）。这个步骤这里可以简单地说一下，首先定义一个vue文件，例如App.vue文件，然后在App.vue中定义组件的内容，并且要使用export来“导出”组件，在需要的地方使用import 来导入组件，然后就可以使用组件了。（上面的main.js使用App.vue组件的时候也有一行import App from './App'）
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127161354.png)
> 【上图有个问题，如果你直接打，你会发现页面的color都变了，如果你想要在当前组件生效，那么可以在style后加个scoped】
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127161714.png)



> 6.组件的常用属性的定义：
> 基本还是按以前的规则，比如data要是一个函数。
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127163229.png)



> 7.router-view是什么？
> 如果你尝试修改App.vue，你会发现里面有一个router-view，这是什么呢？这是一个用于路由显示的元素。它会装载当前路由所定义的内容。如果你学过iframe或者frame，你就明白这个东西的意义，router-view会根据当前所处的url来显示对应的内容（这个路由对应的内容在router目录下的index.js中）。
> ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127171122.png)



------

# 小提醒



## ES6语法

这里提一下一些在Vue中常用的ES6语法：

- 如果键名和值一样，可以单写键名。
  - 所以在使用components的时候，如果组件名和组件别名一致，那么可以单写一个。
  - 其他情况如果key和value是一样的时候也可以单写key：
    ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127164437.png)
- 如果是一个函数，可以单写一个括号
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127163601.png)



## 知识补充

- 在import的时候，如果不写后缀，会自动查找符合后缀的。顺序是：.vue->.js->.json。`import Hello from '@/components/Hello'`
- 在上面使用了`@`,这是一个预定义的路径，代表src目录。这个预定义路径是webpack给我们的。
- [export,import,export default](https://www.cnblogs.com/xiaotanke/p/7448383.html)
- index.html中的id='app'与App.vue中的id='app'的区别：首先index.html里的app给vue找到了管理区域，然后在使用template的时候，会把index.html中的`<div id="app"></div>`渲染成`<App/>`,然后`<App/>`再渲染成对应的组件的内容。也就是说index.html里面的app提供了装载点，App.vue提供了装载的内容。【如果修改了App.vue里面的id，你会发现渲染成功后页面的id会变。】【而为了使用好app这个实例，不要修改App.vue的id，不然装载完内容后，对应的管理区域会指向不明，而导致渲染失败。】



------

# 单文件组件



1.新建vue文件
我选择在src/components下新建一个Hello.vue


2.定义组件的内容

```
<template>
  <div class='hello'>{{msg}}</div>
</template>

<script>
export default {
  name: 'Hello',
  data () {
    return {
      msg: 'This is my msg!'
    }
  },
  created: function () {
    console.log('haha')
  }
}
</script>

<style scoped>
.hello{
  color: red
}
</style>
```



3.在其他文件中使用组件（这个所谓的其他文件，也可以是其他组件）:首先使用import来导入组件，然后使用components来声明使用组件，最后在组件的template中使用组件

```
<template>
    <hello></hello>
</template>

<script>
import Hello from '@/components/Hello'
export default {
  name: 'HelloWorld',
  components: {
    Hello
  }
}
</script>
```



## 使用注意：

- **组件的内容必须有一个根元素包裹，除了template以外，template里面还得有一个根元素。**
- 要进行export default导出，不然这个组件无法在其他地方使用。
- 定义组件的其他属性要遵循以往的格式，如data要是一个函数。



------

# 路由



- 在以往，都是通过a元素来进行页面跳转的，跳转的过程可以称为“路由跳转”，在这里是一个页面到另外一个页面的过程。
- 而在vue中，由于是单页面应用，所以这里的路由本质上是进行了页面重渲染（同时修改了页面的地址）。



## 开启路由

如果要使用路由，那么首先要开启功能：
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127170915.png)



## 定义路由

路由与内容的对应信息定义在router目录下的index.js文件中。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127171122.png)



## 使用路由

### 数据显示

- 首先，路由的数据是使用`<router-view></router-view>`来显示的，`<router-view></router-view>`会渲染出当前路由对应的内容。
- 我们可以尝试修改一下路由对应信息来显示其他组件信息：
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127171445.png)



### 路由跳转

`<router-link></router-link>`可以实现点击后跳转到指定路由的功能。
`<router-link to="/hello">hello</router-link>`点击后可以跳转到`/hello`路由。



## 带参路由

### 动态路径参数

所谓路径参数，就是形似`https://xxxx.xxx.com/info/1011337448`中的1011337448这个参数。这个参数告诉了这个页面该怎么渲染。
在路由中，我们可以使用形如`/path/:id`的方式来匹配参数，`:`后面跟的是参数名；
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127210329.png)
然后在组件中使用`this.$route.params.参数名`来获取参数。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127210352.png)

路径参数是可以有多个的，如`/hello/:id/:name`，这是分别利用`this.$route.params.参数名`来获取参数。



### 查询参数

所谓查询参数，就是形如`https://xxxx.xxx.com/info?name='lilei'`中name='lilei'的这个参数。
给下一个路由带查询参数可以使用如下的方法：
1.`<router-link :to="{path: '/hello',query:{ id :'001'}}" >hello</router-link>`,点击这个得到的url是`/hello?id=001`。【这里使用路由的path来标识路由】



2.`<router-link :to="{ name: 'user', query: { userId: 123 }}">User</router-link>`【这里使用路由的name来标识路由】
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128015339.png)
然后在组件中我们可以使用`$route.query.参数名`来获取参数
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128015258.png)



### 监听路由参数变化

如果想要监听路由参数变化，那么可以使用watch来监听。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127211422.png)



## 嵌套路由

- 假如已经有了一个路由/admin,然后再有一个路由/admin/sys的话就是路由进行了嵌套。【这是一种非常常见的情况】
- 我们上面知道了路由的信息是由router-view来显示的，那么嵌套的路由里的信息要用什么显示呢？其实还是用`<router-view>`，`<router-view>`也是可以嵌套的。
- 如果我们在路由的index.js中使用了children来定义嵌套路由，并且在子组件中也创建了一个router-view，那么嵌套路由的数据会显示在子组件的`<router-view>`中，而不是父组件的router-view中（具体看下图）



步骤：
1.首先，在路由中使用children来定义嵌套路由
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127213919.png)
2.先访问/hello,确认App.vue中的router-view渲染了路由/hello的数据
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127214117.png)
3.在Hello.vue中写一些文字，用来确定路由切换时，第一级的router-view的数据没有被清除。同时新建一个router-view。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127214350.png)
4.随便定义一点Child.vue的内容。
5.先访问/hello,再访问/hello/child:
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127214703.png)



## 命名视图

- 上面讲了`router-view`的嵌套，如果想要在同一组件内使用多个router-view呢？（为什么会需要多个router-view呢？有可能想要发生某些路由变化的时候，页面某一部分不发生变化，只在某些情况发生变化，比如侧边栏这样的东西。通常都想要侧边栏不变化，只有中间的显示区域发生变化，但如果退出登录了，那么两个部分应该都发生变化。）
- 可以给`router-view`添加一个属性name,从而让router-view有名字。`<router-view name='left'/>`
- 当一个组件中有多个命名`router-view`的时候，路由该怎么定义呢？
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127221042.png)
- 如果想要切换路由的时候，希望某些命名路由不变，那么可以这样做：
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127231809.png)
- 嵌套路由也可以与命名路由结合使用：
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190127232328.png)



## 编程式路由

上面的使用router-link的方式可以称为声明式路由，下面讲的这种叫做编程式路由。
我们可以使用`this.$router.push('路由的path')`来进行路由跳转

```
<template>
    <div>雷猴
      <button @click='search'>跳转</button>
    </div>
</template>
<script>
export default {
  name: 'HelloWorld',
  methods: {
    search () {
      // this.$router.push('user') // 根据路由的path来跳转
      // this.$router.push({name: 'user2'}) // 根据路由的name来跳转
      // this.$router.push({ name: 'user', params: { id: '123' }})
      // 上一行是根据路由的name来跳转，带路径参数,要求path带路径参数[如果提供了 path，params 会被忽略]
      this.$router.push({path: '/user', query: {id: 123}}) // 带查询参数
    }
  }
}
</script>
```



## 404路由

在上面的路由匹配中，如果我们访问了一个没有进行定义的路由，那么页面会显示空白。
我们可以在路由的最下面定义一个如下的路由：
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190129233630.png)
这样就可以把匹配不到的路由都渲染成指定的页面。

## 补充：

- 路由的复用问题：当路由跳转时，会复用没有发生变化的组件。
- 没有说的内容：通配路由、匹配优先级、[路由组件传参](https://router.vuejs.org/zh/guide/essentials/passing-props.html#路由组件传参)、[重定向和别名](https://router.vuejs.org/zh/guide/essentials/redirect-and-alias.html#重定向和别名)。
- 一些路由的进阶知识也没讲，如果你觉得上面的路由知识不够用，也可以上官网看一下。[vue-router](https://router.vuejs.org/zh/)



------

# vuex



- [官网的话:]Vuex 是一个专为 Vue.js 应用程序开发的状态管理模式。它采用集中式存储管理应用的所有组件的状态，并以相应的规则保证状态以一种可预测的方式发生变化。
- [小菜鸟的话:]vuex可以用来存储多个组件之间需要共享的数据，并且能够以一种方式来提醒其他组件某个数据发生了变化。有些数据获取了之后应该是在所有组件里面都共享的。比如某些应用中的“城市”这个数据，当选定了城市后，后面的组件的渲染都应该知道选定的城市是什么。

![vuex](https://raw.githubusercontent.com/vuejs/vuex/dev/docs/.vuepress/public/vuex.png)



- Vuex由Actions,Mutations,State组成。
  - Actions负责数据的处理，
  - Mutations负责数据修改，
  - State负责数据存储。
- **组件想要提交数据到Actions,要使用dispatch;**
- Action要提交数据到Mutations,要使用commit。



## 开启vuex并创建store

1.首先进行安装：`npm install vuex --save`
2.然后声明使用vuex，并创建一个store:
【对于大型应用，我们会希望把 Vuex 相关代码分割到模块中。所以下面的代码写在src/store/index.js中（请手动新建）】

```
import Vue from 'vue'
import Vuex from 'vuex'
// 1. 在src\store\index.js中声明使用vuex
Vue.use(Vuex)
// 2.新建一个store,用来存储数据
export default new Vuex.Store({
  state: { // store里面的state用来存储数据，数据以键值对形式存储
    global_info: 'global_info_msg'
  }
})
```



3.在根实例中使用store:
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128163253.png)



## 使用vuex

上面创建store的时候存储了一个数据进去，下面讲一下怎么操作这个数据。

### 获取数据

【由于在根实例中使用了store，所以所有的子实例都会有store，我们可以使用`this.$store.state.数据名`来获取数据】
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128185745.png)



### 改变数据

改变store中的数据有两种方法，一种是actions->mutations->state;一种是mutations->state。
1.通过actions来改变数据：
首先派发action给actions，使用`this.$store.dispatch('action名',参数)`来派发action【派发action给actions之后，actions会调用action来处理数据】
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128190143.png)
定义action如何处理数据，然后使用`commit('mutation名', 参数)`把数据提交给mutations：
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128190554.png)
定义mutation修改哪个数据，利用赋值表达式赋值：
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128190815.png)



上面的修改数据的操作遵循了下图的顺序：
![vuex](https://raw.githubusercontent.com/vuejs/vuex/dev/docs/.vuepress/public/vuex.png)





2.除了一步步来修改数据，还可以直接通过mutations来改变数据：`this.$store.commit('mutation名',参数)`，这时候我们只需要定义一个mutations即可。
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128191014.png)
![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128191046.png)



## 项目结构

- 对于大型应用，我们会希望把 Vuex 相关代码分割到模块中。
- 我们可能会有多次进行上面的store中的数据操作，那么这意味着我们需要写多几个action和mutation,如果把这些action和mutation都写在index.js是很赘余的，我们通常把action的内容写到actions.js中，把mutations的内容写到mutations.js中。
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128191256.png)
  actions.js的内容可以类似这样来写：
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128192133.png)
  然后在index.js中导入：
  ![img](https://progor.oss-cn-shenzhen.aliyuncs.com/img/20190128192155.png)
  mutations.js的内容可以参考上面action.js的方式来写。