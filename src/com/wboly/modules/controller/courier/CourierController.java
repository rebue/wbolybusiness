package com.wboly.modules.controller.courier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wboly.modules.service.courier.VblCourierService;
import com.wboly.rpc.Client.CourierRPCClient;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.annotation.ArgsLog;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.WriterJsonUtil;

@Controller
public class CourierController extends SysController {

	@Autowired
	private VblCourierService vblcourierService;

	/**
	 * 快递员订单列表接口
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/courier/CourierOrders")
	@ArgsLog
	public void CourierOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userid = request.getParameter("userid");

		if (verifyLogin(request, response, "userid")) {

			String state = request.getParameter("state");

			if (state == null || state.equals("")) {
				WriterJsonUtil.writerJson(response, "{\"result\":\"您填写的参数有误!\"}");
				return;
			}

			if (!state.equals("1") && !state.equals("2") && !state.equals("3")) {
				WriterJsonUtil.writerJson(response, "\"uid\":\"0\"");
				return;
			}

			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("userid", userid);
			hmap.put("state", state);

			List<Map<String, Object>> map = vblcourierService.CourierOrders(hmap);

			WriterJsonUtil.writerJson(response, map);
		}
	}

	/**
	 * 快递开始配送
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/delivery")
	@ArgsLog
	public void Delivery(HttpServletRequest request, HttpServletResponse response) throws TException {

		String uid = request.getParameter("userid");
		if (uid !=null && SysCache.getUserJson(uid) != null) {
			String orderId = request.getParameter("orderId");
			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("orderId", orderId);
			hmap.put("userid", uid);
			CourierRPCClient courierService = new CourierRPCClient();
			int i = courierService.client.Delivery(hmap);
			courierService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 当前用户是否快递员
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/isCourier")
	@ArgsLog
	public void isCourier(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userid = request.getParameter("userid");
		if (userid != null && SysCache.getUserJson(userid) != null) {
			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("userid", userid);
			int i = vblcourierService.isCourier(hmap);
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 快递信息接口
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/courier/OrderCourierSite")
	@ArgsLog
	public void OrderCourierSite(HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (verifyLogin(request, response, "buyerUid")) {

			String orderId = request.getParameter("orderId");
			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("orderId", orderId);
			List<Map<String, Object>> map = vblcourierService.OrderCourierSite(hmap);
			WriterJsonUtil.writerJson(response, map);
		}
	}

	/**
	 * 确认收货
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/OrderSignin")
	@ArgsLog
	public void OrderSignin(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userid = request.getParameter("userid");

		if (userid != null && SysCache.getUserJson(userid) != null) {

			String code = request.getParameter("code");
			String orderId = request.getParameter("orderId");
			String userName = request.getParameter("userName");

			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("userid", userid);
			hmap.put("orderId", orderId);
			hmap.put("code", code);
			hmap.put("userName", userName);
			CourierRPCClient courierService = new CourierRPCClient();
			int i = courierService.client.OrderSignin(hmap);
			courierService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 快递信息接口
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/Couriers")
	@ArgsLog
	public void Couriers(HttpServletRequest request, HttpServletResponse response) throws TException {
		if (verifyLogin(request, response, "userid")) {
			Map<String, Object> hmap = new HashMap<String, Object>();
			hmap.put("userid", request.getParameter("userid"));
			Map<String, Object> couriers = vblcourierService.Couriers(hmap);
			WriterJsonUtil.writerJson(response, couriers);
		}
	}

	/**
	 * 添加快递信息接口
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/addOrderCourierSite")
	@ArgsLog
	public void addOrderCourierSite(HttpServletRequest request, HttpServletResponse response) throws TException {

		String courierId = request.getParameter("courierId");

		if (courierId != null && SysCache.getUserJson(courierId) != null) {

			String orderId = request.getParameter("orderId");
			String longitude = request.getParameter("longitude");
			String latitude = request.getParameter("latitude");

			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("courierId", courierId);
			hmap.put("orderId", orderId);
			hmap.put("longitude", longitude);
			hmap.put("latitude", latitude);
			CourierRPCClient courierService = new CourierRPCClient();
			int i = courierService.client.addOrderCourierSite(hmap);
			courierService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 买家拒签
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/courier/RefusedSignByCouriers")
	@ArgsLog
	public void RefusedSignByCouriers(HttpServletRequest request, HttpServletResponse response) throws TException {

		String uid = request.getParameter("userid");
		if (uid != null && SysCache.getUserJson(uid) != null) {

			String orderId = request.getParameter("orderId");
			String username = request.getParameter("userName");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("orderId", orderId);
			hmap.put("userid", uid);
			hmap.put("userName", username);

			CourierRPCClient courierService = new CourierRPCClient();
			int i = courierService.client.RefusedSignByCouriers(hmap);
			courierService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 门店订单列表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/shop/ShopOrders")
	@ArgsLog
	public void ShopOrders(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userid")) {
			List<Map<String, Object>> shopOrders = vblcourierService.ShopOrders(entity);
			WriterJsonUtil.writerJson(response, shopOrders);
		}
	}

	/**
	 * 门店订单清单列表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/shop/ShopOrdersInventory")
	@ArgsLog
	public void ShopOrdersInventory(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userid")) {
			List<Map<String, Object>> shopOrdersInventory = vblcourierService.ShopOrdersInventory(entity);
			WriterJsonUtil.writerJson(response, shopOrdersInventory);
		}
	}

	/**
	 * 门店快递员列表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/shop/ShopCouriers")
	@ArgsLog
	public void ShopCouriers(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "userid")) {
			List<Map<String, Object>> shopCouriers = vblcourierService.ShopCouriers(entity);
			WriterJsonUtil.writerJson(response, shopCouriers);
		}
	}

	/**
	 * 门店给订单分配快递员
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/shop/UpShopOrderCouriers")
	@ArgsLog
	public void UpShopOrderCouriers(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {
		if (verifyLogin(request, response, "userid")) {
			CourierRPCClient courierService = new CourierRPCClient();
			int i = courierService.client.UpShopOrderCouriers(entity);
			courierService.close();
			WriterJsonUtil.writerJson(response, i);
		}
	}

	/**
	 * 门店信息
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/app/shop/shops")
	@ArgsLog
	public void Shops(OrderEntity entity, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (verifyLogin(request, response, "userid")) {
			List<Map<String, Object>> shops = vblcourierService.Shops(entity);
			WriterJsonUtil.writerJson(response, shops);
		}
	}

	/**
	 * 门店可配送地址
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	@RequestMapping("/app/shop/canbedeliveryaddress")
	@ArgsLog
	public void ShopsCanBeDelivery(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {

		String shopId = request.getParameter("shopId");// 门店的编号
		String areaId = request.getParameter("areaId");// 区域编号
		String userId = request.getParameter("userId");// 用户编号
		if (userId != null && SysCache.getUserJson(userId) != null) {
			if (shopId != null && !shopId.equals("")) {
				if (areaId != null && !areaId.equals("")) {
					entity.setShopId(Integer.valueOf(shopId));
					entity.setDateline(Integer.valueOf(areaId));// 区域编号
					List<Map<String, String>> list = vblcourierService.ShopCanBeDelivery(entity);
					WriterJsonUtil.writerJson(response, list);
				} else {
					WriterJsonUtil.writerJson(response, "-3");
				}
			} else {
				WriterJsonUtil.writerJson(response, "-2");
			}
		} else {
			WriterJsonUtil.writerJson(response, "-1");
		}
	}
}
