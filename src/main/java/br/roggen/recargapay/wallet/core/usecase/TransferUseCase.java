package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.core.domain.Transfer;
import br.roggen.recargapay.wallet.core.exception.WalletConflictException;
import br.roggen.recargapay.wallet.core.exception.WalletNotFoundException;
import br.roggen.recargapay.wallet.core.repository.TransactionRepository;
import br.roggen.recargapay.wallet.core.repository.TransferRepository;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.TransferInput;
import br.roggen.recargapay.wallet.core.usecase.output.TransferOutput;
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
public class TransferUseCase {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;

    public TransferUseCase(WalletRepository walletRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository,
                           TransferRepository transferRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // Each retry gets a new transaction
    @Retryable(retryFor = DataIntegrityViolationException.class, backoff = @Backoff(delay = 5000))
    public TransferOutput execute(TransferInput input){
        var user = userRepository.findRequiredByUsername(input.username());
        log.info("user found userId={}", user.getId());
        user.validatePassword(input.password());

        var walletFrom = walletRepository.findByIdAndUser(input.walletIdFrom(), user);
        var walletTo = walletRepository.findById(input.walletIdTo());
        if(walletFrom.getId().equals(walletTo.getId())){
            log.info("invalid walletIds=[{},{}]", walletFrom, walletTo);
            throw new WalletConflictException();
        }

        log.info("wallet from found walletId={}", walletFrom.getId());
        log.info("wallet to found walletId={}", walletTo.getId());

        var transactionFrom = Transaction.createWithdraw(walletFrom, input.value());
        var transactionTo = Transaction.createDeposit(walletTo, input.value());

        log.info("creating both transactions");

        log.debug("withdraw initialized from wallet={} value={} balance={}", walletFrom.getId(), input.value(), walletFrom.getBalance());
        walletFrom.withdraw(input.value());
        transactionFrom.finish(walletFrom);
        log.debug("withdraw finished from wallet={} value={}", walletFrom.getId(), input.value());

        log.debug("deposit initialized from wallet={} value={} balance={}", walletTo.getId(), input.value(), walletTo.getBalance());
        walletTo.deposit(input.value());
        transactionTo.finish(walletFrom);
        log.debug("deposit finished from wallet={} value={}", walletTo.getId(), input.value());

        this.walletRepository.save(walletFrom);
        this.walletRepository.save(walletTo);

        transactionFrom = this.transactionRepository.save(transactionFrom);
        transactionTo = this.transactionRepository.save(transactionTo);

        log.info("commit both transactions");

        Transfer transfer = this.transferRepository.save(new Transfer(transactionFrom, transactionTo));
        TransferOutput output = new TransferOutput(transfer.getId(), transactionFrom.getId(),
                transactionTo.getId(), transfer.getValue(), transfer.getBalanceAfterOperation());
        log.info("output={}", output);
        return output;
    }

    @Recover
    public TransferOutput recover(DataIntegrityViolationException ex){
        log.error("must wait another transactions");
        throw ex;
    }
}
