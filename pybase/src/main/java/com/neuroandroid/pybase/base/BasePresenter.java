package com.neuroandroid.pybase.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by NeuroAndroid on 2017/6/13.
 */

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
