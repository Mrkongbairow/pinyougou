app.controller('itemController',function ($scope) {
	
	$scope.specificationItems={};
	
	$scope.addNum=function(x){
		
		$scope.num += x;
		if($scope.num<1){
			$scope.num =1;
		}
	}
	//用户选择规格
	$scope.specificationSelect=function(key,value){
		$scope.specificationItems[key]=value;
		
		searchSku();//读取sku
	}
	//判断用户规格是否选中
	$scope.isSelected=function(key,value){
		if($scope.specificationItems[key]==value){
			return true;
		}else{
			return false;
		}
	}
	
	//获取sku值
	$scope.sku={};
	
	$scope.loadSku=function(){
		
		$scope.sku=skuList[0];
		
		$scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
	}
	
	//判断两个sku是否相等
	
	matchObject=function(map1,map2){
		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}
		}
		return true;
	}
	//在列表中查询用户选择的sku
	searchSku=function(){
		
		for(var i =0;i<skuList.length;i++){
			if(matchObject(skuList[i].spec,$scope.specificationItems)){
				$scope.sku=skuList[i];
				return;
			}
		}
		///如果没有查询到
		$scope.sku={id:0,title:'-----',price:0}
	}
	
	$scope.addToCart=function(){
		
		alert('skuid:'+$scope.sku.id);
	}
})