app.controller("seckillGoodsController",function ($scope ,$location, $interval,seckillGoodsService) {

   $scope.findList=function () {
       seckillGoodsService.findList().success(
           function (response) {
               $scope.list = response;
           }
       )
   }

    $scope.findOne=function () {
        var id =  $location.search()['id'];
        seckillGoodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;

                //进入详情页时，显示秒杀倒计时
                allScond = Math.floor((new Date($scope.entity.endTime).getTime() - (new Date().getTime()))/1000);//总秒数

                time=  $interval(function () {
                   if (allScond>0){
                       allScond = allScond -1;
                       $scope.timeString = convertTimeString(allScond);//转换时间字符串
                   }else{
                       $interval.cancel(time);
                        alert("秒杀活动结束")
                   }
                },1000)
            }
        )
    }

    //将秒杀时间转换为秒数
    convertTimeString = function (allSecond) {
        var days = Math.floor(allSecond/(60*60*24));//天数
        var hours = Math.floor((allSecond - days*60*60*24)/(60*60) );//小时数
        var minutes = Math.floor((allSecond- days*60*60*24-hours*60*60)/60);//分钟数
        var second = Math.floor(allSecond- days*60*60*24-hours*60*60 - minutes*60);//秒数

        var timeString = "";
        if (days>0){
            timeString = days+"天 "
        }

        return timeString+hours+":"+minutes+":"+second;
    }

    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if (response.success) {
                    alert("下单成功后，请在五分钟内完成支付");
                    location.href="pay.html"
                }else {
                    alert(response.message)
                }
            }
        )
    }

});