package com.wboly.wechat.controller.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.promeg.pinyinhelper.Pinyin;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.SessionUtil;
import com.wboly.system.sys.util.WriterJsonUtil;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 门店.java
 * @Author: nick
 */
@Controller
public class WeChatShopController extends SysController {

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
