package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.usecase.TransferUseCase;
import br.roggen.recargapay.wallet.core.usecase.input.TransferInput;
import br.roggen.recargapay.wallet.core.usecase.output.TransferOutput;
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
public class TransferController {

    final TransferUseCase useCase;

    public TransferController(TransferUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping("/{walletIdFrom}/transfer/to/{walletIdTo}")
    public TransferOutput transfer(@PathVariable("walletIdFrom") String walletIdFrom,
                                   @PathVariable("walletIdTo") String walletIdTo,
                                   @RequestHeader("X-username") String username,
                                   @RequestHeader("X-password") String password,
                                   @RequestBody TransactionRequest request){
        log.info("initialized");
        TransferOutput output = useCase.execute(new TransferInput(username, password, walletIdFrom, walletIdTo, request.value()));
        log.info("finished");
        return output;
    }
}
