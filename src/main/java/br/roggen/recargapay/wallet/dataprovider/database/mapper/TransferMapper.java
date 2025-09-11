package br.roggen.recargapay.wallet.dataprovider.database.mapper;

import br.roggen.recargapay.wallet.core.domain.Transfer;
import br.roggen.recargapay.wallet.dataprovider.database.entity.TransferEntity;

public class TransferMapper {

    public static Transfer toDomain(TransferEntity entity){
        return new Transfer(entity.getId(),
                TransactionMapper.toDomain(entity.getTransactionFrom()),
                TransactionMapper.toDomain(entity.getTransactionTo()),
                entity.getValue(),
                entity.getTransactionFrom().getBalanceAfterOperation());
    }

    public static TransferEntity toEntity(Transfer domain){
        return new TransferEntity(domain.getId(),
                TransactionMapper.toEntity(domain.getTransactionFrom()),
                TransactionMapper.toEntity(domain.getTransactionTo()),
                domain.getValue());
    }
}
