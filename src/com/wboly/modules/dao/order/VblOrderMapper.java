package com.wboly.modules.dao.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wboly.rpc.entity.AddressEntity;
import com.wboly.rpc.entity.AppraiseEntity;
import com.wboly.rpc.entity.CarShopEntity;
import com.wboly.rpc.entity.OrderEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 
 * @author nick
 * @createTime:2016-1-18
 */
public interface VblOrderMapper extends SysMapper {
	
	/**
	* @Name: 根据订单编号查询订单签收码
	* @Author: nick
	*/
	public String selectOrderCodeByOrderId(HashMap<String,Object> hmap);
	
	/**
	 * @Name: 根据订单编号查询订单是否是待支付订单
	 * @Author: nick
	 */
	public Integer selectOrderByOrderId(String orderId)throws Exception;
	
	/**
	 * @Name: 根据地址编号查询订单是否可以修改收货地址
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectOrderStateByAddId(AddressEntity entity);
	
	/**
	 * @Name: 查询购物车用户商品信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectCartByParm(Map<String,Object> map);
	
	/**
	* @Name: 查询这个活动是否有规则限制 
	* @Author: nick
	*/
	public List<Map<String,Object>> selectActivityRuleByParm(Map<String,Object> map);
	
	/**
	* @Name: 查询这个消费者是否购买过该商品 
	* @Author: nick
	*/
	public Map<String,Object> selectActivityRuleLogByParm(Map<String,Object> map);
	
	/**
	* @Name: 获取用户购物车列表
	* @Author: nick
	*/
	public List<Map<String,Object>> UserCarShops(CarShopEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户购物车的商品数量
	 * @Author: nick
	 */
	public Integer selectUserCatGoodsCount(CarShopEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户订单信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserOrderInfo(OrderEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户订单清单信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserOrdersInventory(OrderEntity entity) throws Exception;
	
	/**
	 * @Name: 详情页分类行数
	 * @Author: nick
	 */
	public List<Map<String,Object>> AppraiseclassifyCount(AppraiseEntity entity) throws Exception;
	
	/**
	 * @Name: 详情页评价数据
	 * @Author: nick
	 */
	public List<AppraiseEntity> goodsAppraise(AppraiseEntity entity) throws Exception;
	
	/**
	 * @Name: 浏览足迹和看了又看
	 * @Author: nick
	 */
	public List<Map<String,Object>> UserFootprints(Map<String,String> map) throws Exception;
	
	/**===================================才分存储过程In_ShopOrder开始================================***/
	/** 查询购物车  **/
	public List<Map<String, Object>> selectShoppingCart(Map<String, Object> map);
	
	/** 查询订单是否存在  **/
	public List<Map<String, Object>> slelectShopOrder(Map<String, Object> map);
	
	/** 添加门店交易订单信息  **/
	public int insertVblShopOrder(Map<String, Object> map);
	
	/** 查询门店商品库存  **/
	public String selectShopInventory(Map<String, Object> map);
	
	/** 修改门店库存  **/
	public int updateShopInventory(Map<String, Object> map);
	
	/** 添加门店交易订单清单信息  **/
	public int insertVblShopOrderInventory(Map<String, Object> map);
	
	/** 删除购物车  **/
	public int deleteShoppingCartInId(Map<String, Object> map);
	
	/** 进行订单规则检查  **/
	public void orderRuleChecking(Map<String, Object> map);
	
	/** 保存检查规则日志  **/
	public void saveRuleChecksLog(Map<String, Object> map);
	
	/** 添加门店订单交易付款信息  **/
	public int insertVblShopOrderTradeRecord(Map<String, Object> map);
}
