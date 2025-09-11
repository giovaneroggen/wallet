package br.roggen.recargapay.wallet.entrypoint.rest;

import br.roggen.recargapay.wallet.core.usecase.CreateWalletUseCase;
import br.roggen.recargapay.wallet.core.usecase.input.CreateWalletInput;
import br.roggen.recargapay.wallet.core.usecase.output.CreateWalletOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/wallets")
public class WalletController {

    final CreateWalletUseCase useCase;

    public WalletController(CreateWalletUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public CreateWalletOutput createWallet(@RequestHeader("X-username") String username,
                                           @RequestHeader("X-password") String password){
        log.info("initialized");
        CreateWalletOutput output = useCase.execute(new CreateWalletInput(username, password));
        log.info("finished");
        return output;
    }
}
