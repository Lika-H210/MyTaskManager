package com.portfolio.taskapp.MyTaskManager.auth.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SecurityScheme(name = "userAuth", type = SecuritySchemeType.HTTP, scheme = "basic",
    description = "Swagger UI上ではHTTP Basicとして表示されますが、実際には現在はセッション認証です。")
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login.html", "/user/register.html", "/css/**", "/js/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
            // Todo:本番環境では削除
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login.html") // 静的HTMLを指定
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/project/list.html", true) // ログイン成功後に遷移
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
        );

    return http.build();
  }

}
