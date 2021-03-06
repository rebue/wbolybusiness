package com.wboly.wechat.controller.pay;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.CookiesUtil;
import com.wboly.system.sys.util.HttpUtil;
import com.wboly.system.sys.util.IpUtil;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.system.sys.util.MD5CodeUtil;
import com.wboly.system.sys.util.MacAddressUtil;
import com.wboly.system.sys.util.wx.WXSignUtils;
import com.wboly.system.sys.util.wx.WeixinUtil.SITE;
import com.wboly.system.sys.util.wx.WxConfig;
import rebue.wheel.OkhttpUtils;

/**
 * @Name: 商超支付
 * @Author: nick
 */
@Controller
public class WeChatPayController extends SysController {

    private static final Logger _log = LoggerFactory.getLogger(WeChatPayController.class);

    /**
     * @Name: 微信支付授权，先获取 code，跳转 url 通过 code 获取 openId
     * @Author: knick
     */
    @RequestMapping(value = "/wechat/pay/authpay", params = { "orderId" })
    public String userAuthPay(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("orderId") String orderId) {
        try {
            System.err.println("-----------------------开始进入微信支付授权方法---------------------------");

            // 授权后要跳转的链接
            String backUri = WxConfig.onLineURL + "/wechat/pay/toPay.htm";
            backUri = backUri + "?orderId=" + orderId;
            // URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
            backUri = URLEncoder.encode(backUri, "UTF-8");
            // scope 参数视各自需求而定，这里用 scope=snsapi_base
            // 不弹出授权页面直接授权目的只获取统一支付接口的openid
            String url = SITE.AUTHORIZE.getMessage() + "?appid=" + WxConfig.appid + "&redirect_uri=" + backUri
                    + "&response_type=code&scope=snsapi_base&state=" + orderId + "#wechat_redirect";
            System.err.println("微信支付授权url:" + url);
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @throws InterruptedException
     * @throws IOException
     * @Name: 微信支付
     * @Author: knick
     */
    @RequestMapping(value = "/wechat/pay/toPay", params = { "payOrderId" })
    public ModelAndView wechatToPay(HttpServletRequest request, HttpServletResponse response, Model model,
            @RequestParam("payOrderId") String payOrderId) throws InterruptedException, IOException {

        ModelAndView mav = new ModelAndView();

        System.err.println("-----------------------开始进入微信支付授权回调方法---------------------------");

        String openid = CookiesUtil.getCookieKey(request);

        if (null == openid || "".equals(openid) || "null".equals(openid)) {
            System.err.println("微信支付:获取不到 openid 数据");
            mav.addObject("page", "{\"message\":\"无法获取您的信息,请授权登录\",\"flag\":false,\"page\":3}");
            mav.setViewName("/htm/wechat/prompt/alert");
            return mav;
        }

        System.err.println(openid);

        if (openid.contains("null")) {
            System.err.println("openid.contains('null')");
            mav.addObject("page", "{\"message\":\"无法获取您的信息,请授权登录\",\"flag\":false,\"page\":3}");
            mav.setViewName("/htm/wechat/prompt/alert");
            return mav;
        }

        if (null == payOrderId || "null".equals(payOrderId) || "".equals(payOrderId)) {
            payOrderId = "0";
        }

        System.err.println("微信支付订单:" + payOrderId);

        String buyerUid = SysCache.getWeChatUserByColumn(request, "userId");
        _log.info("获取微信预支付id根据订单支付id查询订单信息的参数为: {}", payOrderId);
        String orderdetail = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/listselective?payOrderId=" + payOrderId);
        _log.info("获取微信预支付id根据订单支付id查询订单信息的返回值为: {}", orderdetail);
        List<Map<String, Object>> orderdetailInfo = JsonUtil.listMaps(orderdetail);
        _log.info("获取微信预支付id根据订单支付id查询订单信息返回值转换为list<Map>的返回值为: {}", String.valueOf(orderdetailInfo));
        if (orderdetailInfo == null || orderdetailInfo.size() < 1) {
            System.err.println("该订单号不存在");
            mav.addObject("page", "{\"message\":\"该订单信息不存在。\",\"flag\":false,\"page\":2}");
            mav.setViewName("/htm/wechat/prompt/alert");
            return mav;
        }

        StringBuilder goodsName = new StringBuilder();// 商品名称

        for (int i = 0; i < orderdetailInfo.size(); i++) {
            goodsName.append(orderdetailInfo.get(i).get("onlineTitle") + ",");
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", buyerUid);
        map.put("wxId", openid); // 用户微信id（根据不同情况可能是unionid或者openid）
        map.put("orderId", payOrderId);// 订单编号
        map.put("tradeTitle", "大卖网络-商品购买"); // 支付交易标题
        String[] goodsNames = String.valueOf(goodsName).split(",");
        String tradeDetail = "";
        if (goodsNames.length > 2) {
            tradeDetail = goodsNames[0] + "," + goodsNames[1] + "等。。。";
        } else {
            tradeDetail = goodsNames[0];
        }

        if (tradeDetail.length() > 150) {
            tradeDetail = tradeDetail.substring(0, tradeDetail.length() - 10) + "等。。。";
        }

        // 订单真实总金额
        BigDecimal realMoney = BigDecimal.ZERO;
        for (int i = 0; i < orderdetailInfo.size(); i++) {
            realMoney = realMoney
                    .add(BigDecimal.valueOf(Double.parseDouble(orderdetailInfo.get(i).get("realMoney").toString())));
        }

        map.put("tradeDetail", "购买商品为：" + tradeDetail); // 支付交易详情
        map.put("tradeAmount", realMoney); // 支付交易金额（单位为元）
        map.put("ip", IpUtil.getIp(request)); // 用户ip地址

        // String finalsign = "";
        String prepay_id = "";

        System.err.println("获取微信预支付Id请求参数：" + map);
        String result = OkhttpUtils.postByJsonParams(SysContext.WXXURL + "/wxx/wxpay/request/prepay", map);
        System.err.println("获取微信预支付Id返回结果：" + result);

        if (null == result || "".equals(result) || "null".equals(result)) {
            System.err.println("请求超时");
        } else {
            String prepayId = JsonUtil.GetJsonValue(result, "prepayId");
            if (prepayId != null && !"".equals(prepayId)) {
                String[] split = prepayId.split(";");
                if (split.length == 2) {
                    // finalsign = split[1];
                }
                prepay_id = split[0];
            }
        }

        if ("".equals(prepay_id)) {
            System.err.println("订单已生成,微信预支付订单出错");
            mav.addObject("page", "{\"message\":\"订单已生成,微信预支付订单出错\",\"flag\":false,\"page\":2}");
            mav.setViewName("/htm/wechat/prompt/alert");
            return mav;
        }

        String nonce_str = UUID.randomUUID().toString().replaceAll("-", "");// 随机字符

        SortedMap<Object, Object> finalpackage = new TreeMap<Object, Object>();

        Long timestamp = System.currentTimeMillis() / 1000;// 当前时间戳

        String packages = "prepay_id=" + prepay_id;

        finalpackage.put("appId", WxConfig.appid);
        finalpackage.put("timeStamp", timestamp.toString());
        finalpackage.put("nonceStr", nonce_str);
        finalpackage.put("package", packages);
//		finalpackage.put("prepay_id", prepay_id);
        finalpackage.put("signType", WxConfig.signType);

        String finalsign1 = WXSignUtils.createSign("UTF-8", finalpackage);

        mav.addObject("appid", WxConfig.appid);
        mav.addObject("timeStamp", timestamp);
        mav.addObject("nonceStr", nonce_str);
        mav.addObject("packageValue", packages);
        mav.addObject("signType", WxConfig.signType);
        mav.addObject("sign", finalsign1);
        mav.addObject("orderId", payOrderId);
        mav.setViewName("/htm/wechat/pay/payPage");
        return mav;
    }

    /**
     * @Name: 获取V支付余额
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/pay/getVBalance", method = RequestMethod.POST)
    public void getPayBalance(HttpServletRequest request, HttpServletResponse response) {

        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }

        String result = HttpUtil.getUrl(SysContext.VPAYURL + "/account/funds?userId=" + userId);
        System.err.println("V支付余额:" + result);
        if (result != null) {
            result = result.replaceAll("\"", "");
        }
        BigDecimal balance = new BigDecimal(String.valueOf(JsonUtil.getJsonValue(result, "balance"))); // 用户余额
        BigDecimal cashback = new BigDecimal(String.valueOf(JsonUtil.getJsonValue(result, "cashback")));
        double totalAmount = balance.add(cashback).doubleValue();
        if (result == null) {
            this.render(response, "{\"message\":\"获取余额超时了 >_<\",\"flag\":false}");
            return;
        }
        if (result.equals("-1")) {
            this.render(response, "{\"message\":\"secretKey Incorrect!\",\"flag\":false}");
            return;
        }
        this.render(response, "{\"message\":\"" + totalAmount + "\",\"flag\":true}");
    }

    /**
     * @throws IOException
     * @Name: 跳转至支付页面
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/pay/paycenterPage")
    public ModelAndView payPage(HttpServletRequest request) throws IOException {
        ModelAndView mav = new ModelAndView();
        System.out.println("开始跳转至订单支付页面");
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        System.out.println("获取到的用户编号为：" + userId);
        if (!"".equals(userId)) {
            mav.addObject("userId", userId);
            mav.setViewName("/htm/wechat/pay/paycenter");
        } else {
            mav.setViewName("/htm/wechat/login/login");
            return mav;
        }

        String orderId = request.getParameter("payOrderId");
        System.out.println("获取到的订单编号为：" + orderId);
        if (orderId == null || orderId.equals("")) {
            System.err.println("请求参数有误[订单编号有误]");
            mav.setViewName("/htm/wechat/login/login");
            return mav;
        } else {
            mav.addObject("payOrderId", orderId);
            String order = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/listselective?payOrderId=" + orderId);
            List<Map<String, Object>> orderdetailInfo = JsonUtil.listMaps(order);
            _log.info("获取微信预支付id根据订单支付id查询订单信息返回值转换为list<Map>的返回值为: {}", String.valueOf(orderdetailInfo));
            if (orderdetailInfo == null || orderdetailInfo.size() < 1) {
                System.err.println("该订单号不存在");
                mav.addObject("page", "{\"message\":\"该订单信息不存在。\",\"flag\":false,\"page\":2}");
                mav.setViewName("/htm/wechat/prompt/alert");
                return mav;
            }
            // 订单总金额
            BigDecimal orderMoney = new BigDecimal("0");
            for (int i = 0; i < orderdetailInfo.size(); i++) {
                orderMoney = orderMoney.add(new BigDecimal(String.valueOf(orderdetailInfo.get(i).get("realMoney"))))
                        .setScale(4, BigDecimal.ROUND_HALF_UP);
            }
            _log.info("跳转至支付页面获取到的订单实际金额为: {}", orderMoney);
            mav.addObject("orderMoney", orderMoney);
        }
        return mav;
    }

    /**
     * @throws IOException
     * @Name: 支付成功页面
     * @Author: knick
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/wechat/pay/success", params = { "type", "orderId" })
    public ModelAndView successPage(@RequestParam String orderId, @RequestParam String type, HttpServletRequest request)
            throws IOException {
        System.err.println("----------------------- 支付成功 ---------------------------");
        ModelAndView mav = new ModelAndView();
        
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/getOderPoint?payOrderId=" + orderId);
        System.out.println("支付成功后获取用户累计收益，总积分，本次积分返回值为：" + results);
        ObjectMapper mapper = new ObjectMapper();
       
        Map resultMap = mapper.readValue(results, Map.class);
        System.out.print(resultMap.get("totalIncome"));
        System.out.print(resultMap.get("point"));
        System.out.print(resultMap.get("thisPoint"));
        
        String totalIncome =String.valueOf(resultMap.get("totalIncome"));
        String point =String.valueOf(resultMap.get("point"));
        String thisPoint =String.valueOf(resultMap.get("thisPoint"));
        mav.addObject("orderId", orderId);
        mav.addObject("totalIncome", totalIncome);
        mav.addObject("point", point);
        mav.addObject("thisPoint", thisPoint);
        mav.setViewName("/htm/wechat/pay/paysuccess");

        return mav;
    }

    /**
     * 获取v支付预支付Id
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/wechat/pay/createVpayPrepaymentId", method = RequestMethod.POST)
    public void createVpayPrepaymentId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String buyerUid = request.getParameter("userId"); // 用户编号
        String orderId = request.getParameter("orderId"); // 订单编号

        if (buyerUid == null || buyerUid.equals("") || buyerUid.equals("null")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }

        if (orderId == null || orderId.equals("") || orderId.equals("null")) {
            this.render(response, "{\"message\":\"订单不能为空\",\"flag\":false}");
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("id", orderId);
        map.put("orderId", orderId);
        map.put("buyerUid", buyerUid);
        map.put("tradeTitle", "大卖网络-商品购买"); // 支付交易标题
        map.put("mac", MacAddressUtil.getLocalMac()); // 用户mac地址
        map.put("ip", IpUtil.getIp(request)); // 用户ip地址
        map.put("userId", buyerUid);
        _log.info("获取v支付预支付id根据支付订单id查询订单信息的参数为: {}", orderId);
        // 获取商品订单
        String orderdetail = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/listselective?payOrderId=" + orderId);
        _log.info("获取v支付预支付id根据支付订单id查询订单信息的返回值为: {}", orderdetail);
        List<Map<String, Object>> orderInventoryList = JsonUtil.listMaps(orderdetail);
        if (orderInventoryList.size() == 0) {
            this.render(response, "{\"message\":\"该订单不存在\",\"flag\":false}");
            return;
        }

        // 订单真实总金额
        BigDecimal realMoney = BigDecimal.ZERO;
        for (int i = 0; i < orderInventoryList.size(); i++) {
            realMoney = realMoney
                    .add(BigDecimal.valueOf(Double.parseDouble(orderInventoryList.get(i).get("realMoney").toString())));
        }

        map.put("orderCode", orderId);
        map.put("tradeAmount", realMoney);

        String goodsNames = "";// 商品名称
        if (orderInventoryList.size() >= 2) {
            goodsNames = orderInventoryList.get(0).get("onlineTitle") + ","
                    + orderInventoryList.get(1).get("onlineTitle") + "等。。。";
        } else {
            goodsNames = String.valueOf(orderInventoryList.get(0).get("onlineTitle"));
        }

        if (goodsNames.length() > 150) {
            goodsNames = goodsNames.substring(0, goodsNames.length() - 10) + "等。。。";
        }
        map.put("tradeDetail", "您在大卖网络购买的商品订单为：" + orderId + "，所购买的商品为：" + goodsNames);

        System.err.println("微信端获取v支付预支付Id的参数为：{}" + String.valueOf(map));
        // 获取v支付预支付Id
        String vpayPrepaymentResult = OkhttpUtils.postByJsonParams(SysContext.VPAYURL + "/vpay/prepay", map);
        // 判断v支付生成预支付Id是否为空
        if (vpayPrepaymentResult == null || vpayPrepaymentResult.equals("") || vpayPrepaymentResult.equals("null")) {
            this.render(response, "{\"message\":\"v支付生成预支付Id出错\",\"flag\":false}");
            return;
        }

        // v支付预支付id
        String prepayId = JsonUtil.GetJsonValue(vpayPrepaymentResult, "prepayId");
        // 是否需要输入支付密码
        String requirePayPswd = JsonUtil.getJSONValue(vpayPrepaymentResult, "requirePayPswd");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("prepayId", prepayId);
        resultMap.put("requirePayPswd", requirePayPswd);
        resultMap.put("flag", true);

        System.err.println(JsonUtil.ObjectToJson(resultMap));

        this.render(response, JsonUtil.ObjectToJson(resultMap));
        return;
    }

    /**
     * 门店订单使用v支付支付
     * 
     * @param request
     * @param response
     *            2018年1月18日14:12:15
     * @throws IOException
     */
    @RequestMapping(value = "/wechat/pay/shopOrderPay", method = RequestMethod.POST)
    public void shopOrderPay(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.err.println("==============订单支付开始============");
        String prepayId = request.getParameter("prepayId"); // v支付预支付id
        String requirePayPswd = request.getParameter("requirePayPswd");
        String umac = MacAddressUtil.getLocalMac(); // 用户mac地址
        String uip = IpUtil.getIp(request); // 用户ip地址

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("prepayId", prepayId);
        map.put("mac", umac);
        map.put("ip", uip);
        map.put("payPswd", MD5CodeUtil.md5("wboly.com"));

        if (!requirePayPswd.equals("false")) {
            String vpassword = request.getParameter("pwd"); // v支付密码
            map.put("payPswd", vpassword);
        }
        System.err.println("支付的参数为：" + map.toString());
        // 订单支付
        String result = OkhttpUtils.postByJsonParams(SysContext.VPAYURL + "/vpay/pay", map);
        String results = JsonUtil.getJSONValue(result, "result");
        System.err.println("调用v支付返回值为：" + result);
        if (results == null || results.equals("") || results.equals("null")) {
            this.render(response, "{\"message\":\"请求V支付出错\",\"flag\":false}");
            return;
        }

        if (results.equals("0")) {
            this.render(response, "{\"message\":\"缓存失败\",\"flag\":false}");
        } else if (results.equals("-1")) {
            this.render(response, "{\"message\":\"参数不正确\",\"flag\":false}");
        } else if (results.equals("-2")) {
            this.render(response, "{\"message\":\"没有找到预支付信息\",\"flag\":false}");
        } else if (results.equals("-3")) {
            this.render(response, "{\"message\":\"找不到用户信息\",\"flag\":false}");
        } else if (results.equals("-4")) {
            this.render(response, "{\"message\":\"密码错误\",\"flag\":false}");
        } else if (results.equals("-5")) {
            this.render(response, "{\"message\":\"账号被锁定\",\"flag\":false}");
        } else if (results.equals("-6")) {
            this.render(response, "{\"message\":\"余额或返现金不足\",\"flag\":false}");
        } else if (results.equals("-7")) {
            this.render(response, "{\"message\":\"订单已经支付\",\"flag\":false}");
        } else if (results.equals("1")) {
            this.render(response, "{\"message\":\"成功支付\",\"flag\":true}");
        } else {
            this.render(response, "{\"message\":\"支付失败\",\"flag\":false}");
        }
    }

}
