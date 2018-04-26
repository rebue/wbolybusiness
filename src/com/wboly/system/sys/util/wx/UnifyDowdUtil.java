package com.wboly.system.sys.util.wx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.wboly.system.sys.util.wx.WeixinUtil.SITE;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UnifyDowdUtil {

	/**
	 * 查询订单
	 * 
	 * @param orderId
	 * @return
	 * @throws Exception
	 */
	public static Map checkWxOrderPay(String orderId) throws Exception {
		String nonce_str = UUID.randomUUID().toString().replaceAll("-", "");
		String sign = "";
		SortedMap<Object, Object> storeMap = new TreeMap<Object, Object>();
		storeMap.put("out_trade_no", orderId); // 商户 后台的贸易单号
		storeMap.put("appid", WxConfig.appid); // appid
		storeMap.put("mch_id", WxConfig.partner); // 商户号
		storeMap.put("nonce_str", nonce_str); // 随机数
		sign = WXSignUtils.createSign("UTF-8", storeMap);

		String xml = "<xml>" + "<appid>" + WxConfig.appid + "</appid>" + "<mch_id>" + WxConfig.partner
				+ "</mch_id>" + "<nonce_str>" + nonce_str + "</nonce_str>" + "<out_trade_no>" + orderId
				+ "</out_trade_no>" + "<sign>" + sign + "</sign>" + "</xml>";

		String resultMsg = getTradeOrder(SITE.PAY_QUERY_ORDER.getMessage(), xml);
		System.out.println("orderquery,result:" + resultMsg);
		if (StringUtils.isNotBlank(resultMsg)) {
			Map resultMap = UnifyDowdUtil.doXMLParse(resultMsg);
			if (resultMap != null && resultMap.size() > 0) {
				return resultMap;
			}
		}
		return null;
	}

	/**
	 * 处理xml请求信息
	 * 
	 * @param request
	 * @return
	 */
	public static String getXmlRequest(HttpServletRequest request) {
		java.io.BufferedReader bis = null;
		String result = "";
		try {
			bis = new java.io.BufferedReader(new java.io.InputStreamReader(request.getInputStream()));
			String line = null;
			while ((line = bis.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * 获取统一订单提交返回字符串值
	 * 
	 * @param url
	 * @param xmlParam
	 * @return
	 */
	public static String getTradeOrder(String url, String xmlParam) {

		return HttpXmlUtils.httpsRequest(url, "POST", xmlParam).toString();
	}

	/**
	 * @Name: 获取预支付标识
	 * @param or_num:商户订单号
	 *            total_fee:支付金额(单位:分) openid:用户标识 attach:自定义参数
	 * @Author: nick
	 */
	public static String getPayNo(String or_num, int total_fee, String openid) {

		String prepay_id = "";

		try {
			String jsonStr = postWX(or_num, total_fee, openid);
			System.err.println("postWX:" + jsonStr);
			if ("".equals(jsonStr) || jsonStr.indexOf("FAIL") != -1) {
				return prepay_id;
			}
			Map map = doXMLParse(jsonStr);

			String return_code = (String) map.get("return_code");

			System.err.println("return_code:" + return_code);

			prepay_id = (String) map.get("prepay_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prepay_id;
	}

	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}

	/**
	 * 解析xml,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值是子节点的xml数据。
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map doXMLParse(String strxml) throws Exception {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}

		Map m = new HashMap();
		InputStream in = String2Inputstream(strxml);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}

			m.put(k, v);
		}

		// 关闭流
		in.close();

		return m;
	}

	/**
	 * 获取子结点的xml
	 * 
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}

	/**
	 * @Name: 微信签字
	 * @Author: nick
	 * @param or_num:商户订单号;total_fee:支付金额(单位:分);openid:用户标识;attach:自定义参数;
	 */
	public static String postWX(String or_num, int total_fee, String openid) {

		String weixinPost = "";

		String backUrl = WxConfig.onLineURL + "/wechat/pay/test/backUrl.htm";// 回调地址 这里 notify_url 是
																			// 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。

		String nonce_str = UUID.randomUUID().toString().replaceAll("-", ""); // 随机字符串

		Date date = new Date();
		long start = date.getTime();// 当前时间
		long expire = start + 1000 * 60 * 60 * 24;// 一天后
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

		String time_start = sdf.format(start); // 开始时间
		String time_expire = sdf.format(expire); // 结束时间

		// 参数：开始生成签名
		SortedMap<Object, Object> sortedmap = new TreeMap<Object, Object>();
		sortedmap.put("appid", WxConfig.appid);// 公众号Id
		sortedmap.put("mch_id", WxConfig.partner); // 商户Id
		sortedmap.put("nonce_str", nonce_str);// 随机字符串，长度要求在32位以内
		sortedmap.put("body", "微薄利连锁商超");// 商品描述
		sortedmap.put("detail", "支付商品");// 商品详细
		/* sortedmap.put("attach", attach); */
		sortedmap.put("out_trade_no", or_num);// 商户订单号
		sortedmap.put("total_fee", total_fee);// 支付金额(单位:分)
		sortedmap.put("time_start", time_start);// 开始时间
		sortedmap.put("time_expire", time_expire);// 结束时间
		sortedmap.put("notify_url", backUrl); // 通知地址.回调地址
		sortedmap.put("trade_type", "JSAPI");// 交易类型
		sortedmap.put("spbill_create_ip", "127.0.0.1");// 终端Ip
		sortedmap.put("openid", openid);// 用户标识

		String sign = WXSignUtils.createSign("UTF-8", sortedmap);
		System.err.println("签名是：" + sign);

		Unifiedorder unifiedorder = new Unifiedorder();
		unifiedorder.setAppid(WxConfig.appid);
		unifiedorder.setMch_id(WxConfig.partner);
		unifiedorder.setNonce_str(nonce_str);
		unifiedorder.setSign(sign);
		unifiedorder.setBody("微薄利连锁商超");
		unifiedorder.setDetail("支付商品");
		/* unifiedorder.setAttach(attach); */
		unifiedorder.setOut_trade_no(or_num);
		unifiedorder.setTotal_fee(total_fee);
		unifiedorder.setSpbill_create_ip("127.0.0.1");
		unifiedorder.setTime_start(time_start);
		unifiedorder.setTime_expire(time_expire);
		unifiedorder.setNotify_url(backUrl);
		unifiedorder.setTrade_type("JSAPI");
		unifiedorder.setOpenid(openid);

		// 构造xml参数
		String xmlInfo = HttpXmlUtils.xmlH5Info(unifiedorder);
		System.err.println("XML格式:" + xmlInfo);
		weixinPost = HttpXmlUtils.httpsRequest(SITE.PAY_UNITY_ORDER.getMessage(), "POST", xmlInfo).toString();

		return weixinPost;
	}

}
