function submitForm(map) {
	var formflag = $(map["formId"]).form().form('validate');
	if (formflag) {
		$.Loading.show(map["loadshow"]);
		var options = {
			url : map["url"],
			type : "POST",
			dataType : 'json',
			success : function(result) {
				if (result.result == 1) {
					$.Loading.show(result.message);
					$(map["divDialog"]).dialog('close');
					$(map["tableId"]).datagrid('reload');
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
		$(map["formId"]).ajaxSubmit(options);
	}
}
	
	function addDialog(map) {
		$(map["divDialog"]).show();
		$(map["divDialog"]).dialog({
			title : map["title"],
			width : map["width"],
			height : map["height"],
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
						 submitForm(map,savebtn);
					}
				}
			}, {
				text : '清空',
				handler : function() {
					clearForm(map);
				}
			} ]
		});
	}
	

	function del(map) {
		var rows = $(map["tableId"]).datagrid("getSelections");
		if (rows.length < 1) {
			$.Loading.error("请选择要删除的项");
			return;
		}
		if (!confirm("确认删除选中的该信息？")) {
			return;
		}
		var options = {
			url : map["delUrl"],
			type : "POST",
			dataType : 'json',
			success : function(result) {
				$.Loading.success(result.message);
				if (result.result == 1) {
					var rows = $(map["tableId"]).datagrid("getSelections");
					for ( var i = 0; i < rows.length; i++) {
						var index = $(map["tableId"]).datagrid('getRowIndex',
								rows[i]);
						$(map["tableId"]).datagrid('deleteRow', index);
					}
				}
			},
			error : function(e) {
				$.Loading.error("出现错误 ，请重试");
			}
		};
		$(map["indexFormId"]).ajaxSubmit(options);
	}
	
	function clearForm(map) {
		$(map["formId"]).form('clear');
	}
	
	function formatOperation(value, row, index) {
		var val = "<a class='edit' title='修改' href='javascript:void(0);' onclick='append("+row.id+",map)' ></a>";
		return val;
	}
	
	function formatImg(value ,row,index){
		return "<img src='"+value+ "' style='width:100xp;height:50px'/>";
	}
	
	