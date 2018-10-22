package com.wboly.wechat.controller.user;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.wechat.service.user.WeChatUserService;

import rebue.wheel.AgentUtils;
import rebue.wheel.NetUtils;
import rebue.wheel.OkhttpUtils;
import rebue.wheel.RegexUtils;

/**
 * @Name: 微信 用户 .java
 * @Author: nick
 */
@Controller
public class WeChatUserController extends SysController {

	private static final Logger _log = LoggerFactory.getLogger(WeChatUserController.class);

	@Autowired
	private WeChatUserService weChatUserService;

	/**
	 * @Name: 地址管理页面(新)__目前使用这个地址链接
	 * @throws Exception
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/user/newAddressPage")
	public ModelAndView newAddressManagerPage(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ModelAndView mav = new ModelAndView();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			String result = OkhttpUtils.get(SysContext.ORDERURL + "/ord/addr?userId=" + userId);
			System.err.println("查询用户收货地址返回值为：" + result);
			List<Map<String, Object>> list = JsonUtil.listMaps(result);
			mav.addObject("AddressList", list);
			mav.setViewName("/htm/wechat/user/newAddress");
		}
		return mav;
	}

	/**
	 * @throws IOException
	 * @throws NumberFormatException
	 * @Name: 删除用户收货地址
	 * @Author: nick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/user/delAddress")
	public void delUserAddress(HttpServletRequest request, HttpServletResponse response)
			throws NumberFormatException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		String addressId = request.getParameter("addressId");
		if (addressId == null || addressId.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		System.err.println("删除用户收货地址的参数为：" + addressId);
		String results = OkhttpUtils.delete(SysContext.ORDERURL + "/ord/addr/" + addressId);
		System.err.println("删除用户收货地址的返回值为：" + results);
		ObjectMapper mapper = new ObjectMapper();
		Map resultMap = mapper.readValue(results, Map.class);
		boolean flag = (boolean) resultMap.get("success");
		if (flag) {
			this.render(response, "{\"message\":\"删除成功\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"删除失败\",\"flag\":false}");
	}

	/**
	 * @throws IOException
	 * @Name: 添加用户收货地址
	 * @Author: nick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/user/addAddress")
	public void addUserAddress(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}
		// 邮编
		String postalCode = request.getParameter("postalCode");
		// 具体地址
		String address = request.getParameter("address");
		// 区域
		String areaIds = request.getParameter("areaIds");
		// 收件人姓名
		String realName = request.getParameter("realName");
		// 联系方式
		String mobile = request.getParameter("mobile");
		if (mobile == null || mobile.equals("") || realName == null || realName.equals("") || address == null
				|| areaIds == null || areaIds.split(",").length < 3) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		String[] areas = areaIds.split(",");

		Map<String, Object> map = new HashMap<String, Object>();
		if (postalCode != null && !postalCode.equals("") && !postalCode.equals("null")) {
			map.put("receiverPostCode", postalCode);
		}
		map.put("userId", userId);
		map.put("receiverProvince", areas[0]);
		map.put("receiverCity", areas[1]);
		map.put("receiverExpArea", areas[2]);
		map.put("receiverAddress", address);
		map.put("receiverName", realName);
		map.put("receiverMobile", mobile);
		map.put("isDef", 0);
		// 添加用户收货地址
		String results = OkhttpUtils.postByFormParams(SysContext.ORDERURL + "/ord/addr", map);
		ObjectMapper mapper = new ObjectMapper();
		Map resultMap = mapper.readValue(results, Map.class);
		int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
		if (result > 0) {
			this.render(response, "{\"message\":\"添加成功\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"添加失败\",\"flag\":false}");
	}

	/**
	 * @throws IOException
	 * @Name: 修改默认收货地址
	 * @Author: nick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/user/upDefault")
	public void upDefaultAddress(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}
		// 是否为默认收货地址
		String addressId = request.getParameter("addressId");
		if (addressId == null || addressId.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", addressId);
		map.put("isDef", true);
		map.put("userId", userId);
		System.err.println("修改用户默认收货地址的参数为：" + map.toString());
		// 修改默认收货地址
		String results = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/addr/def", map);
		System.err.println("修改用户默认收货地址的返回值为：" + results);
		if (results != null && !results.equals("") && !results.equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			Map resultMap = mapper.readValue(results, Map.class);
			int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
			if (result > 0) {
				this.render(response, "{\"message\":\"设置成功\",\"flag\":true}");
				return;
			}
		}
		this.render(response, "{\"message\":\"设置失败\",\"flag\":false}");
	}

	/**
	 * @Name: 用户地址信息修改
	 * @throws TException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @Author: nick
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/user/upAddress")
	public void upAddressData(HttpServletRequest request, HttpServletResponse response)
			throws TException, JsonParseException, JsonMappingException, IOException {
		// 当前用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}
		// 收货地址区域
		String areaArr = request.getParameter("areaIds");
		// 详细地址
		String receiverAddress = request.getParameter("address");
		// 收货人姓名
		String receiverName = request.getParameter("realName");
		// 收货人手机
		String receiverMobile = request.getParameter("mobile");
		// 是否为默认收货地址
		String isDefault = request.getParameter("isDefault");
		// 收货地址编号
		String id = request.getParameter("addressId");
		// 邮编
		String receiverPostCode = request.getParameter("postalCode");
		if (areaArr == null || areaArr.split(",").length < 3 || id == null || isDefault == null) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		String[] areaArrs = areaArr.split(",");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("userId", userId);
		map.put("receiverName", receiverName);
		map.put("receiverMobile", receiverMobile);
		map.put("receiverProvince", areaArrs[0]);
		map.put("receiverCity", areaArrs[1]);
		map.put("receiverExpArea", areaArrs[2]);
		map.put("receiverAddress", receiverAddress);
		if (isDefault.equals("1")) {
			map.put("isDef", true);
		} else {
			map.put("isDef", false);
		}

		if (receiverPostCode != null && !receiverPostCode.equals("") && !receiverPostCode.equals("null")) {
			map.put("receiverPostCode", receiverPostCode);
		}

		System.err.println("修改用户收货地址信息的参数为：" + map.toString());
		// 修改用户收货地址
		String results = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/addr", map);
		if (results != null && !results.equals("") && !results.equals("null")) {
			ObjectMapper mapper = new ObjectMapper();
			Map resultMap = mapper.readValue(results, Map.class);
			int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
			if (result > 0) {
				this.render(response, "{\"message\":\"修改成功\",\"flag\":true}");
			} else {
				this.render(response, "{\"message\":\"修改失败\",\"flag\":false}");
			}
		} else {
			this.render(response, "{\"message\":\"修改失败\",\"flag\":false}");
		}
	}

	/**
	 * @Name: 用户中心页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/userCenter")
	public ModelAndView userCenterPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		Map<String, Object> map = new HashMap<String, Object>();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		// 查询用户是否有提现账号
		String urls = SysContext.VPAYURL + "/withdraw/account/exist/byuserid?userId=" + userId;
		String results = HttpUtil.getUrl(urls);
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			map.put("userName", "点击登录");
			map.put("img", "/wbolybusiness/images/wechat/logo.png");
			map.put("userId", "");
		} else {
			String img = SysCache.getWeChatUserByColumn(request, "img");
			String userName = SysCache.getWeChatUserByColumn(request, "userName");
			map.put("userName", userName);
			map.put("img", img);
			map.put("userId", userId);
		}

		mav.addObject("centerData", map);
		mav.addObject("exist", results);
		mav.setViewName("/htm/wechat/user/usercenter");
		return mav;
	}
	
	/**
	 * @throws IOException 
	 * @Name: 查看登录密码是否存在
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/loginPwIsExis", method = RequestMethod.GET)
	public void loginPwIsExis(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = SysCache.getWeChatUserByColumn(request, "userId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		System.err.println("查看登录密码是否存在的参数为：" + String.valueOf(map));
		String result = OkhttpUtils.get(SysContext.USERCENTERURL + "/user/loginPwIsExis",map);
		System.err.println("查看登录密码是否存在的结果为：" +result );
		this.render(response, result);
	}
	
	/**
	 * @throws IOException 
	 * @Name: 查看用户是否实名认证
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/verifyRealName", method = RequestMethod.GET)
	public void verifyRealName(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		System.err.println("查看登录密码是否存在的参数为：" + String.valueOf(map));
		String result = OkhttpUtils.get(SysContext.RNAURL + "/rna/getbyuserid",map);
		System.err.println("查看登录密码是否存在的结果为：" +result );
		this.render(response, result);
	}
	
	/**
	 * @Name: 我的钱包页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/myWalletPage", method = RequestMethod.GET)
	public ModelAndView myWalletPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			mav.addObject("userId", userId);
			mav.setViewName("/htm/wechat/user/wallet");
		}
		return mav;
	}

	/**
	 * @Name: 用户钱包接口
	 * @Author: nick
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/wechat/user/getMoney", method = RequestMethod.POST)
	public void UserMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		// 商家可用余额接口
		String url = SysContext.VPAYURL + "/account/funds?userId=" + userId;
		String result = HttpUtil.getUrl(url);
		System.err.println(userId + "获取到的余额信息为：" + result);
		ObjectMapper mapper = new ObjectMapper();
		Map map = mapper.readValue(result, Map.class);
		System.err.println("WX:用户编号为:" + userId + "\t 查询账户余额成功返回");
		DecimalFormat df = new DecimalFormat("0.00");
		map.put("availableBalance", df.format(map.get("balance")));// 账户余额,单位:分
		map.put("sumretailBacLimit", df.format(map.get("cashback")));// 可用返现金额，单位:分

		// 返现总金额单位:分
		map.put("usableBacLimit", df.format(map.get("cashbacking")));
		this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(map) + ",\"flag\":true}");
	}

	/**
	 * @Name: 获取用户收货地址
	 * @throws Exception
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/getaddress")
	public void getUserAddress(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		String isDefault = request.getParameter("isDefault");
		if (isDefault == null || isDefault.equals("")) {
			this.render(response, "{\"message\":\"您的请求有误\",\"flag\":false}");
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		if (isDefault.equals("1")) {
			map.put("isDef", "true");
		} else {
			map.put("isDef", "false");
		}
		System.err.println("查询用户收货地址的参数为：" + map.toString());
		String result = OkhttpUtils.get(SysContext.ORDERURL + "/ord/addr", map);
		List<Map<String, Object>> list = JsonUtil.listMaps(result);

		if (list.size() > 0) {
			this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(list) + ",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":\"\",\"flag\":false}");
		}
	}

	/**
	 * @Name: 修改登录密码页面
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/updateloginpwdpage" })
	public ModelAndView updateLoginPwdPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		// 用户编号
		String loginuid = SysCache.getWeChatUserByColumn(request, "userId");
		if (null != loginuid && !"".equals(loginuid)) {
			mav.setViewName("/htm/wechat/user/updatePwd");
		} else {
			mav.setViewName("/wechat/oauth2/checkSignature/login.htm");
		}
		return mav;
	}

	/**
	 * 修改登录密码提交
	 * 
	 * @param request
	 * @param response
	 * @param map
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/changeLogonPassword")
	public void changeLogonPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> map) throws IOException {
		System.out.println(String.valueOf(map));
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			map.put("wxId", wxId);
			System.out.println("修改登录密码的参数为：" + wxId);
			String result = OkhttpUtils.postByFormParams(SysContext.USERCENTERURL + "/loginpswd/modify/bywxid", map);
			System.out.println("微信修改登录密码的返回值为：" + result);
			this.render(response, result);
		} else {
			this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
		}
	}

	/**
	 * 设置登录密码
	 * 
	 * @param request
	 * @param response
	 * @param map
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/setLoginPassword")
	public void setLoginPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> map) throws IOException {
		System.out.println(String.valueOf(map));
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			map.put("wxId", wxId);
			System.out.println("设置登录密码的参数为：" + wxId);
			String result = OkhttpUtils.postByFormParams(SysContext.USERCENTERURL + "/loginpswd/add/bywxid", map);
			System.out.println("微信修改登录密码的返回值为：" + result);
			this.render(response, result);
		} else {
			this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
		}
	}

	/**
	 * 跳转至微信设置登录名称
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/wechat/user/setLoninNamePage")
	public ModelAndView setLoninNamePage(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView andView = new ModelAndView();
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			String result = OkhttpUtils.get(SysContext.USERCENTERURL + "/user/loginName/bywxid?wxId=" + wxId);
			ObjectMapper mapper = new ObjectMapper();
			Map map = mapper.readValue(result, Map.class);
			String loginName = String.valueOf(map.get("loginName"));
			System.out.println("跳转至设置登录名称时获取到的登录名称为：" + loginName);
			andView.addObject("loginName", loginName);
			andView.setViewName("/htm/wechat/user/updateLoginName");
			return andView;
		} else {
			andView.setViewName("redirect:/wechat/oauth2/checkSignature/login.htm");
			return andView;
		}
	}

	/**
	 * 微信设置登录名称
	 * 
	 * @param request
	 * @param response
	 * @param loginName
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/wechat/user/setLoginName")
	public void setLoginName(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("loginName") String loginName) throws JsonParseException, JsonMappingException, IOException {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			if (wxId != null && !wxId.equals("") && !wxId.equals("null")) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("wxId", wxId);
				map.put("loginName", loginName);
				System.err.println("微信设置登录名称的参数为：" + String.valueOf(map));
				String results = OkhttpUtils.postByFormParams(SysContext.USERCENTERURL + "/user/setloginname/bywxid",
						map);
				ObjectMapper mapper = new ObjectMapper();
				Map resultMap = mapper.readValue(results, Map.class);
				String result = String.valueOf(resultMap.get("result"));
				if (result.equals("1")) {
					map.put("userId", userId);
					System.err.println("修改用户名称的参数为：" + String.valueOf(map));
					int updateResult = weChatUserService.updateUserName(map);
					System.err.println("修改用户 名称的返回值为：" + updateResult);
				}
				System.err.println("微信设置登录名称的返回值为：" + results);
				this.render(response, results);
			} else {
				this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
			}
		} else {
			this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
		}
	}

	/**
	 * 跳转至微信提现页面
	 * 
	 * @return 2018年1月19日14:04:13
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value = "/wechat/user/wechatWithdraw")
	public ModelAndView skipWechatWithdraw(HttpServletRequest request, HttpServletResponse response) throws JsonParseException, JsonMappingException, IOException {
		ModelAndView andView = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId == null || userId.equals("") || userId.equals("null")) {
			return new ModelAndView("redirect:/wechat/oauth2/checkSignature/login.htm");
		}
		ObjectMapper objectMapper = new ObjectMapper();
		// 用户可用余额接口
		String url = SysContext.VPAYURL + "/account/funds?userId=" + userId;
		String result = HttpUtil.getUrl(url);
		_log.info("跳转至提现页面查询用户账户的返回值为：{}", result);
		Map accountMap = objectMapper.readValue(result, Map.class);
		_log.info("跳转至提现页面查询用户账户转换后的返回值为：{}", accountMap);
		andView.addObject("balance", accountMap.get("balance"));
		_log.info("查询用户提现账号信息的参数为：{}", userId);
		// 查询用户提现账号
		String urls = SysContext.VPAYURL + "/withdraw/account/info?userId=" + userId;
		String results = HttpUtil.getUrl(urls);
		_log.info("查询用户提现账号信息的返回值为：{}", results);
		String withdrawNumber = "0";
		String id = "";
		String withdrawType = "0";
		String bankAccountNo = "";
		String bankAccountName = "";
		String contactTel = "";
		String openAccountBank = "";
		String seviceCharge = "0";
		if (results != null && !results.equals("") && !results.equals("null")) {
			Map map = objectMapper.readValue(results, Map.class);
			_log.info("将用户提现账号信息转为map的返回值为：{}", String.valueOf(map));
			withdrawNumber = String.valueOf(map.get("withdrawNumber"));
			id = String.valueOf(map.get("id"));
			withdrawType = String.valueOf(map.get("withdrawType"));
			bankAccountNo = String.valueOf(map.get("bankAccountNo"));
			bankAccountName = String.valueOf(map.get("bankAccountName"));
			contactTel = String.valueOf(map.get("contactTel"));
			openAccountBank = String.valueOf(map.get("openAccountBank"));
			seviceCharge = String.valueOf(map.get("seviceCharge"));
		}
		// 获取用户提现次数
		andView.addObject("withdrawNumber", withdrawNumber);
		// 用户账号金额信息
		andView.addObject("orderId", String.valueOf(UUID.randomUUID()).replaceAll("-", ""));
		andView.addObject("id", id);
		andView.addObject("withdrawType", withdrawType);
		andView.addObject("bankAccountNo", bankAccountNo);
		andView.addObject("bankAccountName", bankAccountName);
		andView.addObject("contactTel", contactTel);
		andView.addObject("openAccountBank", openAccountBank);
		andView.addObject("seviceCharge", seviceCharge);
		andView.setViewName("/htm/wechat/user/withdraw");
		return andView;
	}

	/**
	 * 提交提现申请
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/wechat/user/withdrawalApplicationSubmit")
	public void withdrawalApplicationSubmit(HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId == null || userId.equals("") || userId.equals("null")) {
			System.out.println("=======用户未登录=====");
			this.render(response, "{\"msg\":\"您未登录！\", \"flag\":\"false\"}");
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String id = String.valueOf(request.getParameter("id"));
		String withdrawType = String.valueOf(request.getParameter("withdrawType"));
		String orderId = String.valueOf(request.getParameter("orderId"));
		String amount = "";
		if (withdrawType.equals("1")) {
			amount = String.valueOf(request.getParameter("bankAmount"));
		} else {
			amount = String.valueOf(request.getParameter("alipayAmount"));
		}
		map.put("userId", userId);
		map.put("id", id);
		map.put("orderId", orderId);
		map.put("tradeTitle", "大卖网络-用户提现");
		map.put("tradeAmount", amount);
		map.put("opId", userId);
		map.put("mac", NetUtils.getFirstMacAddrOfLocalHost());
		map.put("ip", NetUtils.getFirstIpOfLocalHost());
		System.err.println("用户申请提现的参数为：" + map.toString());
		String results = HttpUtil.postUrl(SysContext.VPAYURL + "/withdraw/apply", map);
		if (!results.equals("") && !results.equals("null") && !results.equals("[]") && results != null) {
			System.err.println("用户申请提现返回：" + results);
			String result = JsonUtil.getJSONValue(results, "result");
			if (result.equals("1")) {
				// 缓存用户提现次数
				this.render(response, "{\"msg\":\"提交成功！\", \"flag\":\"true\"}");
			} else if (result.equals("0")) {
				this.render(response, "{\"msg\":\"参数不正确！\", \"flag\":\"false\"}");
			} else if (result.equals("-1")) {
				this.render(response, "{\"msg\":\"您未登录！\", \"flag\":\"false\"}");
			} else if (result.equals("-2")) {
				this.render(response, "{\"msg\":\"您已被锁定！\", \"flag\":\"false\"}");
			} else if (result.equals("-3")) {
				this.render(response, "{\"msg\":\"您未登录！\", \"flag\":\"false\"}");
			} else if (result.equals("-4")) {
				this.render(response, "{\"msg\":\"您的账号已被锁定！\", \"flag\":\"false\"}");
			} else if (result.equals("-5")) {
				this.render(response, "{\"msg\":\"您未登录！\", \"flag\":\"false\"}");
			} else if (result.equals("-6")) {
				this.render(response, "{\"msg\":\"您的余额不足！\", \"flag\":\"false\"}");
			}
		} else {
			this.render(response, "{\"msg\":\"提交失败！\", \"flag\":\"false\"}");
		}
	}

	/**
	 * 修改登录密码页面
	 * 
	 * @return 2018年2月7日15:12:06
	 */
	@RequestMapping("/wechat/user/updateLoginPwd")
	public ModelAndView updateLoginPwd() {
		return new ModelAndView("/htm/wechat/user/updateLoginPwd");
	}

	/**
	 * 修改登录密码下一步
	 * 
	 * @return 2018年2月7日15:12:09
	 */
	@RequestMapping(value = { "/wechat/user/updateLoginPwdNextPage" })
	public ModelAndView updateLoginPwdNextPage() {
		return new ModelAndView("/htm/wechat/user/updateLoginPwdNextWalk");
	}

	/**
	 * 跳转至实名认证页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/verifyRealNamePage")
	public ModelAndView setVerifyRealNamePage(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		ModelAndView andView = new ModelAndView();
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			andView.setViewName("/htm/wechat/user/verifyRealName");
			return andView;
		} else {
			andView.setViewName("redirect:/wechat/oauth2/checkSignature/login.htm");
			return andView;
		}
	}

	/**
	 * 实名认证申请
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/verifyRealNameApply")
	public void verifyRealNameApply(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> map) throws IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"msg\":\"您没有登录\",\"result\":-11}");
			return;
		}
		String idCard = (String) map.get("idCard");
		boolean flag = RegexUtils.matchIdcard(idCard);
		if (flag == false) {
			this.render(response, "{\"msg\":\"身份证输入有误\",\"result\":-11}");
			return;
		}
		String[] pics = ((String) map.get("pic")).split(",");
		System.err.println("申请实名认证的图片参数为：" + Arrays.toString(pics));
		String picOne = "";
		String picTwo = "";
		String picThree = "";
		String picFour = "";
		if (pics.length > 3) {
			picFour = pics[3];
		}
		if (pics.length > 2) {
			picThree = pics[2];
		}
		if (pics.length > 1) {
			picTwo = pics[1];
		}
		if (pics.length > 0) {
			picOne = pics[0];
		}
		map.put("userId", userId);
		map.put("picOne", picOne);
		map.put("picTwo", picTwo);
		map.put("picThree", picThree);
		map.put("picFour", picFour);
		System.err.println("申请实名认证的参数为：" + String.valueOf(map));
		// 添加用户实名认证申请
		String results = HttpUtil.postUrl(SysContext.RNAURL + "/verifyRealName/apply", map);
		System.err.println("申请实名认证的返回值为：" + results);
		if (results == null || results.equals("") || results.equals("[]")) {
			this.render(response, "{\"msg\":\"提交失败\",\"result\":-10}");
			return;
		}
		this.render(response, results);
	}

	/**
	 * 跳转至实名认证页面结果页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/verifyResult")
	public ModelAndView verifyResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelAndView andView = new ModelAndView();
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			andView.setViewName("/htm/wechat/user/verifyResult");
			return andView;
		} else {
			andView.setViewName("redirect:/wechat/oauth2/checkSignature/login.htm");
			return andView;
		}
	}
	
	

	/**
	 * 查询账号余额交易信息
	 * 
	 * @param request
	 * @param response
	 * @param pageNum
	 * @param pageSize
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/accountTrade")
	public void accountTrade(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		map.put("pageNum", pageNum);
		map.put("pageSize", pageSize);
		map.put("accountId", userId);
		_log.info("查询账号交易信息的参数为：{}", map.toString());
		String trades = OkhttpUtils.get(SysContext.VPAYURL + "/afc/trade/balancelist", map);
		_log.info("查询账号交易信息的返回值为：{}", trades);
		this.render(response, trades);
	}

	/**
	 * 查询用户返现金交易信息
	 * 
	 * @param request
	 * @param response
	 * @param pageNum
	 * @param pageSize
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/cashbackTrade")
	public void cashbackTrade(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) throws IOException {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accountId", userId);
		map.put("pageNum", pageNum);
		map.put("pageSize", pageSize);
		_log.info("查询用户返现金交易记录的参数为：{}", map.toString());
		String cashbackTrades = OkhttpUtils.get(SysContext.VPAYURL + "/afc/trade/cashbacklist", map);
		_log.info("查询用户返现金交易记录的返回值为：{}", cashbackTrades);
		this.render(response, cashbackTrades);
	}

	/**
	 * 查询用户提现中数据信息
	 * 
	 * @param request
	 * @param response
	 * @param pageNum
	 * @param pageSize
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/beBeingWithdraw")
	public void beBeingWithdraw(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("pageNum") int pageNum, @RequestParam("pageSize") int pageSize) throws IOException {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("accountId", userId);
		map.put("withdrawState", 2);
		map.put("pageNum", pageNum);
		map.put("pageSize", pageSize);
		_log.info("查询用户返现金交易记录的参数为：{}", map.toString());
		String beBeingWithdraw = OkhttpUtils.get(SysContext.VPAYURL + "/afc/withdraw", map);
		_log.info("查询用户返现金交易记录的返回值为：{}", beBeingWithdraw);
		this.render(response, beBeingWithdraw);
	}

	/**
	 * 跳转至申请提现账号页面
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/wechat/user/applyWithdrAwaccountPage")
	public String applyWithdrAwaccountPage(HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			return "/htm/wechat/user/applywithdrawaccount";
		} else {
			return "redirect:/wechat/oauth2/checkSignature/login.htm";
		}
	}
	
	/**
	 * 提交申请提现账号信息
	 * @param request
	 * @param response
	 * @param applyWithdrAwaccounts
	 * @throws IOException 
	 */
	@RequestMapping("/wechat/user/submitApplyWithdrAwaccount")
	public void submitApplyWithdrAwaccount(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> applyWithdrAwaccounts) throws IOException {
		// 获取当前用户ID
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId == null || userId.equals("") || userId.equals("null")) {
			this.render(response, "{\"msg\":\"您没有登录\",\"result\":-11}");
			return ;
		}
		applyWithdrAwaccounts.put("applicantId", userId);
		applyWithdrAwaccounts.put("applicantIp", AgentUtils.getIpAddr(request, "nginx"));
		applyWithdrAwaccounts.put("accountId", userId);
		System.out.println("提交申请提现账户信息的参数为：" + String.valueOf(applyWithdrAwaccounts));
		String postByJsonParamsResult = OkhttpUtils.postByJsonParams(SysContext.VPAYURL + "/afc/withdrawaccountbindflow/addex", applyWithdrAwaccounts);
		System.out.println("提交申请提现账户信息的返回值为：" + postByJsonParamsResult);
		this.render(response, postByJsonParamsResult);
	}
	
	/**
	 * @Name: 修改登录密码页面
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/updatepaypwdpage" })
	public ModelAndView updatePayPwdPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		// 用户编号
		String loginuid = SysCache.getWeChatUserByColumn(request, "userId");
		if (null != loginuid && !"".equals(loginuid)) {
			mav.setViewName("/htm/wechat/user/updatePayPwd");
		} else {
			mav.setViewName("/wechat/oauth2/checkSignature/login.htm");
		}
		return mav;
	}
	
	/**
	 * @throws IOException 
	 * @Name: 查看支付密码是否存在
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/payPwIsExis", method = RequestMethod.GET)
	public void payPwIsExis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String id = SysCache.getWeChatUserByColumn(request, "userId");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		System.err.println("查看支付密码是否存在的参数为：" + String.valueOf(map));
		String result = OkhttpUtils.get(SysContext.USERCENTERURL + "/user/payPwIsExis",map);
		System.err.println("查看支付密码是否存在的结果为：" +result );
		this.render(response, result);
	}
	
	/**
	 * 设置支付密码
	 * 
	 * @param request
	 * @param response
	 * @param map
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/setPayPassword")
	public void setPayPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> map) throws IOException {
		System.out.println(String.valueOf(map));
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			map.put("wxId", wxId);
			System.out.println("设置支付密码的参数为：" + wxId);
			_log.info("设置支付密码的参数为：{}", wxId);
			String result = OkhttpUtils.postByFormParams(SysContext.USERCENTERURL + "/paypswd/add/bywxid", map);
			_log.info("设置支付密码的返回值为：{}", result);
			this.render(response, result);
		} else {
			this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
		}
	}
	
	/**
	 * 修改支付密码提交
	 * 
	 * @param request
	 * @param response
	 * @param map
	 * @throws IOException
	 */
	@RequestMapping("/wechat/user/changePayPassword")
	public void changePayPassword(HttpServletRequest request, HttpServletResponse response,
			@RequestParam Map<String, Object> map) throws IOException {
		System.out.println(String.valueOf(map));
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			// 获取当前登录用户编号
			String wxId = SysCache.getWeChatUserByColumn(request, "openid");
			map.put("wxId", wxId);
			System.out.println("修改支付密码的参数为：" + wxId);
			String result = OkhttpUtils.postByFormParams(SysContext.USERCENTERURL + "/paypswd/modify/bywxid", map);
			System.out.println("微信修改支付密码的返回值为：" + result);
			this.render(response, result);
		} else {
			this.render(response, "{\"msg\":\"您未登录！\", \"result\":\"-74110\"}");
		}
	}
	
	/**
	 * 申请提现、申请提现账户、添加实名认证信息
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("/wechat/user/applyWithdraw")
	public void applyWithdraw(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> map) throws IOException {
		// 获取当前登录用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		map.put("applicantId", userId);
		map.put("ip", AgentUtils.getIpAddr(request, "nginx"));
		map.put("tradeTitle", "大卖网络-用户提现");
		System.out.println(map);
		String results = OkhttpUtils.postByJsonParams(SysContext.VPAYURL + "/withdraw/apply", map);
		_log.info("申请提现的返回值为：{}", results);
		this.render(response, results);
	}
}