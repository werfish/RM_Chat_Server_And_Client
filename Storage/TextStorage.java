package Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TextStorage implements Database{
	String schema = "Name,Surname,Username,Password";
	//Name,Suername,Username,Password
	//Robert,Mazurowski,Robert,Haslo
	
	String fileName = "Users.CSV";
	String path;
	File dataFile;
	
	//STREAMS AND FILE READERS AND WRITERS
	FileWriter writer;
	FileReader reader;
	PrintWriter printer;
	BufferedReader bufReader;
	BufferedWriter bufWriter;
	
	public TextStorage() {
		try {
			//Initializing all File
			String path = getCurrentPath();
			dataFile = new File(fileName);

			//Check if the file exists Users.csv
			//if not create the file with the headers
			if(!checkFileExists(dataFile)){
				createFile(dataFile);
				//Initializing all the stream stuff
				initializeStreams();
				printLn(this.schema);
			}else{
				initializeStreams();
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void resetReader() {
		try {
			this.bufReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.reader = new FileReader(dataFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bufReader = new BufferedReader(reader);
	}
	
	private void printLn(String row) {
		this.printer.println(row);
		this.printer.flush();
	}
	
	private void initializeStreams() {;
		try {
			writer = new FileWriter(dataFile,true);
			bufWriter = new BufferedWriter(writer);
			printer = new PrintWriter(bufWriter);
			reader = new FileReader(dataFile);
			bufReader = new BufferedReader(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getCurrentPath() throws IOException {
		File file = new File(".");
		return file.getCanonicalPath();
	}
	
	private boolean checkFileExists(File file) {
		if(file.exists() && !file.isDirectory()) { 
		    return true;
		}
		return false;	
	}
	
	private void createFile(File file) throws IOException {
		file.createNewFile();
	}
	
	public String readRow(int number){
		int i = 1;
		number = number + 1; //compensate for the header row as we want a number of record
		String line = "";
		try {
			for(;i <= number;i++){
			line = bufReader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("ReadRow: " + line);
		resetReader();
		return line;
	}
	
	public String readRow(String user){
		int i = 1;
		String line = "";
		try {
			while((line = bufReader.readLine()) != null){
				if(line.contains(user)){
					break;
				}
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resetReader();
		if(line == null){
			return "";
		}else{
			return line;
		}
	}
	
	public int count(){
		int i = 0;
		String line;
		try {
			while((line = bufReader.readLine()) != null){
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resetReader();
		return i-1; //compensating for the header row as we want the count of records
	}
	
	public void closeStuff() throws IOException {
		reader.close();
		bufReader.close();
		printer.close();
		writer.close();
		bufWriter.close();
	}

	@Override
	public void insertRow(Record user) {
		// TODO Auto-generated method stub
		String row;
		row = user.getName() + ";";
		row = row + user.getSurname() + ";";
		row = row + user.login + ";";
		row = row + user.password;
		printLn(row);
	}
}
