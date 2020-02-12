package com.example.administrator.shiyuji.service.publisher;

import com.example.administrator.shiyuji.support.bean.PublishBean;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 发送内容队列
 * Created by Administrator on 2019/8/6.
 */

public class PublishQueue extends LinkedBlockingQueue<PublishBean> {

    private PublishQueueCallback callback;

    public PublishQueue(PublishQueueCallback callback) {
        this.callback = callback;
    }

    @Override
    public PublishBean poll() {
        PublishBean bean = super.poll();
        if(callback != null && bean != null)
            callback.onPublishPoll(bean);
        return bean;
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public boolean add(PublishBean e) {
        boolean r = super.add(e);
        if(r && callback != null)
            callback.onPublishAdd(e);
        return r;
    }

    @Override
    public PublishBean peek() {
        PublishBean bean = super.peek();
        if(bean != null && callback != null)
            callback.onPublishPeek(bean);
        return bean;
    }

    public interface PublishQueueCallback {

        public void onPublishPoll(PublishBean bean);

        public void onPublishAdd(PublishBean bean);

        public void onPublishPeek(PublishBean bean);

    }
}
