package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.WalletApplicationTests;
import br.roggen.recargapay.wallet.core.domain.TransactionType;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import br.roggen.recargapay.wallet.fixture.UserFixture;
import br.roggen.recargapay.wallet.fixture.WalletFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BalanceControllerTest extends WalletApplicationTests {

    @Test
    public void invalidUser() throws Exception {
        mockMvc.perform(get("/wallets/{walletId}/balance", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.INVALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void invalidPassword() throws Exception {
        testHelper.createUser();
        mockMvc.perform(get("/wallets/{walletId}/balance", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void walletNotFound() throws Exception {
        testHelper.createUser();
        mockMvc.perform(get("/wallets/{walletId}/balance", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isNotFound())
                .andExpect(content().string(WalletFixture.WALLET_NOT_FOUND_MESSAGE));
    }

    @Test
    public void success() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity wallet = testHelper.createWallet(user);
        mockMvc.perform(get("/wallets/{walletId}/balance", wallet.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(wallet.getBalance().doubleValue()));
    }

    @Test
    public void successBySpecificDateTime() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity wallet = testHelper.createWallet(user);
        testHelper.createTransaction(wallet, TransactionType.WITHDRAWAL, BigDecimal.TWO, LocalDateTime.now().minusDays(200));
        testHelper.createTransaction(wallet, TransactionType.WITHDRAWAL, BigDecimal.TWO, LocalDateTime.now().minusDays(200));
        testHelper.createTransaction(wallet, TransactionType.WITHDRAWAL, BigDecimal.TWO, LocalDateTime.now().minusDays(200));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(100));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(100));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(100));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(50));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(50));
        testHelper.createTransaction(wallet, TransactionType.DEPOSIT, BigDecimal.TWO, LocalDateTime.now().minusDays(50));


        mockMvc.perform(get("/wallets/{walletId}/balance?specificDateTime={specificDateTIme}", wallet.getId(), LocalDateTime.now().minusDays(500))
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isNotFound())
                .andExpect(content().string("transaction not found"));                ;

        int EXPECTED_BALANCE_IN_THE_PAST_AFTER_WITHDRAWAL = 4;
        mockMvc.perform(get("/wallets/{walletId}/balance?specificDateTime={specificDateTIme}", wallet.getId(), LocalDateTime.now().minusDays(150))
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(EXPECTED_BALANCE_IN_THE_PAST_AFTER_WITHDRAWAL));

        int EXPECTED_CURRENT_BALANCE_DATE_TIME_NOW = 16;
        mockMvc.perform(get("/wallets/{walletId}/balance?specificDateTime={specificDateTIme}", wallet.getId(), LocalDateTime.now())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(EXPECTED_CURRENT_BALANCE_DATE_TIME_NOW));
    }

}