package application;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientReceiver implements Runnable {
	private Socket clientSocket;
	private int bytesRead;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private List<String> filesToReceive;

	public ClientReceiver(Socket clientSocket, List<String> files) {
		this.clientSocket = clientSocket ;
		this.filesToReceive = files;		
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

			    files[i] = new File(fileName);

			    fos = new FileOutputStream(files[i]);
			    bos = new BufferedOutputStream(fos);

			    for(int j = 0; j < fileLength; j++) 
			    	bos.write(bis.read());

			    bos.close();
			}

			dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		// source https://coderanch.com/t/473799/java/Transfer-multiple-files-Server-Client 
		DataInputStream dis;
		int size;
		byte[] nameInBytes;
		String name;
		byte[] contents;
		
		try {
			for (String file : files) {
				System.out.println("File " + file + " transfer started");
				dis = new DataInputStream(clientSocket.getInputStream());
				size = dis.readInt();
				nameInBytes = new byte[size];
				dis.readFully(nameInBytes);
				name = new String(nameInBytes, "UTF-8");
				size = dis.readInt();
				contents  = new byte[size];
				dis.readFully(contents);
				bos = new BufferedOutputStream(fos);
				bos.write(contents);
				bos.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (String file : files) {
			System.out.println("File transfer started");
			
			
			
			try{
				byte [] mybytearray  = new byte [1024];
				InputStream is = clientSocket.getInputStream();				
				
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos);
				bytesRead = is.read(mybytearray,0,mybytearray.length);
				current = bytesRead;

				bos.write(mybytearray, 0 , bytesRead);
				bos.flush();
				// TODO check si close fout la merde
				bos.close();
				
				//TODO: check boucle infinie
				do {
					bytesRead =
							is.read(mybytearray, current, (mybytearray.length-current));
					if(bytesRead >= 0) current += bytesRead;
				} while(bytesRead > -1);


				System.out.println("File " 
						+ " downloaded (" + current + " bytes read)");
			}catch (IOException e) {
				// TODO: handle exception
			}
		}*/
		
	}

}
