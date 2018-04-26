package com.wboly.wechat.controller.notice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.wx.MessageUtil;
import com.wboly.system.sys.util.wx.WxConfig;
import com.wboly.system.sys.util.wx.XmlParseUtil;
import com.wboly.system.sys.util.wx.aes.WXBizMsgCrypt;

/**
 * @Name: 接收微信推送信息的控制层
 * @Author: knick
 */
@Controller
@RequestMapping(value = { "/wechat/receive" })
public class WeChatNoticeController extends SysController {

	/**
	 * 用于存储超时未响应的信息标识
	 */
	LinkedHashMap<String, Object> lhmap = new LinkedHashMap<String, Object>();

	/**
	 * 接收用户推送的消息 Title: getAcceptPush Description:
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/push" }, params = { "signature", "timestamp", "nonce" }, method = RequestMethod.GET)
	@ResponseBody
	public String getAcceptPush(HttpServletRequest request, HttpServletResponse response) {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		if (verifyServiceURL(signature, echostr, timestamp, nonce)) {
			return echostr;
		} else {
			return "error";
		}
	}

	@RequestMapping(value = { "/push" }, method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	@SuppressWarnings("rawtypes")
	public String setAcceptPush(HttpServletRequest request, HttpServletResponse response) {
		try {
			// xml请求解析
			Map map = XmlParseUtil.doXMLParse(XmlParseUtil.getContent(request.getInputStream(), "UTF-8"));
			if (map == null || map.equals("") || map.equals("null")) {
				return "success";
			}
			System.err.println("xml请求解析到的数据为：" + String.valueOf(map));
			Object msgType = map.get("MsgType");// 消息类型
			System.err.println("接收到的消息类型为" + msgType);

			Object toUserName = map.get("ToUserName"); // 开发者微信号（服务端）
			Object fromUserName = map.get("FromUserName"); // 发送方账户（openid）（客户端）
			Object createTime = map.get("CreateTime");// 消息创建时间（整型）

			// 判断接收到的消息是否为文本消息
			if ("text".equals(msgType)) {

				Object Content = map.get("Content"); // 文本消息内容
				Object msgId = map.get("MsgId"); // 消息id，64位整型

				// 判断该条信息是否重复请求
				if (!lhmap.containsKey(msgId.toString())) {
					lhmap.put(msgId.toString(), msgId);

					String textMessage = MessageUtil.textMessage(fromUserName, toUserName, Content);
					lhmap.remove(msgId);
					// 返回消息内容
					return textMessage;

				} else {
					lhmap.remove(msgId);
					System.err.println("存在重复的啦");
					return "success";
				}
				// 判断接收到的消息是否是文本消息
				// event : 事件;
			} else if ("event".equals(msgType)) {
				// 排重标识
				String flag = fromUserName + "" + createTime;

				if (!lhmap.containsKey(flag)) {

					lhmap.put(flag, flag);
					Object event = map.get("Event"); // 事件类型，subscribe(订阅)、unsubscribe(取消订阅)
					System.err.println("事件类型:" + event);

					// 未关注公众号的用户扫描进行关注
					if ("subscribe".equals(event)) {
						System.err.println("用户未关注");
						String textMessage = MessageUtil.textMessage(fromUserName, toUserName, "您好，欢迎关注微薄利连锁商超！");
						System.err.println(textMessage);
						lhmap.remove(flag);
						request.setCharacterEncoding("UTF-8");
						// 返回消息内容
						return textMessage;
						// 用户已关注公众号的情况下扫描二维码
					} else if ("SCAN".equals(event)) {
						System.err.println("用户已关注");
						lhmap.remove(flag);
						// 返回消息内容
						return "success";
						// 已关注的用户取消关注
					} else if ("unsubscribe".equals(event)) {
						System.err.println("用户取消关注");
						return "success";
						// 用户 点击 事件
					} else if ("CLICK".equals(event)) {

						System.err.println("用户点击菜单");

						Object eventKey = map.get("EventKey"); // 事件KEY值，与自定义菜单接口中KEY值对应
						System.err.println(eventKey);
						String textMessage = "success";

						// 1 : 使用帮助;
						if ("1".equals(eventKey)) {
							textMessage = MessageUtil.textMessage(fromUserName, toUserName, "您点击的是使用帮助 ！");
						} else if ("2".equals(eventKey)) {// 2 : 关于我们
							textMessage = MessageUtil.textMessage(fromUserName, toUserName, "您点击的是关于我们 ！");
						}
						lhmap.remove(flag);
						// 返回消息内容
						return textMessage;
						// 用户点击 网页类型 事件
					} else if ("VIEW".equals(event)) {
						System.err.println("用户点击进入网页");
						Object eventKey = map.get("EventKey"); // 事件KEY值，与自定义菜单接口中KEY值对应
						System.err.println(eventKey);
						lhmap.remove(flag);
						// 返回消息内容
						return "success";
						// 获取用户地理位置信息
					} else if ("LOCATION".equals(event)) {
						System.err.println("用户地理位置信息");
						lhmap.remove(flag);
						return "success";
					} else {
						lhmap.remove(flag);
						return "success";
					}
				} else {
					System.err.println("事件有重复请求啦");
					return "success";
				}
			} else {
				return "success";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "success";
		}
	}

	/**
	 * @Name: 解密
	 * @Author: knick
	 */
	@SuppressWarnings("rawtypes")
	public String decodeAESKey(Map map) {
		String decryptMsg = "";
		try {

			WXBizMsgCrypt pc = new WXBizMsgCrypt(WxConfig.token, WxConfig.EncodingAESKey, WxConfig.appid);

			String format = "<xml><ToUserName><![CDATA[" + map.get("ToUserName")
					+ "]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";

			String fromXML = String.format(format, map.get("Encrypt"));

			decryptMsg = pc.decryptMsg(String.valueOf(map.get("msg_signature")), String.valueOf(map.get("timestamp")),
					String.valueOf(map.get("nonce")), fromXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return decryptMsg;
	}

	/**
	 * @Name: 验证服务器地址 的真实性
	 * @Author: knick
	 */
	public boolean verifyServiceURL(String signature, String echostr, String timestamp, String nonce) {

		boolean flag = false;

		try {
			// 判断该字段是否为null,如果不为空,则需要验证服务器
			if (null != echostr && !"".equals(echostr)) {

				List<String> params = new ArrayList<String>();

				params.add(WxConfig.token);
				params.add(timestamp);
				params.add(nonce);

				// 1. 将token、timestamp、nonce三个参数进行字典序排序
				Collections.sort(params, new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return o1.compareTo(o2);
					}
				});

				// 2.将三个参数字符串拼接成一个字符串进行sha1加密
				String sha1 = MD5CodeUtil.SHA1(params.get(0) + params.get(1) + params.get(2));

				// 判断是否验证成功(认证服务器 URL 的真实性)
				if (sha1.equals(signature)) {
					System.err.println("微信:服务器地址认证成功");
					flag = true;
				} else {
					System.err.println("微信:服务器地址认证失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

}
