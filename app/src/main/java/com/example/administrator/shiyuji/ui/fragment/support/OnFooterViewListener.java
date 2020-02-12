package com.example.administrator.shiyuji.ui.fragment.support;

import com.example.administrator.shiyuji.ui.fragment.base.ABaseFragment;
import com.example.administrator.shiyuji.ui.fragment.base.APagingFragment;
import com.example.administrator.shiyuji.util.task.TaskException;

/**
 * Created by Administrator on 2019/6/29.
 */

public interface OnFooterViewListener {
    void onTaskStateChanged(AFooterItemView<?> var1, ABaseFragment.ABaseTaskState var2, TaskException var3, APagingFragment.RefreshMode var4);

    void setFooterViewToRefreshing();
}

