package com.neuroandroid.pybase.mvp.presenter;

import com.neuroandroid.pybase.base.BaseObserver;
import com.neuroandroid.pybase.base.BasePresenter;
import com.neuroandroid.pybase.base.BaseResponse;
import com.neuroandroid.pybase.config.Constant;
import com.neuroandroid.pybase.model.response.User;
import com.neuroandroid.pybase.mvp.contract.ILoginContract;
import com.neuroandroid.pybase.mvp.model.impl.LoginModelImpl;
import com.neuroandroid.pybase.utils.RxUtils;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public class LoginPresenter extends BasePresenter<LoginModelImpl, ILoginContract.View> implements ILoginContract.Presenter {
    public LoginPresenter(ILoginContract.View view) {
        super(view);
        mModel = new LoginModelImpl(Constant.BASE_URL);
        mView.setPresenter(this);
    }

    @Override
    public void login(String param, String password, int userType, String ip) {
        getModelFilteredFactory(User.class).compose(mModel.login(param, password, userType, ip))
                .compose(RxUtils.bindToLifecycle(mView))
                .subscribe(new BaseObserver<BaseResponse<User>>() {
                    @Override
                    protected void onHandleSuccess(BaseResponse<User> userBaseResponse) {
                        mView.showLoginMsg(userBaseResponse.getData());
                    }

                    @Override
                    protected void onHandleError(String tip) {
                        mView.showTip(tip);
                    }
                });
    }
}
