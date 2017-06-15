package com.neuroandroid.pybase.adapter.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.neuroandroid.pybase.listener.OnItemClickListener;
import com.neuroandroid.pybase.listener.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NeuroAndroid on 2017/6/14.
 */

public abstract class BaseRvAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private Context mContext;
    // 数据源
    private List<T> mDataList;
    private int mLayoutId;
    // 存放头部布局的容器
    protected SparseArray<View> mHeaderViews;
    // 存放底部布局的容器
    protected SparseArray<View> mFooterViews;
    // 行布局点击监听
    protected OnItemClickListener<T> mOnItemClickListener;
    // 行布局长按监听
    protected OnItemLongClickListener<T> mOnItemLongClickListener;
    // 空数据占位View
    protected View mEmptyView;
    @LayoutRes  // 空数据占位View的布局id
    protected int mEmptyViewId;
    // EmptyView的位置
    protected int mEmptyViewPosition = -1;
    protected IMultiItemViewType<T> mMultiItemViewType;

    public BaseRvAdapter(Context context, List<T> dataList, int layoutId) {
        mContext = context;
        mDataList = dataList == null ? new ArrayList<>() : dataList;
        mLayoutId = layoutId;
        this.mMultiItemViewType = null;
    }

    public BaseRvAdapter(Context context, List<T> dataList, IMultiItemViewType<T> multiItemViewType) {
        mContext = context;
        mDataList = dataList == null ? new ArrayList<>() : dataList;
        this.mMultiItemViewType = multiItemViewType == null ? null : multiItemViewType;
    }

    /**
     * 单击监听
     */
    public void setOnItemClickListener(OnItemClickListener<T> clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    /**
     * 长按监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener<T> longClickListener) {
        this.mOnItemLongClickListener = longClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= BaseViewType.FOOTER && mFooterViews != null && mFooterViews.get(viewType) != null) {
            return BaseViewHolder.createViewHolder(mContext, mFooterViews.get(viewType));
        } else if (viewType >= BaseViewType.HEADER && mHeaderViews != null && mHeaderViews.get(viewType) != null) {
            return BaseViewHolder.createViewHolder(mContext, mHeaderViews.get(viewType));
        } else {
            int layoutId;
            if (mMultiItemViewType != null) {
                layoutId = mMultiItemViewType.getLayoutId(viewType);
            } else {
                layoutId = mLayoutId;
            }
            BaseViewHolder viewHolder = BaseViewHolder.createViewHolder(mContext, parent, layoutId);
            setListener(viewHolder);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (isInHeadViewPos(position) || isInFootViewPos(position)) {
            return;
        }
        convert(holder, mDataList.get(position - getHeaderCounts()), position, getItemViewType(position));
    }

    /**
     * 设置空数据占位VIew
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    /**
     * 设置空数据占位View的id
     */
    public void setEmptyView(int layoutId) {
        this.mEmptyViewId = layoutId;
    }

    public abstract void convert(BaseViewHolder holder, T item, int position, int viewType);

    @Override
    public int getItemViewType(int position) {
        if (isInHeadViewPos(position)) {
            return mHeaderViews.keyAt(position);
        } else if (isInFootViewPos(position)) {
            return mFooterViews.keyAt(position - getDataListSize() - getHeaderCounts());
        } else {
            if (mMultiItemViewType != null) {
                int newPosition = position - getHeaderCounts();
                return mMultiItemViewType.getItemViewType(newPosition, mDataList.get(newPosition));
            } else {
                return super.getItemViewType(position);
            }
        }
    }

    /**
     * 获取数据集数量
     */
    public int getDataListSize() {
        return mDataList.size();
    }

    /**
     * 某个位置是否处于HeadView的位置内
     */
    protected boolean isInHeadViewPos(int pos) {
        return pos < getHeaderCounts();
    }

    /**
     * 某个位置是否处于FootView的位置内
     */
    protected boolean isInFootViewPos(int pos) {
        return pos >= getDataListSize() + getHeaderCounts() &&
                pos < getDataListSize() + getHeaderCounts() + getFooterCounts();
    }

    /**
     * 获取HeadView的数量
     */
    public int getHeaderCounts() {
        return mHeaderViews != null ? mHeaderViews.size() : 0;
    }

    /**
     * 获取FootView的数量
     */
    public int getFooterCounts() {
        return mFooterViews != null ? mFooterViews.size() : 0;
    }

    /**
     * 设置布局点击监听[单机和长按]
     */
    protected void setListener(final BaseViewHolder viewHolder) {
        viewHolder.getItemView().setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                int position = viewHolder.getLayoutPosition();
                mOnItemClickListener.onItemClick(viewHolder, position, mDataList.get(position - getHeaderCounts()));
            }
        });

        viewHolder.getItemView().setOnLongClickListener(view -> {
            if (mOnItemLongClickListener != null) {
                int position = viewHolder.getLayoutPosition();
                mOnItemLongClickListener.onItemLongClick(viewHolder, position, mDataList.get(position - getHeaderCounts()));
            }
            return true;
        });
    }

    /**
     * 添加HeaderView
     * 必须在setLayoutManager()方法之后设置
     */
    public void addHeaderView(View... headerViews) {
        if (mHeaderViews == null)
            mHeaderViews = new SparseArray<>();
        for (View headerView : headerViews) {
            mHeaderViews.put(BaseViewType.HEADER + getHeaderCounts(), headerView);
        }
        notifyItemInserted(headerViews.length);
    }

    /**
     * 删除指定位置的HeaderView
     */
    public void removeHeaderViewAt(int index) {
        if (mHeaderViews != null) {
            mHeaderViews.removeAt(index);
            notifyItemRemoved(index);
        }
    }

    /**
     * 清空HeaderView
     */
    public void clearHeaderViews() {
        if (mHeaderViews != null && mHeaderViews.size() > 0) {
            int size = mHeaderViews.size();
            mHeaderViews.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    /**
     * 添加FooterView
     * 必须在setLayoutManager()方法之后设置
     */
    public void addFooterView(View... footerViews) {
        if (mFooterViews == null)
            mFooterViews = new SparseArray<>();
        for (View footerView : footerViews) {
            mFooterViews.put(BaseViewType.FOOTER + getFooterCounts(), footerView);
        }
        notifyItemInserted(footerViews.length);
    }

    /**
     * 删除指定位置的HeaderView
     */
    public void removeFooterViewAt(int index) {
        if (mFooterViews != null) {
            mFooterViews.removeAt(index);
            notifyItemRemoved(index);
        }
    }

    /**
     * 清空HeaderView
     */
    public void clearFooterViews() {
        if (mFooterViews != null && mFooterViews.size() > 0) {
            int size = mFooterViews.size();
            mFooterViews.clear();
            notifyItemRangeRemoved(getHeaderCounts() + getDataListSize(), size);
        }
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        } else {
            return getDataListSize() + getHeaderCounts() + getFooterCounts();
        }
    }
}
