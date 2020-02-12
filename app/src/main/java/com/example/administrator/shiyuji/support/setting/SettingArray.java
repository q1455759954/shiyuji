package com.example.administrator.shiyuji.support.setting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/7/2.
 */
public class SettingArray extends SettingBean implements Serializable {
    private static final long serialVersionUID = 6482515166206579219L;
    private List<Setting> settingArray = new ArrayList();
    private int index;

    public SettingArray() {
    }

    public List<Setting> getSettingArray() {
        return this.settingArray;
    }

    public void setSettingArray(List<Setting> settingArray) {
        this.settingArray = settingArray;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}