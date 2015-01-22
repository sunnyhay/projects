public class TestPassenger implements Runnable {
    P po = new P();
    InputParameter ip = new InputParameter();
    public static String route;
    public static String source;
    public static String destination;
    /*po.routeNumber = Integer.parseInt(args[0]);
    po.sourceStop = Integer.parseInt(args[1]);
    po.destinationStop = Integer.parseInt(args[2]);*/
    public TestPassenger() {
    }

    public void run() {
	ip.routeNumber = Integer.parseInt(route);
	ip.sourceStop = Integer.parseInt(source);
	ip.destinationStop = Integer.parseInt(destination);
	//System.out.println("Hi: " + ip.routeNumber);
	new PositionSubscriberFirst(po, ip).subscriberMain(0,0);
	//System.out.println("Current busname: " + po.vehicle);
	new PositionSubscriberSecond(po).subscriberMain(0,0);	
    }

    public static void main(String[] args) {
	if(args.length > 0) {
	    route = args[0];
	    source = args[1];
	    destination = args[2];
	}
	new Thread(new TestPassenger()).start();
    }
}
	
