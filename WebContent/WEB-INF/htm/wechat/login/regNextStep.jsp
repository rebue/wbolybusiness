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
		<h1 class="mui-title">注册</h1>
	</header>
	<div class="mui-content">
		<form id='login-form' class="mui-input-group">
			<div class="mui-input-row phone-row">
				<input id='password' type="password"
					class="mui-input mui-input-password" placeholder="请输入长度为6-16位的密码"
					data-type="password"> <input id='phone' type="number"
					class="mui-input-clear mui-input" placeholder="请输入您的手机号码"
					data-type="phone">
			</div>
		</form>
		<p class="psw-tip">该密码可用于登录与V支付提现操作，请务必牢记！</p>
		<div class="mui-content-padded">
			<button id='goReg'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">注册</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2018 www.duamai.com</p>
			<p>浏阳市大卖网络科技有限公司   湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script>
			(function($, doc) {
				$.init();
				$.ready(function() {			
					var winHeight = document.documentElement.clientHeight;
					var psw = doc.getElementById('password');
					var goReg = doc.getElementById('goReg');
					var check = false;
					mui(".mui-content")[0].style.height = winHeight+"px";
					mui(".mui-content")[0].style.position = "relative";					
					$.enterfocus('#login-form input', function() {
						$.trigger(goNext, 'tap');
					});
					
					//点击注册按钮
					goReg.addEventListener('tap', function(event) {
						checkForm();
						if(check){
							var source=6;
							if(mui.os.iphone!=undefined || mui.os.ipad!= undefined || mui.os.iOS!=undefined){
								source=7;
							}else if(mui.os.android){
								source=8;
							}
							mui.ajax('${ctx}/wechat/user/smsregister.htm',{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									user:localStorage.getItem("userName_1"),
									mobile:localStorage.getItem("mobile"),
									code:localStorage.getItem("code"),
									password:mui("#password")[0].value,
									source:source
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								timeout:10000,//超时时间设置为10秒；
								success:function(data){
									if(data.flag){
										//提交请求，注册成功后，再执行下面这些：
								    	localStorage.removeItem("mobile");
								    	localStorage.removeItem("code"); 
										var userName_2 = localStorage.getItem("userName_1");
									    localStorage.setItem("userName_2",userName_2);	//储存此注册成功的用户名到本地	
									   
									    //功能都做好后就不用确认框这一步了，注册成功就转到登录页面
									    mui.confirm('注册成功'," ",['去登录'],function(e){	 
									    	if (e.index == 0) {						    		
												window.location.href="${ctx}/wechat/user/logInPage.htm";//去到登录页面							
											}; 
									    });
									}else{
										mui.alert(data.message,"");
									}
								},
								error:function(xhr,type,errorThrown){
									//异常处理；
									console.log(type);
								}
							});
						};
					});										
					
					//验证表单
					function checkForm(){
						var purenum = /^[0-9]*$/;
      					var regspace =/\s/;
      					var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g; 
      					var gets = mui("#password")[0].value;
      					var length = gets.replace(/[^\x00-\xff]/g, "**").length;
      					if(!(regspace.exec(gets)==null) || !gets || gets == ""){
							mui.alert("请勿留空或包含空格"," ");
							check = false;
							return false;
						}else if(special.test(gets) || purenum.test(gets)){
					    	mui.alert('密码不能包含特殊字符<br>并且不可为纯数字'," ");
					    	check = false;
							return false;
					    }else if(length<6 || length>16){
					    	mui.alert('密码长度为6-16个字符'," ");
					    	check = false;
							return false;
					    }else{
					    	check = true;
					    };							
					};
				});
			}(mui, document));
		</script>
</body>
</html>