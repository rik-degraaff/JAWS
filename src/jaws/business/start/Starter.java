package jaws.business.start;
import jaws.data.net.PortListener;

public class Starter {

	public static void main(String[] args) {
		
		PortListener portListener = new PortListener(80);
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				portListener.stop();
			}
		}));
	}
}
