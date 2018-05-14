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
	<link rel="stylesheet" type="text/css" href="${ctx }/css/wechat/mui-icons-extra.css" />
	<link rel="stylesheet" href="${ctx }/css/wechat/wboly_mobile.css">
	<link rel="stylesheet" href="${ctx }/css/wechat/address.css">
	<link href="${ctx }/css/wechat/mui.picker.css" rel="stylesheet" />
	<link href="${ctx }/css/wechat/mui.poppicker.css" rel="stylesheet" />
	<link rel="stylesheet" href="${ctx }/css/wechat/mui.indexedlist.css" />
	<script src="${ctx }/js/wechat/mui.min.js"></script>
	<script src="${ctx }/js/wechat/mui.picker.js"></script>
	<script src="${ctx }/js/wechat/mui.poppicker.js"></script>
	<script src="${ctx }/js/wechat/city.data.js"></script>
	<script src="${ctx }/js/wechat/mui.indexedlist.js"></script>
</head>

<body>

	<div id="offCanvasWrapper" class="mui-off-canvas-wrap mui-slide-in">
		<div class="mui-loading" id="loading">
			<div class="loading-bg">
				<div class="mui-spinner"></div>
			</div>
		</div>
		<!-- 菜单容器 -->
		<aside id="aside" class="mui-off-canvas-right">
			<div id="offCanvasWrapper_2" class="mui-off-canvas-wrap mui-slide-in">
				<aside id="aside_2" class="mui-off-canvas-right">
					<header class="mui-bar mui-bar-nav" id="list-head">
						<a class="mui-icon mui-icon-left-nav mui-pull-left"
							id="action_back_side_2"></a>
						<h1 class="mui-title">所在地区</h1>
					</header>
					<div class="mui-content">
						<div id='list' class="mui-indexed-list">
							<div class="mui-indexed-list-search mui-input-row mui-search">
								<input type="search"
									class="mui-input-clear mui-indexed-list-search-input"
									placeholder="搜索文字或拼音首字母" maxlength="10">
							</div>
							<div class="tab-bar mui-tab-bar" id="status-bar">
								<div id="status-bar-left"></div>
								<a class="tab-active" id="status-bar-right">请选择</a>
							</div>
							<div class="mui-indexed-list-bar">
								<a>A</a> <a>B</a> <a>C</a> <a>D</a> <a>E</a> <a>F</a> <a>G</a> <a>H</a>
								<a>J</a> <a>K</a> <a>L</a> <a>M</a> <a>N</a> <a>O</a> <a>P</a> <a>Q</a>
								<a>R</a> <a>S</a> <a>T</a> <a>W</a> <a>X</a> <a>Y</a> <a>Z</a>
							</div>
							<div class="mui-indexed-list-alert"></div>
							<div class="mui-indexed-list-inner">
								<div class="mui-indexed-list-empty-alert">没有数据</div>
								<ul class="mui-table-view" id="list-area">

								</ul>
							</div>
						</div>
					</div>
				</aside>
				<div class="mui-inner-wrap">
					<div class="mui-scroll">
						<!-- 菜单具体展示内容 -->
						<ul class="mui-table-view mui-input-group">
							<li class="mui-table-view-cell side-head">
								<a class="mui-icon mui-icon-left-nav mui-pull-left" id="action_back_side"></a> 
								<span></span>
							</li>
							<li class="mui-table-view-cell mui-input-row">
								<label>收货人：</label>
								<input type="text" value="" name="receiver" placeholder="建议填写真实姓名">
							</li>
							<li class="mui-table-view-cell mui-input-row">
								<a class="mui-navigate-right" id="AreaPicker"> 
									<label>区域：</label>
									<span class="area-text" id="AreaName"></span>
								</a>
							</li>
							<li class="mui-table-view-cell mui-input-row">
								<label>地址：</label>
								<textarea name="receiver-add" placeholder="详细收货地址，如街道、楼号等" maxlength="30"></textarea>
							</li>
							<li class="mui-table-view-cell mui-input-row">
								<label>邮编：</label>
								<input type="number" value="" name="postal-code" placeholder="必填">
							</li>
							<li class="mui-table-view-cell mui-input-row">
								<label>电话：</label>
								<input type="number" value="" name="receiver-mobile" placeholder="建议填写常用的移动电话号码">
							</li>
							<input type="hidden" name="type" />
							<input type="hidden" name="add-id" />
							<input type="hidden" name="area-id" id="AreaId" />
						</ul>
					</div>
					<button class="saveEdit" id="saveEdit">保 存</button>
				</div>
			</div>
		</aside>

		<!-- 主页面容器 -->
		<div class="mui-inner-wrap">
			<header class="mui-bar mui-bar-nav not-border">
				<a class="mui-action-back mui-icon mui-icon-left-nav mui-pull-left" id="action_back"></a>
				<h1 class="mui-title">地址管理</h1>
			</header>

			<div id="main-addmanager" class="mui-scroll-wrapper">
				<div class="mui-content mui-scroll">
					<p class="tip">您没有填写过收货地址哦，点击底部按钮添加</p>
					<c:forEach items="${AddressList }" var="item">
						<div class="mui-card">
							<input type="hidden" name="add" value="${item.id }" /> 
							<input type="hidden" name="areaId" value="${item.receiverProvince },${item.receiverCity },${item.receiverExpArea },${item.receiverExpArea }" />
							<div class="mui-card-header mui-card-media">
								<span class="mui-pull-left">姓名：<b class="name">${item.receiverName }</b></span>
								<span class="mui-pull-right">手机：<b class="phone">${item.receiverMobile }</b></span>
							</div>
							<div class="mui-card-content">
								<div class="mui-card-content-inner">
									<p>地址：
										<span class="add">${item.receiverProvince} ${item.receiverCity } ${item.receiverExpArea }</span> 
										<span id="context">${item.receiverAddress }</span>
									</p>
								</div>
							</div>
							<div class="mui-card-footer mui-radio mui-left">
								<label for="r1">设为默认</label>
								<!--如果该条是默认地址，则显示checked="checked"，没设则不显示-->
								<c:choose>
									<c:when test="${item.isDef == false }">
										<input name="setAdd" type="radio" id="r1">
									</c:when>
									<c:otherwise>
										<input name="setAdd" type="radio" checked="checked" id="r1">
									</c:otherwise>
								</c:choose>

								<div class="button-group">
									<button class="editor-btn">
										<span class="mui-icon mui-icon-compose"></span> 编辑
									</button>
									<button class="delete-btn">
										<span class="mui-icon mui-icon-trash"></span> 删除
									</button>
								</div>
							</div>
						</div>
					</c:forEach>

				</div>
			</div>

			<footer class="mui-bar" id="addadd-box">
				<button class="mui-pull-right" id="goAdd">新增地址</button>
			</footer>

			<div class="mui-off-canvas-backdrop"></div>
		</div>
	</div>
	<script type="text/javascript" charset="utf-8">
			(function($, doc) {
				$.init();
				//mui初始化
				$.ready(function() {
					var unit;
					var check = false;
					var startHeight = document.body.offsetHeight;
					var levelArray = ["", "", "", ""];
					
					mui('#main-addmanager.mui-scroll-wrapper').scroll({ bounce: false });

					//侧滑容器父节点
					var offCanvasWrapper = mui('#offCanvasWrapper');
					var offCanvasWrapper_2 = mui('#offCanvasWrapper_2');

					//点击编辑按钮
					mui(document).on("tap", ".mui-card button.editor-btn", function(e) {
						unit = this.parentNode.parentNode.parentNode;
						var id = this.parentNode.parentNode.parentNode.querySelector("input[name='add']").value; //地址标识
						var areaid = this.parentNode.parentNode.parentNode.querySelector("input[name='areaId']").value; //区域标识
						var type = 0; //0是标记类型为“修改”
						mui("#aside li.side-head span")[0].innerText = "修改地址";

						//点击编辑按钮时触发ajax事件，根据用户标识和地址标识或区域ID等（根据实际情况），获得以下信息（至少要获取区域名，其他的可以直接从页面取）
						var name = this.parentNode.parentNode.parentNode.querySelector(".name").innerText;// 收货人
						var mobile = this.parentNode.parentNode.parentNode.querySelector(".phone").innerText;// 手机
						var add = this.parentNode.parentNode.parentNode.querySelector(".add").innerText;//区域名称
						var context = this.parentNode.parentNode.parentNode.querySelector("#context").innerText;//详细地址
						//var areaname = "广西 南宁 西乡塘 安吉大道"; //模拟

						mui("#aside input[name='receiver']")[0].value = name;
						mui("#aside textarea[name='receiver-add']")[0].value = context;
						mui("#aside input[name='receiver-mobile']")[0].value = mobile;
						mui("#aside input[name='add-id']")[0].value = id;
						mui("#aside input[name='area-id']")[0].value = areaid;
						mui("#aside input[name='type']")[0].value = type;
						mui("#aside span.area-text")[0].innerHTML = add;
						offCanvasWrapper.offCanvas('show');
					});

					//点击新增地址按钮
					mui(document).on("tap", "#goAdd", function(e) {
						var type = 1;//0是标记类型为“修改”
						mui("#aside li.side-head span")[0].innerText = "新增地址";
						mui("#aside input[name='receiver']")[0].value = "";
						mui("#aside textarea[name='receiver-add']")[0].value = "";
						mui("#aside input[name='receiver-mobile']")[0].value = "";
						mui("#aside input[name='add-id']")[0].value = "";
						mui("#aside input[name='area-id']")[0].value = "";
						mui("#aside span.area-text")[0].innerHTML = "<span style='color:#aaa'>点击选择区域</span>";
						mui("#aside input[name='type']")[0].value = type;	
						offCanvasWrapper.offCanvas('show');
					});

					//点击“保存”按钮保存修改
					mui(document).on("tap", "#saveEdit", function(e) {
						var _type = mui("#aside input[name='type']")[0].value;//标识是修改还是新增，0修改，1新增
						var _name = mui("#aside input[name='receiver']")[0].value;// 收货人
						var _add = mui("#aside textarea[name='receiver-add']")[0].value;// 详细地址
						var _mobile = mui("#aside input[name='receiver-mobile']")[0].value;// 电话
						var _postalCode = mui("#aside input[name='postal-code']")[0].value;// 邮编
						var _areaid = mui("#aside input[name='area-id']")[0].value;// 区域编号
						var _addid = mui("#aside input[name='add-id']")[0].value;// 地址编号
						postadd(_type,_name,_add,_mobile,_areaid,_addid, _postalCode);
					});

					//表单验证
					function formCheck() {
						var _name = mui("#aside input[name='receiver']")[0].value;
						var _add = mui("#aside textarea[name='receiver-add']")[0].value;
						var _mobile = mui("#aside input[name='receiver-mobile']")[0].value;
						var _areaid = mui("#aside input[name='area-id']")[0].value;
						var _postalCode = mui("#aside input[name='postal-code']")[0].value;// 邮编
						var mobile = /^13[0-9]{9}$|14[0-9]{9}$|15[0-9]{9}$|17[0-9]{9}$|18[0-9]{9}$/;
						var special = /[~'\:\：\"\“\”\、\‘\’\。\，\,\$\[\]\{\}\【\】^*!\！?\？\（\）()\/<>;=\\+]/g;
						if(_name != "" && _name != null && !special.test(_name)) {
							if(_name.length < 8){
								check = true;
							}else{
								mui.toast("姓名长度已超出");
								check = false;
								return false;
							}
						} else {
							mui.toast("请填写姓名，勿包含特殊字符");
							check = false;
							return false;
						};
						
						if(_areaid != "" && _areaid != null) {
							check = true;
						} else {
							mui.toast("请选择配送区域");
							check = false;
							return false;
						};
						
						if(_postalCode != "" && _postalCode != null && _postalCode != "null") {
							check = true;
						} else {
							mui.toast("请输入邮政编号");
							check = false;
							return false;
						};
						
						if(_add != "" && _add != null && _add.length < 31 && !special.test(_add)) {
							check = true;
						} else {
							mui.toast("请填写30字以内的具体地址,勿包含特殊字符");
							check = false;
							return false;
						};
						
						if(_mobile != "" && _mobile != null && mobile.test(_mobile)) {
							check = true;
						} else {
							mui.toast("请填写正确的手机号码");
							mui("#aside input[name='receiver-mobile']")[0].value = "";
							check = false;
							return false;
						};
					};

					//提交地址信息事件
					function postadd(type, name, add, mobile, areaId, addId, postalCode) {
						console.log("type==" + type);
						console.log("name==" + name);
						console.log("add==" + add);
						console.log("mobile==" + mobile);
						console.log("areaId==" + areaId);
						console.log("addId==" + addId);
						formCheck();
						if(type == 0 && check) { //如果类型是"修改"，则修改目标元素中的文字
							mui("#loading")[0].style.display="block";
							mui.confirm("设置为默认收货地址?"," ",["不了","好的"],function(e){
								if(e.index==1){
									unit.querySelector("#r1").click();
								}
								console.log(e.index);
								mui.ajax('${ctx}/wechat/user/upAddress.htm',{
									data:{
										key :'59c23bdde5603ef993cf03fe64e448f1',
										addressId : addId,
										areaIds : areaId,
										address : add,
										realName : name,
										mobile : mobile,
										postalCode : postalCode,
										isDefault : e.index
									},
									dataType:'json',//服务器返回json格式数据
									type:'post',//HTTP请求类型
									success:function(data){
										mui("#loading")[0].style.display="none";
										if(data.flag){
											//AJAX提交参数里的信息（可根据实际需要修改），成功后执行以下代码：
											mui.toast(data.message);
											offCanvasWrapper.offCanvas('close');
											unit.querySelector("b.name").innerText = name;
											unit.querySelector("b.phone").innerText = mobile;
											unit.querySelector("span.add").innerText = document.getElementById("AreaName").innerHTML;
											unit.querySelector("#context").innerText = add;
											return ;
										}
										mui.toast(data.message);
									},
									error:function(xhr,type,errorThrown){
										mui("#loading")[0].style.display="none";
										//异常处理；
										console.log(type);
									}
								});
							});
						} else if(type == 1 && check) { //如果类型是"新增"，则页面上新增一块地址
							mui("#loading")[0].style.display="block";
							
							//AJAX提交参数里的信息（可根据实际需要修改），成功后执行以下代码：
							mui.ajax('${ctx}/wechat/user/addAddress.htm',{
								data:{
									key :'59c23bdde5603ef993cf03fe64e448f1',
									areaIds : areaId,
									address : add,
									realName : name,
									postalCode : postalCode,
									mobile : mobile
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									mui("#loading")[0].style.display="none";
									if(data.flag){
										mui.toast("新增成功");
										window.location.reload();
										return ;
									}
									mui.toast(data.message);
								},
								error:function(xhr,type,errorThrown){
									mui("#loading")[0].style.display="none";
									//异常处理；
									console.log(type);
								}
							});
						};
					};

					//设置默认按钮
					mui(document).on("change", ".mui-card input[type='radio']", function(e) {
						mui("#loading")[0].style.display="block";
						console(mui("#loading")[0]);
						if(this.checked == true){
							var _addid = this.parentNode.parentNode.querySelector("input[name='add']").value;//地址标识
							if(_addid==null||_addid==""){
								mui.toast("请求参数有误");
								return ;
							}
							mui.ajax('${ctx}/wechat/user/upDefault.htm',{
								data:{
									key :'59c23bdde5603ef993cf03fe64e448f1',
									addressId : _addid
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									if(data.flag){
										//执行ajax，设置默认地址成功后提示成功：							
										mui.toast(data.message);
										mui("#loading")[0].style.display="none";
										return ;
									}
									mui.toast(data.message);
									mui("#loading")[0].style.display="none";
								},
								error:function(xhr,type,errorThrown){
									mui("#loading")[0].style.display="none";
									//异常处理；
									console.log(type);
								}
							});
						};
					});

					//收起侧栏
					mui(document).on("tap", "#action_back_side", function(e) {
						offCanvasWrapper.offCanvas('close');
					});
					mui(document).on("tap", "#action_back_side_2", function(e) {
						offCanvasWrapper_2.offCanvas('close');
					});

					//INPUT焦点BUG
					document.addEventListener('tap', function(e) {
						var target = e.target;
						if(!(target.tagName && (target.tagName === 'TEXTAREA' || (target.tagName === 'INPUT' && (target.type === 'text' || target.type === 'search' || target.type === 'number'))))) {
							blur();
						}
					});

					function blur() {
						var inputs = document.querySelectorAll("input");
						var textareas = document.querySelectorAll("textarea");
						for(var i = 0; i < inputs.length; i++) {
							inputs[i].blur();
						}
						for(var i = 0; i < textareas.length; i++) {
							textareas[i].blur();
						};
					};

					//删除按钮
					mui(document).on("tap", ".mui-card button.delete-btn", function(e) {
						var id = this.parentNode.parentNode.parentNode.querySelector("input[name='add']").value;//地址标识
						var elem = this;
						mui.confirm("是否确定删除？"," ",["确定","取消"],function(e){
							if(e.index==0){
								//执行ajax，删除成功后提示成功并移除页面元素
								mui.ajax('${ctx}/wechat/user/delAddress.htm',{
								data:{
									key :'59c23bdde5603ef993cf03fe64e448f1',
									addressId : id
								},
								dataType:'json',//服务器返回json格式数据
								type:'post',//HTTP请求类型
								success:function(data){
									if(data.flag){
										//执行ajax，设置默认地址成功后提示成功：							
										mui.toast(data.message);
										elem.parentNode.parentNode.parentNode.remove();		
										return ;
									}
									mui.toast(data.message);
								},
								error:function(xhr,type,errorThrown){
									//异常处理；
									console.log(type);
								}
							});
							}
						});
					});

					//区域选择
					var areaPicked = doc.getElementById('AreaName');
					var areaId = doc.getElementById('AreaId');
					var levelText;
					areaPicked.addEventListener('tap', function(event) {
						offCanvasWrapper_2.offCanvas('show');
						var header = document.querySelector('header#list-head');
						var list = document.getElementById('list');
						list.style.height = (startHeight - header.offsetHeight) + 'px';
						getAddList(0);
					});

					mui('.mui-indexed-list-inner').on('tap', 'li.mui-indexed-list-item', function() {
						var addId = this.getAttribute("data-id");
						var index = addId .lastIndexOf(",");  
						var str  = addId .substring(index + 1, addId.length);
						var level = this.getAttribute("data-level");
						var addName = this.innerText;
						var index = level - 1;
						if(str.length < 12) {
							getAddList(level, addId, addName);
							levelArray[index] = this.innerText;
						} else {
							var addId = this.getAttribute("data-id");
							levelArray[index] = this.innerText;
							levelText = String(levelArray).replace(/,/g, " ");
							offCanvasWrapper_2.offCanvas('close');
							areaId.value = levelArray;
							areaPicked.innerHTML = levelText;
						};
					});

					mui('#status-bar-left').on('tap', 'a', function() {
						var addId = this.getAttribute("data-id");
						var index = addId.lastIndexOf(",");  
						var str  = addId.substring(index + 1, addId.length - 3);
						var level = this.getAttribute("data-level");
						var active = document.getElementById('status-bar-right');
						if(str.length < 12) {
							getAddList(parseInt(level) - parseInt(1), str);
						};
						switch(level) {
							case "1":
								active.innerText = "请选择";
								break;
							case "2":
								active.innerText = "请选择";
								break;
							case "3":
								active.innerText = "请选择";
								break;
							default:
								active.innerText = "请选择";
								break;
						};
					});

					mui(document).on("tap", '#status-bar-left a', function() {
						var index = this.getAttribute("data-level");
						var li = mui('#status-bar-left a');
						for(var i = 0; i < li.length; i++) {
							if(li[i].getAttribute("data-level") >= index) {
								li[i].parentNode.removeChild(li[i]);
							}
						};
					});

				});
			})(mui, document);

			// 区域选择
			function getAddList(level, id, name) {
				if(level == 0) {
					mui("#status-bar-left")[0].innerHTML = "";
					mui("#list-area")[0].innerHTML = "";
					mui('#status-bar-right')[0].innerHTML = "请选择";
				};
				mui("#loading")[0].style.display = "inline-block";
				mui.ajax("${ctx }/wechat/shop/effectiveArea.htm", {
					data: {
						"id" : id, // 所点击条目的编号
						"level" : level, //索引层级（省1、市2、区域3、街道4）
						"key" : "59c23bdde5603ef993cf03fe64e448f1"
					},
					dataType: 'json', //应为JSON
					type: 'post', //根据需要post
					success: function(data) {
						var html = "";
						//-------------------------------------------------------
						//上面这个if判断只是测试用，实际开发用不到，你只要每当点击一个条目，
						//后台根据发送的参数，返回相应的数据就行了，比如在省级列表点击某个条目，
						//后台就返回相应的市级条目的数据，依此类推...
						//当前条目的层级，写在data-level这个属性里（省=1、市=2、区域=3、街道=4），所需要的编号写在data-id属性里（见下面的html），
						//编号的属性看实际需要可自行更改，但data-level一定要有，如果没有的话...那好多地方要重写..
						//-------------------------------------------------------
						mui.each(data.sort, function(index, value){
							html += '<li data-group="' + value + '" class="mui-table-view-divider mui-indexed-list-group">' + value + '</li>'
							mui.each(data.message,function(index,item){
								if(value == item.flag){
									html += '<li data-level="' + item.level + '" data-value="' + item.flag + '" data-id="' + item.id + '" class="mui-table-view-cell mui-indexed-list-item">'
									html += item.name
								}
							});
							html += '</li>'
						});

						mui("#loading")[0].style.display = "none";
						mui("#list-area")[0].innerHTML = html;

						//create 初始化索引
						mui("#list").indexedList().findElements();
						mui("#list").indexedList().search();

						//标签状态
						if(name != null) {
							var newItem = document.createElement("a")
							var parent = document.querySelector('#status-bar-left');
							var active = document.getElementById('status-bar-right');
							newItem.innerHTML = name;
							parent.appendChild(newItem);
							newItem.setAttribute("data-level", level);
							newItem.setAttribute("data-id", id);
							switch(level) {
								case "1":
									active.innerText = "请选择";
									break;
								case "2":
									active.innerText = "请选择";
									break;
								case "3":
									active.innerText = "请选择";
									break;
								default:
									active.innerText = "请选择";
									break;
							};
						}
					},
					error: function(xhr, type, errorThrown) {
						console.log(type);
					}
				})
			};
		</script>

</body>

</html>