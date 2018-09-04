<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>大卖网络</title>
<meta name="wap-font-scale" content="no">
<meta name="viewport"
	content="width=device-width, initial-scale=1,maximum-scale=1,user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="stylesheet" href="${ctx }/css/wechat/mui.min.css">
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/mui-icons-extra.css" />
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
<link href="${ctx }/css/wechat/goods.css" rel="stylesheet" />
<!--index.html引用JS位置调整-->
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<div id="home_index" class="weixin" style="color: #c0c0c0;"
		onclick="window.location.href='${ctx}/wechat/index/indexInfo.htm?promoterId=${userId}'">
		<span class="mui-icon mui-icon-home"></span> <span class="text">首页</span>
	</div>
	<div id="offCanvasWrapper"
		class="mui-off-canvas-wrap mui-draggable mui-slide-in">
		<!-- 菜单容器 -->
		<aside id="aside" class="mui-off-canvas-right">
			<div id="offCanvasSideScroll" class="mui-scroll-wrapper">
				<div class="mui-scroll">
					<!-- 菜单具体展示内容 -->
					<ul class="mui-table-view">
						<li class="mui-table-view-cell side-head">筛选</li>
						<li class="mui-table-view-cell"><p>价格区间：</p></li>
						<li class="mui-table-view-cell mui-row priceselect">
							<div class="mui-input-row mui-col-sm-5 mui-col-xs-5">
								<input type="number" class="mui-input-clear" placeholder="最低价"
									name="low">
							</div>
							<p class="mui-col-sm-2 mui-col-xs-2">──</p>
							<div class="mui-input-row mui-col-sm-5 mui-col-xs-5">
								<input type="number" class="mui-input-clear" placeholder="最高价"
									name="hign">
							</div>
						</li>
						<li class="mui-table-view-cell"><p>商品类型：</p></li>
						<form id="subjectType" class="mui-input-group subjectType">
							<div class="mui-input-row mui-radio mui-left ">
								<label><p style="color: #000">全返商品</p></label> <input
									name="radio" value='1' type="radio">
							</div>
							<div class="mui-input-row mui-radio mui-left ">
								<label><p style="color: #000">普通商品</p></label> <input
									name="radio" value='0' type="radio">
							</div>
						</form>
						<li class="mui-table-view-cell"><p>品牌：</p></li>
						<div id="brandlist" class="mui-card mui-input-group brandlist"></div>
					</ul>
				</div>

				<div id="buttonbox" class="mui-row">
					<button class="mui-col-sm-6 mui-col-xs-6 filterbtn btn1"
						id="clearSelect">清除选项</button>
					<button class="mui-col-sm-6 mui-col-xs-6 filterbtn btn2"
						id="filterReady">确定</button>
				</div>
			</div>
		</aside>
		<!-- 主页面容器 -->
		<div class="mui-inner-wrap">
			<header class="mui-bar mui-bar-nav goods-list-head">
				<a class="mui-icon mui-icon-left-nav mui-pull-left"
					href="${ctx }/wechat/goods/goodsNav.htm?promoterId=${userId}"></a>
				<form class="mui-input-row mui-search" id="form">
					<input type="search" class="mui-input-clear" id="searchbar"
						value="${serach}" placeholder="搜索您想要的商品">
				</form>
				<span id="changeview"
					class="mui-icon mui-icon-list mui-pull-right wboly-listype"></span>
			</header>
			<div id="typetab"
				class="mui-segmented-control mui-segmented-control-inverted">
				<a class="mui-control-item" href="#item1mobile" data-type="0">综合
				</a> <a class="mui-control-item" href="#item3mobile" data-type="2"
					data-sort="0"> 价格 </a> <a class="mui-control-item unactive"
					id="filter" href="#vitem3mobile">筛选<span
					class="mui-icon-extra mui-icon-extra-filter"></span></a>
			</div>
			<div id="refreshContainer" class="mui-scroll-wrapper"
				style="margin-top: 91px;">
				<div id="content" class="mui-content mui-row mui-scroll">
					<div id="goods-list-area" class="mui-row"></div>
				</div>
			</div>
			<div id="shoppingcart" class="shoppingcart">
				<span id="cartnum" class="mui-badge mui-badge-danger"></span> <a
					href="${ctx }/wechat/cart/shoppingcart.htm?promoterId=${userId}">
					<img src="${ctx }/images/wechat/shopping_cart_float.png" alt="" />
				</a>
			</div>
			<div class="mui-off-canvas-backdrop"></div>
		</div>
	</div>
	<script type="text/javascript" charset="utf-8">
		var sort = 0; //0降序 1升序
		var type = 0;
		var lowPrice = "";
		var hignPrice = "";
		var brandId = "";
		var keywords = "";
		var classId = "0";
		var limit = 10;
		var start = 0;
		var subjectTypeValue = "";

		(function($, doc) {
			mui.init({
				//预加载
				preloadPages : [],
				pullRefresh : {
					container : '#refreshContainer',
					down : {
						callback : pulldownRefresh
					},
					up : {
						callback : AjaxAppendData
					}
				}
			});

			$
					.ready(function() {
						AjaxGetData();//载入商品数据
						LoadCartNum();

						// 搜索事件
						form.onsubmit = function() {
							AjaxGetData();
							return false;
						};

						// 返回指定的页面
						mui('.goods-list-head').on('tap', 'a', function() {
							document.location.href = this.href;
						});
						mui('#content').on('tap', 'a', function() {
							document.location.href = this.href;
						});
						mui('#shoppingcart').on('tap', 'a', function() {
							document.location.href = this.href;
						});

						var controls = document.getElementById("typetab");
						controls.querySelector('.mui-control-item').classList
								.add('mui-active');

						//侧滑容器父节点
						var offCanvasWrapper = mui('#offCanvasWrapper');
						document.getElementById('filter').addEventListener(
								'tap', function() {
									offCanvasWrapper.offCanvas('show');
								});
						mui('#offCanvasSideScroll').scroll();

						//事件：切换排列样式
						mui(".goods-list-head")
								.on(
										'tap',
										'.wboly-listype',
										function() {
											if (this.classList
													.contains("mui-icon-list")) {
												this.classList
														.remove("mui-icon-list");
												this.classList
														.add("mui-icon-extra");
												this.classList
														.add("mui-icon-extra-class");
												document
														.getElementById("goods-list-area").classList
														.add("list-mode");
											} else {
												this.classList
														.remove("mui-icon-extra-class");
												this.classList
														.remove("mui-icon-extra");
												this.classList
														.add("mui-icon-list");
												document
														.getElementById("goods-list-area").classList
														.remove("list-mode");
											}
											;
											for (var i = 0; i < mui("#goods-list-area .view-cell img").length; i++) {
												var list_img_width = mui("#goods-list-area .view-cell img")[i].clientWidth;
												mui("#goods-list-area .view-cell img")[i].style.height = list_img_width
														+ "px";
											}
											;
										});

						//事件：切换排序方式
						mui("#typetab")
								.on(
										'tap',
										'a:not(.unactive)',
										function() {
											type = this
													.getAttribute("data-type");//排序方式标识
											if (this.getAttribute("data-sort") == 0) {//升降序标识，如果当前是降序，则转为升序
												this.setAttribute("data-sort",
														"1");
												sort = 1;
											} else if (this
													.getAttribute("data-sort") == 1) {//升降序标识，如果当前是升序，则转为降序
												this.setAttribute("data-sort",
														"0");
												sort = 0;
											}
											;
											AjaxGetData();
										});

						//事件：筛选条件
						mui("#buttonbox")
								.on(
										'tap',
										'#filterReady',
										function() {
											var brandlist = new Array();
											var obj = document
													.querySelectorAll(".brandlist input:checked");
											for (var i = 0; i < obj.length; i++) {
												var brandIds = obj[i].value;
												brandlist.push(brandIds);
											}
											var subjectTypeObject = document
													.querySelectorAll(".subjectType input");
											for (var i = 0; i < subjectTypeObject.length; i++) {
												if(subjectTypeObject[i].checked != false){
													subjectTypeValue = subjectTypeObject[i].defaultValue;
												}
											}
											console.log(subjectTypeValue);
											brandId = String(brandlist);//选中的品牌ID,用逗号隔开 
											lowPrice = document
													.querySelector(".priceselect input[name='low']").value;//价格区间，最低价
											hignPrice = document
													.querySelector(".priceselect input[name='hign']").value;//价格区间，最高价
											AjaxGetData();
											classId = "-1";
											offCanvasWrapper.offCanvas('close');
										});

						//事件：清除筛选项
						mui("#buttonbox")
								.on(
										'tap',
										'#clearSelect',
										function() {
											document
													.querySelector(".priceselect input[name='low']").value = "";
											document
													.querySelector(".priceselect input[name='hign']").value = "";
											var obj = document
													.querySelectorAll(".brandlist input");
											for (var i = 0; i < obj.length; i++) {
												obj[i].checked = false;
											}
											;
										});

						//事件：添加到购物车
						mui("#goods-list-area")
								.on(
										'tap',
										'#cart',
										function() {
											// 商品规格编号
											var specId = this
													.getAttribute("data-specId");
											// 上线编号
											var onlineId = this
													.getAttribute("data-onlineId");
											mui
													.ajax(
															'${ctx}/wechat/cart/listAddCart.htm',
															{
																data : {
																	key : '59c23bdde5603ef993cf03fe64e448f1',
																	specId : specId,
																	cartCount : 1,
																	onlineId : onlineId
																},
																dataType : 'json',//服务器返回json格式数据
																type : 'post',//HTTP请求类型
																success : function(
																		data) {
																	if (data.flag) {//先判断是否登录
																		//写个ajax事件,添加成功后返回购物车里的商品数量
																		//var count = document.getElementById("cartnum").innerText;count ++;//这句是模拟的，不用要																													
																		//mui.alert("加入购物车成功！");
																		mui
																				.toast("加入购物车成功");
																		mui(
																				'#selectBox')
																				.popover(
																						'hide');
																		document
																				.getElementById("cartnum").innerText = data.cartCount;
																	} else {
																		if ("您没有登录" == data.message) {
																			mui
																					.confirm(
																							data.message
																									+ ',请先登录哦',
																							' ',
																							[
																									'取消',
																									'登录' ],
																							function(
																									e) {
																								if (e.index == 1) {
																									window.location.href = "${ctx }/wechat/user/logInPage.htm";//去到登录页面
																								}
																							});
																			return;
																		}
																		mui
																				.toast(data.message);
																	}
																	;
																},
																error : function(
																		xhr,
																		type,
																		errorThrown) {
																	//异常处理；
																	console
																			.log(type);
																}
															});
										});

						//暂时应付input无法失去焦点的bug
						document
								.addEventListener(
										'tap',
										function(e) {
											var target = e.target;
											var inputs = document
													.querySelectorAll("input");
											if (!(target.tagName && (target.tagName === 'TEXTAREA' || (target.tagName === 'INPUT' && (target.type === 'text'
													|| target.type === 'search' || target.type === 'number'))))) {
												for (var i = 0; i < inputs.length; i++) {
													if (inputs[i].type == "search"
															|| inputs[i].type == "text"
															|| inputs[i].type == "number") {
														inputs[i].blur();
													}
												}
											}
										});
						//滚动到指定位置
						mui('#refreshContainer.mui-scroll-wrapper')
								.pullRefresh().scrollTo(0,
										localStorage.getItem('offsetTop'), 700);
					});
		})(mui, document);

		//下拉刷新事件
		function pulldownRefresh() {
			AjaxGetData(1);
		};

		// 常规ajax载入商品列表页
		function AjaxGetData(flushtype) {
			if (flushtype != 1) {
				document.getElementById("goods-list-area").innerHTML = "<a id='loading' class='loading'><span class='mui-spinner'></span></a>";
			}
			;
			limit = 10;
			start = 0;
			//搜索词汇
			keywords = document.getElementById("searchbar").value;

			if (keywords == "") {
				classId = "${goodsclass}";
			} else {
				classId = "0";
			}
			console.log("排序类型:"+type+" 板块类型："+subjectTypeValue+" 升降序:"+sort+" 最低价:"+lowPrice+" 最高价:"+hignPrice+" 品牌:"+brandId+" 搜索词:"+keywords);//测试
			mui
					.ajax(
							'${ctx}/wechat/goods/getAllGoodsList.htm',
							{ //测试用,模拟数据
								data : {
									key : '59c23bdde5603ef993cf03fe64e448f1',
									type : type,//排序方式标识
									sortType : sort,//升序降序标识
									lowPrice : lowPrice,//价格区间，最低价
									hignPrice : hignPrice,//价格区间，最高价
									brandId : brandId,//品牌标识，多个用逗号隔开
									keywords : keywords,//搜索关键词
									limit : 10,
									start : 0,
									classId : classId,
									subjectType:subjectTypeValue
								},
								dataType : 'json',//测试用，正式应为JSON
								type : 'post',
								success : function(data) {
									var html = "";
									if (data == null || data == "[]"
											|| data == "") {
										mui.toast("没有数据哦");
										document.getElementById("loading").innerText = "已载入全部数据";
										return;
									}
									for (var i = 0; i < data.length; i++) {
										if (data[i].onlineTitle != undefined
												&& data[i].salePrice != undefined
												&& data[i].picPath != undefined
												&& data[i].cashbackAmount != undefined) {
											var strs = new Array();
											// 根据逗号获取图片后缀
											strs = data[i].picPath.split(".");
											html += '<div class="view-cell mui-col-xs-6 mui-col-sm-6">';
											html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId='
													+ data[i].onlineId
													+ '&promoterId=${userId}'
													+ '" class="imghref">';
											html += '		<img src="${goodsImgUrl}' + data[i].picPath + '">';
											html += '	</a>';
											html += '	<div class="goods-list-body">';
											html += '		<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId='
													+ data[i].onlineId
													+ '&promoterId=${userId}'
													+ '">'
													+ data[i].onlineTitle
													+ '</a></h5>';
											html += '		<p>';
											html += '			<span class="price">￥<em>'
													+ formatCurrency(data[i].salePrice)
													+ '</em></span>';
											html += ' 		<span class="back-money">返<em>'
													+ formatCurrency(data[i].cashbackAmount)
													+ '</em></span>';
											html += '			<span class="mui-icon-extra mui-icon-extra-cart" id="cart" data-specId="' + data[i].specId + '" data-onlineId="' + data[i].onlineId + '"></span>';
											html += '		</p>';
											html += '	</div>';
											html += '</div>';
										}
									}
									;
									setTimeout(
											function() {
												document
														.getElementById("goods-list-area").innerHTML = html;
												if (flushtype = 1) {
													mui('#refreshContainer')
															.pullRefresh()
															.endPulldownToRefresh();
												}
												;
												for (var i = 0; i < mui("#goods-list-area .view-cell img").length; i++) {
													var list_img_width = mui("#goods-list-area .view-cell img")[i].clientWidth;
													mui("#goods-list-area .view-cell img")[i].style.height = list_img_width
															+ "px";
												}
												;
											}, 200);
									// 返回 top
									mui("#refreshContainer.mui-scroll-wrapper")
											.pullRefresh().scrollTo(0, 0);
									mui('#refreshContainer').pullRefresh()
											.refresh(true);
									// end
								},
								error : function(xhr, type, errorThrown) {
									console.log(type);
								}
							});
		}

		//ajax上滑添加（滑动到最底部时插入新数据，如果已全部加载完，则会有相应提示）
		function AjaxAppendData() {
			var obj = this;
			start += 10;
			limit += 10;
			mui
					.ajax(
							'${ctx}/wechat/goods/getGoodsList.htm',
							{ //测试用,模拟数据
								data : {
									key : '59c23bdde5603ef993cf03fe64e448f1',
									type : type,//排序方式标识
									sortType : sort,//升序降序标识
									lowPrice : lowPrice,//价格区间，最低价
									hignPrice : hignPrice,//价格区间，最高价
									brandId : brandId,//品牌标识，多个用逗号隔开
									keywords : keywords,//搜索关键词
									limit : limit,
									start : start,
									classId : classId
								},
								dataType : 'json',//测试用，正式应为JSON
								type : 'post',
								success : function(data) {
									var html = "";
									if (data != null)
										for (var i = 0; i < data.length; i++) {
											if (data[i].onlineTitle != undefined
													&& data[i].salePrice != undefined
													&& data[i].picPath != undefined
													&& data[i].cashbackAmount != undefined) {
												var strs = new Array();
												// 根据逗号获取图片后缀
												strs = data[i].picPath
														.split(".");
												html += '<div class="view-cell mui-col-xs-6 mui-col-sm-6">';
												html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId='
														+ data[i].onlineId
														+ '&promoterId=${userId}'
														+ '" class="imghref">';
												html += '		<img src="${goodsImgUrl}' + data[i].picPath + '">';
												html += '	</a>';
												html += '	<div class="goods-list-body">';
												html += '		<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId='
														+ data[i].onlineId
														+ '&promoterId=${userId}'
														+ '">'
														+ data[i].onlineTitle
														+ '</a></h5>';
												html += '		<p>';
												html += '			<span class="price">￥<em>'
														+ formatCurrency(data[i].salePrice)
														+ '</em></span>';
												html += ' 		<span class="back-money">返<em>'
														+ formatCurrency(data[i].cashbackAmount)
														+ '</em></span>';
												html += '			<span class="mui-icon-extra mui-icon-extra-cart" id="cart" data-specId="' + data[i].specId + '" data-onlineId="' + data[i].onlineId + '"></span>';
												html += '		</p>';
												html += '	</div>';
												html += '</div>';
											}
										}
									;
									var htmls = document.createElement("div");
									htmls.innerHTML = html;
									for (var i = 0; i < htmls.childNodes.length; i++) {
										document.getElementById(
												"goods-list-area").appendChild(
												htmls.childNodes[i]);
										htmls.innerHTML = html;
									}
									;
									for (var i = 0; i < mui("#goods-list-area .view-cell img").length; i++) {
										var list_img_width = mui("#goods-list-area .view-cell img")[i].clientWidth;
										mui("#goods-list-area .view-cell img")[i].style.height = list_img_width
												+ "px";
									}
									;
									obj.endPullupToRefresh(true)
								},
								error : function(xhr, type, errorThrown) {
									console.log(type);
								}
							});
		}
	</script>
</body>
</html>