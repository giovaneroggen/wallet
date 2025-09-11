package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.exception.UserOrPasswordException;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.usecase.input.CreateUserInput;
import br.roggen.recargapay.wallet.core.usecase.output.CreateUserOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateUserUseCase {
    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CreateUserOutput execute(CreateUserInput input){
        var user = userRepository.findByUsername(input.username());
        if(user.isPresent()){
            log.error("user found userId={}", user.get().getId());
            throw new UserOrPasswordException();
        }
        var userCreated = userRepository.save(User.create(input.username(), input.password()));
        log.debug("user created userId={}", userCreated.getId());
        CreateUserOutput output = new CreateUserOutput(userCreated.getId());
        log.info("output={}", output);
        return output;
    }
}
