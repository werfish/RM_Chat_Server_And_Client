package Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.List;

import Common.Message;

public class Connection {
	Socket socket;
	DataInputStream input;
	DataOutputStream output;
	boolean isConnected;
	
	public Connection() {
		isConnected = false;
	}
	
	public void connect() throws UnknownHostException, IOException {
			socket = new Socket(Client.HOST_INET,Client.PORT);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			isConnected = true;

		System.out.println("Client connection intialized!!!");
	}
	
	public void sendMessage(Message message) throws IOException{
		output.writeUTF(Message.disassemble(message));
	}
	
	public boolean isConnected() {
		return isConnected;
	}
	
	public Message getMessage() throws IOException {
		String message;
		message = input.readUTF();
		return Message.assemble(message);	
		//ReadUTF methods reads the amount of letters send in UTF string which means there will not be problems with sepating messages
	}
	
	public void closeConnection() {
		try {
			socket.close();
			input.close();
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
