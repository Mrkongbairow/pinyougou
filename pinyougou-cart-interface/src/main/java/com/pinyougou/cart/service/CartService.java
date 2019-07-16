package com.pinyougou.cart.service;

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
}
