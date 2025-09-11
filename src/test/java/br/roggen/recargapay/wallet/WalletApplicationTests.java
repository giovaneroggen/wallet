package br.roggen.recargapay.wallet;

import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.helper.TestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = WalletApplication.class)
public abstract class WalletApplicationTests {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    public ObjectMapper objectMapper;
    protected MockMvc mockMvc;

    @Autowired
    protected TestHelper testHelper;

    @BeforeEach
    public void beforeEach() {
        testHelper.clearDatabase();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultRequest(MockMvcRequestBuilders.get("/"))
                .build();
    }

    public UserEntity findUserByWalletId(String walletId){
        return testHelper.findUserByWalletId(walletId);
    }
}
