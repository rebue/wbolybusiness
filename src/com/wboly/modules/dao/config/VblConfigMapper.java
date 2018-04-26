package com.wboly.modules.dao.config;

import java.util.List;
import java.util.Map;

import com.wboly.modules.entity.config.VblConfigEntity;
import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 
 * @author tai yang wang
 * @createTime:2016-1-18
 */
public interface VblConfigMapper extends SysMapper {
	
	
	/**
	 * 查询所有
	 * @param VblConfigEntity
	 * @return list<VblConfigEntity>
	 * @throws Exception
	 */
	public  List<Map<String,Object>> select(VblConfigEntity vblconfigEntity)throws Exception;
	
	/**
	 * 查询一行数据
	 * @param VblConfigEntity
	 * @return Entity
	 * @throws Exception
	 */
	public VblConfigEntity selectOne(VblConfigEntity vblconfigEntity)throws Exception;
	
	/**
	 * 查询行数
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int selectCount(VblConfigEntity vblconfigEntity)throws Exception;
	
	/**
	 * 新增
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int insert(VblConfigEntity vblconfigEntity) throws Exception;
	
	/**
	 * 更新
	 * @param VblConfigEntity
	 * @return int 
	 * @throws Exception
	 */
	public int update(VblConfigEntity vblconfigEntity) throws Exception;
	
	/**
	 * 删除
	 * @param VblConfigEntity
	 * @return int
	 * @throws Exception
	 */
	public int delete(VblConfigEntity vblconfigEntity) throws Exception;
	
}
