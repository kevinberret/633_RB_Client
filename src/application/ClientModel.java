package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class ClientModel {
	private String serverName;
	private String clientName;
	private File[] files;
	private InetAddress serverAddress;
	private Socket mySocket;
	private PrintWriter pout;
	
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
			mySocket = new Socket(serverAddress, 45000);

			// Get an output stream to send data to the server
			pout = new PrintWriter(mySocket.getOutputStream());			
			
			// Send client name to server
			pout.println(clientName);
			pout.flush();
			
			// TODO: get server confirmation
			
			// Send list of all available files
			for (File file : files) {
				if(!file.isDirectory()){
					System.out.println(file.getName());
					pout.println(file.getName());
					pout.flush();
				}
			}
			
			// Send end of transmission message
			pout.println("EOT");
			pout.flush();

			// close connection with server
			pout.close();
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