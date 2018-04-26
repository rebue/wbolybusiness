<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/htm/wechat/global/global.jsp" />
<link rel="stylesheet" href="${ctx }/css/wechat/goods.css">

<div class="mui-content mui-row mui-fullscreen">
	<div class="goods-list-left mui-col-xs-3">
		<!--一级导航开始-->
		<div id="segmentedControls" class="mui-segmented-control mui-segmented-control-inverted mui-segmented-control-vertical"></div>
		<!--一级导航结束-->
	</div>

	<div id="segmentedControlContents" class="mui-col-xs-9">
		<!--详细导航开始-->
		<div id="content" class="mui-control-content">
			<ul class="mui-table-view" id="category"></ul>
		</div>
		<!--详细导航结束-->
	</div>
</div>

<script type="text/javascript" charset="utf-8"> 
	(function($, doc) {	
		mui.init({
			//预加载
			 preloadPages:[
		 	 ]
		});
		$.ready(function() {
			openWindow();
			loadParentMenu();
			LoadCartNum();
			
			// 搜索事件
			form.onsubmit = function() {
				document.getElementById("form").submit();
			    return false;
			};
			
			mui("#segmentedControls").on('tap','a',function(){
			  var id = this.getAttribute("data-id");	
			  	AjaxGetData(id);
			  	setCookie("TAKE", id);
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
			
	// 初始化
	function initPopPicker(){
		 areaPicker = new mui.PopPicker({
			layer: 4
		});
	 	allShopData();
	}
			
	function openWindow(){
		
		initPopPicker();
		
		var showAreaPickerButton = document.getElementById('AreaPicker');
		setShopData(1);
		
		mui.ajax('${ctx}/wechat/shop/getShopByRequest.htm',{
			data:{
				key : '59c23bdde5603ef993cf03fe64e448f1'
			},
			dataType:'json',//服务器返回json格式数据
			type:'post',//HTTP请求类型
			timeout:10000,//超时时间设置为10秒；
			success:function(data){
				document.getElementById('AreaName').innerHTML=data.shopName;
				var shop = document.getElementById('shopId').innerHTML=data.shopId;
				if(shop != null && shop!=0){
					mui.trigger(segmentedControls,'tap');
				}else{
					mui.trigger(showAreaPickerButton,'tap');
				}
			},
			error:function(xhr,type,errorThrown){
				//异常处理；
				console.log(type);
			}
		});
	}
			
	/**
	* 保存门店数据
	*/ 
	function setShopData(shopId){
		console.log(shopId);
		mui.ajax('${ctx}/wechat/shop/saveInSeesion.htm',{
			data:{
				shopId:shopId,
				key:'59c23bdde5603ef993cf03fe64e448f1'
			},
			dataType:'json',//服务器返回json格式数据
			type:'post',//HTTP请求类型
			timeout:10000,//超时时间设置为10秒；
			success:function(data){
				loadParentMenu();
			},
			error:function(xhr,type,errorThrown){
				//异常处理；
				console.log(type+":"+errorThrown);
			}
		});
	}

	// 加载父菜单
	function loadParentMenu(){
		mui.ajax('${ctx}/wechat/goods/loadParentMenu.htm',{
			data : {
				key : '59c23bdde5603ef993cf03fe64e448f1'
			},
			dataType:'json',//服务器返回json格式数据
			type:'post',//HTTP请求类型
			success:function(data){
				document.getElementById("segmentedControls").innerHTML="";
				var html = '';
				mui.each(data,function(index,items){
					html += ' <a class="mui-control-item" href="#content" data-id="'+items.classId+'">'+items.name+'</a> ';
					if(getCookie("TAKE") ==null && data.length >0 && index <1){
						AjaxGetData(items.classId);
						setCookie("TAKE", items.classId);
					}else if(getCookie("TAKE") != null){
						AjaxGetData(getCookie("TAKE"));
					}
				});
				document.getElementById("segmentedControls").innerHTML=html;
				if(html !=''){
					var item = document.querySelectorAll(".mui-control-item");
					for(var i=0;i<item.length;i++){
						item[i].classList.remove("mui-active");
						if(item[i].getAttribute("data-id") == getCookie("TAKE")){
							item[i].classList.add("mui-active");
						}else if(getCookie("TAKE")==null){
							item[0].classList.add("mui-active");
						};
					};
				};
			},
			error:function(xhr,type,errorThrown){
				//异常处理；
				console.log(type+":"+errorThrown);
			}
		});
	}
			
	//ajax载入二三级分类
	function AjaxGetData(id){
		document.getElementById("category").innerHTML="<a class='loading'><span class='mui-spinner'></span></a>";				
		mui.ajax('${ctx}/wechat/goods/loadSonMenu.htm',{ //测试用,模拟数据
			data:{
				key : '59c23bdde5603ef993cf03fe64e448f1',
				classId : id
			},
			dataType:'json',//测试用，正式应为JSON
			type:'post',       
			success:function(data){
				var html = "";
				if(data==null){
					mui.toast("无法获取到数据");
					return ;
				}
				var b = new Base64();
				 for(var i=0;i<data.length;i++){
					html+='<li class="mui-table-view-cell">'
					html+='<h4><em>'+data[i].name+'</em></h4>'
					html+='<ul class="sub-goods-list mui-table-view mui-grid-view mui-grid-9">'
					for(var j=0;j<data[i].nodes.length;j++){
						html+='<li class="mui-table-view-cell mui-media mui-col-xs-4 mui-col-sm-4">'
						if(id == data[i].classId){
							html+='<a href="${ctx}/wechat/class/goods/'+id+'.htm">'
						}else if(data[i].classId == data[i].nodes[j].classId){
							html+='<a href="${ctx}/wechat/class/goods/'+id+"_"+data[i].classId+'.htm">'
						}else{
							html+='<a href="${ctx}/wechat/class/goods/'+id+"_"+data[i].classId+"_"+data[i].nodes[j].classId+'.htm">'
						}
						html+='<span class="wboly-icon">'
						if(data[i].nodes[j].icon !=null && data[i].nodes[j].icon != ''){
							html+='<img src="http://img.wboly.com/goodsclass/'+data[i].nodes[j].icon+'_100_100.jpg" alt="" />'
						}else{
							html+='<img src="${ctx}/images/wechat/empty_icon.png" alt="" />'
						}
						html+='</span>'
						html+='<div class="mui-media-body">'+data[i].nodes[j].name+'</div>'
						html+='</a>'
						html+='</li>'
					}
					html+='</ul>'
					html+='</li>'
				}; 
				setTimeout(function(){document.getElementById("category").innerHTML= html},200);												
			},
			error:function(xhr,type,errorThrown){
				console.log(type+":"+errorThrown);
			}
		}); 
	}
</script>
</body>
</html>