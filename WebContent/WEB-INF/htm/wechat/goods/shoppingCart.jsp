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
	<script type="text/javascript" src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<h1 class="mui-title">购物车</h1>
		<button class="mui-btn mui-btn-gray mui-btn-link mui-pull-right" id="editor">编辑</button>
	</header>

	<nav class="mui-bar mui-bar-tab">
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/index/indexInfo.htm?promoterId=${userId}"> 
			<span class="mui-icon mui-icon-home"></span> 
			<span class="mui-tab-label">首页</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/goods/allGoodsList.htm?promoterId=${userId}"> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-class"></span> 
			<span class="mui-tab-label">全部商品</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/cart/shoppingcart.htm?promoterId=${userId}"> 
			<span id="cartnum" class="mui-badge mui-badge-danger">0</span> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-cart"></span> 
			<span class="mui-tab-label">购物车</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/user/userCenter.htm?promoterId=${userId}"> 
			<span class="mui-icon mui-icon-person"></span>
			<span class="mui-tab-label">个人中心</span>
		</a>
	</nav>

	<div id="main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div id="goods-main-box">
				<div id="shoptop" class="shoptop">
					<span class="mui-pull-left spt">大卖网络</span> 
					<span class="mui-pull-right">左滑可删除噢</span>
				</div>
				<form class="mui-input-group" id="goods-group"></form>
			</div>
		</div>
	</div>

	<footer class="mui-bar total-box mui-checkbox mui-left" id="total-box">
		<label for="totalcheck">全选</label> <input name="totalcheckbox" value="0" type="checkbox" checked="checked" id="totalcheck">
		<div id="case_1" class="case-box">
			<button class="mui-pull-right paybtn" id="goPay">
				结算(<span></span>)
			</button>
			<div class="total-price-box mui-pull-right">
				<h4>
					合计：<span></span>
				</h4>
				<h5>
					返现金：<span></span>
				</h5>
			</div>
		</div>
		<div id="case_2" class="case-box">
			<button class="mui-pull-right delbtn" id="delbtn">删除</button>
		</div>
	</footer>

	<script type="text/javascript" charset="utf-8">
		var totalPrice;//总价格
		var totalBack;//总返现
		var num;//总数量
		var userId;//用户ID
		var start = 0;
		var limit = 200;
		
		(function($, doc) {
			$.init([ GetCartList() ]);
			//mui初始化
			$.ready(function() {
				//购物车商品的详细信息，在点击去付款按钮时会整理成一个JSON，详见控制台
				LoadCartNum();

				compute();
				mui('#goods-group .car-inner-box').on('tap', 'a', function() {
					document.location.href = this.href;
				});
			
				mui('#main.mui-scroll-wrapper').scroll({
					bounce : false
				});
				
				mui(".mui-tab-item-wboly")[2].classList.add('mui-active');

				//全选事件
				mui(document).on("change", "#total-box input[type='checkbox']", function(e) {
					var checkboxs = mui("#goods-main-box .mui-input-row input[type='checkbox']");
					if (e.target.checked == true) {
						checkboxs.each(function() {
							this.checked = true;
						})
					} else if (e.target.checked == false) {
						checkboxs.each(function() {
							this.checked = false;
						})
					};
					compute();
				});

				//单选事件
				mui(document).on("change", "#goods-main-box .mui-input-row input[type='checkbox']", function(e) {
					compute();
					if (e.target.checked == false) {
						mui("#total-box input[type='checkbox']")[0].checked = false;
					};
				});

				// 购物车数量增加和减少事件
				mui(document).on("change", "#goods-main-box .mui-input-row .mui-numbox input[type='number']", function(e) {
					var muiNumBox = this.parentNode;
					// 上线编号
					var onlineId = this.parentNode.parentNode.parentNode.parentNode.querySelector("input[name='onlineId']").value;
					// 上线规格编号
					var onlineSpecId = this.parentNode.parentNode.parentNode.parentNode.querySelector("input[name='onlineSpecId']").value;
					// 购物车编号
					var cartId = this.parentNode.parentNode.parentNode.parentNode.querySelector("input[name='cartId']").value;
					// 购物车数量
					var cartCount = this.parentNode.parentNode.parentNode.parentNode.querySelector("input[name='cartCount']").value;
					// 增加之后的购物车数量
					var addCartCount = this.parentNode.parentNode.parentNode.parentNode.querySelector("input[name='number']").value;
					var updateCartCount = 0;
					
					// 如果购物车数量少于增加的数量说明是增加购物车数量
					// 如果购物车数量大于增加的数量说明是减少购物车的数量
					if (cartCount < addCartCount) {
						console.log("购物车数量少于增加的数量");
						updateCartCount = addCartCount - cartCount;
					} else {
						console.log("购物车数量大于增加的数量");
						updateCartCount = addCartCount - cartCount;
					}
					
					mui.ajax('${ctx}/wechat/cart/listAddCart.htm', {
						data : {
							key : '59c23bdde5603ef993cf03fe64e448f1',
							specId : onlineSpecId, cartCount : updateCartCount, onlineId : onlineId
						},
						dataType : 'json',//服务器返回json格式数据
						type : 'post',//HTTP请求类型
						timeout : 10000,//超时时间设置为10秒；
						success : function(data) {
							if (data.flag) {//先判断是否登录
								//mui.toast("加入购物车成功");
								mui('#selectBox').popover('hide');
								compute();
								document.getElementById("cartnum").innerText = data.cartCount;
								document.getElementById("cartCount" + cartId).value = addCartCount;
							} else {
								mui.toast(data.message);
							};
						},
						error : function(xhr, type, errorThrown) {
							//异常处理；
							console.log(type);
						}
					});
				});

				//点击去付款按钮
				doc.getElementById('goPay').addEventListener('tap', function(event) {
					var inputs = document.querySelectorAll(".mui-numbox input[type='number']");
					for (var i = 0; i < inputs.length; i++) {
						inputs[i].blur();
					}
					;
					// 获取选中的数据
					var checkboxs = mui("#goods-main-box .mui-input-row input[type='checkbox']:checked");
					var arr = new Array();
					var flag = true;
					checkboxs.each(function(e) {
						// 上线价格
						var salePrice = this.parentNode.querySelector(".m-price span").innerText;
						// 上线返现金额
						var cashbackAmount = this.parentNode.querySelector(".b-money span").innerText;
						// 上线编号
						var onlineId = this.parentNode.querySelector("input[name='onlineId']").value;
						// 上线规格编号
						var onlineSpecId = this.parentNode.querySelector("input[name='onlineSpecId']").value;
						// 购物车编号
						var cartId = this.parentNode.querySelector("input[name='cartId']").value;
						// 购物车数量
						var number = this.parentNode.querySelector("input[name='number']").value;
						// 规格名称
						var onlineSpec = this.parentNode.querySelector("input[name='onlineSpec']").value;
						// 上线标题
						var onlineTitle = this.parentNode.querySelector("input[name='onlineTitle']").value;
						// 产品ID
						var produceId = this.parentNode.querySelector("input[name='produceId']").value;
						// 商品主图
						var picPath = this.parentNode.querySelector(".car-inner-box img[class='goodspic']").src;
						var unitJson = '{"salePrice":"' + salePrice
								+ '","cashbackAmount":"' + cashbackAmount
								+ '","onlineId":"' + onlineId
								+ '","onlineSpecId":"' + onlineSpecId
								+ '","cartId":"' + cartId
								+ '","number":"' + number
								+ '","onlineTitle":"' + onlineTitle
								+ '","onlineSpec":"' + onlineSpec
								+ '","produceId":"' + produceId
								+ '","picPath":"' + picPath + '"}';
						arr.push(unitJson);
					});
					if (!flag) {
						return;
					}

					// 判断是否选中数据
					if (arr != null && arr.length > 0) {
						console.log(totalPrice);
						console.log(totalBack);
						console.log(num);
						var jsondata = JSON.parse('[{"info":['+ arr + '],"totalPrice":"' + totalPrice + '","totalBack":"' + totalBack + '","totalNumber":"' + num + '"}]');
						localStorage.setItem("orderInfo", JSON.stringify(jsondata[0]));
						window.location.href = "${ctx}/wechat/order/checkorderpage.htm";
					} else {
						mui.confirm("您还未选购任何商品，去看看?", " ", [ '有点忙', '说走就走' ], function(e) {
							if (e.index == 1) {
								window.location.href = "${ctx }/wechat/goods/allGoodsList.htm";
							};
						});
					}
				});

				//单条操作按钮（删除、收藏）			
				$('#goods-group').on('tap', '.mui-slider-right .mui-btn', function(event) {
					var elem = this;
					var li = elem.parentNode.parentNode;
				    // 购物车编号
					var cartId = elem.parentNode.parentNode.querySelector("input[name='cartId']").value;// 购物车编号
					if (elem.classList.contains("delete")) { //从购物车删除
						mui.ajax('${ctx}/wechat/cart/delUserCart.htm', {
							data : {
								key : '59c23bdde5603ef993cf03fe64e448f1',
								cartId : cartId
							},
							dataType : 'json',//服务器返回json格式数据
							type : 'post',//HTTP请求类型
							timeout : 10000,//超时时间设置为10秒；
							success : function(data) {
								if (data.flag) {
									//此处执行ajax，删除后台相应数据，删除成功再移除前端元素并重新计算价格
									li.parentNode.removeChild(li);
									compute();
									LoadCartNum();
									mui.toast(data.message);
									return;
								}
								mui.toast(data.message);
							},
							error : function(xhr, type, errorThrown) {
								//异常处理；
								console.log(type);
							}
						});
					}
				});

				//编辑按钮
				$('.mui-bar-nav').on('tap', '#editor', function(event) {
					var elem = this;
					var case_1 = mui("#case_1")[0];
					var case_2 = mui("#case_2")[0];
					if (elem.classList.contains("on")) {
						elem.classList.remove("on");
						elem.innerText = "编辑";
						case_1.style.opacity = 1;
						case_1
								.querySelectorAll(".total-price-box")[0].style.opacity = 1;
						case_2.style.top = 50 + "px";
					} else {
						elem.classList.add("on");
						elem.innerText = "退出编辑";
						case_1.style.opacity = 0;
						case_1
								.querySelectorAll(".total-price-box")[0].style.opacity = 0;
						case_2.style.top = 0 + "px";
					}
					;
				});

				//批量操作按钮（批量删除、批量收藏）			
				$('#case_2').on('tap', 'button', function(event) {
					var checkboxs = mui("#goods-main-box .mui-input-row input[type='checkbox']:checked");
					var cartIdarr = new Array();
					
					checkboxs.each(function(e) {
						var cartId = this.parentNode.querySelector("input[name='cartId']").value;
						cartIdarr.push(cartId);
					});
					
					var cartIds = String(cartIdarr);//选中的购物车的ID数组（字符串）
					if (event.target.id == "delbtn") {//批量删除
						if (checkboxs == null || checkboxs.length < 1) {
							mui.toast("没有可删除的数据哦！", " ");
							return;
						}
						mui.confirm("确定从购物车删除选定的商品吗？", " ", [ '确认', '取消' ], function(e) {
							if (e.index == 0) {
								mui.ajax('${ctx}/wechat/cart/batchdelete.htm', {
									data : {
										key : '59c23bdde5603ef993cf03fe64e448f1',
										cartId : cartIds
									},
									dataType : 'json',//服务器返回json格式数据
									type : 'post',//HTTP请求类型
									timeout : 10000,//超时时间设置为10秒；
									success : function(data) {
										if (data.flag) {
											checkboxs.each(function(e) {
												var li = this.parentNode.parentNode;
												li.parentNode.removeChild(li);
											});
											LoadCartNum();
											mui.toast("已删除选中的商品");
											compute();
											return;
										}
										mui.toast(data.message);
									},
									
									error : function(xhr, type, errorThrown) {
										//异常处理；
										console.log(type);
									}
								});
							};
						})
					}
				});
			});
		})(mui, document);

		//计算总额
		function compute() {
			totalPrice = 0;
			totalBack = 0;
			num = 0;
			var checkboxs = mui("#goods-main-box .mui-input-row input[type='checkbox']:checked");
			checkboxs.each(function(e) {
				var pm = this.parentNode.querySelector(".m-price span").innerText;
				var bm = this.parentNode.querySelector(".b-money span").innerText;
				var nb = parseInt(this.parentNode.querySelector(".mui-numbox input[type='number']").value);
				var unit_totalPrice = mul(pm, nb);
				var unit_totalBack = mul(bm, nb);
				totalPrice = totalPrice + unit_totalPrice;
				totalBack = totalBack + unit_totalBack;
				num = num + nb;
			});
			mui(".total-price-box h4 span")[0].innerHTML = "¥"
					+ totalPrice.toFixed(2);
			mui(".total-price-box h5 span")[0].innerHTML = "¥"
					+ totalBack.toFixed(2);
			mui(".paybtn span")[0].innerHTML = num;
		};

		// 精确乘法
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
			return Number(d.replace(".", "")) * Number(e.replace(".", ""))
					/ Math.pow(10, c);
		};

		// 获取购物车列表
		function GetCartList() {
			mui.ajax('${ctx}/wechat/cart/getcartlist.htm', {
				data : {
					key : '59c23bdde5603ef993cf03fe64e448f1',
					start : start,
					limit : limit
				},
				async : false,
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				timeout : 10000,//超时时间设置为10秒；
				success : function(data) {
					console.log(data);
					totalPrice = 0;
					totalBack = 0;
					num = 0;
					if (data.flag) {
						var html = '';
						document.body.querySelector("#goods-group").innerHTML = html;
						for (var i = 0; i < data.message.length; i++) {
							var strs = new Array();
							// 根据逗号获取图片后缀
							strs = data.message[i].picPath.split("."); 
							html += '<div class="mui-table-view-cell">';
							html += '	<div class="mui-slider-right mui-disabled">';
							html += '		<a class="mui-btn mui-btn-red mui-icon mui-icon-trash delete"></a>';
							html += '	</div>';
							html += '	<div class="mui-input-row mui-checkbox mui-left mui-slider-handle">';
							html += '		<div style="display:none">';
							html += '			<input type="text" name="onlineId" value="'+ data.message[i].onlineId + '"/>';
							html += '			<input type="text" name="cartId" value="' + data.message[i].id + '"/>';
							html += '			<input type="text" name="onlineSpecId" value="' + data.message[i].onlineSpecId + '"/>';
							html += '			<input type="text" name="onlineSpec" value="' + data.message[i].onlineSpec + '"/>';
							html += '			<input type="text" name="onlineTitle" value="' + data.message[i].onlineTitle + '"/>';
							html += '			<input type="text" name="produceId" value="' + data.message[i].produceId + '"/>';
							html += '			<input type="text" name="cartCount" id="cartCount' + data.message[i].id + '" value="' + data.message[i].cartCount + '"/>';
							html += '		</div>';
							html += '		<div class="car-inner-box">';
							html += '			<div class="car-inner-box-img">';
							if(data.message[i].subjectType==1){
								html += '				<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].onlineId + '&promoterId=${userId}' + '" class="full-return">';
								html += '					<img src="${goodsImgUrl}' + data.message[i].picPath + '" alt="" class="goodspic"/>';
								html += '				</a>';
							}else{
								html += '				<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].onlineId + '&promoterId=${userId}' + '" class="">';
								html += '					<img src="${goodsImgUrl}' + data.message[i].picPath + '" alt="" class="goodspic"/>';
								html += '				</a>';
							}
							html += '			</div>';
							html += '			<div class="car-inner-body">';
							html += '				<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].onlineId + '&promoterId=${userId}' + '" name="goodsTitle">' + data.message[i].onlineTitle + '</a></h5>';
							html += '				<p name="onlineSpec">' + data.message[i].onlineSpec + '&nbsp;&nbsp;&nbsp;</p><br/>';
							html += '				<div class="price-area">';
							if(data.message[i].subjectType==1){
								html += '					<span class="m-price">¥<span>' + formatCurrency(data.message[i].salePrice) + '</span></span>';
								html += '					<span class="b-money"><span>'  + '</span></span>';
							}else{
								html += '					<span class="m-price">¥<span>' + formatCurrency(data.message[i].salePrice) + '</span></span>';
								html += '					<span class="b-money">返 <span>' + formatCurrency(data.message[i].cashbackAmount) + '</span> 元</span>';
							}
							html += '				</div>';
							html += '				<div class="mui-numbox" data-numbox-min="1"">';
							html += '					<button class="mui-btn mui-numbox-btn-minus" type="button">-</button>';
							html += '					<input class="mui-numbox-input" name="number" type="number" value="' + data.message[i].cartCount + '"/>';
							html += '					<button class="mui-btn mui-numbox-btn-plus" type="button">+</button>';
							html += '				</div>';
							html += '			</div>';
							html += '		</div>';
							html += '		<input name="g-checkbox" value="0" type="checkbox" checked="checked">';
							html += '	</div>';
							html += '</div>';
							totalPrice += parseFloat(formatCurrency(data.message[i].salePrice));
							totalBack += parseFloat(formatCurrency(data.message[i].cashbackAmount));
							num += parseInt(data.message[i].cartCount);
						}
						document.body.querySelector("#goods-group").innerHTML = html;
						return;
					}
					mui.toast(data.message);
				},
				error : function(xhr, type, errorThrown) {
					//异常处理；
					console.log(type);
					if (type == 'timeout') {
						mui.toast("连接超时啦");
					}
				}
			});
		};
	</script>
</body>
</html>