package br.roggen.recargapay.wallet.fixture;

import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;

public class UserFixture {
    public static final String VALID_USERNAME = "giovane.roggen";
    public static final String VALID_PASSWORD = "123456";
    public static final String VALID_PASSWORD_2 = "1234";
    public static final String INVALID_USERNAME = "INVALID_USERNAME";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";

    public static UserEntity createUser() {
        return new UserEntity(null, VALID_USERNAME, VALID_PASSWORD);
    }
}
