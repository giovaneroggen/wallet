package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.NotEmpty;

public record CreateWalletInput(@NotEmpty String username,
                                @NotEmpty String password){
    public CreateWalletInput(String username, String password) {
        this.username = username;
        this.password = password;

        InputValidator.valid(this);
    }
}