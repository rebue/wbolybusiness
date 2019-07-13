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
     * 邀请人id
     */
    private Long inviteId;

    /**
     * 是否匹配给邀请人
     */
    private boolean isInviter;
    /**
     * 购买数量
     */
    private Integer buyCount;

    public Long getInviteId() {
        return inviteId;
    }

    public void setInviteId(Long inviteId) {
        this.inviteId = inviteId;
    }

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

    public boolean isInviter() {
        return isInviter;
    }

    public void setInviter(boolean isInviter) {
        this.isInviter = isInviter;
    }

    @Override
    public String toString() {
        return "OrderDetailTo [onlineId=" + onlineId + ", onlineSpecId=" + onlineSpecId + ", inviteId=" + inviteId
                + ", isInviter=" + isInviter + ", buyCount=" + buyCount + ", cartId=" + cartId + "]";
    }

}
