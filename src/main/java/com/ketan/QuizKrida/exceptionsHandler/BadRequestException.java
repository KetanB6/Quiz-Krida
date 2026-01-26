package com.ketan.QuizKrida.exceptionsHandler;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message) {
        super(message);
    }
}
