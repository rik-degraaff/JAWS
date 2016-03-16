package jaws.business.start;
import jaws.data.net.PortListener;

public class Starter {

	public static void main(String[] args) {
		
		new PortListener(80);
	}
}
