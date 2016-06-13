package jaws.data.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class that represents a connection and offers methods for reading from and writing to that connection.
 * 
 * @author Roy
 *
 */
public interface Connection {
	
	/**
	 * Returns a BufferedReader used to read from the connection.
	 * 
	 * @return a BufferedReader to read from the connection.
	 * @throws IOException
	 */
	public BufferedReader read() throws IOException;
	
	/**
	 * Writes the byte-array to the connection.
	 * 
	 * @param message the message to write to the connection.
	 * @throws IOException
	 */
	public void write(byte[] message) throws IOException;
	
	/**
	 * Writes the exact content of the Stream to the connection. This is important for transferring medias.
	 * 
	 * @param stream the stream to copy the content from.
	 * @throws IOException
	 */
	public void write(ByteArrayOutputStream stream) throws IOException;
}
