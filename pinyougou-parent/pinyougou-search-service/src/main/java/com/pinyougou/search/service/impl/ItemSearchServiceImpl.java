package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;
@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate template;

    @Override
    public Map<String, Object> search(Map mapSerch) {
        Map map = new HashMap();
        //添加查询条件
        Query query = new SimpleFacetQuery();
        Criteria criteria = new Criteria("item_keywords").is(mapSerch.get("keywords"));

        query.addCriteria(criteria);

        ScoredPage<TbItem> items = template.queryForPage(query, TbItem.class);

        map.put("rows",items.getContent());

        return map;
    }
}
