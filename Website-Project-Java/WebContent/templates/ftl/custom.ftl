	<div id="custom" class="part">
		<p><a name="custom">旅游目的地最新更新</a></p>
		<ul>
			<#list customlist as c>
			<li>
			<p>旅游目的地：${c.title}</p>
			${c.summary}
			</li>
			</#list>
		</ul>
	</div>
	<div id="footer" class="part">
		<p>谢谢您订阅旅游宰客榜邮件服务，欢迎您来信（mailto:sunnyhay1@gmail.com）提出您的意见和建议。
		旅游宰客榜诚心希望反映广大游客的心声，通过我们一起的努力来改善中华旅游的不足，请与我们携手创造更美好的明天！</p>
		<p>欢迎访问 <a href="#">旅游宰客榜主页</a>  <a href="#">排行榜</a>  <a href="#">下载</a>  <a href="#">旅游风险百科全书</a>
		  <a href="#">新闻主页</a>  <a href="#">邮件服务</a>  <a href="#">保险箱</a>  <a href="#">专题</a></p>
	</div>
</body>
</html>