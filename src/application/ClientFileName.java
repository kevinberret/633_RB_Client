package application;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

public class ClientFileName extends JLabel implements Observer {
	
	private ClientModel model;

	public ClientFileName(ClientModel model) {
		this.model = model;
		
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		setText("Current file (" + model.getFileName() + ") [" + model.getCurrentProgress() + "%]");
	}
}