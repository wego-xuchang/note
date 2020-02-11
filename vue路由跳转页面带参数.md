# vue路由跳转页面带参数

### 1.router-link跳转

```js

<template>
<div>
  <router-link 
    :to="{
          path: '你要跳转的路由', 
          params: { 
              name: 'name',  // params为传送的参数，name为router.js里为页面配置的name
              data: data
          },
          query: {
              name: 'name', //query和params也是传送的参数，区别在于query会在路径上显示参数
              data: data
          }
        }">
   </router-link> 
  </div>
</template>
```

### 2.router.push

在vue中使用 this.$router.push（{ path:  '/home' }） 默认是替代本窗口

带参数跳转（query）会在网址后面以?参数="***"的形式

 不带参数跳转（params） url里隐藏

传参：

```js
this.$router.push(
    {
    path: '/detail', 
    query:{shopid: item.id}
    }
);

this.$router.push(
    {
        name:'detail',
        params:{name:"你好世界"}
    }
)
```

获取：

```js
this.$route.query.shopid

this.$route.params.name
```

### 3.router.resolve

以新建页面的方式打开

以新页面跳转并携带参数（query） 会在网址后面以?参数="***"的形式

```js
let routeData = this.$router.resolve({ 
    name: 'area', 
    query: {  areaName: params.data.name} 
});
window.open(routeData.href, '_blank');
```

