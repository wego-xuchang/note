# CSS实现自适应高度布局

头部底部固定，中间自适应铺满屏幕剩余高度，中间盒子里左盒子固定右盒子自适应宽度



```htm
<!DOCTYPE html>
<html>
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <style>
        *{
            margin:0;
            padding:0;
        }
        .top {
            width: 100%;
            height: 40px;
            background: #000;
            color:#fff;
            position:absolute;
            top:0;
            /*以上设置是重点必须的*/
            text-align:center;
            line-height:40px;
        }
        .bottom{
            width:100%;
            height:40px;
            background:#000;
            color:#fff;
            position:absolute;
            bottom:0;
            /*以上设置是重点必须的*/
            text-align:center;
            line-height:40px;
        }
        .mainBox{
            width:100%;
            position:absolute;
            top:40px;
            bottom:40px;
            /*以上设置是重点必须的*/
        }
        .mainBox .leftBox{
            height:100%;
            width:200px;
            float:left;
            margin-bottom:40px;
            overflow: auto;
            /*以上设置是重点必须的*/
            border:6px solid green;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            text-align:center;
            line-height:40px;
        }
        .mainBox .rightBox{
            height:100%;
            margin-left:220px;
            /*以上设置是重点必须的*/
            border:6px solid crimson;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;
            overflow: auto;
            text-align:center;
            line-height:40px;
        }
    </style>
</head>
<body>
<div class="top">顶部，高度40px</div>
<div class="mainBox">
    <div class="leftBox">左盒子，固定宽度200px，高度自适应铺满屏幕剩余高度</div>
    <div class="rightBox">右盒子，距离左盒子20px，高度自适应宽度自适应铺满屏幕剩余高度</div>
</div>
<div class="bottom">底部，高度40px</div>
</body>
</html>
```



案列：

```html
<template>
  <div >
    <div class="top">顶部，高度40px</div>
    <div class="mainBox">
      <div class="leftBox">左盒子，固定宽度200px，高度自适应铺满屏幕剩余高度</div>
      <div class="rightBox">右盒子，距离左盒子20px，高度自适应宽度自适应铺满屏幕剩余高度			</div>
    </div>

  </div>
</template>

<script>
  import adminHeader from './components/adminHeader'
  import adminAside from './components/adminAside'
export default {
  data () {
    const item = {
      date: '2016-05-02',
      name: '王小虎',
      address: '上海市普陀区金沙江路 1518 弄'
    };
    return {
      tableData: Array(20).fill(item)
    }
  },
  created () {
  },
  components:{
    adminHeader,
    adminAside
  },
  methods:{
  }
};
</script>
<style  lang="scss" scoped>
  body,html{
    margin:0;
    padding:0;
  }
  .top {
    width: 100%;
    height: 65px;
    background: #000;
    color:#fff;
    position:absolute;
    top:0;
    /*以上设置是重点必须的*/
    text-align:center;
    line-height:65px;
  }
  .mainBox{
    width:100%;
    position:absolute;
    top:65px;
    bottom:0px;
    /*以上设置是重点必须的*/
  }
  .mainBox .leftBox{
    height:100%;
    width:220px;
    float:left;
    /*margin-bottom:40px;*/
    overflow: auto;
    /*以上设置是重点必须的*/
    /*border:6px solid green;*/
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    text-align:center;
    line-height:65px;
    background-color: #00a4ff;
  }
  .mainBox .rightBox{
    height:100%;
    margin-left:220px;
    /*以上设置是重点必须的*/
    /*border:6px solid crimson;*/
    -webkit-box-sizing: border-box;
    -moz-box-sizing: border-box;
    box-sizing: border-box;
    overflow: auto;
    text-align:center;
    line-height:65px;
    background-color: #00a854;
  }
</style>

```

![1581863142186](C:\Users\xuchang\AppData\Roaming\Typora\typora-user-images\1581863142186.png)

