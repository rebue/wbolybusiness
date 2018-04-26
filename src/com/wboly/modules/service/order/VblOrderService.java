package com.wboly.modules.service.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.modules.dao.order.VblOrderMapper;
import com.wboly.rpc.entity.AddressEntity;
import com.wboly.rpc.entity.AppraiseEntity;
import com.wboly.rpc.entity.CarShopEntity;
import com.wboly.rpc.entity.OrderEntity;

/**
* @Name: 用户信息.java
* @Author: nick
*/
@Service
public class VblOrderService {
	
	@Autowired
	private VblOrderMapper vblorderMapper;
	
	/**
	 * @Name: 根据订单编号查询订单是否是待支付订单
	 * @Author: nick
	 */
	public Integer selectOrderByOrderId(String orderId){
		try {
			return vblorderMapper.selectOrderByOrderId(orderId);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* @Name: 根据订单编号查询订单签收码
	* @Author: nick
	*/
	public void selectOrderCodeByOrderId(HashMap<String,Object> hmap){
		try {
			String code = vblorderMapper.selectOrderCodeByOrderId(hmap);
			if(null != code && !"".equals(code)){
				hmap.put("code", code);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Name: 根据地址编号查询订单是否可以修改收货地址
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectOrderStateByAddId(AddressEntity entity){
		return vblorderMapper.selectOrderStateByAddId(entity);
	}
	
	/**
	* @Name: 查询购物车用户商品信息
	* @Author: nick
	*/
	public List<Map<String,Object>> selectCartByParm(Map<String,Object> map){
		return vblorderMapper.selectCartByParm(map);
	}
	
	/**
	* @Name: 查询这个活动是否有规则限制 
	* @Author: nick
	*/
	public List<Map<String,Object>> selectActivityRuleByParm(Map<String,Object> map){
		return vblorderMapper.selectActivityRuleByParm(map);
	}
	
	/**
	* @Name: 查询这个消费者是否购买过该商品 
	* @Author: nick
	*/
	public Map<String,Object> selectActivityRuleLogByParm(Map<String,Object> map){
		return vblorderMapper.selectActivityRuleLogByParm(map);
	}
	
	/**
	* @Name: 获取用户购物车列表
	* @Author: nick
	*/
	public List<Map<String,Object>> UserCarShops(CarShopEntity entity) throws Exception{
		return vblorderMapper.UserCarShops(entity);
	}
	
	/**
	 * @Name: 获取用户购物车商品数量
	 * @Author: nick
	 */
	public Integer UserCarShopCount(CarShopEntity entity) throws Exception{
		return vblorderMapper.selectUserCatGoodsCount(entity);
	}
	
	/**
	 * @Name: 查询用户订单信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> UserOrders(OrderEntity entity) throws Exception{
		return vblorderMapper.selectUserOrderInfo(entity);
	}
	
	/**
	 * @Name: 查询用户订单清单信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> UserOrdersInventory(OrderEntity entity) throws Exception{
		return vblorderMapper.selectUserOrdersInventory(entity);
	}
	
	/**
	 * @Name: 详情页分类行数
	 * @Author: nick
	 */
	@SuppressWarnings("finally")
	public Map<String,String> AppraiseclassifyCount(AppraiseEntity entity) {
		Map<String,String> resultMap = new HashMap<String,String>();
		
		try {
			int total = 0;
			
			List<Map<String,Object>> list = vblorderMapper.AppraiseclassifyCount(entity);
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map = list.get(i);
				String babyevaluation = map.get("babyevaluation").toString();
				String num = map.get("num").toString();
				
				if(babyevaluation.equals("5")){
					total +=Integer.valueOf(num);
					resultMap.put("fine", num);
				}else if(babyevaluation.equals("3")){
					total +=Integer.valueOf(num);
					resultMap.put("middle", num);
				}else if(babyevaluation.equals("1")){
					total +=Integer.valueOf(num);
					resultMap.put("bad", num);
				}else if(babyevaluation.equals("6")){
					resultMap.put("img", num);
				}
			}
			resultMap.put("total", String.valueOf(total));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {			
			return resultMap;
		}
	}
	
	/**
	 * @Name: 详情页评价数据
	 * @Author: nick
	 */
	public List<AppraiseEntity> goodsAppraise(AppraiseEntity entity) throws Exception{
		return vblorderMapper.goodsAppraise(entity);
	}
	
	/**
	 * @Name: 浏览足迹和看了又看
	 * @Author: nick
	 */
	public List<Map<String,Object>> UserFootprints(Map<String,String> map) throws Exception{
		return vblorderMapper.UserFootprints(map);
	}
	
	public int DelUserCarShop(CarShopEntity entity){
		Map<String,Object> data_in = new HashMap<String,Object>();
		data_in.put("data_in", "");
		return 1;
	}
	
	/**===================================才分存储过程In_ShopOrder开始================================***/
	/** 查询购物车  **/
	public List<Map<String, Object>> selectShoppingCart(Map<String, Object> map){
		return vblorderMapper.selectShoppingCart(map);
	}
	
	/** 查询订单是否存在  **/
	public List<Map<String, Object>> slelectShopOrder(Map<String, Object> map){
		return vblorderMapper.slelectShopOrder(map);
	}
	
	/** 添加门店交易订单信息  **/
	public int insertVblShopOrder(Map<String, Object> map) {
		return vblorderMapper.insertVblShopOrder(map);
	}
	
	/** 查询门店商品库存  **/
	public String selectShopInventory(Map<String, Object> map) {
		return vblorderMapper.selectShopInventory(map);
	}
	
	/** 修改门店库存  **/
	public int updateShopInventory(Map<String, Object> map) {
		return vblorderMapper.updateShopInventory(map);
	}
	
	/** 添加门店交易订单清单信息  **/
	public int insertVblShopOrderInventory(Map<String, Object> map) {
		return vblorderMapper.insertVblShopOrderInventory(map);
	}
	
	/** 删除购物车  **/
	public int deleteShoppingCartInId(Map<String, Object> map) {
		return vblorderMapper.deleteShoppingCartInId(map);
	}
	
	/** 进行订单规则检查  **/
	public void orderRuleChecking(Map<String, Object> map) {
		vblorderMapper.orderRuleChecking(map);
	}
	
	/** 保存检查规则日志  **/
	public void saveRuleChecksLog(Map<String, Object> map) {
		vblorderMapper.saveRuleChecksLog(map);
	}
	
	/** 添加门店订单交易付款信息  **/
	public int insertVblShopOrderTradeRecord(Map<String, Object> map) {
		return vblorderMapper.insertVblShopOrderTradeRecord(map);
	}
}
