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

/**
 * This class allow the client to receive files from another client
 * @author Daniel et Kevin
 *
 */
public class ClientReceiver implements Runnable{
	/**
	 * The socket used in connection with another client
	 */
	private Socket clientSocket;
	
	/**
	 * The files list to receive
	 */
	private List<String> filesToReceive;
	
	/**
	 * The model containing data
	 */
	private ClientModel model;

	/**
	 * The default constructor
	 * @param clientSocket The socket used in connection with another client
	 * @param files The files list to receive
	 * @param model The model containing data
	 */
	public ClientReceiver(Socket clientSocket, List<String> files, ClientModel model) {
		this.clientSocket = clientSocket;
		this.filesToReceive = files;
		this.model = model;
	}
	
	/**
	 * Returns the socket used in connection with another client
	 * @return The socket used in connection with another client
	 */
	public Socket getClientSocket() {
		return clientSocket;
	}
	
	@Override
	public void run(){
		// Objects used by file reception
		// Get informations
		BufferedInputStream bis;
		DataInputStream dis;
		// Send informations
		FileOutputStream fos;
		BufferedOutputStream bos;
		// Files informations
		int filesCount;
		File[] files;
		long fileLength;
		String fileName;		
		
		try {
			// Instantiate objectoutput to send data
			ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// Tell the other client which files we want to receive
			objectOutput.writeObject(filesToReceive);
			
			// Get the number of files that will be sent (if files number is different)
			bis = new BufferedInputStream(clientSocket.getInputStream());			
			dis = new DataInputStream(bis);
			filesCount = filesToReceive.size();
			files = new File[filesCount];

			// Get all files
			for(int i = 0; i < filesCount; i++)
			{
				// Get file name and size
			    fileLength = dis.readLong();
			    fileName = dis.readUTF();
				float percentCurrentComplete;

				// Reset progress and file name
			    model.setCurrentProgress(0);
			    model.setFileName(fileName);
			    
			    // Create a new file with good name
			    files[i] = new File(fileName);

			    // Get the file
			    fos = new FileOutputStream(files[i]);
			    bos = new BufferedOutputStream(fos);
			    
			    for(int j = 0; j < fileLength; j++){
			    	// Read part of the file
			    	bos.write(bis.read());
			    	
			    	// Calculate the percentage already downloaded and set it in model
			    	percentCurrentComplete = (float) ((j * 1.0) / fileLength * 100);
			    	model.setCurrentProgress((int) percentCurrentComplete);
			    }
			    
			    // Force display to 100% (sometimes rounded values get stuck to 99)
			    model.setCurrentProgress(100);

			    // close objects
			    bos.close();
			}
		    // close objects
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}