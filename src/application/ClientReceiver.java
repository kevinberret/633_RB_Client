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
	// propri�t�s de la classe
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
		// Objets n�cessaires � la r�ception de fichiers
		BufferedInputStream bis;
		DataInputStream dis;
		int filesCount;
		File[] files;
		long fileLength;
		String fileName;
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try {
			// Instantiation de objectoutputstream pour envoyer des donn�es
			ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// Informer le serveur des fichiers que nous voulons
			objectOutput.writeObject(filesToReceive);
			
			// R�cup�rer les informations sur le nombre de fichiers
			bis = new BufferedInputStream(clientSocket.getInputStream());
			
			dis = new DataInputStream(bis);

			filesCount = dis.readInt();
			files = new File[filesCount];

			// R�cup�ration de tous les fichiers
			for(int i = 0; i < filesCount; i++)
			{
				// R�cup�ration de la taille du fichier en cours et de son nom
			    fileLength = dis.readLong();
			    fileName = dis.readUTF();
				float percentCurrentComplete;

				// Reset de la progression de transfert de fichier et du nom du fichier en cours
			    model.setCurrentProgress(0);
			    model.setFileName(fileName);
			    
			    // Cr�ation d'un nouveau fichier
			    files[i] = new File(fileName);

			    fos = new FileOutputStream(files[i]);
			    bos = new BufferedOutputStream(fos);
			    
			    for(int j = 0; j < fileLength; j++){
			    	// Lecture de la portion de fichier
			    	bos.write(bis.read());
			    	
			    	// Calcul du pourcentage de transfert et modification dans le mod�le pour la gui
			    	percentCurrentComplete = (float) ((j * 1.0) / fileLength * 100);
			    	model.setCurrentProgress((int) percentCurrentComplete);
			    }
			    
			    // Forcer l'affichage du 100% si les arrondis restent bloqu�s � 99%
			    model.setCurrentProgress(100);

			    // fermeture des objets pour la r�ception
			    bos.close();
			}
		    // fermeture des objets pour la r�ception
			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}