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
<script src="${ctx }/js/util/base64.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a id="backPage" class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">我的订单</h4>
	</header>
	<div class="mui-slider tab-slider">
		<div id="sliderSegmentedControl"
			class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
			<a class="mui-control-item ${allOrder }" data-value="0"> 全部订单 </a> 
			<a class="mui-control-item ${stayPay }" data-value="1"> 待付款 </a> 
			<a class="mui-control-item ${stayTake }" data-value="3"> 待收货 </a> 
			<a class="mui-control-item ${stayReturn }" data-value="4"> 待返款 </a>
		</div>
		<div class="active-bar">
			<span class="active-bar-span" id="active-bar-span"></span>
		</div>
	</div>
	<div id="orderlist_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div id="order-list-area"></div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8"> 
		var state = ${OrderType};//订单排序类型
		var limit=10;
		var start=0;
		var b = new Base64();
		(function($, doc) {				
			mui.init({
				pullRefresh: {
					container: '#orderlist_main',
					down: {
						callback: pulldownRefresh
					},
					up: {
						callback: AjaxAppendData
					}
				}
			});
			$.ready(function() {
				mui('#orderlist_main').on('tap','a',function(){document.location.href=this.href;});				
				mui("#active-bar-span")[0].style.left = mui(".tab-slider .mui-control-item.mui-active")[0].offsetLeft+"px";
				AjaxGetData(0);
				
				mui("#backPage")[0].addEventListener("tap",function(e){
					localStorage.clear();
				});

				//标签选中状态
				mui(document).on('tap','.tab-slider .mui-control-item',function(e){
					var left = this.offsetLeft;
					state = this.getAttribute("data-value");
					mui("#active-bar-span")[0].style.left = left+"px";
					AjaxGetData(0);
				});										
			});
		})(mui, document);		
	
		//ajax下拉刷新事件
		function pulldownRefresh() {
			AjaxGetData(1);
		};
		
		//ajax上滑添加事件（滑动到最底部时插入新数据，如果已全部加载完，则会有相应提示）
		function AjaxAppendData(){
			var obj = this;
			start += 10;
			limit += 10;
			mui.ajax('${ctx}/wechat/order/getOrders.htm',{ //测试用,模拟数据
				data:{
					"orderState" : state,
					"key" : "59c23bdde5603ef993cf03fe64e448f1",
					"limit" : limit,
					"start" : start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					console.log(data);
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					
					//在订单状态等于4的时候，将已经得免单的详情去掉，如果订单里面只有一个详情的话，连订单也去掉。
					//先去掉订单中所有详情都已经免单的订单
					var tempData =new Object();
					tempData.message=[];
					console.log("状态--------："+state);
					if(state==4){
						//去掉详情已经是全返的订单
						for(var i = 0; i < data.message.length; i++){
							if (data.message[i] != null && data.message[i] != "" && data.message[i].items.length > 0) {
								var end=true;
								var itemsTemp=[];
								for (j = 0; j < data.message[i].items.length; j++) {
									if(data.message[i].items[j].cashbackCommissionState !=2){
										itemsTemp.push(data.message[i].items[j]);
										end=false
									}

								}
								console.log(itemsTemp);
								data.message[i].items=itemsTemp;
								
								if(end==false){
									tempData.message.push(data.message[i]);
								}
							}

						}
						data=tempData;

					}

					var html = "";
					for(var i = 0; i < data.message.length; i++){
						if (data.message[i] != null && data.message[i] != "" && data.message[i].items.length > 0) {
							systime=data.message[i].system;// 服务器时间
							html += '<div class="order-list-unit" data-statu="' + data.message[i].orderState + '">'
							html += '<div class="shoptop mui-table-view-cell">'
							localStorage.setItem(b.encode(data.message[i].orderCode), JSON.stringify(data.message[i]));
							html += '	<a href="${ctx}/wechat/myorder/'+b.encode(data.message[i].orderCode)+'.htm?promoterId=${userId}" class="mui-navigate-right">'	
							if (data.message[i].orderState == 1) {
								html += '	<span class="status">待支付</span>';
							} else if (data.message[i].orderState == 2) {
								html += '	<span class="orderState">待发货</span>';
							} else if (data.message[i].orderState == 3) {
								html += '	<span class="orderState">待签收</span>';
							} else if (data.message[i].orderState == 4) {
								html += '	<span class="orderState">已签收</span>';
							} else if (data.message[i].orderState == 5) {
								html += '	<span class="orderState">已签收</span>';
							} else if (data.message[i].orderState == "") {
								html += '	<span class="status">全部订单</span>';
							} else if (data.message[i].orderState == -1) {
								html += '	<span style="color:#8f8f94;border:solid 1px #8f8f94;" class="status">已取消</span>';
							};
							html+='			<span class="mui-pull-left spt">订单号：' + data.message[i].orderCode + '</span>';
							html+='		</a>';
							html+='</div>';
							html+='<div class="mui-input-group">';
							var retailBacLimit = 0; // 总返现
							var totalCount = 0; // 总数量
							for (j = 0; j < data.message[i].items.length; j++) {
								// 购买数量
								var buyCount = data.message[i].items[j].buyCount;
								// 退货数量
								var returnCount = data.message[i].items[j].returnCount;
								returnCount = returnCount == null ? 0 : returnCount;
								// 真实购买数量
								var realBuyCount = parseInt(buyCount) - parseInt(returnCount);
								
								var goodsQsmm = data.message[i].items[j].goodsQsmm;
								var strs = new Array();
								// 根据逗号获取图片后缀
								strs = goodsQsmm.split(".");
								html += '<div class="mui-input-row mui-left">';
								html += '	<input type="hidden" name="goodsId" value="' + data.message[i].items[j].goodsId + '">';
								html += '	<div class="car-inner-box">';
								html += '<div class="car-inner-box-img">';
								if(data.message[i].items[j].subjectType==1){
									html += '	<a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm"  class="full-return">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}else{
									html += '	<a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm"  class=\"\">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}
								html += '		</div>';
								html += '		<div class="car-inner-body">';
								html += '			<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId="' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '>' + data.message[i].items[j].onlineTitle +'</a></h5>';
								html += '			<p>规格：'+ data.message[i].items[j].specName+'</p><br/>';
								html += '			<div class="price-area">';
								if(data.message[i].items[j].subjectType==1){
									if(data.message[i].orderState == -1){
										html += '		<span style="color:#8f8f94;" class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}else{
										html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}
									html += '			<span class="b-money"><span>'  + '</span></span>';
									html += '			<span class="b-money"> 返 <span>' + formatCurrency(data.message[i].items[j].cashbackAmount) + '</span></span>';
									if(data.message[i].items[j].ordBuyRelation.length != 0){
										var relation = '';
										for(m = 0;m<data.message[i].items[j].ordBuyRelation.length;m++){
											relation += '下家：'+data.message[i].items[j].ordBuyRelation[m].downlineUserNickName
											if(!data.message[i].items[j].ordBuyRelation[m].isSignIn){
												relation += ' 签收状态：未签收 \\n'
											}else{
												relation += ' 签收状态：已签收 \\n'
											}
										}
										html += '		<a href="javascript:showRelation(\''+relation+'\')" style= "padding-right:10px">';
										for(k=0;k<data.message[i].items[j].ordBuyRelation.length;k++){
											html += '		<span class="numbox"><img  style="height:2em;line-height:2em; border:solid 1px #bbb;border-radius:20px; margin-bottom:-0.5em;" src="'+data.message[i].items[j].ordBuyRelation[k].downlineUserWxFace +'"></span>';
										}
										html += '		</a>';
									}
								}else{
									if(data.message[i].orderState == -1){
										html += '		<span style="color:#8f8f94;" class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}else{
										html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}
									html += '			<span class="b-money"> 返 <span>' + formatCurrency(data.message[i].items[j].cashbackAmount) + '</span></span>';//*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
									html += '			<span class="numbox">数量：<span>' + buyCount + '</span></span>';
								}
								html += '			</div>';
								html += '		</div>';
								html += '	<div class="button-box">';
								if(data.message[i].items[j].returnState == 1){
									if(data.message[i].orderState==2){
										html+='	<button class="mui-btn locked">退款中</button>'
									}else if(data.message[i].orderState==3){
										html+='	<button class="mui-btn locked">退款中</button>'
									}else if(data.message[i].orderState==4){
										html+='	<button class="mui-btn locked">退货/售后中</button>'
									}
								}else if(data.message[i].items[j].returnState ==2){
									html += '	<button class="mui-btn locked">已退货</button>';
								}else if(data.message[i].items[j].returnState == 0  || data.message[i].items[j].returnState == 3){
									if(data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 2){
										html+='	<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn" style = "margin-top:63px">邀请2人免单</a>';
									}else if (data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 1){
										html+='	<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn" style = "margin-top:63px">邀请1人免单</a>';
									}else if(data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 0){
										if(data.message[i].items[j].cashbackCommissionState==2){
											html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn locked" style = "margin-top:63px">已免单</a>';
										}else{
											html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn locked" style = "margin-top:63px">满足免单条件</a>';
										}
									}
								};
								html += '		</div>';
								html += '	</div>';
								html += '</div>';
								retailBacLimit += parseFloat(data.message[i].items[j].cashbackTotal); 
								totalCount += parseInt(data.message[i].items[j].buyCount);
							};	
							html += '</div>';
							html += '<div class="unit-compute">';
							if(data.message[i].orderState == -1){
								html += '	<span style="color:#8f8f94;" class="mui-pull-right unit-price">合计：¥ ' + formatCurrency(data.message[i].orderMoney) + '</span>';

							}else{
								html += '	<span class="mui-pull-right unit-price">合计：¥ ' + formatCurrency(data.message[i].orderMoney) + '</span>';

							}
							if(retailBacLimit !=0){
								html += '	<span class="mui-pull-right unit-back">返现：¥ '+ formatCurrency(retailBacLimit) + '</span>'		
							}
							html += '		<span class="mui-pull-right unit-num">共' + totalCount + '件</span>';
							html += '</div>';
							html += '<div class="opt-box">';
							if (data.message[i].orderState == 1) {
								// 下单时间戳
								var orderTimes = data.message[i].orderTimes;
								// 取消订单时间
								var automaticCancel = parseInt(orderTimes) + 86400;
								html += '<div class="cd-box" data-type="0">'
								html += '	<input type="hidden" value="' + orderTimes + '" class="buytime">'; //订单生成时间戳
								html += '	<input type="hidden" value="'+ automaticCancel + '" class="endtime">'; //订单生成24小时后
								html += '	<p><span class="grey">获取订单状态中...</span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-left mui-btn bg" href="javascript:payPage(\'' + data.message[i].orderId + '\')">立即付款</a>';
								html += '	<a class="mui-pull-right mui-btn bg" href="javascript:giveupOrder(\'' + data.message[i].orderId + '\',1)">取消订单</a>';
								html += '</div>';
							} else if (data.message[i].orderState == 3){
								// 发货时间戳
								var sendTimes = data.message[i].sendTimes;
								// 自动签收时间
								var automaticSignIn = parseInt(sendTimes) + 86400 * 10;
								html += '<div class="cd-box" data-type="1">';
								html += '	<input type="hidden" value="' + sendTimes + '" class="buytime">'; // 发货时间戳
								html += '	<input type="hidden" value="' + automaticSignIn +'" class="endtime">'; // 发货时间加十天（十天签收）
								html += '	<p><span class="grey">获取订单状态中...</span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-left mui-btn bg" href="javascript:queryLogistics(\'' + data.message[i].id + '\')">查看物流</a>';
								html += '	<a class="mui-pull-right mui-btn bg" href="javascript:aboutCinfirmReceipt(\'' + data.message[i].id + '\')">确认收货</a>';
								html += '</div>';
							} else if (data.message[i].orderState == 4) {
								// 签收时间戳
								var receivedTimes = data.message[i].receivedTimes;
								// 自动结算时间
								var automaticSettlement = parseInt(receivedTimes) + 86400 * 7;
								html += '<div class="cd-box" data-type="2">';
								html += '	<input type="hidden" value="' + receivedTimes + '" class="buytime">'; // 签收时间戳
								html += '	<input type="hidden" value="' + automaticSettlement + '" class="endtime">'; // 签收时间加七天（七天返款）
								html += '	<p><span class="grey"></span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-right mui-btn bg"> 已签收</a>';
								html += '</div>';
							};
							html += '	</div>'
							html += '</div>';
						};
					};
					
					 //上拉添加
					var htmls = document.createElement("div");
					htmls.innerHTML= html;
					for(var i=0;i<htmls.childNodes.length;i++){
						document.getElementById("order-list-area").appendChild(htmls.childNodes[i]);
						htmls.innerHTML= html;
					};
					if(data.message.length>0){
						obj.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
					}else{
						obj.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断	
					}
				},
				error:function(xhr,type,errorThrown){
					console.log(type);
				}
			});		
		};

		//倒计时
		var systime = Math.round(new Date().getTime()/1000); //当前时间戳，现在这里是电脑时间，需改成服务器时间（精确到秒，10位数字）
		function runtime() {
			systime++;
			var dateNum = mui("div.cd-box").length;
			for (var i = 0; i < dateNum; i++) {
				var buytime = mui("input.buytime")[i].value;
				var endtime = mui("input.endtime")[i].value;
				var ts = parseInt(parseInt(endtime) * parseInt(1000) - parseInt(systime) * parseInt(1000)); //计算剩余的毫秒数  
				var content;
				if (ts > 0) {
					var dd = parseInt(ts / 1000 / 60 / 60 / 24);//计算剩余的天数  
					var hh = parseInt(ts / 1000 / 60 / 60 % 24, 10);//计算剩余的小时数  
					var mm = parseInt(ts / 1000 / 60 % 60, 10);//计算剩余的分钟数  
					var ss = parseInt(ts / 1000 % 60, 10);//计算剩余的秒数   
					content = dd + "天" + hh + "时" + mm + "分" + ss + "秒";
					if(mui("div.cd-box")[i].getAttribute("data-type")=="1"){
						var label = "签收倒计时：";					
					} else if(mui("div.cd-box")[i].getAttribute("data-type")=="2") {
						var label = "";
					}else{
						var label = "剩余支付时间：";	
					};
					if(mui("div.cd-box")[i].getAttribute("data-type")!="2"){
						mui("div.cd-box p")[i].innerHTML= label+content;
					}
				} else {
					if(mui("div.cd-box")[i].getAttribute("data-type") == "1"){
						mui("div.cd-box p")[i].innerHTML="已签收";  
					} else if (mui("div.cd-box")[i].getAttribute("data-type") == "2") {
						mui("div.cd-box p")[i].innerHTML="";  
					}else{
						mui("div.cd-box p")[i].innerHTML="<span>订单超时未付款</span>"; 
						mui("div.unit-pay")[i].innerHTML='<button class="mui-pull-right mui-btn">已关闭</button>'; 
					};						
				}
			}
		}
		setInterval("runtime()", 1000); 

		//常规ajax载入
		function AjaxGetData(flushtype, object){
			if(flushtype == 0){
				document.getElementById("order-list-area").innerHTML="<a class='loading'><span class='mui-spinner'></span></a>";
			};
			limit=10;
			start=0;
			mui.ajax('${ctx}/wechat/order/getOrders.htm',{ 
				data:{
					"orderState":state,//排序方式标识
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"limit":limit,
					"start":start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					console.log(data);
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					//在订单状态等于4的时候，将已经得免单的详情去掉，如果订单里面只有一个详情的话，连订单也去掉。
					//先去掉订单中所有详情都已经免单的订单
					var tempData =new Object();
					tempData.message=[];
					console.log("状态--------："+state);
					if(state==4){
						//去掉详情已经是全返的订单
						for(var i = 0; i < data.message.length; i++){
							if (data.message[i] != null && data.message[i] != "" && data.message[i].items.length > 0) {
								var end=true;
								var itemsTemp=[];
								for (j = 0; j < data.message[i].items.length; j++) {
									if(data.message[i].items[j].cashbackCommissionState !=2){
										itemsTemp.push(data.message[i].items[j]);
										end=false
									}

								}
								console.log(itemsTemp);
								data.message[i].items=itemsTemp;
								
								if(end==false){
									tempData.message.push(data.message[i]);
								}
							}

						}
						data=tempData;

					}

					

					var html = "";
					for(var i = 0; i < data.message.length; i++){
						if (data.message[i] != null && data.message[i] != "" && data.message[i].items.length > 0) {
							systime = data.message[i].system;//服务器时间
							html += '<div class="order-list-unit" data-statu="' + data.message[i].orderState + '">';
							html += '<div class="shoptop mui-table-view-cell">';
							localStorage.setItem(data.message[i].orderCode, JSON.stringify(data.message[i]));
							html += '<a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm" class="mui-navigate-right">'						
							if (data.message[i].orderState == 1) {
								html += '<span class="status">待支付</span>';
							}else if (data.message[i].orderState == 2) {
								html += '<span class="status">待发货</span>';
							}else if (data.message[i].orderState == 3) {
								html += '<span class="status">待签收</span>';
							}else if (data.message[i].orderState == 4) {
								html += '<span class="status">已签收</span>';
							}else if (data.message[i].orderState == 5) {
								html += '<span class="status">已签收</span>';
							}else if (data.message[i].orderState == -1) {
								html += '<span style="color:#8f8f94;border:solid 1px #8f8f94;"    class="status">已取消</span>';
							};
							html += '	 <span class="mui-pull-left spt">订单号：' + data.message[i].orderCode + '</span>';
							html += ' </a>';
							html += '</div>';
							html += '<div class="mui-input-group">';
							var retailBacLimit = 0; // 总返现
							var totalCount = 0; // 总数量
							for (j = 0; j < data.message[i].items.length; j++) {
								// 购买数量
								var buyCount = data.message[i].items[j].buyCount;
								// 退货数量
								var returnCount = data.message[i].items[j].returnCount;
								returnCount = returnCount == null ? 0 : returnCount;
								// 真实购买数量
								var realBuyCount = parseInt(buyCount) - parseInt(returnCount);
								var goodsQsmm = data.message[i].items[j].goodsQsmm;
								var strs = new Array();
								// 根据逗号获取图片后缀
								strs = goodsQsmm.split(".");
								html += '<div class="mui-input-row mui-left">';
								html += '	<input type="hidden" name="goodsId" value="' + data.message[i].items[j].onlineId + '">';
								html += '	<div class="car-inner-box">';
								html += '		<div class="car-inner-box-img">';
								if(data.message[i].items[j].subjectType==1){
									html += '		<a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm"  class="full-return">';
									html += '			<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '		</a>';
								}else{
									html += '		<a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm"  class=\"\">';
									html += '			<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '		</a>';
								}
								html += '		</div>';
								html += '	<div class="car-inner-body">';
								html += '		<h5><a href="${ctx}/wechat/myorder/' + data.message[i].orderCode + '.htm" >'+ data.message[i].items[j].onlineTitle +'</a></h5>';
								html += '		<p>规格：' + data.message[i].items[j].specName 	+ '</p>';
								if (data.message[i].items[j].subjectType == 1) {
									html += '		<p>积分：' + data.message[i].items[j].buyPoint;
									if (data.message[i].items[j].paySeq == 1) {
										html += '		&nbsp;&nbsp;&nbsp;首单积分：' + data.message[i].items[j].buyPointTotal;
									}
									html += '		</p><br/>';
								}
								html += '		<div class="price-area">';
								if(data.message[i].items[j].subjectType==1){
									if(data.message[i].orderState == -1){
										html += '	<span style="color:#8f8f94;" class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}else{
										html += '	<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}
									html += '		<span class="numbox">数量：<span>' + buyCount + '</span></span>';
									html += '		<span class="b-money"><span>'  + '</span></span>';//*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
										if(data.message[i].items[j].ordBuyRelation.length != 0){
											var relation = '';
											for(m = 0;m<data.message[i].items[j].ordBuyRelation.length;m++){
												relation += '下家：'+data.message[i].items[j].ordBuyRelation[m].downlineUserNickName
												if(!data.message[i].items[j].ordBuyRelation[m].isSignIn){
													relation += ' 签收状态：未签收 \\n'
												}else{
													relation += ' 签收状态：已签收 \\n'
												}
											}
											html += '<a href="javascript:showRelation(\''+relation+'\')" style= "padding-right:10px">';
											for(k=0;k<data.message[i].items[j].ordBuyRelation.length;k++){
												html += '<span class="numbox"><img style="height:2em;line-height:2em; border:solid 1px #bbb;border-radius:20px; margin-bottom:-0.5em;" src="'+data.message[i].items[j].ordBuyRelation[k].downlineUserWxFace +'"></span>';
											}
											html += '</a>';
										}
								}else{
									if(data.message[i].orderState == -1){
										html += '	<span style="color:#8f8f94;" class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}else{
										html += '	<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';

									}
									html += '		<span class="b-money"> 返 <span>' + formatCurrency(data.message[i].items[j].cashbackAmount) + '</span></span>';//*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
									html += '		<span class="numbox">数量：<span>' + buyCount + '</span></span>';
								}
								html += '		</div>';
								html += '	</div>';
								html += '	<div class="button-box">';
								if(data.message[i].items[j].returnState == 1){
									if(data.message[i].orderState==2){
										html+='<button class="mui-btn locked">退款中</button>'
									}else if(data.message[i].orderState==3){
										html+='<button class="mui-btn locked">退款中</button>'
									}else if(data.message[i].orderState==4){
										html+='<button class="mui-btn locked">退货/售后中</button>'
									}
								}else if(data.message[i].items[j].returnState ==2){
									html += '  <button class="mui-btn locked">已退货</button>';
								}else if(data.message[i].items[j].returnState == 0  || data.message[i].items[j].returnState == 3){
									if(data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 2){
										html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn" style = "margin-top:63px">邀请2人免单</a>';
									}else if (data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 1){
										html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn" style = "margin-top:63px">邀请1人免单</a>';
									}else if(data.message[i].items[j].subjectType==1&&data.message[i].orderState > 1&&data.message[i].items[j].cashbackCommissionSlot == 0){
										if(data.message[i].items[j].cashbackCommissionState==2){
											html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn locked" style = "margin-top:63px">已免单</a>';
										}else{
											html+='<a href="${ctx}/wechat/goods/goodsDetail.htm?guideDisplay=none&onlineId='+  data.message[i].items[j].onlineId+ '&promoterId=${userId}'+ '"  class="mui-btn locked" style = "margin-top:63px">满足免单条件</a>';
										}
									}
									
								};
								html += '	</div>';
								html+='	</div>'
								html+='</div>'	
								retailBacLimit += parseFloat(data.message[i].items[j].cashbackTotal); 
								totalCount += parseInt(data.message[i].items[j].buyCount);
							};	
							html += '</div>'
							html += '<div class="unit-compute">'
							if(data.message[i].orderState == -1){
								html += '	<span style="color:#8f8f94;" class="mui-pull-right unit-price">合计：¥ ' + formatCurrency(data.message[i].orderMoney) + '</span>';

							}else{
								html += '	<span class="mui-pull-right unit-price">合计：¥ ' + formatCurrency(data.message[i].orderMoney) + '</span>';

							}
							if(retailBacLimit !=0){
								html += '	<span class="mui-pull-right unit-back">返现：¥ '+ formatCurrency(retailBacLimit) + '</span>'		
							}
							html += '	<span class="mui-pull-right unit-num">共' + totalCount + '件</span>'
							html += '</div>'
							html += '<div class="opt-box">'
							if (data.message[i].orderState == 1) {
								// 下单时间戳
								var orderTimes = data.message[i].orderTimes;
								// 取消订单时间
								var automaticCancel = parseInt(orderTimes) + 86400;
								html += '<div class="cd-box" data-type="0">'
								html += '	<input type="hidden" value="' + orderTimes + '" class="buytime">'; //订单生成时间戳
								html += '	<input type="hidden" value="'+ automaticCancel + '" class="endtime">'; //订单生成24小时后
								html += '	<p><span class="grey">获取订单状态中...</span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-left mui-btn bg" href="javascript:payPage(\'' + data.message[i].id + '\')">立即付款</a>';
								html += '	<a class="mui-pull-right mui-btn bg" href="javascript:giveupOrder(\'' + data.message[i].id + '\',1)">取消订单</a>';
								html += '</div>';
							} else if (data.message[i].orderState == 3){
								// 发货时间戳
								var sendTimes = data.message[i].sendTimes;
								// 自动签收时间
								var automaticSignIn = parseInt(sendTimes) + 86400 * 10;
								html += '<div class="cd-box" data-type="1">';
								html += '	<input type="hidden" value="' + sendTimes + '" class="buytime">'; // 发货时间戳
								html += '	<input type="hidden" value="' + automaticSignIn +'" class="endtime">'; // 发货时间加十天（十天签收）
								html += '	<p><span class="grey">获取订单状态中...</span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-left mui-btn bg" href="javascript:queryLogistics(\'' + data.message[i].id + '\')">查看物流</a>';
								html += '	<a class="mui-pull-right mui-btn bg" href="javascript:aboutCinfirmReceipt(\'' + data.message[i].id + '\')">确认收货</a>';
								html += '</div>';
							} else if (data.message[i].orderState == 4) {
								// 签收时间戳
								var receivedTimes = data.message[i].receivedTimes;
								// 自动结算时间
								var automaticSettlement = parseInt(receivedTimes) + 86400 * 7;
								html += '<div class="cd-box" data-type="2">';
								html += '	<input type="hidden" value="' + receivedTimes + '" class="buytime">'; // 签收时间戳
								html += '	<input type="hidden" value="' + automaticSettlement + '" class="endtime">'; // 签收时间加七天（七天返款）
								html += '	<p><span class="grey"></span></p>';
								html += '</div>';
								html += '<div class="unit-pay">';
								html += '	<a class="mui-pull-right mui-btn bg"> 已签收</a>';
								html += '</div>';
							};
							html += '</div>'
							html += '</div>';
						};
					};
					
					if(flushtype != 2){ //常规加载和下拉刷新
						setTimeout(function(){
							document.getElementById("order-list-area").innerHTML= html;
							if(flushtype= 1){
								mui('#orderlist_main').pullRefresh().endPulldownToRefresh();
							};
							mui("#orderlist_main.mui-scroll-wrapper").pullRefresh().scrollTo(0,0);
							mui('#orderlist_main').pullRefresh().refresh(true);
						},200);
						if(data.message.length<1){
							mui.toast("没有数据哦");
							return ;
						}
					}else{ //上拉添加
						var htmls = document.createElement("div");
						console.log(html);
						htmls.innerHTML= html;
						for(var i=0;i<htmls.childNodes.length;i++){
							document.getElementById("order-list-area").appendChild(htmls.childNodes[i]);
							htmls.innerHTML= html;
						};
						object.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
					};
				},
				error:function(xhr,type,errorThrown){
					console.log(type);
				}
			});
		}
		
		// 放弃订单
		function giveupOrder(orderId, cancelType){
			console.log(orderId);
			if(orderId == null || cancelType == null){
				return ;
			}
			mui.confirm("确定放弃吗?"," ",['放弃','留着吧'],function(e){
				if (e.index == 1) {
					return ;
				}else{
					loading(1);
					mui.ajax('${ctx}/wechat/order/cancelOrder.htm',{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1',
							orderId : orderId,
							cancelType : cancelType
						},
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						success:function(data){
							loading(2);
							if(data.flag){
								AjaxGetData(0);// 订单取消成功之后重新加载
								mui.toast(data.message);
								return ;
							}
							mui.toast(data.message);
						},
						error:function(xhr,type,errorThrown){
							//异常处理；
							console.log(type);
							loading(2);
						}
					});
				};
			});
		}
		
		// 确认收货
		function aboutCinfirmReceipt(orderId) {
			console.log(orderId);
			if(orderId == null || orderId == ""){
				return ;
			}
			mui.confirm("确定要收货吗?"," ",['确定','取消'],function(e){
				if (e.index == 0) {
					loading(1);
					mui.ajax('${ctx}/wechat/order/aboutCinfirmReceipt.htm',{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1',
							orderId : orderId
						},
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						success:function(data){
							loading(2);
							if(data.flag){
								AjaxGetData(0);// 订单取消成功之后重新加载
								mui.toast(data.message);
								return ;
							}
							mui.toast(data.message);
						},
						error:function(xhr,type,errorThrown){
							//异常处理；
							console.log(type);
							loading(2);
						}
					});
				}
			});
		}
		
		// 查看物流
		function queryLogistics(orderId) {
			if (orderId == null) {
				mui.toast("没有该商品信息");
				return;
			}
			console.log(orderId);
			//根据订单号获取订单物流信息
			mui.ajax('${ctx}/wechat/order/getOrderLogisticInfo.htm',{
				data:{
					key:'59c23bdde5603ef993cf03fe64e448f1',
					orderId : orderId
				},
				dataType:'json',//服务器返回json格式数据
				type:'get',//HTTP请求类型
				success:function(data){
					if(data.flag){
						console.log(data);
						var shipperName = data.message.shipperName;
						var logisticCode = data.message.logisticCode;
						var id = data.message.id;
						window.location.href = "https://www.duamai.com/damai-wx-web/kdi/KdiTrace.html?shipperName="+shipperName+'&logisticId='+id+'&logisticCode='+logisticCode;
					}
				},
				error:function(xhr,type,errorThrown){
					mui.toast("查询订单轨迹出错");
					//异常处理；
					console.log(type);
				}
			});
		}
		
		//显示购买关系
		function showRelation(relation) {
			mui.alert(relation);
		}
	</script>
</body>
</html>
