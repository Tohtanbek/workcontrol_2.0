package com.tosDev.web.spring.web.validation;

import com.tosDev.web.dto.client_pages.ClientDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class CustomPhoneOrEmailValidation implements ConstraintValidator<CustomPhoneOrEmail,ClientDto> {
    @Override
    public boolean isValid(ClientDto clientDto, ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.hasText(clientDto.getPhoneNumber())
                ||
                StringUtils.hasText(clientDto.getEmail());
    }
}
