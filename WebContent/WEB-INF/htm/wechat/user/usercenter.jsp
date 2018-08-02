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
<link rel="stylesheet" href="${ctx }/css/wechat/usercenter.css">
<script src="${ctx }/js/wechat/mui.min.js"></script>
<script src="${ctx }/js/util/commonUtil.js"></script>

<script>

</script>

</head>

<body>
	<input type="hidden" id="verifyUserId" value="${centerData.userId}" / >
	<nav class="mui-bar mui-bar-tab">
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/index/indexInfo.htm"> 
			<span class="mui-icon mui-icon-home"></span> 
			<span class="mui-tab-label">首页</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/goods/allGoodsList.htm"> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-class"></span> 
			<span class="mui-tab-label">全部商品</span>
		</a>
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/cart/shoppingcart.htm"> 
			<span id="cartnum" class="mui-badge mui-badge-danger">0</span> 
			<span class="mui-icon mui-icon-extra mui-icon-extra-cart"></span> 
			<span class="mui-tab-label">购物车</span>
		</a> 
		<a class="mui-tab-item-wboly" href="${ctx }/wechat/user/userCenter.htm"> 
			<span class="mui-icon mui-icon-person"></span> 
			<span class="mui-tab-label">个人中心</span>
		</a>
	</nav>
	<div id="guideDiv" style="position: fixed; top: 0px; left: 0px; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.8); display: none; z-index: 99;">
		<img src="${ctx }/images/wechat/guide.png" alt="" style="width: 100%;" />
	</div>
	<div id="usercenter_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="user_top">
				<!-- <div class="msg_btn mui-icon mui-icon-chat"></div> -->
				<div class="user_show" id="user_show">
					<img src="${centerData.img }" alt="" class="user_avatar" />
					<h4 class="user_name">${centerData.userName }</h4>
				</div>
			</div>

			<div class="main-list">
				<ul class="mui-table-view">
					<li class="mui-table-view-cell">
						<a href="${ctx }/wechat/order/myOrders.htm" class="mui-navigate-right notshare"> 
							<span class="mui-icon-extra mui-icon-extra-prech mui-pull-left"></span>
							我的订单 <span class="right-text mui-pull-right">查看所有订单</span>
						</a>
					</li>
					<li class="mui-table-view-cell no-padding">
						<ul id="icon-grid-91" class="icon-grid mui-table-view mui-grid-view mui-grid-9">
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="${ctx }/wechat/order/myOrders.htm?type=stayPay"> 
									<span class="icon mui-icon-extra mui-icon-extra-card"></span>
									<div class="mui-media-body">待付款</div>
								</a>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="${ctx }/wechat/order/myOrders.htm?type=stayTake"> 
									<span class="icon mui-icon-extra mui-icon-extra-express"></span>
									<div class="mui-media-body">待收货</div>
								</a>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="${ctx }/wechat/order/myOrders.htm?type=stayReturn"> 
									<span class="icon mui-icon-extra mui-icon-extra-gift"></span>
									<div class="mui-media-body">待返款</div>
								</a>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="${ctx }/wechat/order/allAfterSalePage.htm">
									<span class="icon mui-icon-extra mui-icon-extra-custom"></span>
									<div class="mui-media-body">售后</div>
								</a>
							</li>
						</ul>
					</li>
					<li class="mui-table-view-cell">
						<a href="${ctx }/wechat/user/myWalletPage.htm" class="mui-navigate-right notshare"> 
							<span class="mui-icon-extra mui-icon-extra-gold mui-pull-left"></span>我的钱包 </a>
					</li>
					<li class="mui-table-view-cell no-padding">
						<ul id="icon-grid-9" class="icon-grid mui-table-view mui-grid-view mui-grid-9">
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="#">
									<div class="mui-media-body">余额</div>
								</a> 
								<span class="money-show" id="yue">0.00</span>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="#">
									<div class="mui-media-body">返现金</div>
								</a> 
								<span class="money-show" id="fanxain">0.00</span>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="#">
									<div class="mui-media-body">待返现金</div>
								</a> 
								<span class="money-show" id="waitFanxian">0.00</span>
							</li>
							<li class="mui-table-view-cell mui-media mui-col-xs-3 mui-col-sm-3">
								<a href="#" id="tixian">
									<div class="mui-media-body">提现</div>
									<span class="money-show" id="yue">0.00</span>
								</a>
							</li>
						</ul>
					</li>
					<li class="mui-table-view-cell">
						<a href="${ctx }/wechat/user/updateloginpwdpage.htm" class="mui-navigate-right notshare"> 
							<span class="mui-icon mui-icon-compose mui-pull-left"></span> 修改或设置登录密码
						</a>
					</li>
					<li  class="mui-table-view-cell">
						<a href="${ctx }/wechat/user/setLoninNamePage.htm" class="mui-navigate-right notshare"> 
							<span class="mui-icon mui-icon-compose mui-pull-left"></span> 修改或设置登录名称
						</a>
					</li>
					<li class="mui-table-view-cell">
						<a href="${ctx }/wechat/user/newAddressPage.htm" class="mui-navigate-right notshare"> 
							<span class="mui-icon mui-icon-map mui-pull-left"></span> 收货地址
						</a>
					</li>
					<li class="mui-table-view-cell">
						<a id="verify" href="${ctx }/wechat/user/verifyRealNamePage.htm"  class="mui-navigate-right notshare"> 
							<span class="mui-icon mui-icon-paperplane mui-pull-left"></span> 申请实名认证
						</a>
					</li>
					<li class="mui-table-view-cell">
						<a href="javascript:guideDiv();" class="mui-navigate-right weixinshare"> 
							<span class="mui-icon mui-icon-redo mui-pull-left"></span> 分享给好友
						</a>
					</li>
				</ul>
			</div>
		</div>
	</div>

	<script type="text/javascript" charset="utf-8"> 
	

		(function($, doc) {
			$.init([LoadCartNum()]);
			$.ready(function() {
				mui('body').on('tap','a',function(){document.location.href=this.href;});
				mui(".mui-tab-item-wboly")[3].classList.add('mui-active');
				mui('#usercenter_main.mui-scroll-wrapper').scroll({bounce: false});
				
				mui("#guideDiv")[0].addEventListener("tap", function(){
					document.getElementById("guideDiv").style.display=(document.getElementById("guideDiv").style.display=="none")?"":"none";
				});
				
				var tixian = doc.getElementById('tixian');
				if(tixian != null){
					//点击提现按钮			
					tixian.addEventListener('tap', function(event) {
						var exist = "${exist}";
						if ("${exist}" == "true") {
							document.location.href= "${ctx }/wechat/user/wechatWithdraw.htm";// 跳转至提现页面
						} else {
							mui.alert("没有发现提现账号"," ");
						}
					});
				}
				getMoney();
				
			    function setCookie(name, value) {
			        var exp = new Date();
			        exp.setTime(exp.getTime() + 60 * 60 * 1000);
			        document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
			    }
				
				function verifyRealName(){
					var userId=document.getElementById("verifyUserId");
					var url="http://192.168.1.16:20088/rna/getbyuserid";
					$.get(url,{'userId':userId.value},function(data){
						console.log("返回的值"+data);
						if(data==null || data ==""){
							return;
						}
						data = JSON.parse(data)
						var verify=document.getElementById("verify");
						var html='';
						if(data.applyState==null){
							return;
						}else{
							verify.href="${ctx }/wechat/user/verifyResult.htm";
						    setCookie("applyState", data.applyState);
						    setCookie("rejectReason", data.rejectReason);

						}

					})
					
				}
				verifyRealName();
			});
		})(mui, document);
		

		
	</script>
</body>
</html>