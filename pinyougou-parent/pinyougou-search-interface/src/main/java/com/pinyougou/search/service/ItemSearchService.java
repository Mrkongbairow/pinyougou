package com.pinyougou.search.service;


import org.springframework.stereotype.Service;

import java.util.Map;

public interface ItemSearchService {
    /**
     * 搜索功能
     * @param mapSerch
     * @return
     */
    public Map<String ,Object> search(Map mapSerch);
}
