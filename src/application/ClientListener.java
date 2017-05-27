package application;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ClientListener implements Runnable {
	private ServerSocket listenSocket = null;
	private int clientAsServerPort;
	private String folder;
	
	public ClientListener(int clientAsServerPort, String folder) {
		this.clientAsServerPort = clientAsServerPort;
		this.folder = folder;
	}
	
	@Override
	public void run() {
		try {
			listenSocket = new ServerSocket(clientAsServerPort);

			while(true){
				ClientSender cs = new ClientSender(listenSocket.accept(), folder);
				
				Thread t = new Thread(cs);
				t.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}