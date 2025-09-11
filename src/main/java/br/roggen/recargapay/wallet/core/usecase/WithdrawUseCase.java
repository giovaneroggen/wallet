package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.core.repository.TransactionRepository;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.TransactionInput;
import br.roggen.recargapay.wallet.core.usecase.output.TransactionOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class WithdrawUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawUseCase(WalletRepository walletRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Each retry gets a new transaction
    @Retryable(retryFor = DataIntegrityViolationException.class, backoff = @Backoff(delay = 5000))
    public TransactionOutput execute(TransactionInput input){
        var user = userRepository.findRequiredByUsername(input.username());
        log.info("user found userId={}", user.getId());
        user.validatePassword(input.password());

        var wallet = walletRepository.findByIdAndUser(input.walletId(), user);
        log.info("wallet found walletId={}", wallet.getId());

        var transaction = Transaction.createWithdraw(wallet, input.value());
        log.info("creating transaction");
        log.debug("withdraw initialized wallet={} value={} balance={}", wallet.getId(), input.value(), wallet.getBalance());
        wallet.withdraw(input.value());
        transaction.finish(wallet);
        log.debug("withdraw finished wallet={} value={} balance={}", wallet.getId(), input.value(), wallet.getBalance());

        this.walletRepository.save(wallet);
        transaction = this.transactionRepository.save(transaction);

        log.info("commit transaction");

        TransactionOutput output = new TransactionOutput(transaction.getId(),
                wallet.getId(),
                transaction.getBalanceAfterOperation(),
                transaction.getStartDateTime(),
                transaction.getEndDateTime());
        log.info("output={}", output);
        return output;
    }

    @Recover
    public TransactionOutput recover(DataIntegrityViolationException ex){
        log.error("must wait another transactions");
        throw ex;
    }
}
