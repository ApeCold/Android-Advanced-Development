>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第一个视频的内容】
1.OSI七层模型，TCP/IP模型(四层)，HTTP格式
  OSI七层参考模型   ---》 TCP/IP参考模型
  TCP/IP参考模型 四层：
  应用层  ---》 HTTP HTTPS ...
  传输层  ---》 Socket

  HTTP  get（请求行，请求属性集）  post（请求行,请求属性集, type,len==请求体）


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第二个视频的内容】
--- OKHTTP源码的主线流程
OKHTTP的使用

OkHttpClient

Request

RealCall implements Call

// 异步方法
RealCall.enqueue(new Callback()
    不能执行大于1次 enqueue
    synchronized (this) {
      if (executed) throw new IllegalStateException("Already Executed");
      executed = true;
    }

    拿到调度器dispatcher。enqueue方法，
    client.dispatcher().enqueue(new AsyncCall(responseCallback));

    -- 调用到 dispatcher的方法
    synchronized void enqueue(AsyncCall call) {
        if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
          runningAsyncCalls.add(call);
          executorService().execute(call);
        } else {
          readyAsyncCalls.add(call);
        }
      }

      Dispatcher {

         等待执行队列
         private final Deque<AsyncCall> readyAsyncCalls = new ArrayDeque<>();

         运行的队列
         private final Deque<AsyncCall> runningAsyncCalls = new ArrayDeque<>();

         synchronized void enqueue(AsyncCall call) {

             同时运行的异步任务小于64 && 同时访问(同一个)服务器 小于5个 ----》把运行的任务加入到 运行队列中 然后执行
             if (runningAsyncCalls.size() < maxRequests && runningCallsForHost(call) < maxRequestsPerHost) {
               runningAsyncCalls.add(call);
               executorService().execute(call); // 执行
             } else {
               加入到等待队列
               readyAsyncCalls.add(call);
             }
           }

           Deque 双端队列：


           AsyncCall 执行耗时任务
             responseCallback true：这个错误是用户造成的，和OKHTTP没关系
                              false：这个错误是OKHTTP造成的， onFailure
      }

     >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
梳理主线流程：
OKHttpClient ---> Request --> newCall RealCall.enqueue(){不能重复执行} ---> Dispatcher.enqueue(AsyncCall) --->

Dispatcher {if:先加入运行队列里面去 执行异步任务 else 直接加入等待队列} --->异步任务 ---> AsyncCall execute{} --》
责任链模式 多个拦截器 response -->


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第三个视频的内容】
----> 分析OKHTTP里面的线程池
executorService().execute(call);

  public synchronized ExecutorService executorService() {
    if (executorService == null) {
      executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), Util.threadFactory("OkHttp Dispatcher", false));
    }
    return executorService;
  }
分析结果：OKHTTP里面的线程池，采用的是缓存 方案
OKHTTP里面的线程池：采用的是缓存 方案，+ 线程工厂 name  不是守护线程

---> 总结：OKHTTP线程池采用的是缓存方案 + 定义线程工程（设置线程名，设置不是守护线程）
缓存方案：参数1 == 0
         参数2 Integer.Max
         参数3/4：60s闲置时间 只要参数1 ,只要Runnable > 参数1 起作用(60s之内 就会复用之前的任务，60s之后就会回收任务)



>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第四个视频的内容】

看OKHTTP源码，发现OKHTTP里面使用到了构建者模式，所以才要学习构建者设计模式 【小节1视频】
OKHttpClient ---构建者设计模式
Request ---构建者设计模式
开始学习 构建者设计模式 --> 盖房子的案例(根据OKHTTP源码中的链式调用)


看OKHTTP源码，发现OKHTTP里面使用到了责任链模式，所以才要学习责任链设计模式 【小节2视频】
责任链模式
最终返回结果 Response
Response getResponseWithInterceptorChain() throws IOException {
    // Build a full stack of interceptors.
    List<Interceptor> interceptors = new ArrayList<>();
    interceptors.addAll(client.interceptors());
    interceptors.add(retryAndFollowUpInterceptor);
    interceptors.add(new BridgeInterceptor(client.cookieJar()));
    interceptors.add(new CacheInterceptor(client.internalCache()));
    interceptors.add(new ConnectInterceptor(client));
    if (!forWebSocket) {
      interceptors.addAll(client.networkInterceptors());
    }
    interceptors.add(new CallServerInterceptor(forWebSocket));

    Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
        originalRequest, this, eventListener, client.connectTimeoutMillis(),
        client.readTimeoutMillis(), client.writeTimeoutMillis());

    return chain.proceed(originalRequest);
  }
}
chain案例 属性 责任链模式
chain2案例 对应 OKHTTP源码 --》getResponseWithInterceptorChain

--- 阅读OKHTTP源码的总结：
1.OSI七层模型，TCP/IP参考模型，HTTP格式。
2.我们阅读了OKHTTP源码的 主线流程。
3.我们阅读了OKHTTP源码 缓存方案的线程池。
4.我们阅读了OKHTTP源码，发现构建者设计模式（1），责任链模式（2）


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第五个视频的内容】
基本架构已经搭建好了
Request 封装我们的请求数据

enqueue异步任务的执行的源码编写
....


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【第六个视频的内容】
    责任链拦截器[编写]
    网络请求
    网络响应 Response


>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>【OKHTTP总结视频的内容】


































