package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TransactionNotFoundException.class)
    public String handleTransactionNotFoundException(TransactionNotFoundException ex) {
        log.error("exception occurred", ex);
        return "transaction not found";
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(UnsuficientBalanceException.class)
    public String handleUnsuficientBalanceException(UnsuficientBalanceException ex) {
        log.error("exception occurred", ex);
        return HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserOrPasswordException.class)
    public String handleUserOrPasswordNotFoundException(UserOrPasswordException ex) {
        log.error("exception occurred", ex);
        return HttpStatus.UNAUTHORIZED.getReasonPhrase();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WalletNotFoundException.class)
    public String handleWalletNotFoundException(WalletNotFoundException ex) {
        log.error("exception occurred", ex);
        return "wallet not found";
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException ex){
        log.error("exception occurred", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(error ->
                errors.put(error.getPropertyPath().toString(), error.getMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException ex){
        log.error("exception occurred", ex);
        return "try again later";
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(WalletConflictException.class)
    public String handleWalletConflictException(WalletConflictException ex){
        log.error("exception occurred", ex);
        return HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase();
    }
}
