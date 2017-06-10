package application;

/**
 * This method allows to run the client
 * @author Daniel
 *
 */
public class ClientMain {
	public static void main(String[] args) {
		ClientModel cm = new ClientModel();
		ClientController cc = new ClientController(cm);
		ClientWindow cw = new ClientWindow(cc, cm);
		cw.setVisible(true);
	}	
}