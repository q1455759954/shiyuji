package com.example.administrator.shiyuji.sdk;

import com.example.administrator.shiyuji.support.bean.AccessToken;
import com.example.administrator.shiyuji.support.bean.Token;
import com.example.administrator.shiyuji.support.bean.UnreadCount;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.AppointmentInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.LifeInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.NotificationInfos;
import com.example.administrator.shiyuji.ui.fragment.bean.SearchInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComment;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusComments;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContent;
import com.example.administrator.shiyuji.ui.fragment.bean.StatusContents;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfo;
import com.example.administrator.shiyuji.ui.fragment.bean.UserInfos;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.sdk.base.ABizLogic;
import com.example.administrator.shiyuji.sdk.http.HttpConfig;
import com.example.administrator.shiyuji.sdk.http.TimelineHttpUtility;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.util.task.TaskException;

import java.io.File;

/**
 * sdk,规定哪个功能调用哪个方法
 * Created by Administrator on 2019/7/8.
 */

public class SinaSDK extends ABizLogic {

    private Token token;

    private SinaSDK( CacheMode cacheMode) {
        super(cacheMode);
    }

    public SinaSDK() {
    }

    public SinaSDK(CacheMode taskCacheMode, Token token) {
        super(taskCacheMode);
        this.token = token;
    }

    public static SinaSDK getInstance(Token token) {
        return new SinaSDK(token);
    }

    public static SinaSDK getInstance() {
        return new SinaSDK();
    }

    public static SinaSDK getInstance(CacheMode taskCacheMode,Token token) {
        return new SinaSDK(taskCacheMode,token);
    }

    public static SinaSDK getInstance(CacheMode taskCacheMode) {
        return new SinaSDK(taskCacheMode);
    }
    private SinaSDK(Token token) {
        super();
        this.token = token;
    }

    @Override
    protected HttpConfig configHttpConfig() {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.baseUrl = getSetting(BASE_URL).getValue();
        if (token != null) {
            httpConfig.addHeader("Authorization", token.getToken());
        }
        return httpConfig;
    }
    protected HttpConfig getHttpConfig() {
        return this.configHttpConfig();
    }

    private Params configParams(Params params) {
        if (params == null) {
            params = new Params();
        }

//        if (!params.containsKey("source"))
//            params.addParameter("source", getAppKey());
//        if (token != null)
//            params.addParameter("access_token", token.getToken());

        return params;
    }

    /**
     * 得到关注人的动态
     * @param params
     * @return
     * @throws TaskException
     */
    public StatusContents getAttendInfo(Params params) throws TaskException {
        if (!params.containsKey("count"))
            params.addParameter("count", String.valueOf(6));

        Setting action = getSetting("getAttendInfo");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), StatusContents.class);
    }
    /**
     * 更新用户信息
     * @param userInfo
     * @return
     * @throws TaskException
     */
    public UserInfo updateUserInfo(UserInfo userInfo) throws TaskException {
        return doPost(getHttpConfig(), getSetting("updateUserInfo"), null, null, userInfo, UserInfo.class);
    }


    /**
     * 得到用户的动态列表
     * @param params
     * @return
     * @throws TaskException
     */
    public StatusContents userTimeline(Params params) throws TaskException {
        if (!params.containsKey("count"))
            params.addParameter("count", String.valueOf(6));

        Setting action = getSetting("getUserTimeline");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), StatusContents.class);
    }

    /**
     *
     * @param since_id
     *            若指定此参数，则返回ID比since_id大的微博（即比since_id时间晚的微博），默认为0
     * @param max_id
     *            若指定此参数，则返回ID小于或等于max_id的微博，默认为0
     * @param count
     *            单页返回的记录条数，最大不超过200，默认为50
     * @return
     */
    public StatusContents friendshipGroupsTimeline(Params params) throws TaskException {
        if (!params.containsKey("count"))
            params.addParameter("count", String.valueOf(6));

        Setting action = getSetting("friendshipGroupsTimeline");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), StatusContents.class);
    }

    /**
     * 根据动态row_key获取评论
     * @param params
     * @return
     */
    public StatusComments commentsHotShow(Params params) throws TaskException {
        Setting action = getSetting("timelineComments");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), StatusComments.class);
    }

    /**
     * 根据评论row_key获取回复
     * @param params
     * @return
     */
    public StatusComments getResponseByRowkey(Params params) throws TaskException {
        Setting action = getSetting("timelineResponse");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), StatusComments.class);
    }


    /**
     * 发布一条微博信息
     * @param
     *            (true) 要发布的微博消息文本内容
     * @param params
     * @return
     */
    public StatusContent statusesUpdate(StatusContent params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("statusesUpdate"), null, null, params, StatusContent.class);
    }

    /**
     * 登录
     * @return
     * @param
     */
    public AccessToken login(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("login"), null, params, null, AccessToken.class);
    }

    /**
     * 得到用户信息
     * @param uid
     * @return
     */
    public UserInfo getUserInfo(String uid) throws TaskException {
        Params params = new Params();
        params.addParameter("userId",uid);
        UserInfo user = doGet(getSetting("getUserInfo"), params, UserInfo.class);
        return user;
    }

    /**
     * 改变关注状态
     * @param params
     */
    public String makeAttends(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("makeAttends"), null, params, null, String.class);
    }

    /**
     * 改变点赞状态
     * @param likeId
     * @param like
     * @return
     */
    public String makeLike(String likeId, boolean like) throws TaskException {
        Params p = new Params();
        p.addParameter("row_key",likeId);
        p.addParameter("type", String.valueOf(like));
        return doPost(getHttpConfig(), getSetting("makeLike"), null, p, null, String.class);
    }

    /**
     * 改变收藏状态
     * @param likeId
     * @param like
     * @return
     */
    public String makeCollection(String likeId, boolean like) throws TaskException {
        Params p = new Params();
        p.addParameter("row_key",likeId);
        p.addParameter("type", String.valueOf(like));
        return doPost(getHttpConfig(), getSetting("makeCollection"), null, p, null, String.class);
    }
    /**
     * 未读消息
     * @return
     */
    public UnreadCount remindUnread() throws TaskException {
        return doPost(getHttpConfig(), getSetting("remindUnread"), null, null, null, UnreadCount.class);
    }

    /**
     * 得到约信息
     * @param params
     * @return
     */
    public AppointmentInfos getAppointment(Params params) throws TaskException {
        return doGet(getSetting("getAppointment"), configParams(params), AppointmentInfos.class);
    }

    /**
     * 发布约信息
     * @param info
     * @return
     */
    public AppointmentInfo publishAppointment(AppointmentInfo info) throws TaskException {
        return doPost(getHttpConfig(), getSetting("publishAppointment"), null, null, info, AppointmentInfo.class);
    }


    /**
     * 发布评论
     * @param statusComment
     * @return
     */
    public StatusComment publishComments(StatusComment statusComment)  throws TaskException{
        return doPost(getHttpConfig(), getSetting("publishComments"), null, null, statusComment, StatusComment.class);
    }

    /**
     * 得到约信息
     * @param params
     * @return
     */
    public NotificationInfos getNotification(Params params) throws TaskException {
        return doGet(getSetting("getNotification"), configParams(params), NotificationInfos.class);
    }

    public LifeInfos getLifeInfo(Params params) throws TaskException {
        return doGet(getSetting("getLifeInfo"), configParams(params), LifeInfos.class);
    }

    /**
     * 得到搜索的下一页
     * @param params
     * @return
     */
    public SearchInfo getNextPage(Params params) throws TaskException {
        return doGet(getSetting("getNextPage"), configParams(params), SearchInfo.class);
    }

    /**
     * 得到搜索
     * @param queryStr
     * @return
     */
    public SearchInfo getSearchInfo(String queryStr) throws TaskException {
        Params p = new Params();
        p.addParameter("queryStr",queryStr);
        return doGet(getSetting("getSearchInfo"), configParams(p), SearchInfo.class);

    }

    /**
     * 上传一张图片<br/>
     *
     * @param file
     * @return
     * @throws TaskException
     */
    public String uploadPicture(File file) throws TaskException {
        Params params = new Params();

        MultipartFile[] files = file == null ? null : new MultipartFile[] { new MultipartFile("image/jpge", "pic", file) };

        return doPostFiles(configHttpConfig(), getSetting("publishUploadPicture"), null, configParams(params), files, String.class);
    }

    /**
     * 发布动态
     * @param statusContent
     * @return
     */
    public StatusContent publishStatus(StatusContent statusContent) throws TaskException {
        return doPost(getHttpConfig(), getSetting("publishstatus"), null, null, statusContent, StatusContent.class);

    }

    /**
     * 发布生活信息
     * @param lifeInfo
     * @return
     */
    public LifeInfo publishLifeInfo(LifeInfo lifeInfo) throws TaskException {
        return doPost(getHttpConfig(), getSetting("publishLifeInfo"), null, null, lifeInfo, LifeInfo.class);
    }

    /**
     * 删除
     * @param params
     * @return
     */
    public String delete(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("delete"), null, params, null, String.class);
    }

    /**
     * 下架
     * @param p
     * @return
     */
    public String soldOut(Params p) throws TaskException {
        return doPost(getHttpConfig(), getSetting("soldOut"), null, p, null, String.class);
    }

    /**
     * 得到用户的约信息
     * @param params
     * @return
     */
    public AppointmentInfos getUserAppointment(Params params) throws TaskException {
        Setting action = getSetting("getUserAppointment");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), AppointmentInfos.class);
    }

    /**
     * 得到用户的生活信息
     * @param params
     * @return
     */
    public LifeInfos getUserLifeInfo(Params params) throws TaskException {
        Setting action = getSetting("getUserLifeInfo");
        action.getExtras().put(HTTP_UTILITY, newSettingExtra(HTTP_UTILITY, TimelineHttpUtility.class.getName(), ""));
        return doGet(action, configParams(params), LifeInfos.class);
    }

    /**
     * 举报
     * @param p
     * @return
     */
    public String report(Params p) throws TaskException {
        return doPost(getHttpConfig(), getSetting("report"), null, p, null, String.class);
    }

    /**
     * 得到关注人或粉丝信息
     * @param params
     * @return
     */
    public UserInfos getUserInfos(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("getUserInfos"), null, params, null, UserInfos.class);
    }

    /**
     * 得到收藏
     * @param params
     * @return
     */
    public StatusContents getCollection(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("getCollection"), null, params, null, StatusContents.class);
    }

    /**
     * 得到我的赞
     * @param params
     * @return
     */
    public StatusContents getMyLike(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("getMyLike"), null, params, null, StatusContents.class);
    }

    /**
     * 得到点赞动态的用户
     * @param params
     * @return
     */
    public UserInfos getLikeUsers(Params params) throws TaskException {
        return doPost(getHttpConfig(), getSetting("getLikeUsers"), null, params, null, UserInfos.class);

    }
}
