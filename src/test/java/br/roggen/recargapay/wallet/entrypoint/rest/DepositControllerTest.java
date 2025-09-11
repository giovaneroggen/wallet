package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.WalletApplicationTests;
import br.roggen.recargapay.wallet.core.domain.TransactionType;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import br.roggen.recargapay.wallet.entrypoint.rest.request.TransactionRequest;
import br.roggen.recargapay.wallet.fixture.TransactionFixture;
import br.roggen.recargapay.wallet.fixture.UserFixture;
import br.roggen.recargapay.wallet.fixture.WalletFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DepositControllerTest extends WalletApplicationTests {

    @Test
    public void invalidUser() throws Exception {
        mockMvc.perform(patch("/wallets/{walletId}/deposit", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.INVALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void invalidPassword() throws Exception {
        testHelper.createUser();
        mockMvc.perform(patch("/wallets/{walletId}/deposit", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void walletNotFound() throws Exception {
        testHelper.createUser();
        mockMvc.perform(patch("/wallets/{walletId}/deposit", WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(WalletFixture.WALLET_NOT_FOUND_MESSAGE));
    }

    @Test
    public void invalidValue() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity wallet = testHelper.createWallet(user);
        mockMvc.perform(patch("/wallets/{walletId}/deposit", wallet.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.invalidRequest())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void success() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity wallet = testHelper.createWallet(user);
        int EXPECTED_BALANCE_AFTER_DEPOSIT = 12;

        mockMvc.perform(patch("/wallets/{walletId}/deposit", wallet.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.startDate").isNotEmpty())
                .andExpect(jsonPath("$.endDate").isNotEmpty())
                .andExpect(jsonPath("$.walletId").value(wallet.getId()))
                .andExpect(jsonPath("$.balanceAfterOperation").value(EXPECTED_BALANCE_AFTER_DEPOSIT));
    }

}