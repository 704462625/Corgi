package com.mlo.listener;

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mlo.util.OrderFresh;

/**
 * Application Lifecycle Listener implementation class TimeListenter
 *
 */
@WebListener
public class TimeListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public TimeListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
       
    }

	/**
	 * 当服务器启动的时候开启定时器
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
    	Date date = new Date();
    	
    	new OrderFresh(date);
    	//在日志标记开始
    	arg0.getServletContext().log("定时器已启动"); 
    	
         // TODO Auto-generated method stub
    }
	
}
