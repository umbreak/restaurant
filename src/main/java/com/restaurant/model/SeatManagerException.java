package com.restaurant.model;

import java.util.UUID;

/**
 * Uncached exception
 */
public class SeatManagerException extends RuntimeException{
    public SeatManagerException(String message) {
        super(message);
    }

    public SeatManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
