package org.lvyouzaike.util;

import java.util.Comparator;

import org.lvyouzaike.model.VoteHill;

public class VoteHillComparator implements Comparator<VoteHill> {

	@Override
	public int compare(VoteHill h1, VoteHill h2) {
		if(h1.getVoteHillRecord().size() > h2.getVoteHillRecord().size())
			return 1;
		else if(h1.getVoteHillRecord().size() < h2.getVoteHillRecord().size())
			return -1;
		else
			return 0;
	}

}
