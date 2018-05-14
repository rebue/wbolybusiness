package com.wboly.wechat.controller.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.modules.service.user.VblUserService;
import com.wboly.rpc.Client.UserRPCClient;
import com.wboly.rpc.entity.CollectionEntity;
import com.wboly.rpc.entity.FeedbackEntity;
import com.wboly.rpc.entity.UserEntity;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.MacAddressUtil;
import com.wboly.system.sys.util.RegexValidateUtil;
import com.wboly.system.sys.util.SessionUtil;
import com.wboly.system.sys.util.SmsUtil;
import com.wboly.system.sys.util.WriterJsonUtil;
import net.sf.json.JSONArray;
import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 用户 .java
 * @Author: nick
 */
@Controller
public class WeChatUserController extends SysController {

	@Autowired
	private VblUserService vbluserService;
	/**
	 * @Name: 反馈页面
	 * @throws Exception
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/user/feedbackPage")
	public ModelAndView feedbackPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			mav.setViewName("/htm/wechat/collect/feedback");
		}

		return mav;
	}

	/**
	 * 添加用户反馈信息
	 * 
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/saveFeedback")
	public void addUserFeedback(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		Object shopId = SessionUtil.getShopByColumn(request, "shopId");

		if (shopId == null) {
			this.render(response, "{\"message\":\"没有门店信息\",\"flag\":false}");
			return;
		}

		String message = request.getParameter("message");
		String imgPath = request.getParameter("imgPath");
		String mobile = request.getParameter("mobile");
		String type = request.getParameter("type");

		if (message == null || "".equals(message) || imgPath == null || mobile == null || type == null
				|| "".equals(type)) {

			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		FeedbackEntity entity = new FeedbackEntity();

		entity.setShopId(Integer.valueOf(shopId.toString()));
		entity.setMessage((message == null ? "" : message));
		entity.setImgPath((imgPath == null ? "" : imgPath));
		entity.setUserId(userId);
		entity.setUsercontact((mobile == null ? "" : mobile));
		entity.setType((type == null ? "" : type));

		UserRPCClient urc = new UserRPCClient();
		int addFeedback = urc.client.addFeedback(entity);
		urc.close();

		if (addFeedback > 0) {
			this.render(response, "{\"message\":\"反馈成功<br/>我们会尽快处理\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"反馈失败\",\"flag\":false}");
	}

	/**
	 * @Name: 地址管理页面(新)__目前使用这个地址链接
	 * @throws Exception
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/user/newAddressPage")
	public ModelAndView newAddressManagerPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
	public void delUserAddress(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, IOException {
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
		if (mobile == null || mobile.equals("") || realName == null || realName.equals("") || address == null || areaIds == null || areaIds.split(",").length < 3) {
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
	public void upAddressData(HttpServletRequest request, HttpServletResponse response) throws TException, JsonParseException, JsonMappingException, IOException {
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
	 * @Name: 用户推广关系列表
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/getRelatedList")
	public void getPopularizeRelatedList(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		String limit = request.getParameter("limit");

		String start = request.getParameter("start");

		if (start == null || start.equals("") || limit == null || limit.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		Map<String, Object> hmap = new HashMap<String, Object>();

		hmap.put("userId", userId);
		hmap.put("start", start);
		hmap.put("limit", limit);

		List<Map<String, Object>> map = vbluserService.selectUserExtendsInfo(hmap);

		this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(map) + ",\"flag\":true}");
	}

	/**
	 * @Name: 获取用户推广收益
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/getGainList")
	public void getPopularizeGainList(HttpServletRequest request, HttpServletResponse response) {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		String start = request.getParameter("start");
		String limit = request.getParameter("limit");
		if (start == null || start.equals("") || limit == null || limit.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		CollectionEntity entity = new CollectionEntity();
		entity.setUserId(userId);
		entity.setStart(Integer.valueOf(start));
		entity.setLimit(Integer.valueOf(limit));
		List<Map<String, String>> list = vbluserService.UserPopularizeGain(entity);
		this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(list) + ",\"flag\":true}");
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
			mav.setViewName("/htm/wechat/user/wallet");
		}
		return mav;
	}

	/**
	 * @Name: 用户钱包接口
	 * @Author: nick
	 */
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
		UserEntity entity = new UserEntity();

		entity.setUserId(userId);

		Map<String, Object> map = vbluserService.UserAllAmount(entity);

		// 待返现总金额 单位:分
		map.put("residueBacLimit", vbluserService.selectResidueBacLimitByParm(entity).get("residueBacLimit"));
		System.err.println("WX:用户编号为:" + userId + "\t 查询账户余额成功返回");
		DecimalFormat df = new DecimalFormat("0.00");
		map.put("availableBalance", df.format(JsonUtil.getJsonValue(result, "balance")));// 账户余额,单位:分
		map.put("sumretailBacLimit", df.format(JsonUtil.getJsonValue(result, "cashback")));// 可用返现金额，单位:分

		// 返现总金额单位:分
		map.put("usableBacLimit", Integer.parseInt(vbluserService.selectResidueBacLimitByParm(entity).get("residueBacLimit").toString())
				+ new BigDecimal(String.valueOf(JsonUtil.getJsonValue(result, "cashback"))).multiply(new BigDecimal(100)).doubleValue());
		this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(map) + ",\"flag\":true}");
	}

	/**
	 * @Name: 获取用户订单收货地址
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/getorderAddress")
	public void getUserOrderAddress(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		String isDefault = request.getParameter("isDefault");
		String addressIds = request.getParameter("address");
		if (isDefault == null || isDefault.equals("") || addressIds == null || addressIds.equals("")) {
			this.render(response, "{\"message\":\"您的请求有误\",\"flag\":false}");
			return;
		}

		UserEntity entity = new UserEntity();
		entity.setIsDefault(Integer.parseInt(isDefault));
		entity.setUserId(userId);
		entity.setAddressIds(addressIds);

		List<Map<String, String>> list = vbluserService.selectUserAddress(entity);
		if (list.size() > 0) {
			this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(list) + ",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":\"\",\"flag\":false}");
		}
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
	 * @Name: 用户登录跳转页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/logInPage")
	public ModelAndView UserLogInJump() {
		return new ModelAndView("/htm/wechat/login/login");
	}

	/**
	 * @Name: 用户注册跳转页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/registerPage")
	public ModelAndView UserRegisterJump() {
		return new ModelAndView("/htm/wechat/login/reg");
	}

	/**
	 * @Name: 用户注册下一步
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/NextStepPage")
	public ModelAndView UserNextStepJump() {
		return new ModelAndView("/htm/wechat/login/regNextStep");
	}

	/**
	 * @Name: 验证该用户名是否已被使用
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/user/verifyName", params = { "name" }, method = RequestMethod.POST)
	public void VerifyIsHaveName(HttpServletRequest request, HttpServletResponse response) {

		String usersName = "";
		try {
			usersName = new String(request.getParameter("name").getBytes(), "gb2312");
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
	 * @Name: 注册获取邮箱验证码
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wachat/user/getRegisterCodeInEmail" }, method = RequestMethod.POST, params = { "name",
			"sendType" })
	public void getregistercodebyemail(HttpServletRequest request, HttpServletResponse response, Model model)
			throws Exception {

		String loginname = request.getParameter("name");// 用户名
		String email = request.getParameter("sendType");// 邮箱

		// retrun 大于0发送成功，-101用户名不能为空，-102邮箱不能为空,-103账号验证失败，-104访问超时,-105用户名存在
		// 0接口参数有误，-1获取验证码频率太快,-2邮件发送失败,-3接口密匙不正确,-4邮箱已存在
		if (loginname == null || "".equals(loginname)) {// 用户名验证
			this.render(response, "{\"message\":\"用户名不能为空\",\"flag\":false}");
		} else {
			if (email == null || "".equals(email)) {// 邮箱验证
				this.render(response, "{\"message\":\"邮箱不能为空\",\"flag\":false}");
			} else {

				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

				String key = MD5CodeUtil.md5GB2312(loginname + dzCookieKey);// 秘钥加密

				String url = innerInterface + "user/isexistsbyusername?";

				Map<String, Object> maploginname = new HashMap<String, Object>();
				maploginname.put("username", loginname);
				maploginname.put("key", key);
				// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
				String content = HttpUtil.postUrl(url, maploginname).replace(System.lineSeparator(), "");

				if (content == null || "".equals(content) || "null".equals(content)) {// 账号验证
					this.render(response, "{\"message\":\"用户验证失败\",\"flag\":false}");
				} else {
					int num = Integer.parseInt(content.trim());
					if (num == 1) {
						this.render(response, "{\"message\":\"用户名已存在\",\"flag\":false}");
					} else if (num != 0 && num != 1) {
						this.render(response, "{\"message\":\"邮箱验证失败\",\"flag\":false}");
					} else {

						// 注册-发送注册验证码到用户注册邮箱
						String sendEmailUrl = innerInterface + "user/postregisteremail?";
						String postkey = MD5CodeUtil.md5(email + dzCookieKey);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("email", email);
						map.put("uid", 0);
						map.put("postkey", postkey);
						String context = HttpUtil.postUrl(sendEmailUrl, map).replace(System.lineSeparator(), "");
						if (context == null || "".equals(context) || "null".equals(context)) {
							this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
						} else {
							Object jsonValue = JsonUtil.getJsonValue(context, "postResult");
							if (jsonValue == null) {
								this.render(response, "{\"message\":\"邮箱验证失败\",\"flag\":false}");
							} else {
								int con = Integer.parseInt(jsonValue.toString());
								if (con > 0) {
									this.render(response, "{\"message\":\"发送成功\",\"flag\":true}");
									return;
								}
								if (con == 0) {
									this.render(response, "{\"message\":\"参数有误\",\"flag\":false}");
									return;
								}
								if (con == -1) {
									this.render(response, "{\"message\":\"获取验证码频率太快\",\"flag\":false}");
									return;
								}
								if (con == -2) {
									this.render(response, "{\"message\":\"邮箱发送失败\",\"flag\":false}");
									return;
								}
								if (con == -3) {
									this.render(response, "{\"message\":\"接口秘钥不正确\",\"flag\":false}");
									return;
								}
								if (con == -4) {
									this.render(response, "{\"message\":\"邮箱已被使用\",\"flag\":false}");
									return;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @Name: 注册获取短信验证码
	 * @Author: nick
	 */
	@RequestMapping(value = { "/wachat/user/getRegisterCodeInSms" }, method = RequestMethod.POST, params = { "name",
			"sendType" })
	public void getsmscode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String unionid = String.valueOf(request.getSession().getAttribute("unionid")); // 获取微信用户的unionid
		String uip = IpUtil.getIp(request); // 获取微信用户ip地址
		String mac = MacAddressUtil.getLocalMac();// request.getParameter("mac");//获取用户MAC地址
		String mobile = request.getParameter("sendType");// 手机号码
		System.err.println(mobile + ", 注册的ip地址为：" + uip + ", 注册的mac地址为：" + mac + ", 注册的微信unionid为：" + unionid);
		if (unionid != null && !"".equals(unionid) && !"null".equals(unionid)) {
			String username = request.getParameter("name");// 用户名

			// return
			// 大于0为成功，-101用户不能为空，-102手机号码不能为空，-103MAC地址不能为空，-104账号验证失败,-105验证手机号失败,-106手机已绑定,-107用户名存在
			// -201 30分钟内已发送三次，-202代码异常
			if (username == null || "".equals(username)) {// 用户名验证
				this.render(response, "{\"message\":\"用户名不能为空\",\"flag\":false}");
			} else {
				if (mobile == null || "".equals(mobile)) {// 手机验证
					this.render(response, "{\"message\":\"手机号码不能为空\",\"flag\":false}");
				} else {
					if (mac == null || "".equals(mac)) {// MAC地址验证
						this.render(response, "{\"message\":\"获取不到您的MAC地址\",\"flag\":false}");
					} else {

						String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
						String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

						String mobileurl = innerInterface + "user/isexistsbymobile?";
						String mobilekey = MD5CodeUtil.md5(mobile + dzCookieKey);

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("mobile", mobile);
						map.put("key", mobilekey);
						String mobileresult = HttpUtil.postUrl(mobileurl, map).replace(System.lineSeparator(), "");

						if (mobileresult == null || "".equals(mobileresult)) {
							this.render(response, "{\"message\":\"验证手机号失败\",\"flag\":false}");
						} else {
							int mobilenum = Integer.parseInt(mobileresult.trim());
							if (mobilenum != 0) {
								this.render(response, "{\"message\":\"该手机号已被绑定\",\"flag\":false}");
							} else {
								String key = MD5CodeUtil.md5GB2312(username + dzCookieKey);// 秘钥加密
								String url = innerInterface + "user/isexistsbyusername?";
								Map<String, Object> mapname = new HashMap<String, Object>();
								mapname.put("username", username);
								mapname.put("key", key);
								// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
								String content = HttpUtil.postUrl(url, mapname).replace(System.lineSeparator(), "");

								if (content == null || "".equals(content.trim())) {// 账号验证
									this.render(response, "{\"message\":\"账户验证失败\",\"flag\":false}");
								} else {
									if ("0".equals(content.trim())) {

										String num = SmsUtil.sendLoginSMS(request, mobile, mac);

										if (null == num || "".equals(num) || "null".equals(num)) {
											this.render(response, "{\"message\":\"系统繁忙!\",\"flag\":false}");
											return;
										}

										if (num.equals("-103")) {
											this.render(response, "{\"message\":\"短信运营商已停止服务\",\"flag\":false}");
											return;
										}
										if (num.equals("-401")) {
											this.render(response,
													"{\"message\":\"每天一个号码只能发送五次,当天凌晨清除\",\"flag\":false}");
											return;
										}
										this.render(response, "{\"message\":\"发送成功\",\"flag\":true}");
									} else if ("1".equals(content.trim())) {
										this.render(response, "{\"message\":\"该用户名已被使用\",\"flag\":false}");
									} else {
										this.render(response, "{\"message\":\"账户验证失败\",\"flag\":false}");
									}
								}
							}

						}
					}
				}
			}
		}
	}

	/**
	 * @Name: 短信验证码注册
	 * @Author: nick
	 */
	@RequestMapping(value = { "/wechat/user/smsregister" })
	public void smsRegister(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String userName = request.getParameter("user");// 用户名
		String passWord = request.getParameter("password");// 登录密码
		// 注册来源(1:微薄利网页端;2:微薄利手机网页端;3:微薄利手机应用端;4:微薄利Android端;5:微薄利IOS端;6:商超网页端;7:商超Android端;8:商超IOS端)
		String source = request.getParameter("source");// 登录密码
		String mobile = request.getParameter("mobile");// 手机号码
		String smscode = request.getParameter("code");// 短信验证码
		String mac = MacAddressUtil.getLocalMac();// 获取用户MAC地址

		// return 大于0注册成功，-101用户名不能为空 -102登录密码不能为空
		// -103手机号码不能为空，-104短信验证码错误，-105mac地址不能为空,-106服务器繁忙
		// 0参数错误，-1用户名已存在 ，-2验证码失效，-3验证码不正确，-4接口密匙错误，-6手机号码已存在，-7手机格式不正确
		if (userName == null || "".equals(userName)) {// 用户名验证
			this.render(response, "{\"message\":\"用户名不能为空\",\"flag\":false}");
		} else {
			if (passWord == null || "".equals(passWord)) {// 密码验证
				this.render(response, "{\"message\":\"密码不能为空\",\"flag\":false}");
			} else {
				if (mobile == null || "".equals(mobile)) {// 手机号验证
					this.render(response, "{\"message\":\"手机号码不能为空\",\"flag\":false}");
				} else {
					if (smscode == null || "".equals(smscode)) {// 短信验证码验证
						this.render(response, "{\"message\":\"短信验证码不能为空\",\"flag\":false}");
					} else {
						if (mac == null || "".equals(mac)) {// mac地址验证
							this.render(response, "{\"message\":\"MAC地址不能为空\",\"flag\":false}");
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
								String content = HttpUtil.postUrl(url, param).replace(System.lineSeparator(), "");
								System.out.println(content + ":注册________");
								// 请求结果：1注册成功，0参数错误，-1用户名已存在
								// ，-2验证码会话超时，-3验证码不正确，-4密匙错误，-5电子邮箱已存在，-6手机号码已存在，-7手机格式不正确
								if (content != null && !"".equals(content) || "null".equals(content)) {

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

									if (content.equals("-1")) {
										this.render(response, "{\"message\":\"用户名已被使用\",\"flag\":false}");
									}
									if (content.equals("-2")) {
										this.render(response, "{\"message\":\"验证码已失效\",\"flag\":false}");
									}
									if (content.equals("-3")) {
										this.render(response, "{\"message\":\"验证码不正确\",\"flag\":false}");
									}
									if (content.equals("-4")) {
										this.render(response, "{\"message\":\"接口秘钥错误\",\"flag\":false}");
									}
									if (content.equals("-6")) {
										this.render(response, "{\"message\":\"手机号码已被使用\",\"flag\":false}");
									}
									if (content.equals("-7")) {
										this.render(response, "{\"message\":\"手机格式不正确\",\"flag\":false}");
									}
									if (Integer.valueOf(content) > 0) {
										this.render(response, "{\"message\":\"注册成功\",\"flag\":true}");
									}

								} else {
									this.render(response, "{\"message\":\"服务器繁忙\",\"flag\":false}");
								}
							} else {
								this.render(response, "{\"message\":\"短信验证码错误\",\"flag\":false}");
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @Name: 自动登录
	 * @Author: nick
	 */
	public String isAutoLogin(HttpServletRequest request, String un) {

		String uid = "";
		String password = "";
		// 获取当前站点的所有Cookie
		if (request.getCookies() != null) {
			Cookie[] cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				if ("wechatuid".equals(cookies[i].getName())) {
					uid = cookies[i].getValue();
				}

				if ("wechatupa".equals(cookies[i].getName())) {
					password = cookies[i].getValue();
				}
			}
			if (!uid.equals("") && !password.equals("")) {
				String userName = SysCache.getWeChatUserByColumn(request, "userName");
				if (userName != null && userName.equals(un)) {
					String compare = SysCache.getWeChatUserByColumn(request, "password");
					if (compare != null && compare.equals(password)) {
						password = SysCache.getWeChatUserByColumn(request, "compare");
					} else {
						password = "";
					}
				} else {
					password = "";
				}
			} else {
				password = "";
			}
		}
		return password;
	}

	/**
	 * @Name: 提交修改支付密码信息
	 * @throws Exception
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/updatepaypwdsubmit" }, params = { "item",
			"password" }, method = RequestMethod.POST)
	public void updatePayPwdNextWalk(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String item = request.getParameter("item");
		String password = request.getParameter("password");

		// 用户名
		String loginname = SysCache.getWeChatUserByColumn(request, "userName");
		// 用户编号
		String loginuid = SysCache.getWeChatUserByColumn(request, "userId");

		if (null == loginname || "".equals(loginname)) {
			this.render(response, "{\"message\":\"您未登陆\",\"flag\":false}");
			return;
		}

		String mac = MacAddressUtil.getLocalMac();// 获取用户MAC地址

		if (null == item || "".equals(item) || null == password || "".equals(password) || null == mac
				|| "".equals(mac)) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		// 手机号/邮箱
		String sendType = JsonUtil.GetJsonValue(item, "sendType");
		// 验证码
		String code = JsonUtil.GetJsonValue(item, "code");

		if (null == sendType || "".equals(sendType)) {
			this.render(response, "{\"message\":\"手机/邮箱必填一个\",\"flag\":false}");
			return;
		}

		if (null == code || "".equals(code)) {
			this.render(response, "{\"message\":\"验证码不能为空\",\"flag\":false}");
			return;
		}

		Integer resetPwdErrorNum = Integer.parseInt(SysCache.getResetPwdErrorNum(loginuid));
		// 重置密码错误次数
		if (resetPwdErrorNum > 4) {
			this.render(response,
					"{\"prompt\":2,\"message\":\"检测到您的账户有异常操作<br/>为了您的账户安全<br/>您在3小时内无法再次访问该操作\",\"flag\":false}");
			return;
		}

		if (sendType.indexOf("@") != -1) {
			if (!RegexValidateUtil.checkEmail(sendType)) {
				this.render(response, "{\"message\":\"邮箱格式有误\",\"flag\":false}");
				return;
			}

			String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
			String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

			String key = MD5CodeUtil.md5(2 + dzCookieKey);

			String url = innerInterface + "user/updatepasswordbyemail?";

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("loginname", loginname);
			param.put("type", 2);
			param.put("email", sendType);
			param.put("key", key);
			param.put("emailCode", code);
			System.err.println("邮箱提交参数:" + param);
			param.put("pswd", password);
			// 以post的方式提交到服务接口，context为返回的结果
			String content = HttpUtil.postUrl(url, param);
			System.err.println("邮箱提交返回:" + content);
			if (content != null && !"".equals(content) && !"null".equals(content)) {
				content = content.replace(System.lineSeparator(), "");
				if (content.equals("-106")) {
					this.render(response, "{\"message\":\"验证码错误\",\"flag\":false}");
					return;
				}
				if (content.equals("0")) {
					this.render(response, "{\"message\":\"参数有误\",\"flag\":false}");
					return;
				}
				if (content.equals("-1")) {
					resetPwdErrorNum += 1;
					SysCache.setResetPwdErrorNum(loginuid, String.valueOf(resetPwdErrorNum));
					this.render(response, "{\"message\":\"密匙不正确\",\"flag\":false}");
					return;
				}
				if (content.equals("-2")) {
					this.render(response, "{\"message\":\"未能找到用户信息\",\"flag\":false}");
					return;
				}
				if (content.equals("-3")) {
					resetPwdErrorNum += 1;
					SysCache.setResetPwdErrorNum(loginuid, String.valueOf(resetPwdErrorNum));
					this.render(response, "{\"message\":\"绑定号码不一致\",\"flag\":false}");
					return;
				}
				if (content.equals("-4")) {
					this.render(response, "{\"message\":\"验证码已过期\",\"flag\":false}");
					return;
				}
				if (content.equals("-5")) {
					this.render(response, "{\"message\":\"验证码不正确\",\"flag\":false}");
					return;
				}
				Integer re = Integer.parseInt(content.toString().trim());
				if (re > 0) {
					this.render(response, "{\"message\":\"修改成功\",\"flag\":true}");
					return;
				}
				this.render(response, "{\"message\":\"修改失败\",\"flag\":false}");
			} else {
				this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
			}
		} else {
			boolean result = SmsUtil.ValidateSmsCode(sendType, mac, code);// 短信验证码验证
			if (result) {

				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

				String key = MD5CodeUtil.md5(2 + dzCookieKey);

				String url = innerInterface + "user/updatepasswordbymobile?";

				Map<String, Object> param = new HashMap<String, Object>();
				param.put("loginname", loginname);
				param.put("type", 2);// 1修改登录密码，2修改支付密码
				param.put("mobile", sendType);
				param.put("key", key);
				// 以post的方式提交到服务接口，context为返回的结果
				System.err.println("手机提交参数:" + param);
				param.put("pswd", password);
				// 0:参数有误;-1:密匙不正确;-2:未能找到用户信息;-3:email与绑定不一致;-4:验证码已过期;-5:验证码不正确;-6:修改失败;>0:修改成功
				String content = HttpUtil.postUrl(url, param);
				System.err.println("手机提交返回:" + content);
				if (content != null && !"".equals(content) && !"null".equals(content)) {
					content = content.replace(System.lineSeparator(), "");
					if (content.equals("-401")) {
						this.render(response, "{\"message\":\"当前手机号发送次数</br>已超出\",\"flag\":false}");
						return;
					}
					if (content.equals("-106")) {
						this.render(response, "{\"message\":\"验证码错误\",\"flag\":false}");
						return;
					}
					if (content.equals("0")) {
						this.render(response, "{\"message\":\"参数有误\",\"flag\":false}");
						return;
					}
					if (content.equals("-1")) {
						resetPwdErrorNum += 1;
						SysCache.setResetPwdErrorNum(loginuid, String.valueOf(resetPwdErrorNum));
						this.render(response, "{\"message\":\"密匙不正确\",\"flag\":false}");
						return;
					}
					if (content.equals("-2")) {
						this.render(response, "{\"message\":\"未能找到用户信息\",\"flag\":false}");
						return;
					}
					if (content.equals("-3")) {
						resetPwdErrorNum += 1;
						SysCache.setResetPwdErrorNum(loginuid, String.valueOf(resetPwdErrorNum));
						this.render(response, "{\"message\":\"绑定号码不一致\",\"flag\":false}");
						return;
					}
					if (content.equals("-103")) {
						this.render(response, "{\"message\":\"短信服务已关闭\",\"flag\":false}");
						return;
					}
					Integer re = Integer.parseInt(content.toString().trim());
					if (re > 0) {
						this.render(response, "{\"message\":\"修改成功\",\"flag\":true}");
						return;
					}
					this.render(response, "{\"message\":\"修改失败\",\"flag\":false}");
				} else {
					this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
				}

			} else {
				this.render(response, "{\"message\":\"验证码错误\",\"flag\":false}");
			}
		}

	}

	/**
	 * @Name: 修改支付密码页面(下一步)
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/updatepaypwdnextpage" })
	public ModelAndView updatePayPwdNextPage() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/htm/wechat/user/nextWalk");
		return mav;
	}

	/**
	 * @Name: 修改支付密码页面
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/updatepaypwdpage" })
	public ModelAndView updatePayPwdPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();

		// 用户编号
		String loginuid = SysCache.getWeChatUserByColumn(request, "userId");

		if (null != loginuid && !"".equals(loginuid)) {
			// 获取网络服务接口前缀
			try {
				String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
				String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名
				String key = MD5CodeUtil.md5(dzCookieKey);

				String url = innerInterface + "user/getuserbindinfobyuserid?";// MD5加密

				Map<String, Object> maploginname = new HashMap<String, Object>();
				maploginname.put("userid", loginuid);
				maploginname.put("key", key);
				System.err.println("获取用户绑定信息参数:" + maploginname);
				// -1:参数有误;-2:密匙不正确;1:存在;0:不存在
				String content = HttpUtil.postUrl(url, maploginname);
				System.err.println("获取用户绑定信息返回:" + content);
				if (null == content) {
					mav.setViewName("/htm/wechat/login/login");
				} else {
					String binddata = JsonUtil.getJSONValue(content, "mobile");
					if (null == binddata || "".equals(binddata)) {
						binddata = JsonUtil.getJSONValue(content, "email");
					}
					mav.addObject("binddata", binddata);
					mav.setViewName("/htm/wechat/user/updatePwd");
				}
			} catch (Exception e) {
				e.printStackTrace();
				mav.setViewName("/htm/wechat/login/login");
			}

		} else {
			mav.setViewName("/htm/wechat/login/login");
		}

		return mav;
	}
	
	/**
	 * 验证手机号登录
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = { "/wechat/user/sendVerificationCode" }, params = { "sendType" }, method = RequestMethod.POST)
	public void sendVerificationCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.err.println("----- 微信 通过手机号码找回支付密码 -----");
		String sendType = request.getParameter("sendType");// 手机号码
		String mac = MacAddressUtil.getLocalMac();
		if (sendType.indexOf("@") != -1) {
			this.render(response, "{\"message\":\"目前只支持手机号码验证登录\",\"flag\":false}");
			return;
		} else {
			if (mac == null || "".equals(mac)) {// MAC地址验证
				this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
				return;
			} else {
				if (RegexValidateUtil.checkMobileNumber(sendType)) {
					String num = SmsUtil.sendLoginSMS(request, sendType, mac);// 发送短信验证码
					System.err.println("短信返回:" + num);
					if (null == num || "".equals(num) || "null".equals(num)) {
						this.render(response, "{\"message\":\"系统繁忙!\",\"flag\":false}");
						return;
					}
					Integer re = Integer.parseInt(num.toString().trim());
					if (re > 0) {
						this.render(response, "{\"message\":\"发送成功\",\"flag\":true}");
						return;
					}
					if (re == -401) {
						this.render(response, "{\"message\":\"当前手机号发送次数</br>已超出\",\"flag\":false}");
						return;
					}
					if (re == -201) {
						this.render(response, "{\"message\":\"30分钟内已发送三次\",\"flag\":false}");
						return;
					}
					if (re == -103) {
						this.render(response, "{\"message\":\"短信服务已关闭\",\"flag\":false}");
						return;
					}

					this.render(response, "{\"message\":\"发送验证码失败\",\"flag\":false}");
					return;
				} else {
					this.render(response, "{\"message\":\"输入的手机号码格式有误\",\"flag\":false}");
				}
			}
		}
	}

	/**
	 * @Name: 获取修改支付密码的短信验证码/短信验证码
	 * @Author: knick
	 */
	@RequestMapping(value = { "/wechat/user/getupdatepaypwdcode" }, params = { "sendType" }, method = RequestMethod.POST)
	public void getupdatepaypasswordsmscode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String mac = MacAddressUtil.getLocalMac();
		// 用户名
		String loginname = SysCache.getWeChatUserByColumn(request, "userName");

		if (null == loginname || "".equals(loginname)) {
			this.render(response, "{\"message\":\"您未登陆\",\"flag\":false}");
			return;
		}

		String sendType = request.getParameter("sendType");// 手机号码

		if (null == sendType || "".equals(sendType)) {
			this.render(response, "{\"message\":\"手机/邮箱必填一个\",\"flag\":false}");
			return;
		}

		if (sendType.indexOf("@") != -1) {
			System.err.println("----- 微信 通过邮箱找回支付密码 -----");
			String type = "2";// 修改类型（1：登录密码；2：支付密码）
			// retrun 大于0发送成功，-101用户名不能为空，-102邮箱不能为空,-103账号不存在，-104访问超时
			// 0接口参数有误，-1获取验证码频率太快,-2邮件发送失败,-3接口密匙不正确,-4邮箱已存在

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

			if (content == null || "".equals(content) || "null".equals(content)) {
				this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
			} else {
				content = content.replace(System.lineSeparator(), "");
				long uid = Long.parseLong(content.trim());
				if (uid <= 0) {
					this.render(response, -103);
				} else {

					// 注册-发送注册验证码到用户注册邮箱
					String sendEmailUrl = innerInterface + "user/postresetpwdemailsc?";
					String postkey = MD5CodeUtil.md5(sendType + dzCookieKey);
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("email", sendType);
					map.put("uid", uid);
					map.put("postkey", postkey);
					map.put("uname", loginname);
					map.put("type", type);
					System.err.println("邮箱请求参数:" + map);
					String context = HttpUtil.postUrl(sendEmailUrl, map);
					System.err.println("邮箱返回:" + context);
					if (context == null || "".equals(context)) {
						this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
					} else {
						context = context.replace(System.lineSeparator(), "");
						String postResult = JsonUtil.GetJsonValue(context, "postResult");
						if (null == postResult || "".equals(postResult)) {
							this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");
							return;
						}
						Integer re = Integer.parseInt(postResult.toString().trim());
						if (re > 0) {
							// 缓存用户邮箱验证码
							SysCache.setEmailVerificationCode(sendType, JsonUtil.GetJsonValue(context, "code"));
							this.render(response, "{\"message\":\"发送成功\",\"flag\":true}");
							return;
						}
						if (re == -2) {
							this.render(response, "{\"message\":\"网络异常\",\"flag\":false}");
							return;
						}
						this.render(response, "{\"message\":\"发送失败\",\"flag\":false}");
						return;
					}
				}
			}

		} else {
			System.err.println("----- 微信 通过手机号码找回支付密码 -----");
			if (mac == null || "".equals(mac)) {// MAC地址验证
				this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
				return;
			} else {

				if (RegexValidateUtil.checkMobileNumber(sendType)) {
					String num = SmsUtil.sendLoginSMS(request, sendType, mac);// 发送短信验证码
					System.err.println("短信返回:" + num);
					if (null == num || "".equals(num) || "null".equals(num)) {
						this.render(response, "{\"message\":\"系统繁忙!\",\"flag\":false}");
						return;
					}
					Integer re = Integer.parseInt(num.toString().trim());
					if (re > 0) {
						this.render(response, "{\"message\":\"发送成功\",\"flag\":true}");
						return;
					}
					if (re == -401) {
						this.render(response, "{\"message\":\"当前手机号发送次数</br>已超出\",\"flag\":false}");
						return;
					}
					if (re == -201) {
						this.render(response, "{\"message\":\"30分钟内已发送三次\",\"flag\":false}");
						return;
					}
					if (re == -103) {
						this.render(response, "{\"message\":\"短信服务已关闭\",\"flag\":false}");
						return;
					}

					this.render(response, "{\"message\":\"发送验证码失败\",\"flag\":false}");
					return;
				} else {
					this.render(response, "{\"message\":\"输入的手机号码格式有误\",\"flag\":false}");
				}
			}
		}
	}

	/**
	 * @Name: 用户登录
	 * @Author: nick
	 */
	@RequestMapping(value = { "/wechat/user/userlogin" }, method = RequestMethod.POST)
	public void userLogin(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String username = request.getParameter("user");// 获取用户名
		String autoLogin = isAutoLogin(request, username);
		String password = autoLogin.equals("") ? request.getParameter("password") : autoLogin;// 获取密码
		String source = request.getParameter("source");// 获取登录来源
		String agentInformation = request.getParameter("agentInformation");// 获取用户设备信息

		String areaId = SessionUtil.getShopByColumn(request, "areaId") == null ? "4"
				: SessionUtil.getShopByColumn(request, "areaId").toString();// 获取用户区域编号

		// 大于0登录成功，-101用户名不能为空,-102密码不能为空,-103mac地址不能为空，-104访问超时,-105密码错误3次，需要改密码才能登陆，-106账号不存在,-107登录失败
		// -2:密码错误,-3:账号被锁定,-5:用 户邮箱未验证
		if (username != null && !"".equals(username)) {

			if (password != null && !"".equals(password)) {

				if (source != null && !"".equals(source)) {

					int num = SysCache.getLoginErrorNum(username);// 查询用户错误次数

					if (num < 4) {

						String dzCookieKey = SysContext.CONFIGMAP.get("dzCookieKey");// 获取系统秘钥
						String innerInterface = SysContext.CONFIGMAP.get("innerInterface"); // 获取查询账号域名

						String postkey = MD5CodeUtil.md5(dzCookieKey);

						String url = innerInterface + "user/login?";

						Map<String, Object> map = new HashMap<String, Object>();
						map.put("userName", username);
						map.put("passWord", password);
						map.put("postkey", postkey);
						String loginMarker = "";
						if (source.equals("6")) {
							String ip = IpUtil.getIp(request);
							if (ip == null || "".equals(ip)) {
								this.render(response, "{\"message\":\"IP地址不能为空\",\"flag\":false}");// 网站登陆IP地址不能为空
								return;
							}
							loginMarker = ip;
							map.put("ip", ip);
						} else {
							String localMac = MacAddressUtil.getLocalMac();
							if (localMac == null || "".equals(localMac)) {
								this.render(response, "{\"message\":\"MAC地址不能为空\",\"flag\":false}");// 手机端登陆MAC地址不能为空
								return;
							}
							loginMarker = localMac;
							map.put("mac", localMac);
						}
						map.put("source", source);

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
									hmap.put("loginMarker", loginMarker);
									hmap.put("agentInfo", agentInformation);
									hmap.put("areaId", areaId);

									UserRPCClient userService = new UserRPCClient();
									int i = userService.client.UpUserInfo(hmap);
									if (i == 0) {
										System.out.println("记录登录信息失败");
									}
									userService.close();

									if (userId != null && !"".equals(userId)) {
										long userid = Long.parseLong(userId);
										if (userid > 0) {

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

												String userjson = "{\"userId\":" + userid + ",\"userName\":\""
														+ username + "\",\"img\":\"" + img + "\",\"score\":" + score
														+ ",\"experience\":" + experience + ",\"grade\":" + grade
														+ ",\"maxexperience\":" + maxexperience + ",\"password\":\""
														+ URLEncoder.encode(MD5CodeUtil.md5(password), "utf-8")
														+ "\",\"compare\":\"" + password + "\"}";

												Cookie cookie1 = new Cookie("wechatuid",
														MD5CodeUtil.md5(String.valueOf(userid)));
												cookie1.setPath("/");
												cookie1.setMaxAge(60 * 60 * 24 * 30);// 一个月内有效
												Cookie cookie2 = new Cookie("wechatupa", MD5CodeUtil.md5(password));
												cookie2.setPath("/");
												cookie2.setMaxAge(60 * 60 * 24 * 3);// 三天内有效
												response.addCookie(cookie1);
												response.addCookie(cookie2);

												// 用户信息存缓存
												SysCache.setWechatUser(MD5CodeUtil.md5(String.valueOf(userid)),
														userjson);

												this.render(response, "{\"message\":\"登陆成功\",\"flag\":true}");
											} else {
												this.render(response, "{\"message\":\"登陆失败\",\"flag\":false}");
											}
										} else {
											this.render(response, "{\"message\":\"登陆失败\",\"flag\":false}");
										}
									} else {
										this.render(response, "{\"message\":\"登陆失败\",\"flag\":false}");
									}

								} else {

									if (con == -2) {
										int num1 = SysCache.getLoginErrorNum(username) + 1;
										SysCache.setLoginErrorNum(username, num1);
									}

									switch (con) {
									case 0:
										this.render(response, "{\"message\":\"访问超时\",\"flag\":false}");// 参数不正确
										break;
									case -1:
										this.render(response, "{\"message\":\"账户不存在\",\"flag\":false}");
										break;
									case -2:
										this.render(response, "{\"message\":\"密码错误\",\"flag\":false}");
										break;
									case -3:
										this.render(response, "{\"message\":\"" + lockReason + "\",\"flag\":false}");// 账号被锁定!
										break;
									case -4:
										this.render(response, "{\"message\":\"账户不存在\",\"flag\":false}");// 缺少业务用户信
										break;
									case -5:
										this.render(response, "{\"message\":\"密码错误3次,需要修改密码才能登陆\",\"flag\":false}");
										break;
									case -6:
										this.render(response, "{\"message\":\"超时访问\",\"flag\":false}");
										break;
									default:
										this.render(response, "{\"message\":\"超时访问\",\"flag\":false}");
										break;
									}
								}

							} else {
								this.render(response, "{\"message\":\"超时访问\",\"flag\":false}");
							}
						} else {
							this.render(response, "{\"message\":\"超时访问\",\"flag\":false}");
						}

					} else {
						this.render(response, "{\"message\":\"密码错误3次,需要修改密码才能登陆\",\"flag\":false}");
					}
				} else {
					this.render(response, "{\"message\":\"登陆来源不能为空\",\"flag\":false}");
				}

			} else {
				this.render(response, "{\"message\":\"密码不能为空\",\"flag\":false}");
			}
		} else {
			this.render(response, "{\"message\":\"用户名不能为空\",\"flag\":false}");
		}
	}

	/**
	 * 跳转至微信提现页面
	 * @return
	 * 2018年1月19日14:04:13
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/wechat/user/wechatWithdraw")
	public ModelAndView skipWechatWithdraw(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView andView = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId == null || userId.equals("") || userId.equals("null")) {
			return new ModelAndView("redirect:/wechat/oauth2/checkSignature/login.htm");
		}
		// 查询用户是否有提现账号
		String urls = SysContext.VPAYURL + "/withdraw/account/exist/byuserid?userId=" + userId;
		String results = HttpUtil.getUrl(urls);
		System.out.println(results);
		// 如果没有则跳转至用户中心首页
		if (results.equals("true") || results.equals(true)) {
			// 获取用户提现次数
			int num = SysCache.getUserWithdrawalNumber(userId);
			andView.addObject("num", 3 - num);
			// 获取用户账号金额信息
			String priceUrl = SysContext.VPAYURL + "/account/funds?userId=" + userId;
			String priceResult = HttpUtil.getUrl(priceUrl);
			String balance = "0";
			if (priceResult != null && !priceResult.equals("null") && !priceResult.equals("")) {
				balance = JsonUtil.getJSONValue(priceResult, "balance");
			}
			andView.addObject("balance", balance);
			// 获取用户提现账号信息
			String url = SysContext.VPAYURL + "/withdraw/account?userId=" + userId;
			String result = HttpUtil.getUrl(url);
			String id = "0";
			String withdrawType = "0";
			String bankAccountNo = "0";
			String bankAccountName = "0";
			String contactTel = "0";
			String openAccountBank = "0"; // 开户银行
			if (result != null && !result.equals("") && !result.equals("null") && !result.equals("[]")) {
				List<Map<String, Object>> list = JSONArray.fromObject(result);
				id = String.valueOf(list.get(0).get("id")); 
				withdrawType = String.valueOf(list.get(0).get("withdrawType")); // 提现类型
				bankAccountNo = String.valueOf(list.get(0).get("bankAccountNo")); // 提现账号
				bankAccountName = String.valueOf(list.get(0).get("bankAccountName")); // 账号名称
				contactTel = String.valueOf(list.get(0).get("contactTel")); // 联系电话
				if (withdrawType.equals("2")) {
					openAccountBank = String.valueOf(list.get(0).get("openAccountBank"));
				} else {
					openAccountBank = "支付宝网银";
				}
			}
			andView.addObject("orderId", String.valueOf(UUID.randomUUID()).replaceAll("-", ""));
			andView.addObject("id", id);
			andView.addObject("withdrawType", withdrawType);
			andView.addObject("bankAccountNo", bankAccountNo);
			andView.addObject("bankAccountName", bankAccountName);
			andView.addObject("contactTel", contactTel);
			andView.addObject("openAccountBank", openAccountBank);
			andView.setViewName("/htm/wechat/user/withdraw");
		} else {
			andView.setViewName("redirect:/wechat/user/userCenter.htm");
		}
		return andView;
	}
	
	/**
	 * 提交提现申请
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
			return ;
		}
		// 获取用户提现次数
		int num = SysCache.getUserWithdrawalNumber(userId);
		if (num == 3) {
			this.render(response, "{\"msg\":\"您今天的提现次数已用完！\", \"flag\":\"false\"}");
			return ;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String id = String.valueOf(request.getParameter("id"));
		String withdrawType = String.valueOf(request.getParameter("withdrawType"));
		String orderId = String.valueOf(request.getParameter("orderId"));
		String amount = "";
		if (withdrawType.equals("2")) {
			amount = String.valueOf(request.getParameter("bankAmount"));
		} else {
			amount = String.valueOf(request.getParameter("alipayAmount"));
		}
		map.put("userId", userId);
		map.put("id", id);
		map.put("orderId", orderId);
		map.put("tradeTitle", "商超-用户提现");
		map.put("tradeAmount", amount);
		map.put("opId", userId);
		map.put("mac", MacAddressUtil.getLocalMac());
		map.put("ip", IpUtil.getIp(request));
		System.err.println("用户申请提现的参数为：" + map.toString());
		String results = HttpUtil.postUrl(SysContext.VPAYURL + "/withdraw/apply", map);
		if (!results.equals("") && !results.equals("null") && !results.equals("[]") && results != null) {
			System.err.println("用户申请提现返回：" + results);
			String result = JsonUtil.getJSONValue(results, "result");
			if (result.equals("1")) {
				// 缓存用户提现次数
				SysCache.setUserWithdrawalNumber(userId, String.valueOf(num + 1));
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
	 * @return
	 * 2018年2月7日15:12:06
	 */
	@RequestMapping("/wechat/user/updateLoginPwd")
	public ModelAndView updateLoginPwd() {
		return new ModelAndView("/htm/wechat/user/updateLoginPwd");
	}
	
	/**
	 * 修改登录密码下一步
	 * @return
	 * 2018年2月7日15:12:09
	 */
	@RequestMapping(value = { "/wechat/user/updateLoginPwdNextPage" })
	public ModelAndView updateLoginPwdNextPage() {
		return new ModelAndView("/htm/wechat/user/updateLoginPwdNextWalk");
	}
}
