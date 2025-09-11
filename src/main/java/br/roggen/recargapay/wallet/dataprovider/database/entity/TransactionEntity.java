package br.roggen.recargapay.wallet.dataprovider.database.entity;

import br.roggen.recargapay.wallet.core.domain.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document("transactions")
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    private String id;
    @DBRef
    private WalletEntity wallet;
    private TransactionType type;
    private BigDecimal value;
    private BigDecimal balanceAfterOperation;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
