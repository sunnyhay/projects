package finalSelectionList;

import java.util.concurrent.locks.Condition;

public class RecvEvent extends CommEvent{
	/*ChannelPool cp;  //the channel pool associated.
	int indexOfChannel;  //the index of the specific channel used.
	Channel c;  //the specific channel decided by channel pool and use's channel choice, index here.
	*/Object o;  //temporary object from sender to receiver through SendEvent to RecvEvent.
	
	public RecvEvent(ChannelPool cp, int indexOfChannel){
		//System.out.println(cp);
		super(cp, indexOfChannel);
		//System.out.println(this.cp);
		/*this.cp = cp;
		this.indexOfChannel = indexOfChannel;*/
	}
	
	Object getValue(){
		return o;
	}
	
	void sync(){
		cp.lock();
		try{
			if(poll(this)){
				cp.signal(c.commConditionQueue.remove().getCondition());  //remove CommEvent and condition and signal the latter's corresponding thread.
			}else{
				Condition condition = cp.createNewCondition();
				enqueue(this, condition);
				cp.await(condition);
			}
		}catch (InterruptedException e){
			e.printStackTrace();
		}finally{
			o = c.dataQueue.remove();  //transfer the object to the receiver from the channel's data queue.
			cp.unlock();
		}
	}
}
