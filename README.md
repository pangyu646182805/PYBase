# PYBase
Android基础项目，封装了BaseActivity、BaseFragment、BaseLazyFragment，使用MVP设计模式、RxJava + Retrofit作为网络框架，目前处于初步阶段，项目会逐步进行更新

# 记录自己学到的东西

# MVP+RxJava+Retrofit的封装
首先是RetrofitService：
```
/**
 * 登录
 */
@GET("login/{param}")
Observable<BaseResponse<User>> login(@Path("param") String param,
                                     @Query("password") String password,
                                     @Query("userType") int userType,
                                     @Query("visitIp") String ip);
```
retrofit配合rxjava使用需要将login()返回类型从原来的Call<T>类型改为Observable<T>类型

BaseResponse：
```
class BaseResponse<T> {
  private String msg;
  private int code;
  private T data;
}
```

接着是RetrofitUtils得到Retrofit实例：
```
public static Retrofit getInstance(String url) {
  sRetrofit = new Retrofit.Builder().baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getClient())
                .build();
        return sRetrofit;
}
```
```
private static OkHttpClient getClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.retryOnConnectionFailure(true).connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS);

    if (L.getGlobalToggle())  // 控制是否需要打印调试
      builder.addInterceptor(new LogInterceptor());
    return builder.build();
}
```
