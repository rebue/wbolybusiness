package com.wboly.wechat.controller.oauth2;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.MacAddressUtil;
import com.wboly.system.sys.util.wx.WeixinUtil;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;
import com.wboly.system.sys.util.wx.WxConfig;
import com.wboly.wechat.service.user.WeChatUserService;

import rebue.wheel.NetUtils;
import rebue.wheel.OkhttpUtils;
import rebue.wheel.turing.JwtUtils;
import rebue.wheel.turing.SignUtils;

/**
 * @Author: nick
 */
@Controller
@RequestMapping(value = "/wechat/oauth2/")
public class WeChatOAuth2Controller extends SysController {

	@Autowired
	private WeChatUserService weChatUserService;
	
	private static final Logger _log = LoggerFactory.getLogger(WeChatOAuth2Controller.class);

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url = SysContext.USERCENTERURL + "/user/id/byusername?userName="
				+ URLEncoder.encode("15819931039", "utf-8");
		String userId = HttpUtil.getUrl(url);
		System.out.println(userId);
	}

	/**
	 * @Name: 微信授权用户信息
	 * @Author: nick
	 */
	@RequestMapping(value = "checkSignature")
	public void authWeiXin(@RequestParam("checkSignature") String uuid, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			String url = "";
			try {
				if (uuid != null && uuid.equals("login")) {
					String backUrl = WxConfig.onLineURL + "/wechat/oauth2/myPreReg.htm";// 微信回调地址
					String encodeUrl = URLEncoder.encode(backUrl, "UTF-8");// 对url进行编码
					url = SITE.AUTHORIZE.getMessage() + "?appid=" + WxConfig.appid + "&redirect_uri=" + encodeUrl
							+ "&response_type=code&scope=snsapi_userinfo&state=" + uuid;
				}
			} catch (UnsupportedEncodingException e) {
				System.err.println("请求微信接口异常:URL编码异常");
				e.printStackTrace();
			}
			if (!url.equals("")) {
				response.sendRedirect(url);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信授权回调地址 Title: wechatBackss Description:
	 * 
	 * @param request
	 * @param response
	 * @param code
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@RequestMapping(value = { "myPreReg" })
	@ResponseBody
	public ModelAndView wechatBackss(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> wxMaps)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		System.out.println("微信授权回调地址的请求参数为：" + wxMaps.toString());
		ModelAndView andView = new ModelAndView();

//		// 本地
//		String code = request.getParameter("code");
//		// 微信授权登陆获取到的用户信息
//		String userData = getUserData(code, response);
//		System.err.println("微信授权登陆返回的用户信息:" + userData);
//		ObjectMapper mapper = new ObjectMapper();
//		Map userMap = mapper.readValue(userData, Map.class); //
//		// 微信授权登陆获取到的用户信息
//		String openid = String.valueOf(userMap.get("openid"));
//		String unionid = String.valueOf(userMap.get("unionid"));
//		String nickname = String.valueOf(userMap.get("nickname"));
//		String headimgurl = String.valueOf(userMap.get("headimgurl"));
//
//		System.out.println("微信回调解码之前的参数为：" + String.valueOf(wxMaps));

		/*
		 * // 对微信回调参数进行解码 MapUtils.decodeUrl(wxMaps); // 微信回调登录校验 if
		 * (!SignUtils.verify1(wxMaps, SysContext.WXLOGINKEY)) {
		 * System.out.println("========微信校验出错======="); andView.addObject("errormsg",
		 * "微信校验出错"); andView.setViewName("/htm/wechat/prompt/errors"); return andView;
		 * }
		 */

		// 线上

		String openid = String.valueOf(wxMaps.get("openid"));
		String unionid = String.valueOf(wxMaps.get("unionid"));
		String nickname = String.valueOf(wxMaps.get("nickname"));
		String headimgurl = String.valueOf(wxMaps.get("headimgurl"));

		System.out.println("微信登录获取到的用户 信息为：openid=" + openid + "====unionid=" + unionid + "=====nickname=" + nickname
				+ "====headimgurl=" + headimgurl);
		String wxId = openid;
		if (unionid != null && !"".equals(unionid) && !"null".equals(unionid)) {
			wxId = unionid;
		}
		// 判断是否获取到微信授权登录的用户信息
		if (openid != null && !openid.equals("") && !openid.equals("null") && !openid.equals("[]")) {
			// 缓存openid到cookie
			CookiesUtil.setCookie(openid, response);

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("wxId", wxId);// 微信openid或微信unionid
			map.put("openid", openid);
			map.put("wxOpenid", openid);
			map.put("wxNickname", nickname);// 微信昵称
			map.put("wxFace", headimgurl);// 微信头像
			map.put("sysId", "damai-wx");// 应用编号
			map.put("userAgent", request.getHeader("User-Agent").replaceAll("-", ""));// ；浏览器信息
			map.put("mac", NetUtils.getFirstMacAddrOfLocalHost());
			map.put("ip", NetUtils.getFirstIpOfLocalHost());// 登录用户ip
			map.put("state", wxMaps.get("state"));
			System.err.println("微信登录的参数为：" + map.toString());
			// 微信用户登录
			Map<String, Object> wechatLoginResult = wechatLogin(request, response, map);
			System.out.println("微信用户登录的返回值为：" + String.valueOf(wechatLoginResult));
			long result = Long.parseLong(String.valueOf(wechatLoginResult.get("result")));
			request.getSession().setAttribute("userId", wechatLoginResult.get("userId"));
			System.out.println("微信用户登录的result的值为：" + result);
			if (result < 0) {
				System.out.println("==============登录失败，开始返回===========");
				andView.addObject("errormsg", wechatLoginResult.get("msg"));
				andView.setViewName("/htm/wechat/prompt/errors");
				return andView;
			} else {
				System.out.println("==============登录成功，开始返回===========");
				andView.addObject("JSURL", request.getRequestURL());
				andView.addObject("userId", wechatLoginResult.get("userId"));
				andView.addObject("onlineId", wechatLoginResult.get("onlineId"));
				andView.addObject("specId", wechatLoginResult.get("specId"));
				andView.setViewName(wechatLoginResult.get("msg").toString());
				return andView;
			}
		} else {
			System.err.println("微信回调:无法获取到授权登陆的用户数据");
			andView.addObject("errormsg", "无法获取到您的授权登陆信息");
			andView.setViewName("/htm/wechat/prompt/errors");
			return andView;
		}
	}

	/**
	 * 微信登录 2017.12.22 Title: wechatLogin Description:
	 * 
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> wechatLogin(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> map) throws JsonParseException, JsonMappingException, IOException, ParseException {
		Map<String, Object> m = new HashMap<String, Object>();
		String state = String.valueOf(map.get("state"));
		String msg = "/htm/wechat/index/index";
		String promoterId = "";
		String onlineId = "";
		String specId = "";
		if (state != null && !state.equals("") && !state.equals("null") && !state.equals("login") && state.contains(",")) {
			String[] states = state.split(",");
			promoterId = states[1];
			map.put("promoterId", promoterId);
			msg = "redirect:" + states[0].replaceFirst(SysContext.SYS_NAME, "");
			onlineId = states[2];
			specId = states[3];
			map.put("promoterId", promoterId);
			map.put("onlineId", onlineId);
		}
		System.out.println("微信用户登录的参数为：" + String.valueOf(map));
		String wechatLoginUrl = SysContext.USERCENTERURL + "/user/login/by/wx";
		String wechatLoginResults = OkhttpUtils.postByJsonParams(wechatLoginUrl, map)/*
																						  "{\"result\":\"1\", \"msg\":\"/htm/wechat/index/index\", \"userId\":\"472984484858298368\", \"expirationTime\":\"2018-8-10 20:18:18\", \"sign\":\"asdaadadadadadadda\"}"
																						  */;
		if (!wechatLoginResults.equals("") && !wechatLoginResults.equals("null") && wechatLoginResults != null) {
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> resultMap = objectMapper.readValue(wechatLoginResults, Map.class);
			String wechatLoginResult = JsonUtil.GetJsonValue(wechatLoginResults, "result");
			System.err.println("微信登录返回值为：" + wechatLoginResults);
			// 返回值说明 1：登录成功 0：缓存失败
			// -1：参数不正确(没有填写用户名称/QQ的ID/QQ昵称/QQ头像/微信的ID/微信昵称/微信头像/密码/应用ID/浏览器类型/MAC/IP)
			// -2：找不到用户信息 -3：密码错误 -4：账户被锁定 -5：用户用Email登录，但Email尚未通过验证 -6：用户用手机号登录，但手机号尚未通过验证
			if (wechatLoginResult.equals("1")) {

				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date ExpiryTime;
				try {
					ExpiryTime = format.parse(String.valueOf(resultMap.get("expirationTime")));
				} catch (ParseException e) {
					e.printStackTrace();
					throw e;
				}
				System.out.println(ExpiryTime);
				String sign = resultMap.get("sign").toString();
				System.out.println("添加jwt的签名为：" + sign);
				JwtUtils.addCookie(sign, ExpiryTime, response);
				System.err.println(map.get("wxNickname") + "：登录成功");
				String userId = JsonUtil.GetJsonValue(wechatLoginResults, "userId");
				map.put("userId", userId);
				if (onlineId != null && !onlineId.equals("") && !onlineId.equals("null")) {
					if (!promoterId.equals(userId)) {
						Map<String, Object> buyRelationMap = new HashMap<String, Object>();
						buyRelationMap.put("uplineUserId", promoterId);
						buyRelationMap.put("downlineUserId", userId);
						buyRelationMap.put("onlineId", onlineId);
						buyRelationMap.put("createTime", new Date());
						_log.info("添加用戶商品購買關係的參賽為: {}", buyRelationMap);
						String addBuyRelationResult = OkhttpUtils.postByJsonParams(SysContext.ORDERURL + "/ord/goodsbuyrelation", buyRelationMap);
						_log.info("添加用戶商品購買關係的返回值为: {}", addBuyRelationResult);
						Map<String, Object> addBuyRelationResultMap = objectMapper.readValue(addBuyRelationResult, Map.class);
						System.out.println(addBuyRelationResultMap.get("result"));
						if (!String.valueOf(addBuyRelationResultMap.get("result")).equals("1")) {
							m.put("result", -8);
							m.put("msg", "添加购买关系失败！");
							return m;
						}
					}
				}
				m = insertRegInfoAndCacheUserInfo(map);
				m.put("msg", msg);
				m.put("promoterId", promoterId);
				m.put("onlineId", onlineId);
				m.put("specId", specId);
			} else if (wechatLoginResult.equals("0")) {
				System.err.println(map.get("wxNickname") + "：缓存失败");
				m.put("result", wechatLoginResult);
				m.put("msg", "缓存失败！");
			} else if (wechatLoginResult.equals("-1")) {
				System.err.println(map.get("wxNickname") + "：参数不正确");
				m.put("result", wechatLoginResult);
				m.put("msg", "登录参数不正确！");
			} else if (wechatLoginResult.equals("-2")) {
				System.err.println(map.get("wxNickname") + "：找不到用户信息，用户未注册");
				// 微信注册
				Map<String, Object> regMap = wechatReg(request, response, map);
				String regResult = String.valueOf(regMap.get("result"));
				if (regResult.equals("1")) {
					map.put("userId", regMap.get("msg"));
					m = insertRegInfoAndCacheUserInfo(map);
				} else {
					m = regMap;
				}
				m.put("msg", msg);
				m.put("promoterId", promoterId);
				m.put("onlineId", onlineId);
				m.put("specId", specId);
			} else if (wechatLoginResult.equals("-3")) {
				System.err.println(map.get("wxNickname") + "：密码错误");
				m.put("result", wechatLoginResult);
				m.put("msg", "密码错误！");
			} else if (wechatLoginResult.equals("-4")) {
				System.err.println(map.get("wxNickname") + "：该账户已被锁定");
				m.put("result", wechatLoginResult);
				m.put("msg", "该账户已被锁定！");
			} else if (wechatLoginResult.equals("-5")) {
				System.err.println(map.get("wxNickname") + "：用户用Email登录，但Email尚未通过验证");
				m.put("result", wechatLoginResult);
				m.put("msg", "该邮箱尚未通过验证！");
			} else if (wechatLoginResult.equals("-6")) {
				System.err.println(map.get("wxNickname") + "：用户用手机号登录，但手机号尚未通过验证");
				m.put("result", wechatLoginResult);
				m.put("msg", "该手机号尚未通过验证！");
			}
		} else {
			System.err.println("登录返回空值，登录失败");
			m.put("result", -7);
			m.put("msg", "登录返回空值，登录失败！");
		}
		return m;
	}

	/**
	 * 微信注册 2017.12.22 Title: wechatReg Description:
	 * 
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public Map<String, Object> wechatReg(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> map) throws JsonParseException, JsonMappingException, IOException, ParseException {
		SignUtils.sign1(map, SysContext.LOGINSIGNKEY);
		System.out.println("微信用户注册的参数为：" + String.valueOf(map));
		// 微信用户注册URL
		String wechatRegUrl = SysContext.USERCENTERURL + "/user/reg/by/wx";
		String wechatRegResults = OkhttpUtils.postByJsonParams(wechatRegUrl, map);
		Map<String, Object> m = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		if (wechatRegResults != null && !wechatRegResults.equals("") && !wechatRegResults.equals("null")) {
			Map regMap = mapper.readValue(wechatRegResults, Map.class);
			String wechatRegResult = String.valueOf(regMap.get("result"));
			// 微信注册返回值说明：
			// 1：注册成功 0：缓存失败 -1：参数不正确
			// 没有填写用户登录名称/EMAIL/MOBILE/QQ的ID/QQ昵称/QQ头像/微信的ID/微信昵称/微信头像/登录密码/应用ID/浏览器类型/MAC/IP
			// 登录名称格式不正确 长度不能小于3，且不能大于20，不能为Email或手机号码的格式,Email格式不正确，手机号码格式不正确
			// -2：用户登录名已存在 -3：Email已存在 -4：手机号码已存在 -5：身份证号码已存在 -6：QQ的ID已存在 -7：微信的ID已存在
			if (wechatRegResult.equals("1")) {
				System.err.println(map.get("wxNickname") + "：注册成功");
				Map<String, Object> results = wechatLogin(request, response, map);
				m.put("result", wechatRegResult);
				m.put("msg", regMap.get("userId"));
			} else if (wechatRegResult.equals("0")) {
				System.err.println(map.get("wxNickname") + "：在注册时出现缓存失败");
				m.put("result", wechatRegResult);
				m.put("msg", "缓存失败！");
			} else if (wechatRegResult.equals("-1")) {
				System.err.println(map.get("wxNickname") + "：在注册时出现参数不正确");
				m.put("result", wechatRegResult);
				m.put("msg", "参数不正确！");
			} else if (wechatRegResult.equals("-2")) {
				System.err.println("用户名：" + map.get("wxNickname") + "已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该用户名已存在！");
			} else if (wechatRegResult.equals("-3")) {
				System.err.println(map.get("wxNickname") + "：正在注册的邮箱已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该邮箱已存在！");
			} else if (wechatRegResult.equals("-4")) {
				System.err.println(map.get("wxNickname") + "：正在注册的手机号码已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该手机号码已存在！");
			} else if (wechatRegResult.equals("-5")) {
				System.err.println(map.get("wxNickname") + "：身份证编号已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该身份证编号已存在！");
			} else if (wechatRegResult.equals("-6")) {
				System.err.println(map.get("wxNickname") + "：正在注册的QQ已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该QQ已存在！");
			} else if (wechatRegResult.equals("-7")) {
				System.err.println(map.get("wxNickname") + "：正在注册的微信已存在");
				m.put("result", wechatRegResult);
				m.put("msg", "该微信号已存在！");
			}
		} else {
			m.put("result", -8);
			m.put("msg", "微信注册返回空值，注册失败！");
		}
		return m;
	}

	/**
	 * 添加用户注册信息和缓存用户信息 Title: insertLoginRecordAndRegInfo Description:
	 * 
	 * @param map
	 * @return
	 * @date 2018年4月24日 下午12:34:19
	 */
	public Map<String, Object> insertRegInfoAndCacheUserInfo(Map<String, Object> map) {
		Map<String, Object> m = new HashMap<String, Object>();
		// 用户编号
		String userId = String.valueOf(map.get("userId"));
		// 用户名称
		String userName = String.valueOf(map.get("wxNickname"));
		String userjson = "{\"userId\":" + userId + ",\"userName\":\"" + userName + "\",\"img\":\"" + map.get("wxFace")
				+ "\",\"openid\":\"" + map.get("openid") + "\"}";
		SysCache.setWechatUser(map.get("openid").toString(), userjson);// 缓存用户信息
		System.err.println("微信登录或者缓存缓存用户信息成功");
		m.put("result", userId);
		m.put("userId", userId);

		// 获取当前时间戳
		long currentTime = System.currentTimeMillis() / 1000;
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("userId", userId);
		maps.put("userName", userName);
		maps.put("lastLoninType", 1);
		maps.put("userSource", "");
		maps.put("regTime", currentTime);
		maps.put("lastLoginMarket", NetUtils.getFirstIpOfLocalHost());

		// 根据用户编号查询用户是否存在
		String userIds = weChatUserService.selectUserInformation(maps);
		if (userIds == null || userIds.equals("") || userIds.equals("null")) {
			// 添加用户注册信息
			weChatUserService.insertUserRegInformation(maps);
		} else {
			weChatUserService.updateUserRegInformation(maps);
		}
		return m;
	}

	/**
	 * @Name: 根据 code 获取用户信息
	 * @Author: nick
	 */
	public static String getUserData(String code, HttpServletResponse response) {
		try {
			String json = WeixinUtil.getAccessToken(code);
			if (json != null) {
				String openid = JsonUtil.GetJsonValue(json, "openid");
				if ("".equals(openid)) {
					return null;
				}
				String accessToken = JsonUtil.GetJsonValue(json, "access_token");
				System.err.println("最新版本：openid:" + openid + "\taccessToken:" + accessToken);
				CookiesUtil.setCookie(openid, response);
				SysCache.setRefreshData(MD5CodeUtil.md5(openid), json);
				String userInfo = WeixinUtil.getOauthInfo(accessToken, openid);
				if (userInfo == null) {
					return null;
				}
				SysCache.setWeChatAuthData(openid, userInfo);
				return userInfo;
			}
		} catch (Exception e) {

		}
		return null;
	}

	/**
	 * 用户登录并绑定微信
	 * 
	 * @param request
	 * @param response 2018年2月7日17:19:21
	 */
	@RequestMapping("loginAndBind")
	public void loginAndBind(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("user"); // 用户名
		String pwd = request.getParameter("password"); // 用户密码

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userName", userName);
		map.put("loginPswd", pwd);
		map.put("appId", 11);
		map.put("userAgent", request.getHeader("User-Agent").replaceAll("-", ""));
		map.put("mac", MacAddressUtil.getLocalMac().replaceAll("-", ""));
		map.put("ip", IpUtil.getIp(request));
		System.err.println("用户登录的参数为：" + map.toString());
		String loginResult = HttpUtil.postUrl(SysContext.USERCENTERURL + "/user/login/by/user/name", map);
		System.err.println("用户登录返回值为：" + loginResult);
		if (loginResult == null || loginResult.equals("") || loginResult.equals("null")) {
			this.render(response, "{\"message\":\"登录失败\",\"flag\":false}");
			return;
		}
		String result = JsonUtil.getJSONValue(loginResult, "result");
		System.err.println("用户登录结果返回为：" + result);
		String userId = "";
		if (result.equals("1")) {
			userId = JsonUtil.getJSONValue(loginResult, "userId");
		} else if (result.equals("0")) {
			this.render(response, "{\"message\":\"缓存失败\",\"flag\":false}");
			return;
		} else if (result.equals("-1")) {
			this.render(response, "{\"message\":\"参数不正确\",\"flag\":false}");
			return;
		} else if (result.equals("-2")) {
			this.render(response, "{\"message\":\"找不到用户信息\",\"flag\":false}");
			return;
		} else if (result.equals("-3")) {
			this.render(response, "{\"message\":\"密码错误\",\"flag\":false}");
			return;
		} else if (result.equals("-4")) {
			this.render(response, "{\"message\":\"您的账号已被锁定\",\"flag\":false}");
			return;
		} else if (result.equals("-5")) {
			this.render(response, "{\"message\":\"该邮箱未注册\",\"flag\":false}");
			return;
		} else if (result.equals("-6")) {
			this.render(response, "{\"message\":\"该手机号未注册\",\"flag\":false}");
			return;
		} else {
			this.render(response, "{\"message\":\"登录失败\",\"flag\":false}");
			return;
		}
		// 获取cookie中的openid
		String openid = CookiesUtil.getCookieKey(request);
		System.err.println("从cookie中获取到的微信openid为：" + openid);
		if (openid == null || openid.equals("") || openid.equals("null")) {
			System.err.println("从cookie中获取不到openid");
			return;
		}
		// 获取已缓存的微信用户信息
		String wechatUserInfo = SysCache.getWeChatAuthData(openid);
		System.err.println("从缓存中获取到的微信用户信息为：" + wechatUserInfo);
		if (wechatUserInfo == null || wechatUserInfo.equals("") || wechatUserInfo.equals("null")) {
			System.err.println("从缓存中获取不到微信授权信息");
			return;
		}
		String bindTheUser = bindTheUser(request, userId);
		this.render(response, bindTheUser);
	}

	/**
	 * 注册新用户
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws ParseException
	 */
	@RequestMapping("registrationAndBinding")
	public void registrationAndBinding(HttpServletRequest request, HttpServletResponse response)
			throws JsonParseException, JsonMappingException, IOException, ParseException {
		String openid = CookiesUtil.getCookieKey(request); // 获取cookie中的微信openid
		System.err.println("在cookie中获取到的微信openid为：" + openid);
		if (openid == null || openid.equals("") || openid.equals("null")) {
			System.err.println("从cookie中获取不到openid");
			this.render(response, "{\"message\":\"获取微信openid失败\",\"flag\":false}");
			return;
		}

		// 获取已缓存的微信用户信息
		String wechatUserInfo = SysCache.getWeChatAuthData(openid);
		System.err.println("从缓存中获取到的微信用户信息为：" + wechatUserInfo);
		if (wechatUserInfo == null || wechatUserInfo.equals("") || wechatUserInfo.equals("null")) {
			System.err.println("从缓存中获取不到微信授权信息");
			this.render(response, "{\"message\":\"获取微信授权信息失败\",\"flag\":false}");
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("wxId", JsonUtil.GetJsonValue(wechatUserInfo, "unionid"));// 微信openid或微信unionid
		map.put("openid", JsonUtil.GetJsonValue(wechatUserInfo, "openid"));
		map.put("wxNickname", JsonUtil.GetJsonValue(wechatUserInfo, "nickname"));// 微信昵称
		map.put("wxFace", JsonUtil.GetJsonValue(wechatUserInfo, "headimgurl"));// 微信头像
		map.put("appId", 11);// 应用编号
		map.put("userAgent", request.getHeader("User-Agent").replaceAll("-", ""));// ；浏览器信息
		map.put("mac", MacAddressUtil.getLocalMac());// 登录用户mac
		map.put("ip", IpUtil.getIp(request));// 登录用户ip

		// 新用户注册
		Map<String, Object> wechatRegs = wechatReg(request, response, map);
		String wechatReg = String.valueOf(wechatRegs.get("result"));
		if (wechatReg.equals("11")) {
			this.render(response, "{\"message\":\"注册并登陆成功\",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":\"" + wechatRegs.get("msg") + "\",\"flag\":false}");
		}
	}

	/**
	 * 通过验证码登录并绑定
	 * 
	 * @param request
	 * @param response 2018年2月8日18:01:29
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("verifyLoginAndBind")
	public void verifyLoginAndBind(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		// 用户输入的手机号码
		String code = request.getParameter("code");
		// 用户输入的用户名/手机号码/邮箱号
		String userName = request.getParameter("userName").trim();
		// 用户输入的手机号/邮箱
		String phone = request.getParameter("phone").trim();
		String cachedCode = "";
		if (phone.contains("@")) {
			cachedCode = SysCache.getEmailVerificationCode(phone);
		} else {
			cachedCode = SysCache.getMobileVerificationCode(phone);
		}
		if (cachedCode != null && !cachedCode.equals("") && !cachedCode.equals("null")) {
			if (cachedCode.equals(code)) {
				System.err.println("查询用户编号的参数为：" + userName);
				String url = SysContext.USERCENTERURL + "/user/id/byusername?userName="
						+ URLEncoder.encode(phone, "utf-8");
				String userId = HttpUtil.getUrl(url);
				System.err.println("查询用户编号的返回值为：" + userId);
				if (userId != null && !userId.equals("") && !userId.equals("null")) {
					// 获取微信openid
					String openid = CookiesUtil.getCookieKey(request);
					if (openid != null && !openid.equals("") && !openid.equals("null")) {
						// 获取已缓存的微信用户信息
						String wechatUserInfo = SysCache.getWeChatAuthData(openid);
						if (wechatUserInfo != null && !wechatUserInfo.equals("") && !wechatUserInfo.equals("null")) {
							String bindTheUser = bindTheUser(request, userId);
							this.render(response, bindTheUser);
						} else {
							System.err.println("获取不到已缓存的微信用户信息");
							this.render(response, "{\"message\":\"获取不到微信授权信息\",\"flag\":false}");
							return;
						}
					} else {
						System.err.println("获取不到微信openid");
						this.render(response, "{\"message\":\"获取不到微信授权信息\",\"flag\":false}");
						return;
					}
				} else {
					System.err.println("获取不到用户编号");
					this.render(response, "{\"message\":\"该用户不存在\",\"flag\":false}");
					return;
				}
			} else {
				System.err.println("已发送的验证码跟输入的验证码不一致");
				this.render(response, "{\"message\":\"输入的验证码与发送的验证码不一致，请重新输入\",\"flag\":false}");
			}
		} else {
			System.err.println("获取不到用户输入的验证码");
			this.render(response, "{\"message\":\"获取不到您发送的验证码，请重新发送\",\"flag\":false}");
		}
	}

	/**
	 * 绑定用户
	 * 
	 * @param request
	 * @return 2018年2月8日09:35:01
	 */
	public String bindTheUser(HttpServletRequest request, String userId) {
		// 获取cookie中的openid
		String openid = CookiesUtil.getCookieKey(request);
		System.err.println("从cookie中获取到的微信openid为：" + openid);
		if (openid == null || openid.equals("") || openid.equals("null")) {
			System.err.println("从cookie中获取不到openid");
			return "{\"message\":\"获取微信openid失败\",\"flag\":false}";
		}
		// 获取已缓存的微信用户信息
		String wechatUserInfo = SysCache.getWeChatAuthData(openid);
		System.err.println("从缓存中获取到的微信用户信息为：" + wechatUserInfo);
		if (wechatUserInfo == null || wechatUserInfo.equals("") || wechatUserInfo.equals("null")) {
			System.err.println("从缓存中获取不到微信授权信息");
			return "{\"message\":\"获取微信授权信息失败\",\"flag\":false}";
		}

		String unionid = JsonUtil.getJSONValue(wechatUserInfo, "unionid"); // 微信unionid
		String nickname = JsonUtil.getJSONValue(wechatUserInfo, "nickname"); // 微信昵称
		String headimgurl = JsonUtil.getJSONValue(wechatUserInfo, "headimgurl");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("wxId", unionid);
		map.put("wxNickname", nickname);
		map.put("wxFace", headimgurl);
		map.put("appId", 11);
		map.put("userAgent", request.getHeader("User-Agent").replaceAll("-", ""));
		map.put("mac", MacAddressUtil.getLocalMac().replaceAll("-", ""));
		map.put("ip", IpUtil.getIp(request));

		System.err.println("用户绑定微信的参数为：" + map.toString());
		String results = HttpUtil.postUrl(SysContext.USERCENTERURL + "/user/bind/wx", map);
		System.err.println("用户绑定微信返回值为：" + results);
		if (results != null && !results.equals("") && !results.equals("null")) {
			String result = JsonUtil.getJSONValue(results, "result");
			if (result.equals("1")) {
				return "{\"message\":\"登录并绑定微信成\",\"flag\":true}";
			} else if (result.equals("0")) {
				return "{\"message\":\"参数不正确\",\"flag\":false}";
			} else if (result.equals("-1")) {
				return "{\"message\":\"该用户不存在\",\"flag\":false}";
			} else if (result.equals("-2")) {
				return "{\"message\":\"您已被锁定\",\"flag\":false}";
			} else if (result.equals("-3")) {
				return "{\"message\":\"当前微信已绑定其他账号\",\"flag\":false}";
			}
		} else {
			return "{\"message\":\"登录并绑定微信失败\",\"flag\":false}";
		}
		return "{\"message\":\"微信失败\",\"flag\":false}";
	}

}
