package com.wboly.modules.controller.Login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.wboly.rpc.Client.UserRPCClient;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.OrderKeyRandom;
import com.wboly.system.sys.util.RegexValidateUtil;
import com.wboly.system.sys.util.RsaUtil;
import com.wboly.system.sys.util.SmsUtil;
import com.wboly.system.sys.util.WriterJsonUtil;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;

@Controller
public class LoginController extends SysController {

	/**
	 * @Name: 通过code换取网页授权access_token
	 * @Author: nick
	 * @param code
	 *            填写获取的code参数
	 * @return access_token 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同;
	 *         expires_in access_token接口调用凭证超时时间，单位（秒）; refresh_token
	 *         用户刷新access_token; openid
	 *         用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID; scope
	 *         用户授权的作用域，使用逗号（,）分隔;
	 */
	//@RequestMapping(value = { "/app/user/getoauthbycode" }, params = { "code", "mac", "source", "agentInformation",
	//		"areaId" }, method = RequestMethod.POST)
	public void getOauthData(@RequestParam("code") String code, @RequestParam("mac") String mac,
			@RequestParam("source") String source, @RequestParam("agentInformation") String agentInformation,
			@RequestParam("areaId") String areaId, HttpServletRequest requset, HttpServletResponse response,
			HttpServletRequest request) {

		if (code == null) {
			this.render(response, "{\"message\":\"code invalid\",\"flag\":false}");
			return;
		}
		// 为 APP 授权登陆的默认地址
		String url = SITE.ACCESS_TOKEN_OAUTH.getMessage()
				+ "?appid=wx735b3e0ac0ec1cae&secret=26daba56e47d47c44f08411429099470&code=" + code
				+ "&grant_type=authorization_code";

		String result = HttpUtil.getUrl(url);
		if (result != null) {
			String errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40029")) {
				System.err.println("APP 获取 accessToken 的 code 无效");// code 无效
				this.render(response, "{\"message\":\"code invalid\",\"flag\":false}");
				return;
			}
			System.err.println("APP 获取 ");
			url = SITE.USERINFO.getMessage() + "?access_token=" + JsonUtil.GetJsonValue(result, "access_token")
					+ "&openid=" + JsonUtil.GetJsonValue(result, "openid") + "&lang=zh_CN";

			result = HttpUtil.getUrl(url);
			errcode = JsonUtil.GetJsonValue(result, "errcode");
			if (errcode != null && errcode.equals("40003")) {// 为openid无效
				System.err.println("获取用户信息 的 openid 无效");
				this.render(response, "{\"message\":\"openid invalid\",\"flag\":false}");
				return;
			}
			System.err.println("APP 授权得到的用户信息:" + result);
			try {
				appwechatlogin(request, response, JsonUtil.GetJsonValue(result, "unionid"), mac, source, result,
						agentInformation, areaId);
			} catch (TException e) {
				e.printStackTrace();
			}
			return;
		}
		this.render(response, "{\"message\":\"return null\",\"flag\":false}");
		return;
	}

	/**
	 * @Name: 修改微信用户名称
	 * @Author: nick
	 */
	//@RequestMapping(value = "/app/user/wechatupdateusername", method = RequestMethod.POST, params = { "userid",
	//		"username" })
	public void updateWeiXinUserName(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("userid") String userid, @RequestParam("username") String username) {

		if (userid == null || userid.equals("")) {
			this.render(response, "{\"userid\":-101}");// 用户编号不能为空
			return;
		}
		if (username == null || username.equals("")) {
			this.render(response, "{\"userid\":-102}");// 用户名称不能为空
			return;
		}

		String verify = verifyIsMinGanCihui(username);

		if (!verify.equals("200")) {
			this.render(response, "{\"userid\":" + verify + "}");
			return;
		}

		verify = verifyUserNameAndEmailAndMobile(username);

		if (!verify.equals("200")) {
			this.render(response, "{\"userid\":" + verify + "}");
			return;
		}

		String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
		String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号 域名
		String url = innerInterface + "user/updateusername?";// 修改用户名

		Map<String, Object> map = new HashMap<String, Object>();

		String key = MD5CodeUtil.md5GB2312(userid + username + dzCookieKey);

		map.put("key", key);// 验证秘钥
		map.put("userid", userid);// 用户编号
		map.put("username", username);// 新用户名

		System.err.println("APP修改用户名参数::" + map);
		String content = HttpUtil.postUrl(url, map);
		System.err.println("APP修改用户名返回的参数:" + content);
		if (content == null || content.equals("")) {
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}
		// （-4:非首次修改用户名[非“vboly_”开头];-3:未能找到用户信息;-2:密匙不正确;-1:用户名已存在;0:参数有误;>0:修改成功）
		this.render(response, "{\"userid\":" + content + "}");
	}

	/**
	 * @Name: 检测用户名和邮箱手机号是否已被使用
	 * @Author: knick
	 * @return -106:网络异常; -107:参数有误; -108:秘钥不正确; -110:用户名已存在; 200:不存在
	 */
	public static String verifyUserNameAndEmailAndMobile(String detectionParm) {

		Map<String, Object> map = new HashMap<String, Object>();

		String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
		String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
		String url = innerInterface + "user/isexistsbyusername?";// 检查手机号用户名邮箱是否已经存在
		String key = MD5CodeUtil.md5GB2312(detectionParm + dzCookieKey);

		map.put("key", key);
		map.put("username", detectionParm);

		String content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			System.err.println("检测是否已存在失败");
			return "-106";
		}

		if (content.equals("-1")) {
			System.err.println("检测参数有误");
			return "-107";
		}
		if (content.equals("-2")) {
			System.err.println("检测秘钥不正确");
			return "-108";
		}
		if (content.equals("1")) {
			System.err.println("该用户名已存在");
			return "-110";
		}
		return "200";
	}

	/**
	 * @Name: 检测该用户名是否存在敏感词汇
	 * @Author: knick
	 * @return -106:网络异常; -107:参数有误; -108:秘钥不正确; -109:存在敏感词汇; 200:不存在
	 */
	public static String verifyIsMinGanCihui(String detectionParm) {

		Map<String, Object> map = new HashMap<String, Object>();

		String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
		String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
		String url = innerInterface + "user/isnotexistsnameword?";// 检查是否有敏感词汇
		String key = MD5CodeUtil.md5GB2312(detectionParm + dzCookieKey);

		map.put("key", key);
		map.put("username", detectionParm);

		String content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			System.err.println("检测用户名失败");
			return "-106";
		}

		if (content.equals("-1")) {
			System.err.println("检测用户名参数有误");
			return "-107";
		}
		if (content.equals("-2")) {
			System.err.println("检测用户名秘钥不正确");
			return "-108";
		}
		if (content.equals("0")) {
			System.err.println("该用户名存在敏感词汇");
			return "-109";
		}
		return "200";
	}

	/**
	 * @Name: 注册微信用户(邮箱注册以及微信注册)
	 * @Author: knick
	 */
	//@RequestMapping(value = "/app/user/wechatregister", method = RequestMethod.POST, params = { "unionid", "username",
	//		"code", "mac", "password" })
	public void registerWeiXinUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("unionid") String unionid, @RequestParam("username") String username,
			@RequestParam("password") String password, @RequestParam("email") String email,
			@RequestParam("mobile") String mobile, @RequestParam("source") String source,
			@RequestParam("code") String code, @RequestParam("mac") String mac) {

		Map<String, Object> map = new HashMap<String, Object>();

		if (username == null || username.equals("")) {
			this.render(response, "{\"userid\":-103}");// 用户名不能为空
			return;
		}

		String verify = verifyIsMinGanCihui(username);
		if (!verify.equals("200")) {
			this.render(response, "{\"userid\":" + verify + "}");
			return;
		}

		verify = verifyUserNameAndEmailAndMobile(username);
		if (!verify.equals("200")) {
			this.render(response, "{\"userid\":" + verify + "}");
			return;
		}

		// 检查是以什么方式进行注册
		if (mobile == null || mobile.equals("")) {
			if (email == null || email.equals("")) {
				System.err.println("手机以及邮箱必填一个");
				this.render(response, "{\"userid\":-101}");// 手机号码以及邮箱必填一个
				return;
			} else {
				map.put("email", email);// 邮箱
			}
		} else {
			map.put("mobile", mobile);// 电话
		}

		if (unionid == null || unionid.equals("")) {
			this.render(response, "{\"userid\":-102}");// unionid 不能为空
			return;
		}

		RsaUtil RU = new RsaUtil();

		String rsaDecode = RU.RsaDecode(password);
		if (rsaDecode == null || rsaDecode.equals("")) {
			this.render(response, "{\"userid\":-104}");// 登陆密码不能为空
			return;
		}
		if (code == null || code.equals("")) {
			this.render(response, "{\"userid\":-105}");// 验证码不能为空
			return;
		}
		password = rsaDecode;
		if (source == null || source.equals("")) {
			source = "4";
		}
		if (source.equals("4") || source.equals("5")) {
			map.put("mac", mac == null || mac.equals("") ? "000000000000" : mac);// mac地址
		} else {
			map.put("ip", IpUtil.getIp(request));// web端登陆的ip地址
		}

		String popularize_url = SysContext.DOMAINNAME;// 推广来源URL(可为空)
		String popularize_uid = request.getParameter("popularize_uid");// 推广编号(没有写0)
		String nickname = request.getParameter("nickname");// 微信昵称(可为空)
		String headimgurl = request.getParameter("headimgurl");// 微信用户头像(可为空)
		String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
		String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
		String key = MD5CodeUtil.md5(unionid + dzCookieKey);
		String url = innerInterface + "user/wechatregister?";// 微信注册

		map.put("key", key);// 秘钥
		map.put("popularize_uid", popularize_uid);// 推广编号
		map.put("popularize_url", popularize_url);// 推广来源
		map.put("nickname", nickname);// 微信昵称
		map.put("headimgurl", headimgurl);// 微信头像地址
		map.put("unionid", unionid);// 微信唯一标识
		map.put("password", password);// 登陆密码
		map.put("username", username);// 用户名
		map.put("source", source);// 登陆来源
		map.put("code", code);// 验证码

		String content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}
		content = content.replaceAll(System.lineSeparator(), "");
		// （-8:手机号码格式不正确;-7:手机号码已存在;-6:email已存在;-5:注册成功,但绑定失败;-4:密匙不正确;-3:验证码不正确;-2:验证码会话超时;-1:用户名已存在;0:参数有误;1:注册成功）
		this.render(response, "{\"userid\":" + content + "}");
	}

	/**
	 * @Name: 微信绑定商超已有账户
	 * @Author: nick
	 */
	@RequestMapping(value = "/app/user/appwechatbind", method = RequestMethod.POST, params = { "unionid", "loginpswd",
			"loginname", "nickname", "headimgurl" })
	public void bindWechatUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("unionid") String unionid, @RequestParam("loginname") String loginname,
			@RequestParam("loginpswd") String loginpswd, @RequestParam("nickname") String nickname,
			@RequestParam("headimgurl") String headimgurl) {

		if (unionid == null || unionid.equals("")) {
			this.render(response, "{\"userid\":-101}");// unionid 不能为空
			return;
		}
		RsaUtil RU = new RsaUtil();
		String password = RU.RsaDecode(loginpswd);
		if (password == null || password.equals("")) {
			this.render(response, "{\"userid\":-102}");// loginpswd 不能为空
			return;
		}
		if (loginname == null || loginname.equals("")) {
			this.render(response, "{\"userid\":-103}");// loginname 邮箱或用户名不能为空(邮箱用户名选一)
			return;
		}
		if (nickname == null || nickname.equals("")) {
			this.render(response, "{\"userid\":-104}");// nickname 微信用户名不能为空
			return;
		}
		if (headimgurl == null || headimgurl.equals("")) {
			this.render(response, "{\"userid\":-105}");// headimgurl 微信用户头像不能为空
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();

		String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
		String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
		String key = MD5CodeUtil.md5(unionid + dzCookieKey);
		String url = innerInterface + "user/getuseridbyunionid?";// 检测该微信用户是否已绑定

		map.put("key", key);// 验证秘钥
		map.put("unionid", unionid);// 微信唯一标识

		String content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			System.err.println("检测微信用户是否已绑定失败");
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}

		content = content.replace(System.lineSeparator(), "");
		if (Integer.parseInt(content) > 0) {
			System.err.println("微信号:" + unionid + "\t已绑定");
			this.render(response, "{\"userid\":-109}");// 该微信用户已绑定过商超账户
			return;
		}

		map.clear();

		url = innerInterface + "user/isnotexistsnameword?";// 检查是否有敏感词汇

		nickname = "vboly_" + nickname;

		key = MD5CodeUtil.md5GB2312(nickname + dzCookieKey);

		map.put("key", key);
		map.put("username", nickname);

		content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			System.err.println("检测用户名失败");
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}

		if (content.equals("-1")) {
			System.err.println("检测用户名参数有误");
			this.render(response, "{\"userid\":-107}");// 检测用户名参数有误
			return;
		}
		if (content.equals("-2")) {
			System.err.println("检测用户名秘钥不正确");
			this.render(response, "{\"userid\":-108}");// 检测用户名秘钥有误
			return;
		}
		if (content.equals("0")) {
			System.err.println("该用户名存在敏感词汇");
			nickname = "vboly_" + OrderKeyRandom.GetIntString(6);
		}
		System.err.println(nickname);
		url = innerInterface + "user/isexistsbyusername?";// 检查手机号用户名邮箱是否已经存在

		content = HttpUtil.postUrl(url, map);

		if (content == null || content.equals("")) {
			System.err.println("检测用户名是否已存在失败");
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}

		if (content.equals("-1")) {
			System.err.println("检测用户名参数有误");
			this.render(response, "{\"userid\":-107}");// 检测用户名参数有误
			return;
		}
		if (content.equals("-2")) {
			System.err.println("检测用户名秘钥不正确");
			this.render(response, "{\"userid\":-108}");// 检测用户名秘钥有误
			return;
		}
		if (content.equals("0")) {
			System.err.println("该用户名已存在");
			nickname = "vboly_" + OrderKeyRandom.GetIntString(6);
		}
		map.clear();
		url = innerInterface + "user/binduseraccountbywechat?";// 微信绑定已有账户

		key = MD5CodeUtil.md5(unionid + dzCookieKey);

		map.put("key", key);// 验证秘钥
		map.put("loginname", loginname);// 用户名或者邮箱
		map.put("loginpswd", password);// 密码
		map.put("unionid", unionid);// 微信唯一标识
		map.put("nickname", nickname);// 微信用户名
		map.put("headimgurl", headimgurl);// 微信用户头像地址

		content = HttpUtil.postUrl(url, map);

		System.err.println("请求绑定信息接口返回的数据:" + content);

		if (content == null || content.equals("")) {
			System.err.println("绑定商超已有账户失败");
			this.render(response, "{\"userid\":-106}");// 网络异常
			return;
		}

		content = content.replaceAll(System.lineSeparator(), "");

		// （0:参数有误;-1:密匙不正确;-2:未能找到用户信息;-3:登录密码错误;-4:用户账号被锁定;-5:绑定失败;-6:用户已被绑定;>0绑定成功）
		this.render(response, "{\"userid\":" + content + "}");
	}

	/**
	 * @Name: APP 微信登陆
	 * @Author: knick
	 * @throws TException
	 */
	//@RequestMapping(value = "/app/user/appwechatlogin", method = RequestMethod.POST, params = { "unionid", "source" })
	public void appwechatlogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("unionid") String unionid, @RequestParam("mac") String mac,
			@RequestParam("source") String source, String oauth,
			@RequestParam("agentInformation") String agentInformation, @RequestParam("areaId") String areaId)
			throws TException {

		if (unionid == null || "".equals(unionid)) {// 微信唯一标识
			this.render(response, "{\"userid\":-101}");// unionid 不能为空
		} else {
			if (mac == null || "".equals(mac)) {// 手机mac地址不能为空
				this.render(response, "{\"userid\":-102}");
			} else {

				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
				String key = MD5CodeUtil.md5(unionid + dzCookieKey);
				String loginUrl = innerInterface + "user/wechatlogin?";// 微信登陆
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("unionid", unionid);// 微信唯一标识
				map.put("key", key);
				map.put("source", source);// web = 1;android = 4;ios = 5;获取登录来源
				if (source != null) {
					if (source.equals("4") || source.equals("5")) {
						map.put("mac", mac == null || mac.equals("") ? "000000000000" : mac);
					} else {
						map.put("ip", IpUtil.getIp(request));
					}
				} else {
					this.render(response, "{\"userid\":-104}");// 登陆来源不能为空
					return;
				}

				System.err.println("微信登陆的参数:" + map);
				String content = HttpUtil.postUrl(loginUrl, map);
				System.err.println("登陆返回的参数:" + content);

				// 0:参数不正确;-1:找不到用户信息;-2:密码错误;-3:账号被锁定;-4:缺少业务用户信息;-5:用户邮箱未验证;-6:密匙不正确;
				if (content == null || "".equals(content) || "null".equals(content)) {
					this.render(response, "{\"userid\":-103}");// 网络异常
				} else {

					content = content.replaceAll(System.lineSeparator(), "");

					String postresult = JsonUtil.getJSONValue(content, "postResult");
					int con = Integer.parseInt(postresult);
					if (con <= 0) {
						if (con == -1) {
							if (oauth != null) {
								this.render(response,
										"{\"userid\":" + con + ",\"username\":" + JsonUtil.ObjectToJson(oauth) + "}");
								return;
							}
						}
						this.render(response, "{\"userid\":" + con + "}");
					} else {
						String userid = JsonUtil.getJSONValue(content, "userid");
						String username = JsonUtil.getJSONValue(content, "userName");

						Map<String, String> hmap = new HashMap<String, String>();
						hmap.put("userId", userid);
						hmap.put("userName", username);
						String regTime = JsonUtil.getJSONValue(content, "regTime");
						if (regTime.indexOf("Date") > -1) {
							regTime = regTime.substring(regTime.indexOf("(") + 1, regTime.indexOf(")"));
						}
						hmap.put("regTime", regTime);
						hmap.put("userSource", "1");
						hmap.put("loginType", source);
						hmap.put("loginMarker", mac);
						hmap.put("agentInfo", agentInformation);
						hmap.put("areaId", areaId);

						UserRPCClient userService = new UserRPCClient();
						int i = userService.client.UpUserInfo(hmap);
						if (i == 0) {
							System.out.println("app微信登录记录登录信息失败");
						}
						userService.close();

						key = MD5CodeUtil.md5(userid + dzCookieKey);
						String url = innerInterface + "user/getscoreexperience?";

						Map<String, Object> param = new HashMap<String, Object>();
						param.put("userid", userid);
						param.put("key", key);
						String result = HttpUtil.postUrl(url, param);

						if (result != null && !result.equals("") && !"null".equals(result)) {
							result = result.replace(System.lineSeparator(), "");
						}

						if (JsonUtil.getJSONValue(result, "postResult").equals("1")) {
							// String isgroupuser=vue.getIsgroupuser();//是否圈子用户
							String score = JsonUtil.getJSONValue(result, "score");// 当前积分
							String experience = JsonUtil.getJSONValue(result, "experience");// 当前经验值
							int grade = Integer.valueOf(JsonUtil.getJSONValue(result, "grade"));// 当前等级
							int maxexperience = 0;

							switch (grade) {
							case 1:
								maxexperience = 250;
								break;
							case 2:
								maxexperience = 1000;
								break;
							case 3:
								maxexperience = 3000;
								break;
							case 4:
								maxexperience = 10000;
								break;
							case 5:
								maxexperience = 20000;
								break;
							case 6:
								maxexperience = 20000;
								break;
							}

							String img = SysContext.USERPHOTOURL + "&uid=" + userid;// 获取用户的头像

							String userjson = "{\"userid\":" + userid + ",\"username\":\"" + username + "\",\"img\":\""
									+ img + "\",\"score\":" + score + ",\"experience\":" + experience + ",\"grade\":"
									+ grade + ",\"maxexperience\":" + maxexperience + "}";
							// 用户信息存缓存

							SysCache.setUserJson(userid, userjson);

							this.render(response, userjson);

						} else {
							System.err.println("返回数据异常,不在规定范围内");
							this.render(response, "{\"userid\":-105}");
						}
					}
				}
			}
		}
	}

	/**
	 * 注册时查询用户名是否已经被使用
	 * 
	 * @author dwh
	 * @param request
	 * @param response
	 */
	//@RequestMapping("/app/user/registerverifiactionName")
	public void registerverifiactionUsersName(HttpServletRequest request, HttpServletResponse response) {
		String usersName = "";
		try {
			usersName = new String(request.getParameter("uname").getBytes(), "gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String json = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", MD5CodeUtil.md5GB2312(usersName + SysContext.CONFIGMAP.get("dzCookieKey")));
		map.put("username", usersName);

		if (RegexValidateUtil.checkEmail(usersName)) {
			String result = HttpUtil
					.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/isexistsbyusername?", map)
					.replaceAll("\r\n", "");
			if (result.equals("0")) {
				json = "{\"info\":\"邮箱可以使用\",\"result\":\"1\"}";
			} else {
				json = "{\"info\":\"邮箱已被使用\",\"result\":\"0\"}";
			}
		} else if (RegexValidateUtil.checkMobileNumber(usersName)) {
			String result = HttpUtil
					.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/isexistsbyusername?", map)
					.replaceAll("\r\n", "");
			if (result.equals("0")) {
				json = "{\"info\":\"手机可以使用\",\"result\":\"1\"}";
			} else {
				json = "{\"info\":\"手机已被使用\",\"result\":\"0\"}";
			}
		} else {
			String result = HttpUtil.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/isnotexistsnameword",
					map);
			if (result != null) {
				result = result.replaceAll(System.lineSeparator(), "");
				if (!result.equals("1")) {
					json = "{\"info\":\"您填写的昵称不允许使用！\",\"result\":\"0\"}";
					WriterJsonUtil.writerJson(response, json);
					return;
				}
			}
			result = HttpUtil.postUrl(SysContext.CONFIGMAP.get("innerInterface") + "user/isexistsbyusername?", map)
					.replaceAll("\r\n", "");
			if (result.equals("0")) {
				json = "{\"info\":\"用户名可以使用\",\"result\":\"1\"}";
			} else {
				json = "{\"info\":\"用户名已被使用\",\"result\":\"0\"}";
			}
		}
		WriterJsonUtil.writerJson(response, json);
	}

	/**
	 * 注册获取短信验证码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/getregistercodebysms" })
	public void getsmscode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String username = request.getParameter("uname");// 用户名
		String mobile = request.getParameter("mobile");// 手机号码
		String mac = request.getParameter("mac");// 获取用户MAC地址

		// return
		// 大于0为成功，-101用户不能为空，-102手机号码不能为空，-103MAC地址不能为空，-104账号验证失败,-105验证手机号失败,-106手机已绑定,-107用户名存在,-108:已关闭短信运营商的服务
		// -201 30分钟内已发送三次，-202代码异常

		if (username == null || "".equals(username)) {// 手机验证
			this.render(response, -101);
		} else {
			if (mobile == null || "".equals(mobile)) {// 手机验证
				this.render(response, -102);
			} else {
				if (mac == null || "".equals(mac)) {// MAC地址验证
					this.render(response, -103);
				} else {

					String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
					String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

					String mobileurl = innerInterface + "user/isexistsbymobile?";
					String mobilekey = MD5CodeUtil.md5(mobile + dzCookieKey);

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("mobile", mobile);
					map.put("key", mobilekey);
					String mobileresult = HttpUtil.postUrl(mobileurl, map);

					if (mobileresult == null || "".equals(mobileresult)) {
						this.render(response, -105);
					} else {

						mobileresult = mobileresult.replace(System.lineSeparator(), "");

						int mobilenum = Integer.parseInt(mobileresult.trim());
						if (mobilenum != 0) {
							this.render(response, -106);
						} else {
							String key = MD5CodeUtil.md5GB2312(username + dzCookieKey);// 秘钥加密
							String url = innerInterface + "user/isexistsbyusername?";
							Map<String, Object> mapname = new HashMap<String, Object>();
							mapname.put("username", username);
							mapname.put("key", key);
							// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
							String content = HttpUtil.postUrl(url, mapname);

							if (content == null || "".equals(content.trim())) {// 账号验证
								this.render(response, -104);
							} else {

								content = content.replace(System.lineSeparator(), "");

								if ("0".equals(content.trim())) {
									// vblSmsService.appSendSms(mac,
									// mobile,1);//发送短信验证码
									String num = SmsUtil.sendLoginSMS(request, mobile, mac);
									if (num.equals("-103")) {
										this.render(response, "-108");// 已关闭短信运营商的服务
										return;
									}

									if (null == num || "".equals(num) || "null".equals(num)) {
										num = "-202";
									}
									this.render(response, num);
								} else if ("1".equals(content.trim())) {
									this.render(response, -107);// -107用户名存在
								} else {
									this.render(response, -104);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 注册获取邮箱验证码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/getregistercodebyemail" })
	public void getregistercodebyemail(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");// 用户名
		String email = request.getParameter("email");// 邮箱

		// retrun 大于0发送成功，-101用户名不能为空，-102邮箱不能为空,-103账号验证失败，-104访问超时,-105用户名存在
		// 0接口参数有误，-1获取验证码频率太快,-2邮件发送失败,-3接口密匙不正确,-4邮箱已存在
		if (loginname == null || "".equals(loginname)) {// 用户名验证
			this.render(response, -101);
		} else {
			if (email == null || "".equals(email)) {// 邮箱验证
				this.render(response, -102);
			} else {

				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
				String key = MD5CodeUtil.md5GB2312(loginname + dzCookieKey);// 秘钥加密
				String url = innerInterface + "user/isexistsbyusername?";

				Map<String, Object> maploginname = new HashMap<String, Object>();
				maploginname.put("username", loginname);
				maploginname.put("key", key);
				// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
				String content = HttpUtil.postUrl(url, maploginname);

				if (content == null || "".equals(content) || "null".equals(content)) {// 账号验证
					this.render(response, -104);
				} else {

					content = content.replace(System.lineSeparator(), "");

					int num = Integer.parseInt(content.trim());
					if (num == 1) {
						this.render(response, -105);
					} else if (num != 0 && num != 1) {
						this.render(response, -103);
					} else {

						// 注册-发送注册验证码到用户注册邮箱
						String sendEmailUrl = innerInterface + "user/postregisteremail?";
						String postkey = MD5CodeUtil.md5(email + dzCookieKey);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("email", email);
						map.put("uid", 0);
						map.put("postkey", postkey);
						String context = HttpUtil.postUrl(sendEmailUrl, map);
						if (context == null || "".equals(context) || "null".equals(context)) {
							this.render(response, -104);
						} else {
							context = context.replace(System.lineSeparator(), "");
							JSONObject json = new JSONObject(context);
							this.render(response, json.get("postResult").toString());
						}
					}
				}
			}
		}
	}

	/**
	 * 短信验证码注册
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/smsregister" })
	public void smsRegister(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String userName = request.getParameter("username");// 用户名
		String passWord = request.getParameter("password");// 登录密码
		// int source=4;//注册来源(1微薄利web端，2 微薄利wap端，3 QQ登录，4安卓客户端，5苹果客户端)
		String source = request.getParameter("source");// 登录密码
		String mobile = request.getParameter("mobile");// 手机号码
		String smscode = request.getParameter("smscode");// 短信验证码
		// String
		// popularize_uid=request.getParameter("popularize_uid");//推广者用户编号
		String mac = request.getParameter("mac");// 获取用户MAC地址

		String rsa = new RsaUtil().RsaDecode(passWord);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			passWord = rsa;
		}

		// return 大于0注册成功，-101用户名不能为空 -102登录密码不能为空
		// -103手机号码不能为空，-104短信验证码错误，-105mac地址不能为空,-106服务器繁忙
		// 0参数错误，-1用户名已存在 ，-2验证码失效，-3验证码不正确，-4接口密匙错误，-6手机号码已存在，-7手机格式不正确
		if (userName == null || "".equals(userName)) {// 用户名验证
			this.render(response, -101);
		} else {
			if (passWord == null || "".equals(passWord)) {// 密码验证
				this.render(response, -102);
			} else {
				if (mobile == null || "".equals(mobile)) {// 手机号验证
					this.render(response, -103);
				} else {
					if (smscode == null || "".equals(smscode)) {// 短信验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {
							boolean result = SmsUtil.ValidateSmsCode(mobile, mac, smscode);// 短信验证码验证
							if (result) {

								int uid = 0;// 查找推广者ID

								String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
								String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

								// MD5加密
								String postkey = MD5CodeUtil.md5GB2312(userName + source + dzCookieKey);
								Map<String, Object> param = new HashMap<String, Object>();
								param.put("userName", userName);
								param.put("passWord", passWord);
								param.put("source", source);
								param.put("postkey", postkey);
								param.put("mac", mac);
								param.put("popularize_url", SysContext.DOMAINNAME);
								param.put("popularize_uid", uid);
								param.put("mobile", mobile);

								// 用户注册
								String url = innerInterface + "user/register?";
								String content = HttpUtil.postUrl(url, param);
								// 请求结果：1注册成功，0参数错误，-1用户名已存在
								// ，-2验证码会话超时，-3验证码不正确，-4密匙错误，-5电子邮箱已存在，-6手机号码已存在，-7手机格式不正确
								if (content != null && !"".equals(content) && !"null".equals(content)) {

									content = content.replace(System.lineSeparator(), "");

									/********************* 注册成功记录登录信息 ************************/
									String userId = JsonUtil.getJSONValue(content, "userid");

									Map<String, String> hmap = new HashMap<String, String>();
									hmap.put("userId", userId);
									hmap.put("userName", userName);
									String regTime = JsonUtil.getJSONValue(content, "regTime");
									if (regTime.indexOf("Date") > -1) {
										regTime = regTime.substring(regTime.indexOf("(") + 1, regTime.indexOf(")"));
									}
									hmap.put("regTime", regTime);
									hmap.put("userSource", "1");
									hmap.put("loginType", source);
									hmap.put("loginMarker", mac);
									hmap.put("agentInfo", "");
									hmap.put("areaId", "0");

									UserRPCClient userService = new UserRPCClient();
									int i = userService.client.UpUserInfo(hmap);
									if (i == 0) {
										System.out.println("记录登录信息失败");
									}
									userService.close();
									/******************* 注册成功记录登录信息 **************************/
									this.render(response, content);// 1注册成功
								} else {
									this.render(response, -106);// -6服务器繁忙
								}
							} else {
								this.render(response, -104);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 邮箱验证码注册
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/emailregister" })
	public void emailRegister(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String userName = request.getParameter("username");// 用户名
		String passWord = request.getParameter("password");// 登录密码
		// int source=4;//注册来源(1:微薄利网页端;2:微薄利手机网页端;3:微薄利手机应用端;
		// 4:微薄利Android端;5:微薄利IOS端;6:商超网页端;7:商超Android端;8:商超IOS端)
		String source = request.getParameter("source");// 登录密码
		String email = request.getParameter("email");// 邮箱
		String emailcode = request.getParameter("emailcode");// 邮箱验证码
		String mac = request.getParameter("mac");// 获取用户MAC地址

		String rsa = new RsaUtil().RsaDecode(passWord);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			passWord = rsa;
		}

		// return 大于0注册成功 -101用户名不能为空 -102登录密码不能为空
		// -103邮箱不能为空，-104邮箱验证码错误，-105mac地址不能为空,-106服务器繁忙
		// 0参数错误，-1用户名已存在 ，-2验证码失效，-3验证码不正确，-4密匙错误，-5电子邮箱已存在
		if (userName == null || "".equals(userName)) {// 用户名验证
			this.render(response, -101);
		} else {
			if (passWord == null || "".equals(passWord)) {// 密码验证
				this.render(response, -102);
			} else {
				if (email == null || "".equals(email)) {// 邮箱验证
					this.render(response, -103);
				} else {
					if (emailcode == null || "".equals(emailcode)) {// 邮箱验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {
							long uid = 0;// 查找推广者ID

							String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
							String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
							// MD5加密
							String postkey = MD5CodeUtil.md5GB2312(userName + source + dzCookieKey);
							Map<String, Object> param = new HashMap<String, Object>();
							param.put("userName", userName);
							param.put("passWord", passWord);// SecurityEncode.decoderOrEncoder(passWord,
															// 1470294692)
							param.put("source", source);
							param.put("postkey", postkey);
							param.put("mac", mac);
							param.put("popularize_url", SysContext.DOMAINNAME);
							param.put("popularize_uid", uid);
							param.put("email", email);
							param.put("emailCode", emailcode);

							// 用户注册
							String url = innerInterface + "user/register?";
							String content = HttpUtil.postUrl(url, param);
							// 请求结果：1注册成功，0参数错误，-1用户名已存在
							// ，-2验证码会话超时，-3验证码不正确，-4密匙错误，-5电子邮箱已存在，-6手机号码已存在，-7手机格式不正确
							if (content != null && !"".equals(content) && !"null".equals(content)) {

								content = content.replace(System.lineSeparator(), "");

								/********************* 注册成功记录登录信息 ************************/
								String userId = JsonUtil.getJSONValue(content, "userid");

								Map<String, String> hmap = new HashMap<String, String>();
								hmap.put("userId", userId);
								hmap.put("userName", userName);
								String regTime = JsonUtil.getJSONValue(content, "regTime");
								if (regTime.indexOf("Date") > -1) {
									regTime = regTime.substring(regTime.indexOf("(") + 1, regTime.indexOf(")"));
								}
								hmap.put("regTime", regTime);
								hmap.put("userSource", "1");
								hmap.put("loginType", source);
								hmap.put("loginMarker", mac);
								hmap.put("agentInfo", "");
								hmap.put("areaId", "0");

								UserRPCClient userService = new UserRPCClient();
								int i = userService.client.UpUserInfo(hmap);
								if (i == 0) {
									System.out.println("记录登录信息失败");
								}
								userService.close();
								/******************* 注册成功记录登录信息 **************************/
								this.render(response, content);// 1注册成功
							} else {
								this.render(response, -106);// -6服务器繁忙
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 用户登录
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/applogin" }, method = RequestMethod.POST)
	public void applogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String username = request.getParameter("username");// 获取用户名
		String password = request.getParameter("password");// 获取密码
		String mac = request.getParameter("mac");// 获取用户MAC地址
		String source = request.getParameter("source");// 获取登录来源
		String agentInformation = request.getParameter("agentInformation");// 获取用户设备信息
		String areaId = request.getParameter("areaId");// 获取用户区域编号

		// return
		// 大于0登录成功，-101用户名不能为空,-102密码不能为空,-103mac地址不能为空，-104访问超时,-105密码错误3次，需要改密码才能登陆，-106账号不存在,-107登录失败
		// -2:密码错误,-3:账号被锁定,-5:用 户邮箱未验证
		if (username != null && !"".equals(username)) {
			if (password != null && !"".equals(password)) {
				if (mac != null && !"".equals(mac)) {

					int num = SysCache.getLoginErrorNum(username);// 查询用户错误次数
					if (num < 4) {
						RsaUtil RU = new RsaUtil();
						String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
						String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

						String postkey = MD5CodeUtil.md5(dzCookieKey);

						String url = innerInterface + "user/login?";

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("userName", username);
						String rsaDecode = RU.RsaDecode(password);
						if (rsaDecode == null) {
							map.put("passWord", password);
						} else {
							password = rsaDecode.split(" ")[0];
							map.put("passWord", password);
						}
						map.put("postkey", postkey);
						map.put("mac", mac);
						map.put("source", source.equals("2") || source.equals("3") ? 7 : source);

						String content = HttpUtil.postUrl(url, map);

						// 0:参数不正确;-1:找不到用户信息;-2:密码错误;-3:账号被锁定;-4:缺少业务用户信息;-5:用户邮箱未验证;-6:接口密匙不正确
						if (content != null && !"".equals(content.trim()) && !"null".equals(content.trim())) {
							content = content.replace(System.lineSeparator(), "");
							String postresult = JsonUtil.getJSONValue(content, "postResult");
							String lockReason = JsonUtil.getJSONValue(content, "lockReason");
							if (postresult != null && !"".equals(postresult)) {
								int con = Integer.parseInt(postresult);
								if (con > 0) {
									String userId = JsonUtil.getJSONValue(content, "userid");
									if (userId != null && !"".equals(userId)) {
										// 七天的时间毫秒数
										Long sevenDays = (long) (1000 * 60 * 60 * 24 * 7);
										// 每次访问的时间(服务器系统时间)
										Long currentTime = System.currentTimeMillis();

										if (SysCache.getUserJson(userId) != null) {

											if (rsaDecode != null) {
												if (JsonUtil.getJsonValue(SysCache.getUserJson(userId),
														"pastTime") != null) {
													// 第一次登录的时间
													Long startTime = Long.parseLong(JsonUtil
															.getJsonValue(SysCache.getUserJson(userId), "pastTime")
															.toString());
													// 有效时间
													Long entTime = Long.parseLong(JsonUtil
															.getJsonValue(SysCache.getUserJson(userId), "pastTime")
															.toString()) + sevenDays;
													// 因为有的版本没更新,全部更新之后就可以去掉了
													if (startTime != 0) {
														if (startTime < currentTime && currentTime < entTime) {
															System.err.println(username + ":登录___在有效的时间范围内");
														} else {
															SysCache.removeUserJson(userId);
															this.render(response, "{\"userid\":-108}");// 已超过自动登录的有效期
															return;
														}
													}
												}
											}
										}

										Map<String, String> hmap = new HashMap<String, String>();
										hmap.put("userId", userId);
										hmap.put("userName", username);
										String regTime = JsonUtil.getJSONValue(content, "regTime");
										if (regTime.indexOf("Date") > -1) {
											regTime = regTime.substring(regTime.indexOf("(") + 1, regTime.indexOf(")"));
										}
										hmap.put("regTime", regTime);
										hmap.put("userSource", "1");
										hmap.put("loginType", source);
										hmap.put("loginMarker", mac);
										hmap.put("agentInfo", agentInformation);
										hmap.put("areaId", areaId);

										UserRPCClient userService = new UserRPCClient();
										int i = userService.client.UpUserInfo(hmap);
										if (i == 0) {
											System.out.println("记录登录信息失败");
										}
										userService.close();

										if (userId != null && !userId.equals("")) {

											String key = MD5CodeUtil.md5(userId + dzCookieKey);
											url = innerInterface + "user/getscoreexperience?";

											Map<String, Object> param = new HashMap<String, Object>();
											param.put("userid", userId);
											param.put("key", key);
											String result = HttpUtil.postUrl(url, param).replace(System.lineSeparator(),
													"");

											if (JsonUtil.getJSONValue(result, "postResult").equals("1")) {

												String score = JsonUtil.getJSONValue(result, "score");// 当前积分
												String experience = JsonUtil.getJSONValue(result, "experience");// 当前经验值

												int grade = Integer.valueOf(JsonUtil.getJSONValue(result, "grade"));// 当前等级
												int maxexperience = 0;

												switch (grade) {
												case 1:
													maxexperience = 250;
													break;
												case 2:
													maxexperience = 1000;
													break;
												case 3:
													maxexperience = 3000;
													break;
												case 4:
													maxexperience = 10000;
													break;
												case 5:
													maxexperience = 20000;
													break;
												case 6:
													maxexperience = 20000;
													break;
												}

												String img = SysContext.USERPHOTOURL + "&uid=" + userId;// 获取用户的头像

												// 加密过后的秘钥
												String sysKey = RU.RsaEncode(password + " " + currentTime);
												Object pastTime = JsonUtil.getJsonValue(SysCache.getUserJson(userId),
														"pastTime");

												if (pastTime == null) {
													pastTime = currentTime;
												} else {
													// 有效时间
													Long entTime = Long.parseLong(pastTime.toString()) + sevenDays;
													if (Long.parseLong(pastTime.toString()) < currentTime
															&& currentTime < entTime) {

													} else {
														pastTime = currentTime;
													}
												}

												String userjson = "{\"userid\":" + userId + ",\"username\":\""
														+ username + "\",\"img\":\"" + img + "\",\"score\":" + score
														+ ",\"experience\":" + experience + ",\"grade\":" + grade
														+ ",\"maxexperience\":" + maxexperience + ",\"pastTime\":\""
														+ (pastTime == null ? currentTime : pastTime.toString())
														+ "\",\"sysKey\":\"" + sysKey + "\"}";

												// 用户信息存缓存
												SysCache.setUserJson(userId, userjson);

												this.render(response, userjson);
											} else {
												this.render(response, "{\"userid\":-107}");
											}
										} else {
											this.render(response, "{\"userid\":-107}");
										}
									} else {
										this.render(response, "{\"userid\":-107}");
									}
								} else {
									if (con == -2) {
										int num1 = SysCache.getLoginErrorNum(username) + 1;
										SysCache.setLoginErrorNum(username, num1);
									}

									switch (con) {
									case 0:
										this.render(response, "{\"userid\":-104}");// 参数不正确
										break;
									case -1:
										this.render(response, "{\"userid\":-106}");
										break;
									case -2:
										this.render(response, "{\"userid\":-2}");
										break;
									case -3:
										this.render(response, "{\"userid\":-3,\"lockReason\":\"" + lockReason + "\"}");// 账号被锁定!
										break;
									case -4:
										this.render(response, "{\"userid\":-106}");// 缺少业务用户信
										break;
									case -5:
										this.render(response, "{\"userid\":-105}");
										break;
									case -6:
										this.render(response, "{\"userid\":-104}");
										break;
									default:
										this.render(response, "{\"userid\":-104}");
										break;
									}
								}
							} else {
								this.render(response, "{\"userid\":-104}");
							}
						} else {
							this.render(response, "{\"userid\":-104}");
						}
					} else {
						this.render(response, "{\"userid\":-105}");
					}
				} else {
					this.render(response, "{\"userid\":-103}");
				}
			} else {
				this.render(response, "{\"userid\":-102}");
			}
		} else {
			this.render(response, "{\"userid\":-101}");
		}
	}

	/**
	 * 修改密码时获取邮箱验证码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/getupdatepwdcodebyemail" })
	public void getupdatepasswordcodebyemail(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");// 用户名
		String email = request.getParameter("email");// 邮箱
		String type = request.getParameter("type");// 修改类型
		// retrun 大于0发送成功，-101用户名不能为空，-102邮箱不能为空,-103账号不存在，-104访问超时
		// 0接口参数有误，-1获取验证码频率太快,-2邮件发送失败,-3接口密匙不正确,-4邮箱已存在
		if (loginname == null || "".equals(loginname)) {// 用户名验证
			this.render(response, -101);
		} else {
			if (email == null || "".equals(email)) {// 邮箱验证
				this.render(response, -102);
			} else {

				// 获取网络服务接口前缀
				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
				String key = MD5CodeUtil.md5(dzCookieKey);

				String url = innerInterface + "user/getuseridbyusername?";// MD5加密

				Map<String, Object> maploginname = new HashMap<String, Object>();
				maploginname.put("username", loginname);
				maploginname.put("key", key);
				// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
				String content = HttpUtil.postUrl(url, maploginname);

				if (content == null || "".equals(content) || "null".equals(content)) {// 账号验证
					this.render(response, -104);
				} else {
					content = content.replace(System.lineSeparator(), "");
					long uid = Long.parseLong(content.trim());
					if (uid <= 0) {
						this.render(response, -103);
					} else {

						// 注册-发送注册验证码到用户注册邮箱
						String sendEmailUrl = innerInterface + "user/postresetpwdemailsc?";
						String postkey = MD5CodeUtil.md5(email + dzCookieKey);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("email", email);
						map.put("uid", uid);
						map.put("postkey", postkey);
						map.put("uname", loginname);
						map.put("type", type);
						String context = HttpUtil.postUrl(sendEmailUrl, map);
						if (context == null || "".equals(context)) {
							this.render(response, -104);
						} else {
							context = context.replace(System.lineSeparator(), "");
							JSONObject json = new JSONObject(context);
							this.render(response, json.get("postResult").toString());
						}
					}
				}
			}
		}
	}

	/**
	 * 修改登录密码时获取短信验证码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/getupdateloginpwdsmscode" })
	public void getupdateloginpasswordsmscode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String mobile = request.getParameter("mobile");// 手机号码
		String mac = request.getParameter("mac");// 获取用户MAC地址

		// return 大于0为成功，-102手机号码不能为空，-103MAC地址不能为空，
		// -201 30分钟内已发送三次，-202代码异常
		if (mobile == null || "".equals(mobile)) {// 手机验证
			this.render(response, -102);
		} else {
			if (mac == null || "".equals(mac)) {// MAC地址验证
				this.render(response, -103);
			} else {
				String num = SmsUtil.sendLoginSMS(request, mobile, mac);

				if (null == num || "".equals(num) || "null".equals(num)) {
					num = "-202";
				}
				this.render(response, num.trim());
			}
		}
	}

	/**
	 * 通过短信验证码修改登录密码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/updateloginpwdbysmscode" })
	public void updateloginpasswordbysmscode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");
		String mobile = request.getParameter("mobile");
		String pswd = request.getParameter("password");
		String code = request.getParameter("smscode");
		String mac = request.getParameter("mac");

		String rsa = new RsaUtil().RsaDecode(pswd);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			pswd = rsa;
		}

		// return
		// 大于0成功，-101用户名不能为空，-102手机号码不能为空，-103新登录密码不能为空，-104短信验证码不能为空，-105手机MAC地址不能为空，-106短信验证码错误,-107访问超时
		// 0参数有误,-1接口密匙不正确,-2未能找到用户信息,-3手机号码与绑定不一致,-4修改失败

		if (loginname == null || "".equals(loginname)) {// 登录名验证
			this.render(response, -101);
		} else {
			if (mobile == null || "".equals(mobile)) {// 手机号码验证
				this.render(response, -102);
			} else {
				if (pswd == null || "".equals(pswd)) {// 新密码验证
					this.render(response, -103);
				} else {
					if (code == null || "".equals(code)) {// 短信验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {

							boolean result = SmsUtil.ValidateSmsCode(mobile, mac, code);// 短信验证码验证

							if (result) {

								String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
								String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

								String key = MD5CodeUtil.md5(1 + dzCookieKey);

								String url = innerInterface + "user/updatepasswordbymobile?";

								Map<String, Object> param = new HashMap<String, Object>();
								param.put("loginname", loginname);
								param.put("type", 1);// 1修改登录密码，2修改支付密码
								param.put("pswd", pswd);// SecurityEncode.decoderOrEncoder(pswd,
														// 1470294692)
								param.put("mobile", mobile);
								param.put("key", key);
								// 以post的方式提交到服务接口，context为返回的结果
								String content = HttpUtil.postUrl(url, param);
								if (content != null && !"".equals(content) && !"null".equals(content)) {
									content = content.replace(System.lineSeparator(), "");
									int num = Integer.parseInt(content.trim());
									if (num > 0) {
										SysCache.deleteLoginErrorNum(loginname);
									}
									this.render(response, content);
								} else {
									this.render(response, -107);
								}

							} else {
								this.render(response, -106);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 通过邮箱修改登录密码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/updateloginpwdbyemailcode" })
	public void updateloginpasswordbyemailcode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");
		String email = request.getParameter("email");
		String pswd = request.getParameter("password");
		String code = request.getParameter("emailcode");
		String mac = request.getParameter("mac");
		String rsa = new RsaUtil().RsaDecode(pswd);
		
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			pswd = rsa;
		}

		// return
		// 大于0成功，-101用户名不能为空，-102邮箱不能为空，-103新登录密码不能为空，-104邮箱验证码不能为空，-105手机MAC地址不能为空,-107访问超时
		// 0参数有误，-1接口密匙不正确，-2未能找到用户信息，-3手机号码与绑定不一致，-4验证码错误

		if (loginname == null || "".equals(loginname)) {// 登录名验证
			this.render(response, -101);
		} else {
			if (email == null || "".equals(email)) {// 手机号码验证
				this.render(response, -102);
			} else {
				if (pswd == null || "".equals(pswd)) {// 新密码验证
					this.render(response, -103);
				} else {
					if (code == null || "".equals(code)) {// 短信验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {

							String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
							String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
							String key = MD5CodeUtil.md5("1" + dzCookieKey);
							String url = innerInterface + "user/updatepasswordbyemail?";

							Map<String, Object> param = new HashMap<String, Object>();
							param.put("loginname", loginname);
							param.put("type", "1");
							param.put("pswd", pswd);// SecurityEncode.decoderOrEncoder(pswd,
													// 1470294692)
							param.put("email", email);
							param.put("key", key);
							param.put("emailCode", code);
							// 以post的方式提交到服务接口，context为返回的结果
							String content = HttpUtil.postUrl(url, param);
							if (content != null && !"".equals(content)) {
								content = content.replace(System.lineSeparator(), "");
								int num = Integer.parseInt(content.trim());
								if (num > 0) {
									SysCache.deleteLoginErrorNum(loginname);
								}
								this.render(response, content);
							} else {
								this.render(response, -107);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 获取修改支付密码短信验证码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/getupdatepaypwdsmscode" })
	public void getupdatepaypasswordsmscode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String mobile = request.getParameter("mobile");// 手机号码
		String mac = request.getParameter("mac");// 获取用户MAC地址

		// return 大于0为成功，-102手机号码不能为空，-103MAC地址不能为空，
		// -201 30分钟内已发送三次，-202代码异常
		if (mobile == null || "".equals(mobile)) {// 手机验证
			this.render(response, -102);
		} else {
			if (mac == null || "".equals(mac)) {// MAC地址验证
				this.render(response, -103);
			} else {
				String num = SmsUtil.sendLoginSMS(request, mobile, mac);// 发送短信验证码

				if (null == num || "".equals(num) || "null".equals(num)) {
					num = "-202";
				}
				this.render(response, num);
			}
		}
	}

	/**
	 * 通过短信验证码修改支付密码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/updatepaypwdbysmscode" })
	public void updatepayasswordbysmscode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");
		String mobile = request.getParameter("mobile");
		String pswd = request.getParameter("password");
		String code = request.getParameter("smscode");
		String mac = request.getParameter("mac");

		String rsa = new RsaUtil().RsaDecode(pswd);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			pswd = rsa;
		}

		// return
		// 大于0成功，-101用户名不能为空，-102手机号码不能为空，-103新支付密码不能为空，-104短信验证码不能为空，-105手机MAC地址不能为空，-106短信验证码错误,-107访问超时
		// 0参数有误,-1接口密匙不正确,-2未能找到用户信息,-3手机号码与绑定不一致,-4修改失败

		if (loginname == null || "".equals(loginname)) {// 登录名验证
			this.render(response, -101);
		} else {
			if (mobile == null || "".equals(mobile)) {// 手机号码验证
				this.render(response, -102);
			} else {
				if (pswd == null || "".equals(pswd)) {// 新密码验证
					this.render(response, -103);
				} else {
					if (code == null || "".equals(code)) {// 短信验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {
							boolean result = SmsUtil.ValidateSmsCode(mobile, mac, code);// 短信验证码验证
							if (result) {

								String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
								String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

								String key = MD5CodeUtil.md5(2 + dzCookieKey);

								String url = innerInterface + "user/updatepasswordbymobile?";

								Map<String, Object> param = new HashMap<String, Object>();
								param.put("loginname", loginname);
								param.put("type", 2);// 1修改登录密码，2修改支付密码
								param.put("pswd", pswd);// SecurityEncode.decoderOrEncoder(pswd,
														// 1470294692)
								param.put("mobile", mobile);
								param.put("key", key);
								// 以post的方式提交到服务接口，context为返回的结果
								String content = HttpUtil.postUrl(url, param);
								if (content != null && !"".equals(content) && !"null".equals(content)) {
									content = content.replace(System.lineSeparator(), "");
									this.render(response, content);
								} else {
									this.render(response, -107);
								}
							} else {
								this.render(response, -106);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 通过邮箱修改支付密码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/updatepaypwdbyemailcode" })
	public void updatepaypasswordbyemailcode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("username");
		String email = request.getParameter("email");
		String pswd = request.getParameter("password");
		String code = request.getParameter("emailcode");
		String mac = request.getParameter("mac");

		String rsa = new RsaUtil().RsaDecode(pswd);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			pswd = rsa;
		}

		// return
		// 大于0成功，-101用户名不能为空，-102邮箱不能为空，-103新登录密码不能为空，-104邮箱验证码不能为空，-105手机MAC地址不能为空,-107访问超时
		// 0参数有误，-1接口密匙不正确，-2未能找到用户信息，-3手机号码与绑定不一致，-4验证码错误

		if (loginname == null || "".equals(loginname)) {// 登录名验证
			this.render(response, -101);
		} else {
			if (email == null || "".equals(email)) {// 手机号码验证
				this.render(response, -102);
			} else {
				if (pswd == null || "".equals(pswd)) {// 新密码验证
					this.render(response, -103);
				} else {
					if (code == null || "".equals(code)) {// 短信验证码验证
						this.render(response, -104);
					} else {
						if (mac == null || "".equals(mac)) {// 手机mac地址验证
							this.render(response, -105);
						} else {

							String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
							String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

							String key = MD5CodeUtil.md5(2 + dzCookieKey);

							// 0:参数有误;-1:密匙不正确;-2:未能找到用户信息;-3:email与绑定不一致;-4:验证码已过期;-5:验证码不正确;-6:修改失败;>0:修改成功
							String url = innerInterface + "user/updatepasswordbyemail?";

							Map<String, Object> param = new HashMap<String, Object>();
							param.put("loginname", loginname);
							param.put("type", 2);
							param.put("pswd", pswd);// SecurityEncode.decoderOrEncoder(pswd,
													// 1470294692)
							param.put("email", email);
							param.put("key", key);
							param.put("emailCode", code);
							// 以post的方式提交到服务接口，context为返回的结果
							String content = HttpUtil.postUrl(url, param);
							if (content != null && !"".equals(content) && !"null".equals(content)) {
								content = content.replace(System.lineSeparator(), "");
								this.render(response, content);
							} else {
								this.render(response, -107);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * QQ登录
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/qqlogin" })
	public void appqqlogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String openid = request.getParameter("openid");// 获取QQ登录返回的openid
		String mac = request.getParameter("mac");// 获取手机的mac地址
		String source = request.getParameter("source");// 获取登录来源
		String agentInformation = request.getParameter("agentInformation");// 获取用户设备信息
		String areaId = request.getParameter("areaId");// 获取用户区域编号
		// return 大于0成功，-101qq开放ID不能为空，-102手机mac地址不能为空,-103访问超时
		// -1找不到绑定用户信息,-2找不到用户信息,-3号被锁定,-4用户账号未激活,0账号信息不存在

		if (openid == null || "".equals(openid)) {// qq开放ID
			this.render(response, "{\"userid\":-101}");
		} else {
			if (mac == null || "".equals(mac)) {// 手机mac地址不能为空
				this.render(response, "{\"userid\":-102}");
			} else {

				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
				String key = MD5CodeUtil.md5(openid + dzCookieKey);
				String url = innerInterface + "user/qqlogin?";

				Map<String, Object> mapqqlogin = new HashMap<String, Object>();
				mapqqlogin.put("openid", openid);
				mapqqlogin.put("key", key);
				mapqqlogin.put("mac", mac);
				mapqqlogin.put("source", 4);
				// -1找不到绑定用户信息,-2找不到用户信息,-3号被锁定,-4用户账号未激活,0账号信息不存在
				String content = HttpUtil.postUrl(url, mapqqlogin);

				if (content == null || "".equals(content) || "null".equals(content)) {
					this.render(response, "{\"userid\":-103}");
				} else {
					content = content.replace(System.lineSeparator(), "");

					String postresult = JsonUtil.getJSONValue(content, "postResult");
					int con = Integer.parseInt(postresult);
					if (con <= 0) {
						this.render(response, "{\"userid\":" + con + "}");
					} else {
						String userid = JsonUtil.getJSONValue(content, "userid");
						String username = JsonUtil.getJSONValue(content, "userName");

						Map<String, String> hmap = new HashMap<String, String>();
						hmap.put("userId", userid);
						hmap.put("userName", username);
						String regTime = JsonUtil.getJSONValue(content, "regTime");
						if (regTime.indexOf("Date") > -1) {
							regTime = regTime.substring(regTime.indexOf("(") + 1, regTime.indexOf(")"));
						}
						hmap.put("regTime", regTime);
						hmap.put("userSource", "1");
						hmap.put("loginType", source);
						hmap.put("loginMarker", mac);
						hmap.put("agentInfo", agentInformation);
						hmap.put("areaId", areaId);

						UserRPCClient userService = new UserRPCClient();
						int i = userService.client.UpUserInfo(hmap);
						if (i == 0) {
							System.out.println("qq登录记录登录信息失败");
						}
						userService.close();

						// 判断用户是否为圈中用户
						key = MD5CodeUtil.md5(userid + dzCookieKey);
						url = innerInterface + "user/getscoreexperience?";

						Map<String, Object> param = new HashMap<String, Object>();
						param.put("userid", userid);
						param.put("key", key);
						String result = HttpUtil.postUrl(url, param);

						if (result != null && !result.equals("null") && !result.equals("")) {
							result = result.replace(System.lineSeparator(), "");
						}

						if (JsonUtil.getJSONValue(result, "postResult").equals("1")) {
							// String isgroupuser=vue.getIsgroupuser();//是否圈子用户
							String score = JsonUtil.getJSONValue(result, "score");// 当前积分
							String experience = JsonUtil.getJSONValue(result, "experience");// 当前经验值
							int grade = Integer.valueOf(JsonUtil.getJSONValue(result, "grade"));// 当前等级
							int maxexperience = 0;

							switch (grade) {
							case 1:
								maxexperience = 250;
								break;
							case 2:
								maxexperience = 1000;
								break;
							case 3:
								maxexperience = 3000;
								break;
							case 4:
								maxexperience = 10000;
								break;
							case 5:
								maxexperience = 20000;
								break;
							case 6:
								maxexperience = 20000;
								break;
							}

							String img = SysContext.USERPHOTOURL + "&uid=" + userid;// 获取用户的头像

							String userjson = "{\"userid\":" + userid + ",\"username\":\"" + username + "\",\"img\":\""
									+ img + "\",\"score\":" + score + ",\"experience\":" + experience + ",\"grade\":"
									+ grade + ",\"maxexperience\":" + maxexperience + "}";

							// 用户信息存缓存
							SysCache.setUserJson(userid, userjson);

							this.render(response, userjson);

						} else {
							this.render(response, "{\"userid\":-107}");
						}
					}
				}
			}
		}
	}

	/**
	 * QQ第三方绑定商超账号
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/appqqbinding" })
	public void appqqbinding(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String openid = request.getParameter("openid");// QQ开放ID
		String loginname = request.getParameter("username");// 微薄利登录名(用户名/email/手机)
		String loginpswd = request.getParameter("password");// 微薄利用户登录密码
		String username = request.getParameter("qqname");// QQ用户名
		String userface = request.getParameter("qqphoto");// QQ用户头像地址

		String rsa = new RsaUtil().RsaDecode(loginpswd);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			loginpswd = rsa;
		}

		// return
		// 大于0绑定成功，-101QQ开放ID不能为空，-102微薄利账号不能为空，-103微薄利密码不能为空，-104QQ用户名不能为空，-105QQ用户头像地址不能为空,-106访问超时
		// 0参数有误，-1接口密匙不正确，-2未能找到用户信息，-3登录密码错误，-4用户账号被锁定，-5绑定失败
		if (openid == null || "".equals(openid)) {
			this.render(response, -101);
		} else {
			if (loginname == null || "".equals(loginname)) {
				this.render(response, -102);
			} else {
				if (loginpswd == null || "".equals(loginpswd)) {
					this.render(response, -103);
				} else {
					if (username == null || "".equals(username)) {
						this.render(response, -104);
					} else {
						if (userface == null || "".equals(userface)) {
							this.render(response, -105);
						} else {
							String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
							String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

							String key = MD5CodeUtil.md5(openid + dzCookieKey);

							String url = innerInterface + "user/binduseraccount?";

							Map<String, Object> param = new HashMap<String, Object>();
							param.put("openid", openid);
							param.put("loginname", loginname);
							param.put("loginpswd", loginpswd);// SecurityEncode.decoderOrEncoder(loginpswd,
																// 1470294692)
							param.put("username", username);
							param.put("userface", userface);
							param.put("key", key);
							// 以post的方式提交到服务接口，context为返回的结果
							String content = HttpUtil.postUrl(url, param);

							if (content == null || "".equals(content) || "null".equals(content)) {
								this.render(response, -106);
							} else {
								content = content.replace(System.lineSeparator(), "");
								this.render(response, content);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * QQ绑定注册（短信验证码）
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/appqqregisterbysmscode" })
	public void appqqregisterbysmscode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String openid = request.getParameter("openid");// 身份标识编号
		String username = request.getParameter("username");// 用户名
		String password = request.getParameter("password");// 登录密码
		String source = "4";// 注册来源(1微薄利web端，2 微薄利wap端，3 QQ登录，4安卓客户端，5苹果客户端)
		source = request.getParameter("source");
		String mobile = request.getParameter("mobile");// 手机号码
		String smscode = request.getParameter("smscode");// 验证码(可为空)
		String mac = request.getParameter("mac");// 手机MAC地址
		String popularize_uid = request.getParameter("popularize_uid");// 推广者用户编号(可为空)
		String popularize_url = SysContext.DOMAINNAME;// 推广来源URL(可为空)
		String qqusername = request.getParameter("qqname");// QQ昵称(可为空)
		String qquserface = request.getParameter("qqphoto");// QQ用户头像(可为空)

		String rsa = new RsaUtil().RsaDecode(password);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			password = rsa;
		}

		// return 大于0成功，-101QQ开发ID不
		// 能为空，-102用户名不能为空。-103登录密码不能为空，-104手机号码不能为空，-105验证码不能为空，-106手机mac地址不能为空,-107验证码错误
		// ，-108访问超时，0参数有误，-1用户名已存在，-2验证码会话超时，-3验证码不正确，-4密匙不正确，-5注册成功,但绑定失败，-6电子邮箱已存在，-7手机号码已存在，-8手机格式错误
		if (openid == null || "".equals(openid)) {
			this.render(response, -101);
		} else {
			if (username == null || "".equals(username)) {
				this.render(response, -102);
			} else {
				if (password == null || "".equals(password)) {
					this.render(response, -103);
				} else {
					if (mobile == null || "".equals(mobile)) {
						this.render(response, -104);
					} else {
						if (smscode == null || "".equals(smscode)) {
							this.render(response, -105);
						} else {
							if (mac == null || "".equals(mac)) {
								this.render(response, -106);
							} else {
								boolean result = SmsUtil.ValidateSmsCode(mobile, mac, smscode);// 短信验证码验证
								if (result) {

									String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
									String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

									String key = MD5CodeUtil.md5(openid + dzCookieKey);

									String url = innerInterface + "user/qqregister?";

									Map<String, Object> param = new HashMap<String, Object>();
									param.put("openId", openid);
									param.put("username", username);
									param.put("password", password);// SecurityEncode.decoderOrEncoder(password,
																	// 1470294692)
									param.put("source", Integer.parseInt(source));
									param.put("mac", mac);
									param.put("key", key);
									param.put("mobile", mobile);

									int uid = 0;// 查找推广者ID
									if (popularize_uid != null && !"".equals(popularize_uid)) {
										param.put("popularize_uid", popularize_uid);
									} else {
										param.put("popularize_uid", uid);
									}
									if (popularize_url != null && !"".equals(popularize_url)) {
										param.put("popularize_url", popularize_url);
									}
									if (qqusername != null && !"".equals(qqusername)) {
										param.put("qqusername", URLEncoder.encode(qqusername, "UTF-8"));
									}
									if (qquserface != null && !"".equals(qquserface)) {
										param.put("qquserface", qquserface);
									}

									// 以post的方式提交到服务接口，context为返回的结果
									String content = HttpUtil.postUrl(url, param);
									if (content == null || "".equals(content) || "null".equals(content)) {
										this.render(response, -108);
									} else {
										content = content.replace(System.lineSeparator(), "");
										this.render(response, content);
									}
								} else {
									this.render(response, -107);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * QQ绑定注册（邮箱验证码）
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/appqqregisterbyemailcode" })
	public void appqqregisterbyemailcode(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String openid = request.getParameter("openid");// 身份标识编号
		String username = request.getParameter("username");// 用户名
		String password = request.getParameter("password");// 登录密码
		String source = "4";// 注册来源(1微薄利web端，2 微薄利wap端，3 QQ登录，4安卓客户端，5苹果客户端)
		source = request.getParameter("source");
		String email = request.getParameter("email");// 邮箱
		String emailcode = request.getParameter("emailcode");// 邮箱验证码
		String mac = request.getParameter("mac");// 手机MAC地址
		String popularize_url = SysContext.DOMAINNAME;// 推广来源URL(可为空)
		String qqusername = request.getParameter("qqname");// QQ昵称(可为空)
		String qquserface = request.getParameter("qqphoto");// QQ用户头像(可为空)

		String rsa = new RsaUtil().RsaDecode(password);
		if (rsa != null && !rsa.equals("null") && !rsa.equals("")) {
			password = rsa;
		}

		// return 大于0成功，-101QQ开发ID不
		// 能为空，-102用户名不能为空。-103登录密码不能为空，-104邮箱不能为空，-105验证码不能为空，-106手机mac地址不能为空,-107验证码错误
		// -108访问超时，0参数有误，-1用户名已存在，-2验证码会话超时，-3验证码不正确，-4密匙不正确，-5注册成功,但绑定失败，-6电子邮箱已存在，-7手机号码已存在，-8手机格式错误
		if (openid == null || "".equals(openid)) {
			this.render(response, -101);
		} else {
			if (username == null || "".equals(username)) {
				this.render(response, -102);
			} else {
				if (password == null || "".equals(password)) {
					this.render(response, -103);
				} else {
					if (email == null || "".equals(email)) {
						this.render(response, -104);
					} else {
						if (emailcode == null || "".equals(emailcode)) {
							this.render(response, -105);
						} else {
							if (mac == null || "".equals(mac)) {
								this.render(response, -106);
							} else {
								String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
								String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
								String key = MD5CodeUtil.md5(openid + dzCookieKey);
								String url = innerInterface + "user/qqregister?";

								Map<String, Object> param = new HashMap<String, Object>();
								param.put("openId", openid);
								param.put("username", username);
								param.put("password", password);// SecurityEncode.decoderOrEncoder(password,
																// 1470294692)
								param.put("source", Integer.parseInt(source));
								param.put("mac", mac);
								param.put("key", key);
								param.put("email", email);
								param.put("code", emailcode);

								if (popularize_url != null && !"".equals(popularize_url)) {
									param.put("popularize_url", popularize_url);
								}
								if (qqusername != null && !"".equals(qqusername)) {
									param.put("qqusername", URLEncoder.encode(qqusername, "UTF-8"));
								}
								if (qquserface != null && !"".equals(qquserface)) {
									param.put("qquserface", qquserface);
								}

								// 以post的方式提交到服务接口，context为返回的结果
								String content = HttpUtil.postUrl(url, param);
								if (content == null || "".equals(content) || "null".equals(content)) {
									this.render(response, -108);
								} else {
									content = content.replace(System.lineSeparator(), "");
									this.render(response, content);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 激活邮箱接口
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/activationemail" })
	public void activationemail(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String username = request.getParameter("username");// 获取用户名
		String email = request.getParameter("email");// 获取电子邮箱
		String code = request.getParameter("emailcode");// 获取电子邮箱的验证码

		// return 大于0成功，-101用户名不能为空，-102邮箱不能为空，-103邮箱验证码不能为空
		// ，-104访问超时,-105获取用户ID失败
		// -1传入参数有误,-2接口密匙不正确,-3验证码过期，需重新获取,-4未能获取用户基本信息
		if (username == null || "".equals(username)) {
			this.render(response, -101);
		} else {
			if (email == null || "".equals(email)) {
				this.render(response, -102);
			} else {
				if (code == null || "".equals(code)) {
					this.render(response, -103);
				} else {

					String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
					String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
					String getuseridurl = innerInterface + "user/getuseridbyusername?";
					String key = MD5CodeUtil.md5(dzCookieKey);

					Map<String, Object> maploginname = new HashMap<String, Object>();
					maploginname.put("userName", username);
					maploginname.put("key", key);

					String content = HttpUtil.postUrl(getuseridurl, maploginname);

					if (content == null || "".equals(content)) {
						this.render(response, -105);
					} else {
						int userid = Integer.parseInt(content.trim());
						String postkey = MD5CodeUtil.md5(userid + dzCookieKey);
						String url = innerInterface + "user/bindemail?";
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("uid", userid);
						map.put("email", email);
						map.put("code", code);
						map.put("postkey", postkey);
						// 以post的方式提交到接口，context为返回的结果
						String context = HttpUtil.postUrl(url, map);
						if (context == null || "".equals(context)) {
							this.render(response, -104);
						} else {
							context = context.replace(System.lineSeparator(), "");
							this.render(response, context);
						}
					}
				}
			}
		}
	}

	/**
	 * 根据登录名获取用户绑定信息（邮箱和手机号）
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/user/usersecurity" })
	public void usersecurity(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String loginname = request.getParameter("username");

		// return user大于0访问成功，-101用户名不能为空,-102访问失败
		if (loginname == null || "".equals(loginname)) {
			this.render(response, "{\"userid\":-101}");
		} else {
			String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
			String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
			String url = innerInterface + "user/getuserbindinfobyloginname?";
			String key = MD5CodeUtil.md5(dzCookieKey);

			Map<String, Object> mapmibao = new HashMap<String, Object>();
			mapmibao.put("loginname", loginname);
			mapmibao.put("key", key);
			String content = HttpUtil.postUrl(url, mapmibao);

			if (content == null || "".equals(content)) {
				this.render(response, "{\"result\":-102}");
			} else {
				content = content.replace(System.lineSeparator(), "");
				String userid = JsonUtil.getJSONValue(content, "userid");
				String email = JsonUtil.getJSONValue(content, "email");
				String mobile = JsonUtil.getJSONValue(content, "mobile");
				String result = "{\"result\":" + userid + ",\"email\":\"" + email + "\",\"mobile\":\"" + mobile + "\"}";
				this.render(response, result);
			}
		}
	}
}
