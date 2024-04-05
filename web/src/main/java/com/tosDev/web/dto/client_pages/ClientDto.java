package com.tosDev.web.dto.client_pages;

import com.tosDev.web.spring.web.validation.CustomPhoneOrEmail;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@CustomPhoneOrEmail
public class ClientDto {
    @NotBlank(message = "Please input your name")
    private String username;

    private String email;
    private String phoneNumber;
}
