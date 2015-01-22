package org.lvyouzaike.util;

import java.util.Comparator;

import org.lvyouzaike.model.Resort;

public class ResortMailSubComparator implements Comparator<Resort> {

	@Override
	public int compare(Resort r1, Resort r2) {
		if(r1.getMails().size() > r2.getMails().size())
			return 1;
		else if(r1.getMails().size() < r2.getMails().size())
			return -1;
		else
			return 0;
	}

}
