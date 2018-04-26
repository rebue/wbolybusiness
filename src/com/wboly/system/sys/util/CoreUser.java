package com.wboly.system.sys.util;

public class CoreUser {
	// 用户类型枚举
	public enum UserType {
        // 系统
        System(0),
        // 买家
        Buyer(1),
        // 卖家
        Seller(2),
        // 管理员
        Manager(3);
        // 定义私有变量
        private final int ncode;
	    // 构造函数，枚举类型只能为私有
        private UserType(int ncode){
    	 this.ncode=ncode;
        }
        public int getVlue(){
        	return ncode;
        }
        public String toString(){
	    	return String.valueOf(this.ncode);
	    }
    }
	// 用户类型枚举
	public enum DoUserType{
		// 系统
		System(0),
		// 用户
		User(1),
		// 管理员
		Manager(2);
		// 定义私有变量
	    private final int ncode;
		// 构造函数，枚举类型只能为私有
	    private DoUserType(int ncode){
	       this.ncode=ncode;
	    }
	    public int getVlue(){
	       return ncode;
	    }
	    public String toString(){
	       return String.valueOf(this.ncode);
	    }
	}
	// 用户头像尺寸类型
	public enum UserFaceType{
		// 小
		 small,
		// 中
		 middle,
		// 大
		 big;
	
	}
	// 开放平台类型枚举
	public enum OpenType {
        // 腾讯QQ
        Qq(1);
        // 定义私有变量
        private final int ncode;
		// 构造函数，枚举类型只能为私有
	    private OpenType(int ncode){
	    	this.ncode=ncode;
	    }
	    public int getVlue(){
		    return ncode;
		}
	    public String toString(){
	    	return String.valueOf(this.ncode);
	    }
    }
	// 用户账号锁定类型枚举
	public enum UserLockType{
		// 人工锁定
		ManPower(1),
		// 系统锁定-抢购未下单规则
		SystemNoPost(2),
		// 系统锁定-单号有误规则
		SystemErrOrder(3);
		// 定义私有变量
	    private  int ncode;
		// 构造函数，枚举类型只能为私有
	    private UserLockType(int ncode){
	    	 this.ncode=ncode;
	    }
	    public int getVlue(){
		       return ncode;
		}
	    public String toString(){
	    	return String.valueOf(this.ncode);
	    }
	}
	// 登录来源类型
	public enum LoginSourceType{
		// 微薄利web端
		VbolyWeb(1),
		// 微薄利wap端
		VbolyWap(2),
		// QQ登录
		Ten(3),
        // 安卓客户端
        Android(4),
        //苹果客户端
        IOS(5);
		// 定义私有变量
	    private  int ncode;
		// 构造函数，枚举类型只能为私有
	    private LoginSourceType(int ncode){
	    	 this.ncode=ncode;
	    }
	    public int getVlue(){
		       return ncode;
		}
	    public String toString(){
	    	return String.valueOf(this.ncode);
	    }
	}
	public static void main(String arg[]){
		LoginSourceType[] lst=LoginSourceType.values();
		for(LoginSourceType ls:lst){
			System.out.println(ls);
		}
	}
}
