package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.usecase.CreateUserUseCase;
import br.roggen.recargapay.wallet.core.usecase.input.CreateUserInput;
import br.roggen.recargapay.wallet.core.usecase.output.CreateUserOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    final CreateUserUseCase useCase;

    public UserController(CreateUserUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public CreateUserOutput createUser(@RequestHeader("X-username") String username,
                                       @RequestHeader("X-password") String password){
        log.info("initialized");
        CreateUserOutput output = useCase.execute(new CreateUserInput(username, password));
        log.info("finished");
        return output;
    }
}
