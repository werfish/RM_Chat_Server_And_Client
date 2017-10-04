package Common;

import java.util.StringTokenizer;

public class Credentials {
	
	private String USERNAME;
	private String PASSWORD;
	
	public Credentials(String username, String password) {
		this.USERNAME = username;
		this.PASSWORD = password;
	}
	
	public Credentials(String content) {
		contentToCredentials(content);
	}
	
	private void contentToCredentials(String content) {
		StringTokenizer st = new StringTokenizer(content);
		this.USERNAME = st.nextToken();
		this.PASSWORD  = st.nextToken();
	}
	
	public User toUser(){
		return new User(USERNAME);
	}
	
	public boolean checkPass(Credentials creds){
		if(this.PASSWORD.equals(creds.getPass())){
			return true;
		}else{
			return false;
		}
	}
	
	private String getPass(){
		return this.PASSWORD;
	}
	
	public Message toMessage(){
		String content = USERNAME + " " + PASSWORD;
		return new Message(content,new User(USERNAME),MessageType.LOGIN);
	}
	
}
