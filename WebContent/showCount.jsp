<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">

table{
  border:1px solid black;
  border-collapse: collapse;
}
tr{
    border-bottom: solid 1px #000;
}
td{
	text-align:center;
	font-size:12px;
	border-right: 1px solid #000;  
}
#showTable td{
    padding:5px;
    
}
#main{ 
	overflow:auto;
	margin-bottom:20px;
}
#main img{
    width:30px;
    height:30px;
}
#show{
	
} 
#remove{
	/* width:150px;
	height:50px; */
	/* background-color:green; */
	/* color:white; */
	/* font-size:14px; */
} 
#option {
	margin-top:50px;
}
#optionTable td {
	background-color:#f7f7f7;
	border-color:#251714;
	border-style:solid;
	border-width: 1px;
	/* padding:5px; */
	text-align:center;

}
</style>
<!-- <script type="text/javascript" src="js/jquery-2.1.1.min.js"></script> -->
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
<script src="http://pv.sohu.com/cityjson?ie=utf-8"></script>
<script type="text/javascript">
	//浏览人数
	var visitCount = 0;
	//点击人数
	var clickCount = 0;
	//单一页面点击人数
	var singleVisit=0;
	//单一页面点击数
	var singleClick =0;
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
       //websocket = new WebSocket("ws://love18k.com:8028/Corgi/count/1/"+ip+"/"+city+"/"+url+"/"+plan); 
	   websocket = new WebSocket("ws://localhost:8080/Corgi/count/1/"+ip+"/"+city+"/"+url+"/"+plan);
   }   
   else {   
       alert('当前浏览器 Not support websocket');  
   }   
   //连接成功建立的回调方法   
   websocket.onopen = function () { 
	   
	   setMessageInnerHTML("0");
	   
	  
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
    		  $("#selecturl").html("<option value='all'>all</option>");
    		  
    	  for(var i =0;i<a.urlAll.length;i++){
    	  	  $("#selecturl").append("<option value='"+a.urlAll[i]+"'>"+a.urlAll[i]+"</option>");
    	  }
      }else if(a.action=="init"&& a.memory!=null){
    	  
    	  var text ="";
    	  if(a.memory.length>10){
    		  for(var i =a.memory.length-9; i<a.memory.length;i++){
    			  
        		  if(a.memory[i].plan=="undefined"){
        			  //将undefined换掉
        			  a.memory[i].plan="无";
        		  }
        		  if(a.memory[i].longTime!=null){
        			  
    	    		  text="<tr style='color:#696969'><td>⇏</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank' >◉</a> "+a.memory[i].url+" </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'>"+transminsec(a.memory[i].longTime)+"</td></tr>";
        		  }else if (a.memory[i].pic!=null){
        			  text="<tr bgcolor='#31a662' style='color:#fff'><td>☜</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank' >◉</a> "+a.memory[i].url+"  </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'>"+a.memory[i].pic+"</td></tr>";
        		  }else {
        			  text="<tr bgcolor='#007ef3' style='color:#fff'><td>⇒</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank' >◉</a> "+a.memory[i].url+" </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'></td></tr>";
        		  }
        		  $("#showTable").append(text);
        		  
        		  move();
        	  }
        	  var  tipText ="<tr><td></td><td colspan='5' align='center' style='color:#AAA'>以上是历史内容</td></tr>";
        	  $("#showTable").append(tipText);
        	  $("#visitCount").html("");
        	  $("#clickCount").html("");
        	  
        	  $("#visitCount").html(a.visitCount);
        	  $("#clickCount").html(a.clickCount);
    		  
    		  
    	  }else{
    		  for(var i =0; i<a.memory.length;i++){
    			  if(a.memory[i].plan=="undefined"){
        			  a.memory[i].plan="无";
        		  }
        		  if(a.memory[i].longTime!=null){
    	    		  text="<tr style='color:#696969'><td>⇏</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank' >◉</a> "+a.memory[i].url+"  </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'>"+transminsec(a.memory[i].longTime)+"</td></tr>";
        		  }else if (a.memory[i].pic!=null){
        			  text="<tr bgcolor='#31a662' style='color:#fff'><td>☜</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank'>◉</a> "+a.memory[i].url+"  </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'>"+a.memory[i].pic+"</td></tr>";
        		  }else {
        			  text="<tr bgcolor='#007ef3' style='color:#fff'><td>⇒</td><td>"+a.memory[i].city+"</td><td>"+a.memory[i].inTime+" </td><td style='text-align:left'><a href='"+a.memory[i].url+"' target='_Blank' >◉</a> "+a.memory[i].url+"  </td><td>"+a.memory[i].plan+"</td><td style='width:100px;height:18px;'></td></tr>";
        		  }
        		  $("#showTable").append(text);
        		 
        	  }
        	  var  tipText ="<tr><td></td><td colspan='5' align='center' style='color:#AAA'>以上是历史内容</td></tr>";
        	  $("#showTable").append(tipText);
        	  $("#visitCount").html("");
        	  $("#clickCount").html("");
        	  $("#visitCount").html(a.visitCount);
        	  $("#clickCount").html(a.clickCount);
        	 move();
    	  }
    	  
    	  
      }else if (a.action=="showCount"){
    	 
    	  $("#visitCount").html("");
    	  $("#clickCount").html("");
    	  $("#visitCount").html(a.visitCount);
    	  $("#clickCount").html(a.clickCount);
    	

    	  
      }
      //setMessageInnerHTML(data); 
   };   
  
   //连接关闭的回调方法   
   websocket.onclose = function () { 
       setMessageInnerHTML("1");   
   };   
  
   //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。   
   window.onbeforeunload = function () {
       closeWebSocket();   
   }; 
   
	function closeWebSocket() {  
       
       websocket.close();    
       
   }    
   //将消息显示在网页上    
    function setMessageInnerHTML(text) { 
    	if(text=="0"){
    		$("#message").html("链接成功");
	    	$("#message").css("background","green");
    	}else if (text=="1"){
    		$("#message").html("链接关闭");
	    	$("#message").css("background","red");
    	}
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
		//当tr超出范围时就清屏
  		table1 = document.getElementById("showTable");
  		var count = table1.rows.length;
  		if(count>100){
  			removelist();
  		}
		if(a.status==1){
			
			var audioin=document.getElementById('audioin');
			audioin.play();
			//console.log(data);
			if(a.plan=="undefined"){
  			  a.plan="无";
  		    }
	  		var text="<tr bgcolor='#007ef3' style='color:#fff'><td>⇒</td><td>"+a.city+"</td><td>"+a.inTime+" </td><td style='text-align:left'><a href='"+a.url+"' target='_Blank'>◉</a> "+a.url+" </td><td>"+a.plan+"</td><td style='width:100px;height:18px;'></td></tr>";
	  		visitCount = a.visitCount;
	  		$("#visitCount").html("");
	  		$("#visitCount").html(visitCount);
	  		$("#showTable").append(text);
	  		move();
	  		
		}else if (a.status==-1){
			if(a.plan=="undefined"){
	  			  a.plan="无";
	  		}
			var text="<tr style='color:#696969'><td>⇏</td><td> "+a.city+"</td><td>"+a.inTime+" </td><td style='text-align:left'><a href='"+a.url+"' target='_Blank' >◉</a> "+a.url+" </td><td>"+a.plan+"</td><td style='width:100px;height:18px;'>"+transminsec(a.longTime)+" </td></tr>";
			$("#showTable").append(text);
			move();
		}else if (a.status==0){
			if(a.plan=="undefined"){
	  			  a.plan="无";
	  		}
			var text="<tr bgcolor='#31a662' style='color:#fff'><td>☜</td><td> "+a.city+"</td><td> "+a.inTime+"</td><td style='text-align:left'><a href='"+a.url+"' target='_Blank' >◉</a> "+a.url+"  </td><td>"+a.plan+"</td><td style='width:100px;height:18px;'>"+a.pic+"</td></tr>";
			$("#showTable").append(text);
			move();
			var audioclick=document.getElementById('audioclick');
			audioclick.play();
			$("#clickCount").html("");
			$("#clickCount").html(a.clickCount);
		}
  	}
  	function transminsec(value){
 	   return Math.floor(parseInt(value)/60)+"\'"+parseInt(value)%60+"\"";
    }
  	function  removelist(){
 	   $("#showTable tbody").html("");
 	   
    }
  	//自动滚动
  	function move(){
  		var show = document.querySelector("#show");
  		var showH = show.clientHeight;
  		var option = document.querySelector("option");
  		var optionH = option.clientHeight;
  		var main= document.querySelector("#main");
  		var mainH= main.clientHeight;
  		var h= showH+50+optionH;
  		if(h>mainH){
  			$("#main").scrollTop($("#main")[0].scrollHeight);
  		}
  		
  	}
  	function fresh(){
  		
  		var text =$("#selecturl option:selected").text();
  		websocket.send("0-"+text);
  		
  	}
</script> 
</head>
<body>
	<div >
		<div id="main" style="float:left;height:900px;" > 
			<!-- 数据显示表格 -->
			<div id="show" >
				<table id="showTable" border="1" cellpacing="0">
				<thead>
					<tr>
						<th width="60px" >ACTION</th>
						<th width="150px">AREA</th>
						<th width="150px">TIME</th>
						<th width="320px">MARKED URL</th>
						<th width="120ox">DESRC</th>
						<th width="100px">STAISTICS</th>
					</tr>
				</thead> 
				<!--动态生成显示内容  -->
			    </table>
			</div>
			<!--数据操作表格  -->
			<div id="option">
				<table id="optionTable">
					<tr>
						<td width="60px">运行状态</td>
						<td width="150px" id="message" ></p> </td>
						<td width="120px">当前监控页面</td>
						<td colspan="3" style="width:480px;">
							<select id="selecturl" onchange="urlchange(this.value)" style="width:480px;height:25px;font-size:1.2em;">
						    	<option value="all">all</option>
						    	<!--动态生成-->
						    </select>
					    </td>
						<td width="40px"><a id="remove"  onclick="removelist()">[清屏]</a></td>
					</tr>
					<tr>
						<td>浏览人数</td>
						<td id="visitCount"></td>
						<td>页面点击数</td>
						<td width="150px" id="clickCount"></td>
						<td width="120px">平均浏览时长</td>
						<td ></td>
						<td id="fresh"  onclick="fresh()">[刷新]</td>
					</tr>
				</table>
				<div id="tip">
				  <audio id="audioin"   src="song/in.wav" type="audio/wav"></audio>
				  <audio id="audioclick"  src="song/click.wav" type="audio/wav"></audio>
				</div>
			</div>
		</div> 
		<div style="float:left">
		
		</div> 
		
	</div>
</body>
</html>