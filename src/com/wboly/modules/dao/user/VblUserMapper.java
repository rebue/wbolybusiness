package com.wboly.modules.dao.user;

import java.util.List;
import java.util.Map;

import com.wboly.rpc.entity.CollectionEntity;
import com.wboly.rpc.entity.UserEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 
 * @author nick
 * @createTime:2016-1-18
 */
public interface VblUserMapper extends SysMapper {
	
	/**
	* @Name:  待返现总金额 单位:分
	* @Author: nick
	*/
	public Map<String,Object> selectResidueBacLimitByParm(UserEntity entity) throws Exception;
	
	/**
	 * @Name: 总收益金额,以及剩余收益金额 单位:分
	 * @Author: nick
	 */
	public Map<String,Object> UserAllAmount(UserEntity entity) throws Exception;

	/**
	 * @Name: 查询用户推广信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserExtendsInfo(Map<String,Object> map) throws Exception;
	
	/**
	 * @Name: 查询用户操作的数据(待付款,待收货,待返款,售后(售后中,退货中))
	 * @Author: nick
	 */
	public Map<String,Object> selectUserOperationInfo(Map<String,String> map) throws Exception;
	
	/**
	 * @Name: 查询app是否有新版本
	 * @Author: nick
	 */
	public Map<String,String> selectNewApp(String id) throws Exception;
	
	/**
	 * @Name: 查询用户收货地址
	 * @Author: nick
	 */
	public List<Map<String,String>> selectUserAddress(UserEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户信息
	 * @Author: nick
	 */
	public UserEntity selectUserInfo(UserEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户收藏信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserCollectionInfo(CollectionEntity entity) throws Exception;
	
	/**
	 * @Name: 查询用户是否关注收藏信息
	 * @Author: nick
	 */
	public Integer selectUserWhetherAttention(CollectionEntity entity) throws Exception;
	
	/**
	 * @Name:  查询用户佣金记录信息 
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserVfriendInfo(CollectionEntity entity) throws Exception;
	
	/**
	 * @Name:  根据用户编号查询用户信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserInfoByUserId(Map<String,Object> map) throws Exception;
	
}
