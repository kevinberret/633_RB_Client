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
		System.out.println("Client receiver started");

		BufferedInputStream bis;
		DataInputStream dis;
		int filesCount;
		File[] files;
		long fileLength;
		String fileName;
		FileOutputStream fos;
		BufferedOutputStream bos;
		
		try {
			// Instantiate objectoutputstream to send data
			ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			
			// Tell the server which file we want
			objectOutput.writeObject(filesToReceive);
			
			bis = new BufferedInputStream(clientSocket.getInputStream());
			
			dis = new DataInputStream(bis);

			filesCount = dis.readInt();
			files = new File[filesCount];

			for(int i = 0; i < filesCount; i++)
			{
			    fileLength = dis.readLong();
			    fileName = dis.readUTF();
				float percentCurrentComplete;

			    model.setCurrentProgress(0);
			    model.setFileName(fileName);
			    
			    files[i] = new File(fileName);

			    fos = new FileOutputStream(files[i]);
			    bos = new BufferedOutputStream(fos);
			    
			    for(int j = 0; j < fileLength; j++){
			    	// Read data from input stream from socket
			    	bos.write(bis.read());
			    	
			    	// Calculate percentage and display it
			    	percentCurrentComplete = (float) ((j * 1.0) / fileLength * 100);
			    	model.setCurrentProgress((int) percentCurrentComplete);
			    }
			    
			    // force display 100% if transfer is finished [sometimes casting to in leads to a 99%)
			    model.setCurrentProgress(100);

			    bos.close();
			}

			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}