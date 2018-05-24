<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>大卖网络-登录</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page login-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" id="action_back"></a>
		<h1 class="mui-title">登录</h1>
	</header>
	<div class="mui-content">
		<form id='login-form' class="mui-input-group">
			<div class="mui-input-row">
				<input id='account' type="text" class="mui-input-clear mui-input" placeholder="请输入用户名/邮箱/手机号" data-type="userName">
			</div>
			<div class="mui-input-row mui-password">
				<input id='password' type="password" class="mui-input mui-input-password" placeholder="请输入密码" data-type="password">
			</div>
			<div class="mui-content-padded" id="loginButton">
				<button id="login" class="mui-btn mui-btn-block mui-btn-danger mui-btn-primary" style="font-size: 18px;">登 录</button>
			</div>
			<div class="mui-content-padded" style="margin-top: 10px;">
				<a class="mui-btn" style="margin: 0px 0px 0px 18%; text-align: center; float: left; font-size: 18px; color: #333" href="${ctx}/wechat/user/updateLoginPwd.htm">忘记密码</a>
				<button id="wechatlogin" class="mui-btn" style="margin: 0px 18% 0px 0px; float: right; font-size: 18px;">我要注册</button>
			</div>
		</form>
		
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2016-2018 wboly.com</p>
			<p>浏阳市大卖网络科技有限公司   湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
	<script type="text/javascript" src="${ctx }/js/util/md5.js"></script>
	<script type="text/javascript" src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
	<script>
		(function($, doc) {
			$.ready(function() {
				var winHeight = document.documentElement.clientHeight;
				var loginButton = doc.getElementById('login');
				var accountBox = doc.getElementById('account');
				var passwordBox = doc.getElementById('password');
				var autoLoginButton = doc.getElementById("autoLogin");
				var regButton = doc.getElementById('reg');
				var forgetButton = doc.getElementById('forgetPassword');
				
				var isAutoLogin = false;
				var check = false;
				if(localStorage.getItem("userName_2") != null){
					accountBox.value = localStorage.getItem("userName_2");
				};
				
				mui(".mui-content")[0].style.height = winHeight+"px";
				mui(".mui-content")[0].style.position = "relative";
				$.enterfocus('#login-form input', function() {
					$.trigger(loginButton, 'tap');
				});
				
				//alternative  replace replacement --替代
				//点击登录按钮					
				loginButton.addEventListener('tap', function(event) {
					FromSubmit();
				});
				
				var authLogin = doc.getElementById('wechatlogin');
				if(authLogin != null){
					//点击注册按钮					
					authLogin.addEventListener('tap', function(event) {
						mui.confirm('<span style="font-size: 28px; font-weight:bold; color:red;">警 告</span></br><div style="text-align: left;">由于一个微信号只能绑定一个用户，注册新用户将覆盖原有账号，<span style="font-size: 18px; font-weight:bold; color:red;">由此造成的经济损失，将由用户本人承担！！！</span></br></br><input type="checkbox" id="cr"/><label for="cr">我没有旧账号</label></div>', ' ', ['注册新用户', '<span style="font-weight:bold; color:#333;">返回登录</span>'], function(e) {
							if (e.index == 0) {
								var statue = document.getElementById("cr");
								if(statue.checked){
									mui.ajax('${ctx}/wechat/oauth2/registrationAndBinding.htm',{
										dataType:'json',//服务器返回json格式数据
										type:'post',//HTTP请求类型
										timeout:10000,//超时时间设置为10秒；
										success:function(data){
											//服务器返回响应，根据响应结果，分析是否登录成功；
											if(data.flag){
												mui.toast(data.message);
												//登录成功跳转到首页
												window.location.href="${ctx }/wechat/index/indexInfo.htm";
											}else{
												mui.toast(data.message);
											}
										},
										error:function(xhr,type,errorThrown){
											loading(2);
											//异常处理；
											console.log(type);
											if(type=='timeout'){
												mui.toast("连接超时啦"," ");
											}
										}
									});
								}else{
									mui.toast("请先确认是否有旧账号"); 
								}
							}
						})
					});
				}
				
				// 验证表单
				function checkForm(){
					mui("#login-form input").each(function() {
						var gets = this.value.trim();
						var regspace =/\s/;
						if(!(regspace.exec(this.value)==null) || !this.value || gets == "") {
						    mui.alert("请勿留空或输入空格"," ");
						    check = false;
						    return false;
						}else{
							check = true;
						};
					});								
				};
				
				function FromSubmit(){
					var source=6;
					if(mui.os.iphone!=undefined || mui.os.ipad!= undefined || mui.os.iOS!=undefined){
						source=8;
					}else if(mui.os.android){
						source=7;
					}
					var password = passwordBox.value.trim();
					while(/[\u4E00-\u9FA5]+/.test(password)){
						password = password.replace(/[\u4E00-\u9FA5]+/,""); 
					}
					
					if(password == ""){
						mui.toast("密码不能为空");
						return ;
					}
					
					loading(1);
					mui.ajax('${ctx}/wechat/oauth2/loginAndBind.htm',{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1',
							user : accountBox.value.trim(),
							password : encodeURIComponent(CryptoJS.MD5(password)),
							source : source,
							agentInformation : navigator.appVersion,
						},
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						timeout:10000,//超时时间设置为10秒；
						success:function(data){
							loading(2);
							//服务器返回响应，根据响应结果，分析是否登录成功；
							if(data.flag){
								mui.toast(data.message);
								localStorage.setItem("userName_2",accountBox.value.trim());
								//登录成功跳转到用户中心首页
								window.location.href="${ctx }/wechat/user/userCenter.htm";
							}else{
								if (data.message == "密码错误") {
									mui.confirm('您输入的密码错误，是否通过验证手机或者邮箱进行登录？', ' ', ['是', '否'], function(e) {
										if (e.index == 0) {
											window.location.href="${ctx}/wechat/user/updateLoginPwd.htm";
										}
									})
								}
								mui.toast(data.message);
							}
						},
						error:function(xhr,type,errorThrown){
							loading(2);
							//异常处理；
							console.log(type);
							if(type=='timeout'){
								mui.toast("连接超时啦"," ");
							}
						}
					});
				}
			});
		}(mui, document));
	</script>
</body>

</html>