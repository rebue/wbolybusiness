<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
<link rel="stylesheet" href="${ctx }/css/wechat/goods.css">
<link href="${ctx }/css/wechat/swiper-3.4.1.min.css" rel="stylesheet" />

<!--index.html引用JS位置调整-->
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/wechat/mui.zoom.js"></script>
<script src="${ctx }/js/wechat/mui.previewimage.js"></script>
<script src="${ctx }/js/wechat/swiper-3.4.1.min.js"></script>
<script src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
<script src="${ctx }/js/wechat/jquery-scrollLoading.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
</head>
<body>
	<div id="home_index" class="weixin-index"
		onclick="window.location.href='${ctx}/wechat/index/indexInfo.htm?promoterId=${userId}'">
		<span class="mui-icon mui-icon-home"></span> <span class="text">首页</span>
	</div>
	<header class="mui-bar mui-bar-nav goods-detail-head">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" id="action_back"></a>
	</header>
	<nav class="mui-bar mui-bar-tab goods-detail-nav">
		<a class="mui-tab-item-wboly smaller" href="#"> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-custom"></span> 
			<span class="mui-tab-label">联系商家</span>
		</a> 
		<a class="mui-tab-item-wboly smaller tab-cart" href="${ctx }/wechat/cart/shoppingcart.htm?promoterId=${userId}">
			<span id="cartnum" class="mui-badge mui-badge-danger">0</span> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-cart"></span> 
			<span class="mui-tab-label">购物车</span>
		</a> 
		<a class="mui-tab-item-wboly smaller tab-share" href="javascript:guideDiv();"> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-share"></span> 
			<span class="mui-tab-label">分享</span>
		</a>
		<button class="bottombtn mui-pull-right" id="addcartbtn">加入购物车</button>
	</nav>
	<div id="guideDiv"
		style="position: fixed; top: 0px; left: 0px; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.8); display: ${guideDisplay}; z-index: 99;">
		<img src="${ctx }/images/wechat/guide.png" alt="" style="width: 100%;" />
	</div>
	<div id="goods-detail-content" class="mui-content mui-scroll-wrapper">
		<div class="mui-scroll">
			<!--轮播开始-->
			<!-- 详情页如果需要加图片的限购 则在下面这个div 的class 加上 limit -->
			<div id="slider" class="swiper-container  goods-detail-slider">
				<div class="swiper-wrapper">
					<!-- 第1张 -->
					<div class="swiper-slide">
						<div class="swiper-zoom-container">
							<img src="">
						</div>
					</div>
				</div>
				<div class="swiper-pagination"></div>
			</div>
			<!--轮播结束-->

			<div class="detail-content">
				<ul class="mui-table-view" id="detail-content-ul"></ul>
			</div>

			<div id="detail_slider" class="mui-slider">
				<div id="fixedTab1">
					<div id="sliderSegmentedControl"
						class="mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
						<a class="mui-control-item mui-active" href="#item1mobile">图文详情</a>
						<!-- <a class="mui-control-item" href="#item2mobile"> 产品参数 </a>  -->
					</div>
					<div id="sliderProgressBar" class="mui-slider-progress-bar mui-col-xs-4"></div>
				</div>
				<div class="mui-slider-group">
					<div id="item1mobile" class="mui-slider-item mui-control-content mui-active">
						<div id="tuwen" class="detail-item-box"></div>
					</div>

					<div id="item2mobile" class="mui-slider-item mui-control-content">
						<div id="canshu" class="detail-item-box">
							<ul class="mui-table-view"></ul>
						</div>
					</div>

					<div id="item3mobile" class="mui-slider-item mui-control-content">
						<div id="comments" class="detail-item-box">
							<div class="comments-tab-box  mui-segmented-control mui-segmented-control-inverted"></div>
							<ul class="mui-table-view" id="comments_list">
								<div class="mui-loading">
									<div class="mui-spinner"></div>
								</div>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!--悬浮选项卡开始-->
		<div id="fixedTab2">
			<div id="sliderSegmentedControl" class="mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
				<a class="mui-control-item mui-active" href="#item1mobile"> 图文详情</a>
				<!-- <a class="mui-control-item" href="#item2mobile"> 产品参数 </a>  -->
			</div>
		</div>
		<!--悬浮选项卡结束-->
	</div>

	<!--弹出层开始-->
	<div id="selectBox"
		class="mui-popover mui-popover-bottom mui-popover-action ">
		<div class="selectBox_titlebar mui-row" id="selectBox_titlebar">
			<a href="#selectBox" class="mui-icon mui-icon-close" id="ss_close_btn"></a>
			<div class="selectBox_titlebar_img mui-col-sm-3 mui-col-xs-3"></div>
			<div class="selectBox_titlebar_con mui-col-sm-9 mui-col-xs-9">
				<h5>${goodsBase['goodsName']}</h5>
				<div class="sbox_price_bar">
					<!--以下三个数字是在弹出窗口后,通过AJAX方法【AjaxUpdate()】获取并设值-->
					
				</div>
				<div class="skuNameShow">请选择商品规格、购买数量</div>
			</div>
		</div>
		<div id="holder"></div>
		<div class="mui-scroll-wrapper" id="scroll-wrapper">
			<div class="mui-scroll" id="scroll-body">
				<ul class="mui-table-view" id="sku_box">
					<li class="mui-table-view-cell sku_unit"></li>
					<li class="mui-table-view-cell buynum_bar">
						<span class="buynum_tit">购买数量</span>
						<div class="mui-numbox" id="mui-numbox" data-numbox-min="1" data-numbox-max="100">
							<!-- 大数字为默认SKU库存，或当前选中SKU的库存 -->
							<button class="mui-btn mui-btn-numbox-minus" id="btn-minus" type="button">-</button>
							<input id="buyNum" class="mui-input-numbox" type="number" value="1">
							<button class="mui-btn mui-btn-numbox-plus" id="btn-plus" type="button">+</button>
						</div>
					</li>
				</ul>
			</div>
		</div>
		<div id="selectBox_bottombar">
			<ul class="mui-table-view">
				<li class="mui-table-view-cell buy_add_bar">
					<div class="total_bar mui-pull-left">
						合计：<span id="total_price"></span>
					</div>
					<button class="bottombtn mui-pull-right" id="addcartbtn">加入购物车</button>
				</li>
			</ul>
		</div>
	</div>
	<!--弹出层结束-->

	<script type="text/javascript" charset="utf-8">
		var skuIds = ""; //选中的SKU数组(字符串)
		var buyNum = 1; //购买数量								
		var price = "";//默认SKU、或当前选中SKU的单价;
		var back = "";//默认SKU、或当前选中SKU的返现;
		var stock = "";//默认SKU、或当前选中SKU的库存;
		var total = parseFloat(mul(price, buyNum)).toFixed(2);//当前总价	
		var goodsId = "${goodsBase['goodsId']}";
		//如果立即购买、收藏、添加购物车等动作需要某些数据,最好一进来就设值
		//比如var stock = "${abc.def}";var price = "${ghi.jkl}"等;	
		var commentType = "0"; //默认会员评价筛选的类型=全部;

		//图片放大初始化
		mui.previewImage();

		(function($, doc) {
			mui.init();
			$.ready(function() {
				mui('body').on('tap', '.tab-cart', function() {
					document.location.href = this.href;
				});
				mui('body').on('tap', '.tab-share', function() {
					document.location.href = this.href;
				});
				//区域滚动初始化
				mui('#goods-detail-content.mui-scroll-wrapper').scroll({
					bounce : false
				});
	
				LoadAds();
				LoadGoodsContent();
				LoadGoodsSku();
// 				mui('#selectBox').popover('show');
				SaveSKU();
	
				LoadCartNum();
				setconHeight();
	
				//SKU选择层弹出事件
				var selectBtn = document.getElementById('selectBox');
				selectBtn.addEventListener('shown', function(e) {
					mui('#selectBox .mui-scroll-wrapper').scroll();
					if (document.querySelector(".sku_active") != null) {
						// 商品规格编号
						var specId = document.querySelector(".sku_active").getAttribute("data-sku");
						AjaxUpdate(document.querySelector(".sku_active").getAttribute("data-value"), specId);
						SaveSKU(1);
					}
				});
	
				mui("#guideDiv")[0].addEventListener("tap", function() {
					document.getElementById("guideDiv").style.display = (document.getElementById("guideDiv").style.display == "none") ? "" : "none";
				});
	
				//主图初始化
				var mySwiper = new Swiper('#slider.swiper-container', {
					loop : true,
					speed : 600,
					pagination : '.swiper-pagination',
					zoom : true
				});
	
				//图片比例调整								
				var slider_img_width = mui("#slider .swiper-slide img")[0].clientWidth;
				for (var i = 0; i < mui("#slider .swiper-slide img").length; i++) {
					mui("#slider .swiper-slide img")[i].style.height = slider_img_width + "px";
				};
	
				//加入购物车
				mui(document).on('tap', '#addcartbtn', function() {
					var attr = document.querySelector("#toSelect span").innerHTML;
					if (attr == "点击选择商品规格") {
						mui('#selectBox').popover('show');
						mui.toast("请选择商品规格");
						return;
					}
					var buyNumss = mui("#buyNum")[0].value;
					loading(1);
					mui.ajax('${ctx}/wechat/cart/listAddCart.htm', {
						data : {
							key : '59c23bdde5603ef993cf03fe64e448f1',
							onlineId : "${onlineId}",
							specId : skuIds,
							cartCount : buyNumss
						},
						dataType : 'json',//服务器返回json格式数据
						type : 'post',//HTTP请求类型
						success : function(data) {
							loading(2);
							if (data.flag) {//先判断是否登录
								//写个ajax事件,添加成功后返回购物车里的商品数量
								//var count = document.getElementById("cartnum").innerText;count ++;//这句是模拟的，不用要																													
								mui.toast("成功加入购物车！");
								mui('#selectBox').popover('hide');
								document.getElementById("cartnum").innerText = data.cartCount;
							} else {
								if ("您没有登录" == data.message) {
									mui.confirm(data.message + ',请先登录哦', ' ', ['等等吧', '说走就走' ], function(e) {
										if (e.index == 1) {
											window.location.href = "${ctx }/wechat/user/logInPage.htm";//去到登录页面
										}
									});
									return;
								}
								mui.toast(data.message);
							};
						},
						error : function( xhr, type, errorThrown) {
							loading(2);
							//异常处理；
							console.log(type);
						}
					});
				});
	
				//SKU点击事件
				mui("#sku_box").on('tap', '.sku_list li', function() {
					// 商品规格编号
					var specId = this.getAttribute("data-sku");
					var _this = this;
					buyNum = "1";//重置购买数量
					if (!_this.classList.contains("sku_active")) {
						for (var i = 0; i < siblings(_this).length; i++) {
							siblings(_this)[i].classList.remove('sku_active');
						};
						_this.classList.add("sku_active");
					};
					history.pushState("", "", "?promoterId=${userId}&onlineId=${onlineId}&specId=" + specId);
					SaveSKU(1);
					var sTop = document.getElementById('selectBox_titlebar').clientHeight + 1;
					document.getElementById('scroll-wrapper').style.top = sTop + "px";
					AjaxUpdate(document.querySelector(".sku_active").getAttribute("data-value"), specId);
				});
	
				//改变购买数量时触发
				mui("#mui-numbox").on('change', '#buyNum', function() {
					buyNum = mui("#mui-numbox").numbox().getValue();//全局变量：购买数量
					total = parseFloat(mul(price, buyNum)).toFixed(2);//全局变量：计算当前总价（当前单价*购买数量）
					document.querySelector("#total_price").innerText = "¥" + total;//显示当前总价
				});
	
				//保存选中SKU
				function SaveSKU(type) {
					var sku_arr = new Array();
					var sku_name = new Array();
					var sku_list = document.querySelectorAll(".sku_list li.sku_active");
					for (var i = 0; i < sku_list.length; i++) {
						var skuId = sku_list[i].getAttribute("data-sku");
						var skuName = sku_list[i].innerText;
						sku_arr.push(skuId);
						sku_name.push(skuName);
					};
					skuNames = String(sku_name);
	
					if (skuNames != "") {
						document.querySelector(".skuNameShow").innerHTML = "您已选择：" + skuNames;
					} else {
						document.querySelector(".skuNameShow").innerHTML = "请选择商品规格、购买数量";
					};
					if (type == 1) {
						document.querySelector("#toSelect span").innerHTML = "您已选择：<em>" + skuNames + "</em>";
					};
					skuIds = String(sku_arr);//全局变量：选中的SKU数组（字符串）						
				};
	
				//切换会员评价筛选方式
				mui(".comments-tab-box").on('tap', 'a.mui-control-item', function() {
					commentType = this.getAttribute("data-id");
					getComments(commentType);
				});
	
				//input焦点bug
				document.addEventListener('tap', function(e) {
					var target = e.target;
					var inputs = document.querySelectorAll("input");
					if (!(target.tagName && (target.tagName === 'TEXTAREA' || (target.tagName === 'INPUT' && (target.type === 'text' || target.type === 'search' || target.type === 'number'))))) {
						for (var i = 0; i < inputs.length; i++) {
							if (inputs[i].type == "search" || inputs[i].type == "text" || inputs[i].type == "number") {
								inputs[i].blur();
							}
						}
					}
				});
	
				//计算内容区最小高度
				function setconHeight() {
					var head = mui(".goods-detail-head")[0].clientHeight;
					var nav = mui(".goods-detail-nav")[0].clientHeight;
					var doc = document.documentElement.clientHeight - head - nav;
					var tab = document.getElementById("fixedTab1").clientHeight;
					var height = doc - tab;
					var obj = document.querySelectorAll("#detail_slider .mui-slider-group .mui-control-content");
					for (var i = 0; i < obj.length; i++) {
						obj[i].style.minHeight = height + "px";
					};
				}
	
				//选项卡滚动到顶部时悬浮固定
				document.getElementById("goods-detail-content").addEventListener("scroll", function(e) {
					var obj1 = document.getElementById("fixedTab1");
					var obj2 = document.getElementById("fixedTab2");
					var iconback = document.getElementById("action_back");
					var s1 = mui("#slider")[0].clientHeight;
					var s2 = mui(".detail-content")[0].clientHeight;
					var top = -(s1 + s2 + 13);
					if (e.detail.lastY < top) {
						obj2.style.display = "block";
						obj1.style.visibility = "hidden";
					} else {
						obj2.style.display = "none";
						obj1.style.visibility = "visible ";
					};
					if (e.detail.lastY <= top + 40) {
						iconback.style.display = "none";
					} else {
						iconback.style.display = "inline-block";
					};
				});
	
				//切换选项卡事件
				document.querySelector('#detail_slider.mui-slider').addEventListener('slide', function(event) {
					var s1 = mui("#slider")[0].clientHeight;
					var s2 = mui(".detail-content")[0].clientHeight;
					var top = -(s1 + s2 + 13); //从商品详情选项卡栏顶部到轮播图顶部的距离，13是选项卡上的margin
					/*
					var head = mui(".goods-detail-head")[0].clientHeight;
					var nav = mui(".goods-detail-nav")[0].clientHeight;
					var view = document.documentElement.clientHeight-head-nav; //除去头部和底部的实际显示高度
					var scroll = -(top-view+340);//主体区域滚动需要向上滚动多少，才能使详情内容区至少显示300的高度
					 */
					if (mui('#goods-detail-content.mui-scroll-wrapper').scroll().lastY > top) {
						mui('#goods-detail-content.mui-scroll-wrapper').scroll().scrollTo(0, top + 1, 700);
					} else {
						mui('#goods-detail-content.mui-scroll-wrapper').scroll().scrollTo(0, top - 1, 700);
					};
					var j = event.detail.slideNumber;
					var item = mui('#fixedTab2 .mui-control-item')[j];
					item.classList.add("mui-active");
					for (var i = 0; i < siblings(item).length; i++) {
						siblings(item)[i].classList.remove("mui-active");
					};
	
					if (event.detail.slideNumber === 2) {
						getComments(commentType); //切换到会员评价选项卡，加载评价
					};
				});
			});
		})(mui, document);

		//兄弟元素选择器
		function siblings(o) {
			var a = [];
			var p = o.previousSibling;
			while (p) {
				if (p.nodeType === 1) {
					a.push(p);
				}
				p = p.previousSibling
			}
			a.reverse()
			var n = o.nextSibling;
			while (n) {
				if (n.nodeType === 1) {
					a.push(n);
				}
				n = n.nextSibling;
			}
			return a
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

		// 加载轮播
		function LoadAds() {
			mui.ajax('${ctx}/wechat/goods/goodsCarouselPic.htm', {
				data : {
					onlineId : "${onlineId}"
				},
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				success : function(data) {
					var html = "";
					for (var i = 0; i < data.length; i++) {
						var strs = new Array();
						// 根据逗号获取图片后缀
						strs = data[i].picPath.split(".");
						html += '<div class="swiper-slide">';
						html += '	<div class="swiper-zoom-container">';
						html += '		<img src="${goodsImgUrl}' + data[i].picPath + '">';
						html += '	</div>';
						html += '</div>';
					}
					document.body.querySelector("#slider .swiper-wrapper").innerHTML = html;
				}
			});
		}

		// 加载商品内容
		function LoadGoodsContent() {
			mui.ajax('${ctx}/wechat/goods/selectGoodsDetails.htm', {
				data : {
					onlineId : "${onlineId}"
				},
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				success : function(data) {
					console.log(data);
					var html = "";
					var sboxHtml = "";
					document.body.querySelector("#detail-content-ul").innerHTML = html;
					document.body.querySelector(".sbox_price_bar").innerHTML = sboxHtml;
					html += '<li class="mui-table-view-cell mui-row title-bar">';
					html += '	<div class="mui-col-xs-11 mui-col-sm-11" id="title-bar">';
					html += '		<h5>' + data[0].onlineTitle + '</h5>';
					html += '	</div>';
					html += '	<div class="mui-col-xs-1 mui-col-sm-1">';
					html += '		<div id="collection" class="collection"></div>';
					html += '	</div>';
					html += '</li>';
					console.log(data[0].subjectType);
					if(data[0].subjectType==0){
						html += '<li class="mui-table-view-cell price-bar">';
						html += '	<span class="price">¥<em>' + formatCurrency(data[0].salePrice) + '</em></span>';
						html += ' 	<span class="back-money">返<em>' + formatCurrency(data[0].cashbackAmount) + '</em>元</span>';
						html += '</li>';
						sboxHtml +='<span class="ss_price">¥<span id="ss_price"></span></span> ';
						sboxHtml +='<span class="ss_back">返<span id="ss_back"></span>元</span>';
						sboxHtml +='<span class="goodsnum" id="stock">库存：<span></span></span>';
					}else{
						html += '<li class="mui-table-view-cell price-bar">';
						html += '	<span class="full-return-price price">¥<em>' + formatCurrency(data[0].salePrice) + '</em></span>';
						html += ' 	<span class="now-price">￥<em>0元'  + '</em></span>';
						html += '<span id="ss_back" style="display:none"></span>'
						html += ' 	<span style="display:none" class="back-money">返<em></em></span>'
						html += '</li>';
						sboxHtml +='<span class="ss_full_return_price">¥<span id="ss_price"></span></span> ';
						sboxHtml +='<span class="ss_now_price">￥<em style="display=none;" ></em>0元</span>';
						sboxHtml +='<span class="goodsnum" id="stock">库存：<span></span></span>';
					}
					html += '<li class="mui-table-view-cell selectbar">';
					html += '	<a href="#selectBox" class="mui-navigate-right" id="toSelect"><span>点击选择商品规格</span></a>';
					html += '</li>';
					document.body.querySelector("#detail-content-ul").innerHTML = html;
					document.body.querySelector(".sbox_price_bar").innerHTML = sboxHtml;

					// 商品详情图片
					//第二个参数中的 g 表示全部匹配,i表示忽略大小写
					var regS = new RegExp("src", "g");
					//服务器返回响应
					document.body.querySelector("#tuwen").innerHTML = data[0].onlineDetail.replace(regS, "data-url");
					$("img").scrollLoading();
				}
			});
		}

		// 加载商品规格
		function LoadGoodsSku() {
			mui.ajax(
			'${ctx}/wechat/goods/selectGoodsSpecDetails.htm', {
				data : {
					specId : 0,
					onlineId : "${onlineId}"
				},
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				success : function(data) {
					if (data.length > 0) {
						var strs = new Array();
						// 根据逗号获取图片后缀
						strs = data[0].picPath.split(".");
						document.body.querySelector("#selectBox_titlebar .mui-col-xs-3").innerHTML = '<img src="${goodsImgUrl}' + data[0].picPath + '" alt="" />';
						var html = "";
						document.body.querySelector("#sku_box .sku_unit").innerHTML = html;
						html += '<ul class="sku_list">';
						for (var i = 0; i < data.length; i++) {
							html += '<li class="sku_item" data-sku="' + data[i].specId + '" data-value="' + data[i].onlineSpec + '">' + data[i].onlineSpec + '</li>';
						}
						html += '</ul>';
						document.body.querySelector("#sku_box .sku_unit").innerHTML = html;
						var specId = "${specId}";
						if (specId != null && specId != "" && specId != "null") {
							for (var i = 0; i < document.querySelectorAll(".sku_list").length; i++) {
								var skulist = document.querySelectorAll(".sku_list")[i];
								for (var j = 0; j < skulist.querySelectorAll("li").length; j++) {
									var skuitem = skulist.querySelectorAll("li")[j];
									var specIds = skuitem.getAttribute("data-sku");
									if (specId == specIds) {
										skuitem.classList.add("sku_active");
										AjaxUpdate(data[i].onlineSpec, specId);
									}
								}
							};
						} else {
							//默认选中第一个SKU
							for (var i = 0; i < document.querySelectorAll(".sku_list").length; i++) {
								var skulist = document.querySelectorAll(".sku_list")[i];
								var skuitem = skulist.querySelectorAll("li")[0];
								var specId = skuitem.getAttribute("data-sku");
								history.pushState("", "", "?promoterId=${userId}&onlineId=${onlineId}&specId=" + specId);
								skulist.querySelectorAll("li")[0].classList.add("sku_active");
							};
						}
					}
				}
			});
		}
		
		//提交选中的SKU数据,返回得到相应SKU的单价、返现金、库存等;
		function AjaxUpdate(id, specId) {
			skuIds = specId;
			//写ajax方法，可以提交skuIds等数据
			mui.ajax('${ctx}/wechat/goods/selectGoodsSpecDetails.htm', {
				data : {
					key : '59c23bdde5603ef993cf03fe64e448f1',
					specId : specId,
					onlineId : "${onlineId}"
				},
				dataType : 'json',//服务器返回json格式数据
				type : 'post',//HTTP请求类型
				success : function(data) {
					//以下是得到返回数据success后执行的：
					//如果选择了多种SKU,而这种SKU的组合不存在,库存返回0即可	
					mui.each(data, function(index, item) {
						if (id == item.onlineSpec) {
							price = formatCurrency(item.salePrice);//全局变量：当前单价
							back = formatCurrency(item.cashbackAmount);//全局变量：当前返现
							stock = item.currentOnlineCount - item.saleCount;//全局变量：当前库存
							total = parseFloat(mul(price, buyNum)).toFixed(2);//全局变量：计算当前总价（当前单价*购买数量）
							document.querySelector("#ss_price").innerText = price;//显示所选SKU的单价
							document.querySelector(".price em").innerText = price;
							document.querySelector("#ss_back").innerText = back;//显示所选SKU的返现
							document.querySelector(".back-money em").innerText = back;
							document.querySelector("#stock span").innerText = stock;//显示所选SKU的库存	
							document.querySelector("#total_price").innerText = "¥" + total;//显示当前总价
							mui("#mui-numbox").numbox().setOption('max', stock);//限制数量输入框不能超出库存
							mui("#mui-numbox").numbox().setValue(1);
						}
					});
				},
				error : function(xhr, type,
						errorThrown) {
					//异常处理；
					console.log(type);
				}
			});
		};
	</script>
</body>
</html>