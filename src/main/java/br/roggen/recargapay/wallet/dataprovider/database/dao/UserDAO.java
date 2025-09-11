package br.roggen.recargapay.wallet.dataprovider.database.dao;

import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserDAO extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findFirstByUsername(String username);
}
