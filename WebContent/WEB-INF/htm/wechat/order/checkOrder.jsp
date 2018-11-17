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
</head>

<body>
	<header class="mui-bar mui-bar-nav check-order-top">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"
			id="action_back"></a>
		<h1 class="mui-title">确认订单</h1>
	</header>

	<div id="main-checkorder" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div id="goods-main-box" class="check-order-box">
				<form class="mui-input-group" id="goods-group"></form>
			</div>

			<ul class="mui-table-view" id="AddList"></ul>

			<div class="message-box">
				<h5>买家留言</h5>
				<textarea id="user-message" rows="2"
					placeholder="填写内容已与卖家确认（可选）"></textarea>
			</div>

		</div>
	</div>

	<footer class="mui-bar total-box mui-checkbox mui-left fixbtm"
		id="total-box">
		<button class="mui-pull-right paybtn" id="goPay">立即支付</button>
		<div class="total-price-box mui-pull-left">
			<h4>
				合计：<span></span>
			</h4>
			<h5 class="h5_1">
				返现金：<span></span>
			</h5>
			<h5 class="h5_2">
				商品：<span></span>件
			</h5>
		</div>
	</footer>

	<script type="text/javascript" charset="utf-8">
		var totalPrice;//总价格
		var totalBack;//总返现
		var num;//总数量
		var message;
		var json = Array();

		(function($, doc) {
			$.init([ GetCartList() ]);
			//mui初始化
			$.ready(function() {
				// 加载收货地址
				commonAddress();
				//点击地址栏，转到地址设置页面
				mui("#AddList")[0].addEventListener("tap",function(e){
					window.location.href = "${ctx}/wechat/user/newAddressPage.htm?promoterId=${userId}";
				});

				//订单商品的详细信息，在点击去付款按钮时会整理成一个JSON，详见控制台
				compute();
				mui('#goods-group .car-inner-box').on('tap', 'a', function() {
					document.location.href = this.href;
				});
				
				mui('#main-checkorder.mui-scroll-wrapper').scroll({
					bounce : false
				});
				
				//生成订单
				var cartIds = "";
				//点击去付款按钮
				doc.getElementById('goPay').addEventListener('tap', function(event) {
					var li = mui("#goods-main-box .mui-input-row");
					li.each(function(e) {
						var cartId = this.querySelector("input[name='cartId']").value;
						cartIds += cartId + ",";
					});

					var addressId = 0;
					// 地址编号
					if (document.querySelector("input[name='addressId']") != null) {
						addressId = document.querySelector("input[name='addressId']").value;
					}
					if (addressId == 0) {
						mui.toast("收货地址不能为空");
						return ;
					}

					// 订单留言
					mui("#user-message")[0].blur();
					message = mui("#user-message")[0].value;
					if (json == "") {
						mui.toast("没有商品信息");
						return;
					}
					var details = new Array();
					for (var i = 0; i < json.info.length; i++) {
						details.push({
							onlineId: json.info[i].onlineId,
							onlineSpecId: json.info[i].onlineSpecId,
							cartId: json.info[i].cartId,
							buyCount: json.info[i].buyCount,
						})
					}
					console.log(details);
					loading(1);
					mui.ajax('${ctx}/wechat/order/createOrder.htm', {
						data : {
							// key : '59c23bdde5603ef993cf03fe64e448f1',
							orderMessages: message,
							addrId: addressId,
							details : JSON.stringify(details)
						},
						dataType : 'json',//服务器返回json格式数据
						type : 'post',//HTTP请求类型
						timeout : 10000,//超时时间设置为10秒；
						success : function(data) {
							console.log(data);
							loading(2);
							if (data.flag) {
								window.location.href = "${ctx}/wechat/pay/center/" + data.payOrderId + ".htm?promoterId=${userId}";
								return;
							}
							mui.toast(data.message);
						},
						error : function(xhr, type, errorThrown) {
							loading(2);
							mui.toast("访问超时");
							//异常处理；
							console.log(type);
						}
					});
				});
			});
		})(mui, document);

		//计算总额
		function compute() {
			totalPrice = 0;
			totalBack = 0;
			num = 0;
			var li = mui("#goods-main-box .mui-input-row");
			li.each(function(e) {
				var pm = this.querySelector(".m-price span").innerText;
				var bm = this.querySelector(".b-money span").innerText;
				var nb = parseInt(this.querySelector(".numbox span").innerText);
				var unit_totalPrice = mul(pm, nb);
				var unit_totalBack = mul(bm, nb);
				totalPrice = totalPrice + unit_totalPrice;
				totalBack = totalBack + unit_totalBack;
				num = num + nb;
			});
			mui(".total-price-box h4 span")[0].innerHTML = "¥" + totalPrice.toFixed(2);
			mui(".total-price-box h5.h5_1 span")[0].innerHTML = "¥" + totalBack.toFixed(2);
			mui(".total-price-box h5.h5_2 span")[0].innerHTML = num;
		};

		//精确乘法
		function mul(a, b) {
			var c = 0, d = a.toString(), e = b.toString();
			try {
				c += d.split(".")[1].length;
			} catch (f) {
			}
			try {
				c += e.split(".")[1].length;
			} catch (f) {
			}
			return Number(d.replace(".", "")) * Number(e.replace(".", "")) / Math.pow(10, c);
		};

		function GetCartList() {
			if (localStorage.getItem("orderInfo") != null) {
				totalPrice = 0;
				totalBack = 0;
				num = 0;
				json = JSON.parse(localStorage.getItem("orderInfo"));
				console.log(json);
				if (json == null) {
					mui.toast("没有商品数据");
					return;
				}
				totalBack = formatCurrency(json.totalBack);
				totalPrice = formatCurrency(json.totalPrice);
				num = json.totalNumber;
				var html = '';
				document.body.querySelector("#goods-group").innerHTML = html;
				mui.each(json.info, function(index, item) {
					html += '<div class="mui-input-row mui-checkbox mui-left">';
					html += '	<input type="hidden" name="onlineId" value="' + item.onlineId + '"/>';
					html += '	<div class="car-inner-box">';
					html += '		<div class="car-inner-box-img">';
					if(item.subjectType==1){
						html += '				<a  class="full-return">';
						html += '			<img src="' + item.picPath + '" alt="" class="goodspic"/>';
						html += '				</a>';
					}else{
						html += '				<a  class="">';
						html += '			<img src="' + item.picPath + '" alt="" class="goodspic"/>';
						html += '				</a>';
					}
					html += '		</div>';
					html += '		<div class="car-inner-body">';
					html += '			<input type="hidden" name="cartId" value="' + item.cartId + '"/>';
					html += '			<h5>';
					html += '				<a href="${ctx}/wechat/goods/' + item.parm + '.htm?promoterId=${userId}">' + item.onlineTitle + '</a>';
					html += '			</h5>';
					html += '			<p>' + item.onlineSpec + '</p><br/>';
					html += '			<div class="price-area">';
					if(item.subjectType==1){
						html += '				<span class="m-price">¥<span>' + formatCurrency(item.salePrice) + '</span></span>';
						html += '  				<span class="b-money"><span>'+ '</span></span>';
					}else{
						html += '				<span class="m-price">¥<span>' + formatCurrency(item.salePrice) + '</span></span>';
						html += '  				<span class="b-money"> 返 <span>' + formatCurrency(item.cashbackAmount) + '</span> 元</span>';
					}
					html += '			</div>';
					html += '			<div class="numbox">数量：<span>' + item.buyCount + '</span></div>';
					html += '		</div>';
					html += '	</div>';
					html += '</div>';
				});
				document.body.querySelector("#goods-group").innerHTML = html;
			}
		}
	</script>
</body>
</html>