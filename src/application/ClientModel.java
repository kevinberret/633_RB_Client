package application;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ClientModel {
	private String serverName;
	private String clientName;
	private int serverPort;
	private ArrayList<String> data;
	private File[] files;
	private InetAddress serverAddress;
	private Socket mySocket;
	private ObjectOutputStream objectOutput;
	
	public String getClientName() {
		return clientName;
	}

	public ClientModel() {
		// Get application settings
		getResources();
	}
	
	private void getResources(){
		ResourceBundle bundle = ResourceBundle.getBundle("application.properties.config");
		serverName = bundle.getString("server.ip");
		serverPort = Integer.parseInt(bundle.getString("server.port"));
		clientName = bundle.getString("client.ip");		
	}
	
	public boolean selectFolder(String selectedFolder){		
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
			
			// Send arraylist of client's ip and files to server
			objectOutput = new ObjectOutputStream(mySocket.getOutputStream());
			objectOutput.writeObject(data);
			objectOutput.flush();
			
			// close connection with server
			objectOutput.close();
			mySocket.close();
			
			return true;
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}

		return false;
	}
}