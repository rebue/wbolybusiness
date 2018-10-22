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
		<h4 class="mui-title">我的钱包</h4>
		<h5 class="mui-title" style="left: auto; font-size: 15px;">
			<a id="withdraw">提现</a>
		</h5>
	</header>
	<div id="wallet_main">
		<div class="mui-content">
			<ul id="icon-grid-9" class="mui-table-view mui-grid-view mui-grid-9"></ul>

			<div class="mui-slider tab-slider">
				<div id="sliderSegmentedControl"
					class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
					<a class="mui-control-item mui-active" data-value="1" href="#item1">余额<br /><span class="money-show" id="balance">0.00</span></a> 
					<a class="mui-control-item" data-value="2" href="#item2">待全返<br /><span class="money-show" id="commissioning">0.00</span></a> 
					<a class="mui-control-item" data-value="3" href="#item3">返现金<br /><span class="money-show" id="cashback">0.00</span></a> 
					<a class="mui-control-item" data-value="4" href="#item4">待返现<br /><span class="money-show" id="balance">0.00</span></a> 
					<a class="mui-control-item" data-value="5" href="#item5">提现中 <br /><span class="money-show" id="withdrawing">0.00</span></a>
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
									<li>商品名称</li>
									<li>规格名称</li>
									<li>返现总额</li>
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

					<div id="item4"
						class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item4_inner" class="inner">
								<ul class="inner_tab">
									<li>商品名称</li>
									<li>规格名称</li>
									<li>返现总额</li>
								</ul>
								<ul class="inner_data" id="inner_data_4"></ul>
							</div>
						</div>
					</div>

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
					container: '#item1.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多的内容
			                pulldownRefresh(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
					container: '#item2.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多的内容
			                toBeFullyReturnedPulldownRefresh(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
					container: '#item3.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多的内容
			                cashbackPulldownRefresh(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
					container: '#item4.mui-scroll-wrapper',
					up: {
						callback : function() {
			                var self = this; // 这里的this == mui('#refreshContainer').pullRefresh()
			                // 加载更多的内容
			                ordinaryCashbackPulldownRefresh(this);
			            }, //必选，刷新函数，根据具体业务来编写，比如通过ajax从服务器获取新数据；
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				},{
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
				mui("#active-bar-span")[0].style.left = mui(".tab-slider .mui-control-item.mui-active")[0].offsetLeft + "px";
				accountTrade(0);
				setconHeight();
				getMoney();
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
				
				// 提现
				document.getElementById('withdraw').addEventListener('tap',function(){
					window.location.href="${ctx}/wechat/user/wechatWithdraw.htm";
				});
			});
		})(mui, document, jQuery);

		// 账号交易ajax下拉刷新事件
		function pulldownRefresh(obj) {
			accountTrade(1, obj);
		};
		
		// 待全返ajax下拉刷新事件
		function toBeFullyReturnedPulldownRefresh(obj) {
			toBeFullyReturned(1, obj);
		};
		
		// 返现金ajax下拉刷新事件
		function cashbackPulldownRefresh(obj) {
			accountCashback(1, obj);
		};
		
		// 普通待返现ajax下拉刷新事件
		function ordinaryCashbackPulldownRefresh(obj) {
			ordinaryCashback(1, obj);
		};
		
		// 提现中ajax下拉刷新事件
		function beBeingWithdrawPulldownRefresh(obj) {
			beBeingWithdraw(1, obj);
		}
		
		//获取账号交易信息
		function accountTrade(flushtype, object){
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/accountTrade.htm', {
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
						html += '<span>'+data.list[i].tradeTitle+'</span>'
						html += '<span>'+data.list[i].tradeAmount+'</span>'
						html += '<span>'+data.list[i].tradeTime+'</span>'
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
		
		// 待全返
		function toBeFullyReturned(flushtype, object) {
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/order/getCashBack.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum": start,
					"pageSize": limit,
					"subjectType": 1,
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					console.log(data)
					var html = "";
					for(var i = 0; i < data.message.list.length; i++) {
						html += '<li>'
						html += '	<span>'+data.message.list[i].onlineTitle+'</span>'
						html += '	<span>'+data.message.list[i].specName+'</span>'
						html += '	<span>'+data.message.list[i].cashbackTotal+'</span>'
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
							document.getElementById("inner_data_2").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有待全反信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});
		}
		
		// ajax上滑加载数据  账号交易信息（返现金）
		function accountCashback(flushtype, object){
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/cashbackTrade.htm', {
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
						html += '	<span>'+data.list[i].tradeTitle+'</span>'
						html += '	<span>'+data.list[i].tradeAmount+'</span>'
						html += '	<span>'+data.list[i].tradeTime+'</span>'
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
						mui.toast("没有反现信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});				
		}
		
		// 普通返现
		function ordinaryCashback(flushtype, object) {
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/order/getCashBack.htm', {
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"pageNum": start,
					"pageSize": limit,
					"subjectType": 0,
				},
				dataType: 'json',
				type: 'post',
				success: function(data) {
					console.log(data)
					var html = "";
					for(var i = 0; i < data.message.list.length; i++) {
						html += '<li>'
						html += '	<span>' + data.message.list[i].onlineTitle + '</span>'
						html += '	<span>' + data.message.list[i].specName + '</span>'
						html += '	<span>' + data.message.list[i].cashbackTotal + '</span>'
						html += '<li>'
					};
					if(flushtype != 2) {
						setTimeout(function() {
							var htmls = document.createElement("item4_inner");
							htmls.innerHTML = html;
							for(var i = 0; i < htmls.childNodes.length; i++) {
								document.getElementById("inner_data_4").appendChild(htmls.childNodes[i]);
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
							document.getElementById("inner_data_4").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.list.length < 1){
						mui.toast("没有待反现信息");
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});
		}
		
		// ajax上滑加载数据  提现中
		function beBeingWithdraw(flushtype, object) {
			limit = 10;
			if(flushtype == 0) {
				start = 1;
			} else {
				start += 1;
			}
			mui.ajax('${ctx }/wechat/user/beBeingWithdraw.htm', {
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