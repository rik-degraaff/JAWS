package jaws.data.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class PortListener implements Runnable {

	private ServerSocket server;
	private int port;
	private Consumer<Connection> connectionHandler;

	public PortListener(int port, Consumer<Connection> connectionHandler) {

		this.port = port;
		this.connectionHandler = connectionHandler;
	}

	@Override
	public void run() {

		try {

			server = new ServerSocket(port);
			while (true) {

				System.out.println("waiting for requests");
				Socket socket = server.accept();
				Connection client = new SocketConnection(socket);
				connectionHandler.accept(client);
//				final RequestProcessor handler = new RequestProcessor(ModuleLoader.getHandlerGetter());
//				threadPool.execute(() -> handler.handle(client));
			}
		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			System.out.println("entered finally clause");
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
