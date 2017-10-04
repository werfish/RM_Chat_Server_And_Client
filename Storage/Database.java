package Storage;

interface Database {
	
	public String readRow(int number);
	public String readRow(String user);
	public void insertRow(Record user);
	public int count();

}
