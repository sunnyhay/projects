package org.lvyouzaike.service;

public interface NewsCommentService {
	//加入新的新闻评论
	public boolean addNewsComment(String name, String ip, String nickname, String comment);
}
