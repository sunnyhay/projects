package org.lvyouzaike.service;

public interface VoteHillRecordService {
	public boolean add(String ip, String hills);  //add a vote record, hills is a string with hills delimited by commas
}
