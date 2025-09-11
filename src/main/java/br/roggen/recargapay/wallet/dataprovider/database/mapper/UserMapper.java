package br.roggen.recargapay.wallet.dataprovider.database.mapper;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;

public class UserMapper {

    public static User toDomain(UserEntity entity){
        return new User(entity.getId(),
                entity.getUsername(),
                entity.getPassword());
    }

    public static UserEntity toEntity(User domain){
        return new UserEntity(domain.getId(),
                domain.getUsername(),
                domain.getPassword());
    }
}
