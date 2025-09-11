package br.roggen.recargapay.wallet.fixture;

import br.roggen.recargapay.wallet.core.domain.TransactionType;
import br.roggen.recargapay.wallet.dataprovider.database.entity.TransactionEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import br.roggen.recargapay.wallet.entrypoint.rest.request.TransactionRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class TransactionFixture {

    public static TransactionEntity createTransaction(WalletEntity wallet, TransactionType transactionType,
                                                      BigDecimal value, LocalDateTime endDateTime) {
        return new TransactionEntity(null, wallet, transactionType,
                value, getBalanceAfterOperation(wallet, value, transactionType),
                endDateTime, endDateTime);

    }

    private static BigDecimal getBalanceAfterOperation(WalletEntity wallet, BigDecimal value, TransactionType transactionType) {
        BigDecimal balance = wallet.getBalance();
        switch (transactionType){
            case WITHDRAWAL -> balance = balance.subtract(value).setScale(2, RoundingMode.HALF_UP);
            case DEPOSIT -> balance = balance.add(value).setScale(2, RoundingMode.HALF_UP);
        };
        wallet.setBalance(balance);
        return balance;
    }

    public static TransactionRequest invalidRequest() {
        return new TransactionRequest(BigDecimal.ZERO);
    }

    public static TransactionRequest validRequest() {
        return new TransactionRequest(BigDecimal.TWO);
    }

    public static TransactionRequest request(BigDecimal value) {
        return new TransactionRequest(value);
    }
}
