package fr.univ_rouen.categorymanagement.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class InvalidCategoryException extends RuntimeException {

    public InvalidCategoryException(String message) {
        super(message);
    }

    public InvalidCategoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

