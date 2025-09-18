package br.roggen.recargapay.wallet.core.usecase;

import br.roggen.recargapay.wallet.core.domain.User;
import br.roggen.recargapay.wallet.core.domain.Wallet;
import br.roggen.recargapay.wallet.core.exception.TransferConflictException;
import br.roggen.recargapay.wallet.core.repository.UserRepository;
import br.roggen.recargapay.wallet.core.repository.WalletRepository;
import br.roggen.recargapay.wallet.core.usecase.input.TransferInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransferUseCaseConcurrencyTest {

    @Autowired
    private TransferUseCase transferUseCase;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Wallet walletFrom;
    private Wallet walletTo;

    @BeforeEach
    void setup() {
        // Criar usuário e carteiras
        user = userRepository.save(new User(null, "testuser", "pass"));
        walletFrom = walletRepository.save(new Wallet(null, BigDecimal.valueOf(1000), user));
        walletTo = walletRepository.save(new Wallet(null, BigDecimal.ZERO, user));
    }

    @Test
    void testConcurrentTransfers() throws InterruptedException {
        int threadCount = 50;
        BigDecimal transferAmount = BigDecimal.valueOf(200);

        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger conflictCount = new AtomicInteger();
        AtomicInteger errorCount = new AtomicInteger();
        try (var executor = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        TransferInput input = new TransferInput(
                                user.getUsername(),
                                "pass",
                                walletFrom.getId(),
                                walletTo.getId(),
                                transferAmount
                        );
                        transferUseCase.execute(input);
                        successCount.incrementAndGet();
                    } catch (TransferConflictException e) {
                        conflictCount.incrementAndGet();
                        System.out.println("[CONFLICT] " + Thread.currentThread().getName() + " -> " + e.getMessage());
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        System.err.println("[ERROR] " + Thread.currentThread().getName() + " -> " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();
        }

        // busca carteiras
        walletFrom = walletRepository.findById(walletFrom.getId());
        walletTo = walletRepository.findById(walletTo.getId());

        BigDecimal finalFromBalance = walletFrom.getBalance();
        BigDecimal finalToBalance = walletTo.getBalance();
        BigDecimal total = finalFromBalance.add(finalToBalance);

        System.out.println("========== STRESS TEST REPORT ==========");
        System.out.println("Threads total       : " + threadCount);
        System.out.println("Transfers success   : " + successCount.get());
        System.out.println("Transfers conflict  : " + conflictCount.get());
        System.out.println("Transfers error     : " + errorCount.get());
        System.out.println("Final balance FROM  : " + finalFromBalance);
        System.out.println("Final balance TO    : " + finalToBalance);
        System.out.println("Total consistency   : " + total);

        // ✅ Asserts
        assertTrue(finalFromBalance.compareTo(BigDecimal.ZERO) >= 0, "Saldo da carteira origem não pode ser negativo");
        assertEquals(BigDecimal.valueOf(5000), total, "A soma dos saldos deve permanecer constante");
    }
}