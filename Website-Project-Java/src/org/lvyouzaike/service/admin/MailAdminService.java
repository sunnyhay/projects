package org.lvyouzaike.service.admin;

import java.util.Date;
import java.util.List;

import org.lvyouzaike.model.Mail;

public interface MailAdminService {
	public List<Mail> getAll();  //get all mail subscriptions
	public List<Mail> getValidAll();  //get all valid mail subscription
	public List<Mail> getBySubTime(Date from, Date to);  //get the list of mail subscription according to the scope of subscription date
	public boolean isAllSentAfter(Date current);  //check if any mail subscriptions have not been sent after the current time
	public int numOfCustomContent();  //get the number of all mail subscriptions with customized content
	public int numOfValid();  //get the number of all valid mail subscriptions
	public boolean updateMail(Mail mail);   //update the mail subscription, usually after a round of sending
}
