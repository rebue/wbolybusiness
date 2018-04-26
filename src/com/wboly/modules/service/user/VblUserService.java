package com.wboly.modules.service.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.modules.dao.user.VblUserMapper;
import com.wboly.rpc.entity.CollectionEntity;
import com.wboly.rpc.entity.UserEntity;

/**
* @Name: 用户信息.java
* @Author: nick
*/
@Service
public class VblUserService {
	
	@Autowired
	private VblUserMapper vbluserMapper;
	
	/**
	* @Name:  待返现总金额 单位:分
	* @Author: nick
	*/
	public Map<String,Object> selectResidueBacLimitByParm(UserEntity entity) throws Exception{
		return vbluserMapper.selectResidueBacLimitByParm(entity);
	}
	
	/**
	 * @Name: 总收益金额,以及剩余收益金额 单位:分
	 * @Author: nick
	 */
	public Map<String,Object> UserAllAmount(UserEntity entity) throws Exception{
		return vbluserMapper.UserAllAmount(entity);
	}
	
	/**
	 * @Name: 查询用户推广信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserExtendsInfo(Map<String,Object> map) throws Exception{
		return vbluserMapper.selectUserExtendsInfo(map);
	}

	/**
	 * @Name: 查询用户操作的数据(待付款,待收货,待返款,售后(售后中,退货中))
	 * @Author: nick
	 */
	public Map<String,Object> selectUserOperationInfo(Map<String,String> map) throws Exception{
		return vbluserMapper.selectUserOperationInfo(map);
	}
	
	/**
	 * @Name: 查询app是否有新版本
	 * @Author: nick
	 */
	public Map<String,String> selectNewApp(String id) throws Exception{
		return vbluserMapper.selectNewApp(id);
	}
	
	/**
	 * @Name: 查询用户收货地址
	 * @Author: nick
	 */
	public List<Map<String,String>> selectUserAddress(UserEntity entity) throws Exception{
		return vbluserMapper.selectUserAddress(entity);
	}
	
	/**
	 * @Name: 查询用户信息
	 * @Author: nick
	 */
	public UserEntity selectUserInfo(UserEntity entity) throws Exception{
		return vbluserMapper.selectUserInfo(entity);
	}
	
	/**
	 * @Name: 查询用户收藏信息
	 * @Author: nick
	 */
	public List<Map<String,Object>> selectUserCollectionInfo(CollectionEntity entity) throws Exception{
		return vbluserMapper.selectUserCollectionInfo(entity);
	}
	
	/**
	 * @Name: 查询用户是否关注收藏信息
	 * @Author: nick
	 */
	public Integer selectUserWhetherAttention(CollectionEntity entity) throws Exception{
		return vbluserMapper.selectUserWhetherAttention(entity);
	}
	
	@SuppressWarnings("finally")
	public List<Map<String,String>> UserPopularizeGain(CollectionEntity entity){
		List<Map<String,String>> resultlist = new ArrayList<Map<String,String>>();
		try{
			List<Map<String, Object>> list = vbluserMapper.selectUserVfriendInfo(entity);
			
			for (int i = 0; i < list.size(); i++) {
				Map<String,String> hmap = new HashMap<String,String>();
				Map<String,Object> map = list.get(i);
				hmap.put("brokerage", map.get("brokerage").toString());
				hmap.put("userName", map.get("userName").toString());
				hmap.put("regTime", map.get("regTime").toString().substring(0, map.get("regTime").toString().lastIndexOf(".")));
				resultlist.add(i,hmap);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			return resultlist;
		}
	}
}
