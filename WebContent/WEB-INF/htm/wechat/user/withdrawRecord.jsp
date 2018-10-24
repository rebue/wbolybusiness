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
<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
<link rel="stylesheet" type="text/css"
	href="${ctx }/css/wechat/usercenter.css" />
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/wechat/jquery.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>
<script src="${ctx }/js/util/base64.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">提现记录</h4>
	</header>
	<div id="wallet_main">
		<div class="mui-content">
			<ul id="icon-grid-9" class="mui-table-view mui-grid-view mui-grid-9"></ul>

			<div class="mui-slider tab-slider">
				<div id="sliderSegmentedControl"
					class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
					<div class="mui-slider-group">
					<div id="item5"
						class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item5_inner" class="inner">
								<ul class="inner_tab">
									<li>实际到账金额</li>
									<li>提现服务费</li>
									<li>申请时间</li>
								</ul>
								<ul class="inner_data" id="inner_data_5"></ul>
							</div>
						</div>
					</div>
				</div>
				</div>
				<div class="active-bar">
					<span id="active-bar-span"></span>
				</div>
				
			</div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8">
		var userId = 12345;
		var state = 2;
		var pageNum = 1;
		var pageSize = 10;
		var $$=jQuery.noConflict();
		var b = new Base64();
		
		(function($, doc, $$) {
			mui.init({
				pullRefresh: [{
					container: '#item5.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多的内容
			                beBeingWithdrawPulldownRefresh(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				}]
			});
			
			$.ready(function() {
				mui('#wallet_main').on('tap','.car-inner-body a',function(){document.location.href=this.href;});
				mui('#wallet_main').on('tap','.mui-table-view-cell a',function(){document.location.href=this.href;});
				// mui('body').on('tap','.mui-bar-nav a',function(){document.location.href=this.href;});
				//mui("#active-bar-span")[0].style.left = mui(".tab-slider .mui-control-item.mui-active")[0].offsetLeft + "px";
				beBeingWithdraw(0);
				setconHeight();
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
						document.getElementById("inner_data_1").innerHTML = "";
						accountTrade(0);
					} else if(event.detail.slideNumber == 1) {
						document.getElementById("inner_data_2").innerHTML = "";
						toBeFullyReturned(0);
					} else if(event.detail.slideNumber == 2) {
						document.getElementById("inner_data_3").innerHTML = "";
						accountCashback(0);
					} else if(event.detail.slideNumber == 3) {
						document.getElementById("inner_data_4").innerHTML = "";
						ordinaryCashback(0);
					} else if (event.detail.slideNumber == 4) {
						document.getElementById("inner_data_5").innerHTML = "";
						beBeingWithdraw(0);
					};
				});
			});
		})(mui, document, jQuery);

		// 提现中ajax下拉刷新事件
		function beBeingWithdrawPulldownRefresh(obj) {
			beBeingWithdraw(1, obj);
		}
		
		// ajax上滑加载数据  提现
		function beBeingWithdraw(flushtype, object) {
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/getWithdrawRecord.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum": start,
					"pageSize": limit
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					console.log(data)
					var html = "";
					for(var i = 0; i < data.list.length; i++) {
						html += '<li>'
						html += '	<span>'+data.list[i].realAmount+'</span>'
						html += '	<span>'+data.list[i].seviceCharge+'</span>'
						html += '	<span>'+data.list[i].applyTime+'</span>'
						html += '<li>'
					};
					if(flushtype != 2) {
						setTimeout(function() {
							var htmls = document.createElement("item5_inner");
							htmls.innerHTML = html;
							for(var i = 0; i < htmls.childNodes.length; i++) {
								document.getElementById("inner_data_5").appendChild(htmls.childNodes[i]);
								htmls.innerHTML = html;
							};
							mui('#item5.mui-scroll-wrapper').pullRefresh().endPulldownToRefresh();
							mui('#item5.mui-scroll-wrapper').pullRefresh().refresh(true);
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
							document.getElementById("inner_data_5").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有提现中信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});
		}

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