package br.roggen.recargapay.wallet.core.usecase.output;

import java.math.BigDecimal;

public record TransferOutput(String transferId,
                             String transactionIdFrom,
                             String transactionIdTo,
                             BigDecimal value,
                             BigDecimal balanceAfterOperation) {
}
