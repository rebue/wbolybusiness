package com.wboly.wechat.controller.cart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wboly.system.sys.spring.SysController;
import com.wboly.system.sys.system.SysCache;
import com.wboly.system.sys.system.SysContext;

import rebue.wheel.OkhttpUtils;

/**
 * @Name: 购物车
 * @Author: nick
 */
@Controller
public class WeChatCartController extends SysController {

	/**
	 * 用户删除购物车商品
	 * 
	 * @param entity
	 * @param response
	 * @throws TException
	 * @throws IOException 
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/cart/delUserCart")
	public void delCart(HttpServletRequest request, HttpServletResponse response) throws TException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (!userId.equals("")) {
			String cartId = String.valueOf(request.getParameter("cartId"));
			if (cartId.equals("") || cartId.equals("null") || cartId == null) {
				this.render(response, "{\"message\":\"购物车为空\",\"flag\":false}");
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", cartId);
			map.put("userId", userId);
			System.err.println("删除购物车的参数为：" + map.toString());
			String results = OkhttpUtils.delete(SysContext.ONLINEURL + "/onl/cart?id=" + cartId + "&userId=" + userId);
			System.err.println("删除购物车返回值为：" + results);
			if (results != null && !results.equals("") && !results.equals("null")) {
				ObjectMapper mapper = new ObjectMapper();
				Map resultMap = mapper.readValue(results, Map.class);
				int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
				if (result < 0) {
					this.render(response, "{\"message\":\"删除失败\",\"flag\":false}");
					return;
				} else {
					this.render(response, "{\"message\":\"删除成功\",\"flag\":true}");
					return;
				}
			}
			this.render(response, "{\"message\":\"删除失败\",\"flag\":false}");
		} else {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
		}
	}

	/**
	 * @Name: 购物车页面跳转
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/cart/shoppingcart")
	public ModelAndView shoppingCatJump() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/htm/wechat/goods/shoppingCart");
		return mav;
	}

	/**
	 * @Name: 获取购物车列表
	 * @throws Exception
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/cart/getcartlist")
	public void getShoppingCartList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取当前用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (!userId.equals("")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			// 获取购物车列表
			String results = OkhttpUtils.get(SysContext.ONLINEURL + "/onl/cart", map);
			if (results != null && !results.equals("") && !results.equals("null")) {
				this.render(response, "{\"message\":" + results + ",\"flag\":true}");
			} else {
				this.render(response, "{\"message\":\"购物车无商品。\",\"flag\":false}");
			}
		} else {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
		}
	}

	/**
	 * 列表页加入购物车 Title: addShoppingCart1 Description:
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @date 2018年3月29日 下午1:03:51
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/wechat/cart/listAddCart", method = RequestMethod.POST)
	public void listAddCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 获取当前用户编号
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId == null || userId.equals("") || userId.equals("null")) {
			this.render(response, "{\"message\":\"您没有登录\",\"flag\":false}");
			return;
		}
		// 上线编号
		String onlineId = request.getParameter("onlineId");
		// 上线编号
		String specId = request.getParameter("specId");
		// 要加入购物车的数量
		String cartCount = request.getParameter("cartCount");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("onlineId", onlineId);
		map.put("onlineSpecId", specId);
		map.put("userId", userId);
		map.put("cartCount", cartCount);
		System.err.println("加入购物车的参数为：" + map.toString());
		String results = OkhttpUtils.postByFormParams(SysContext.ONLINEURL + "/onl/cart", map);
		System.err.println("加入购物车的返回值为：" + results);
		if (results == null || results.equals("") || results.equals("null")) {
			this.render(response, "{\"message\":\"加入购物车出错\",\"flag\":false}");
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		Map resultMap = mapper.readValue(results, Map.class);
		int result = Integer.parseInt(String.valueOf(resultMap.get("result")));
		if (result == 1) {
			this.render(response, "{\"message\":\"加入购物车成功\",\"flag\":true, \"cartCount\":" + resultMap.get("cartCount") + "}");
		} else if (result == -1) {
			this.render(response, "{\"message\":\"商品库存不足\",\"flag\":false, \"cartCount\":" + resultMap.get("cartCount") + "}");
		} else {
			this.render(response, "{\"message\":\"加入购物车失败\",\"flag\":false, \"cartCount\":" + resultMap.get("cartCount") + "}");
		}
	}

	/**
	 * @throws IOException
	 * @throws NumberFormatException
	 * @Name: 获取购物车商品数量
	 * @Author: nick
	 */
	@RequestMapping(value = "/wechat/cart/getCartNum")
	public void getShoppingCartCount(HttpServletRequest request, HttpServletResponse response)
			throws NumberFormatException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (!userId.equals("")) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("userId", userId);
			// 根据用户编号获取购物车数量
			Integer num = Integer.parseInt(OkhttpUtils.get(SysContext.ONLINEURL + "/onl/cart/count?userId=" + userId));
			num = num == null ? 0 : num;
			if (num < 0) {
				num = 0;
			}
			this.render(response, "\"" + num + "\"");
		} else {
			this.render(response, 0);
		}
	}
	
	/**
	 * 批量删除购物车
	 * Title: batchdelete
	 * Description: 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws NumberFormatException 
	 * @date 2018年4月3日 下午3:32:09
	 */
	@RequestMapping("/wechat/cart/batchdelete")
	public void batchdelete(HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, IOException {
		String userId = SysCache.getWeChatUserByColumn(request, "userId");
		if (userId != null && !userId.equals("") && !userId.equals("null")) {
			String cartId = request.getParameter("cartId");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ids", cartId);
			map.put("userId", userId);
			int result = Integer.parseInt(OkhttpUtils.deleteByFormParams(SysContext.ONLINEURL + "/onl/cart/deletes", map));
			if (result < 1) {
				this.render(response, "{\"message\":\"删除失败\",\"flag\":false}");
			} else {
				this.render(response, "{\"message\":\"删除成功\",\"flag\":true}");
			}
		} else {
			this.render(response, "{\"message\":\"您未登录，请先登录\",\"flag\":false}");
		}
		
	}
}
