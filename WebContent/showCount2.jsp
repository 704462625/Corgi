<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
#div2 {
	width: 500px;
	margin: 200px 0px 0px 400px;
}
#content td{
    padding:10px;
}
#main{ 
	overflow:auto;
}
#main img{
    width:20px;
    height:20px;
}
#showTable{
	float:left;
	width:75%; 
} 
/* #save{
	width:100px;
	height:50px;
	color:white;
	background-color:grey;
	font-size:14px;
} */
/* #remove{
	width:150px;
	height:50px;
	background-color:green;
	color:white;
	font-size:14px;
} */
</style>
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
<script type="text/javascript">
	var aurl=window.location.href;
	var urlarr=aurl.split("/");
	var url1="";
	for(var a=0;a<urlarr.length;a++){
		url1=url1+urlarr[a]+"()";
	}
	var urlarr1=url1.split("?");
	var url=urlarr1[0];
	//获取当前用户的地址和ip
	var ip =returnCitySN["cip"];	
	var city=returnCitySN["cname"];
	var plan =urlarr1[1];
	//使用websocket
	var websocket = null; 
	//判断当前浏览器是否支持WebSocket   
   if ('WebSocket' in window) {   
       websocket = new WebSocket("ws://localhost:8080/Corgi/count/1/"+ip+"/"+city+"/"+url+"/"+plan); 
   }   
   else {   
       alert('当前浏览器 Not support websocket');  
   }   
   //连接成功建立的回调方法   
   websocket.onopen = function () { 
	   
	   setMessageInnerHTML("WebSocket连接成功");
	  
   }; 
   //连接发生错误的回调方法   
   websocket.onerror = function () {   
       setMessageInnerHTML("WebSocket连接发生错误");   
   };   
  
   //接收到消息的回调方法   
   websocket.onmessage = function (message) {   
      var a = JSON.parse(message.data);
      
      if(a.action=="showMsg"){
    	 showText(a);
      }else if (a.action=="selectUrl"){
    		
    	  for(var i =0;i<a.urlAll.length;i++){
    	  	  $("#selecturl").append("<option value='"+a.urlAll[i]+"'>"+a.urlAll[i]+"</option>");
    		 
    	  }
    	  
      } 
      //setMessageInnerHTML(data); 
   };   
  
   //连接关闭的回调方法   
   websocket.onclose = function () { 

       setMessageInnerHTML("关闭问题群");   
   };   
  
   //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。   
   window.onbeforeunload = function () {
	   
       closeWebSocket();   
   }; 
   
	function closeWebSocket() {  
       var myDate = new Date();  
       var mytime=myDate.toLocaleTimeString().substring(2,myDate.toLocaleTimeString().length);   
       var message = mytime+" "+name+":退出问题群";    
       websocket.send(message);  
       websocket.close();    
       
   }    
   //将消息显示在网页上    
    function setMessageInnerHTML(innerHTML) {    
       document.getElementById('message').innerHTML= innerHTML;   
   }
	//格式化时间
    function format(d){
		return  d.getFullYear() + '-' + (d.getMonth() + 1) + '-' + d.getDate() + ' ' + d.getHours() + ':' + d.getMinutes() + ':' + d.getSeconds();
	}
	//选择制定url，并向服务器端发送选定的url（状态1代表是选指定的url进行监控并用&拼接起来）
    function urlchange(value){
		
    	websocket.send(value);
    }
	//动态拼table显示内容
  	function showText(a){
		if(a.status==1){
			//console.log(data);
	  		var text="<tr><td><img src='img/in.jpeg'/></td><td>"+a.city+"</td><td>"+a.inTime+" </td><td>"+a.url+" </td><td>"+a.plan+"</td><td></td></tr>";
	  		$("#content").append(text);
		}else if (a.status==-1){
			var text="<tr><td><img src='img/out.jpeg'/></td><td> "+a.city+"</td><td>"+a.inTime+" </td><td>"+a.url+" </td><td>"+a.plan+"</td><td>"+transminsec(a.longTime)+" </td></tr>";
			$("#content").append(text);
		}else if (a.status==0){
			var text="<tr><td><p><img src='img/click.jpeg'/></td><td> "+a.city+"</td><td> "+a.inTime+"</td><td>"+a.url+" </td><td>"+a.plan+"</td><td></td></tr>";
			$("#content").append(text);
		}
  	}
  	function transminsec(value){
 	   return Math.floor(parseInt(value)/60)+"\'"+parseInt(value)%60+"\"";
    }
</script> 
</head>
<body>

	<span id="message"></span>
	<div id="main" style="height:800px;">
		<div id="showTable">
			<table id="content" border="1" cellpacing="0">
			<!--动态生成显示内容  -->
		    </table>
		</div>
		<div>
			<select id="selecturl" onchange="urlchange(this.value)" style="width:500px;height:50px;font-size:1.2em;">
		    	<option value="all">all</option>
		    	
		    </select>
		</div>
		
 		
	</div>
</body>
</html>