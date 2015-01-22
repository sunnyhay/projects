package org.lvyouzaike.util;

import java.util.Comparator;

import org.lvyouzaike.model.Resort;

public class ResortVoteComparator implements Comparator<Resort> {

	@Override
	public int compare(Resort r1, Resort r2) {
		if(r1.getVoters().size() > r2.getVoters().size())
			return 1;
		else if(r1.getVoters().size() < r2.getVoters().size())
			return -1;
		else
			return 0;
	}

}
