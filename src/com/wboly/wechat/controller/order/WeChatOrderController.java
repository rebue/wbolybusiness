package com.wboly.wechat.controller.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;
import com.wboly.system.sys.util.Base64EnOut;
import com.wboly.system.sys.util.JsonUtil;
import com.wboly.wechat.entity.OrderDetailTo;
import com.wboly.wechat.entity.OrderTo;

import rebue.wheel.NetUtils;
import rebue.wheel.OkhttpUtils;

/**
 * @Name: 微信 订单.java
 * @Author: nick
 */
@Controller
public class WeChatOrderController extends SysController {

    private static final Logger _log = LoggerFactory.getLogger(WeChatOrderController.class);

    /**
     * @Name: 所有订单售后页面跳转
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/allAfterSalePage")
    public ModelAndView orderAfterSalePage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            mav.setViewName("/htm/wechat/login/login");
        } else {
            mav.setViewName("/htm/wechat/order/orderAfterSale");
        }
        return mav;
    }

    /**
     * @Name: 申请退货页面跳转
     * @Author: nick
     */
    @SuppressWarnings("static-access")
    @RequestMapping(value = "/wechat/order/returnPage")
    public ModelAndView returnPage(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            mav.setViewName("/htm/wechat/login/login");
        } else {
            String returnData = request.getParameter("returnData");
            if (returnData != null) {
                Base64EnOut b64 = new Base64EnOut();
                returnData = b64.Decode(returnData);
                returnData = b64.Decode(returnData);
                System.out.println(returnData);
                mav.addObject("returnData", JsonUtil.jsonStringToMap(returnData));
            } else {
                mav.addObject("returnData", "");
            }
            mav.setViewName("/htm/wechat/order/returnGoods");
        }
        return mav;
    }

    /**
     * @throws IOException
     * @Name: 查看物流信息页面跳转
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/queryLogistics")
    public ModelAndView queryLogistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
        new ObjectMapper();

        String orderInfo = OkhttpUtils
                .get(SysContext.ORDERURL + "/ord/order/info?orderCode=" + request.getParameter("orderId"));
        List<Map<String, Object>> list = JsonUtil.listMaps(orderInfo);
        System.out.println(String.valueOf(list));
        String shipperCode = String.valueOf(list.get(0).get("shipperCode"));
        String logisticCode = String.valueOf(list.get(0).get("logisticCode"));
        ModelAndView mav = new ModelAndView();
        mav.addObject("shipperCode", shipperCode);
        mav.addObject("logisticCode", logisticCode);
        mav.setViewName("/htm/wechat/order/queryLogistics");
        return mav;
    }

    /**
     * @throws IOException
     * @Name: 买家部分退货提交
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/returnGoods")
    public void ReturnPartofGoods(HttpServletRequest request, HttpServletResponse response,
            @RequestParam Map<String, Object> map) throws TException, IOException {
        _log.info("添加用户退货信息的请求参数为：{}", String.valueOf(map));
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            this.render(response, "{\"msg\":\"您没有登录\",\"result\":-11}");
            return;
        }
        map.put("applicationOpId", userId);
        _log.info("添加用户退货信息的参数为：{}", String.valueOf(map));
        // 添加用户退货信息
        String results = OkhttpUtils.postByJsonParams(SysContext.ORDERURL + "/ord/return", map);
        _log.info("添加用户退货信息的返回值为：{}", results);
        if (results == null || results.equals("") || results.equals("[]")) {
            _log.error("添加用户退货信息出错，返回值为：{}", results);
            this.render(response, "{\"msg\":\"提交失败\",\"result\":-10}");
            return;
        }
        this.render(response, results);
    }

    /**
     * @Name: 订单详情页面
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/myOrderDetail")
    public ModelAndView orderDetailPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("order_data",
                request.getParameter("order_data") != null ? request.getParameter("order_data") : 0);
        mav.setViewName("/htm/wechat/order/orderDetail");
        return mav;
    }

    /**
     * @Name: 我的订单页面
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/myOrders")
    public ModelAndView ordersPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            mav.setViewName("/htm/wechat/login/login");
        } else {

            String type = request.getParameter("type");
            if (type != null && !type.equals("")) {
                switch (type) {
                case "stayPay":
                    mav.addObject("OrderType", 1);
                    mav.addObject("allOrder", "");
                    mav.addObject("stayTake", "");
                    mav.addObject("stayReturn", "");
                    mav.addObject("stayPay", "mui-active");
                    break;
                case "stayTake":
                    mav.addObject("OrderType", 3);
                    mav.addObject("allOrder", "");
                    mav.addObject("stayTake", "mui-active");
                    mav.addObject("stayReturn", "");
                    mav.addObject("stayPay", "");
                    break;
                case "stayReturn":
                    mav.addObject("OrderType", 4);
                    mav.addObject("allOrder", "");
                    mav.addObject("stayTake", "");
                    mav.addObject("stayReturn", "mui-active");
                    mav.addObject("stayPay", "");
                    break;
                }
            } else {
                mav.addObject("OrderType", 0);
                mav.addObject("allOrder", "mui-active");
                mav.addObject("stayTake", "");
                mav.addObject("stayReturn", "");
                mav.addObject("stayPay", "");
            }

            mav.setViewName("/htm/wechat/order/orders");
        }
        return mav;
    }

    /**
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     * @Name: 用户放弃订单
     * @Author: nick
     */
    @SuppressWarnings({ "rawtypes" })
    @RequestMapping(value = "/wechat/order/cancelOrder")
    public void CancelShopOrder(HttpServletRequest request, HttpServletResponse response)
            throws TException, JsonParseException, JsonMappingException, IOException {

        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }
        String orderId = request.getParameter("orderId");
        String cancelType = request.getParameter("cancelType");
        if (orderId == null || orderId.equals("") || cancelType == null || cancelType.equals("")) {
            this.render(response, "{\"message\":\"请求参数有误\",\"flag\":false}");
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", orderId);
        map.put("userId", userId);
        map.put("cancelReason", "买家取消订单");
        map.put("cancelingOrderOpId", userId);
        System.out.println("取消订单的参数为：{}" + String.valueOf(map));
        // 取消订单
        String results = OkhttpUtils.putByJsonParams(SysContext.ORDERURL + "/ord/order/cancel", map);
        System.out.println("取消订单的返回值为：" + results);
        ObjectMapper mapper = new ObjectMapper();
        Map resultMap = mapper.readValue(results, Map.class);
        int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
        if (result > 0) {
            this.render(response, "{\"message\":\"订单已取消\",\"flag\":true}");
        } else {
            this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
        }
    }

    /**
     * @Name: 用户订单列表
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/getOrders")
    public void UserOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = SysCache.getWeChatUserByColumn(request, "userId");

        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }

        // 开始条数
        String start = request.getParameter("start");
        // 结束条数
        String limit = request.getParameter("limit");
        // 订单状态
        String orderState = request.getParameter("orderState");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!orderState.equals("") && orderState != null && !orderState.equals("null") && !orderState.equals("0")) {
            map.put("orderState", orderState);
        }
        map.put("start", start);
        map.put("limit", limit);
        map.put("userId", userId);
        System.out.println("获取用户订单的参数为：" + String.valueOf(map));
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/info", map);
        System.out.println("获取用户订单的返回值为：" + results);
        this.render(response, "{\"message\":" + results + ",\"flag\":true}");
    }

    /**
     * @Name: 检查订单页面
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/checkorderpage")
    public ModelAndView checkOrder(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/htm/wechat/order/checkOrder");
        return mav;
    }

    /**
     * @throws IOException
     * @Name: 生成订单
     * @Author: nick
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping("/wechat/order/createOrder")
    public void CreateShopOrder(HttpServletRequest request, HttpServletResponse response,
            @RequestParam Map<String, Object> map) throws TException, IOException {
        _log.info("生成订单的请求参数为: {}", map);
        // 当前用户编号
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        _log.info("生成订单获取到的用户编号为: {}", userId);
        if (userId == null || userId.equals("") || userId.equals("null")) {
            this.render(response, "{\"message\":\"您未登录,请登录后再试...\",\"flag\":false}");
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        OrderTo orderTo = new OrderTo();
        orderTo.setUserId(Long.parseLong(userId));
        orderTo.setAddrId(Long.parseLong(String.valueOf(map.get("addrId"))));
        orderTo.setOrderMessages(String.valueOf(map.get("orderMessages")));
        _log.info("生成订单将订单详情转换为List<Map>的参数为: {}", String.valueOf(map.get("details")));
        List<Map<String, Object>> detailList = JsonUtil.listMaps(String.valueOf(map.get("details")));
        _log.info("生成订单将订单详情转换为List<Map>的返回值为: {}", detailList.toString());
        List<OrderDetailTo> detailsList = new ArrayList<OrderDetailTo>();
        for (int i = 0; i < detailList.size(); i++) {
            System.out.println(String.valueOf(detailList.get(i)));
            OrderDetailTo orderDetailTo = new OrderDetailTo();
            orderDetailTo.setOnlineId(Long.parseLong(String.valueOf(detailList.get(i).get("onlineId"))));
            orderDetailTo.setOnlineSpecId(Long.parseLong(String.valueOf(detailList.get(i).get("onlineSpecId"))));
            orderDetailTo.setCartId(Long.parseLong(String.valueOf(detailList.get(i).get("cartId"))));
            orderDetailTo.setBuyCount(Integer.parseInt(String.valueOf(detailList.get(i).get("buyCount"))));

            if (detailList.get(i).get("isInviter") != null && (boolean) detailList.get(i).get("isInviter") != false) {

                orderDetailTo.setInviteId(Long.parseLong(String.valueOf(detailList.get(i).get("inviteId"))));
            }
            detailsList.add(orderDetailTo);
        }
        orderTo.setDetails(detailsList);
        _log.info("生成订单请求ord的参数为: {}", orderTo);
        System.err.println("用户下订单的参数为=====" + orderTo);
        String results = OkhttpUtils.postByJsonParams(SysContext.ORDERURL + "/ord/order", orderTo);
        _log.info("生成订单请求ord的参数为: {}", results);
        if (results != null && !results.equals("") && !results.equals("null") && !results.equals("{}")) {
            Map resultMap = mapper.readValue(results, Map.class);
            int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
            if (result > 0) {
                this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"payOrderId\":\""
                        + resultMap.get("payOrderId") + "\",\"flag\":true}");
            } else {
                this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
            }
        } else {
            this.render(response, "{\"message\":\"下单失败\",\"flag\":false}");
        }
    }

    /**
     * 买家确认收货 Title: aboutCinfirmReceipt Description:
     * 
     * @param request
     * @param response
     * @throws IOException
     * @date 2018年4月14日 下午1:37:41
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping("/wechat/order/aboutCinfirmReceipt")
    public void aboutCinfirmReceipt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 当前用户编号
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId != null && !userId.equals("") && !userId.equals("null")) {
            // 订单编号
            String orderId = request.getParameter("orderId");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("userId", userId);
            map.put("orderId", orderId);
            map.put("receivedOpId", userId);
            map.put("mac", NetUtils.getFirstMacAddrOfLocalHost());
            map.put("ip", NetUtils.getFirstIpOfLocalHost());
            System.err.println("用户订单签收的参数为：" + String.valueOf(map));
            String signInResult = OkhttpUtils.putByFormParams(SysContext.ORDERURL + "/ord/order/ordersignin", map);
            System.err.println("用户订单签收的返回值为：" + signInResult);
            ObjectMapper mapper = new ObjectMapper();
            Map resultMap = mapper.readValue(signInResult, Map.class);
            int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
            if (result != 1) {
                this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":false}");
            } else {
                this.render(response, "{\"message\":\"" + resultMap.get("msg") + "\",\"flag\":true}");
            }
        } else {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
        }
    }

    /**
     * @Name: 用户待返现订单列表
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/getCashBackOrders")
    public void getCashBackOrders(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = SysCache.getWeChatUserByColumn(request, "userId");

        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }

        // 开始条数
        String pageNum = request.getParameter("pageNum");
        // 结束条数
        String pageSize = request.getParameter("pageSize");
        // 订单状态
        String orderState = request.getParameter("orderState");
        Map<String, Object> map = new HashMap<String, Object>();
        if (!orderState.equals("") && orderState != null && !orderState.equals("null") && !orderState.equals("0")) {
            map.put("orderState", orderState);
        }
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("userId", userId);
        System.out.println("获取用户订单的参数为：" + String.valueOf(map));
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/getCashBackOrders", map);
        System.out.println("获取用户订单的返回值为：" + results);
        this.render(response, "{\"message\":" + results + ",\"flag\":true}");
    }

    /**
     * 获取返现信息
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/wechat/order/getCashBack")
    public void getCashBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = SysCache.getWeChatUserByColumn(request, "userId");

        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }

        // 开始条数
        String pageNum = request.getParameter("pageNum");
        // 结束条数
        String pageSize = request.getParameter("pageSize");
        // 板块类型
        String subjectType = request.getParameter("subjectType");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("userId", userId);
        map.put("commissionState", 1);
        map.put("subjectType", subjectType);
        System.out.println("获取用户返现信息的参数为：" + String.valueOf(map));
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/orderdetail", map);
        System.out.println("获取用户订单的返回值为：" + results);
        this.render(response, "{\"message\":" + results + ",\"flag\":true}");
    }

    /**
     * 根据订单ID获取订单物流信息
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "/wechat/order/getOrderLogisticInfo")
    public void getOrderLogisticInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        // 订单
        String orderId = request.getParameter("orderId");
        map.put("orderId", orderId);
        System.out.println("获取订单物流信息参数为：" + String.valueOf(map));
        String results = OkhttpUtils.get(SysContext.KDIURL + "/kdi/logistic/getLogisticInfo", map);
        System.out.println("获取用户订单物流信息返回值为：" + results);
        this.render(response, "{\"message\":" + results + ",\"flag\":true}");
    }

    /**
     * 取消退货
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/wechat/order/cancelReturn")
    public void cancelReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId.equals("")) {
            this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
            return;
        }
        // 订单
        String orderId = request.getParameter("orderId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", orderId);
        map.put("cancelOpId", userId);
        map.put("applicationState", -1);
        _log.info("取消退货的参数为：{}", String.valueOf(map));
        String result = OkhttpUtils.putByJsonParams(SysContext.ORDERURL + "/ord/return/cancel", map);
        _log.info("取消退货的返回值为：{}", result);
        this.render(response, "{\"message\":" + result + ",\"flag\":true}");
    }

    /**
     * 修改支付订单Id
     * 
     * @param request
     * @param response
     * @param orderId
     * @throws IOException
     */
    @RequestMapping("/wechat/order/modifyPayOrderId")
    public void modifyPayOrderId(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("orderId") String orderId) throws IOException {
        _log.info("修改支付订单Id的参数为: {}", orderId);
        String results = OkhttpUtils.put(SysContext.ORDERURL + "/ord/order/modifypayorderid?id=" + orderId);
        _log.info("修改支付订单id的返回值为: {}", results);
        this.render(response, results);
    }

    /**
     * @Name: 根据用户id获取用户待结算全返金额
     * @Author: nick
     */
    @RequestMapping(value = "/wechat/order/commissionTotal", method = RequestMethod.GET)
    public void UserCommissionTotal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("根据用户id获取用户待结算全返金额");
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        _log.info("根据用户id获取用户待结算全返金额参数为-------------: {}", userId);
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/commissionTotal?userId=" + userId);
        this.render(response, results);
    }

    /**
     * 转移订单
     * 
     * @param request
     * @param response
     * @throws IOException
     */
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/wechat/order/transfer", method = RequestMethod.GET)
    public ModelAndView transferOrder(HttpServletRequest request, HttpServletResponse response) throws IOException {
        _log.info("开始转移订单");
        ModelAndView andView = new ModelAndView();
        // 当前登录用户id
        String userId = SysCache.getWeChatUserByColumn(request, "userId");
        if (userId == null || userId.equals("") || userId.equals("null")) {
            andView.setViewName("/htm/wechat/prompt/500");
            return andView;
        }
        // 订单旧用户id
        String oldUserId = request.getParameter("oldUserId");
        // 支付订单id
        String payOrderId = request.getParameter("payOrderId");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("payOrderId", payOrderId);
        map.put("oldUserId", oldUserId);
        map.put("newUserId", userId);
        _log.info("转移订单的请求参数为：{}", String.valueOf(map));
        String results = OkhttpUtils.get(SysContext.ORDERURL + "/ord/order/shift", map);
        _log.info("转移订单的返回值为：{}", results);
        if (results == null || results.equals("") || results.equals("null")) {
            _log.error("转移订单出现异常，请求的参数为：{}", String.valueOf(map));
            andView.setViewName("/htm/wechat/prompt/500");
            return andView;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map m = mapper.readValue(results, Map.class);
        int result = Integer.parseInt(String.valueOf(m.get("result")));
        
        // 这里的可能返回值1(成功) -1(失败) -2(警告-超过购买数量)，但是为了友好显示只要不是1都跳转到超过购买数量页面而不是500页面
        if (result != 1) {
            andView.setViewName("/htm/wechat/order/limitCount");
            return andView;
        }
        
//        if (result != 1) {
//            andView.setViewName("/htm/wechat/prompt/500");
//            return andView;
//        }
        andView.addObject("userId", userId);
        andView.addObject("payOrderId", payOrderId);
        andView.addObject("orderMoney",
                new BigDecimal(String.valueOf(m.get("realMoney"))).setScale(2, BigDecimal.ROUND_HALF_UP));
        andView.setViewName("/htm/wechat/pay/paycenter");
        return andView;
    }
}
