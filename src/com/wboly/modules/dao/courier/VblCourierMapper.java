package com.wboly.modules.dao.courier;

import java.util.List;
import java.util.Map;

import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 
 * @author tai yang wang
 * @createTime:2016-1-18
 */
public interface VblCourierMapper extends SysMapper {
	
	/**
	 *  快递员订单列表接口
	 * @param VblConfigEntity
	 * @return list<Map<String,Object>>
	 * @throws Exception
	 */
	public  List<Map<String,Object>> CourierOrders(Map<String,String> map)throws Exception;
	
	/**
	* @Name: 验证快递员信息表是否有这个快递员 
	* @Author: nick
	*/
	public int VerifyCourierInfo(Map<String,String> map)throws Exception;
	
	/**
	* @Name:  验证用户是否是有门店信息
	* @Author: nick
	*/
	public int VerifyUserIsWhat(Map<String,String> map)throws Exception;
	
	/**
	* @Name: 快递信息接口
	* @Author: nick
	*/
	public List<Map<String,Object>> OrderCourierSite(Map<String,String> map)throws Exception;
	
	/**
	* @Name: 门店订单列表
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopOrders(OrderEntity entity)throws Exception;
	
	/**
	* @Name: 门店订单列表 
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopOrdersInventory(OrderEntity entity)throws Exception;
	
	/**
	* @Name: 门店快递员列表
	* @Author: nick
	*/
	public List<Map<String,Object>> ShopCouriers(OrderEntity entity)throws Exception;
	
	/**
	 * @Name: 门店信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> Shops(OrderEntity entity)throws Exception;
	
	/**
	 * @Name: 查询门店可配送的区域
	 * @Author: nick
	 */
	public List<Map<String,Object>> ShopCanBeDelivery(OrderEntity entity)throws Exception;
	
	public Map<String,Object> selectCourierInfo(Map<String,Object> map)throws Exception;
	
	public Map<String,Object> selectShopInfo(Map<String,Object> map)throws Exception;
	
	public Map<String,Object> selectLeagueInfo(Map<String,Object> map)throws Exception;

}
