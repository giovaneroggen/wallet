package br.roggen.recargapay.wallet.core.domain;

import br.roggen.recargapay.wallet.core.exception.UserOrPasswordException;
import lombok.Getter;

@Getter
public class User {

    private final String id;
    private final String username;
    private final String password;

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public static User create(String username, String password){
        return new User(null, username, password);
    }

    public void validatePassword(String password) {
        if (!this.password.equals(password)){
            throw new UserOrPasswordException();
        }
    }
}
