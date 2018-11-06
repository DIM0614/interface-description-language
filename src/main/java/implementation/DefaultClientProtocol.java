package implementation;

import exception.RemoteError;
import interfaces.ClientProtocolPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Represents the default protocol to the Client Request Handler,
 * which handles networking synchronous communication
 * inside client applications
 * 
 * Allows to set limit the number of threads connecting to server and
 * the time which connections will be cached
 * 
 * @author victoragnez
 */
public class DefaultClientProtocol implements ClientProtocolPlugin {
	
	/**
	 * ExecutorService to limit number of threads connecting to server
	 */
	private final ExecutorService tasksExecutor;
	
	/**
	 * Maps the address to the available connections
	 */
	private final Map<String, Queue<Connection> > cache = new ConcurrentHashMap<String, Queue<Connection> >();
	
	/**
	 * Queue with available connections to close the old unused ones.
	 */
	private final Queue<WrappedConnection> oldConnections = new ConcurrentLinkedQueue<WrappedConnection>();
	
	/**
	 * Maximum time a connection can be alive and not used
	 */
	private final long timeLimit;
	
	/**
	 * Avoids racing when adding a new queue to the cache
	 */
	private final WrappedPut synchronizedPut = new WrappedPut();
	
	/**
	 * Default constructor with maximum number of threads set to 1000
	 */
	public DefaultClientProtocol() {
		this(1000);
	}
	
	/**
	 * Creates the client protocol with maximum number of threads and
	 * sets time limit of caching connections to 10s
	 * @param maxConnections maximum number of threads
	 */
	public DefaultClientProtocol(int maxConnections) {
		this(maxConnections, 10000000L);
	}
	
	/**
	 * Creates the client protocol with maximum number of threads and
	 * the time limit (in milliseconds) of keeping connections alive to cache
	 * @param maxConnections maximum number of threads
	 */
	public DefaultClientProtocol(int maxConnections, long timeLimit) {
		if(timeLimit < 0) {
			throw new IllegalArgumentException("timeLimit cannot be negative, got " + timeLimit);
		}
		if(maxConnections <= 0) {
			throw new IllegalArgumentException("maxConnections must be positive, got " + maxConnections);
		}
		tasksExecutor = Executors.newFixedThreadPool(maxConnections + 1);
		tasksExecutor.submit(() -> deleteOldConnections());
		this.timeLimit = timeLimit; 
	}
	
	/*
	 * Delete connections that have not been used after timeLimit milliseconds
	 */
	private void deleteOldConnections() {
		while(true) {
			WrappedConnection w = oldConnections.peek();
			if(w == null) {
				try {
					Thread.sleep(timeLimit);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
				continue;
			}
			long now = System.currentTimeMillis();
			if(w.getDeathTime() < now) {
				w = oldConnections.poll();
				if(w != null) {
					Connection con = w.getConnection();
					if(!con.isUsed() && con.getCurrentDeathTime() < now) {
						if(!con.use()) {
							continue;
						}
						try {
							con.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				try {
					Thread.sleep(w.getDeathTime() - now);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		}
	}

	/**
	 * Sends the data using TCP protocol
	 */
	@Override
	public ByteArrayInputStream send(String host, int port, ByteArrayOutputStream msg) throws RemoteError {
		try {
			return tasksExecutor.submit(() -> sendAndCache(host, port, msg) ).get();
		} catch (InterruptedException | ExecutionException e1) {
			throw new RemoteError(e1);
		}
	}
	
	/**
	 * Sends the data using a cached connection if available, and caches after
	 * sending and receiving the server reply
	 * 
	 * @param host the host to send the data
	 * @param port the port to send the data
	 * @param msg the message to be sent
	 * @return the server reply
	 * @throws RemoteError if any error occur
	 */
	private ByteArrayInputStream sendAndCache(String host, int port, ByteArrayOutputStream msg) throws RemoteError {
		Connection con = null;
		String fullAddr = host + ":" + port;
		
		while(cache.get(fullAddr) != null && (con = cache.get(fullAddr).poll()) != null) {
			if(!con.use()) {
				con = null;
				continue;
			}
		}
		
		if(con == null) {
			con = new Connection(host, port);
			con.use();
		}
		
		DataOutputStream outToServer = con.getOutput();
		DataInputStream inFromServer = con.getInput();
		ByteArrayInputStream ret;
		
		byte[] byteMsg = msg.toByteArray();
		
		try {
			outToServer.writeInt(byteMsg.length);
			outToServer.write(byteMsg);
			
			int length = inFromServer.readInt();
			byte[] byteAns = new byte[length];
			
			inFromServer.readFully(byteAns, 0, byteAns.length);
			ret = new ByteArrayInputStream(byteAns);
			
		} catch (IOException e) {
			throw new RemoteError(e);
		}
		
		if(cache.get(fullAddr) == null)
			synchronizedPut.put(fullAddr);
		
		long newDeathTime = System.currentTimeMillis() + timeLimit;

		con.finish();
		con.setCurrentDeathTime(newDeathTime);
		cache.get(fullAddr).add(con);
		
		WrappedConnection w = new WrappedConnection(newDeathTime, con);
		oldConnections.add(w);
		
		return ret;
	}
	
	/**
	 * Stop all threads and closes sockets
	 * @throws RemoteError exception if any error occurs
	 */
	public void shutdown() throws RemoteError {
		tasksExecutor.shutdownNow();
		while(!oldConnections.isEmpty()) {
			WrappedConnection current = oldConnections.poll();
			if(current != null) {
				try {
					current.getConnection().close();
				} catch (IOException e) {
					throw new RemoteError(e);
				}
			}
		}
	}
	
	/**
	 * Wraps connections with deathTime
	 * 
	 * @author victoragnez
	 *
	 */
	private static class WrappedConnection {
		private final long deathTime;
		private final Connection connection;
		
		public WrappedConnection(long deathTime, Connection connection) {
			this.deathTime = deathTime;
			this.connection = connection;
		}
		
		public long getDeathTime() {
			return deathTime;
		}
		
		public Connection getConnection() {
			return connection;
		}
	}
	
	/**
	 * Wraps the put method to be synchronized
	 * @author victoragnez
	 */
	private class WrappedPut {
		public synchronized void put(String key) {
			if(cache.get(key) == null)
				cache.put(key, new ConcurrentLinkedQueue<Connection>());
		}
	}
	
	/**
	 * Trivial way to send the data, which doesn't create new threads nor cache connection
	 * 
	 * @param host the hostname to send the data
	 * @param port the port to be used
	 * @param msg the data to be sent
	 * @return the server reply
	 * @throws RemoteError 
	 */
	@Deprecated
	public ByteArrayInputStream singleSocketSend(String host, int port, ByteArrayOutputStream msg) throws RemoteError {
		
		byte[] byteMsg = msg.toByteArray();

		try {
			Socket client;
			DataOutputStream outToServer;
			DataInputStream inFromServer;
						
			client = new Socket(host, port);
			outToServer = new DataOutputStream(client.getOutputStream());
			inFromServer = new DataInputStream(client.getInputStream());
			
			outToServer.writeInt(byteMsg.length);
			outToServer.write(byteMsg);
			
			int length = inFromServer.readInt();
			byte[] byteAns = new byte[length];
			
			inFromServer.readFully(byteAns, 0, byteAns.length);
			ByteArrayInputStream ret = new ByteArrayInputStream(byteAns);
			
			client.close();
			
			return ret;
			
		} catch (IOException e1 ) {
			throw new RemoteError(e1);
		}
	}
}
