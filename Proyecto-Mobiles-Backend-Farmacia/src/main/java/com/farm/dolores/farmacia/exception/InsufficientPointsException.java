package com.farm.dolores.farmacia.exception;

public class InsufficientPointsException extends RuntimeException {
    public InsufficientPointsException(String message) {
        super(message);
    }

    public InsufficientPointsException(Integer required, Integer available) {
        super(String.format("Puntos insuficientes. Requeridos: %d, Disponibles: %d", required, available));
    }
}
