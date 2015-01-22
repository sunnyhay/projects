package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.CommentModel;

public interface CommentAdminService {
	public boolean delete(int id);      //delete a comment with this id
	public boolean deleteMany(int fromId, int toId);   //delete comments with id scope from fromId to toId
	public List<CommentModel> getAll();       //get all the comments
	public List<CommentModel> getMany(Date from, Date to);  //get comments in the scope <from, to>
	public CommentModel getById(int id);  //get a comment with this id
}
