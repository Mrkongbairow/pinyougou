//控制层
app.controller('goodsController', function ($scope, $controller,$location, goodsService, uploadService,itemCatService,typeTemplateService) {

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
    $scope.findOne = function () {
        var id = $location.search()['id'];
        if (id == null){//如果id为空，不执行查询
            return;
        }
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                //显示商品介绍
                editor.html($scope.entity.goodsDesc.introduction);
                //显示商品图片
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //读取商品扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse( $scope.entity.goodsDesc.customAttributeItems);
                //读取商品规格属性
                $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                //获取商品sku
                for (var i = 0; i < $scope.entity.itemList.length; i++) {
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
            }
        );
    }

    //保存
    $scope.save=function(){
       //提取文本编辑器的值
        $scope.entity.goodsDesc.introduction = editor.html()
        var serviceObject;//服务层对象
        if($scope.entity.tbGoods.id!=null){//如果有 ID
            serviceObject=goodsService.update( $scope.entity ); //修改
        }else{
            serviceObject=goodsService.add( $scope.entity );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    alert('修改成功！');
                    location.href="goods.html";
                }else{
                    alert(response.message);
                }
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
    $scope.$watch('entity.tbGoods.category1Id',function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemList2 = response;
            }
        )
    })
    //显示三级下拉列表
        $scope.$watch('entity.tbGoods.category2Id',function (newVale, oldValue) {
            itemCatService.findByParentId(newVale).success(
                function (response) {
                    $scope.itemList3=response;
                }
            )
        });

    //显示模板ID
    $scope.$watch('entity.tbGoods.category3Id',function (newValue, odlValue) {
        itemCatService.findOne(newValue).success(
            function (response){
                $scope.entity.tbGoods.typeTemplateId=response.typeId;
            }
        )
    });

    //显示品牌列表，与模板id进行绑定
    $scope.$watch('entity.tbGoods.typeTemplateId',function (newValue, odlValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;//获取模板对象
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds)//将字符串转化为json对象
                if ($location.search()['id'] == null){
                    $scope.entity.goodsDesc.customAttributeItems= JSON.parse($scope.typeTemplate.customAttributeItems)
                }
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
    $scope.updateSpecAttribute=function ($event, name, value) {
        var object = $scope.findObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
        if (object != null) {
            if($event.target.checked){//选中复选框，给集合添加值
                object.attributeValue.push(value)
            }else {//取消选择，移除值
                object.attributeValue.splice(object.attributeValue.indexOf(value),1)

                if(object.attributeValue.length==0){//如果没有一个复选框，将集合整个删除
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(object),1)
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]})
        }

    }

    //创建SKU列表
    $scope.createItemList=function () {
        //创建初始化数组
        $scope.entity.itemList=[ {spec:{},price:0,num:9999,status:'0',isDefault:'0'} ]
        //desc集合,里面存储的是规格
        var items = $scope.entity.goodsDesc.specificationItems;
        //遍历规格数组，给初始化集合添加规格
        for (var i = 0; i < items.length; i++) {

            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
        }
    }
    //添加列值，list为初始化数组
    addColumn=function (list,columnName,columnValue) {
        //创建新的数组
        var newList=[];
        for (var i = 0; i < list.length; i++) {
            //获取原先的数组
           var oldRow=list[i];
            for (var j = 0; j < columnValue.length; j++) {
               var newRow= JSON.parse(JSON.stringify(oldRow))//通过深克隆新获取一行
                newRow.spec[columnName]=columnValue[j];//将规格属性赋值给spec集合
               newList.push(newRow);//将新创建的行添加到数组
            }
        }
        return newList;
    }
    $scope.statusList=["未申请","申请中","审核通过","已驳回"]
    //显示商品分类信息
    $scope.showCatList=[];
    $scope.itemCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.showCatList[response[i].id]=response[i].name;
                }
            }
        )
    }
    //显示CheckBox选中数据
    $scope.checkAttributeValue=function (name,optionName) {
        var items = $scope.entity.goodsDesc.specificationItems;

        var objectByKey = $scope.findObjectByKey(items,'attributeName',name);
        if (objectByKey == null) {
            return false;
        }else {
            if (objectByKey.attributeValue.indexOf(optionName)>=0){
                return true;
            } else {
                return false;
            }
        }
    }

    //设置是否下架
    $scope.isMarket=["上架","已下架","已上架"];
    $scope.updateMarket=function (status) {
        goodsService.updateMarket($scope.selectIds,status).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectIds=[];
                }else {
                    alert(response.message)
                }
            }
        )
    }

});
