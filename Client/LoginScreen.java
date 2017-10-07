package Client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Common.Credentials;
import Common.Message;

public class LoginScreen extends JPanel implements ActionListener {
	
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
	
	//Connection handler singleton
	private ConnectionHandler conn;
	
	//Variables holding the username and values
	String username;
	String password;

	public LoginScreen() {
		setLoginPanel();
		setLoginScreen();
		setActonListeners();
		conn = ConnectionHandler.getInstance();
	}
	
	private void setLoginPanel(){
		loginPanel.add(loginLabel);
		loginPanel.add(usernameTextField);
		loginPanel.add(passwordLabel);
		loginPanel.add(passwordTextField);
		loginPanel.add(loginButton);
		loginPanel.add(registerButton);
	}
	
	private void setLoginScreen() {
		this.add(loginPanel);
	}
	
	private void setActonListeners() {
		// TODO Auto-generated method stub
		this.registerButton.addActionListener(this);
		this.loginButton.addActionListener(this);
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == registerButton){
			CardPanel login = CardPanel.getInstance();
			login.add(new RegisterScreen(),"register");
			login.getCardLayout().show(login, "register");
		} 
		if(e.getSource() == loginButton) {
			System.out.println("Clicked Login!!!");
			this.username = usernameTextField.getText();
			this.password = passwordTextField.getText();
			Credentials cred = new Credentials(username,password);
			conn.sendRequest(cred.toMessage());
			Message loginAnswer  = 	null;
			while(loginAnswer == null){
				loginAnswer = conn.checkLogRegQue();
			}	
			System.out.println("Message from Server: " + loginAnswer.getType().toString() + " " + loginAnswer.getUsername() + " " + loginAnswer.getContent());
			if(loginAnswer.getContent().contains("SUCCESS")){
				CardPanel login = CardPanel.getInstance();
				login.add(new ChatWindow(cred.toMessage().getUser()),"MainScreen");
				login.getCardLayout().show(login, "MainScreen");
			}
		}
	}

}
