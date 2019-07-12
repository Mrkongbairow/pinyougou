//控制层
app.controller('brandController', function ($scope,$controller, brandService) {

    //引入baseController
    $controller("baseController",{$scope:$scope});//继承自baseController

   //查找所有品牌记录
    $scope.findAll= function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    };


    $scope.findPage = function (page, row) {
        brandService.findPage(page, row).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//总记录数
            }
        )
    };
    //分页结束

    //添加与修改品牌
    $scope.save = function () {
        var object = null;
        //如果有id，方法切换为更新
        if ($scope.entity.id != null) {
            object = brandService.update($scope.entity)
        }else{
            object = brandService.add($scope.entity)
        }
        object.success(
            function (response) {
                if (response.success) {//添加成功
                    $scope.reloadList();//刷新结果
                } else {//添加失败
                    alert(response.message);
                }
            }
        )
    };

    //修改品牌信息
    $scope.findOne = function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    //删除品牌信息
    $scope.dele=function () {
       brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                   $scope.reloadList();
                }else {
                    alert(response.message)
                }
            }
        )
    };

    //条件查询
    $scope.searchEntity={};//定义搜索对象

    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(
            function (response) {
                $scope.paginationConf.totalItems=response.total;//总记录数
                $scope.list=response.rows;//每页显示行数
            }
        )
    };
});