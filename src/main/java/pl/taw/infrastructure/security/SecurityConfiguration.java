package pl.taw.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Set;

@Configuration
@EnableWebSecurity(debug = true) // TODO !!!! Przed puszczeniem na produkcję usuń debug !!!!!!
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public AuthenticationManager authManager(
//            HttpSecurity httpSecurity,
//            PasswordEncoder passwordEncoder,
//            UserDetailsService userDetailService)
//            throws Exception {
//        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
//                .userDetailsService(userDetailService)
//                .passwordEncoder(passwordEncoder)
//                .and()
//                .build();
//    }

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
//                response.sendRedirect("/doctors");
                response.sendRedirect("/clinic");
            } else if (roles.contains("USER")) {
                response.sendRedirect("/clinic");
//                response.sendRedirect("/clinic/patients");
            } else {
                response.sendRedirect("/welcome");
            }
        };
    }

//    @Bean
//    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
//    SecurityFilterChain securityEnabled(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf().disable()
//                .authorizeHttpRequests()
////                .requestMatchers("/clinic/welcome").permitAll()
//                .requestMatchers("/welcome").permitAll()
//                .requestMatchers("/register").permitAll()
//                .requestMatchers("/register/addUser").permitAll()
//                .requestMatchers("/templates/registration-confirmation.html").permitAll()
//                .requestMatchers("/clinic", "/login", "/images/**", "/css/**", "/daypilot/**", "/register/**").permitAll()
//                .requestMatchers(HttpMethod.DELETE).hasAnyAuthority("ADMIN")
//                //.requestMatchers("/patients/**", "/doctors/**").hasAnyAuthority("ADMIN", "DOCTOR", "USER")
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .successHandler(authenticationSuccessHandler())
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/welcome")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll();
//
//        return httpSecurity.build();
//    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
    SecurityFilterChain securityEnabled(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/welcome").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/register/addUser").permitAll()
                        .requestMatchers("/templates/registration-confirmation.html").permitAll()
                        .requestMatchers("/clinic", "/login", "/images/**", "/css/**", "/daypilot/**", "/register/**").permitAll()
                                .anyRequest().authenticated()
//                        .requestMatchers(HttpMethod.DELETE, HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.PATCH).hasAnyAuthority("ADMIN", "USER", "DOCTOR")
                ).formLogin(login -> login.permitAll().successHandler(authenticationSuccessHandler()))
                .logout(logout -> logout
                        .logoutSuccessUrl("/welcome")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .build();
    }


//    @Bean
//    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
//    SecurityFilterChain securityDisabled(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .anyRequest()
//                .permitAll();
//
//        return httpSecurity.build();
//    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
    SecurityFilterChain securityDisabled(HttpSecurity httpSecurity) throws Exception {
       return httpSecurity
               .csrf(AbstractHttpConfigurer::disable)
               .authorizeHttpRequests(request -> request.anyRequest().permitAll())
               .build();
    }

    //    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeHttpRequests()
//                .requestMatchers("/login", "/error", "/images/oh_no.png", "/welcome").permitAll()
//                .requestMatchers("/patients/**", "/images/**").hasAnyAuthority("USER", "DOCTOR", "ADMIN")
//                .requestMatchers(HttpMethod.DELETE).hasAnyAuthority("ADMIN")
//                .and()
//                .formLogin()
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/welcome")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll();
//
//        return httpSecurity.build();
//    }

//    @Bean
//    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
//    SecurityFilterChain securityEnabled(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf()
//                .disable()
//                .authorizeHttpRequests()
//                .requestMatchers("/login", "/error", "/images/**", "/clinic").permitAll()
//                .requestMatchers("/patients/**", "/doctors/**", "/opinions/**", "/visits/**", "/reservations/**").hasAnyAuthority("ADMIN", "DOCTOR", "USER")
//                .requestMatchers("/panel/**").hasAnyAuthority("ADMIN")
//                .requestMatchers(HttpMethod.DELETE).hasAnyAuthority("ADMIN")
//                .requestMatchers("/api/**").hasAnyAuthority("REST_API")
//                .and()
//                .formLogin()
//                .defaultSuccessUrl("/clinic") // dodane
//                .permitAll()
//                .and()
//                .logout()
//                .logoutSuccessUrl("/login")
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .permitAll();
//
//        return httpSecurity.build();
//    }

}
