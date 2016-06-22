package jaws.data.net.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import jaws.data.net.Connection;
import jaws.data.net.SocketConnection;

public class SocketConnectionTest {
	
	private Object setUpLock;
	
	private int port;
	private Thread serverThread;
	private ServerSocket serverMockup;
	private Socket serverConnection;
	private Connection connection;
	
	@Before
	public void setUp() throws IOException {
		
		port = Thread.currentThread().hashCode() % (65536 - 2048) + 2048;
		
		setUpLock = new Object();
		
		serverThread = new Thread(() -> {
			try {
				synchronized(setUpLock) {
					serverMockup = new ServerSocket(port);
					serverConnection = serverMockup.accept();
				}
			} catch (Exception e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
		});
		serverThread.start();
		
		connection = new SocketConnection(new Socket("localhost", port));
		
		synchronized(setUpLock) {}
	}

	@Test
	public void testMessageRecieving() throws IOException {
		
		String messageToSend = "This is a test message to be sent over the Connection\n\n";
		OutputStream serverOut = serverConnection.getOutputStream();
		BufferedReader connectionIn = connection.read();
		
		serverOut.write(messageToSend.getBytes());
		serverOut.flush();
		
		StringBuilder recievedMessage = new StringBuilder();
		{
			String line;
			while(!(line = connectionIn.readLine()).isEmpty()) {
				recievedMessage.append(line);
			}
		}

		assertEquals(messageToSend.trim(), recievedMessage.toString()); // trim messageToSend because succeeding newlines were only used as end end-of-stream symbol
	}
	
	@Test
	public void testMessageSending() throws IOException {

		String messageToSend = "This is a test message to be sent over the Connection\n\n";
		BufferedReader serverIn = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
		
		connection.write(messageToSend.getBytes());
		
		StringBuilder recievedMessage = new StringBuilder();
		{
			String line;
			while(!(line = serverIn.readLine()).isEmpty()) {
				recievedMessage.append(line);
			}
		}
		
		assertEquals(messageToSend.trim(), recievedMessage.toString()); // trim messageToSend because succeeding newlines were only used as end end-of-stream symbol
	}
	
	@After
	public void tearDown() throws IOException {

		serverMockup.close();
		serverThread.interrupt();
	}
}
