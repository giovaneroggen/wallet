package br.roggen.recargapay.wallet.core.usecase;


import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.CreateWalletInput;
import br.roggen.recargapay.wallet.core.usecase.output.CreateWalletOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateWalletUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public CreateWalletUseCase(WalletRepository walletRepository,
                               UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public CreateWalletOutput execute(CreateWalletInput input){
        var user = userRepository.findRequiredByUsername(input.username());
        log.info("user found userId={}", user.getId());
        user.validatePassword(input.password());

        var wallet = walletRepository.save(Wallet.create(user));
        log.debug("wallet created walletId={}", wallet.getId());

        CreateWalletOutput output = new CreateWalletOutput(wallet.getId());
        log.info("output={}", output);
        return output;
    }
}
