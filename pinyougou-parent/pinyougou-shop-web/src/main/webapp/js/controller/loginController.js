//控制层
app.controller('loginController' ,function($scope,$controller,loginService) {

//显示用户名
    $scope.findName = function () {
        loginService.findName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        )
    }
})