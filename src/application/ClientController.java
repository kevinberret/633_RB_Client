package application;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This class is the business part of the application. It communicates with the view and the model and execute checks and make actions according to
 * team
 * @author Kevin
 *
 */
public class ClientController {
	/**
	 * Client model object
	 */
	private ClientModel model;
	
	/**
	 * This client receiver object is used to receive files
	 */
	private ClientReceiver clientReceiver;
	
	/**
	 * This object is used to listen for clients asking for files
	 */
	private ClientListener clientListener;
	
	/**
	 * Default constructor
	 * @param model The model containing data necessary for this application
	 */
	public ClientController(ClientModel model) {
		this.model = model;
	}

	/**
	 * Returns the client's ip address
	 * @return Client's ip address as String
	 */
	public String getClientIP() {
		return model.getClientIP();
	}
	
	/**
	 * Returns the server's ip address
	 * @return Server's ip address as String
	 */
	public String getServerIP() {
		return model.getServerIP();
	}
	
	/**
	 * Returns the server's port
	 * @return Server's port as String
	 */
	public String getServerPort() {
		return Integer.toString(model.getServerPort());
	}
	
	/**
	 * Returns client's timeout
	 * @return Client's tiemout as String
	 */
	public String getClientTimeOut() {
		return Integer.toString(model.getClientTimeOut());
	}	
	
	/**
	 * Returns port used by client when it's a server that send files
	 * @return Port as String
	 */
	public String getClientAsServerPort() {
		return Integer.toString(model.getClientAsServerPort());
	}
	
	/**
	 * Gets the clients connected to the server
	 * @return Clients list
	 */
	public LinkedHashMap<String, Client> getClientsList(){
		// get clients
		LinkedHashMap<String, Client> clients = model.getClientsList();
		
		// remove us from list
		clients.remove(model.getUuid());
		
		// return all clients except us
		return clients;
	}
	
	/**
	 * Returns this client object
	 * @return This client
	 */
	public Client getThisClient(){
		return model.getThisClient();
	}
	
	/**
	 * Sets the client's ip address
	 * @param ip The ip address to set as String
	 */
	public void setClientIp(String ip){
		model.setClientIP(ip);
	}

	/**
	 * Set the server's ip address
	 * @param ip The ip address to set as String
	 */
	public void setServerIP(String ip) {
		model.setServerIP(ip);
	}	

	/**
	 * Set the port to use to open a connection with the server
	 * @param serverPort The port as a String
	 */
	public void setServerPort(String serverPort) {
		model.setServerPort(Integer.parseInt(serverPort));
	}	

	/**
	 * Set the timeout for connection with servers
	 * @param clientTimeOut The timeout as String
	 */
	public void setClientTimeOut(String clientTimeOut) {
		model.setClientTimeOut(Integer.parseInt(clientTimeOut));
	}

	/**
	 * Set the port used by client when it's a server that send files
	 * @param clientAsServerPort The port as String
	 */
	public void setClientAsServerPort(String clientAsServerPort) {		
		model.setClientAsServerPort(Integer.parseInt(clientAsServerPort));
	}
	
	/**
	 * This method save the settings
	 * @return true if it worked, false if not
	 */
	public boolean saveSettings(){
		return model.saveSettings();
	}
	
	/**
	 * This method sets the folder as sharing folder and opens the connection with the server and then opens a thread that listens for other clients connections
	 * @param selectedFolder
	 * @return
	 */
	public boolean selectFolder(String selectedFolder){
		// Check if folder exists and is not null
		if(selectedFolder != null && !selectedFolder.isEmpty())
			// connection to server if selection possible
			if(model.selectFolder(selectedFolder))
				if(model.connectToServer()){
					// open the threads and validate selection of folder
					clientListener = new ClientListener(model.getClientAsServerPort(), model.getFolder());
					Thread t = new Thread(clientListener);
					t.start();
					return true;
				}
		
		// errors
		return false;					
	}
	
	/**
	 * This method opens a connection with another client and asks for desired files
	 * @param serverAddress The other client's IP address
	 * @param files The desired files
	 * @return true if reception started, false if an error occured
	 */
	public boolean getFiles(String serverAddress, List<String> files){
		try {
			// Initialise the socket with the other client
			Socket clientSocket = new Socket(serverAddress, model.getClientAsServerPort());
			
			// Create and start file receiving with the other client
			clientReceiver = new ClientReceiver(clientSocket, files, model);
			Thread threadReceiver = new Thread(clientReceiver);
			threadReceiver.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Errors
		return false;
	}

	/**
	 * This method close connections with other clients and server
	 */
	public void closeConnections() {
		try {			
			// Closing connection with clients
			if(clientReceiver != null && clientReceiver.getClientSocket() != null)
				clientReceiver.getClientSocket().close();
			if(clientListener != null && clientListener.getListenSocket() != null)
				clientListener.getListenSocket().close();
			
			// Closing connection with server
			if(model != null && model.getMySocket() != null){
				model.disconnectFromServer();
				model.getMySocket().close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method gets a client by his uuid
	 * @param uuid The unique identifier of a client as String
	 * @return The desired client
	 */
	public Client getClientByUuid(String uuid) {		
		return model.getClientByUuid(uuid);
	}
}