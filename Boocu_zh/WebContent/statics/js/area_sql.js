/*
*	全国三级城市联动sql版
*/
function addOption(id,parentname){
	$.ajax({
		url: "/area/findChildrenByName.jspx",
		data: {
			parentName:parentname
		},
		async:false,
		type: "post",
		cache: false,
		beforeSend: function(request, settings) {
		},
		success: function(result) {
			var sel= document.getElementById(id); 
			var data=JSON.parse(result); 
			if(data.length>0){
				for(i=0;i<data.length;i++){
					sel.options.add(new Option(data[i].name,data[i].name));
				}
			}
		},
		error: function() {
		}
	});
}
/**
 * 全国地区三级联动sql版，初始化。
 * 需初始化个默认的option，如下。
 * <select id="province" name="province"><option>省</option></select>
	<select id="city" name="city"><option>市</option></select>
	<select id="county" name="county"><option>区</option></select>
 * 
 * @param provinceid
 * @param cityid
 * @param countyid
 */
function initAreaSelect(provinceid,cityid,countyid){
	addOption(provinceid,"");
	$("#"+provinceid).change(function(){
		document.getElementById(cityid).options.length=1; 
		document.getElementById(countyid).options.length=1; 
		if($(this).val()!=null&&$(this).val()!=""){
			addOption(cityid,$(this).val());
		}
	});
	$("#"+cityid).change(function(){
		document.getElementById(countyid).options.length=1; 
		if($(this).val()!=null&&$(this).val()!=""){
			addOption(countyid,$(this).val());
		}
	});
}
