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
<script src="${ctx }/js/util/commonUtil.js"></script>
<script src="${ctx }/js/util/base64.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav" style="box-shadow: none;">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">我的钱包</h4>
	</header>
	<div id="wallet_main">
		<div class="mui-content">
			<ul id="icon-grid-9" class="mui-table-view mui-grid-view mui-grid-9">
				<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
					<a href="#"> 
						<span class="money-show" id="yue">0.00</span>
						<div class="mui-media-body">余额</div>
					</a>
				</li>
				<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
					<a href="#"> 
						<span class="money-show" id="fanxain">0.00</span>
						<div class="mui-media-body">返现金</div>
					</a>
				</li>
				<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
					<a href="#"> 
						<span class="money-show" id="waitFanxian">0.00</span>
						<div class="mui-media-body">待返现金</div>
					</a>
				</li>
				<!--<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">
					<a href="#"> <span class="money-show" id="shouyi">0.00</span>
						<div class="mui-media-body">收益</div>
				</a>
				</li>-->
			</ul>

			<div class="mui-slider tab-slider">
				<div id="sliderSegmentedControl" class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
					<a class="mui-control-item mui-active" data-value="3" href="#item1"></a> 
					<a class="mui-control-item" data-value="1" href="#item2">
						<!--推广收益 </a> <a class="mui-control-item" data-value="2" href="#item3">
						推广关系 </a>-->
				</div>
				<div class="active-bar">
					<span id="active-bar-span"></span>
				</div>
				<div class="mui-slider-group">
					<div id="item1" class="mui-slider-item mui-control-content mui-scroll-wrapper mui-active">
						<div class="mui-scroll">
							<div id="item1_inner" class="inner"></div>
						</div>
					</div>

					<div id="item2" class="mui-slider-item mui-control-content mui-scroll-wrapper">
						<div class="mui-scroll">
							<div id="item2_inner" class="inner">
								<ul class="inner_tab">
									<li>好友昵称</li>
									<li>收益时间</li>
									<li>获得总收益</li>
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
									<li>好友昵称</li>
									<li>注册时间</li>
									<li>是否有效</li>
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
		
		var b = new Base64();
		
		(function($, doc) {
			mui.init({
				pullRefresh: {
					container: '#item1.mui-scroll-wrapper',
					down: {
						callback: pulldownRefresh
					},
					up: {
						callback: AjaxAppendData,
						contentnomore: '<div class="fade">已到底部,不能再拉了</div>'
					},
				}
			});
			
			$.ready(function() {
				mui('#wallet_main').on('tap','.car-inner-body a',function(){document.location.href=this.href;});
				mui('#wallet_main').on('tap','.mui-table-view-cell a',function(){document.location.href=this.href;});
				mui("#active-bar-span")[0].style.left = mui(".tab-slider .mui-control-item.mui-active")[0].offsetLeft + "px";
				AjaxGetData(0);
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
					} else if(event.detail.slideNumber == 1) {
						AjaxGetData_2();
					} else if(event.detail.slideNumber == 2) {
						AjaxGetData_3();
					};
				});
			});
		})(mui, document);

		//ajax下拉刷新事件
		function pulldownRefresh() {
			AjaxGetData(1);
		};
		
		//ajax上滑添加事件（滑动到最底部时插入新数据，如果已全部加载完，则会有相应提示）
		function AjaxAppendData() {
			var obj = this;
			pageNum += 1;
			mui.ajax('${ctx }/wechat/order/getCashBackOrders.htm', { 
				data: {
					"orderState":state,//排序方式标识
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"pageNum":pageNum,
					"pageSize":pageSize
				},
				dataType: 'json', //测试用，正式应为JSON
				type: 'post',
				success: function(data) {
					console.log(data);
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					var html = "";
					for(var i=0;i<data.message.length;i++){
						localStorage.setItem(b.encode(data.message[i].orderCode), JSON.stringify(data.message[i]));
						html += '<div class="order-list-unit" data-statu="' + data.message[i].orderState + '">'
						html += '<div class="shoptop mui-table-view-cell">'
						html += '<a href="${ctx}/wechat/myorder/'+b.encode(data.message[i].orderCode)+'.htm" class="mui-navigate-right">'
						var totalBacLimit = 0;
						for(var j = 0; j < data.message[i].items.length; j++) {
							totalBacLimit += data.message[i].items[j].cashbackTotal;
						} 
						html += '<span class="mui-pull-left spt">订单号：' + data.message[i].orderCode + '</span>'
						html += '<span class="mui-pull-right odt">待返现：<span>¥ ' + formatCurrency(totalBacLimit) + '</span></span>'
						html += '</a>'
						html += '</div>'
						html += '<div class="mui-input-group">'
						var totalnum = 0;
						for(j = 0; j < data.message[i].items.length; j++) {
							var goodsQsmm = data.message[i].items[j].goodsQsmm;
							var strs = new Array();
							// 根据逗号获取图片后缀
							strs = goodsQsmm.split(".");
							html += '<div class="mui-input-row mui-left">'
							html += '<input type="hidden" name="goodsId" value="' + data.message[i].items[j].onlineId + '">'
							html += '<div class="car-inner-box">'
							html += '<div class="car-inner-box-img">';
							html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">';
							html += '		<img src="${goodsImgUrl}' + goodsQsmm + '_187_187.' + strs[1] + '" alt="" class="goodspic">';
							html += '	</a>';
							html += '</div>';
							html += '<div class="car-inner-body">'
							html += '<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">'+ data.message[i].items[j].onlineTitle +'</a></h5>'
							html += '<p>规格:' + data.message[i].items[j].specName + '</p><br/>'
							html += '<div class="price-area">'
							html += '<span class="m-price">¥<span>' + formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>'
							html += '<span class="b-money"> 返 <span> '+ formatCurrency(data.message[i].items[j].cashbackAmount) +'</span></span>' //*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
							html += '<span class="numbox">数量：<span>' + data.message[i].items[j].buyCount + '</span></span>'
							html += '</div>'
							html += '</div>'
							html += '</div></div>'
								totalnum += parseInt(data.message[i].items[j].retailBacLimit*data.message[i].items[j].num);
						};
						html += '</div>'
						html += '<div class="opt-box">'
						html += '<div class="cd-box" data-type="1">'
						html += '<input type="hidden" value="' + data.message[i].dateline + '" class="buytime">' //dateline计划返现时间
						html += '<input type="hidden" value="' + parseInt(parseInt(data.message[i].dateline)) + '" class="endtime">' //
						html += '<p><span class="grey">获取订单状态中...</span></p>'
						html += '</div>'
						html += '</div>'
						html += '</div>';
					};
				
					var htmls = document.createElement("div");
					htmls.innerHTML = html;
					for(var i = 0; i < htmls.childNodes.length; i++) {
						document.getElementById("item1_inner").appendChild(htmls.childNodes[i]);
						htmls.innerHTML = html;
					};
					
					if(data.message.length>0){
						obj.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
					}else{
						obj.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
				},
				error: function(xhr, type, errorThrown) {
					console.log(type);
				}
			});
		};
		//常规ajax载入
		function AjaxGetData(flushtype, object) {
			var obj = this;
			if(flushtype == 0) {
				document.getElementById("item1_inner").innerHTML = "<a class='loading'><span class='mui-spinner'></span></a>";
			}
			pageNum = 1;
			pageSize = 10;
			mui.ajax('${ctx }/wechat/order/getCashBackOrders.htm', { 
				data: {
					"orderState":state,//排序方式标识
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"pageNum":pageNum,
					"pageSize":pageSize
				},
				dataType: 'json', //测试用，正式应为JSON
				type: 'post',
				success: function(data) {
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					var html = "";
					for(var i=0;i<data.message.length;i++){
							localStorage.setItem(b.encode(data.message[i].orderCode), JSON.stringify(data.message[i]));
							html += '<div class="order-list-unit" data-statu="' + data.message[i].orderState + '">'
							html += '<div class="shoptop mui-table-view-cell">'
							html += '<a href="${ctx}/wechat/myorder/'+b.encode(data.message[i].orderCode)+'.htm?promoterId=${userId}" class="mui-navigate-right">'
							var totalBacLimit = 0;
							for(var j = 0; j < data.message[i].items.length; j++) {
								totalBacLimit += data.message[i].items[j].cashbackTotal;
							} 
							html += '<span class="mui-pull-left spt">订单号：' + data.message[i].orderCode + '</span>'
							html += '<span class="mui-pull-right odt">待返现：<span>¥ ' + formatCurrency(totalBacLimit) + '</span></span>'
							html += '</a>'
							html += '</div>'
							html += '<div class="mui-input-group">'
							var totalnum = 0;
							for(j = 0; j < data.message[i].items.length; j++) {
								var goodsQsmm = data.message[i].items[j].goodsQsmm;
								var strs = new Array();
								// 根据逗号获取图片后缀
								strs = goodsQsmm.split(".");
								html += '<div class="mui-input-row mui-left">'
								html += '<input type="hidden" name="goodsId" value="' + data.message[i].items[j].onlineId + '">'
								html += '<div class="car-inner-box">'
								html += '<div class="car-inner-box-img">';
								html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">';
								html += '		<img src="${goodsImgUrl}' + goodsQsmm + '_187_187.' + strs[1] + '" alt="" class="goodspic">';
								html += '	</a>';
								html += '</div>';
								html += '<div class="car-inner-body">'
								html += '<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">'+ data.message[i].items[j].onlineTitle +'</a></h5>'
								html += '<p>规格:' + data.message[i].items[j].specName + '</p><br/>'
								html += '<div class="price-area">'
								html += '<span class="m-price">¥<span>' + formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>'
								html += '<span class="b-money"> 返 <span> '+ formatCurrency(data.message[i].items[j].cashbackAmount) +'</span></span>' //*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
								html += '<span class="numbox">数量：<span>' + data.message[i].items[j].buyCount + '</span></span>'
								html += '</div>'
								html += '</div>'
								html += '</div></div>'
									totalnum += parseInt(data.message[i].items[j].retailBacLimit*data.message[i].items[j].num);
							};
							html += '</div>'
							html += '<div class="opt-box">'
							html += '<div class="cd-box" data-type="1">'
							html += '<input type="hidden" value="' + data.message[i].dateline + '" class="buytime">' //dateline返现任务执行时间
							html += '<input type="hidden" value="' + parseInt(parseInt(data.message[i].dateline)) + '" class="endtime">' //付款时间加三天（三天返款）
							html += '<p><span class="grey">获取订单状态中...</span></p>'
							html += '</div>'
							html += '</div>'
							html += '</div>';
					};
					if(flushtype != 2) {
						setTimeout(function() {
							document.getElementById("item1_inner").innerHTML = html;
							mui('#item1.mui-scroll-wrapper').pullRefresh().endPulldownToRefresh();
							mui('#item1.mui-scroll-wrapper').pullRefresh().refresh(true);
						}, 200);
					} else {
						var htmls = document.createElement("div");
						htmls.innerHTML = html;
						for(var i = 0; i < htmls.childNodes.length; i++) {
							document.getElementById("item1_inner").appendChild(htmls.childNodes[i]);
							htmls.innerHTML = html;
						};
						object.endPullupToRefresh(true); //已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
					if(data.message.length<1){
						mui.toast("没有待反现信息");
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