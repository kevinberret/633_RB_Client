package application;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
	private ClientModel cm;
	private ClientReceiver clientReceiver;
	private ClientListener clientListener;
	
	public ClientController(ClientModel cm) {
		this.cm = cm;
	}

	/*
	 * GETTERS
	 */
	public String getClientName() {
		return cm.getClientName();
	}
	
	public String getServerName() {
		return cm.getServerName();
	}
	
	public String getServerPort() {
		return Integer.toString(cm.getServerPort());
	}
	
	public String getClientTimeOut() {
		return Integer.toString(cm.getClientTimeOut());
	}
	
	public String getClientAsServerPort() {
		return Integer.toString(cm.getClientAsServerPort());
	}
	
	public ArrayList<Object> getClientsList(){
		return cm.getClientsList();
	}
	
	public ArrayList<String> getNetworkInterfaces(){
		return cm.getNetworkInterfaces();
	}
	
	/*
	 * SETTERS
	 */
	public void setClientIp(String ip){
		cm.setClientName(ip);
	}
	
	public void setClientName(String ip) {
		cm.setClientName(ip);
	}	

	public void setServerName(String serverName) {
		cm.setServerName(serverName);
	}	

	public void setServerPort(String serverPort) {
		cm.setServerPort(Integer.parseInt(serverPort));
	}	

	public void setClientTimeOut(String clientTimeOut) {
		cm.setClientTimeOut(Integer.parseInt(clientTimeOut));
	}

	public void setClientAsServerPort(String clientAsServerPort) {		
		cm.setClientAsServerPort(Integer.parseInt(clientAsServerPort));
	}
	
	public boolean saveSettings(){
		return cm.saveSettings();
	}
	
	/*
	 * APPLICATION CORE
	 */
	public boolean selectFolder(String selectedFolder){
		if(selectedFolder != null && !selectedFolder.isEmpty())
			if(cm.selectFolder(selectedFolder))
				if(cm.connectToServer()){					
					clientListener = new ClientListener(cm.getClientAsServerPort(), cm.getFolder());
					Thread t = new Thread(clientListener);
					t.start();
					return true;
				}
		
		return false;					
	}
	
	public boolean getFiles(String serverAddress, List<String> files){
		try {
			Socket clientSocket = new Socket(serverAddress, cm.getClientAsServerPort());
			clientReceiver = new ClientReceiver(clientSocket, files);
			Thread threadReceiver = new Thread(clientReceiver);
			threadReceiver.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public void closeConnections() {
		try {
			// close connection with clients
			if(clientReceiver != null)
				clientReceiver.getClientSocket().close();
			if(clientListener != null)
				clientListener.getListenSocket().close();
			
			// close connection with main server
			if(cm.getMySocket() != null)
				cm.getMySocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}