# vue-cli安装sass

npm 依次执行

```cpp
npm install --save-dev css-loader
npm install --save-dev sass-loader
npm install --save-dev style-loader
set SASS_BINARY_SITE=https://npm.taobao.org/mirrors/node-sass/
npm install node-sass
```

在webpack.base.conf.js的rules中添加

```bash
rules:[
    ....
    {
        test: /\.sass$/,
        loaders: ['style', 'css', 'sass']
    }
]
```

然后在
 要用到scss的组件里面的style标签上加上 lang='scss'



```xml
<style lang="scss" scoped>
        ......
</style>
```