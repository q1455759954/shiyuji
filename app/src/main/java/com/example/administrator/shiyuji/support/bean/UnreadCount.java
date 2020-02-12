package com.example.administrator.shiyuji.support.bean;


import com.example.administrator.shiyuji.base.AppContext;
import com.example.administrator.shiyuji.util.annotation.PrimaryKey;

import java.io.Serializable;
import java.util.UUID;

/**
 * 用户的各种消息未读数
 * 
 * @author wangdan
 * 
 */
public class UnreadCount implements Serializable {

	private static final long serialVersionUID = 3633461417848980465L;

	@PrimaryKey(column = "id")
	private String id = String.valueOf(AppContext.getAccount().getUserInfo().getId());

	/**
	 * 会话未读数
	 */
	private int chat;

	/**
	 * 新微博未读数
	 */
	private int status;

	/**
	 * 新粉丝数
	 */
	private int fans;

	/**
	 * 新评论数
	 */
	private int comment_num;

	/**
	 * 新私信数
	 */
	private int dm;// privateMessage

	/**
	 * 新点赞数
	 */
	private int like_num;

	/**
	 * 新提及我的微博数
	 */
	private int mention_status;

	/**
	 * 新提及我的评论数
	 */
	private int mention_cmt;

	public int getChat() {
		return chat;
	}

	public void setChat(int chat) {
		this.chat = chat;
	}

	public int getStatus() {
		return status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getFans() {
		return fans;
	}

	public void setFans(int fans) {
		this.fans = fans;
	}

	public int getComment_num() {
		return comment_num;
	}

	public void setComment_num(int comment_num) {
		this.comment_num = comment_num;
	}

	public int getDm() {
		return dm;
	}

	public void setDm(int dm) {
		this.dm = dm;
	}

	public int getLike_num() {
		return like_num;
	}

	public void setLike_num(int like_num) {
		this.like_num = like_num;
	}

	public int getMention_status() {
		return mention_status;
	}

	public void setMention_status(int mention_status) {
		this.mention_status = mention_status;
	}

	public int getMention_cmt() {
		return mention_cmt;
	}

	public void setMention_cmt(int mention_cmt) {
		this.mention_cmt = mention_cmt;
	}
	
}
