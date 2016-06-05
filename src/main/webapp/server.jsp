<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	String contentPath = request.getContextPath();
%>
<html>
<head>
	<title>WebSocket 聊天室</title>
	<!-- 引入CSS文件 -->
	<link rel="stylesheet" type="text/css" href="ext4/resources/css/ext-all.css">
	<link rel="stylesheet" type="text/css" href="ext4/shared/example.css" />
	<link rel="stylesheet" type="text/css" href="css/websocket.css" />
	
	<!-- 映入Ext的JS开发包，及自己实现的webscoket. -->
	<script type="text/javascript" src="ext4/ext-all-debug.js"></script>
<script> 
/*! HTML5 Shiv vpre3.6 | @afarkas @jdalton @jon_neal @rem | MIT/GPL2 Licensed 
Uncompressed source: https://github.com/aFarkas/html5shiv */
var contentPath ='<%=contentPath%>';
(function (a, b) { 
function h(a, b) { 
var c = a.createElement("p"), 
d = a.getElementsByTagName("head")[0] || a.documentElement; 
return c.innerHTML = "x<style>" + b + "</style>", 
d.insertBefore(c.lastChild, d.firstChild) 
} 
function i() { 
var a = l.elements; 
return typeof a == "string" ? a.split(" ") : a 
} 
function j(a) { 
var b = {}, 
c = a.createElement, 
f = a.createDocumentFragment, 
g = f(); 
a.createElement = function (a) { 
if (!l.shivMethods) 
return c(a); 
var f; 
return b[a] ? f = b[a].cloneNode() : e.test(a) ? f = (b[a] = c(a)).cloneNode() : f = c(a), 
f.canHaveChildren && !d.test(a) ? g.appendChild(f) : f 
}, 
a.createDocumentFragment = Function("h,f", "return function(){var n=f.cloneNode(),c=n.createElement;h.shivMethods&&(" + i().join().replace(/\w+/g, function (a) { 
return c(a), 
g.createElement(a), 
'c("' + a + '")'
}) + ");return n}")(l, g) 
} 
function k(a) { 
var b; 
return a.documentShived ? a : (l.shivCSS && !f && (b = !!h(a, "article,aside,details,figcaption,figure,footer,header,hgroup,nav,section{display:block}audio{display:none}canvas,video{display:inline-block;*display:inline;*zoom:1}[hidden]{display:none}audio[controls]{display:inline-block;*display:inline;*zoom:1}mark{background:#FF0;color:#000}")), g || (b = !j(a)), b && (a.documentShived = b), a) 
} 
var c = a.html5 || {}, 
d = /^<|^(?:button|form|map|select|textarea|object|iframe|option|optgroup)$/i, 
e = /^<|^(?:a|b|button|code|div|fieldset|form|h1|h2|h3|h4|h5|h6|i|iframe|img|input|label|li|link|ol|option|p|param|q|script|select|span|strong|style|table|tbody|td|textarea|tfoot|th|thead|tr|ul)$/i, 
f, 
g; 
(function () { 
var c = b.createElement("a"); 
c.innerHTML = "<xyz></xyz>", 
f = "hidden"in c, 
f && typeof injectElementWithStyles == "function" && injectElementWithStyles("#modernizr{}", function (b) { 
b.hidden = !0, 
f = (a.getComputedStyle ? getComputedStyle(b, null) : b.currentStyle).display == "none"
}), 
g = c.childNodes.length == 1 || function () { 
try { 
b.createElement("a") 
} catch (a) { 
return !0
} 
var c = b.createDocumentFragment(); 
return typeof c.cloneNode == "undefined" || typeof c.createDocumentFragment == "undefined" || typeof c.createElement == "undefined"
} 
() 
})(); 
var l = { 
elements : c.elements || "abbr article aside audio bdi canvas data datalist details figcaption figure footer header hgroup mark meter nav output progress section summary time video", 
shivCSS : c.shivCSS !== !1, 
shivMethods : c.shivMethods !== !1, 
type : "default", 
shivDocument : k 
}; 
a.html5 = l, 
k(b) 
})(this, document) 
</script> 
   <script type="text/javascript" src="sockjs-0.3.min.js"></script>
	<script type="text/javascript" src="server.js"></script>
	<script type="text/javascript">
		var user = "客服100";
		var userId = "100";
	</script>
</head>
<body>
	<h1>WebSocket聊天室</h1>
	<p>通过HTML5标准提供的API与Ext富客户端框架相结合起来，实现聊天室，有以下特点：</p>
	<ul class="feature-list" style="padding-left: 10px;">
		<li>实时获取数据，由服务器推送，实现即时通讯</li>
		<li>利用WebSocket完成数据通讯，区别于轮询，长连接等技术，节省服务器资源</li>
		<li>结合Ext进行页面展示</li>
		<li>用户上线下线通知</li>
	</ul>
	<div id="websocket_button"></div>
</body>
</html>
