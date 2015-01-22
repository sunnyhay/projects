package finalSelectionList;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChannelPool {
	public LinkedList<Channel> pool;  //the collection of all channels used for CommEvents.
	//public LinkedList<CommEvent> matchedCommEventQueue;  //those matched CommEvent returned by the complementary thread.
	public CommEvent temp;
	private int count;  //the amount of channels in the pool, decided by the consensus among client threads.
	private final Lock lock = new ReentrantLock();  //the unique lock to the pool.
	//Condition condition;  //the key to client threads using the unique pool.
	
	ChannelPool(LinkedList<Channel> pool, int count, CommEvent temp){
		this.pool = pool;
		this.count = count;
		this.temp = temp;
		init();
	}
	
	public CommEvent getTemp(){
		return temp;
	}
	
	public void setTemp(CommEvent temp){
		this.temp = temp;
	}
	
	private void init(){
		for(int i = 0; i < count; i ++)
			pool.add(new Channel(new LinkedList<CommCondition>(), new LinkedList<Object>()));
	}
	
	public void lock(){
		lock.lock();
	}
	
	public void unlock(){
		lock.unlock();
	}
	
	public Condition createNewCondition(){
		return lock.newCondition();
	}
	
	public void await(Condition condition) throws InterruptedException{//may be modified.
		condition.await();
	}
	
	public void signal(Condition condition){//may be modified.
		condition.signal();
	}
}
