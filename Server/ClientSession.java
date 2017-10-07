package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import Common.Commands;
import Common.Credentials;
import Common.Message;
import Common.MessageType;
import Common.User;
import Common.Users;
import Storage.StorageHandler;

public class ClientSession implements Runnable {
	Connections connections;
	Connection conn;
	Users usersList;
	List<Message> msgBuffer;
	
	DataInputStream input;
	DataOutputStream output;
	
	//SESSION FLAGS
	Boolean LOGGED_IN;
	String CONNECTED_USER;
	
	public ClientSession(Connections connections, Connection conn, Users usersList) throws IOException {
		this.connections = connections;
		this.conn = conn;
		this.usersList = usersList;
		System.out.println("Session Constructed");
		List<Message> msgToSend = null; //List of messages cached which is set on the GetMsgAmount command
		//SET FLAGS
		boolean LOGGED_IN = false;
		String CONNECTED_USER = "APP";
		
		
		connections.addConnection(conn);
		
		input = new DataInputStream(conn.getSocket().getInputStream());
		output = new DataOutputStream(conn.getSocket().getOutputStream());
	}

	@Override
	public void run() {
		System.out.println("Session started!");
		// TODO Auto-generated method stub
		while(true) {
			
			try {
				Message receivedMessage;
				receivedMessage = readMessage();
				System.out.println("New message received of type: " + receivedMessage.getType().toString() + " " + receivedMessage.getUsername() + " " + receivedMessage.getContent());
				
				if(receivedMessage.getType() == MessageType.REGISTER){
					boolean register = register(receivedMessage);
					System.out.println("Register message received");
					continue;
				}else if(receivedMessage.getType() == MessageType.LOGIN){
					System.out.println("Login message received");
					boolean login = login(receivedMessage);
					System.out.println("Login message Processed");
					if(login == true){
						LOGGED_IN = true;
						usersList.addUser(receivedMessage.getUser());
						connections.addUserToConnection(conn.getSocket(),receivedMessage.getUser());
						this.CONNECTED_USER = receivedMessage.getUsername();
					}
					continue;
				}else if(receivedMessage.getType() == MessageType.DISCONNECT){
					System.out.println("DISCONNECT PROCEDURE LOOK HERE!!!!!!!!!!!!!!!!!!!!");
					disconnect();
					break;
				}
				//AFTER USER LOGIN PROTOCOL
				if(LOGGED_IN == true){
					if(receivedMessage.getType() == MessageType.MESSAGE){
						distribute(receivedMessage);
					}else if(receivedMessage.getType() == MessageType.COMMAND){
						if(receivedMessage.getContent().equals(Commands.GetLoggedInUsers.toString())){
							Message answer;
							answer = this.usersList.toMessage();
							sendMessage(answer);
						}else if(receivedMessage.getContent().equals(Commands.ClientMsgAmount.toString())){
							msgBuffer = getMsgAmount();
						}else if(receivedMessage.getContent().equals(Commands.ClientMsgFlush.toString())){
							msgBuffer = getMsgAmount();
							clientMsgFlush(this.msgBuffer);
							this.msgBuffer  = null;
						}		
					}else if(receivedMessage.getType() == MessageType.LOGOUT){
						System.out.println("LOGOUZT PROCEDURE PLEASE LOOK HERE!@#!@!@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						logout();
						System.out.println("Ater logout procedure");
						continue;
					}
					
				}
				//NON LOGIN DEPENDENT PROTOCOL
				//Message which do not matter whehther logged in or not
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}
	
	private void disconnect() {
		//Close streams
		try {
			input.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connections.removeConnection(conn);
		conn.closeConnection();
	}
	
	private void logout(){
		System.out.println("Start Logout");
		LOGGED_IN = false;
		System.out.println("Pre msgBuffer");
		if(!(msgBuffer == null)){
			msgBuffer = null;
		}
		//Delete the use from users list
		System.out.println("Pre removeUser");
		usersList.removeUser(new User(this.CONNECTED_USER));
		System.out.println("Post removeUser");
		CONNECTED_USER = "APP";
		//remove the user from the connections list
		System.out.println("Pre rremove user");
		conn.removeUser();
		System.out.println("Post remove user");
	}
	
	private List<Message> getMsgAmount() {
		List<Message> msgList = conn.getCachedMessages(); //get the list of messages and count the numbers
		int countMsg = msgList.size();
		
		//Send the amount of messages to be sent to the client so the client can prepare how many will receive
		Message amountMsg;
		amountMsg = new Message(String.valueOf(countMsg),new User("Server"), MessageType.MESSAGE);
		sendMessage(amountMsg);
		
		return msgList;
	}
	
	private void clientMsgFlush(List<Message> list) {
		for(Message msg: list){
			sendMessage(msg);
		}
		list = null;
	}
	
	private boolean register(Message message) {
		Message query;
		Message answer;
		Users dbUsers;
		
		//First query the database for list of all users to check if it already exists
		query = new Message(Commands.GetUsers.toString(),message.getUser(),MessageType.COMMAND);
		answer = StorageHandler.sendQuery(query);
		dbUsers = new Users(answer);
		System.out.println("Users: " + dbUsers.toMessage().getContent());
		//if the username exists then send a message that user exists to the client
		if(dbUsers.isUser(message.getUser())){
			sendMessage(new Message("Username Already Exists. ",new User("Server"),MessageType.REGISTER));
			System.out.println("User exists in DB");
			return false;
		}
		//else add the data to the database, by sending the received message as a query
		answer = StorageHandler.sendQuery(message);
		//Send the response to the client
		sendMessage(answer);
		return true;		
	}
	
	private boolean login(Message loginQuery) {
		Message answer;
		Credentials creds = new Credentials(loginQuery.getContent());
		Credentials dbCreds;
		
		answer = StorageHandler.sendQuery(loginQuery);
		dbCreds = new Credentials(answer.getContent());
				
		//Login Validatiion
		//check if user exists in the database
		if(dbCreds.toUser().getUsername().equals("NULL")){
			System.out.println("Login prcoess: User does not exist!");
			sendMessage(new Message("FAILURE: User does not exist!",new User("Server"),MessageType.LOGIN));
			return false;
		}else if(!dbCreds.checkPass(creds)){ //check if password is not correct correct(if correct move on to the next validation)
			System.out.println("Login prcoess: Wrong password!");
			sendMessage(new Message("FAILURE: Wrong password",new User("Server"),MessageType.LOGIN));
			return false;
		}else if(this.usersList.isUser(creds.toUser())){ 		//check if user is logged in
			System.out.println("Login prcoess: User logged in!");
			sendMessage(new Message("FAILURE: User already logged in",new User("Server"),MessageType.LOGIN));
			return false;
		}//if validation didn't find anything wrong then proceed with login
		System.out.println("Login prcoess SUCCESS");
		sendMessage(new Message("SUCCESS",new User("Server"),MessageType.LOGIN));
		System.out.println("Login Success Message Sent");
		return true;
	}
	
	//THIS was BAD Mmkay
	private void distribute(Message msg){
		for(int i = 0; i < this.usersList.size();i++){
			User usr = usersList.getUser(i);
			Connection conn = connections.getConnection(usr);
			if(!(conn == null) && !conn.getLogin().equals(CONNECTED_USER)){
				conn.addToQue(msg);
			}
		}
	}
	
	///////////////////////SEND READ////////////////
	
	private Message readMessage() throws IOException {
		String receivedString;
		receivedString = input.readUTF();
		Message receivedMessage = Message.assemble(receivedString);

		return receivedMessage;
	}
	
	private void sendMessage(Message message){
		
		try {
			output.writeUTF(Message.disassemble(message));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}


