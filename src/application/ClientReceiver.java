package application;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientReceiver implements Runnable {
	private Socket clientSocket;
	private int bytesRead;
	private int current = 0;
	private FileOutputStream fos = null;
	private BufferedOutputStream bos = null;
	private ArrayList<String> files;

	public ClientReceiver(Socket clientSocket, ArrayList<String> files) {
		this.clientSocket = clientSocket ;
		this.files = files;
	}
	
	@Override
	public void run(){
		for (String file : files) {
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
