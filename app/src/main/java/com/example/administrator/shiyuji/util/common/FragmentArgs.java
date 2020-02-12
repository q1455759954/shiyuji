package com.example.administrator.shiyuji.util.common;

import android.os.Bundle;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2019/7/5.
 */

public class FragmentArgs implements Serializable {
    private static final long serialVersionUID = 5526514482404853100L;
    private Map<String, Serializable> values = new HashMap();

    public FragmentArgs() {
    }

    public FragmentArgs add(String key, Serializable value) {
        if(!TextUtils.isEmpty(key) && value != null) {
            this.values.put(key, value);
        }

        return this;
    }

    public Serializable get(String key) {
        return (Serializable)this.values.get(key);
    }

    public static void setToBundle(Bundle bundle, FragmentArgs args) {
        Set keys = args.values.keySet();
        Iterator var3 = keys.iterator();

        while(var3.hasNext()) {
            String key = (String)var3.next();
            Serializable value = args.get(key);
            if(value != null) {
                bundle.putSerializable(key, value);
            }
        }

    }

    public static FragmentArgs transToArgs(Bundle bundle) {
        FragmentArgs args = new FragmentArgs();
        Iterator var2 = bundle.keySet().iterator();

        while(var2.hasNext()) {
            String s = (String)var2.next();
            Object o = bundle.get(s);
            if(o != null) {
                args.add(s, (Serializable)o);
            }
        }

        return args;
    }

    public static Bundle transToBundle(FragmentArgs args) {
        Bundle bundle = new Bundle();
        setToBundle(bundle, args);
        return bundle;
    }
}

