package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.CommentModel;

/*
 * this interface should be implemented by all the concrete implementations for CommentDaoImpl*** classes. *** indicates comment name
 * the argument of CommentModel object should be replaced with Comment*** object in CommentService*** classes
 */

public interface CommentDao {
	public boolean add(CommentModel comment);     //add a new CommentModel object
	public boolean delete(int id);      //delete a comment with this id
	public boolean deleteMany(int fromId, int toId);   //delete comments with id scope from fromId to toId
	public List<CommentModel> getAll();       //get all the comments
	public List<CommentModel> getMany(Date from, Date to);  //get comments in the scope <from, to>
	public CommentModel get(int id);  //get a comment with this id
}
