package com.wboly.modules.service.courier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.wboly.modules.dao.courier.VblCourierMapper;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.cache.RedisBase;

import redis.clients.jedis.Jedis;

/**
* @Name: 快递员.java
* @Author: nick
*/
@Service
public class VblCourierService {
	
	@Autowired
	private VblCourierMapper vblcourierMapper;
	
	/**
	 * 快递员订单列表接口
	 * @param Map<String,String>
	 * @return list<VblConfigEntity>	
	 * @throws Exception
	 */
	public  List<Map<String,Object>> CourierOrders(Map<String,String> map)throws Exception{
		return this.vblcourierMapper.CourierOrders(map);
	}
	
	/**
	* @Name: 当前用户是否是快递员
	* @Author: nick
	*/
	@SuppressWarnings("finally")
	public int isCourier(Map<String,String> map){
		int i = 0;
		try {
			i = vblcourierMapper.VerifyCourierInfo(map);
			if(i==0){
				i = vblcourierMapper.VerifyUserIsWhat(map);
				if(i==0){
					i =-1;
				}
			}else{
				i=0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			return i;
		}
	}
	
	/**
	* @Name: 快递信息接口
	* @Author: nick
	*/
	public List<Map<String,Object>> OrderCourierSite(Map<String,String> map)throws Exception{
		return vblcourierMapper.OrderCourierSite(map);
	}
	
	/**
	* @Name: 快递员信息列表
	* @Author: nick
	*/
	@SuppressWarnings("finally")
	public Map<String,Object> Couriers(Map<String,Object> map) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		try {
			Map<String,Object> hmap = vblcourierMapper.selectCourierInfo(map);
			if(hmap.get("result")==null){
				for (Map.Entry<String, Object> entry : hmap.entrySet()) {			     
					resultMap.put(entry.getKey(),  entry.getValue().toString());			      
				}
				
				hmap = vblcourierMapper.selectShopInfo(resultMap);
				if(hmap.get("result")==null){
						for (Map.Entry<String, Object> entry : hmap.entrySet()) {			     
							resultMap.put(entry.getKey(),  entry.getValue().toString());			      
						}					
						hmap = vblcourierMapper.selectLeagueInfo(resultMap);
						for (Map.Entry<String, Object> entry : hmap.entrySet()) {			     
							resultMap.put(entry.getKey(),  entry.getValue().toString());			      
						}
				}else{
					resultMap.put("shopName", "");
					resultMap.put("shopPhone", "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			return resultMap;
		}
	}
	
	/**
	* @Name: 门店订单列表
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopOrders(OrderEntity entity)throws Exception{
		return vblcourierMapper.ShopOrders(entity);
	}
	
	/**
	* @Name: 门店订单列表 
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopOrdersInventory(OrderEntity entity)throws Exception{
		return vblcourierMapper.ShopOrdersInventory(entity);
	}
	
	/**
	* @Name: 门店快递员列表
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopCouriers(OrderEntity entity)throws Exception{
		return vblcourierMapper.ShopCouriers(entity);
	}
	
	/**
	 * @Name: 门店信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> Shops(OrderEntity entity)throws Exception{
		return vblcourierMapper.Shops(entity);
	}
	
	/**
	 * @Name: 查询门店可配送的区域
	 * @Author: nick
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "finally" })
	public List<Map<String,String>> ShopCanBeDelivery(OrderEntity entity) {
		
		List<Map<String, String>> resultlist = new ArrayList<Map<String, String>>();
		
		Jedis jedis = null;
		
		try {
			Gson gson = new Gson();
			
			jedis = RedisBase.getJedis();
			
			if(null != jedis){
				jedis.select(entity.getShopId());
				
				String result = jedis.get("CanBeDeliveryArea:"+entity.getDateline());
				
				List cache = (List)JSON.parse(result);
				
				if(cache != null && cache.size()>0){
					
					for (int i = 0; i < cache.size(); i++) {
						Map<String, String> map = (Map<String, String>) cache.get(i);
						resultlist.add(map);
					}
				}else{
					List<Map<String, Object>> list = vblcourierMapper.ShopCanBeDelivery(entity);
					
					for (int i = 0; i < list.size(); i++) {
						Map<String, Object> map = list.get(i);
						Map<String,String> newMap =new HashMap<String,String>();
						for (Map.Entry<String, Object> entry : map.entrySet()) {			     
						      newMap.put(entry.getKey(), entry.getValue().toString());			      
						}
						resultlist.add(newMap);
					}
					jedis.set("CanBeDeliveryArea:"+entity.getDateline(), resultlist != null ? gson.toJson(resultlist) : "[]");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(null != jedis){
				jedis.close();
			}
			return resultlist;
		}
	}
}
