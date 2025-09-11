package br.roggen.recargapay.wallet.core.domain;

import br.roggen.recargapay.wallet.core.exception.UnsuficientBalanceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;

@Slf4j
@Getter
public class Wallet {

    private String id;
    private BigDecimal balance;
    private final User user;
    private final Currency currency = Currency.getInstance(Locale.US);

    public Wallet(String id, BigDecimal balance, User user) {
        this.id = id;
        validateExpectedBalance(balance);
        this.balance = balance.setScale(2, RoundingMode.HALF_UP);
        this.user = user;
    }

    public Wallet(User user) {
        this.user = user;
        this.balance = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }

    public static Wallet create(User user){
        return new Wallet(user);
    }

    public void deposit(BigDecimal value){
        log.debug("balance {} + {} value", this.balance, value);
        this.balance = this.balance.add(value).setScale(2, RoundingMode.HALF_UP);
    }

    public void withdraw(BigDecimal value) throws UnsuficientBalanceException {
        log.debug("balance {} - {} value", this.balance, value);
        BigDecimal expectedBalance = this.balance.subtract(value).setScale(2, RoundingMode.HALF_UP);
        validateExpectedBalance(expectedBalance);
        this.balance = expectedBalance;
    }

    private void validateExpectedBalance(BigDecimal expectedBalance) {
        if (expectedBalance.signum() == -1){
            throw new UnsuficientBalanceException();
        }
    }
}
