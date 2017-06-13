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
import java.util.logging.Level;

import util.ClientLogger;

/**
 * This class allows a client to send files to another client
 * @author Daniel et Kevin
 *
 */
public class ClientSender implements Runnable {
	/**
	 * The socket used in connection with another client
	 */
	private Socket clientSocket;
	
	/**
	 * The folder where shared files are stored in
	 */
	private String folder;
	
	/**
	 * Default constructor
	 * @param clientSocket The socket used in connection with another client
	 * @param folder The folder where shared files are stored in
	 */
	public ClientSender(Socket clientSocket, String folder) {
		this.clientSocket = clientSocket;
		this.folder = folder;
	}	
	
	@Override
	public void run() {
		// Create des objects for transaction
		ObjectInputStream objectInputStream;
		ArrayList<String> filesList;
		BufferedOutputStream bos;
		DataOutputStream dos;
		BufferedInputStream bis;
		FileInputStream fis;
		
		try {
			// Get files list
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			filesList = (ArrayList<String>) objectInputStream.readObject();
			
			// Create objects used to send data
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			dos = new DataOutputStream(bos);
			File fileToSend;

			for(String file : filesList)
			{
				// Create the file 
				fileToSend = new File(folder+"\\"+file);
				
				// Send his size and name
			    long length = fileToSend.length();
			    dos.writeLong(length);
			    String name = fileToSend.getName();
			    dos.writeUTF(name);

			    // Create objects to send file
			    fis = new FileInputStream(folder+"\\"+file);
			    bis = new BufferedInputStream(fis);

			    // Sending file
			    int theByte = 0;
			    while((theByte = bis.read()) != -1) 
			    	bos.write(theByte);

			    // Close objects
			    bis.close();
			}

			// Close objects
			dos.close();
		} catch (IOException e) {
			ClientLogger.getLogger().log(Level.SEVERE, e.getMessage(),e);
		} catch (ClassNotFoundException e) {
			ClientLogger.getLogger().log(Level.SEVERE, e.getMessage(),e);
		}
	}
}