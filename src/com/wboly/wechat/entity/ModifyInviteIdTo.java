package com.wboly.wechat.entity;

public class ModifyInviteIdTo {

    /**
     * 订单详情id
     */
    private Long id;

    /**
     * 邀请人id，用户id
     */
    private Long inviterId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    @Override
    public String toString() {
        return "ModifyInviteIdTo [id=" + id + ", inviterId=" + inviterId + ", getId()=" + getId() + ", getInviterId()="
                + getInviterId() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()="
                + super.toString() + "]";
    }

}
