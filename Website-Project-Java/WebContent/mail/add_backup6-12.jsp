<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript" src="../jslib/provinces.js"></script>
<script type="text/javascript" src="../jslib/resorts.js"></script>
<script type="text/javascript" src="../jslib/ui.core.js"></script>
<script type="text/javascript" src="../jslib/ui.sortable.js"></script>

<style type="text/css">
body {
	background-color: #2daebf;
}

form.mail {
	width: 600px;
	margin: 10px auto;
	padding: 10px;
	font-family: "Trebuchet MS";
}

form.mail fieldset {
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

form.mail legend {
	text-align: left;
	color: #fff;
	font-size: 14px;
	padding: 0px 4px 15px 4px;
	margin-left: 20px;
	font-weight: bold;
}

form.mail label {
	font-size: 18px;
	width: 200px;
	float: left;
	text-align: right;
	clear: left;
	margin: 4px 4px 0px 0px;
	padding: 0px;
	color: #FFF;
	text-shadow: 0 1px 1px rgba(0, 0, 0, 0.8);
}

form.mail input {
	font-family: "Trebuchet MS";
	font-size: 18px;
	float: left;
	width: 300px;
	border: 1px solid #cccccc;
	margin: 2px 0px 4px 2px;
	color: #00abdf;
	height: 26px;
	padding: 3px;
	-moz-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-box-shadow: 0 1px 3px rgba(0, 0, 0, 0.5);
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
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
</style>


<script type="text/javascript">
	var arr = new Array();  //hold all the selected resorts or cities;
	
	(function($) {

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

	})(jQuery);
	
	$(function() {
		$("#sortable").sortable();
		$("#sortable").disableSelection();
		$('ul').shuffle();
		
		//the function deals with the customized resort subscription
		$("#continent label").click(function() {//this level fetch the province list
			$("#btn-submit").blur();  //blur the submit button so that the validation on email is processed
			var c = $(this).attr("for");
			var pr = eval(country[c]);  //pr is the array to hold all the provinces (or countries) under country (or continent) c
			var province = $("#province");  //the div #province
				
			province.html("");  //clear div province
			$("#area").html("");  //clear div area
			for(var i = 0; i < pr.length; i++){
				//$("#province").append(pr[i]["name"] + " : " + pr[i]["value"] + "<br/>");
				province.append("<label for='" + pr[i]["name"] + "'>" + pr[i]["value"] + "</label><br/>");
			}
			//after the dynamic adding to div #province, the new labels can be activeted with click events.
			$("#province label").click(function() {//this level fetch resort list
				$("#btn-submit").blur();  //blur the submit button so that the validation on email is processed
				$("#submit").show(1000);  //show the submit button after this click
				var pc = $(this).attr("for");
				var re = eval(resorts[pc]);
				var area = $("#area");  //the div #area
				//alert(re);
				area.html("");  //clear div area
				
				var cityAmount = re[0]["cityNum"];
				//alert(parseInt(cityAmount));
				var amount = parseInt(cityAmount);
				//alert(++amount);
								
				area.append("<label>城市：</label><br/>");  //begin to append cities
				for(var k = 1; k <= amount; k++){
					area.append("<label for='" + re[k]["name"] + "'>" + re[k]["value"] + "</label>");
					if(jQuery.inArray(re[k]["value"],arr) > -1)
						area.append("<input type='checkbox' name='subCheck' value='" + encodeURI(re[k]["value"]) + "' checked='checked'/><br/>");
					else
						area.append("<input type='checkbox' name='subCheck' value='" + encodeURI(re[k]["value"]) + "' /><br/>");
				}
				area.append("<br/><label>景区：</label><br/>");  //begin to append resorts
				for(var j = ++amount; j < re.length; j++){
					area.append("<label for='" + re[j]["name"] + "'>" + re[j]["value"] + "</label>");
					if(jQuery.inArray(re[j]["value"],arr) > -1)
						area.append("<input type='checkbox' name='subCheck' value='" + encodeURI(re[j]["value"]) + "' checked='checked'/><br/>");
					else
						area.append("<input type='checkbox' name='subCheck' value='" + encodeURI(re[j]["value"]) + "' /><br/>");
				}
				$("#area :checkbox").click(function() {//choose checkboxes and add choices in array 'arr' for later submission
					var rem = decodeURI($(this).val());
					if($(this).attr("checked") == "checked"){
						arr.push(rem);
						$("#choice").append("<label for='" + rem + "'>" + rem + " " + "</label>");
					}else{
						arr = jQuery.grep(arr, function(value) {
							return value != rem;
							});
						$("#choice label").remove(":contains('" + rem + "')");  //if unchecked remove the original content
					}						
				});
			});
		});
		//the function deals with validation on email before submission
		$("#btn-submit").click(function() {
			$('#hidden').val(encodeURI(arr));
			$(".error").hide();
			var hasError = false;
			var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
			
			var emailAdrVal = $("#subAdr").val();
			if(emailAdrVal == ''){
				$("#subAdr").after('<span class="error">您的邮件不能为空，请重新输入！</span>');
				$("#subAdr").focus();
				hasError = true;
			}
			else if(!emailReg.test(emailAdrVal)){
				$("#subAdr").after('<span class="error">这不是一个有效的邮件地址，请重新输入！</span>');
				$("#subAdr").focus();
				hasError = true;
			}			
			
			if(hasError == true)
				return false;
			if($("ul").validate()){
				alert("您通过了考验");
				return true;
			}else{
				alert("您不是人类");
				return false;
			}
		});
		//when the email input loses focus, validate it firstly before submission
		$("#btn-submit").blur(function() {
			$(".error").hide();
			var emailReg = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/;
			var emailAdrVal = $("#subAdr").val();
			if(emailAdrVal == ''){
				$("#subAdr").after('<span class="error">您的邮件不能为空，请重新输入！</span>');
				$("#subAdr").focus();
			}
			else if(!emailReg.test(emailAdrVal)){
				$("#subAdr").after('<span class="error">这不是一个有效的邮件地址，请重新输入！</span>');
				$("#subAdr").focus();
			}	
		}); 
	});
</script>
<title>邮件定制</title>
</head>
<body>
	<form name="mail" action="mailAddAction!add" method="post">
		邮件地址：<input type="text" name="subAdr" id="subAdr" value="" size="32" /> <br>
		<div id="continent">
			<label for="china">中国</label>
			<label for="asia">亚洲</label>
			<label for="america">美洲</label>
		</div><br/>
		<div id="province"></div><br/>		
		<div id="area"></div><br/>
		<div id="choice"></div><br/>
		<div id="submit" style="display: none;">
			<input id="hidden" name="subhidden" type="hidden" value="" />
			<fieldset>
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
			</fieldset>
			<input type="submit" value="submit"	id="btn-submit" />
		</div>		
	</form>
</body>
</html>