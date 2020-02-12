package com.example.administrator.shiyuji.support.bean;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * 授权成功后返回给客户端的token类
 */
public class AccessToken extends Token implements Serializable {

	private static final long serialVersionUID = 1L;

	private String verifier;
	//用户id
	private String uid;
	//token
	private String access_token;
    //过期时间（时长，不是特定时间）
	private long expires_in;

    private String appKey;

    private String appScreet;

    @Override
    public String toString() {
        return "AccessToken{" +
                "verifier='" + verifier + '\'' +
                ", uid='" + uid + '\'' +
                ", access_token='" + access_token + '\'' +
                ", expires_in=" + expires_in +
                ", appKey='" + appKey + '\'' +
                ", appScreet='" + appScreet + '\'' +
                ", create_at=" + create_at +
                '}';
    }

    private long create_at = System.currentTimeMillis();

	public String getVerifier() {
		return verifier;
	}

	public void setVerifier(String verifier) {
		this.verifier = verifier;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Override
	public String getToken() {
		return getAccess_token();
	}
	
	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
		setToken(access_token);
	}

    public long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(long expires_in) {
        this.expires_in = expires_in;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }

    public boolean isExpired() {
        String days = String.valueOf(TimeUnit.SECONDS.toDays(getExpires_in()));

        return System.currentTimeMillis() - create_at >= expires_in * 1000;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppScreet() {
        return appScreet;
    }

    public void setAppScreet(String appScreet) {
        this.appScreet = appScreet;
    }
}
