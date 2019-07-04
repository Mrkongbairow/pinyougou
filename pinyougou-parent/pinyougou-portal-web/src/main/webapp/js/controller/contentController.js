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
})