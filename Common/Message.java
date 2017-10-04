package Common;
import java.awt.Color;
import java.awt.Font;
import java.util.StringTokenizer;

import Client.Client;

public class Message {
	String content;
	User sender;
    MessageType type;
    Font font;
    Color color;
	
	public Message(String content,User sender){
		this.content = content;
		this.sender = sender;
	}
	
	public Message(String content,User sender, MessageType type){
		this.content = content;
		this.sender = sender;
		this.type = type;
	}
	
	public static String disassemble(Message message){
		String decomposed;
		
		decomposed = message.type.toString() + " " + message.getUsername() + " " + message.getContent();
		
		return decomposed;
		
	}
	
	public static Message assemble(String decomposed){
		Message message;
		
		StringTokenizer st = new StringTokenizer(decomposed);
		MessageType type = MessageType.valueOf(st.nextToken());
		String user = st.nextToken();
		String content = "";
		
		content = st.nextToken();
		while(st.hasMoreTokens()){
			content = content + " " + st.nextToken();
		}
		
		message = new Message(content, new User(user), type);
		
		return message;
		
	}
	
	public String getContent(){
		return this.content;
	}
	
	public String getUsername(){
		return sender.getUsername();
	}
	
	public User getUser(){
		return sender;
	}
	
	public MessageType getType(){
		return this.type;
	}
	
}
