package jaws.data.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface Connection {
	
	public BufferedReader read() throws IOException;
	public void write(byte[] message) throws IOException;
	public void write(ByteArrayOutputStream stream) throws IOException;
}
