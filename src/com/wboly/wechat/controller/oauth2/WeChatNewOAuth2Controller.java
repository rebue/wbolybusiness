package com.wboly.wechat.controller.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.MacAddressUtil;
import com.wboly.system.sys.util.OrderKeyRandom;
import com.wboly.system.sys.util.wx.WeixinUtil;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;
import com.wboly.system.sys.util.wx.WxConfig;

/**
 * @Name: 新的微信授权登陆控制层
 * @Author: knick
 */
@Controller
public class WeChatNewOAuth2Controller extends SysController {

	/**
	 * 验证参数
	 */
	String verifyParm = "vbolywxlogin";

	/**
	 * @Name: 跳转至微信授权登陆
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/newoauth/login" })
	public ModelAndView authJump() {

		ModelAndView mav = new ModelAndView();
		String salt = OrderKeyRandom.GenerateOrderNo();// 随机生成秘钥
		String verifykey = MD5CodeUtil.md5(MD5CodeUtil.md5(verifyParm + salt) + MD5CodeUtil.md5(salt));
		String backUrl = WxConfig.onLineURL + "/wechat/newoauth/back.htm?p=" + salt;// 微信回调地址
		String encodeUrl = "";
		try {
			// 对url进行编码
			encodeUrl = URLEncoder.encode(backUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			mav.setViewName("redirect:/wechat/user/userCenter.htm?p=500");
			return mav;
		}

		String authUrl = SITE.AUTHORIZE.getMessage() + "?appid=" + WxConfig.appid + "&redirect_uri=" + encodeUrl
				+ "&response_type=code&scope=snsapi_userinfo&state=" + verifykey;

		mav.setViewName("redirect:" + authUrl);

		return mav;
	}

	/**
	 * @Name: 微信授权回调地址
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/newoauth/back" }, params = { "code",
			"state" }, produces = "application/json; charset=utf-8")
	public ModelAndView oauthBack(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mav = new ModelAndView();

		HashMap<String, Object> hmap = new HashMap<String, Object>();

		try {
			// code作为换取access_token的票据，每次用户授权带上的code将不一样，code只能使用一次，5分钟未被使用自动过期。
			String code = request.getParameter("code");

			// 自定义携带字段
			String state = request.getParameter("state");

			// 随机数
			String random = request.getParameter("p");

			// 验证秘钥
			String verifykey = MD5CodeUtil.md5(MD5CodeUtil.md5(verifyParm + random) + MD5CodeUtil.md5(random));

			System.out.println("salt:" + random);
			System.out.println("state:" + state);
			System.out.println("verifykey:" + verifykey);

			// 判断校验通过
			if (state.equals(verifykey)) {

				System.out.println("秘钥验证成功");

				// 授权登陆获取到的用户信息
				String json = WeixinUtil.getAccessTokens(code);

				if (null != json && !"".equals(json)) {

					// 获取 openid
					String openid = JsonUtil.GetJsonValue(json, "openid");

					if ("".equals(openid)) {
						mav.setViewName("redirect:/wechat/user/userCenter.htm?p=500");
						return mav;
					}

					// 通讯秘钥
					String accessToken = JsonUtil.GetJsonValue(json, "access_token");

					// 获取用户授权的用户信息
					String userInfo = WeixinUtil.getOauthInfo(accessToken, openid);

					if (userInfo == null || "".equals(userInfo)) {
						mav.setViewName("redirect:/wechat/user/userCenter.htm?p=500");
						return mav;
					}

					System.err.println("new_授权的用户信息:" + userInfo);

					SysCache.setWeChatAuthData(MD5CodeUtil.md5(openid), userInfo);

					// 微信开放平台信息编号(微信的ID)[补充说明]
					String unionid = JsonUtil.GetJsonValue(userInfo, "unionid");

					// 用户昵称
					String nickname = JsonUtil.GetJsonValue(userInfo, "nickname");

					// 用户头像信息
					String headimgurl = JsonUtil.GetJsonValue(userInfo, "headimgurl");

					// 请求登录地址
					String reqUrl = "http://121.41.27.191:8080/user/wechatlogin";

					// 登录秘钥
					String dzCookieKey = "1234567890abcdefghijklmnopqrstuvwxyz";

					// 验证秘钥
					String key = MD5CodeUtil.md5(unionid + dzCookieKey); // 验证密匙 MD5(unionid+dzCookieKey)

					hmap.put("key", key);
					hmap.put("unionid", unionid);// 微信开放平台信息编号(微信的ID)[补充说明]
					hmap.put("nickname", nickname);// 用户昵称
					hmap.put("headimgurl", headimgurl);// 用户头像
					hmap.put("source", 4);// 登录的来源 编码与用户注册的注册来源一致
					hmap.put("mac", MacAddressUtil.getLocalMac());// 登录mac
					hmap.put("ip", IpUtil.getIp2(request));// 登录ip

					String result = HttpUtil.postUrl(reqUrl, hmap);

					// 2: 登录并注册成功 登录时如果发现没有此微信号，先进行注册再登录
					// 1: 登录成功;
					// 0: 参数不正确 没有填写微信的ID/登录来源/MAC/IP
					// -1: 账号被锁定
					System.err.println("new_登录返回参数:" + result);

					Integer re = null;
					Long userId = null;
					try {
						re = Integer.parseInt(JsonUtil.GetJsonValue(result, "result"));// 处理结果
						userId = Long.parseLong(JsonUtil.GetJsonValue(result, "userId"));// 用户编号
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

					// 登陆成功
					if (null != re && re > 0 && userId != null) {

						hmap.clear();

						hmap.put("userId", userId);// 用户编号
						hmap.put("userName", nickname);// 用户昵称
						hmap.put("img", headimgurl);// 用户头像

						System.err.println("授权登陆:登陆的用户数据_:" + hmap);

						// 写入 cookie
						CookiesUtil.setCookie(openid, response);

						// 用户信息存缓存
						SysCache.setWechatUser(MD5CodeUtil.md5(openid), JsonUtil.ObjectToJson(hmap));

						mav.setViewName("redirect:/wechat/user/userCenter.htm?p=200");

						return mav;
					}

					// 账号被锁定
					if (null != re && re == -1) {

						mav.setViewName("redirect:/wechat/user/userCenter.htm?p=501");

						return mav;
					}

					mav.setViewName("redirect:/wechat/user/userCenter.htm?p=500");

					return mav;
				}

				mav.setViewName("redirect:/wechat/user/userCenter.htm?p=500");
				return mav;

			} else {
				System.out.println("秘钥验证失败");
				mav.setViewName("/htm/wechat/prompt/404");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			hmap = null;
		}

		return mav;
	}

}
