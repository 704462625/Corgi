package com.mlo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.mlo.servlet.CountServer;



public class OrderFresh {
	//时间间隔（一天）
	private static final long SPAN=24*60*60*1000;
	//获取当前系统的时间
	public OrderFresh(Date date){
		//System.out.println(date);
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		final Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    //距第一次执行的时间间隔
	    final long diff = cal.getTimeInMillis() - now.getTimeInMillis();
	    Timer timer = new Timer();  
        Task task = new Task();  
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。  
        timer.schedule(task,diff,SPAN); 
		
	}
	static class Task extends TimerTask{

		@Override
		public void run() {
			CountServer.setVisitCount(0);
			
		}
		
		
	}
	

}
