package br.roggen.recargapay.wallet.fixture;

import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;

import java.math.BigDecimal;

public class WalletFixture {
    public static final String INVALID_ID = "INVALID_WALLET_ID";
    public static final String WALLET_NOT_FOUND_MESSAGE = "wallet not found";
    public static final BigDecimal VALID_BALANCE = BigDecimal.TEN;

    public static WalletEntity createWallet(UserEntity user) {
        return new WalletEntity(null, VALID_BALANCE, user, "USD");
    }
}
