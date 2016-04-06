package jaws.data.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class PortListener {

	public PortListener(int port, Consumer<Connection> connectionHandler) {

		ServerSocket server = null;


		try {

			server = new ServerSocket(port);
			while (true) {

				Socket socket = server.accept();
				Connection client = new SocketConnection(socket);
				connectionHandler.accept(client);
//				final RequestProcessor handler = new RequestProcessor(ModuleLoader.getHandlerGetter());
//				threadPool.execute(() -> handler.handle(client));
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
