package com.mlo.entity;

import java.io.Serializable;

public class VisitorRecord implements Serializable{
	private Integer id ;
	private String inTime;
	private Long longTime;
	private String city;
	private String related;
	private String url;
	private String plan;
	private String pic;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getInTime() {
		return inTime;
	}
	public void setInTime(String inTime) {
		this.inTime = inTime;
	}
	public Long getLongTime() {
		return longTime;
	}
	public void setLongTime(Long longTime) {
		this.longTime = longTime;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRelated() {
		return related;
	}
	public void setRelated(String related) {
		this.related = related;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	
	public String getPic() {
		return pic;
	}
	public void setPic(String pic) {
		this.pic = pic;
	}
	@Override
	public String toString() {
		return "VisitorRecord [id=" + id + ", inTime=" + inTime + ", longTime=" + longTime + ", city=" + city
				+ ", related=" + related + ", url=" + url + ", plan=" + plan + ", pic=" + pic + "]";
	}
	
	
	
	

}
