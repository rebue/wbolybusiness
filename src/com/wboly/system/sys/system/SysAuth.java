package com.wboly.system.sys.system;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SysAuth implements Filter {

	// 微信的公共接口
	private static String WECHAT_PUBLIC_INTERFACE = "/wechat/index/indexInfo.htm,/wechat/goods/allGoodsList.htm,"
			+ "/wechat/goods/goodsNav.htm,/wechat/goods/goodsList.htm,/wechat/goods/goodsDetail.htm,"
			+ "/wechat/user/logInPage.htm,/wechat/user/NextStepPage.htm,/wechat/cart/shoppingcart.htm,"
			+ "/wechat/user/registerPage.htm,/wechat/order/checkorderpage.htm,/wechat/pay/paycenterPage.htm,"
			+ "/wechat/user/userCenter.htm,/wechat/order/myOrders.htm,/wechat/collect/collectPage.htm,"
			+ "/wechat/collect/footprintPage.htm,/wechat/user/myWalletPage.htm,/wechat/user/addressPage.htm"
			+ "/wechat/order/commentPage.htm,/wechat/order/returnPage.htm,/wechat/order/afterSalePage.htm"
			+ "/wechat/user/feedbackPage.htm,/wechat/order/allAfterSalePage.htm,/wechat/oauth2/myPreReg.htm,"
			+ "/wechat/oauth2/checkSignature.htm,/wechat/user/newAddressPage.htm,/wechat/oauth2/regnewuser/page.htm,"
			+ "/wechat/oauth2/bindHelis.htm,/wechat/oauth2/prompt.htm,/wechat/oauth2/bind/user/page.htm,"
			+ "/wechat/pay/test/success.htm,/wechat/paytest/toPay.htm,/wechat/paytest/authpay.htm,"
			+ "/wechat/paytest/jssdk.htm,/wechat/paytest/backUrl.htm,/wechat/user/updateLoginPwd.htm"
			+ "/wechat/pay/authpay.htm,/wechat/pay/toPay.htm,/wechat/pay/success.htm,/wechat/user/updatepaypwdpage.htm,"
			+ "/wechat/user/updatepaypwdnextpage.htm,/wechat/newoauth/back.htm,/wechat/newoauth/login.htm,/wechat/receive/push.htm, "
			+ "/wechat/pay/shopOrderPay.htm, /wechat/pay/createVpayPrepaymentId.htm,/wechat/user/wechatWithdraw.htm,"
			+ "/wechat/oauth2/checkSignatures.htm,/wechat/oauth2/myPreRegs.htm,/wechat/oauth2/myPreRegss.htm,"
			+ "/wechat/user/updateLoginPwdNextPage.htm,/wechat/oauth2/loginAndBind.htm,"
			+ "/wechat/oauth2/registrationAndBinding.htm,/wechat/oauth2/verifyLoginAndBind.htm,/wechat/user/sendVerificationCode.htm, "
			+ "/wechat/goods/goodsCarouselPic.htm,/wechat/goods/selectGoodsDetails.htm,/wechat/goods/selectGoodsSpecDetails.htm,"
			+ "/wechat/cart/batchdelete.htm,/wechat/order/aboutCinfirmReceipt.htm";

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		// 请求路径
		String requestUrlss = request.getRequestURI();
		System.out.println("==============微信端拦截器获取到的url为" + requestUrlss + "==============");
		HttpServletResponse resp = (HttpServletResponse) servletResponse;
		resp.setHeader("Access-Control-Allow-Credentials", "false");
		resp.setHeader("Access-Control-Allow-Origin", "*");
		resp.setHeader("Access-Control-Allow-Methods", "*");
		resp.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");

		if (urlDoFilter(servletRequest)) {
			chain.doFilter(servletRequest, servletResponse);
		}
	}

	public Boolean urlDoFilter(ServletRequest servletRequest) {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String url = request.getRequestURI();
		url = url.replaceFirst(SysContext.SYS_NAME, "");
		System.out.println("拦截器替换url后的值为：" + url);
		String SYS_KEY = request.getParameter("key");

		if (SysContext.PUBLIC_INTERFACE.contains(url) || WECHAT_PUBLIC_INTERFACE.contains(url)) {
			System.out.println("=========访问公共接口:" + url);
			return true;
		} else {
			if (SysContext.SYS_KEY.equals(SYS_KEY)) {
				System.out.println("==========访问私有接口通过:" + url);
				return true;
			} else {
				System.out.println("==========访问私有接口失败:"+url);
				return false;
			}
		}
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public void initData() {
	}
}