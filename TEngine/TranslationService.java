package TEngine;

import Common.Credentials;

public interface TranslationService extends Runnable{
	
	public Credentials cacheServiceCredentials();
	public void setTargetLanguage();
	public void setCurrentLanguage();
	public String Translate();
	public void run();
}
