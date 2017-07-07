package com.mlo.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


 


public class DBFartroy {
	
	private static Connection connection=null;
	private static PreparedStatement pstatement = null;

    //本地数据库	
	private static final String URL="jdbc:mysql://121.43.162.65:3306/testdb?useUnicode=true&characterEncoding=utf-8";
	private static final String USERNAME="root";
	private static final String PASSWORD="doublemint";

	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动类，加载失败");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected DBFartroy(){
	}
	
	public static Connection getInstance(){
		 
			try {
				connection =  DriverManager.getConnection(URL,USERNAME,PASSWORD);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return connection;
	}
	
	
	public static <T>List<T> getListData(Class<T> type,String Sql){
		List<T> result = new ArrayList<T>();
		connection = getInstance();
		try {
			pstatement = connection.prepareStatement(Sql);
			ResultSet list = pstatement.executeQuery();
			LoadObject(list,result,type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public static <T> T getObject(Class<T> type,String Sql){
		List<T> result = new ArrayList<T>();
		PreparedStatement pstatement = null;
		connection = getInstance();
		
		try {
			pstatement = connection.prepareStatement(Sql);
			
			ResultSet list = pstatement.executeQuery();
			LoadObject(list,result,type);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(result.size()>0)
		{
			return result.get(0);
		}else {
			return null;
		}
	}
	
	public static boolean executeUpdate(String Sql){
		PreparedStatement pstatement = null;
		connection = getInstance();
		int result=0;
		try {
			pstatement = connection.prepareStatement(Sql);
			
			result = pstatement.executeUpdate();
			return result>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private static <T> void LoadObject(ResultSet list,List<T> result,Class<T> type){
		try {
			ResultSetMetaData rsmd = list.getMetaData();
			Constructor<T> constructor=type.getConstructor(null);
			Method[] methods = type.getMethods();
			while(list.next()){

				T obj = constructor.newInstance(null);

				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					String colName = rsmd.getColumnName(i);
					//int sqltype =rsmd.getColumnType(i);
					Method execMethod =null;
					for (Method method : methods) {
						
						if(method.getName().contains("get"))
						{
							continue;
						}

						String methodname = method.getName().toLowerCase();
						String temp = colName.replace("_", "").toLowerCase();
						
						if(methodname.contains(temp))
						{
							execMethod = method;
							break;
						}
						
					}
					Object val = list.getObject(i);
					if(val !=null){
						if (val instanceof Date) {//处理时间
							SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
						execMethod.invoke(obj, format.format(val));
						}
						else {
							execMethod.invoke(obj, val);
						}
												
					}
				}
				result.add(obj);
		}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
