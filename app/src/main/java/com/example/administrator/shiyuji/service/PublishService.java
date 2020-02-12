package com.example.administrator.shiyuji.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.service.publisher.InterfacePublisher;
import com.example.administrator.shiyuji.service.publisher.PublishManager;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.support.sqlit.PublishDB;

/**
 * Created by Administrator on 2019/7/10.
 */

public class PublishService extends Service implements InterfacePublisher {

    public static void publish(Context context, PublishBean bean) {
        Intent intent = new Intent(context, PublishService.class);
        intent.setAction("PUBLISH");
        intent.putExtra("data", bean);
        context.startService(intent);
    }

    private PublishManager publishManager;
    private PublishService publisher;
    private PublishBinder binder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (publisher == null)
            publisher = this;
        if (binder == null)
            binder = new PublishBinder();
        if (publishManager == null)
            publishManager = new PublishManager(this, AppContext.getAccount());

        if (intent != null) {
            if ("PUBLISH".equals(intent.getAction())) {
                PublishBean data = (PublishBean) intent.getSerializableExtra("data");

                if (data != null)
                    publish(data);
            }
            else if ("Cancel".equals(intent.getAction())) {
                publishManager.cancelPublish();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void publish(PublishBean data) {
        publishManager.onPublish(data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        publishManager.stop();
    }


    public class PublishBinder extends Binder {

        public InterfacePublisher getPublisher() {
            return publisher;
        }

    }
}
