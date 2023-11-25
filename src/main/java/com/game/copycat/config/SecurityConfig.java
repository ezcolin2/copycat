package com.game.copycat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(
                csrf -> csrf.disable()
        );
        httpSecurity.sessionManagement(
                sessionManagement -> {
                    sessionManagement.maximumSessions(1).maxSessionsPreventsLogin(true);
                    sessionManagement.invalidSessionUrl("/login");
                }
        );
        httpSecurity.formLogin(
                formLogin-> {
                    formLogin.usernameParameter("memberId");
                    formLogin.passwordParameter("password");
                    formLogin.loginPage("/login");
                    formLogin.loginProcessingUrl("/api/login");
                    formLogin.defaultSuccessUrl("/rooms");
                    formLogin.failureUrl("/login");
                }

        );
        httpSecurity.authorizeHttpRequests(
                request -> request.requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
        );
        httpSecurity.logout(
                logout -> {
                    logout.logoutUrl("/api/logout");
                    logout.invalidateHttpSession(true).deleteCookies("SESSION");
                }
        );
        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**", "/css/**", "error");
    }

}
