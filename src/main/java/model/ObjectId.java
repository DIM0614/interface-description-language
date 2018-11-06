package model;

import java.util.UUID;

/**
 * Defines a valid (globally unique) identifier for each remote object instance, 
 * which is a 64-bit number value.
 * 
 * @author Yuri Alessandro Martins 
 * @version 1.0
 */
public class ObjectId {
	
	private UUID objectId;
	
	/**
	 * Construct new model.ObjectId
	 */
	public ObjectId() {
		this.objectId = UUID.randomUUID();
	}
	
	/**
	 * Construct new model.ObjectId from given String.
	 * @param seed	String seed for new model.ObjectId.

	 */
	public ObjectId(String seed) {
		this.objectId = UUID.fromString(seed);
	}
	
	/**
	 * Return the least significant 64 bits of this UUID's 128 bit value.
	 * @return Least significant 64 bits.
	 */
	public long getObjectId() {
		return this.objectId.getLeastSignificantBits();
	}
}
