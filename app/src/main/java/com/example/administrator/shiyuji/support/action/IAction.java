package com.example.administrator.shiyuji.support.action;

/**
 * Created by Administrator on 2019/8/3.
 */


import android.app.Activity;

public abstract class IAction {
    private Activity context;
    private IAction parent;
    private IAction child;

    public IAction(Activity context, IAction parent) {
        this.context = context;
        this.parent = parent;
        if(parent != null) {
            parent.setChild(this);
        }

    }

    protected boolean interrupt() {
        return false;
    }

    public void doInterrupt() {
    }

    public void run() {
        if(this.parent == null || !this.parent.interrupt()) {
            this.doAction();
        }

    }

    protected final void setChild(IAction child) {
        this.child = child;
    }

    public final IAction getChild() {
        return this.child;
    }

    public final IAction getParent() {
        return this.parent;
    }

    protected final Activity getContext() {
        return this.context;
    }

    public void doAction() {
        if(this.getChild() != null) {
            this.getChild().run();
        }

    }
}

