# Vue中引入Video.js视频播放器

#### 安装



```ruby
 $ npm install video.js
```

#### main.js中引入



```jsx
import Video from 'video.js'
import 'video.js/dist/video-js.css'

Vue.prototype.$video = Video
```

#### 使用（代码中有注释说明）



```xml
    <template>
    <div class="test_two_box">
        <video
        id="myVideo"
        class="video-js"
        >
        <source
            src="//vjs.zencdn.net/v/oceans.mp4"
            type="video/mp4"
        >
        </video>
    </div>
    </template>

    <script>
    /* eslint-disable */
    export default {
    name: "TestTwo",
    data() {
        return {};
    },
    mounted() { 
        this.initVideo();
    },
    methods: {
        initVideo() {
        //初始化视频方法
        let myPlayer = this.$video(myVideo, {
            //确定播放器是否具有用户可以与之交互的控件。没有控件，启动视频播放的唯一方法是使用autoplay属性或通过Player API。
            controls: true,
            //自动播放属性,muted:静音播放
            autoplay: "muted",
            //建议浏览器是否应在<video>加载元素后立即开始下载视频数据。
            preload: "auto",
            //设置视频播放器的显示宽度（以像素为单位）
            width: "800px",
            //设置视频播放器的显示高度（以像素为单位）
            height: "400px"
        });
        }
    }
    };
    </script>

    <style scoped>
    </style>
```

### 实现效果截图



![img](https:////upload-images.jianshu.io/upload_images/4242443-4014e351011fe22e.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

进入这个组件时，自动默认播放视频

附上Video.js官网地址:[Video.js](https://videojs.com/)

vue中使用video

```vue
<template>
  <div class="index_main">
    <navbar></navbar>
    <br>
    <div class="video-play">
      <video ref="viodeRef" id="myVideo"
             class="video-js vjs-default-skin vjs-big-play-centered"
             controls
             preload="auto"
             width="1080px"
             height="600px"
             poster="http://static.qiakr.com/app/full_res.jpg">
        <source src="http://134.175.30.90/group1/M00/00/00/rBAADV4X6EmABK5eAeTlS8Utq0M81..mp4" type="video/mp4" >
        <p class="vjs-no-js">不支持播放</p>
      </video>
    </div>
    <div class="video-content">

    </div>
  <footerbar></footerbar>
  </div>
</template>

<script>
  import videojs from  'video.js/dist/video.min'
  import 'video.js/dist/video-js.min.css'
  import 'videojs-flash/dist/videojs-flash';
  import navbar from '../../components/navbar'
  import footerbar from '../../components/footerbar'

  export default {
    name: 'Videojs',
    data:function () {
      return {
        playOptions:{
          autoplay: false, // 自动播放
          controls: true, // 是否显示控制栏
          notSupportedMessage: '此视频暂无法播放，检查相机状态是否正常或请查看是否安装flash',//无法播放时显示的信息
          techOrder: ['flash', 'html5'],// 兼容顺序
          sourceOrder: true,
          flash: {
            swf: '../../../static/VideoJS.swf'   //如果是本地视频  需要相应的videoJS.swf文件
          },
          source:[{
            type: 'rtmp/flv',
            src:'rtmp://184.72.239.149/vod/&mp4:BigBuckBunny_115k.mov'
          }],
          poster:'', //播放器 默认图片
          controlBar: {                     // 配置控制栏
            timeDivider: false, // 时间分割线
            durationDisplay: false, // 总时间
            progressControl: true, // 进度条
            customControlSpacer: true, // 未知
            fullscreenToggle: true // 全屏
          },

        },
        myPlayer:''
      }
    },

    mounted:function () {

      const _this = this;
      _this.myPlayer = videojs('myVideo',_this.playOptions,function () {
        //--------methods
        this.load();
        this.play();
        //this.pause();//暂停

        //------events    绑定事件用on    移除事件用off
        this.on('loadstart',function () {
          console.log('loadstart------------')
        });
        this.on('loadedmetadata',function () {
          console.log('loadedmetadata---视频源数据加载完成----')
        });
        this.on('loadeddata',function () {
          console.log('loadeddata---渲染播放画面----');//autoPlay必须为false
        });
        this.on('progress',function () {
          console.log('progress-------加载过程----')
        });
        this.on('timeupdate',function () {
          const curTime = this.currentTime();
          console.log('timeupdate-------------',curTime);
        })
        this.off('timeupdate',function () {
          console.log('off----------')
        })
        this.on('ended',function() {
          console.log('播放结束了')
        })
      });

    },
    methods:{

    },
    components:{
      navbar,
      footerbar
    },
    beforeDestroy:function(){
      const videoDom = this.$refs.viodeRef;  //不能用document 获取节点
      videojs(videoDom).dispose();  //销毁video实例，避免出现节点不存在 但是flash一直在执行，报 this.el.......is not function
    }
  }
</script>

<style scope>
  /*页面布局*/
  .index_main{
    width:78%;
    height:100%;
    background:#ffffff;
    margin:0 auto;
  }
  .video-play{
    width: 100%;
    height: 100%;
    margin: 0;
    padding: 0;
  }
  #myVideo{
    margin: 0 auto;
  }
</style>

```





