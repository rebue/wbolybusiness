package com.wboly.wechat.controller.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.modules.service.index.VblIndexService;
import com.wboly.rpc.entity.IndexEntity;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.SessionUtil;
import com.wboly.system.sys.util.WriterJsonUtil;
import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 首页.java
 * @Author: nick
 */
@Controller
public class WeChatIndexController extends SysController {

	@Autowired
	private VblIndexService vblindexService;

	/**
	 * @Name: 首页页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/index/indexInfo")
	public ModelAndView indexInfo(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView();
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null) {
			mav.addObject("userId", userId);
		} else {
			userId = String.valueOf(request.getSession().getAttribute("loginId"));
		}
		mav.addObject("JSURL", request.getRequestURL());
		mav.setViewName("/htm/wechat/index/index");
		return mav;
	}

	/**
	 * @Name: 获取首页所有数据
	 * @Author: nick
	 */
	public void getIndexAllData(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Object shopByColumn = SessionUtil.getShopByColumn(request, "shopId");

		Integer shopId = (shopByColumn != null ? (Integer) shopByColumn : 0);
		// 首页轮播图
		IndexEntity entity = new IndexEntity();
		entity.setModuletype(11);
		entity.setPlateId("1");
		entity.setShopId(shopId);
		List<Map<String, Object>> AdsList = vblindexService.indexAds(entity);

		// 首页公告
		List<Map<String, Object>> AfficheList = vblindexService.indexAffiche();

		// 首页商品信息
		Map<String, List<Map<String, String>>> AllMap = new HashMap<String, List<Map<String, String>>>();
		// 根据门店获取首页商品信息
		entity.setShopId(shopId);
		entity.setModuletype(1);
		List<Map<String, String>> appSwitchGoods1 = vblindexService.appSwitchGoods(entity);
		entity.setModuletype(2);
		List<Map<String, String>> appSwitchGoods2 = vblindexService.appSwitchGoods(entity);
		entity.setModuletype(3);
		List<Map<String, String>> appSwitchGoods3 = vblindexService.appSwitchGoods(entity);
		AllMap.put("1", appSwitchGoods1);
		AllMap.put("2", appSwitchGoods2);
		AllMap.put("3", appSwitchGoods3);

		this.render(response,
				"{\"message\":{\"AdsList\":" + JsonUtil.ObjectToJson(AdsList) + ",\"AfficheList\":"
						+ JsonUtil.ObjectToJson(AfficheList) + ",\"AllMap\":" + JsonUtil.ObjectToJson(AllMap)
						+ "},\"flag\":true}");
	}

	/**
	 * @Name: 首页商品信息
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/index/getGoodsInfo")
	public void indexGoodsInfo(HttpServletRequest request, HttpServletResponse response) {

		Map<String, List<Map<String, String>>> AllMap = new HashMap<String, List<Map<String, String>>>();
		Object shopByColumn = SessionUtil.getShopByColumn(request, "shopId");
		Integer shopId = (shopByColumn != null ? (Integer) shopByColumn : 0);
		IndexEntity entity = new IndexEntity();

		// 根据门店获取首页商品信息
		entity.setShopId(shopId);

		entity.setModuletype(1);
		List<Map<String, String>> appSwitchGoods1 = vblindexService.appSwitchGoods(entity);
		entity.setModuletype(2);
		List<Map<String, String>> appSwitchGoods2 = vblindexService.appSwitchGoods(entity);
		entity.setModuletype(3);
		List<Map<String, String>> appSwitchGoods3 = vblindexService.appSwitchGoods(entity);

		AllMap.put("1", appSwitchGoods1);
		AllMap.put("2", appSwitchGoods2);
		AllMap.put("3", appSwitchGoods3);

		WriterJsonUtil.writerJson(response, AllMap);
	}

	/**
	 * @Name: 首页轮播图
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/index/getAdsInfo")
	public void indexAdsInfo(HttpServletRequest request, HttpServletResponse response) {
		try {

			Object shopByColumn = SessionUtil.getShopByColumn(request, "shopId");

			Integer shopId = (shopByColumn != null ? (Integer) shopByColumn : 0);

			IndexEntity entity = new IndexEntity();

			entity.setModuletype(11);
			entity.setPlateId("1");
			entity.setShopId(shopId);

			List<Map<String, Object>> AdsList = vblindexService.indexAds(entity);
			WriterJsonUtil.writerJson(response, AdsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取首页推广上线商品信息 Title: promotionOnlineGoodsList Description:
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @date 2018年3月29日 上午11:55:38
	 */
	@RequestMapping(value = "/wechat/index/getAllIndexData", method = RequestMethod.POST)
	public void promotionOnlineGoodsList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取首页推广上线商品信息
		String results = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/onlinepromotion/list?promotionType=1");
		System.out.println("获取到的首页推广上线商品信息为：" + results);
		this.render(response, results);
	}

}
