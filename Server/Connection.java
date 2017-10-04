package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import Common.Message;

public class Connection {
	Socket socket;
	String login;
	DataInputStream input;
	DataOutputStream output;
	BlockingQueue<Message> msgQue; 
	Boolean queInitialized;
	
	public Connection(Socket socket){
		this.socket = socket;
		queInitialized = false;
		try {
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Connection(Socket socket, String login){
		this.socket = socket;
		this.login = login;
	}
	
	public Socket getSocket(){
		return this.socket;
	}
	
	public String getLogin() {
		return this.login;
	}
	
	public void setSocket(Socket socket){
		this.socket = socket;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public boolean isQueInitialized(){
		return this.queInitialized;
	}
	
	public void initializeQue(){
		this.queInitialized = true;
		this.msgQue = new ArrayBlockingQueue<Message>(100);
	}
	
	public void decomisionQue(){
		this.msgQue = null;
		this.queInitialized = false;
	}
	
	public boolean addToQue(Message msg){
		if(isQueInitialized() == true){
			try {
				this.msgQue.put(msg);
				return true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;

	}
	
	//the method returns a list of all the cached messeges in the message que for this connection so the client can display them
	public List<Message> getCachedMessages() {
		List<Message> newList;
		newList = new ArrayList<Message>();
		
		for(Message msg: msgQue){
			try {
				newList.add(msgQue.take());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return newList;
	}
	
	public void removeUser(){
		decomisionQue();
		this.login = null;
	}
	
	public void closeConnection() {
		if(!(this.login == null)){
		removeUser();
		}
		try {
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMessage(Message message) throws IOException {
		output.writeUTF(Message.disassemble(message));
	}
	
	public Message getMessage() throws IOException {
		String message;
		message = input.readUTF(); //ReadUTF methods reads the amount of letters send in UTF string which means there will not be problems with sepating messages
		return Message.assemble(message);	
	}
	
	
	
}
