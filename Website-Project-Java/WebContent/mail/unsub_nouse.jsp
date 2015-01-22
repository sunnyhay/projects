<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript" src="../jslib/ui.core.js"></script>
<script type="text/javascript" src="../jslib/ui.sortable.js"></script>

<!-- <style type="text/css">
fieldset {
	background-color: #707070;
	border: none;
	padding: 10px;
	-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-moz-border-radius: 15px;
	-webkit-border-radius: 15px;
	padding: 6px;
	margin: 0px 30px 0px 0px;
}

legend {
	text-align: left;
	color: #fff;
	font-size: 14px;
	padding: 0px 4px 15px 4px;
	margin-left: 20px;
	font-weight: bold;
}

.button,.button:visited {
	float: right;
	background: #2daebf url(../images/overlay.png) repeat-x;
	font-weight: bold;
	display: inline-block;
	padding: 5px 10px 6px;
	color: #fff;
	text-decoration: none;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	text-shadow: 0 -1px 1px rgba(0, 0, 0, 0.25);
	border-bottom: 1px solid rgba(0, 0, 0, 0.25);
	cursor: pointer;
	margin-top: 95px;
	margin-right: 15px;
}

.button:hover {
	background-color: #007d9a;
}

#sortable {
	list-style-type: none;
	margin: 5px 0px 0px 16px;
	padding: 0;
}

#sortable li {
	margin: 3px 3px 3px 0;
	padding: 1px;
	float: left;
	width: 35px;
	height: 35px;
	font-size: 20px;
	text-align: center;
	line-height: 35px;
	cursor: pointer;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	-moz-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 1px rgba(0, 0, 0, 0.5);
	text-shadow: 0 -1px 1px rgba(0, 0, 0, 0.25);
	background: #2daebf url(images/overlay.png) repeat-x scroll 50% 50%;
	color: #fff;
	font-weight: normal;
}

.captcha_wrap {
	border: 1px solid #fff;
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	float: left;
	height: 120px;
	overflow: auto;
	width: 150px;
	overflow: hidden;
	margin: 0px 0px 0px 210px;
	background-color: #fff;
}

.captcha {
	-moz-border-radius: 10px;
	-webkit-border-radius: 10px;
	font-size: 12px;
	color: #BBBBBB;
	text-align: center;
	border-bottom: 1px solid #CCC;
	background-color: #fff;
}
</style> -->

<title>取消邮件定制页面</title>
<script type="text/javascript">
/* (function($) {

	$.fn.shuffle = function() {
		return this.each(function() {
			var items = $(this).children();

			return (items.length) ? $(this).html($.shuffle(items, $(this)))
					: this;
		});
	}

	$.fn.validate = function() {
		var res = false;
		this
				.each(function() {
					var arr = $(this).children();
					res = ((arr[0].innerHTML == "1")
							&& (arr[1].innerHTML == "2")
							&& (arr[2].innerHTML == "3")
							&& (arr[3].innerHTML == "4")
							&& (arr[4].innerHTML == "5") && (arr[5].innerHTML == "6"));
				});
		return res;
	}

	$.shuffle = function(arr, obj) {
		for ( var j, x, i = arr.length; i; j = parseInt(Math.random() * i), x = arr[--i], arr[i] = arr[j], arr[j] = x)
			;
		if (arr[0].innerHTML == "1")
			obj.html($.shuffle(arr, obj))
		else
			return arr;
	}

})(jQuery); */

$(function() {
	/* $("#sortable").sortable();
	$("#sortable").disableSelection();
	$('ul').shuffle(); */
	
	//register click event on #btn to add comment on hill_comment
	$("#btn").click(function() {
		//alert("ok");
		var hasError = false;
		var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
		
		var emailAdrVal = $("#unsubAdr").val();
		if(emailAdrVal == ''){
			//alert("ok");
			$(".error").hide();
			//$("#unsubAdr").val("haha");
			$("#unsubAdr").after('<span class="error">您的邮件不能为空，请重新输入！</span>');
			$("#unsubAdr").focus();
			hasError = true;
		}
		else if(!emailReg.test(emailAdrVal)){
			$(".error").hide();
			$("#unsubAdr").after('<span class="error">这不是一个有效的邮件地址，请重新输入！</span>');
			$("#unsubAdr").focus();
			hasError = true;
		}
		
		$(".error").hide();
		if(hasError == true)
			return false;
		
		/* if($("ul").validate()){
			alert("您通过了考验");
			$("#show").show(1000);
			$("#show").html("<p>提交中，请稍候</p>");
			$.post("mailUnsubAction!unsub", {"unsubAdr":encodeURI($("#unsubAdr").val())}, 
				function(returnedData,status) {
					if ("success" == status)
						$("#show").html("您的邮件定制已经成功取消，谢谢");					  					
				});
		}else{
			alert("您不是人类");
			return false;
		} */
		
		
			 				
	});
	
});
</script>
</head>
<body>
	<h2>取消邮件定制页</h2>
		
	<br/> 请输入您的邮件地址:
	<input type="text" id="unsubAdr" name="unsubAdr">
	<br/>
	<div id="show" style="height:100px;width:250px;display: none;"></div>
	<br/>
	<!-- <fieldset>
		<legend>
			请排列以下6个数字成为这么一个形状：<br />第一行从左到右 <b>1 2 3</b>。第二行从左到右为 <b>4 5 6</b>
		</legend>
		<div class="captcha_wrap">
			<div class="captcha">请用鼠标点住数字进行拖动</div>
			<ul id="sortable">
				<li class="captchaItem">1</li>
				<li class="captchaItem">2</li>
				<li class="captchaItem">3</li>
				<li class="captchaItem">4</li>
				<li class="captchaItem">5</li>
				<li class="captchaItem">6</li>
			</ul>
		</div>
	</fieldset> -->
	<br/>
	<input id="btn" type="button" value="提交" />
	<br/>	
	
</body>
</html>