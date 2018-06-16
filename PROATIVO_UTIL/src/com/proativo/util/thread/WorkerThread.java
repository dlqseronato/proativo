package com.proativo.util.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Percorre a coleção setada para a classe e executa o processo de validação
 * definido na classe Validate.
 * 
 * @author G0002730
 */
public class WorkerThread extends Observable implements Runnable
{
	private List<?> list = null;

	@SuppressWarnings("rawtypes")
	private ActionAbstract validateObject = null;
	
	private String threadName = null;
			
	public WorkerThread(Observer o, List<?> l, ActionAbstract<?> v, String threadName){		
		this.addObserver(o);
		this.list = l;
		this.validateObject = v;
		this.threadName = threadName;
	}
	
	public void run(){
		if (this.list != null){
			Iterator<?> it = this.list.iterator();
			
			while (it.hasNext()){
				validate(it.next());
			}
		}
		this.setChanged();
		this.notifyObservers(validateObject.getClass());
	}

	@SuppressWarnings("unchecked")
	private void validate(Object reg) {
		if (validateObject != null) validateObject.exec(reg);
	}

	public String toString() {
		return this.threadName;
	}
}