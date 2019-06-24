//控制层
app.controller('brandController', function ($scope, brandService) {

   //查找所有品牌记录
    $scope.findAll= function () {
        brandService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        )
    };

    //分页开始
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 10,//总记录数
        itemsPerPage: 10,//每页记录数
        perPageOptions: [10, 20, 30, 40, 50],//分页选项
        onChange: function () { //当页码变更后，自动触发的方法
            $scope.reloadList();//重新加载
        }
    };
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
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
    $scope.selectIds=[];//选中的id集合
    $scope.updateSelection=function ($event, id) {
        if ($event.target.checked) {//被选中，增加到数组
            $scope.selectIds.push(id);
        }else {
            var indexOf = $scope.selectIds.indexOf(id);//id当前位置
            $scope.selectIds.splice(indexOf,1);//参数一：id当前位置；参数二：删除的个数
        }
    };
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