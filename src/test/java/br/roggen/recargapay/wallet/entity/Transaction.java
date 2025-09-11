package br.roggen.recargapay.wallet.entity;

import java.math.BigDecimal;

public class Transaction {
    private String id;
    private Wallet walletFrom;
    private Wallet walletTo;
    private TransactionType type;
    private BigDecimal value;
    private BigDecimal expectedBalanceAfterOperation;

    public Transaction(String id, Wallet walletFrom, Wallet walletTo, TransactionType type, BigDecimal value, BigDecimal expectedBalanceAfterOperation) {
        this.id = id;
        this.walletFrom = walletFrom;
        this.walletTo = walletTo;
        this.type = type;
        this.value = value;
        this.expectedBalanceAfterOperation = expectedBalanceAfterOperation;
    }

    public Transaction(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Wallet getWalletFrom() {
        return walletFrom;
    }

    public void setWalletFrom(Wallet walletFrom) {
        this.walletFrom = walletFrom;
    }

    public Wallet getWalletTo() {
        return walletTo;
    }

    public void setWalletTo(Wallet walletTo) {
        this.walletTo = walletTo;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getExpectedBalanceAfterOperation() {
        return expectedBalanceAfterOperation;
    }

    public void setExpectedBalanceAfterOperation(BigDecimal expectedBalanceAfterOperation) {
        this.expectedBalanceAfterOperation = expectedBalanceAfterOperation;
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }

    public static class Wallet {
        public String getId() {
            return id;
        }

        private String id;
        public Wallet(){}
        public Wallet(String id){
            this.id = id;
        }
    }
}