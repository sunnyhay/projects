<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript" src="../jslib/provinces.js"></script>
<script type="text/javascript" src="../jslib/resorts.js"></script>
<script type="text/javascript">
	var arr = new Array();  //hold all the selected resorts or cities;
	$(function() {
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
	<form name="mail" action="add" method="post">
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
			<input type="submit" value="submit"	id="btn-submit" />
		</div>
	
		<!-- 邮件地址：<input type="text" name="subAdr"> <br> 
		选择城市 <input type="checkbox" name="subCheck" value="北京" checked="checked">
		<input type="checkbox" name="subCheck" value="泰山"> 
		<input type="checkbox" name="subCheck" value="兵马俑"> 
		<input type="checkbox" name="subCheck" value="海口" checked="checked">
		<input type="checkbox" name="subCheck" value="凤凰"> <br> 
		<input type="button" value="submit"	onclick="javascript:document.mail.action='add';alert(arr);document.mail.submit();" /> -->
	</form>
</body>
</html>