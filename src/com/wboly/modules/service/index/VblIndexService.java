package com.wboly.modules.service.index;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.wboly.modules.dao.index.VblIndexMapper;
import com.wboly.modules.entity.area.AllAreaEntity;
import com.wboly.modules.entity.area.AreaEntity;
import com.wboly.rpc.entity.IndexEntity;
import com.wboly.system.sys.cache.RedisBase;
import com.wboly.system.sys.util.JsonUtil;

import redis.clients.jedis.Jedis;

/**
 * @Name: 首页信息.java
 * @Author: nick
 */
@Service
public class VblIndexService {

	@Autowired
	private VblIndexMapper vblindexMapper;
	
	/**
	* @Name:  查询所有门店的有效地址   
	* @Author: nick
	*/
	@SuppressWarnings({"rawtypes" })
	public  String  selectAllShopAddress(){
		
		Jedis jedis = null;
		try {
			jedis = RedisBase.getJedis();
			
			if(null == jedis){
				return null;
			}
			
			String result = jedis.get("allAddress");
			
			List cache = (List)JSON.parse(result);
			if(cache != null && cache.size()>0){
				
				return JsonUtil.ObjectToJson(cache);
			}else{
				List<AllAreaEntity> allAddress = vblindexMapper.selectAllProvinceAddress();
				
				for(AllAreaEntity p : allAddress){
					
					List<AllAreaEntity> selectAllCityByProvinceId = vblindexMapper.selectAllCityAddress(p.getProvinceId());
					
					for(AllAreaEntity c : selectAllCityByProvinceId){
						
						if(p.getProvinceId() == c.getProvinceId()){
							
							List<AllAreaEntity> selectAllAreaByCityId = vblindexMapper.selectAllAreaAddress(c.getCityId());
							
							for(AllAreaEntity a : selectAllAreaByCityId){
								
								if(c.getCityId() == a.getCityId()){
									List<AllAreaEntity> selectAllStreetAddress = vblindexMapper.selectAllStreetAddress(a.getAreaId());
									
									for(AllAreaEntity s : selectAllStreetAddress){
										
										if(a.getAreaId() == s.getAreaId()){
											a.setListNodes(selectAllStreetAddress);
										}
									}
									c.setListNodes(selectAllAreaByCityId);
								}
							}
							p.setListNodes(selectAllCityByProvinceId);
						}
					}
				}
				jedis.set("allAddress", allAddress!=null ? JsonUtil.ObjectToJson(allAddress):"[]");
				return JsonUtil.ObjectToJson(allAddress);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(jedis != null){
				jedis.close();
			}
		}
		return null;
	}
	
	/**
	 * @throws Exception
	 * @Name: 查询所有门店的区域信息
	 * @Author: nick
	 */
	public List<AreaEntity> selectHaveAreaShop() throws Exception {
		
		List<AreaEntity> selectAllProvince = vblindexMapper.selectAllProvince();
		
		for(AreaEntity p : selectAllProvince){
			List<AreaEntity> selectAllCityByProvinceId = vblindexMapper.selectAllCityByProvinceId(p.getProvinceId());
			
			for(AreaEntity c : selectAllCityByProvinceId){
				if(p.getProvinceId() == c.getProvinceId()){
					List<AreaEntity> selectAllAreaByCityId = vblindexMapper.selectAllAreaByCityId(c.getCityId());
					
					for(AreaEntity a : selectAllAreaByCityId){
						if(c.getCityId() == a.getCityId()){
							c.setListNodes(selectAllAreaByCityId);
						}
					}
					p.setListNodes(selectAllCityByProvinceId);
				}
			}
		}
		return selectAllProvince;
	}
	
	/**
	* @Name:  根据区域查询所有门店信息  
	* @Author: nick
	*/
	public List<AreaEntity> selectAllShopByArea(Map<String,String> map) throws Exception{
		return vblindexMapper.selectAllShopByArea(map);
	}

	/**
	 * @throws Exception
	 * @Name: 根据区域查询门店
	 * @Author: nick
	 */
	public List<Map<String, Object>> ShopsByArea(Map<String, String> map) throws Exception {
		return vblindexMapper.selectAllShopInfoByArea(map);
	}

	/**
	 * @Name: 根据类目编号获取品牌信息
	 * @Author: nick
	 */
	public List<Map<String, Object>> listBrandByClassId(Integer classId) throws Exception {
		return vblindexMapper.selectBrandInfoByClassId(classId);
	}
	
	/**
	 * @Name: 首页公告
	 * @Author: nick
	 */
	public List<Map<String, Object>> indexAffiche() throws Exception {
		return vblindexMapper.selectAfficheInfo();
	}

	/**
	 * @Name: 首页广告
	 * @Author: nick
	 */
	public List<Map<String, Object>> indexAds(IndexEntity entity) throws Exception {
		return vblindexMapper.selectAdvertInfo(entity);
	}

	/**
	 * @Name: 首页商品
	 * @Author: nick
	 */
	@SuppressWarnings("finally")
	public List<Map<String, String>> appSwitchGoods(IndexEntity entity) {
		List<Map<String, String>> swtichGoodsList = new ArrayList<Map<String, String>>();

		try {
			List<Map<String, Object>> list = vblindexMapper.selectHomePageInfo(entity);
			List<Integer> lp = new ArrayList<Integer>();
			Map<String, Object> mp = new HashMap<String, Object>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				Map<String, String> newMap = new HashMap<String, String>();
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					newMap.put(entry.getKey(), entry.getValue().toString());
					if (entry.getKey().equals("activityId")) {
						lp.add(Integer.valueOf(entry.getValue().toString()));
					}
				}
				swtichGoodsList.add(i, newMap);
			}

			if (lp.size() > 0) {
				mp.put("ids", lp);
				List<Map<String, Object>> list1 = vblindexMapper.selectOnLineInfo(mp);
				for (int i = 0; i < swtichGoodsList.size(); i++) {
					Map<String, String> newMap = swtichGoodsList.get(i);
					String activityId = newMap.get("activityId");
					for (int j = 0; j < list1.size(); j++) {
						Map<String, Object> map = list1.get(j);
						if (map.get("activityId").toString().equals(activityId)) {
							for (Map.Entry<String, Object> entry : map.entrySet()) {
								newMap.put(entry.getKey(), entry.getValue()!=null?entry.getValue().toString():"");
							}
							swtichGoodsList.remove(i);
							swtichGoodsList.add(i, newMap);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return swtichGoodsList;
		}
	}

	/**
	 * @Name: 缓存所有活动
	 * @Author: nick
	 */
	public void initIndexAll() {
		Jedis jedis =  null;
		try {
			List<IndexEntity> list = vblindexMapper.selectActivityCount();

			jedis =  RedisBase.getJedis();
			
			if(jedis != null){

				for (int j = 0; j < list.size(); j++) {
					
					Integer id = list.get(j).getShopId();
	
					List<Map<String, Object>> ActivityList = vblindexMapper.selectAllActivityInfo(id);
	
					jedis.select(id);
					String key1 = "allActivitys:";
					for (int i = 0; i < ActivityList.size(); i++) {
						Map<String, Object> activity = (Map<String, Object>) ActivityList.get(i);
	
						Map<byte[], byte[]> goodsindex = new HashMap<byte[], byte[]>();
	
						for (Map.Entry<String, Object> entry : activity.entrySet()) {
	
							goodsindex.put(String.valueOf(entry.getKey()).getBytes("UTF-8"), String.valueOf(entry.getValue()).getBytes("UTF-8"));
	
							String key = key1 + activity.get("activityId").toString();
							jedis.hmset(key.getBytes("UTF-8"), goodsindex);
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(jedis != null){
				jedis.close();
			}
		}
	}

	/**
	 * @Name: 初始化商品类目信息
	 * @Author: nick
	 */
	public void initMenu() {
		Jedis jedis = null;
		try {

			jedis = RedisBase.getJedis();
			
			if(null != jedis){
				List<Integer> shopCount = vblindexMapper.selectShopCount();

				for (int i = 0; i < shopCount.size(); i++) {

					jedis.select(shopCount.get(i));

					String script = "";
					script = "local t = redis.call('keys', 'ClassMenu*') if #t ~= 0 then redis.call('del', unpack(redis.call('keys', 'ClassMenu*')))  end";
					String code = jedis.scriptLoad(script);
					jedis.evalsha(code);

					Map<String,Object> map =new HashMap<>();
					
					map.put("shopId", shopCount.get(i));
					
					List<Map<String, Object>> alllist = vblindexMapper.selectGoodsClassInfo(map);

					Map<String, Map<String, String>> allMenu = new HashMap<String, Map<String, String>>();

					for (Map<String, Object> m : alllist) {
						Map<String, String> newMap = new HashMap<String, String>();
						for (Map.Entry<String, Object> entry : m.entrySet()) {
							newMap.put(entry.getKey(), entry.getValue().toString());
						}
						allMenu.put(m.get("parentId").toString() + ":" + m.get("classId").toString(), newMap);
					}
					String key = "ClassMenu:";
					String key1 = "";
					for (int j = 0; j < alllist.size(); j++) {
						Map<String, Object> p1 = alllist.get(j);
						key1 = p1.get("parentId").toString() + ":" + p1.get("classId").toString();
						jedis.hmset(key + key1, allMenu.get(key1));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != jedis){
				jedis.close();
			}
		}
	}

	/**
	 * @Name: 初始化商品类目信息 --beifen
	 * @Author: nick
	 */
	public void initMenu2() {
		Jedis jedis = null;
		try {

			jedis = RedisBase.getJedis();
			
			if(null != jedis){
				jedis.select(0);

				String script = "";
				script = "local t = redis.call('keys', 'ClassMenu*') if #t ~= 0 then redis.call('del', unpack(redis.call('keys', 'ClassMenu*')))  end";
				String code = jedis.scriptLoad(script);
				jedis.evalsha(code);

				Map<String,Object> map =new HashMap<>();
				
				map.put("shopId", 999);
				
				List<Map<String, Object>> alllist = vblindexMapper.selectGoodsClassInfo(map);

				Map<String, Map<String, String>> allMenu = new HashMap<String, Map<String, String>>();

				for (Map<String, Object> m : alllist) {
					Map<String, String> newMap = new HashMap<String, String>();
					for (Map.Entry<String, Object> entry : m.entrySet()) {
						newMap.put(entry.getKey(), entry.getValue().toString());
					}
					allMenu.put(m.get("parentId").toString() + ":" + m.get("sort").toString(), newMap);
				}

				String key = "ClassMenu:";
				String key1 = "";
				for (int j = 0; j < alllist.size(); j++) {
					Map<String, Object> p1 = alllist.get(j);
					key1 = p1.get("parentId").toString() + ":" + p1.get("sort").toString();
					jedis.hmset(key + key1, allMenu.get(key1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != jedis){
				jedis.close();
			}
		}
	}

	/**
	 * @Name: 初始化活动信息
	 * @Author: nick
	 */
	public void initIndex() {
		
		Jedis jedis = null;

		try {

			jedis = RedisBase.getJedis();
			
			if(null != jedis){
				List<IndexEntity> list = vblindexMapper.selectActivityCount();
				
				for (int j = 0; j < list.size(); j++) {
					Integer id = list.get(j).getShopId();

					List<Map<String, Object>> ActivityList = vblindexMapper.selectActivityInfo(id);
					jedis.select(id);
					// jedis.flushDB();
					String key1 = "activity:";
					for (int i = 0; i < ActivityList.size(); i++) {
						Map<String, Object> activity = (Map<String, Object>) ActivityList.get(i);

						Map<byte[], byte[]> goodsindex = new HashMap<byte[], byte[]>();

						String keys = "complex:" + activity.get("goodsId").toString() + "-"
								+ activity.get("skuId").toString();
						Map<String, Double> brandindex = new HashMap<String, Double>();
						Map<String, Double> priceSizeindex = new HashMap<String, Double>();
						for (Map.Entry<String, Object> entry : activity.entrySet()) {

							goodsindex.put(String.valueOf(entry.getKey()).getBytes("UTF-8"),
									String.valueOf(entry.getValue()).getBytes("UTF-8"));

							if (entry.getKey().toString().equals("brandId")) {
								double value = (double) Integer.valueOf(entry.getValue().toString());
								brandindex.put(activity.get("activityId").toString(), value);
							}
							if (entry.getKey().toString().equals("retailPrice")) {
								double value = (double) Integer.valueOf(entry.getValue().toString());
								priceSizeindex.put(activity.get("activityId").toString(), value);
							}
							String key = key1 + activity.get("activityId").toString();

							jedis.hmset(key.getBytes("UTF-8"), goodsindex);
							jedis.set(keys, activity.get("activityId").toString());
						}
						jedis.lpush("index:glistindex", activity.get("activityId").toString());
						jedis.zadd("index:brandindex", brandindex);
						jedis.zadd("index:priceSizeindex", priceSizeindex);
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(null != jedis){
				jedis.close();
			}
		}
	}
	
	/**
	 * @Name:  查询门店数量 
	 * @Author: nick
	 */
	public List<Integer> selectShopCount(){
		return vblindexMapper.selectShopCount();
	}
}
