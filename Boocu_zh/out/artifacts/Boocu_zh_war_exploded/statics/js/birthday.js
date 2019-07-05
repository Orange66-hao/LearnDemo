$.fn.birthday = function(){
	       	        
	        var defalt_birthday = $(this).val().split("/") || [];
	       
	        var birthday = {
		            y:defalt_birthday[0],
		            m:defalt_birthday[1],
		            d:defalt_birthday[2]
		        };
	        
	        var container_class = ".birthday_container";
	        
	        $(this).attr({type:'hidden'});

	        $(this).after($(document.createElement("div")).attr({class:container_class.replace(".","")}));
	       
	        var container = $(this).parent().find(container_class);

	        var that = $(this);

	        for(var i = 0 ; i< 3; i++){
	            var ele = document.createElement("select");
	            switch (i){
	                case 0 :
	                    $(ele).html(getYear());
	                    break;
	                case 1 :
	                    $(ele).html(getMonth());
	                    break;
	                case 2 :
	                    $(ele).html(getDay());
	                    break;
	            }
	            container.append(ele);
	        }


	        container.find("select").bind("change",function(){
	        	that.val("");
	            var next = $(this).nextAll();
	            switch (next.length ){
	                case  1 :
	                	
	                    birthday.m = $(this).val();
	                    birthday.d = 0;
	                    $(this).next().html(getDay());	                    
	                    break;
	                case 2 :
	                    birthday.y = $(this).val();
	                    birthday.d = 0;
	                    birthday.m = 0;	                    
	                    $(this).next().html(getMonth());
	                    $(this).next().next().html(getDay());
	                    
	                    break;
	                default :birthday.d = $(this).val();
	                setBirthDay();
	            }

	        });

	        function setBirthDay(){
	            var birthDay = [];
	            $.each(birthday,function(i,v){
	                birthDay.push(v);
	            });
				that.val(birthDay.join('/'));
	        }

	        function setDefaltVal(){
	            return "<option value=''>--请选择--</option>";
	        }

	        function getYear(){

	            var y = new Date().getFullYear();
			    var str = setDefaltVal();
	            for (var i = (y - 60); i < y ; i++)
	            {
	                if( i == birthday.y){
	                    str += "<option value='" + i + "' selected='selected'> " + i +  "</option>\r\n";
	                }else{
	                    str += "<option value='" + i + "'> " + i  + "</option>\r\n";
	                }

	            }
	            return str;
	        }

	        function getMonth(){
	            var str = setDefaltVal();
	            for (var i = 1; i <= 12; i++)
	            {
	                if( i == parseInt(birthday.m)){
	                    str += "<option value='" + i + "' selected='selected'> " + i +  "</option>\r\n";
	                }else {
	                    str += "<option value='" + i + "'> " + i +  "</option>\r\n";
	                }
	            }
	            return str;
	        }
	        function getLastDay(){

	            var year = birthday.y;
	            var month = birthday.m;
	            var dateTime = year+"/"+(parseInt(month)+1)+"/1";
	            var cdt = new Date( new Date(dateTime).getTime()-1000*60*60*24);
	            return cdt.getDate();
	        }
	        function getDay(){
	            var str = setDefaltVal();
	            if(!birthday.m || !birthday.y)
	                return str;

	            var days = getLastDay();

	            for (var i = 1; i <= days; i++)
	            {
	                if( i == birthday.d){
	                    str += "<option value='" + i + "' selected='selected'> " + i  + "</option>\r\n";
	                }else {
	                    str += "<option value='" + i + "'> " + i  + "</option>\r\n";
	                }
	            }
	            return str;
	        }
	    }