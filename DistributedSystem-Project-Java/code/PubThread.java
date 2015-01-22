import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

/*
 * This is the Bus class with implemented thread interface. 
 * Each PubThread represents a vehicle on a route. It receives the following
 * information before starting:
 *   The route and the vehicle it represents.
 *   The number of stops along the route.
 *   The time spent by the vehicle between two stops.
 * Once all PubThreads are created, they are started by the PubLauncher. At 
 * each stop, the thread publishes a position message and an accident message
 * depending on the situation.
 */

public class PubThread implements Runnable{
	
	private final static int looptimes = 3;       //the running loop times for a bus
	private final static int accidentcost = 10;   //the seconds for a bus to cost in case of an accident
		
	String route;                      //route's name on which the bus runs
	int numOfStops;                    //number of stops the bus should go
	int baseInterval;                      //the time interval between stops for a bus to run
	String busName;                    //the unique identifier for the bus
	String traffic;                    //the traffic condition in each stop
	int fillInRatio;                   //the number of passengers in the bus
	boolean accidentFlag;              //the accident indicator
	//Position position = new Position();//the position message 
	//PositionPublisher pp;              //the position publisher
	
	public PubThread(){		
	}
	
	public PubThread(String route, int numOfStops, int interval, String busName){
	//constructor with parsed route info from PubLauncher
		this.route = route;
		this.numOfStops = numOfStops;
		this.baseInterval = interval;
		this.busName = busName;
	}
	
	/*
	 * Step 1: three random number generators for traffic, fillinratio and accident
	 * And one interval calculator
	 */
	/*
	 * Step 1 start-----------------------------------------------------
	 */
	private float intervalCal(int baseInterval, String traffic, boolean accidentFlag){
		float interval = baseInterval;
		
		if(traffic.equals("heavy"))
			interval *= 1.25;
		else if(traffic.equals("light"))
			interval *= 0.75; 
		if(accidentFlag)
			interval += accidentcost;
		
		return interval;
	}
	private String trafficGenerator(){
	//three chances: normal with 50%, heavy and light with each 25%
	//so use r.nextInt(4) we have 25% for 0,1,2,3. 0->heavy, 1->light
	//and 2,3 together ->normal.
		String condition;
		Random r = new Random();
		int ran = r.nextInt(4);
		if(ran == 0)
			condition = "heavy";
		else if(ran == 1)
			condition = "light";
		else
			condition = "normal";	
		return condition;
	}
	
	private int fillInRatioGenerator(){
	//return a number of passengers in the bus, within [1, 100].
		Random r = new Random();
		return r.nextInt(100) + 1;
	}
	
	private boolean accidentGenerator(){
	//return the Boolean value of accident occurence. from [0,9] there is 10 
	//percent chance to encounter an accident.
		Random r = new Random();
		if(r.nextInt(10) == 9) //9 or any single digit in [0,9]
			return true;
		else
			return false;		
	}
	
	/*
	 * Step 1 end-----------------------------------------------------
	 */
	/*
	 * Step 2: the core run() method in which each bus will run 3 rounds.
	 * Its interval between buses is dynamically determined by both traffic
	 * condition and accident occurrence. Each interval passes in a sleep mode.
	 * Before the interval (bus stop) publish current message, including bus 
	 * name, stop #, route name, timestamp, traffic, accident (if any), passenger
	 * fill-in-ratio.   
	 * An assistant method printout to print messages out for test. 
	 */
	/*
	 * Step 2 start-----------------------------------------------------
	 */
	public void run() {
		//here put all the messages to be output by the bus thread in place
		for(int i = 0; i < looptimes; i++){
			int currentStop = 1;  //the beginning stop #
			while (currentStop <= numOfStops){
				//printout all messages here
				traffic = trafficGenerator();
				accidentFlag = accidentGenerator();
				//get the interval here from accident and traffic conditions
				float interval = intervalCal(baseInterval,traffic,accidentFlag);    //the actual interval 
				//fill out position message
				Position position = new Position();
				position.timestamp = DateFormat.getTimeInstance().format(new Date());
				position.route = route;
				position.vehicle = busName;
				position.stopNumber = currentStop;
				position.numStops = numOfStops;
				position.timeBetweenStops = interval;
				position.trafficConditions = traffic;
				position.fillInRatio = fillInRatioGenerator();
				System.out.println(position.vehicle + " has published a position message at stop #" + position.stopNumber + " on route " + position.route + " at " + position.timestamp);
				
				//position message ready!
				new PositionPublisher(position).publisherMain(0,1);
				//PositionPublisher ready
				//fill out accident message if accident flag is set
				if(accidentFlag) {
					Accident accident = new Accident();
					accident.timestamp = DateFormat.getTimeInstance().format(new Date());
					accident.route = route;
					accident.vehicle = busName;
					accident.stopNumber = currentStop;
					System.out.println(accident.vehicle + " has published an accident message at stop #" + accident.stopNumber + " on route " + accident.route + " at " + accident.timestamp);
					new AccidentPublisher(accident).publisherMain(0,1);
				}
				//printout(busName, currentStop, route, traffic, fillInRatioGenerator(), accidentFlag, interval);
				try {
					long sleepInterval = (long)(interval * 1000);
					Thread.sleep(sleepInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentStop ++;				
			}
		}
		
    }
	
	private static void printout(String busName, int currentStop, String route,
			String traffic, int passenger, boolean accident, double interval){
		String accidentInd = "No";
		if(accident)
			accidentInd = "Yes";
		System.out.format("%s at stop: %d on route: %s at time: %s with traffic: %s " +
				"& %d persons by %s accident & interval: %f.%n", busName, currentStop,
				route, DateFormat.getTimeInstance().format(new Date()), traffic, 
				passenger, accidentInd, interval);
	}
	/*
	 * Step 2 end-----------------------------------------------------
	 */
	
	public static void main(String[] args) {
		//System.out.println(new PubThread().trafficGenerator());
		//System.out.println(intervalCal(2, "heavy", false));
		new Thread(new PubThread("Express1", 4, 2, "Bus11")).start();
	}

}

