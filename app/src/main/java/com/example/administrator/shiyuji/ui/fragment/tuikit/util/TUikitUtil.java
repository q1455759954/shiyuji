package com.example.administrator.shiyuji.ui.fragment.tuikit.util;

import android.util.Log;
import android.view.View;

import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.service.publisher.PublishManager;
import com.example.administrator.shiyuji.support.bean.PublishBean;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.detail.AppointmentDetailFragment;
import com.example.administrator.shiyuji.ui.fragment.mainFragment.appointment.item.AppointmentItemView;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMGroupAddOpt;
import com.tencent.imsdk.TIMGroupManager;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfo;
import com.tencent.imsdk.ext.group.TIMGroupDetailInfoResult;
import com.tencent.qcloud.tim.uikit.base.IUIKitCallBack;
import com.tencent.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.tencent.qcloud.tim.uikit.utils.TUIKitLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/10/10.
 */

public class TUikitUtil {

    /**
     * 加入群组
     * @param groupId
     * @param reason
     * @return
     */
    public static void applyJoinGroup( String groupId, String reason, final AppointmentDetailFragment callback){
        final String[] result = new String[1];
        TIMGroupManager.getInstance().applyJoinGroup(groupId, reason, new TIMCallBack() {
            @java.lang.Override
            public void onError(int code, String desc) {
                //接口返回了错误码 code 和错误描述 desc，可用于原因
                //错误码 code 列表请参见错误码表
                Log.d("TUikitUtil",desc);
                callback.parseResult(false);
            }

            @java.lang.Override
            public void onSuccess() {
                callback.parseResult(true);
            }
        });
    }

    /**
     * 判断是否已加入群组
     * @param id
     * @param callback
     * @return
     */
    public static void getGroupMembers(final String groupId, final String id, final AppointmentDetailFragment callback){

        //创建回调
        TIMGroupManager.getInstance().getGroupMembers(groupId, new TIMValueCallBack<List<TIMGroupMemberInfo>>() {

            @Override
            public void onError(int code, String desc) {
                callback.applyJoinGroup(groupId,false);
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> timGroupMemberInfos) {
                for(TIMGroupMemberInfo info : timGroupMemberInfos) {
                    Log.d("看成员id", String.valueOf(info.getTinyId()));
                    if (String.valueOf(info.getUser()).equals(id)) {
                        callback.applyJoinGroup(groupId,true);
                        break;
                    }
                }
            }
        });

    }

    /**
     * 得到群人数
     * @param id
     * @param callback
     * @return
     */
    public static void getGroupNum(final String groupId, final String id, final AppointmentItemView callback){

        List<String> list = new ArrayList<>();
        list.add(groupId);
        //创建回调
        TIMGroupManager.getInstance().getGroupInfo(list, new TIMValueCallBack<List<TIMGroupDetailInfoResult>>() {
            @Override
            public void onError(int code, String desc) {
                //错误码 code 和错误描述 desc，可用于定位请求失败原因
                //错误码 code 列表请参见错误码表
            }

            @Override
            public void onSuccess(List<TIMGroupDetailInfoResult> infoList) { //参数中返回群组信息列表
                for(TIMGroupDetailInfo info : infoList) {
                    callback.setAddInfo((int) info.getMemberNum()-1==0?0:(int) info.getMemberNum()-1);
                }
            }
        });

    }

    /**
     * 创建群组
     * @return
     * @param
     * @param bean
     */
    public static void createGroup(final PublishManager calback, final PublishBean bean){

        final String[] result = new String[1];
        TIMGroupManager.CreateGroupParam param = new TIMGroupManager.CreateGroupParam("Public", AppContext.getAccount().getUserInfo().getNickname()+"的群组");
        param.setAddOption(TIMGroupAddOpt.TIM_GROUP_ADD_ANY);
        param.setMaxMemberNum(10);
        TIMValueCallBack<String> cb = new TIMValueCallBack<String>() {
            @Override
            public void onError(int i, String s) {
                calback.parseAppointment(false,bean,null);
            }

            @Override
            public void onSuccess(String s) {
                calback.parseAppointment(true,bean,s);

            }
        };
        TIMGroupManager.getInstance().createGroup(param,cb);
    }

}
