package com.example.web_app.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    CustomSuccessHandler customSuccessHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/signup/**").permitAll()
                        .requestMatchers("/login/**").permitAll()

                        .requestMatchers("/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/roles/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/reviews/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/orders/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/messages/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/products/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/categories/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/admin").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/seller/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SELLER")
                        .requestMatchers("/buyer/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_BUYER")

                        .anyRequest().authenticated())

                .formLogin((form) -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(customSuccessHandler)
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .permitAll())

                .logout(obj -> obj.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true).deleteCookies("JSESSIONID"));

        http.exceptionHandling((ex) -> ex.accessDeniedPage("/unAuthorized"));
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/", "/home", "/about", "/contact", "/images/logo.png",
                "/images/login.jpg", "/images/signup.jpg", "/unAuthorized");
    }

}

@Component
class CustomSuccessHandler implements AuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        handleRedirect(request, response, authentication.getAuthorities());
    }

    private void handleRedirect(HttpServletRequest request, HttpServletResponse response,
            Collection<? extends GrantedAuthority> authorities) throws IOException {

        boolean isAdmin = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isBuyer = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_BUYER"));

        boolean isSeller = authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_SELLER"));

        if (isAdmin) {
            redirectStrategy.sendRedirect(request, response, "/admin");
        } else if (isBuyer) {
            redirectStrategy.sendRedirect(request, response, "/buyer");
        } else if (isSeller) {
            redirectStrategy.sendRedirect(request, response, "/seller");
        } else {
            redirectStrategy.sendRedirect(request, response, "/?error=true");
        }
    }

}