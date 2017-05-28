package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClientSender implements Runnable {
	private Socket clientSocket;
	private String folder;
	private FileInputStream fis = null;
	private BufferedInputStream bis = null;
	private OutputStream out = null;
	
	public ClientSender(Socket clientSocket, String folder) {
		this.clientSocket = clientSocket;
		this.folder = folder;
	}
	@Override
	public void run() {
		System.out.println("Client sender started");
		
		ObjectInputStream objectInputStream;
		ArrayList<String> filesList;
		BufferedOutputStream bos;
		DataOutputStream dos;
		BufferedInputStream bis;
		FileInputStream fis;
		
		try {
			// Get the files list to send
			objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			filesList = (ArrayList<String>) objectInputStream.readObject();
			
			// Create necessary objects to send the files
			bos = new BufferedOutputStream(clientSocket.getOutputStream());
			dos = new DataOutputStream(bos);
			
			// Send the number of file that will be sent
			dos.writeInt(filesList.size());
			
			// File that will be sended
			File fileToSend;

			for(String file : filesList)
			{
				// Create the file to get all necessary informations
				fileToSend = new File(folder+file);
				
				// Send file length and name
			    long length = fileToSend.length();
			    dos.writeLong(length);

			    String name = fileToSend.getName();
			    dos.writeUTF(name);

			    // Create necessary objects to send the file
			    fis = new FileInputStream(file);
			    bis = new BufferedInputStream(fis);

			    // Send the file
			    int theByte = 0;
			    while((theByte = bis.read()) != -1) 
			    	bos.write(theByte);

			    bis.close();
			}

			dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		/*
		// source : https://coderanch.com/t/473799/java/Transfer-multiple-files-Server-Client
		DataOutputStream dos;
		byte[] nameInBytes;
		byte[] fileInBytes;
		File fileToSend;		
		
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			ArrayList<String> filesList = (ArrayList<String>) objectInputStream.readObject();	
			
			for (String file : filesList) {
				fileToSend = new File (folder + file);
				fileInBytes = Files.readAllBytes(Paths.get(folder+file));
				
				dos = new DataOutputStream(clientSocket.getOutputStream());
				nameInBytes = file.getBytes("UTF-8");
				dos.writeInt(nameInBytes.length);
				dos.write(nameInBytes);
				dos.writeInt((int) fileToSend.length());
				dos.write(fileInBytes);
				dos.flush();
				System.out.println("file sent");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			ArrayList<String> filesList = (ArrayList<String>) objectInputStream.readObject();
			
			File fileToSend;
			
			for (String file : filesList) {
				fileToSend = new File (folder + file);
				
				byte [] mybytearray  = new byte [(int)fileToSend.length()];
				
				try{
					fis = new FileInputStream(fileToSend);
					bis = new BufferedInputStream(fis);

					bis.read(mybytearray,0,mybytearray.length);
					out = clientSocket.getOutputStream() ; 
					System.out.println("Sending " + fileToSend.getName() + "(" + mybytearray.length + " bytes)");
					out.write(mybytearray,0,mybytearray.length);
					out.flush();

					out.close();
					
					out.flush();
					clientSocket.close();
					out.close();
					System.out.println("Done.");
				} catch ( IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
	}
}