package jaws.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import jaws.module.ModuleLoader;
import jaws.net.util.Connection;
import jaws.net.util.RequestHandler;
import jaws.thread.ThreadPool;

public class PortListener {

	public PortListener(int port) {
		
		ThreadPool requestHandlers = new ThreadPool(5);
		ServerSocket server = null;
		
		try {
			server = new ServerSocket(port);
			while (true) {
				
				Socket socket = server.accept();
				Connection client = new Connection(socket);
				final RequestHandler handler = new RequestHandler(ModuleLoader.getHandlerGetter());
				requestHandlers.execute(() -> handler.handle(client));
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (InterruptedException e) {
			
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
