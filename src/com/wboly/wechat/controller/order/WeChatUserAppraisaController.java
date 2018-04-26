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
import com.wboly.wechat.service.order.WeChatUserAppraisalService;

@Controller
public class WeChatUserAppraisaController extends SysController {

	@Autowired
	private WeChatUserAppraisalService weChatUserAppraisalService;
	@Autowired
	private WeChatShopAftersaleService weChatShopAftersaleService;
	@Autowired
	private WeChatOrderService weChatOrderService;
	
	/**
	 * @Name: 评价页面跳转
	 * @Author: nick
	 * 2018年1月17日16:07:50
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/wechat/order/commentPage")
	public ModelAndView commentPage(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			mav.setViewName("/htm/wechat/login/login");
		} else {
			String commentData = request.getParameter("commentData");
			if (commentData != null) {
				Base64EnOut b64 = new Base64EnOut();
				commentData = b64.Decode(commentData);
				commentData = b64.Decode(commentData);

				mav.addObject("commentData", JsonUtil.jsonStringToMap(commentData));
			} else {
				mav.addObject("commentData", "");
			}
			mav.setViewName("/htm/wechat/order/comment");
		}
		return mav;
	}
	
	/**
	 * 买家评价商品
	 * @param request
	 * @param response
	 * 2018年1月17日16:07:56
	 */
	@RequestMapping(value = "/wechat/order/appraiseGoods")
	public void appraiseOrder(HttpServletRequest request, HttpServletResponse response) {
		String loginId = SysCache.getWeChatUserByColumn(request, "userId"); // 当前用户编号
		String loginName = SysCache.getWeChatUserByColumn(request, "userName"); // 当前用户名称
		String orderId = request.getParameter("orderId"); // 订单编号
		String activityId = request.getParameter("activityId"); // 活动编号
		String babyEvaluation = request.getParameter("babyEvaluation"); // 宝贝评价
		String content = request.getParameter("content"); // 评价内容
		String imgs = request.getParameter("imgs"); // 评价图片
		// 获取当前时间戳
		long currentTime = System.currentTimeMillis() / 1000; 
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("activityId", activityId);
		map.put("orderId", orderId);
		map.put("babyEvaluation", babyEvaluation);
		map.put("content", content);
		map.put("buyerUid", loginId);
		map.put("dateline", currentTime);
		map.put("logiSticsService", 0);
		map.put("sellerService", 0);
		map.put("userid", loginId);
		map.put("userName", loginName);
		
		// 
		List<Map<String, Object>> orderStateAndBuyerList = weChatShopAftersaleService.selectOrderStateAndBuyer(map);
		if (orderStateAndBuyerList.size() == 0) {
			this.render(response, "{\"message\":\"未购买该商品<br/>无法评价\",\"flag\":false}");
			return;
		}
		
		map.put("shopId", orderStateAndBuyerList.get(0).get("shopId"));
		
		// 根据订单活动获取门店编号、商品编号、商品Sku编号、供应商用户编号
		List<Map<String, Object>> shopGoodsSkuAndSupplierToActivityIdList = weChatShopAftersaleService.selectShopGoodsSkuAndSupplierToActivityId(map);
		if (shopGoodsSkuAndSupplierToActivityIdList.size() == 0) {
			this.render(response, "{\"message\":\"该商品已下线或者不存在\",\"flag\":false}");
			return;
		}
		
		map.put("goodsId", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("goodsId"));
		map.put("skuId", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("skuId"));
		map.put("supplierUid", shopGoodsSkuAndSupplierToActivityIdList.get(0).get("supplierUid"));
		
		// 查询售后状态 0:可以售后 1 进行中 2 结束
		List<Map<String, Object>> orderStateList = weChatShopAftersaleService.selectOrderState(map);
		if (orderStateList.size() == 0) {
			this.render(response, "{\"message\":\"没有找到该订单信息\",\"flag\":false}");
			return;
		}
		
		// 评价状态 0：待评价；1：追加评价；2：评价结束
		String appraiseState = String.valueOf(orderStateList.get(0).get("appraiseState"));
		
		if (appraiseState.equals("2")) {
			this.render(response, "{\"message\":\"无需重复追加评价\",\"flag\":false}");
			return;
		}
		
		map.put("appraiseState", Integer.parseInt(appraiseState) + 1);
		System.err.println("修改订单评价状态的参数为：" + String.valueOf(map));
		// 修改订单商品详情评价状态
		int updateUserAppraiseStateResult = weChatUserAppraisalService.updateUserAppraiseState(map);
		System.err.println("修改订单评价状态返回值为：" + updateUserAppraiseStateResult);
		if (updateUserAppraiseStateResult <= 0) {
			this.render(response, "{\"message\":\"修改商品订单评价状态失败\",\"flag\":false}");
			return;
		}
		
		StringBuilder imgIds = new StringBuilder();
		
		if (imgs != null && !imgs.equals("") && !imgs.equals("null")) {
			if (imgs.contains(",")) {
				String[] imgss = imgs.split(",");
				for (int i = 0; i < imgss.length; i++) {
					map.put("img", imgss[i]);
					
					// 添加评价图片
					int insertUserAppraiseImgResult = weChatUserAppraisalService.insertUserAppraiseImg(map);
					if (insertUserAppraiseImgResult <= 0) {
						this.render(response, "{\"message\":\"添加评价图片失败\",\"flag\":false}");
						return;
					}
					int imgId = weChatUserAppraisalService.selectUserAppraiseImgId(map);
					imgIds.append(imgId);
				}
			} else {
				//img.append(imgs);
				map.put("img", imgs);
				// 添加评价图片
				int insertUserAppraiseImgResult = weChatUserAppraisalService.insertUserAppraiseImg(map);
				if (insertUserAppraiseImgResult <= 0) {
					this.render(response, "{\"message\":\"添加评价图片失败\",\"flag\":false}");
					return;
				}
				int imgId = weChatUserAppraisalService.selectUserAppraiseImgId(map);
				imgIds.append(imgId);
			}
		}
		
		map.put("imgId", imgIds);
		System.err.println("添加评价信息的参数为：" + map.toString());
		// 添加评价信息
		int insertVblShopOrderAppraiseDataResult = weChatUserAppraisalService.insertVblShopOrderAppraiseData(map);
		System.err.println("添加评价信息的返回值为：" + insertVblShopOrderAppraiseDataResult);
		if (insertVblShopOrderAppraiseDataResult <= 0) {
			this.render(response, "{\"message\":\"评价失败\",\"flag\":false}");
			return;
		}
		
		System.err.println("map.get(appraiseId)====" + insertVblShopOrderAppraiseDataResult);
		System.err.println("查询商品累计数的参数为：" + map.toString());
		List<Map<String, Object>> goodsTotalAppraiseNumberList = weChatUserAppraisalService.selectGoodsTotalAppraiseNumber(map);
		System.err.println("查询商品累计数的返回值为：" + String.valueOf(goodsTotalAppraiseNumberList));
		if (goodsTotalAppraiseNumberList.size() == 0) {
			System.err.println("添加累计评价数的参数为：" + map.toString());
			// 增加累积评价数
			int insertGoodsTotalAppraiseNumberResult = weChatUserAppraisalService.insertGoodsTotalAppraiseNumber(map);
			System.err.println("添加累计评价数的返回值为：" + insertGoodsTotalAppraiseNumberResult);
			if (insertGoodsTotalAppraiseNumberResult <= 0) {
				this.render(response, "{\"message\":\"添加累计评价失败\",\"flag\":false}");
				return;
			}
		} else {
			System.err.println("修改累计评价数的参数为：" + map.toString());
			int updateGoodsTotalAppraiseNumberResult = weChatUserAppraisalService.updateGoodsTotalAppraiseNumber(map);
			System.err.println("修改累计评价数的返回值为：" + updateGoodsTotalAppraiseNumberResult);
			if (updateGoodsTotalAppraiseNumberResult <= 0) {
				this.render(response, "{\"message\":\"修改累计评价失败\",\"flag\":false}");
				return;
			}
		}
		
		// 添加门店交易订单状态跟踪信息
		if (appraiseState.equals("0")) {
			map.put("describe", "生成评价单");
		} else {
			map.put("describe", "生成追加评价单");
		}
		System.err.println("添加门店交易订单状态跟踪信息的参数为：" + map.toString());
		int insertVblShopOrderTradeRecordResult = weChatOrderService.insertVblShopOrderTradeRecord(map);
		System.err.println("添加门店交易订单状态跟踪信息的返回值为：" + insertVblShopOrderTradeRecordResult);
		if (insertVblShopOrderTradeRecordResult <= 0) {
			this.render(response, "{\"message\":\"添加门店交易订单状态跟踪信息失败\",\"flag\":false}");
			return;
		}
		this.render(response, "{\"message\":\"已提交\",\"flag\":true}");
	}
}
