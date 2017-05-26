package application;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientModel {
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
	
	public ClientModel() {
		// Get application settings
		getResources();
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
		return clientsList;
	}
	
	public String getFolder() {
		return folder;
	}

	/*
	 * APPLICATION CORE
	 */	
	private void getResources(){
		ResourceBundle bundle = ResourceBundle.getBundle("application.properties.config");
		serverName = bundle.getString("server.ip");
		serverPort = Integer.parseInt(bundle.getString("server.port"));
		clientName = bundle.getString("client.ip");
		clientAsServerPort = Integer.parseInt(bundle.getString("client.asserver.port"));
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
			mySocket = new Socket(serverAddress, serverPort);		
			
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
	
}