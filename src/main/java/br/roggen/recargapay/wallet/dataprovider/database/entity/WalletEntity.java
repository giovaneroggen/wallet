package br.roggen.recargapay.wallet.dataprovider.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@Document("wallets")
@NoArgsConstructor
@AllArgsConstructor
public class WalletEntity {

    @Id
    private String id;
    private BigDecimal balance;
    @DBRef
    private UserEntity user;
    private String currency;
}
