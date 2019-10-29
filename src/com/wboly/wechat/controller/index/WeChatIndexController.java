package com.wboly.wechat.controller.index;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.CookiesUtil;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 首页.java
 * @Author: nick
 */
@Controller
public class WeChatIndexController extends SysController {

    /**
     * @Name: 首页页面跳转
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/index/indexInfo")
    public ModelAndView indexInfo(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId != null) {
            mav.addObject("userId", userId);
        } else {
            userId = String.valueOf(request.getSession().getAttribute("loginId"));
        }
        mav.addObject("JSURL", request.getRequestURL());
        mav.setViewName("/htm/wechat/index/index");
        return mav;
    }

    /**
     * 获取首页推广上线商品信息 Title: promotionOnlineGoodsList Description:
     * 
     * @param request
     * @param response
     * @throws IOException
     * @date 2018年3月29日 上午11:55:38
     */
    @RequestMapping(value = "/wechat/index/getAllIndexData", method = RequestMethod.POST)
    public void promotionOnlineGoodsList(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取首页推广上线商品信息
        String results = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/onlinepromotion/list?promotionType=1");
        System.out.println("获取到的首页推广上线商品信息为：" + results);
        this.render(response, results);
    }

    /**
     * @Name: 首页获取用户是否关注
     */
    @RequestMapping(value = "/wechat/index/checkIsSubscribe")
    public void checkIsSubscribe(HttpServletRequest request, HttpServletResponse response) {
        // 获取当前登录用户编号
        String openid = CookiesUtil.getCookieKey(request);
//		String wxId = SysCache.getWeChatUserByColumn(request, "openid");
        System.out.println("获取到的用户openId为：" + openid);
        String results = "";
        try {
            results = OkhttpUtils.get(
                    SysContext.WXXURL + "wxx/request/issubscribe?appId=" + SysContext.wxAppId + "&openId=" + openid);
            System.out.println("获取到的用户是否关注返回值：" + results);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.render(response, results);
    }
}
