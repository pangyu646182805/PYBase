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
BaseModel获取RetrofitService实例：
```
public class BaseModel implements IModel {
    protected ApiService mService;

    public BaseModel(String baseUrl) {
        mService = RetrofitUtils.getInstance(baseUrl).create(ApiService.class);
    }

    @Override
    public void onDestroy() {
        mService = null;
    }

```
ILoginContract：
```
public interface ILoginContract {
    interface Presenter extends IPresenter {
        /**
         * 登录
         */
        void login(String param, String password, int userType, String ip);
    }

    interface View extends IView<Presenter> {
        /**
         * 获取登录信息
         * @param user
         */
        void showLoginMsg(User user);
    }
}
```
LoginModelImpl省略...

LoginPresenter：
```
public class LoginPresenter extends BasePresenter<LoginModelImpl, ILoginContract.View> implements ILoginContract.Presenter {
    public LoginPresenter(String baseUrl, ILoginContract.View view) {
        super(baseUrl, view);
        mModel = new LoginModelImpl(Constant.BASE_URL);
        mView.setPresenter(this);
    }

    @Override
    public void login(String param, String password, int userType, String ip) {
        ModelFilteredFactory.compose(mModel.login(param, password, userType, ip))
                .compose(RxUtils.bindToLifecycle(mView))
                .subscribe(new BaseObserver<User>() {
                    @Override
                    protected void onHandleSuccess(User user) {
                        mView.showLoginMsg(user);
                    }

                    @Override
                    protected void onHandleError(String tip) {
                        mView.showTip(tip);
                    }
                });
    }
}
```
其中使用了RxLifecycle绑定了activity和fragment的生命周期，防止rxjava使用过程当中的内存泄漏(生命周期的解除订阅)
```
public static <T> LifecycleTransformer<T> bindToLifecycle(IView view) {
    if (view instanceof RxAppCompatActivity) {
        return ((RxAppCompatActivity) view).bindToLifecycle();
    } else if (view instanceof RxFragment) {
        return ((RxFragment) view).bindToLifecycle();
    } else {
        throw new IllegalArgumentException("view isn't activity or fragment");
    }
}
```
BaseObserver(简单封装了Observer)：
```
@Override
public void onNext(@NonNull BaseResponse<T> response) {
    if (response.getCode() == Constant.RESPONSE_CODE_OK) {
        T data = response.getData();
        onHandleSuccess(data);
    } else {
        onHandleError(response.getMsg());
    }
}

@Override
public void onError(Throwable e) {
    L.e("error:" + e.toString());
    if (e instanceof APIException) {
        APIException exception = (APIException) e;
        onHandleError(exception.getMessage());
    } else if (e instanceof UnknownHostException) {
        onHandleError("请打开网络");
    } else if (e instanceof SocketTimeoutException) {
        onHandleError("请求超时");
    } else if (e instanceof ConnectException) {
        onHandleError("连接失败");
    } else if (e instanceof HttpException) {
        onHandleError("请求超时");
    } else {
        onHandleError("请求失败");
    }
    e.printStackTrace();
}
```
在activity(封装了BaseActivity简化代码)中使用：
```
public class MainActivity extends BaseActivity<ILoginContract.Presenter> implements ILoginContract.View
```
```
mPresenter = new LoginPresenter(Constant.BASE_URL, this);
```
发送网络请求就仅仅只需一行代码：
```
mPresenter.login("用户名", "123456", 0, "");
```
在showLoginMsg()中返回服务器返回的信息：
```
@Override
public void showLoginMsg(User user) {
  // DO SOMETHING
}
```

# RecyclerView.Adapter的简单封装：
* 极大简化了代码
* 支持添加多个Header和Footer
* 支持item的单击和长按监听
* 支持RecyclerView多ViewType

使用方法：
```
class  PYAdapter extends BaseRvAdapter<String> {
    public PYAdapter(Context context, List<String> dataList, int layoutId) {
        super(context, dataList, layoutId);
    }

    @Override
    public void convert(BaseViewHolder holder, String item, int position, int viewType) {
        holder.setText(R.id.tv, item);
    }
}
```
```
rc.setAdapter(new PYAdapter(this, dataList, R.layout.item));
```
多布局使用方法：
```
class PYAdapter extends BaseRvAdapter<String> {
    public PYAdapter(Context context, List<String> dataList, IMultiItemViewType<String> multiItemViewType) {
        super(context, dataList, multiItemViewType);
    }

    @Override
    public void convert(BaseViewHolder holder, String item, int position, int viewType) {
        switch (viewType) {
            case 100:
                holder.setText(R.id.tv, item + " 100");
                break;
            case 200:
                holder.setText(R.id.tv, item + " 200");
                break;
        }
    }
}
```
```
PYAdapter pyAdapter = new PYAdapter(this, dataList, new IMultiItemViewType<String>() {
  @Override
  public int getViewTypeCount() {
      return 2;
  }

  @Override
  public int getItemViewType(int position, String s) {
      if (position % 2 == 0) {
          return 100;
      } else {
          return 200;
      }
  }

  @Override
  public int getLayoutId(int viewType) {
      return R.layout.item;
  }
});
rv.setAdapter(pyAdapter);
```



