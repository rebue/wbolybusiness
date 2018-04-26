package com.wboly.wechat.dao.order;

import java.util.List;
import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

/**
 * @Name: 微信 订单.java
 * @Author: nick
 */
public interface WeChatOrderMapper extends SysMapper {

	/**
	 * @Name: 根据消费者以及订单号查询订单详情信息
	 * @Author: nick
	 */
	public List<Map<String, Object>> selectOrderInventoryByParm(Map<String, Object> map);
	
	/**
	 * 查询商品订单详情
	 * @param map
	 * @return
	 * 2018年1月18日11:30:44
	 */
	public List<Map<String, Object>> selectOrderInventoryData(Map<String, Object> map);

	/**
	 * @Name: 根据活动编号以及买家编号查询用户是否购买过该规则商品
	 * @Author: nick
	 */
	public List<Map<String, Object>> selectGoodsRuleLog(Map<String, Object> map);

	/************************************	修改门店订单状态开始	********************************************/
	/**
	 * 获取门店编号、消费用户编号
	 * Title: selectShopAndBuyer
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日14:42:25
	 */
	public List<Map<String, Object>> selectShopAndBuyer(Map<String, Object> map);
	
	/**
	 * 更新门店交易订单支付方式、状态
	 * Title: updateShopOrderStatus
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日14:46:31
	 */
	public int updateShopOrderStatus(Map<String, Object> map);

	/**
	 * 添加信息到门店交易订单付款信息表 
	 * Title: insertShopTransactionPaymentInfo
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日14:46:39
	 */
	public int insertShopTransactionPaymentInfo(Map<String, Object> map);
	
	/**
	 * 添加门店交易订单状态跟踪信息
	 * Title: insertVblShopOrderTradeRecord
	 * Description: 
	 * @param map
	 * @return
	 * 2018-1-8 15:25:11
	 */
	public int insertVblShopOrderTradeRecord(Map<String, Object> map);
	
	/**
	 * 添加信息到订单返款计划表
	 * Title: insertVblShopOrderPlantorefund
	 * Description: 
	 * @param map
	 * @return
	 */
	public int insertVblShopOrderPlantorefund(Map<String, Object> map);
	
	/**
	 * 添加快递费
	 * Title: insertExpressFeeVblShopOrderPlantorefund
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日15:54:11
	 */
	public int insertExpressFeeVblShopOrderPlantorefund(Map<String, Object> map);
	
	/**
	 * 添加推送消息计划
	 * Title: insertVblPushPlan
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日16:06:02
	 */
	public int insertVblPushPlan(Map<String, Object> map);
	
	/**
	 * 更新vbl_goods销量
	 * Title: updateVblGoodsSales
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日16:12:21
	 */
	public int updateVblGoodsSales(Map<String, Object> map);
	
	/**
	 * 更新vbl_goods_stat销量
	 * Title: updateVblGoodsStatSales
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日16:24:25
	 */
	public int updateVblGoodsStatSales(Map<String, Object> map);
	
	/**
	 * 更新vbl_activity销量
	 * Title: updateVblActivitySales
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日16:29:25
	 */
	public int updateVblActivitySales(Map<String, Object> map);
	
	/**
	 * 更新vbl_activity_off销量
	 * Title: updateVblActivityOff
	 * Description: 
	 * @param map
	 * @return
	 * 2018年1月8日16:39:40
	 */
	public int updateVblActivityOff(Map<String, Object> map);
	
	/****************************************门店修改订单结束******************************************************/
	
	/****************************************用户生成售后订单开始*****************************************************/
	
	
	/****************************************用户生成售后订单结束*****************************************************/
}
