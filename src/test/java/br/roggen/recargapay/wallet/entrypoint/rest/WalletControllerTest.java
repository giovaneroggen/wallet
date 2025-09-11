package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.WalletApplicationTests;
import br.roggen.recargapay.wallet.core.domain.TransactionType;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.dataprovider.database.entity.WalletEntity;
import br.roggen.recargapay.wallet.fixture.TransactionFixture;
import br.roggen.recargapay.wallet.fixture.UserFixture;
import br.roggen.recargapay.wallet.fixture.WalletFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest extends WalletApplicationTests {

    @Test
    public void invalidUser() throws Exception {
        mockMvc.perform(post("/wallets")
                        .header("X-username", UserFixture.INVALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void invalidPassword() throws Exception {
        testHelper.createUser();
        mockMvc.perform(post("/wallets")
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.INVALID_PASSWORD))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(HttpStatus.UNAUTHORIZED.getReasonPhrase()));
    }

    @Test
    public void success() throws Exception {
        testHelper.createUser();
        mockMvc.perform(post("/wallets")
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }
}