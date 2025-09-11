package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.RetrieveBalanceInput;
import br.roggen.recargapay.wallet.core.usecase.output.RetrieveBalanceOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetrieveBalanceUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public RetrieveBalanceUseCase(WalletRepository walletRepository,
                                  UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public RetrieveBalanceOutput execute(RetrieveBalanceInput input){
        var user = userRepository.findRequiredByUsername(input.username());
        user.validatePassword(input.password());
        log.info("user found userId={}", user.getId());

        var wallet = walletRepository.findByIdAndUser(input.walletId(), user);
        log.info("wallet found walletId={}", wallet.getId());

        RetrieveBalanceOutput output = new RetrieveBalanceOutput(wallet.getBalance());
        log.info("output={}", output);
        return output;
    }
}
