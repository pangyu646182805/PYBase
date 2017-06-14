package com.neuroandroid.pybase.net;

import android.support.annotation.NonNull;

import com.neuroandroid.pybase.base.BaseResponse;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public class ModelFilteredFactory {
    private static final ObservableTransformer TRANSFORMER = new SimpleTransformer();

    @SuppressWarnings("unchecked")
    public static <T> Observable<BaseResponse<T>> compose(Observable<BaseResponse<T>> observable) {
        return observable.compose(TRANSFORMER);
    }

    private static class SimpleTransformer<T> implements ObservableTransformer<BaseResponse<T>, BaseResponse<T>> {
        @Override
        public ObservableSource apply(@NonNull Observable upstream) {
            return upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .timeout(5, TimeUnit.SECONDS)  // 重连间隔时间
                    .retry(5);  // 重连次数
        }
    }
}
