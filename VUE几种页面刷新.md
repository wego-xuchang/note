# VUE几种页面刷新

1、this.$router.go(0)

这种方法页面会一瞬间的白屏，体验不是很好，虽然只是一行代码的事

2、location.reload()

这种也是一样，画面一闪，效果总不是很好

3、跳转空白页再跳回原页面

在需要页面刷新的地方写上：this.$router.push('/emptyPage')，跳转到一个空白页。在emptyPage.vue里beforeRouteEnter 钩子里控制页面跳转，从而达到刷新的效果

beforeRouteEnter (to, from, next) {
      next(vm => {
        vm.$router.replace(from.path)
      })
}。

这种画面虽不会一闪，但是能看见路由快速变化。

4、控制<router-view>的显示隐藏

默认<router-view v-if="isRouterAlive" />isRouterAlive肯定是true，在需要刷新的时候把这个值设为false，接着再重新设为true：

this.isRouterAlive = false
this.$nextTick(function () {
  this.isRouterAlive = true
})
这种方法从画面上是看不出破绽的。也可以搭配provide、inject使用。例如：

```vue
<template>
  <div id="app">
    <router-view v-if="isRouterAlive"></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
  provide () {
    return {
      reload: this.reload
    }
  },
  data () {
    return {
      isRouterAlive: true
    }
  },
  methods: {
    reload () {
      this.isRouterAlive = false
      this.$nextTick(function () {
        this.isRouterAlive = true
      })
    }
  }
}
</script>

<style>
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  /*width: 100%;*/
  /*height: 100%;*/
  color: #2c3e50;
}
</style>

```





然后在需要刷新的页面引入依赖：inject: ['reload'],

在需要执行的地方直接调用方法即可：this.reload()。

```vue
inject:['reload']

this.reload();
```



我的业务需求是在home里，页面右上角，在我的里面点击修改个人资料成功后，页面1就要刷新一下，如果是在app里面搭配provide、inject这样用的话，会出现一个问题，就是所有的弹窗以及menu部分就都不见了。还没有找到为什么，，

后来发现，我的需求不通过刷新页面也可以解决，就是利用VUE组件通信，监听事件发生，然后重新调一下获取数据的接口就行。

也就是说：

1、给Vue的原型上添加一个bus属性

main.js：Vue.prototype.$bus = new Vue()

2、home页面进行修改个人资料操作时触发事件，

home.vue： changeProfile （） {this.$bus.$emit('change')}

3、页面1里监听如果执行了操作，就调取页面1需要重新加载的数据接口。

mounted () {
  this.$bus.$on('change', ()=> {
    this.doSomething()
  })
},
对于我的需求来说，页面刷新的第四种方法和利用组件通信都能解决我的问题，前者更简单后者更专业，也更强大。
