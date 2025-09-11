package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.usecase.RetrieveBalanceUseCase;
import br.roggen.recargapay.wallet.core.usecase.RetrieveSpecificHistoricalBalanceUseCase;
import br.roggen.recargapay.wallet.core.usecase.input.RetrieveBalanceInput;
import br.roggen.recargapay.wallet.core.usecase.input.RetrieveSpecificBalanceInput;
import br.roggen.recargapay.wallet.core.usecase.output.RetrieveBalanceOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@Slf4j
@RestController
@RequestMapping("/wallets")
public class BalanceController {

    final RetrieveBalanceUseCase retrieveBalanceUseCase;
    final RetrieveSpecificHistoricalBalanceUseCase retrieveSpecificHistoricalBalanceUseCase;

    public BalanceController(RetrieveBalanceUseCase retrieveBalanceUseCase,
                             RetrieveSpecificHistoricalBalanceUseCase retrieveSpecificHistoricalBalanceUseCase) {
        this.retrieveBalanceUseCase = retrieveBalanceUseCase;
        this.retrieveSpecificHistoricalBalanceUseCase = retrieveSpecificHistoricalBalanceUseCase;
    }

    @GetMapping("/{walletId}/balance")
    public RetrieveBalanceOutput retrieveBalance(@PathVariable("walletId") String walletId,
                                                 @RequestHeader("X-username") String username,
                                                 @RequestHeader("X-password") String password,
                                                 @RequestParam(value = "specificDateTime", required = false) LocalDateTime specificDateTime){
        log.info("initialized");
        RetrieveBalanceOutput output;
        if(nonNull(specificDateTime)){
            output = retrieveSpecificHistoricalBalanceUseCase.execute(new RetrieveSpecificBalanceInput(username, password, walletId, specificDateTime));
        }else{
            output = retrieveBalanceUseCase.execute(new RetrieveBalanceInput(username, password, walletId));
        }
        log.info("finished");
        return output;
    }
}
