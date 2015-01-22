package org.lvyouzaike.service;

public interface VoteTempleRecordService {
	public boolean add(String ip, String temples);  //add a vote record, temples is a string with temples delimited by commas
}
