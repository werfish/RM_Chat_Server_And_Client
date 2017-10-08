package TEngine;

import java.util.List;

import Client.ConnectionHandler;

public class TEngine {
	//TEngine is the main class of the translation engine which the server uses to communicate with the APIs
	//This class is a singleton
	
	private static TEngine singleton;
	
	//The TranslationService instance
	TranslationService service;
	
	//CONFIG
	final static int AMOUNT_OF_THREADS = 3; //;Amount of threads which handle API calls at the same time
	static boolean ENGINE_RUNNING;
	
	private TEngine() {
		
	}
	
	public TEngine getInstance() {
		if(singleton == null){
			synchronized (TEngine.class){
				singleton  = new TEngine();
			}
		}
		return singleton;
	}
	
	public void start() {
		ENGINE_RUNNING = true;
	}
	
	public void stop() {
		ENGINE_RUNNING = false;
	}
	
	public void getTranslationRequest(TranslationRequest request) {
		
	}
	
	public void getTranslationRequest(List<TranslationRequest> reqList) {
		
	}
}
