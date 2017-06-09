package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientSender implements Runnable {
	// propriétés de la classe
	private Socket clientSocket;
	private String folder;
	
	public ClientSender(Socket clientSocket, String folder) {
		this.clientSocket = clientSocket;
		this.folder = folder;
	}
	
	
	@Override
	public void run() {
		// Création des objets nécessaires pour la transaction
		ObjectInputStream objectInputStream;
		ArrayList<String> filesList;
		BufferedOutputStream bos;
		DataOutputStream dos;
		BufferedInputStream bis;
		FileInputStream fis;
		
		try {
			// Récupération de la liste de fichiers à envoyer
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			filesList = (ArrayList<String>) objectInputStream.readObject();
			
			// Création des objets nécessaires pour l'envoi de fichiers
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			dos = new DataOutputStream(bos);
			
			// Envoi du nombre de fichiers qui seront envoyés
			dos.writeInt(filesList.size());
			
			// Le fichier à envoyer
			File fileToSend;

			for(String file : filesList)
			{
				// Création du fichier 
				fileToSend = new File(folder+"\\"+file);
				
				// Envoi de sa taille et de son nom
			    long length = fileToSend.length();
			    dos.writeLong(length);

			    String name = fileToSend.getName();
			    dos.writeUTF(name);

			    // Création des objets nécessaires pour l'envoi des fichiers en bytes
			    fis = new FileInputStream(folder+"\\"+file);
			    bis = new BufferedInputStream(fis);

			    // Envoi du fichier
			    int theByte = 0;
			    while((theByte = bis.read()) != -1) 
			    	bos.write(theByte);

			    bis.close();
			}

			// Fermeture de l'objet pour envoyer les fichiers
			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}