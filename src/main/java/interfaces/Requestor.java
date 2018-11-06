package interfaces;

import exception.RemoteError;
import model.AbsoluteObjectReference;

import java.io.IOException;

/**
 * Represents a UnsyncRequestor, which abstracts
 * the network and interacts directly with
 * the request handlers and possibly the client.
 * 
 * @author vitorgreati
 */
public interface Requestor {

	/**
	 * Acquires the Absolute Object Reference via naming lookup,
	 * invokes the Marshaller and sends the bytes
	 * via the Client Request Handler.
	 * 
	 * @return the return of the invoked operation
	 */
	Object request(AbsoluteObjectReference aor, String operationName, Object ... parameters) throws RemoteError, IOException, ClassNotFoundException;
	
}
