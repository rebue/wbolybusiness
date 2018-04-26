package com.wboly.modules.controller.User;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.modules.service.order.VblOrderService;
import com.wboly.modules.service.user.VblUserService;
import com.wboly.rpc.Client.UserRPCClient;
import com.wboly.rpc.entity.AddressEntity;
import com.wboly.rpc.entity.CollectionEntity;
import com.wboly.rpc.entity.FeedbackEntity;
import com.wboly.rpc.entity.UserEntity;
import com.wboly.system.sys.annotation.ArgsLog;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.FileUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.QrcodeUtil;
import com.wboly.system.sys.util.WriterJsonUtil;

@Controller
public class UserController extends SysController {

	@Autowired
	private VblUserService vbluserService;

	@Autowired
	private VblOrderService vblOrderService;

	/**
	 * 获取最新的APP版本信息
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/wbolyapp/getNewApp" })
	public void getNewApp(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String id = request.getParameter("id");
		id = id == null ? "1" : id;
		Map<String, String> map = vbluserService.selectNewApp(id);
		if (map != null) {
			writerJson(response, map);
		} else {
			this.render(response, "{}");
		}
	}

	/**
	 * 新的 app 下载地址
	 * 
	 * @param request
	 * @param response
	 */
	//@RequestMapping(value = { "/app/wbolyapp/newDown" })
	public ModelAndView DownHtml(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		id = (id == null || !id.equals("2")) ? "wboly.apk" : "courier.apk";
		return new ModelAndView("/htm/appDown").addObject("app", id);
	}

	/**
	 * app下载(包含 商超App与快递员APP)
	 * 
	 * @param vblappEntity
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/wbolyapp/down" })
	public void Down(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		String id = request.getParameter("id");
		id = id == null ? "1" : id;
		try {
			String userid = request.getParameter("userid");
			if (userid == null) {
				userid = "1";
			}
			Map<String, String> map = vbluserService.selectNewApp(id);
			String fileName = "wboly.apk";
			if (id.equals("2")) {
				fileName = "courier.apk";
			}
			if (map != null) {
				FileUtil.download(response, map.get("PATH"), fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 商超app下载
	 * 
	 * @param vblappEntity
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/wbolyapp/wboly.apk" })
	public void WbolyDown(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			Map<String, String> map = vbluserService.selectNewApp("1");
			if (map != null) {
				FileUtil.download(response, map.get("PATH"), "wboly.apk");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 快递员app下载
	 * 
	 * @param vblappEntity
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/wbolyapp/courier.apk" })
	public void CourierDown(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		try {
			Map<String, String> map = vbluserService.selectNewApp("2");
			if (map != null) {
				FileUtil.download(response, map.get("PATH"), "courier.apk");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取我的推广二维码
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	//@RequestMapping(value = { "/app/wbolyapp/myQRCode" })
	public void myQRCode(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

		String userId = request.getParameter("userid");
		Boolean bool = true;
		String returnpath = SysCache.getUserQRCodePath(userId);
		if (returnpath != null) {
			if (new File(returnpath).exists()) {
				bool = false;
				this.render(response, returnpath);
			}
		}
		if (bool) {
			returnpath = userId + ".jpg";
			String dowmurl = "http://m.vboly.com/Member/Register?rtype=1&uid=" + userId;// 最新注册推广
			String imgPath = SysContext.QRCODEPATH + returnpath;
			String userQRCodePath = QrcodeUtil.createQrcode(dowmurl, imgPath, "jpg");
			if (userQRCodePath != null && !"".equals(userQRCodePath)) {
				SysCache.setUserQRCodePath(userId, returnpath);
				this.render(response, returnpath);
			} else {
				this.render(response, "");
			}
		}
	}

	/**
	 * 用户收货地址管理
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/user/UserAddress")
	public void UserAddress(UserEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userId")) {
			List<Map<String, String>> list = vbluserService.selectUserAddress(entity);
			if (list.size() > 0) {
				writerJson(response, list);
			} else {
				writerJson(response, "");
			}
		}
	}

	/**
	 * 用户收货地址添加
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/AddAddress")
	public void AddAddress(AddressEntity entity, HttpServletResponse response) throws TException {
		if (entity.getUserId() != null) {
			if (SysCache.getUserJson(entity.getUserId()) != null) {

				UserRPCClient userService = new UserRPCClient();
				int i = userService.client.AddAddress(entity);
				userService.close();

				this.render(response, i);
			} else {
				this.render(response, -1);
			}
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 用户收货地址删除
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/DelAddress")
	public void DelAddress(AddressEntity entity, HttpServletResponse response) throws TException {
		if (entity.getUserId() != null) {
			if (SysCache.getUserJson(entity.getUserId()) == null) {
				this.render(response, -1);
			} else {
				UserRPCClient userService = new UserRPCClient();
				int i = userService.client.DelAddress(entity);
				userService.close();
				this.render(response, i);
			}
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 用户收货地址编辑
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/EditAddress")
	public void EditAddress(AddressEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {
		if (entity.getUserId() != null) {

			if (SysCache.getUserJson(entity.getUserId()) == null) {

				this.render(response, -1);

			} else {

				List<Map<String, Object>> selectOrderStateByAddId = vblOrderService.selectOrderStateByAddId(entity);

				if (selectOrderStateByAddId != null && selectOrderStateByAddId.size() > 0) {
					this.render(response, -2);
					return;
				}

				UserRPCClient userService = new UserRPCClient();

				int i = userService.client.EditAddress(entity);

				userService.close();

				this.render(response, i);
			}
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 获取用户信息
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/user/getUsers")
	public void getUsers(UserEntity entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (verifyLogin(request, response, "userId")) {
			UserEntity user = vbluserService.selectUserInfo(entity);
			this.render(response, user);
		}
	}

	/**
	 * 用户关注收藏表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/user/UserCollection")
	public void UserCollection(CollectionEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userId")) {
			List<Map<String, Object>> list = vbluserService.selectUserCollectionInfo(entity);
			writerJson(response, list);
		}
	}

	/**
	 * 用户关注收藏表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/user/whetherAttention")
	public void whetherAttention(CollectionEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userId")) {
			int re = vbluserService.selectUserWhetherAttention(entity);
			writerJson(response, re);
		}
	}

	/**
	 * 用户添加关注收藏
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/AddUserCollection")
	public void AddUserCollection(CollectionEntity entity, HttpServletResponse response) throws TException {
		if (entity.getUserId() != null && SysCache.getUserJson(entity.getUserId()) != null) {
			UserRPCClient userService = new UserRPCClient();
			int i = userService.client.AddUserCollection(entity);
			userService.close();
			this.render(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 用户删除关注收藏
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/DelUserCollection")
	public void DelUserCollection(HttpServletRequest request, HttpServletResponse response) throws TException {
		String userId = request.getParameter("userId");
		if (userId != null && SysCache.getUserJson(userId) != null) {
			CollectionEntity entity = new CollectionEntity();
			entity.setUserId(userId);
			String id = request.getParameter("Id");
			entity.setId(Integer.valueOf(id == null || id.equals("") ? "0" : id));

			UserRPCClient userService = new UserRPCClient();
			int i = userService.client.DelUserCollection(entity);
			userService.close();
			this.render(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 用户推广收益列表
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/UserPopularizeGain")
	public void UserPopularizeGain(HttpServletRequest request, HttpServletResponse response) throws TException {
		if (verifyLogin(request, response, "userId")) {
			CollectionEntity entity = new CollectionEntity();
			entity.setUserId(request.getParameter("userId"));
			entity.setStart(Integer.valueOf(request.getParameter("start")));
			entity.setLimit(Integer.valueOf(request.getParameter("limit")));
			List<Map<String, String>> list = vbluserService.UserPopularizeGain(entity);
			writerJson(response, list);
		}
	}

	/**
	 * 用户钱包接口
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/user/money")
	public void UserMoney(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (verifyLogin(request, response, "userId")) {
			String userId = request.getParameter("userId");
			String dzCookieKey = SysContext.CONFIGMAP.get("vPayKey");
			String key = MD5CodeUtil.encode(userId + dzCookieKey);// String.valueOf将整形转换成字符串
			// 商家可用余额接口
			String url = SysContext.CONFIGMAP.get("vpaySite") + "/appbackgroundfinancesum.do?uid=" + userId + "&key="
					+ key;
			String result = null;

			try {
				result = HttpUtil.postUrl(url, new HashMap<String, Object>());
				if (result != null) {
					result = result.replace(System.lineSeparator(), "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result != null) {
				result = result.replaceAll("\"", "");
			}

			UserEntity entity = new UserEntity();
			entity.setUserId(userId);

			// 总收益金额,以及剩余收益金额 单位:分
			Map<String, Object> map = vbluserService.UserAllAmount(entity);

			// 待返现总金额 单位:分
			map.put("residueBacLimit", vbluserService.selectResidueBacLimitByParm(entity).get("residueBacLimit"));

			if (result != null && !result.equals("-1")) {

				System.err.println("APP:用户编号为:" + userId + "\t 查询账户余额成功返回");

				String[] arr = result.split("#");// 返回的单位都是 '元' 根据'#'切割

				if (arr != null && arr.length > 1) {
					BigDecimal one = new BigDecimal(arr[0].replaceAll("\"", ""));
					BigDecimal two = new BigDecimal(arr[1].replaceAll("\"", ""));
					one = one.multiply(new BigDecimal(100));
					two = two.multiply(new BigDecimal(100));
					System.out.println(one.intValue() + ":" + two.intValue());
					map.put("availableBalance", one.intValue());// 账户余额, 单位:分
					map.put("sumretailBacLimit", two.intValue());// 可用返现金额，单位:分
					// 返现总金额单位:分
					map.put("usableBacLimit", Integer.parseInt(
							vbluserService.selectResidueBacLimitByParm(entity).get("residueBacLimit").toString())
							+ two.intValue());
				}
			} else {
				System.err.println("APP:用户编号为:" + userId + "\t 查询账户余额接口返回 -1");

				map.put("availableBalance", 0);// 账户余额, 单位:分
				map.put("sumretailBacLimit", 0);// 可用返现金额，单位:分
				// 返现总金额单位:分
				map.put("usableBacLimit", 0);
			}
			writerJson(response, map);
		}
	}

	/**
	 * 使用java正则表达式去掉多余的.与0
	 * 
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s) {
		if (s.indexOf(".") > 0) {
			s = s.replaceAll("0+?$", "");// 去掉多余的0
			s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 签到
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/signIn")
	@ArgsLog
	public void SignIn(HttpServletRequest request, HttpServletResponse response) throws TException {
		String userId = request.getParameter("userId");
		if (userId != null) {
			if (SysCache.getUserJson(userId) != null) {
				this.render(response, "{\"result\":\"" + false + "\"}");// 返回值为1是成功，其他为失败
			} else {
				this.render(response, "{\"result\":\"-102\"}");// 获取不到用户信息
			}
		} else {
			this.render(response, "{\"result\":\"0\"}");
		}
	}

	/**
	 * 用户是否签到
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/isSignIn")
	@ArgsLog
	public void isSignIn(HttpServletRequest request, HttpServletResponse response) throws TException {
		if (verifyLogin(request, response, "userId")) {
			this.render(response, "{\"result\":\"0\"}");// 返回值为1是成功，其他为失败
			return;
		}
	}

	/**
	 * 消息接口
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/pushMessage")
	@ArgsLog
	public void pushMessage(HttpServletRequest request, HttpServletResponse response) throws TException {
		Map<String, String> hmap = new HashMap<String, String>();

		if (verifyLogin(request, response, "userid")) {
			String userid = request.getParameter("userid");
			String pushType = request.getParameter("type");
			String start = request.getParameter("start");
			String limit = request.getParameter("limit");

			hmap.put("userid", userid);
			hmap.put("pushType", pushType);
			hmap.put("start", start);
			hmap.put("end", "" + (Integer.valueOf(start) + Integer.valueOf(limit)));

			UserRPCClient userService = new UserRPCClient();
			List<Map<String, String>> list = userService.client.pushMessage(hmap);
			userService.close();

			WriterJsonUtil.writerJson(response, list);
		}
	}

	/**
	 * 消息接口数量
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/user/pushMessageCount")
	@ArgsLog
	public void pushMessageCount(HttpServletRequest request, HttpServletResponse response) throws TException {
		Map<String, String> hmap = new HashMap<String, String>();
		if (verifyLogin(request, response, "userid")) {
			String userid = request.getParameter("userid");
			hmap.put("userid", userid);
			UserRPCClient userService = new UserRPCClient();
			List<Map<String, String>> list = userService.client.pushMessageCount(hmap);
			userService.close();
			WriterJsonUtil.writerJson(response, list);
		}
	}

	/**
	 * @throws Exception
	 * @Name: 查询用户操作的数据(待付款,待收货,待返款,售后(售后中,退货中))
	 * @Author: nick
	 */
	//@RequestMapping("/app/user/getUserOperationInfo")
	@ArgsLog
	public void UserOperationInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (verifyLogin(request, response, "userId")) {
			String userid = request.getParameter("userId");
			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("buyerUid", userid);
			Map<String, Object> map = vbluserService.selectUserOperationInfo(hmap);
			WriterJsonUtil.writerJson(response, map);
		}
	}

	/**
	 * @throws Exception
	 * @Name: 用户推广关系信息列表
	 * @Author: nick
	 */
	//@RequestMapping("/app/user/getUserExtendsInfo")
	@ArgsLog
	public void UserMarketingInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (verifyLogin(request, response, "userId")) {
			String userid = request.getParameter("userId");
			String limit = request.getParameter("limit");
			String start = request.getParameter("start");

			if (start != null && start != null && !start.equals("") && !limit.equals("")) {

				Map<String, Object> hmap = new HashMap<String, Object>();
				hmap.put("userId", userid);
				hmap.put("start", start);
				hmap.put("limit", limit);

				List<Map<String, Object>> map = vbluserService.selectUserExtendsInfo(hmap);
				WriterJsonUtil.writerJson(response, map);
			} else {
				WriterJsonUtil.writerJson(response, "-1");
			}
		}
	}

	/**
	 * @throws TException
	 * @Name: addUserFeedback
	 * @Author: 添加用户反馈信息
	 */
	//@RequestMapping("/app/user/setUserFeedbackInfo")
	@ArgsLog
	public void addUserFeedback(HttpServletRequest request, HttpServletResponse response) throws TException {
		if (verifyLogin(request, response, "userId")) {
			String userid = request.getParameter("userId");
			String shopId = request.getParameter("shopId");
			String message = request.getParameter("message");
			String imgPath = request.getParameter("imgPath");
			String mobile = request.getParameter("mobile");
			String type = request.getParameter("type");

			if (shopId == null || userid == null || shopId.equals("")) {
				WriterJsonUtil.writerJson(response, "{\"result\":\"您输入的参数不正确 !\",\"flag\":false}");
				return;
			}

			FeedbackEntity entity = new FeedbackEntity();
			entity.setShopId(Integer.valueOf(shopId));
			entity.setMessage((message == null ? "" : message));
			entity.setImgPath((imgPath == null ? "" : imgPath));
			entity.setUserId(userid);
			entity.setUsercontact((mobile == null ? "" : mobile));
			entity.setType((type == null ? "" : type));

			UserRPCClient urc = new UserRPCClient();
			int addFeedback = urc.client.addFeedback(entity);
			urc.close();

			WriterJsonUtil.writerJson(response, addFeedback);
		}
	}
}
