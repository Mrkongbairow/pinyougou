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
    //查找用户收货地址
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList=response;
                //设置默认的地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList.length.isDefault == '1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        )
    }

    //选中用户
    $scope.selectAddress=function (address) {
        $scope.address= address;
    }

    //判断是否当前选中地址
    $scope.isSelectedAddress=function (address) {
        if ($scope.address == address) {
            return true;
        }else {
            return false;
        }
    }
    //新增收货地址
    $scope.addAddress=function () {
        cartService.addAddress($scope.address).success(
            function (response) {
                if (response.success){
                    $scope.address={};//将添加列表清空
                    //刷新地址列表
                    $scope.findAddressList();

                    //alert(response.message)
                } else {
                    alert(response.message);
                }
            }
        )
    }
    //付款方式
    $scope.order={paymentType:"1"};//默认付款方式

    $scope.selectPayType=function (type) {
        $scope.order.paymentType = type;
    }
    //提交订单
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {
                    if ( $scope.order.paymentType=="1"){//如果是微信支付，跳转到微信支付页面
                        location.href="pay.html"
                    } else {//如果是货到付款，跳转到提示页面
                        location.href="paysuccess.html"
                    }
                }else{
                    alert(response.message);
                }
            }
        )
    }
})