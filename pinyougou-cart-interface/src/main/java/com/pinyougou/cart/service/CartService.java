package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.group.Cart;

import java.util.List;

public interface CartService {

    /**
     * 商品添加购物车
     * @param list
     * @param itemId
     * @param num
     * @return
     */
    public List<Cart> addGoodsToCartList(List<Cart> list,Long itemId,Integer num);

    /**
     * 从缓存中查找购物车
     * @param username
     * @return
     */
    public List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车存入缓存
     * @param username
     * @param cartList
     */
    public void saveCatListToRedis(String username,List<Cart> cartList);

    /**
     * 购物车合并（登录后，将缓存中的购物车和cookie中的购物车进行合并）
     * @param cartList1
     * @param cartList2
     * @return
     */
    public List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);

}
