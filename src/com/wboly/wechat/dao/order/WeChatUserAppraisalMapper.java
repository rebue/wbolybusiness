package com.wboly.wechat.dao.order;

import java.util.List;
import java.util.Map;

import com.wboly.system.sys.mybatis.SysMapper;

/**
 * 用户评价
 * @author admin
 * 2018年1月17日15:28:19
 */
public interface WeChatUserAppraisalMapper extends SysMapper {

	/**
	 * 修改用户评价状态
	 * @param map
	 * @return
	 * 2018年1月17日15:06:01
	 */
	public int updateUserAppraiseState(Map<String, Object> map);
	
	/**
	 * 添加评价图片
	 * @param map
	 * @return
	 * 2018年1月17日16:44:12
	 */
	public int insertUserAppraiseImg(Map<String, Object> map);
	
	/**
	 * 查询用户评价图片编号
	 * @param map
	 * @return
	 * 2018年1月17日15:17:46
	 */
	public int selectUserAppraiseImgId(Map<String, Object> map);
	
	/**
	 * 添加评价信息
	 * @param map
	 * @return
	 * 2018年1月17日15:47:12
	 */
	public int insertVblShopOrderAppraiseData(Map<String, Object> map);
	
	/**
	 * 查询累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日16:02:25
	 */
	public List<Map<String, Object>> selectGoodsTotalAppraiseNumber(Map<String, Object> map);
	
	/**
	 * 增加累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日15:50:38
	 */
	public int insertGoodsTotalAppraiseNumber(Map<String, Object> map);
	
	/**
	 * 修改累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日15:55:06
	 */
	public int updateGoodsTotalAppraiseNumber(Map<String, Object> map);
}
