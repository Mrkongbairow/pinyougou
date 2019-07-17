package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.group.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //1.根据skuId查询商品sku明细对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null){//如果商品不存在
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())){//如果商品状态不为1
            throw new RuntimeException("商品状态无效");
        }

        //2.根据sku对象获取商家id
        String sellerId = item.getSellerId();

        //3.根据商家id在购物车列表中查询购物车对象
        Cart cart = searchCart(cartList, sellerId);

        if (cart == null){//4.如果购物车中不存在该商家的购物车
            //4.1创建一个新的购物车对象
            cart = new Cart();
            cart.setSellerID(sellerId);
            cart.setSellerName(item.getSeller());

            TbOrderItem orderItem = createOrderItem(item, num);

            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(orderItem);

            cart.setOrderItemList(orderItemList);
            //4.2将新创建的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else {//5.如果购物车列表中存在该商家的购物车
            //判断该商品是否存在购物车明细列表中
            TbOrderItem orderItem = searchOderItem(cart.getOrderItemList(), itemId);

            if (orderItem == null){ //5.1如果不存在，创建购物车明细对象，并添加到商家购物车中
              orderItem =  createOrderItem(item,num);
              cart.getOrderItemList().add(orderItem);
            }else {//5.2如果存在，在原有的数量上添加数量，并更新金额
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //如果商品数量在操作后小于等于0，则移除该明细
                if (orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果购物车中商品数量为0 ，则移除该商家购物车
                if (cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }

        }

        return cartList;
    }
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 从缓存中查找购物车列表
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从缓存中获取购物车");
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将购物车列表存入缓存
     * @param username
     * @param cartList
     */
    @Override
    public void saveCatListToRedis(String username, List<Cart> cartList) {
        System.out.println("将购物车存入缓存");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        //不能进行简单合并
        for (Cart cart : cartList1) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                cartList2 = addGoodsToCartList(cartList2, orderItem.getItemId(), orderItem.getNum());
            }
        }

        return cartList2;
    }

    /**
     * 根据商家id查找购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCart(List<Cart> cartList,String sellerId){
        for (Cart cart : cartList) {
            if (sellerId.equals(cart.getSellerID())){
                return cart;
            }
        }
        return null;
    }

    /**
     * 查找购物车中是否存在该商品
     * @param orderList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOderItem(List<TbOrderItem> orderList,Long itemId){
        for (TbOrderItem tbOrderItem : orderList) {
            if (tbOrderItem.getItemId().longValue() == itemId.longValue()){
                return tbOrderItem;
            }
        }
        return  null;
    }
    /**
     * 创建新的购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        if (num <= 0){
            throw new RuntimeException("数量非法");
        }
        TbOrderItem tbOrderItem = new TbOrderItem();

        tbOrderItem.setGoodsId(item.getGoodsId());
        tbOrderItem.setItemId(item.getId());
        tbOrderItem.setNum(num);
        tbOrderItem.setPicPath(item.getImage());
        tbOrderItem.setPrice(item.getPrice());
        tbOrderItem.setSellerId(item.getSellerId());
        tbOrderItem.setTitle(item.getTitle());
        tbOrderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));

        return tbOrderItem;
    }
}
