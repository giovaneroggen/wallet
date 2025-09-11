package br.roggen.recargapay.wallet.core.repository;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.exception.TransactionNotFoundException;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public interface TransactionRepository {

    Transaction save(Transaction transaction);
    Transaction findLatest(Wallet wallet, LocalDateTime localDateTime) throws TransactionNotFoundException;
}
