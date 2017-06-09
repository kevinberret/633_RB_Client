package application;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
	// Propri�t�s de la classe
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
	
	// Sauvegarde des param�tres de l'application	
	public boolean saveSettings(){
		return cm.saveSettings();
	}
	
	/*
	 * APPLICATION CORE
	 */
	public boolean selectFolder(String selectedFolder){
		// V�rification que le dossier fourni n'est pas vide et qu'il existe
		if(selectedFolder != null && !selectedFolder.isEmpty())
			// si possible de s�lectionner le dossier d�sir�, conexion au server
			if(cm.selectFolder(selectedFolder))
				if(cm.connectToServer()){
					// si connexion au serveur ok, d�marrage du listener dans un thread et valider la s�lection du dossier
					clientListener = new ClientListener(cm.getClientAsServerPort(), cm.getFolder());
					Thread t = new Thread(clientListener);
					t.start();
					return true;
				}
		
		// en cas de n'importe quelle erreur, retourner l'information � la vue
		return false;					
	}
	
	// R�cup�ration des fichiers d�sir�s
	public boolean getFiles(String serverAddress, List<String> files){
		try {
			// Ouverture d'un socket avec le client poss�dant le fichier d�sir�
			Socket clientSocket = new Socket(serverAddress, cm.getClientAsServerPort());
			
			// Cr�ation et d�marrage de la r�ception des fichiers dans un thread
			clientReceiver = new ClientReceiver(clientSocket, files, cm);
			Thread threadReceiver = new Thread(clientReceiver);
			threadReceiver.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Retour d'erreur
		return false;
	}

	public void closeConnections() {
		try {
			// close connection with clients
			if(clientReceiver != null && clientReceiver.getClientSocket() != null)
				clientReceiver.getClientSocket().close();
			if(clientListener != null && clientListener.getListenSocket() != null)
				clientListener.getListenSocket().close();
			
			// close connection with main server
			if(cm != null && cm.getMySocket() != null)
				cm.getMySocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}