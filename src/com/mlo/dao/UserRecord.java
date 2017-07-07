package com.mlo.dao;

import com.mlo.entity.VisitorRecord;

public interface UserRecord {
	/**
	 * 保存进入用户的信息
	 * @param v
	 */
	public boolean insertInRecord(VisitorRecord v );
	
	/**
	 * 保存离开用户信息
	 * @param v
	 */
	public boolean insertOutRecord(VisitorRecord  v );

}
