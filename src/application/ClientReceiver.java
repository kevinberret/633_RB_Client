package application;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientReceiver implements Runnable {
	private Socket clientSocket;
	private int bytesRead;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private List<String> files;

	public ClientReceiver(Socket clientSocket, List<String> files) {
		this.clientSocket = clientSocket ;
		this.files = files;
	}
	
	@Override
	public void run(){
		System.out.println("Client receiver started");
		
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
			
			
			/*
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
			}*/
		}
		
	}

}
