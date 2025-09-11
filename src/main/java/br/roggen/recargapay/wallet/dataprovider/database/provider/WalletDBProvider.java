package br.roggen.recargapay.wallet.dataprovider.database.provider;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.exception.WalletNotFoundException;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.dataprovider.database.dao.WalletDAO;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.UserMapper;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.WalletMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class WalletDBProvider implements WalletRepository {

    private final WalletDAO walletDAO;
    private static final Logger log = LoggerFactory.getLogger(WalletDBProvider.class);

    @Autowired
    WalletDBProvider(WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

    @Override
    public Wallet save(Wallet wallet) {
        return Optional.of(wallet)
                .map(WalletMapper::toEntity)
                .map(walletDAO::save)
                .map(WalletMapper::toDomain)
                .get();
    }

    @Override
    public Wallet findByIdAndUser(String id, User user) throws WalletNotFoundException {
        return Optional.of(user)
                .filter(it -> id != null)
                .map(UserMapper::toEntity)
                .flatMap(it -> walletDAO.findFirstByIdAndUser(id, it))
                .map(WalletMapper::toDomain)
                .orElseThrow(WalletNotFoundException::new);
    }

    @Override
    public Wallet findById(String id) throws WalletNotFoundException {
        return Optional.of(id)
                .flatMap(walletDAO::findById)
                .map(WalletMapper::toDomain)
                .orElseThrow(WalletNotFoundException::new);
    }
}
