<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>微薄利商超-注册</title>
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
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row">
				<input id='account' type="text" class="mui-input-clear mui-input"
					placeholder="请输入用户名" data-type="userName">
			</div>
			<div class="mui-input-row phone-row">
				<input id='phone' type="number" class="mui-input-clear mui-input"
					placeholder="请输入您的手机号码" data-type="phone">
				<button class="mui-btn mui-btn-outlined btn-getcode locked"
					id="getcode"></button>
			</div>
			<div class="mui-input-row">
				<input id='verification' type="number"
					class="mui-input-clear mui-input" placeholder="请输入验证码"
					data-type="verification">
			</div>
		</div>
		<div class="mui-content-padded">
			<button id='goNext'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">下一步</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2014-2018 wboly.com</p>
			<p>广西微薄利科技有限公司 桂ICP备16006215号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script>
		
		// 验证用户名
		function verifyName(){
			var name = document.getElementById("account").value.trim();
			var flag=false;
			if(name !=null && name!=''){
				var purenum = /^[0-9]*$/;
				var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g; 
				if(!special.test(name) && !purenum.test(name)){
					mui.ajax('${ctx}/wechat/user/verifyName.htm',{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1',
							name:name
						},
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						timeout:10000,//超时时间设置为10秒；
						async:false,
						success:function(data){
							if(data.result==0){
								mui.alert('该用户名已被使用<br>请重新输入'," ");
							}else{
								flag=true;
							}
						},
						error:function(xhr,type,errorThrown){
							//异常处理；
							console.log(type);
						}
					});
				}else{
					mui.alert('用户名不能包含特殊字符<br>并且不可为纯数字'," ");
				}
			}else{
				mui.alert('用户名不能为空'," ");
			}
			return flag;
		}
		
		// 验证手机号码
		function verifyMobile(){
			var flag=false;
			var reg=/^13[0-9]{9}$|14[0-9]{9}$|15[0-9]{9}$|17[0-9]{9}$|18[0-9]{9}$/;
			var mobole=document.getElementById("phone").value.trim();
			if(mobole!=null&&mobole!=""){
				if(reg.test(mobole)){
					flag=true;
				}else{
					mui.alert('请输入正确的手机号码'," ");
				}
			}else{
				mui.alert('手机号码不能为空'," ");
			}
			return flag;
		}
			
			(function($, doc) {
				$.init();
				$.ready(function() {					
					var winHeight = document.documentElement.clientHeight;
					var accountBox = doc.getElementById('account');
					var goNext = doc.getElementById('goNext');
					var phone = doc.getElementById('phone');
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
						//checkForm();
						if(verifyName()){
							if(verifyMobile()){
								check=true;
							}else{
								return ;
							}
						}else{
							return ;
						}

						if(btn.innerHTML=='获取验证码'){
							mui.alert("请获取验证码","");
							return ;
						}
						if(verification.value.trim()==''){
							mui.alert("请输入验证码","");
							return ;
						}
						
						if(check){
							//提交验证请求，验证成功后，再执行下面这些：
							localStorage.removeItem('ctd');
							localStorage.setItem("userName_1",accountBox.value.trim());
							localStorage.setItem("code",verification.value.trim());
							localStorage.setItem("mobile",phone.value.trim());
							window.location.href="${ctx}/wechat/user/NextStepPage.htm";
							//调试完成后就不用下面的弹窗了，验证成功就直接转到下一步的页面
						   /*  mui.alert('验证通过'+'<br>'
						    +'用户名：'+accountBox.value.trim()+'<br>'
						    +'手机：'+phone.value.trim()+'<br>'
						    +'验证码：'+verification.value.trim()+'<br>'
						    ," ","下一步",function(){
						    	window.location.href="${ctx}/wechat/user/NextStepPage.htm";//去到下一步的页面	
						    }); */
						};
					});		
					
					//点击获取验证码
					var cdTime = 180;//倒计时时间
					mui(document).on('tap','#getcode:not(.locked)',function(){	
						if(verifyName()){
							if(verifyMobile()){
								check=true;
							}else{
								return ;
							}
						}else{
							return ;
						}
						if(check){
							mui.ajax('${ctx}/wachat/user/getRegisterCodeInSms.htm',{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									mobile:phone.value.trim(),
									name:accountBox.value.trim()
								},
								async:false,
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								timeout:10000,//超时时间设置为10秒；
								success:function(data){
									if(data.flag){
										//此处若发送请求成功，才开始执行倒计时,就是以下代码
										mui.alert("验证码已发送至您填写的手机号码，请注意查收"," ");
										clearInterval(cd);
										localStorage.removeItem("ctd");
										btn.innerHTML = cdTime+"秒";
										btn.classList.add("locked");
										cd = setInterval(countdown,1000);
										
									}else{
										mui.alert(data.message," ");
									}
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
							//console.log(localStorage.getItem("ctd"));
						};						
					};
					
					//验证表单
					function checkForm(){
						mui("#login-form input").each(function() {
							var type = this.getAttribute("data-type");
							var purenum = /^[0-9]*$/;
	      					var regspace =/\s/;
	      					var mobile=/^13[0-9]{9}$|14[0-9]{9}$|15[0-9]{9}$|17[0-9]{9}$|18[0-9]{9}$/;
	      					var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\\s+]/g; 
	      					var gets = this.value.trim();
	      					var length = gets.replace(/[^\x00-\xff]/g, "**").length;
	      					if(!(regspace.exec(this.value)==null) || !this.value || gets == ""){
								    mui.alert("请勿留空或包含空格"," ");
								    check = false;
								    return false;
							}else if(type == "userName"){															 
								if(special.test(gets) || purenum.test(gets)){
						    		mui.alert('用户名不能包含特殊字符<br>并且不可为纯数字'," ");
						    		check = false;
								    return false;
						    	}else if(length<4 || length>14){
						    		mui.alert('用户名长度为4-14个字符（汉字占2字符）'," ");
						    		check = false;
								    return false;
						    	}else{
						    		check = true;
						    	};
							}else if(type == "phone"){
								if(!(mobile.test(gets))){
						    		mui.alert('请输入正确的手机号码'," ");
						    		check = false;
								    return false;
						    	}else{
						    		check = true;
						    	}
							};							
						});								
					};
					
				});
			}(mui, document));
		</script>
</body>

</html>