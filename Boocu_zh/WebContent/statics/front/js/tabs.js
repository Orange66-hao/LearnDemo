/**
 * Created by zxm on 2017/5/24.
 */
var tabs_takes={
    "init":function(containId){
        if(containId==null||containId==""){
            alert("id不能为空");
            return;
        }
        $("#"+containId+">ul>li").on("click",function(){
            tabs_takes.tabItemTakes(containId,this)
            var type=1;
            if($(".selectActive")[0].textContent=="销售订阅"){
                type=1;
                currentIndex=1
            	$("#sale").show()
            }else{
            	$("#sale").hide()
            }
            if($(".selectActive").html()=="求购订阅"){
                type=2;
                currentIndex=2
            	$("#buy").show()
            }else{
            	$("#buy").hide()
            }
            if($(".selectActive").html()=="租赁订阅"){
                type=3;
                currentIndex=3
            	$("#rent").show()
            }else{
            	$("#rent").hide()
            }
            if($(".selectActive").html()=="求租订阅"){
                type=4;
                currentIndex=4
            	$("#needRent").show()
            }else{
            	$("#needRent").hide()
            }
            if($(".selectActive").html()=="维修订阅"){
                type=5;
                currentIndex=5
            	$("#repair").show()
            }else{
            	$("#repair").hide()
            }
            if($(".selectActive").html()=="求修订阅"){
                type=6;
                currentIndex=6
            	$("#needRepair").show()
            }else{
            	$("#needRepair").hide()
            }
            if($(".selectActive").html()=="测试方案"){
                type=7;
                currentIndex=7
            	$("#autoTest").show()
            }else{
            	$("#autoTest").hide()
            }
            if($(".selectActive").html()=="方案需求"){
                type=8;
                currentIndex=8
            	$("#projectNeed").show()
            }else{
            	$("#projectNeed").hide()
            }
           
            if($(".selectActive").html()=="招标订阅"){
                currentIndex=10
                type=10;
            	$("#invite").show()
            }else{
            	$("#invite").hide()
            }
            if($(".selectActive").html()=="产品测试"){
                type=11;
                currentIndex=11
            	$("#productTest").show()
            }else{
            	$("#productTest").hide()
            }
            if($(".selectActive").html()=="计量校准"){
                type=13;
                currentIndex=13
            	$("#calibration").show()
            }else{
            	$("#calibration").hide()
            }
            if($(".selectActive").html()=="测试需求"){
                type=12;
                currentIndex=12
            	$("#requireTest").show()
            }else{
            	$("#requireTest").hide()
            }

            $.ajax({
                type:"post",
                data:{"type":type},
                url:"/subscribeInfo/getSubscribrStatus.jspx",
                success:function (data) {
                    if(data.isSubscribe){
                        $("#hasSubscribe").show("slow")
                        $("#noSubscribe").hide("slow")
                        $("#setting1").show("slow");
                        //邮箱频率
                        $.each($("input[type='radio'][name='radPl2']"),function (index,ele) {
                           if(ele.value==data.emailRate.charAt(0)){
                               ele.checked=true;
                               if(ele.value==2){//每周
                                   //初始化为没选中
                                   $.each($("#weekCheckBoxList input[type='checkbox']"),function (index,ele) {
                                       ele.checked=false;
                                   })
                                   $("#weekCheckBoxList").show("slow");
                                   $("#monthCheckBoxList").hide("slow");
                                   var emailRate = data.emailRate.substring(2,data.emailRate.length);
                                   var split = emailRate.split(",");
                                   for(var j=0;j<split.length;j++){
                                     $("#weekCheckBoxList input[value="+split[j]+"]").attr('checked',true);
                                   }
                               } else if(ele.value==3){//每月
                                   //初始化为没选中
                                   $.each($("#monthCheckBoxList input[type='checkbox']"),function (index,ele) {
                                       ele.checked=false;
                                   })
                                   $("#weekCheckBoxList").hide("slow");
                                   $("#monthCheckBoxList").show("slow");
                                   var emailRate = data.emailRate.substring(2,data.emailRate.length);
                                   var split = emailRate.split(",");
                                   for(var j=0;j<split.length;j++){
                                       $("#monthCheckBoxList input[value="+split[j]+"]").attr('checked',true);
                                   }
                               }else{
                                   $("#weekCheckBoxList").hide("slow");
                                   $("#monthCheckBoxList").hide("slow");
                               }
                           }
                        })
                        //手机频率
                        $.each($("input[type='radio'][name='radPl3']"),function (index,ele) {
                            if(ele.value==data.mobileRate.charAt(0)){
                                ele.checked=true;
                                if(ele.value==2){
                                    $("#weekCheckBoxList1").show();
                                    $("#monthCheckBoxList1").hide();
                                    var mobileRate = data.mobileRate.substring(2,data.mobileRate.length);
                                    var split = mobileRate.split(",");
                                    for(var j=0;j<split.length;j++){
                                        $("#weekCheckBoxList1 input[value="+split[j]+"]").attr('checked',true);
                                    }
                                } else if(ele.value==3){
                                    $("#weekCheckBoxList1").hide();
                                    $("#monthCheckBoxList1").show();
                                    var mobileRate = data.mobileRate.substring(2,data.mobileRate.length);
                                    var split = mobileRate.split(",");
                                    for(var j=0;j<split.length;j++){
                                        $("#monthCheckBoxList1 input[value="+split[j]+"]").attr('checked',true);
                                    }
                                }else{
                                    $("#weekCheckBoxList1").hide();
                                    $("#monthCheckBoxList1").hide();
                                }
                            }
                        })
                        //初始化为没选中
                        $.each($("#subscribeCondition input[type='checkbox']"),function (index,ele) {
                            ele.checked=false;
                        })
                       for(var i=0;i< data.subscribeTerm.length;i++){
                           var s = data.subscribeTerm.charAt(i);
                           switch(parseInt(s))
                           {
                               case 1:
                                   $('input[name="isNew"]').attr("checked",true)
                                   break;
                               case 2:
                                   $('input[name="isSecondHand"]').attr("checked",true)
                                   break;
                               case 3:
                                   $('input[name="isImport"]').attr("checked",true)
                                   break;
                               case 4:
                                   $ ('input[name="isChinese"]').attr("checked",true)
                                   break;
                               default:
                                  return;
                           }
                       }
                    }else{
                        $("#hasSubscribe").hide();
                        $("#noSubscribe").show();
                        $("#setting1").hide();
                    }
                }
            })
        });
        var liActiveNumber =  $("#"+containId+" ul li.selectActive").length;
        if(liActiveNumber>0){
            var liRel = $("#"+containId+">ul>li.selectActive").eq(0).attr("rel");
            $("#"+containId+">div").css("display","none");
            $("#"+containId+">div[rel='"+liRel+"']").css("display","block");
            var tabHrefRel = $("#"+containId+">ul>li.selectActive").eq(0).attr("relHref");
            if(tabHrefRel!=null&&tabHrefRel!=""){
                $("#"+containId+">div[rel='"+liRel+"']").load(tabHrefRel);
            }
        }else{
            var liRel = $("#"+containId+">ul>li").eq(0).attr("rel");
            $("#"+containId+">ul>li").eq(0).addClass("selectActive");
            $("#"+containId+">div").eq(0).css("display","block");
            var tabHrefRel = $("#"+containId+">ul>li").eq(0).attr("relHref");
            if(tabHrefRel!=null&&tabHrefRel!=""){
                $("#"+containId+">div[rel='"+liRel+"']").load(tabHrefRel);
            }
        }
        
    },
    "tabItemTakes":function(containId,thisObj){
        var tabRel = $(thisObj).attr("rel");
        $("#"+containId+">ul>li").removeClass("selectActive");
        $(thisObj).addClass("selectActive");
        $("#"+containId+">div").css("display","none");
        $("#"+containId+">div[rel='"+tabRel+"']").css("display","block");
        var tabHrefRel = $(thisObj).attr("relHref");
        if(tabHrefRel!=null&&tabHrefRel!=""){
            $("#"+containId+">div[rel='"+tabRel+"']").load(tabHrefRel);
        }
    }
}