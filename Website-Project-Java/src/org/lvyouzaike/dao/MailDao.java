package org.lvyouzaike.dao;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.Mail;

public interface MailDao {
	public boolean check(String address);  //check if the mail address exists in the list of mail subscription
	public boolean add(Mail mail);  //add a new mail subscription
	public boolean update(Mail mail);  //update a mail subscription
	public Mail getById(int id);  //get a mail object by id
	public Mail getByAddress(String address);  //get a mail object by its address
	public List<Mail> getAll();  //get all mail subscriptions
	public List<Mail> getValidAll();  //get all valid mail subscription
	public List<Mail> getBySubTime(Date from, Date to);  //get the list of mail subscription according to the scope of subscription date
	public boolean isAllSentAfter(Date current);  //check if any mail subscriptions have not been sent after the current time
	public int numOfCustomContent();  //get the number of all mail subscriptions with customized content
	public int numOfValid();  //get the number of all valid mail subscriptions	
}
