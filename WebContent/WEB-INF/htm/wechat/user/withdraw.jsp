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
	// 提现类型
	var withdrawType = 2;

	$(function() {
		// 提现次数
		var withdrawNumber = "${withdrawNumber}";
		var balance = "${balance}";
		console.log(balance)
		$("#bankCanCarry").html("当前可用金额：<span><font color=\"red\">" + balance + "</font></span>元，本月已提现" + withdrawNumber + "次，总共可提现<span><font color=\"red\">" + balance + "</font></span>元");

		if (withdrawNumber == 0) {
			$(".seviceCharge").hide();
		}

		// 提现账号
		var bankAccountNo = "${bankAccountNo}";
		if (bankAccountNo != null && bankAccountNo != "" && bankAccountNo != "null") {
			// 隐藏身份证号
			$("#idCards").hide();
			// 回显账户姓名、账号、提现类型、开户银行（只有提现类型为银行卡才显示）、移动电话
			$("#bankAccountName").val("${bankAccountName}");
			$("#bankAccountNo").val("${bankAccountNo}");
			$("#contactTel").val("${contactTel}");
			if ("${withdrawType}" == 1) {
				$("#openAccountBank").val("${openAccountBank}");
			}
			$("#bankAccountName").attr("disabled", true);
			$("#bankAccountNo").attr("disabled", true);
			$("#contactTel").attr("disabled", true);
			$("#openAccountBank").attr("disabled", true);
		}

		// 提现提交
		$(document).on("tap", "#bankSubmit", function() {
			console.log(withdrawType)
			var bankAmount = $("#bankAmount").val();
			// 验证只能输入大于0的正整数或者保留两位小数的正小数
			var reg = /^([1-9]\d*(\.\d*[1-9])?)|(0\.\d*[1-9])$/;
			console.log(bankAmount);
			console.log(reg.test(bankAmount));
			if (!reg.test(bankAmount)) {
				mui.toast("金额只能输入大于0的整数或者保留两位小数!");
				return;
			}
			if (bankAmount > 5000) {
				mui.toast("已超出单次提现最大额度5000元!");
				return;
			}
			// 提现账户真实姓名
			var bankAccountName = $("#bankAccountName").val();
			// 身份证号
			var idCard = $("#idCard").val();
			// 提现账户
			var bankAccountNo = $("#bankAccountNo").val();
			// 开户银行
			var openAccountBank = $("#openAccountBank").val();
			// 联系方式
			var contactTel = $("#contactTel").val();
			if (bankAccountName == null || bankAccountName == "" || bankAccountName == "null") {
				mui.toast("账户姓名不能为空!");
				return;
			}
			if (bankAccountNo == null || bankAccountNo == "" || bankAccountNo == "null") {
				mui.toast("提现账号不能为空!");
				return;
			}
			if (contactTel == null || contactTel == "" || contactTel == "null") {
				mui.toast("移动电话不能为空!");
				return;
			}
			if(withdrawType == 1) {
				if (openAccountBank == null || openAccountBank == "" || openAccountBank == "null") {
					mui.toast("开户银行不能为空!");
					return;
				}
			}
			// 设置提现账户、申请提现、添加实名认证
			$.ajax({
				url: "${ctx}/wechat/user/applyWithdraw.htm",
				type: "post",
				dataType: "json",
				data: {
					bankAccountName: bankAccountName,
					idCard: idCard,
					bankAccountNo: bankAccountNo,
					withdrawType: withdrawType,
					openAccountBank: openAccountBank,
					contactTel: contactTel,
					withdrawAmount: bankAmount,
					orderId: "${orderId}",
				},
				success: function(data) {
					console.log(data);
					if (data.result == 1) {
						mui.toast(data.msg);
						window.location.href = "${ctx }/wechat/user/wechatWithdraw.htm?promoterId=${userId}";
					}
				}
			})
		})

		//可选择菜单
		var info = document.getElementById("r-info");
		mui(document).on('tap', '.mui-popover-action .btn-list li>a', function() {
			var a = this, parent;
			//根据点击按钮，反推当前是哪个actionsheet
			for (parent = a.parentNode; parent != document.body; parent = parent.parentNode) {
				if (parent.classList.contains('mui-popover-action')) {
					break;
				}
			}
			//关闭actionsheet
			mui('#' + parent.id).popover('toggle');
			info.innerHTML = a.innerHTML;
			withdrawType = a.getAttribute("data-value");
			if (withdrawType == 1) {
				$("#openBanks").show();
			} else {
				$("#openBanks").hide();
			}
		});

		withdrawTypes();
	})

	// 选择提现类型
	function withdrawTypes() {
		var info = document.getElementById("r-info");
		var select = document.getElementById("withdrawType");
		if (select.value == 2) {
			info.innerHTML = "支付宝";
			$("#openBanks").hide();
		} else {
			info.innerHTML = "银行卡";
			$("#openBanks").show();
		}
	}
</script>
<style type="text/css">
.divTop2 {
	mcolor: #555;
	padding: 6px;
	margin-top: 3px;
}

input {
	-webkit-user-select: auto
}
</style>
<body>
	<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">我的提现</h4>
		<h5 class="mui-title" style="left: auto; font-size: 15px;">提现记录</h5>
	</header>
	<div id="wallet_main">
		<div class="mui-content">
			<div class="divTop2">
				<input type="hidden" id="withdrawType" value="${withdrawType }">
				<div class="divTop2" style="font-size: 14px; color: #555;" id="bankCanCarry"></div>
				<div class="divTop2">
					<label style="color: #555;">金&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;额：</label>
					<input id="bankAmount" value="${balance}">
				</div>
				<div class="divTop2">
					<label style="color: #555;">账户姓名：</label> 
					<input id="bankAccountName">
				</div>
				<div class="divTop2" id="idCards">
					<label style="color: #555;">身份证号：</label> 
					<input id="idCard">
				</div>
				<div class="divTop2">
					<label style="color: #555;">账&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：</label>
					<input id="bankAccountNo">
				</div>
				<div class="divTop2">
					<a href="#sheet"> 
						<span>提现类型：</span> 
						<span class="r-info" id="r-info"></span>
					</a>
				</div>
				<div class="divTop2" id="openBanks" style="display: none;">
					<label style="color: #555;">开户银行：</label> 
					<input id="openAccountBank">
				</div>
				<div class="divTop2">
					<label style="color: #555;">移动电话：</label> <input id="contactTel">
				</div>
				<div class="divTop2" style="font-size: 14px; color: #555;">银行卡2个工作日内到账，支付宝5个工作日内到账，当次提现最大金额5000元</div>
				<div class="divTop2 seviceCharge">
					<label style="color: #555;">&nbsp;手续费&nbsp;&nbsp;：</label> <input
						value="${seviceCharge }" disabled="disabled">
				</div>
				<div class="divTop2" style="text-align: center;">
					<button id="bankSubmit">确认提现</button>
				</div>
			</div>
			<div style="padding-top: 3px;"></div>
			<div></div>
		</div>
	</div>
	<div id="sheet"
		class="mui-popover mui-popover-bottom mui-popover-action ">
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
</body>
</html>