package finalSelectionList;

public class ReceiverEntity implements Runnable{
	ChannelPool cp;
	int index;
	static int count = 0;
		
	ReceiverEntity(ChannelPool cp, int index){
		this.cp = cp;
		this.index = index;
	}
	
	public void run(){
		RecvEvent re = new RecvEvent(cp, index);
		//System.out.println(cp);
		//System.out.println(re.cp);
		re.sync();
		count ++;
		//if(count > 9990)
			System.out.println("count: " + count);
		/*System.out.println("Get value: " + re.getValue());
		System.out.println("Receiver " + Thread.currentThread() + " died.");*/
	}
}
