package com.pinyougou.pojo.group;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private String sellerName;
    private String sellerID;
    private List<TbOrderItem> oderItemList;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public List<TbOrderItem> getOderItemList() {
        return oderItemList;
    }

    public void setOderItemList(List<TbOrderItem> oderItemList) {
        this.oderItemList = oderItemList;
    }
}
