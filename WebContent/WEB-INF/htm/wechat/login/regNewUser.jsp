<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>大卖网络-注册</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title">注册新账号</h1>
	</header>
	<div class="mui-content">
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row">
				<input id='account' type="text" class="mui-input-clear mui-input"
					placeholder="请输入用户名" data-type="userName">
			</div>
			<div class="mui-input-row phone-row">
				<input id='password' type="password"
					class="mui-input mui-input-password" placeholder="请输入长度为6-16位的密码"
					data-type="password">
			</div>
			<div class="mui-input-row phone-row">
				<input id='phone' type="text" class="mui-input-clear mui-input"
					placeholder="请输入您的手机号码/邮箱" data-type="phone">
				<button class="mui-btn mui-btn-outlined btn-getcode locked"
					id="getcode"></button>
			</div>
			<div class="mui-input-row">
				<input id='verification' type="number"
					class="mui-input-clear mui-input" placeholder="请输入验证码"
					data-type="verification">
			</div>
		</div>
		<p class="psw-tip">该密码可用于登录与V支付提现操作，请务必牢记！</p>
		<div class="mui-content-padded">
			<button id='goNext'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">注册</button>
			<div class="link-area" style="float: right;">
				<a id='regNewUser' class="mui-pull-left"
					href="${ctx }/wechat/oauth2/bind/user/page.htm"><h5
						style="color: red;">我已有大卖网络账号,去绑定>>></h5></a>
			</div>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2018 www.duamai.com</p>
			<p>浏阳市大卖网络科技有限公司   湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script type="text/javascript">
		
			//验证密码是否符合规范
			function verifyPwd(){
				var purenum = /^[0-9]*$/;
				var regspace =/\s/;
				var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g; 
				var gets = document.getElementById("password").value.trim();
				var length = gets.length;
				var flag = false;
				if(!(regspace.exec(gets) == null) || gets == ""){
					mui.toast("密码请勿留空或包含空格");
				}else if(special.test(gets) || purenum.test(gets)){
			    	mui.toast('密码不能包含特殊字符并且不可为纯数字');
			    }else if(length<6 || length>16){
			    	mui.toast('密码长度为6-16个字符');
			    }else{
			    	flag = true;
			    };	
			    return flag;
			};
			
			// 检查用户名
			function verifyName(){
				var name = document.getElementById("account").value.trim();
				var flag = false;
				if(name !=null && name!=''){
					var purenum = /^[0-9]*$/;
					var special = /[~'\`\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g; 
					if(!special.test(name) && !purenum.test(name)){
						return verifyFun(name);
					}else{
						mui.toast('用户名不能包含特殊字符并且不可为纯数字');
					}
				}else{
					mui.toast('请输入用户名');
				}
				return flag;
			};
			
			// 验证手机号码 
			function verifyMobile(){
				var flag = false;
				var regInMobile = /^13[0-9]{9}$|14[0-9]{9}$|15[0-9]{9}$|17[0-9]{9}$|18[0-9]{9}$/;// 手机号的正则表达式
				var regInEmail = new  RegExp("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");// 邮箱的正则表达式
				var mobole = document.getElementById("phone").value.trim();
				if(mobole!=null && mobole!=""){
					if(mobole.indexOf("@")!=-1){
						if(regInEmail.test(mobole)){
							return verifyFun(mobole);
						}else{
							mui.toast('请输入<br/>正确的邮箱格式');
						}
					}else{
						if(regInMobile.test(mobole)){
							return verifyFun(mobole);
						}else{
							mui.toast('请输入<br/>正确的号码格式');
						}
					}
				}else{
					mui.toast('请输入<br/>手机号码/邮箱');
				}
				return flag;
			};
			
			// 验证手机号/邮箱/用户名的方法
			function verifyFun(name){
				var flag = false;
				mui.ajax('${ctx}/wechat/user/verifyName.htm',{
					data:{
						key:'59c23bdde5603ef993cf03fe64e448f1',
						name:name
					},
					dataType:'json',//服务器返回json格式数据
					type:'post',//HTTP请求类型
					async:false,
					success:function(data){
						if(data.result==0){
							mui.toast(data.info);
						}else{
							flag = true;
						}
					},
					error:function(xhr,type,errorThrown){
						//异常处理；
						console.log(type);
					}
				});
				return flag;
			};
		
			
			(function($, doc) {
				$.init();
				$.ready(function() {
					
					mui('body').on('tap','#regNewUser',function(){document.location.href=this.href;});
					
					mui.ajax("${ctx}/wechat/oauth2/user/getname.htm",{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1'
						},
						async:false,
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						success:function(data){
							if(data.flag){
								doc.getElementById('account').value = data.message;
								return ;
							}
							doc.getElementById('account').value = data.message;
						},
						error:function(xhr,type,errorThrown){
							//异常处理；
							console.log(type);
						}
					});
					
					var accountBox = doc.getElementById('account');
					var winHeight = document.documentElement.clientHeight;
					var goNext = doc.getElementById('goNext');
					var phone = doc.getElementById('phone');
					var password = doc.getElementById('password');
					var verification = doc.getElementById('verification');
					var btn = doc.querySelector('#getcode');
					var check = false;
					var cd;
					if(localStorage.getItem("ctd") != null){
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
						if(!verifyName()){
							return ;
						}
						if(!verifyPwd()){
							return ;
						}
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
						
						var source=1;
						if(mui.os.iphone!=undefined || mui.os.ipad!= undefined || mui.os.iOS!=undefined){
							source=5;
						}else if(mui.os.android){
							source=4;
						}
						check = true;
						if(check){
							mui.ajax("${ctx}/wechat/oauth2/register/user/submit.htm",{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									registerType : phone.value.trim(),
									username : accountBox.value.trim(),
									source : source,
									code : verification.value.trim(),
									password : password.value.trim()
								},
								async:false,
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									console.log(data);
									if(data.flag){
										//提交验证请求，验证成功后，再执行下面这些：
										mui.alert("注册成功",' ',function(){
											window.location.href="${ctx}/wechat/user/userCenter.htm";
										});
										return ;
									}
									mui.toast(data.message);
								},
								error:function(xhr,type,errorThrown){
									//异常处理；
									console.log(type);
								}
							});
							localStorage.clear();
						};
					});		
					
					//点击获取验证码
					var cdTime = 120;//倒计时时间
					mui(document).on('tap','#getcode:not(.locked)',function(){
						if(!verifyName()){
							return ;
						}
						if(!verifyPwd()){
							return ;
						}
						if(!verifyMobile()){
							return ;
						}
						check = true;
						if(check){
							var title = "手机号码"; 
							var codeUrl ="${ctx}/wachat/user/getRegisterCodeInSms.htm";
							if(phone.value.trim().indexOf("@")!=-1){
								title = "邮箱";
								codeUrl ="${ctx}/wachat/user/getRegisterCodeInEmail.htm";
							}
							
							mui.ajax(codeUrl,{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									sendType:phone.value.trim(),
									name:accountBox.value.trim()
								},
								async:false,
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									if(data.flag){
										//此处若发送请求成功，才开始执行倒计时,就是以下代码
										clearInterval(cd);
										localStorage.removeItem("ctd");
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
						if(localStorage.getItem("ctd") != null){
							var countdown = localStorage.getItem("ctd");
						}else{
							var countdown = cdTime;
						};	
						countdown--;
						if(countdown<=0){
							clearInterval(cd);
							localStorage.removeItem("ctd");	
							btn.classList.remove("locked");
							btn.innerHTML = "重新获取";
						}else{
							localStorage.setItem("ctd",countdown);//储存倒计时数字到本地														
							btn.innerHTML = countdown+"秒";
						};						
					};
					
				});
			}(mui, document));
		</script>
</body>

</html>