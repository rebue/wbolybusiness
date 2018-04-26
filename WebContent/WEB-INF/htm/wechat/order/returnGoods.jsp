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
<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/mui.min.css">
<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/wboly_mobile.css">
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
		<h4 class="mui-title">退货</h4>
	</header>
	<div id="opt_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="info-bar order-num mui-table-view-cell">订单号：${returnData['orderCode'] }</div>
			<div class="order-detail-box mui-table-view-cell">
				<div class="order-img-box">
					<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=${returnData['onlineId'] }">
						<img src="${returnData['goodsQsmm'] }" alt="" />
					</a>
				</div>
				<div class="order-info-box">
					<h5>
						<a href="${ctx}/wechat/goods/goodsDetail.htm?onlineId=${returnData['onlineId'] }">${returnData['onlineTitle'] }</a>
					</h5>
					<h6>规格： ${returnData['specName'] }</h6><br/>
					<div class="price-box">
						<span id="price" class="mui-pull-left">¥${returnData['buyPrice'] }</span>
						<div class="mui-numbox" id="mui-numbox" data-numbox-min="1" data-numbox-max="${returnData['buyCount'] }">
							<button class="mui-btn mui-btn-numbox-minus" id="btn-minus" type="button">-</button>
							<input id="count" class="mui-input-numbox" type="number" value="${returnData['buyCount'] }">
							<button class="mui-btn mui-btn-numbox-plus" id="btn-plus" type="button">+</button>
						</div>
					</div>
				</div>
			</div>
			<div class="comment-text mui-table-view-cell mui-input-row">
				<textarea name="text" rows="5" maxlength="150" placeholder="亲，写点退货原因吧，您的信息对商家有很大帮助" id="text"></textarea>
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

	<footer class="mui-bar" id="ulbtn-box">
		<button class="mui-pull-right" id="uploadBtn">提交申请</button>
	</footer>

	<script type="text/javascript" charset="utf-8">
		// 订单编号
		var orderCode = "${returnData['orderCode'] }";
		// 订单详情编号
		var orderDetailId = "${returnData['orderDetailId'] }";
		// 规格名称
		var specName = "${returnData['specName'] }";
		// 上线编号
		var onlineId = "${returnData['onlineId'] }";
		// 退货图片
		var arr = new Array();

		var label = "点此上传凭证图片，最多3张";
		var NumLimit = 3;
		var SizeLimit = 9 * 1024 * 1024; // 15 M     
		var SingleSizeLimit = 3 * 1024 * 1024; // 3 M 
		var serverUrl = '${ctx}/app/Util/fileUpload.htm?key=59c23bdde5603ef993cf03fe64e448f1&configName=returnGoodsSavePath'; //测试

		(function($, doc) {
			mui.init();
			$.ready(function() {
				mui('#opt_main').on('tap', 'a', function() {
					document.location.href = this.href;
				});
				mui('#opt_main.mui-scroll-wrapper').scroll({
					bounce : false
				});

				window.addEventListener('touchmove', function(e) {
					var target = e.target;
					if (target && target.tagName === 'TEXTAREA') { //textarea阻止冒泡
						e.stopPropagation();
					}
				}, true);

				//焦点BUG
				document.addEventListener('tap', function(e) {
					var textareas = document.querySelectorAll("textarea");
					for (var i = 0; i < textareas.length; i++) {
						textareas[i].blur();
					}
					;
				});

				mui(document).on("tap", "#uploadBtn", function() {
					if (mui("#text")[0].value != "") {
						if (mui(".filelist img").length > 0) {
							uploader.upload(); //在uploader的上传成功事件里触发ajaxupdate();
						} else {
							mui.toast("请上传凭证图片");
						}
					} else {
						mui.toast("请填写退货原因");
					}
				});
			});
		})(mui, document);

		function ajaxupdate() {
			// 退货数量
			var returnNum = mui("#mui-numbox").numbox().getValue();
			// 该规格订单数量
			var num = "${returnData['buyCount'] }";
			// 退货原因
			var returnReason = mui("#text")[0].value;
			if (returnNum > num) {
				mui.toast("退货数量不能大于订单数量");
				return;
			};
			// 购买单价
			var buyPrice = "${returnData['buyPrice'] }";
			// 退货金额
			var returnPrice = parseFloat(buyPrice * returnNum);
			console.log("returnTotalPrice===" + returnPrice);
			mui.ajax('${ctx}/wechat/order/returnGoods.htm', { //url测试的
				type : 'post',
				dataType : 'json',
				data : {
					"orderCode" : orderCode,
					"orderDetailId" : orderDetailId,
					"onlineId" : onlineId,
					"returnReason" : returnReason,
					"returnNum" : returnNum,
					"specName" : specName,
					"returnPrice" : returnPrice,
					"returnImg" : arr.join(","),
					"key" : "59c23bdde5603ef993cf03fe64e448f1"
				},
				success : function(data) {
					console.log(data);
					if (data.result == 1) {
						mui.alert(data.msg, ' ', function() {
							window.location.href = "${ctx}/wechat/order/myOrders.htm"
						});
						return;
					};
					mui.toast(data.msg);
				},
				error : function() {
					mui.alert("提交失败");
				}
			});
		};
	</script>
	<script type="text/javascript" src="${ctx }/js/wechat/upload.js"></script>
</body>
</html>