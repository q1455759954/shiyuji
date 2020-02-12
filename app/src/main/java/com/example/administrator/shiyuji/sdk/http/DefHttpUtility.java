package com.example.administrator.shiyuji.sdk.http;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.example.administrator.shiyuji.util.common.GlobalContext;
import com.example.administrator.shiyuji.util.network.bean.Params;
import com.example.administrator.shiyuji.sdk.base.ParamsUtil;
import com.example.administrator.shiyuji.support.setting.Setting;
import com.example.administrator.shiyuji.util.task.TaskException;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Request.Builder;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Set;

import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * 具体的http获取数据实现
 * Created by Administrator on 2019/7/8.
 */

public class DefHttpUtility implements IHttpUtility {

    public DefHttpUtility() {
    }

    @Override
    public <T> T doGet(HttpConfig config, Setting action, Params urlParams, Class<T> responseCls) throws TaskException {
        Builder builder = this.createRequestBuilder(config, action, urlParams, "Get");
        Request request = builder.build();
        return this.executeRequest(request, responseCls, action, "Get");
    }

    public <T> T doPost(HttpConfig config, Setting action, Params urlParams, Params bodyParams, Object requestObj, Class<T> responseCls) throws TaskException {
        Builder builder = this.createRequestBuilder(config, action, urlParams, "Post");
        String requestBodyStr;
        if(bodyParams != null) {
            requestBodyStr = ParamsUtil.encodeToURLParams(bodyParams);
            builder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), requestBodyStr));
        } else if(requestObj != null) {
            if(requestObj instanceof String) {
                requestBodyStr = requestObj + "";
            } else {
                requestBodyStr = JSON.toJSONString(requestObj);
            }
            try {
                builder.post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"),URLEncoder.encode(requestBodyStr,"utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        return this.executeRequest(builder.build(), responseCls, action, "Post");
    }

    public <T> T doPostFiles(HttpConfig config, Setting action, Params urlParams, Params bodyParams, MultipartFile[] files, Class<T> responseCls) throws TaskException {
        String method = "doPostFiles";
        Builder builder = this.createRequestBuilder(config, action, urlParams, method);
        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);
        if(bodyParams != null && bodyParams.getKeys().size() > 0) {
            Iterator requestBody = bodyParams.getKeys().iterator();

            while(requestBody.hasNext()) {
                String key = (String)requestBody.next();
                String value = bodyParams.getParameter(key);
                multipartBuilder.addFormDataPart(key, value);
            }
        }

        if(files != null && files.length > 0) {
            MultipartFile[] var14 = files;
            int var16 = files.length;

            for(int var17 = 0; var17 < var16; ++var17) {
                MultipartFile file = var14[var17];
                if(file.getBytes() != null) {
                    multipartBuilder.addFormDataPart(file.getKey(), file.getKey(), createRequestBody(file));
                } else if(file.getFile() != null) {
                    multipartBuilder.addFormDataPart(file.getKey(), file.getFile().getName(), createRequestBody(file));
                }
            }
        }

        RequestBody var15 = multipartBuilder.build();
        builder.post(var15);
        return this.executeRequest(builder.build(), responseCls, action, method);
    }

    /**
     * 执行查询
     * @param request
     * @param responseCls
     * @param action
     * @param method
     * @param <T>
     * @return
     * @throws TaskException
     */
    private <T> T executeRequest(Request request, Class<T> responseCls, Setting action, String method) throws TaskException {

        try {
            Response e = this.getOkHttpClient().newCall(request).execute();
            String responseStr;
            if(e.code() != 200 && e.code() != 206) {
                responseStr = e.body().string();
                TaskException.checkResponse(responseStr);
                throw new TaskException(TaskException.TaskError.timeout.toString());
            } else {
                responseStr = e.body().string();
                return this.parseResponse(responseStr, responseCls);
            }
        } catch (SocketTimeoutException var7) {
            throw new TaskException(TaskException.TaskError.timeout.toString());
        } catch (IOException var8) {
            throw new TaskException(TaskException.TaskError.timeout.toString());
        } catch (TaskException var9) {
            throw var9;
        } catch (Exception var10) {
            throw new TaskException(TaskException.TaskError.resultIllegal.toString());
        }
    }

    /**
     * 将string类型的结果转化为对象
     * @param resultStr
     * @param responseCls
     * @param <T>
     * @return
     * @throws TaskException
     */
    protected <T> T parseResponse(String resultStr, Class<T> responseCls) throws TaskException {
        if(responseCls.getSimpleName().equals("String")) {
            return (T) resultStr;
        } else {
            Object result = JSON.parseObject(resultStr, responseCls);
            return (T) result;
        }
    }

    /**
     * 得到http连接
     * @return
     */
    public synchronized OkHttpClient getOkHttpClient() {
        return GlobalContext.getOkHttpClient();
    }

    /**
     * 构造请求头
     * @param config
     * @param action
     * @param urlParams
     * @param method
     * @return
     * @throws TaskException
     */
    private Builder createRequestBuilder(HttpConfig config, Setting action, Params urlParams, String method) throws TaskException {
        if(false) {
//            Logger.w(getTag(action, method), "没有网络连接");
            throw new TaskException(TaskException.TaskError.noneNetwork.toString());
        } else {
            String url = (config.baseUrl + action.getValue() + (urlParams == null?"":"?" + ParamsUtil.encodeToURLParams(urlParams))).replaceAll(" ", "");
//            Logger.d(getTag(action, method), url);
            Builder builder = new Builder();
            builder.url(url);
            if(!TextUtils.isEmpty(config.cookie)) {
                builder.header("Cookie", config.cookie);
            }

            if(config.headerMap.size() > 0) {
                Set keySet = config.headerMap.keySet();
                Iterator it = keySet.iterator();

                while(it.hasNext()) {
                    String key = (String)it.next();
                    builder.addHeader(key, (String)config.headerMap.get(key));
                }
            }

            return builder;
        }
    }

    static RequestBody createRequestBody(final MultipartFile file) {
        return new RequestBody() {
            public MediaType contentType() {
                return MediaType.parse(file.getContentType());
            }

            public long contentLength() throws IOException {
                return file.getBytes() != null?(long)file.getBytes().length:file.getFile().length();
            }

            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                if(file.getFile() != null) {
                    source = Okio.source(file.getFile());
                } else {
                    source = Okio.source(new ByteArrayInputStream(file.getBytes()));
                }

                OnFileProgress onFileProgress = file.getOnProgress();
                if(onFileProgress != null) {
                    try {
                        long e = this.contentLength();
                        long writeLen = 0L;
                        long readLen = -1L;
                        Buffer buffer = new Buffer();
                        long MIN_PROGRESS_STEP = 65536L;
                        long MIN_PROGRESS_TIME = 300L;
                        long mLastUpdateBytes = 0L;
                        long mLastUpdateTime = 0L;

                        while(true) {
                            long now;
                            do {
                                if((readLen = source.read(buffer, 8192L)) == -1L) {
                                    return;
                                }

                                sink.write(buffer, readLen);
                                writeLen += readLen;
                                now = System.currentTimeMillis();
                            } while((writeLen - mLastUpdateBytes <= MIN_PROGRESS_STEP || now - mLastUpdateTime <= MIN_PROGRESS_TIME) && writeLen != e);

                            onFileProgress.onProgress(writeLen, e);
                            mLastUpdateBytes = writeLen;
                            mLastUpdateTime = now;
                        }
                    } catch (IOException var33) {
                        throw var33;
                    } finally {
                        Util.closeQuietly(source);
                    }
                } else {
                    try {
                        sink.writeAll(source);
                    } catch (IOException var31) {
                        throw var31;
                    } finally {
                        Util.closeQuietly(source);
                    }
                }

            }
        };
    }

}
