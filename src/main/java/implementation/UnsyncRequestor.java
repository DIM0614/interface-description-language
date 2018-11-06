package implementation;

import exception.RemoteError;
import interfaces.ClientRequestHandler;
import interfaces.Marshaller;
import interfaces.Requestor;
import model.AbsoluteObjectReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Unsynchronized requestor to be used for
 * requestors instantiated per request.
 * When one needs one requestor for all requests,
 * a synchronized version must be used.
 *
 * @author vitorgreati
 */
public class UnsyncRequestor implements Requestor {

    private Marshaller marshaller;

    private ClientRequestHandler clientRequestHandler;

    public UnsyncRequestor() {
    	this.marshaller = new JavaMarshaller();
    	this.clientRequestHandler = ClientRequestHandlerImpl.getInstance();
    }

    public UnsyncRequestor(Marshaller marshaller) {
    	this.marshaller = marshaller;
		this.clientRequestHandler = ClientRequestHandlerImpl.getInstance();
	}

	public Object request(AbsoluteObjectReference aor, String operationName, Object... parameters) throws RemoteError, IOException, ClassNotFoundException {

		InvocationData invocationData = new InvocationData(aor, operationName, parameters);

		Invocation invocation = new Invocation(invocationData);

		ByteArrayOutputStream outputStream = this.marshaller.marshal(invocation);

		ByteArrayInputStream inputStream = this.clientRequestHandler.send(aor.getHost(), aor.getPort(), outputStream);

		Object returnValue = this.marshaller.unmarshal(inputStream, Object.class);

		return returnValue;
	}

}
