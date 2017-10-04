package Storage;

import Common.Credentials;
import Common.Message;
import Common.User;
import Common.Users;

public class Storage {
	String storageType;
	Database database;
	
	public Storage(String storageType){
		this.storageType = storageType;
		
		if(this.storageType.equals("Custom")){
			database = new TextStorage();
		}
	}
	
	public Message getUsersList() {
		Users list;
		
		list = new Users();
		System.out.println("Database records count: " + database.count());
		for(int i = 1; i <= database.count();i++){
			System.out.println("getUsers iteration: " + i + " row value: " + database.readRow(i) );
			Record line = new Record(database.readRow(i));
			list.addUser(new User(line.getLogin()));
		}
		return list.toMessage();
	}
	
	public void insertRegDetails(Message msg) {
		Record data = new Record(msg);
		database.insertRow(data);
	}
	
	public Message getCredentials(User user) {
		Message msg;
		Record line;
		Credentials creds;
		
		line = new Record(database.readRow(user.getUsername()));
		creds = new Credentials(line.getLogin(),line.getPassword());
		
		msg = creds.toMessage();
		
		return msg;
	}

}
