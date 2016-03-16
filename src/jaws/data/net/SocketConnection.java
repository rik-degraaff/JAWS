package jaws.data.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

class SocketConnection implements Connection {

	private Socket socket;
	
	public SocketConnection(Socket socket) {
		
		this.socket = socket;
	}
	
	@Override
	public BufferedReader read() throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return in;
	}
	
	@Override
	public void write(byte[] message) throws IOException {
		
		OutputStream out = socket.getOutputStream();
		out.write(message);
		out.flush();
		out.close();
	}
	
	@Override
	public void write(ByteArrayOutputStream stream) throws IOException {
		
		OutputStream out = socket.getOutputStream();
		stream.writeTo(out);
		out.flush();
		out.close();
	}
}
