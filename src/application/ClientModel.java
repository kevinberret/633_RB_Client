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
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Properties;
import java.util.UUID;

public class ClientModel extends Observable{
	// propri�t�s de la classe
	private String uuid;
	private String serverName;
	private String clientIp;
	private int serverPort;
	private int clientAsServerPort;
	private ArrayList<String> filesList;
	private File[] files;
	private InetAddress serverAddress;
	private Socket mySocket;
	private ObjectOutputStream objectOutput;
	private String folder;
	private int clientTimeOut;
	private ArrayList<String> networks;
	private Properties props;	
	private int currentProgress;
	private String fileName;
	private Client thisClient;
	
	public ClientModel() {
		// Get application settings
		getResources();		
		
		// Get all network interfaces
		getNetworkInterfaces();
		
		// Create this client object
		thisClient = new Client(uuid);
	}
	
	/*
	 * GETTERS
	 */
	public String getClientName() {
		return clientIp;
	}	
	
	public String getUuid() {
		return uuid;
	}

	public int getClientAsServerPort() {
		return clientAsServerPort;
	}

	public LinkedHashMap<String, Client> getClientsList() {
		return getClientFiles();
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
	
	public Client getThisClient() {
		return thisClient;
	}
	
	/*
	 * SETTERS
	 */

	public void setClientName(String ip) {
		clientIp = ip;
		thisClient.setClientIp(ip);
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
		// R�cup�ration des param�tres enregistr�s
		File configFile;		 
		FileReader reader;
		props = new Properties();
		
		try {
			configFile = new File("config.properties");
			reader = new FileReader(configFile);			
			
			// Chargement des param�tres
			props.load(reader);
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// D�finition des param�tres de l'application
		serverName = props.getProperty("server.ip");
		serverPort = Integer.parseInt(props.getProperty("server.port"));
		clientTimeOut = Integer.parseInt(props.getProperty("client.timeout"));
		clientAsServerPort = Integer.parseInt(props.getProperty("client.asserver.port"));
		
		// R�cup�ration de l'id unique du client
		uuid = props.getProperty("client.uuid");
		
		// Si le client ne poss�de pas d'id unique, g�n�ration d'un nouveau et sauvegarde dans le fichier config
		if(uuid == null){
			uuid = UUID.randomUUID().toString();
			props.setProperty("client.uuid", uuid);
			saveSettings();
		}
	}
	
	// Sauvegarde des param�tres
	public boolean saveSettings(){
		// Cr�ation du fichier dans lequel seront enregistr�es les donn�es
		File configFile = new File("config.properties");		
		FileWriter writer;
		
		// Sauvegarde des donn�es
		try {
			writer = new FileWriter(configFile);
			props.store(writer, "application settings");
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Retourne faux en cas d'erreur d'enregistrement
		return false;
	}
	
	// R�cup�ration de la liste des ip pour le client
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
				e.printStackTrace();
			}
		}
		
		return networks;
	}
	
	// V�rification que le dossier s�lectionn� contient bien des fichiers � partager.
	public boolean selectFolder(String selectedFolder){
		folder = selectedFolder;
    	files = new File(selectedFolder).listFiles();
    	
    	if(files.length > 0)
    		return true;
    	
    	return false;
	}
	
	// Connexion au serveur
	public boolean connectToServer(){		
		try {			
			serverAddress = InetAddress.getByName(serverName);

			// Tentative de connexion au serveur
			mySocket = new Socket();
			mySocket.connect(new InetSocketAddress(serverAddress, serverPort), clientTimeOut);
			
			// Cr�ation d'une arraylist de string qui contient les adresses ip des clients et leurs fichiers
			filesList = new ArrayList<String>();
			
			// Ajout de l'ip du client
			//data.add(clientIp);
			
			// Ajout des fichiers du client
			for (File file : files) {
				if(!file.isDirectory())
					filesList.add(file.getName());
			}
			
			thisClient.setFiles(filesList);
			
			// Objectoutputstream pour envoyer des donn�es
			objectOutput = new ObjectOutputStream(mySocket.getOutputStream());
			
			// Informer le serveur de notre volont� de s'enregistrer
			objectOutput.writeObject(new String("registration"));
			objectOutput.flush();
			
			// Envoi de l'adresse ip et des fichiers partag�s
			objectOutput.writeObject(thisClient);
			objectOutput.flush();
			
			// retourne vrai si enregistrement ok
			return true;
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}

		// erreur d'enregistrement
		return false;
	}
	
	// R�cup�rer la liste des clients et leurs fichiers
	public LinkedHashMap<String, Client> getClientFiles(){
		try {
			// Informer le serveur que nous voulons r�cup�rer les clients et la liste des fichiers qu'ils partagent
			objectOutput.writeObject(new String("getfiles"));
			objectOutput.flush();
			
			// R�cup�rer les infos voulues
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			return (LinkedHashMap<String, Client>) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// en cas d'erreur, retour objet null
		return null;
	}
	
	// R�cup�rer un client par son unique id
	public Client getClientByUuid(String uuid){
		try {
			// Informer le serveur que nous voulons r�cup�rer un client et envoi du uuid du client d�sir�
			objectOutput.writeObject(new String("getclientbyuuid"));
			objectOutput.flush();
			
			objectOutput.writeObject(uuid);
			objectOutput.flush();
			
			// R�cup�rer les infos voulues
			ObjectInputStream objectInputStream = new ObjectInputStream(mySocket.getInputStream());
			return (Client) objectInputStream.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// en cas d'erreur, retour objet null
		return null;
	}
	
	/*
	 * M�thodes getters et setters pour observable
	 */
	public int getCurrentProgress() {
		return currentProgress;
	}
	
	public void setCurrentProgress(int currentProgress){
		this.currentProgress = currentProgress;
		setChanged();
		notifyObservers();
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		setChanged();
		notifyObservers();
	}
	
	public String getFileName() {
		return fileName;
	}	
}