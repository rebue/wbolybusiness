<%@page import="com.wboly.system.sys.util.wx.WXSignUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%  
	Map<String, String> ret = new HashMap<String, String>();  
	ret = WXSignUtils.createSign(request.getAttribute("JSURL").toString());  
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>JSSDK</title>
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css">
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script type="text/javascript"
	src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
</head>
<body>
	<button id="shareLine">分享</button>
</body>
<script type="text/javascript">  
		
		    wx.config({  
		        debug: true,  
		        appId: '<%=ret.get("appid")%>',  
		        timestamp:'<%=ret.get("timestamp")%>',  
		        nonceStr:'<%=ret.get("nonceStr")%>',  
		        signature:'<%=ret.get("signature")%>', // 签名   
		        jsApiList : ['checkJsApi',// 判断当前客户端版本是否支持指定JS接口
		                     'getLocation'// 获取地理位置接口
		                     ]  
		    });//end_config  
		  
		    wx.error(function(res) {  
		        console.log("配置验证失败后执行");
		        mui.alert("出错了：" + res.errMsg); 
		    });  
		  
		  //ready函数用于调用API，如果你的网页在加载后就需要自定义分享和回调功能，需要在此调用分享函数。
		  //如果是微信游戏结束后，需要点击按钮触发得到分值后分享，这里就不需要调用API了，可以在按钮上绑定事件直接调用。
		  //因此，微信游戏由于大多需要用户先触发获取分值，此处请不要填写如下所示的分享API  
		    wx.ready(function() {
		    	
		    	console.log("配置验证成功后执行");
		    	
		    	// 判断当前客户端版本是否支持指定JS接口
		        wx.checkJsApi({
		            jsApiList : ['getLocation'],// 地理位置接口
		            complete :function(){
		            	console.log("无论成功或失败都执行");
		            	mui.alert("无论成功或失败都执行");
		            },
		            success : function(res) { 
		            	 // 以键值对的形式返回，可用的api值true，不可用为false
		                // 如：{"checkResult":{"chooseImage":true},"errMsg":"checkJsApi:ok"}
		            	console.log("成功后执行");
		            	mui.alert("成功:"+res.errMsg);
		            },
		            fail : function(err){
		            	console.log("失败后执行");
		            	mui.alert("失败:"+err);
		            }
		        });
		        
		    	// 获取地理位置接口
		        wx.getLocation({
		            type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
		            success: function (res) {
		            	console.log(res);
		                var latitude = res.latitude; // 纬度，浮点数，范围为90 ~ -90
		                var longitude = res.longitude; // 经度，浮点数，范围为180 ~ -180。
		                var speed = res.speed; // 速度，以米/每秒计
		                var accuracy = res.accuracy; // 位置精度
		            }
		        });
		    });//end_ready  
	</script>
</html>