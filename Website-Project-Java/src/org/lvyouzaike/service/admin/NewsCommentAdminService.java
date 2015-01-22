package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.NewsComment;

public interface NewsCommentAdminService {
	public boolean deleteNewsComment(int id);      //delete a comment with this id
	public List<NewsComment> getAllNewsComments();       //get all the comments
	public List<NewsComment> getNewsCommentsForName(String name);
	public List<NewsComment> getNewsCommentsForNameByDate(String name, Date from, Date to);  //get comments in the scope <from, to> for the same news
	public List<NewsComment> getNewsCommentsForIp(String ip);
}
