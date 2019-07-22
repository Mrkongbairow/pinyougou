package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTast {
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 更新秒杀商品
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void refreshSeckillGoods(){
        //从缓存中取出秒杀商品的id
        List ids = new ArrayList(redisTemplate.boundHashOps("seckillGoods").keys());
        System.out.println("执行了更新商品的任务"+ids);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");//审核通过
        criteria.andStockCountGreaterThan(0);//剩余库存大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//秒杀开始时间小于等于当前时间
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());//秒杀结束时间大于等于当前时间
        if (ids.size()>0){
            criteria.andIdNotIn(ids);//添加缓存中不存在的商品
        }

        List<TbSeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);

        for (TbSeckillGoods seckillGood : seckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(),seckillGood);

            System.out.println("执行了增量更新商品："+seckillGood.getId());
        }
        System.out.println("缓存完成~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        System.out.println("执行删除过期商品的任务");
        //从Redis中查找goods
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        ///遍历所有商品，对比商品是否过期
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {

            if (seckillGoods.getEndTime().getTime() < new Date().getTime()) {
                //如果当前时间大于过期时间，删除缓存，并将商品信息存入数据库
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);//存入数据库

                redisTemplate.boundHashOps("seckillGoods").delete(seckillGoods.getId());//删除缓存
                System.out.println("删除了商品："+seckillGoods.getId());
            }
        }
        System.out.println("执行删除过期商品的任务完成~~~~~~~~~~~~~~~~~~~~~");

    }

}
