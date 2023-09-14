package pl.taw.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import pl.taw.api.controller.RegisterController;

import java.util.Set;

@Configuration
@EnableWebSecurity(debug = true) // TODO !!!! Przed puszczeniem na produkcję usuń debug !!!!!!
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService
    ) {
            DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
            provider.setPasswordEncoder(passwordEncoder);
            provider.setUserDetailsService(userDetailsService);
            return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity httpSecurity,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider)
                .build();
    }

    @Bean  // TODO to chyba jednak nie będzie potrzebne, każdy będzie przekierowywany na stronę główną
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

            if (roles.contains("ADMIN")) {
                response.sendRedirect("/clinic");
            } else if (roles.contains("DOCTOR")) {
                response.sendRedirect("/clinic");
            } else if (roles.contains("USER")) {
                response.sendRedirect("/clinic");
            } else {
                response.sendRedirect("/welcome");
            }
        };
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true) // jeżeli nie będzie podane w konfiguracji, to wartość domyślna jest true
    public SecurityFilterChain securityEnabled(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/welcome").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/register/addUser").permitAll()
                        .requestMatchers("/templates/core/registration-confirmation.html").permitAll()
                        .requestMatchers("/clinic", "/login", "/images/**", "/css/**", "/daypilot/**", "/register/**").permitAll()
                                .anyRequest().authenticated()
//                        .requestMatchers(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH).hasAnyAuthority("ADMIN", "USER", "DOCTOR")
                )
//                .formLogin(login -> login.permitAll().successHandler(authenticationSuccessHandler()))
                .formLogin(form -> form
                        .loginPage("/register/loginPage")
//                        .loginPage(RegisterController.REGISTER.concat(RegisterController.LOGIN_PAGE))
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/welcome")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
    public SecurityFilterChain securityDisabled(HttpSecurity httpSecurity) throws Exception {
       return httpSecurity
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests(request -> request.anyRequest().permitAll())
               .build();
    }

}
