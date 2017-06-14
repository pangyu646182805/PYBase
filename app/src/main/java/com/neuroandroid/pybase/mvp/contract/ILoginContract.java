package com.neuroandroid.pybase.mvp.contract;

import com.neuroandroid.pybase.base.IPresenter;
import com.neuroandroid.pybase.base.IView;
import com.neuroandroid.pybase.model.response.User;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

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
