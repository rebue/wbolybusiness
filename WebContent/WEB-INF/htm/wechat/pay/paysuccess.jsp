<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html class="noticepage">
<head>
<meta charset="utf-8" />
<title>大卖网络</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="wap-font-scale" content="no" />
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css" />
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
<style>
.pay-success-box {
	text-align: center;
	background: white;
	height: 100vh;
}

.box-tips {
	height: 28px;
	width: 100%;
	background-color: #FFFFE0;
	text-align: center;
	line-height: 31px;
	 display:none; 
}

.box-seccess-icon img {
	height: 5.5rem;
	width: 5rem;
	padding: 0;
	margin: 0;
}

.box-seccess-text {
	font-weight: 700;
}

.box-strive-text {
	height: 2.3rem;
	background: #00CC00;
	line-height: 2.3rem;
	width: 92%;
	margin: 0.6rem 0 0 4%;
	border-radius: 1rem;
	color: white;
	font-size: 1rem;
}

.box-data {
	display: -webkit-flex; /* Safari */
	display: flex;
	justify-content: space-around;
	margin-top: 1rem;
}

.data-item {
	width: 4rem;
	font-size: 0.9rem;
	text-align: center;
	margin: 0 auto;
}

.data-item img {
	height: 1.5rem;
	display: block;
	border: node;
	margin-left: 28%;
}

.data-item div {
	color: #999999;
}

.data-item span {
	color: #FF0000
}

.box-point-explain {
	width: 92%;
	margin: 1.5rem 0 0 5%;
	text-align: left;
}

.box-point-explain  h5 {
	color: black;
	padding-bottom: 0.5rem;
}

.box-point-explain p {
	font-size: 0.9rem;
	padding: 0;
	mading: 0;
	padding: 0.3rem 0 0 0.4rem;
}

.box-bottom {
	display: -webkit-flex; /* Safari */
	display: flex;
	justify-content: space-around;
	margin-top: 1.5rem;
}

.box-bottom  button {
	font-size: 0.9rem;
	border-radius: 0.7rem;
}
</style>
</head>
<body>
	<div class="pay-success-box">
		<div id="boxtipsDom" class="box-tips">
			<a href="https://mp.weixin.qq.com/s/XDvCVq8rN20dW2aooJgXmQ"> <img
				src="${ctx }/images/wechat/guanzhu.png" width="18px" height="18px"
				style="margin-top: 5px" /><span style="color: red; font-size: 14px">&nbsp;&nbsp;&nbsp;+关注查看积分与收益</span>
			</a>
		</div>

		<div class="box-seccess-icon">
			<img src="${ctx }/images/wechat/wechatPay.png" />
		</div>
		<div class="box-seccess-text">支付成功</div>
		<div class="box-strive-text">今天也是努力的一天鸭！</div>
		<div class="box-data">
			<div class="data-item">
				<img src="${ctx }/images/wechat/coin.png" />
				<div>累计收益</div>
				<span id="totalIncomeDom">1.2</span>
			</div>
			<div class="data-item">
				<img src="${ctx }/images/wechat/money.png" />
				<div>总积分</div>
				<span id="pointDom">350</span>
			</div>
			<div class="data-item">
				<img src="${ctx }/images/wechat/add.png" />
				<div>本次积分</div>
				<span id="thisPointDom">+10</span>
			</div>
		</div>
		<div class="box-point-explain">
			<h5>积分攻略</h5>
			<p>1、每次消费均能获得积分</p>
			<p>2、每万分每月获得10元收益，每天0点发放收益</p>
			<p>3、超过三十天未消费，积分将扣除三分之一/30天</p>
		</div>
		<div class="box-point-explain">
			<h5>积分获取</h5>
			<p>1、麻溜烫（安吉店）</p>
			<p>2、大卖网（duamai.com）</p>
			<p>3、微薄利O2O连锁商超（安吉店）</p>
			<p>4、微薄利O2O连锁商超（大唐世家店）</p>
		</div>
		<div class="box-bottom">
			<button type="button"
				onclick="window.location.href='${ctx }/wechat/order/myOrders.htm?promoterId=${userId }'">我的订单</button>
			<button type="button"
				onclick="window.location.href='${ctx }/wechat/goods/allGoodsList.htm?promoterId=${userId }'">返回</button>

		</div>
	</div>
	<script type="text/javascript" charset="utf-8">
		(function($, doc) {
			//mui初始化
			$
					.ready(function() {
						var orderId = "${orderId}";
						var totalIncome = "${totalIncome}";
						var point = "${point}";
						var thisPoint = "${thisPoint}";
						/* 	console.log("info:" + totalIncome + ":" + point + ":"
									+ thisPoint) */

						document.getElementById("totalIncomeDom").innerText = totalIncome;
						document.getElementById("thisPointDom").innerText = thisPoint;
						document.getElementById("pointDom").innerText = point;
						
						// 获取用户是否关注
						mui.ajax('${ctx}/wechat/index/checkIsSubscribe.htm',{
							dataType:'json',// 服务器返回json格式数据
							type:'post',// HTTP请求类型
							success:function(data){
								console.log("查询用户是否关注的返回值："+data);
								if(!data){
									document.getElementById("boxtipsDom").style.display = "block"
								}
							}
						})
						
					});
		})(mui, document);
	</script>
</body>
</html>