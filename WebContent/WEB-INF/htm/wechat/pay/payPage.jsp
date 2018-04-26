<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>订单-支付</title>
<meta name="wap-font-scale" content="no">
<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css">
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
</head>
<body>
</body>
<script type="text/javascript" charset="utf-8">
	(function($, doc) {
		$.init();
		$.ready(function() {
			function onBridgeReady() {
				WeixinJSBridge.invoke('getBrandWCPayRequest', {
					"appId" : "${appid}", //公众号名称，由商户传入     
					"timeStamp" : "${timeStamp}" + "", //时间戳，自1970年以来的秒数     
					"nonceStr" : "${nonceStr}", //随机串     
					"package" : "${packageValue}",
					"signType" : "${signType}", //微信签名方式：     
					"paySign" : "${sign}" //微信签名 
				},
				
				function(res) {
					// 使用以下方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。 
					if (res.err_msg == "get_brand_wcpay_request:ok") {
						window.location.href = "${ctx }/wechat/success/wxpay/${orderId}.htm";
					} else if (res.err_msg == "get_brand_wcpay_request:fail") {
						alert("微信支付失败");
						window.location.href = "${ctx}/web/order/updateorderno/${orderId}.htm";
					} else if (res.err_msg == "get_brand_wcpay_request:cancel") {
						if (confirm("您的订单已生成了,去支付?")) {
							window.location.href = "${ctx}/wechat/order/myOrders.htm";
						} else {
							window.location.href = "${ctx}/wechat/cart/shoppingcart.htm"
						}
					}
				});
			}

			if (typeof WeixinJSBridge == "undefined") {
				if (document.addEventListener) {
					document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
				} else if (document.attachEvent) {
					document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
					document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
				}
			} else {
				onBridgeReady();
			}
		});
	}(mui, document));
</script>
</html>