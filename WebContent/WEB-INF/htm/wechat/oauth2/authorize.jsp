<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body id="html">
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script>
			(function($, doc) {
				$.init();
				$.ready(function() {
					if(isWeiXin()){
						console.log("当前在微信浏览器中打开");
						window.location.href="${ctx}/wechat/oauth2/checkSignature.htm";
					}else{
						console.log("不在微信浏览器打开");
						window.location.href="${ctx}/wechat/index/indexInfo.htm";
					}
					
				    function isWeiXin(){  
				        var ua = navigator.userAgent.toLowerCase();  
				        if(ua.match(/MicroMessenger/i)=="micromessenger") {  
				            return true;  
				        } else {  
				            return false;  
				        }  
				    }  
				});
			}(mui, document));
		</script>
</body>
</html>
