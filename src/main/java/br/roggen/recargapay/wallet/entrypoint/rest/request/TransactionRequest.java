package br.roggen.recargapay.wallet.entrypoint.rest.request;

import java.math.BigDecimal;

public record TransactionRequest(BigDecimal value) {
}
