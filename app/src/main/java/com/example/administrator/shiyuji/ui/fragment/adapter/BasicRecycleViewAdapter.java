package com.example.administrator.shiyuji.ui.fragment.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.notification.support.ANotificationHeaderView;
import com.example.administrator.shiyuji.ui.fragment.support.AHeaderItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.ARecycleViewItemView;
import com.example.administrator.shiyuji.ui.fragment.support.IITemView;
import com.example.administrator.shiyuji.ui.fragment.support.IItemViewCreator;
import com.example.administrator.shiyuji.ui.fragment.support.IPagingAdapter;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * RecyclerView的适配器
 */


public class BasicRecycleViewAdapter<T extends Serializable> extends RecyclerView.Adapter implements IPagingAdapter {
    private IItemViewCreator<T> itemViewCreator;
    private ArrayList<T> datas;
    private IITemView<T> footerItemView;
    private AHeaderItemViewCreator<T> headerItemViewCreator;
    private int[][] headerItemTypes;
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private final Activity activity;
    private final APagingFragment ownerFragment;
    View.OnClickListener innerOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            IITemView itemView = (IITemView)v.getTag(R.id.itemview);
            if(BasicRecycleViewAdapter.this.onItemClickListener != null && itemView != null) {
                BasicRecycleViewAdapter.this.onItemClickListener.onItemClick((AdapterView)null, itemView.getConvertView(), itemView.itemPosition(), BasicRecycleViewAdapter.this.getItemId(itemView.itemPosition()));
            }

        }
    };
    View.OnLongClickListener innerOnLongClickListener = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            IITemView itemView = (IITemView)v.getTag(R.id.itemview);
            return BasicRecycleViewAdapter.this.onItemLongClickListener != null?BasicRecycleViewAdapter.this.onItemLongClickListener.onItemLongClick((AdapterView)null, itemView.getConvertView(), itemView.itemPosition(), BasicRecycleViewAdapter.this.getItemId(itemView.itemPosition())):false;
        }
    };

    public BasicRecycleViewAdapter(Activity activity, APagingFragment ownerFragment, IItemViewCreator<T> itemViewCreator, ArrayList<T> datas) {
        this.activity = activity;
        if(datas == null) {
            datas = new ArrayList();
        }

        this.itemViewCreator = itemViewCreator;
        this.ownerFragment = ownerFragment;
        this.datas = datas;
    }

    public void addFooterView(IITemView<T> footerItemView) {
        this.footerItemView = footerItemView;
        if(footerItemView.getConvertView().getLayoutParams() == null) {
            footerItemView.getConvertView().setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        }

    }

    public void setHeaderItemViewCreator(AHeaderItemViewCreator<T> headerItemViewCreator) {
        this.headerItemViewCreator = headerItemViewCreator;
        this.headerItemTypes = headerItemViewCreator.setHeaders();
    }

    /**
     * 设置视图的类型，即header，item，footer
     * position与getItemCount方法有关
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        if(this.footerItemView != null && position == this.getItemCount() - 1) {
            return 2000;
        } else if(this.headerItemViewCreator != null && position < this.headerItemTypes.length) {
            return this.headerItemTypes[position][1];
        } else {
            int headerCount = this.headerItemTypes != null?this.headerItemTypes.length:0;
            if(position >= headerCount) {
                int realPosition = position - headerCount;
                Serializable t = (Serializable)this.getDatas().get(realPosition);
                if(t instanceof ItemTypeData) {
                    return ((ItemTypeData)t).itemType();
                }
            }

            return 1000;
        }
    }

    private boolean isHeaderType(int viewType) {
        if(this.headerItemTypes != null) {
            int[][] var2 = this.headerItemTypes;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                int[] itemResAndType = var2[var4];
                if(viewType == itemResAndType[1]) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isFooterType(int viewType) {
        return viewType == 2000;
    }

    /**
     * 创建视图，viewType是在getItemViewType方法中设置
     * @param parent
     * @param viewType
     * @return
     */
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView;
        IITemView itemView;//viewHolder
        if(this.isFooterType(viewType)) {
            itemView = this.footerItemView;
            convertView = itemView.getConvertView();
            if(this.ownerFragment.getRefreshView() != null && this.ownerFragment.getRefreshView() instanceof RecyclerView) {
                RecyclerView recyclerView = (RecyclerView)this.ownerFragment.getRefreshView();
                if(recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams layoutParams;
                    if(convertView.getLayoutParams() != null && convertView.getLayoutParams() instanceof android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams) {
                        layoutParams = (android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams)convertView.getLayoutParams();
                    } else {
                        layoutParams = new android.support.v7.widget.StaggeredGridLayoutManager.LayoutParams(-1, -2);
                        convertView.setLayoutParams(layoutParams);
                    }

                    if(!layoutParams.isFullSpan()) {
                        layoutParams.setFullSpan(true);
                    }
                }
            }
        } else if(this.isHeaderType(viewType)) {
            convertView = this.headerItemViewCreator.newContentView(this.activity.getLayoutInflater(), parent, viewType);
            itemView = this.headerItemViewCreator.newItemView(convertView, viewType);
            convertView.setTag(R.id.itemview, itemView);
            if (itemView instanceof ANotificationHeaderView){
                headerView = (ANotificationHeaderView) itemView;
            }
        } else {
            convertView = this.itemViewCreator.newContentView(this.activity.getLayoutInflater(), parent, viewType);
            itemView = this.itemViewCreator.newItemView(convertView, viewType);
            convertView.setTag(R.id.itemview, itemView);
        }

        itemView.onBindView(convertView);
        if(!(itemView instanceof ARecycleViewItemView)) {
            throw new RuntimeException("RecycleView只支持ARecycleViewItemView，请重新配置");
        } else {
            return (ARecycleViewItemView)itemView;
        }
    }

    /**
     * 顶部视图
     */
    private ANotificationHeaderView headerView ;

    public ANotificationHeaderView getHeaderView(){
        return this.headerView;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ARecycleViewItemView itemView = (ARecycleViewItemView)holder;
        int headerCount = this.headerItemTypes != null?this.headerItemTypes.length:0;
        if(position >= headerCount) {
            int realPosition = position - headerCount;
            itemView.reset(this.datas.size(), realPosition);
            if(realPosition < this.datas.size()) {
                itemView.onBindData(itemView.getConvertView(), (Serializable)this.datas.get(realPosition), realPosition);
            }

            if(this.onItemClickListener != null) {
                itemView.getConvertView().setOnClickListener(this.innerOnClickListener);
            } else {
                itemView.getConvertView().setOnClickListener((View.OnClickListener)null);
            }

            if(this.onItemLongClickListener != null) {
                itemView.getConvertView().setOnLongClickListener(this.innerOnLongClickListener);
            } else {
                itemView.getConvertView().setOnLongClickListener((View.OnLongClickListener)null);
            }
        }

    }

    public int getItemCount() {
        int footerCount = this.footerItemView == null?0:1;
        int headerCount = this.headerItemTypes != null?this.headerItemTypes.length:0;
        return this.datas.size() + footerCount + headerCount;
    }

    public ArrayList<T> getDatas() {
        return this.datas;
    }

    public T getData(int position) {
        return (T) this.datas.get(position);
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener() {
        return this.onItemLongClickListener;
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
