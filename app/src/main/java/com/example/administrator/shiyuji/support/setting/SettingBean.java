package com.example.administrator.shiyuji.support.setting;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/7/2.
 */

class SettingBean implements Serializable {
    private static final long serialVersionUID = -3694407301270573142L;
    private String description;
    private String type;
    private String value;

    SettingBean() {
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
