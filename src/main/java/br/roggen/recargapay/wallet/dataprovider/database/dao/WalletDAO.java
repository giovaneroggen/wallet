package br.roggen.recargapay.wallet.dataprovider.database.dao;

import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WalletDAO extends MongoRepository<WalletEntity, String> {
    Optional<WalletEntity> findFirstByIdAndUser(String id, UserEntity user);
}
