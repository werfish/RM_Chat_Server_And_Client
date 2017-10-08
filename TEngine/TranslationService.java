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
	private BlockingQueue<Message> taskQue = new ArrayBlockingQueue<Message>(100);
	
	//the threadpool
	ExecutorService executor;
	
	private TranslationService() {
		executor = Executors.newFixedThreadPool(TEngine.AMOUNT_OF_THREADS);;
	}
	
	public TranslationService getInstance() {
		if(singleton == null){
			synchronized (TranslationService.class){
				singleton  = new TranslationService();
			}
		}
		return singleton;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(TEngine.ENGINE_RUNNING) {
			if(!taskQue.isEmpty()){
				
			}
		}
	}
}
