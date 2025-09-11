package br.roggen.recargapay.wallet.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
public class Transfer {
    private String id;
    private final Transaction transactionFrom;
    private final Transaction transactionTo;
    private final BigDecimal value;
    private final BigDecimal balanceAfterOperation;

    public Transfer(Transaction transactionFrom,
                    Transaction transactionTo) {
        this.transactionFrom = transactionFrom;
        this.transactionTo = transactionTo;
        this.value = transactionFrom.getValue().setScale(2, RoundingMode.HALF_UP);
        balanceAfterOperation = transactionFrom.getBalanceAfterOperation().setScale(2, RoundingMode.HALF_UP);
    }
}
