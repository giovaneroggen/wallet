package br.roggen.recargapay.wallet.dataprovider.database.provider;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.exception.UserOrPasswordException;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.dataprovider.database.dao.UserDAO;
import br.roggen.recargapay.wallet.dataprovider.database.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class UserDBProvider implements UserRepository {

    private final UserDAO userDAO;

    @Autowired
    UserDBProvider(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public User save(User user) {
        return Optional.of(user)
                .map(UserMapper::toEntity)
                .map(userDAO::save)
                .map(UserMapper::toDomain)
                .get();
    }

    @Override
    public User findRequiredByUsername(String username) throws UserOrPasswordException {
        return findByUsername(username)
                .orElseThrow(UserOrPasswordException::new);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.of(username)
                .flatMap(userDAO::findFirstByUsername)
                .map(UserMapper::toDomain);
    }
}
