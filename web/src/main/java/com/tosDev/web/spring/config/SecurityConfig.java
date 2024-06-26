package com.tosDev.web.spring.config;

import com.tosDev.web.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile({"prod","dev"})
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/tables/**").hasAuthority(Role.ADMIN.getAuthority())
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/sass/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/fonts/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/form/**").permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/tables/equip/main")
                        .permitAll())
                .rememberMe(Customizer.withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll())
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
       return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

}
