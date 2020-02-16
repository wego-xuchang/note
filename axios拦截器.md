# 一：什么是axios拦截器、为什么要使用axios拦截器？

在vue项目中，我们通常使用axios与后台进行数据交互，axios是一款基于promise封装的库，可以运行在浏览器端和node环境中。它有很多优秀的特性，例如拦截请求和响应、取消请求、转换json、客户端防御XSRF等。所以vue官方开发组放弃了对其官方库[vue-resource](https://www.npmjs.com/package/vue-resource)的维护，直接推荐我们使用axios库。axios官方文档请飞[axios中文文档](http://www.axios-js.com/)
 页面发送http请求，很多情况我们要对请求和其响应进行特定的处理；例如每个请求都附带后端返回的token，拿到response之前loading动画的展示等。如果请求数非常多，这样处理起来会非常的麻烦，程序的优雅性也会大打折扣。在这种情况下，axios为开发者提供了这样一个API：拦截器。拦截器分为 请求（request）拦截器和 响应（response）拦截器。

# 二：为你的axios配置拦截器

##### 1.axios的基础配置

项目目录如下图所示：





![img](https:////upload-images.jianshu.io/upload_images/6667949-1c33b38b2f60f8bf.png?imageMogr2/auto-orient/strip|imageView2/2/w/779/format/webp)

项目目录.png



其中，api一般存放的为页面的请求，这些请求都需要统一经过请求拦截器的处理，这部分不是重点，随便拿出一个文件来进行展示，一看就能懂





![img](https:////upload-images.jianshu.io/upload_images/6667949-deb20eade827c6d6.png?imageMogr2/auto-orient/strip|imageView2/2/w/882/format/webp)

api-article.png


 重点在于request文件的编写，以下是axios进行基础配置的部分代码 

##### 2.axios请求拦截器

请求拦截器的作用是在请求发送前进行一些操作，例如在每个请求体里加上token，统一做了处理如果以后要改也非常容易。
 话不多说，直接上代码



```tsx
// create an axios instance
service.interceptors.request.use(
  config => {
    // 在发送请求之前做什么
    if (config.method === "post") {
      // 序列化
      // config.data = qs.stringify(config.data);
      // config.data = JSON.stringify(config.data);
      // 温馨提示,若是贵公司的提交能直接接受json 格式,可以不用 qs 来序列化的
    }else {
          if (store.getters.token) {
               // 若是有做鉴权token , 就给头部带上token
               // 让每个请求携带token-- ['X-Token']为自定义key 请根据实际情况自行修改
               // 若是需要跨站点,存放到 cookie 会好一点,限制也没那么多,有些浏览环境限制了 localstorage (隐身模式)的使用
                config.headers['X-Token'] = getToken()
          }
    }
    return config;
  },
  error => {
    // 对请求错误做些什么，自己定义
    Message({                  //使用element-ui的message进行信息提示
      showClose: true,
      message: error,
      type: "warning"
    });
    return Promise.reject(error);
  }
```

这里说一下token，一般是在登录完成之后，将用户的token通过localStorage或者cookie存在本地，然后用户每次在进入页面的时候，会首先从本地存储中读取token，如果token存在说明用户已经登陆过，则更新vuex中的token状态。然后，在每次请求接口的时候，都会在请求的header中携带token，服务器就可以根据你携带的token来判断你的登录是否过期，如果没有携带，则说明没有登录过。

##### 3.响应拦截器

响应拦截器的作用是在接收到响应后进行一些操作，例如在服务器返回登录状态失效，需要重新登录的时候，跳转到登录页等。
 话不多说，直接上代码



```jsx
// response interceptor
service.interceptors.response.use(
    response => {
        // 如果返回的状态码为200，说明接口请求成功，可以正常拿到数据
        // 否则的话抛出错误
        if (response.status === 200) {
            return Promise.resolve(response);
        } else {
            return Promise.reject(response);
        }
    },
    // 服务器状态码不是2开头的的情况
    // 这里可以跟你们的后台开发人员协商好统一的错误状态码
    // 然后根据返回的状态码进行一些操作，例如登录过期提示，错误提示等等
    // 下面列举几个常见的操作，其他需求可自行扩展
    error => {
        if (error.response.status) {
            switch (error.response.status) {
                // 401: 未登录
                // 未登录则跳转登录页面，并携带当前页面的路径
                // 在登录成功后返回当前页面，这一步需要在登录页操作。
                case 401:
                    router.replace({
                        path: '/login',
                        query: {
                            redirect: router.currentRoute.fullPath
                        }
                    });
                    break;

                // 403 token过期
                // 登录过期对用户进行提示
                // 清除本地token和清空vuex中token对象
                // 跳转登录页面
                case 403:
                      Message({
                        message: '登录过期，请重新登录',
                        duration: 1000,
                        forbidClick: true
                    });
                    // 清除token
                    localStorage.removeItem('token');
                    store.commit('loginSuccess', null);
                    // 跳转登录页面，并将要浏览的页面fullPath传过去，登录成功后跳转需要访问的页面
                    setTimeout(() => {
                        router.replace({
                            path: '/login',
                            query: {
                                redirect: router.currentRoute.fullPath
                            }
                        });
                    }, 1000);
                    break;

                // 404请求不存在
                case 404:
                    Message({
                        message: '网络请求不存在',
                        duration: 1500,
                        forbidClick: true
                    });
                    break;
                // 其他错误，直接抛出错误提示
                default:
                    Message({
                        message: error.response.data.message,
                        duration: 1500,
                        forbidClick: true
                    });
            }
            return Promise.reject(error.response);
        }
    }
});
```

响应拦截器很好理解，就是服务器返回给我们的数据，我们在拿到之前可以对他进行一些处理。例如：如果后台返回的状态码是200，则正常返回数据，否则的根据错误的状态码类型进行一些我们需要的错误，其实这里主要就是进行了错误的统一处理和没登录或登录过期后调整登录页的一个操作。

##### 4.在项目中调用拦截器

axios封装好之后，调用就很简单了。我们把接口统一写在api文件夹中。（如果你的业务非常复杂，建议把不同模块或组件的请求分开写到不同的文件里，这样方便维护）。



```jsx
//   api.js
import request from '@/utils/request'

export function userSearch(name) {
  return request({
    url: '/search/user',
    method: 'get',
    params: { name }
  })
}
```

然后在具体的组件中进行调用即可



```jsx
import { userSearch} from '@/api/api'
export default {
  data() {
    return {
        name: '大大大大大西瓜G'
    }
  },
  methods:{
      getUserInfo () {
          userSearch(this.name).then(res => {
              //对拿到的res.data进行一番操作或者渲染
          })
      }
  },
  mounted() {
      this.getUserInfo ();
  }
}
```