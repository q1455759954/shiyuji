package com.example.administrator.shiyuji.sdk.cache;

/**
 * Created by Administrator on 2019/7/8.
 */

public class Extra {
    private String owner;
    private String key;

    public Extra() {
    }

    public Extra(String owner, String key) {
        this.key = key;
        this.owner = owner;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
