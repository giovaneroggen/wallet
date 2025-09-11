package br.roggen.recargapay.wallet.dataprovider.database.provider;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.exception.TransactionNotFoundException;
import br.roggen.recargapay.wallet.core.repository.TransactionRepository;
import br.roggen.recargapay.wallet.dataprovider.database.dao.TransactionDAO;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.TransactionMapper;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
class TransactionDBProvider implements TransactionRepository {

    private final TransactionDAO transactionDAO;

    @Autowired
    TransactionDBProvider(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return Optional.of(transaction)
                .map(TransactionMapper::toEntity)
                .map(transactionDAO::save)
                .map(TransactionMapper::toDomain)
                .get();
    }

    @Override
    public Transaction findLatest(Wallet wallet, LocalDateTime specificDateTime) throws TransactionNotFoundException {
        return Optional.of(wallet)
                .map(WalletMapper::toEntity)
                .flatMap(it -> transactionDAO.findFirstByWalletAndEndDateTimeLessThanEqualOrderByEndDateTimeDesc(it, specificDateTime))
                .map(TransactionMapper::toDomain)
                .orElseThrow(TransactionNotFoundException::new);
    }
}
