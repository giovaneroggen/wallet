package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record RetrieveSpecificBalanceInput(@NotEmpty String username,
                                           @NotEmpty String password,
                                           @NotEmpty String walletId,
                                           @NotNull LocalDateTime specificDateTime) {
    public RetrieveSpecificBalanceInput(String username, String password, String walletId, LocalDateTime specificDateTime) {
        this.username = username;
        this.password = password;
        this.walletId = walletId;
        this.specificDateTime = specificDateTime;

        InputValidator.valid(this);
    }
}
