package application;

import java.io.BufferedOutputStream;
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
				bos.close();
				
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
		}
		
	}

}
