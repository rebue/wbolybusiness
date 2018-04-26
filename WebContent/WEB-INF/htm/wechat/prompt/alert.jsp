<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>大卖网络</title>
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css">
<script src="${ctx }/js/wechat/mui.min.js"></script>
</head>
<body>
</body>
<script type="text/javascript" charset="utf-8">
		(function($, doc) {
			$.init();
			$.ready(function() {
				var json = JSON.parse('${page}');
				if(json.page==1){
					if(json.flag){
						mui.alert(json.message,' ',function(){
							window.location.href="${ctx }/wechat/user/userCenter.htm";
						});
						return ;
					}
					mui.alert(json.message,' ',function(){
						window.location.href="${ctx }/wechat/user/logInPage.htm";
					});
				}
				if(json.page==2){
					mui.alert(json.message,' ',function(){
						window.location.href="${ctx }/wechat/order/myOrders.htm";
					});
				}
				if(json.page==3){
					mui.alert(json.message,' ',function(){
						window.location.href="${ctx}/wechat/oauth2/checkSignature/login.htm";
					});
				}
			});
		}(mui, document));
		</script>
</html>