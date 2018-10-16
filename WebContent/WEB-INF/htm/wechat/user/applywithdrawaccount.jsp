<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/operation.css" />
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/webuploader.css" />
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/jquery.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/wechat/webuploader.html5only.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">申请提现账号</h4>
	</header>
	<div id="opt_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">账户名称：</span> 
				<input style="border: 0px; outline: none; cursor: pointer; font-size: 0.875rem" name="bankAccountName" id="bankAccountName" placeholder="支付宝/银行卡真实姓名"/>
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">联系方式：</span> 
				<input style="border: 0px; outline: none; cursor: pointer; font-size: 0.875rem" name="contactTel" id="contactTel" placeholder="填写错误概不负责"/>
			</div>
			<div class="info-bar mui-table-view-cell">
				<a href="#sheet" class="arrowdown mui-navigate-right"> 
					<span class="mui-pull-left">提现账号类型：</span> 
					<span class="r-info mui-pull-left" id="r-info">支付宝</span>
				</a>
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">提现账号：</span> 
				<input oninput = "value=value.replace(/[^\d]/g,'')" style="border: 0px; outline: none; cursor: pointer; font-size: 0.875rem" name="bankAccountNo" id="bankAccountNo" placeholder="支付宝/银行账号(填写错误概不负责)" />
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">开户银行：</span> 
				<input style="border: 0px; outline: none; cursor: pointer; font-size: 0.875rem" name="openAccountBank" id="openAccountBank" placeholder="支付宝用户不用填写" />
			</div>
		</div>
	</div>
	<div id="sheet" class="mui-popover mui-popover-bottom mui-popover-action ">
		<!-- 可选择菜单 -->
		<ul class="mui-table-view btn-list">
			<li class="mui-table-view-cell"><a data-value="2">支付宝</a></li>
			<li class="mui-table-view-cell"><a data-value="1">银行卡</a></li>
		</ul>
		<!-- 取消菜单 -->
		<ul class="mui-table-view">
			<li class="mui-table-view-cell"><a href="#sheet"><b>取消</b></a></li>
		</ul>
	</div>
	<footer class="mui-bar" id="ulbtn-box">
		<button class="mui-pull-right" id="uploadBtn">提交申请</button>
	</footer>

	<script type="text/javascript" charset="utf-8">
		// 提现类型
		var withdrawType = 2;

		(function($, doc) {
			mui.init();
			$.ready(function() {
				mui('#opt_main').on('tap','.order-info-box a',function(){document.location.href=this.href;});
				mui('#opt_main.mui-scroll-wrapper').scroll({ bounce: false });

				window.addEventListener('touchmove', function(e) {
					var target = e.target;
					if(target && target.tagName === 'TEXTAREA') { //textarea阻止冒泡
						e.stopPropagation();
					}
				}, true);

				//焦点BUG
				document.addEventListener('tap', function(e) {
					var textareas = document.querySelectorAll("textarea");
					for(var i = 0; i < textareas.length; i++) {
						textareas[i].blur();
					};
				});

				//可选择菜单
				var info = document.getElementById("r-info");
				mui(document).on('tap', '.mui-popover-action .btn-list li>a', function() {
					var a = this,
						parent;
					//根据点击按钮，反推当前是哪个actionsheet
					for(parent = a.parentNode; parent != document.body; parent = parent.parentNode) {
						if(parent.classList.contains('mui-popover-action')) {
							break;
						}
					}
					//关闭actionsheet
					mui('#' + parent.id).popover('toggle');
					info.innerHTML = a.innerHTML;
					withdrawType = a.getAttribute("data-value");
				});
			});
			
			// 提交申请提现账号信息
			mui(document).on("tap", "#ulbtn-box", function() {
				// 账号名称
				var bankAccountName = mui("#bankAccountName")[0].value;
				console.log(bankAccountName);
				// 联系方式
				var contactTel = mui("#contactTel")[0].value;
				console.log(contactTel);
				// 提现账号
				var bankAccountNo = mui("#bankAccountNo")[0].value;
				console.log(bankAccountNo);
				// 开户银行
				var openAccountBank = mui("#openAccountBank")[0].value;
				console.log(openAccountBank);
				mui.ajax('${ctx}/wechat/user/submitApplyWithdrAwaccount.htm', {
					type : 'post',
					dataType : 'json',
					data : {
						bankAccountName : bankAccountName,
						contactTel : contactTel,
						withdrawType : withdrawType,
						bankAccountNo : bankAccountNo,
						openAccountBank : openAccountBank,
					},
					success : function(data) {
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
						mui.alert("提交失败");
					}
				});
			});
		})(mui, document);
	</script>
</body>
</html>