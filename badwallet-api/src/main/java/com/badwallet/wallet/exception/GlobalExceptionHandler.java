package com.badwallet.wallet.exception;

import com.badwallet.wallet.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestResponse<Void> handleEntityNotFoundException(EntityNotFoundException ex) {
        return RestResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public RestResponse<Void> handleEntityExistsException(EntityExistsException ex) {
        return RestResponse.error(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse<Void> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return RestResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentServiceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public RestResponse<Void> handlePaymentServiceException(PaymentServiceException ex) {
        return RestResponse.error(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return RestResponse.error("Erreur de validation", HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public RestResponse<Void> handleGenericException(Exception ex) {
        return RestResponse.error("Erreur interne du serveur", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
