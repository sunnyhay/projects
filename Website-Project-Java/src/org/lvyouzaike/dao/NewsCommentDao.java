package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.NewsComment;

public interface NewsCommentDao {
	public boolean add(NewsComment comment);     //add a new mail comment
	public boolean delete(int id);      //delete a comment with this id
	public List<NewsComment> getAll();       //get all the comments
	public List<NewsComment> getForSameName(String name);
	public List<NewsComment> getManyForSameName(String name, Date from, Date to);  //get comments in the scope <from, to> for the same news
	public List<NewsComment> getForSameIp(String ip);
}
