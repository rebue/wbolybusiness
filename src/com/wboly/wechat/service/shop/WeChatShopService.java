package com.wboly.wechat.service.shop;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.modules.dao.index.VblIndexMapper;
import com.wboly.modules.entity.area.AreaEntity;
import com.wboly.wechat.dao.shop.WeChatShopMapper;

/**
 * @Name: 门店.java
 * @Author: nick
 */
@Service
public class WeChatShopService {

	@Autowired(required = false)
	private WeChatShopMapper wechatShopMapper;
	
	@Autowired
	private VblIndexMapper vblindexMapper;
	
	/**
	 * @Name: 查询该用户地址是否是该门店的可配送范围内
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectCanbeDelivery(Map<String,Object> map){
		return wechatShopMapper.selectCanbeDelivery(map);
	}
	
	
	/**
	 * @Name: 查询该门店的商品库存
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectShopStockByParm(Map<String,Object> map){
		return wechatShopMapper.selectShopStockByParm(map);
	}
	

	/**
	 * @throws Exception
	 * @Name: 查询所有门店的区域信息
	 * @Author: nick
	 * 该查询是查广西下面的所有区域,如果以后有店铺在外省,把注释拿掉即可
	 */
	public List<AreaEntity> selectHaveAreaShop() throws Exception {
		
		List<AreaEntity> selectAllProvince = vblindexMapper.selectAllProvince();
		
		for(AreaEntity p : selectAllProvince){
			
			List<AreaEntity> selectAllCityByProvinceId = vblindexMapper.selectAllCityByProvinceId(p.getProvinceId());
			
			for(AreaEntity c : selectAllCityByProvinceId){
				
				if(p.getProvinceId() == c.getProvinceId()){
					
					List<AreaEntity> selectAllAreaByCityId = vblindexMapper.selectAllAreaByCityId(c.getCityId());
					
					for(AreaEntity a : selectAllAreaByCityId){
						
						if(c.getCityId() == a.getCityId()){
							c.setListNodes(selectAllAreaByCityId);
						}
					}
					p.setListNodes(selectAllCityByProvinceId);
				}
			}
		}
		
		return selectAllProvince;
	}

	
	/**
	* @Name: 查询门店信息
	* @Author: nick
	*/
	public List<Map<String,Object>> selectAllShop(Map<String,Object> map){
		return wechatShopMapper.selectAllShop(map);
	}
}
