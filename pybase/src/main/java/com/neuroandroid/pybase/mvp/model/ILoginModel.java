package com.neuroandroid.pybase.mvp.model;

import com.neuroandroid.pybase.base.BaseResponse;
import com.neuroandroid.pybase.model.response.User;

import io.reactivex.Observable;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public interface ILoginModel {
    Observable<BaseResponse<User>> login(String param, String password, int userType, String ip);
}
