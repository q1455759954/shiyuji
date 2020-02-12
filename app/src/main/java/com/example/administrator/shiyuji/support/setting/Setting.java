package com.example.administrator.shiyuji.support.setting;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/2.
 */
public class Setting extends SettingBean implements Serializable {
    private static final long serialVersionUID = 4801654811733634325L;
    private Map<String, SettingExtra> extras = new HashMap();

    public Setting() {
    }

    public Map<String, SettingExtra> getExtras() {
        return this.extras;
    }

    public void setExtras(Map<String, SettingExtra> extras) {
        this.extras = extras;
    }

    public Setting copy() {
        return (Setting) JSON.parseObject(JSON.toJSONString(this), Setting.class);
    }
}