package application;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
				if(cm.connectToServer()){					
					ClientListener cl = new ClientListener(cm.getClientAsServerPort(), cm.getFolder());
					Thread t = new Thread(cl);
					t.start();
					return true;
				}
		
		return false;					
	}
	
	public boolean getFiles(String serverAddress, List<String> files){
		try {
			Socket clientSocket = new Socket(serverAddress, cm.getClientAsServerPort());
			ClientReceiver cr = new ClientReceiver(clientSocket, files);
			Thread t = new Thread(cr);
			t.start();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}