package com.wboly.wechat.controller.goods;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.system.sys.cache.RedisBase;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.SessionUtil;
import rebue.wheel.OkhttpUtils;
import redis.clients.jedis.Jedis;

/**
 * @Name: 微信 商品.java
 * @Author: nick
 */
@Controller
@SuppressWarnings("unchecked")
public class WeChatGoodsController extends SysController {

	/**
	 * @Name: 商品详情页面
	 * @throws Exception
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/goodsDetail")
	public ModelAndView GoodsDetail(HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		// 上线id
		String onlineId = request.getParameter("onlineId");
		if(request.getParameter("guideDisplay") == null) {
			mav.addObject("guideDisplay", "none");
		}else {
			mav.addObject("guideDisplay", "");
		}
		String goodsPics = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/onlinepic?onlineId=" + onlineId);
		System.out.println("获取到的商品上线图片为：{}" + goodsPics);
		List<Map<String, Object>> listPics = JsonUtil.listMaps(goodsPics);
		StringBuilder pics = new StringBuilder();
		for (int i = 0; i < listPics.size(); i++) {
			pics.append(listPics.get(i).get("picPath") + ",");
		}
		mav.addObject("onlineId", onlineId);
		mav.addObject("goodsPics", pics.substring(0, pics.length() - 1));
		mav.setViewName("/htm/wechat/goods/goodsDetail");// 商品详情
		return mav;
	}

	/**
	 * @Name: 全部商品列表页面
	 * @Author: nick
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping(value = "/wechat/goods/allGoodsList")
	public ModelAndView allGoodsList(HttpServletRequest request) throws UnsupportedEncodingException {
		ModelAndView mav = new ModelAndView();
		Object shopByColumn = SessionUtil.getShopData(request);

		if (shopByColumn != null) {
			mav.addObject("shop", (Map<String, Object>) shopByColumn);
		} else {
			mav.addObject("shop", retuenShop());
		}
		String classId = request.getParameter("c");
		if (classId != null) {
			String method = request.getMethod();
			if (method.equals("GET")) {
				byte[] bytes = classId.getBytes("iso-8859-1");

				classId = new String(bytes, "UTF-8");

				String regex = "\u4e00-\u9fa5";

				Pattern pat = Pattern.compile(regex);

				Matcher mat = pat.matcher(classId);

				classId = mat.replaceAll("");
			}
			mav.addObject("goodsclass", classId);
		} else {
			mav.addObject("goodsclass", 0);
		}
		String serach = request.getParameter("searchbar");
		if (serach != null) {
			String method = request.getMethod();
			if (method.equals("GET")) {
				byte[] bytes = serach.getBytes("iso-8859-1");

				serach = new String(bytes, "UTF-8");
			}
			mav.addObject("serach", serach);
		} else {
			mav.addObject("serach", "");
		}
		mav.setViewName("/htm/wechat/goods/goodsLists");
		return mav;
	}

	/**
	 * @Name: 获取全部商品列表
	 * @throws TException
	 * @throws IOException
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/getAllGoodsList")
	public void getAllGoodsList(HttpServletRequest request, HttpServletResponse response)
			throws TException, IOException {
		// 排序方式标识,0:综合; ;2:价格
		String sortIde = request.getParameter("type");
		// 排序字段
		String sortField = "";
		if (sortIde != null && sortIde.equals("2")) {
			sortField = "oos.SALE_PRICE";
		} else {
			sortField = "oo.ONLINE_TIME";
		}

		// 升序降序标识,1:升序;0:降序
		String sortType = request.getParameter("sortType");
		if (sortType != null && sortType.equals("1")) {
			sortType = "asc";
		} else {
			sortType = "desc";
		}
		// 起始条数
		String start = request.getParameter("start");
		// 每页条数
		String limit = request.getParameter("limit");
		// 最低价
		String lowPrice = request.getParameter("lowPrice");
		// 最高价
		String hignPrice = request.getParameter("hignPrice");
		// 商品类型
		String subjectType = request.getParameter("subjectType");
		System.out.println("商品类型为：" + subjectType);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sortname", sortField);
		map.put("sortOrder", sortType);
		map.put("start", start);
		map.put("size", limit);
		if (lowPrice != null && !lowPrice.equals("") && !lowPrice.equals("null")) {
			map.put("lowPrice", lowPrice);
		}
		if (hignPrice != null && !hignPrice.equals("") && !hignPrice.equals("null")) {
			map.put("hignPrice", hignPrice);
		}
		if (subjectType != null && !subjectType.equals("") && !subjectType.equals("null")) {
			map.put("subjectType", subjectType);
		}
		// 搜索关键字
		String keywords = request.getParameter("keywords");
		if (keywords != null && !keywords.equals("") && !keywords.equals("null")) {
			map.put("onlineTitle", keywords);
		}
		System.out.println("获取已上线商品列表信息的参数为：" + String.valueOf(map));
		// 获取已上线商品列表
		String result = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/online/list", map);
		System.err.println("获取到的已上线商品列表参数为：" + result);
		this.render(response, result);
	}

	/**
	 * @Name: 商品列表页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/goodsList")
	public ModelAndView GoodsList(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		Object shopByColumn = SessionUtil.getShopData(request);

		if (shopByColumn != null) {
			mav.addObject("shop", (Map<String, Object>) shopByColumn);
		} else {
			mav.addObject("shop", retuenShop());
		}

		mav.setViewName("/htm/wechat/goods/goodsList");
		return mav;
	}

	/**
	 * @Name: 跳转至商品类目页面
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/goodsNav")
	public ModelAndView getGoodsNav(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/htm/wechat/goods/goodsNav");
		return mav;
	}

	/**
	 * @Name: 上拉获取商品列表
	 * @throws TException
	 * @throws IOException
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/getGoodsList")
	public void getGoodsList(HttpServletRequest request, HttpServletResponse response) throws TException, IOException {
		// 排序方式标识,0:综合; ;2:价格
		String sortIde = request.getParameter("type");
		// 排序字段
		String sortField = "";
		if (sortIde != null && sortIde.equals("2")) {
			sortField = "oos.SALE_PRICE";
		} else {
			sortField = "oo.ONLINE_TIME";
		}

		// 升序降序标识,1:升序;0:降序
		String sortType = request.getParameter("sortType");
		if (sortType != null && sortType.equals("1")) {
			sortType = "asc";
		} else {
			sortType = "desc";
		}
		// 起始条数
		String start = request.getParameter("start");
		// 每页条数
		String limit = request.getParameter("limit");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sortname", sortField);
		map.put("sortOrder", sortType);
		map.put("start", start);
		map.put("size", limit);
		System.out.println("获取已上线商品列表信息的参数为：" + String.valueOf(map));
		// 获取已上线商品列表
		String result = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/online/list", map);
		System.err.println("获取到的已上线商品列表参数为：" + result);
		this.render(response, result);
	}

	/**
	 * @Name: 加载子级菜单
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/loadSonMenu")
	public void loadSonMenu(HttpServletRequest request, HttpServletResponse response) {

		Object shopByColumn = SessionUtil.getShopByColumn(request, "shopId");

		if (shopByColumn != null) {

			String parentId = request.getParameter("classId");

			Jedis jedis = null;

			List<Map<String, Object>> menulist = null;
			try {
				jedis = RedisBase.getJedis();

				if (null != jedis) {

					menulist = new ArrayList<Map<String, Object>>();

					jedis.select(Integer.valueOf(String.valueOf(shopByColumn)));

					String key = "ClassMenu:";

					// 查询该菜单节点下的所有子菜单
					Set<String> keys = jedis.keys(key + parentId + ":*");

					// 判断该节点下是否有子菜单
					if (keys != null && keys.size() > 0) {

						for (String keys1 : keys) {

							Map<String, String> map = jedis.hgetAll(keys1);

							Map<String, Object> newMap = new HashMap<String, Object>();
							for (Map.Entry<String, String> entry : map.entrySet()) {
								newMap.put(entry.getKey(), entry.getValue().toString());
							}
							List<Map<String, String>> nodelist = new ArrayList<Map<String, String>>();
							String classId = map.get("classId");
							Set<String> nextNodes = jedis.keys(key + classId + ":*");

							if (nextNodes != null && nextNodes.size() > 0) {
								for (String keys2 : nextNodes) {
									Map<String, String> hgetAll = jedis.hgetAll(keys2);
									if (hgetAll != null && hgetAll.size() > 0) {
										nodelist.add(hgetAll);
									}
								}
								newMap.put("nodes", nodelist);
							} else {
								Set<String> nextNodes1 = jedis.keys(key + parentId + ":" + classId);

								for (String keys2 : nextNodes1) {
									Map<String, String> hgetAll = jedis.hgetAll(keys2);
									if (hgetAll != null && hgetAll.size() > 0) {
										nodelist.add(hgetAll);
									}
								}
								newMap.put("nodes", nodelist);
							}

							if (newMap != null && newMap.size() > 0) {
								menulist.add(newMap);
							}
						}
						// 如果该节点下没有子节点,把父节点当成子节点
					} else {
						Set<String> keyone = jedis.keys(key + 0 + ":" + parentId);

						for (String keys1 : keyone) {

							Map<String, String> map = jedis.hgetAll(keys1);

							Map<String, Object> newMap = new HashMap<String, Object>();
							for (Map.Entry<String, String> entry : map.entrySet()) {
								newMap.put(entry.getKey(), entry.getValue().toString());
							}

							List<Map<String, String>> nodelist = new ArrayList<Map<String, String>>();
							String classId = map.get("classId");

							Set<String> nextNodes = jedis.keys(key + 0 + ":" + classId);

							if (nextNodes != null && nextNodes.size() > 0) {
								for (String keys2 : nextNodes) {
									Map<String, String> hgetAll = jedis.hgetAll(keys2);
									if (hgetAll != null && hgetAll.size() > 0) {
										nodelist.add(hgetAll);
									}
								}
								newMap.put("nodes", nodelist);
							} else {
								Set<String> nextNodes1 = jedis.keys(key + parentId + ":" + classId);

								for (String keys2 : nextNodes1) {
									Map<String, String> hgetAll = jedis.hgetAll(keys2);
									if (hgetAll != null && hgetAll.size() > 0) {
										nodelist.add(hgetAll);
									}
								}
								newMap.put("nodes", nodelist);
							}

							if (newMap != null && newMap.size() > 0) {
								menulist.add(newMap);
							}
						}
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} finally {
				if (null != jedis) {
					jedis.close();
				}
			}
			writerJson(response, menulist);
		}
	}

	/**
	 * 获取商品轮播图 Title: goodsCarouselPic Description:
	 * 
	 * @throws IOException
	 * @date 2018年4月1日 下午2:40:12
	 */
	@RequestMapping("/wechat/goods/goodsCarouselPic")
	public void goodsCarouselPic(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		String onlineId = request.getParameter("onlineId").trim();
		map.put("onlineId", onlineId);
		map.put("picType", "0");
		// 获取上线商品轮播图
		String results = "";
		try {
			results = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/onlinepic", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("获取到的上线商品轮播图的返回值为：" + results);
		this.render(response, results);
	}

	/**
	 * 获取商品详情信息 Title: selectGoodsDetails Description:
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @date 2018年4月1日 下午3:55:14
	 */
	@RequestMapping("/wechat/goods/selectGoodsDetails")
	public void selectGoodsDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取商品详情信息
		String results = "";
		try {
			results = OkhttpUtils
					.get(SysContext.ONLINEURL + "/onl/online/list?id=" + request.getParameter("onlineId").trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("获取到的商品详情信息为：" + results);
		this.render(response, results);
	}

	/**
	 * 获取上线商品规格详情信息 Title: selectGoodsSpecDetails Description:
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @date 2018年4月1日 下午4:38:15
	 */
	@RequestMapping("/wechat/goods/selectGoodsSpecDetails")
	public void selectGoodsSpecDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 上线规格编号
		String specId = request.getParameter("specId");
		// 上线编号
		String onlineId = request.getParameter("onlineId").trim();
		Map<String, Object> map = new HashMap<String, Object>();
		if (!specId.equals("0")) {
			map.put("id", specId);
		}
		map.put("onlineId", onlineId);
		// 获取商品规格详情信息
		String results = "";
		try {
			results = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/onlinespec/details", map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("获取到的商品规格详情信息的返回值为：" + results);
		this.render(response, results);
	}

	/**
	 * 获取全部全返商品列表
	 */

	@RequestMapping(value = "/wechat/goods/fullReturnGoodsList")
	public ModelAndView allfullReturnGoodsList(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		System.out.println("开始获取全返商品列表");
		ModelAndView mav = new ModelAndView();
		Object shopByColumn = SessionUtil.getShopData(request);
		if (shopByColumn != null) {
			mav.addObject("shop", (Map<String, Object>) shopByColumn);
		} else {
			mav.addObject("shop", retuenShop());
		}
		String classId = request.getParameter("c");
		if (classId != null) {
			String method = request.getMethod();
			if (method.equals("GET")) {
				byte[] bytes = classId.getBytes("iso-8859-1");

				classId = new String(bytes, "UTF-8");

				String regex = "\u4e00-\u9fa5";

				Pattern pat = Pattern.compile(regex);

				Matcher mat = pat.matcher(classId);

				classId = mat.replaceAll("");
			}
			mav.addObject("goodsclass", classId);
		} else {
			mav.addObject("goodsclass", 0);
		}
		String serach = request.getParameter("searchbar");
		if (serach != null) {
			String method = request.getMethod();
			if (method.equals("GET")) {
				byte[] bytes = serach.getBytes("iso-8859-1");

				serach = new String(bytes, "UTF-8");
			}
			mav.addObject("serach", serach);
		} else {
			mav.addObject("serach", "");
		}
		mav.setViewName("/htm/wechat/goods/fullReturnGoodsList");
		return mav;
	}

	/**
	 * @Name: 获取全返商品列表
	 * @throws TException
	 * @throws IOException
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/goods/getFullReturnGoodsList")
	public void getFullReturnGoodsList(HttpServletRequest request, HttpServletResponse response)
			throws TException, IOException {
		// 排序方式标识,0:综合; ;2:价格
		String sortIde = request.getParameter("type");
		// 排序字段
		String sortField = "";
		if (sortIde != null && sortIde.equals("2")) {
			sortField = "oos.SALE_PRICE";
		} else {
			sortField = "oo.ONLINE_TIME";
		}

		// 升序降序标识,1:升序;0:降序
		String sortType = request.getParameter("sortType");
		if (sortType != null && sortType.equals("1")) {
			sortType = "asc";
		} else {
			sortType = "desc";
		}
		// 起始条数
		String start = request.getParameter("start");
		// 每页条数
		String limit = request.getParameter("limit");
		// 板块类型
		String subjectType = request.getParameter("subjectType");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sortname", sortField);
		map.put("sortOrder", sortType);
		map.put("start", start);
		map.put("size", limit);
		map.put("subjectType", subjectType);
		// 搜索关键字
		String keywords = request.getParameter("keywords");
		if (keywords != null && !keywords.equals("") && !keywords.equals("null")) {
			map.put("onlineTitle", keywords);
		}
		System.out.println("获取已上线商品列表信息的参数为：" + String.valueOf(map));
		// 获取已上线商品列表
		String result = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/online/list", map);
		System.err.println("获取到的已上线商品列表参数为：" + result);
		this.render(response, result);
	}
}
