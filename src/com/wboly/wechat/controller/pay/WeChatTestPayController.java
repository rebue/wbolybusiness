package com.wboly.wechat.controller.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.wx.UnifyDowdUtil;
import com.wboly.system.sys.util.wx.WXSignUtils;
import com.wboly.system.sys.util.wx.WeixinUtil;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;
import com.wboly.system.sys.util.wx.WxConfig;
import com.wboly.wechat.service.order.WeChatOrderService;

/**
 * @Name: 微信公众号支付测试目录
 * @Author: nick
 */
@Controller
@RequestMapping(value = "/wechat/paytest/")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class WeChatTestPayController extends SysController {

	@Autowired
	private WeChatOrderService weChatOrderService;

	/**
	 * @Name: jssdk 页面
	 * @Author: nick
	 */
	@RequestMapping(value = "jssdk")
	public ModelAndView jsSDK(HttpServletRequest request) {
		return new ModelAndView("/htm/wechat/pay/test/jssdk").addObject("JSURL", request.getRequestURL());
	}

	/**
	 * @Name: 微信支付授权，先获取 code，跳转 url 通过 code 获取 openId
	 * @Author: knick
	 */
	@RequestMapping(value = "authpay", params = { "orderId" })
	public String userAuthPay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("orderId") String orderId) {
		try {
			System.err.println("-----------------------开始进入支付授权方法 测试目录---------------------------");

			// 授权后要跳转的链接
			String backUri = WxConfig.onLineURL + "/wechat/paytest/toPay.htm";
			backUri = backUri + "?orderId=" + orderId;
			// URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
			backUri = URLEncoder.encode(backUri, "UTF-8");
			// scope 参数视各自需求而定，这里用 scope=snsapi_base
			// 不弹出授权页面直接授权目的只获取统一支付接口的openid
			String url = SITE.AUTHORIZE.getMessage() + "?appid=" + WxConfig.appid + "&redirect_uri=" + backUri
					+ "&response_type=code&scope=snsapi_userinfo&state=" + orderId + "#wechat_redirect";
			System.err.println("微信支付授权url:" + url);
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Name: 微信支付
	 * @Author: knick
	 */
	@RequestMapping(value = "toPay", params = { "code", "state" })
	public ModelAndView wechatToPay(HttpServletRequest request, HttpServletResponse response, Model model,
			@RequestParam("code") String code, @RequestParam("state") String state) {
		ModelAndView mav = new ModelAndView();

		System.err.println("-----------------------开始进入支付授权回调方法 测试目录---------------------------");

		String openid = "";

		String cookieKey = CookiesUtil.getCookieKey(request);

		if (null == cookieKey || "".equals(cookieKey)) {
			System.err.println("微信支付:获取不到 openid (正在通过 code [" + code + "] 获取openid)");
			openid = WeixinUtil.getOpenIdByCode(code);
		} else {
			String refreshData = SysCache.getRefreshData(cookieKey);

			if (null == refreshData || "".equals(refreshData) || "null".equals(refreshData)) {
				System.err.println("微信支付:获取不到 refreshData 数据(可能已失效)");
				openid = WeixinUtil.getOpenIdByCode(code);
			} else {
				String refresh = JsonUtil.GetJsonValue(refreshData, "refresh_token");
				openid = WeixinUtil.getOpenIdByRefresh(refresh);
			}
		}
		
		//openid = "onCVJvw6phS6xLrSEnONm40LFjF4";

		if (null == openid || "".equals(openid)) {
			System.err.println("微信支付:获取不到 openid 数据");
			mav.addObject("page", "{\"message\":\"无法获取您的信息\",\"flag\":false,\"page\":2}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}

		String orderId = request.getParameter("orderId");
		if (null == orderId || "null".equals(orderId) || "".equals(orderId)) {
			orderId = "0";
		}

		System.err.println("微信支付订单:" + orderId);

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("orderId", orderId);
		map.put("buyerUid", SysCache.getWeChatUserByColumn(request, "userId"));

		List<Map<String, Object>> orderInfo = weChatOrderService.selectOrderInventoryByParm(map);

		if (orderInfo == null || orderInfo.size() < 1) {
			System.err.println("该订单号不存在");
			mav.addObject("page", "{\"message\":\"该订单信息不存在。\",\"flag\":false,\"page\":2}");
			mav.setViewName("/htm/wechat/prompt/alert");
			return mav;
		}

		String goodsId = ""; // 商品编号
		String summoney = ""; // 订单总金额
		String buyerUid = ""; // 买家编号
		BigDecimal big = new BigDecimal("0");
		String goodsidNum = "";// 活动编号,多个 "_" 连接
		int shopId = 0;// 门店编号
		String message = ""; // 买家留言信息

		for (int i = 0; i < orderInfo.size(); i++) {

			goodsId += "_" + orderInfo.get(i).get("goodsId");

			goodsidNum += "_" + orderInfo.get(i).get("activityId");

			summoney = String.valueOf(orderInfo.get(i).get("money"));

			buyerUid = String.valueOf(orderInfo.get(i).get("buyerUid"));

			shopId = Integer.parseInt(String.valueOf(orderInfo.get(i).get("shopId")));

			message = String.valueOf(orderInfo.get(i).get("message"));
			
			if (orderInfo.get(i).get("retailPrice") != orderInfo.get(i).get("retailBacLimit")) {
				big = big.add(new BigDecimal(Integer.parseInt(String.valueOf(orderInfo.get(i).get("retailPrice")))
						* Integer.parseInt(String.valueOf(orderInfo.get(i).get("num")))));
			}
		}

		map.clear();
		
		summoney = "0.01";// 写死总金额

		String key = MD5CodeUtil.md5(summoney + buyerUid + orderId + SysContext.CONFIGMAP.get("wechatvpay"));

		// md5(summoney+uId+orderId+isvpay+通密)
		map.put("key", key);// 交互密钥
		map.put("orderId", orderId);// 订单编号
		map.put("uid", buyerUid);// 用户编号
		map.put("goodsid", goodsId.substring(1, goodsId.length()));// 商品编号  多个‘_’相连
		map.put("goodsidNum", goodsidNum.substring(1, goodsidNum.length()));// 活动编号 多个‘_’相连
		map.put("shopId", shopId);// 门店编号
		map.put("isvpay", 1);// 是否用v支付支付 1用 2不用
		map.put("summoney", summoney);// 总金额
		big = big.divide(new BigDecimal(100));
		map.put("cashVolume", "0.01");// 可用返现金额付款的金额
		map.put("payType", 3);// 支付类型 4：v支付 1 ：支付宝 2 网银 3 微信
		map.put("site", 1);// 网站来源 1: 商超
		// 支付不能传空
		if (null == message || "".equals(message)) {
			message = "- - -";
		}
		message = message.replaceAll(",", "#").replaceAll("，", "#");
		map.put("message", message);// 备注
		map.put("backurl", "backurl");// 跳转url
		map.put("notetyurl", "notetyurl");// 通知url
		map.put("openid", openid);// openid

		key = MD5CodeUtil.md5(openid + orderId + summoney + big);
		
		String prepay_id = SysCache.getWxPayInfo(key);

		if ("".equals(prepay_id)) {
			
			System.err.println("支付参数:" + map);
			
			String result = HttpUtil.postUrl(SysContext.CONFIGMAP.get("vpaySite") + "/getwechatpaykey.do", map);

			System.err.println("返回结果:" + result);

			if (null == result || "".equals(result) || "null".equals(result)) {
				System.err.println("请求超时");
			} else {

				String errorpar = JsonUtil.GetJsonValue(result, "errorpar");

				String returnurl = JsonUtil.GetJsonValue(result, "returnurl");

				if ("1".equals(errorpar)) {

					if (!"".equals(returnurl)) {
						SysCache.setWxPayInfo(key, returnurl);
						prepay_id = returnurl;
					}
				}
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

		String finalsign = WXSignUtils.createSign("UTF-8", finalpackage);

		System.err.println("finalpackage:" + finalpackage);
		System.err.println("签字:" + finalsign);

		mav.addObject("appid", WxConfig.appid);
		mav.addObject("timeStamp", timestamp);
		mav.addObject("nonceStr", nonce_str);
		mav.addObject("packageValue", packages);
		mav.addObject("signType", WxConfig.signType);
		mav.addObject("sign", finalsign);

		mav.addObject("orderId", orderId);

		mav.setViewName("/htm/wechat/pay/test/payPage");

		return mav;
	}

	/**
	 * @Name: 微信异步回调，不会跳转页面
	 * @Author: nick
	 */
	@RequestMapping(value = "backUrl")
	public void weixinReceive(HttpServletRequest request, HttpServletResponse response, Model model) {

		System.err.println("-----------------------开始进入h5支付回调方法---------------------------");
		System.err.println(new Date().getTime());

		String xml_review_result = UnifyDowdUtil.getXmlRequest(request);
		System.err.println("微信支付结果:" + xml_review_result);

		String attach = request.getParameter("attach");// 自定义的附加数据
		System.err.println("自定义的附加数据:" + attach);

		Map resultMap = null;
		try {
			resultMap = UnifyDowdUtil.doXMLParse(xml_review_result);
			System.err.println("resultMap:" + resultMap);
			if (resultMap != null && resultMap.size() > 0) {
				String sign_receive = (String) resultMap.get("sign");
				System.err.println("sign_receive:" + sign_receive);
				resultMap.remove("sign");
				String checkSign = WXSignUtils.getSign(resultMap, WxConfig.partnerkey);
				System.err.println("checkSign:" + checkSign);

				// 签名校验成功
				if (checkSign != null && sign_receive != null && checkSign.equals(sign_receive.trim())) {
					System.err.println("weixin receive check Sign sucess");
					try {
						// 获得返回结果
						String return_code = (String) resultMap.get("return_code");

						if ("SUCCESS".equals(return_code)) {
							String out_trade_no = (String) resultMap.get("out_trade_no");
							System.err.println("weixin pay sucess,out_trade_no:" + out_trade_no);
							// 处理支付成功以后的逻辑，这里是写入相关信息到文本文件里面，如果有订单的处理订单
							try {
								/*
								 * SimpleDateFormat sdf = new SimpleDateFormat(
								 * "yyyy-MM-dd hh24:mi:ss"); String content =
								 * out_trade_no+"        "+sdf.format(new
								 * Date()); String fileUrl =
								 * System.getProperty("user.dir") +
								 * File.separator+"WebContent" + File.separator
								 * + "data" + File.separator + "order.txt";
								 * TxtUtil.writeToTxt(content, fileUrl);
								 */
								System.err.println("支付成功后的逻辑");
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							model.addAttribute("payResult", "0");
							model.addAttribute("err_code_des", "通信错误");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// 签名校验失败
					System.err.println("weixin receive check Sign fail");
					String checkXml = "<xml><return_code><![CDATA[FAIL]]></return_code>"
							+ "<return_msg><![CDATA[check sign fail]]></return_msg></xml>";
					UnifyDowdUtil.getTradeOrder(WxConfig.onLineURL + "/wechat/paytest/backUrl.htm", checkXml);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Name: 页面js返回支付成功后，查询微信后台是否支付成功，然后跳转结果页面
	 * @Author: nick
	 */
	@RequestMapping(value = "success", params = { "orderId", "type" })
	public ModelAndView toWXPaySuccess(HttpServletRequest request, HttpServletResponse response, Model model)
			throws IOException {
		ModelAndView mav = new ModelAndView();

		System.err.println("-----------------------支付成功---------------------------");
		System.err.println(new Date());
		String id = request.getParameter("orderId");
		System.err.println("toWXPaySuccess, orderId: " + id);
		String type = request.getParameter("type");
		System.err.println("toWXPaySuccess, type: " + type);
		if (null == type) {
			mav.setViewName("/htm/wechat/index/index");
			return mav;
		}
		if (type.equals("vpay")) {
			System.err.println("____进入V支付返回通知通道____");
			model.addAttribute("orderId", id);
			model.addAttribute("payResult", "1");
			mav.setViewName("/htm/wechat/paytest/payResult");
			return mav;
		} else if (type.equals("wxpay")) {
			System.err.println("____进入微信支付返回通知通道____");
			try {
				Map resultMap = UnifyDowdUtil.checkWxOrderPay(id);
				System.out.println("resultMap:" + resultMap);
				String return_code = (String) resultMap.get("return_code");
				String result_code = (String) resultMap.get("result_code");
				System.out.println("return_code:" + return_code + ",result_code:" + result_code);
				if ("SUCCESS".equals(return_code)) {
					if ("SUCCESS".equals(result_code)) {
						model.addAttribute("orderId", id);
						model.addAttribute("payResult", "1");
					} else {
						String err_code = (String) resultMap.get("err_code");
						String err_code_des = (String) resultMap.get("err_code_des");
						System.out.println("weixin resultCode:" + result_code + ",err_code:" + err_code
								+ ",err_code_des:" + err_code_des);

						model.addAttribute("err_code", err_code);
						model.addAttribute("err_code_des", err_code_des);
						model.addAttribute("payResult", "0");
					}
				} else {
					model.addAttribute("payResult", "0");
					model.addAttribute("err_code_des", "通信错误");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		mav.setViewName("/htm/wechat/pay/test/payResult");

		return mav;
	}

}
