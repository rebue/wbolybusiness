package com.wboly.wechat.service.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.wechat.dao.index.WeChatIndexMapper;

/**
 * @Name: 微信 首页.java
 * @Author: nick
 */
@Service
public class WeChatIndexService {

	@Autowired(required = false)
	private WeChatIndexMapper wechatIndexMapper;
	
	/**
	* @Name: 查询所有已上线的品牌信息
	* @Author: nick
	*/
	public List<Map<String,Object>> selectAllBrandInfo(){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("goodsList", wechatIndexMapper.selectAllActivityOfGoodsIdList());
		map.put("brandList", wechatIndexMapper.selectGoodsOfbrandIdByGoodsId(map));
		return wechatIndexMapper.selectBrandInfoByBrandId(map);
	}
	
	
	/**
	* @Name: 查询所有已上线活动的商品编号 
	* @Author: nick
	*/
	public List<Integer> selectAllActivityOfGoodsIdList(){
		return wechatIndexMapper.selectAllActivityOfGoodsIdList();
	}
		
	/**
	 * @Name: 根据商品编号查询商品表的品牌编号
	 * @Author: nick
	 */
	public List<Integer> selectGoodsOfbrandIdByGoodsId(Map<String,Object> map){
		return wechatIndexMapper.selectGoodsOfbrandIdByGoodsId(map);
	}
	
	/**
	 * @Name: 根据品牌编号查询品牌信息 
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectBrandInfoByBrandId(Map<String,Object> map){
		return wechatIndexMapper.selectBrandInfoByBrandId(map);
	}
	
}
