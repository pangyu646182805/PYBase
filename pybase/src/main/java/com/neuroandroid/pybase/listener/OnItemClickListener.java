package com.neuroandroid.pybase.listener;

import com.neuroandroid.pybase.adapter.base.BaseViewHolder;

/**
 * Created by NeuroAndroid on 2017/2/14.
 */

public interface OnItemClickListener<T> {
    void onItemClick(BaseViewHolder holder, int position, T item);
}
