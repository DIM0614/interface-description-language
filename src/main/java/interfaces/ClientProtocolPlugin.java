package interfaces;

import exception.RemoteError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Represents the connection protocol to the Client Request Handler,
 * which handles networking communication
 *  
 * @author victoragnez
 */

public interface ClientProtocolPlugin {
	
	/**
	 * Function used by the requestor and delegated by the client handler to send the data,
	 * handling network communication
	 *  
	 * @param host the hostname to send the data
	 * @param port the port to be used
	 * @param msg the data to be sent
	 * @return the server reply
	 */
	public ByteArrayInputStream send(String host, int port, ByteArrayOutputStream msg) throws RemoteError;
	
	/**
	 * Shutdown the plug-in. Called when Client Request Handler changes the protocol plug-in
	 * @throws RemoteError if any error occurs
	 */
	public void shutdown() throws RemoteError;
}
