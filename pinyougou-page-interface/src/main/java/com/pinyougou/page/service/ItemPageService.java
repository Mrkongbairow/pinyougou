package com.pinyougou.page.service;

public interface ItemPageService {

    public Boolean genItemHtml(Long goodsId);

    /**
     * 删除静态页面
     */
    public boolean deleteItemHtml(Long[] ids);
}
