package com.wboly.modules.dao.index;

import java.util.List;
import java.util.Map;

import com.wboly.modules.entity.area.AllAreaEntity;
import com.wboly.modules.entity.area.AreaEntity;
import com.wboly.rpc.entity.IndexEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 
 * @author nick
 * @createTime:2016-1-18
 */
public interface VblIndexMapper extends SysMapper {
	
	/**
	* @Name:  查询所有门店的省份地址   
	* @Author: nick
	*/
	public List<AllAreaEntity> selectAllProvinceAddress() throws Exception;
	
	/**
	 * @Name:  查询所有门店的城市地址   
	 * @Author: nick
	 */
	public List<AllAreaEntity> selectAllCityAddress(Integer provinceId) throws Exception;
	
	/**
	 * @Name:  查询所有门店的区域地址   
	 * @Author: nick
	 */
	public List<AllAreaEntity> selectAllAreaAddress(Integer cityId) throws Exception;
	
	/**
	 * @Name:  查询所有门店的街道地址   
	 * @Author: nick
	 */
	public List<AllAreaEntity> selectAllStreetAddress(Integer areaId) throws Exception;
	
	/**
	 * @Name:  根据区域查询所有门店信息  
	 * @Author: nick
	 */
	public List<AreaEntity> selectAllShopByArea(Map<String,String> map) throws Exception;
	
	/**
	 * @Name:  查询所有省份信息 
	 * @Author: nick
	 */
	public List<AreaEntity> selectAllProvince() throws Exception;
	
	/**
	 * @Name:  根据省份编号查询所有城市信息 
	 * @Author: nick
	 */
	public List<AreaEntity> selectAllCityByProvinceId(Integer provinceId) throws Exception;
	
	/**
	 * @Name:  根据城市编号查询所有区域信息
	 * @Author: nick
	 */
	public List<AreaEntity> selectAllAreaByCityId(Integer cityId) throws Exception;
	
	/**
	 * @Name:  查询门店数量 
	 * @Author: nick
	 */
	public List<Integer> selectShopCount();
	
	/**
	* @Name:   查询已上线活动信息条数以及门店编号
	* @Author: nick
	*/
	public List<IndexEntity> selectActivityCount() throws Exception;
	
	/**
	 * @Name:  查询已上线活动信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectActivityInfo(Integer shopId) throws Exception;
	
	/**
	 * @Name:  查询所有已上线活动信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectAllActivityInfo(Integer shopId) throws Exception;
	
	/**
	* @Name: 查询商品类目信息
	* @Author: nick
	*/
	public List<Map<String,Object>> selectGoodsClassInfo(Map<String,Object> map) throws Exception;
	
	/**
	* @Name: 查询首页商品信息
	* @Author: nick
	*/
	public  List<Map<String,Object>> selectHomePageInfo(IndexEntity entity) throws Exception;

	/**
	* @Name: 查询已上线活动信息
	* @Author: nick
	*/
	public  List<Map<String,Object>> selectOnLineInfo(Map<String,Object> map) throws Exception;
	
	/**
	 * @Name: 查询首页广告信息
	 * @Author: 查询首页公告信息
	 */
	public  List<Map<String,Object>> selectAdvertInfo(IndexEntity entity) throws Exception;
	
	/**
	 * @Name: 查询首页公告信息
	 * @Author: nick
	 */
	public  List<Map<String,Object>> selectAfficheInfo() throws Exception;
	
	/**
	 * @Name: 根据类目编号查询品牌信息
	 * @Author: nick
	 */
	public  List<Map<String,Object>> selectBrandInfoByClassId(Integer classId) throws Exception;
	
	/**
	 * @Name: 根据区域查询所有门店信息 
	 * @Author: nick
	 */
	public  List<Map<String,Object>> selectAllShopInfoByArea(Map<String,String> map) throws Exception;
	
}
