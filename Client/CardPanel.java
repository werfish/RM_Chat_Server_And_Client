package Client;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Common.Credentials;
import Common.Message;
import Common.MessageType;
import Common.User;


public class CardPanel extends JPanel implements ActionListener{
	//Configuration variables
	JFrame loginFrame = new JFrame(Client.APPNAME);
	Socket socket;
	
	//A panel which will hold the login panel
	JPanel loginScreenPanel = new JPanel();
	
	//the login panel will hold all the buttons and labels
	JPanel loginPanel = new JPanel(new GridLayout(3,2));
	
	//The whole application will be based on cardLayout
	
	//The labels for the Password and Login textBOXES
	JLabel loginLabel = new JLabel("Username: ");
	JLabel passwordLabel = new JLabel("Password: ");
	
	//the below are Text fields that will be used for password and login
	final JTextField usernameTextField = new JTextField("Login", 20);
	final JTextField passwordTextField = new JTextField("Password", 20);
	//the buttons for login or register
	JButton registerButton = new JButton("Register");
	JButton loginButton = new JButton("Login");
	
	//Card layout
	CardLayout card;
	
	//Variables holding the username and values
	String username;
	String password;
	
	CardPanel(){
		setLayout(new CardLayout());
		add(loginScreenPanel, "login");
		card = (CardLayout) getLayout();
		setFrame();
		setLoginPanel();
		setLoginScreen();
		setActonListeners();
		addCloseAdapter();
	}
	
	private void setActonListeners() {
		// TODO Auto-generated method stub
		this.registerButton.addActionListener(this);
		this.loginButton.addActionListener(this);
	}

	private void setFrame(){
		loginFrame.setSize(500, 400);
		loginFrame.add(this);
		loginFrame.setVisible(true);
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private void setLoginScreen() {
		loginScreenPanel.add(loginPanel);
	}
	
	private void setLoginPanel(){
		loginPanel.add(loginLabel);
		loginPanel.add(usernameTextField);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordTextField);
		loginPanel.add(loginButton);
		loginPanel.add(registerButton);
	}
	
	//important exit logoutStuff
	private void addCloseAdapter() {
		loginFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				ConnectionHandler.sendRequest(new Message("content",new User("App"),MessageType.LOGOUT));
				System.out.println("Client is logging out..............");
				ConnectionHandler.sendRequest(new Message("content",new User("App"),MessageType.DISCONNECT));
				System.out.println("Client is disconnecting..............");
				System.exit(1);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == registerButton){
			add(new RegisterScreen(),"register");
			card.show(this, "register");
		} 
		if(e.getSource() == loginButton) {
			System.out.println("Clicked Login!!!");
			this.username = usernameTextField.getText();
			this.password = passwordTextField.getText();
			Credentials cred = new Credentials(username,password);
			ConnectionHandler.sendRequest(cred.toMessage());
			Message loginAnswer  = 	null;
			while(loginAnswer == null){
				loginAnswer = ConnectionHandler.checkLogRegQue();
			}	
			System.out.println("Message from Server: " + loginAnswer.getType().toString() + " " + loginAnswer.getUsername() + " " + loginAnswer.getContent());
			if(loginAnswer.getContent().contains("SUCCESS")){
				add(new ChatWindow(cred.toMessage().getUser()),"MainScreen");
				card.show(this, "MainScreen");
			}
		}
	}
	
}
