app.controller("payController",function ($scope,$location,payService) {

    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                $scope.money=(response.total_fee/100).toFixed(2);//交易金额
                $scope.out_trade_no = response.out_trade_no;//订单号

                //生成二维码
                var qr = new QRious({
                    element:document.getElementById('qrious'),
                    size:250,
                    level:'H',
                    value:response.code_url
                });
                queryPayStatus();//生成二维码后查询支付状态
            }
        )
    }
    //查询支付状态
    queryPayStatus = function () {
        payService.queryPayStatus($scope.out_trade_no).success(
            function (response) {
                if (response.success){
                    location.href="paysuccess.html#?money="+$scope.money;
                }else {
                    if (response.message == "二维码超时") {
                        $scope.createNative();//如果二维码超时，但是页面还存在，则重新生成二维码
                    }else{
                        location.href="payfail.html";
                    }
                }
            }
        )
    }
    //支付成功后在页面显示支付金额
    $scope.showMoney=function () {
        return $location.search()['money'];
    }
})