package com.kareemmushtaha.library.exception;

// my own exception, I use it when something wrong happens in the library rules
public class LibraryException extends Exception {

    public LibraryException(String message) {
        super(message);
    }
}