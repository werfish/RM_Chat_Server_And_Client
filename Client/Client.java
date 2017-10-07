package Client;

import javax.swing.JFrame;

public class Client {
	final static String APPNAME = "RM Chat App";
	final static String COMMAND_STARTER = "??"; 
	final static String HOST_INET = "localhost";
	final static int PORT = 5217;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//JFrame frame = new JFrame(APPNAME);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(500,500);
		
		//initalizaing the connection handler
		ConnectionHandler handler = new ConnectionHandler();
		Thread connThread = new Thread(handler);
		connThread.start();
		
		CardPanel login = CardPanel.getInstance();
		
		//frame.add(login);
		//frame.setVisible(true);

	}

}
