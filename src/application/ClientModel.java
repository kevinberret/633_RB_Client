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
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Properties;

public class ClientModel extends Observable{
	private String serverName;
	private String clientName;
	private int serverPort;
	private int clientAsServerPort;
	private ArrayList<String> data;
	private File[] files;
	private InetAddress serverAddress;
	private Socket mySocket;
	private ObjectOutputStream objectOutput;
	private ArrayList<Object> clientsList;
	private String folder;
	private int clientTimeOut;
	private ArrayList<String> networks;
	private Properties props;
	
	private int currentProgress;
	
	public ClientModel() {
		// Get application settings
		getResources();
		
		// Get all network interfaces
		getNetworkInterfaces();
	}
	
	/*
	 * GETTERS
	 */
	public String getClientName() {
		return clientName;
	}
	
	public int getClientAsServerPort() {
		return clientAsServerPort;
	}

	public ArrayList<Object> getClientsList() {
		if (clientsList == null)
			return getClientFiles();
		
		return clientsList;
	}
	
	public String getFolder() {
		return folder;
	}
	
	public String getServerName() {
		return serverName;
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
	
	/*
	 * SETTERS
	 */

	public void setClientName(String ip) {
		clientName = ip;
	}	

	public void setServerName(String serverName) {
		props.setProperty("server.ip", serverName);
		this.serverName = serverName;
	}	

	public void setServerPort(int serverPort) {
		props.setProperty("server.port", Integer.toString(serverPort));
		this.serverPort = serverPort;
	}	

	public void setClientTimeOut(int clientTimeOut) {
		props.setProperty("client.timeout", Integer.toString(clientTimeOut));
		this.clientTimeOut = clientTimeOut;
	}

	public void setClientAsServerPort(int clientAsServerPort) {
		props.setProperty("client.asserver.port", Integer.toString(clientAsServerPort));		
		this.clientAsServerPort = clientAsServerPort;
	}

	/*
	 * APPLICATION CORE
	 */	
	private void getResources(){
		// source : http://www.codejava.net/coding/reading-and-writing-configuration-for-java-application-using-properties-class#CreateProperties
		File configFile;		 
		FileReader reader;
		props = new Properties();
		
		try {
			configFile = new File("config.properties");
			reader = new FileReader(configFile);			
			
			// load the properties file
			props.load(reader);
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		serverName = props.getProperty("server.ip");
		serverPort = Integer.parseInt(props.getProperty("server.port"));
		clientTimeOut = Integer.parseInt(props.getProperty("client.timeout"));
		clientAsServerPort = Integer.parseInt(props.getProperty("client.asserver.port"));		
		
		/*
		ResourceBundle bundle = ResourceBundle.getBundle("application.properties.config");
		serverName = bundle.getString("server.ip");
		serverPort = Integer.parseInt(bundle.getString("server.port"));
		clientAsServerPort = Integer.parseInt(bundle.getString("client.asserver.port"));
		clientTimeOut = Integer.parseInt(bundle.getString("client.timeout"));
		*/
	}
	
	public boolean saveSettings(){
		File configFile = new File("config.properties");
		FileWriter writer;
		try {
			writer = new FileWriter(configFile);
			props.store(writer, "application settings");
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<String> getNetworkInterfaces(){
		// source : https://docs.oracle.com/javase/tutorial/networking/nifs/listing.html
		if(networks == null){
			networks = new ArrayList<String>();
			
			try {
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();			
				for (NetworkInterface netint : Collections.list(nets)){
		        	Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();	        	
		            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
		            	if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress()) {
		    	        	networks.add(inetAddress.getHostAddress());
		            	}
		            }
		        }
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return networks;
	}
	
	public boolean selectFolder(String selectedFolder){
		folder = selectedFolder;
    	files = new File(selectedFolder).listFiles();
    	
    	if(files.length > 0)
    		return true;
    	
    	return false;
	}
	
	

	public boolean connectToServer(){		
		try {			
			serverAddress = InetAddress.getByName(serverName);

			// Try to connect to the server
			mySocket = new Socket();
			mySocket.connect(new InetSocketAddress(serverAddress, serverPort), clientTimeOut);
			
			// Creation of the arraylist of string that contains client's ip address and files list
			data = new ArrayList<String>();
			
			// Add client ip
			data.add(clientName);
			
			// Add all files to arraylist
			for (File file : files) {
				if(!file.isDirectory())
					data.add(file.getName());
			}
			
			// Instantiate objectoutputstream to send data
			objectOutput = new ObjectOutputStream(mySocket.getOutputStream());
			
			// Tell the server we want to register
			objectOutput.writeObject(new String("registration"));
			objectOutput.flush();
			
			// Send arraylist of client's ip and files to server
			objectOutput.writeObject(data);
			objectOutput.flush();
			
			return true;
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}

		return false;
	}
	
	public ArrayList<Object> getClientFiles(){
		try {
			// Tell the server we want to get the clients list and their files
			objectOutput.writeObject(new String("getfiles"));
			objectOutput.flush();
			
			// Get the clients list and their files
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			clientsList = (ArrayList<Object>) objectInputStream.readObject();
			return clientsList;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/*
	 * Observable methods
	 */
	public int getCurrentProgress() {
		return currentProgress;
	}
	
	public void setCurrentProgress(int currentProgress){
		this.currentProgress = currentProgress;
		setChanged();
		notifyObservers();
	}
	
}