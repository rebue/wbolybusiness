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
<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/mui-icons-extra.css" />
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
<link rel="stylesheet" href="${ctx }/css/wechat/cart.css">
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
<script src="${ctx }/js/util/base64.js"></script>
<script src="${ctx }/js/util/md5.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav not-border">
		<a class="mui-icon mui-icon-left-nav mui-pull-left" href="${ctx }/wechat/goods/allGoodsList.htm?promoterId=${userId}" id="action_back"></a>
		<h1 class="mui-title">支付中心</h1>
	</header>

	<div id="main-checkorder" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div id="goods-main-box" class="pay-box">
				<ul class="pay-info">
					<li class="mui-table-view-cell">
						<h5>
							订单金额<span id="smoney" class="ordertotal mui-pull-right">${orderMoney}元</span>
						</h5>
					</li>
					<li class="mui-table-view-cell"><h5>请选择一种支付方式</h5></li>
				</ul>
				<form class="mui-input-group" id="goods-group"></form>
			</div>

		</div>
	</div>

	<footer class="mui-bar total-box mui-checkbox mui-left fixbtm fullbtm" id="total-box">
		<button class="mui-pull-right paybtn" id="goPay">立即支付</button>
	</footer>

	<script type="text/javascript" charset="utf-8">
		(function($, doc) {
			$.init({
				//预加载
				preloadPages : [ initPayFun() ]
			});
			//mui初始化
			$.ready(function() {
				var totalPrice = 24.50;//总价格
				var userId = 12345;//用户ID
				var payby;//支付方式
				var orderMoney = "${orderMoney}";
				var vmoney = 0;

				// 返回指定页面
				mui('.not-border').on('tap', 'a', function() {
					document.location.href = this.href;
				});

				var b = new Base64();

				localStorage.setItem("payurl", window.location.href);

				document.getElementById("action_back").addEventListener("tap", function() {
					localStorage.removeItem("orderInfo");
				});

				mui('#main-checkorder.mui-scroll-wrapper').scroll({
					bounce : false
				});

				getVBalance();

				//获取余额
				function getVBalance() {
					mui.ajax('${ctx}/wechat/pay/getVBalance.htm', {
						data : {
							key : '59c23bdde5603ef993cf03fe64e448f1'
						},
						dataType : 'json',
						type : 'post',
						//async : false,
						success : function(data) {
							if (data.flag) {
								vmoney = data.message;
								document.getElementById("vbalance").innerHTML = data.message;
								if (parseFloat(vmoney) >= parseFloat(orderMoney)) {
									document.getElementById("vpay").checked = true;
									payby = 0;
								} else {
									if (isWeiXin()) {
										document.getElementById("alipay").checked = true;
										payby = 1;
									}
									document.getElementById("vpay").disabled = true;
								};
								return;
							}
							mui.toast(data.message);
							document.getElementById("vpay").disabled = true;
						},
						error : function(xhr, type, errorThrown) {
							//异常处理；
							console.log(type);
						}
					});
				};

				//切换支付方式
				mui(document).on("change", "#goods-group .mui-input-row input[type='radio']", function(e) {
					if (e.target.id == "vpay") {
						payby = 0;
					} else if (e.target.id == "alipay") {
						payby = 1;
					};
				});

				var flag = true;

				//点击去付款按钮
				doc.getElementById('goPay').addEventListener('tap', function(event) {

					if (!flag) {
						mui.toast("手速太快了,先歇歇");
						return;
					}

					if (payby == null) {
						mui.toast("请选择支付方式");
						return;
					}

					if (payby == 1) {
						window.location.href = "${ctx }/wechat/pay/toPay.htm?orderId=${payOrderId}&promoterId=${userId}"
						return;
					}

					if (parseFloat(orderMoney) > parseFloat(vmoney)) {
						mui.toast("余额不足");
						return;
					}

					// 获取v支付预支付Id
					mui.ajax({
						url : "${ctx}/wechat/pay/createVpayPrepaymentId.htm",
						type : "post",
						data : {
							userId : "${userId}",
							orderId : "${payOrderId}"
						},
						dataType : 'json',
						success : function(data) {
							console.log(data);
							if (data.flag == "false") {
								mui.toast(data.message);
								return;
							}
							var prepayId = data.prepayId;
							var requirePayPswd = data.requirePayPswd;
							if (requirePayPswd == "true") {
								console.log("开始弹窗");
								event.detail.gesture.preventDefault(); //修复iOS 8.x平台存在的bug，使用plus.nativeUI.prompt会造成输入法闪一下又没了
								var btnArray = ['取消', '确定' ];
								mui.prompt('请输入您的V支付密码：', '密码', ' ', btnArray, function(e) {
									if (e.index == 1) {
										if (!flag) {
											mui.toast("手速太快了,先歇歇");
											return;
										}

										if (e.value == null || e.value == "") {
											mui.toast("密码不能为空");
											return;
										}

										flag = false;
										var pwd = encodeURIComponent(CryptoJS.MD5(e.value));
										mui.ajax('${ctx}/wechat/pay/shopOrderPay.htm', {
											data : {
												prepayId : prepayId,
												pwd : pwd,
												requirePayPswd : requirePayPswd,
												orderId : "${payOrderId}"
											},
											dataType : 'json',
											type : 'post',
											success : function(data) {
												loading(2);
												flag = true;
												if (data.flag == true) {
													window.location.href = "${ctx }/wechat/success/vpay/${payOrderId}.htm?promoterId=${userId}";
													return;
												};
												
												if (data.message == "密码错误") {
													mui.confirm('是否找回支付密码?', data.message, ['找回支付密码', '取消' ], function(e) {
														if (e.index == 0) {
															window.location.href = "${ctx }/wechat/user/updatepaypwdpage.htm";//去到登录页面
														}
													});
													return;
												}
												mui.toast(data.message);
											},
											error : function(xhr, type, errorThrown) {
												loading(2);
												flag = true;
												//异常处理；
												console.log(type)
											}
										});
									} else {
										flag = true;
									}
								}, 'div');
								//修改弹出框默认input类型为password 
								document.querySelector('.mui-popup-input input').type = 'password'
							} else {
								mui.ajax('${ctx}/wechat/pay/shopOrderPay.htm', {
									data : {
										prepayId : prepayId,
										requirePayPswd : requirePayPswd,
										orderId : "${payOrderId}"
									},
									dataType : 'json',
									type : 'post',
									success : function(data) {
										console.log(data.message);
										flag = true;
										if (data.flag == true) {
											window.location.href = "${ctx }/wechat/success/vpay/${payOrderId}.htm?promoterId=${userId}";
											return;
										};
										mui.toast(data.message);
									},
									error : function(xhr, type, errorThrown) {
										loading(2);
										flag = true;
										//异常处理；
										console.log(type)
									}
								});
							}
						}
					});
				});
			});
		})(mui, document);

		function initPayFun() {
			if (isWeiXin()) {
				var html = "";
				document.getElementById("goods-group").innerHTML = html;
				html += '<div class="mui-input-row mui-radio mui-left">';
				html += '	<input type="hidden" name="goodsId" value="01"/>';
				html += '	<div class="car-inner-box">';
				html += '		<div class="car-inner-box-img">';
				html += '			<img src="${ctx }/images/wechat/vpay.png" alt="" class="goodspic"/>';
				html += '		</div>';
				html += '		<div class="car-inner-body">';
				html += '			<h5>V支付</h5>';
				html += '			<p>微薄利旗下安全快捷支付</p>';
				html += '			<div id="vmoney">余额：<span id="vbalance">0.00</span> 元</div>';
				html += '		</div>';
				html += '	</div>';
				html += '	<input name="pay" type="radio" id="vpay">';
				html += '</div>';
				html += '<div class="mui-input-row mui-radio mui-left">';
				html += '	<input type="hidden" name="goodsId" value="01"/>';
				html += '	<div class="car-inner-box">';
				html += '		<div class="car-inner-box-img">';
				html += '			<img src="${ctx }/images/wechat/wechatPay.png" alt="" class="goodspic"/>';
				html += '		</div>';
				html += '		<div class="car-inner-body">';
				html += '			<h5>微信支付</h5>';
				html += '			<p>微信快捷支付</p>';
				html += '		</div>';
				html += '	</div>';
				html += '	<input name="pay" type="radio" id="alipay">';
				html += '</div>';
				document.getElementById("goods-group").innerHTML = html;
			} else {
				var html = "";
				document.getElementById("goods-group").innerHTML = html;
				html += '<div class="mui-input-row mui-radio mui-left">';
				html += '	<input type="hidden" name="goodsId" value="01"/>';
				html += '	<div class="car-inner-box">';
				html += '		<div class="car-inner-box-img">';
				html += '			<img src="${ctx }/images/wechat/vpay.png" alt="" class="goodspic"/>';
				html += '		</div>';
				html += '		<div class="car-inner-body">';
				html += '			<h5>V支付</h5>';
				html += '			<p>微薄利旗下安全快捷支付</p>';
				html += '			<div id="vmoney">余额：<span id="vbalance">0.00</span> 元</div>';
				html += '		</div>';
				html += '	</div>';
				html += '	<input name="pay" type="radio" id="vpay">';
				html += '</div>';
				document.getElementById("goods-group").innerHTML = html;
			}
		}
	</script>
</body>
</html>