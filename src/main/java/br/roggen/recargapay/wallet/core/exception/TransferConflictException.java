package br.roggen.recargapay.wallet.core.exception;

public class TransferConflictException extends RuntimeException {
    public TransferConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
