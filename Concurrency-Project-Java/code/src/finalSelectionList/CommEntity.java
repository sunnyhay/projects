package finalSelectionList;

public class CommEntity implements Runnable{
	SelectionList list;
	
	public CommEntity(SelectionList list){
		this.list = list;
	}
	
	public void addEvent(CommEvent ce){
		list.addEvent(ce);
	}
	
	public void printout(CommEvent ce){
		if(!ce.getType(ce)){
			RecvEvent re = (RecvEvent)ce;
			System.out.println("Get value: " + re.getValue());
		}		
	}
	
	public void run(){
		try {
			System.out.println("Current Thread begins with: " + Thread.currentThread());
			System.out.println("Matched CommEvent:" + list.select());
			//printout();
			System.out.println("Current Thread done: " + Thread.currentThread());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
