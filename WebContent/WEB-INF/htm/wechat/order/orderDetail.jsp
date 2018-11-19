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
<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/orderlist.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">订单详情</h4>
	</header>
	<div id="orderDetail_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<ul class="mui-table-view" id="AddList"></ul>
			<div id="goodsDetail" class="order-list-unit sp"></div>
			<div id="orderDetail" class="order-info"></div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8">
    var b = new Base64();
	var unitJson="";
		(function($, doc) {
			mui.init([ getOrderDetail() ]);
			$.ready(function() {
				mui('#orderlist_main').on('tap', 'a', function() {
					document.location.href = this.href;
				});
				
				mui('#orderDetail_main.mui-scroll-wrapper').scroll({
					bounce : false
				});
				// 退货
				document.getElementById('returnButtom').addEventListener('tap',function(){
					returnPage(b.encode(unitJson));
				});
				
			});
		})(mui, document);

		// 订单详情
		function getOrderDetail() {
			var json = JSON.parse(localStorage.getItem("${order_data}"));
			
			console.log(json);
			if (json == null || json == "") {
				mui.confirm("无法获取您的订单，现在去看看？", " ", [ '取消', '确定' ], function(e) {
					if (e.index == 1) {
						window.location.href = "${ctx }/wechat/order/myOrders.htm?promoterId=${userId}";
					};
				})
				return;
			}
			var html = '';
			document.getElementById("goodsDetail").innerHTML = html;
			// getOrderAddress(json.addressId);
			var backMoney = 0;
			mui.each(json.items, function(index, item) {
				var strs = new Array();
				// 根据逗号获取图片后缀
				strs = item.goodsQsmm.split(".");
				console.log(strs);
				html += '<div class="mui-input-group">';
				html += '	<div class="mui-input-row mui-left">';
				html += '		<div class="car-inner-box">';
				html += '			<div class="car-inner-box-img">';
				if(item.subjectType==1){
					html += '	<a href=""  class="full-return">';
					html += '		<img src="${goodsImgUrl}' + item.goodsQsmm + '" alt="" class="goodspic">';
					html += '	</a>';
				}else{
					html += '	<a href=""  class=\"\">';
					html += '		<img src="${goodsImgUrl}' + item.goodsQsmm + '" alt="" class="goodspic">';
					html += '	</a>';
				}
				html += '			</div>';
				html += '			<div class="car-inner-body">';
				html += '				<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + item.onlineId + '&promoterId=${userId}' + '">' + item.onlineTitle + '</a></h5>';
				html += '				<p>规格：' + item.specName + '</p><br/>';
				html += '				<div class="price-area">';
				if(item.subjectType==1){
					html += '					<span class="m-price">¥ <span>' + formatCurrency(item.buyPrice) + '</span></span>';
					html += '					<span class="numbox">数量：<span>' + item.buyCount + '</span></span>';
				}else{
					html += '					<span class="m-price">¥ <span>' + formatCurrency(item.buyPrice) + '</span></span>';
					html += '					<span class="b-money"> 返 <span>' + formatCurrency(item.cashbackAmount) + '</span></span>';
					html += '					<span class="numbox">数量：<span>' + item.buyCount + '</span></span>';
				}
				
				html += '				</div>';
				html += '			</div>';
				html += '<div class="button-box">';
				    var realBuyCount = parseInt(item.buyCount) - parseInt(item.returnCount);
				     b = new Base64();
					 unitJson ='{"orderCode":"' + json.orderCode + '","orderId":"' + json.id +'","orderDetailId":"' + item.id + '","onlineId":"' + item.onlineId + '","onlineTitle":"' + item.onlineTitle + '","goodsQsmm":"${goodsImgUrl}' + item.goodsQsmm + '","productId":"' + item.productId + '","buyPrice":"' + formatCurrency(item.buyPrice) + '","cashbackAmount":"' + formatCurrency(item.cashbackAmount) + '","buyCount":"' + realBuyCount + '","specName":"' + item.specName +'","orderState":"' + json.orderState + '"}';
					if (json.orderState != 1 && json.orderState != -1){
						if(item.returnState == 1){
							if(json.orderState==2){
								html+='<button class="mui-btn locked">退款中</button>'
							}else if(json.orderState==3){
								html+='<button class="mui-btn locked">退款中</button>'
							}else if(json.orderState==4){
								html+='<button class="mui-btn locked">退货/售后中</button>'
							}
							
						}else if(item.returnState ==2){
							html+='<button class="mui-btn locked">已退货</button>'
						}else if(item.returnState == 0 || item.returnState == 3){
							if(json.orderState==2){
								html+='<a id="returnButtom" class="mui-btn">退款</a>';
							}else if(json.orderState==3){
								html+='<a id="returnButtom" class="mui-btn">退货退款</a>';
							}else if(json.orderState==4){
								if(new Date().getTime()-json.receivedTime>604800000){
									html+='<a id="returnButtom"  class="mui-btn">售后</a>';
								}else{
									html+='<a id="returnButtom"  class="mui-btn">退货/售后</a>';
								}
							}
						};
					};
				html += '</div>';
				html += '		</div>';
				html += '	</div>'
				html += '</div>';
				backMoney += item.cashbackAmount * item.buyCount;
			});

			html += '<div class="unit-compute sp">'
			html += '	<span class="fullwidth">商品总价<span class="mui-pull-right">¥ ' + formatCurrency(json.orderMoney) + '</span></span>';
			html += '	<span class="fullwidth">返现金额<span class="mui-pull-right">¥ ' + formatCurrency(backMoney) + '</span></span>';
			html += '	<span class="fullwidth">运费<span class="mui-pull-right">¥ ' + formatCurrency(json.courierFee / 100) + '</span></span>';
			html += '	<span class="fullwidth sp">实付款<span class="mui-pull-right">¥ ' + formatCurrency((json.realMoney)) + '</span></span>';
			html += '</div>';
			document.getElementById("goodsDetail").innerHTML = html;
			html = '';
			document.getElementById("orderDetail").innerHTML = html;
			html += '<span class="fullwidth">订单编号：' + json.orderCode + '</span>';
			html += '<span class="fullwidth">下单时间：' + timestampToTime(json.orderTime) + '</span>';
			document.getElementById("orderDetail").innerHTML = html;
			
			html = '';
			document.body.querySelector("#AddList").innerHTML = html;
			html += '<li class="mui-table-view-cell">';
			html += '	<div class="mui-table mui-navigate-right">';
			html += ' 		<div class="mui-table-cell mui-col-xs-8 mui-col-sm-8">';
			html += ' 			<h5 class="mui-ellipsis" id="receiver">' + json.receiverName + '</h5>';
			html += '		</div>';
			html += '		<div class="mui-table-cell mui-col-xs-4  mui-col-sm-4 mui-text-right">';
			html += '	 		<span class="mui-h5" id="receiver-phone">' + json.receiverMobile + '</span>';
			html += ' 		</div>';
			html += '	</div>';
			html += '	<p class="mui-h6 mui-ellipsis" id="receiver-add">' + json.receiverProvince + json.receiverCity + json.receiverExpArea + '-' + json.receiverAddress + '</p>';
			html += '</li>';
			document.body.querySelector("#AddList").innerHTML = html;
		}
		

		//获取用户订单收货地址
		function getOrderAddress(address) {
			//AddList
			mui.ajax('${ctx}/wechat/user/getorderAddress.htm', {
				data : {
					key : '59c23bdde5603ef993cf03fe64e448f1',
					isDefault : '0',
					address : address
				},
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				timeout : 10000,//超时时间设置为10秒；
				success : function(data) {
					if (data.flag) {
						var html = '';
						document.body.querySelector("#AddList").innerHTML = html;

						html += '<li class="mui-table-view-cell">';
						html += '	<div class="mui-table mui-navigate-right">';
						html += ' 		<div class="mui-table-cell mui-col-xs-8 mui-col-sm-8">';
						html += ' 			<h5 class="mui-ellipsis" id="receiver">' + data.message[0].realName + '</h5>';
						html += '			<input type="hidden" name="addressId" value="' + data.message[0].addressId + '"/>';
						html += '		</div>';
						html += '		<div class="mui-table-cell mui-col-xs-4  mui-col-sm-4 mui-text-right">';
						html += '	 		<span class="mui-h5" id="receiver-phone">' + data.message[0].mobile + '</span>';
						html += ' 		</div>';
						html += '	</div>';
						html += '	<p class="mui-h6 mui-ellipsis" id="receiver-add">' + data.message[0].provinceName + data.message[0].cityName + data.message[0].areaName + '-' + data.message[0].address + '</p>';
						html += '</li>';
						document.body.querySelector("#AddList").innerHTML = html;
					} else {
						if (data.message != "") {
							mui.toast(data.message);
							return;
						}
						var html = '';
						document.body.querySelector("#AddList").innerHTML = html;
						html += '<li class="mui-table-view-cell">';
						html += '	<div class="mui-table mui-navigate-right">';
						html += '		<div class="mui-table-cell mui-col-xs-8 mui-col-sm-8">';
						html += '			<input type="hidden" name="addressId" value="0"/>';
						html += '			<h5 class="mui-ellipsis">我要自提</h5>';
						html += '		</div>';
						html += '	</div>';
						html += '	<p class="mui-h6 mui-ellipsis">自提用户需自己到该门店提取商品</p>';
						html += '</li>';
						document.body.querySelector("#AddList").innerHTML = html;
					}
				},
				error : function(xhr, type, errorThrown) {
					//异常处理；
					console.log(type);
				}
			});
		}
		
		
	</script>
</body>
</html>
