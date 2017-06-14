package com.neuroandroid.pybase.mvp.model.impl;

import com.neuroandroid.pybase.base.BaseModel;
import com.neuroandroid.pybase.base.BaseResponse;
import com.neuroandroid.pybase.model.response.User;
import com.neuroandroid.pybase.mvp.model.ILoginModel;

import io.reactivex.Observable;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public class LoginModelImpl extends BaseModel implements ILoginModel {
    public LoginModelImpl(String baseUrl) {
        super(baseUrl);
    }

    @Override
    public Observable<BaseResponse<User>> login(String param, String password, int userType, String ip) {
        return mService.login(param, password, userType, ip);
    }
}
