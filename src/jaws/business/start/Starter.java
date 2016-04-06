package jaws.business.start;
import jaws.business.init.WebInitializer;

public class Starter {

	private static final String configFile = "../config";

	public static void main(String[] args) {
		
		WebInitializer.init(configFile);
	}
}
