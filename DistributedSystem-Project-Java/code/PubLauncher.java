/*
 * The PubLauncher reads all of its initialization parameters from a properties
 *  file called pubsub.properties and starts a thread for each vehicle on each 
 *  route. A thread (PubThread) publishes all the messages, alerts (accidents) 
 *  and positions for this vehicle at each bus stop along the route.
 */
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class PubLauncher {
	
	Properties props = new Properties();
	int numRoutes;                      //number of routes 
	int numVehicles;                    //number of vehicles for each route
	
	String route1;                      //name of route1
	String route2;                      //name of route2
	
	int route1numStops;                 //number of bus stops on route1
	int route1TimeBetweenStops;         //base time interval between stops on route1
	String route1Vehicle1;              //name of Bus#1 on route1
	String route1Vehicle2;              //name of Bus#2 on route1
	String route1Vehicle3;              //name of Bus#3 on route1
	
	int route2numStops;                 //number of bus stops on route2
	int route2TimeBetweenStops;         //base time interval between stops on route2
	String route2Vehicle1;              //name of Bus#1 on route2
	String route2Vehicle2;              //name of Bus#2 on route2
	String route2Vehicle3;	            //name of Bus#3 on route2
	
	/*
	 * Step 1: parse pub.properties file and read all info inside for PubThread.
	 * Notice difference between Windows and Linux directory for file path.
	 * Method parsePubProperties is used for parsing.
	 */
	/*
	 * Step 1 start-----------------------------------------------------
	 */
	private void parsePubProperties(){
	//parse pub.properties and read corresponding values
		try{
        	props.load(new FileInputStream("pub.properties"));
        	
        	numRoutes = Integer.parseInt(props.getProperty("numRoutes"));
        	numVehicles= Integer.parseInt(props.getProperty("numVehicles"));
        	//printout("No of Routes is: ",numRoutes);
        	//printout("No of Buses is: ",numVehicles);
        	
        	route1 = props.getProperty("route1");
        	route2 = props.getProperty("route2");
        	//printout("Name of Route1 is: ",route1);
        	//printout("Name of Route2 is: ",route2);
        	        	
        	route1numStops = Integer.parseInt(props.getProperty("route1numStops"));
        	route1TimeBetweenStops = Integer.parseInt(props.getProperty("route1TimeBetweenStops"));
        	route1Vehicle1 = props.getProperty("route1Vehicle1");
        	route1Vehicle2 = props.getProperty("route1Vehicle2");
        	route1Vehicle3 = props.getProperty("route1Vehicle3");
        	//printout("No of Stops in Route1 is: ",route1numStops);
        	//printout("Interval stops for Route1(seconds) is: ",route1TimeBetweenStops);
        	//printout("Name of Bus1 in route1 is: ",route1Vehicle1);
        	//printout("Name of Bus2 in route1 is: ",route1Vehicle2);
        	//printout("Name of Bus3 in route1 is: ",route1Vehicle3);
        	
        	route2numStops = Integer.parseInt(props.getProperty("route2numStops"));
        	route2TimeBetweenStops = Integer.parseInt(props.getProperty("route2TimeBetweenStops"));
        	route2Vehicle1 = props.getProperty("route2Vehicle1");
        	route2Vehicle2 = props.getProperty("route2Vehicle2");
        	route2Vehicle3 = props.getProperty("route2Vehicle3");
        	//printout("No of Stops in Route2 is: ",route2numStops);
        	//printout("Interval stops for Route2(seconds) is: ",route2TimeBetweenStops);
        	//printout("Name of Bus1 in route2 is: ",route2Vehicle1);
        	//printout("Name of Bus2 in route2 is: ",route2Vehicle2);
        	//printout("Name of Bus3 in route2 is: ",route2Vehicle3);
                	
        }//catch exception in case properties file does not exist
        catch(IOException e){
        	e.printStackTrace();
        }
	}
	
	/*private static void printout(String message, Object o){
	//test print out method for parsePubProperties
		System.out.format("%s %s%n", message, o.toString());
	}*/
	/*
	 * Step 1 end-----------------------------------------------------
	 */
	/*
	 * Step 2: start() runs all PubThreads according to parsed parameters
	 */
	/*
	 * Step 2 start-----------------------------------------------------
	 */
	
	private void start() throws InterruptedException{
		
		String[][] busNames = new String[numRoutes][numVehicles];
		String[] routeGroup = {route1, route2};
		int[] stopGroup = {route1numStops, route2numStops};
		int[] intervalGroup = {route1TimeBetweenStops, route2TimeBetweenStops};
		busNames[0][0] = route1Vehicle1;
		busNames[0][1] = route1Vehicle2;
		busNames[0][2] = route1Vehicle3;
		busNames[1][0] = route2Vehicle1;	
		busNames[1][1] = route2Vehicle2;
		busNames[1][2] = route2Vehicle3;
		ArrayList<Thread> busGroup = new ArrayList<Thread>();
		for(int i = 0; i < numRoutes; i ++)
			for(int j = 0; j < numVehicles; j ++)
				busGroup.add(new Thread(new PubThread(routeGroup[i], 
						stopGroup[i], intervalGroup[i], busNames[i][j])));
		System.out.println("Start the PubLauncher!");
		Iterator<Thread> it = busGroup.iterator();
		int threadOrder = 0;
		while(it.hasNext()) {
			System.out.println("Thread #" + threadOrder + " started.");
			threadOrder ++;
			it.next().start();
		}
		System.out.println("All buses have started. Waiting for them to terminate...");
		busGroup.get(0).join();
		busGroup.get(1).join();
		busGroup.get(2).join();
		busGroup.get(3).join();
		busGroup.get(4).join();
		busGroup.get(5).join();
		System.out.println("Finally all done!");
	}
	
	/*
	 * Step 2 end-----------------------------------------------------
	 */
	
	public static void main(String[] args) throws InterruptedException {
		PubLauncher pl = new PubLauncher();
		pl.parsePubProperties();
		pl.start();
	}

}

