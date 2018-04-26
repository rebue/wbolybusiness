package com.wboly.wechat.service.order;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.wechat.dao.order.WeChatShopAftersaleMapper;

@Service
public class WeChatShopAftersaleService {

	@Autowired
	private WeChatShopAftersaleMapper weChatShopAftersaleMapper;
	
	/**
	 * 获取订单号、门店编号、商品编号、商品Sku编号
	 * @param map
	 * @return
	 * 2018年1月16日14:20:53
	 */
	public List<Map<String, Object>> selectOrderStateAndBuyer(Map<String, Object> map) {
		return weChatShopAftersaleMapper.selectOrderStateAndBuyer(map);
	}
	
	/**
	 * 查询售后状态 0:可以售后 1 进行中 2 结束	
	 * @param map
	 * @return
	 * 2018年1月16日16:16:18
	 */
	public List<Map<String, Object>> selectOrderState(Map<String, Object> map) {
		return weChatShopAftersaleMapper.selectOrderState(map);
	}
	
	/**
	 * 更新订单状态为售后状态
	 * @param map
	 * @return
	 * 2018年1月16日14:52:19
	 */
	public int updateOrderStatusToBeAftersales(Map<String, Object> map) {
		return weChatShopAftersaleMapper.updateOrderStatusToBeAftersales(map);
	}
	
	/**
	 * 检查订单是否存在未处理完的售后(state:0.待处理;1.已回应;2:已关闭;3:处理完毕。)
	 * @param map
	 * @return
	 * 2018年1月16日14:59:51
	 */
	public List<Map<String, Object>> checkWhetherThereIsAnUnfinishedAftersaleOrde(Map<String, Object> map) {
		return weChatShopAftersaleMapper.checkWhetherThereIsAnUnfinishedAftersaleOrde(map);
	}
	
	/**
	 * 根据商品活动编号获取门店编号、商品编号、商品Sku编号、供应商用户编号
	 * @param map
	 * @return
	 * 2018年1月16日15:09:58
	 */
	public List<Map<String, Object>> selectShopGoodsSkuAndSupplierToActivityId(Map<String, Object> map) {
		return weChatShopAftersaleMapper.selectShopGoodsSkuAndSupplierToActivityId(map);
	}
	
	/**
	 * 添加用户售后图片
	 * @param map
	 * @return
	 * 2018年1月16日16:24:33
	 */
	public int insertUserAftersaleImg(Map<String, Object> map) {
		return weChatShopAftersaleMapper.insertUserAftersaleImg(map);
	}
	
	/**
	 * 查询用户已添加的售后图片编号
	 * @param map
	 * @return
	 * 2018年1月16日16:28:08
	 */
	public int selectHaveBeenAddedAftersaleImgId(Map<String, Object> map) {
		return weChatShopAftersaleMapper.selectHaveBeenAddedAftersaleImgId(map);
	}
	
	/**
	 * 添加用户申请售后信息
	 * @param map
	 * @return
	 * 2018年1月16日16:31:36
	 */
	public int insertVblShopOrderAftersaleData(Map<String, Object> map) {
		return weChatShopAftersaleMapper.insertVblShopOrderAftersaleData(map);
	}
	
	/**
	 * 	更新门店交易订单清单信息售后状态
	 * @param map
	 * @return
	 * 2018年1月16日16:40:34
	 */
	public int updateShopTransactionOrderInventoryState(Map<String, Object> map) {
		return weChatShopAftersaleMapper.updateShopTransactionOrderInventoryState(map);
	}
}
