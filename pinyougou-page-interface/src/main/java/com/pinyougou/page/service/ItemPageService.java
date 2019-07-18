package com.pinyougou.page.service;

import com.pinyougou.pojo.TbAddress;

import java.util.List;

public interface ItemPageService {

    public Boolean genItemHtml(Long goodsId);

    /**
     * 删除静态页面
     */
    public boolean deleteItemHtml(Long[] ids);


}
