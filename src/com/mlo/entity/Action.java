package com.mlo.entity;

import java.io.Serializable;

public class Action implements Serializable{
	private Integer id ;
	private Integer type;
	private String text ;
	private String related;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getRelated() {
		return related;
	}
	public void setRelated(String related) {
		this.related = related;
	}
	@Override
	public String toString() {
		return "Action [id=" + id + ", type=" + type + ", text=" + text + ", related=" + related + "]";
	}
	
	

}
