package br.roggen.recargapay.wallet.dataprovider.database.dao;

import br.roggen.recargapay.wallet.dataprovider.database.entity.TransactionEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionDAO extends MongoRepository<TransactionEntity, String> {

    Optional<TransactionEntity> findFirstByWalletAndEndDateTimeLessThanEqualOrderByEndDateTimeDesc(WalletEntity walletEntity, LocalDateTime specificDateTime);
}
