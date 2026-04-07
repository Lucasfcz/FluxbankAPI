package io.github.Lucasfcz.fluxbank.mapper;

import io.github.Lucasfcz.fluxbank.dto.response.RegisterUserResponseDTO;
import io.github.Lucasfcz.fluxbank.model.JwtUser;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public RegisterUserResponseDTO toRegisterUserResponseDTO(JwtUser user) {
        return new RegisterUserResponseDTO(user.getEmail());
    }
}



