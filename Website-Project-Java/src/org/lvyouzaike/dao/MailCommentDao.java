package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.MailComment;

public interface MailCommentDao {
	public boolean add(MailComment comment);  //add a new mail comment when the user tries to unsubscribe
	public boolean delete(int id);  //delete a mail comment by id
	public List<MailComment> getAll();  //get all the comments
	public List<MailComment> get(Date current);  //get all the comments submitted after current time
}
