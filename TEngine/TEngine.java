package TEngine;

import java.util.List;

import Client.ConnectionHandler;
import Common.Credentials;

public class TEngine {
	//TEngine is the main class of the translation engine which the server uses to communicate with the APIs
	//This class is a singleton
	
	private static TEngine singleton;
	
	//The TranslationService instance
	TranslationTaskHandler service;
	Thread serviceThread;
	
	//CONFIG
	final static int AMOUNT_OF_THREADS = 3; //Amount of threads which handle API calls at the same time
	static boolean ENGINE_RUNNING;
	
	//API Credentials
	Credentials googleCreds;
	Credentials watsonCreds;
	
	final String googleFile = "";
	final String watsonFile = "";
	
	//FILE structure
	//Login
	//Password
	//api key

	
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
		service = TranslationTaskHandler.getInstance();
		serviceThread = new Thread(service);
		serviceThread.start();
	}
	
	public void stop() {
		ENGINE_RUNNING = false;
	}
	
	public void takeTranslationRequest(TranslationRequest request) {
		try {
			service.sendRequest(request);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void takeTranslationRequest(List<TranslationRequest> reqList) {
		for(TranslationRequest req: reqList) {
			try {
				service.sendRequest(req);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
