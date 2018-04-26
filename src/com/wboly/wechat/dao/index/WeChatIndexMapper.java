package com.wboly.wechat.dao.index;

import java.util.List;
import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 微信
 * @author nick
 * @createTime:2016-1-18
 */
public interface WeChatIndexMapper extends SysMapper {
	
	/**
	* @Name: 查询所有已上线活动的商品编号 
	* @Author: nick
	*/
	public List<Integer> selectAllActivityOfGoodsIdList();
		
	/**
	 * @Name: 根据商品编号查询商品表的品牌编号
	 * @Author: nick
	 */
	public List<Integer> selectGoodsOfbrandIdByGoodsId(Map<String,Object> map);
	
	/**
	 * @Name: 根据品牌编号查询品牌信息 
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectBrandInfoByBrandId(Map<String,Object> map);
	
}
