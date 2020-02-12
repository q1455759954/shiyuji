package com.example.administrator.shiyuji.sdk.base;

import com.example.administrator.shiyuji.util.network.bean.Params;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

/**
 * Created by Administrator on 2019/7/8.
 */


public class ParamsUtil {
    public ParamsUtil() {
    }

    public static char[] base64Encode(byte[] data) {
        char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        char[] out = new char[(data.length + 2) / 3 * 4];
        int i = 0;

        for(int index = 0; i < data.length; index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = 255 & data[i];
            val <<= 8;
            if(i + 1 < data.length) {
                val |= 255 & data[i + 1];
                trip = true;
            }

            val <<= 8;
            if(i + 2 < data.length) {
                val |= 255 & data[i + 2];
                quad = true;
            }

            out[index + 3] = alphabet[quad?val & 63:64];
            val >>= 6;
            out[index + 2] = alphabet[trip?val & 63:64];
            val >>= 6;
            out[index + 1] = alphabet[val & 63];
            val >>= 6;
            out[index + 0] = alphabet[val & 63];
            i += 3;
        }

        return out;
    }

    public static String appendParams(Params params) {
        StringBuffer paramsBuffer = new StringBuffer();
        Iterator var2 = params.getKeys().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            if(paramsBuffer.length() != 0) {
                paramsBuffer.append(",");
            }

            paramsBuffer.append(key + "=");

            try {
                paramsBuffer.append("\"" + encode(params.getParameter(key)) + "\"");
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return paramsBuffer.toString();
    }

    public static String encodeParams(Params params, String splitStr, boolean encode) {
        StringBuffer paramsBuffer = new StringBuffer();
        Iterator var4 = params.getKeys().iterator();

        while(true) {
            String key;
            do {
                if(!var4.hasNext()) {
                    return paramsBuffer.toString();
                }

                key = (String)var4.next();
            } while(params.getParameter(key) == null);

            if(paramsBuffer.length() != 0) {
                paramsBuffer.append(splitStr);
            }

            paramsBuffer.append(key + "=");
            paramsBuffer.append(encode && params.isEncodeAble()?encode(params.getParameter(key)):params.getParameter(key));
        }
    }

    public static String encodeToURLParams(Params params) {
        return encodeParams(params, "&", true);
    }

    public static String encodeParamsToJson(Params params) {
        JSONObject json = new JSONObject();
        Iterator var2 = params.getKeys().iterator();

        while(var2.hasNext()) {
            String key = (String)var2.next();
            if(params.getParameter(key) != null) {
                try {
                    json.put(key, encode(params.getParameter(key)));
                } catch (JSONException var5) {
                    var5.printStackTrace();
                }
            }
        }

        return json.toString();
    }

    public static String encode(String value) {
        if(value == null) {
            return "";
        } else {
            String encoded = null;

            try {
                encoded = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException var5) {
                ;
            }

            StringBuffer buf = new StringBuffer(encoded.length());

            for(int i = 0; i < encoded.length(); ++i) {
                char focus = encoded.charAt(i);
                if(focus == 42) {
                    buf.append("%2A");
                } else if(focus == 43) {
                    buf.append("%20");
                } else if(focus == 37 && i + 1 < encoded.length() && encoded.charAt(i + 1) == 55 && encoded.charAt(i + 2) == 69) {
                    buf.append('~');
                    i += 2;
                } else {
                    buf.append(focus);
                }
            }

            return buf.toString();
        }
    }

    public static Params deCodeUrl(String content) {
        Params params = new Params();

        try {
            String decodeSource = "";
            if(content.indexOf("?") != -1) {
                decodeSource = content.substring(content.indexOf("?") + 1, content.length());
            } else {
                decodeSource = content;
            }

            String[] decodeParams = decodeSource.split("&");
            String[] var4 = decodeParams;
            int var5 = decodeParams.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String keyValues = var4[var6];
                String[] keyValue = keyValues.split("=");
                params.addParameter(keyValue[0], keyValue[1]);
            }
        } catch (Exception var9) {
            ;
        }

        return params;
    }

    public static String encodeUrl(String url, Params params) {
        StringBuffer urlBuffer = new StringBuffer();
        urlBuffer.append(url + "?");
        urlBuffer.append(encodeParams(params, "&", true));
        return urlBuffer.toString();
    }
}

