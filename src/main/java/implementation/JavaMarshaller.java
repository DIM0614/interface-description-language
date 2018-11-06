package implementation;

import interfaces.Marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * Marshaller that simply serializes (marshal)
 * and deserializes (unmarshal) java objects.
 * 
 * @author carlosemv
 */
public class JavaMarshaller implements Marshaller {
	/**
	 * Uses ObjectOutputStream to serialize an Object into a byte stream.
	 * 
	 * @param object an Object that must implement the Java.io.Serializable interface
	 */
	public ByteArrayOutputStream marshal(Object object) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
		objectStream.writeObject(object);
		
		return byteStream;
	}

	/**
	 * Uses ObjectInputStream to deserialize a byte stream into an Object
	 * 
	 * @param byteStream a byte stream that represents a serialized instance of a known class
	 * @param tgtClass is not used, result is cast to Object
	 */
	public <T> Object unmarshal(ByteArrayInputStream byteStream, Class<T> tgtClass) throws IOException, ClassNotFoundException {
		ObjectInputStream objectStream = new ObjectInputStream(byteStream);
		Object obj = objectStream.readObject();
		return obj;
	}
}
