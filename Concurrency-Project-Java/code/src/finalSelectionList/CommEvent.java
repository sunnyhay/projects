package finalSelectionList;

import java.util.concurrent.locks.Condition;

public class CommEvent {
	ChannelPool cp;  //the channel pool associated.
	int indexOfChannel;  //the index of the specific channel used.
	Channel c;  //the specific channel decided by channel pool and use's channel choice, index here.
	
	public CommEvent(ChannelPool cp, int indexOfChannel){
		this.cp = cp;
		this.indexOfChannel = indexOfChannel;
		//System.out.println(this.cp);
		c = cp.pool.get(indexOfChannel);  //assign c to a channel candidate in pool by means of a given index.
		//System.out.println(c);
		//System.out.println(this.cp);
	}
	
	void sync(){};
	
	boolean getType(CommEvent ce){
		//return TRUE for SendEvent, false for RecvEvent.
		return ce.getClass() == SendEvent.class ? true : false;
	}
	
	boolean poll(CommEvent ce){
		//common poll() method for SendEvent and RecvEvent.
		//Two false cases: commConditionQueue is empty or same CommEvent type for ce and first element in commConditionQueue.
		//only true: commConditionQueue nonempty and complementary CommEvent type for ce and first element in commConditionQueue.
		if(c.commConditionQueue.isEmpty())
			return false;
		else
			return getType(ce) == getType(c.commConditionQueue.getFirst().getCommEvent()) ? false : true;
	}
	
	void enqueue(CommEvent ce, Condition condition){
		c.commConditionQueue.add(new CommCondition(ce, condition));
		//System.out.println("now commConditionQueue empty? " + c.commConditionQueue.isEmpty());
	}
}
