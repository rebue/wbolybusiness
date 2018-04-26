package com.wboly.modules.controller.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.gson.Gson;
import com.wboly.modules.service.goods.VblGoodsService;
import com.wboly.modules.service.order.VblOrderService;
import com.wboly.rpc.Client.AppGoodsRPCClient;
import com.wboly.rpc.Client.OrderRPCClient;
import com.wboly.rpc.entity.AppraiseEntity;
import com.wboly.rpc.entity.CarShopEntity;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.annotation.ArgsLog;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.WriterJsonUtil;

@Controller
public class OrderController extends SysController {

	@Autowired
	private VblOrderService vblorderService;

	@Autowired
	private VblGoodsService vblGoodsService;

	/**
	 * @Name: 获取订单签收码
	 * @Author: knick
	 */
	//@RequestMapping(value = { "/app/order/getcode" }, params = { "orderId", "buyerUid" })
	public void getOrderCode(@RequestParam("orderId") String orderId, @RequestParam("buyerUid") String buyerUid,
			HttpServletResponse response) {
		try {
			HashMap<String, Object> hmap = new HashMap<String, Object>();

			hmap.put("orderId", orderId);
			hmap.put("buyerUid", buyerUid);
			hmap.put("code", 0);

			vblorderService.selectOrderCodeByOrderId(hmap);

			this.render(response, hmap.get("code").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Name: 保存微信预支付id
	 * @Author: knick
	 */
	//@RequestMapping(value = { "/app/order/getprepay" }, params = { "uuid", "orderId", "summoney", "cashVolume" })
	public void setAppPrepayId(HttpServletRequest request, @RequestParam("uuid") String uuid,
			@RequestParam("summoney") String summoney, @RequestParam("cashVolume") String cashVolume,
			HttpServletResponse response, @RequestParam("orderId") String orderId) {
		System.err.println("--------------  开始进入 App 存储(获取) prepay_id ------------------");
		String prepayid = request.getParameter("prepayid");
		try {
			System.err.println(
					"uuid:" + uuid + "\torderId:" + orderId + "\tsummoney:" + summoney + "\tcashVolume:" + cashVolume);

			String key = MD5CodeUtil.md5(uuid + orderId + summoney + cashVolume);

			String prepay_id = SysCache.getWxPayInfo(key);

			if ("".equals(prepay_id)) {
				if (null != prepayid && !"".equals(prepayid)) {
					System.err.println("存储 prepay_id:" + prepay_id);
					SysCache.setWxPayInfo(key, prepayid);
					this.render(response, "{\"message\":\"" + prepayid + "\",\"flag\":false}");
				} else {
					this.render(response, "{\"message\":\"\",\"flag\":false}");
				}
			} else {
				System.err.println("得到 prepay_id 返回:" + prepay_id);
				this.render(response, "{\"message\":\"" + prepay_id + "\",\"flag\":true}");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("出现异常");
			this.render(response, "{\"message\":\"\",\"flag\":false}");
		}
	}

	/**
	 * @Name: 验证限购商品(查询用户还能购买限购的商品数)
	 * @Author: nick
	 */
	public Integer verifyRestriction(Object buyerUid, Object activityId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Integer can = -512;
		map.put("activityId", activityId);
		// : 查询这个活动是否有规则限制
		List<Map<String, Object>> allRule = vblorderService.selectActivityRuleByParm(map);
		if (allRule != null && allRule.size() > 0) {
			for (Map<String, Object> map2 : allRule) {
				String[] split = map2.get("afterNum").toString().split("_");
				can = Integer.parseInt(split[1]);
			}
		}
		return can;
	}

	/**
	 * 用户购物车列表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/UserCarShops")
	@ArgsLog
	public void UserCarShops(CarShopEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "buyerUid")) {
			List<Map<String, Object>> list = vblorderService.UserCarShops(entity);
			if (list.size() > 0) {
				WriterJsonUtil.writerJson(response, list);
			} else {
				WriterJsonUtil.writerJson(response, "");
			}
		}
	}

	/**
	 * 用户删除购物车商品
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/DelUserCarShop")
	@ArgsLog
	public void DelUserCarShop(CarShopEntity entity, HttpServletResponse response) throws TException {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.DelUserCarShop(entity);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, "-2");
		}
	}

	/**
	 * @Name: 根据订单号修改该订单的所有者信息
	 * @Author: knick
	 */
	@RequestMapping(value = { "/app/order/upOrderHolders" }, params = { "buyerUid", "orderId",
			"sign" }, method = RequestMethod.POST)
	@ArgsLog
	public void upOrderHolders(HttpServletRequest request, @RequestParam("buyerUid") String buyerUid,
			@RequestParam("orderId") String orderId, @RequestParam("sign") String sign, HttpServletResponse response)
			throws TException {
		try {
			String userId = SysCache.getWeChatUserByColumn(request, "userId");
			if (userId == null || "".equals(userId) || "null".equals(userId)) {
				if (!verifyLogin(request, response, "buyerUid")) {
					System.err.println("upOrderHolders:用户未登录");
					this.render(response, -3);
					return;
				}
			} else {
				buyerUid = userId;
			}

			if (null == buyerUid || "".equals(buyerUid) || null == orderId || "".equals(orderId) || null == sign
					|| "".equals(sign)) {
				System.err.println("upOrderHolders:参数有误");
				this.render(response, -1);
				return;
			}

			try {
				Long.parseLong(orderId);
				Long.parseLong(buyerUid);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("upOrderHolders:参数有误");
				this.render(response, -1);
				return;
			}

			if (sign.equals(buyerUid)) {
				sign = MD5CodeUtil.md5(buyerUid + orderId);
			}

			if (!MD5CodeUtil.md5(buyerUid + orderId).equals(sign)) {
				System.err.println("upOrderHolders:签名参数有误");
				this.render(response, -1);
				return;
			}

			Integer state = vblorderService.selectOrderByOrderId(orderId);

			if (null == state || state != 0) {
				System.err.println("upOrderHolders:该订单号有误");
				this.render(response, -1);
				return;
			}

			OrderRPCClient orderService = new OrderRPCClient();
			int upOrderHolders = orderService.client.upOrderHolders(orderId, buyerUid);
			orderService.close();

			this.render(response, upOrderHolders);
			System.out.println(upOrderHolders);
		} catch (Exception e) {
			e.printStackTrace();
			this.render(response, -2);
		}
	}

	/**
	 * 用户添加购物车商品
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/EditUserCarShop")
	@ArgsLog
	public void EditUserCarShop(HttpServletRequest request, CarShopEntity entity, HttpServletResponse response)
			throws Exception {

		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {

			Map<String, Object> hmap = new HashMap<String, Object>();
			hmap.put("shopId", entity.getShopId());
			hmap.put("goodsId", entity.getGoodsId());
			hmap.put("supplierUid", entity.getSupplierUid());
			hmap.put("skuId", entity.getSkuId());
			hmap.put("buyerUid", entity.getBuyerUid());

			// 商品库存数量
			Long goodsStock = vblGoodsService.GoodsStockNum(hmap);

			if (null == goodsStock || goodsStock < 1) {
				System.err.println(entity.getGoodsId() + ":商品库存不足");
				WriterJsonUtil.writerJson(response, -2);
				return;
			}

			// 获取用户的购物车信息
			List<Map<String, Object>> selectCartByParm = vblorderService.selectCartByParm(hmap);

			int haveNum = 0;// 用户已加入购物车数量

			if (selectCartByParm != null && selectCartByParm.size() > 0) {
				// 获取用户已经加入购物车的数量
				haveNum = Integer.parseInt(selectCartByParm.get(0).get("num").toString());
			}

			System.err.println(JsonUtil.ObjectToJson(entity));

			// 根据多个参数查询已上线活动信息
			List<Map<String, Object>> selectActivityByParm = vblGoodsService.selectActivityByParm(hmap);

			if (selectActivityByParm != null && selectActivityByParm.size() > 0) {

				// 获取限购商品可以购买的总数量
				Integer canNum = verifyRestriction(entity.getBuyerUid(), selectActivityByParm.get(0).get("activityId"));

				if (canNum != -512) {// 等于-512 说明该活动没有限购规则
					// 数量总数
					haveNum = haveNum + entity.getNum();// 已加入购物车数量 加上 增加的数量
					// 如果数量总数 大于等于 限购总数
					if (haveNum >= canNum) {
						// 你们是不是很疑惑,我就不写注释
						int one = haveNum - canNum;
						int two = entity.getNum() - one;
						if (two == 0) {
							two = 0;
						}
						entity.setNum(two);
					}
				}
			}

			// 商品库存 < 加入数量
			try {
				if (goodsStock < entity.getNum()) {
					System.err.println(entity.getGoodsId() + ":商品库存不足");
					WriterJsonUtil.writerJson(response, -2);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				WriterJsonUtil.writerJson(response, -2);
				return;
			}

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.EditUserCarShop(entity);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
			return;

		} else {
			WriterJsonUtil.writerJson(response, "-4");
		}
	}

	/**
	 * 用户购物车商品数
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/UserCarShopCount")
	@ArgsLog
	public void UserCarShopCount(CarShopEntity entity, HttpServletResponse response) throws Exception {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			Integer userCarShopCount = vblorderService.UserCarShopCount(entity);
			WriterJsonUtil.writerJson(response, userCarShopCount);
		} else {
			WriterJsonUtil.writerJson(response, "-1");
		}
	}

	/**
	 * 用户订单列表
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/UserOrders")
	@ArgsLog
	public void UserOrders(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (verifyLogin(request, response, "buyerUid")) {
			List<Map<String, Object>> list = vblorderService.UserOrders(entity);

			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < list.size(); i++) {

				Map<String, Object> hm = new HashMap<String, Object>();
				Map<String, Object> map = list.get(i);
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					hm.put(entry.getKey(), entry.getValue());
				}
				String orderId = String.valueOf(map.get("orderId"));
				OrderEntity o = new OrderEntity();
				o.setOrderIds(orderId);
				o.setAftersaleState(entity.getAftersaleState());
				o.setReturnState(entity.getReturnState());
				List<Map<String, Object>> items = vblorderService.UserOrdersInventory(o);
				hm.put("items", items);
				result.add(i, hm);
			}
			WriterJsonUtil.writerJson(response, result);
		}
	}

	/**
	 * 用户根据购物车生成订单
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/CreateShopOrder")
	@ArgsLog
	public void CreateShopOrder(OrderEntity entity, HttpServletResponse response) throws TException {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {

			Map<String, Object> map = new HashMap<String, Object>();

			List<Integer> list = new ArrayList<Integer>();

			String[] idArr = entity.getIds().split(",");

			for (int i = 0; i < idArr.length; i++) {
				list.add(Integer.parseInt(idArr[i]));
			}

			// 购物车编号,多个逗号分开
			map.put("cartIds", list);

			List<Map<String, Object>> allUserCart = vblorderService.selectCartByParm(map);

			System.err.println("APP用户购物车信息:" + allUserCart);

			boolean flag = true;// 判断消费者的这些商品是否符合下单条件

			one: if (allUserCart != null && allUserCart.size() > 0) {
				for (Map<String, Object> map2 : allUserCart) {
					// 判断查询到的购物车买家和要购买的买家是否一致
					if (map2.get("buyerUid").toString().equals(String.valueOf(entity.getBuyerUid()))) {

						map.put("activityId", map2.get("activityId"));
						map.put("buyerUid", entity.getBuyerUid());
						map.put("afterNum", 0);// 规则规定的限购数量

						// 查询这个活动是否有规则限制
						List<Map<String, Object>> haveMap = vblorderService.selectActivityRuleByParm(map);

						if (haveMap != null && haveMap.size() > 0) {
							System.err.println("有规则限制");
							for (Map<String, Object> map3 : haveMap) {
								// 截取规则类型,因为返回的数据是以 规则类型_数量
								String[] split = map3.get("afterNum").toString().split("_");
								map.put("ruleType", split[0]);// 规则类型
								map.put("haveNum", 0);// 用户已购买的限购数量

								// 查询这个消费者是否购买过该商品
								map = vblorderService.selectActivityRuleLogByParm(map);
								if (map != null) {

									// 走到这一步说明该商品活动有了这个限购规则
									Integer canShoppingNum = Integer.parseInt(split[1])
											- Integer.parseInt(map.get("haveNum").toString());

									// 用户要购买的商品个数
									Integer userCanNum = Integer.valueOf(map2.get("num").toString());

									// 如果小于1 说明该商品已经不能购买了
									if (canShoppingNum < 1 || userCanNum > canShoppingNum) {
										System.err.println("该商品不能购买了....");
										flag = false;
										break one;
									}
								}
							}
						}
					}
				}
			}
			if (flag) {
				Gson gson = new Gson();
				System.err.println(gson.toJson(entity) + ":_________生成订单参数");
				OrderRPCClient orderService = new OrderRPCClient();
				long i = orderService.client.CreateShopOrder(entity);
				System.err.println("生成订单返回:" + i);
				orderService.close();
				WriterJsonUtil.writerJson(response, i);
			} else {
				System.out.println("本人的限制");
				WriterJsonUtil.writerJson(response, -6);
			}
		} else {
			WriterJsonUtil.writerJson(response, -7);
		}
	}

	/**
	 * 用户放弃订单
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/CancelShopOrder")
	@ArgsLog
	public void CancelShopOrder(OrderEntity entity, HttpServletResponse response) throws TException {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.CancelShopOrder(entity);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 用户退货
	 * 
	 * @param entity
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/ReturnedPurchase")
	@ArgsLog
	public void ReturnedPurchase(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			String reason = request.getParameter("reason");
			String refundAmount = request.getParameter("refundAmount");
			String img = request.getParameter("img");
			if (reason == null || "".equals(reason) || refundAmount == null || "".equals(refundAmount)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("reason", reason);
			map.put("refundAmount", refundAmount);
			map.put("img", img);
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ReturnedPurchase(entity, map);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 详情页分类行数
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/AppraiseclassifyCount")
	@ArgsLog
	public void goodsSpecification(AppraiseEntity entity, HttpServletResponse response) throws TException {
		Map<String, String> map = vblorderService.AppraiseclassifyCount(entity);
		WriterJsonUtil.writerJson(response, map);
	}

	/**
	 * 详情页评价数据
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/getAppraisePage")
	@ArgsLog
	public void getAppraisePage(AppraiseEntity entity, HttpServletResponse response) throws Exception {
		List<AppraiseEntity> list = vblorderService.goodsAppraise(entity);
		if (list.size() == 0) {
			WriterJsonUtil.writerJson(response, "");
		} else {
			WriterJsonUtil.writerJson(response, list);
		}
	}

	/**
	 * 用户申请售后
	 * 
	 * @param entity
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/ApplyAftermarket")
	@ArgsLog
	public void ApplyAftermarket(OrderEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			String activityId = request.getParameter("activityId");
			String appealType = request.getParameter("appealType");
			String buyerContact = request.getParameter("buyerContact");
			String img = request.getParameter("img");
			if (activityId == null || "".equals(activityId) || appealType == null || "".equals(appealType)
					|| entity.getOrderIds().length() == 0) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("activityId", activityId);
			map.put("appealType", appealType);
			map.put("buyerContact", buyerContact);
			map.put("img", img);
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ApplyAftermarket(entity, map);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 评价商品
	 * 
	 * @param entity
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/AppraiseOrder")
	@ArgsLog
	public void AppraiseOrder(AppraiseEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws TException {

		String activityId = request.getParameter("activityId");
		String babyEvaluation = request.getParameter("babyEvaluation");
		String orderId = request.getParameter("orderId");
		String userName = request.getParameter("userName");
		if (entity.getBuyerUid() != null && SysCache.getUserJson(entity.getBuyerUid()) != null) {
			if (activityId == null || "".equals(activityId) || babyEvaluation == null || "".equals(babyEvaluation)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("activityId", activityId);
			map.put("babyEvaluation", babyEvaluation);
			map.put("userId", entity.getBuyerUid());
			map.put("orderId", orderId);
			map.put("userName", userName);
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.AppraiseOrder(entity, map);
			orderService.close();
			WriterJsonUtil.writerJson(response, i);
		} else {
			WriterJsonUtil.writerJson(response, -6);
		}
	}

	/**
	 * 浏览足迹和看了又看
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/order/UserFootprints")
	@ArgsLog
	public void UserFootprints(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, String> hmap = new HashMap<String, String>();

		if (verifyLogin(request, response, "userId")) {

			String shopId = request.getParameter("shopId");
			String userId = request.getParameter("userId");
			String type = request.getParameter("type");
			String limit = request.getParameter("limit");
			String start = request.getParameter("start");
			hmap.put("shopId", shopId);
			hmap.put("userId", userId);
			hmap.put("type", type);
			hmap.put("limit", limit);
			hmap.put("start", start);

			List<Map<String, Object>> map = vblorderService.UserFootprints(hmap);
			WriterJsonUtil.writerJson(response, map);
		}
	}

	/**
	 * 添加浏览的足迹
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/AddFootprint")
	@ArgsLog
	public void AddFootprint(HttpServletRequest request, HttpServletResponse response) throws TException {

		String userId = request.getParameter("userId");

		if (userId != null && SysCache.getUserJson(userId) != null) {

			String shopId = request.getParameter("shopId");
			String goodsId = request.getParameter("goodsId");
			String supplierUid = request.getParameter("supplierUid");
			String goodsTitle = request.getParameter("goodsTitle");
			String faceImg = request.getParameter("faceImg");
			String retailPrice = request.getParameter("retailPrice");
			String retailBacLimit = request.getParameter("retailBacLimit");
			String marketPrice = request.getParameter("marketPrice");

			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("shopId", shopId);
			hmap.put("userId", userId);
			hmap.put("goodsId", goodsId);
			hmap.put("supplierUid", supplierUid);
			hmap.put("goodsTitle", goodsTitle);
			hmap.put("faceImg", faceImg);
			hmap.put("retailPrice", retailPrice);
			hmap.put("retailBacLimit", retailBacLimit);
			hmap.put("marketPrice", marketPrice);
			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.AddFootprint(hmap);
			orderService.close();
			if (i == 1) {
				AppGoodsRPCClient goodsService = new AppGoodsRPCClient();
				i = goodsService.client.AddGoodsHits(hmap);
				goodsService.close();
			} else {
				i = 0;
			}
			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 删除浏览的足迹
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/DelFootprint")
	@ArgsLog
	public void DelFootprint(HttpServletRequest request, HttpServletResponse response) throws TException {
		String uid = request.getParameter("uid");
		if (uid != null && SysCache.getUserJson(uid) != null) {

			String ids = request.getParameter("ids");

			if (ids == null || "".equals(ids)) {
				WriterJsonUtil.writerJson(response, 0);
			}

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.DelFootprint(ids);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 完成售后申诉
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/finishAfterSale")
	@ArgsLog
	public void FinishAfterSale(HttpServletRequest request, HttpServletResponse response) throws TException {
		String uid = request.getParameter("userid");

		if (uid != null && SysCache.getUserJson(uid) != null) {

			String orderId = request.getParameter("id");
			String username = request.getParameter("userName");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}

			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("id", orderId);
			hmap.put("userid", uid);
			hmap.put("userName", username);

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.FinishAfterSale(hmap);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 继续售后申诉
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/continueAfterSale")
	@ArgsLog
	public void ContinueAfterSale(HttpServletRequest request, HttpServletResponse response) throws TException {
		String uid = request.getParameter("userid");

		if (uid != null && SysCache.getUserJson(uid) != null) {

			String name = request.getParameter("userName");
			String orderId = request.getParameter("id");
			String aftersale = request.getParameter("aftersale");
			String img = request.getParameter("img");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}

			Map<String, String> hmap = new HashMap<String, String>();
			hmap.put("id", orderId);
			hmap.put("aftersale", aftersale);
			hmap.put("img", img);
			hmap.put("userid", uid);
			hmap.put("userName", name);

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ContinueAfterSale(hmap);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 买家全部退货
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/ReturnAllGoods")
	@ArgsLog
	public void ReturnAllGoods(HttpServletRequest request, HttpServletResponse response) throws TException {
		String uid = request.getParameter("userid");
		if (uid != null && SysCache.getUserJson(uid) != null) {

			String orderId = request.getParameter("orderId");
			String reason = request.getParameter("reason");
			String imgs = request.getParameter("imgs");
			String username = request.getParameter("userName");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("orderId", orderId);
			hmap.put("reason", reason);
			if (imgs == null || imgs.equals("null")) {
				imgs = "";
			}
			hmap.put("img", imgs);
			hmap.put("userid", uid);
			hmap.put("userName", username);

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ReturnAllGoods(hmap);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 买家部分退货
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/ReturnPartofGoods")
	@ArgsLog
	public void ReturnPartofGoods(HttpServletRequest request, HttpServletResponse response) throws TException {
		String uid = request.getParameter("userid");
		if (uid != null && !uid.equals("") && SysCache.getUserJson(uid) != null) {

			String orderId = request.getParameter("orderId");
			String activityIds = request.getParameter("activityIds");
			String nums = request.getParameter("nums");
			String reason = request.getParameter("reason");
			String img = request.getParameter("img");
			String username = request.getParameter("userName");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}

			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("orderId", orderId);
			hmap.put("activityIds", activityIds);
			hmap.put("nums", nums);
			hmap.put("reason", reason);
			hmap.put("img", img);
			hmap.put("userid", uid);
			hmap.put("userName", username);

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ReturnPartofGoods(hmap);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}

	/**
	 * 买家申请返款
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/order/ApplyRefund")
	@ArgsLog
	public void ApplyRefund(HttpServletRequest request, HttpServletResponse response) throws TException {

		String uid = request.getParameter("userid");

		if (uid != null && SysCache.getUserJson(uid) != null) {

			String orderId = request.getParameter("orderId");
			String goodsId = request.getParameter("goodsId");
			String skuId = request.getParameter("skuId");
			String supplierUid = request.getParameter("supplierUid");
			String username = request.getParameter("userName");

			if (orderId == null || "".equals(orderId)) {
				WriterJsonUtil.writerJson(response, 0);
			}
			Map<String, String> hmap = new HashMap<String, String>();

			hmap.put("orderId", orderId);
			hmap.put("goodsId", goodsId);
			hmap.put("skuId", skuId);
			hmap.put("supplierUid", supplierUid);
			hmap.put("userid", uid);
			hmap.put("userName", username);

			OrderRPCClient orderService = new OrderRPCClient();
			int i = orderService.client.ApplyRefund(hmap);
			orderService.close();

			WriterJsonUtil.writerJson(response, i);
		} else {
			this.render(response, -1);
		}
	}
}
