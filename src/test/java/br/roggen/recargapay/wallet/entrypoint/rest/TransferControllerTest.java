package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.WalletApplicationTests;
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
import java.math.RoundingMode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransferControllerTest extends WalletApplicationTests {

    @Test
    public void invalidUser() throws Exception {
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", WalletFixture.INVALID_ID, WalletFixture.INVALID_ID)
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
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", WalletFixture.INVALID_ID, WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void walletFromNotFound() throws Exception {
        testHelper.createUser();
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", WalletFixture.INVALID_ID, WalletFixture.INVALID_ID)
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(content().string(WalletFixture.WALLET_NOT_FOUND_MESSAGE));
    }
    
    @Test
    public void walletToNotFound() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity walletFrom = testHelper.createWallet(user);
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), WalletFixture.INVALID_ID)
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
        WalletEntity walletFrom = testHelper.createWallet(user);
        WalletEntity walletTo = testHelper.createWallet(user);
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), walletTo.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.invalidRequest())))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void unsuficientBalanceException() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity walletFrom = testHelper.createWallet(user);
        WalletEntity walletTo = testHelper.createWallet(user);

        BigDecimal value = BigDecimal.TEN.add(BigDecimal.valueOf(0.01)).setScale(2, RoundingMode.HALF_UP);
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), walletTo.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TransactionFixture.request(value))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void sameWallet() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity walletFrom = testHelper.createWallet(user);
        TransactionRequest request = TransactionFixture.validRequest();
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), walletFrom.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    public void success() throws Exception {
        UserEntity user = testHelper.createUser();
        WalletEntity walletFrom = testHelper.createWallet(user);
        WalletEntity walletTo = testHelper.createWallet(user);
        int EXPECTED_BALANCE_AFTER_TRANSFER_BETWEEN_ACCOUNTS = 8;

        TransactionRequest request = TransactionFixture.validRequest();
        mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), walletTo.getId())
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transferId").isNotEmpty())
                .andExpect(jsonPath("$.transactionIdFrom").isNotEmpty())
                .andExpect(jsonPath("$.transactionIdTo").isNotEmpty())
                .andExpect(jsonPath("$.value").value(request.value().doubleValue()))
                .andExpect(jsonPath("$.balanceAfterOperation").value(EXPECTED_BALANCE_AFTER_TRANSFER_BETWEEN_ACCOUNTS));
    }

}