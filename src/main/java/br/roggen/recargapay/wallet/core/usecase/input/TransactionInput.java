package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

public record TransactionInput(@NotEmpty String username,
                               @NotEmpty String password,
                               @NotEmpty String walletId,
                               @DecimalMin("0.01") BigDecimal value) {
    public TransactionInput(String username, String password, String walletId, BigDecimal value) {
        this.username = username;
        this.password = password;
        this.walletId = walletId;
        this.value = value;

        InputValidator.valid(this);
    }
}
