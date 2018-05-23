package com.wboly.wechat.service.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.wechat.dao.user.WeChatUserMapper;

@Service
public class WeChatUserService {

	@Autowired
	private WeChatUserMapper weChatUserMapper;

	/**
	 * @Name: 修改用户默认地址信息
	 * @Author: nick
	 */
	public int upDefaultAddressByParm(Map<String, Object> map) {
		return weChatUserMapper.upDefaultAddressByParm(map);
	}

	/**
	 * @Name: 删除用户地址信息
	 * @Author: nick
	 */
	public int delAddressInfo(Integer addressId) {
		return weChatUserMapper.delAddressInfo(addressId);
	}

	/**
	 * @Name: 添加用户地址信息
	 * @Author: nick
	 */
	public int addAddressInfo(Map<String, Object> map) {
		return weChatUserMapper.addAddressInfo(map);
	}

	/** 添加用户注册信息 **/
	public int insertUserRegInformation(Map<String, Object> map) {
		return weChatUserMapper.insertUserRegInformation(map);
	}

	/** 修改用户注册信息 **/
	public int updateUserRegInformation(Map<String, Object> map) {
		return weChatUserMapper.updateUserRegInformation(map);
	}

	/** 根据用户编号查询用户是否存在 **/
	public String selectUserInformation(Map<String, Object> map) {
		return weChatUserMapper.selectUserInformation(map);
	}

	/** 添加用户登录记录 **/
	public int insertLoginRecord(Map<String, Object> map) {
		return weChatUserMapper.insertLoginRecord(map);
	}

	/**
	 * 修改用户名称
	 * 
	 * @param map
	 * @return
	 */
	public int updateUserName(Map<String, Object> map) {
		return weChatUserMapper.updateUserName(map);
	}
}
