
	var aurl=window.location.href;
	var urlarr=aurl.split("/");
	var url1="";
	for(var a=0;a<urlarr.length;a++){
		url1=url1+urlarr[a]+"()";
	}
	var urlarr1=url1.split("?");
	var url=urlarr1[0];
	var plan =urlarr1[1];
	//获取当前用户的地址和ip
	var ip =returnCitySN["cip"];	
	var city =returnCitySN["cname"];
	
	//使用websocket
	var websocket = null; 
	//链接时间
	var  inTime = null ;
	//判断当前浏览器是否支持WebSocket   
   if ('WebSocket' in window) {   
       //websocket = new WebSocket("ws://love18k.com:8028/Corgi/count/0/"+ip+"/"+city+"/"+url+"/"+plan); 
       websocket = new WebSocket("ws://localhost:8080/Corgi/count/0/"+ip+"/"+city+"/"+url+"/"+plan);
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
   websocket.onmessage = function (event) {   
       setMessageInnerHTML(event.data);   
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
  	//当用户点击图片时，向服务器传数据
    function userclick(text){
    	
    	websocket.send(text);
    	
    }
  	//点击联系我们向管理员页面发送消息 	
  	function cliContactUs(){
  		websocket.send("联系我们");
  	}