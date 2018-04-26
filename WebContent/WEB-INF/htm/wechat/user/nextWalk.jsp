<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>微薄利商超-重置支付密码</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title">重置支付密码</h1>
	</header>
	<div class="mui-content">
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row phone-row">
				<input id='password' type="password"
					class="mui-input mui-input-password" placeholder="请输入长度为6-16位的密码"
					data-type="password">
			</div>
		</div>
		<p class="psw-tip">该密码可用于V支付提现操作，请务必牢记！</p>
		<div class="mui-content-padded">
			<button id='goNext'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">确定</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2016-2018 wboly.com</p>
			<p>广西微薄利科技有限公司 桂ICP备16006215号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
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
			
			(function($, doc) {
				$.init();
				$.ready(function() {
					
					mui('body').on('tap','#regNewUser',function(){document.location.href=this.href;});
					
					var winHeight = document.documentElement.clientHeight;
					var goNext = doc.getElementById('goNext');
					var password = doc.getElementById('password');
					var check = false;
					mui(".mui-content")[0].style.height = winHeight+"px";
					mui(".mui-content")[0].style.position = "relative";					
					$.enterfocus('#login-form input', function() {
						$.trigger(goNext, 'tap');
					});
					
					//点击下一步按钮					
					goNext.addEventListener('tap', function(event) {

						if(!verifyPwd()){
							return ;
						}
						var item = getItem("updatepayinfo",1000*60*30);
						if(item == null || item==""){
							mui.toast("验证信息已过期</br>返回上一步重新输入");
							return ;
						}
						check = true;
						if(check){
							loading(1);
							mui.ajax("${ctx}/wechat/user/updatepaypwdsubmit.htm",{
								data:{
									key:'59c23bdde5603ef993cf03fe64e448f1',
									item : JSON.stringify(item),
									password : password.value.trim()
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									loading(2);
									if(data.flag){
										localStorage.removeItem("updatepayinfo");
										//提交验证请求，验证成功后，再执行下面这些：
										mui.alert("修改成功",' ',function(){
											var url = localStorage.getItem("payurl");
											if(url == null || url==""){
												url ="${ctx }/wechat/order/myOrders.htm";
											}
											window.location.href=url;
										});
										return ;
									}
									if(data.prompt!=undefined){
										if(data.prompt==2){
											mui.alert(data.message,' ',function(){
												window.location.href=localStorage.getItem("payurl");
											});
											return ;
										}
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
				});
			}(mui, document));
		</script>
</body>

</html>