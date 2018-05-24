<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="wap-font-scale" content="no">
<meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1,maximum-scale=1,user-scalable=no" />
<title>大卖网络</title>
<link href="${ctx }/css/wechat/mui.min.css" rel="stylesheet" />
<link href="${ctx }/css/wechat/account.css" rel="stylesheet" />
</head>

<body class="account-page">
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" id="action_back"></a>
		<h1 class="mui-title">设置登录名称</h1>
	</header>
	<div class="mui-content">
		<div id='login-form' class="mui-input-group">
			<div class="mui-input-row phone-row">
				<input id='loginName' type="text" class="mui-input-clear mui-input" placeholder="请输入您的登录名称">
			</div>
		</div>
		<div class="mui-content-padded">
			<button id='goNext' class="main-btn mui-btn mui-btn-block mui-btn-primary">确定</button>
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
		(function($, doc) {
			doc.getElementById("loginName").value = "${loginName}";
			mui(document).on("tap", "#goNext", function(e) {
				var loginName = doc.getElementById('loginName').value;
				console.log(loginName);
				if (loginName == null || loginName == ""
						|| loginName == "null") {
					mui.toast('请输入登录名称');
					return;
				}
				mui.ajax('${ctx}/wechat/user/setLoginName.htm', {
					data : {
						loginName : loginName.trim()
					},
					dataType : 'json',//服务器返回json格式数据
					type : 'post',//HTTP请求类型
					success : function(data) {
						if (data.result == 1) {
							mui.toast(data.msg);
							window.location.href = "${ctx }/wechat/user/userCenter.htm";
						} else {
							mui.toast(data.msg);
						}
					},
					error : function(xhr, type, errorThrown) {
						//异常处理；
						console.log(type);
					}
				});
			});
		})(mui, document);
	</script>
</body>

</html>