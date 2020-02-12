package com.example.administrator.shiyuji.sdk.http;

import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;

/**
 * Created by Administrator on 2019/7/8.
 */

public class TimelineHttpUtility extends HttpsUtility {

//    @Override
//    protected <T> T parseResponse(String resultStr, Class<T> responseCls) throws TaskException {
//        T result = super.parseResponse(resultStr, responseCls);
//
//        if (result != null) {
//            if (result instanceof StatusContents) {
//                StatusContents statusContents = (StatusContents) result;
//
//                // 解析普通网络链接、视频链接
//                VideoService.parseStatusURL(statusContents.getStatuses());
//            }
//            else if (result instanceof Favorities) {
//                Favorities favorities = (Favorities) result;
//                List<StatusContent> statusContents = new ArrayList<>();
//
//                for (Favority favority : favorities.getFavorites()) {
//                    statusContents.add(favority.getStatus());
//                }
//
//                // 解析普通网络链接、视频链接
//                VideoService.parseStatusURL(statusContents);
//            }
//        }
//
//        return result;
//    }
}
