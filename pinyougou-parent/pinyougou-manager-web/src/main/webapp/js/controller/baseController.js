//基本控制层
app.controller("baseController",function ($scope) {

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

    //删除品牌信息
    $scope.selectIds = [];//选中的id集合
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {//被选中，增加到数组

            $scope.selectIds.push(id);
        } else {
            var indexOf = $scope.selectIds.indexOf(id);//id当前位置
            $scope.selectIds.splice(indexOf, 1);//参数一：id当前位置；参数二：删除的个数
        }
    };
    //转换json字符串
    $scope.jsonToString=function (jsonString, key) {
       var json = JSON.parse(jsonString);//将json字符串转换为json对象
        var value="";
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += "，";
            }
            value += json[i][key];
        }

        return value;
    }

});