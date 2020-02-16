# 从 Vue Router 0.7.x 迁移

[TPshop 中国免费商城系统 - 搜豹商城系统 - 免费50小时Vue视频教程 立即查看 >](http://www.tp-shop.cn/index.php?http_referer=vuejs)

> 只有 Vue Router 2 是与 Vue 2 相互兼容的，所以如果你更新了 Vue ，你也需要更新 Vue Router 。这也是我们在主文档中将迁移路径的详情添加进来的原因。
> 有关使用 Vue Router 2 的完整教程，请参阅 [Vue Router 文档](https://router.vuejs.org/zh-cn/)。

## [Router 初始化](https://cn.vuejs.org/v2/guide/migration-vue-router.html#Router-初始化)

### [`router.start` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-start-替换)

不再会有一个特殊的 API 用来初始化包含 Vue Router 的 app ，这意味着不再是：

```
router.start({
  template: '<router-view></router-view>'
}, '#app')
```

你只需要传一个路由属性给 Vue 实例：

```
new Vue({
  el: '#app',
  router: router,
  template: '<router-view></router-view>'
})
```

或者，如果你使用的是运行时构建 (runtime-only) 方式：

```
new Vue({
  el: '#app',
  router: router,
  render: h => h('router-view')
})
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `router.start` 被调用的示例。

## [Route 定义](https://cn.vuejs.org/v2/guide/migration-vue-router.html#Route-定义)

### [`router.map` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-map-替换)

路由现在被定义为一个在 router 实例里的一个 [`routes` 选项](https://router.vuejs.org/zh-cn/essentials/getting-started.html#javascript)数组。所以这些路由：

```
router.map({
  '/foo': {
    component: Foo
  },
  '/bar': {
    component: Bar
  }
})
```

会以这种方式定义：

```
var router = new VueRouter({
  routes: [
    { path: '/foo', component: Foo },
    { path: '/bar', component: Bar }
  ]
})
```

考虑到不同浏览器中遍历对象不能保证会使用相同的 property 顺序，这种数组的语法可以保证更多可预测的路由匹配。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `router.map` 被调用的示例。

### [`router.on` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-on-移除)

如果你需要在启动的 app 时通过代码生成路由，你可以动态地向路由数组推送定义来完成这个操作。举个例子：

```
// 普通的路由
var routes = [
  // ...
]

// 动态生成的路由
marketingPages.forEach(function (page) {
  routes.push({
    path: '/marketing/' + page.slug
    component: {
      extends: MarketingComponent
      data: function () {
        return { page: page }
      }
    }
  })
})

var router = new Router({
  routes: routes
})
```

如果你需要在 router 被实例化后增加新的路由，你可以把 router 原来的匹配方式换成一个包括你新添的加路由的匹配方式：

```
router.match = createMatcher(
  [{
    path: '/my/new/path',
    component: MyComponent
  }].concat(router.options.routes)
)
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `router.on` 被调用的示例。

### [`router.beforeEach` changed](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-beforeEach-changed)

`router.beforeEach` 现在是异步工作的，并且携带一个 `next` 函数作为其第三个参数。

```
router.beforeEach(function (transition) {
  if (transition.to.path === '/forbidden') {
    transition.abort()
  } else {
    transition.next()
  }
})
router.beforeEach(function (to, from, next) {
  if (to.path === '/forbidden') {
    next(false)
  } else {
    next()
  }
})
```

### [`subRoutes` 换名](https://cn.vuejs.org/v2/guide/migration-vue-router.html#subRoutes-换名)

出于 Vue Router 和其他路由库一致性的考虑，重命名为[`children`](https://router.vuejs.org/zh-cn/essentials/nested-routes.html)

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `subRoutes` 选项的示例。

### [`router.redirect` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-redirect-替换)

现在用一个[路由定义的选项](https://router.vuejs.org/zh-cn/essentials/redirect-and-alias.html)作为代替。举个例子，你将会更新：

```
router.redirect({
  '/tos': '/terms-of-service'
})
```

成像下面的`routes`配置里定义的样子：

```
{
  path: '/tos',
  redirect: '/terms-of-service'
}
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `router.redirect` 被调用的示例。

### [`router.alias` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-alias-替换)

现在是你进行 alias 操作的[路由定义里的一个选项](https://router.vuejs.org/zh-cn/essentials/redirect-and-alias.html)。举个例子，你需要在你的`routes`定义里将：

```
router.alias({
  '/manage': '/admin'
})
```

配置这个样子：

```
{
  path: '/admin',
  component: AdminPanel,
  alias: '/manage'
}
```

如果你需要进行多次 alias 操作，你也可以使用一个数组语法去实现：

```
alias: ['/manage', '/administer', '/administrate']
```

#### 升级路径

运行[迁移助手](https://github.com/vuejs/vue-migration-helper)找到 `router.alias` 被调用的示例。

### [任意的 Route 属性 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#任意的-Route-属性-替换)

现在任意的 route 属性必须在新 meta 属性的作用域内，以避免和以后的新特性发生冲突。举个例子，如果你以前这样定义：

```
'/admin': {
  component: AdminPanel,
  requiresAuth: true
}
```

你现在需要把它更新成：

```
{
  path: '/admin',
  component: AdminPanel,
  meta: {
    requiresAuth: true
  }
}
```

如果在一个路由上访问一个属性，你仍然会通过 meta 。举个例子：

```
if (route.meta.requiresAuth) {
  // ...
}
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到任意的路由不在 meta 作用域下的示例。

### [URL 中的 Query 数组 [\] 语法 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#URL-中的-Query-数组-语法-移除)

当传递数组给 query 参数时，URL 语法不再是 `/foo?users[]=Tom&users[]=Jerry`，取而代之，新语法是 `/foo?users=Tom&users=Jerry`，此时 `$route.query.users` 将仍旧是一个数组，不过如果在该 query 中只有一个参数：`/foo?users=Tom`，当直接访问该路由时，vue-router 将无法知道我们期待的 `users` 是个数组。因此，可以考虑添加一个计算属性并且在每个使用 `$route.query.users` 的地方以该计算属性代替：

```
export default {
  // ...
  computed: {
    // 此计算属性将始终是个数组
    users () {
      const users = this.$route.query.users
      return Array.isArray(users) ? users : [users]
    }
  }
}
```

## [Route 匹配](https://cn.vuejs.org/v2/guide/migration-vue-router.html#Route-匹配)

路由匹配现在使用 [path-to-regexp](https://github.com/pillarjs/path-to-regexp) 这个包，这将会使得工作与之前相比更加灵活。

### [一个或者更多的命名参数 改变](https://cn.vuejs.org/v2/guide/migration-vue-router.html#一个或者更多的命名参数-改变)

语法稍微有些许改变，所以以`/category/*tags`为例，应该被更新为`/category/:tags+`。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到弃用路由语法的示例。

## [链接](https://cn.vuejs.org/v2/guide/migration-vue-router.html#链接)

### [`v-link` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#v-link-替换)

`v-link` 指令已经被一个新的 [`` 组件](https://router.vuejs.org/zh-cn/api/router-link.html)指令替代，这一部分的工作已经被 Vue 2 中的组件完成。这将意味着在任何情况下，如果你拥有这样一个链接：

```
<a v-link="'/about'">About</a>
```

你需要把它更新成：

```
<router-link to="/about">About</router-link>
```

注意：`<router-link>`不支持`target="_blank"`属性，如果你想打开一个新标签页，你必须用`<a>`标签。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `v-link` 指令的示例。

### [`v-link-active` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#v-link-active-替换)

`v-link-active` 也因为指定了一个在 [`` 组件](https://router.vuejs.org/zh-cn/api/router-link.html)上的 tag 属性而被弃用了。举个例子，你需要更新：

```
<li v-link-active>
  <a v-link="'/about'">About</a>
</li>
```

成这个写法：

```
<router-link tag="li" to="/about">
  <a>About</a>
</router-link>
```

<a>标签将会成为真实的链接 (并且可以获取到正确的跳转)，但是激活的类将会被应用在外部的<li>标签上。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `v-link-active` 指令的示例

## [编程导航](https://cn.vuejs.org/v2/guide/migration-vue-router.html#编程导航)

### [`router.go` 改变](https://cn.vuejs.org/v2/guide/migration-vue-router.html#router-go-改变)

为了与 [HTML5 History API](https://developer.mozilla.org/en-US/docs/Web/API/History_API) 保持一致性，`router.go` 已经被用来作为 [后退/前进导航](https://router.vuejs.org/zh-cn/essentials/navigation.html#routergon)，[`router.push` ](https://router.vuejs.org/zh-cn/essentials/navigation.html#routerpushlocation)用来导向特殊页面。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) ，找到 `router.go` 和 `router.push` 指令被调用的示例。

## [路由选择：Modes](https://cn.vuejs.org/v2/guide/migration-vue-router.html#路由选择：Modes)

### [`hashbang: false` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#hashbang-false-移除)

Hashbangs 将不再为谷歌需要去爬去一个网址，所以他们将不再成为哈希策略的默认选项。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `hashbang: false` 选项的示。

### [`history: true` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#history-true-替换)

所有路由模型选项将被简化成一个单个的[`mode` 选项](https://router.vuejs.org/zh-cn/api/options.html#mode)。你需要更新：

```
var router = new VueRouter({
  history: 'true'
})
```

成这个写法：

```
var router = new VueRouter({
  mode: 'history'
})
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `history: true` 选项的示。

### [`abstract: true` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#abstract-true-替换)

所有路由模型选项将被简化成一个单个的[`mode` 选项](https://router.vuejs.org/zh-cn/api/options.html#mode)。你需要更新：

```
var router = new VueRouter({
  abstract: 'true'
})
```

成这个写法：

```
var router = new VueRouter({
  mode: 'abstract'
})
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `abstract: true` 选项的示例。

## [路由选项：Misc](https://cn.vuejs.org/v2/guide/migration-vue-router.html#路由选项：Misc)

### [`saveScrollPosition` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#saveScrollPosition-替换)

它已经被替换为可以接受一个函数的 [`scrollBehavior` 选项](https://router.vuejs.org/zh-cn/advanced/scroll-behavior.html)，所以滑动行为可以完全的被定制化处理 - 甚至为每次路由进行定制也可以满足。这将会开启很多新的可能，但是简单的复制旧的行为：

```
saveScrollPosition: true
```

你可以替换为：

```
scrollBehavior: function (to, from, savedPosition) {
  return savedPosition || { x: 0, y: 0 }
}
```

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `saveScrollPosition: true` 选项的示例。

### [`root` 换名](https://cn.vuejs.org/v2/guide/migration-vue-router.html#root-换名)

为了与 [HTML 的`` 标签](https://developer.mozilla.org/en-US/docs/Web/HTML/Element/base)保持一致性，重命名为 `base`。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `root` 选项的示例。

### [`transitionOnLoad` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#transitionOnLoad-移除)

由于 Vue 的过渡系统 [`appear` transition control](https://cn.vuejs.org/v2/guide/transitions.html#初始渲染的过渡) 的存在，这个选项将不再需要。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `transitionOnLoad: true` 选项的示例。

### [`suppressTransitionError` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#suppressTransitionError-移除)

出于简化钩子的考虑被移除。如果你真的需要抑制过渡错误，你可以使用 [`try`…`catch`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/try...catch) 作为替代。

#### 升级路径

运行 [迁移指令](https://github.com/vuejs/vue-migration-helper) 找到 `suppressTransitionError: true` 选项的示例。

## [路由挂钩](https://cn.vuejs.org/v2/guide/migration-vue-router.html#路由挂钩)

### [`activate` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#activate-替换)

使用 [`beforeRouteEnter`](https://router.vuejs.org/zh-cn/advanced/navigation-guards.html#组件内的钩子) 这一组件进行替代。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `activate` 钩子的示例。

### [`canActivate` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#canActivate-替换)

使用[`beforeEnter`](https://router.vuejs.org/en/advanced/navigation-guards.html#perroute-guard) 在路由中作为替代。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `canActivate` 钩子的示例。

### [`deactivate` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#deactivate-移除)

使用[`beforeDestroy`](https://cn.vuejs.org/v2/api/#beforeDestroy) 或者 [`destroyed`](https://cn.vuejs.org/v2/api/#destroyed) 钩子作为替代。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `deactivate` 钩子的示例。

### [`canDeactivate` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#canDeactivate-替换)

在组件中使用[`beforeRouteLeave`](https://router.vuejs.org/zh-cn/advanced/navigation-guards.html#组件内的钩子) 作为替代。

#### 升级路径

运行 [迁移路径](https://github.com/vuejs/vue-migration-helper) 找到 `canDeactivate` 钩子的示例。

### [`canReuse: false` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#canReuse-false-移除)

在新的 Vue 路由中将不再被使用。

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `canReuse: false` 选项的示例。

### [`data` 替换](https://cn.vuejs.org/v2/guide/migration-vue-router.html#data-替换)

`$route`属性是响应式的，所以你可以就使用一个 watcher 去响应路由的改变，就像这样：

```
watch: {
  '$route': 'fetchData'
},
methods: {
  fetchData: function () {
    // ...
  }
}
```

#### 升级路径

运行 [迁移助手](https://github.com/vuejs/vue-migration-helper) 找到 `data` 钩子的示例。

### [`$loadingRouteData` 移除](https://cn.vuejs.org/v2/guide/migration-vue-router.html#loadingRouteData-移除)

定义你自己的属性 (例如：`isLoading`)，然后在路由上的 watcher 中更新加载状态。举个例子，如果使用 [axios](https://github.com/mzabriskie/axios) 获取数据：

```
data: function () {
  return {
    posts: [],
    isLoading: false,
    fetchError: null
  }
},
watch: {
  '$route': function () {
    var self = this
    self.isLoading = true
    self.fetchData().then(function () {
      self.isLoading = false
    })
  }
},
methods: {
  fetchData: function () {
    var self = this
    return axios.get('/api/posts')
      .then(function (response) {
        self.posts = response.data.posts
      })
      .catch(function (error) {
        self.fetchError = error
      })
  }
}
```

← [从 Vue 1.x 迁移](https://cn.vuejs.org/v2/guide/migration.html)[从 Vuex 0.6.x 迁移到 1.0](https://cn.vuejs.org/v2/guide/migration-vuex.html) →



发现错误？想参与编辑？ 