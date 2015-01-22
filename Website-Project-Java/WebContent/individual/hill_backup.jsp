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

<title>名山大川投票排行榜</title>
<script type="text/javascript">
var arr = new Array();  //hold all the checked hill choices;
$(function() {
	//show the hint if the support express is voted!
	if($(":input[type=radio]").attr("disabled") =="disabled"){
		$("#info1").html("您已经表达过您的支持了");		
	}
	
	/*
	 * when the window is loaded, show the comments for hill list
	 */
	$.post("hillListAction!getHillComments", {}, function(returnedData,status) {
		if ("success" == status) {
			var result = eval(returnedData);
			for ( var index in result) {
				var c = result[index];
				$("#content").prepend("<p>" + c.id + ", " + c.nickname + ", " + c.comment+ ", " + c.commentDate + "</p>");
			}
			
			//how much items per page to show
			var show_per_page = 5; 
			//getting the amount of elements inside content div
			var number_of_items = $('#content').children().size();
			//calculate the number of pages we are going to have
			var number_of_pages = Math.ceil(number_of_items/show_per_page);
			
			//set the value of our hidden input fields
			$('#current_page').val(0);
			$('#show_per_page').val(show_per_page);
			
			//now when we got all we need for the navigation let's make it '
			
			/* 
			what are we going to have in the navigation?
				- link to previous page
				- links to specific pages
				- link to next page
			*/
			//var navigation_html = '<a class="previous_link" href="javascript:previous();">前一页</a>';
			var navigation_html = '<a class="first_link" href="javascript:go_to_page(0);">第一页</a>' + '<a class="previous_link" href="javascript:previous();">前一页</a>';
			var current_link = 0;
			while(number_of_pages > current_link){
				navigation_html += '<a class="page_link" href="javascript:go_to_page(' + current_link +')" longdesc="' + current_link +'">'+ (current_link + 1) +'</a>';
				current_link++;
			}
			//navigation_html += '<a class="next_link" href="javascript:next();">后一页</a>';
			navigation_html += '<a class="next_link" href="javascript:next();">后一页</a>' + '<a class="first_link" href="javascript:go_to_page(' + (number_of_pages-1) + ');">最后一页</a>';
			
			$('#page_navigation').html(navigation_html);
			
			//add active_page class to the first page link
			$('#page_navigation .page_link:first').addClass('active_page');
			
			//hide all the elements inside content div
			$('#content').children().css('display', 'none');
			
			//and show the first n (show_per_page) elements
			$('#content').children().slice(0, show_per_page).css('display', 'block');
		}
	});
	//register check
	$(":checkbox").click(function() {
		var rem = decodeURI($(this).val());
		if($(this).attr("checked") == "checked"){
			arr.push(rem);
			//alert(arr);
		}else{
			arr = jQuery.grep(arr, function(value) {
				return value != rem;
				});
			//alert(arr);
		}		
	});
	
	//register click event on #vote to add a vote record on hill list
	$("#vote").click(function() {
		if($(":input[type=checkbox]:checked").length > 0){
			$("#showVote").show(1500);
			$("#showVote").html("您的投票正在记录中，请稍候...");
			//alert(arr);
			$.post("hillListAction!vote", {"hillCheck":encodeURI(arr)}, 
					function(returnedData,status) {
						if ("success" == status){
							$("#showVote").html("谢谢您的投票");
							$(":checkbox").attr("disabled","disabled");
							$("#vote").attr("disabled","disabled");
							var result = eval(returnedData);
							alert(result);
						}						
					});
		}else{
			$("#showVote").show(1500);
			$("#showVote").html("<b>请选择以上至少一项</b>");
			return false;
		}		
	});
	
	//register click event on #btn to add comment on hill_comment
	$("#btn").click(function() {
		if($("#comment").val() == ""){//simple validation with empty condition
			$("#commentSpan").html("<p><b>评论不能为空</b></p>");
			return false;
		}else{//submit the json object {nickname,comment}
			$("#commentSpan").html("<p>谢谢您的评论，显示如下</p>");
			$.post("hillListAction!addHillComment", {"nickname":encodeURI($("#nickname").val()),"comment":encodeURI($("#comment").val())}, 
				function(returnedData,status) {
					if ("success" == status)
						$("#content").prepend("<p>" + $("#comment").val() + "</p>");  //insert the new comment firstly;					
				});
		}			
	});
	
	/*
	 * bind the click event to each radio box to vote the express support on this hill list
	 */
	$(":input[type=radio]").click(function() {
		//1. if any radio box is selected, disable all;
		$(":input[type=radio]").attr("disabled","disabled"); 
		//2.show chart1 div
		$("#info1").html("");
		$("#chart1").show(1500);
		$("#chart1").html("请稍候...");
		//3.pass the user choice to action via ajax post() method
		$.post("hillListAction!express", {support:$(this).val()}, function(returnedData,status) {
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
			    /* $('#chart1').bind('jqplotDataHighlight',
			        function (ev, seriesIndex, pointIndex, data) {
			            $('#info1').html('series: '+seriesIndex+', point: '+pointIndex+', data: '+data);
			        }
			    );
			    //7. bind unhighlight method (optional)    
			    $('#chart1').bind('jqplotDataUnhighlight',
			        function (ev) {
			            $('#info1').html('鼠标放在图形上可显示具体投票结果');
			        }
			    ); */
			}
		});
	});
});

function previous(){
	
	new_page = parseInt($('#current_page').val()) - 1;
	//if there is an item before the current active link run the function
	if($('.active_page').prev('.page_link').length==true){
		go_to_page(new_page);
	}	
};

function next(){
	new_page = parseInt($('#current_page').val()) + 1;
	//if there is an item after the current active link run the function
	if($('.active_page').next('.page_link').length==true){
		go_to_page(new_page);
	}	
};

function go_to_page(page_num){
	//get the number of items shown per page
	var show_per_page = parseInt($('#show_per_page').val());
	
	//get the element number where to start the slice from
	start_from = page_num * show_per_page;
	
	//get the element number where to end the slice
	end_on = start_from + show_per_page;
	
	//hide all children elements of content div, get specific items and show them
	$('#content').children().css('display', 'none').slice(start_from, end_on).css('display', 'block');
	
	/*get the page link that has longdesc attribute of the current page and add active_page class to it
	and remove that class from previously active page link*/
	$('.page_link[longdesc=' + page_num +']').addClass('active_page').siblings('.active_page').removeClass('active_page');
	
	//update the current page input field
	$('#current_page').val(page_num);
};

</script>
<style>
#page_navigation a{
	padding:3px;
	border:1px solid gray;
	margin:2px;
	color:black;
	text-decoration:none
}
.active_page{
	background:darkblue;
	color:white !important;
}
</style>
</head>
<body>
	<h2>名山大川投票排行榜</h2>
	<p>1.泰山<input type="checkbox" name="hillCheck" value="泰山" /></p>
	<p>2.华山<input type="checkbox" name="hillCheck" value="华山" /></p>
	<p>3.峨眉山<input type="checkbox" name="hillCheck" value="峨眉山" /></p>
	<p>4.九华山<input type="checkbox" name="hillCheck" value="九华山" /></p>
	<p>5.嵩山<input type="checkbox" name="hillCheck" value="嵩山" /></p>
	<br/>
	<input id="vote" type="button" value="投票" />
	<br/>
	<div id="showVote" style="height:100px;width:250px;display: none;"></div>
	<br/>
	<br/>
	
	<label>支持系统</label>
	<br/>
	<input type="radio" name="support" value="hill1">完全支持
	<input type="radio" name="support" value="hill2">支持
	<input type="radio" name="support" value="hill3">中立
	<input type="radio" name="support" value="hill4">不太支持
	<input type="radio" name="support" value="hill5">不支持
	<br/>
	<div id="info1"></div>
	<br/>
	<div id="chart1"  style="height:100px;width:250px;display: none;"></div>
	<br/>
	
	<label>针对名山大川投票排行榜的评论如下：</label>
	<!-- the input fields that will hold the variables we will use -->
	<input type='hidden' id='current_page' />
	<input type='hidden' id='show_per_page' />
	
	<br/> 昵称(可选):
	<input type="text" id="nickname" name="nickname">
	<br/> 评论:
	<textarea rows="10" cols="30" id="comment" name="comment"></textarea><span id="commentSpan"></span>
	<br/>
	<input id="btn" type="button" value="提交您的评论" />
	<br/>
	
	<!-- Content div. The child elements will be used for paginating(they don't have to be all the same,
		you can use divs, paragraphs, spans, or whatever you like mixed together). '-->
	<div id="content"></div>
	
	<!-- An empty div which will be populated using jQuery -->
	<div id='page_navigation'></div>
</body>
</html>