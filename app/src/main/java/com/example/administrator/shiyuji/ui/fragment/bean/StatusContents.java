package com.example.administrator.shiyuji.ui.fragment.bean;


import com.example.administrator.shiyuji.sdk.cache.ResultBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StatusContents extends ResultBean implements Serializable {

	private List<StatusContent> statuses;

	private String type;

	private int total_number;

	private String since_id;// 适配热门微博

	public List<StatusContent> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<StatusContent> statuses) {
		this.statuses = statuses;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTotal_number() {
		return total_number;
	}

	public void setTotal_number(int total_number) {
		this.total_number = total_number;
	}

	public String getSince_id() {
		return since_id;
	}

	public void setSince_id(String since_id) {
		this.since_id = since_id;
	}
}
