package com.neuroandroid.pybase.ui.activity;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;
import com.neuroandroid.pybase.R;
import com.neuroandroid.pybase.adapter.base.BaseViewHolder;
import com.neuroandroid.pybase.adapter.base.ISelect;
import com.neuroandroid.pybase.adapter.base.SelectAdapter;
import com.neuroandroid.pybase.base.BaseActivity;
import com.neuroandroid.pybase.bean.TestSelectBean;
import com.neuroandroid.pybase.config.Constant;
import com.neuroandroid.pybase.model.response.User;
import com.neuroandroid.pybase.mvp.contract.ILoginContract;
import com.neuroandroid.pybase.mvp.presenter.LoginPresenter;
import com.neuroandroid.pybase.utils.L;
import com.neuroandroid.pybase.widget.NoPaddingTextView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<ILoginContract.Presenter> implements ILoginContract.View {
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.rv)
    RecyclerView mRv;
    private MyAdapter mAdapter;

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
        mRefreshLayout.setHeaderView(new ProgressLayout(this));
    }

    @Override
    protected void initData() {
        List<TestSelectBean> dataList = new ArrayList<>();
        TestSelectBean testSelectBean;
        for (int i = 0; i < 60; i++) {
            testSelectBean = new TestSelectBean();
            testSelectBean.setSelected(i == 0);
            testSelectBean.setText("position : " + i);
            dataList.add(testSelectBean);
        }
        mAdapter = new MyAdapter(this, dataList, R.layout.item);
        mRv.setAdapter(mAdapter);

        View headerView = LayoutInflater.from(this).inflate(R.layout.item, null);
        NoPaddingTextView tvHeader = (NoPaddingTextView) headerView.findViewById(R.id.tv);
        tvHeader.setTextSize(30);
        tvHeader.setText("我是头布局");

        View headerView1 = LayoutInflater.from(this).inflate(R.layout.item, mRv, false);
        NoPaddingTextView tvHeader1 = (NoPaddingTextView) headerView1.findViewById(R.id.tv);
        tvHeader1.setTextSize(35);
        tvHeader1.setText("我是头布局1");

        View footerView = LayoutInflater.from(this).inflate(R.layout.item, mRv, false);
        NoPaddingTextView tvFooter = (NoPaddingTextView) footerView.findViewById(R.id.tv);
        tvFooter.setTextSize(30);
        tvFooter.setText("我是尾布局");

        View footerView1 = LayoutInflater.from(this).inflate(R.layout.item, mRv, false);
        NoPaddingTextView tvFooter1 = (NoPaddingTextView) footerView1.findViewById(R.id.tv);
        tvFooter1.setTextSize(30);
        tvFooter1.setText("我是尾布局1");

        mAdapter.addHeaderView(headerView, headerView1);
        mAdapter.addFooterView(footerView, footerView1);

        mAdapter.setSelectedMode(ISelect.MULTIPLE_MODE);
        mAdapter.setItemSelectedListener(new SelectAdapter.OnItemSelectedListener<TestSelectBean>() {
            @Override
            public void onItemSelected(BaseViewHolder viewHolder, int position, boolean isSelected, TestSelectBean testSelectBean) {

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    @OnClick(R.id.btn)
    public void test() {
        mPresenter.login("18805864649", "123456", 0, "");
    }

    @Override
    public void showLoginMsg(User user) {
        TestSelectBean testSelectBean = new TestSelectBean();
        testSelectBean.setText(new Gson().toJson(user));
        mAdapter.set(2, testSelectBean);
        L.e("json : " + new Gson().toJson(user));
    }

    @Override
    public void showTip(String tip) {
        TestSelectBean testSelectBean = new TestSelectBean();
        testSelectBean.setText(new Gson().toJson(tip));
        mAdapter.set(2, testSelectBean);
        L.e("tip : " + tip);
    }

    class MyAdapter extends SelectAdapter<TestSelectBean> {
        public MyAdapter(Context context, List<TestSelectBean> dataList, int layoutId) {
            super(context, dataList, layoutId);
        }

        @Override
        public void convert(BaseViewHolder holder, TestSelectBean item, int position, int viewType) {
            CheckBox cb = holder.getView(R.id.cb);
            cb.setChecked(item.isSelected());
            holder.setText(R.id.tv, item.getText());
        }
    }
}
