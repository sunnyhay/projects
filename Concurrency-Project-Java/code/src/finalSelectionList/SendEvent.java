package finalSelectionList;

import java.util.concurrent.locks.Condition;

public class SendEvent extends CommEvent{
	/*ChannelPool cp;  //channel pool.
	int indexOfChannel;  //the index of associated channel for the event used by a client thread.
	*/Object o;  //temporary data from sender to receiver through matching between SendEvent and RecvEvent.
	/*Channel c;  //the specific channel decided by pool and indexOfChannel.
*/	
	public SendEvent(Object o, ChannelPool cp, int indexOfChannel){
		super(cp, indexOfChannel);
		/*this.cp = cp;
		this.indexOfChannel = indexOfChannel;*/
		this.o = o;		
	}
	
	void sync(){
		cp.lock();
		try{
			c.dataQueue.add(o);  //add object into dataQueue in this channel.
			if(poll(this)){//check complementary CommEvent. if true do matching work, otherwise enqueue.
				cp.signal(c.commConditionQueue.remove().getCondition());  //remove CommEvent and condition and signal the latter's corresponding thread.
			}else{
				Condition condition = cp.createNewCondition();  //create current thread's key
				enqueue(this, condition);
				cp.await(condition);
			}
		}catch (InterruptedException e){
			e.printStackTrace();
		}finally {
			cp.unlock();
		}
	}
	
}
