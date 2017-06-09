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
	// propriétés de la classe
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
	private String fileName;
	
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
		// Récupération des paramètres enregistrés
		File configFile;		 
		FileReader reader;
		props = new Properties();
		
		try {
			configFile = new File("config.properties");
			reader = new FileReader(configFile);			
			
			// Chargement des paramètres
			props.load(reader);
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Définition des paramètres de l'application
		serverName = props.getProperty("server.ip");
		serverPort = Integer.parseInt(props.getProperty("server.port"));
		clientTimeOut = Integer.parseInt(props.getProperty("client.timeout"));
		clientAsServerPort = Integer.parseInt(props.getProperty("client.asserver.port"));
	}
	
	// Sauvegarde des paramètres
	public boolean saveSettings(){
		// Création du fichier dans lequel seront enregistrées les données
		File configFile = new File("config.properties");		
		FileWriter writer;
		
		// Sauvegarde des données
		try {
			writer = new FileWriter(configFile);
			props.store(writer, "application settings");
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Retourne faux en cas d'erreur d'enregistrement
		return false;
	}
	
	// Récupération de la liste des ip pour le client
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
	
	// Vérification que le dossier sélectionné contient bien des fichiers à partager.
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
			
			// Création d'une arraylist de string qui contient les adresses ip des clients et leurs fichiers
			data = new ArrayList<String>();
			
			// Ajout de l'ip du client
			data.add(clientName);
			
			// Ajout des fichiers du client
			for (File file : files) {
				if(!file.isDirectory())
					data.add(file.getName());
			}
			
			// Objectoutputstream pour envoyer des données
			objectOutput = new ObjectOutputStream(mySocket.getOutputStream());
			
			// Informer le serveur de notre volonté de s'enregistrer
			objectOutput.writeObject(new String("registration"));
			objectOutput.flush();
			
			// Envoi de l'adresse ip et des fichiers partagés
			objectOutput.writeObject(data);
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
	
	// Récupérer la liste des clients et leurs fichiers
	public ArrayList<Object> getClientFiles(){
		try {
			// Informer le serveur que nous voulons récupérer les clients et la liste des fichiers qu'ils partagent
			objectOutput.writeObject(new String("getfiles"));
			objectOutput.flush();
			
			// Récupérer les infos voulues
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
		
		// en cas d'erreur, retour objet null
		return null;
	}
	
	/*
	 * Méthodes getters et setters pour observable
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