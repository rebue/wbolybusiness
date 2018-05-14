package com.wboly.wechat.controller.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.modules.controller.Util.MacAddressUtil;
import com.wboly.modules.service.order.VblOrderService;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.Base64EnOut;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.wx.UnifyDowdUtil;
import com.wboly.system.sys.util.wx.WXSignUtils;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;
import com.wboly.system.sys.util.wx.WxConfig;
import com.wboly.wechat.dao.order.WeChatOrderMapper;
import com.wboly.wechat.service.order.WeChatOrderService;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 商超支付
 * @Author: nick
 */
@Controller
public class WeChatPayController extends SysController {

	@Autowired
	private WeChatOrderService weChatOrderService;

	@Autowired
	private VblOrderService vblorderService;
	
	@Autowired
	private WeChatOrderMapper weChatOrderMapper;

	/**
	 * @Name: 微信支付授权，先获取 code，跳转 url 通过 code 获取 openId
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/pay/authpay", params = { "orderId" })
	public String userAuthPay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("orderId") String orderId) {
		try {
			System.err.println("-----------------------开始进入微信支付授权方法---------------------------");

			// 授权后要跳转的链接
			String backUri = WxConfig.onLineURL + "/wechat/pay/toPay.htm";
			backUri = backUri + "?orderId=" + orderId;
			// URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
			backUri = URLEncoder.encode(backUri, "UTF-8");
			// scope 参数视各自需求而定，这里用 scope=snsapi_base
			// 不弹出授权页面直接授权目的只获取统一支付接口的openid
			String url = SITE.AUTHORIZE.getMessage() + "?appid=" + WxConfig.appid + "&redirect_uri=" + backUri
					+ "&response_type=code&scope=snsapi_base&state=" + orderId + "#wechat_redirect";
			System.err.println("微信支付授权url:" + url);
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @throws InterruptedException
	 * @throws IOException 
	 * @Name: 微信支付
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/pay/toPay", params = { "orderId" })
	public ModelAndView wechatToPay(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("orderId") String orderId) throws InterruptedException, IOException {

		ModelAndView mav = new ModelAndView();

		System.err.println("-----------------------开始进入微信支付授权回调方法---------------------------");

		String openid = CookiesUtil.getCookieKey(request);

		if (null == openid || "".equals(openid) || "null".equals(openid)) {
			System.err.println("微信支付:获取不到 openid 数据");
			mav.addObject("page", "{\"message\":\"无法获取您的信息,请授权登录\",\"flag\":false,\"page\":3}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}

		System.err.println(openid);

		if (openid.contains("null")) {
			System.err.println("openid.contains('null')");
			mav.addObject("page", "{\"message\":\"无法获取您的信息,请授权登录\",\"flag\":false,\"page\":3}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}

		if (null == orderId || "null".equals(orderId) || "".equals(orderId)) {
			orderId = "0";
		}

		System.err.println("微信支付订单:" + orderId);

		Map<String, Object> map = new HashMap<String, Object>();
		String buyerUid = SysCache.getWeChatUserByColumn(request, "userId");
		map.put("orderId", orderId);
		
		System.err.println("微信订单的参数为：" + String.valueOf(map));
		String orderdetail = OkhttpUtils.get(SysContext.ORDERURL + "/ord/orderdetail/info", map);
		List<Map<String, Object>> orderdetailInfo = JsonUtil.listMaps(orderdetail);
		Map<String, Object> ordMap = new HashMap<String, Object>();
		ordMap.put("orderCode", orderId);		
		String order = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", ordMap);
		List<Map<String, Object>> orderInfo = JsonUtil.listMaps(order);
		if (orderdetailInfo == null || orderdetailInfo.size() < 1) {
			System.err.println("该订单号不存在");
			mav.addObject("page", "{\"message\":\"该订单信息不存在。\",\"flag\":false,\"page\":2}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}
		
		StringBuilder goodsName = new StringBuilder();// 商品名称

		for (int i = 0; i < orderdetailInfo.size(); i++) {
			goodsName.append(orderdetailInfo.get(i).get("onlineTitle") + ",");
		}

		map.clear();

		map.put("userId", buyerUid);
		map.put("wxId", openid); // 用户微信id（根据不同情况可能是unionid或者openid）
		map.put("orderId", orderId);// 订单编号
		map.put("tradeTitle", "微薄利商超-商品购买"); // 支付交易标题
		String[] goodsNames = String.valueOf(goodsName).split(",");
		String tradeDetail = "";
		if (goodsNames.length > 2) {
			tradeDetail = goodsNames[0] + "," + goodsNames[1] + "等。。。";
		} else {
			tradeDetail = goodsNames[0];
		}
		map.put("tradeDetail", "购买商品为：" + tradeDetail); // 支付交易详情
		map.put("tradeAmount", orderInfo.get(0).get("realMoney")); // 支付交易金额（单位为元）
		map.put("ip", IpUtil.getIp(request)); // 用户ip地址

		//String finalsign = "";
		String prepay_id = "";
		
		System.err.println("获取微信预支付Id请求参数：" + map);
		String result = HttpUtil.postUrl(SysContext.VPAYURL + "/wxpay/prepay", map);
		System.err.println("获取微信预支付Id返回结果：" + result);

		if (null == result || "".equals(result) || "null".equals(result)) {
			System.err.println("请求超时");
		} else {
			String prepayId = JsonUtil.GetJsonValue(result, "prepayId");
			if (prepayId != null && !"".equals(prepayId)) {
				String[] split = prepayId.split(";");
				if (split.length == 2) {
					// finalsign = split[1];
				}
				prepay_id = split[0];
			}
		}

		if ("".equals(prepay_id)) {
			System.err.println("订单已生成,微信预支付订单出错");
			mav.addObject("page", "{\"message\":\"订单已生成,微信预支付订单出错\",\"flag\":false,\"page\":2}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}

		String nonce_str = UUID.randomUUID().toString().replaceAll("-", "");// 随机字符

		SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();

		Long timestamp = System.currentTimeMillis() / 1000;// 当前时间戳

		String packages = "prepay_id=" + prepay_id;

		finalpackage.put("appId", WxConfig.appid);
		finalpackage.put("timeStamp", timestamp.toString());
		finalpackage.put("nonceStr", nonce_str);
		finalpackage.put("package", packages);
		finalpackage.put("signType", WxConfig.signType);

		String finalsign1 = WXSignUtils.createSign("UTF-8", finalpackage);

		mav.addObject("appid", WxConfig.appid);
		mav.addObject("timeStamp", timestamp);
		mav.addObject("nonceStr", nonce_str);
		mav.addObject("packageValue", packages);
		mav.addObject("signType", WxConfig.signType);
		mav.addObject("sign", finalsign1);
		mav.addObject("orderId", orderId);
		mav.setViewName("/htm/wechat/pay/payPage");
		return mav;
	}

	/**
	 * @Name: 获取V支付余额
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/pay/getVBalance", method = RequestMethod.POST)
	public void getPayBalance(HttpServletRequest request, HttpServletResponse response) {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		String result = HttpUtil.getUrl(SysContext.VPAYURL + "/account/funds?userId=" + userId);
		System.err.println("V支付余额:" + result);
		if (result != null) {
			result = result.replaceAll("\"", "");
		}
		BigDecimal balance = new BigDecimal(String.valueOf(JsonUtil.getJsonValue(result, "balance"))); // 用户余额
		BigDecimal cashback = new BigDecimal(String.valueOf(JsonUtil.getJsonValue(result, "cashback")));
		double totalAmount = balance.add(cashback).doubleValue();
		if (result == null) {
			this.render(response, "{\"message\":\"获取余额超时了 >_<\",\"flag\":false}");
			return;
		}
		if (result.equals("-1")) {
			this.render(response, "{\"message\":\"secretKey Incorrect!\",\"flag\":false}");
			return;
		}
		this.render(response, "{\"message\":\"" + totalAmount + "\",\"flag\":true}");
	}

	/**
	 * @throws IOException 
	 * @Name: 跳转至支付页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/pay/paycenterPage")
	public ModelAndView payPage(HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (!"".equals(userId)) {
			mav.addObject("userId", userId);
			mav.setViewName("/htm/wechat/pay/paycenter");
		} else {
			mav.setViewName("/htm/wechat/login/login");
			return mav;
		}

		String orderId = request.getParameter("payOrderId");
		if (orderId == null || orderId.equals("")) {
			System.err.println("请求参数有误[订单编号有误]");
			mav.setViewName("/htm/wechat/login/login");
			return mav;
		} else {
			mav.addObject("payOrderId", orderId);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderCode", orderId);
			map.put("userId", userId);
			String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", map);
			System.out.println("查询订单信息的返回值为：" + results);
			List<Map<String, Object>> listMap = JsonUtil.listMaps(results);
			if (listMap != null && listMap.size() > 0) {
				mav.addObject("orderMoney", listMap.get(0).get("realMoney"));
			} else {
				mav.addObject("orderMoney", "0.00");
			}
		}

		return mav;
	}

	/**
	 * @Name: V支付 支付确认
	 * @Author: nick
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/wechat/pay/payVerify", method = RequestMethod.POST)
	public void payVerify(HttpServletRequest request, HttpServletResponse response) {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		String requestOrderId = request.getParameter("payOrderId");
		if (requestOrderId == null || requestOrderId.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		String password = request.getParameter("pwd");
		if (password == null || password.equals("")) {
			this.render(response, "{\"message\":\"请输入您的V支付密码\",\"flag\":false}");
			return;
		}

		Base64EnOut base64 = new Base64EnOut();
		password = base64.Decode(password);

		if (password == null || password.equals("") || password.equals("null")) {
			this.render(response, "{\"message\":\"支付密码错误\",\"flag\":false}");
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", requestOrderId);
		map.put("buyerUid", userId);
		List<Map<String, Object>> listMap = weChatOrderService.selectOrderInventoryByParm(map);

		if (listMap == null || listMap.size() < 1) {
			this.render(response, "{\"message\":\"该订单信息不存在。\",\"flag\":false}");
			return;
		}

		String goodsId = ""; // 商品编号
		String summoney = ""; // 订单总金额
		String buyerUid = ""; // 买家编号
		BigDecimal big = new BigDecimal("0");
		String goodsidNum = ""; // 活动编号,多个 "_" 连接
		Integer shopId = 0; // 门店编号
		String message = ""; // 买家留言信息

		for (int i = 0; i < listMap.size(); i++) {

			goodsId += "_" + listMap.get(i).get("goodsId");

			goodsidNum += "_" + listMap.get(i).get("activityId");

			summoney = String.valueOf(listMap.get(i).get("money"));

			buyerUid = String.valueOf(listMap.get(i).get("buyerUid"));

			shopId = Integer.parseInt(String.valueOf(listMap.get(i).get("shopId")));

			message = String.valueOf(listMap.get(i).get("message"));

			if (!listMap.get(i).get("retailPrice").equals(listMap.get(i).get("retailBacLimit"))) {
				big = big.add(new BigDecimal(Integer.parseInt(String.valueOf(listMap.get(i).get("retailPrice")))
						* Integer.parseInt(String.valueOf(listMap.get(i).get("num")))));
			}
		}

		map.clear();

		String key = MD5CodeUtil.md5(summoney + buyerUid + requestOrderId + 1 + SysContext.CONFIGMAP.get("wechatvpay"));

		map.put("key", key);// 交互密钥
		map.put("orderId", requestOrderId);// 订单编号
		map.put("uid", buyerUid);// 用户编号
		map.put("password", MD5CodeUtil.md5(password));// 用户密码
		map.put("goodsid", goodsId.substring(1, goodsId.length()));// 商品编号
																	// 多个‘_’相连
		map.put("goodsidNum", goodsidNum.substring(1, goodsidNum.length()));// 活动编号
																			// 多个‘_’相连
		map.put("shopId", shopId);// 门店编号
		map.put("isvpay", 1);// 是否用v支付支付 1用 2不用
		map.put("summoney", summoney);// 总金额
		big = big.divide(new BigDecimal(100));
		map.put("cashVolume", big);// 可用返现金额付款的金额
		map.put("payType", 4);// 支付类型 4：v支付 1 ：支付宝 2 网银 3 微信
		map.put("site", 1);// 网站来源 1: 商超
		// 支付不能传空
		if (null == message || "".equals(message)) {
			message = "- - -";
		}
		map.put("message", message);// 备注
		map.put("backurl", "backurl");// 跳转url
		map.put("notetyurl", "notetyurl");// 通知url

		System.err.println("请求V支付参数:" + map.toString());

		String result = HttpUtil.postUrl(SysContext.CONFIGMAP.get("vpaySite") + "/wechatmobileconfirmpass.do", map);
		System.err.println("返回结果:" + result);
		if (null == result || "".equals(result) || "null".equals(result)) {
			this.render(response, "{\"message\":\"请求超时\",\"flag\":false}");
			return;
		}
		String errorpar = JsonUtil.GetJsonValue(result, "errorpar");
		if (errorpar.equals("1")) {
			this.render(response, "{\"message\":\"成功支付\",\"flag\":true}");
			return;
		}
		if (errorpar.equals("-4")) {
			this.render(response, "{\"message\":\"该订单已处理\",\"flag\":false}");
			return;
		}
		if (errorpar.equals("-9")) {
			this.render(response, "{\"message\":\"v支付余额不足\",\"flag\":false}");
			return;
		}
		if (errorpar.equals("-8")) {
			this.render(response, "{\"message\":\"密码错误\",\"flag\":false}");
			return;
		}
		if (errorpar.equals("-3")) {
			this.render(response, "{\"message\":\"余额不足\",\"flag\":false}");
			return;
		}
		this.render(response, "{\"message\":\"请求超时\",\"flag\":false}");
	}

	/**
	 * @throws IOException 
	 * @Name: 支付成功页面
	 * @Author: knick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/pay/success", params = { "type", "orderId" })
	public ModelAndView successPage(@RequestParam String orderId, @RequestParam String type,
			HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		System.err.println("----------------------- 支付成功 ---------------------------");

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("orderCode", orderId);

		if (type.equals("vpay")) {
			System.err.println("____进入V支付返回通知通道____");

			String order = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/order/info", map);
			List<Map<String, Object>> orderInfo = JsonUtil.listMaps(order);

			if (orderInfo == null || orderInfo.size() < 1) {
				System.err.println("V支付:该订单号不存在");
				mav.addObject("orderId", "- - - - - - - - - -");
				mav.addObject("money", "- -");
			} else {
				mav.addObject("orderId", String.valueOf(orderInfo.get(0).get("orderId")));
				mav.addObject("money", String.valueOf(orderInfo.get(0).get("money")));
			}

		} else if (type.equals("wxpay")) {
			System.err.println("____进入微信支付返回通知通道____");
			try {
				// 查询微信统一下单信息
				Map resultMap = UnifyDowdUtil.checkWxOrderPay(orderId);
				System.err.println("resultMap:" + resultMap);

				String return_code = (String) resultMap.get("return_code");

				System.err.println("return_code:" + return_code);

				if ("SUCCESS".equals(return_code)) {
					String result_code = (String) resultMap.get("result_code");
					System.err.println("result_code:" + result_code);
					if ("SUCCESS".equals(result_code)) {
						// 商家订单号
						String out_trade_no = (String) resultMap.get("out_trade_no");
						// 支付完成时间
						String time_end = (String) resultMap.get("time_end");
						// 订单金额 (单位:分)
						String total_fee = (String) resultMap.get("total_fee");
						// 微信支付订单号
						String transaction_id = (String) resultMap.get("transaction_id");
						BigDecimal big = new BigDecimal(total_fee);
						big = big.divide(new BigDecimal(100));
						big = big.setScale(2, BigDecimal.ROUND_HALF_DOWN);
						System.err.println("---------- 微信支付订单信息 ------------");
						System.err.println("订单号:" + out_trade_no);
						System.err.println("支付金额(元):" + big);
						System.err.println("微信支付订单号:" + transaction_id);
						System.err.println("支付完成时间:" + time_end);
						mav.addObject("orderId", out_trade_no);
						mav.addObject("money", big);
					} else {
						System.err.println("微信支付业务结果:支付失败");
						String err_code = (String) resultMap.get("err_code");
						String err_code_des = (String) resultMap.get("err_code_des");
						System.err.println("weixin resultCode:" + result_code + ",err_code:" + err_code
								+ ",err_code_des:" + err_code_des);

						mav.addObject("orderId", "- - - - - - - - - -");
						mav.addObject("money", "- -");
					}
				} else {
					System.err.println("微信支付:支付失败");
					mav.addObject("orderId", "- - - - - - - - - -");
					mav.addObject("money", "- -");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.err.println("微信支付:抛出异常");
				mav.addObject("orderId", "- - - - - - - - - -");
				mav.addObject("money", "- -");
			}
		}

		map.put("code", 0);

		vblorderService.selectOrderCodeByOrderId(map);

		mav.addObject("code", map.get("code").toString());

		System.err.println(map.toString());

		map = null;

		mav.setViewName("/htm/wechat/pay/paysuccess");

		return mav;
	}
	
	/**
	 * 获取v支付预支付Id
	 * @param request
	 * @param response
	 * @throws SocketException 
	 */
	@RequestMapping(value = "/wechat/pay/createVpayPrepaymentId", method = RequestMethod.POST)
	public void createVpayPrepaymentId(HttpServletRequest request, HttpServletResponse response) throws SocketException {
		String buyerUid = request.getParameter("userId"); // 用户编号
		String orderId = request.getParameter("orderId"); // 订单编号
		
		if (buyerUid == null || buyerUid.equals("") || buyerUid.equals("null")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return ;
		}
		
		if (orderId == null || orderId.equals("") || orderId.equals("null")) {
			this.render(response, "{\"message\":\"订单不能为空\",\"flag\":false}");
			return ;
		}
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("orderCode", orderId);
		map.put("orderId", orderId);
		map.put("buyerUid", buyerUid);
		map.put("tradeTitle", "微薄利商超-商品购买"); // 支付交易标题
		map.put("mac", MacAddressUtil.getLocalMac()); // 用户mac地址
		map.put("ip", IpUtil.getIp(request)); // 用户ip地址
		map.put("userId", buyerUid);
		
		// 查询订单是否存在
		String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", map);
		List<Map<String, Object>> orderDataList = JsonUtil.listMaps(results);
		if (orderDataList.size() == 0) {
			this.render(response, "{\"message\":\"该订单不存在\",\"flag\":false}");
			return ;
		}
		BigDecimal decimal = new BigDecimal(String.valueOf(orderDataList.get(0).get("realMoney")));
		map.put("tradeAmount", decimal.toString());
		
		// 获取商品订单详情
		String orderdetail = OkhttpUtils.get(SysContext.ORDERURL + "/ord/orderdetail/info", map);
		List<Map<String, Object>> orderInventoryList = JsonUtil.listMaps(orderdetail);
		if (orderInventoryList.size() == 0) {
			this.render(response, "{\"message\":\"该订单不存在\",\"flag\":false}");
			return ;
		}
		
		String goodsNames = "";// 商品名称
		if (orderInventoryList.size() >= 2) {
			goodsNames = orderInventoryList.get(0).get("onlineTitle") + "," + orderInventoryList.get(1).get("onlineTitle") + "等。。。";
		} else {
			goodsNames = String.valueOf(orderInventoryList.get(0).get("onlineTitle"));
		}
		map.put("tradeDetail", "您在微薄利商超购买的商品订单为：" + orderId + "，所购买的商品为：" + goodsNames);
		System.err.println();
		// 获取v支付预支付Id
		String vpayPrepaymentResult = HttpUtil.postUrl(SysContext.VPAYURL + "/vpay/prepay", map);
		// 判断v支付生成预支付Id是否为空
		if (vpayPrepaymentResult == null || vpayPrepaymentResult.equals("") || vpayPrepaymentResult.equals("null")) {
			this.render(response, "{\"message\":\"v支付生成预支付Id出错\",\"flag\":false}");
			return ;
		}
		
		// v支付预支付id
		String prepayId = JsonUtil.GetJsonValue(vpayPrepaymentResult, "prepayId");
		// 是否需要输入支付密码
		String requirePayPswd = JsonUtil.getJSONValue(vpayPrepaymentResult, "requirePayPswd");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("prepayId", prepayId);
		resultMap.put("requirePayPswd", requirePayPswd);
		resultMap.put("flag", true);
		
		System.err.println(JsonUtil.ObjectToJson(resultMap));
		
		this.render(response, JsonUtil.ObjectToJson(resultMap));
		return;
	}
	
	/**
	 * 门店订单使用v支付支付
	 * @param request
	 * @param response
	 * 2018年1月18日14:12:15
	 * @throws SocketException 
	 */
	@RequestMapping(value = "/wechat/pay/shopOrderPay", method = RequestMethod.POST)
	public void shopOrderPay(HttpServletRequest request, HttpServletResponse response) throws SocketException {
		System.err.println("==============订单支付开始============");
		String prepayId = request.getParameter("prepayId"); // v支付预支付id
		String requirePayPswd = request.getParameter("requirePayPswd");
		String umac = MacAddressUtil.getLocalMac(); // 用户mac地址
		String uip = IpUtil.getIp(request); // 用户ip地址
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("prepayId", prepayId);
		map.put("mac", umac);
		map.put("ip", uip);
		map.put("payPswd", MD5CodeUtil.md5("wboly.com"));
		
		if (!requirePayPswd.equals("false")) {
			String vpassword = request.getParameter("pwd"); // v支付密码
			map.put("payPswd", vpassword);
		}
		System.err.println("支付的参数为：" + map.toString());
		// 订单支付
		String result = HttpUtil.postUrl(SysContext.VPAYURL + "/vpay/pay", map);
		String results = JsonUtil.getJSONValue(result, "result");
		System.err.println("调用v支付返回值为：" + result);
		if (results == null || results.equals("") || results.equals("null")) {
			this.render(response, "{\"message\":\"请求V支付出错\",\"flag\":false}");
			return ;
		}
		
		if (results.equals("0")) {
			this.render(response, "{\"message\":\"缓存失败\",\"flag\":false}");
		} else if (results.equals("-1")) {
			this.render(response, "{\"message\":\"参数不正确\",\"flag\":false}");	
		} else if (results.equals("-2")) {
			this.render(response, "{\"message\":\"没有找到预支付信息\",\"flag\":false}");
		} else if (results.equals("-3")) {
			this.render(response, "{\"message\":\"找不到用户信息\",\"flag\":false}");
		} else if (results.equals("-4")) {
			this.render(response, "{\"message\":\"密码错误\",\"flag\":false}");
		} else if (results.equals("-5")) {
			this.render(response, "{\"message\":\"账号被锁定\",\"flag\":false}");
		} else if (results.equals("-6")) {
			this.render(response, "{\"message\":\"余额或返现金不足\",\"flag\":false}");
		} else if (results.equals("-7")) {
			this.render(response, "{\"message\":\"订单已经支付\",\"flag\":false}");
		} else if (results.equals("1")) {
			this.render(response, "{\"message\":\"成功支付\",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":\"支付失败\",\"flag\":false}");
		}
	}

}
