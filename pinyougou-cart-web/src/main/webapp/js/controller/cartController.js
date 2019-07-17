app.controller("cartController",function ($scope, cartService) {

    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;

                $scope.totalValue = cartService.sum($scope.cartList);
            }
        )
    }

    $scope.addGoodsToCart=function (itemId, num) {
        cartService.addGoodsToCart(itemId,num).success(
            function (response) {
                if (response.success){
                    $scope.findCartList();//刷新列表
                }else {
                    alert(response.message);
                }
            }
        )
    }
})