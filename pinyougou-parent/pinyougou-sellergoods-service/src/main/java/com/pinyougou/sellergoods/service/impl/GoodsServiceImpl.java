package com.pinyougou.sellergoods.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.BrandService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemMapper itemMapper;
	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		goods.getTbGoods().setAuditStatus("0");//设置未申请状态
		goods.getTbGoods().setIsMarketable("0");//设置上架状态0审核中，1下架，2上架
		goodsMapper.insert(goods.getTbGoods());

		goods.getGoodsDesc().setGoodsId(goods.getTbGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());

		saveItems(goods);//添加sku商品
	}
	private void saveItems(Goods goods){
		if ("1".equals(goods.getTbGoods().getIsEnableSpec())){

			List<TbItem> itemList = goods.getItemList();
			for (TbItem item :itemList) {
				//设置商品标题
				String title = goods.getTbGoods().getGoodsName();
				Map<String,Object> map =  JSON.parseObject(item.getSpec());
				for (String s : map.keySet()) {
					title += " "+ map.get(s);
				}
				item.setTitle(title);
				setItems(item,goods);
				itemMapper.insert(item);
			}

		}else {
			TbItem item = new TbItem();
			//商品名称
			item.setTitle(goods.getTbGoods().getGoodsName());
			//商品价格
			item.setPrice(goods.getTbGoods().getPrice());
			item.setStatus("1");
			item.setIsDefault("1");
			item.setNum(9999);
			item.setSpec("{}");

			setItems(item,goods);
			itemMapper.insert(item);
		}
	}
	private void setItems(TbItem item,Goods goods){

		//设置商品spu编号
		item.setGoodsId(goods.getTbGoods().getId());
		//设置商家编号
		item.setSellerId(goods.getTbGoods().getSellerId());
		//设置商品分类编号
		item.setCategoryid(goods.getTbGoods().getCategory3Id());
		//设置创建日期
		item.setCreateTime(new Date());
		//设置修改日期
		item.setUpdateTime(new Date());
		//设置品牌名称
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getTbGoods().getBrandId());
		item.setBrand(tbBrand.getName());
		//设置分类名称
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getTbGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());
		//设置商家名称
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getTbGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());
		//设置图片信息
		List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if (maps.size()>0){
			item.setImage((String) maps.get(0).get("url"));
		}
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		goods.getTbGoods().setAuditStatus("0");//设置审核状态
		goodsMapper.updateByPrimaryKey(goods.getTbGoods());
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());

		//先删除原有数据，再添加
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getTbGoods().getId());
		itemMapper.deleteByExample(example);

		//添加sku商品
		saveItems(goods);

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		goods.setTbGoods( goodsMapper.selectByPrimaryKey(id));
		goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));


		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> tbItems = itemMapper.selectByExample(example);
		goods.setItemList(tbItems);

		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");

			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();

		//criteria.andIsMarketableNotEqualTo("1");
			//criteria.andIsMarketableEqualTo("1");

		if(goods!=null){			
				if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
//				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
					criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	@Override
	public void updateStatus(Long[] ids,String status){

		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setAuditStatus(status);

			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public void updateMarket(Long[] ids, String status) {
		for (Long id : ids) {
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsMarketable(status);

			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}


}
