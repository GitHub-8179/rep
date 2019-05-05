package com.zzkj.reptile.entity;

import org.springframework.beans.factory.annotation.Value;

public class IpPostEntity {

	private String ip;
	private Integer post;
	private Integer state;
	private int serverNum ;
	private int remainderNum;

	public int getRemainderNum() {
		return remainderNum;
	}
	public void setRemainderNum(int remainderNum) {
		this.remainderNum = remainderNum;
	}
	public int getServerNum() {
		return serverNum;
	}
	public void setServerNum(int serverNum) {
		this.serverNum = serverNum;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPost() {
		return post;
	}
	public void setPost(Integer post) {
		this.post = post;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}

	public IpPostEntity() {
	}

	public IpPostEntity(int serverNum, int remainderNum) {
		this.serverNum = serverNum;
		this.remainderNum = remainderNum;
	}
}
