<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html class="noticepage">
<head>
<meta charset="utf-8" />
<title>大卖网络</title>
<meta name="viewport" content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="wap-font-scale" content="no" />
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css" />
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
</head>
<body>
	<div class="noticepage noticepage-main">
		<div class="mui-icon mui-icon-checkmarkempty success"></div>
		<h3>付款成功</h3>
		<p>您的宝贝就要飞向您的怀抱啦，请保持电话畅通</p>
	</div>
	<div class="btn-box">
		<button type="button" class="mui-btn" onclick="window.location.href='${ctx }/wechat/goods/allGoodsList.htm?promoterId=${userId }'">继续购物</button>
		<button type="button" class="mui-btn" onclick="window.location.href='${ctx }/wechat/order/myOrders.htm?promoterId=${userId }'">返回</button>
	</div>
</body>
</html>
