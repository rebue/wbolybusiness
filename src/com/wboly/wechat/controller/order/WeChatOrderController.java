package com.wboly.wechat.controller.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.rpc.Client.OrderRPCClient;
import com.wboly.rpc.entity.AppraiseEntity;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.Base64EnOut;
import com.wboly.system.sys.util.JsonUtil;
import rebue.wheel.NetUtils;
import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 订单.java
 * @Author: nick
 */
@Controller
public class WeChatOrderController extends SysController {

	/**
	 * @Name: 所有订单售后页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/allAfterSalePage")
	public ModelAndView orderAfterSalePage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			mav.setViewName("/htm/wechat/order/orderAfterSale");
		}
		return mav;
	}

	/**
	 * @Name: 用户申请售后提交
	 * @Author: nick
	 */
	//@RequestMapping(value = "/wechat/order/afterSaleApply")
	public void ApplyAftermarket1(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		String activityId = request.getParameter("activityId");
		String appealType = request.getParameter("appealType");
		String buyerContact = request.getParameter("buyerContact");
		String img = request.getParameter("img");
		if (activityId == null || "".equals(activityId) || appealType == null || "".equals(appealType)
				|| entity.getOrderIds() == null || entity.getOrderIds().length() == 0 || img == null || "".equals(img)
				|| buyerContact == null || "".equals(buyerContact)) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		String userName = SysCache.getWeChatUserByColumn(request, "userName");
		entity.setUserName(userName);
		entity.setBuyerUid(userId);

		Map<String, String> map = new HashMap<String, String>();
		map.put("activityId", activityId);
		map.put("appealType", appealType);
		map.put("buyerContact", buyerContact);
		map.put("img", img);
		OrderRPCClient orderService = new OrderRPCClient();
		int i = orderService.client.ApplyAftermarket(entity, map);
		orderService.close();
		if (i == -3) {// 当前用户还有未处理完的售后
			this.render(response, "{\"message\":\"有未处理完成的收货\",\"flag\":false}");
			return;
		}
		if (i == -2) {// 订单当前状态不允许申请售后
			this.render(response, "{\"message\":\"当前状态不能售后\",\"flag\":false}");
			return;
		}
		if (i == -1) {// 添加失败
			this.render(response, "{\"message\":\"提交失败\",\"flag\":false}");
			return;
		}
		if (i == -4) {// 更新订单状态失败
			this.render(response, "{\"message\":\"更新状态失败\",\"flag\":false}");
			return;
		}
		if (i == -5) {// 当前门店的活动已申请过售后
			this.render(response, "{\"message\":\"无需重复申请售后\",\"flag\":false}");
			return;
		}
		if (i > 0) {// 成功提交
			this.render(response, "{\"message\":\"成功提交<br/>请耐心等待结果\",\"flag\":true}");
			return;
		}

		this.render(response, "{\"message\":\"无法提交\",\"flag\":false}");

	}

	/**
	 * @Name: 申请退货页面跳转
	 * @Author: nick
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/wechat/order/returnPage")
	public ModelAndView returnPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			String returnData = request.getParameter("returnData");
			if (returnData != null) {
				Base64EnOut b64 = new Base64EnOut();
				returnData = b64.Decode(returnData);
				returnData = b64.Decode(returnData);
				System.out.println(returnData);
				mav.addObject("returnData", JsonUtil.jsonStringToMap(returnData));
			} else {
				mav.addObject("returnData", "");
			}
			mav.setViewName("/htm/wechat/order/returnGoods");
		}
		return mav;
	}
	
	/**
	 * @throws IOException 
	 * @Name: 查看物流信息页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/queryLogistics")
	public ModelAndView queryLogistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
		new ObjectMapper();
		
		String orderInfo = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info?orderCode="+request.getParameter("orderId"));
		List<Map<String, Object>> list = JsonUtil.listMaps(orderInfo);
		System.out.println(String.valueOf(list));
		String shipperCode = String.valueOf(list.get(0).get("shipperCode"));
		String logisticCode = String.valueOf(list.get(0).get("logisticCode"));
		ModelAndView mav = new ModelAndView();
		mav.addObject("shipperCode", shipperCode);
		mav.addObject("logisticCode", logisticCode);
		mav.setViewName("/htm/wechat/order/queryLogistics");
		return mav;
	}
	
	/**
	 * @throws IOException 
	 * @Name: 买家部分退货提交
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/returnGoods")
	public void ReturnPartofGoods(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> map) throws TException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"msg\":\"您没有登录\",\"result\":-11}");
			return;
		}
		
		map.put("userId", userId);
		System.err.println("添加用户退货信息的参数为：" + String.valueOf(map));
		// 添加用户退货信息
		String results = OkhttpUtils.postByFormParams(SysContext.ORDERURL + "/ord/return", map);
		System.err.println("添加用户退货信息的返回值为：" + results);
		if (results == null || results.equals("") || results.equals("[]")) {
			this.render(response, "{\"msg\":\"提交失败\",\"result\":-10}");
			return ;
		}
		this.render(response, results);
	}

	/**
	 * @Name: 买家评价商品
	 * @Author: nick
	 */
	//@RequestMapping(value = "/wechat/order/appraiseGoods")
	public void AppraiseOrder(AppraiseEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		entity.setBuyerUid(userId);

		String activityId = request.getParameter("activityId");
		String babyEvaluation = request.getParameter("babyEvaluation");
		String orderId = request.getParameter("orderId");
		String userName = SysCache.getWeChatUserByColumn(request, "userName");

		if (orderId == null || "".equals(orderId) || activityId == null || "".equals(activityId)
				|| babyEvaluation == null || "".equals(babyEvaluation)) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("activityId", activityId);
		map.put("babyEvaluation", babyEvaluation);
		map.put("userId", userId);
		map.put("orderId", orderId);
		map.put("userName", userName);
		OrderRPCClient orderService = new OrderRPCClient();
		int i = orderService.client.AppraiseOrder(entity, map);
		orderService.close();
		if (i == -3) {// 买家没有在此店面购买此活动商品
			this.render(response, "{\"message\":\"未购买该商品<br/>无法评价\",\"flag\":false}");
			return;
		}
		if (i == -2) {// 当前门店的活动买家已追加评价
			this.render(response, "{\"message\":\"无需重复追加评价\",\"flag\":false}");
			return;
		}
		if (i == -1) {// 评价失败
			this.render(response, "{\"message\":\"评价失败\",\"flag\":false}");
			return;
		}
		if (i >= 0) {// 评价成功
			this.render(response, "{\"message\":\"评价成功\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"无法评价\",\"flag\":true}");
	}

	/**
	 * @Name: 订单详情页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/myOrderDetail")
	public ModelAndView orderDetailPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("order_data",
				request.getParameter("order_data") != null ? request.getParameter("order_data") : 0);
		mav.setViewName("/htm/wechat/order/orderDetail");
		return mav;
	}

	/**
	 * @Name: 我的订单页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/myOrders")
	public ModelAndView ordersPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {

			String type = request.getParameter("type");
			if (type != null && !type.equals("")) {
				switch (type) {
				case "stayPay":
					mav.addObject("OrderType", 1);
					mav.addObject("allOrder", "");
					mav.addObject("stayTake", "");
					mav.addObject("stayReturn", "");
					mav.addObject("stayPay", "mui-active");
					break;
				case "stayTake":
					mav.addObject("OrderType", 2);
					mav.addObject("allOrder", "");
					mav.addObject("stayTake", "mui-active");
					mav.addObject("stayReturn", "");
					mav.addObject("stayPay", "");
					break;
				case "stayReturn":
					mav.addObject("OrderType", 3);
					mav.addObject("allOrder", "");
					mav.addObject("stayTake", "");
					mav.addObject("stayReturn", "mui-active");
					mav.addObject("stayPay", "");
					break;
				}
			} else {
				mav.addObject("OrderType", 0);
				mav.addObject("allOrder", "mui-active");
				mav.addObject("stayTake", "");
				mav.addObject("stayReturn", "");
				mav.addObject("stayPay", "");
			}

			mav.setViewName("/htm/wechat/order/orders");
		}
		return mav;
	}

	/**
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 * @Name: 用户放弃订单
	 * @Author: nick
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/wechat/order/cancelOrder")
	public void CancelShopOrder(HttpServletRequest request, HttpServletResponse response) throws TException, JsonParseException, JsonMappingException, IOException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		String orderId = request.getParameter("orderId");
		String cancelType = request.getParameter("cancelType");
		if (orderId == null || orderId.equals("") || cancelType == null || cancelType.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", orderId);
		map.put("userId", userId);
		map.put("cancelReason", "买家取消订单");
		map.put("cancelingOrderOpId", userId);
		System.out.println("取消订单的参数为：{}" + String.valueOf(map));
		// 取消订单
		String results = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/order/cancel", map);
		System.out.println("取消订单的返回值为：" + results);
		ObjectMapper mapper = new ObjectMapper();
		Map resultMap = mapper.readValue(results, Map.class);
		int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
		if (result > 0) {
			this.render(response, "{\"message\":\"订单已取消\",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
		}
	}

	/**
	 * @Name: 用户订单列表
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/getOrders")
	public void UserOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		// 开始条数
		String start = request.getParameter("start");
		// 结束条数
		String limit = request.getParameter("limit");
		// 订单状态
		String orderState = request.getParameter("orderState");
		Map<String, Object> map = new HashMap<String, Object>();
		if (!orderState.equals("") && orderState != null && !orderState.equals("null") && !orderState.equals("0")) {
			map.put("orderState", orderState);
		}
		map.put("start", start);
		map.put("limit", limit);
		map.put("userId", userId);
		System.out.println("获取用户订单的参数为：" + String.valueOf(map));
		String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", map);
		System.out.println("获取用户订单的返回值为：" + results);
		this.render(response, "{\"message\":" + results + ",\"flag\":true}");
	}

	/**
	 * @Name: 检查订单页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/checkorderpage")
	public ModelAndView checkOrder(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/htm/wechat/order/checkOrder");
		return mav;
	}

	/**
	 * @throws IOException 
	 * @Name: 生成门店订单
	 * @Author: nick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/wechat/order/createOrder")
	public void CreateShopOrder(HttpServletRequest request, HttpServletResponse response) throws TException, IOException {
		// 当前用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		// 当前用户名称
		String userName = SysCache.getWeChatUserByColumn(request, "userName");
		String jsonData = request.getParameter("jsonData");
		String goodsInfo = request.getParameter("goodsInfo");
		System.out.println("生成订单的商品信息=====" + goodsInfo);
		System.out.println("生成订单的总金额、总返现金、总数量、收货地址编号=====" +jsonData);
		List<Map<String, Object>> list = JsonUtil.listMaps(jsonData);
		// 将goodsInfo转为List<Map>
		List<Map<String, Object>> goodsList = JsonUtil.listMaps(goodsInfo);
		goodsList.get(0).put("totalPrice", list.get(0).get("totalPrice"));
		goodsList.get(0).put("totalBack", list.get(0).get("totalBack"));
		goodsList.get(0).put("totalNumber", list.get(0).get("totalNumber"));
		goodsList.get(0).put("address", list.get(0).get("address"));
		goodsList.get(0).put("userId", userId);
		goodsList.get(0).put("userName", userName);
		goodsList.get(0).put("orderMessages", list.get(0).get("orderMessages"));
		ObjectMapper mapper = new ObjectMapper();
		String goodsJson = mapper.writeValueAsString(goodsList);
		System.err.println("用户下订单的参数为=====" + goodsJson);
		String results = OkhttpUtils.post(SysContext.ORDERURL + "/ord/order?orderJson=" + java.net.URLEncoder.encode(goodsJson, "UTF-8"));
		System.err.println("用户下订单返回值为：" + results);
		if (results != null && !results.equals("") && !results.equals("null") && !results.equals("{}")) {
			Map resultMap = mapper.readValue(results, Map.class);
			int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
			if (result > 0) {
				this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"orderId\":\"" + resultMap.get("orderId") + "\",\"flag\":true}");
			} else {
				this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
			}
		} else {
			this.render(response, "{\"message\":\"下单失败\",\"flag\":false}");
		}
	}
	
	/**
	 * 买家确认收货
	 * Title: aboutCinfirmReceipt
	 * Description: 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @date 2018年4月14日 下午1:37:41
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/wechat/order/aboutCinfirmReceipt")
	public void aboutCinfirmReceipt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 当前用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 订单编号
			String orderCode = request.getParameter("orderId");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("orderCode", orderCode);
			map.put("receivedOpId", userId);
			map.put("mac", NetUtils.getFirstMacAddrOfLocalHost());
			map.put("ip", NetUtils.getFirstIpOfLocalHost());
			System.err.println("用户订单签收的参数为：" + String.valueOf(map));
			String signInResult = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/order/ordersignin", map);
			System.err.println("用户订单签收的返回值为：" + signInResult);
			ObjectMapper mapper = new ObjectMapper();
			Map resultMap = mapper.readValue(signInResult, Map.class);
			int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
			if (result != 1) {
				this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
			} else {
				this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":true}");
			}
		} else {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
		}
	}
	
	/**
	 * @Name: 用户待返现订单列表
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/order/getCashBackOrders")
	public void getCashBackOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		// 开始条数
		String pageNum = request.getParameter("pageNum");
		// 结束条数
		String pageSize = request.getParameter("pageSize");
		// 订单状态
		String orderState = request.getParameter("orderState");
		Map<String, Object> map = new HashMap<String, Object>();
		if (!orderState.equals("") && orderState != null && !orderState.equals("null") && !orderState.equals("0")) {
			map.put("orderState", orderState);
		}
		map.put("pageNum", pageNum);
		map.put("pageSize", pageSize);
		map.put("userId", userId);
		System.out.println("获取用户订单的参数为：" + String.valueOf(map));
		String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/getCashBackOrders", map);
		//String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", map);
		System.out.println("获取用户订单的返回值为：" + results);
		this.render(response, "{\"message\":" + results + ",\"flag\":true}");
	}
	
}
