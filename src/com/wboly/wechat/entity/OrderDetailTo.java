package com.wboly.wechat.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 下单时订单详情的参数
 */
@JsonInclude(Include.NON_NULL)
public class OrderDetailTo {
	/**
	 * 上线ID
	 */
	private Long onlineId;

	/**
	 * 上线规格ID
	 */
	private Long onlineSpecId;

	/**
	 * 购买数量
	 */
	private Integer buyCount;

	/**
	 * 购物车ID
	 */
	private Long cartId;

	public Long getOnlineId() {
		return onlineId;
	}

	public void setOnlineId(Long onlineId) {
		this.onlineId = onlineId;
	}

	public Long getOnlineSpecId() {
		return onlineSpecId;
	}

	public void setOnlineSpecId(Long onlineSpecId) {
		this.onlineSpecId = onlineSpecId;
	}

	public Integer getBuyCount() {
		return buyCount;
	}

	public void setBuyCount(Integer buyCount) {
		this.buyCount = buyCount;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	@Override
	public String toString() {
		return "OrderDetailTo [onlineId=" + onlineId + ", onlineSpecId=" + onlineSpecId + ", buyCount=" + buyCount
				+ ", cartId=" + cartId + "]";
	}

}
