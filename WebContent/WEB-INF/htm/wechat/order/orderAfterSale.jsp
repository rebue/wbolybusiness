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
		<h4 class="mui-title">售后</h4>
	</header>
	<div class="mui-slider tab-slider">
		<div id="sliderSegmentedControl"
			class="order-tab-box mui-slider-indicator mui-segmented-control mui-segmented-control-inverted">
			<a class="mui-control-item mui-active" data-value="100"> 退货中 </a> 
			<a class="mui-control-item " data-value="101"> 退货完成 </a>
			<!--  
			<a class="mui-control-item " data-value="102"> 售后中 </a> 
			<a class="mui-control-item " data-value="103"> 售后完成 </a>
			--> 
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
		var state = 100;//订单排序类型
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
			
			mui.ajax('${ctx}/wechat/order/getReturnOrders.htm',{ //测试用,模拟数据
				data:{
					"state":state,
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"limit":limit,
					"start":start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					var html = "";
					for(var i=0;i<data.message.length;i++){
						if (data.message[i]!=null && data.message[i] != "" && data.message[i].items.length>0) {
							systime=data.message[i].system;//服务器时间
							html += '<div class="order-list-unit" data-statu="'+data.message[i].state+'">'
							html += '<div class="shoptop mui-table-view-cell">'
							localStorage.setItem(b.encode(data.message[i].orderId), JSON.stringify(data.message[i]));
							html +='<a href="${ctx}/wechat/myorder/'+b.encode(data.message[i].orderId)+'.htm?promoterId=${userId}" class="mui-navigate-right">'						
						
							html += '<span class="mui-pull-left spt">订单号：'+ data.message[i].orderId+'</span>'		
							html += '</a>'
							html += '</div>'
							html += '<div class="mui-input-group">'
							var retailBacLimit = 0; // 总返现
							for (j = 0; j < data.message[i].items.length; j++) {
								var goodsQsmm = data.message[i].items[j].goodsQsmm;
								var strs = new Array();
								// 根据逗号获取图片后缀
								strs = goodsQsmm.split(".");
								html += '<div class="mui-input-row mui-left">'
								html += '	<input type="hidden" name="goodsId" value="'+data.message[i].items[j].onlineId +'">'
								html += '	<div class="car-inner-box">'
								html += '		<div class="car-inner-box-img">'
								if(data.message[i].items[j].subjectType==1){
									html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '" class="full-return">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}else{
									html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}
								html += '		</div>'
								html += '	<div class="car-inner-body">'
								html += '<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">'+ data.message[i].items[j].onlineTitle +'</a></h5>'
								html += '	<p>规格：'+ data.message[i].items[j].specName+'</p><br/>'
								html += '	<div class="price-area">'
								if(data.message[i].items[j].subjectType==1){
									html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';
									html += '		<span class="b-money"><span>'  + '</span></span>';
								}else{
									html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';
									html += '		<span class="b-money"> 返 <span>' + formatCurrency(data.message[i].items[j].cashbackAmount) + '</span></span>';//*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
									html += '		<span class="numbox">数量：<span>' + buyCount + '</span></span>';
								}
								html += '	</div>'
								html += '</div>'									
								html += '<div class="button-box">'
								//returnState   标记退货状态
								//appraiseState 标记评价状态
								//supplierUid   和返现状态有关，具体得问小邓
								if(data.message[i].applicationState==-1){
									html += '<button class="mui-btn locked">已取消</button>'
								}else if(data.message[i].applicationState==1){
									html += '<button class="mui-btn locked">待审核</button>'
								}else if(data.message[i].applicationState==2){
									html += '<button class="mui-btn locked">退货中</button>'
								}else if(data.message[i].applicationState==3){
									html += '<button class="mui-btn locked">已退货</button>'
								}else if(data.message[i].applicationState==4){
									html += '<button class="mui-btn locked">已拒绝</button>'
								}else if(data.message[i].items[j].returnState ==0){
									var unitJson = '{"shopId":"'+data.message[i].items[j].shopid+'","supplierUid":"'+data.message[i].items[j].supplierUid+'","orderId":"'+data.message[i].orderId+'","ruleNum":"'+data.message[i].items[j].ruleNum+'","attrVal":"'+data.message[i].items[j].attrval+'","activityId":"'+data.message[i].items[j].activityId+'","img":"http://img.wboly.com/goods/'+data.message[i].items[j].faceimg+'.jpg","goodsId":"'+data.message[i].items[j].goodsId+'","price":"'+formatCurrency(data.message[i].items[j].retailPrice/100)+'","back":"'+formatCurrency(data.message[i].items[j].retailBacLimit/100)+'","number":"'+data.message[i].items[j].num+'","goodsName":"'+data.message[i].items[j].goodsTitle+'"}';
									if (data.message[i].state == 3 || data.message[i].state == 4){
										if(data.message[i].items[j].appraiseState == 0){
											html += '<a href="javascript:commentPage(\''+b.encode(unitJson)+'\')" class="mui-btn">待评价</a>'
										}else if(data.message[i].items[j].appraiseState == 1){
											html += '<a href="javascript:commentPage(\''+b.encode(unitJson)+'\')" class="mui-btn">追加评价</a>'
										}else if(data.message[i].items[j].appraiseState == 2){
											html += '<button class="mui-btn locked">评价结束</button>'
										};
									};
									if (data.message[i].state == 3){
										if(data.message[i].items[j].backState == 0){
											html += '<a class="mui-btn">申请返现</a>';
										};
										if(data.message[i].items[j].returnState == 0){
											html += '<a href="javascript:returnPage(\''+b.encode(unitJson)+'\')"  class="mui-btn">退货</a>'
										};
									};
									if(data.message[i].state == 4){
										if(data.message[i].items[j].aftersaleState == 0){
											html += '<a href="javascript:afterSalePage(\''+b.encode(unitJson)+'\')" class="mui-btn">申请售后</a>'
										};
									};
									if(data.message[i].state == 7){
										if(data.message[i].items[j].aftersaleState == 1){
											html += '<button class="mui-btn locked">正在售后</button>'
										};
										if(data.message[i].items[j].aftersaleState == 2){
											html += '<button class="mui-btn locked">售后结束</button>'
										};
									};
								};
								html += '</div>'	
								html += '</div></div>'	
								retailBacLimit += parseInt(data.message[i].items[j].retailBacLimit*data.message[i].items[j].num); 
							};	
							html += '</div>'
							html += '<div class="unit-compute">'
							html += '	<span class="mui-pull-right unit-price">合计：¥ '+ formatCurrency(data.message[i].returnRental) +'</span>'
// 							html += '	<span class="mui-pull-right unit-back">返现：¥ '+formatCurrency(retailBacLimit/100)+'</span>'								
// 							html += '	<span class="mui-pull-right unit-num">共'+data.message[i].num+'件</span>'
							html += '</div>'
							html += '<div class="opt-box">'
							
							html += '</div>'
							html += '</div>';
						};
					};
					
					//上拉添加
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

		//倒计时
		var systime = Math.round(new Date().getTime()/1000); //当前时间戳，现在这里是电脑时间，需改成服务器时间（精确到秒，10位数字）
		function runtime() {
			systime++;
			var dateNum = mui("div.cd-box").length;
			for (var i = 0; i < dateNum; i++) {
				var buytime = mui("input.buytime")[i].value;
				var endtime = mui("input.endtime")[i].value;
				var ts = endtime * 1000 - systime * 1000; //计算剩余的毫秒数  
				var content;
				if (ts > 0) {
					var dd = parseInt(ts / 1000 / 60 / 60 / 24);//计算剩余的天数  
					var hh = parseInt(ts / 1000 / 60 / 60 % 24, 10);//计算剩余的小时数  
					var mm = parseInt(ts / 1000 / 60 % 60, 10);//计算剩余的分钟数  
					var ss = parseInt(ts / 1000 % 60, 10);//计算剩余的秒数   
					content = dd + "天" + hh + "时" + mm + "分" + ss + "秒";
					if(mui("div.cd-box")[i].getAttribute("data-type")=="1"){
						var label = "返款倒计时：";							
					}else{
						var label = "剩余支付时间：";	
					};
					mui("div.cd-box p")[i].innerHTML= label+content;
				} else {
					if(mui("div.cd-box")[i].getAttribute("data-type")=="1"){
						mui("div.cd-box p")[i].innerHTML="已返现";  
					}else{
						mui("div.cd-box p")[i].innerHTML="<span>订单超时未付款</span>"; 
						mui("div.unit-pay")[i].innerHTML='<button class="mui-pull-right mui-btn">已关闭</button>'; 
					};						
				}
			}
		}
		setInterval("runtime()", 1000); 

		//常规ajax载入
		function AjaxGetData(flushtype,object){
			if(flushtype == 0){
				document.getElementById("order-list-area").innerHTML="<a class='loading'><span class='mui-spinner'></span></a>";
			};
			limit=10;
			start=0;
			
			mui.ajax('${ctx}/wechat/order/getReturnOrders.htm',{ 
				data:{
					"state":state,//排序方式标识
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"limit":limit,
					"start":start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					if(!data.flag){
						mui.toast(data.message);
						return ;
					}
					console.log(data);
					var html = "";
					for(var i=0;i<data.message.length;i++){
						if (data.message[i]!=null && data.message[i] != "" && data.message[i].items.length>0) {
							systime=data.message[i].system;//服务器时间
							html += '<div class="order-list-unit" data-statu="'+data.message[i].state+'">'
							html += '<div class="shoptop mui-table-view-cell">'
							localStorage.setItem(b.encode(data.message[i].orderId), JSON.stringify(data.message[i]));
							html +='<a href="${ctx}/wechat/myorder/'+b.encode(data.message[i].orderId)+'.htm?promoterId=${userId}" class="mui-navigate-right">'						
						
							html += '<span class="mui-pull-left spt">订单号：'+ data.message[i].orderId+'</span>'		
							html += '</a>'
							html += '</div>'
							html += '<div class="mui-input-group">'
							var retailBacLimit = 0; // 总返现
							for (j = 0; j < data.message[i].items.length; j++) {
								var goodsQsmm = data.message[i].items[j].goodsQsmm;
								var strs = new Array();
								// 根据逗号获取图片后缀
								strs = goodsQsmm.split(".");
								html += '<div class="mui-input-row mui-left">'
								html += '	<input type="hidden" name="goodsId" value="'+data.message[i].items[j].onlineId +'">'
								html += '	<div class="car-inner-box">'
								html += '		<div class="car-inner-box-img">'
								if(data.message[i].items[j].subjectType==1){
									html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '" class="full-return">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}else{
									html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">';
									html += '		<img src="${goodsImgUrl}' + goodsQsmm + '" alt="" class="goodspic">';
									html += '	</a>';
								}
								html += '		</div>'
								html += '	<div class="car-inner-body">'
								html += '<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data.message[i].items[j].onlineId + '&promoterId=${userId}' + '">'+ data.message[i].items[j].onlineTitle +'</a></h5>'
								html += '	<p>规格：'+ data.message[i].items[j].specName+'</p><br/>'
								html += '	<div class="price-area">'
								if(data.message[i].items[j].subjectType==1){
									html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';
									html += '		<span class="b-money"><span>'  + '</span></span>';
								}else{
									html += '		<span class="m-price">¥ <span>'+ formatCurrency(data.message[i].items[j].buyPrice) + '</span></span>';
									html += '		<span class="b-money"> 返 <span>' + formatCurrency(data.message[i].items[j].cashbackAmount) + '</span></span>';//*注：测试数据是复制PC版的，里面没有【单个商品返现金额】数据，你直接传入就好
									html += '		<span class="numbox">数量：<span>' + buyCount + '</span></span>';
								}
								html += '	</div>'
								html += '</div>'									
								html += '<div class="button-box">'
								//returnState   标记退货状态
								//appraiseState 标记评价状态
								//supplierUid   和返现状态有关，具体得问小邓
								if(data.message[i].applicationState==-1){
									html += '<button class="mui-btn locked">已取消</button>'
								}else if(data.message[i].applicationState==1){
									html += '<button class="mui-btn locked">待审核</button>'
								}else if(data.message[i].applicationState==2){
									html += '<button class="mui-btn locked">退货中</button>'
								}else if(data.message[i].applicationState==3){
									html += '<button class="mui-btn locked">已退货</button>'
								}else if(data.message[i].applicationState==4){
									html += '<button class="mui-btn locked">已拒绝</button>'
								}else if(data.message[i].items[j].returnState ==0){
									var unitJson = '{"shopId":"'+data.message[i].items[j].shopid+'","supplierUid":"'+data.message[i].items[j].supplierUid+'","orderId":"'+data.message[i].orderId+'","ruleNum":"'+data.message[i].items[j].ruleNum+'","attrVal":"'+data.message[i].items[j].attrval+'","activityId":"'+data.message[i].items[j].activityId+'","img":"http://img.wboly.com/goods/'+data.message[i].items[j].faceimg+'.jpg","goodsId":"'+data.message[i].items[j].goodsId+'","price":"'+formatCurrency(data.message[i].items[j].retailPrice/100)+'","back":"'+formatCurrency(data.message[i].items[j].retailBacLimit/100)+'","number":"'+data.message[i].items[j].num+'","goodsName":"'+data.message[i].items[j].goodsTitle+'"}';
									if (data.message[i].state == 3 || data.message[i].state == 4){
										if(data.message[i].items[j].appraiseState == 0){
											html += '<a href="javascript:commentPage(\''+b.encode(unitJson)+'\')" class="mui-btn">待评价</a>'
										}else if(data.message[i].items[j].appraiseState == 1){
											html += '<a href="javascript:commentPage(\''+b.encode(unitJson)+'\')" class="mui-btn">追加评价</a>'
										}else if(data.message[i].items[j].appraiseState == 2){
											html += '<button class="mui-btn locked">评价结束</button>'
										};
									};
									if (data.message[i].state == 3){
										if(data.message[i].items[j].backState == 0){
											html += '<a class="mui-btn">申请返现</a>';
										};
										if(data.message[i].items[j].returnState == 0){
											html += '<a href="javascript:returnPage(\''+b.encode(unitJson)+'\')"  class="mui-btn">退货</a>'
										};
									};
									if(data.message[i].state == 4){
										if(data.message[i].items[j].aftersaleState == 0){
											html += '<a href="javascript:afterSalePage(\''+b.encode(unitJson)+'\')" class="mui-btn">申请售后</a>'
										};
									};
									if(data.message[i].state == 7){
										if(data.message[i].items[j].aftersaleState == 1){
											html += '<button class="mui-btn locked">正在售后</button>'
										};
										if(data.message[i].items[j].aftersaleState == 2){
											html += '<button class="mui-btn locked">售后结束</button>'
										};
									};
								};
								html += '</div>'	
								html += '</div></div>'	
								retailBacLimit += parseInt(data.message[i].items[j].retailBacLimit*data.message[i].items[j].num); 
							};	
							html += '</div>'
							html += '<div class="unit-compute">'
							html += '	<span class="mui-pull-right unit-price">合计：¥ '+ formatCurrency(data.message[i].returnRental) +'</span>'
// 							html += '	<span class="mui-pull-right unit-back">返现：¥ '+formatCurrency(retailBacLimit/100)+'</span>'								
// 							html += '	<span class="mui-pull-right unit-num">共'+data.message[i].num+'件</span>'
							html += '</div>'
							html += '<div class="opt-box">'
							
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
		function giveupOrder(orderId,cancelType){
			if(orderId==null||cancelType==null){
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
							orderId:orderId,
							cancelType:cancelType
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
	</script>
</body>
</html>
