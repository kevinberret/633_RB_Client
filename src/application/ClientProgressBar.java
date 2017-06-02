package application;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;

public class ClientProgressBar extends JProgressBar implements Observer{
	private int min = 0;
	private int max = 100;
	
	public ClientProgressBar() {
		setMinimum(min);
		setMaximum(max);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		ClientReceiver cr = (ClientReceiver) o;
		setValue(cr.getCurrentProgress());
	}
	
}
