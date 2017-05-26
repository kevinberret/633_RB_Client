package application;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientController {
	private ClientModel cm;
	
	public ClientController(ClientModel cm) {
		this.cm = cm;
	}

	public String getClientName() {
		return cm.getClientName();
	}
	
	public ArrayList<Object> getClientsList(){
		return cm.getClientsList();
	}
	
	public boolean selectFolder(String selectedFolder){
		if(selectedFolder != null && !selectedFolder.isEmpty())
			if(cm.selectFolder(selectedFolder))
				if( cm.connectToServer()){
					ClientListener cl = new ClientListener(cm.getClientAsServerPort(), cm.getFolder());
					Thread t1 = new Thread(cl);					
					t1.start();
				}
		
		return false;					
	}
	
	public void getFiles(String serverAddress, ArrayList<String> files){
		try {
			Socket clientSocket = new Socket(serverAddress, cm.getClientAsServerPort());
			ClientReceiver cr = new ClientReceiver(clientSocket, files);
			Thread t = new Thread(cr);
			t.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
