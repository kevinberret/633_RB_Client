package application;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JProgressBar;

public class ClientProgressBar extends JProgressBar implements Observer{
	// propri�t�s de la classe
	private ClientModel model;
	
	public ClientProgressBar(ClientModel model) {
		this.model = model;

		// barre de progression de 0 � 100
		setMinimum(0);
		setMaximum(100);
		
		this.model.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		// modifier la valeur selon la progression actuelle
		setValue(model.getCurrentProgress());
	}
	
}
