package application;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JProgressBar;

/**
 * This class allow you to create a jprogressbar implementing an observer so you can add it to an observable object
 * @author Kevin
 *
 */
public class ClientProgressBar extends JProgressBar implements Observer{
	/**
	 * The model that contains all data
	 */
	private ClientModel model;
	
	/**
	 * Default constructor
	 * @param model The model that contains all data
	 */
	public ClientProgressBar(ClientModel model) {
		this.model = model;

		// The progressbar goes from 0 to 100%
		setMinimum(0);
		setMaximum(100);
		
		// add this to model observers
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// modify the value according to current progress
		setValue(model.getCurrentProgress());
	}	
}