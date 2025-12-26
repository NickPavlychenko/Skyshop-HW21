package org.skypro.skyshop.controller;

import org.skypro.skyshop.exception.NoSuchProductException;
import org.skypro.skyshop.model.ShopError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ShopControllerAdvice {

    @ExceptionHandler(NoSuchProductException.class)
    public ResponseEntity<ShopError> handleNoSuchProductException(NoSuchProductException e) {
        ShopError error = new ShopError(
                "PRODUCT_NOT_FOUND",  // Код ошибки
                e.getMessage()         // Сообщение
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)  // HTTP 404
                .body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ShopError> handleIllegalArgumentException(IllegalArgumentException e) {
        ShopError error = new ShopError(
                "INVALID_INPUT",
                e.getMessage()
        );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // HTTP 400
                .body(error);
    }
}