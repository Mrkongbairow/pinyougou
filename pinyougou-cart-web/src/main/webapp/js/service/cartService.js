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
})