var rsaKey = new RSAKey();
rsaKey.setPublic(b64tohex(modulus), b64tohex(exponent));
var $username = $("#username"), $password = $("#password"), $captcha = $("#valid_code");
			
var Loginer={
		init:function(){
			var self = this;
			self.initValidCode();
			if($("#username").val()){$("#psdinput").focus();}
			$("#username").parent().parent().addClass("curr");
			$("#login_btn").click(function(){
				self.login();
			});
			$("#login_btn").attr("disabled",false).val("确定"); 
			
			$(document).keydown(function(event){
				if(event.keyCode==13){
					self.login();
				}
				
			});
			self.infoText("#username", ".username_img");
			self.infoText("#password", ".password_img");
			self.infoText("#valid_code", ".validcode_img");
			$(".inputstyle").focus(function(){
				$(this).parent().parent().find(".text").hide();
				$(this).parent().parent().addClass("curr");
			});
			$(".inputstyle").blur(function(){
				$(this).parent().parent().removeClass("curr");
				if ( $(this).val()==''){
					$(this).parent().parent().find(".text").show();
				}
			});
			$(".inputstyle").keydown(function(){
				$(this).parent().parent().find(".text").hide();
			});

		},
		login:function(){
			$("#login_btn").attr("disabled",true).val("正在登陆..."); 
			$.ajax({
				url: "/admin/login.jspx",
				data: {
					username: $username.val(),
					password: hex2b64(rsaKey.encrypt($password.val())),
					//password: $password.val()//对password不进行rsa加密，传到后台加密成md5Hex，直接与数据库密码进行比对
					captchaId: captchaId,
					captcha: $captcha.val()
				},
				type: "post",
				dataType: "json",
				cache: false,
				beforeSend: function(request, settings) {
					request.setRequestHeader("token", $.cookie("token"));
					
				},
				success: function(result) {
				 
					if(result.type=="success"){
						var referer=$("#referer").val();
						var url = "/admin/home/index.jspx";
						if(referer!=""){
							url=referer;
						}
						location.href=url;
					}else{
						$("#login_btn").attr("disabled",false).val("确定"); 
						 alert(result.cont);
						 location.href="/admin/login.jspx";
					}

				},
				error: function() {
					$("#login_btn").attr("disabled",false).val("确定"); 
					alert("出现错误 ，请重试");
					location.href="/admin/login.jspx";
				}
			});
			return;

		},
		infoText:function(n,m){

			if($(n).val()==''){
				$(m).next(".text").show();
			}else{
				$(m).next(".text").hide();
			};
			
		},
		initValidCode:function(){
		 
			$("#username").focus();
		    var that =this;
			$("#code_img").attr("src","/captcha?vtype=admin&captchaId=" + captchaId )
			.click(function(){
				$(this).attr("src","/captcha?vtype=admin&captchaId="+ captchaId +"&date="+new Date());
			});		
		}
}