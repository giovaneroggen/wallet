package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.repository.TransactionRepository;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.RetrieveSpecificBalanceInput;
import br.roggen.recargapay.wallet.core.usecase.output.RetrieveBalanceOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RetrieveSpecificHistoricalBalanceUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public RetrieveSpecificHistoricalBalanceUseCase(WalletRepository walletRepository,
                                                    UserRepository userRepository,
                                                    TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public RetrieveBalanceOutput execute(RetrieveSpecificBalanceInput input){
        var user = userRepository.findRequiredByUsername(input.username());
        log.info("user found userId={}", user.getId());
        user.validatePassword(input.password());

        var wallet = walletRepository.findByIdAndUser(input.walletId(), user);
        log.info("wallet found walletId={}", wallet.getId());
        var transaction = transactionRepository.findLatest(wallet, input.specificDateTime());
        log.info("latest transaction found transactionId={}", transaction.getId());
        RetrieveBalanceOutput output = new RetrieveBalanceOutput(transaction.getBalanceAfterOperation());
        log.info("output={}", output);
        return output;
    }
}
