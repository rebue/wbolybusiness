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

import org.apache.commons.lang3.StringUtils;

import com.wboly.system.sys.util.wx.WeixinUtil.SITE;

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
            + "/wechat/cart/batchdelete.htm,/wechat/order/aboutCinfirmReceipt.htm,/wechat/order/queryLogistics.htm,/wechat/order/returnGoods.htm,"
            + "/wechat/user/setLoginName.htm,/wechat/user/setLoninNamePage.htm,/wechat/user/setLoginPassword.htm,/wechat/user/changeLogonPassword.htm,/wechat/user/updateloginpwdpage.htm,"
            + "/wechat/user/rulePage.htm,/wechat/user/uploadRealImg.htm,/wechat/user/verifyRealName.htm,/wechat/user/verifyRealNameApply.htm,/wechat/user/verifyResult.htm,"
            + "/wechat/goods/fullReturnGoodsList.htm,/wechat/goods/getFullReturnGoodsList.htm, /wechat/user/accountTrade.htm, /wechat/user/cashbackTrade.htm, /wechat/user/beBeingWithdraw.htm,"
            + "/wechat/order/getOrderLogisticInfo.htm, /wechat/order/getCashBack.htm, /wechat/user/applyWithdrAwaccountPage.htm, /wechat/user/applyWithdrAwaccountPage.htm,"
            + "/wechat/user/submitApplyWithdrAwaccount.htm, /wechat/order/cancelReturn.htm, /wechat/user/updatepaypwdpage.htm, /wechat/user/payPwIsExis.htm,"
            + "/wechat/user/setPayPassword.htm, /wechat/user/changePayPassword.htm, /wechat/user/applyWithdraw.htm, /wechat/user/withdrawRecord.htm, /wechat/user/getWithdrawRecord.htm,"
            + "/wechat/index/checkIsSubscribe.htm, /wechat/order/modifyPayOrderId.htm, /wechat/order/commissionTotal.htm, /wechat/user/jumpMyPoint.htm, /wechat/user/cumulativeIncome.htm, "
            + "/wechat/user/waitingPoint.htm, /wechat/user/point.htm, /wechat/user/getPoint.htm, /wechat/order/transfer.htm, /wechat/order/modifyInviteId.htm,/wechat/order/getPayOrderList.htm"
            + "/wechat/user/appPaycenterPage.htm,/wechat/order/limitCount.htm";

    @Override
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
        // 推广者id
        String promoterId = request.getParameter("promoterId");
        System.out.println("拦截器获取到的推广者id为：" + promoterId);
        if (urlDoFilter(servletRequest)) {
            if (requestUrlss.contains("/wechat/oauth2/myPreReg.htm")
                    || requestUrlss.contains("/wechat/oauth2/checkSignature.htm")
                    || requestUrlss.contains("/wechat/user/rulePage.htm")) {
                System.out.println("11111111111");
                chain.doFilter(servletRequest, servletResponse);
            } else {

//              CookiesUtil.setCookie("oNklT0bp29pc3Qtk0UEdnCVzotKU", resp);
                // 获取当前登录用户编号
                String userId = SysCache.getWeChatUserByColumn(request, "userId");
                System.out.println("拦截器获取到的当前用户id为：" + userId);
                request.getSession().setAttribute("userId", userId);
                // 上线id
                String onlineId = request.getParameter("onlineId");
                // 支付订单ID
                String payOrderId = request.getParameter("payOrderId");
                // 旧订单用户id（转单时必须传过来）
                String oldUserId = request.getParameter("oldUserId");
                if (!StringUtils.isAnyBlank(promoterId, onlineId) && !userId.equals(promoterId)) {
                    System.out.println("拦截器获取到的上线id为：" + onlineId);
                    // 线上微信回调地址
                    String encodeUrl = "https%3A%2F%2Fwww.duamai.com%2Fwxx-svr%2Fwxx%2Fresponse%2Fauthorizecode%3fappid%3d"
                            + SysContext.wxAppId;
                    // 线下微信回调地址
//                     String encodeUrl =
//                     "http%3A%2F%2Fxym.natapp1.cc%2Fwxx%2Fresponse%2Fauthorizecode%3fappid%3d"+SysContext.wxAppId;

                    String state = requestUrlss + "," + promoterId + "," + onlineId + ",invite";
                    String url = SITE.AUTHORIZE.getMessage() + "?appid=" + SysContext.wxAppId + "&redirect_uri="
                            + encodeUrl + "&response_type=code&scope=snsapi_userinfo&state=" + state;
                    System.out.println(url);
                    resp.sendRedirect(url);
                } else if (!StringUtils.isAnyBlank(payOrderId, oldUserId)
                        && (userId.equals("") || userId.equals("null") || userId == null)) {
                    System.out.println("拦截器获取到的支付订单id为：" + payOrderId);

//                    String backUrl = WxConfig.onLineURL + "/wechat/oauth2/myPreReg.htm";// 微信回调地址
//                    String encodeUrl = URLEncoder.encode(backUrl, "UTF-8");// 对url进行编码
                    // 线上微信回调地址
                    String encodeUrl = "https%3A%2F%2Fwww.duamai.com%2Fwxx-svr%2Fwxx%2Fresponse%2Fauthorizecode%3fappid%3d"
                            + SysContext.wxAppId;
                    // 线下微信回调地址
//                     String encodeUrl =
//                     "http%3A%2F%2Fxym.natapp1.cc%2Fwxx%2Fresponse%2Fauthorizecode%3fappid%3d"+
//                     SysContext.wxAppId;
                    String state = requestUrlss + "," + payOrderId + "," + oldUserId + ",transfer";
                    String url = SITE.AUTHORIZE.getMessage() + "?appid=" + SysContext.wxAppId + "&redirect_uri="
                            + encodeUrl + "&response_type=code&scope=snsapi_userinfo&state=" + state;
                    System.out.println(url);
                    resp.sendRedirect(url);
                } else {
                    System.out.println("2222222");
                    chain.doFilter(servletRequest, servletResponse);
                }
            }
        }
    }

    public Boolean urlDoFilter(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String url = request.getRequestURI();
        url = url.replaceFirst(SysContext.SYS_NAME, "");
        System.out.println("拦截器替换url后的值为：" + url);
        String SYS_KEY = request.getParameter("key");
        System.out.println("SYS_KEY====" + SYS_KEY);
        if (WECHAT_PUBLIC_INTERFACE.contains(url)) {
            System.out.println("=========访问公共接口:" + url);
            return true;
        } else {
            System.out.println("==========访问私有接口通过:" + url);
            return true;
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    public void initData() {
    }
}
