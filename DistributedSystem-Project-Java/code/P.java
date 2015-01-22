public class P {
    public String timestamp;
    public String route;
    public String vehicle;
    public int stopNumber;
    public int numStops;
    public float timeBetweenStops;
    public String trafficConditions;
    public int fillInRatio;
    public int remainingStops;
    /*public int routeNumber;
    public int sourceStop;
    public int destinationStop;*/

    public P() {
    }
    public P(String timestamp, String route, String vehicle, int stopNumber, int numStops, float timeBetweenStops, String trafficConditions, int fillInRatio, int remainingStops) {
	this.timestamp = timestamp;
	this.route = route;
	this.vehicle = vehicle;
	this.stopNumber = stopNumber;
	this.numStops = numStops;
	this.timeBetweenStops = timeBetweenStops;
	this.trafficConditions = trafficConditions;
	this.fillInRatio = fillInRatio;
	this.remainingStops = remainingStops;
	/*this.routeNumber = routeNumber;
	this.sourceStop = sourceStop;
	this.destinationStop = destinationStop;*/
    }
}
