# vue 中引入bootstrap

##### 1.安装jquery和bootstrap



```css
npm install jquery@1.12.4        //jquery建议安装1.X.X的版本3.4.1
npm install bootstrap@3.3.7
```

##### 2.配置jquery

在bulid/webpack.base.conf.js中进行配置,



```jsx
var webpack = require('webpack')

plugins: [
    new webpack.ProvidePlugin({
      $: "jquery",
      jQuery: "jquery",
    })
  ]
```





 完整代码如下图



![img](https:////upload-images.jianshu.io/upload_images/15410740-daf9556d56a85965.png?imageMogr2/auto-orient/strip|imageView2/2/w/676/format/webp)

##### 3.引入bootstrap

 在main.js文件中引入css、js文件，这里就不需要再引入jquery了

![img](https:////upload-images.jianshu.io/upload_images/15410740-f6411a362322f766.png?imageMogr2/auto-orient/strip|imageView2/2/w/424/format/webp)