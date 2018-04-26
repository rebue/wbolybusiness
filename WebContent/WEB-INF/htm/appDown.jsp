<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>APP下载</title>
<meta name="viewport"
	content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<script
	src="http://csdnimg.cn/public/common/libs/jquery/jquery-1.9.1.min.js"
	type="text/javascript"></script>
<script type="text/javascript">
	$(function () {
		verifyDown();
	});
	
	function verifyDown(){
		function isWeiXin() {
			var ua = window.navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == 'micromessenger') {
				return true;
			} else {
				return false;
			}
		}

		if (browser.versions.ios || browser.versions.iPhone || browser.versions.iPad) {
			if(isWeiXin()){
				window.location = "https://itunes.apple.com/cn/app/wei-bao-li-shang-chao/id1179392082?mt=8&uo=4";
			}else{
				window.location = "https://itunes.apple.com/cn/app/wei-bao-li-shang-chao/id1179392082?mt=8&uo=4";
			}

		}else{
			if(isWeiXin()){
			}else{
				window.location = "http://serverapp.wboly.com/wbolybusiness/app/wbolyapp/"+"${app}";
			}
		}
	}

	/*
	 * 智能机浏览器版本信息:
	 *
	 */
	var browser = {
		versions: function () {
			var u = navigator.userAgent, app = navigator.appVersion;
			return {//移动终端浏览器版本信息
				trident: u.indexOf('Trident') > -1, //IE内核
				presto: u.indexOf('Presto') > -1, //opera内核
				webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
				gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
				mobile: !!u.match(/AppleWebKit.*Mobile.*/) || !!u.match(/AppleWebKit/), //是否为移动终端
				ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端
				android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
				iPhone: u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
				iPad: u.indexOf('iPad') > -1, //是否iPad
				webApp: u.indexOf('Safari') == -1 //是否web应该程序，没有头部与底部
			};
		}(),
		language: (navigator.browserLanguage || navigator.language).toLowerCase()
	}
</script>
</head>
<body>
</body>
</html>