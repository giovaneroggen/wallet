package br.roggen.recargapay.wallet.stress;

import br.roggen.recargapay.wallet.WalletApplicationTests;
import br.roggen.recargapay.wallet.dataprovider.database.entity.UserEntity;
import br.roggen.recargapay.wallet.entity.Transaction;
import br.roggen.recargapay.wallet.entrypoint.rest.request.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class StressLoadTransactionTests extends WalletApplicationTests {

    @Test
    public void execute5KRandomTransactionsAndAssertBalances() throws Exception {
        testHelper.loadUserBase();
        testHelper.loadWalletBase();

        for (Transaction transaction: testHelper.retrieve50KTransactions()){
            Transaction.Wallet walletFrom = transaction.getWalletFrom();
            Transaction.Wallet walletTo = transaction.getWalletTo();
            UserEntity user = super.findUserByWalletId(walletFrom.getId());
            switch (transaction.getType()){
                case DEPOSIT -> {
                    mockMvc.perform(patch("/wallets/{walletId}/deposit", walletFrom.getId())
                                    .header("X-username", user.getUsername())
                                    .header("X-password", user.getPassword())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new TransactionRequest(transaction.getValue()))))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.transactionId").isNotEmpty())
                            .andExpect(jsonPath("$.startDate").isNotEmpty())
                            .andExpect(jsonPath("$.endDate").isNotEmpty())
                            .andExpect(jsonPath("$.walletId").value(walletFrom.getId()))
                            .andExpect(jsonPath("$.balanceAfterOperation").value(transaction.getExpectedBalanceAfterOperation().doubleValue()));
                }
                case WITHDRAWAL -> {
                    mockMvc.perform(patch("/wallets/{walletId}/withdraw", walletFrom.getId())
                                    .header("X-username", user.getUsername())
                                    .header("X-password", user.getPassword())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new TransactionRequest(transaction.getValue()))))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.transactionId").isNotEmpty())
                            .andExpect(jsonPath("$.startDate").isNotEmpty())
                            .andExpect(jsonPath("$.endDate").isNotEmpty())
                            .andExpect(jsonPath("$.walletId").value(walletFrom.getId()))
                            .andExpect(jsonPath("$.balanceAfterOperation").value(transaction.getExpectedBalanceAfterOperation().doubleValue()));
                }
                case TRANSFER -> {
                    mockMvc.perform(patch("/wallets/{walletIdFrom}/transfer/to/{walletIdTo}", walletFrom.getId(), walletTo.getId())
                                    .header("X-username", user.getUsername())
                                    .header("X-password", user.getPassword())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(new TransactionRequest(transaction.getValue()))))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.transferId").isNotEmpty())
                            .andExpect(jsonPath("$.transactionIdFrom").isNotEmpty())
                            .andExpect(jsonPath("$.transactionIdTo").isNotEmpty())
                            .andExpect(jsonPath("$.value").value(transaction.getValue().doubleValue()))
                            .andExpect(jsonPath("$.balanceAfterOperation").value(transaction.getExpectedBalanceAfterOperation().doubleValue()));
                }
            }
        }
    }

}
