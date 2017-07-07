package com.mlo.entity;

import java.io.Serializable;

public class Page implements Serializable{
	
	private Integer id ;
	private String plan ;
	private String url ;
	private String count;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Page [id=" + id + ", plan=" + plan + ", url=" + url + ", count=" + count + "]";
	}
	

}
