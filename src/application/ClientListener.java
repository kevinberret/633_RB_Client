package application;

import java.net.ServerSocket;

/**
 * This class is used to listen to client and open connections with clients that want files that we share
 * @author Daniel
 *
 */
public class ClientListener implements Runnable {
	/**
	 * The server socket that will transform our client as a server
	 */
	private ServerSocket listenSocket = null;
	
	/**
	 * The port listened by server socket
	 */
	private int clientAsServerPort;
	
	/**
	 * The folder shared by the client
	 */
	private String folder;
	
	/**
	 * Default constructor
	 * @param clientAsServerPort The port on which the client will listen
	 * @param folder The folder to share
	 */
	public ClientListener(int clientAsServerPort, String folder) {
		this.clientAsServerPort = clientAsServerPort;
		this.folder = folder;
	}	
	
	/**
	 * Returns the listening socket
	 * @return The socket
	 */
	public ServerSocket getListenSocket() {
		return listenSocket;
	}
	
	@Override
	public void run() {
		try {
			// Instantiate the serversocket
			listenSocket = new ServerSocket(clientAsServerPort);

			while(true){
				// Opening a socket with a client and begin a thread for their file exchange
				ClientSender cs = new ClientSender(listenSocket.accept(), folder);
				
				Thread t = new Thread(cs);
				t.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}