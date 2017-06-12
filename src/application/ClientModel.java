package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Properties;
import java.util.UUID;

/**
 * This class contains all data values
 * @author Kevin
 *
 */
public class ClientModel extends Observable{
	
	/**
	 * client's uuid
	 */
	private String uuid;
	
	/**
	 * server's ip address
	 */
	private String serverIP;
	
	/**
	 * default server's ip address
	 */
	private String defaultServerIP = new String("192.168.108.10");
	
	/**
	 * client's ip address
	 */
	private String clientIp;
	
	/**
	 * server's port
	 */
	private int serverPort;
	
	/**
	 * default server's port
	 */
	private int defaultServerPort = 45006;
	
	/**
	 * port used by the client when it has to send files
	 */
	private int clientAsServerPort;
	
	/**
	 * default port used by the client when it has to send files
	 */
	private int defaultClientAsServerPort = 5600;
	
	/**
	 * the files shared list
	 */
	private ArrayList<String> filesList;
	
	/**
	 * the files shared array
	 */
	private File[] files;
	
	/**
	 * server's ip address
	 */
	private InetAddress serverAddress;
	
	/**
	 * socket for connection with server
	 */
	private Socket mySocket;
	
	/**
	 * used to send objects through socket
	 */
	private ObjectOutputStream objectOutput;
	
	/**
	 * folder shared
	 */
	private String folder;
	
	/**
	 * timeout for connection with server
	 */
	private int clientTimeOut;
	
	/**
	 * default timeout for connection with server
	 */
	private int defaultClientTimeOut = 5000;
	
	/**
	 * settings
	 */
	private Properties properties;
	
	/**
	 * current download progress
	 */
	private int currentProgress;
	
	/**
	 * current downloaded filename
	 */
	private String fileName;
	
	/**
	 * this client
	 */
	private Client thisClient;
	
	
	/**
	 * Default constructor
	 */
	public ClientModel() {
		// Get application settings
		getResources();		
		
		// Create this client object
		thisClient = new Client(uuid);
	}
	
	/**
	 * get client's ip address
	 * @return client's ip address
	 */
	public String getClientIP() {
		return clientIp;
	}	
	
	/**
	 * get client's uuid
	 * @return client's uuid
	 */
	public String getUuid() {
		return uuid;
	}

	
	/**
	 * get port used by client when sends files
	 * @return port used by client when sends files
	 */
	public int getClientAsServerPort() {
		return clientAsServerPort;
	}

	public LinkedHashMap<String, Client> getClientsList() {
		return getClientFiles();
	}
	
	public String getFolder() {
		return folder;
	}
	
	public String getServerIP() {
		return serverIP;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public int getClientTimeOut() {
		return clientTimeOut;
	}
	
	public Socket getMySocket() {
		return mySocket;
	}
	
	public Client getThisClient() {
		return thisClient;
	}
	
	/*
	 * SETTERS
	 */

	public void setClientIP(String ip) {
		clientIp = ip;
		thisClient.setClientIp(ip);
	}	

	public void setServerIP(String serverName) {
		properties.setProperty("server.ip", serverName);
		this.serverIP = serverName;
	}	

	public void setServerPort(int serverPort) {
		properties.setProperty("server.port", Integer.toString(serverPort));
		this.serverPort = serverPort;
	}	

	public void setClientTimeOut(int clientTimeOut) {
		properties.setProperty("client.timeout", Integer.toString(clientTimeOut));
		this.clientTimeOut = clientTimeOut;
	}

	public void setClientAsServerPort(int clientAsServerPort) {
		properties.setProperty("client.asserver.port", Integer.toString(clientAsServerPort));		
		this.clientAsServerPort = clientAsServerPort;
	}

	/**
	 * This method get settings from config file
	 */
	private void getResources(){
		// source : http://www.codejava.net/coding/reading-and-writing-configuration-for-java-application-using-properties-class#CreatePropertie
		File configFile;		 
		FileReader fr;
		properties = new Properties();
		
		try {
			configFile = new File("config.properties");
			fr = new FileReader(configFile);			
			
			// load settings from file
			properties.load(fr);
			
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Set application settings
		serverIP = properties.getProperty("server.ip", defaultServerIP);
		serverPort = Integer.parseInt(properties.getProperty("server.port", String.valueOf(defaultServerPort)));
		clientTimeOut = Integer.parseInt(properties.getProperty("client.timeout", String.valueOf(defaultClientTimeOut)));
		clientAsServerPort = Integer.parseInt(properties.getProperty("client.asserver.port", String.valueOf(defaultClientAsServerPort)));		
		
		// Get client's uuid
		uuid = properties.getProperty("client.uuid");
		
		// if no uuid, generate one and save it into config
		if(uuid == null){
			uuid = UUID.randomUUID().toString();
			properties.setProperty("client.uuid", uuid);
			saveSettings();
		}
	}
	
	/**
	 * This method saves settings into config.properties
	 * @return true if save ok, false if not
	 */
	public boolean saveSettings(){
		// create config file
		File configFile = new File("config.properties");		
		FileWriter fw;
		
		// write data in file
		try {
			fw = new FileWriter(configFile);
			properties.store(fw, "application settings");
			fw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// errors
		return false;
	}
	
	/**
	 * Set the selected folder
	 * @param selectedFolder The chosen folder to set
	 * @return true if contains files, false if not
	 */
	public boolean selectFolder(String selectedFolder){
		folder = selectedFolder;
    	files = new File(selectedFolder).listFiles();
    	
    	if(files.length > 0)
    		return true;
    	
    	return false;
	}
	
	/**
	 * This method connects the client to the server
	 * @return true if connection worked, false if not
	 */
	public boolean connectToServer(){		
		try {			
			serverAddress = InetAddress.getByName(serverIP);

			// Try to connect to server
			mySocket = new Socket();
			mySocket.connect(new InetSocketAddress(serverAddress, serverPort), clientTimeOut);
			
			// set client files list
			filesList = new ArrayList<String>();
			for (File file : files) {
				if(!file.isDirectory())
					filesList.add(file.getName());
			}			
			thisClient.setFiles(filesList);
			
			// set client's ip
			thisClient.setClientIp(mySocket.getLocalAddress().toString().substring(1));
			
			// Objectoutputstream to send data through socket
			objectOutput = new ObjectOutputStream(mySocket.getOutputStream());
			
			// tell the server we want to register
			objectOutput.writeObject(new String("registration"));
			objectOutput.flush();
			
			// send this client representation
			objectOutput.writeObject(thisClient);
			objectOutput.flush();
			
			// connection established
			return true;
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}

		// error
		return false;
	}
	
	/**
	 * This method get the clients
	 * @return a linkedhashmap of all clients
	 */
	public LinkedHashMap<String, Client> getClientFiles(){
		try {
			// Tell the server we want to get all clients and their files
			objectOutput.writeObject(new String("getfiles"));
			objectOutput.flush();
			
			// get the linkedhashmap from server
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			return (LinkedHashMap<String, Client>) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// errors
		return null;
	}
	
	/**
	 * Get a client by his uuid
	 * @param uuid The client's uuid
	 * @return The client
	 */
	public Client getClientByUuid(String uuid){
		try {
			// tell the server we want a specific client by uuid and send the uuid
			objectOutput.writeObject(new String("getclientbyuuid"));
			objectOutput.flush();
			
			objectOutput.writeObject(uuid);
			objectOutput.flush();
			
			// get the client
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			return (Client) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// error
		return null;
	}
	
	/**
	 * This method allows to disconnect from server
	 */
	public void disconnectFromServer(){
		if(objectOutput != null){
			try {
				// Tell the server we want to quit the connection
				objectOutput.writeObject(new String("quit"));
				objectOutput.flush();
				
				objectOutput.writeObject(uuid);
				objectOutput.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method allows to get the current download progress
	 * @return the current download progress
	 */
	public int getCurrentProgress() {
		return currentProgress;
	}
	
	/**
	 * This method allows to set the current download progress and notify observers of download progress modification
	 * @param currentProgress The current progress
	 */
	public void setCurrentProgress(int currentProgress){
		this.currentProgress = currentProgress;
		setChanged();
		notifyObservers();
	}

	/**
	 * This method allows to set the current download filename and notify observers of downloaded filename
	 * @param fileName The current filename
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		setChanged();
		notifyObservers();
	}
	
	/**
	 * This method allows to get the current downloaded file name
	 * @return the current downloaded file name
	 */
	public String getFileName() {
		return fileName;
	}	
}