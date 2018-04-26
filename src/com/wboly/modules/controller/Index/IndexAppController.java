package com.wboly.modules.controller.Index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.wboly.modules.service.index.VblIndexService;
import com.wboly.rpc.Client.IndexRPCClient;
import com.wboly.rpc.entity.IndexEntity;
import com.wboly.rpc.entity.UserEntity;
import com.wboly.system.sys.annotation.ArgsLog;
import com.wboly.system.sys.cache.RedisBase;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.CacheUtil;
import com.wboly.system.sys.util.WriterJsonUtil;

import redis.clients.jedis.Jedis;

@Controller
public class IndexAppController extends SysController {

	@Autowired
	private VblIndexService vblindexService;
	
	//@RequestMapping("/app/index/flushCache")
	public void flushCache(HttpServletResponse response, HttpServletRequest request){
		String flushKey = request.getParameter("flushKey");
		String type = request.getParameter("type");
		if(flushKey!=null && !"".equals(flushKey)){
			SysCache.flushAll(flushKey);
		}
		if(null != type && type.equals("2")){
			CacheUtil.removeObject(flushKey);
		}
	}

	/**
	 * @throws Exception
	 * @Name: 所有的有效区域地址
	 * @Author: nick
	 */
	//@RequestMapping("/app/index/effectiveArea")
	@ArgsLog
	public void allShopAddress(HttpServletResponse response, HttpServletRequest request){
		this.render(response, vblindexService.selectAllShopAddress());
	}
	/**
	 * @throws Exception
	 * @Name: 有商品的门店区域信息
	 * @Author: nick
	 */
	//@RequestMapping("/app/index/haveAreaShop")
	@ArgsLog
	public void HaveGoodsShop(HttpServletResponse response, HttpServletRequest request) throws Exception {
		this.render(response, vblindexService.selectHaveAreaShop());
	}

	/**
	 * 数据初始化
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/initIndex")
	@ArgsLog
	public void initIndex(IndexEntity entity, HttpServletResponse response, HttpServletRequest request)
			throws TException {
		vblindexService.initIndex();
		vblindexService.initMenu2();
		vblindexService.initMenu();
		vblindexService.initIndexAll();
	}

	/**
	 * 首页商品
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/indexGoods")
	@ArgsLog
	public void indexGoods(IndexEntity entity, HttpServletResponse response) throws TException {
		Map<String, List<Map<String, String>>> AllMap = new HashMap<String, List<Map<String, String>>>();
		/********************* 1.今日推荐 ************************/
		entity.setModuletype(1);
		List<Map<String, String>> appSwitchGoods1 = vblindexService.appSwitchGoods(entity);
		/********************* 1.今日推荐 ************************/
		/********************* 2. 新品上市 ************************/
		entity.setModuletype(2);
		List<Map<String, String>> appSwitchGoods2 = vblindexService.appSwitchGoods(entity);
		/********************* 2. 新品上市 ************************/
		/********************* 3..热销商品 ************************/
		entity.setModuletype(3);
		List<Map<String, String>> appSwitchGoods3 = vblindexService.appSwitchGoods(entity);
		/********************* 3.热销商品 ************************/
		AllMap.put("1", appSwitchGoods1);
		AllMap.put("2", appSwitchGoods2);
		AllMap.put("3", appSwitchGoods3);
		WriterJsonUtil.writerJson(response, AllMap);
	}

	/**
	 * 首页广告图片
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/index/indexAds")
	@ArgsLog
	public void indexAds(IndexEntity entity, HttpServletResponse response) throws Exception {
		entity.setModuletype(11);
		List<Map<String, Object>> AdsList = vblindexService.indexAds(entity);
		WriterJsonUtil.writerJson(response, AdsList);
	}

	/**
	 * 首页公告信息
	 * 
	 * @param entity
	 * @param response
	 * @throws Exception
	 */
	//@RequestMapping("/app/index/indexAffiche")
	@ArgsLog
	public void indexAffiche(IndexEntity entity, HttpServletResponse response) throws Exception {
		entity.setModuletype(1);
		List<Map<String, Object>> AfficheList = vblindexService.indexAffiche();
		WriterJsonUtil.writerJson(response, AfficheList);
	}

	/**
	 * 通过类别获取品牌信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	//@RequestMapping("/app/index/listBrandByClassId")
	@ArgsLog
	public void listBrandByClassId(HttpServletRequest request, HttpServletResponse response)
			throws NumberFormatException, Exception {
		String classId = request.getParameter("classId");
		if (classId != null && !classId.equals("")) {
			List<Map<String, Object>> BrandList = vblindexService.listBrandByClassId(Integer.valueOf(classId));
			WriterJsonUtil.writerJson(response, BrandList);
		} else {
			WriterJsonUtil.writerJson(response, "");
		}
	}

	/**
	 * 列表页
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/listPage")
	@ArgsLog
	public void listPage(HttpServletRequest request, HttpServletResponse response) throws TException {
		String sortFeild = request.getParameter("sortFeild");
		String sortType = request.getParameter("sortType");
		String limit = request.getParameter("limit");
		String start = request.getParameter("start");
		String priceSize = request.getParameter("priceSize");
		String keyword = request.getParameter("keyword");
		String calssid = request.getParameter("class");
		String brandid = request.getParameter("brandid");
		String shopId = request.getParameter("shopId");
		if (sortFeild == null || "".equals(sortFeild) || sortType == null || "".equals(sortType) || limit == null
				|| "".equals(limit) || start == null || "".equals(start) || shopId == null || "".equals(shopId)) {
			WriterJsonUtil.writerJson(response, "0:参数错误");
		}
		System.out.println("keyword++:" + keyword);
		Map<String, String> hmap = new HashMap<String, String>();
		hmap.put("shopId", shopId);
		hmap.put("sortFeild", sortFeild);
		hmap.put("sortType", sortType);
		hmap.put("pageSize", start + "," + limit);
		hmap.put("priceSize", priceSize == null ? "" : priceSize);
		if (keyword != null) {
			hmap.put("keyword", keyword);
		} else if (calssid != null) {
			hmap.put("class", calssid);
		}
		hmap.put("brandid", brandid == null ? "" : brandid);
		IndexRPCClient indexService = new IndexRPCClient();
		List<Map<String, String>> list = indexService.client.listPage(hmap);
		indexService.close();
		WriterJsonUtil.writerJson(response, list);
	}

	/**
	 * 全部商品列表页
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/listPage1")
	@ArgsLog
	public void listPage1(HttpServletRequest request, HttpServletResponse response) throws TException {
		String sortFeild = request.getParameter("sortFeild");
		String sortType = request.getParameter("sortType");
		String limit = request.getParameter("limit");
		String start = request.getParameter("start");
		String priceSize = request.getParameter("priceSize");
		String shopId = request.getParameter("shopId");
		if (sortFeild == null || "".equals(sortFeild) || sortType == null || "".equals(sortType) || limit == null
				|| "".equals(limit) || start == null || "".equals(start) || shopId == null || "".equals(shopId)) {
			WriterJsonUtil.writerJson(response, "0:参数错误");
		}

		Map<String, String> hmap = new HashMap<String, String>();
		hmap.put("shopId", shopId);
		hmap.put("sortFeild", sortFeild);
		hmap.put("sortType", sortType);
		hmap.put("pageSize", start + "," + limit);
		hmap.put("priceSize", priceSize == null ? "" : priceSize);
		IndexRPCClient indexService = new IndexRPCClient();
		List<Map<String, String>> list = indexService.client.listPage1(hmap);
		indexService.close();
		WriterJsonUtil.writerJson(response, list);
	}

	/**
	 * 菜单显示
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/listMune")
	@ArgsLog
	public void listMune(HttpServletRequest request, HttpServletResponse response) throws TException {
		String parentId = request.getParameter("p");

		if (parentId != null) {

			Jedis jedis = RedisBase.getJedis();
			
			if(null == jedis){
				System.err.println("listMune:无法获取 redis 实例");
				WriterJsonUtil.writerJson(response, "");
				return ;
			}

			jedis.select(0);

			String key = "ClassMenu:";

			if (!jedis.exists(key + "0:1")) {
				vblindexService.initMenu2();
			}

			if ("0".equals(parentId)) {
				List<Map<String, String>> menulist = new ArrayList<Map<String, String>>();
				Set<String> keys = jedis.keys(key + "0:*");
				for (String string : keys) {
					menulist.add(jedis.hgetAll(string));
				}
				WriterJsonUtil.writerJson(response, menulist);
			} else {
				List<Map<String, Object>> menulist = new ArrayList<Map<String, Object>>();

				Set<String> keys = jedis.keys(key + parentId + ":*");

				for (String keys1 : keys) {

					Map<String, String> map = jedis.hgetAll(keys1);

					Map<String, Object> newMap = new HashMap<String, Object>();
					for (Map.Entry<String, String> entry : map.entrySet()) {
						newMap.put(entry.getKey(), entry.getValue().toString());
					}
					List<Map<String, String>> nodelist = new ArrayList<Map<String, String>>();
					String classId = map.get("classId");
					Set<String> nextNodes = jedis.keys(key + classId + ":*");

					for (String keys2 : nextNodes) {
						Map<String, String> hgetAll = jedis.hgetAll(keys2);
						if (hgetAll != null && hgetAll.size() > 0) {
							nodelist.add(hgetAll);
						}
					}

					if (nodelist != null && nodelist.size() > 0) {
						newMap.put("nodes", nodelist);
					}
					if (newMap != null && newMap.size() > 0) {
						menulist.add(newMap);
					}
				}
				WriterJsonUtil.writerJson(response, menulist);
			}
			jedis.close();
		} else {
			WriterJsonUtil.writerJson(response, "");
		}

	}

	/**
	 * 菜单显示
	 * 
	 * @param request
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/newMune")
	@ArgsLog
	public void newMune(HttpServletRequest request, HttpServletResponse response) throws TException {
		String parentId = request.getParameter("p");
		String shopId = request.getParameter("s");

		if (parentId != null && shopId != null) {

			Jedis jedis = RedisBase.getJedis();
			
			if(null == jedis){
				System.err.println("newMune:无法获取 redis 实例");
				WriterJsonUtil.writerJson(response, "");
				return ;
			}

			jedis.select(Integer.valueOf(shopId));

			String key = "ClassMenu:";

			// 第一次请求
			if ("0".equals(parentId)) {
				List<Map<String, String>> menulist = new ArrayList<Map<String, String>>();
				Set<String> keys = jedis.keys(key + "0:*");
				
				if(keys == null || keys.size()<1){
					vblindexService.initMenu();
				}
				
				for (String string : keys) {
					menulist.add(jedis.hgetAll(string));
				}
				WriterJsonUtil.writerJson(response, menulist);
			} else {
				// 第二次请求
				
				List<Map<String, Object>> menulist = new ArrayList<Map<String, Object>>();
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

						Set<String> nextNodes = jedis.keys(key + 0 + ":"+classId);

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
				WriterJsonUtil.writerJson(response, menulist);
			}
			jedis.close();
		} else {
			WriterJsonUtil.writerJson(response, "");
		}
	}

	/**
	* @Name: 通过区域ID获取所有门店信息
	* @Author: nick
	*/
	//@RequestMapping("/app/index/getShopsByArea")
	@ArgsLog
	public void getShopsByArea(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String areaId = request.getParameter("areaId");
		String cityId = request.getParameter("cityId");
		if (areaId != null) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("cityId", cityId);
			map.put("areaId", areaId);
			List<Map<String, Object>> list = vblindexService.ShopsByArea(map);
			WriterJsonUtil.writerJson(response, list);
		} else {
			WriterJsonUtil.writerJson(response, "");
		}
	}

	/**
	 * 图片映射地址
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 */
	//@RequestMapping("/app/index/urlName")
	@ArgsLog
	public void url(UserEntity user, HttpServletResponse response) throws TException {

		Map<String, String> map = new HashMap<String, String>();
		String imgurl = SysContext.CONFIGMAP.get("imgurl");
		map.put("imgurl", imgurl);
		String adsImgSavePath = SysContext.CONFIGMAP.get("adsImgSavePath");

		map.put("adsImgSavePath", adsImgSavePath
				.substring(adsImgSavePath.substring(0, adsImgSavePath.length() - 1).lastIndexOf("/") + 1));
		String appraiseSavePath = SysContext.CONFIGMAP.get("appraiseSavePath");
		map.put("appraiseSavePath", appraiseSavePath
				.substring(appraiseSavePath.substring(0, appraiseSavePath.length() - 1).lastIndexOf("/") + 1));
		String goodsclassImg = SysContext.CONFIGMAP.get("goodsclassImg");
		map.put("goodsclassImg",
				goodsclassImg.substring(goodsclassImg.substring(0, goodsclassImg.length() - 1).lastIndexOf("/") + 1));
		String goodsdetailsimg = SysContext.CONFIGMAP.get("goodsdetailsimg");
		map.put("goodsdetailsimg", goodsdetailsimg
				.substring(goodsdetailsimg.substring(0, goodsdetailsimg.length() - 1).lastIndexOf("/") + 1));
		String goodsImgSavePath = SysContext.CONFIGMAP.get("goodsImgSavePath");
		map.put("goodsImgSavePath", goodsImgSavePath
				.substring(goodsImgSavePath.substring(0, goodsImgSavePath.length() - 1).lastIndexOf("/") + 1));
		WriterJsonUtil.writerJson(response, map);
	}
}
