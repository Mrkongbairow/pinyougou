package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.group.Cart;
import com.pinyougou.util.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    /**
     * 从cookie中获取购物车集合
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr == null ||cartListStr.equals("")){
            cartListStr = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr, Cart.class);

        return cartList_cookie;
    }

    /**
     * 将商品存入购物车中
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            //从cookie中获取购物车集合对象
            List<Cart> cartList = findCartList();
            //将商品添加到购物车中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //将购物车集合对象存入cookie中
            String jsonString = JSON.toJSONString(cartList);
            CookieUtil.setCookie(request,response,"cartList",jsonString,3600*24,"UTF-8");

            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
