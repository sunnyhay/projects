package finalSelectionList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;

public class SelectionList {
	LinkedList<CommEvent> selectionList;  //selection list to hold CommEvents.
	ChannelPool cp;  //associate channel pool to this selectionList.
	
	SelectionList(LinkedList<CommEvent> selectionList, ChannelPool cp){
		this.selectionList = selectionList;
		this.cp = cp;
	}
	
	void addAllAndBlock() throws InterruptedException{
		//add all the events in this selectionList to commConditionQueues for corresponding channels. also, data enqueue for SendEvents.
		Condition condition = cp.createNewCondition();  //the same condition for all events in the same client thread.		
		Iterator<CommEvent> iterator = selectionList.iterator();
		while(iterator.hasNext()){
			CommEvent ce = iterator.next();  //fetch one CommEvent from selection list.
			System.out.println("Now add CommEvent: " + ce);
			ce.enqueue(ce, condition);
			//System.out.println("Add operation commConditionQueue empty? " + ce.c.commConditionQueue.isEmpty());
			//System.out.println("Add operation's channel: " + ce.c);
			if(ce.getType(ce)){
				SendEvent se = (SendEvent)ce;
				ce.c.dataQueue.add(se.o);  //add object into channel's data queue if this CommEvent is SendEvent.
				//System.out.println("Add operation dataqueue empty? " + ce.c.dataQueue.isEmpty());
			}			
		}
		cp.await(condition);
	}
	
	void addEvent(CommEvent ce){
		selectionList.add(ce);
	}
	
	private int matchCommEvent(CommEvent ce, LinkedList<CommCondition> commConditionQueue){
		//find matched CommCondition in this queue for given CommEvent.
		Iterator<CommCondition> iterator = commConditionQueue.iterator();
		
		while(iterator.hasNext()){
			CommCondition cc = iterator.next();
			if(ce == cc.getCommEvent())
				return commConditionQueue.indexOf(cc);
		}
		return -1;  //not found, but impossible to happen.
	}
	
	void removeAllFromQueues(){
		//remove all the CommEvents in selection list from corresponding queues in channels. Notice: match CommEvents first, then remove CommConditions.
		int index;  //the index for each CommCondition in channels' queues. used only for remove object from data queue.
		Iterator<CommEvent> iterator = selectionList.iterator();
		
		while(iterator.hasNext()){
			CommEvent ce = iterator.next();
			index = matchCommEvent(ce, ce.c.commConditionQueue);  //now get the index of the given CommEvent (and its condition) in a queue.
			if(index == -1)
				continue;
			//System.out.println("remove operation channel: " + ce.c);
			//System.out.println("remove operation commConditionQueue empty? " + ce.c.commConditionQueue.isEmpty());
			ce.c.commConditionQueue.remove(index);  //index inside remove
			if(ce.getType(ce))  //if SendEvent, remove the object added previously from corresponding data queue.
				ce.c.dataQueue.remove(index);  //index inside remove
		}
	}
	
	void removeEvent(CommEvent ce){
		selectionList.remove(ce);
	}
	
	CommEvent pollAndSync(){
		//poll all CommEvents in selection list, and do REAL COMMUNICATIION WORK if success on any. return the matched CommEvent in my own selection list.
		Iterator<CommEvent> iterator = selectionList.iterator();
		
		while(iterator.hasNext()){
			CommEvent ce = iterator.next();
			System.out.println("Now poll CommEvent: " + ce);
			if(ce.poll(ce)){//poll success. do communication work here. finally return this ce.
				//int index = matchCommEvent(ce, ce.c.commConditionQueue);  //get the exact index of matching CommEvent in the channel's commCondition queue
				CommCondition cc = ce.c.commConditionQueue.remove();  //index inside. remove this commCondition object from the channel's queue.
				if(ce.getType(ce)){//SendEvent. need to add object into its channel's data queue.
					SendEvent se = (SendEvent)ce;
					ce.c.dataQueue.add(se.o);
				}else{//RecvEvent, need to dequeue from data queue.
					RecvEvent re = (RecvEvent)ce;
					re.o = ce.c.dataQueue.remove();
					System.out.println("Get value: " + re.getValue());
				}
				cp.signal(cc.getCondition());  //signal 
				cp.setTemp(cc.getCommEvent());  //add this matched CommEvent into the global queue.
				return ce;
			}
		}
		return null;
	}
	
	CommEvent select() throws InterruptedException{
		//poll for each CommEvent in selection list: if true for someone, match it and done. otherwise, call enqueue for each CommEvent and block.
		cp.lock();
		System.out.println("Begin select!");
		CommEvent ce = pollAndSync();
		if(ce != null){
			cp.unlock();
			return ce;
		}else{
			addAllAndBlock();
			removeAllFromQueues();
			CommEvent ce1 = cp.getTemp();
			removeEvent(ce1);
			cp.unlock();
			return ce1;
		}		
	}
	
	public static void main(String[] args) {
		LinkedList<Channel> pool = new LinkedList<Channel>();
		ChannelPool cp = new ChannelPool(pool, 2, null);  //a channel pool with only two channels.
		
		//test coide for select() and sync() combination.
		CommEntity receiver = new CommEntity(new SelectionList(new LinkedList<CommEvent>(),cp));  //receiver
		SenderEntity sender1 = new SenderEntity(cp, 0, "sender1");
		SenderEntity sender2 = new SenderEntity(cp, 1, "sender2");
		RecvEvent re = new RecvEvent(cp, 0);
		RecvEvent re2 = new RecvEvent(cp, 1);
		receiver.addEvent(re);
		receiver.addEvent(re2);
		
		new Thread(sender1).start();
		new Thread(sender2).start();
		new Thread(receiver).start();
		new Thread(receiver).start();
		
		
		/*//test code of select() for two threads, four events. 
		CommEntity entity1 = new CommEntity(new SelectionList(new LinkedList<CommEvent>(),cp));  //entity 1
		CommEntity entity2 = new CommEntity(new SelectionList(new LinkedList<CommEvent>(),cp));  //entity 2
		
		SendEvent se = new SendEvent("sender1", cp, 0);
		SendEvent se2 = new SendEvent("sender2", cp, 1);
		RecvEvent re = new RecvEvent(cp, 0);
		RecvEvent re2 = new RecvEvent(cp, 1);
		
		entity1.addEvent(se);
		entity1.addEvent(re2);
		entity2.addEvent(re);
		entity2.addEvent(se2);
		
		new Thread(entity1).start();
		new Thread(entity2).start();
		new Thread(entity1).start();
		new Thread(entity2).start();*/
				
		/*//test code for phase-2.
		SenderEntity sender1 = new SenderEntity(cp, 1, "sender1");
		SenderEntity sender2 = new SenderEntity(cp, 1, "sender2");
		ReceiverEntity receiver = new ReceiverEntity(cp, 1);
		for(int i = 0; i < 10000; i ++){
			new Thread(receiver).start();
			if(i % 2 == 0)
				new Thread(sender1).start();
			else 
				new Thread(sender2).start();
		}*/		
	}
}
