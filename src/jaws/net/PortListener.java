package jaws.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jaws.module.ModuleLoader;
import jaws.net.util.Connection;
import jaws.net.util.RequestHandler;

public class PortListener {
	
	RequestHandler handler;

	public PortListener(int port) {
		
		handler = new RequestHandler(ModuleLoader.getHandlerGetter());
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port);
			while (true) {
				
				Socket socket = server.accept();
				Connection client = new Connection(socket);
				handler.handle(client);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
			
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
