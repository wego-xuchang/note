## Vue教程
  [官方文档](https://cn.vuejs.org/v2/guide/syntax.html)

## 1、Vue.js 是什么
Vue (读音 /vjuː/，类似于 view) 是一套用于构建用户界面的渐进式框架。与其它大型框架不同的是，Vue 被设计为可以自底向上逐层应用。Vue 的核心库只关注视图层，不仅易于上手，还便于与第三方库或既有项目整合。另一方面，当与现代化的工具链以及各种支持类库结合使用时，Vue 也完全能够为复杂的单页应用提供驱动。

如果你想在深入学习 Vue 之前对它有更多了解，我们制作了一个视频，带您了解其核心概念和一个示例工程。

如果你已经是有经验的前端开发者，想知道 Vue 与其它库/框架有哪些区别，请查看对比其它框架。

### Vue.js与ECMAScript
Vue 不支持 IE8 及以下版本，因为 Vue 使用了 IE8 无法模拟的 ECMAScript 5 特性。
什么是ECMAScript?

![](E:\markdown\vue\ECMAScript.PNG)

## 2、基础
### 声明式渲染

1.文本 v-text v-model

	Vue.js 的核心是一个允许采用简洁的模板语法来声明式地将数据渲染进 DOM 的系统
	
		<div id="app">
		  {{ message }}
		</div>
	
		var app = new Vue({
		  el: '#app',
		  data: {
		message: 'Hello Vue!'
		  }
		})


​	
2.绑定 v-bind :

    绑定元素
    
        <div id="app-2">
          <span v-bind:title="message">
            鼠标悬停几秒钟查看此处动态绑定的提示信息！
          </span>
        </div>
        var app2 = new Vue({
          el: '#app-2',
          data: {
            message: '页面加载于 ' + new Date().toLocaleString()
          }
        })

你看到的 v-bind 特性被称为指令。指令带有前缀 v-，以表示它们是 Vue 提供的特殊特性。可能你已经猜到了，它们会在渲染的 DOM 上应用特殊的响应式行为。在这里，该指令的意思是：“将这个元素节点的 title 特性和 Vue 实例的 message 属性保持一致”。

3.条件与循环 v-if v-for

	<div id="app-3">
	  <p v-if="seen">现在你看到我了</p>
	</div>
	var app3 = new Vue({
	  el: '#app-3',
	  data: {
	    seen: true
	  }
	})

----------
	<div id="app-4">
	  <ol>
	    <li v-for="todo in todos">
	      {{ todo.text }}
	    </li>
	  </ol>
	</div>
	
	var app4 = new Vue({
	  el: '#app-4',
	  data: {
	    todos: [
	      { text: '学习 JavaScript' },
	      { text: '学习 Vue' },
	      { text: '整个牛项目' }
	    ]
	  }
	})


## 创建Vue 实例


### 1.MVVM模式
vue.js是一个MVVM的框架，理解MVVM有利于学习vue.js。
MVVM拆分解释为：
Model:负责数据存储
View:负责页面展示
View Model:负责业务逻辑处理（比如Ajax请求等），对数据进行加工后交给视图展示
MVVM要解决的问题是将业务逻辑代码与视图代码进行完全分离，使各自的职责更加清晰，后期代码维护更加简单用图解的形式分析Ajax请求回来数据后直接操作Dom来达到视图的更新的缺点，以及使用MVVM模式是如何来解决这个缺点的Vue中的 MVVM

### 2.创建一个Vue 实例

每个 Vue 应用都是通过用 Vue 函数创建一个新的 Vue 实例开始的：

	var vm = new Vue({
	  // 选项
	})

虽然没有完全遵循 MVVM 模型，但是 Vue 的设计也受到了它的启发。因此在文档中经常会使用 vm (ViewModel 的缩写) 这个变量名表示 Vue 实例。

当创建一个 Vue 实例时，你可以传入一个选项对象。这篇教程主要描述的就是如何使用这些选项来创建你想要的行为。作为参考，你也可以在 API 文档 中浏览完整的选项列表。

一个 Vue 应用由一个通过 new Vue 创建的根 Vue 实例，以及可选的嵌套的、可复用的组件树组成。举个例子，一个 todo 应用的组件树可以是这样的：

根实例

	└─ TodoList
	   ├─ TodoItem
	   │  ├─ DeleteTodoButton
	   │  └─ EditTodoButton
	   └─ TodoListFooter
	      ├─ ClearTodosButton
	      └─ TodoListStatistics
我们会在稍后的组件系统章节具体展开。不过现在，你只需要明白所有的 Vue 组件都是 Vue 实例，并且接受相同的选项对象 (一些根实例特有的选项除外)。

### 3.数据与方法
当一个 Vue 实例被创建时，它将 data 对象中的所有的属性加入到 Vue 的响应式系统中。当这些属性的值发生改变时，视图将会产生“响应”，即匹配更新为新的值。

	var data = { a: 1 }
	var vm = new Vue({
	  el: '#example',
	  data: data
	})
	
	vm.$data === data // => true
	vm.$el === document.getElementById('example') // => true
	
	// $watch 是一个实例方法
	vm.$watch('a', function (newValue, oldValue) {
	  // 这个回调将在 `vm.a` 改变后调用
	})

### 4.生命周期图示
每个 Vue 实例在被创建时都要经过一系列的初始化过程——例如，需要设置数据监听、编译模板、将实例挂载到 DOM 并在数据变化时更新 DOM 等。同时在这个过程中也会运行一些叫做生命周期钩子的函数，这给了用户在不同阶段添加自己的代码的机会。

![](https://cn.vuejs.org/images/lifecycle.png)

![](https://segmentfault.com/img/bVEs9x?w=847&h=572)

### 5.vue 生命周期分析

1) 初始化显示

* beforeCreate() 

* created() 
* beforeMount()
*   mounted(



2) 更新状态: this.xxx = value * beforeUpdate() * updated()
3) 销毁 vue 实例: vm.$destory() * beforeDestory() * destoryed()

### 6. 常用的生命周期方法

1) created()/mounted(): 发送 ajax 请求, 启动定时器等异步任务
2) beforeDestory(): 做收尾工作, 如: 清除定时器

生命周期

	<!DOCTYPE html>
	<html>
	<head>
	    <title></title>
	    <script type="text/javascript" src="https://cdn.jsdelivr.net/vue/2.1.3/vue.js"></script>
	</head>
	<body>
	
	<div id="app">
	     <p>{{ message }}</p>
	</div>
	
	<script type="text/javascript">
	    
	  var app = new Vue({
	      el: '#app',
	      data: {
	          message : "xuxiao is boy" 
	      },
	       beforeCreate: function () {
	                console.group('beforeCreate 创建前状态===============》');
	               console.log("%c%s", "color:red" , "el     : " + this.$el); //undefined
	               console.log("%c%s", "color:red","data   : " + this.$data); //undefined 
	               console.log("%c%s", "color:red","message: " + this.message)  
	        },
	        created: function () {
	            console.group('created 创建完毕状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el); //undefined
	               console.log("%c%s", "color:red","data   : " + this.$data); //已被初始化 
	               console.log("%c%s", "color:red","message: " + this.message); //已被初始化
	        },
	        beforeMount: function () {
	            console.group('beforeMount 挂载前状态===============》');
	            console.log("%c%s", "color:red","el     : " + (this.$el)); //已被初始化
	            console.log(this.$el);
	               console.log("%c%s", "color:red","data   : " + this.$data); //已被初始化  
	               console.log("%c%s", "color:red","message: " + this.message); //已被初始化  
	        },
	        mounted: function () {
	            console.group('mounted 挂载结束状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el); //已被初始化
	            console.log(this.$el);    
	               console.log("%c%s", "color:red","data   : " + this.$data); //已被初始化
	               console.log("%c%s", "color:red","message: " + this.message); //已被初始化 
	        },
	        beforeUpdate: function () {
	            console.group('beforeUpdate 更新前状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el);
	            console.log(this.$el);   
	               console.log("%c%s", "color:red","data   : " + this.$data); 
	               console.log("%c%s", "color:red","message: " + this.message); 
	        },
	        updated: function () {
	            console.group('updated 更新完成状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el);
	            console.log(this.$el); 
	               console.log("%c%s", "color:red","data   : " + this.$data); 
	               console.log("%c%s", "color:red","message: " + this.message); 
	        },
	        beforeDestroy: function () {
	            console.group('beforeDestroy 销毁前状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el);
	            console.log(this.$el);    
	               console.log("%c%s", "color:red","data   : " + this.$data); 
	               console.log("%c%s", "color:red","message: " + this.message); 
	        },
	        destroyed: function () {
	            console.group('destroyed 销毁完成状态===============》');
	            console.log("%c%s", "color:red","el     : " + this.$el);
	            console.log(this.$el);  
	               console.log("%c%s", "color:red","data   : " + this.$data); 
	               console.log("%c%s", "color:red","message: " + this.message)
	        }
	    })
	</script>
	</body>
	</html>

## 3、模板语法

### 插值
Vue.js 使用了基于 HTML 的模板语法，允许开发者声明式地将 DOM 绑定至底层 Vue 实例的数据。所有 Vue.js 的模板都是合法的 HTML ，所以能被遵循规范的浏览器和 HTML 解析器解析。

在底层的实现上，Vue 将模板编译成虚拟 DOM 渲染函数。结合响应系统，Vue 能够智能地计算出最少需要重新渲染多少组件，并把 DOM 操作次数减到最少。

如果你熟悉虚拟 DOM 并且偏爱 JavaScript 的原始力量，你也可以不用模板，直接写渲染 (render) 函数，使用可选的 JSX 语法
#### 文本
数据绑定最常见的形式就是使用“Mustache”语法 (双大括号) 的文本插值：

	<span>Message: {{ msg }}</span>
Mustache 标签将会被替代为对应数据对象上 msg 属性的值。无论何时，绑定的数据对象上 msg 属性发生了改变，插值处的内容都会更新。

通过使用 **v-once** 指令，你也能执行一次性地插值，当数据改变时，插值处的内容不会更新。但请留心这会影响到该节点上的其它数据绑定：

	<span v-once>这个将不会改变: {{ msg }}</span>
#### 原始 HTML

双大括号会将数据解释为普通文本，而非 HTML 代码。为了输出真正的 HTML，你需要使用 v-html 指令：

	<p>Using mustaches: {{ rawHtml }}</p>
	<p>Using v-html directive: <span v-html="rawHtml"></span></p>

这个 span 的内容将会被替换成为属性值 rawHtml，直接作为 HTML——会忽略解析属性值中的数据绑定。注意，你不能使用 v-html 来复合局部模板，因为 Vue 不是基于字符串的模板引擎。反之，对于用户界面 (UI)，组件更适合作为可重用和可组合的基本单位。
#### 特性
Mustache 语法不能作用在 HTML 特性上，遇到这种情况应该使用 v-bind 指令：

	<div v-bind:id="dynamicId"></div>
对于布尔特性 (它们只要存在就意味着值为 true)，v-bind 工作起来略有不同，在这个例子中：

	<button v-bind:disabled="isButtonDisabled">Button</button>
如果 isButtonDisabled 的值是 null、undefined 或 false，则 disabled 特性甚至不会被包含在渲染出来的` <button>` 元素中。

#### 使用 JavaScript 表达式
迄今为止，在我们的模板中，我们一直都只绑定简单的属性键值。但实际上，对于所有的数据绑定，Vue.js 都提供了完全的 JavaScript 表达式支持。

	{{ number + 1 }}
	
	{{ ok ? 'YES' : 'NO' }}
	
	{{ message.split('').reverse().join('') }}
	
	<div v-bind:id="'list-' + id"></div>
这些表达式会在所属 Vue 实例的数据作用域下作为 JavaScript 被解析。有个限制就是，每个绑定都只能包含单个表达式，所以下面的例子都不会生效。

	<!-- 这是语句，不是表达式 -->
	{{ var a = 1 }}
	
	<!-- 流控制也不会生效，请使用三元表达式 -->
	{{ if (ok) { return message } }}
模板表达式都被放在沙盒中，只能访问全局变量的一个白名单，如 Math 和 Date 。你不应该在模板表达式中试图访问用户定义的全局变量。

## 4、指令
指令 (Directives) 是带有 `v-` 前缀的特殊特性。指令特性的值预期是单个 JavaScript 表达式 (v-for 是例外情况，稍后我们再讨论)。指令的职责是，当表达式的值改变时，将其产生的连带影响，响应式地作用于 DOM。回顾我们在介绍中看到的例子：


这里，v-if 指令将根据表达式 seen 的值的真假来插入/移除 <p> 元素。

### 参数
一些指令能够接收一个“参数”，在指令名称之后以冒号表示。例如，v-bind 指令可以用于响应式地更新 HTML 特性：

```html
<a v-bind:href="url">...</a>
```



在这里 href 是参数，告知 v-bind 指令将该元素的 href 特性与表达式 url 的值绑定。

另一个例子是 v-on 指令，它用于监听 DOM 事件：

```html
<a v-on:click="doSomething">...</a>
```



在这里参数是监听的事件名。我们也会更详细地讨论事件处理。

### 动态参数
2.6.0 新增

从 2.6.0 开始，可以用方括号括起来的 JavaScript 表达式作为一个指令的参数：

```html
<a v-bind:[attributeName]="url"> ... </a>
```
这里的 attributeName 会被作为一个 JavaScript 表达式进行动态求值，求得的值将会作为最终的参数来使用。例如，如果你的 Vue 实例有一个 data 属性 attributeName，其值为 "href"，那么这个绑定将等价于 v-bind:href。

同样地，你可以使用动态参数为一个动态的事件名绑定处理函数：

	<a v-on:[eventName]="doSomething"> ... </a>
同样地，当 eventName 的值为 "focus" 时，v-on:[eventName] 将等价于 v-on:focus。

对动态参数的值的约束
动态参数预期会求出一个字符串，异常情况下值为 null。这个特殊的 null 值可以被显性地用于移除绑定。任何其它非字符串类型的值都将会触发一个警告。

对动态参数表达式的约束
动态参数表达式有一些语法约束，因为某些字符，例如空格和引号，放在 HTML 特性名里是无效的。同样，在 DOM 中使用模板时你需要回避大写键名。

例如，下面的代码是无效的：

	<!-- 这会触发一个编译警告 -->
	<a v-bind:['foo' + bar]="value"> ... </a>
变通的办法是使用没有空格或引号的表达式，或用计算属性替代这种复杂表达式。

另外，如果你在 DOM 中使用模板 (直接在一个 HTML 文件里撰写模板)，需要留意浏览器会把特性名全部强制转为小写：

	<!-- 在 DOM 中使用模板时这段代码会被转换为 `v-bind:[someattr]` -->
	<a v-bind:[someAttr]="value"> ... </a>
###修饰符
修饰符 (modifier) 是以半角句号 . 指明的特殊后缀，用于指出一个指令应该以特殊方式绑定。例如，.prevent 修饰符告诉 v-on 指令对于触发的事件调用 event.preventDefault()：

	<form v-on:submit.prevent="onSubmit">...</form>
在接下来对 v-on 和 v-for 等功能的探索中，你会看到修饰符的其它例子。

##缩写

v- 前缀作为一种视觉提示，用来识别模板中 Vue 特定的特性。当你在使用 Vue.js 为现有标签添加动态行为 (dynamic behavior) 时，v- 前缀很有帮助，然而，对于一些频繁用到的指令来说，就会感到使用繁琐。同时，在构建由 Vue 管理所有模板的单页面应用程序 (SPA - single page application) 时，v- 前缀也变得没那么重要了。因此，Vue 为 v-bind 和 v-on 这两个最常用的指令，提供了特定简写：

###v-bind 缩写
	<!-- 完整语法 -->
	<a v-bind:href="url">...</a>
	
	<!-- 缩写 -->
	<a :href="url">...</a>

###v-on 缩写
	<!-- 完整语法 -->
	<a v-on:click="doSomething">...</a>
	
	<!-- 缩写 -->
	<a @click="doSomething">...</a>

它们看起来可能与普通的 HTML 略有不同，但 : 与 @ 对于特性名来说都是合法字符，在所有支持 Vue 的浏览器都能被正确地解析。而且，它们不会出现在最终渲染的标记中。缩写语法是完全可选的，但随着你更深入地了解它们的作用，你会庆幸拥有它们。


##计算属性和侦听器
模板内的表达式非常便利，但是设计它们的初衷是用于简单运算的。在模板中放入太多的逻辑会让模板过重且难以维护。例如：
	
	<div id="example">
	  {{ message.split('').reverse().join('') }}
	</div>
在这个地方，模板不再是简单的声明式逻辑。你必须看一段时间才能意识到，这里是想要显示变量 message 的翻转字符串。当你想要在模板中多次引用此处的翻转字符串时，就会更加难以处理。

所以，对于任何复杂逻辑，你都应当使用计算属性。

基础例子

	<div id="example">
	  <p>Original message: "{{ message }}"</p>
	  <p>Computed reversed message: "{{ reversedMessage }}"</p>
	</div>
	var vm = new Vue({
	  el: '#example',
	  data: {
	    message: 'Hello'
	  },
	  computed: {
	    // 计算属性的 getter
	    reversedMessage: function () {
	      // `this` 指向 vm 实例
	      return this.message.split('').reverse().join('')
	    }
	  }
	})

结果：

	Original message: "Hello"
	
	Computed reversed message: "olleH"

这里我们声明了一个计算属性 reversedMessage。我们提供的函数将用作属性 vm.reversedMessage 的 getter 函数：

	console.log(vm.reversedMessage) // => 'olleH'
	vm.message = 'Goodbye'
	console.log(vm.reversedMessage) // => 'eybdooG'
你可以打开浏览器的控制台，自行修改例子中的 vm。vm.reversedMessage 的值始终取决于 vm.message 的值。

你可以像绑定普通属性一样在模板中绑定计算属性。Vue 知道 vm.reversedMessage 依赖于 vm.message，因此当 vm.message 发生改变时，所有依赖 vm.reversedMessage 的绑定也会更新。而且最妙的是我们已经以声明的方式创建了这种依赖关系：计算属性的 getter 函数是没有副作用 (side effect) 的，这使它更易于测试和理解。

计算属性缓存 vs 方法
你可能已经注意到我们可以通过在表达式中调用方法来达到同样的效果：

	<p>Reversed message: "{{ reversedMessage() }}"</p>
	// 在组件中
	methods: {
	  reversedMessage: function () {
	    return this.message.split('').reverse().join('')
	  }
	}
我们可以将同一函数定义为一个方法而不是一个计算属性。两种方式的最终结果确实是完全相同的。然而，不同的是计算属性是基于它们的响应式依赖进行缓存的。只在相关响应式依赖发生改变时它们才会重新求值。这就意味着只要 message 还没有发生改变，多次访问 reversedMessage 计算属性会立即返回之前的计算结果，而不必再次执行函数。

这也同样意味着下面的计算属性将不再更新，因为 Date.now() 不是响应式依赖：

	computed: {
	  now: function () {
	    return Date.now()
	  }
	}
相比之下，每当触发重新渲染时，调用方法将总会再次执行函数。

我们为什么需要缓存？假设我们有一个性能开销比较大的计算属性 A，它需要遍历一个巨大的数组并做大量的计算。然后我们可能有其他的计算属性依赖于 A 。如果没有缓存，我们将不可避免的多次执行 A 的 getter！如果你不希望有缓存，请用方法来替代。

计算属性 vs 侦听属性
Vue 提供了一种更通用的方式来观察和响应 Vue 实例上的数据变动：侦听属性。当你有一些数据需要随着其它数据变动而变动时，你很容易滥用 watch——特别是如果你之前使用过 AngularJS。然而，通常更好的做法是使用计算属性而不是命令式的 watch 回调。细想一下这个例子：

	<div id="demo">{{ fullName }}</div>
	var vm = new Vue({
	  el: '#demo',
	  data: {
	    firstName: 'Foo',
	    lastName: 'Bar',
	    fullName: 'Foo Bar'
	  },
	  watch: {
	    firstName: function (val) {
	      this.fullName = val + ' ' + this.lastName
	    },
	    lastName: function (val) {
	      this.fullName = this.firstName + ' ' + val
	    }
	  }
	})
上面代码是命令式且重复的。将它与计算属性的版本进行比较：

	var vm = new Vue({
	  el: '#demo',
	  data: {
	    firstName: 'Foo',
	    lastName: 'Bar'
	  },
	  computed: {
	    fullName: function () {
	      return this.firstName + ' ' + this.lastName
	    }
	  }
	})
好得多了，不是吗？

计算属性的 setter
计算属性默认只有 getter ，不过在需要时你也可以提供一个 setter ：

	// ...
	computed: {
	  fullName: {
	    // getter
	    get: function () {
	      return this.firstName + ' ' + this.lastName
	    },
	    // setter
	    set: function (newValue) {
	      var names = newValue.split(' ')
	      this.firstName = names[0]
	      this.lastName = names[names.length - 1]
	    }
	  }
	}
	// ...
现在再运行 vm.fullName = 'John Doe' 时，setter 会被调用，vm.firstName 和 vm.lastName 也会相应地被更新。

### 侦听器

虽然计算属性在大多数情况下更合适，但有时也需要一个自定义的侦听器。这就是为什么 Vue 通过 watch 选项提供了一个更通用的方法，来响应数据的变化。当需要在数据变化时执行异步或开销较大的操作时，这个方式是最有用的。

例如：

	<div id="watch-example">
	  <p>
	    Ask a yes/no question:
	    <input v-model="question">
	  </p>
	  <p>{{ answer }}</p>
	</div>
	<!-- 因为 AJAX 库和通用工具的生态已经相当丰富，Vue 核心代码没有重复 -->
	<!-- 提供这些功能以保持精简。这也可以让你自由选择自己更熟悉的工具。 -->
	<script src="https://cdn.jsdelivr.net/npm/axios@0.12.0/dist/axios.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/lodash@4.13.1/lodash.min.js"></script>
	<script>
	var watchExampleVM = new Vue({
	  el: '#watch-example',
	  data: {
	    question: '',
	    answer: 'I cannot give you an answer until you ask a question!'
	  },
	  watch: {
	    // 如果 `question` 发生改变，这个函数就会运行
	    question: function (newQuestion, oldQuestion) {
	      this.answer = 'Waiting for you to stop typing...'
	      this.debouncedGetAnswer()
	    }
	  },
	  created: function () {
	    // `_.debounce` 是一个通过 Lodash 限制操作频率的函数。
	    // 在这个例子中，我们希望限制访问 yesno.wtf/api 的频率
	    // AJAX 请求直到用户输入完毕才会发出。想要了解更多关于
	    // `_.debounce` 函数 (及其近亲 `_.throttle`) 的知识，
	    // 请参考：https://lodash.com/docs#debounce
	    this.debouncedGetAnswer = _.debounce(this.getAnswer, 500)
	  },
	  methods: {
	    getAnswer: function () {
	      if (this.question.indexOf('?') === -1) {
	        this.answer = 'Questions usually contain a question mark. ;-)'
	        return
	      }
	      this.answer = 'Thinking...'
	      var vm = this
	      axios.get('https://yesno.wtf/api')
	        .then(function (response) {
	          vm.answer = _.capitalize(response.data.answer)
	        })
	        .catch(function (error) {
	          vm.answer = 'Error! Could not reach the API. ' + error
	        })
	    }
	  }
	})
	</script>
结果：

	Ask a yes/no question:  
	
	I cannot give you an answer until you ask a question!

在这个示例中，使用 watch 选项允许我们执行异步操作 (访问一个 API)，限制我们执行该操作的频率，并在我们得到最终结果前，设置中间状态。这些都是计算属性无法做到的。

除了 watch 选项之外，您还可以使用命令式的 vm.$watch API。

##条件渲染

###v-if
###v-if 
指令用于条件性地渲染一块内容。这块内容只会在指令的表达式返回 truthy 值的时候被渲染。

	<h1 v-if="awesome">Vue is awesome!</h1>
也可以用 v-else 添加一个“else 块”：

	<h1 v-if="awesome">Vue is awesome!</h1>
	<h1 v-else>Oh no 😢</h1>
在 <template> 元素上使用 v-if 条件渲染分组
因为 v-if 是一个指令，所以必须将它添加到一个元素上。但是如果想切换多个元素呢？此时可以把一个 <template> 元素当做不可见的包裹元素，并在上面使用 v-if。最终的渲染结果将不包含 <template> 元素。

	<template v-if="ok">
	  <h1>Title</h1>
	  <p>Paragraph 1</p>
	  <p>Paragraph 2</p>
	</template>
### v-else
你可以使用 v-else 指令来表示 v-if 的“else 块”：

	<div v-if="Math.random() > 0.5">
	  Now you see me
	</div>
	<div v-else>
	  Now you don't
	</div>
v-else 元素必须紧跟在带 v-if 或者 v-else-if 的元素的后面，否则它将不会被识别。

### v-else-if
2.1.0 新增

v-else-if，顾名思义，充当 v-if 的“else-if 块”，可以连续使用：

	<div v-if="type === 'A'">
	  A
	</div>
	<div v-else-if="type === 'B'">
	  B
	</div>
	<div v-else-if="type === 'C'">
	  C
	</div>
	<div v-else>
	  Not A/B/C
	</div>
类似于 v-else，v-else-if 也必须紧跟在带 v-if 或者 v-else-if 的元素之后。

用 key 管理可复用的元素
Vue 会尽可能高效地渲染元素，通常会复用已有元素而不是从头开始渲染。这么做除了使 Vue 变得非常快之外，还有其它一些好处。例如，如果你允许用户在不同的登录方式之间切换：

	<template v-if="loginType === 'username'">
	  <label>Username</label>
	  <input placeholder="Enter your username">
	</template>
	<template v-else>
	  <label>Email</label>
	  <input placeholder="Enter your email address">
	</template>
那么在上面的代码中切换 loginType 将不会清除用户已经输入的内容。因为两个模板使用了相同的元素，`<input>` 不会被替换掉——仅仅是替换了它的 placeholder。

自己动手试一试，在输入框中输入一些文本，然后按下切换按钮：

这样也不总是符合实际需求，所以 Vue 为你提供了一种方式来表达“这两个元素是完全独立的，不要复用它们”。只需添加一个具有唯一值的 key 属性即可：

	<template v-if="loginType === 'username'">
	  <label>Username</label>
	  <input placeholder="Enter your username" key="username-input">
	</template>
	<template v-else>
	  <label>Email</label>
	  <input placeholder="Enter your email address" key="email-input">
	</template>
现在，每次切换时，输入框都将被重新渲染。请看：

注意，<label> 元素仍然会被高效地复用，因为它们没有添加 key 属性。

##v-show
另一个用于根据条件展示元素的选项是 v-show 指令。用法大致一样：

	<h1 v-show="ok">Hello!</h1>
不同的是带有 v-show 的元素始终会被渲染并保留在 DOM 中。v-show 只是简单地切换元素的 CSS 属性 display。

注意，v-show 不支持 <template> 元素，也不支持 v-else。

###v-if vs v-show
v-if 是“真正”的条件渲染，因为它会确保在切换过程中条件块内的事件监听器和子组件适当地被销毁和重建。

v-if 也是惰性的：如果在初始渲染时条件为假，则什么也不做——直到条件第一次变为真时，才会开始渲染条件块。

相比之下，v-show 就简单得多——不管初始条件是什么，元素总是会被渲染，并且只是简单地基于 CSS 进行切换。

一般来说，v-if 有更高的切换开销，而 v-show 有更高的初始渲染开销。因此，如果需要非常频繁地切换，则使用 v-show 较好；如果在运行时条件很少改变，则使用 v-if 较好。

###v-if 与 v-for 一起使用
不推荐同时使用 v-if 和 v-for。请查阅风格指南以获取更多信息。

当 v-if 与 v-for 一起使用时，v-for 具有比 v-if 更高的优先级。请查阅列表渲染指南 以获取详细信息。

## 5、VUE组件化编码

### 5.1 使用 vue-cli 创建模板项目

> 1) vue-cli 是 vue 官方提供的脚手架工具
> 2) github: https://github.com/vuejs/vue-cli
> 3) 作用: 从 https://github.com/vuejs-templates 下载模板项目

### 5.1.1 创建 vue 项目

npm install -g vue-cli
vue init webpack vue_demo
cd vue_demo
npm install
npm run dev
访问: http://localhost:8080/

> 建议使用webstorm或者IDEA自动化构建

### 5.1.2 模板项目的结构说明

|-- build : webpack 相关的配置文件夹(基本不需要修改)
|-- dev-server.js : 通过 express 启动后台服务器
|-- config: webpack 相关的配置文件夹(基本不需要修改)
|-- index.js: 指定的后台服务的端口号和静态资源文件夹
|-- node_modules
|-- src : 源码文件夹
|-- components: vue 组件及其相关资源文件夹
|-- App.vue: 应用根主组件
|-- main.js: 应用入口 js
|-- static: 静态资源文件夹
|-- .babelrc: babel 的配置文件
|-- .eslintignore: eslint 检查忽略的配置
|-- .eslintrc.js: eslint 检查的配置
|-- .gitignore: git 版本管制忽略的配置
|-- index.html: 主页面文件
|-- package.json: 应用包配置文件
|-- README.md: 应用描述说明的 readme 文件



### 5.2 eslint

#### 5.2.1 说明

1)  ESLint 是一个代码规范检查工具

2)  它定义了很多特定的规则, 一旦你的代码违背了某一规则, eslint会作出非常有用的提示

3) 官网: http://eslint.org/4) 基本已替代以前的 JSLint

#### 5.2.2  ESLint 提供以下支持

1) ES

2) JSX

3) style 检查

4)  自定义错误和提示

#### 5.2.3. ESLint 提供以下几种校验

1)  语法错误校验

2) 不重要或丢失的标点符号，如分号

3)  没法运行到的代码块（使用过 WebStorm 的童鞋应该了解）

4) 未被使用的参数提醒

5) 确保样式的统一规则，如 sass 或者 less

6) 检查变量的命名

#### 5.2.4 规则的错误等级有三种

1)  0：关闭规则。2)  1：打开规则，并且作为一个警告（信息打印黄色字体）

3)  2：打开规则，并且作为一个错误（信息打印红色字体）



### 5.3组件的使用和定义

#### 5.3.1 vue架子

```vue
<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <h2>Essential Links</h2>
    <h2>vue</h2>
  </div>
</template>

<script>
export default {
  name: 'HelloWorld',
  data () {
    return {
      msg: '欢迎使用vue'
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>

```



#### 5.3.2组件的使用

* 引入组件
* 映射成标签
* 使用组件标签

```vue
<template>
  <div>
    <header class="site-header jumbotron">
      <div class="container">
        <div class="row">
          <div class="col-xs-12">
            <h1>请发表对Vue的评论</h1>
          </div>
        </div>
      </div>
    </header>
    <div class="container">
      <Add :addComment="addComment"/>
      <List :comments="comments" :deleteComment="deleteComment" />
    </div>
  </div>
</template>

<script>
  import  Add from './components/Add'
  import  List from './components/List'
export default {

    data() {
      return {
        comments:[{
          name : 'hello1',
          content : 'hello world'
        },{
          name : 'hello2',
          content : 'hello world'
        },{
          name : 'hello3',
          content : 'hello world'
        }
        ]
      }
    },
  methods:{
      addComment(comment){
        this.comments.unshift(comment)
      },
    deleteComment(index){
        this.comments.splice(index,1)
    }
  },

  components:{
    Add,
    List
  }
}
</script>

<style>

</style>

```



> 关于标签名与标签属性名书写问题
> 1) 写法一: 一模一样
> 2) 写法二: 大写变小写, 并用-连接



### 5.4 组件的通讯



props类型

到这里，我们只看到了以字符串数组形式列出的 prop：

```java
props: ['title', 'likes', 'isPublished', 'commentIds', 'author']
```

但是，通常你希望每个 prop 都有指定的值类型。这时，你可以以对象形式列出 prop，这些属性的名称和值分别是 prop 各自的名称和类型：

```java
props: {
  title: String,
  likes: Number,
  isPublished: Boolean,
  commentIds: Array,
  author: Object,
  callback: Function,
  contactsPromise: Promise // or any other constructor
}
```

这不仅为你的组件提供了文档，还会在它们遇到错误的类型时从浏览器的 JavaScript 控制台提示用户。你会在这个页面接下来的部分看到[类型检查和其它 prop 验证](https://cn.vuejs.org/v2/guide/components-props.html#Prop-验证)。



