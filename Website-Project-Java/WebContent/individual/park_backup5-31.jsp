<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="../jslib/jquery-1.7.2.js"></script>
<script type="text/javascript" src="../jslib/jquery.jqplot.min.js"></script>
<script type="text/javascript" src="../jslib/jqplot.barRenderer.min.js"></script>
<script type="text/javascript" src="../jslib/jqplot.categoryAxisRenderer.min.js"></script>
<script type="text/javascript" src="../jslib/jqplot.pointLabels.min.js"></script>
<link rel="stylesheet" type="text/css" href="../style/jquery.jqplot.css" />

<title>公园对比排行榜</title>
<script type="text/javascript">
$(function() {
	//show the hint if the support express is voted!
	if($(":input[type=radio]").attr("disabled") =="disabled"){
		$("#info1").html("您已经表达过您的支持了");		
	}
	
	/*
	 * when the window is loaded, show the comments for park list
	 */
	$.post("parkListAction!getParkComments", {}, function(returnedData,status) {
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
			$("#commentSpan").html("<p>谢谢您的评论，显示如下</p>");
			$.post("parkListAction!addParkComment", {"nickname":encodeURI($("#nickname").val()),"comment":encodeURI($("#comment").val())}, 
				function(returnedData,status) {
					if ("success" == status)
						$("#showComment").prepend($("#comment").val() + "<br/>");  //insert the new comment firstly;					
				});
		}			
	});
	
	/*
	 * bind the click event to each radio box to vote the express support on this park list
	 */
	$(":input[type=radio]").click(function() {
		//1. if any radio box is selected, disable all;
		$(":input[type=radio]").attr("disabled","disabled"); 
		//2.show chart1 div
		$("#info1").html("");
		$("#chart1").show(1500);
		$("#chart1").html("请稍候...");
		//3.pass the user choice to action via ajax post() method
		$.post("parkListAction!express", {support:$(this).val()}, function(returnedData,status) {
			if ("success" == status) {
				$("#chart1").html("");
				var result = eval(returnedData);
				temp = result;
				//4. define plot element to be shown
				$.jqplot.config.enablePlugins = true;
			    var ticks = ['完全支持', '支持', '中立', '不太支持', '不支持'];
			    //5. draw the plot 
			    plot1 = $.jqplot('chart1', [result], {
			        // Only animate if we're not using excanvas (not in IE 7 or IE 8)..
			        animate: !$.jqplot.use_excanvas,
			        seriesDefaults:{
			            renderer:$.jqplot.BarRenderer,
			            pointLabels: { show: true }
			        },
			        axes: {
			            xaxis: {
			                renderer: $.jqplot.CategoryAxisRenderer,
			                ticks: ticks
			            }
			        },
			        highlighter: { show: false }
			    });
			 	//6. bind highlight method (optional)
			    $('#chart1').bind('jqplotDataHighlight',
			        function (ev, seriesIndex, pointIndex, data) {
			            $('#info1').html('series: '+seriesIndex+', point: '+pointIndex+', data: '+data);
			        }
			    );
			    //7. bind unhighlight method (optional)    
			    $('#chart1').bind('jqplotDataUnhighlight',
			        function (ev) {
			            $('#info1').html('鼠标放在图形上可显示具体投票结果');
			        }
			    );
			}
		});
	});
});

</script>
</head>
<body>
	<h2>公园对比排行榜</h2>
	<p>1.福建某公园对纽约中央公园</p>
	<p>2.张家界地址公园对黄石公园</p>
	<p>3.西安兴庆公园对芝加哥某公园</p>
	<p>4.北京天坛公园对香港黄大仙公园</p>
	<p>5.澳门赌场公园对墨西哥毒品公园</p>
	<br/>
	
	<label>支持系统</label>
	<br/>
	<input type="radio" name="support" value="park1">完全支持
	<input type="radio" name="support" value="park2">支持
	<input type="radio" name="support" value="park3">中立
	<input type="radio" name="support" value="park4">不太支持
	<input type="radio" name="support" value="park5">不支持
	<br/>
	<div id="info1"></div>
	<br/>
	<div id="chart1"  style="height:100px;width:250px;display: none;"></div>
	<br/>
	
	<label>针对公园对比排行榜的评论如下：</label>
	<br/> 昵称(可选):
	<input type="text" id="nickname" name="nickname">
	<br/> 评论:
	<textarea rows="10" cols="30" id="comment" name="comment"></textarea><span id="commentSpan"></span>
	<br/>
	<input id="btn" type="button" value="提交您的评论" />
	<br/>
	<div id="showComment"></div>
</body>
</html>