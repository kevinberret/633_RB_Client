/**
 * 
 */
package application;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author kb
 *
 */
public class ClientSettings extends Dialog {
	// propriétés de la classe
	private ClientController cc;
	private JTextField jtfServerName;
	private JTextField jtfServerPort;
	private JTextField jtfClientTimeOut;
	private JTextField jtfClientAsServerPort;
	private ClientWindow frame;

	public ClientSettings(JFrame frame, String title, boolean modal, ClientController cc) {
		super(frame, title, modal);
		this.cc = cc;
		this.frame = (ClientWindow) frame;	
		
		// création de l'affichage
		generateGUI();
		
		pack();
	}
	
	private void generateGUI(){
		// Génération de la gui
		JPanel pnlCenter = new JPanel();
		pnlCenter.setBorder(new EmptyBorder(10, 10, 10, 10));
		JLabel lblServerName = new JLabel("Server IP");
		JLabel lblServerPort = new JLabel("Server port");
		JLabel lblClientTimeOut = new JLabel("Time out");
		JLabel lblClientAsServerPort = new JLabel("Port to share files");
		jtfServerName = new JTextField(cc.getServerName());
		jtfServerPort = new JTextField(cc.getServerPort());
		jtfClientTimeOut = new JTextField(cc.getClientTimeOut());
		jtfClientAsServerPort = new JTextField(cc.getClientAsServerPort());
		
		JPanel pnlBottom = new JPanel();
		pnlBottom.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new SaveAction());		
		pnlBottom.add(btnSave);
		
		pnlCenter.setLayout(new GridLayout(4, 2));
		pnlCenter.add(lblServerName);
		pnlCenter.add(jtfServerName);
		pnlCenter.add(lblServerPort);
		pnlCenter.add(jtfServerPort);
		pnlCenter.add(lblClientTimeOut);
		pnlCenter.add(jtfClientTimeOut);
		pnlCenter.add(lblClientAsServerPort);
		pnlCenter.add(jtfClientAsServerPort);
		
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlBottom, BorderLayout.SOUTH);
		
		addWindowListener(new CloseAction());
	}
	
	/*
	 * EVENTS
	 */
	
	class CloseAction extends WindowAdapter{
		@Override
		public void windowClosing(WindowEvent e) {
			// à la fermeture de la fenêtre de settings, fermeture du dialog
			dispose();
		}		
	}

	class SaveAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// Récupération des infos entrées et définition via le controller
			cc.setClientAsServerPort(jtfClientAsServerPort.getText());
			cc.setClientTimeOut(jtfClientTimeOut.getText());
			cc.setServerName(jtfServerName.getText());
			cc.setServerPort(jtfServerPort.getText());
			
			// Tentative de sauvegarde et affichage du succès ou de l'erreur
			if(cc.saveSettings()){
				dispose();
				frame.showErrorDialog("Saved", "Settings saved successfully", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				dispose();
				frame.showErrorDialog("Error", "Settings couldn't be saved....", JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
}
