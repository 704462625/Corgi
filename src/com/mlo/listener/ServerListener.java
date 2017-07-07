package com.mlo.listener;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mlo.dao.UserRecordMapper;
import com.mlo.dbfactory.Factory;
import com.mlo.entity.Action;
import com.mlo.entity.VisitorRecord;
import com.mlo.servlet.CountServer;

/**
 * Application Lifecycle Listener implementation class ServerListener
 *
 */
@WebListener
public class ServerListener implements ServletContextListener {
	private SqlSessionFactory sessionFactory;
    private SqlSession mbtsession;
    private UserRecordMapper urmapper;
    /**
     * Default constructor. 
     */
    public ServerListener() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * 
	 * 在服务器关闭时，将websocket中list没有存入数据库中数据存入数据库
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0)  { 
    	
    	List<VisitorRecord> vr = CountServer.getVisitorList();
    	List<Action> actions = CountServer.getActionList();
    	if(vr.size()>0){
    		save(vr,actions);
    	}
    	
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0)  { 
         // TODO Auto-generated method stub
    }
	public void save(List<VisitorRecord>  vr ,List<Action> actions) {
	    	int a;
			try {  
				sessionFactory=Factory.getFactory();
				mbtsession=sessionFactory.openSession();
				urmapper =mbtsession.getMapper(UserRecordMapper.class);
				a=urmapper.savesql(vr);
				urmapper.saveAction(actions);
				mbtsession.commit(); 
			}
			finally{
				mbtsession.close();
			}
			if(a>=1){
				vr=new ArrayList<VisitorRecord>();
			}
			
	    }
}
