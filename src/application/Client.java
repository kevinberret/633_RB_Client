package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;

public class Client {
	public static void main(String[] args) {
		InetAddress serverAddress;
		String message_distant = "";
		Socket mySocket;
		BufferedReader buffin;
		PrintWriter pout;
        String serverName = "192.168.108.10";
        String defaultFolder = "C:\temp\633-2";
        /*
        Scanner scan = new Scanner(System.in);
        
        System.out.printf("Which folder would you like to share? [default: %s] (press enter to keep default...)\n", defaultFolder);
        String folder = scan.nextLine();
        
        System.out.print(folder);*/
        
        JFrame mainWindow = new Window();
        mainWindow.setVisible(true);
        
		/*try {
			serverAddress = InetAddress.getByName(serverName);
			System.out.println("Get the address of the server : "+ serverAddress);

			//try to connect to the server
			mySocket = new Socket(serverAddress, 45000);

			//get an output stream to send data to the server
			pout = new PrintWriter(mySocket.getOutputStream());

			//listen to the input from the socket
			//exit when the order quit is given			
			while(true)
			{	
				// send ip address and file list to the server
				pout.println(message_distant);
				pout.flush();		

				//display message received by the server
				System.out.println("\nMessage received from server:\n" + message_distant);

				//if quit then exit the loop
				if (message_distant.equals("quit"))
				{
					System.out.println("\nquit sent from server...");
					break;
				}			        
			}

			//send back the message to the server to kill it
			pout.close();
			buffin.close();
			mySocket.close();

			System.out.println("\nTerminate program...");


		}catch (UnknownHostException e) {
			e.printStackTrace();
		}catch (IOException e) {
			System.out.println("server connection error, dying.....");
		}*/
	}
}