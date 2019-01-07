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
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/usercenter.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/wechat/jquery.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
<script src="${ctx }/js/util/base64.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">我的积分</h4>
	</header>
	<div id="wallet_main">
		<div class="mui-content">
			<ul id="icon-grid-9" class="mui-table-view mui-grid-view mui-grid-9"></ul>
			<div class="mui-slider tab-slider">
				<div id="sliderSegmentedControl"
					class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
					<!-- <a class="mui-control-item" data-value="1" href="#item1">昨日收益<br />
						<span class="money-show" id="yesterdayIncome">0.000000</span>
					</a>  -->
					<a class="mui-control-item mui-active" data-value="1" href="#item1">累计收益<br />
						<span class="money-show" id="cumulativeIncome">0.000000</span>
					</a> 
					<a class="mui-control-item" data-value="2" href="#item2">待入积分<br />
						<span class="money-show" id="waitingPoint">0</span>
					</a> 
					<a class="mui-control-item" data-value="3" href="#item3">积分<br />
						<span class="money-show" id="point">0</span>
					</a>
				</div>
				<div class="active-bar">
					<span id="active-bar-span"></span>
				</div>
				<div class="mui-slider-group">
					<div id="item1"
						class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item1_inner" class="inner">
								<ul class="inner_tab">
									<li>交易标题</li>
									<li>交易总额</li>
									<li>交易时间</li>
								</ul>
								<ul class="inner_data" id="inner_data_1"></ul>
							</div>
						</div>
					</div>

					<div id="item2"
						class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item2_inner" class="inner">
								<ul class="inner_tab">
									<li>订单编号</li>
									<li>商品名称</li>
									<li>交易总额</li>
								</ul>
								<ul class="inner_data" id="inner_data_2"></ul>
							</div>
						</div>
					</div>

					<div id="item3"
						class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item3_inner" class="inner">
								<ul class="inner_tab">
									<li>交易标题</li>
									<li>交易总额</li>
									<li>交易时间</li>
								</ul>
								<ul class="inner_data" id="inner_data_3"></ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8">
		var userId = 12345;
		var state = 2;
		var pageNum = 1;
		var pageSize = 10;
		var $$ = jQuery.noConflict();
		var b = new Base64();
		
		(function($, doc, $$) {
			mui.init({
				pullRefresh: [{
					container: '#item1.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
 			                // 加载更多累计收益信息
			                cumulativeIncomeLoadMore(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
					container: '#item2.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多待入积分信息
			                waitingPointLoadMore(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
					container: '#item3.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多积分信息
			                pointLoadMore(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				}]
			});
			
			$.ready(function() {
				mui('#wallet_main').on('tap','.car-inner-body a',function(){document.location.href=this.href;});
				mui('#wallet_main').on('tap','.mui-table-view-cell a',function(){document.location.href=this.href;});
				// mui('body').on('tap','.mui-bar-nav a',function(){document.location.href=this.href;});
				mui("#active-bar-span")[0].style.left = mui(".tab-slider .mui-control-item.mui-active")[0].offsetLeft + "px";
				// 默认加载累计收益
				cumulativeIncomeList(0);
				// 计算内容区最小高度
				setconHeight();
				
				// 积分账号信息
				getPoint();

				//标签选中状态
				mui(document).on('tap', '.tab-slider .mui-control-item', function(e) {
					var left = this.offsetLeft;
					state = this.getAttribute("data-value");
					mui("#active-bar-span")[0].style.left = left + "px";
				});

				//切换选项卡事件
				document.querySelector('.mui-slider').addEventListener('slide', function(event) {
					mui('#wallet_main.mui-scroll-wrapper').scroll({ bounce: false });
					var i = event.detail.slideNumber;
					var obj = mui('.tab-slider .mui-control-item')[i];
					var left = obj.offsetLeft;
					state = obj.getAttribute("data-value");
					mui("#active-bar-span")[0].style.left = left + "px";
					if(event.detail.slideNumber == 0) {
						// 累计收益
						document.getElementById("inner_data_1").innerHTML = "";
						cumulativeIncomeList(0);
					} else if(event.detail.slideNumber == 1) {
						// 待入积分
						document.getElementById("inner_data_2").innerHTML = "";
						waitingPointList(0);
					} else if(event.detail.slideNumber == 2) {
						// 积分
						document.getElementById("inner_data_3").innerHTML = "";
						pointList(0);
					};
				});
			});
		})(mui, document, jQuery);
		
		// 累计收益ajax下拉刷新事件
		function cumulativeIncomeLoadMore(obj) {
			cumulativeIncomeList(1, obj);
		};
		
		// 待入积分ajax下拉刷新事件
		function waitingPointLoadMore(obj) {
			waitingPointList(1, obj);
		};
		
		// 积分ajax下拉刷新事件
		function pointList(obj) {
			pointLoadMore(1, obj);
		};
		
		//获取累计收益信息
		function cumulativeIncomeList(flushtype, object){
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/cumulativeIncome.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum":start,
					"pageSize":limit
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					var html = "";
					for(var i = 0; i < data.list.length; i++) {
						html += '<li>'
						html += '	<span>' + data.list[i].changedTitile + '</span>'
						html += '	<span>' + data.list[i].changedIncome + '</span>'
						html += '	<span>' + timestampToTime(data.list[i].modifiedTimestamp) + '</span>'
						html += '<li>'
					};
					if(flushtype != 2) {
						setTimeout(function() {
							var htmls = document.createElement("item1_inner");
							htmls.innerHTML = html;
							for(var i = 0; i < htmls.childNodes.length; i++) {
								document.getElementById("inner_data_1").appendChild(htmls.childNodes[i]);
								htmls.innerHTML = html;
							};
							mui('#item1.mui-scroll-wrapper').pullRefresh().endPulldownToRefresh();
							mui('#item1.mui-scroll-wrapper').pullRefresh().refresh(true);
							if (data.pageNum < data.pages) {
								object.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
							} else {
								object.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
							}
						}, 200);
					} else {
						var htmls = document.createElement("ul");
						htmls.innerHTML = html;
						for(var i = 0; i < htmls.childNodes.length; i++) {
							document.getElementById("inner_data_1").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有交易信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});				
		}
		
		// 待入积分
		function waitingPointList(flushtype, object){
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/waitingPoint.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum": start,
					"pageSize": limit
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					var html = "";
					for(var i = 0; i < data.list.length; i++) {
						html += '<li>'
						html += '	<span>' + data.list[i].orderCode + '</span>'
						html += '	<span>' + data.list[i].onlineTitle + '</span>'
						html += '	<span>' + data.list[i].buyPointTotal + '</span>'
						html += '<li>'
					};
					if(flushtype != 2) {
						setTimeout(function() {
							var htmls = document.createElement("item2_inner");
							htmls.innerHTML = html;
							for(var i = 0; i < htmls.childNodes.length; i++) {
								document.getElementById("inner_data_2").appendChild(htmls.childNodes[i]);
								htmls.innerHTML = html;
							};
							mui('#item2.mui-scroll-wrapper').pullRefresh().endPulldownToRefresh();
							mui('#item2.mui-scroll-wrapper').pullRefresh().refresh(true);
							if (data.pageNum < data.pages) {
								object.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
							} else {
								object.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
							}
						}, 200);
					} else {
						var htmls = document.createElement("ul");
						htmls.innerHTML = html;
						for(var i = 0; i < htmls.childNodes.length; i++) {
							document.getElementById("inner_data_2").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有待入积分信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});				
		}
		
		// 积分列表
		function pointList(flushtype, object) {
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/point.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum": start,
					"pageSize": limit,
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					console.log(data)
					var html = "";
					for(var i = 0; i < data.list.length; i++) {
						html += '<li>'
						html += '	<span>' + data.list[i].changedTitile + '</span>'
						html += '	<span>' + data.list[i].changedPoint + '</span>'
						html += '	<span>' + timestampToTime(data.list[i].modifiedTimestamp) + '</span>'
						html += '<li>'
					};
					if(flushtype != 2) {
						setTimeout(function() {
							var htmls = document.createElement("item3_inner");
							htmls.innerHTML = html;
							for(var i = 0; i < htmls.childNodes.length; i++) {
								document.getElementById("inner_data_3").appendChild(htmls.childNodes[i]);
								htmls.innerHTML = html;
							};
							mui('#item3.mui-scroll-wrapper').pullRefresh().endPulldownToRefresh();
							mui('#item3.mui-scroll-wrapper').pullRefresh().refresh(true);
							if (data.pageNum < data.pages) {
								object.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
							} else {
								object.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
							}
						}, 200);
					} else {
						var htmls = document.createElement("ul");
						htmls.innerHTML = html;
						for(var i = 0; i < htmls.childNodes.length; i++) {
							document.getElementById("inner_data_3").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有积分交易信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});
		}
		
		//倒计时
		var systime = Math.round(new Date().getTime() / 1000); //当前时间戳，现在这里是电脑时间，需改成服务器时间（精确到秒，10位数字）
		function runtime() {
			systime++;
			var dateNum = mui("div.cd-box").length;
			for(var i = 0; i < dateNum; i++) {
				var buytime = mui("input.buytime")[i].value;
				var endtime = mui("input.endtime")[i].value;
				var ts = endtime * 1000 - systime * 1000; //计算剩余的毫秒数  
				var content;
				if(ts > 0) {
					var dd = parseInt(ts / 1000 / 60 / 60 / 24); //计算剩余的天数  
					var hh = parseInt(ts / 1000 / 60 / 60 % 24, 10); //计算剩余的小时数  
					var mm = parseInt(ts / 1000 / 60 % 60, 10); //计算剩余的分钟数  
					var ss = parseInt(ts / 1000 % 60, 10); //计算剩余的秒数   
					content = dd + "天" + hh + "时" + mm + "分" + ss + "秒";
					var label = "自动返款倒计时：";
					mui("div.cd-box p")[i].innerHTML = label + content;
				} else {
					mui("div.cd-box p")[i].innerHTML = "已返现";
				}

			}
		}
		setInterval("runtime()", 1000);

		//计算内容区最小高度
		function setconHeight() {
			var head = mui(".mui-bar.mui-bar-nav")[0].clientHeight;
			var nav = mui("#icon-grid-9")[0].clientHeight;
			var tab = document.getElementById("sliderSegmentedControl").clientHeight + 1;
			var doc = document.documentElement.clientHeight;
			var height = doc - head - nav - tab;
			var obj = document.querySelectorAll(".mui-slider-item");
			for(var i = 0; i < obj.length; i++) {
				obj[i].style.height = height + "px";
			};
		}
	</script>
</body>
</html>