package finalSelectionList;

public class SenderEntity implements Runnable{
	ChannelPool cp;
	int index;
	Object o;
	
	SenderEntity(ChannelPool cp, int index, Object o){
		this.cp = cp;
		this.index = index;
		this.o = o;
	}
	
	public void run(){
		SendEvent se = new SendEvent(o, cp, index);
		/*System.out.println(cp);
		System.out.println(se.cp);*/
		se.sync();
		/*System.out.println("Sender " + Thread.currentThread() + " died.");*/
	}
}
