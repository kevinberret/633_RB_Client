package application;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;

public class ClientProgressBar extends JProgressBar implements Observer{
	private ClientModel model;
	
	public ClientProgressBar(ClientModel model) {
		this.model = model;

		setMinimum(0);
		setMaximum(100);
		
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		setValue(model.getCurrentProgress());
	}
	
}
