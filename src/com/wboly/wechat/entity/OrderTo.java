package com.wboly.wechat.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 下单时的参数
 */
@JsonInclude(Include.NON_NULL)
public class OrderTo {
	/**
	 * 下单的用户ID
	 */
	private Long userId;

	/**
	 * 订单留言
	 *
	 */
	private String orderMessages;

	/**
	 * 收货地址ID
	 */
	private Long addrId;

	/**
	 * 订单详情
	 */
	private List<OrderDetailTo> details;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOrderMessages() {
		return orderMessages;
	}

	public void setOrderMessages(String orderMessages) {
		this.orderMessages = orderMessages;
	}

	public Long getAddrId() {
		return addrId;
	}

	public void setAddrId(Long addrId) {
		this.addrId = addrId;
	}

	public List<OrderDetailTo> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetailTo> details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "OrderTo [userId=" + userId + ", orderMessages=" + orderMessages + ", addrId=" + addrId + ", details="
				+ details + "]";
	}

}
