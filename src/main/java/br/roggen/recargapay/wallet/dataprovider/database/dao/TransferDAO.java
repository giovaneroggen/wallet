package br.roggen.recargapay.wallet.dataprovider.database.dao;

import br.roggen.recargapay.wallet.dataprovider.database.entity.TransferEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransferDAO extends MongoRepository<TransferEntity, String> {
}
