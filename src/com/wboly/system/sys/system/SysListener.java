package com.wboly.system.sys.system;

import java.util.ArrayList;
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

		// 缓存公共接口
		String public_interface = PropertiesUtil.getPropertiesValue("system.properties", "PUBLIC_INTERFACE").toString();
		SysContext.PUBLIC_INTERFACE = new ArrayList<String>();
		for (String url : public_interface.split(",")) {
			if (url != null && !url.equals(",")) {
				SysContext.PUBLIC_INTERFACE.add(url);
			}
		}

		// 获取用户头像域名
		String userPhotoUrl = PropertiesUtil.getPropertiesValue("system.properties", "USERPHOTOURL").toString();
		SysContext.USERPHOTOURL = userPhotoUrl;

		// 支付路径
		String payUrl = PropertiesUtil.getPropertiesValue("system.properties", "PAYURL").toString();
		SysContext.PAYURL = payUrl;

		// 新v支付路径
		String vPayUrl = PropertiesUtil.getPropertiesValue("system.properties", "VPAYURL").toString();
		SysContext.VPAYURL = vPayUrl;

		// 新用户中心路径
		String userCenterUrl = PropertiesUtil.getPropertiesValue("system.properties", "USERCENTERURL").toString();
		SysContext.USERCENTERURL = userCenterUrl;
		
		// 上线微服务地址
		String onlineUrl = PropertiesUtil.getPropertiesValue("system.properties", "ONLINEURL").toString();
		SysContext.ONLINEURL = onlineUrl;
		
		// 订单微服务地址
		String orderUrl = PropertiesUtil.getPropertiesValue("system.properties", "ORDERURL").toString();
		SysContext.ORDERURL = orderUrl;
		
		// 订单微服务地址
		String areaUrl = PropertiesUtil.getPropertiesValue("system.properties", "AREAURL").toString();
		SysContext.AREAURL = areaUrl;
		
		// 图片地址
		String imgPath = PropertiesUtil.getPropertiesValue("system.properties", "IMGPATH").toString();
		SysContext.IMGPATH = imgPath;
		
		// 图片路径
		String imgUrl = PropertiesUtil.getPropertiesValue("system.properties", "IMGURL").toString();
		SysContext.IMGURL = imgUrl;
		
		// 商品轮播图和商品主图路径
		sc.setAttribute("goodsImgUrl", imgUrl + "goods/");
		// 保存图片路径进Session
		sc.setAttribute("imgUrl", imgUrl);

		String QRCODEPATH = PropertiesUtil.getPropertiesValue("system.properties", "QRCODEPATH").toString();
		SysContext.QRCODEPATH = QRCODEPATH;

		// 查询接口所有配置文件的key和value转化为map

		// 执行定时任务
		TimeUtil.timer();

		System.out.println("-----------------------------------系统初始化结束-------------------------------------------");

	}
}
