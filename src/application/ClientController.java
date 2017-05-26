package application;

public class ClientController {
	private ClientModel cm;
	
	public ClientController(ClientModel cm) {
		this.cm = cm;
	}
	
	public void control(){
		if(cm.selectFolder("sss"))
			cm.connectToServer();
	}

	public String getClientName() {
		return cm.getClientName();
	}
	
	public boolean selectFolder(String selectedFolder){
		if(selectedFolder != null && !selectedFolder.isEmpty())
			if(cm.selectFolder(selectedFolder))
				return cm.connectToServer();
		
		return false;					
	}
}
