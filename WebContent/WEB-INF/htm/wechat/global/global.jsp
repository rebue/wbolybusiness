<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html >
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
	<link href="${ctx }/css/wechat/mui.picker.css" rel="stylesheet" />
	<link href="${ctx }/css/wechat/mui.poppicker.css" rel="stylesheet" />
	<link href="${ctx }/css/wechat/swiper-3.4.1.min.css" rel="stylesheet" />
	<link href="${ctx }/css/wechat/goods.css" rel="stylesheet" />
	<!--index.html引用JS位置调整-->
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.picker.js"></script>
	<script src="${ctx }/js/wechat/mui.poppicker.js"></script>
	<script src="${ctx }/js/wechat/swiper-3.4.1.min.js"></script>
	
	<!-- 引用 js -->
	<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<%-- <header class="mui-bar mui-bar-nav">
		<!-- <button id="AreaPicker" class="mui-btn mui-btn-link mui-btn-nav">
			<span id="shopId" style="display: none;"></span><span id="AreaName"></span><span
				class="mui-icon mui-icon-location-filled"></span>
		</button> -->
		<a href="" class="mui-icon mui-icon-chat mui-pull-right wboly-message"></a>
		<form class="mui-input-row mui-search" id="form"
			action="${ctx}/wechat/goods/allGoodsList.htm" method="post">
			<input type="search" name="searchbar" id="searchbar"
				class="mui-input-clear" placeholder="搜索商品">
		</form>
	</header> --%>
	<nav class="mui-bar mui-bar-tab">
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/index/indexInfo.htm"> 
			<span class="mui-icon mui-icon-home"></span> 
			<span class="mui-tab-label">首页</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/goods/allGoodsList.htm"> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-class"></span> 
			<span class="mui-tab-label">全部商品</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/cart/shoppingcart.htm"> 
			<span id="cartnum" class="mui-badge mui-badge-danger">0</span> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-cart"></span> 
			<span class="mui-tab-label">购物车</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/user/userCenter.htm"> 
			<span class="mui-icon mui-icon-person"></span> 
			<span class="mui-tab-label">个人中心</span>
		</a>
	</nav>