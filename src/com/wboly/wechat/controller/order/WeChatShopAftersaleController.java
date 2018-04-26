package com.wboly.wechat.controller.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.util.Base64EnOut;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.wechat.service.order.WeChatOrderService;
import com.wboly.wechat.service.order.WeChatShopAftersaleService;
import com.wboly.wechat.service.shop.WeChatShopService;

@Controller
public class WeChatShopAftersaleController extends SysController {

	@Autowired
	private WeChatShopAftersaleService weChatShopAftersaleService;
	@Autowired
	private WeChatShopService weChatShopService;
	@Autowired
	private WeChatOrderService weChatOrderService;
	
	/**
	 * @Name: 申请售后页面跳转
	 * @Author: nick
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/wechat/order/afterSalePage")
	public ModelAndView afterSalePage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			String afterSaleData = request.getParameter("afterSaleData");
			if (afterSaleData != null) {
				Base64EnOut b64 = new Base64EnOut();
				afterSaleData = b64.Decode(afterSaleData);
				afterSaleData = b64.Decode(afterSaleData);

				mav.addObject("afterSaleData", JsonUtil.jsonStringToMap(afterSaleData));
			} else {
				mav.addObject("afterSaleData", "");
			}
			mav.setViewName("/htm/wechat/order/applyAfterSale");
		}
		return mav;
	}
	
	/**
	 * 用户提交申请售后
	 * @param request
	 * @param response
	 * 2018年1月16日16:54:03
	 */
	@RequestMapping(value = "/wechat/order/afterSaleApply")
	public void ApplyAftermarket(HttpServletRequest request, HttpServletResponse response) {
		// 获取当前登录用户编号
		String loginId = SysCache.getWeChatUserByColumn(request, "userId");
		// 判断用户是否登录
		if (loginId == null || loginId.equals("") || loginId.equals("null")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		
		String orderId = request.getParameter("orderIds"); // 订单编号
		String activityId = request.getParameter("activityId"); // 商品活动编号
		String message = request.getParameter("message"); // 申请详情
		String appealType = request.getParameter("appealType"); // 申请售后原因
		String buyerContact = request.getParameter("buyerContact"); // 用户联系方式
		String img = request.getParameter("img"); // 申请售后图片
		// 获取当前时间戳
		long currentTime = System.currentTimeMillis() / 1000;	
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderId", orderId);
		map.put("activityId", activityId);
		map.put("aftersale", message);
		map.put("appealType", appealType);
		map.put("buyerUid", loginId);
		map.put("buyerContact", buyerContact);
		map.put("dateline", currentTime);
		
		// 获取订单号、门店编号、商品编号、商品Sku编号
		List<Map<String, Object>> orderStateAndBuyerList = weChatShopAftersaleService.selectOrderStateAndBuyer(map);
		if (orderStateAndBuyerList.size() == 0) {
			this.render(response, "{\"message\":\"没有找到该订单信息\",\"flag\":false}");
			return;
		} 
		
		map.put("shopId", orderStateAndBuyerList.get(0).get("shopId"));
		
		// 根据门店查询门店信息
		List<Map<String, Object>> shopList = weChatShopService.selectAllShop(map);
		// 判断输入的门店是否为存在
		if (shopList.size() == 0) {
			this.render(response, "{\"message\":\"没有该门店信息\",\"flag\":false}");
			return ;
		}

		map.put("userid", shopList.get(0).get("userid"));// 加盟商编号
		map.put("userName", shopList.get(0).get("userName"));// 加盟商名称
		System.err.println("orderStateAndBuyerList.get(0).get(state)==" + orderStateAndBuyerList.get(0).get("state"));
		String state = String.valueOf(orderStateAndBuyerList.get(0).get("state"));
		// state：0=待支付;1=待配送;2=正在配送;3=签收待返现;4=已返现;5=取消订单;6=待退货;7=售后;8=已结束
		// 判断订单是否处于已返现状态，如果处于已返现状态则说明可以售后
		if (!state.equals("4")) {
			this.render(response, "{\"message\":\"当前状态不能售后\",\"flag\":false}");
			return;
		}
		
		// 根据商品活动编号获取门店编号、商品编号、商品Sku编号、供应商用户编号
		List<Map<String, Object>> shopGoodsSkuAndSupplierToActivityIdList = weChatShopAftersaleService.selectShopGoodsSkuAndSupplierToActivityId(map);
		if (shopGoodsSkuAndSupplierToActivityIdList.size() == 0) {
			this.render(response, "{\"message\":\"该商品已下线或者不存在\",\"flag\":false}");
			return;
		}
		
		map.put("goodsId", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("goodsId"));
		map.put("skuId", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("skuId"));
		map.put("supplierUid", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("supplierUid"));
		
		// 查询售后状态 0:可以售后 1 进行中 2 结束
		List<Map<String, Object>> ifCanAftersalesList = weChatShopAftersaleService.selectOrderState(map);
		if (ifCanAftersalesList.size() == 0) {
			this.render(response, "{\"message\":\"没有找到该订单信息\",\"flag\":false}");
			return;
		}
		
		String aftersaleState = String.valueOf(ifCanAftersalesList.get(0).get("aftersaleState"));
		
		if (!aftersaleState.equals("0")) {
			this.render(response, "{\"message\":\"无需重复申请售后\",\"flag\":false}");
			return;
		}
		
		// 检查订单是否存在未处理完的售后(state:0.待处理;1.已回应;2:已关闭;3:处理完毕。)
		List<Map<String, Object>> checkWhetherThereIsAnUnfinishedAftersaleOrdeList = weChatShopAftersaleService.checkWhetherThereIsAnUnfinishedAftersaleOrde(map);
		if (checkWhetherThereIsAnUnfinishedAftersaleOrdeList.size() != 0) {
			this.render(response, "{\"message\":\"有未处理完成的收货\",\"flag\":false}");
			return;
		}
		
		// 更新订单状态为售后状态
		int updateOrderStatusToBeAftersalesResult = weChatShopAftersaleService.updateOrderStatusToBeAftersales(map);
		if (updateOrderStatusToBeAftersalesResult != 1) {
			this.render(response, "{\"message\":\"更新订单状态失败\",\"flag\":false}");
			return;
		}
		
		// 判断图片
		if (img == null || img.equals("") || img.equals("null")) {
			this.render(response, "{\"message\":\"图片不能为空\",\"flag\":false}");
			return;
		}
		
		StringBuilder imgId = new StringBuilder(); 
		if (img.contains(",")) {
			String[] imgs = img.split(",");
			for (int i = 0; i < imgs.length; i++) {
				map.put("img", imgs[i]);
				// 添加图片
				int insertImgResult = weChatShopAftersaleService.insertUserAftersaleImg(map);
				if (insertImgResult != 1) {
					this.render(response, "{\"message\":\"添加图片失败\",\"flag\":false}");
					return;
				}
				// 查询当前添加的图片编号
				int selectImgId = weChatShopAftersaleService.selectHaveBeenAddedAftersaleImgId(map);
				imgId.append(selectImgId);
			}
		} else {
			map.put("img", img);
			// 添加图片
			int insertImgResult = weChatShopAftersaleService.insertUserAftersaleImg(map);
			if (insertImgResult != 1) {
				this.render(response, "{\"message\":\"添加图片失败\",\"flag\":false}");
				return;
			}
			// 查询当前添加的图片编号
			int selectImgId = weChatShopAftersaleService.selectHaveBeenAddedAftersaleImgId(map);
			imgId.append(selectImgId);
		}
		map.put("imgId", imgId);
		
		// 添加用户申请售后信息
		int insertVblShopOrderAftersaleDataResult = weChatShopAftersaleService.insertVblShopOrderAftersaleData(map);
		if (insertVblShopOrderAftersaleDataResult != 1) {
			this.render(response, "{\"message\":\"添加申请售后信息失败\",\"flag\":false}");
			return;
		}
		
		// 更新门店交易订单清单信息售后状态
		int updateShopTransactionOrderInventoryStateResult = weChatShopAftersaleService.updateShopTransactionOrderInventoryState(map);
		if (updateShopTransactionOrderInventoryStateResult != 1) {
			this.render(response, "{\"message\":\"更新门店交易订单清单信息售后状态失败\",\"flag\":false}");
			return;
		}
		
		map.put("describe", "订单" + orderId + "生成售后单，请留意售后进程。");// 推送信息
		map.put("pushContent", "申请售后");
		// 添加推送消息计划
		int insertVblPushPlanResult = weChatOrderService.insertVblPushPlan(map);
		if (insertVblPushPlanResult != 1) {
			this.render(response, "{\"message\":\"添加推送消息计划失败\",\"flag\":false}");
			return;
		}
		this.render(response, "{\"message\":\"成功提交<br/>请耐心等待结果\",\"flag\":true}");
		return;
	}
}
