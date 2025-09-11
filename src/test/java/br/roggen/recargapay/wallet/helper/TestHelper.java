package br.roggen.recargapay.wallet.helper;

import br.roggen.recargapay.wallet.core.domain.TransactionType;
import br.roggen.recargapay.wallet.dataprovider.database.dao.TransactionDAO;
import br.roggen.recargapay.wallet.dataprovider.database.dao.TransferDAO;
import br.roggen.recargapay.wallet.dataprovider.database.dao.UserDAO;
import br.roggen.recargapay.wallet.dataprovider.database.dao.WalletDAO;
import br.roggen.recargapay.wallet.dataprovider.database.entity.TransactionEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import br.roggen.recargapay.wallet.entity.Transaction;
import br.roggen.recargapay.wallet.fixture.TransactionFixture;
import br.roggen.recargapay.wallet.fixture.UserFixture;
import br.roggen.recargapay.wallet.fixture.WalletFixture;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TestHelper {

    @Autowired
    private WalletDAO walletDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private TransactionDAO transactionDAO;
    @Autowired
    private TransferDAO transferDAO;
    @Autowired
    private ObjectMapper objectMapper;

    public void clearDatabase(){
        userDAO.deleteAll();
        walletDAO.deleteAll();
        transactionDAO.deleteAll();
        transferDAO.deleteAll();
    }

    public void loadUserBase() throws IOException {
        objectMapper.readValue(new ClassPathResource("/data/user.json").getInputStream(), new TypeReference<List<UserEntity>>() {
                })
                .forEach(userDAO::save);
        Assertions.assertEquals(1000, userDAO.count());
    }

    public void loadWalletBase() throws IOException {
        objectMapper.readValue(new ClassPathResource("/data/wallet.json").getInputStream(), new TypeReference<List<WalletEntity>>() {
                })
                .forEach(walletDAO::save);
        Assertions.assertEquals(5000, walletDAO.count());
    }

    public List<Transaction> retrieve50KTransactions() throws IOException {
        return objectMapper.readValue(new ClassPathResource("/data/transaction.json").getInputStream(), new TypeReference<List<Transaction>>() {
                });
    }

    public UserEntity findUserByWalletId(String walletId) {
        return this.walletDAO.findById(walletId)
                .map(WalletEntity::getUser)
                .get();
    }

    public UserEntity createUser() {
        return userDAO.save(UserFixture.createUser());
    }

    public WalletEntity createWallet(UserEntity user) {
        return walletDAO.save(WalletFixture.createWallet(user));
    }

    public TransactionEntity createTransaction(WalletEntity wallet, TransactionType transactionType,
                                               BigDecimal value, LocalDateTime endDateTime) {
        TransactionEntity save = transactionDAO.save(TransactionFixture.createTransaction(wallet, transactionType, value, endDateTime));
        walletDAO.save(wallet);
        return save;
    }
}
