package com.mlo.servlet;

import java.io.IOException;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.json.JSONObject;

import com.mlo.dao.UserRecordMapper;
import com.mlo.dbfactory.Factory;
import com.mlo.entity.Action;
import com.mlo.entity.VisitorRecord;


/** 
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端, 
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端 
 */    
@ServerEndpoint(value="/count/{tag}/{ip}/{city}/{url}/{plan}")    
public class CountServer {
	
	//静态变量，用来记录浏览人数。应该把它设计成线程安全的。    
    private static int visitCount = 0;  
    //静态变量，用来记录点击数量，应该设计成线程安全。
    private static int clickCount =0;
    //visitors，用来存放每个客户端对应的用户WebSocket对象。
    private static Set<CountServer > visitors = new CopyOnWriteArraySet<>();
    //存放所有被监控页面的url，用来初始化管理者页面的下拉选项
    private static HashSet< String> allUrl = new HashSet<>();
    //集合里面有一个key为all的map。存放所有管理者的websocket对象，剩下的存放所有选择了url监控的websocket对象
    private static HashMap<String,HashSet<CountServer>> allMap=new HashMap<String,HashSet<CountServer>>();
    //放每个页面的访问量
    private static HashMap<String, Integer> allCount = new HashMap<>();
    //放每个页面的点击量
    private static HashMap<String, Integer> allClick = new HashMap<>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据    
    private Session session; 
    //用来存储前台传来的ip
    private String ip ="";
    //用来存储前台传来的city
    private String city ="";
    //用来存储前台传来的表示是管理员页面还是用户页面   0 代表用户，1代表管理员
    private Integer tag =null ;
    //存储用户访问进来时的时间
    private Date inTime =null ;
    //存储用户离开的时间
    private Date outTime = null;
    //用来存储前台传来的
    private String url ="";
    //用来存储前台传来的计划（投放计划）
    private String plan="";
    //用来存储用户的状态
    private Integer status =null ;//-1代表退出，0 代表点击，1代表进入
    //监控的url。默认是all
    private String controlUrl ="all";
    //正经格式的url
    private String url1="";
    //用来保存所有的管理客户端
    private static HashSet< CountServer> all = new HashSet<>();
    private SqlSessionFactory sessionFactory;
    private SqlSession mbtsession;
    private UserRecordMapper urmapper;
   
    private static List<VisitorRecord> visitorList  = Collections.synchronizedList(new ArrayList<>());
    private static List<Action> actionList = Collections.synchronizedList(new ArrayList<>());
    private String related ;
    /** 
     * 连接建立成功调用的方法 
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据 
     */    
    @OnOpen    
    public void onOpen(Session session,@PathParam(value = "tag")Integer tag,
    		@PathParam(value = "ip") String ip,
			@PathParam(value = "city")String city,
			@PathParam(value = "url")String url,
			@PathParam(value = "plan")String plan){ 
    	
    	this.session=session;
		this.tag=tag;
		this.ip = ip ;
		this.city = city;
		this.url=url;
		this.plan =plan;
		this.status = 1;
			//用户身份
			if(tag==0){	
				
				//用户进去时间
				inTime = new Date();
				//map中添加一个已链接的用户
				visitors.add(this);
				addVisitCount();
				//将要发给管理员的信息组装成json
				JSONObject message = new JSONObject();
				message.accumulate("status", status);
				message.accumulate("city", city);
				message.accumulate("inTime",format(inTime));
				message.accumulate("url", url.replace("()", "/"));
				message.accumulate("plan", plan.replace("()", ""));
				message.accumulate("visitCount", visitCount);
				message.accumulate("action", "showMsg");//传回前台表示数据的类型showMsg表示要显示在table里的信息
				
				
				url1 =url.replace("()", "/"); 
				if(!allUrl.contains(url1)){
					allUrl.add(url1);
					
					if(!allMap.isEmpty()){
						
						//管理者页面初始化下拉选项
						JSONObject urlJ = new JSONObject();
						urlJ.accumulate("urlAll", allUrl);
						urlJ.accumulate("action", "selectUrl");
						sendToAll(urlJ.toString(), allMap.get("all"));
					}
				}
				//保存每个页面的访问量
				if(!allCount.containsKey(url1)){
					allCount.put(url1, 1);
					
				}else{
					allCount.put(url1,allCount.get(url1)+1);
					
				}
				//如果符合管理者指定的url，发给他们
				if(allMap.containsKey(url1)&&!url1.equals("all")){
					message.remove("visitCount");
					message.accumulate("visitCount", allCount.get(url1));
					sendToAll(message.toString(), allMap.get(url1));
				}
				//发给没有指定的管理者，全部监控
				if(!allMap.isEmpty()){
					message.remove("visitCount");
					message.accumulate("visitCount", visitCount);
					sendToAll(message.toString(), allMap.get("all"));
					
				}
				//如果list够长度，将数据填入到数据库中。
				VisitorRecord vr = new VisitorRecord() ;
				related = UUID.randomUUID().toString();
				vr.setCity(city);
				vr.setInTime(format(inTime));
				vr.setUrl(url1);
				vr.setPlan(plan.replace("()", ""));
				vr.setRelated(related);
				visitorList.add(vr);
				Action action =  new Action();
				action.setType(status);
				action.setText("jinru");
				action.setRelated(related);
				actionList.add(action);
				if(visitorList.size()>=200||actionList.size()>=200){
					save();
				}
			}else if(tag==1){//tag ==1 代表管理客户端
				//向监控全部的管理者集合添加
				
				all.add(this);
				allMap.put("all", all);
				//管理者页面初始化下拉选项
				JSONObject urlJ = new JSONObject();
				urlJ.accumulate("urlAll", allUrl);
				urlJ.accumulate("action", "selectUrl");
				sendToAll(urlJ.toString(), allMap.get("all"));
				//管理者页面加载的时候。默认显示内存中的数据
				JSONObject msg = new JSONObject();
				msg.accumulate("memory", visitorList);
				msg.accumulate("action", "init");
				msg.accumulate("visitCount", visitCount);
				msg.accumulate("clickCount", clickCount);
				sendToself(msg.toString(), this);
			}
           
    }    
    /** 
     * 连接关闭调用的方法 
     */    
    @OnClose    
    public void onClose(){
    	if(tag==1){
    		
    		if(allMap.containsKey(controlUrl)){
    			//根据选择的url判断在哪个url集合下，然后移除
    			allMap.get(controlUrl).remove(this);
    		}
    		
    	}else if(tag==0){
    		//将状态变成离开
    		status = -1;
    		//离开时的时间
    		outTime = new Date();
    		//离开时向管理员页面发送消息
    		//String message = status+","+city+","+format(inTime)+","+getTime(inTime, outTime)+","+url.replace("()", "/")+","+plan.replace("()", "");
    		//将要发给管理员的信息组装成json
			JSONObject message = new JSONObject();
			message.accumulate("status", status);
			message.accumulate("city", city);
			message.accumulate("inTime",format(inTime));
			message.accumulate("url", url.replace("()", "/"));
			message.accumulate("plan", plan.replace("()", ""));
			message.accumulate("longTime", getTime(inTime, outTime));
			message.accumulate("action", "showMsg");//传回前台表示数据的类型showMsg表示要显示在table里的信息
    		//sendToAll(message.toString(),);
			String url1 =url.replace("()", "/"); 
			//如果符合管理者指定的url，发给他们
			if(allMap.containsKey(url1)&&!url1.equals("all")){
				sendToAll(message.toString(), allMap.get(url1));
			}
			//发给没有指定的管理者，全部监控
			if(!allMap.isEmpty()){
				sendToAll(message.toString(), allMap.get("all"));
			}
    		//subOnlineCount();           //在线数减1    
    		//如果list够长度，将数据填入到数据库中。
			VisitorRecord vr = new VisitorRecord() ;
			vr.setCity(city);
			vr.setInTime(format(inTime));
			vr.setUrl(url1);
			vr.setLongTime(getTime(inTime, outTime));
			vr.setPlan(plan.replace("()", ""));
			vr.setRelated(related);
			visitorList.add(vr);
			Action action =  new Action();
			action.setType(status);
			action.setText("tuichu");
			action.setRelated(related);
			actionList.add(action);
			if(visitorList.size()>=200||actionList.size()>=200){
				save();
			}
			visitors.remove(this);  //从set中删除    
    	}
    	  
    }    
   
    /** 
     * 收到客户端消息后调用的方法 
     * @param message 客户端发送过来的消息 
     * @param session 可选的参数 
     */    
    @OnMessage    
    public void onMessage(String message, Session session) { 
    	//管理者发送信息
    	if(tag==1){
    		//刷新
    		String mes[]=message.split("-");
    		
    		if(mes[0].equals("0")){//0代表是点击刷新
    			//如果符合管理者指定的url，发给他们
    			JSONObject m = new JSONObject();
				if(allMap.containsKey(mes[1])&&!mes[1].equals("all")){
					m.accumulate("visitCount", allCount.get(mes[1]));
					if( allClick.get(mes[1])==null){
						m.accumulate("clickCount", 0);
						
					}else{
						m.accumulate("clickCount", allClick.get(mes[1]));
					}
					m.accumulate("action", "showCount");
					sendToAll(m.toString(), allMap.get(controlUrl));
				}else if(mes[1].equals("all")){
					m.accumulate("visitCount", visitCount);
					if( allClick.get(mes[1])==null){
						m.accumulate("clickCount", 0);
						
					}else{
						m.accumulate("clickCount", allClick.get(mes[1]));
					}
					m.accumulate("action", "showCount");
					sendToAll(m.toString(), allMap.get(mes[1]));
					
				}
    			
    		}else{
    			//message----管理者向服务器发送监管页面url
    			
    			//将原来的集合中这个websocket对象移除
    			 allMap.get(controlUrl).remove(this);
    			 
    			//判断传过来的url是否已经存在，如果存在，就将websockt直接加入到这个集合中，如果没有，创建新的集合
    			if(allMap.containsKey(message)){
    				allMap.get(message).add(this);
    			}else{
    				HashSet<CountServer >  n = new HashSet<>();
    				n.add(this);
    				allMap.put(message,n);
    			}
    			//操作转换url的操作做完，将标志url的字符串转换成传过来的url，方便下次比较
    			controlUrl=message;
    		}
    		
			
			
    	}else if(tag==0){//用户发送信息
    		if(message.equals("联系我们")){
    			
    			
    		}else{
    			status=0;
    			addClickCount();
    			//保存每个页面点击的情况
    			url1 =url.replace("()", "/"); 
    			if(!allClick.containsKey(url1)){
    				allClick.put(url1, 1);
    				
    			}else{
    				allClick.put(url1, allClick.get(url1)+1);
    				
    			}
    			//将要发给管理员的信息组装成json
				JSONObject m = new JSONObject();
				m.accumulate("status", status);
				m.accumulate("city", city);
				m.accumulate("inTime",format(inTime));
				m.accumulate("url", url.replace("()", "/"));
				m.accumulate("plan", plan.replace("()", ""));
				m.accumulate("pic", message);
				m.accumulate("clickCount", clickCount);
				m.accumulate("action", "showMsg");//传回前台表示数据的类型showMsg表示要显示在table里的信息
    			//如果符合管理者指定的url，发给他们
				if(allMap.containsKey(url1)&&!url1.equals("all")){
					m.remove("clickCount");
					m.accumulate("clickCount", allClick.get(url1));
					sendToAll(m.toString(), allMap.get(url1));
				}
				//发给没有指定的管理者，全部监控
				if(!allMap.isEmpty()){
					sendToAll(m.toString(), allMap.get("all"));
					
				}
				//如果list够长度，将数据填入到数据库中。
				VisitorRecord vr = new VisitorRecord() ;
				vr.setCity(city);
				vr.setInTime(format(inTime));
				vr.setUrl(url1);
				vr.setPlan(plan.replace("()", ""));
				vr.setPic(message);
				vr.setRelated(related);
				//点击的图片待补充
				visitorList.add(vr);
				Action action =  new Action();
				action.setType(status);
				action.setText("dianji");
				action.setRelated(related);
				actionList.add(action);
				if(visitorList.size()>=200||actionList.size()>=200){
					save();
				}
    			
    		}
    		
    	}
    }   
   
    /** 
     * 发生错误时调用 
     * @param session 
     * @param error 
     */    
    @OnError    
    public void onError(Session session, Throwable error){    
        System.out.println("发生错误");    
        error.printStackTrace();    
    }    
   
    /** 
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。 
     * 发送信息
     * @param message 
     * @throws IOException 
     */    
    public void sendMessage(String message) throws IOException{    
        this.session.getBasicRemote().sendText(message);    
        //this.session.getAsyncRemote().sendText(message);    
    }    
   /**
    * 将时间类型转换成字符串类型并格式化
    * @param date
    * @return
    */
    public String format(Date date){
    	SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	return spf.format(date);
    }
    /**
     * 计算两个时间点之间经过多长时间
     * @param inTime 进入时间
     * @param outTime 出去时间
     * @return
     */
    public long getTime(Date inTime , Date outTime){
		long time= outTime.getTime()-this.inTime.getTime();
			 
		long day=time/(24*60*60*1000);
		 
		long hour=(time/(60*60*1000)-time/(24*60*60*1000)*24);
			
		long min=((time/(60*1000))-day*24*60-hour*60);
			
		Long s=(time/1000-day*24*60*60-hour*60*60);
		return s;
    	
    }
    public void sendToself(String message,CountServer self){
    	if(self!=null){
    		try {
    			
    			self.sendMessage(message);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}else{
    		System.out.println("没有此对象");
    		
    	}
    	
    }
    /**
     * 向管理员页面群发消息
     * @param message 发送的信息内容
     */
    public void sendToAll(String message,Set<CountServer> set){
    	
    	if(set.size()!=0){
			for(CountServer item: set){    
	            try {    	                
	            	item.sendMessage(message);    
		         } catch (IOException e) {    
	                e.printStackTrace();    
	                continue;    
		         }    
	         }    
		}
    }
    /**
     * 保存数据
     * @return
     */
    public void save(){
    	

    	int a;
		try {  
			
			sessionFactory=Factory.getFactory();
			
			mbtsession=sessionFactory.openSession();
			
			urmapper =mbtsession.getMapper(UserRecordMapper.class);

		 
		a=urmapper.savesql(visitorList);
		  urmapper.saveAction(actionList);
		
		mbtsession.commit(); 
		
		}
		finally{
			mbtsession.close();
		}
		if(a>=1){
		visitorList=new ArrayList<VisitorRecord>();
		}
		
    }
    
    public static synchronized int getVisitCount() {    
        return visitCount;    
    }  
    public static synchronized void  setVisitCount(int i) {    
           visitCount=i;
    }  
   
    public static synchronized void addVisitCount() {    
        CountServer.visitCount++;    
    }    
   
    public static synchronized void subVisitCount() {    
       CountServer.visitCount--;    
    }
    public static synchronized int getClickCount() {    
        return clickCount;    
    }  
    public static synchronized void  setclickCount(int i) {    
           clickCount=i;
    }  
   
    public static synchronized void addClickCount() {    
        CountServer.clickCount++;    
    }    
   
    public static synchronized void subClickCount() {    
       CountServer.clickCount--;    
    }
	public static List<VisitorRecord> getVisitorList() {
		return visitorList;
	}
	public static void setVisitorList(List<VisitorRecord> visitorList) {
		CountServer.visitorList = visitorList;
	}
	public static List<Action> getActionList() {
		return actionList;
	}
	public static void setActionList(List<Action> actionList) {
		CountServer.actionList = actionList;
	}
	
	 
    

}
