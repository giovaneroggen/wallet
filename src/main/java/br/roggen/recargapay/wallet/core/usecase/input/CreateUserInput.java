package br.roggen.recargapay.wallet.core.usecase.input;

import br.roggen.recargapay.wallet.core.usecase.input.validator.InputValidator;
import jakarta.validation.constraints.NotEmpty;

public record CreateUserInput(@NotEmpty String username,
                              @NotEmpty String password){
    public CreateUserInput(String username, String password) {
        this.username = username;
        this.password = password;

        InputValidator.valid(this);
    }
}