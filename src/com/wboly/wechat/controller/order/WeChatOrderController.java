package com.wboly.wechat.controller.order;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.wboly.wechat.service.order.WeChatOrderService;
import com.wboly.wechat.service.shop.WeChatShopService;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 订单.java
 * @Author: nick
 */
@Controller
public class WeChatOrderController extends SysController {

	@Autowired
	private WeChatOrderService weChatOrderService;
	@Autowired
	private WeChatShopService weChatShopService;

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
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/wechat/order/queryLogistics")
	public ModelAndView queryLogistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		
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
	public void ReturnPartofGoods(HttpServletRequest request, HttpServletResponse response) throws TException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"msg\":\"您没有登录\",\"result\":-11}");
			return;
		}
		// 订单编号
		String orderCode = request.getParameter("orderCode").trim();
		// 上线编号
		String onlineId = request.getParameter("onlineId").trim();
		// 订单详情编号
		String orderDetailId = request.getParameter("orderDetailId");
		// 退货数量
		String returnNum = request.getParameter("returnNum");
		// 退货原因
		String returnReason = request.getParameter("returnReason").trim();
		// 退货图片
		String returnImg = request.getParameter("returnImg");
		// 退货金额
		String returnPrice = request.getParameter("returnPrice");
		// 规格名称
		String specName = request.getParameter("specName");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderCode", orderCode);
		map.put("onlineId", onlineId);
		map.put("orderDetailId", orderDetailId);
		map.put("returnNum", returnNum);
		map.put("returnReason", returnReason);
		map.put("returnImg", returnImg);
		map.put("userId", userId);
		map.put("returnPrice", returnPrice);
		map.put("specName", specName);
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
		map.put("orderCode", orderId);
		map.put("userId", userId);
		map.put("cancelReason", "买家取消订单");
		map.put("cancelingOrderOpId", userId);
		System.out.println("取消订单的参数为：{}" + String.valueOf(map));
		// 取消订单
		String results = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "ord/order/cancel", map);
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
		Map<String, String> map = new HashMap<String, String>();
		if (!orderState.equals("") && orderState != null && !orderState.equals("null") && !orderState.equals("0")) {
			map.put("orderState", orderState);
		}
		map.put("start", start);
		map.put("limit", limit);
		map.put("userId", userId);
		System.out.println("获取用户订单的参数为：" + String.valueOf(map));
		String results = OkhttpUtils.get(SysContext.ORDERURL + "ord/order/info", map);
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
		ObjectMapper mapper = new ObjectMapper();
		String goodsJson = mapper.writeValueAsString(goodsList);
		System.err.println("用户下订单的参数为=====" + goodsJson);
		String results = OkhttpUtils.post(SysContext.ORDERURL + "ord/order?orderJson=" + java.net.URLEncoder.encode(goodsJson, "UTF-8"));
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
			System.err.println("用户订单签收的参数为：" + String.valueOf(map));
			String signInResult = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "ord/order/ordersignin", map);
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
	 * 更新门店订单信息 Title: updateOrderNo Description:
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping("/web/order/updateorderno")
	@ResponseBody
	public String updateOrderNo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.err.println("=============更新订单开始=============");
		// 订单编号
		String orderId = request.getParameter("orderId");
		// 支付总金额
		String payAmount = request.getParameter("payAmount");
		// 判断传过来的订单号是否为空
		if (orderId == null || orderId.equals("") || orderId.equals("null")) {
			System.err.println("需要更新的订单为空！！！！！！！！！");
			return "no";
		}

		// 获取当前时间戳
		long currentTime = System.currentTimeMillis() / 1000;

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("dateline", currentTime);
		map.put("orderId", orderId);

		// 获取门店编号、消费用户编号
		List<Map<String, Object>> shopAndBuyerList = weChatOrderService.selectShopAndBuyer(map);

		// 判断传过来的订单号是否为真实的订单号
		if (shopAndBuyerList.size() == 0) {
			System.err.println("vbl_shop_order找不到该订单！！！！！！！！！");
			return "no";
		}
		// 快递费
		String courierFee = String.valueOf(shopAndBuyerList.get(0).get("courierFee"));
		// 订单总金额
		//String totalMoney = String.valueOf(shopAndBuyerList.get(0).get("money"));

		/*// 判断支付总金额和订单总金额是否一致
		if (!totalMoney.equals(payAmount)) {
			System.err.println("支付总金额和订单总金额不一致！！！！！！！！！");
			return "no";
		}*/
		map.put("buyerUid", shopAndBuyerList.get(0).get("buyerUid"));// 消费者编号
		map.put("courierFee", courierFee);
		map.put("shopId", shopAndBuyerList.get(0).get("shopId"));
		map.put("payType", shopAndBuyerList.get(0).get("payType"));// 支付类型
		map.put("describe", "您的订单" + orderId + "已经成功付款" + payAmount + "元，宝贝正使用洪荒之力飞向您的怀抱，请耐心等待。");// 推送信息
		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 根据门店查询门店信息
		List<Map<String, Object>> shopList = weChatShopService.selectAllShop(map);
		// 判断输入的门店是否为存在
		if (shopList.size() == 0) {
			System.err.println("没有该门店！！！！！！！！！");
			return "no";
		}

		map.put("userid", shopList.get(0).get("userid"));// 加盟商编号
		map.put("userName", shopList.get(0).get("userName"));// 加盟商名称
		map.put("pushContent", "支付成功");
		
		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 更新门店交易订单支付方式、状态
		int shopOrderStatusResult = weChatOrderService.updateShopOrderStatus(map);
		System.err.println("更新门店交易订单支付方式状态返回值为：" + shopOrderStatusResult);
		// 判断更新门店交易订单支付方式、状态是否成功
		if (shopOrderStatusResult < 0) {
			System.err.println("更新门店交易订单支付方式、状态失败！！！！！！！！！");
			return "no";
		}
		
		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 添加信息到门店交易订单付款信息表
		int shopTransactionPaymentInfoResult = weChatOrderService.insertShopTransactionPaymentInfo(map);
		System.err.println("添加门店交易付款信息表返回值为：" + shopTransactionPaymentInfoResult);
		// 判断添加信息到门店交易订单付款信息表是否成功
		if (shopTransactionPaymentInfoResult < 1) {
			System.err.println("添加信息到门店交易订单付款信息表失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 添加门店交易订单状态跟踪信息
		int vblShopOrderTradeRecordResult = weChatOrderService.insertVblShopOrderTradeRecord(map);
		System.err.println("添加门店交易订单状态跟踪信息返回值为：" + vblShopOrderTradeRecordResult);
		// 判断添加门店交易订单状态跟踪信息是否成功
		if (vblShopOrderTradeRecordResult < 1) {
			System.err.println("添加门店交易订单状态跟踪信息失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 判断是否有快递费
		if (!courierFee.equals("0") && !courierFee.equals("null") && !courierFee.equals("") && courierFee != null) {
			// 添加快递费（如果有）
			int expressFeeVblShopOrderPlantorefundResult = weChatOrderService
					.insertExpressFeeVblShopOrderPlantorefund(map);
			System.err.println("添加快递费返回值为： " + expressFeeVblShopOrderPlantorefundResult);
			// 判断添加快递费（如果有）是否成功
			if (expressFeeVblShopOrderPlantorefundResult < 1) {
				System.err.println("添加快递费（如果有）失败！！！！！！！！！");
				return "no";
			}
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 添加推送消息计划
		int vblPushPlanResult = weChatOrderService.insertVblPushPlan(map);
		System.out.println("添加推送消息计划返回值为：" + vblPushPlanResult);
		// 判断添加推送消息计划是否成功
		if (vblPushPlanResult < 1) {
			System.err.println("添加推送消息计划失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 更新vbl_goods销量
		int vblGoodsSalesResult = weChatOrderService.updateVblGoodsSales(map);
		System.err.println("更新vbl_goods销量返回值为：" + vblGoodsSalesResult);
		// 判断更新vbl_goods销量是否成功
		if (vblGoodsSalesResult < 1) {
			System.err.println("更新vbl_goods销量失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 更新vbl_goods_stat销量
		int vblGoodsStatSalesResult = weChatOrderService.updateVblGoodsStatSales(map);
		System.err.println("更新vbl_goods_stat销量返回值为：" + vblGoodsStatSalesResult);
		// 判断更新vbl_goods_stat销量是否成功
		if (vblGoodsStatSalesResult < 1) {
			System.err.println("更新vbl_goods_stat销量失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 更新vbl_activity销量
		int vblActivitySalesResult = weChatOrderService.updateVblActivitySales(map);
		System.err.println("更新vbl_activity销量返回值为：" + vblActivitySalesResult);
		// 判断更新vbl_activity销量是否成功
		if (vblActivitySalesResult < 1) {
			System.err.println("更新vbl_activity销量失败！！！！！！！！！");
			return "no";
		}

		System.err.println("---------------------------------");
		System.err.println(map.toString());
		// 更新vbl_activity_off销量
		int vblActivityOffResult = weChatOrderService.updateVblActivityOff(map);
		System.err.println("更新vbl_activity_off销量返回值为：" + vblActivityOffResult);
		// 判断更新vbl_activity_off销量是否成功
		if (vblActivityOffResult < 1) {
			System.err.println("更新vbl_activity_off销量失败！！！！！！！！！");
			return "no";
		}

		System.err.println("=============更新订单成功=============");
		return "yes";
	}
}
