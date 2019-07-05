/**
 * 项目名：读者云图书馆网站
 * 功能简述：图书选择
 * 作者：读者云图书馆网站项目组
 * $Id: bookSelect.js 1565 2015-04-07 02:39:29Z xyf $
 */
(function($){
	var data_name = 'bookSelect_data';
	/**
	 * 选择图书。
	 * options：
	 * @param whereArray
	 * @param success
	 */
	$.bookSelect = function(options){
		var aa = options.dataTable ? options.dataTable.aoColumns : null;
		options = $.extend(true, {}, $.bookSelect.defaults, options);
		if(aa)
			options.dataTable.aoColumns = aa;
		
		var newstr = '\
			<div class="'+options.containerClassName+'">\
			<table style="width:100%;"></table>\
			</div>\
			';
		var newobj = $(newstr);
		var $table = newobj.find("table:eq(0)");
		
		options.tableobj = $table;
//		newobj.data(data_name, options);
		$table.data(data_name, options);

		options.dataTable_fnRowCallback = options.dataTable.fnRowCallback;
		options.dataTable.fnRowCallback = myRowCallback;

		options.dataTable_fnInitComplete = options.dataTable.fnInitComplete;
		options.dataTable.fnInitComplete = myInitComplete;
		
		if(!options.selected)
			options.selected = { };
		else{
			var typeName = options.selected.constructor.name;
			if(typeName == 'Array'){
				var a = { };
				$.each(options.selected, function(idx, ele){
					a[ele[options.dataTable.keyColumn]] = ele;
				});
				options.selected = a;
			}else if(typeName == 'Object'){
				// do nothing
			}else{
				options.selected = { };
			}
		}
		
		var dialogOption = {
			modal: true,
			title: options.lang.title,
			width: options.width,
			height: options.height,
			closeText: options.lang.cancel,
			open: function(event, ui){
				$table.dataTable(options.dataTable);
			},
			buttons: {
			},
			create: function(event, ui) {
				//debugger;
				if(! options.multiSelect) return;
				
				var btnset = $('<div class="ui-dialog-buttonset" style="float:left"></div>');
				$(this).parent().find('.ui-dialog-buttonpane').append(btnset);
				var btn;
				btn = $('<button>全选本页项目</button>').button().click(function(){
					var rows = options.tableobj.find('tbody tr');
					var dt = options.tableobj.dataTable();
					$.each(rows, function(idx, ele){
						var dd = dt.fnGetData(ele);
						$(ele).addClass(options.selectedClass);
						options.selected[dd[options.dataTable.keyColumn]] = dd;
					});
					if(options.selectedFunc)
						options.selectedFunc(options.selected);
				});
				btnset.append(btn);
				btn = $('<button>取消所有选择</button>').button().click(function(){
					options.tableobj.find('tbody tr').removeClass(options.selectedClass);
					options.selected = { };
					if(options.selectedFunc)
						options.selectedFunc(options.selected);
				});
				btnset.append(btn);
			}
		};
		dialogOption.buttons[options.lang.ok] = function() {
			var itemdata = getcurrentdata($table);
			if(options.success){
				var ret = options.success(itemdata);
				if(ret === false)
					return false;
			}
			newobj.dialog('close');
			clean(newobj);
		};
		dialogOption.buttons[options.lang.cancel] = function() {
			newobj.dialog('close');
			clean(newobj);
		};
		newobj.dialog(dialogOption);
	};
	
	var methods = {
		init : function(options) {
			return $(this).each(function(idx, ele){
				var $table = $(ele);
				options = $.extend(true, {}, $.bookSelect.defaults, options);
				
				options.tableobj = $table;
				$table.data(data_name, options);
	
				options.dataTable_fnRowCallback = options.dataTable.fnRowCallback;
				options.dataTable.fnRowCallback = myRowCallback;
	
				options.dataTable_fnInitComplete = options.dataTable.fnInitComplete;
				options.dataTable.fnInitComplete = myInitComplete;
				
				options.selected = { };
				
				$table.dataTable(options.dataTable);
			});
		},
		selectAllInPage: function() {
			return $(this).each(function(idx, ele){
				var $table = $(ele), options = $table.data(data_name);
				var rows = options.tableobj.find('tbody tr');
				var dt = options.tableobj.dataTable();
				$.each(rows, function(idx, ele){
					var dd = dt.fnGetData(ele);
					$(ele).addClass(options.selectedClass);
					options.selected[dd[options.dataTable.keyColumn]] = dd;
				});
				if(options.selectedFunc)
					options.selectedFunc(options.selected);
			});
		},
		clearSelect: function() {
			return $(this).each(function(idx, ele){
				var $table = $(ele), options = $table.data(data_name);
				options.tableobj.find('tbody tr').removeClass(options.selectedClass);
				options.selected = { };
				if(options.selectedFunc)
					options.selectedFunc(options.selected);
			});
		}
	};

	$.fn.bookSelect = function(method) {
		// Method calling logic
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.regionSelect');
		}
	};

	function myRowCallback( nRow, aData, iDisplayIndex ) {
		var $table = $(this), options = $table.data(data_name);
		$(nRow).click(function(){
			var dt = $table.dataTable();
			var dd = dt.fnGetData(this);
			if(options.multiSelect){
				if ( $(this).hasClass(options.selectedClass) ) {
					$(this).removeClass(options.selectedClass);
					delete options.selected[dd[options.dataTable.keyColumn]];
				}else{
					$(this).addClass(options.selectedClass);
					options.selected[dd[options.dataTable.keyColumn]] = dd;
				}
			}else{
				if ( $(this).hasClass(options.selectedClass) ) {
		            $(this).removeClass(options.selectedClass);
		            delete options.selected[dd[options.dataTable.keyColumn]];
		        }else {
		        	$table.find('tr.'+options.selectedClass).removeClass(options.selectedClass);
		            $(this).addClass(options.selectedClass);
		            options.selected = { };
		            options.selected[dd[options.dataTable.keyColumn]] = dd;
		        }
			}
			if(options.selectedFunc)
				options.selectedFunc(options.selected);
		});
		if(options.selected[aData[options.dataTable.keyColumn]])
			$(nRow).addClass(options.selectedClass);
		if(options.dataTable_fnRowCallback){
			options.dataTable_fnRowCallback.call(this, nRow, aData, iDisplayIndex);
		}
	}
	
	function myInitComplete( ){
		var that = $(this);
		var options = that.data(data_name);
		var dt = that.dataTable();
		var foot = that.find('tfoot:eq(0) tr:eq(0)');
		if(!foot.length){
			foot = $('<tfoot><tr></tr></tfoot>');
			$(this).append(foot);
			foot = foot.find('tr:eq(0)');
		}
		var settings = dt.fnSettings();
		var colCount = 0;
		$.each(settings.aoColumns, function(id, ele){
			if(!ele.bVisible) return true;
			
			colCount ++;
			var $td;
			$td = foot.find('td:nth-child('+colCount+')');
			while(!$td.length){
				$td = $('<td/>');
				foot.append($td);
				$td = foot.find('td:nth-child('+colCount+')');
			}
			
			if(ele.oFilter){
				switch(ele.oFilter.type){
				case 'text':
					var $obj = $('<input/>');
					if(ele.oFilter.hints){
						$obj.title = ele.oFilter.hints;
					}
					$td.append($obj);
					$obj.change(function(){
						dt.fnFilter($obj.val(), id);
					});
					break;
				case 'select':
					var $obj = $('<select><option></option></select>');
					$obj.data(data_name + '_filter', ele.oFilter);
					if(ele.oFilter.url){
						$.ajax(ele.oFilter.url, {
							dataType: 'json',
							success: function(data){
								$obj.empty();
								$obj.append('<option></option>');
								if(data.status){
									$.each(data.data, function(id,ele1){
										var nameField = ele.oFilter.valField || 'name';
										var $o = $('<option></option>');
										$o.appendTo($obj);
										$o.text(ele1[nameField].replace(/&nbsp;/g, '\xa0'));
										//$o.html(ele1[nameField]);
										if(ele.oFilter.keyField){
											$o.attr('value', ele1[ele.oFilter.keyField]);
										}
									});
								}
							}
						});
					}
					$td.append($obj);
					$obj.change(function(){
						var oFilter = $(this).data(data_name + '_filter');
						var val;
						if(oFilter.getFilterValue){
							val = oFilter.getFilterValue(that, $obj);
						}else{
							val = $obj.val();
						}
						dt.fnFilter(val, id);
					});
					break;
				}
			}
		});
		if(options.dataTable_fnInitComplete){
			options.dataTable_fnInitComplete.call(this);
		}
	}

	function getcurrentdata(oTableLocal){
		var options = $(oTableLocal).data(data_name);
		var ret = null;
		$.each(options.selected, function(idx, ele){
			if(options.multiSelect){
				if(!ret) ret = [];
				ret.push(ele);
			}else{
				ret = ele;
				return false;
			}			
		});
		return ret;
	}
	
	$.bookSelect.defaults = {
		lang: {
			title: '选择图书',
			ok: '确定',
			cancel: '取消'
		},
		containerClassName: 'bs_container',
		selectedClass: 'row_selected',
		selectedFunc: function(selected) { },
		width: 800,
		//height: 430,
		dataTable: {
			bFilter: true,
			keyColumn: 'book_id',
			"sAjaxSource": "/ajax.php?act=bookList",
			bServerSide: true,
			"aoColumns": [
				{ "sName": "book_id", mDataProp: 'book_id', "sTitle": "产品id", bVisible: false},
				{ "sName": "book_name", mDataProp: 'book_name', "sTitle": "产品名称", oFilter: { type: 'text', hints: '过滤产品名称' } },
				{ "sName": "book_catalog_name", mDataProp: 'book_catalog_name', "sTitle": "产品类别", oFilter: { type: 'select', url: '/ajax.php?act=selectOptionList&type=catalog', keyField: 'id', valField: 'name', getFilterValue: function(dataTable, field){
					var val = $(field).val();
					if(val)
						return val.trim();
					else
						return val;
				} } },
				{ "sName": "vendor_name", mDataProp: 'vendor_name', "sTitle": "产品出版商", oFilter: { type: 'select', url: '/ajax.php?act=selectOptionList&type=vendor' } },
				{ "sName": "book_price", mDataProp: 'book_price', "sTitle": "产品价格" }
			],
			"oLanguage": { "sUrl": "/admin/plugins/datatables/language/ch_gb.txt", "sSearch": "Search all columns:" }
		}
	};
	
	function clean(newobj) {
		newobj.remove();
	}
	
})(jQuery);
