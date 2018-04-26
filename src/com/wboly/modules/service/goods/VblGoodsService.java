package com.wboly.modules.service.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.modules.dao.goods.VblGoodsMapper;
import com.wboly.rpc.entity.GoodsEntity;
import com.wboly.system.sys.cache.RedisBase;

import redis.clients.jedis.Jedis;

/**
* @Name: 商品信息.java
* @Author: nick
*/
@Service
public class VblGoodsService {
	
	@Autowired
	private VblGoodsMapper vblgoodsMapper;
	
	/**
	 * @Name: 根据多个参数查询已上线活动信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectActivityByParm(Map<String,Object> hmap)throws Exception{
		return vblgoodsMapper.selectActivityByParm(hmap);
	}
	
	/**
	* @Name: 详情页商品基本信息
	* @Author: nick
	*/
	@SuppressWarnings("finally")
	public Map<String,String> findGoodsBase(Map<String,String> hmap) {
		Map<String,String> resultMap = new HashMap<String,String>();
		Jedis jedis =  null;
		try{
			jedis =  RedisBase.getJedis();
			
			if(jedis != null){
				String webDescribe = jedis.get("webDescribe:"+hmap.get("goodsId"));
				resultMap.put("deteils", webDescribe==null?"":webDescribe);
				String imgs = jedis.get("goodsImgs:"+hmap.get("goodsId"));
				String goodsInfo = jedis.get("goodsInfo:"+hmap.get("goodsId"));			
				resultMap.put("imgs", imgs==null?"":imgs);
				resultMap.put("specification", goodsInfo==null?"":goodsInfo);
				int activityId = Integer.valueOf(hmap.get("activityId"));
				if(activityId==0 && hmap.get("supplierUid") != null){
					Object obj = vblgoodsMapper.findGoodsBase(hmap);
					 activityId = (int) (obj!=null?obj:0);
				}
				jedis.select(Integer.valueOf(hmap.get("shopId")));
				Map<byte[],byte[]> m = jedis.hgetAll(("allActivitys:"+activityId).getBytes("UTF-8"));
				for (Map.Entry<byte[],byte[]> entry : m.entrySet()) {			     
					resultMap.put(new String(entry.getKey(), "UTF-8"),  new String(entry.getValue(), "UTF-8"));			      
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null != jedis){
				jedis.close();
			}
			return resultMap;
		}
	}
	
	/**
	* @Name: 根据商品编号获取商品详情图片
	* @Author: nick
	*/
	@SuppressWarnings("finally")
	public Map<String,String> getGoodsDeteilById(GoodsEntity entity) {
		Map<String,String> resultMap = new HashMap<String,String>();
		Jedis jedis = null;
		try{
			jedis =  RedisBase.getJedis();
			if(null != jedis){
				String webDescribe = jedis.get("webDescribe:"+entity.getGoodsId());
				resultMap.put("deteils", webDescribe==null?"":webDescribe);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null != jedis){
				jedis.close();
			}
			return resultMap;
		}
	}
	
	/**
	 * @Name: 商品sku信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> goodsSKU(GoodsEntity entity)throws Exception{
		return vblgoodsMapper.goodsSKU(entity);
	}
	
	/**
	* @Name: 商品库存数量
	* @Author: nick
	*/
	public Long GoodsStockNum(Map<String,Object> map)throws Exception{
		return vblgoodsMapper.GoodsStockNum(map);
	}
}
