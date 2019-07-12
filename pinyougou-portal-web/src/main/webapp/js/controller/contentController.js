app.controller('contentController',function ($scope, contentService) {

    //根据id查找广告图片
    $scope.contentList=[]//广告集合
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId]=response;
            }
        )
    }

    //根据关键字查找，并跳转页面
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
})