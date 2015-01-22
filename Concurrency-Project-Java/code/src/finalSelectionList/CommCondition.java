package finalSelectionList;

import java.util.concurrent.locks.Condition;

public class CommCondition {
	//the class is used in Channel's primary commConditionQueue.
	private CommEvent ce;
	private Condition condition;
	
	public CommCondition(CommEvent ce, Condition condition) {
		this.ce = ce;
		this.condition = condition;
	}
	
	public CommEvent getCommEvent(){
		return ce;
	}
	
	public Condition getCondition(){
		return condition;
	}
}
