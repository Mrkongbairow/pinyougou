package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;

import java.io.Serializable;
import java.util.List;

public class Goods implements Serializable {
    private TbGoods tbGoods;
    private TbGoodsDesc goodsDesc;
    private List<TbItem> itemLsit;

    public TbGoods getTbGoods() {
        return tbGoods;
    }

    public void setTbGoods(TbGoods tbGoods) {
        this.tbGoods = tbGoods;
    }

    public TbGoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(TbGoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<TbItem> getItemLsit() {
        return itemLsit;
    }

    public void setItemLsit(List<TbItem> itemLsit) {
        this.itemLsit = itemLsit;
    }
}
