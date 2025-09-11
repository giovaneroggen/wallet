package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.NotEmpty;

public record RetrieveBalanceInput(@NotEmpty String username,
                                   @NotEmpty String password,
                                   @NotEmpty String walletId) {
    public RetrieveBalanceInput(String username, String password, String walletId) {
        this.username = username;
        this.password = password;
        this.walletId = walletId;

        InputValidator.valid(this);
    }
}
