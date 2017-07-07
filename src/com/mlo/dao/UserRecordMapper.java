package com.mlo.dao;

import java.util.List;

import com.mlo.entity.Action;
import com.mlo.entity.VisitorRecord;


public interface UserRecordMapper {
	
	 int savesql(List<VisitorRecord> vrList);
	 
	 int saveAction(List<Action> actionList );

}
