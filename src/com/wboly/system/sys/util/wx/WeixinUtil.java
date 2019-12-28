package com.wboly.system.sys.util.wx;

import java.util.Map;

import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.JsonUtil;

/**
 * @Name: 微信通用工具类
 * @Author: knick
 */
public class WeixinUtil {

	public static void main(String[] args) {
		// String jsapiTicket = getJsapiTicket(WeixinUtil.getAccessToken());
		// System.err.println(jsapiTicket);
		String at = WeixinUtil.getAccessToken();
		System.err.println(at);
		System.err.println(createMenu(at));
	}

	/**
	 * @Name: 创建自定义菜单
	 * @param at
	 *            访问秘钥(access token)
	 * @Author: knick
	 * @return true : 创建成功; false : 创建失败;
	 */
	public static boolean createMenu(String at) {

		String str = "{\"button\":[" + "{\"type\":\"view\"" + ",\"name\":\"微薄利\"" + ",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx9e24a0de9e3e136c&redirect_uri=https%3A%2F%2Fwww.duamai.com%2Fwxx-svr%2Fwxx%2Fresponse%2Fauthorizecode%3Fappid%3Dwx9e24a0de9e3e136c&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect\"}]}";
		System.err.println(str);
		String reqUrl = WX_MENU.CREATE_MENU.GET() + "?access_token=" + at;

		String result = HttpUtil.POST(reqUrl, str);

		System.out.println("微信创建自定义菜单返回:" + result);

		if (null == result || "".equals(result)) {
			return false;
		}

		try {
			Map<String, Object> map = JsonUtil.jsonStringToMap(result);

			Object errcode = map.get("errcode");

			Object errmsg = map.get("errmsg");

			if ("0".equals(errcode) && "ok".equals(errmsg)) {
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @Name: 根据 refresh_token 获取用户的 openId
	 * @Author: knick
	 */
	public static String getOpenIdByRefresh(String refresh_token) {

		if (refresh_token == null || "".equals(refresh_token))
			return null;

		String url = SITE.REFRESH_TOKEN.getMessage() + "?appid=" + WxConfig.appid
				+ "&grant_type=refresh_token&refresh_token=" + refresh_token;

		String result = HttpUtil.getUrl(url);
		if (result != null) {
			String errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40029")) {
				System.err.println("刷新 accessToken 的 code 无效");
				return "";
			}
			return JsonUtil.GetJsonValue(result, "openid");
		}
		return "";
	}

	/**
	 * @Name: 根据 code 获取用户的 openid 不弹出授权窗
	 * @Author: kcnik
	 */
	public static String getOpenIdByCode(String code) {
		if (code == null || code.equals("")) {
			System.err.println("微信支付:code = null");
			return "";
		}
		String url = SITE.ACCESS_TOKEN_OAUTH.getMessage() + "?appid=" + WxConfig.appid + "&secret=" + WxConfig.appsecret
				+ "&code=" + code + "&grant_type=authorization_code";

		String result = HttpUtil.getUrl(url);
		if (result != null) {
			String errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40029")) {
				System.err.println("微信支付:获取 accessToken 的 code 无效");
				return "";
			}
			return JsonUtil.GetJsonValue(result, "openid");
		}
		return "";
	}

	/**
	 * @Name: 根据 网页授权的 AccessToken 以及 openid 获取用户授权的信息
	 * @Author: knick
	 */
	public static String getOauthInfo(String accessToken, String openid) {

		if (null == accessToken || null == openid) {
			return null;
		}

		String url = SITE.USERINFO.getMessage() + "?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";

		String result = HttpUtil.getUrl(url);

		if (result != null && !"null".equals(result)) {

			String errcode = JsonUtil.GetJsonValue(result, "errcode");

			if (errcode != null && errcode.equals("40003")) {// 为openid无效
				System.err.println("获取用户信息 的 openid 无效");
				return "";
			}
			return result;
		}
		return "";
	}

	/**
	 * @Name: 通过 code 换取网页授权 access_token
	 * @Author: knick
	 */
	public static String getAccessToken(String code) {

		if (null == code || "".equals(code))
			return null;

		String url = SITE.ACCESS_TOKEN_OAUTH.getMessage() + "?appid=" + WxConfig.appid + "&secret=" + WxConfig.appsecret
				+ "&code=" + code + "&grant_type=authorization_code";

		String result = HttpUtil.getUrl(url);

		if (null != result && !"".equals(result) && !"null".equals(result)) {
			String errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40029")) {
				System.err.println("获取网页授权的 accessToken 的 code 无效");
				return "";
			}
			if (null == result || "".equals(result) || "null".equals(result)) {
				System.err.println("获取不到网页授权的 access_token");
				return "";
			}
			return result;
		}
		return "";
	}
	
	public static String getAccessTokens(String code) {

		if (null == code || "".equals(code))
			return null;

		String url = SITE.ACCESS_TOKEN_OAUTH.getMessage() + "?appid=" + WxConfig.appid + "&secret=" + WxConfig.appsecret
				+ "&code=" + code + "&grant_type=authorization_code";

		String result = HttpUtil.getUrl(url);

		if (null != result && !"".equals(result) && !"null".equals(result)) {
			String errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40029")) {
				System.err.println("获取网页授权的 accessToken 的 code 无效");
				return "";
			}
			if (null == result || "".equals(result) || "null".equals(result)) {
				System.err.println("获取不到网页授权的 access_token");
				return "";
			}
			return result;
		}
		return "";
	}

	/**
	 * @Name: 获取 jsapi_ticket
	 *        (由于获取jsapi_ticket的api调用次数非常有限，频繁刷新jsapi_ticket会导致api调用受限，影响自身业务，开发者必须在自己的服务全局缓存jsapi_ticket
	 *        。)
	 * @Author: knick
	 */
	public static String getJsapiTicket(String accessToken) {
		String ticket = SysCache.getJsapiTicket();
		if (null != ticket && !"".equals(ticket) && !"null".equals(ticket)) {
			return ticket;
		}
		String url = SITE.JSAPI_TICKET.URL + "?access_token=" + accessToken + "&type=jsapi";
		String result = HttpUtil.getUrl(url);
		String errcode = JsonUtil.GetJsonValue(result, "errcode");
		// 错误时微信会返回错误码等信息，示例为AppID无效错误
		if (errcode != null && errcode.equals("40001")) {
			System.err.println("初始化凭证失败,AccessToken 无效");
			return null;
		}
		SysCache.setJsapiTicket(result);
		ticket = JsonUtil.GetJsonValue(result, "ticket");
		if (null == ticket || "".equals(ticket) || "null".equals(ticket)) {
			System.err.println("获取不到ticket");
			return null;
		}
		return ticket;
	}

	/**
	 * @Name: 获取基础支持的 AccessToken
	 * @Author: knick
	 */
	public static String getAccessToken() {
		String accessToken = SysCache.getBaseAccessToken();
		if (accessToken != null && !accessToken.equals("") && !accessToken.equals("null")) {
			return accessToken;
		}
		System.out.println("WxConfig.appid=" + WxConfig.appid);
		System.out.println("WxConfig.appsecret=" + WxConfig.appsecret);
		String url = SITE.ACCESS_TOKEN_BASE.URL + "?grant_type=client_credential&appid=" + WxConfig.appid + "&secret="
				+ WxConfig.appsecret;
		String result = HttpUtil.getUrl(url);
		String errcode = JsonUtil.GetJsonValue(result, "errcode");
		// 错误时微信会返回错误码等信息，示例为AppID无效错误
		if (errcode != null && errcode.equals("40013")) {
			System.err.println("APPID 无效");
			return null;
		}
		SysCache.setBaseAccessToken(result);
		accessToken = JsonUtil.GetJsonValue(result, "access_token");
		if (null == accessToken || "".equals(accessToken) || "null".equals(accessToken)) {
			System.err.println("获取不到accessToken");
			return null;
		}
		return accessToken;
	}

	/**
	 * @Name: 微信 API 自定义菜单
	 * @Author: knick
	 */
	public enum WX_MENU {

		/**
		 * (POST) 创建自定义菜单接口 (需要 base access Token 参数)
		 */
		CREATE_MENU("https://api.weixin.qq.com/cgi-bin/menu/create"),

		/**
		 * (GET) 查询自定义菜单接口 (需要 base access Token 参数)
		 */
		FIND_MENU("https://api.weixin.qq.com/cgi-bin/menu/get"),

		/**
		 * (GET) 自定义菜单删除接口 (需要 base access Token 参数)
		 */
		DEL_MENU("https://api.weixin.qq.com/cgi-bin/menu/delete");

		String url;

		WX_MENU(String url) {
			this.url = url;
		};

		public String GET() {
			return url;
		}
	}

	/**
	 * @Name: 微信公众号开发的一些接口地址
	 * @Author: knick
	 */
	public enum SITE {

		/**
		 * 授权地址
		 */
		AUTHORIZE("https://open.weixin.qq.com/connect/oauth2/authorize"),
		/**
		 * 网页授权接口调用凭证(与基础支持的access_token不同)
		 */
		ACCESS_TOKEN_OAUTH("https://api.weixin.qq.com/sns/oauth2/access_token"),
		/**
		 * 获取基础支持的 accessToken
		 */
		ACCESS_TOKEN_BASE("https://api.weixin.qq.com/cgi-bin/token"),
		/**
		 * 获取用户授权信息URL
		 */
		USERINFO("https://api.weixin.qq.com/sns/userinfo"),
		/**
		 * 获取用户是否关注公众号的地址
		 */
		JUDGE_SUBSCRIBE("https://api.weixin.qq.com/cgi-bin/user/info"),
		/**
		 * 获取 jsapi_ticket 公众号用于调用微信JS接口的临时票据
		 */
		JSAPI_TICKET("https://api.weixin.qq.com/cgi-bin/ticket/getticket"),
		/**
		 * 获取凭证信息
		 */
		CLIENT_CREDENTIAL("https://api.weixin.qq.com/cgi-bin/token"),
		/**
		 * 微信统一下单接口
		 */
		PAY_UNITY_ORDER("https://api.mch.weixin.qq.com/pay/unifiedorder"),
		/**
		 * 微信查询订单
		 */
		PAY_QUERY_ORDER("https://api.mch.weixin.qq.com/pay/orderquery"),
		/**
		 * 刷新 网页授权接口调用凭证
		 */
		REFRESH_TOKEN("https://api.weixin.qq.com/sns/oauth2/refresh_token");

		private String URL;

		private SITE(String URL) {
			this.URL = URL;
		}

		public String getMessage() {
			return URL;
		}

	}

}