package br.roggen.recargapay.wallet.core.repository;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.exception.UserOrPasswordException;

import java.util.Optional;

public interface UserRepository {

    User save(User user);
    User findRequiredByUsername(String username) throws UserOrPasswordException;
    Optional<User> findByUsername(String username) ;
}
