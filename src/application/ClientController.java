package application;

public class ClientController {
	private ClientModel cm;
	
	public ClientController(ClientModel cm) {
		this.cm = cm;
	}

	public String getClientName() {
		return cm.getClientName();
	}
	
	public boolean selectFolder(String selectedFolder){
		if(selectedFolder != null && !selectedFolder.isEmpty())
			if(cm.selectFolder(selectedFolder))
				if( cm.connectToServer()){
					// TODO: start 2 threads (receive & send)
					
				}
		
		return false;					
	}
}
