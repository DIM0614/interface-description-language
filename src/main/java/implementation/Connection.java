package implementation;

import exception.RemoteError;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
	private final Socket socket;
	private final DataOutputStream outToServer;
	private final DataInputStream inFromServer;
	private long currentDeathTime = 0L;
	private boolean used = false;
	
	public Connection(String host, int port) throws RemoteError {
		try {
			this.socket = new Socket(host, port);
			this.outToServer = new DataOutputStream(socket.getOutputStream());
			this.inFromServer = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			throw new RemoteError(e);
		}
	}
	
	/**
	 * Gets the socket instance
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}
	
	/**
	 * Closes the socket
	 */
	public synchronized void close() throws IOException {
		if(!socket.isClosed())
			socket.close();
	}
	
	/**
	 * Check if it's been used
	 * @return true if its used
	 */
	public boolean isUsed() {
		return used;
	}
	
	/**
	 * Asks to use the connection
	 * @return true if it was available and false if it's already in use
	 */
	public synchronized boolean use() {
		if(isUsed()) {
			return false;
		}
		used = true;
		return true;
	}
	
	/**
	 * Shows that this connection is available now
	 */
	public synchronized void finish() {
		used = false;
	}

	/**
	 * @return the currentDeathTime
	 */
	public long getCurrentDeathTime() {
		return currentDeathTime;
	}

	/**
	 * @param currentDeathTime the currentDeathTime to set
	 */
	public void setCurrentDeathTime(long currentDeathTime) {
		this.currentDeathTime = currentDeathTime;
	}
	
	/**
	 * gets the socket's outputStream
	 * @return the socket's outputStream
	 */
	public DataOutputStream getOutput() {
		return outToServer;
	}
	
	/**
	 * gets the socket's inputStream
	 * @return the socket's inputStream
	 */
	public DataInputStream getInput() {
		return inFromServer;
	}
}
