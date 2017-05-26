package application;

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
					ClientListener cl = new ClientListener(cm.getClientAsServerPort());
					Thread t1 = new Thread(cl);					
					t1.start();
				}
		
		return false;					
	}
}
