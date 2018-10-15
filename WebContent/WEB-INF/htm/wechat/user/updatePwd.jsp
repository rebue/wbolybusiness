<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>大卖网络</title>
		<meta name="wap-font-scale" content="no">
		<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
		<meta name="apple-mobile-web-app-capable" content="yes">
		<meta name="apple-mobile-web-app-status-bar-style" content="black">
		<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css">
		<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
		<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
		<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/usercenter.css" />
		<script src="${ctx }/js/wechat/mui.min.js"></script>
		<script src="${ctx }/js/util/commonUtil.js"></script>
		<script src="${ctx }/js/util/base64.js"></script>
		<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
		<script type="text/javascript" src="${ctx }/js/util/md5.js"></script>
		<script type="text/javascript" src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
	</head>
	<script type="text/javascript">
		$(function () {
			$.ajax({
				url : "${ctx }/wechat/user/loginPwIsExis.htm?userId=${userId}",
				type : "get",
				dataType:'json',//服务器返回json格式数据
				success:function(data){
					if (data == "1") {
						$(".divTop2").hide();
						$(".divTop1").show();
						$("#bankCard").css("display","none")
						$("#payTreasure").css("color","#fe3000")
					} else {
						$(".divTop1").hide();
						$(".divTop2").show();
						$("#payTreasure").css("display","none")
						$("#bankCard").css("color","#fe3000")
					}
				}
			});
			/*  这里是原本的点击显示，后面改为上面的动态显示
			$(document).on("tap", "#payTreasure", function() {
				$(".divTop1").show();
				$(".divTop2").hide();
			});
			
			$(document).on("tap", "#bankCard", function() {
				$(".divTop2").show();
				$(".divTop1").hide();
			}); 
			
			
			*/
			
			// 修改密码提交
			$(document).on("tap", "#updateSubmit", function() {
				// 验证数字
				var purenum = /^[0-9]*$/;
				var regspace =/\s/;
				// 验证特殊字符
				var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g;
				// 旧登录密码
				var updateOldPassword = document.getElementById("updateOldPassword").value.trim();
				// 新登录密码
				var updateNewPassword = document.getElementById("updateNewPassword").value.trim();
				// 确认后的新登录密码
				var updateValNewPassword = document.getElementById("updateValNewPassword").value.trim();
				var oldPasswordlength = updateOldPassword.length;
				var flag = false;
				if(!(regspace.exec(updateOldPassword) == null) || updateOldPassword == ""){
					mui.toast("密码请勿留空或包含空格");
				}else if(special.test(updateOldPassword) || purenum.test(updateOldPassword)){
			    	mui.toast('密码不能包含特殊字符并且不可为纯数字');
			    }else if(oldPasswordlength < 6 || oldPasswordlength > 16){
			    	mui.toast('密码长度为6-16个字符');
			    }else{
			    	flag = true;
			    };
			    if(!verifyPwd(updateNewPassword, updateValNewPassword)){
			    	console.log("测试失败");
					return ;
				}
			    console.log("测试成功");
				$.ajax({
					url : "${ctx}/wechat/user/changeLogonPassword.htm",
					type : "post",
					dataType:'json',//服务器返回json格式数据
					data : {oldLoginPswd : encodeURIComponent(CryptoJS.MD5(updateOldPassword)), newLoginPswd : encodeURIComponent(CryptoJS.MD5(updateValNewPassword))},
					success:function(data){
						if (data.result == 1) {
							mui.toast(data.msg);
							window.location.href = "${ctx }/wechat/user/userCenter.htm?promoterId=${userId}";
						} else {
							mui.toast(data.msg);
						}
					}
				});
			});
			
			// 设置密码提交
			$(document).on("tap", "#setSubmit", function() {
				// 新登录密码
				var setNewPassword = document.getElementById("setNewPassword").value.trim();
				// 确认后的新登录密码
				var setValNewPassword = document.getElementById("setValNewPassword").value.trim();
				if(!verifyPwd(setNewPassword, setValNewPassword)){
			    	console.log("测试失败");
					return ;
				}
				console.log("测试成功");
				$.ajax({
					url : "${ctx}/wechat/user/setLoginPassword.htm",
					type : "post",
					dataType:'json',//服务器返回json格式数据
					data : {newLoginPswd : encodeURIComponent(CryptoJS.MD5(setValNewPassword))},
					success:function(data){
						if (data.result == 1) {
							mui.toast(data.msg);
							window.location.href = "${ctx }/wechat/user/userCenter.htm?promoterId=${userId}";
						} else {
							mui.toast(data.msg);
						}
					}
				});
			});
		});
		
		//验证密码是否符合规范
		function verifyPwd(newPassword, valNewPassword){
			var purenum = /^[0-9]*$/;
			var regspace =/\s/;
			var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g;
			var newPasswordLength = newPassword.length;
			var valNewPasswordLength = valNewPassword.length;
			var flag = false;
			
			if(!(regspace.exec(newPassword) == null) || newPassword == ""){
				mui.toast("密码请勿留空或包含空格");
			}else if(special.test(newPassword) || purenum.test(newPassword)){
		    	mui.toast('密码不能包含特殊字符并且不可为纯数字');
		    }else if(newPasswordLength < 6 || newPasswordLength > 16){
		    	mui.toast('密码长度为6-16个字符');
		    }else{
		    	flag = true;
		    };
		    
			if(!(regspace.exec(valNewPassword) == null) || valNewPassword == ""){
				mui.toast("密码请勿留空或包含空格");
			}else if(special.test(valNewPassword) || purenum.test(valNewPassword)){
		    	mui.toast('密码不能包含特殊字符并且不可为纯数字');
		    }else if(valNewPasswordLength < 6 || valNewPasswordLength > 16){
		    	mui.toast('密码长度为6-16个字符');
		    }else{
		    	flag = true;
		    };
		    
		    if (flag) {
		    	if (newPassword != valNewPassword) {
			    	mui.toast('两次输入的密码不一样');
			    	flag = false;
				} else {
					flag = true;
				}
			}
		    return flag;
		};
		
	</script>
	<style type="text/css">
		.divTop1 { mcolor: #555; padding: 10px; margin-top: 3px; }
		.divTop2 { mcolor: #555; padding: 10px; margin-top: 3px; }
		.mui-input-row{padding: 5px; margin-top: -10px;}
		input { -webkit-user-select:auto }
	</style>
	<body>
		<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
			<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
			<h4 class="mui-title">修改或设置登录密码</h4>
		</header>
		<div id="wallet_main">
			<div class="mui-content">
				<div class="mui-slider tab-slider">
					<div id="sliderSegmentedControl"
						class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
						<div class="mui-control-item mui-active" id="payTreasure">修改登录密码</div>
						<div class="mui-control-item" id="bankCard">设置登录密码</div>
					</div>
				</div>
				<div id='login-form' class="divTop1">
					<div class="mui-input-row">
						<input id='updateOldPassword' type="password" class="mui-input mui-input-password" placeholder="请输入旧密码" data-type="password">
					</div>
					<div class="mui-input-row">
						<input id='updateNewPassword' type="password" class="mui-input mui-input-password" placeholder="请输入长度为6-16位的新密码" data-type="password">
					</div>
					<div class="mui-input-row">
						<input id='updateValNewPassword' type="password" class="mui-input mui-input-password" placeholder="请再次输入新密码" data-type="password">
						<p style="color: red;">该密码可用于登录操作，请务必牢记！</p>
					</div>
					<div class="divTop1" style="text-align: center;">
						<button id="updateSubmit">确&nbsp;&nbsp;认</button>
					</div>
				</div>
				<div id='login-form' class="divTop2">
					<div class="mui-input-row">
						<input id='setNewPassword' type="password" class="mui-input mui-input-password" placeholder="请输入长度为6-16位的新密码" data-type="password">
					</div>
					<div class="mui-input-row">
						<input id='setValNewPassword' type="password" class="mui-input mui-input-password" placeholder="请再次输入新密码" data-type="password">
						<p style="color: red;">该密码可用于登录操作，请务必牢记！</p>
					</div>
					<div class="divTop2" style="text-align: center;">
						<button id="setSubmit">确&nbsp;&nbsp;认</button>
					</div>
				</div>
				<div style="padding-top: 3px;"></div>
				<div></div>
			</div>
		</div>
	</body>
</html>