package com.wboly.wechat.controller.collect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.modules.service.order.VblOrderService;
import com.wboly.modules.service.user.VblUserService;
import com.wboly.rpc.Client.OrderRPCClient;
import com.wboly.rpc.Client.UserRPCClient;
import com.wboly.rpc.entity.CollectionEntity;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.SessionUtil;

/**
 * @Name: 收藏以及足迹的 控制层.java
 * @Author: nick
 */
@Controller
public class WeChatCollectController extends SysController {

	@Autowired
	private VblUserService vbluserService;

	@Autowired
	private VblOrderService vblorderService;

	/**
	 * @Name: 足迹页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/footprintPage")
	public ModelAndView footprintPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			mav.setViewName("/htm/wechat/collect/viewed");
		}
		return mav;
	}

	/**
	 * @Name: 收藏页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/collectPage")
	public ModelAndView collectPage(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			mav.setViewName("/htm/wechat/collect/favorites");
		}
		return mav;
	}

	/**
	 * @Name: 浏览足迹和看了又看
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/getfootprint")
	public void UserFootprints(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, String> hmap = new HashMap<String, String>();

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		Object shopByColumn = SessionUtil.getShopByColumn(request, "shopId");
		if (shopByColumn == null) {
			this.render(response, "{\"message\":\"请选择门店\",\"flag\":false}");
			return;
		}

		String type = request.getParameter("type");
		String limit = request.getParameter("limit");
		String start = request.getParameter("start");
		if (type == null || type.equals("") || limit == null || limit.equals("") || start == null || start.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}
		hmap.put("shopId", shopByColumn.toString());
		hmap.put("userId", userId);
		hmap.put("type", type);
		hmap.put("limit", limit);
		hmap.put("start", start);

		List<Map<String, Object>> map = vblorderService.UserFootprints(hmap);
		if (map.size() > 0) {
			this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(map) + ",\"flag\":true}");
		} else {
			this.render(response, "{\"message\":0,\"flag\":true}");
		}
	}

	/**
	 * @Name: 删除浏览的足迹(可以多个删除,参数ids以逗号隔开)
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/deleteFootprint")
	public void DelFootprint(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		String ids = request.getParameter("ids");

		if (ids == null || "".equals(ids)) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		OrderRPCClient orderService = new OrderRPCClient();
		int i = orderService.client.DelFootprint(ids);
		orderService.close();
		if (i > 0) {
			this.render(response, "{\"message\":\"移除成功\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"移除失败\",\"flag\":false}");
	}

	/**
	 * @Name: 删除用户收藏表
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/deleteSingle")
	public void DelUserCollection(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		String id = request.getParameter("id");
		if (id == null || id.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		CollectionEntity entity = new CollectionEntity();
		entity.setUserId(userId);
		entity.setId(Integer.valueOf(id == null || id.equals("") ? "0" : id));
		UserRPCClient userService = new UserRPCClient();
		int i = userService.client.DelUserCollection(entity);
		userService.close();
		if (i > 0) {
			this.render(response, "{\"message\":\"移除成功\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"移除失败\",\"flag\":false}");
	}

	/**
	 * @Name: 用户关注收藏表
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/listInfo")
	public void UserCollection(CollectionEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		entity.setUserId(userId);

		List<Map<String, Object>> list = vbluserService.selectUserCollectionInfo(entity);
		if (list.size() > 0) {
			this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(list) + ",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":0,\"flag\":true}");
	}

	/**
	 * @Name: 添加商品收藏
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/collect/addGoods")
	public void AddUserCollection(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");

		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}

		Object shopByColumn = null;

		// 因为订单详情是所有门店的订单,从全部订单那里进来需要传一个shopId,如果不传默认是当前门店shopId
		String shopId = request.getParameter("shopId");
		if (shopId == null || shopId.equals("")) {
			shopByColumn = SessionUtil.getShopByColumn(request, "shopId");
		} else {
			shopByColumn = shopId;
		}

		if (shopByColumn == null) {
			this.render(response, "{\"message\":\"请选择门店\",\"flag\":false}");
			return;
		}

		String goodsId = request.getParameter("goods");// 商品编号
		String skuId = request.getParameter("sku");// sku编号
		String supplierUid = request.getParameter("supplierUid");// 供应商编号
		if (goodsId == null || goodsId.equals("") || skuId == null || skuId.equals("") || supplierUid == null
				|| supplierUid.equals("")) {
			this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
			return;
		}

		CollectionEntity entity = new CollectionEntity();

		entity.setUserId(userId);
		entity.setShopId(Integer.parseInt(shopByColumn.toString()));
		entity.setGoodsId(Integer.parseInt(goodsId));
		entity.setSkuId(Integer.parseInt(skuId));
		entity.setSupplierUid(supplierUid);

		UserRPCClient userService = new UserRPCClient();

		int i = userService.client.AddUserCollection(entity);

		userService.close();

		switch (i) {
		case 0:
			this.render(response, "{\"message\":\"收藏失败\",\"flag\":false}");
			break;
		case 1:
			this.render(response, "{\"message\":\"收藏成功\",\"flag\":true}");
			break;
		case 2:
			this.render(response, "{\"message\":\"已收藏\",\"flag\":false}");
			break;
		}
	}
}
