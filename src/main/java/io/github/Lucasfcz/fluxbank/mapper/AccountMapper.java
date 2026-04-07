package io.github.Lucasfcz.fluxbank.mapper;

import io.github.Lucasfcz.fluxbank.dto.response.AccountResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindCpfResponseDTO;
import io.github.Lucasfcz.fluxbank.dto.response.FindEmailResponseDTO;
import io.github.Lucasfcz.fluxbank.model.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponseDTO toAccountResponseDTO(Account account) {
        return new AccountResponseDTO(
                account.getId(),
                account.getHolderName(),
                account.getEmail(),
                account.getAccountType(),
                account.getBalance(),
                account.isActive(),
                account.getCreatedAt()
        );
    }

    public FindEmailResponseDTO toFindEmailResponseDTO(Account account) {
        return new FindEmailResponseDTO(
                account.getEmail(),
                account.getBalance()
        );
    }

    public FindCpfResponseDTO toFindCpfResponseDTO(Account account) {
        return new FindCpfResponseDTO(
                account.getCpf(),
                account.getBalance()
        );
    }
}

