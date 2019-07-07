app.controller("searchController",function ($scope, searchService) {

    //定义搜索条件结构
    $scope.searchMap ={'keywords':'','category':'','brand':'','spec':{}}

    $scope.search=function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;
            }
        )
    }
    
    $scope.addOptions=function (key, value) {
        if (key == 'category' || key == 'brand') {//如果点击的是分类和品牌，则添加到resultMap中
            $scope.searchMap[key] = value;
        }else{//如果是规格选项
            $scope.searchMap.spec[key] = value;
        }
    }
});