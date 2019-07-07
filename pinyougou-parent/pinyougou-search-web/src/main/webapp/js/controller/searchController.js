app.controller("searchController", function ($scope, searchService) {

    //定义搜索条件结构
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNum': 1,
        'pageSize': 40
    }

    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt( $scope.searchMap.pageNum);//将字符串转成数字

        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;

                createPageLabel();//构建分页栏
            }
        )
    };
    //分页查询
    createPageLabel = function () {
        $scope.pageLabel = [];//重新构建分页栏

        var firstPage = 1;//开始页
        var lastPage = $scope.resultMap.totalPages;//结束页码

        if ($scope.resultMap.totalPages > 5) {//总页数小于等于5页就直接显示

            if ($scope.searchMap.pageNum <= 3) {//如果当前页小于等于第三页
                lastPage = 5;//最后的页码就等于5
            } else if ($scope.searchMap.pageNum >= $scope.resultMap.totalPages - 2) {//如果当前页大于等于最大页数减2
                firstPage = $scope.resultMap.totalPages - 4;//第一页就等于最大页数减4
            } else {
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }
        }

        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //添加搜索条件
    $scope.addOptions = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类和品牌，则添加到resultMap中
            $scope.searchMap[key] = value;
        } else {//如果是规格选项
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    }
    //移除搜索条件
    $scope.removeOptions = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {//如果点击的是分类和品牌，则添加到resultMap中
            $scope.searchMap[key] = '';
        } else {//如果是规格选项
            delete $scope.searchMap.spec[key];
        }
        $scope.search();
    }

    //根据分页进行查询
    $scope.searchPage= function (pageNum) {
        if (pageNum < 1 || pageNum > $scope.resultMap.totalPages) {
            return ;
        }
        $scope.searchMap.pageNum = pageNum;//当前页
        $scope.search();//查询
    }

    //判断是否为第一页
    $scope.isFirstPage = function () {
        if ($scope.searchMap.pageNum == 1){
            return true;
        } else{
            return false;
        }
    }
    //判断是否为最后一页
    $scope.isEndPage = function () {
        if ($scope.searchMap.pageNum == $scope.resultMap.totalPges){
            return true;
        } else{
            return false;
        }
    }
});