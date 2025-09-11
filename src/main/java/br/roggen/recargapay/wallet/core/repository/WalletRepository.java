package br.roggen.recargapay.wallet.core.repository;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.exception.WalletNotFoundException;

public interface WalletRepository {

    Wallet save(Wallet wallet);
    Wallet findByIdAndUser(String id, User user) throws WalletNotFoundException;
    Wallet findById(String id) throws WalletNotFoundException;
}
