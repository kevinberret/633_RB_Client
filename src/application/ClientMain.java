package application;

public class ClientMain {
	public static void main(String[] args) {
		ClientModel cm = new ClientModel();
		ClientController cc = new ClientController(cm);
		ClientWindow cw = new ClientWindow(cc, cm);
		cw.setVisible(true);
	}	
}