

	/**
	 *主营设备品牌型号js代码开始
	 */
	$("#brand").combobox({
		valueField: 'id',
		textField: 'nameEn',
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
	var condition_company=true;

	$('#brand').combobox({
		onSelect: function(record){
			additional(0, record, null, null, null)
		}
	});
	$("#su_productclass_name").combotree({
		required:false,
		valueField:'id',
		textField:'text',
		editable:false,
		url:'/productClass/combotreeData.json',
		onBeforeSelect:function (node) {//只可选择子节点
			if (!$("#su_productclass_name").combotree("tree").tree('isLeaf', node.target)) {
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
		if(val.su_productclass_name){
			arr[i].su_productclass_name = val.su_productclass_name
		}
		val = arr[i];
		$.ajax({
			url:"/admin/mcBrandAndModel/addBrandAndModel.jspx",
			data:{
				brand:val.brand,
				model:val.model,
				su_productclass_name:val.su_productclass_name,
				su_company:'${item.name}'
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
			if(!t.brand && !t.model && !t.su_productclass_name){//什么都不写时不能让其再添加输入框
				alert("请填写仪器品牌")
				return true
			}
			if(t.brand && !t.model){//只写了品牌没写型号就点击再添加一个型号获取保存提交
				alert("请填写仪器型号")
				return true;
			} else if (t.brand && !t.su_productclass_name) {//写了品牌和型号，但是没选仪器名称，点击再添加一个型号获取保存提交
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
			} else if (t.brand && !t.su_productclass_name) {//写了品牌和型号，但是没选仪器名称，点击再添加一个型号获取保存提交
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
			obj1.su_productclass_name === obj2.su_productclass_name)
			return true;
		return false;
	}

	//    $('#su_productclass_coll').combotree({
	//        width:300,
	//        method:'get',
	//        url: '/productClass/combotreeData.json',
	//        multiple: true,
	//        onlyLeafCheck:true,
	//        onLoadSuccess: function (e) {
	//            $('#su_productclass_coll').combotree('setValues',"${item.su_productclass}".split(','))
	//        }
	//    });

	var arr = [{brand:null, model:null, number:null, su_productclass_name:null}]
	var ch
	$('tbody tr').each(function (i, e) {
		if(e.id === 'multiple_list') {
			ch = i
		}
	})

	function getVal(brand, model, number, mpn) {
		return {brand: brand?brand.id:null, model: model?$(model).val():null, number: number?$(number).val():null, su_productclass_name: mpn?mpn.id:null }
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
			"<input class=\"input_text easyui-validatebox\" style=\"display:none\" type=\"number\" name=\"number\" value=\"1\" data-options=\"required:true\">" +
			"名称<select style=\"width: 140px; height: 36px;\"  class=\"easyui-combobox\" name=\"su_productclass_name\" ></select>" +
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

		$($("select[name='su_productclass_name']")[0]).combotree({
			required:true,
			valueField:'id',
			textField:'text',
			editable:false,
			url:'/productClass/combotreeData.json',
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
	function deleteInput(obj, i) {
		arr.splice(i, 1)
		console.log(obj.parentElement.parentElement)
		//var  tableObj= document.getElementById("addForm").firstElementChild;
		$(obj.parentElement.parentElement).empty()
	}
	/**
	 * 主营设备品牌型号js代码结束
	 */

	/*
	新增行业
	*/
	function submitFormsuindustryclass(map) {
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
						$('#su_industry_class_coll').combobox('reload');//新增后，刷新树
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
    /**
     *主营设备品牌型号js代码结束
     */
	
	function appendsuindustryclass(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suIndustyClass/add_suIndustryClass.jspx";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suIndustyClass/save_suIndustryClass.jspx?ajax=yes";
			map["title"] = "添加行业";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suIndustyClass/edit_suIndustryClass.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suIndustyClass/save_edit_suIndustryClass.jspx?ajax=yes";
			map["title"] = "修改行业";
			map["loadshow"] = "正在修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#dataList";
		addDialogsuindustryclass(map);
	}
	
	function addDialogsuindustryclass(map) {
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
					 submitFormsuindustryclass(map,savebtn);
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
	function submitFormsumajor(map) {
		
	var data = {};
	//data.su_industryclass=$("#su_industryclass").combobox('getValue'); 
	data.name=$("#mma_name").val();
	data.sort=$("#mma_sort").val();
	data.remark=$("#mma_remark").val();
	//var formflag = $(map["formId"]).form().form('validate');
	if (data.name != '' && data.sort != '' && data.su_industryclass != '') {
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
	
	
	function appendsumajor(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suMajor/add_suMajor.jspx?model_yincang=true";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suMajor/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
			map["title"] = "添加信息";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suMajor/edit_memberGrade.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suMajor/doEdit_memberGrade.jspx?ajax=yes";
			map["title"] = "修改信息";
			map["loadshow"] = "正修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#dataList";
		addDialogsumajor(map);
	}
	
	function addDialogsumajor(map) {
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
					 submitFormsumajor(map,savebtn);
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
	function submitFormsuproductclass(map) {
		
	var data = {};
	//data.su_major=$("#su_major").combobox('getValue'); 
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
					$('#su_productclass_coll').combobox('reload');//新增后，刷新树
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
	
	
	function appendsuproductclasstwo(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/basedata/suproductclass/add_suproductclass.jspx?model_yincang=true";
			map["formId"] = "#addForm";
			map["url"] = "/admin/basedata/suproductclass/save_suproductclass.jspx?ajax=yes&model_yincang=true";
			map["title"] = "添加常用仪器";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/basedata/suproductclass/edit_suproductclass.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/basedata/suproductclass/save_edit_suproductclass.jspx?ajax=yes";
			map["title"] = "修改常用仪器";
			map["loadshow"] = "正在修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#dataList";
		addDialogsuproductclass(map);
	}
	
	function addDialogsuproductclass(map) {
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
					 submitFormsuproductclass(map,savebtn);
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
	//新增品牌
	
	function submitFormsubrand(map) {
	var data = {};
	data.su_productclass=$("#su_productclass").combobox('getValue'); 
	data.name=$("#mb_name").val();
	data.sort=$("#mb_sort").val();
	data.remark=$("#mb_remark").val();
	//var formflag = $(map["formId"]).form().form('validate');
	if (data.name != '' && data.sort != '' && data.su_productclass != '') {
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
					$('#su_brand_coll').combobox('reload');//新增后，刷新树
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
	
	
	function appendsubrand(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suBrand/add_suBrand.jspx?model_yincang=true";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suBrand/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
			map["title"] = "添加信息";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suBrand/edit_memberGrade.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suBrand/doEdit_memberGrade.jspx?ajax=yes";
			map["title"] = "修改信息";
			map["loadshow"] = "正修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#dataList";
		addDialogsubrand(map);
	}
	
	function addDialogsubrand(map) {
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
					 submitFormsubrand(map,savebtn);
				}
			}
		}//, {
			//text : '清空',
		//	handler : function() {
		//		clearForm(map);
		//	}
		//}
		]
	});
	}	
	
	
	
	//新增型号
	
	function submitFormsumodel(map) {
	var data = {};
	data.su_brand=$("#su_brand").combobox('getValue'); 
	data.name=$("#mm_name").val();
	data.sort=$("#mm_sort").val();
	data.remark=$("#mm_remark").val();
	//var formflag = $(map["formId"]).form().form('validate');
	if (data.name != '' && data.sort != '' && data.su_brand != '') {
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
					$('#su_model_coll').combobox('reload');//新增后，刷新树
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
	
	
	function appendsumodel(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suModel/add_suModel.jspx?model_yincang=true";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suModel/doAdd_memberGrade.jspx?ajax=yes";
			map["title"] = "添加型号";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suModel/edit_memberGrade.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suModel/doEdit_memberGrade.jspx?ajax=yes";
			map["title"] = "修改型号";
			map["loadshow"] = "正修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#dataList";
		addDialogsumodel(map);
	}
	
	function addDialogsumodel(map) {
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
					 submitFormsumodel(map,savebtn);
				}
			}
		}// , {
		//	text : '清空',
		//	handler : function() {
		//		clearForm(map);
		//	}
		//}  
		]
	});
	}	 */
	
 
/*
 * 新增常用联系人
 */
 function submitForsuontact(map) {
	 	var data = {};
	 	data.su_company=$("#su_company").combobox('getValue'); 
		data.name=$("#co_name").val();
		data.job=$("#co_job").val();
		data.mail=$("#co_mail").val();
		data.phone=$("#co_phone").val();
		data.sort=$("#co_sort").val();
		data.remark=$("#co_remark").val();
		//var formflag = $(map["formId"]).form().form('validate');
		if (data.name != '' && data.su_company != '') {
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
                    var url1 ='/admin/suContacts/get_sumember_names.jspx?su_company_id='+id;
                    $("#contact").combobox('reload',url1);
                    $.ajax({
                        //几个参数需要注意一下
                        type: "POST",//方法类型
                        dataType: "json",//预期服务器返回的数据类型
                        url: url1 ,//url
                        success: function (result) {
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
function appendcontact(id,companyId) {
    var newCompanyId;//定义变量
    if (companyId == undefined) {//是添加公司页面中的添加常用联系人的方法
        newCompanyId = $("#name").combobox('getValue');//获取combobox选中的公司id
    } else {//是编辑公司的页面中的添加常用联系人的方法
        newCompanyId = companyId;//获取后台传回来的公司id'${item.name}’
    }
	var map = {}; // Map map = new HashMap();
	if (!id) {
		map["href"] = "/admin/suContacts/add_suContacts.jspx?model_yincang=true&companyId="+newCompanyId;
		map["formId"] = "#addForm";
		map["url"] = "/admin/suContacts/doAdd_memberGrade.jspx?ajax=yes&model_yincang=true";
		map["title"] = "添加信息";
		map["loadshow"] = "正在添加......";
	} else {
		map["href"] = "/admin/suContacts/edit_memberGrade.jspx?id=" + id;
		map["formId"] = "#editForm";
		map["url"] = "/admin/suContacts/doEdit_memberGrade.jspx?ajax=yes";
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
	　　					submitForsuontact(map,savebtn);
					}
				}
			}/* , {
				text : '清空',
				handler : function() {
					clearForsuontact(map);
				}
			} */ ]
		});
	}
	
 
	/*
	*	新增负责人
	*/
	function submitFormsumember(map) {
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
		if (data.name != '' && data.username != '' && data.password != ''  && condition_sumember && data.sort != '') {
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
	function appendsumember(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suMember/add_suMember.jspx";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suMember/doAdd_memberGrade.jspx?ajax=yes";
			map["title"] = "添加信息";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suMember/edit_memberGrade.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suMember/doEdit_memberGrade.jspx?ajax=yes";
			map["title"] = "修改信息";
			map["loadshow"] = "正修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#memberdata";
		addDialogsumember(map);
	}
	
	function addDialogsumember(map) {
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
						 submitFormsumember(map,savebtn);
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
	function submitFormsucompanyname(map) {
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
	
	
	function appendsucompanyname(id) {
		var map = {}; // Map map = new HashMap();
		if (!id) {
			map["href"] = "/admin/suCompanyName/add_suCompanyName.jspx";
			map["formId"] = "#addForm";
			map["url"] = "/admin/suCompanyName/doAdd_memberGrade.jspx?ajax=yes";
			map["title"] = "添加信息";
			map["loadshow"] = "正在添加......";
		} else {
			map["href"] = "/admin/suCompanyName/edit_memberGrade.jspx?id=" + id;
			map["formId"] = "#editForm";
			map["url"] = "/admin/suCompanyName/doEdit_memberGrade.jspx?ajax=yes";
			map["title"] = "修改信息";
			map["loadshow"] = "正修改......";
		}
		map["divDialog"] = "#divdia2";
		map["gridreload"] = "#memberdata";
		addDialogsucompanyname(map);
	}
	
	function addDialogsucompanyname(map) {
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
						 submitFormsucompanyname(map,savebtn);
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
