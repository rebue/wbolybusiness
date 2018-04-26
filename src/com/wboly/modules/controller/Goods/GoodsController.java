package com.wboly.modules.controller.Goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.wboly.modules.service.goods.VblGoodsService;
import com.wboly.rpc.Client.AppGoodsRPCClient;
import com.wboly.rpc.entity.GoodsEntity;
import com.wboly.system.sys.annotation.ArgsLog;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.util.WriterJsonUtil;

@Controller
public class GoodsController extends SysController {

	@Autowired
	private VblGoodsService vblgoodsService;

	/**
	 * 详情页商品基本信息
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/goods/info")
	@ArgsLog
	public void goodsInfo(HttpServletRequest request, HttpServletResponse response) throws TException {
		String goodsId = request.getParameter("goodsId");
		String shopId = request.getParameter("shopId");
		String supplierUid = request.getParameter("supplierUid");
		String activityId = request.getParameter("activityId");
		if (Integer.valueOf(goodsId) == 0 || "".equals(Integer.valueOf(goodsId))) {
			WriterJsonUtil.writerJson(response, "");
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("goodsId", goodsId);
		map.put("shopId", shopId);
		map.put("activityId", activityId);
		map.put("supplierUid", supplierUid);

		Map<String, String> findGoodsBase = vblgoodsService.findGoodsBase(map);
		WriterJsonUtil.writerJson(response, findGoodsBase);
	}

	/**
	 * 商品SKU信息
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/goods/sku")
	@ArgsLog
	public void goodssku(GoodsEntity entity, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String supplierUid = request.getParameter("supplierUid") == null ? "" : request.getParameter("supplierUid");
		entity.setPostUid(supplierUid);
		List<Map<String, Object>> goodsSKU = vblgoodsService.goodsSKU(entity);
		WriterJsonUtil.writerJson(response, goodsSKU);
	}

	/**
	 * 详情页商品图文详情信息
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/goods/details")
	@ArgsLog
	public void goodsDetails(GoodsEntity entity, HttpServletResponse response) throws TException {
		Map<String, String> map = vblgoodsService.getGoodsDeteilById(entity);
		String deteils = map.get("deteils");
		WriterJsonUtil.writerJson(response, deteils);
	}

	/**
	 * 详情页商品产品信息
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/goods/specification")
	@ArgsLog
	public void goodsSpecification(GoodsEntity entity, HttpServletResponse response) throws TException {
		AppGoodsRPCClient goodsService = new AppGoodsRPCClient();
		Map<String, String> map = goodsService.client.goodsSpecifications(entity);
		WriterJsonUtil.writerJson(response, map);
	}

	/**
	 * 商品库存数量
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/goods/GoodsStockNum")
	@ArgsLog
	public void GoodsStockNum(HttpServletRequest request, HttpServletResponse response) {
		try {
			String shopId = request.getParameter("shopId");
			String skuId = request.getParameter("skuId");
			String goodsId = request.getParameter("goodsId");
			String supplierUid = request.getParameter("supplierUid");

			Map<String, Object> hmap = new HashMap<String, Object>();

			hmap.put("shopId", shopId);
			hmap.put("skuId", skuId);
			hmap.put("goodsId", goodsId);
			hmap.put("supplierUid", supplierUid);

			Long re = vblgoodsService.GoodsStockNum(hmap);
			WriterJsonUtil.writerJson(response, re);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
