package org.lvyouzaike.util;

import java.util.Comparator;

import org.lvyouzaike.model.VoteTemple;

public class VoteTempleComparator implements Comparator<VoteTemple> {

	@Override
	public int compare(VoteTemple t1, VoteTemple t2) {
		if(t1.getVoteTempleRecord().size() > t2.getVoteTempleRecord().size())
			return 1;
		else if(t1.getVoteTempleRecord().size() < t2.getVoteTempleRecord().size())
			return -1;
		else
			return 0;
	}

}
