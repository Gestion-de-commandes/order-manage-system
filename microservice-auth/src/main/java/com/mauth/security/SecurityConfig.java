package com.mauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * BCrypt — hachage des mots de passe.
     * Lent intentionnellement pour résister aux attaques brute force.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Relie Spring Security à notre base de données via UserDetailsService.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Nécessaire pour déclencher l'authentification manuellement dans AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Règles de sécurité HTTP.
     *
     * Endpoints publics (sans token) :
     *   /auth/register  → inscription
     *   /auth/login     → connexion
     *   /auth/refresh   → renouvellement du token
     *   /auth/validate  → vérification token par le Gateway
     *   /auth/me        → infos utilisateur depuis token
     *   /swagger-ui/**  → documentation API visuelle
     *   /v3/api-docs/** → données brutes pour Swagger
     *
     * Tout le reste nécessite un token JWT valide.
     * STATELESS = pas de session HTTP, chaque requête est indépendante.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                .antMatchers(
                    "/auth/register",
                    "/auth/login",
                    "/auth/refresh",
                    "/auth/admin/**", 
                    "/auth/validate",
                    "/auth/me",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}