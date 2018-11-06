package interfaces;

import exception.RemoteError;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public interface ClientRequestHandler {

	/**
	 * default port 22334
	 */
	int defaultPort = 22334;

	/**
	 * Function used by the requestor to send the data,
	 * using the specific protocol
	 *  
	 * @param host the hostname to send the data
	 * @param port the port to be used
	 * @param msg the data to be sent
	 * @return the server reply
	 */
	ByteArrayInputStream send(String host, int port, ByteArrayOutputStream msg) throws RemoteError;

	/**
	 * @return the protocol
	 */
	ClientProtocolPlugin getProtocol();

	/**
	 * @param protocol the protocol to set
	 * @throws RemoteError if any error occurs
	 */
	void setProtocol(ClientProtocolPlugin protocol) throws RemoteError;

}