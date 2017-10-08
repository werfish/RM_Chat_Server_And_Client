package TEngine;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Common.Message;
import Server.Server;

public class TranslationService implements Runnable {
	//Translation service is the main class of the translation engine which processes all th API requests and manages them with sending them back to requestor 
	private static TranslationService singleton;
	
	//The main task que of the application
	private BlockingQueue<TranslationRequest> taskQue = new ArrayBlockingQueue<TranslationRequest>(100);
	
	//the threadpool
	ExecutorService executor;
	
	private TranslationService() {
		executor = Executors.newFixedThreadPool(TEngine.AMOUNT_OF_THREADS);;
	}
	
	public static TranslationService getInstance() {
		if(singleton == null){
			synchronized (TranslationService.class){
				singleton  = new TranslationService();
			}
		}
		return singleton;
	}
	
	public void sendRequest(TranslationRequest request) throws InterruptedException{
		taskQue.put(request);
	};

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(TEngine.ENGINE_RUNNING) {
			if(!taskQue.isEmpty()){
				//Get the request
				try {
					TranslationRequest request = taskQue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
}
