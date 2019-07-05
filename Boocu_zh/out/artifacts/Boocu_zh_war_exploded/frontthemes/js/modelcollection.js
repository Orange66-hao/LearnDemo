	/**
	 * 常用仪器品牌型号js代码开始，注：客户管理的添加modelcollection_add.html和编辑页面modelcollection_edit.html，
	 * 高校管理的添加university_add.html和编辑页面university_edit.html都引入了这个js，常用仪器品牌型号js代码都是共用的！！！
	 */

	var condition_company=true;
	$('#brand').combobox({
		onSelect: function(record){
			additional(0, record, null, null, null)
		},
        onHidePanel: function() {
            var valueField = $(this).combobox("options").valueField;
            var val = $(this).combobox("getValue");  //当前combobox的值
            var allData = $(this).combobox("getData");   //获取combobox所有数据
            var result = true;      //为true说明输入的值在下拉框数据中不存在
            for (var i = 0; i < allData.length; i++) {
                if (val == allData[i][valueField]) {
                    result = false;
                    break;
                }
            }
            if (result) {
                PostbirdAlertBox.alert({
                    'title': '提 示',
                    'content': '请选择下拉框中的品牌',
                    'okBtn': '好的',
                    'contentColor': 'blue',
                    'onConfirm': function () {
                    }
                });
                $(this).combobox("clear");
            }
        }
	});
	$("#mc_productclass_name").combotree({
		required:false,
		valueField:'id',
		textField:'text',
		editable:false,
		url:'/productClass/combotreeData.json',
		onBeforeSelect:function (node) {//只可选择子节点
			if (!$("#mc_productclass_name").combotree("tree").tree('isLeaf', node.target)) {//如果不是子节点，返回false
				return false;
			}
		},
		onSelect:function(node){
			additional(0, null, null, null, node)
		}
	});

	function additional(i, brand, model, number, mpn) {
		var val = getVal(brand, model, number, mpn)
		if(val.brand){
			arr[i].brand = val.brand
		}
		if(val.model){
			arr[i].model = val.model
		}
		if(val.number){
			arr[i].number = val.number
		}
		if(val.mc_productclass_name){
			arr[i].mc_productclass_name = val.mc_productclass_name
		}
		val = arr[i];
		$.ajax({
			url:"/admin/mcBrandAndModel/addBrandAndModel.jspx",
			data:{
				brand:val.brand,
				model:val.model,
				mc_productclass_name:val.mc_productclass_name,
				mc_company:'${item.name}'
			},
			dataType:"json",
			type:"post",
			success:function(msg){
				if(msg.cont == 'false'){
					$(".rr_brand_and_model").text('数据已存在，请修改！');
					condition_company=false;
					return false;
				}
				if(msg.cont == 'true'){
					$(".rr_brand_and_model").text('');
					condition_company=true;
					return true;
				}
			},
			error:function(){
				alert("网络异常,请重试!");
				return false;
			}
		});
	}

	function check(){
		for (var i = arr.length-1; i >= 0; i--){
			var t = arr[i];
			if(!t.brand && !t.model && !t.mc_productclass_name){//什么都不写时不能让其再添加输入框
				alert("请填写仪器品牌")
				return true
			}
			if(t.brand && !t.model){//只写了品牌没写型号就点击再添加一个型号获取保存提交
				alert("请填写仪器型号")
				return true;
			} else if (t.brand && !t.mc_productclass_name) {//写了品牌和型号，但是没选仪器名称，点击再添加一个型号获取保存提交
				alert("请选择仪器名称")
				return true;
			}
			for (var s = i-1; s >= 0; s--){
				// console.log(arr[s])
				if (obj(t, arr[s])) {
					alert("填写内容已重复，请修改")
					return true;
				}
			}
			if(!t.number){
				arr[i].number = 1
			}
		}
		return false
	}
	function checkaddForm(){
		for (var i = arr.length-1; i >= 0; i--){
			var t = arr[i];

			if(t.brand && !t.model){//只写了品牌没写型号就点击再添加一个型号获取保存提交
				alert("请填写仪器型号")
				return true;
			} else if (t.brand && !t.mc_productclass_name) {//写了品牌和型号，但是没选仪器名称，点击再添加一个型号获取保存提交
				alert("请选择仪器名称")
				return true;
			} else if (!t.brand && t.length>0) {
				alert("请完整再提交")
				return true;
			}
			for (var s = i-1; s >= 0; s--){
				// console.log(arr[s])
				if (obj(t, arr[s])) {
					alert("填写内容已重复，请修改")
					return true;
				}
			}
			if(!t.number){
				arr[i].number = 1
			}
		}
		return false
	}

	function  obj(obj1,obj2){
		if (obj1.brand === obj2.brand &&
			obj1.model === obj2.model &&
			obj1.mc_productclass_name === obj2.mc_productclass_name)
			return true;
		return false;
	}

	/*    $('#mc_productclass_coll').combotree({
			width:300,
			method:'get',
			url: '/productClass/combotreeData.json',
			multiple: true,
			onlyLeafCheck:true,
			onLoadSuccess: function (e) {
				$('#mc_productclass_coll').combotree('setValues',"${item.mc_productclass}".split(','))
			}
		});*/

	var arr = [{brand:null, model:null, number:null, mc_productclass_name:null}]
	var ch
	$('tbody tr').each(function (i, e) {
		if(e.id === 'multiple_list') {
			ch = i
		}
	})

	function getVal(brand, model, number, mpn) {
		return {brand: brand?brand.id:null, model: model?$(model).val():null, number: number?$(number).val():null, mc_productclass_name: mpn?mpn.id:null }
	}

	function addInput() {
		if(check()){
			return
		}
		arr.push(getVal());

		var index = $("input[name='model']").length

		//添加dom
		var trObj = document.createElement("tr");
		trObj.innerHTML="<th>品牌</th>" +
			"<td>" +
			"<select style=\"width: 140px; height: 36px;\"  class=\"easyui-combobox\" name=\"brand\"></select>" +
			"型号<input class=\"input_text easyui-validatebox\" style=\"width: 100px\" type=\"text\" name=\"model\" data-options=\"required:true\">" +
			"数量<input class=\"input_text easyui-validatebox\" style=\"width: 100px\" type=\"number\" name=\"number\" value=\"1\" data-options=\"required:true\">" +
			"名称<select style=\"width: 140px; height: 36px;\"  class=\"easyui-combobox\" name=\"mc_productclass_name\" ></select>" +
			"<a href=\"javascript:void(0)\" class=\"button blueButton\"" +
			"data-options=\"iconCls:'icon-add',plain:true\" onclick=\"deleteInput(this, "+index+")\">删除此项</a>" +
			"</td>";

		$('tbody tr').eq(ch+index-1).after(trObj)

		$($("select[name='brand']")).combobox({
			valueField: 'id',
			textField: 'text',
			url: '/admin/basedata/brand/get_brand_names.jspx',
			required: true,
			multiple: false, //多选
			//editable: false, //是否可编辑
			filter: function (q, row) {
				var opts = $(this).combobox('options');
				return row.text.indexOf(q) >= 0||row.nameEn.indexOf(q)>=0;//这里改成>=即可在任意地方匹配
			},
			onSelect:function(node){
				additional(index, node, null, null, null)
			},
            onHidePanel: function() {
                var valueField = $(this).combobox("options").valueField;
                var val = $(this).combobox("getValue");  //当前combobox的值
                var allData = $(this).combobox("getData");   //获取combobox所有数据
                var result = true;      //为true说明输入的值在下拉框数据中不存在
                for (var i = 0; i < allData.length; i++) {
                    if (val == allData[i][valueField]) {
                        result = false;
                        break;
                    }
                }
                if (result) {
                    PostbirdAlertBox.alert({
                        'title': '提 示',
                        'content': '请选择下拉框中的品牌',
                        'okBtn': '好的',
                        'contentColor': 'blue',
                        'onConfirm': function () {
                        }
                    });
                    $(this).combobox("clear");
                }
            }
		})

		$($("select[name='mc_productclass_name']")[0]).combotree({
			required:true,
			valueField:'id',
			textField:'text',
			editable:false,
			url:'/productClass/combotreeData.json',
			onBeforeSelect:function (node) {//只可选择子节点
				if (!$("#mc_productclass_name").combotree("tree").tree('isLeaf', node.target)) {
					return false;
				}
			},
			onSelect:function(node){
				additional(index, null, null, null, node)
			}
		})

		var model = $("input[name='model']")[index]

		$(model).blur(function () {
			additional(index, null, model, null, null)
		})
		var number = $("input[name='number']")[index]
		$(number).blur(function () {
			additional(index, null, null, number, null)
		})
	}
	function deleteInput(obj,i) {
		arr.splice(i, 1)
		console.log(obj.parentElement.parentElement)
		//var  tableObj= document.getElementById("addForm").firstElementChild;
		$(obj.parentElement.parentElement).empty()
	}
	$("#brand").combobox({
		valueField: 'id',
		textField: 'text',
		url: '/admin/basedata/brand/get_brand_names.jspx',
		required: false,
		multiple: false, //多选
		//editable: false, //是否可编辑
		filter: function (q, row) {
			var opts = $(this).combobox('options');
			return row.text.indexOf(q) >= 0||row.nameEn.toUpperCase().indexOf(q.toUpperCase())>=0;//这里改成>=即可在任意地方匹配
		},
        onHidePanel: function() {
            var valueField = $(this).combobox("options").valueField;
            var val = $(this).combobox("getValue");  //当前combobox的值
            var allData = $(this).combobox("getData");   //获取combobox所有数据
            var result = true;      //为true说明输入的值在下拉框数据中不存在
            for (var i = 0; i < allData.length; i++) {
                if (val == allData[i][valueField]) {
                    result = false;
                    break;
                }
            }
            if (result) {
                PostbirdAlertBox.alert({
                    'title': '提 示',
                    'content': '请选择下拉框中的品牌',
                    'okBtn': '好的',
                    'contentColor': 'blue',
                    'onConfirm': function () {
                    }
                });
                $(this).combobox("clear");
            }
        }
	})
	/*$("#mc_brand_coll").combobox({
		valueField: 'id',
		textField: 'text',
		url: '/admin/basedata/brand/get_brand_names.jspx',
		required: true,
		multiple: false, //多选
		editable: true, //是否可编辑
		filter: function (q, row) {
			var opts = $(this).combobox('options');
			return row.text.indexOf(q) >= 0||row.nameEn.toUpperCase().indexOf(q.toUpperCase())>=0;//这里改成>=即可在任意地方匹配
		}
	})*/
/**
 * 常用仪器品牌型号js代码结束
 */

/*
新增行业
*/
 	function submitFormmcindustryclass(map) {
 		
 	var data = {};
 	//data.status=$("#me_status").combobox('getValue');
 	data.name=$("#in_name").val();
 	data.sort=$("#in_sort").val();
 	data.remark=$("#in_remark").val();
 	//var formflag = $(map["formId"]).form().form('validate');
 	if (data.name != '' && data.sort != '') {
 		$.Loading.show(map["loadshow"]);
 		var options = {
 			url : map["url"],
 			type : "POST",
 			dataType : 'json',
 			data:data,
 			success : function(result) {
 				if (result.result == 1) {
 					$.Loading.show(result.message);
 					$(map["divDialog"]).dialog('close');
 					//$(map["gridreload"]).datagrid('reload');
 					$('#mc_industry_class_coll').combobox('reload');//新增后，刷新树
 				}
 				if (result.result == 0) {
 					$.Loading.error(result.message);
 				}
 				$.Loading.hide();
 			},
 			error : function(e) {
 				alert("出现错误 ，请重试");
 			}
 		};
 		console.log(data);
 		$.ajax(options);
 	}
 	}
 	
 	
 	function appendmcindustryclass(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcIndustyClass/add_mcIndustryClass.jspx";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcIndustyClass/save_mcIndustryClass.jspx?ajax=yes";
 			map["title"] = "添加行业";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcIndustyClass/edit_mcIndustryClass.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcIndustyClass/save_edit_mcIndustryClass.jspx?ajax=yes";
 			map["title"] = "修改行业";
 			map["loadshow"] = "正在修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#dataList";
 		addDialogmcindustryclass(map);
 	}
 	
 	function addDialogmcindustryclass(map) {
 	$(map["divDialog"]).show();
 	$(map["divDialog"]).dialog({
 		title : map["title"],
 		width : 600,
 		height : 300,
 		closed : false,
 		cache : false,
 		href : map["href"],
 		modal : true,
 		buttons : [ {
 			text : '保存',
 			iconCls : 'icon-ok',
 			handler : function() {
 				for(instance in CKEDITOR.instances){
 					CKEDITOR.instances[instance].updateElement();
 				}
 				var savebtn = $(this);
 					var disabled=savebtn.hasClass("l-btn-disabled");
 					if(!disabled){
 					 submitFormmcindustryclass(map,savebtn);
 				}
 			}
 		}/* , {
 			text : '清空',
 			handler : function() {
 				clearForm(map);
 			}
 		} */ ]
 	});
 	}



 	/*
 	新增主营产品
 	*/
 	function submitFormmcmajor(map) {
 		
 	var data = {};
 	data.mc_industryclass=$("#mc_industryclass").combobox('getValue'); 
 	data.name=$("#mma_name").val();
 	data.sort=$("#mma_sort").val();
 	data.remark=$("#mma_remark").val();
 	//var formflag = $(map["formId"]).form().form('validate');
 	if (data.name != '' && data.sort != '' && data.mc_industryclass != '') {
 		$.Loading.show(map["loadshow"]);
 		var options = {
 			url : map["url"],
 			type : "POST",
 			dataType : 'json',
 			data:data,
 			success : function(result) {
 				if (result.result == 1) {
 					$.Loading.show(result.message);
 					$(map["divDialog"]).dialog('close');
 					//$(map["gridreload"]).datagrid('reload');
 					$('#major_product_coll').combobox('reload');//新增后，刷新树
 				}
 				if (result.result == 0) {
 					$.Loading.error(result.message);
 				}
 				$.Loading.hide();
 			},
 			error : function(e) {
 				alert("出现错误 ，请重试");
 			}
 		};
 		console.log(data);
 		$.ajax(options);
 	}
 	}
 	
 	
 	function appendmcmajor(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcMajor/add_mcMajor.jspx?model_yincang=true";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcMajor/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
 			map["title"] = "添加信息";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcMajor/edit_memberGrade.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcMajor/doEdit_memberGrade.jspx?ajax=yes";
 			map["title"] = "修改信息";
 			map["loadshow"] = "正修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#dataList";
 		addDialogmcmajor(map);
 	}
 	
 	function addDialogmcmajor(map) {
 	$(map["divDialog"]).show();
 	$(map["divDialog"]).dialog({
 		title : map["title"],
 		width : 600,
 		height : 300,
 		closed : false,
 		cache : false,
 		href : map["href"],
 		modal : true,
 		buttons : [ {
 			text : '保存',
 			iconCls : 'icon-ok',
 			handler : function() {
 				var savebtn = $(this);
 					var disabled=savebtn.hasClass("l-btn-disabled");
 					if(!disabled){
 					 submitFormmcmajor(map,savebtn);
 				}
 			}
 		}/* , {
 			text : '清空',
 			handler : function() {
 				clearForm(map);
 			}
 		} */ ]
 	});
 	}


 	/*
 	新增仪器
 	*/
 	function submitFormmcproductclass(map) {
 		
 	var data = {};
 	//data.mc_major=$("#mc_major").combobox('getValue'); 
 	data.name=$("#pr_name").val();
 	data.sort=$("#pr_sort").val();
 	data.remark=$("#pr_remark").val();
 	//var formflag = $(map["formId"]).form().form('validate');
 	if (data.name != '' && data.sort != '') {
 		$.Loading.show(map["loadshow"]);
 		var options = {
 			url : map["url"],
 			type : "POST",
 			dataType : 'json',
 			data:data,
 			success : function(result) {
 				if (result.result == 1) {
 					$.Loading.show(result.message);
 					$(map["divDialog"]).dialog('close');
 					//$(map["gridreload"]).datagrid('reload');
 					$('#mc_productclass_coll').combobox('reload');//新增后，刷新树
 				}
 				if (result.result == 0) {
 					$.Loading.error(result.message);
 				}
 				$.Loading.hide();
 			},
 			error : function(e) {
 				alert("出现错误 ，请重试");
 			}
 		};
 		console.log(data);
 		$.ajax(options);
 	}
 	}
 	
 	
 	function appendmcproductclasstwo(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/basedata/mcproductclass/add_mcproductclass.jspx?model_yincang=true";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/basedata/mcproductclass/save_mcproductclass.jspx?ajax=yes&model_yincang=true";
 			map["title"] = "添加常用仪器";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/basedata/mcproductclass/edit_mcproductclass.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/basedata/mcproductclass/save_edit_mcproductclass.jspx?ajax=yes";
 			map["title"] = "修改常用仪器";
 			map["loadshow"] = "正在修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#dataList";
 		addDialogmcproductclass(map);
 	}
 	
 	function addDialogmcproductclass(map) {
 	$(map["divDialog"]).show();
 	$(map["divDialog"]).dialog({
 		title : map["title"],
 		width : 600,
 		height : 300,
 		closed : false,
 		cache : false,
 		href : map["href"],
 		modal : true,
 		buttons : [ {
 			text : '保存',
 			iconCls : 'icon-ok',
 			handler : function() {
 				for(instance in CKEDITOR.instances){
 					CKEDITOR.instances[instance].updateElement();
 				}
 				var savebtn = $(this);
 					var disabled=savebtn.hasClass("l-btn-disabled");
 					if(!disabled){
 					 submitFormmcproductclass(map,savebtn);
 				}
 			}
 		}/* , {
 			text : '清空',
 			handler : function() {
 				clearForm(map);
 			}
 		} */ ]
 	});
 	}
  
 	/*
 	新增品牌
 	*/
 	function submitFormmcbrand(map) {
 	var data = {};
 	data.mc_productclass=$("#mc_productclass").combobox('getValue');
 	data.name=$("#mb_name").val();
 	data.sort=$("#mb_sort").val();
 	data.remark=$("#mb_remark").val();
 	//var formflag = $(map["formId"]).form().form('validate');
 	if (data.name != '' && data.sort != '' && data.mc_productclass != '') {
 		$.Loading.show(map["loadshow"]);
 		var options = {
 			url : map["url"],
 			type : "POST",
 			dataType : 'json',
 			data:data,
 			success : function(result) {
 				if (result.result == 1) {
 					$.Loading.show(result.message);
 					$(map["divDialog"]).dialog('close');
 					//$(map["gridreload"]).datagrid('reload');
 					$('#mc_brand_coll').combobox('reload');//新增后，刷新树
 				}
 				if (result.result == 0) {
 					$.Loading.error(result.message);
 				}
 				$.Loading.hide();
 			},
 			error : function(e) {
 				alert("出现错误 ，请重试");
 			}
 		};
 		console.log(data);
 		$.ajax(options);
 	}
 	}
 	
 	
 	function appendmcbrand(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcBrand/add_mcBrand.jspx?model_yincang=true";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcBrand/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
 			map["title"] = "添加信息";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcBrand/edit_memberGrade.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcBrand/doEdit_memberGrade.jspx?ajax=yes";
 			map["title"] = "修改信息";
 			map["loadshow"] = "正修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#dataList";
 		addDialogmcbrand(map);
 	}
 	
 	function addDialogmcbrand(map) {
 	$(map["divDialog"]).show();
 	$(map["divDialog"]).dialog({
 		title : map["title"],
 		width : 600,
 		height : 300,
 		closed : false,
 		cache : false,
 		href : map["href"],
 		modal : true,
 		buttons : [ {
 			text : '保存',
 			iconCls : 'icon-ok',
 			handler : function() {
 				for(instance in CKEDITOR.instances){
 					CKEDITOR.instances[instance].updateElement();
 				}
 				var savebtn = $(this);
 					var disabled=savebtn.hasClass("l-btn-disabled");
 					if(!disabled){
 					 submitFormmcbrand(map,savebtn);
 				}
 			}
 		}/* , {
 			text : '清空',
 			handler : function() {
 				clearForm(map);
 			}
 		} */ ]
 	});
 	}	
 	
 	
 	/*
 	新增型号
 	*/
 	function submitFormmcmodel(map) {
 	var data = {};
 	data.mc_brand=$("#mc_brand").combobox('getValue'); 
 	data.name=$("#mm_name").val();
 	data.sort=$("#mm_sort").val();
 	data.remark=$("#mm_remark").val();
 	//var formflag = $(map["formId"]).form().form('validate');
 	if (data.name != '' && data.sort != '' && data.mc_brand != '') {
 		$.Loading.show(map["loadshow"]);
 		var options = {
 			url : map["url"],
 			type : "POST",
 			dataType : 'json',
 			data:data,
 			success : function(result) {
 				if (result.result == 1) {
 					$.Loading.show(result.message);
 					$(map["divDialog"]).dialog('close');
 					//$(map["gridreload"]).datagrid('reload');
 					$('#mc_model_coll').combobox('reload');//新增后，刷新树
 				}
 				if (result.result == 0) {
 					$.Loading.error(result.message);
 				}
 				$.Loading.hide();
 			},
 			error : function(e) {
 				alert("出现错误 ，请重试");
 			}
 		};
 		console.log(data);
 		$.ajax(options);
 	}
 	}
 	
 	
 	function appendmcmodel(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcModel/add_mcModel.jspx?model_yincang=true";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcModel/doAdd_memberGrade.jspx?ajax=yes";
 			map["title"] = "添加型号";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcModel/edit_memberGrade.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcModel/doEdit_memberGrade.jspx?ajax=yes";
 			map["title"] = "修改型号";
 			map["loadshow"] = "正修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#dataList";
 		addDialogmcmodel(map);
 	}
 	
 	function addDialogmcmodel(map) {
 	$(map["divDialog"]).show();
 	$(map["divDialog"]).dialog({
 		title : map["title"],
 		width : 600,
 		height : 300,
 		closed : false,
 		cache : false,
 		href : map["href"],
 		modal : true,
 		buttons : [ {
 			text : '保存',
 			iconCls : 'icon-ok',
 			handler : function() {
 				var savebtn = $(this);
 					var disabled=savebtn.hasClass("l-btn-disabled");
 					if(!disabled){
 					 submitFormmcmodel(map,savebtn);
 				}
 			}
 		}/* , {
 			text : '清空',
 			handler : function() {
 				clearForm(map);
 			}
 		} */ ]
 	});
 	}	
 	
  
 /*
  * 新增常用联系人
  */
  function submitFormcontact(map) {
 	 	var data = {};
 	 	data.mc_company=$("#mc_company").combobox('getValue'); 
 		data.name=$("#co_name").val();
 		data.job=$("#co_job").val();
 		data.phone=$("#co_phone").val();
 		data.mail=$("#co_mail").val();
 		data.sort=$("#co_sort").val();
 		data.remark=$("#co_remark").val();
 		//var formflag = $(map["formId"]).form().form('validate');
 		if (data.name != '' && data.mc_company != '') {
 			$.Loading.show(map["loadshow"]);
 			var options = {
 				url : map["url"],
 				type : "POST",
 				dataType : 'json',
 				data:data,
 				success : function(result) {
 					if (result.result == 1) {
 						$.Loading.show(result.message);
 						$(map["divDialog"]).dialog('close');
 						//$(map["gridreload"]).datagrid('reload');
 						$('#contact').combobox('reload');//新增后，刷新树
 						$('#name').combobox('reload');//新增后，刷新树
 					}
 					if (result.result == 0) {
 						$.Loading.error(result.message);
 					}
 					$.Loading.hide();

                    var id=$("#name").combobox('getValue')
                    var url1 ='/admin/mcContacts/get_mcmember_names.jspx?mc_company_id='+id;
                    $("#contact").combobox('reload',url1);
                    $.ajax({
                        //几个参数需要注意一下
                        type: "POST",//方法类型
                        dataType: "json",//预期服务器返回的数据类型
                        url: url1 ,//url
                        success: function (result) {
                            console.log(url1)
                            for (var i=0;i<result.length;i++){
                                $("#contact").combobox('select',result[i].id);//input框中有树方法
                            }
                        },
                        error : function() {
                            alert("添加异常！");
                        }
                    });
 				},
 				error : function(e) {
 					alert("出现错误 ，请重试");
 				}
 			};
 			$.ajax(options);
 		}
 }

 function appendcontact(id, companyId) {
  	var newCompanyId;//定义变量
  	if (companyId == undefined) {//是添加公司页面中的添加常用联系人的方法
        newCompanyId = $("#name").combobox('getValue');//获取combobox选中的公司id
	} else {//是编辑公司的页面中的添加常用联系人的方法
        newCompanyId = companyId;//获取后台传回来的公司id'${item.name}’
	}
 	var map = {}; // Map map = new HashMap();
 	if (!id) {
 		map["href"] = "/admin/mcContacts/add_mcContacts.jspx?model_yincang=true&companyId="+newCompanyId;
 		map["formId"] = "#addForm";
 		map["url"] = "/admin/mcContacts/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
 		map["title"] = "添加信息";
 		map["loadshow"] = "正在添加......";
 	} else {
 		map["href"] = "/admin/mcContacts/edit_memberGrade.jspx?id=" + id;
 		map["formId"] = "#editForm";
 		map["url"] = "/admin/mcContacts/doEdit_memberGrade.jspx?ajax=yes";
 		map["title"] = "修改信息";
 		map["loadshow"] = "正修改......";
 	}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#memberdata";
 		addcontact(map);
 	}
 	function addcontact(map) {
 		$(map["divDialog"]).show();
 		$(map["divDialog"]).dialog({
 			title : map["title"],
 			width : 620,
 			height : 400,
 			closed : false,
 			cache : false,
 			href : map["href"],
 			modal : true,
 			buttons : [ {
 				text : '保存',
 				iconCls : 'icon-ok',
 				handler : function() {
 					for(instance in CKEDITOR.instances){
 						CKEDITOR.instances[instance].updateElement();
 					}
 					var savebtn = $(this);
 	　　				var disabled=savebtn.hasClass("l-btn-disabled");
 	　　				if(!disabled){
 	　　					submitFormcontact(map,savebtn);
 					}
 				}
 			}/* , {
 				text : '清空',
 				handler : function() {
 					clearFormcontact(map);
 				}
 			} */ ]
 		});
 	}
 	
  
 	/*
 	*	新增负责人
 	*/
 	function submitFormmcmember(map) {
 		$("#me_password").val($("#me_mima").val());
 		var data = {};
 		data.name=$("#me_name").val();
 		//data.status=$("#me_status").combobox('getValue');
 		data.content=$("#me_content").val();
 		data.phone=$("#me_phone").val();
 		data.username=$("#me_username").val();
 		data.sort=$("#me_sort").val();
 		data.password=$("#me_password").val();
 		//var formflag = $(map["formId"]).form().form('validate');
 		if (data.name != '' && data.username != '' && data.password != ''  && condition_mcmember && data.sort != '') {
 			$.Loading.show(map["loadshow"]);
 			var options = {
 				url : map["url"],
 				type : "POST",
 				dataType : 'json',
 				data:data,
 				success : function(result) {
 					if (result.result == 1) {
 						$.Loading.show(result.message);
 						$(map["divDialog"]).dialog('close');
 						//$(map["gridreload"]).datagrid('reload');
 						$('#blame').combobox('reload');//新增后，刷新树
 					}
 					if (result.result == 0) {
 						$.Loading.error(result.message);
 					}
 					$.Loading.hide();
 				},
 				error : function(e) {
 					alert("出现错误 ，请重试");
 				}
 			};
 			$.ajax(options);
 		}
 	}
 	function appendmcmember(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcMember/add_mcMember.jspx";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcMember/doAdd_memberGrade.jspx?ajax=yes";
 			map["title"] = "添加信息";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcMember/edit_memberGrade.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcMember/doEdit_memberGrade.jspx?ajax=yes";
 			map["title"] = "修改信息";
 			map["loadshow"] = "正修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#memberdata";
 		addDialogmcmember(map);
 	}
 	
 	function addDialogmcmember(map) {
 		$(map["divDialog"]).show();
 		$(map["divDialog"]).dialog({
 			title : map["title"],
 			width : 600,
 			height : 340,
 			closed : false,
 			cache : false,
 			href : map["href"],
 			modal : true,
 			buttons : [ {
 				text : '保存',
 				iconCls : 'icon-ok',
 				handler : function() {
 					for(instance in CKEDITOR.instances){
 						CKEDITOR.instances[instance].updateElement();
 					}
 					var savebtn = $(this);
 	　　				var disabled=savebtn.hasClass("l-btn-disabled");
 	　　				if(!disabled){
 						 submitFormmcmember(map,savebtn);
 					}
 				}
 			}/* , {
 				text : '清空',
 				handler : function() {
 					clearForm(map);
 				}
 			} */ ]
 		});
 	}
 	
 	
 	/*
 		新增公司
 	*/
 	function submitFormmccompanyname(map) {
 		var data = {};
 		//data.status=$("#me_status").combobox('getValue');
 		data.address=$("#cn_address").val();
 		data.name=$("#cn_name").val();
 		data.phone=$("#cn_phone").val();
 		data.sort=$("#cn_sort").val();
 		data.remark=$("#cn_remark").val();
 		//var formflag = $(map["formId"]).form().form('validate');
 		if (data.name != '' && data.sort != '' && condition_companyname) {
 			$.Loading.show(map["loadshow"]);
 			var options = {
 				url : map["url"],
 				type : "POST",
 				dataType : 'json',
 				data:data,
 				success : function(result) {
 					if (result.result == 1) {
 						$.Loading.show(result.message);
 						$(map["divDialog"]).dialog('close');
 						//$(map["gridreload"]).datagrid('reload');
 						$('#name').combobox('reload');//新增后，刷新树
 					}
 					if (result.result == 0) {
 						$.Loading.error(result.message);
 					}
 					$.Loading.hide();
 				},
 				error : function(e) {
 					alert("出现错误 ，请重试");
 				}
 			};
 			console.log(data);
 			$.ajax(options);
 		}
 	}
 	
 	
 	function appendmccompanyname(id) {
 		var map = {}; // Map map = new HashMap();
 		if (!id) {
 			map["href"] = "/admin/mcCompanyName/add_mcCompanyName.jspx";
 			map["formId"] = "#addForm";
 			map["url"] = "/admin/mcCompanyName/doAdd_memberGrade.jspx?ajax=yes";
 			map["title"] = "添加信息";
 			map["loadshow"] = "正在添加......";
 		} else {
 			map["href"] = "/admin/mcCompanyName/edit_memberGrade.jspx?id=" + id;
 			map["formId"] = "#editForm";
 			map["url"] = "/admin/mcCompanyName/doEdit_memberGrade.jspx?ajax=yes";
 			map["title"] = "修改信息";
 			map["loadshow"] = "正修改......";
 		}
 		map["divDialog"] = "#divdia2";
 		map["gridreload"] = "#memberdata";
 		addDialogmccompanyname(map);
 	}

     function appendmccompanyname2(id) {
         var map = {}; // Map map = new HashMap();
         if (!id) {
             map["href"] = "/admin/mcCompanyName/add_universitiesName.jspx";
             map["formId"] = "#addForm";
             map["url"] = "/admin/mcCompanyName/doAdd_memberGrade.jspx?ajax=yes";
             map["title"] = "添加信息";
             map["loadshow"] = "正在添加......";
         } else {
             map["href"] = "/admin/mcCompanyName/edit_memberGrade.jspx?id=" + id;
             map["formId"] = "#editForm";
             map["url"] = "/admin/mcCompanyName/doEdit_memberGrade.jspx?ajax=yes";
             map["title"] = "修改信息";
             map["loadshow"] = "正修改......";
         }
         map["divDialog"] = "#divdia2";
         map["gridreload"] = "#memberdata";
         addDialogmccompanyname(map);
     }
 	
 	function addDialogmccompanyname(map) {
 		$(map["divDialog"]).show();
 		$(map["divDialog"]).dialog({
 			title : map["title"],
 			width : 600,
 			height : 300,
 			closed : false,
 			cache : false,
 			href : map["href"],
 			modal : true,
 			buttons : [ {
 				text : '保存',
 				iconCls : 'icon-ok',
 				handler : function() {
 					for(instance in CKEDITOR.instances){
 						CKEDITOR.instances[instance].updateElement();
 					}
 					var savebtn = $(this);
 	　　				var disabled=savebtn.hasClass("l-btn-disabled");
 	　　				if(!disabled){
 						 submitFormmccompanyname(map,savebtn);
 					}
 				}
 			}/* , {
 				text : '清空',
 				handler : function() {
 					clearForm(map);
 				}
 			}  */]
 		});
 	}
	
 	var condition_company=true;
 	 $("#name").combobox({
		required:true,
		valueField:'id',
		textField:'text',
		editable:true,
		url:'/admin/mcCompanyName/get_mcmember_names.jspx',
		onSelect:function(node){
			$.ajax({
				url:"/admin/ModelCollection/register.jspx",
				data:{name:node.id,edit_name:'${item.name}'},
				dataType:"json",
				type:"post",
				success:function(msg){
					if(msg.cont == 'false'){
						$(".rr_model").text('数据已存在，请修改！');
						condition_company=false;
						return false;
					}
					if(msg.cont == 'true'){
						$(".rr_model").text('');
						condition_company=true;
						return true;
					}
				},
				error:function(){
					alert("网络异常,请重试!");
					return false;
				}
			});
			
					$("#contact").combobox('clear');
					$("#name_fujian").val('false');
					var url ='/admin/mcContacts/get_mcmember_names.jspx?mc_company_id='+node.id;
					$("#contact").combobox('reload',url);//input框中有树方法

		}
	}); 
 	 
 	//该方法判断公司是否为下拉选择，当为直接输入时，提醒，并禁止保存（当combobox失去焦点时触发）
 	$("#name").combobox('textbox').bind('blur', function(e) {
 		var name=$("#name").combobox('getValue');
 		if(isNaN(name)){ //isNaN()函数，如果传入的参数是数字返回false,否则返回true
 			$(".rr_model").text('不可输入，请下拉选择！');
 			condition_company=false;
 			return false;
 		}
 		
 	/*	$.ajax({
 			url:"/admin/ModelCollection/register.jspx",
 			data:{name:name},
 			dataType:"json",
 			type:"post",
 			success:function(msg){
 				if(msg.cont == 'false'){
 					$(".rr_model").text('数据已存在，请修改！');
 					condition_company=false;
 					return false;
 				}
 				if(msg.cont == 'true'){
 					$(".rr_model").text('');
 					condition_company=true;
 					return true;
 				}
 			},
 			error:function(){
 				alert("网络异常,请重试!");
 				return false;
 			}
 		});
 				$("#contact").combobox('clear');
 				var url ='/admin/mcContacts/get_mcmember_names.jspx?mc_company_id='+name;
 				$("#contact").combobox('reload',url);//input框中有树方法
*/ 		
 		
 		
 	});
 	 
 	 
 	 
 	 