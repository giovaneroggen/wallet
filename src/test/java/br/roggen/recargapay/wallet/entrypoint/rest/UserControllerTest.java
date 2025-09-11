package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.WalletApplicationTests;
import br.roggen.recargapay.wallet.fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends WalletApplicationTests {

    @Test
    public void invalidUser() throws Exception {
        mockMvc.perform(post("/users")
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidPassword() throws Exception {
        mockMvc.perform(post("/users")
                        .header("X-username", UserFixture.VALID_USERNAME))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void success() throws Exception {
        mockMvc.perform(post("/users")
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty());

        mockMvc.perform(post("/users")
                        .header("X-username", UserFixture.VALID_USERNAME)
                        .header("X-password", UserFixture.VALID_PASSWORD_2))
                .andExpect(status().isUnauthorized());
    }
}