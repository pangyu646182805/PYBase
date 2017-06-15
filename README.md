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

接下来就是MVP的使用：

首先是一些基类：

IView：
```
public interface IView<T extends IPresenter> {
    /**
     * 设置presenter
     * @param presenter
     */
    void setPresenter(T presenter);

    /**
     * 显示加载动画
     */
    void showLoading();

    /**
     * 隐藏加载
     */
    void hideLoading();

    /**
     * 显示错误状态
     */
    void showError(StateLayout.OnRetryListener onRetryListener);

    /**
     * 显示提示
     */
    void showTip(String tip);
}
```
BasePresenter：
```
public class BasePresenter<M extends IModel, V extends IView> implements IPresenter {
    protected CompositeDisposable mCompositeDisposable;

    protected M mModel;
    protected V mView;

    public BasePresenter(String baseUrl, V view) {
        this.mView = view;
    }

    protected void addDispose(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        // 将所有disposable放入，集中处理
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        unDispose();
        if (mModel != null)
            mModel.onDestroy();
        this.mModel = null;
        this.mView = null;
        this.mCompositeDisposable = null;
    }

    /**
     * 解除订阅
     */
    protected void unDispose() {
        if (mCompositeDisposable != null) {
            // 保证activity结束时取消所有正在执行的订阅
            mCompositeDisposable.clear();
        }
    }
}
```


