package TEngine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.Message;
import Server.Server;

public class TranslationTaskHandler implements Runnable {
	//Translation service is the main class of the translation engine which processes all th API requests and manages them with sending them back to requestor 
	private static TranslationTaskHandler singleton;
	
	//The main task que of the application
	private BlockingQueue<TranslationRequest> taskQue = new ArrayBlockingQueue<TranslationRequest>(100);
	
	//the threadpool
	ExecutorService executor;
	
	private TranslationTaskHandler() {
		executor = Executors.newFixedThreadPool(TEngine.AMOUNT_OF_THREADS);;
	}
	
	public static TranslationTaskHandler getInstance() {
		if(singleton == null){
			synchronized (TranslationTaskHandler.class){
				singleton  = new TranslationTaskHandler();
			}
		}
		return singleton;
	}
	
	public void sendRequest(TranslationRequest request) throws InterruptedException{
		taskQue.put(request);
	}
	
	private boolean checkLanguage(String target) {
		
		return false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(TEngine.ENGINE_RUNNING) {
			if(!taskQue.isEmpty()){
				//Get the request
				try {
					TranslationRequest request;
					request = taskQue.take();
					boolean isTargetOnWatsonList = checkLanguage(request.getTargetLanguage());
					//Check if the target languge is one of the watson supported languages
					//if not then use the google service
					TranslationService service;
					if(isTargetOnWatsonList){
						service = new WatsonTEngine();
					}else{
						service = new GoogleTEngine();
					}
					executor.submit(service);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
