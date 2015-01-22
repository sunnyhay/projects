/*import java.net.InetAddress;
import java.net.UnknownHostException;
import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;
import java.io.IOException;
*/

public class TestPositionRun{
	public static void main(String[] args){
		Position p = new Position();
		p.route = "Express1";
		p.vehicle = "Bus21";
		p.stopNumber = 2;
		p.numStops = 4;
		p.timeBetweenStops = 2;
		p.trafficConditions = "heavy";
		p.fillInRatio = 39;
		new PositionPublisher(p).publisherMain(0,20);
		//new Thread(new PubThread("Express1", 4, 2, "Bus11")).start();
	}
}
