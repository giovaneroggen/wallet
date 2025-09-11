package br.roggen.recargapay.wallet.dataprovider.database.provider;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.core.domain.Transfer;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.repository.TransactionRepository;
import br.roggen.recargapay.wallet.core.repository.TransferRepository;
import br.roggen.recargapay.wallet.dataprovider.database.dao.TransactionDAO;
import br.roggen.recargapay.wallet.dataprovider.database.dao.TransferDAO;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.TransactionMapper;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.TransferMapper;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
class TransferDBProvider implements TransferRepository {

    private final TransferDAO transferDAO;

    @Autowired
    TransferDBProvider(TransferDAO transferDAO) {
        this.transferDAO = transferDAO;
    }

    @Override
    public Transfer save(Transfer transfer) {
        return Optional.of(transfer)
                .map(TransferMapper::toEntity)
                .map(transferDAO::save)
                .map(TransferMapper::toDomain)
                .get();
    }
}
