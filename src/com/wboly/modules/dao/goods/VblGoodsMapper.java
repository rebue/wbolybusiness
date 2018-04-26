package com.wboly.modules.dao.goods;

import java.util.List;
import java.util.Map;

import com.wboly.rpc.entity.GoodsEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * @author nick
 */
public interface VblGoodsMapper extends SysMapper {
	
	/**
	 * @Name: 根据多个参数查询已上线活动信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectActivityByParm(Map<String,Object> hmap)throws Exception;
	
	/**
	 * @Name: 详情页商品基本信息
	 * @Author: nick
	 */
	public int findGoodsBase(Map<String,String> hmap)throws Exception;
	
	/**
	 * @Name: 商品sku信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> goodsSKU(GoodsEntity entity)throws Exception;
	
	/**
	* @Name: 商品库存数量
	* @Author: nick
	*/
	public Long GoodsStockNum(Map<String,Object> map)throws Exception;
}
