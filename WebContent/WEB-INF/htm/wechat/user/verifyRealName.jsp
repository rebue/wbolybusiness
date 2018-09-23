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

<body class="account-page">
	<input type="hidden" id="verifyUserId" value="${userId}" / >
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title">实名认证申请</h1>
	</header>
	<div class="mui-content">
		<div class="mui-input-group">
			<div class="mui-input-row phone-row">
				<label>名字</label> <input id='realName' type="text" style="margin-top:-0.3rem;"
					class="mui-input-clear mui-input" placeholder="请输入您的真实名字">
			</div>
		</div>
		<div class="mui-input-group">
			<div class="mui-input-row phone-row">
				<label style="width:22%;padding-right:0rem;" >身份证</label> <input id='idCard' style="margin-top:-0.3rem;" type="text"
					class="mui-input-clear mui-input" placeholder="请输入您的身份证号码">
			</div>
		</div>
		<div class="img-upload-box mui-table-view-cell">
			<div id="container">
				<!--头部，相册选择和格式选择 -->
				<div id="uploader">
					<div class="queueList">
						<div id="dndArea" class="placeholder">
							<div id="filePicker"></div>
						</div>
					</div>
					<div class="statusBar" style="display: none;">
						<div class="progressBox">
							<div class="progress">
								<span class="text">0%</span> <span class="percentage"></span>
							</div>
							<div class="info"></div>
						</div>
						<div class="btns">
							<div id="filePicker2"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="mui-content-padded" id="ulbtn-box">
			<button id='uploadBtn'
				class="main-btn mui-btn mui-btn-block mui-btn-primary">提交申请</button>
		</div>
		<div class="mui-content-padded btm-area">
			<p>Copyright © 2018 www.duamai.com</p>
			<p>浏阳市大卖网络科技有限公司 湘ICP备18005719号-1</p>
		</div>
	</div>
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.enterfocus.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
	<script type="text/javascript" src="${ctx }/js/wechat/upload.js"></script>
	<script type="text/javascript">
		var label = "点击上传手持身份证正反面以及身份证正反面照片";
		var NumLimit = 4;
		var SizeLimit = 9 * 1024 * 1024; // 15 M     
		var SingleSizeLimit = 3 * 1024 * 1024; // 3 M 
		// 上传图片
		var arr = new Array();
		var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
		var serverUrl = 'https://www.duamai.com/ise-svr/ise/upload'; 
		(function($, doc) {
			mui.init();
			$.ready(function() {
						mui('#opt_main').on('tap', 'a', function() {
							document.location.href = this.href;
						});
						mui('#opt_main.mui-scroll-wrapper').scroll({
							bounce : false
						});

						window.addEventListener('touchmove', function(e) {
							var target = e.target;
							if (target && target.tagName === 'TEXTAREA') { //textarea阻止冒泡
								e.stopPropagation();
							}
						}, true);

						//焦点BUG
						document.addEventListener('tap', function(e) {
							var textareas = document
									.querySelectorAll("textarea");
							for (var i = 0; i < textareas.length; i++) {
								textareas[i].blur();
							}
							;
						});

						//可选择菜单
						var info = document.getElementById("r-info");
						mui(document).on(
										'tap',
										'.mui-popover-action .btn-list li>a',
										function() {
											var a = this, parent;
											//根据点击按钮，反推当前是哪个actionsheet
											for (parent = a.parentNode; parent != document.body; parent = parent.parentNode) {
												if (parent.classList.contains('mui-popover-action')) {
													break;
												}
											}
											//关闭actionsheet
											mui('#' + parent.id).popover(
													'toggle');
											info.innerHTML = a.innerHTML;
											returnType = a.getAttribute("data-value");
										});

						mui(document).on("tap", "#uploadBtn", function() {
							if(mui("#realName")[0].value==''){
								mui.toast("请填写您的真名！");
							}else if(mui("#idCard")[0].value==''){
								mui.toast("请填写您的身份证号！");
							}else if(reg.test(mui("#idCard")[0].value)==false){
								mui.toast("输入的身份证号不合法！");
							}else{
								if (mui(".filelist img").length > 0) {
									 uploader.upload(); //在uploader的上传成功事件里触发ajaxupdate();
								} else {
									mui.toast("请上传认证照片");
								}
							};
						});
					});
		})(mui, document);
		
		function ajaxupdate() {
			var mask = mui.createMask();
			mask.show();
			var realName = mui("#realName")[0].value;
			var idCard = mui("#idCard")[0].value;
			mui.ajax('${ctx}/wechat/user/verifyRealNameApply.htm', { //url测试的
				type : 'post',
				dataType : 'json',
				data : {
					realName : realName,
					idCard : idCard,
					pic: arr.join(",")
				},
				success : function(data) {
					mask.close();
					console.log(data);
					if (data.result == 1) {
						mui.alert(data.msg, ' ', function() {
							window.location.href = "${ctx}/wechat/user/userCenter.htm?promoterId=${userId}"
						});
						return;
					};
					mui.toast(data.msg);
				},
				error : function() {
					mask.close();
					mui.alert("提交失败");
				}
			});
		};
	</script>

</body>

</html>