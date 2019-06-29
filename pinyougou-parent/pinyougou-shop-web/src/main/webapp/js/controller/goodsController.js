//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, uploadService,itemCatService,typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.add = function () {
        $scope.entity.goodsDesc.introduction = editor.html()
        goodsService.add($scope.entity).success(
            function (response) {
                if (response.success) {
                    //重新查询
                    alert("保存成功")
                    $scope.entity = {};//清空表格数据
                    editor.html("");
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //上传文件
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
                if (response.success) {//储存成功，取出URL
                    $scope.image_entity.url=response.message;
                } else {
                    alert(response.message);
                }
            }).error(function () {
            alert("文件上传失败！")
        })
    }

    //图片在列表显示
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}}//定义页面数组结构

    $scope.add_image_entity=function () {
        //将图片添加到数组中
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1)
    }

    //显示一级下拉列表
    $scope.selectItemCatList1=function () {
        itemCatService.findByParentId(0).success(
            function (response) {
                $scope.itemList1=response;
            }
        )
    }

    //显示二级下拉列表
    $scope.$watch('entity.goods.category1Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemList2 = response;
            }
        )
    })
    //显示三级下拉列表
        $scope.$watch('entity.goods.category2Id',function (newVale, oldValue) {
            itemCatService.findByParentId(newVale).success(
                function (response) {
                    $scope.itemList3=response;
                }
            )
        });

    //显示模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue, odlValue) {
        itemCatService.findOne(newValue).success(
            function (response){
                $scope.entity.goods.typeTemplateId=response.typeId;
            }
        )
    });

    //显示品牌列表，与模板id进行绑定
    $scope.$watch('entity.goods.typeTemplateId',function (newValue, odlValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;//获取模板对象
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds)//将字符串转化为json对象
               $scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.typeTemplate.customAttributeItems)
            }
        );
        //查找规格属性,根据模板id查询
        typeTemplateService.findSpecList(newValue).success(
         function (response) {
             $scope.specList=response;
         }
        )
    })

    //添加扩展属性
    $scope.updateSpecAttribute=function ($evnet, name, value) {
        var object = $scope.findObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
        if (object != null) {
            if($event.target.checked){//选中复选框，给集合添加值
                object.attributeValue.push(value)
            }else {//取消选择，移除值
                object.attributeValue.splice(object.attributeValue.indexOf(value),1)
                //如果所有取消所有
                if(object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1)
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':value})
        }

    }

});
