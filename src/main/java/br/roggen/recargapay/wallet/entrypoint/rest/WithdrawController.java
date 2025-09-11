package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.usecase.WithdrawUseCase;
import br.roggen.recargapay.wallet.core.usecase.input.TransactionInput;
import br.roggen.recargapay.wallet.core.usecase.output.TransactionOutput;
import br.roggen.recargapay.wallet.entrypoint.rest.request.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wallets")
public class WithdrawController {

    final WithdrawUseCase useCase;

    public WithdrawController(WithdrawUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping("/{walletId}/withdraw")
    public TransactionOutput withdraw(@PathVariable("walletId") String walletId,
                                      @RequestHeader("X-username") String username,
                                      @RequestHeader("X-password") String password,
                                      @RequestBody TransactionRequest request){
        log.info("initialized");
        TransactionOutput output = useCase.execute(new TransactionInput(username, password, walletId, request.value()));
        log.info("finished");
        return output;
    }
}
