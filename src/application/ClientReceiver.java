package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Observable;

public class ClientReceiver  extends Observable implements Runnable{
	// propriétés de la classe
	private Socket clientSocket;
	private List<String> filesToReceive;
	private ClientModel model;

	public ClientReceiver(Socket clientSocket, List<String> files, ClientModel model) {
		this.clientSocket = clientSocket;
		this.filesToReceive = files;
		this.model = model;
	}
	
	public Socket getClientSocket() {
		return clientSocket;
	}
	
	@Override
	public void run(){
		// Objets nécessaires à la réception de fichiers
		BufferedInputStream bis;
		DataInputStream dis;
		int filesCount;
		File[] files;
		long fileLength;
		String fileName;
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try {
			// Instantiation de objectoutputstream pour envoyer des données
			ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// Informer le serveur des fichiers que nous voulons
			objectOutput.writeObject(filesToReceive);
			
			// Récupérer les informations sur le nombre de fichiers
			bis = new BufferedInputStream(clientSocket.getInputStream());
			
			dis = new DataInputStream(bis);

			filesCount = dis.readInt();
			files = new File[filesCount];

			// Récupération de tous les fichiers
			for(int i = 0; i < filesCount; i++)
			{
				// Récupération de la taille du fichier en cours et de son nom
			    fileLength = dis.readLong();
			    fileName = dis.readUTF();
				float percentCurrentComplete;

				// Reset de la progression de transfert de fichier et du nom du fichier en cours
			    model.setCurrentProgress(0);
			    model.setFileName(fileName);
			    
			    // Création d'un nouveau fichier
			    files[i] = new File(fileName);

			    fos = new FileOutputStream(files[i]);
			    bos = new BufferedOutputStream(fos);
			    
			    for(int j = 0; j < fileLength; j++){
			    	// Lecture de la portion de fichier
			    	bos.write(bis.read());
			    	
			    	// Calcul du pourcentage de transfert et modification dans le modèle pour la gui
			    	percentCurrentComplete = (float) ((j * 1.0) / fileLength * 100);
			    	model.setCurrentProgress((int) percentCurrentComplete);
			    }
			    
			    // Forcer l'affichage du 100% si les arrondis restent bloqués à 99%
			    model.setCurrentProgress(100);

			    // fermeture des objets pour la réception
			    bos.close();
			}
		    // fermeture des objets pour la réception
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}