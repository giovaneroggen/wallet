package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.exception.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserOrPasswordException.class)
    public ResponseEntity<ApiError> handleUserOrPassword(UserOrPasswordException ex) {
        log.error("UserOrPasswordException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", ex);
    }

    @ExceptionHandler(UnsuficientBalanceException.class)
    public ResponseEntity<ApiError> handleUnsuficientBalance(UnsuficientBalanceException ex) {
        log.error("UnsuficientBalanceException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Insufficient balance", ex);
    }

    @ExceptionHandler(WalletConflictException.class)
    public ResponseEntity<ApiError> handleWalletConflict(WalletConflictException ex) {
        log.error("WalletConflictException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.CONFLICT, "Conflict in wallet operation", ex);
    }

    @ExceptionHandler(TransferConflictException.class)
    public ResponseEntity<ApiError> handleTransferConflict(TransferConflictException ex) {
        log.error("TransferConflictException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.CONFLICT, "Transfer could not be completed due to concurrency conflict", ex);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiError> handleWalletNotFound(WalletNotFoundException ex) {
        log.error("WalletNotFoundException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.NOT_FOUND, "Wallet not found", ex);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiError> handleTransactionNotFound(TransactionNotFoundException ex) {
        log.error("TransactionNotFoundException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.NOT_FOUND, "Transaction not found", ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .toList();
        log.error("ConstraintViolationException: {} - errors={}", ex.getMessage(), errors, ex);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", ex, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", ex);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, Exception ex) {
        return buildResponse(status, message, ex, null);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, Exception ex, List<String> errors) {
        ApiError error = new ApiError(
                status.value(),
                message,
                ex.getClass().getSimpleName(),
                LocalDateTime.now(),
                errors
        );
        return ResponseEntity.status(status).body(error);
    }

    public record ApiError(
            int status,
            String message,
            String error,
            LocalDateTime timestamp,
            List<String> errors
    ) {}
}
