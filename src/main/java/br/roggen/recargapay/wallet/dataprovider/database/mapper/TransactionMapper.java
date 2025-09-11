package br.roggen.recargapay.wallet.dataprovider.database.mapper;

import br.roggen.recargapay.wallet.core.domain.Transaction;
import br.roggen.recargapay.wallet.dataprovider.database.entity.TransactionEntity;

public class TransactionMapper {

    public static Transaction toDomain(TransactionEntity entity){
        return new Transaction(entity.getId(),
                WalletMapper.toDomain(entity.getWallet()),
                entity.getType(),
                entity.getValue(),
                entity.getBalanceAfterOperation(),
                entity.getStartDateTime(),
                entity.getEndDateTime());
    }

    public static TransactionEntity toEntity(Transaction domain){
        return new TransactionEntity(domain.getId(),
                WalletMapper.toEntity(domain.getWallet()),
                domain.getType(),
                domain.getValue(),
                domain.getBalanceAfterOperation(),
                domain.getStartDateTime(),
                domain.getEndDateTime());
    }
}
