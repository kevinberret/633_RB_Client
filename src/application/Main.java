package application;

public class Main {
	public static void main(String[] args) {
		ClientModel cm = new ClientModel();
		ClientController cc = new ClientController(cm);
		ClientWindow cw = new ClientWindow(cc);
		cw.setVisible(true);
	}
	
}
