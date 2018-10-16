<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/usercenter.css" />
		<script src="${ctx }/js/wechat/mui.min.js"></script>
		<script src="${ctx }/js/util/commonUtil.js"></script>
		<script src="${ctx }/js/util/base64.js"></script>
		<script type="text/javascript" src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
	</head>
	<script type="text/javascript">
		$(function () {
			$("#bankCanCarry").html("当前可用金额：${balance}元，本月已提现${withdrawNumber}次，总共可提现${balance}元");
			$("#alipayCanCarry").html("当前可用金额：${balance}元，本月已提现${withdrawNumber}次，总共可提现${balance}元");
			var withdrawType = "${withdrawType}";
			var bankAccountNo = "${bankAccountNo}";
			var bankAccountName = "${bankAccountName}";
			var contactTel = "${contactTel}";
			var openAccountBank = "${openAccountBank}";
			var orderId = "${orderId}";
			var id = "${id}";
			var withdrawNumber = "${withdrawNumber}";
			console.log("${seviceCharge}")
			if (withdrawNumber > 0) {
				$(".seviceCharge").show();
			} else {
				$(".seviceCharge").remove();
			}
			if (withdrawType != 0) {
				if (withdrawType == 2) {
					$("#alipayName").val(bankAccountName);
					$("#alipayAccount").val(bankAccountNo);
					$("#alipayTelephone").val(contactTel);
					
					$("#alipayName").attr("disabled", true);
					$("#alipayAccount").attr("disabled", true);
					$("#alipayTelephone").attr("disabled", true);
					$(".divTop2").remove();
					$("#bankCard").remove();
				}
				
				if (withdrawType == 1) {
					$("#bankName").val(bankAccountName);
					$("#bankAccount").val(bankAccountNo);
					$("#openBank").val(openAccountBank);
					$("#bankCall").val(contactTel);
					
					$("#bankName").attr("disabled", true);
					$("#bankAccount").attr("disabled", true);
					$("#openBank").attr("disabled", true);
					$("#bankCall").attr("disabled", true);
					$(".divTop1").remove();
					$("#payTreasure").remove();
				}
			}
			
			$(document).on("tap", "#payTreasure", function() {
				$(".divTop1").show();
				$(".divTop2").hide();
			})
			
			$(document).on("tap", "#bankCard", function() {
				$(".divTop2").show();
				$(".divTop1").hide();
			})
			
			// 支付宝提现提交
			$(document).on("tap", "#alipaySubmit", function() {
				var alipayAmount = $("#alipayAmount").val();
				// 验证只能输入大于0的正整数或者保留两位小数的正小数
				var reg = /^([1-9]\d*(\.\d*[1-9])?)|(0\.\d*[1-9])$/;
				console.log(reg.test(alipayAmount));
				if (!reg.test(alipayAmount)) {
					mui.toast("金额只能输入大于0的整数或者保留两位小数!");
					return ;
				}
				$.ajax({
					url : "${ctx}/wechat/user/withdrawalApplicationSubmit",
					type : "post",
					dataType:'json',//服务器返回json格式数据
					data : {id : id, withdrawType : withdrawType, bankAccountNo : bankAccountNo, alipayAmount : alipayAmount,
						bankAccountName : bankAccountName, contactTel : contactTel, orderId : orderId},
					success:function(data){
						console.log(data)
						if (data.flag == true || data.flag == "true") {
							mui.toast(data.msg);
							window.location.href="${ctx }/wechat/user/wechatWithdraw.htm?promoterId=${userId}";
						} else {
							mui.toast(data.msg);
						}
					}
				})
			})
			
			// 银行卡提现提交
			$(document).on("tap", "#bankSubmit", function() {
				var bankAmount = $("#bankAmount").val();
				// 验证只能输入大于0的正整数或者保留两位小数的正小数
				var reg = /^([1-9]\d*(\.\d*[1-9])?)|(0\.\d*[1-9])$/;
				console.log(bankAmount);
				console.log(reg.test(bankAmount));
				if (!reg.test(bankAmount)) {
					mui.toast("金额只能输入大于0的整数或者保留两位小数!");
					return ;
				}
				$.ajax({
					url : "${ctx}/wechat/user/withdrawalApplicationSubmit",
					type : "post",
					dataType:'json',//服务器返回json格式数据
					data : {id : id, withdrawType : withdrawType, bankAccountNo : bankAccountNo, bankAmount : bankAmount,
						bankAccountName : bankAccountName, contactTel : contactTel, openAccountBank : openAccountBank, orderId : orderId},
					success:function(data){
						console.log(data)
						if (data.flag == true || data.flag == "true") {
							mui.toast(data.msg);
							window.location.href="${ctx }/wechat/user/wechatWithdraw.htm?promoterId=${userId}";
						} else {
							mui.toast(data.msg);
						}
					}
				})
			})
		})
	</script>
	<style type="text/css">
		.divTop1 { mcolor: #555; padding: 6px; margin-top: 3px; }
		.divTop2 { mcolor: #555; padding: 6px; margin-top: 3px; }
		input { -webkit-user-select:auto }
	</style>
	<body>
		<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
			<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
			<h4 class="mui-title">我的提现</h4>
			<h5 class="mui-title" style="left: auto; font-size: 15px;">提现记录</h5>
		</header>
		<div id="wallet_main">
			<div class="mui-content">
				<div class="mui-slider tab-slider">
					<div id="sliderSegmentedControl"
						class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
						<div class="mui-control-item mui-active" id="payTreasure">转出到支付宝</div>
						<div class="mui-control-item" id="bankCard">转出到银行卡</div>
					</div>
				</div>
				<div class="divTop1">
					<div class="divTop1" style="font-size: 14px; color: #555;" id="alipayCanCarry"></div>
					<div class="divTop1">
						<label style="color: #555;">账户姓名：</label> <input id="alipayName" />
					</div>
					<div class="divTop1">
						<label style="color: #555;">账&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</label> 
						<input id="alipayAccount" />
					</div>
					<div class="divTop1">
						<label style="color: #555;">移动电话：</label> 
						<input id="alipayTelephone" />
					</div>
					<div class="divTop1" style="font-size: 14px; color: #555;">银行卡2个工作日内到账，支付宝5个工作日内到账，当次提现最大金额5000元</div>
					<div class="divTop1">
						<label style="color: #555;">金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额：</label> 
						<input id="alipayAmount" />
					</div>
					<div class="divTop1 seviceCharge">
						<label style="color: #555;">手续费：</label> 
						<input value="${seviceCharge }" disabled="disabled">
					</div>
					<div class="divTop1" style="text-align: center;">
						<button id="alipaySubmit">确认提现</button>
					</div>
				</div>
				<div class="divTop2">
					<div class="divTop2" style="font-size: 14px; color: #555;" id="bankCanCarry"></div>
					<div class="divTop2">
						<label style="color: #555;">账户姓名：</label> <input id="bankName">
					</div>
					<div class="divTop2">
						<label style="color: #555;">账&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</label> 
						<input id="bankAccount">
					</div>
					<div class="divTop2">
						<label style="color: #555;">开户银行：</label> <input id="openBank">
					</div>
					<div class="divTop2">
						<label style="color: #555;">移动电话：</label> <input id="bankCall">
					</div>
					<div class="divTop2" style="font-size: 14px; color: #555;">银行卡2个工作日内到账，支付宝5个工作日内到账，当次提现最大金额5000元</div>
					<div class="divTop2">
						<label style="color: #555;">金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额：</label> 
						<input id="bankAmount">
					</div>
					<div class="divTop2 seviceCharge">
						<label style="color: #555;">&nbsp;手续费&nbsp;&nbsp;：</label> 
						<input value="${seviceCharge }"  disabled="disabled">
					</div>
					<div class="divTop2" style="text-align: center;">
						<button id="bankSubmit">确认提现</button>
					</div>
				</div>
				<div style="padding-top: 3px;"></div>
				<div></div>
			</div>
		</div>
	</body>
</html>