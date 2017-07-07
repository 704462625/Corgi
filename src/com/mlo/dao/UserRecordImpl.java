package com.mlo.dao;

import com.mlo.entity.VisitorRecord;
import com.mlo.util.DBFartroy;


public class UserRecordImpl implements  UserRecord{

	@Override
	public boolean insertInRecord(VisitorRecord v) {
		
		String sqlq="insert into  visitorRecord (  intime , city , url ) values " +
				"  ('"+v.getInTime()+"','"+v.getCity()+"','"+v.getUrl()+"')";
				
		System.out.print(sqlq);
		return DBFartroy.executeUpdate(sqlq);
		
	}

	@Override
	public boolean insertOutRecord(VisitorRecord v) {
		String sqlq="insert into  `visitorRecord` (  `intime`, `longTime`,`city`,'url') values " +
				"  ('"+v.getCity()+"','"+v.getLongTime()+"','"+v.getCity()+"','"+v.getUrl()+"')";
				
		System.out.print(sqlq);
		return DBFartroy.executeUpdate(sqlq);
	}

}
