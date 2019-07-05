var isDusMutiple = false, isProMutiple = false, isBrandMutiple = false, isSerTypeMutiple = false;
var isProductSearch = GetQueryString("isProductSearch");// 是否是产品搜索 fang20160425
if (isProductSearch == "true") {
	isProductSearch = true;
} else {
	isProductSearch = false;
}
function searProLeafs() {
	$.ajax({
		url : "/industryClass/searchLeafs",
		data : {
			name : $("#proText").val()
		},
		dataType : "json",
		type : "post",
		success : function(data) {
			$("#indClass_children_children").find("li").remove();
			for ( var item in data) {
				$("#indClass_children_children").find("ul").append(
						"<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"
								+ data[item].id + "'>" + data[item].text
								+ "</a></li>");
			}
		},
		error : function(msg) {
			alert("网络错误,请重试!");
		}
	});
	return false;
}
function GetQueryString(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
	var r = window.location.search.substr(1).match(reg);
	if (r != null)
		return unescape(r[2]);
	return null;
}
function searIndLeafs() {
	$.ajax({
		url : "/productClass/searchLeafs",
		data : {
			name : $("#indusText").val()
		},
		dataType : "json",
		type : "post",
		success : function(data) {
			$("#proClass_children_children").find("li").remove();
			for ( var item in data) {
				$("#proClass_children_children").find("ul").append(
						"<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"
								+ data[item].id + "'>" + data[item].text
								+ "</a></li>");
			}
		},
		error : function(msg) {
			alert("网络错误,请重试!");
		}
	});
	return false;
}
function searBrandLeafs() {
	$.ajax({
		url : "/brand/searchLeafs",
		data : {
			name : $("#brandText").val()
		},
		dataType : "json",
		type : "post",
		success : function(data) {
			$("#brandArea_children").find("li").remove();
			for ( var item in data) {
				$("#brandArea_children").find("ul").append(
						"<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"
								+ data[item].id + "'>" + data[item].text
								+ "</a></li>");
			}
		},
		error : function(msg) {
			alert("网络错误,请重试!");
		}
	});
	return false;
}

$(function() {
	// 服务类型不限 add by fang 20160220
	$("#serTypeRemove").on("click", function() {
		$(this).closest(".listtypelid").find(".contd li").removeClass("on");
		$("#searchForm").children("input[name=serTypeIds]").attr("value", "");
		$(".whered").children(".wlid[s_type=serTypeIds]").remove();
		$("#seletedCons [s_type=serTypeIds]").remove();
		submitPost();
	})
	// 产品分类不限 add by fang 20160220
	$("#indRemove").on("click", function() {
		$(this).closest(".listtypelid").find(".contd li").removeClass("on");
		$("#searchForm").children("input[name=indClass]").attr("value", "");
		$(".whered").children(".wlid[s_type=indClass]").remove();
		submitPost();
	})
	// 设备分类不限 add by fang 20160220
	$("#proRemove").on("click", function() {
		$(this).closest(".listtypelid").find(".contd li").removeClass("on");
		$("#searchForm").children("input[name=proClass]").attr("value", "");
		$(".whered").children(".wlid[s_type=proClass]").remove();
		submitPost();
	})
	// 设备分类不限 add by fang 20160220
	$("#brandRemove").on("click", function() {
		$(this).closest(".listtypelid").find(".contd li").removeClass("on");
		$("#searchForm").children("input[name=brands]").attr("value", "");
		$(".whered").children(".wlid[s_type=brands]").remove();
		submitPost();
	})

	// 服务类型单选 add by fang 20150917
	$("#serTypeIds").find(".contd").find("li a").click(function() {
		addCondition(this);
	});

	// 产品分类内二阶搜索
	seachChild("#proClass", "#proClass_children", "/productClass/dataJson/");
	// 条件搜索栏搜索 add by fang 20160112
	$("#proSearch").on("click", searProLeafs);
	$("#proText").on("keydown", function(e) {
		if (e && e.keyCode == 13) {
			searProLeafs();
		}
	})
	// 行业分类内二阶搜索
	seachChild("#indClass", "#indClass_children", "/industryClass/dataJson/");
	$("#indusSearch").on("click", searIndLeafs);
	$("#indusText").on("keydown", function(e) {
		if (e && e.keyCode == 13) {
			searIndLeafs();
		}
	})
	// 字母区域对应品牌
	seachChild("#brandArea", "#brandArea_children", "/brand/dataJson/");
	// 品牌点击搜索
	$("#brands").find(".contd").find("li a").click(function() {
		addCondition(this);
	});
	$("#brandSearch").on("click", searBrandLeafs);
	$("#brandText").on("keydown", function(e) {
		if (e && e.keyCode == 13) {
			searBrandLeafs();
		}
	})
	// 综合排序搜索
	$(".prosortd").find("input[type='radio']").change(function() {
		addCondition(this);
	});

	

	// 分页
	$(".pagd").find("a").click(
			function() {
				if ($(this).attr("s_val")) {
					$("#searchForm").find("input[name='pageNum']").attr(
							"value", $(this).attr("s_val"));
					if (!$(this).hasClass("on")) {
						submitPost();
						$(document).scrollTop(300);
						$("#searchForm").find("input[name='pageNum']").attr(
								"value", "");
					}
				}

			});

	// 多选 add by fang 20150916
	// 多选
	$(".listtypelid .mseld").click(
			function() {
				$that = $(this);
				var parentObj = $(this).parent().parent();
				var isMutiple = $(this).attr("isMut");
				var listObj = parentObj.find(".list");
				if (isMutiple == 1) {
					parentObj.removeClass("show");
					parentObj.find(".linkd").hide();
					$that.attr("isMut", "0");
					switch (parentObj.attr("id")) {
					case "indClass":
						isDusMutiple = false;
						break;
					case "proClass":
						isProMutiple = false;
						break;
					case "brands":
						isBrandMutiple = false;
						break;
					case "serTypeIds":
						isSerTypeMutiple = false;
						break;
					}

					$("#seletedCons").show();
					// 将多选取消
					parentObj.find("li").removeClass("on");

					$("#searchForm").find(
							"input[name='" + parentObj.attr("id") + "']").attr(
							"value", "");

				} else {
					parentObj.addClass("show");
					// listObj.slideDown(200);
					parentObj.find(".linkd").show();
					$that.attr("isMut", "1");
					switch (parentObj.attr("id")) {
					case "indClass":
						isDusMutiple = true;
						break;
					case "proClass":
						isProMutiple = true;
						break;
					case "brands":
						isBrandMutiple = true;
						break;
					case "serTypeIds":
						isSerTypeMutiple = true;
						break;
					}
				}
			});

	// 多选确认提交
	$(".qr")
			.click(
					function() {
						var obj = $(this).closest(".listtypelid");
						var text = "";

						obj.find("li").each(function() {
							if ($(this).hasClass("on")) {
								text += " " + $(this).text();
							}
						});
						// 显示已选条件
						$(".whered .wlid").each(function() {
							if ($(this).attr("s_type") == obj.attr("id")) {
								$(this).remove();
							}
						});
						var whereItem = "<div class='wlid' s_type='"
								+ obj.attr("id")
								+ "' s_id='多选' ><p>"
								+ text
								+ "</p><p class='ico'><a href='javascript:;' onclick='remCondition(this,"
								+ false + ");'></a></p></div>";
						$(".whered").append(whereItem);
						// 隐藏多选条件
						obj.slideUp(200);
						// 提交表单数据
						submitPost();
					});
});

// 取得该节点下的子节点
function seachChild(parentId, childId, url) {
	$(parentId)
			.find("li a")
			.click(
					function() {
						var val = $(this).attr("s_val"), data = {}, isMutCheck = isMutiple($(parentId));
						$that = $(this);
						if (!val) {
							// 点击品牌字母时
							val = "areaBrands";
							data.area = $(this).text();
						} else {
							addCondition(this);
						}
						// 多选情况下不查询出子节点信息
						if (isMutCheck) {
							return;
						}
						$
								.ajax({
									url : url + val,
									type : "post",
									datatype : "json",
									data : data,
									success : function(data) {
										$(childId).find("li").remove();
										$(childId + "_children").find("li")
												.remove();
										for ( var item in data) {
											$(childId).find("ul").append(
													"<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"
															+ data[item].id
															+ "'>"
															+ data[item].text
															+ "</a></li>");
										}
										// 加入三阶搜索 add by fang 20150911
										$(childId)
												.find("li a")
												.click(
														function() {
															var childData = {};
															var is3MutCheck = false;
															if (isMutiple($(parentId))) {
																return;
															}
															$
																	.ajax({
																		url : url
																				+ $(
																						this)
																						.attr(
																								"s_val"),
																		type : "post",
																		datatype : "json",
																		success : function(
																				data) {
																			$(
																					childId
																							+ "_children")
																					.find(
																							"li")
																					.remove();
																			for ( var item in data) {
																				$(
																						childId
																								+ "_children")
																						.find(
																								"ul")
																						.append(
																								"<li><a href='javascript:void(0);' onclick='addCondition(this);' s_val='"
																										+ data[item].id
																										+ "'>"
																										+ data[item].text
																										+ "</a></li>");
																			}
																		},
																		error : function(
																				error) {
																		}
																	});
														});
									},
									error : function(error) {
									}
								});

					});
}

function isMutiple(typeObj) {
	switch (typeObj.attr("id")) {
	case "indClass":
		return isDusMutiple;
	case "proClass":
		return isProMutiple;
	case "brands":
		return isBrandMutiple;
	case "serTypeIds":
		return isSerTypeMutiple;
	}
}

//增加条件
function addCondition(obj) {
	var $this = $(obj), isExist = false, isTypeExist = false, whereItem = "", text = "";
	// 是否是综合排序
	var isCooOrder = false;
	var id = $this.attr("s_val");
	var type = $this.closest(".listtypelid").attr("id"), isMutCheck = isMutiple($this
			.closest(".listtypelid"));
	if (!type) {
		// 综合排序时情况
		type = $this.attr("name");
		isCooOrder = true;
		text = $this.parent().text();
	} else {
		text = $this.text();
	}

	if (isMutCheck) {
		// 多选 add by fang 20150916
		// 增加条件到表单条件
		var beforeid = $("#searchForm").find("input[name='" + type + "']")
				.attr("value");
		var isNull = $.trim(beforeid) == "";
		if ($this.closest("li").hasClass("on")) {
			$this.closest("li").removeClass("on");
			var values = beforeid.split(",");
			var delIndex = 0;
			for ( var index in values) {
				if (values[index] == id) {
					delIndex = index;
				}
			}
			values.splice(delIndex, 1);
			$("#searchForm").find("input[name='" + type + "']").attr("value",
					values.join(","));
		} else {
			$("#searchForm").find("input[name='" + type + "']").attr("value",
					beforeid + (isNull ? "" : ",") + id);
			$this.closest("li").addClass("on");
		}
	} else {
		// 单选
		// 表单条件增添20150908
		
		/*//加载时，如果有查询条件的话，将条件添加到searchForm中，进而返回到后台（该问题是，修改分页方式，而产生的bug，进而用此方法解决）
		if('${isNew}' != null && '${isNew}' != ''){
			$("#searchForm").find("input[name='isNew']").attr("value", "${isNew}");
		}
		
		if('${popuOrder}' != null && '${popuOrder}' != ''){
			$("#searchForm").find("input[name='popuOrder']").attr("value", "${popuOrder}");
		}
		
		if('${priceOrder}' != null && '${priceOrder}' != ''){
			$("#searchForm").find("input[name='priceOrder']").attr("value", "${priceOrder}");
		}
		
		if('${isSelf}' != null && '${isSelf}' != ''){
			$("#searchForm").find("input[name='isSelf']").attr("value", "${isSelf}");
		}*/

		
		$("#searchForm").find("input[name='" + type + "']").attr("value", id);
		$("#seletedCons").show();
		if (!isCooOrder) {
			$this.closest(".listtypelid").find("li").removeClass("on");
			$this.closest("li").addClass("on");
		}
		$(".whered .wlid").each(function() {
			if ($(this).attr("s_type") == type) {
				isTypeExist = true;
				if ($(this).attr("s_id") == id) {
					isExist = true;
				}
				$(this).remove();
			}
		});

		// 显示已选条件
		whereItem = "<div class='wlid' s_type='"
				+ type
				+ "' s_id='"
				+ id
				+ "' ><p class=te>"
				+ text
				+ "</p><p class='ico'><a href='javascript:;' onclick='remCondition(this,"
				+ isCooOrder + ");'></a></p></div>";
		$(".whered").append(whereItem);
		
		// 提交表单数据
		submitPost();
	}

}
// 删除条件
function remCondition(obj, isCooOrder) {
	var $this = $(obj), type = $this.closest(".wlid").attr("s_type"), id = $this
			.closest(".wlid").attr("s_id");
	$this.closest(".wlid").remove();

	// 当没有显示条件时,隐藏'已选条件'字样 add by fang 20150917
	if ($(".whered").find(".wlid").size() == 0) {
		$("#seletedCons").hide();
	}

	// 重置条件样式
	if (isCooOrder) {
		$(".prosortd").find("input[name='" + type + "']").each(function() {
			$(this).attr("checked", false);
		});
	} else {
		$("#" + type).find("a[s_val='" + id + "']").parent().removeClass("on");
	}
	// 重置表单条件数据

	if (isMutiple($("#" + type))) {
		// 多选情况下,展开被隐藏的条件
		$("#" + type).slideDown(200);
	} else {
		$("#searchForm").find("input[name='" + type + "']").attr("value", "");
	}

	// 去掉所在的子版块
	if (type == 'indClass') {
		$("#indClass_children_children").find("li").remove();
		$("#indClass_children").find("li").remove();
	}
	if (type == 'proClass') {
		$("#proClass_children_children").find("li").remove();
		$("#proClass_children").find("li").remove();
	}
	if (type = "brands") {
		$("#brandArea_children").find("li").remove();
	}
	submitPost();
}
function productDataJson(_this){
	$(".js-keywordBtn a").removeClass("on");
	var type = $(_this).data("type");
		$(".js-keywordBtn a").each(function() {
			if ($(this).data("type") == type) {
				$(this).addClass("on");
			}
		})
	isProductSearch = ("product" == type);
	var keyword = $(".js-keyword").find("input").val();
	$("#searchForm").find("input[name='keyword']").attr("value", $.trim(keyword));
		submitPost();
}

function deviceDataJson(_this){
	$(".js-keywordBtn a").removeClass("on");
		var type = $(_this).data("type");
		$(".js-keywordBtn a").each(function() {
			if ($(this).data("type") == type) {
				$(this).addClass("on");
			}
		})
	isProductSearch = ("product" == type);
	var keyword = $(".js-keyword").find("input").val();
	$("#searchForm").find("input[name='keyword']").attr("value", $.trim(keyword));
		submitPost();
}



// 提交表单
function submitPost() {
	$(".prould").html("");
	$(".pagd").html("");
	$(".prolistpd .listloadingd").show();
	$.ajax({
		url : "/product/dataJson",
		data : $("#searchForm").serialize() + "&isProductSearch="
				+ isProductSearch,
		datatype : "html",
		type : "post",
		success : function(data) {
			$(".pageList").html(data);
			$(".prolistpd .listloadingd").hide();
		}
	});
}

// 提交表单
function submitPost2(_isProductSearch) {
	$(".prould").html("");
	$(".pagd").html("");
	$(".prolistpd .listloadingd").show();
	$.ajax({
		url : "/product/dataJson",
		data : $("#searchForm").serialize() + "&isProductSearch="
				+ _isProductSearch,
		datatype : "html",
		type : "post",
		success : function(data) {
			$(".pageList").html(data);
			$(".prolistpd .listloadingd").hide();
		}
	});
}

