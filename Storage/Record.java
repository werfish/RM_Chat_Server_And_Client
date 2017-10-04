package Storage;

import java.util.StringTokenizer;

import Common.Message;
import Common.MessageType;

public class Record {
	String name;
	String surname;
	String login;
	String password;
	String isEmpty;
	
	public Record(String rawData) {
		if(!rawData.isEmpty() || !rawData.equals("")){
			StringTokenizer st;
			st = new StringTokenizer(rawData,";");
			setName(st.nextToken());
			setSurname(st.nextToken());
			setLogin(st.nextToken());
			setPassword(st.nextToken());
		}else{
			setName("NULL");
			setSurname("NULL");
			setLogin("NULL");
			setPassword("NULL");
		}
	}
	
	public Record(Message regMsg){
		StringTokenizer st;
		st = new StringTokenizer(regMsg.getContent());
		setName(st.nextToken());
		setSurname(st.nextToken());
		setLogin(st.nextToken());
		setPassword(st.nextToken());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
