app.controller("searchController", function ($scope,$location,searchService) {

    //定义搜索条件结构
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNum': 1,
        'pageSize': 40,
        'sort':'',
        'sortField':''
    }

    $scope.search = function () {
        $scope.searchMap.pageNum = parseInt( $scope.searchMap.pageNum);//将字符串转成数字

        // $scope.searchMap.sort ='';//清空排序规则
        // $scope.searchMap.sortField = '';//清空排序规则

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

        $scope.firstPoint = true;//前面显示点
        $scope.lastPoint = true;//后面显示点

        if ($scope.resultMap.totalPages > 5) {//总页数小于等于5页就直接显示

            if ($scope.searchMap.pageNum <= 3) {//如果当前页小于等于第三页
                lastPage = 5;//最后的页码就等于5

                $scope.firstPoint = false;//如果为前五页，则不显示前面的省略号

            } else if ($scope.searchMap.pageNum >= $scope.resultMap.totalPages - 2) {//如果当前页大于等于最大页数减2
                firstPage = $scope.resultMap.totalPages - 4;//第一页就等于最大页数减4

               $scope.lastPoint = false;//如果为最后五页，则不现实后面的省略号

            } else {
                firstPage = $scope.searchMap.pageNum - 2;
                lastPage = $scope.searchMap.pageNum + 2;
            }
        }else{
            $scope.firstPoint = false;//如果为前五页，则不显示前面的省略号
            $scope.lastPoint = false;//如果为最后五页，则不现实后面的省略号
        }

        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    };


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

    //根据价格排序
    $scope.sortSearch=function (sort, sortField) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortField = sortField;

        $scope.search();
    }
    //判断搜索关键字是否为品牌
    $scope.keywordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含品牌字段
                return true;
            }
        }
        return false;
    }

    //获取首页跳转关键字
    $scope.loadKeywords= function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        //查询
        $scope.search();
    }
});