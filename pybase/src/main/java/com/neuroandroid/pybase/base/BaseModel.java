package com.neuroandroid.pybase.base;

import com.neuroandroid.pybase.model.api.ApiService;
import com.neuroandroid.pybase.net.RetrofitUtils;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public class BaseModel implements IModel {
    protected ApiService mService;

    public BaseModel(String baseUrl) {
        this(baseUrl, false);
    }

    public BaseModel(String baseUrl, boolean needCache) {
        mService = RetrofitUtils.getInstance(baseUrl, needCache).create(ApiService.class);
    }

    @Override
    public void onDestroy() {
        mService = null;
    }
}
