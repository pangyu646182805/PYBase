package com.neuroandroid.pybase.ui.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.neuroandroid.pybase.R;
import com.neuroandroid.pybase.adapter.base.BaseRvAdapter;
import com.neuroandroid.pybase.adapter.base.BaseViewHolder;
import com.neuroandroid.pybase.base.BaseActivity;
import com.neuroandroid.pybase.config.Constant;
import com.neuroandroid.pybase.model.response.User;
import com.neuroandroid.pybase.mvp.contract.ILoginContract;
import com.neuroandroid.pybase.mvp.presenter.LoginPresenter;
import com.neuroandroid.pybase.widget.NoPaddingTextView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<ILoginContract.Presenter> implements ILoginContract.View {
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.rv)
    RecyclerView mRv;

    @Override
    protected void initPresenter() {
        mPresenter = new LoginPresenter(Constant.BASE_URL, this);
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).build());
        mRefreshLayout.setPureScrollModeOn();
    }

    @Override
    protected void initData() {
        // mPresenter.login("18805864649", "123456", 0, "");
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            dataList.add(i + "");
        }
        MyAdapter myAdapter = new MyAdapter(this, dataList, R.layout.item);
        mRv.setAdapter(myAdapter);

        View headerView = LayoutInflater.from(this).inflate(R.layout.item, null);
        NoPaddingTextView tvHeader = (NoPaddingTextView) headerView.findViewById(R.id.tv);
        tvHeader.setTextSize(30);
        tvHeader.setText("我是头布局");

        View headerView1 = LayoutInflater.from(this).inflate(R.layout.item, null);
        NoPaddingTextView tvHeader1 = (NoPaddingTextView) headerView1.findViewById(R.id.tv);
        tvHeader1.setTextSize(35);
        tvHeader1.setText("我是头布局1");

        View footerView = LayoutInflater.from(this).inflate(R.layout.item, null);
        NoPaddingTextView tvFooter = (NoPaddingTextView) footerView.findViewById(R.id.tv);
        tvFooter.setTextSize(30);
        tvFooter.setText("我是尾布局");

        View footerView1 = LayoutInflater.from(this).inflate(R.layout.item, null);
        NoPaddingTextView tvFooter1 = (NoPaddingTextView) footerView1.findViewById(R.id.tv);
        tvFooter1.setTextSize(30);
        tvFooter1.setText("我是尾布局1");

        myAdapter.addHeaderView(headerView, headerView1);
        myAdapter.addFooterView(footerView, footerView1);
    }

    @Override
    public void showLoginMsg(User user) {
        ((NoPaddingTextView) findViewById(R.id.tv)).setText(new Gson().toJson(user));
    }

    @Override
    public void showTip(String tip) {
        ((NoPaddingTextView) findViewById(R.id.tv)).setText(tip);
    }

    class MyAdapter extends BaseRvAdapter<String> {
        public MyAdapter(Context context, List<String> dataList, int layoutId) {
            super(context, dataList, layoutId);
        }

        @Override
        public void convert(BaseViewHolder holder, String item, int position) {
            holder.setText(R.id.tv, item);
        }
    }
}
