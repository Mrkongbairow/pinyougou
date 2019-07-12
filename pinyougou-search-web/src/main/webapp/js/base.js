//定义模块
var app = angular.module('pinyougou', []);
//$sce服务过滤器
app.filter("trustHtml",['$sce',function ($sce) {
    return function (data) {//需要过滤的内容
        return $sce.trustAsHtml(data)//过滤后的内容
    }
}]);