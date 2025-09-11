package br.roggen.recargapay.wallet.dataprovider.database.mapper;

import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;

public class WalletMapper {
    public static Wallet toDomain(WalletEntity entity) {
        return new Wallet(entity.getId(),
                entity.getBalance(),
                UserMapper.toDomain(entity.getUser()));
    }

    public static WalletEntity toEntity(Wallet domain) {
        return new WalletEntity(domain.getId(),
                domain.getBalance(),
                UserMapper.toEntity(domain.getUser()),
                domain.getCurrency().getSymbol());
    }
}
