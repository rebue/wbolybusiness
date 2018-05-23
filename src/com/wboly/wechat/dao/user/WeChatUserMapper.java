package com.wboly.wechat.dao.user;

import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

public interface WeChatUserMapper extends SysMapper {

	/**
	 * @Name: 修改用户默认地址信息
	 * @Author: nick
	 */
	public int upDefaultAddressByParm(Map<String, Object> map);

	/**
	 * @Name: 删除用户地址信息
	 * @Author: nick
	 */
	public int delAddressInfo(Integer addressId);

	/**
	 * @Name: 添加用户地址信息
	 * @Author: nick
	 */
	public int addAddressInfo(Map<String, Object> map);

	/** 添加用户注册信息 **/
	public int insertUserRegInformation(Map<String, Object> map);

	/** 修改用户注册信息 **/
	public int updateUserRegInformation(Map<String, Object> map);

	/** 根据用户编号查询用户是否存在 **/
	public String selectUserInformation(Map<String, Object> map);

	/** 添加用户登录记录 **/
	public int insertLoginRecord(Map<String, Object> map);

	/**
	 * 修改用户名称
	 * 
	 * @param map
	 * @return
	 */
	public int updateUserName(Map<String, Object> map);
}
