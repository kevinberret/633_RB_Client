package application;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;

public class ClientProgressBar extends JProgressBar implements Observer{
	private int min = 0;
	private int max = 100;
	private ClientModel model;
	
	public ClientProgressBar(ClientModel model) {
		this.model = model;
		
		setMinimum(min);
		setMaximum(max);
		
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		setValue(model.getCurrentProgress());
	}
	
}
