package com.proativo.util.exception;

public class TratamentoException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	 public TratamentoException(String message) {
	        super(message);
	    }

	    public TratamentoException(String message, Throwable throwable) {
	        super(message, throwable);
	    }

}
