package application;

import java.net.ServerSocket;

public class ClientListener implements Runnable {
	// propriétés de la classe
	private ServerSocket listenSocket = null;
	private int clientAsServerPort;
	private String folder;
	
	public ClientListener(int clientAsServerPort, String folder) {
		this.clientAsServerPort = clientAsServerPort;
		this.folder = folder;
	}	
	
	/*
	 * GETTERS
	 */
	public ServerSocket getListenSocket() {
		return listenSocket;
	}
	
	@Override
	public void run() {
		try {
			// Démarrage du socket en tant que serveur
			listenSocket = new ServerSocket(clientAsServerPort);

			while(true){
				// Ouverture de socket avec un client et démarrage de l'envoi dans un thread
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