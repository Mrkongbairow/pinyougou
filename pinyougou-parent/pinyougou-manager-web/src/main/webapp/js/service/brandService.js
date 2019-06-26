//定义服务层
app.service("brandService",function ($http) {
    //查找所有品牌数据
    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    };
    //数据分页
    this.findPage=function (page,row) {
        return $http.get("../brand/findPage.do?pageNum=" + page + "&pageSize=" + row);
    };
    //根据id查找
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id=" + id);
    };
    //添加操作
    this.add=function (entity) {
        return $http.post("../brand/add.do", entity);
    };
    //更新操作
    this.update=function (entity) {
        return $http.post("../brand/update.do", entity);
    };
    //删除操作
    this.dele=function (ids) {
        return  $http.get("../brand/delet.do?ids="+ids);
    };
    //条件查询
    this.search=function (page,rows,searchEntity) {
        return  $http.post("../brand/search.do?pageNum=" + page + "&pageSize=" + rows,searchEntity);
    }
    //查询品牌列表
    this.selectOptionList = function () {
        return $http.get("../brand/selectOptionList.do")
    }
});