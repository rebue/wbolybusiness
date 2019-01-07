package com.wboly.system.sys.system;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.wboly.system.sys.util.PropertiesUtil;
import com.wboly.system.sys.util.TimeUtil;

/**
 * 
 * @author Sea
 *
 */
public class SysListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent se) {
	}

	public void contextInitialized(ServletContextEvent se) {
		this.initSys(se);
	}

	/**
	 * 系统初始化
	 * 
	 * @author Sea
	 * @param se
	 */
	private void initSys(ServletContextEvent se) {

		System.out.println("----------------------------------系统初始化开始-------------------------------------------");

		ServletContext sc = se.getServletContext();

		// 获取项目在服务器的路径
		String realpath = sc.getRealPath("/");

		// 获取项目名
		String contextPath = sc.getContextPath();

		// 保存项目名进Session
		sc.setAttribute("ctx", contextPath);

		// ͨ加载全局静态变量
		SysContext.SYS_NAME = contextPath;
		SysContext.REALPATH = realpath;

		System.out.println("项目在服务器的路径：" + realpath);
		System.out.println("项目名：" + contextPath);

		// ͨ读取Spring配置文件信息
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(se.getServletContext());
		SysContext.WAC = wac;

		// 新v支付路径
		String vPayUrl = PropertiesUtil.getPropertiesValue("system.properties", "VPAYURL").toString();
		SysContext.VPAYURL = vPayUrl;
		sc.setAttribute("vPayUrl", vPayUrl);
		// 新用户中心路径
		String userCenterUrl = PropertiesUtil.getPropertiesValue("system.properties", "USERCENTERURL").toString();
		SysContext.USERCENTERURL = userCenterUrl;

		// 上线微服务地址
		String onlineUrl = PropertiesUtil.getPropertiesValue("system.properties", "ONLINEURL").toString();
		SysContext.ONLINEURL = onlineUrl;

		// 订单微服务地址
		String orderUrl = PropertiesUtil.getPropertiesValue("system.properties", "ORDERURL").toString();
		SysContext.ORDERURL = orderUrl;

		// 区域微服务地址
		String areaUrl = PropertiesUtil.getPropertiesValue("system.properties", "AREAURL").toString();
		SysContext.AREAURL = areaUrl;

		// 微信微服务地址
		String wxxUrl = PropertiesUtil.getPropertiesValue("system.properties", "WXXURL").toString();
		SysContext.WXXURL = wxxUrl;

		// 图片地址
		String imgPath = PropertiesUtil.getPropertiesValue("system.properties", "IMGPATH").toString();
		SysContext.IMGPATH = imgPath;

		// 图片路径
		String imgUrl = PropertiesUtil.getPropertiesValue("system.properties", "IMGURL").toString();
		SysContext.IMGURL = imgUrl;

		// 实名认证微服务地址
		String rnaUrl = PropertiesUtil.getPropertiesValue("system.properties", "RNAURL").toString();
		SysContext.RNAURL = rnaUrl;
		sc.setAttribute("rnaUrl", rnaUrl);

		// 快递微服务地址
		String kdiUrl = PropertiesUtil.getPropertiesValue("system.properties", "KDIURL").toString();
		SysContext.KDIURL = kdiUrl;
		
		// 快递微服务地址
		String pntUrl = PropertiesUtil.getPropertiesValue("system.properties", "PNTURL").toString();
		SysContext.PNTURL = pntUrl;

		/** 微信登录校验key **/
		String loginSignKey = PropertiesUtil.getPropertiesValue("system.properties", "LOGINSIGNKEY").toString();
		SysContext.LOGINSIGNKEY = loginSignKey;

		/**
		 * 微信appid
		 */
		String wxAppid = PropertiesUtil.getPropertiesValue("system.properties", "WXAPPID").toString();
		SysContext.wxAppId = wxAppid;

		// 商品轮播图和商品主图路径
		sc.setAttribute("goodsImgUrl", imgUrl);
		// 保存图片路径进Session
		sc.setAttribute("imgUrl", imgUrl);

		// 查询接口所有配置文件的key和value转化为map
//		String userjson = "{\"userId\":525616558689484801,\"userName\":\"名字不要太长这样就好了\",\"img\":\"http://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83epRv921niaj5x7YBGHcWDdD65icDvBzs8icsbkwOyDnKhQ9pmibJxHiafPXOOiaw8oKtOWibS0SaI8RrSN4A/132\",\"openid\":\"oAwIr04JK8GVF5xvNaOZ4IxwQhhQ\"}";
//		SysCache.setWechatUser("oAwIr04JK8GVF5xvNaOZ4IxwQhhQ", userjson);// 缓存用户信息
		// 执行定时任务
		TimeUtil.timer();

		System.out.println("-----------------------------------系统初始化结束-------------------------------------------");

	}
}
