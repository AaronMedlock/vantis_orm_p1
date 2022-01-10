package com.aaronmedlock.exception;

public class ClassNotAnnotatedWithIdException extends RuntimeException{

	public ClassNotAnnotatedWithIdException(String message) {
		super(message);
	}
}
