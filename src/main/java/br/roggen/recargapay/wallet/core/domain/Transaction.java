package br.roggen.recargapay.wallet.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Transaction {
    private String id;
    private final Wallet wallet;
    private final TransactionType type;
    private final BigDecimal value;
    private BigDecimal balanceAfterOperation;
    private final LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public Transaction(Wallet wallet,
                       TransactionType type,
                       BigDecimal value) {
        this.wallet = wallet;
        this.type = type;
        this.value = value.setScale(2, RoundingMode.HALF_UP);
        startDateTime = LocalDateTime.now();
    }

    public static Transaction createDeposit(Wallet wallet,
                                            BigDecimal value){
        return new Transaction(wallet, TransactionType.DEPOSIT, value);
    }

    public static Transaction createWithdraw(Wallet wallet,
                                             BigDecimal value) {
        return new Transaction(wallet, TransactionType.WITHDRAWAL, value);
    }

    public void finish(Wallet wallet){
        this.endDateTime = LocalDateTime.now();
        this.balanceAfterOperation = wallet.getBalance();
    }

}
