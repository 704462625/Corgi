package com.mlo.dbfactory;


import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;  
  
public class Factory {  
    private static Factory factory;  
    private static SqlSessionFactory sqlSessionFactory;  
    private Factory(){  
        try{  
            String resource = "com/mlo/web/config/Mybatyis-config.xml";   
            Reader reader = Resources.getResourceAsReader(resource);   
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);   
        }catch(IOException e){  
            System.out.println(e.getMessage());  
        }catch(Exception e){  
            System.out.println(e.getMessage());  
        }  
    }  
    public static SqlSessionFactory getFactory(){  
        if(factory == null){  
            factory = new Factory();  
            return factory.sqlSessionFactory;  
        }else  
            return factory.sqlSessionFactory;  
    }  
} 