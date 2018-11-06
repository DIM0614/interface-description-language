package model;

/**
 * This class describes a remote absolute reference that uniquely identifies a 
 * remote object, holding information such as its id, INVOKATOR identifier, and 
 * network information: host and port.
 * 
 * @author Yuri Alessandro Martins
 * @version 1.0
 * @see ObjectId
 */
public class AbsoluteObjectReference {
	private ObjectId objectId;
	private String host;
	private int port;
	private int invokerId;
	
	/**
	 * Creates the unique identifier for remote objects: AOR
	 * @param objectId	The remote object's object id
	 * @param host		Host of the network
	 * @param port		Port of the network
	 * @param invokerId	Invoker unique identification
	 */
	public AbsoluteObjectReference(ObjectId objectId, String host, int port,
								   int invokerId) {
		this.objectId = objectId;
		this.host = host;
		this.port = port;
		this.invokerId = invokerId;
	}
	
	/**
	 * 
	 * @return AOR model.ObjectId
	 */
	public ObjectId getObjectId() {
		return objectId;
	}
	
	/**
	 * 
	 * @return AOR network host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * 
	 * @return AOR network port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * 
	 * @return	AOR invoker identification
	 */
	public int getInvokerId() {
		return invokerId;
	}
	
}
