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
		<h4 class="mui-title">发表评价</h4>
	</header>
	<div id="opt_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="info-bar order-num mui-table-view-cell">订单号：
				${commentData['orderId'] }</div>
			<div class="order-detail-box mui-table-view-cell">
				<c:choose>
					<c:when test='${commentData["ruleNum"]!="" }'>
						<div class="order-img-box limit">
							<img src="${commentData['img'] }" alt="" />
						</div>
					</c:when>
					<c:otherwise>
						<div class="order-img-box">
							<img src="${commentData['img'] }" alt="" />
						</div>
					</c:otherwise>
				</c:choose>

				<div class="order-info-box">
					<h5>
						<a href="${ctx}/wechat/goods/${commentData['shopId'] }/${commentData['supplierUid'] }/${commentData['goodsId'] }/${commentData['activityId'] }.htm">${commentData['goodsName'] }</a>
					</h5>
					<h6>规格：${commentData['attrVal'] }</h6>
					<div class="price-box">
						<span id="price" class="mui-pull-left">¥${commentData['price'] }</span> 
						<span id="count" class="mui-pull-right">数量：${commentData['number'] }</span>
					</div>
				</div>
			</div>


			<div class="info-bar rate mui-table-view-cell">
				<span class="mui-pull-left">评分：</span>
				<ul class="mui-pull-left">
					<li class="mui-icon mui-icon-star-filled on"></li>
					<li class="mui-icon mui-icon-star-filled on"></li>
					<li class="mui-icon mui-icon-star-filled on"></li>
					<li class="mui-icon mui-icon-star-filled on"></li>
					<li class="mui-icon mui-icon-star-filled on"></li>
				</ul>
			</div>
			<div class="comment-text mui-table-view-cell mui-input-row">
				<textarea name="text" rows="4" maxlength="140" placeholder="填写评价内容" id="text"></textarea>
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
		<button class="mui-pull-right" id="uploadBtn">提交评价</button>
	</footer>

	<script type="text/javascript" src="${ctx }/js/wechat/upload.js"></script>
	<script type="text/javascript" charset="utf-8">
		var orderId =  "${commentData['orderId'] }";
		var activityId =  "${commentData['activityId'] }";
		var rate = 5;
		
		var arr = new Array();
		
		var label = "点此上传图片（可选），最多5张";
		var NumLimit = 5;
		var SizeLimit = 15 * 1024 * 1024; // 15 M     
		var SingleSizeLimit = 3 * 1024 * 1024; // 3 M 
		
		var serverUrl = '${ctx}/app/Util/fileUpload.htm?key=59c23bdde5603ef993cf03fe64e448f1&configName=appraiseSavePath';//测试
		
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
				
				//INPUT焦点BUG
				document.addEventListener('tap', function(e) {
					var target = e.target;
					var inputs = document.querySelectorAll("input");
					var textareas = document.querySelectorAll("textarea");
					if(!(target.tagName && (target.tagName === 'TEXTAREA' || (target.tagName === 'INPUT' && (target.type === 'text' || target.type === 'search' || target.type === 'number'))))) {
						for(var i = 0; i < inputs.length; i++) {
							inputs[i].blur();
						}
					};
					for(var i = 0; i < textareas.length; i++) {
						textareas[i].blur();
					};
				});
				
				mui(document).on("tap", "#uploadBtn", function() {
					if(rate != 0 && rate != "" && mui("#text")[0].value != "") {
						//下面的判断意思是，如果没有图片，则直接提交文本(ajaxupdate)，如果有图，则先上传图片，上传成功后会自动运行ajaxupdate();
						if(mui(".filelist img").length > 0) {
							uploader.upload();
						} else {
							ajaxupdate();
						}
					} else {
						mui.toast("请填写评价内容");
					}
				});

			});
		})(mui, document);

		var li = mui('.rate ul li');
		for(var i = 0; i < li.length; i++) {
			li[i].onclick = taped(i);
		};

		function taped(i) {
			var ontap = function() {
				rate = i + 1;
				star(rate);
			}
			return ontap;
		};

		function star(index) {
			var li = mui('.rate ul li');
			for(var i = 0; i < li.length; i++) {
				li[i].classList.remove("att");
				li[index - 1].classList.add("att");
				if(i < index) {
					li[i].classList.add("on");
				} else {
					li[i].classList.remove("on");
				};
			};
		};

		function ajaxupdate() {
			mui.ajax('${ctx}/wechat/order/appraiseGoods.htm', {
				type: 'post',
				dataType: 'json',
				data: {
					"orderId" : orderId,
					"activityId" : activityId,
					"babyEvaluation" : rate,
					"content" : mui("#text")[0].value,
					"key" : "59c23bdde5603ef993cf03fe64e448f1",
					"imgs" : arr.join(",")
				},
				success: function(msg) {
					if(msg.flag){
						mui.alert(msg.message,' ',function() {
							window.location.href="${ctx}/wechat/order/myOrders.htm"
						});
						return ;
					};
					mui.toast(msg.message);
				},
				error: function() {
					mui.alert("提交失败");
				}
			});
		};
	</script>
</body>
</html>