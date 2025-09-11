package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

public record TransferInput(@NotEmpty String username,
                            @NotEmpty String password,
                            @NotEmpty String walletIdFrom,
                            @NotEmpty String walletIdTo,
                            @DecimalMin("0.01") BigDecimal value) {
    public TransferInput(String username, String password, String walletIdFrom, String walletIdTo, BigDecimal value) {
        this.username = username;
        this.password = password;
        this.walletIdFrom = walletIdFrom;
        this.walletIdTo = walletIdTo;
        this.value = value;

        InputValidator.valid(this);
    }
}
