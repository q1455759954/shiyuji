package com.example.administrator.shiyuji.service.message;

import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.administrator.shiyuji.service.notifier.UnreadCountNotifier;
import com.example.administrator.shiyuji.support.bean.UnreadCount;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static com.example.administrator.shiyuji.support.setting.SettingUtility.getSetting;

/**
 * Created by Administrator on 2019/9/24.
 */

public class MessageClient extends WebSocketClient {

    private MessageClient client;
    private UserInfo userInfo;

    //通知
    public UnreadCountNotifier unreadCountNotifier;

    public MessageClient(URI serverUri, UserInfo userInfo, UnreadCountNotifier unreadCountNotifier) {
        super(serverUri);
        this.client=this;
        this.userInfo=userInfo;
        this.unreadCountNotifier=unreadCountNotifier;
    }

    /**
     * 服务端返回消息调用此方法
     * @param message
     */
    @Override
    public void onMessage(String message) {
        Log.d("看",message);
        UnreadCount unreadCount = JSON.parseObject(message,UnreadCount.class);
        if (unreadCount!=null){
//            AppContext.getAccount().setUnreadCount(unreadCount);
            unreadCountNotifier.notinfyUnreadCount(unreadCount);
        }
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        this.send("online_"+userInfo.getId());
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private static final long HEART_BEAT_RATE = 30 * 1000;//每隔60秒进行一次对长连接的心跳检测
    private long sendTime = 0L;
    // 发送心跳包
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
                try{
                    client.send("");//发送一个空消息给服务器，通过发送消息的成功失败来判断长连接的连接状态

                }catch (Exception e){
                    try {
                        mHandler.removeCallbacks(heartBeatRunnable);
                        client.close();
                        Setting action = getSetting("base_url");
                        String base_url = action.getValue().split("/")[2].split(":")[0];
                        new MessageClient(new URI("ws://"+base_url+":8887/"), userInfo, unreadCountNotifier).connect();//创建一个新的连接
                    } catch (URISyntaxException e1) {
                        e1.printStackTrace();
                    }
                }

                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };

}
