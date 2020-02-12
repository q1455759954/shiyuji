package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/10/18.
 */

public class LifeInfos implements Serializable {

    private List<LifeInfo> lifeInfos;


    public LifeInfos() {
    }

    public List<LifeInfo> getLifeInfos() {
        return lifeInfos;
    }

    public void setLifeInfos(List<LifeInfo> lifeInfos) {
        this.lifeInfos = lifeInfos;
    }
}
