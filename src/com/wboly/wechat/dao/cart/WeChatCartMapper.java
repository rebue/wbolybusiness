package com.wboly.wechat.dao.cart;

import java.util.List;
import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 微信 购物车
 * @author nick
 * @createTime:2016-1-18
 */
public interface WeChatCartMapper extends SysMapper {
	
	/**
	* @Name: 查询购物车商品数量
	* @Author: nick
	*/
	public Integer selectShoppingCartCount(Map<String,Object> map);
		
	/****************************************用户加入购物车开始******************************************************/
	/**
	 * 查询已上线商品价格和名称
	 * @param map
	 * @return
	 * 2018年1月15日11:29:23
	 */
	public List<Map<String, Object>> selectHasBeenLaunchedGoodsPriceAndName(Map<String, Object> map);
	
	/**
	 * 查询商品图片
	 * @param map
	 * @return
	 * 2018年1月15日11:37:30
	 */
	public List<Map<String, Object>> selectGoodsImg(Map<String, Object> map);
	
	/**
	 * 获取购物车编号、订单数量
	 * @param map
	 * @return
	 * 2018年1月15日11:45:28
	 */
	public List<Map<String, Object>> selectShoppingCartNumberAndNum(Map<String, Object> map);
	
	/**
	 * 根据购物车编号删除购物车信息
	 * @param map
	 * @return
	 * 2018年1月15日11:55:47
	 */
	public int delectVblCartUserInId(Map<String, Object> map);
	
	/**
	 * 更新购物车信息
	 * @param map
	 * @return
	 * 2018年1月15日11:59:21
	 */
	public int updateVblCartUserDataInId(Map<String, Object> map);
	
	/**
	 * 添加购物车信息
	 * @param map
	 * @return
	 * 2018年1月15日14:10:06
	 */
	public int insertVblCartUserData(Map<String, Object> map);
	
	/**
	 * 查询用户已购买规则商品数量和类型
	 * @param map
	 * @return
	 * 2018年1月15日17:55:23
	 */
	public List<Map<String, Object>> selectUserHaveRuleTypeAndNum(Map<String, Object> map);
	/****************************************用户加入购物车结束******************************************************/
	
	/** 根据用户编号和购物车编号删除购物车编号  **/
	public int deleteShoppingCartInUserIdAndId(Map<String, Object> map);
}
