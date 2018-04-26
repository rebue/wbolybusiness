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
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/operation.css" />
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/webuploader.css" />
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/jquery.min.js" type="text/javascript"></script>
	<script src="${ctx }/js/wechat/webuploader.html5only.min.js" type="text/javascript"></script>
</head>

<body>
	<header class="mui-bar mui-bar-nav">
		<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left"></a>
		<h4 class="mui-title">意见反馈</h4>
	</header>
	<div id="opt_main" class="mui-scroll-wrapper">
		<div class="mui-content mui-scroll">
			<div class="info-bar mui-table-view-cell advice">
				<a href="#sheet" class="mui-navigate-right"> <span
					class="mui-pull-left">请选择您建议的版块：</span> <span
					class="r-info mui-pull-left" id="r-info"></span>
				</a>
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">联系方式：</span> <input type="number" name="phone" id="phone" placeholder="您的手机号码（选填）" oninput="if(value.length>11)value=value.slice(0,11)" />
			</div>
			<div class="info-bar mui-table-view-cell phone-box">
				<span class="mui-pull-left">联系客服：</span> 
				<span class="r-info mui-pull-left" id="r-info" style="color: #FE3000;">0771-6738777</span>
			</div>
			<div class="comment-text mui-table-view-cell mui-input-row">
				<textarea name="text" rows="7" maxlength="150" placeholder="欢迎留下您宝贵的意见和建议，我们会认真对待每一位用户提出的意见和建议。（10到150字）" id="text"></textarea>
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
			<li class="mui-table-view-cell"><a data-value="1">登录、注册</a></li>
			<li class="mui-table-view-cell"><a data-value="2">商品浏览、购买</a></li>
			<li class="mui-table-view-cell"><a data-value="3">订单、支付</a></li>
			<li class="mui-table-view-cell"><a data-value="4">经验、积分</a></li>
			<li class="mui-table-view-cell"><a data-value="5">其他</a></li>
		</ul>
		<!-- 取消菜单 -->
		<ul class="mui-table-view">
			<li class="mui-table-view-cell"><a href="#sheet"><b>取消</b></a></li>
		</ul>
	</div>
	<footer class="mui-bar" id="ulbtn-box">
		<button class="mui-pull-right" id="uploadBtn">提交</button>
	</footer>

	<script type="text/javascript" charset="utf-8">
		var reason = 0;

		var arr = new Array();
		
		var label = "上传图片（选填），最多一张";
		var NumLimit = 1;
		var SizeLimit = 3 * 1024 * 1024; // 15 M     
		var SingleSizeLimit = 3 * 1024 * 1024; // 3 M 
		var serverUrl = '${ctx}/app/Util/fileUpload.htm?key=59c23bdde5603ef993cf03fe64e448f1&configName=feedbackPath'; //测试

		(function($, doc) {
			mui.init();
			$.ready(function() {
				mui('#opt_main').on('tap', '.order-info-box a', function() { document.location.href = this.href; });
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
					var msgtext = mui("#text")[0].value;
					var phone = mui("#phone")[0].value;
					var imglength = mui(".filelist img").length;
					if(reason == 0) {
						mui.toast("请选择<br/>建议的版块");
					} else if(msgtext == "" || msgtext.length < 10 || msgtext.length > 150) {
						mui.toast("请填写10到150字的反馈内容");
					} 
					//好像安卓里手机号不是必填项
					else if(imglength > 0) {
						uploader.upload();
					} else {
						ajaxupdate();
					}
				});

			});
		})(mui, document);

		function ajaxupdate() {
			mui.ajax('${ctx}/wechat/user/saveFeedback.htm', { //url测试的
				type: 'post',
				dataType: 'json',
				data: {
					"key": "59c23bdde5603ef993cf03fe64e448f1",
					"type": reason,
					"mobile": mui("#phone")[0].value,
					"message": mui("#text")[0].value,
					"imgPath": arr.join(","),
				},
				success: function(msg) {
					if(msg.flag){
						mui.alert(msg.message,' ', function() {
							window.location.href="${ctx}/wechat/user/userCenter.htm"
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