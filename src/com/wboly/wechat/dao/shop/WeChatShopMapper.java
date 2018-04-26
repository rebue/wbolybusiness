package com.wboly.wechat.dao.shop;

import java.util.List;
import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 微信
 * @author nick
 * @createTime:2016-1-18
 */
public interface WeChatShopMapper extends SysMapper {
	
	/**
	* @Name: 查询门店信息
	* @Author: nick
	*/
	public List<Map<String,Object>> selectAllShop(Map<String,Object> map);
		
	/**
	 * @Name: 查询该门店的商品库存
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectShopStockByParm(Map<String,Object> map);
	
	/**
	 * @Name: 查询该用户地址是否是该门店的可配送范围内
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectCanbeDelivery(Map<String,Object> map);
	
}
