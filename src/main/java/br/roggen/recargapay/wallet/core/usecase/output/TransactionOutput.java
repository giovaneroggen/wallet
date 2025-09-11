package br.roggen.recargapay.wallet.core.usecase.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionOutput(String transactionId,
                                String walletId,
                                BigDecimal balanceAfterOperation,
                                LocalDateTime startDate,
                                LocalDateTime endDate) {
}
