package com.wboly.modules.service.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.wboly.modules.dao.config.VblConfigMapper;
import com.wboly.modules.entity.config.VblConfigEntity;
import com.wboly.rpc.Client.SystemRPCClient;
import com.wboly.rpc.entity.SystemConfigEntity;

/**
 * 
 * @author tai yang wang
 * @createTime:2016-1-18
 */
public class VblConfigService {
	
	@Autowired
	private VblConfigMapper vblconfigMapper;
	
	/**
	 * 查询所有
	 * @param VblConfigEntity
	 * @return list<VblConfigEntity>
	 * @throws Exception
	 */
	public  List<Map<String,Object>> select(VblConfigEntity vblconfigEntity)throws Exception{
		return this.vblconfigMapper.select(vblconfigEntity);
	}
	
	/**
	 *  查询一行数据
	 * @param VblConfigEntity
	 * @return VblConfigEntity
	 * @throws Exception
	 */
	public VblConfigEntity selectOne(VblConfigEntity vblconfigEntity)throws Exception{
		return this.vblconfigMapper.selectOne(vblconfigEntity);
	}
	
	/**
	 *  查询行数
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int selectCount(VblConfigEntity vblconfigEntity)throws Exception{
		return this.vblconfigMapper.selectCount(vblconfigEntity);
	}
	
	/**
	 * 新增
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int insert(VblConfigEntity vblconfigEntity) throws Exception{
		return this.vblconfigMapper.insert(vblconfigEntity);
	}
	
	/**
	 * 修改
	 * @param VblConfigEntity
	 * @return int 
	 * @throws Exception
	 */
	public int update(VblConfigEntity vblconfigEntity) throws Exception{
		return this.vblconfigMapper.update(vblconfigEntity);
	}
	
	/**
	 * 删除
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int delete(VblConfigEntity vblconfigEntity) throws Exception{
		return this.vblconfigMapper.delete(vblconfigEntity);
	}
	
	/**
	 * 查询接口所有配置文件的key和value转化为map
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> selectAllKeyAndValues()throws Exception{
		
		Map<String,String> map=new HashMap<String,String>();
		
		SystemRPCClient systemService =  new SystemRPCClient();
		List<SystemConfigEntity> list=systemService.client.selectSystemConfig();
		for(SystemConfigEntity m:list){
			map.put(m.getConfigName(), m.configValue);
		}
		return map;
	}
}
