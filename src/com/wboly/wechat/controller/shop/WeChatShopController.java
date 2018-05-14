package com.wboly.wechat.controller.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.promeg.pinyinhelper.Pinyin;
import com.wboly.modules.service.courier.VblCourierService;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.SessionUtil;
import com.wboly.system.sys.util.WriterJsonUtil;
import com.wboly.wechat.service.shop.WeChatShopService;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 门店.java
 * @Author: nick
 */
@Controller
public class WeChatShopController extends SysController {

	@Autowired
	private WeChatShopService weChatShopService;

	@Autowired
	private VblCourierService vblcourierService;

	/**
	 * @throws IOException 
	 * @Name: 所有的有效区域地址
	 * @throws Exception
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/shop/effectiveArea", params = { "level" })
	public void allShopAddress(HttpServletResponse response, HttpServletRequest request, @RequestParam("level") String level, @RequestParam("id") String id) throws IOException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		TreeSet<String> noReapted = new TreeSet<String>();// 带有String类型的TreeSet泛型(TreeSet泛型能保证重复的不加入 , 而且有序)
		if (level.equals("0")) {
			System.out.println(id);
			// 获取所有省份信息
			String provinceResult = OkhttpUtils.get(SysContext.AREAURL + "/are/area/provinceall");
			System.err.println("获取所有省份的返回值为：" + provinceResult);
			List<Map<String, Object>> provinceList = JsonUtil.listMaps(provinceResult);
			for (int i = 0; i < provinceList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				char[] ca = String.valueOf(provinceList.get(i).get("areaName")).toCharArray();
				noReapted.add("" + Pinyin.toPinyin(ca[0]).substring(0, 1));
				map.put("title", provinceList.get(i).get("areaCode"));
				map.put("name", provinceList.get(i).get("areaName"));
				map.put("level", 1);// （省=1、市=2、区域=3、街道=4）
				map.put("id", provinceList.get(i).get("areaCode"));
				map.put("flag", Pinyin.toPinyin(ca[0]).substring(0, 1));
				list.add(map);
			}
		} else if (level.equals("1")) {
			System.out.println(id);
			String[] split1 = id.split(",");
			// 根据上级编号获取下级信息
			String LowerAreaResult = OkhttpUtils.get(SysContext.AREAURL + "/are/area/lowerareaall?areaCode=" + split1[0]);
			System.err.println("获取所有下级信息的返回值为：" + LowerAreaResult);
			List<Map<String, Object>> LowerAreaList = JsonUtil.listMaps(LowerAreaResult);
			for (int i = 0; i < LowerAreaList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				char[] ca = String.valueOf(LowerAreaList.get(i).get("areaName")).toCharArray();
				noReapted.add("" + Pinyin.toPinyin(ca[0]).substring(0, 1));
				map.put("title", LowerAreaList.get(i).get("areaCode"));
				map.put("name", LowerAreaList.get(i).get("areaName"));
				map.put("level", 2);// （省=1、市=2、区域=3、街道=4）
				map.put("id", split1[0] + "," + LowerAreaList.get(i).get("areaCode"));
				map.put("flag", Pinyin.toPinyin(ca[0]).substring(0, 1));
				list.add(map);
			}
		} else if (level.equals("2")) {
			System.out.println(id);
			String[] split1 = id.split(",");
			String areaId = "";
			if (split1.length == 2) {
				areaId = split1[1];
			} else {
				areaId = id;
			}
			// 根据上级编号获取下级信息
			String LowerAreaResult = OkhttpUtils.get(SysContext.AREAURL + "/are/area/lowerareaall?areaCode=" + areaId);
			if (LowerAreaResult == null || LowerAreaResult.equals("") || LowerAreaResult.equals("null") || LowerAreaResult.equals("[]")) {
				LowerAreaResult = OkhttpUtils.get(SysContext.AREAURL + "/are/area/countyall?areaCode=" + split1[1]);
			}
			System.err.println("获取所有下级信息的返回值为：" + LowerAreaResult);
			List<Map<String, Object>> LowerAreaList = JsonUtil.listMaps(LowerAreaResult);
			for (int i = 0; i < LowerAreaList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				char[] ca = String.valueOf(LowerAreaList.get(i).get("areaName")).toCharArray();
				noReapted.add("" + Pinyin.toPinyin(ca[0]).substring(0, 1));
				map.put("title", LowerAreaList.get(i).get("areaCode"));
				map.put("name", LowerAreaList.get(i).get("areaName"));
				map.put("level", 3);// （省=1、市=2、区域=3、街道=4）
				map.put("id", id + "," + LowerAreaList.get(i).get("areaCode"));
				map.put("flag", Pinyin.toPinyin(ca[0]).substring(0, 1));
				list.add(map);
			}
		} else if (level.equals("3")) {
			System.out.println(id);
			String[] split1 = id.split(",");
			String areaId = "";
			if (split1.length == 3) {
				areaId = split1[2];
			} else {
				areaId = split1[1];
			}
			// 根据上级编号获取下级信息
			String LowerAreaResult = OkhttpUtils.get(SysContext.AREAURL + "/are/area/lowerareaall?areaCode=" + areaId);
			System.err.println("获取所有下级信息的返回值为：" + LowerAreaResult);
			List<Map<String, Object>> LowerAreaList = JsonUtil.listMaps(LowerAreaResult);
			for (int i = 0; i < LowerAreaList.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				char[] ca = String.valueOf(LowerAreaList.get(i).get("areaName")).toCharArray();
				noReapted.add("" + Pinyin.toPinyin(ca[0]).substring(0, 1));
				map.put("title", LowerAreaList.get(i).get("areaCode"));
				map.put("name", LowerAreaList.get(i).get("areaName"));
				map.put("level", 4);// （省=1、市=2、区域=3、街道=4）
				map.put("id", id + "," + LowerAreaList.get(i).get("areaCode"));
				map.put("flag", Pinyin.toPinyin(ca[0]).substring(0, 1));
				list.add(map);
			}
		}
		this.render(response, "{\"sort\":" + JsonUtil.ObjectToJson(noReapted) + ",\"message\":" + JsonUtil.ObjectToJson(list) + ",\"flag\":true}");
	}

	public static void main(String[] args) {
		String re = HttpUtil
				.getUrl("http://api.map.baidu.com/location/ip?ak=GY7epABzpfsPo2d2Pm6SVBWIstGtHtyo&coor=bd09ll");

		System.err.println("百度地图返回参数:" + re);

		Map<String, Object> m = JsonUtil.jsonStringToMap(re);

		m = JsonUtil.jsonStringToMap(m.get("content").toString());
		m = JsonUtil.jsonStringToMap(m.get("point").toString());

		System.err.println("百度地图返回的经纬度:" + m);
	}

	/**
	 * @Name: 获取最近的门店信息
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/shop/latelyshop", params = { "lng", "lat" })
	public void getLatelyShop(HttpServletRequest request, HttpServletResponse response) {
		String lng = request.getParameter("lng");// 经度
		String lat = request.getParameter("lat");// 纬度
		if (lng == null || lat == null || lng.equals("") || lat.equals("") || lng.equals("undefined")
				|| lat.equals("undefined") || lng.equals("null") || lat.equals("null")) {
			this.render(response, "{\"message\":\"地理位置参数有误\",\"flag\":false}");
		}

		Map<String, Object> map = new HashMap<String, Object>();

		// 后台经度纬度url
		// String url = "http://192.168.1.202:8081/wbolymanage/Frnchisee/ShopCoord/getShopIdByPonits";
		String url = "http://center.wboly.com/wbolymanage/Frnchisee/ShopCoord/getShopIdByPonits";

		map.put("lng", lng);
		map.put("lat", lat);

		System.err.println("H5的经纬度:" + map);

		String result = HttpUtil.postUrl(url, map);
		//String result = "{\"areaId\":\"4\",\"lng\":108.291489,\"shopName\":\"田东店\",\"shopId\":\"6\",\"class\":\"classcom.wboly.modules.entity.Franchisee.ShopCoord.VblShopCoordInfo\",\"lat\":22.885085}";

		System.err.println("调后台返回:" + result);

		if (result != null && !result.equals("") && !result.equals("null")) {
			String shopId = JsonUtil.getJSONValue(result, "shopId");
			String userId = SysCache.getWeChatUserByColumn(request, "userId");
			String lastShopId = "";
			if (userId != null && !userId.equals("") && !"null".equals(userId)) {
				// 获取上次进入的门店
				String shopdatas = SysCache.getUserAccessShopData(userId);
				if (shopdatas != null && !shopdatas.equals("") && !shopdatas.equals("null")) {
					lastShopId = JsonUtil.getJSONValue(shopdatas, "shopId");
				}
				String shopdata = "{\"shopName\":\"" + JsonUtil.getJSONValue(result, "shopName") + "\",\"shopId\":\"" + shopId + "\"}";
				// 先清除上次进入的门店
				SysCache.delUserAccessShopData(userId);
				// 缓存当前进入的门店
				SysCache.setUserAccessShopData(userId, shopdata);
			}

			if (shopId == null || shopId.equals("null") || shopId.equals("") || shopId.equals("undefined")) {
				this.render(response, "{\"message\":null,\"flag\":true,\"idx\":2}");
				return;
			}

			map.clear();
			map.put("shopId", shopId);

			// 根据 门店编号 获取门店信息
			List<Map<String, Object>> data = weChatShopService.selectAllShop(map);
			if (data.size() > 0) {
				if (!shopId.equals(lastShopId)) {
					this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(data.get(0)) + ",\"flag\":true,\"idx\":1}");
					return;
				}
				this.render(response, "{\"message\":" + JsonUtil.ObjectToJson(data.get(0)) + ",\"flag\":true,\"idx\":2}");
				return;
			}
		}
		this.render(response, "{\"message\":null,\"flag\":true,\"idx\":2}");
	}

	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static double Distance(double long1, double lat1, double long2, double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径 6378.137(km) 转 m
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		// 2 *
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		return d;
	}

	/**
	 * @Name: 验证用户当前的默认地址是否是该门店的可配送地址
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/shop/verifyArea")
	public void verifyCanbeDelivery(HttpServletRequest request, HttpServletResponse response) {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		Object shopId = SessionUtil.getShopByColumn(request, "shopId");
		if (shopId == null) {
			this.render(response, "{\"message\":\"请选择门店\",\"flag\":false}");
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("userId", userId);
		map.put("shopId", shopId);
		map.put("isDefault", 1);

		List<Map<String, Object>> canbe = weChatShopService.selectCanbeDelivery(map);
		if (canbe.size() > 0) {
			this.render(response, "{\"message\":\"门店可配送\",\"flag\":true}");
			return;
		}
		this.render(response, "{\"message\":\"超出配送范围\",\"flag\":false}");
	}

	/**
	 * @Name: 门店可配送区域地址
	 * @throws Exception
	 * @Author: knick
	 */
	@RequestMapping(value = "/wechat/shop/getCanbeDeliveryArea")
	public void getCanbeDeliveryArea(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId.equals("")) {
			this.render(response, "{\"message\":\"您没有登陆\",\"flag\":false}");
			return;
		}

		Object shopId = SessionUtil.getShopByColumn(request, "shopId");
		if (shopId == null) {
			this.render(response, "{\"message\":\"请选择门店\",\"flag\":false}");
			return;
		}
		Object areaId = SessionUtil.getShopByColumn(request, "areaId");

		OrderEntity entity = new OrderEntity();

		entity.setShopId(Integer.valueOf(shopId.toString()));
		entity.setDateline(Integer.valueOf(areaId.toString()));// 区域编号

		List<Map<String, String>> list = vblcourierService.ShopCanBeDelivery(entity);

		List<Map<String, String>> lists = new ArrayList<Map<String, String>>();

		TreeSet<String> noReapted = new TreeSet<String>();// 带有String类型的TreeSet泛型(TreeSet泛型能保证重复的不加入 , 而且有序)

		for (Map<String, String> map : list) {
			char[] charArray = map.get("streetName").toCharArray();
			noReapted.add("" + Pinyin.toPinyin(charArray[0]).substring(0, 1));
			map.put("title", map.get("streetId"));
			map.put("name", map.get("streetName"));
			map.put("level", "0");// （省=1、市=2、区域=3、街道=4）
			map.put("id", map.get("provinceId") + "," + map.get("cityId") + "," + map.get("areaId") + ","
					+ map.get("streetId"));
			map.put("allname", map.get("provinceName") + "," + map.get("cityName") + "," + map.get("areaName") + ","
					+ map.get("streetName"));
			map.put("flag", Pinyin.toPinyin(charArray[0]).substring(0, 1));
			lists.add(map);
		}

		this.render(response, "{\"sort\":" + JsonUtil.ObjectToJson(noReapted) + ",\"message\":"
				+ JsonUtil.ObjectToJson(lists) + ",\"flag\":true}");
	}

	/**
	 * @Name: 保存门店信息到Session
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/shop/saveInSeesion")
	public void setShopInSession(HttpServletRequest request, HttpServletResponse response) {

		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		String shopId = request.getParameter("shopId");
		String shopName = request.getParameter("shopName");
		String shopdata = "{\"shopName\":\"" + shopName + "\",\"shopId\":\"" + 1 + "\"}";
		SysCache.setUserAccessShopData(userId, shopdata);
		Map<String, Object> map = new HashMap<String, Object>();

		map.put("shopId", (shopId != null && !shopId.equals("") && !shopId.equals("undefined")) ? shopId : 0);

		List<Map<String, Object>> selectAllShop = weChatShopService.selectAllShop(map);

		request.getSession().setAttribute("ASD",
				selectAllShop != null && selectAllShop.size() > 0 ? selectAllShop.get(0) : null);
		WriterJsonUtil.writerJson(response, "");
	}

	/**
	 * @Name: 从Session中获取门店信息
	 * @Author: nick
	 * @throws Exception
	 */
	@RequestMapping(value = "/wechat/shop/getShopByRequest")
	public void getShopBySession(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Object shopByColumns = SessionUtil.getShopData(request);

		if (shopByColumns != null) {
			WriterJsonUtil.writerJson(response, shopByColumns, "UTF-8");
		} else {
			WriterJsonUtil.writerJson(response, retuenShop(), "UTF-8");
		}
	}
}
