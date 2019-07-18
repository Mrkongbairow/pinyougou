app.service("cartService",function ($http) {
    this.findCartList=function () {
        return $http.get("cart/findCartList.do");
    }

    this.addGoodsToCart=function (itemId, num) {
        return $http.get("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num);
    }

    this.sum=function (cartList) {
       var totalValue={totalNum:0,totalMoney:0};

        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem= cart.orderItemList[j];

                totalValue.totalNum += orderItem.num;//累加数量
                totalValue.totalMoney += orderItem.totalFee;//累加金额
            }
        }
        return totalValue;
    }
    //查找用户收货地址
    this.findAddressList=function () {
        return $http.get("address/findListByUserId.do");
    }
    //新增收货地址
    this.addAddress = function (address) {
        return $http.post("address/addAddress.do",address);
    }
    //添加订单
    this.submitOrder=function (order) {
        return $http.post("order/add.do",order);
    }
})