
	function httpPost(username,password) {
		//动态生成form表单
		var temp = document.createElement("form");
		temp.action = "http://192.168.31.148:8080/subscribeInfo/toList.jspx";
		temp.method = "post";
		temp.style.display = "none";
		
		var opt = document.createElement("input");
		opt.name = 'username';
		opt.value = username;
		temp.appendChild(opt);
		var p = document.createElement("input");
		p.name = 'password';
		p.value = password;
		temp.appendChild(p);
		
		document.body.appendChild(temp);
		temp.submit();
	}
