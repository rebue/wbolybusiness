package com.wboly.wechat.service.order;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wboly.wechat.dao.order.WeChatUserAppraisalMapper;

/**
 * 用户评价
 * @author admin
 * 2018年1月17日15:29:35
 */
@Service
public class WeChatUserAppraisalService {

	@Autowired
	private WeChatUserAppraisalMapper weChatUserAppraisalMapper;
	/**
	 * 修改用户评价状态
	 * @param map
	 * @return
	 * 2018年1月17日15:06:01
	 */
	public int updateUserAppraiseState(Map<String, Object> map) {
		return weChatUserAppraisalMapper.updateUserAppraiseState(map);
	}
	
	/**
	 * 添加评论图片
	 * @param map
	 * @return
	 * 2018年1月17日16:43:32
	 */
	public int insertUserAppraiseImg(Map<String, Object> map) {
		return weChatUserAppraisalMapper.insertUserAppraiseImg(map);
	}
	
	/**
	 * 查询用户评价图片编号
	 * @param map
	 * @return
	 * 2018年1月17日15:17:46
	 */
	public int selectUserAppraiseImgId(Map<String, Object> map) {
		return weChatUserAppraisalMapper.selectUserAppraiseImgId(map);
	}
	
	/**
	 * 添加评价信息
	 * @param map
	 * @return
	 * 2018年1月17日15:47:12
	 */
	public int insertVblShopOrderAppraiseData(Map<String, Object> map) {
		return weChatUserAppraisalMapper.insertVblShopOrderAppraiseData(map);
	}
	
	/**
	 * 查询累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日16:02:25
	 */
	public List<Map<String, Object>> selectGoodsTotalAppraiseNumber(Map<String, Object> map) {
		return weChatUserAppraisalMapper.selectGoodsTotalAppraiseNumber(map);
	}
	
	/**
	 * 增加累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日15:50:38
	 */
	public int insertGoodsTotalAppraiseNumber(Map<String, Object> map) {
		return weChatUserAppraisalMapper.insertGoodsTotalAppraiseNumber(map);
	}
	
	/**
	 * 修改累积评价数
	 * @param map
	 * @return
	 * 2018年1月17日15:55:06
	 */
	public int updateGoodsTotalAppraiseNumber(Map<String, Object> map) {
		return weChatUserAppraisalMapper.updateGoodsTotalAppraiseNumber(map);
	}
}
