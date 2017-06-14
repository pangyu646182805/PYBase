package com.neuroandroid.pybase.mvp.presenter;

import com.neuroandroid.pybase.base.BaseObserver;
import com.neuroandroid.pybase.base.BasePresenter;
import com.neuroandroid.pybase.config.Constant;
import com.neuroandroid.pybase.model.response.User;
import com.neuroandroid.pybase.mvp.contract.ILoginContract;
import com.neuroandroid.pybase.mvp.model.impl.LoginModelImpl;
import com.neuroandroid.pybase.net.ModelFilteredFactory;
import com.neuroandroid.pybase.utils.RxUtils;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

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
