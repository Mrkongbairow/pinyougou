package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll(){

        return brandService.findAll();
    }

    /**
     * 品牌分页功能能
     * @param pageNum 当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int pageNum,int pageSize){

        return brandService.findPage(pageNum, pageSize);
    }

    /**
     * 添加品牌
     * @param brand
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try{
            brandService.add(brand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * 根据id查找品牌
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbBrand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改品牌信息
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try{
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    /**
     * 根据id删除品牌
     * @param ids
     * @return
     */
    @RequestMapping("/delet")
    public Result delet(Long[] ids){
        try{
            brandService.delet(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    /**
     * 条件查询加分页
     * @param brand
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbBrand brand,int pageNum,int pageSize){
        return brandService.findPage(brand,pageNum,pageSize);
    }
}
