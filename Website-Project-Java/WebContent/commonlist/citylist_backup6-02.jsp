<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		//1. when the window is loaded, show the comments from city_comment;
		$.post("commonListAction!getCityComments", {}, function(returnedData,status) {
			if ("success" == status) {
				var result = eval(returnedData);
				for ( var index in result) {
					var c = result[index];
					$("#showComment").prepend(c.id + ", " + c.nickname + ", " + c.comment+ ", " + c.commentDate + "<br>");
				}
			}
		});
		//2. register click event on #btn to add comment on city_comment
		$("#btn").click(function() {
			if($("#comment").val() == ""){//simple validation with empty condition
				$("#commentSpan").html("<p><b>评论不能为空</b></p>");
				return false;
			}else{//submit the json object {nickname,comment}
				$.post("commonListAction!addCityComment", {"nickname":encodeURI($("#nickname").val()),"comment":encodeURI($("#comment").val())}, 
					function(returnedData,status) {
						$("#showComment").prepend($("#comment").val() + "<br/>");  //insert the new comment firstly;
						var result = eval(returnedData);
						alert(result); 
					})
			}
			
		});
		
	});	
</script>
<title>国内城市排行榜</title>
</head>
<body>

	榜单： 1. 北京 2. 西安 3. 成都 4. 海口 5. 沈阳 6. 凤凰 7. 昆明 8. 大理 9. 乌鲁木齐 10. 广州

	<br> 昵称(可选):
	<input type="text" id="nickname" name="nickname">
	<br /> 评论:
	<textarea rows="10" cols="30" id="comment" name="comment"></textarea><span id="commentSpan"></span>
	<br />
	<input id="btn" type="button" value="提交您的评论" />
	<br />
	<br />
	<br />
	<div id="showComment"></div>

	<%-- <s:debug></s:debug> --%>


</body>
</html>