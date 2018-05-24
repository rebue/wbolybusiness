<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>大卖网络-验证登录</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" id="action_back"></a>
		<h1 class="mui-title">验证手机号登录</h1>
	</header>
	<div class="mui-content">
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row phone-row">
				<input id='userName' type="text" class="mui-input-clear mui-input" placeholder="请输入您的用户名/手机号" data-type="phone">
			</div>
			<div class="mui-input-row phone-row">
				<input id='phone' type="text" class="mui-input-clear mui-input" placeholder="请输入您的手机号码" data-type="phone">
				<button class="mui-btn mui-btn-outlined btn-getcode locked" id="getcode"></button>
			</div>
			<div class="mui-input-row">
				<input id='verification' type="number" class="mui-input-clear mui-input" placeholder="请输入验证码" data-type="verification">
			</div>
		</div>
		<div class="mui-content-padded">
			<button id='goNext' class="main-btn mui-btn mui-btn-block mui-btn-primary">登录</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2018 www.duamai.com</p>
			<p>浏阳市大卖网络科技有限公司   湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
	<script type="text/javascript">
			// 验证手机号码 
			function verifyMobile(){
				var flag = false;
				var regInMobile = /^1[0-9]{10}/;// 手机号的正则表达式
				var regInEmail = new  RegExp("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");// 邮箱的正则表达式
				var mobole = document.getElementById("phone").value.trim();
				var moboles = mobole.trim();
				if(moboles!=null && moboles!=""){
					if(moboles.indexOf("@")!=-1){
						if(regInEmail.test(moboles)){
							return true;
						}else{
							mui.toast('请输入<br/>正确的邮箱格式');
						}
					}else{
						if(regInMobile.test(moboles)){
							return true;
						}else{
							mui.toast('请输入<br/>正确的号码格式');
						}
					}
				}else{
					mui.toast('请输入<br/>手机号码/邮箱');
				}
				return flag;
			};
		
			(function($, doc) {
				$.init();
				$.ready(function() {
					mui('body').on('tap','#regNewUser',function(){document.location.href=this.href;});
					
					var winHeight = document.documentElement.clientHeight;
					var goNext = doc.getElementById('goNext');
					var userName = doc.getElementById('userName'); 
					var phone = doc.getElementById('phone');
					var verification = doc.getElementById('verification');
					var btn = doc.querySelector('#getcode');
					var check = false;
					var cd;
					if(getItem("ctb",1000*60*2) != null){
						cd = setInterval(countdown,1000);
					}else{
						btn.innerHTML = "获取验证码";
						btn.classList.remove("locked");
					};
					mui(".mui-content")[0].style.height = winHeight+"px";
					mui(".mui-content")[0].style.position = "relative";					
					$.enterfocus('#login-form input', function() {
						$.trigger(goNext, 'tap');
					});
					
					//点击下一步按钮					
					goNext.addEventListener('tap', function(event) {
						if(!verifyMobile()){
							return ;
						}

						if(btn.innerHTML=='获取验证码'){
							mui.toast("请获取验证码");
							return ;
						}
						
						if(verification.value.trim()==''){
							mui.toast("请输入验证码");
							return ;
						}
						
						if(userName.value.trim() == ''){
							mui.toast("请输入用户名/手机号");
							return ;
						}

						check = true;
						if(check){
							var updateinfo = "{\"sendType\":\"" + phone.value.trim() + "\",\"code\":\"" + verification.value.trim() + "\"}";
							setItem("updatepayinfo", updateinfo);
							var item = getItem("updatepayinfo", 1000*60*30);
							if(item != null){
								mui.ajax("${ctx}/wechat/oauth2/verifyLoginAndBind.htm",{
									data:{
										userName : userName.value.trim(),
										phone : phone.value.trim(),
										code : verification.value.trim(),
									},
									dataType:'json',//服务器返回json格式数据
									type:'post',//HTTP请求类型
									success:function(data){
										if(data.flag){
											//此处若发送请求成功，才开始执行倒计时,就是以下代码
											clearInterval(cd);
											localStorage.removeItem("ctb");
											btn.innerHTML = cdTime+"秒";
											btn.classList.add("locked");
											cd = setInterval(countdown,1000);
											mui.alert("验证码已发送至您填写的"+title+"</br>请注意查收",' ');
											return ;
										}
										mui.toast(data.message);
									},
									error:function(xhr, type, errorThrown){
										//异常处理；
										console.log(type);
									}
								});
								return ;
							}
							mui.toast("验证信息无效，请重新输入");
						};
					});		
					
					//点击获取验证码
					var cdTime = 120;//倒计时时间
					mui(document).on('tap','#getcode:not(.locked)',function(){
						if(!verifyMobile()){
							return ;
						}
						check = true;
						if(check){
							
							var title = "手机号码"; 
							if(phone.value.trim().indexOf("@")!=-1){
								title = "邮箱";
							}
							loading(1);
							mui.ajax("${ctx}/wechat/user/sendVerificationCode.htm",{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									sendType : phone.value.trim(),
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									loading(2);
									if(data.flag){
										//此处若发送请求成功，才开始执行倒计时,就是以下代码
										clearInterval(cd);
										localStorage.removeItem("ctb");
										btn.innerHTML = cdTime+"秒";
										btn.classList.add("locked");
										cd = setInterval(countdown,1000);
										mui.alert("验证码已发送至您填写的"+title+"</br>请注意查收",' ');
										return ;
									}
									mui.toast(data.message);
								},
								error:function(xhr,type,errorThrown){
									//异常处理；
									console.log(type);
								}
							});
						};
					});
			
					//验证码倒计时
					function countdown(){
						if(getItem("ctb",1000*60*2) != null){
							var countdown = getItem("ctb",1000*60*2);
						}else{
							var countdown = cdTime;
						};	
						countdown--;
						if(countdown<=0){
							clearInterval(cd);
							localStorage.removeItem("ctb");	
							btn.classList.remove("locked");
							btn.innerHTML = "重新获取";
						}else{
							setItem("ctb",countdown);//储存倒计时数字到本地														
							btn.innerHTML = countdown+"秒";
						};						
					};
				});
			}(mui, document));
		</script>
</body>

</html>