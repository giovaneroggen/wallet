package br.roggen.recargapay.wallet.dataprovider.database.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document("transfers")
@NoArgsConstructor
@AllArgsConstructor
public class TransferEntity {
    @Id
    private String id;
    @DBRef
    private TransactionEntity transactionFrom;
    @DBRef
    private TransactionEntity transactionTo;
    private BigDecimal value;
}
