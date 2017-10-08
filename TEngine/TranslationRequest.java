package TEngine;

import java.util.ArrayList;
import java.util.List;

import Common.Message;
import Server.ClientSession;

public class TranslationRequest {
	Message message;
	ClientSession originSession;
	
	//Langues things
	String sourceLanguage;
	String targetLanguage;
	
	public TranslationRequest(Message message,ClientSession originSession, String sourceLanguage, String targetLanguage) {
		this.message = message;
		this.originSession = originSession;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
	}
	
	public static List<TranslationRequest> toTranslationRequestList(List<Message> msgList,ClientSession originSession, String senderLanguage, String targetLanguage) {
		List<TranslationRequest> reqList;
		reqList = new ArrayList<TranslationRequest>();
		for(Message msg: msgList){
			reqList.add(new TranslationRequest(msg,originSession,senderLanguage,targetLanguage));
		}
		
		return reqList;
		
	}
	
	public String getTargetLanguage() {
		return this.targetLanguage;
	}

}
