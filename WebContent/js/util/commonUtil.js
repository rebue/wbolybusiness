document.write(" <script type='text/javascript' src='/wbolybusiness/js/util/base64.js'></script>");

var areaPicker;

/**
 * 所有门店数据
 */
function allShopData() {
	if (getItem("allShopData", 1000 * 60 * 60) == null) {
		mui.ajax('/wbolybusiness/wechat/index/getShopArea.htm', {
			data : {
				key : '59c23bdde5603ef993cf03fe64e448f1'
			},
			dataType : 'json',// 服务器返回json格式数据
			type : 'post',// HTTP请求类型
			success : function(data) {
				areaPicker.setData(data);
				setItem("allShopData", JSON.stringify(data));
			},
			error : function(xhr, type, errorThrown) {
				// 异常处理；
				console.log(type + ":" + errorThrown);
			}
		});
	} else {
		areaPicker.setData(getItem("allShopData", 1000 * 60 * 60));
	}
}

/**
 * 保存数据到缓存
 * 
 * @param key
 *            主键
 * @param value
 *            数据
 */
function setItem(key, value) {
	var curTime = new Date().getTime();// 当前时间
	localStorage.setItem(key, JSON.stringify({
		data : value,
		time : curTime
	}));
}

/**
 * 读取缓存数据
 * 
 * @param key
 *            主键
 * @param exp
 *            过期时间(1000*60*60 一小时)
 */
function getItem(key, exp) {
	var data = localStorage.getItem(key);
	if (data == null) {
		return null;
	}
	var dataObj = JSON.parse(data);
	if (new Date().getTime() - dataObj.time > exp) {
		return null;
	} else {
		return JSON.parse(dataObj.data);
	}
}

/**
 * 时间戳转日期
 * 
 * @param nS
 * @returns
 */
function getLocalTime(nS) {
	return new Date(parseInt(nS) * 1000).toLocaleString().replace(/:\d{1,2}$/, ' ');
}

/**
 * 时间戳转日期格式
 * 
 * @param nS
 * @returns {String}
 */
function getTime(nS) {
	var date = new Date(parseInt(nS) * 1000); // 中国标准时间
	var year = date.getFullYear();// 年
	var mon = date.getMonth() + 1; // 月
	var day = date.getDate(); // 日
	var hours = date.getHours(); // 时
	var minu = date.getMinutes(); // 分
	var sec = date.getSeconds(); // 秒
	// 拼成时间格式
	return year + '-' + mon + '-' + day + ' ' + hours + ':' + minu + ':' + sec;
}

/**
 * 时间戳转日期格式
 * 
 * @param nS
 * @returns {String}
 */
function timestampToTime(nS) {
	var date = new Date(parseInt(nS)); // 中国标准时间
	var year = date.getFullYear();// 年
	var mon = date.getMonth() + 1; // 月
	var day = date.getDate(); // 日
	var hours = date.getHours(); // 时
	var minu = date.getMinutes(); // 分
	var sec = date.getSeconds(); // 秒
	// 拼成时间格式
	return year + '-' + mon + '-' + day + ' ' + hours + ':' + minu + ':' + sec;
}

/**
 * 格式化金钱
 * 
 * @param num
 * @returns {String}
 */
function formatCurrency(num) {
	num = parseFloat(num).toString().replace(/\$|\,/g, '');
	if (isNaN(num))
		num = "0";
	sign = (num == (num = Math.abs(num)));
	num = Math.floor(num * 100 + 0.50000000001);
	cents = num % 100;
	num = Math.floor(num / 100).toString();
	if (cents < 10)
		cents = "0" + cents;
	for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
		num = num.substring(0, num.length - (4 * i + 3)) + // ','+
		num.substring(num.length - (4 * i + 3));
	return (((sign) ? '' : '-') + num + '.' + cents);
}

/**
 * 写入cookies
 * 
 * @param name
 * @param value
 */
function setCookie(name, value) {
	// 判断客户端是否开启cookie
	if (navigator.cookieEnabled) {
		var Days = 30;
		var exp = new Date();
		exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
		document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
	} else {
		mui.toast("您的Cookie尚未开启,无法执行流程");
	}
}

/**
 * 读取cookies
 * 
 * @param name
 * @returns
 */
function getCookie(name) {
	// 判断客户端是否开启cookie
	if (navigator.cookieEnabled) {
		var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
		if (arr = document.cookie.match(reg))
			return unescape(arr[2]);
		else
			return null;
	} else {
		mui.toast("您的Cookie尚未开启,无法执行流程");
	}
}

/**
 * 删除cookies
 * 
 * @param name
 */
function delCookie(name) {
	var exp = new Date();
	exp.setTime(exp.getTime() - 1);
	var cval = getCookie(name);
	if (cval != null)
		document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
}

/**
 * 加载购物车数量
 */
function LoadCartNum() {
	mui.ajax('/wbolybusiness/wechat/cart/getCartNum.htm', {
		data : {
			key : '59c23bdde5603ef993cf03fe64e448f1',
		},
		dataType : 'json',// 服务器返回json格式数据
		type : 'post',// HTTP请求类型
		success : function(data) {
			document.getElementById("cartnum").innerText = data;
		},
		error : function(xhr, type, errorThrown) {
			// 异常处理；
			console.log(type);
		}
	});
}

/**
 * 获取用户默认收货地址
 */
function commonAddress() {
	// AddList
	mui.ajax('/wbolybusiness/wechat/user/getaddress.htm', {
		data : {
			key : '59c23bdde5603ef993cf03fe64e448f1',
			isDefault : '1'
		},
		dataType : 'json',// 服务器返回json格式数据
		type : 'post',// HTTP请求类型
		success : function(data) {
			console.log(data);
			if (data.flag) {
				var html = '';
				document.body.querySelector("#AddList").innerHTML = html;
				html += '<li class="mui-table-view-cell">';
				html += '	<div class="mui-table mui-navigate-right">';
				html += ' 		<div class="mui-table-cell mui-col-xs-8 mui-col-sm-8">';
				html += ' 			<h5 class="mui-ellipsis" id="receiver">'+ data.message[0].receiverName + '</h5>	';
				html += '			<input type="hidden" name="addressId" value="' + data.message[0].id + '"/>';
				html += '		</div>';
				html += '		<div class="mui-table-cell mui-col-xs-4  mui-col-sm-4 mui-text-right">';
				html += ' 			<span class="mui-h5" id="receiver-phone">' + data.message[0].receiverMobile + '</span>';
				html += ' 		</div>';
				html += '	 </div>';
				html += 	'<p class="mui-h6 mui-ellipsis" id="receiver-add">' + data.message[0].receiverProvince +  data.message[0].receiverCity + data.message[0].receiverExpArea + '-' + data.message[0].receiverAddress + '</p>';
				html += '</li>';
				document.body.querySelector("#AddList").innerHTML = html;
			} else {
				var html = '';
				document.body.querySelector("#AddList").innerHTML = html;
				html += '<li class="mui-table-view-cell">';
				html += '	<div class="mui-table mui-navigate-right">';
				html += '  		<div class="mui-table-cell mui-col-xs-8 mui-col-sm-8">';
				html += '  			<h5 class="mui-ellipsis">您还没有设置默认地址哦</h5>';
				html += '		</div>';
				html += '	</div>';
				html += ' 	<p class="mui-h6 mui-ellipsis">点击此处去填写地址或设置默认地址</p>';
				html += '</li>';
				document.body.querySelector("#AddList").innerHTML = html;
				if (data.message != "") {
					mui.toast(data.message);
				}
			}
		},
		error : function(xhr, type, errorThrown) {
			// 异常处理；
			console.log(type);
		}
	});
}

/**
 * @param type
 *            加载时 type=1 加载完成 type=2
 */
function loading(type) {
	var loadingDiv = document.createElement("div");
	var bgDiv = document.createElement("div");
	loadingDiv.innerHTML = '<div class="common mui-loading" id="loading"><div class="loading-bg"><div class="mui-spinner"></div></div></div>';
	bgDiv.innerHTML = '<div id="ldbg" class="common mui-popup-backdrop mui-active"></div>';
	if (type == 1) {
		document.body.appendChild(loadingDiv.childNodes[0]);
		document.body.appendChild(bgDiv.childNodes[0]);
		document.getElementById("loading").style.display = "block";
		document.getElementById("ldbg").style.display = "block"
	} else if (type == 2) {
		var div = document.getElementById("loading");
		var div2 = document.getElementById("ldbg");
		div.parentNode.removeChild(div);
		div2.parentNode.removeChild(div2)
	}
};

/**
 * 申请售后页面
 * 
 * @param gn
 *            售后数据
 */
function afterSalePage(gn) {
	if (gn == null) {
		mui.toast("没有该商品信息");
		return;
	}
	window.location.href = "/wbolybusiness/wechat/order/afterSalePage.htm?afterSaleData=" + b.encode("\"" + gn + "\"");
};

/**
 * 评价商品页面
 * 
 * @param gn
 *            商品评价数据
 */
function commentPage(gn) {
	if (gn == null) {
		mui.toast("没有该商品信息");
		return;
	}
	window.location.href = "/wbolybusiness/wechat/order/commentPage.htm?commentData=" + b.encode("\"" + gn + "\"");
};

/**
 * 退货页面
 * 
 * @param gn
 *            退货数据
 */
function returnPage(gn) {
	if (gn == null) {
		mui.toast("没有该商品信息");
		return;
	}
	window.location.href = "/wbolybusiness/wechat/order/returnPage.htm?returnData=" + b.encode("\"" + gn + "\"");
};

/**
 * 立即付款
 * 
 * @param key
 */
function payPage(key) {
	loading(1);
	/*
	 * Post("/wbolybusiness/wechat/pay/paycenterPage.htm", { payOrderId:key },
	 * 0);
	 */
	window.location.href = "/wbolybusiness/wechat/pay/center/" + key + ".htm";
	loading(2);
};

/**
 * 我的钱包金额
 */
function getMoney() {
	mui.ajax('/wbolybusiness/wechat/user/getMoney.htm', {
		data : {
			key : '59c23bdde5603ef993cf03fe64e448f1'
		},
		dataType : 'json',// 服务器返回json格式数据
		type : 'post',// HTTP请求类型
		timeout : 10000,// 超时时间设置为10秒；
		success : function(data) {
			if (data.flag) {
				document.getElementById("balance").innerHTML = formatCurrency(data.message.balance);
				document.getElementById("cashback").innerHTML = formatCurrency(data.message.cashback);
				if (data.message.commissioning != 0) {
					document.getElementById("commissioning").innerHTML = formatCurrency(data.message.commissioning);
				}
				document.getElementById("commissionTotal").innerHTML = formatCurrency(data.message.commissionTotal);
				document.getElementById("withdrawing").innerHTML = formatCurrency(data.message.withdrawing);
				return;
			}
			mui.toast(data.message);
		},
		error : function(xhr, type, errorThrown) {
			// 异常处理；
			console.log(type);
			if (type == "timeout") {
				mui.toast("请求我的钱包超时");
			}
		}
	});
};

/**
 * 功能： 模拟form表单的提交 参数： url 跳转地址 parameters 参数 FLAG 打开新页面 1,默认 0
 */
function Post(url, parameters, flag) {
	// 创建form表单
	var temp_form = document.createElement("form");
	temp_form.action = url;
	// 如需打开新窗口，form的target属性要设置为'_blank'
	if (flag != undefined && flag == 1) {
		temp_form.target = "_blank";
	} else {
		temp_form.target = "_self";
	}
	temp_form.method = "post";
	temp_form.style.display = "none";
	// 添加参数
	for ( var key in parameters) {
		var opt = document.createElement("textarea");
		opt.name = key;
		opt.value = parameters[key];
		temp_form.appendChild(opt);
	}
	document.body.appendChild(temp_form);
	// 提交数据
	return temp_form.submit();
}

/**
 * 判断是否是在微信里面
 */
function isWeiXin() {
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) == "micromessenger") {
		return true;
	} else {
		return false;
	}
}

/**
 * 复制分享内容
 */
function copyShare() {
	var obj = document.getElementById("shareurl");
	obj.select(); // 选择对象
	document.execCommand("Copy"); // 执行浏览器复制命令
	alert("已复制好，可贴粘。");
}

/**
 * 引导提示
 */
function guideDiv() {
	if (isWeiXin()) {
		document.getElementById("guideDiv").style.display = (document.getElementById("guideDiv").style.display == "none") ? "" : "none";
	}
}

/**
 * 是获取url参数的方法
 */
function GetQueryString(name) // 是获取url参数的方法
{
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return decodeURI(r[2]);
	return null;
};

