package application;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JLabel;

/**
 * This class is a JLabel Observer and allows to be updated when Observable sends the notification
 * @author Kevin
 *
 */
public class ClientFileName extends JLabel implements Observer {
	/**
	 * The model required to add an observer
	 */
	private ClientModel model;

	/**
	 * The default constructor
	 * @param model The data model
	 */
	public ClientFileName(ClientModel model) {
		this.model = model;
		
		// add this to model observers
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object oo) {
		// modify the text according to current file name and download progress
		setText("Current file (" + model.getFileName() + ") [" + model.getCurrentProgress() + "%]");
	}
}