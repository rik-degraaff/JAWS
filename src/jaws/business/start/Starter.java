package jaws.business.start;
import jaws.business.init.WebInitializer;

public class Starter {

	private static final String configFile = "../config";

	public static void main(String[] args) {

//		PortListener portListener = new PortListener(80);
//
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//			public void run() {
//				portListener.stop();
//			}
//		}));

		WebInitializer.init(configFile);
	}
}
