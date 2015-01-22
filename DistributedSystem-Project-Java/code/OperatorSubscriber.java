
public class OperatorSubscriber implements Runnable{
    boolean flag;

    public OperatorSubscriber(boolean flag) {
	this.flag = flag;
    }

    public void run() {
	//new PositionSubscriber().subscriberMain(0,0);
	if(flag)
	    new AccidentSubscriber().subscriberMain(0,0);
	else
	    new PositionSubscriber().subscriberMain(0,0);
    }

    public static void main(String[] args) {
	System.out.format("%s%8s%14s%10s%8s%8s%20s%8s%12s", "MessageType","Route","Vehicle","Traffic","Stop#","#Stop","TimeBetweenStops","Fill%","Timestamp");
	new Thread(new OperatorSubscriber(true)).start();
	new Thread(new OperatorSubscriber(false)).start();
    }
}
