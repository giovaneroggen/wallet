package br.roggen.recargapay.wallet.core.repository;

import br.roggen.recargapay.wallet.core.domain.Transfer;

public interface TransferRepository {
    Transfer save(Transfer transfer);
}
