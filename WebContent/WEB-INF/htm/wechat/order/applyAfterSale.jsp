<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/operation.css" />
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/webuploader.css" />
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/jquery.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/wechat/webuploader.html5only.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/util/commonUtil.js"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">申请售后</h4>
	</header>
	<div id="opt_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="info-bar order-num mui-table-view-cell">订单号：${afterSaleData.orderId }</div>
			<div class="order-detail-box mui-table-view-cell">

				<c:choose>
					<c:when test='${afterSaleData["ruleNum"]!="" }'>
						<div class="order-img-box limit">
							<img src="${afterSaleData['img'] }" alt="" />
						</div>
					</c:when>
					<c:otherwise>
						<div class="order-img-box">
							<img src="${afterSaleData['img'] }" alt="" />
						</div>
					</c:otherwise>
				</c:choose>

				<div class="order-info-box">
					<h5>
						<a href="javascript:GoodsDetailJump(${afterSaleData['goodsId'] },${afterSaleData['activityId'] },${afterSaleData['supplierUid'] },${afterSaleData['shopId'] });">${afterSaleData.goodsName }</a>
					</h5>
					<h6>规格：${afterSaleData.attrVal }</h6>
					<div class="price-box">
						<span id="price" class="mui-pull-left">¥${afterSaleData.price }</span> 
						<span id="count" class="mui-pull-right">数量：${afterSaleData.number }</span>
					</div>
				</div>
			</div>
			<div class="info-bar mui-table-view-cell">
				<a href="#sheet" class="arrowdown mui-navigate-right"> 
					<span class="mui-pull-left">申请售后原因：</span> 
					<span class="r-info mui-pull-left" id="r-info">不发货</span>
				</a>
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">联系方式：</span> 
				<input type="number" name="phone" id="phone" placeholder="手机号码(填写错误概不负责)" oninput="if(value.length>11)value=value.slice(0,11)" />
			</div>
			<div class="comment-text mui-table-view-cell mui-input-row">
				<textarea name="text" rows="4" maxlength="100" placeholder="填写申请详情" id="text"></textarea>
			</div>
			<div class="img-upload-box mui-table-view-cell">
				<div id="container">
					<!--头部，相册选择和格式选择 -->
					<div id="uploader">
						<div class="queueList">
							<div id="dndArea" class="placeholder">
								<div id="filePicker"></div>
							</div>
						</div>
						<div class="statusBar" style="display: none;">
							<div class="progressBox">
								<div class="progress">
									<span class="text">0%</span> <span class="percentage"></span>
								</div>
								<div class="info"></div>
							</div>
							<div class="btns">
								<div id="filePicker2"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="sheet"
		class="mui-popover mui-popover-bottom mui-popover-action ">
		<!-- 可选择菜单 -->
		<ul class="mui-table-view btn-list">
			<li class="mui-table-view-cell"><a data-value="1">不发货</a></li>
			<li class="mui-table-view-cell"><a data-value="2">质量问题</a></li>
			<li class="mui-table-view-cell"><a data-value="3">服务不好</a></li>
		</ul>
		<!-- 取消菜单 -->
		<ul class="mui-table-view">
			<li class="mui-table-view-cell"><a href="#sheet"><b>取消</b></a></li>
		</ul>
	</div>
	<footer class="mui-bar" id="ulbtn-box">
		<button class="mui-pull-right" id="uploadBtn">提交申请</button>
	</footer>

	<script type="text/javascript" charset="utf-8">
		var orderId = "${afterSaleData.orderId }";
		var activityId = "${afterSaleData.activityId }";
		var reason = 1;

		var arr = new Array();
		
		var label = "点此上传凭证图片，最多3张";
		var NumLimit = 3;
		var SizeLimit = 9 * 1024 * 1024; // 15 M     
		var SingleSizeLimit = 3 * 1024 * 1024; // 3 M 
		var serverUrl = '${ctx}/app/Util/fileUpload.htm?key=59c23bdde5603ef993cf03fe64e448f1&configName=returnGoodsSavePath';//测试
		
		(function($, doc) {
			mui.init();
			$.ready(function() {
				mui('#opt_main').on('tap','.order-info-box a',function(){document.location.href=this.href;});
				mui('#opt_main.mui-scroll-wrapper').scroll({ bounce: false });

				window.addEventListener('touchmove', function(e) {
					var target = e.target;
					if(target && target.tagName === 'TEXTAREA') { //textarea阻止冒泡
						e.stopPropagation();
					}
				}, true);

				//焦点BUG
				document.addEventListener('tap', function(e) {
					var textareas = document.querySelectorAll("textarea");
					for(var i = 0; i < textareas.length; i++) {
						textareas[i].blur();
					};
				});

				//可选择菜单
				var info = document.getElementById("r-info");
				mui(document).on('tap', '.mui-popover-action .btn-list li>a', function() {
					var a = this,
						parent;
					//根据点击按钮，反推当前是哪个actionsheet
					for(parent = a.parentNode; parent != document.body; parent = parent.parentNode) {
						if(parent.classList.contains('mui-popover-action')) {
							break;
						}
					}
					//关闭actionsheet
					mui('#' + parent.id).popover('toggle');
					info.innerHTML = a.innerHTML;
					reason = a.getAttribute("data-value");
				});

				mui(document).on("tap", "#uploadBtn", function() {
					if(reason != "" && mui("#text")[0].value != "" && mui("#phone")[0].value != "") {
						if(mui(".filelist img").length > 0) {
							uploader.upload(); //在uploader的上传成功事件里触发ajaxupdate();
						} else {
							mui.toast("请上传凭证图片");
						}
					} else {
						mui.toast("申请详情或联系方式未填写");
					}
				});

			});
		})(mui, document);

		function ajaxupdate() {
			mui.ajax('${ctx}/wechat/order/afterSaleApply.htm', { //url测试的
				type: 'post',
				dataType: 'json',
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"orderIds": orderId,
					"activityId": activityId,
					"message": mui("#text")[0].value,
					"appealType":reason,
					"buyerContact":mui("#phone")[0].value,
					"img":arr.join(","),
				},
				success: function(msg) {
					if(msg.flag){
						mui.alert(msg.message,' ', function() {
							window.location.href="${ctx}/wechat/order/myOrders.htm?promoterId=${userId}"
						});
						return ;
					};
					mui.toast(msg.message);
				},
				error: function() {
					mui.alert("提交失败");
				}
			});
		}
	</script>
	<script type="text/javascript" src="${ctx }/js/wechat/upload.js"></script>
</body>
</html>