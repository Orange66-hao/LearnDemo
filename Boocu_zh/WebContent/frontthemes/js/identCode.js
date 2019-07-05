(function(){
    var randstr = function(length){
        var key = {
 
            str : [
                'a','b','c','d','e','f','g','h','i','j','k','l','m',
                'o','p','q','r','s','t','x','u','v','y','z','w','n',
                '0','1','2','3','4','5','6','7','8','9'
            ],
 
            randint : function(n,m){
                var c = m-n+1;
                var num = Math.random() * c + n;
                return  Math.floor(num);
            },
 
            randStr : function(){
                var _this = this;
                var leng = _this.str.length - 1;
                var randkey = _this.randint(0, leng);
                return _this.str[randkey];
            },
 
            create : function(len){
                var _this = this;
                var l = len || 10;
                var str = '';
 
                for(var i = 0 ; i<l ; i++){
                    str += _this.randStr();
                }
 
                return str;
            }
 
        };
 
        length = length ? length : 10;
 
        return key.create(length);
    };
 
    var randint = function(n,m){
        var c = m-n+1;
        var num = Math.random() * c + n;
        return  Math.floor(num);
    };
 
    var vCode = function(dom, options){
        this.codeDoms = [];
        this.lineDoms = [];
        this.initOptions(options);
        this.dom = dom;
        this.init();
        this.addEvent();
        this.update();
        this.mask();
    };
 
    vCode.prototype.init = function(){
        this.dom.style.position = "relative";
        this.dom.style.overflow = "hidden";
        this.dom.style.cursor = "pointer";
        this.dom.title = "点击更换验证码";
        this.dom.style.background = this.options.bgColor;
        this.w = this.dom.clientWidth;
        this.h = this.dom.clientHeight;
        this.uW = this.w / this.options.len;
    };
 
    vCode.prototype.mask = function(){
        var dom = document.createElement("div");
        dom.style.cssText = [
            "width: 100%",
            "height: 100%",
            "left: 0",
            "top: 0",
            "position: absolute",
            "cursor: pointer",
            "z-index: 9999999"
        ].join(";");
 
        dom.title = "点击更换验证码";
 
        this.dom.appendChild(dom);
    };
 
    vCode.prototype.addEvent = function(){
        var _this = this;
        _this.dom.addEventListener("click", function(){
            _this.update.call(_this);
        });
    };
 
    vCode.prototype.initOptions = function(options){
 
        var f = function(){
            this.len = 6;
            this.fontSize=18;
            this.bgColor = "#FFF";
            this.fonts = [
                "arial",
            ];
            this.colors = [
                 "#1500a5",
                 "#420b20",
                         ];
            this.lines = 30;
            this.lineColors = [
                "#b6c1bb"
            ];
 
            this.lineHeightMin = 1;
            this.lineHeightMax = 2;
            this.lineWidthMin = 1;
            this.lineWidthMax = 40;
        };
 
        this.options = new f();
 
        if(typeof options === "object"){
            for(i in options){
                this.options[i] = options[i];
            }
        }
    };
 
    vCode.prototype.update = function(){
        for(var i=0; i<this.codeDoms.length; i++){
            this.dom.removeChild(this.codeDoms[i]);
        }
        for(var i=0; i<this.lineDoms.length; i++){
            this.dom.removeChild(this.lineDoms[i]);
        }
        this.createCode();
        this.draw();
    };
 
    vCode.prototype.createCode = function(){
        this.code = randstr(this.options.len);
    };
 
    vCode.prototype.verify = function(code){
        return this.code === code;
    };
 
    vCode.prototype.draw = function(){
        this.codeDoms = [];
        for(var i=0; i<this.code.length; i++){
            this.codeDoms.push(this.drawCode(this.code[i], i));
        }
 
        this.drawLines();
    };
 
    vCode.prototype.drawCode = function(code, index){
        var dom = document.createElement("span");
 
        dom.style.cssText = [
            "font-size:" + this.options.fontSize + "px",
            "position: absolute",
            "color:" + this.options.colors[randint(0,  this.options.colors.length - 1)],
            "left:" + randint(this.uW * index, this.uW * index + this.uW - 10) + "px",
            "font-family:" +  this.options.fonts,
        ].join(";");
 
        dom.innerHTML = code;
        this.dom.appendChild(dom);
 
        return dom;
    };
 
    vCode.prototype.drawLines = function(){
        this.lineDoms = [];
        for(var i=0; i<this.options.lines; i++){
            var dom = document.createElement("div");
 
            dom.style.cssText = [
                "position: absolute",
                "opacity: " + randint(3, 8) / 10,
                "width:" + randint(this.options.lineWidthMin, this.options.lineWidthMax) + "px",
                "height:" + randint(this.options.lineHeightMin, this.options.lineHeightMax) + "px",
                "background: " + this.options.lineColors[randint(0, this.options.lineColors.length - 1)],
                "left:" + randint(0, this.w - 20) + "px",
                "top:" + randint(0, this.h) + "px",
                "transform:rotate(" + randint(-20, 20) + "deg)",
                "-ms-transform:rotate(" + randint(-20, 20) + "deg)",
                "-moz-transform:rotate(" + randint(-20, 20) + "deg)",
                "-webkit-transform:rotate(" + randint(-20, 20) + "deg)",
                "-o-transform:rotate(" + randint(-20, 20) + "deg)",
                "font-family:" + this.options.fonts[randint(0, this.options.fonts.length - 1)],
                "font-weight:" + randint(400, 900)
            ].join(";");
            this.dom.appendChild(dom);
 
            this.lineDoms.push(dom);
        }
    };
 
    this.vCode = vCode;
 
}).call(this);