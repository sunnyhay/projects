package finalSelectionList;

import java.util.LinkedList;

public class Channel {
	LinkedList<CommCondition> commConditionQueue;  //the queue to hold both CommEvent and its condition (key).
	LinkedList<Object> dataQueue;  //the queue to hold data object from SendEvent to RecvEvent.
	
	Channel(LinkedList<CommCondition> commConditionQueue, LinkedList<Object> dataQueue){
		this.commConditionQueue = commConditionQueue;
		this.dataQueue =dataQueue;
	}	
}
