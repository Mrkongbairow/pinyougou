//服务层
app.service('loginService',function($http) {

//页面显示用户名
    this.findName = function () {
        return $http.get('../login/loginName.do')
    }
})