package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeInfo implements Serializable {

    private Commodity commodity;

    private WorkInfo workInfo;

    public LifeInfo() {
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public WorkInfo getWorkInfo() {
        return workInfo;
    }

    public void setWorkInfo(WorkInfo workInfo) {
        this.workInfo = workInfo;
    }
}
