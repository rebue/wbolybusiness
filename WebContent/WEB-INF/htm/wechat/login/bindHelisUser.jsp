<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>微薄利商超-绑定</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title">绑定已有账户</h1>
	</header>
	<div class="mui-content">
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row">
				<input id='account' type="text" class="mui-input-clear mui-input"
					placeholder="请输入微薄利用户名/邮箱" data-type="userName">
			</div>
			<div class="mui-input-row phone-row">
				<input id='password' type="password"
					class="mui-input mui-input-password" placeholder="请输入长度为6-16位的密码"
					data-type="password"> <input id='phone' type="number"
					class="mui-input-clear mui-input" placeholder="请输入您的手机号码"
					data-type="phone">
			</div>
		</div>
		<div class="mui-content-padded">
			<button id='goNext'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">确认绑定</button>
			<div class="link-area" style="float: right;">
				<a id='regNewUser' class="mui-pull-left"
					href="${ctx }/wechat/oauth2/regnewuser/page.htm"><h5
						style="color: red;">我没有微薄利商超账号,去注册>>></h5></a>
			</div>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2014-2018 wboly.com</p>
			<p>广西微薄利科技有限公司 桂ICP备16006215号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
	<script type="text/javascript">
			(function($, doc) {
				$.init();
				$.ready(function() {
					var winHeight = document.documentElement.clientHeight;
					var goNext = doc.getElementById('goNext');
					var b = new Base64();
					mui(".mui-content")[0].style.height = winHeight+"px";
					mui(".mui-content")[0].style.position = "relative";		
					
					$.enterfocus('#login-form input', function() {
						$.trigger(goNext, 'tap');
					});
					
					//点击确认按钮					
					goNext.addEventListener('tap', function(event) {
						var name = document.getElementById("account").value.trim();
						if(name == null || name == ""){
							mui.toast("请输入用户名或邮箱");
							return ;
						}
						var password = document.getElementById("password").value.trim();

						while(/[\u4E00-\u9FA5]+/.test(password)){
							password = password.replace(/[\u4E00-\u9FA5]+/,""); 
						}
						password = document.getElementById("password").value = password;
						if(password == null || password == ""){
							mui.toast("请输入登陆密码");
							return ;
						}
						
						var source=6;
						if(mui.os.iphone!=undefined || mui.os.ipad!= undefined || mui.os.iOS!=undefined){
							source=8;
						}else if(mui.os.android){
							source=7;
						}
						loading(1);
						mui.ajax('${ctx}/wechat/oauth2/bindHelis.htm',{
							data:{
								user : name,
								password : b.encode(password),
								source : source
							},
							dataType:'json',// 服务器返回json格式数据
							type:'post',//HTTP请求类型
							success:function(data){
								loading(2);
								if(data.flag){
									mui.alert(data.message,' ',function(){
										window.location.href="${ctx}/wechat/user/userCenter.htm";
									});
									return ;
								}
								
								if(data.alert!=undefined){
									if(data.alert==2){
										mui.alert(data.message,' ',function(){
											window.location.href="${ctx}/wechat/user/userCenter.htm";
										});
										return ;
									}
									document.getElementById("account").value="";
									document.getElementById("password").value="";
								}
								mui.toast(data.message);
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
					});		
				});
			}(mui, document));
		</script>
</body>
</html>