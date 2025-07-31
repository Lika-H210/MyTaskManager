package com.portfolio.taskapp.MyTaskManager.auth.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.portfolio.taskapp.MyTaskManager.auth.service.UserAccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final UserAccountDetailsService accountDetailsService;

  @Autowired
  public SecurityConfig(UserAccountDetailsService accountDetailsService) {
    this.accountDetailsService = accountDetailsService;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/css/**", "/js/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
            .anyRequest().permitAll()
        )
        .formLogin(withDefaults());

    return http.build();
  }

}