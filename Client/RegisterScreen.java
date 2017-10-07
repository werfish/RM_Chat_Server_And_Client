package Client;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Common.Message;
import Common.MessageType;
import Common.User;

public class RegisterScreen extends JPanel implements ActionListener {	
	JPanel registerPanel = new JPanel();
	
	//All labels for the screen
	final JLabel nameLabel = new JLabel("First Name: ");
	final JLabel surnameLabel = new JLabel("Surname: ");
	final JLabel loginLabel = new JLabel("Login: ");
	final JLabel passLabel = new JLabel("Password: ");
	final JLabel rpassLabel = new JLabel("Repeat pass: ");
	
	//All the textfields
	final JTextField firstNameField = new JTextField("Name",20);
	final JTextField surnameField = new JTextField("Surname",20);
	final JTextField usernameTextField = new JTextField("Login", 20);
	final JTextField passwordTextField = new JTextField("Password", 20);
	final JTextField repeatPasswordTextField = new JTextField("Repaat Pass",20);
	
	//buttons to accept and send or to cancel
	JButton acceptButton = new JButton("Accept");
	JButton cancelButton = new JButton("Cancel");
	
	//Connection handler singleton
	private ConnectionHandler conn;
	
	//Variables holding the username and values
	String name;
	String surname;
	String username;
	String password;
	String reapeatPass;
	
	RegisterScreen(){
		add(registerPanel);
		registerPanel.setLayout(new GridLayout(6,2));
		setPanel();
		setActionListeners();
		conn = ConnectionHandler.getInstance();
	}
	
	private void setActionListeners() {
		// TODO Auto-generated method stub
		cancelButton.addActionListener(this);
		acceptButton.addActionListener(this);
	}

	private void setPanel(){
		registerPanel.add(nameLabel);
		registerPanel.add(firstNameField);
		
		registerPanel.add(surnameLabel);
		registerPanel.add(surnameField);
		
		registerPanel.add(loginLabel);
		registerPanel.add(usernameTextField);
		
		registerPanel.add(passLabel);
		registerPanel.add(passwordTextField);
		
		registerPanel.add(rpassLabel);
		registerPanel.add(repeatPasswordTextField);
		
		//Buttons
		registerPanel.add(acceptButton);
		registerPanel.add(cancelButton);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == cancelButton){
			CardPanel login = CardPanel.getInstance();
			login.getCardLayout().show(login, "login");
		}if(e.getSource() == acceptButton){
			System.out.println("Register Clicked!!!");
			this.name = firstNameField.getText();
			this.surname = surnameField.getText();
			this.username = usernameTextField.getText();
			this.password = passwordTextField.getText();
			
			Message registerMessage;
			String content = name + " " + surname + " " + username + " " + password;
			registerMessage = new Message(content,new User(username),MessageType.REGISTER);
			System.out.println(registerMessage.getContent() + " " + registerMessage.getUsername() + " " + registerMessage.getType().toString());
			
			conn.sendRequest(registerMessage);
			Message answer = null;
			while(answer == null){
				answer = conn.checkLogRegQue();
			}
			System.out.println(answer.getContent());
		}
	}
	
}
