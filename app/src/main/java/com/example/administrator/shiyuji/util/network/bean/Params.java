package com.example.administrator.shiyuji.util.network.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/7/8.
 */

public class Params implements Serializable {
    private Map<String, String> mParameters = new HashMap();
    private List<String> mKeys = new ArrayList();
    private boolean encodeAble = true;

    public Params() {
    }

    public Params(String[] keys, String[] values) {
        for(int i = 0; i < keys.length; ++i) {
            String key = keys[i];
            this.mKeys.add(key);
            this.mParameters.put(key, values[i]);
        }

    }

    public Params(String key, String value) {
        this.mKeys.add(key);
        this.mParameters.put(key, value);
    }

    public int size() {
        return this.mKeys.size();
    }

    public boolean containsKey(String key) {
        return this.mKeys.contains(key);
    }

    public List<String> getKeys() {
        return this.mKeys;
    }

    public void addParameter(String key, String value) {
        if(!this.mKeys.contains(key)) {
            this.mKeys.add(key);
        }

        this.mParameters.put(key, value);
    }

    public String getParameter(String key) {
        return (String)this.mParameters.get(key);
    }

    public Map<String, String> getVaules() {
        return this.mParameters;
    }

    public void remove(String key) {
        if(this.mKeys.contains(key)) {
            this.mKeys.remove(key);
            this.mParameters.remove(key);
        }

    }

    public void addParams(Params params) {
        String key;
        for(Iterator var2 = params.getKeys().iterator(); var2.hasNext(); this.mParameters.put(key, params.getParameter(key))) {
            key = (String)var2.next();
            if(!this.mKeys.contains(key)) {
                this.mKeys.add(key);
            }
        }

    }

    public boolean isEncodeAble() {
        return this.encodeAble;
    }

    public void setEncodeAble(boolean encodeAble) {
        this.encodeAble = encodeAble;
    }

    public void clearParams() {
        this.mParameters.clear();
        this.mKeys.clear();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator var2 = this.mKeys.iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            sb.append(key).append("=").append(this.getParameter(key)).append(",");
        }

        return sb.toString();
    }
}

