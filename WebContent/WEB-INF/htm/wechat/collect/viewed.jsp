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
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/orderlist.css" />
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">我的足迹</h4>
	</header>
	<div id="viewed_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div id="viewed_main_inner"></div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8"> 
		var limit=20;
		var start=0;
		(function($, doc) {				
			mui.init({
				pullRefresh: {
					container: '#viewed_main',
					down: {
						callback: pulldownRefresh
					},
					up: {
						callback: AjaxAppendData
					}
				}
			});
			
			$.ready(function() {
				mui('#viewed_main').on('tap','h5 a',function(){document.location.href=this.href;});									
				AjaxGetData(1);
				
				//单条操作按钮（删除）			
				$(document).on('tap', '.mui-slider-right .mui-btn', function(event) {
					var elem = this;
					var li = elem.parentNode.parentNode;
					var unit = li.parentNode;
					var id = li.querySelector("input[name='footprintId']").value;					
					if(elem.classList.contains("delete")){ //删除
						loading(1);
						mui.ajax('${ctx}/wechat/collect/deleteFootprint.htm',{
							data:{
								key:'59c23bdde5603ef993cf03fe64e448f1',
								ids:id
							},
							dataType:'json',//服务器返回json格式数据
							type:'post',//HTTP请求类型
							success:function(data){
								loading(2);
								if(data.flag){
									unit.parentNode.removeChild(unit);
									mui.toast(data.message);
									return ;
								}
								$.swipeoutClose(li);
								mui.toast(data.message);
							},
							error:function(xhr,type,errorThrown){
								loading(2);
								//异常处理；
								console.log(type);
							}
						});
					}							
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
			loading(1);
			limit+=20;
			start+=20;
			mui.ajax('${ctx}/wechat/collect/getfootprint.htm',{ //测试用,模拟数据
				data:{
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"type":1,
					"limit":limit,
					"start":start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					loading(2);
					var html = "";
					if(data.flag){
						mui.each(data.message,function(index,item){
							html+='<div class="order-list-unit sp fav">'
							html+='<div class="mui-input-group mui-table-view-cell">'
							// 左划删除
							html+='<input name="footprintId" type="hidden" value="'+item.Id+'">'
							html+='<div class="mui-slider-right mui-disabled">'
							html+='<a class="mui-btn mui-btn-red mui-icon mui-icon-trash delete"></a>'
							html+='</div>'
							
							html+='<div class="mui-input-row mui-left mui-slider-handle">'
							html+='<div class="car-inner-box">'
							html+='<div class="car-inner-box-img">'
							html+='<img src="http://img.wboly.com/goods/'+item.faceImg+'_187_187.jpg" alt="" class="goodspic">'
							html+='</div>'
							html+='<div class="car-inner-body">'
							html+='<h5><a href="${ctx}/wechat/goods/'+item.shopId+'/'+item.supplierUid+'/'+item.goodsId+'/0.htm">'+item.goodsTitle+'</a></h5>'
							html+='<div class="price-area">'
							html+='<span class="m-price">¥ <span>'+formatCurrency(item.retailPrice/100)+'</span></span>'
							html+='<span class="b-money">返 <span>'+formatCurrency(item.retailBacLimit/100)+'</span>元</span></div>'
							html+='<div class="view-info"><span>浏览次数：'+item.hits+'</span><span>'+getTime(item.lineDate)+'</span></div>'
							html+='</div></div></div></div></div>'
						});
						var htmls = document.createElement("div");
						htmls.innerHTML= html;
						for(var i=0;i<htmls.childNodes.length;i++){
							document.getElementById("viewed_main_inner").appendChild(htmls.childNodes[i]);
							htmls.innerHTML= html;
						};
						if(data.message==0){
							obj.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断		
						}else{
							obj.endPullupToRefresh(false);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
						}
						return ;
					}
					mui.toast(data.message);
				},
				error:function(xhr,type,errorThrown){
					loading(2);
					console.log(type);
				}
			});		
		};
		
		//常规ajax载入
		function AjaxGetData(flushtype,object){
			loading(1);
			limit=20;
			start=0;
			mui.ajax('${ctx}/wechat/collect/getfootprint.htm',{ //测试用,模拟数据
				data:{
					"key":"59c23bdde5603ef993cf03fe64e448f1",
					"type":1,
					"limit":limit,
					"start":start
				},
				dataType:'json',//测试用，正式应为JSON
				type:'post',       
				success:function(data){
					loading(2);
					if(data.message==0){
						mui.toast("您还未浏览任何商品哦");
						return ;
					}
					var html = "";
					if(data.flag){
						mui.each(data.message,function(index,item){
							html+='<div class="order-list-unit sp fav">'
							html+='<div class="mui-input-group mui-table-view-cell">'
							// 左划删除
							html+='<input name="footprintId" type="hidden" value="'+item.Id+'">'
							html+='<div class="mui-slider-right mui-disabled">'
							html+='<a class="mui-btn mui-btn-red mui-icon mui-icon-trash delete"></a>'
							html+='</div>'
							
							html+='<div class="mui-input-row mui-left mui-slider-handle">'
							html+='<div class="car-inner-box">'
							html+='<div class="car-inner-box-img">'
							html+='<img src="http://img.wboly.com/goods/'+item.faceImg+'_187_187.jpg" alt="" class="goodspic">'
							html+='</div>'
							html+='<div class="car-inner-body">'
							html+='<h5><a href="${ctx}/wechat/goods/'+item.shopId+'/'+item.supplierUid+'/'+item.goodsId+'/0.htm">'+item.goodsTitle+'</a></h5>'
							html+='<div class="price-area">'
							html+='<span class="m-price">¥ <span>'+formatCurrency(item.retailPrice/100)+'</span></span>'
							html+='<span class="b-money">返 <span>'+formatCurrency(item.retailBacLimit/100)+'</span>元</span></div>'
							html+='<div class="view-info"><span>浏览次数：'+item.hits+'</span><span>'+getTime(item.lineDate)+'</span></div>'
							html+='</div></div></div></div></div>'
						});
						if(flushtype != 2){ //常规加载和下拉刷新
							setTimeout(function(){
								document.getElementById("viewed_main_inner").innerHTML= html;
								if(flushtype= 1){
									mui('#viewed_main').pullRefresh().endPulldownToRefresh();
								};
	
								mui("#viewed_main.mui-scroll-wrapper").pullRefresh().scrollTo(0,0);
								mui('#viewed_main').pullRefresh().refresh(true);
							},200);
						}else{ //上拉添加
							var htmls = document.createElement("div");
							htmls.innerHTML= html;
							for(var i=0;i<htmls.childNodes.length;i++){
								document.getElementById("viewed_main_inner").appendChild(htmls.childNodes[i]);
								htmls.innerHTML= html;
							};
							object.endPullupToRefresh(true);//已加载全部数据则传入true，还有剩下的数据则传入false，加个判断							
						};
						return ;
					}
					mui.toast(data.message);
				},
				error:function(xhr,type,errorThrown){
					loading(2);
					console.log(type);
				}
			});
		}			
	</script>
</body>
</html>
