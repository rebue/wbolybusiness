<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@page import="com.wboly.system.sys.util.wx.WXSignUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%  
	Map<String, String> ret = new HashMap<String, String>();  
	ret = WXSignUtils.createSign(request.getAttribute("JSURL").toString());  
%>
<jsp:include page="/WEB-INF/htm/wechat/global/global.jsp" />
<div id="allmap"></div>
<div id="refreshContainer" class="mui-content mui-scroll-wrapper">
	<div class="mui-scroll">
		<div class="mui-table-view mui-table-view-chevron">
			<!--轮播开始-->
			<div id="slider" class="swiper-container">
				<div class="swiper-wrapper"></div>
				<div class="swiper-pagination"></div>
			</div>
			<!--轮播结束-->

			<h5 class="mui-content-padded wboly-title-bar">
				每日热门
				<div class="wboly-bar-more">
					<a href="${ctx }/wechat/goods/allGoodsList.htm">查询更多</a> 
					<span class="mui-icon mui-icon-arrowright"></span>
				</div>
			</h5>
			<!--商品列表开始-->
			<div id="index-goods-list"
				class="mui-table-view mui-grid-view mui-grid-9">
				<div class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4"></div>
			</div>
			<!--商品列表结束-->

			<h5 class="mui-text-center bottom-more">
				<a href="${ctx }/wechat/goods/allGoodsList.htm">点击查看更多商品 <span class="mui-icon mui-icon-arrowright"></span></a>
			</h5>
		</div>
	</div>
</div>

	<!--
		作者：364733037@qq.com
		时间：2017-01-03
		描述：原引用JS位置
	-->
	<script src="${ctx }/js/wechat/city.data.js"></script>
	<script type="text/javascript" src="${ctx }/js/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=GY7epABzpfsPo2d2Pm6SVBWIstGtHtyo"></script>
	<script type="text/javascript" charset="utf-8">
		var mySwiper;
		var mySwiper2;
		var ide = 1;
		var idess = 0;
				
		function pulldownRefresh() {
			setTimeout(function() {
				reloadData();
				mui('#refreshContainer').pullRefresh().endPulldownToRefresh(); //refresh completed		
			}, 500);
		};
				
		(function($, doc) {
			//mui初始化
			mui.init({
				//预加载
				 preloadPages:[
					
			 	 ],
				//下拉刷新、上拉加载
				pullRefresh: {
					container: '#refreshContainer',
					down: {
						callback: pulldownRefresh
					}
				}
			});
			
			setShopData(1, "安吉华尔街工谷店");
			
			wx.config({
	               debug : false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	               appId : '<%=ret.get("appid")%>', // 必填，公众号的唯一标识
	               timestamp : '<%=ret.get("timestamp")%>', // 必填，生成签名的时间戳
	               nonceStr : '<%=ret.get("nonceStr")%>', // 必填，生成签名的随机串
	               signature : '<%=ret.get("signature")%>',// 必填，签名，见附录1
	               jsApiList : [ 'checkJsApi','scanQRCode' ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	           })
			
			$.ready(function() {
				LoadCartNum();
				mui('body').on('tap','a',function(){document.location.href=this.href;});
				mui(".mui-tab-item-wboly")[0].classList.add('mui-active');
				
				mySwiper = new Swiper ('#slider.swiper-container', {
				    loop: true,			    
				    autoplay : 4000,
				    speed:1000,
				    autoplayDisableOnInteraction : false,
				    pagination: '.swiper-pagination',	    		
				});
				
				mySwiper2 = new Swiper ('#message-slider.swiper-container', {
					direction:'vertical',
				    loop: true,			    
				    autoplay : 4000,
				    speed:1000,
				    effect : "flip",
				    autoplayDisableOnInteraction : false,	    		
				});   					  
				  
				//普通示例				
				// var a_width = mui("#wboly-scoll-message img")[0].clientWidth+1;
				///var b_width = mui("#wboly-scoll-message span")[0].clientWidth+13;
				//var e_width = mui("#wboly-scoll-message")[0].clientWidth-a_width-b_width-20-5;
				//document.getElementsByClassName("wboly-scoll-message-zone")[0].style.width=e_width+"px";
				
				var list_img_width =mui("#index-goods-list .mui-table-view-cell")[0].clientWidth;
				var slider_img_width=mui("#slider")[0].clientWidth;
				
				for(var i=0;i<mui("#index-goods-list .mui-table-view-cell img").length;i++){
					mui("#index-goods-list .mui-table-view-cell .imghref")[i].style.width = "100%";
					mui("#index-goods-list .mui-table-view-cell .imghref")[i].style.height = list_img_width+"px";
				};
				
				for(var i=0;i<mui("#slider .swiper-slide").length;i++){
					mui("#slider .swiper-slide a")[i].style.width = "100%";
				};
				
				// 添加到购物车
				mui("#index-goods-list").on('tap','#cart',function(){
				  // 商品规格编号
				  var specId = this.getAttribute("data-specId");
				  // 上线编号
				  var onlineId = this.getAttribute("data-onlineId");
				  mui.ajax('${ctx}/wechat/cart/listAddCart.htm',{
						data:{
							key:'59c23bdde5603ef993cf03fe64e448f1',
							specId : specId, cartCount : 1, onlineId : onlineId
						},
						dataType:'json',//服务器返回json格式数据
						type:'post',//HTTP请求类型
						success:function(data){
							if(data.flag){//先判断是否登录
								//写个ajax事件,添加成功后返回购物车里的商品数量
								//var count = document.getElementById("cartnum").innerText;count ++;//这句是模拟的，不用要																													
								//mui.alert("加入购物车成功！");
								mui.toast("加入购物车成功");
								mui('#selectBox').popover('hide');
								document.getElementById("cartnum").innerText = data.cartCount;
							}else{
								if("您没有登录" == data.message){
									mui.confirm(data.message+',请先登录哦', ' ', ['等等吧', '说走就走'], function(e) {
										if (e.index == 1) {
											window.location.href="${ctx }/wechat/user/logInPage.htm";//去到登录页面
										}
									}); 
									return ;
								}
								mui.toast(data.message);
							};
						},
						error:function(xhr,type,errorThrown){
							//异常处理；
							console.log(type);
						}
					});
				});
				
				//暂时应付input无法失去焦点的bug
				document.addEventListener('tap', function(e) {
					var target = e.target;
					var inputs = document.querySelectorAll("input");
					if (!(target.tagName && (target.tagName === 'TEXTAREA' || (target.tagName === 'INPUT' && (target.type === 'text' || target.type === 'search' || target.type === 'number'))))) {
						for(var i=0;i<inputs.length;i++){
							if(inputs[i].type=="search"||inputs[i].type=="text"||inputs[i].type=="number"){
								inputs[i].blur();
							}
						}
					}
				});
			});
		})(mui, document);	
				
		/**
		* 保存门店数据
		*/ 
		function setShopData(shopId, shopName){
			mui.ajax('${ctx}/wechat/shop/saveInSeesion.htm',{
				data:{
					shopId:shopId,
					shopName : shopName,
					key:'59c23bdde5603ef993cf03fe64e448f1'
				},
				dataType:'json',//服务器返回json格式数据
				type:'post',//HTTP请求类型
				success:function(data){
					reloadData();
				},
				error:function(xhr,type,errorThrown){
					//异常处理；
					console.log(type+":"+errorThrown);
				}
			});
		}
				
		/**
		* 加载首页信息
		*/ 
		function reloadData(){
			mui.ajax('${ctx}/wechat/index/getAllIndexData.htm',{
				data : {
					key : '59c23bdde5603ef993cf03fe64e448f1'
				},
				dataType:'json',// 服务器返回json格式数据
				type:'post',// HTTP请求类型
				success:function(data){
					html="";
					// 首页商品信息
					document.getElementById("index-goods-list").innerHTML=html;
					for (var i = 0; i < data.length; i++) {
						var strs = new Array();
						// 根据逗号获取图片后缀
						strs = data[i].picPath.split(".");
						html += '<div class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">';
						html += '	<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data[i].onlineId + '" class="imghref"><img src="${goodsImgUrl}' + data[i].picPath + '">' + '</a>';
						html += '	<h5><a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=' + data[i].onlineId + '">' + data[i].onlineTitle + '</a></h5>';
						html += '	<p>';
						html +=	'		<span class="price">￥<em>' + formatCurrency(data[i].salePrice) + '</em></span>';
						html +=	' 		<span class="back-money">返 <em>' + formatCurrency(data[i].cashbackAmount) + '</em></span>';
						html +=	' 		<span class="mui-icon-extra mui-icon-extra-cart" id="cart" data-specId="' + data[i].specId + '" data-onlineId="' + data[i].onlineId + '"></span>';
						html +=	'	</p>';
						html +=	'</div>';
					}
					document.getElementById("index-goods-list").innerHTML=html;
					
					var list_img_width =mui("#index-goods-list .mui-table-view-cell")[0].clientWidth;
					for(var i=0;i<mui("#index-goods-list .mui-table-view-cell img").length;i++){
						mui("#index-goods-list .mui-table-view-cell .imghref")[i].style.width = "100%";
						mui("#index-goods-list .mui-table-view-cell .imghref")[i].style.height = list_img_width+"px";
					};
					
					// 返回 top
					mui("#refreshContainer.mui-scroll-wrapper").pullRefresh().scrollTo(0,0);
					mui('#refreshContainer').pullRefresh().refresh(true);
					// end
				},
				error:function(xhr,type,errorThrown){
					//异常处理；
					console.log(type+":"+errorThrown);
					mui.toast("链接超时");
				}
			});
		}
	</script>
</body>
</html>