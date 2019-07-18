package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.group.Cart;
import com.pinyougou.util.CookieUtil;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 10000)
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
        ///获取登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);

        String cartListStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListStr == null ||cartListStr.equals("")){
            cartListStr = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListStr, Cart.class);

        if (username.equals("anonymousUser")){//未登录,从cookie中获取购物车信息
            System.out.println("从cookie中获取购物车数据");

            return cartList_cookie;
        }else {//登录，从缓存中获取购物车,并将缓存中的购物车进行合并
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);

            if (cartList_cookie.size() > 0){//如果cookie中的购物车存在物品，则进行合并
                //合并购物车
                cartListFromRedis = cartService.mergeCartList(cartList_cookie, cartListFromRedis);
                //清除cookie中的购物车信息
                CookieUtil.deleteCookie(request,response,"cartList");

                //将合并后的购物车存入缓存
                cartService.saveCatListToRedis(username,cartListFromRedis);
                System.out.println("执行了合并购车的逻辑");
            }

            return cartListFromRedis ;
        }


    }

    /**
     * 将商品存入购物车中
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
//        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");//允许跨域请求（不需要操作cookie）
//        response.setHeader("Access-Control-Allow-Credentials","true");//允许操作cookie

        ///获取登录用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            //从cookie中获取购物车集合对象
            List<Cart> cartList = findCartList();
            //将商品添加到购物车中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            if (name.equals("anonymousUser")){//如果未登录，将购物车信息存入cooKie中
                System.out.println("将数据存入cookie中");

                //将购物车集合对象存入cookie中
                String jsonString = JSON.toJSONString(cartList);
                CookieUtil.setCookie(request,response,"cartList",jsonString,3600*24,"UTF-8");
            }else {

                cartService.saveCatListToRedis(name,cartList);
            }


            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
}
