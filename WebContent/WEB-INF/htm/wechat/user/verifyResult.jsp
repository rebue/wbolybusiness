<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/webuploader.css" />
<title>大卖网络</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/webuploader.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/wboly_mobile.css">
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/mui.min.css">
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/wboly_mobile.css">
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/operation.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/webuploader.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/wechat/jquery.min.js" type="text/javascript"></script>
<script src="${ctx }/js/wechat/webuploader.html5only.min.js"
	type="text/javascript"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>


</head>

<body class="account-page"  >
	<input type="hidden" id="verifyUserId" value="${userId}" / >
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title" style="font-size:1.5rem" >实名认证申请结果</h1>
	</header>
	<div class="mui-content" style="background:#efeff4;padding-left:0.3rem; padding-right:0.3rem; "  >
		<div class="mui-input-group">
			<div style="padding-top:0.7rem;height:3rem; "  class="mui-input-row phone-row">
				<p style="padding-left:0.5rem; font-size:1.3rem;"  id="applyState" ></p>
			</div>
		</div>
		<div  class="mui-input-group">
			<div style="padding-top:0.7rem;height:8rem; " >
				<p id="rejectReason" style="padding-left:0.5rem;font-size:1.3rem;"" ></p>
			</div>
		</div>

		<div   class="mui-content-padded" id="ulbtn-box">
			<button id='uploadBtn'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">申请实名认证</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2018 www.duamai.com</p>
			<p>浏阳市大卖网络科技有限公司 湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
	<script type="text/javascript">

		(function($, doc) {
			mui.init();
			$.ready(function() {
				
				    //读取cookies
				    function getCookie(name) {
				        var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
		
				        if (arr = document.cookie.match(reg))
		
				            return unescape(arr[2]);
				        else
				            return null;
				    }
					//从cookie中获取申请状态并插入
					var userId=document.getElementById("applyState");
					var applyStateResult="";
					if(getCookie("applyState")==1){
						applyStateResult="已通过"
					}else if(getCookie("applyState")==2){
						applyStateResult="待审核中"

					}else if(getCookie("applyState")==3){
						applyStateResult="已拒绝"

					}
					var html='<b>结果</b>:'+applyStateResult;
					userId.innerHTML=html;
					//从cookie中获取拒绝原因并插入
					var rejectReason=document.getElementById("rejectReason");
					var Reason= getCookie("rejectReason");
					if(Reason != "undefined"){
						 html='<b>备注</b>:'+Reason;
					}else{

						html='<b>备注</b>:'
					}
					rejectReason.innerHTML=html;

					});
					var uploadBtn=document.getElementById("uploadBtn");
					if(getCookie("applyState")==1 || getCookie("applyState")==2){
						var uploadBtn=document.getElementById("ulbtn-box");
						uploadBtn.style.display="none";
					}
					uploadBtn.addEventListener('tap',function(){
						document.location.href= "${ctx }/wechat/user/verifyRealNamePage.htm";// 跳转至申请页面
		            });
 
		})(mui, document);
		
	
	</script>

</body>

</html>