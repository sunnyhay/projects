<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>旅游宰客邮件</title>
<style type="text/css">
a {
	text-decoration: inherit;
	color: blue;
}
a:hover {
	color: #900;
	text-decoration: underline;
}
.part {
	color: #000;
	width: 980px;
	border: 1px dotted #06C;
	margin-top: 1px;
	margin-left: auto;
	margin-right: auto;	
	float: left;
}
#newscontent, #boxcontent{
	width: 650px;
	margin: 0px;
	padding: 0px;
	float: left;
}
#newspic, #boxpic{
	width: 320px;
	height: 200px;
	margin: 1px;
	padding: 0px;
	float: right;
}
#header{
	color: #000;
	width: 980px;
	margin-left: auto;
	margin-right: auto;
	float: left;
}
</style>
</head>
<body>
	<div id="header">
		<h4>旅游宰客邮件第${std.order}期发布于${std.time}</h4>
		<p>尊敬的用户，以下是您在<a href="#">旅游宰客榜</a>的邮件订阅信息。</p>
		<p>邮件地址：${ctm.email}</p>
		<p>目的旅游城市或景区：${ctm.resortlist}</p>
		<p>订阅日期：${ctm.sub_date}</p>
		<p>上次邮件发送日期：${ctm.send_date}</p>
	</div>
	<div id="abstract" class="part">
		<p>本期目录</p>
		<p>1. <a href="#news">宰客新闻</a>   2. <a href="#policy">政策法规</a>   3. <a href="#list">排行榜概要</a></p>
		<p>4. <a href="#box">推荐保险箱</a>   5. <a href="#download">下载更新</a>   6. <a href="#custom">用户订阅旅游目的地最新更新</a></p>
	</div>
	<div id="news" class="part">
		<p><a name="news">宰客新闻</a></p>
		<div id="newscontent">
			<ul>
				<#list newslist as news>
				<li>
				<p>${news.title}</p>
				${news.summary}
				</li>
				</#list>
			</ul>
		</div>
		<div id="newspic">
			<img alt="宰客新闻图片" src="cid:news" width=320 height=200>
		</div>
	</div>
	<div id="policy" class="part">
		<p><a name="policy">政策法规</a></p>
		<ul>
			<#list policylist as policy>
			<li>
			<p>${policy.title}</p>
			${policy.summary}
			</li>
			</#list>
		</ul>
	</div>
	<div id="list" class="part">
		<p><a name="list">排行榜概要</a></p>
		<ul>
			<li>首页宰客小测验显示：在已经投票的${lst.numOfFirstTotal}名受访者中，被宰人数为${lst.numOfFirstBeizai}，
			没有被宰熟人数为${lst.numOfFirstMeizai}，被宰比率高达${lst.numOfFirstRatio}%！</li>
			<li>首页宰客程度小测验显示：在已经投票的${lst.numOfSecondTotal}名受访者中，
			${lst.numOfSecondMost}人被宰金额超过2万元，${lst.numOfSecondBig}人介于5千到2万之间，
			${lst.numOfSecondMedium}人介于1千到5千，${lst.numOfSecondSmall}人在1千以内，
			完全没有损失的仅仅${lst.numOfSecondNone}人而已！
			</li>
			<li>国内景区排行榜前三甲是${lst.resortFirstName}（${lst.resortFirstAmount}票），
			${lst.resortSecondName}（${lst.resortSecondAmount}票）和
			${lst.resortThirdName}（${lst.resortThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>
			<li>国内城市排行榜前三甲是${lst.cityFirstName}（${lst.cityFirstAmount}票），
			${lst.citySecondName}（${lst.citySecondAmount}票）和
			${lst.cityThirdName}（${lst.cityThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>
			<li>海外国家排行榜前三甲是${lst.outerNationFirstName}（${lst.outerNationFirstAmount}票），
			${lst.outerNationSecondName}（${lst.outerNationSecondAmount}票）和
			${lst.outerNationThirdName}（${lst.outerNationThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>
			<li>海外景区排行榜前三甲是${lst.outerResortFirstName}（${lst.outerResortFirstAmount}票），
			${lst.outerResortSecondName}（${lst.outerResortSecondAmount}票）和
			${lst.outerResortThirdName}（${lst.outerResortThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>
			<li>名山宰客榜前三甲是${lst.hillFirstName}（${lst.hillFirstAmount}票），
			${lst.hillSecondName}（${lst.hillSecondAmount}票）和
			${lst.hillThirdName}（${lst.hillThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>
			<li>烧香拜佛榜前三甲是${lst.templeFirstName}（${lst.templeFirstAmount}票），
			${lst.templeSecondName}（${lst.templeSecondAmount}票）和
			${lst.templeThirdName}（${lst.templeThirdAmount}票）。点击<a href="#">这里</a>访问该排行榜查看详情！</li>			
		</ul>
		<p>欲知更多旅游宰客排行榜详情，请访问<a href="#">旅游宰客榜</a>！</p>
	</div>
	<div id="box" class="part">
		<p><a name="box">推荐保险箱</a></p>
		<div id="boxcontent">
			<ul>
				<#list boxlist as box>
				<li>
				<p>${box.title}</p>
				${box.summary}
				</li>
				</#list>
			</ul>
		</div>
		<div class="boxpic">
			<img alt="保险箱图片" src="cid:box" width=320 height=200>
		</div>
	</div>
	<div id="download" class="part">
		<p><a name="download">下载更新</a></p>
		<p>《旅游风险红宝书》最新版已经上传，请点击<a href="#">这里</a>前往下载页面。</p>
	</div>