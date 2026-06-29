package com.dyma.tennis.shared.security;

import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import org.springframework.http.HttpMethod;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@EnableWebSecurity
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class SecurityConfiguration {

        @Autowired
        private KeycloakTokenConverter keycloakTokenConverter;

        /*
         * Les propriétés et beans suivants ne sont plus nécessaires avec Keycloak :
         * private static final Logger log =
         * LoggerFactory.getLogger(SecurityConfiguration.class);
         * 
         * @Value("${jwt.base64-secret}")
         * private String jwtSecret;
         * 
         * @Autowired
         * private DymaUserDetailsService dymaUserDetailsService;
         * 
         * @Bean
         * public PasswordEncoder passwordEncoder() {
         * return new BCryptPasswordEncoder();
         * }
         * 
         * @Bean
         * public AuthenticationManager authenticationManager(UserDetailsService
         * userDetailsService,
         * PasswordEncoder passwordEncoder) {
         * 
         * DaoAuthenticationProvider authenticationProvider = new
         * DaoAuthenticationProvider(dymaUserDetailsService);
         * authenticationProvider.setPasswordEncoder(passwordEncoder);
         * 
         * return new ProviderManager(authenticationProvider);
         * }
         * 
         * @Bean
         * public JwtEncoder jwtEncoder() {
         * return new NimbusJwtEncoder(new ImmutableSecret<>(getScretKey()));
         * }
         * 
         * @Bean
         * public JwtDecoder jwtDecoder() {
         * NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getScretKey())
         * .macAlgorithm(SecurityUtils.JWT_ALGORITHM).build();
         * return token -> {
         * try {
         * return jwtDecoder.decode(token);
         * } catch (Exception e) {
         * log.error("Erreur de décodage du JWT : {}", e.getMessage());
         * throw e;
         * }
         * };
         * }
         * 
         * @Bean
         * public JwtAuthenticationConverter jwtAuthenticationConverter() {
         * JwtAuthenticationConverter jwtAuthenticationConverter = new
         * JwtAuthenticationConverter();
         * jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
         * return jwt.getClaimAsStringList(SecurityUtils.AUTHORITIES_CLAIM_KEY)
         * .stream()
         * .map(SimpleGrantedAuthority::new)
         * .collect(Collectors.toList());
         * });
         * return jwtAuthenticationConverter;
         * }
         * 
         * private SecretKey getScretKey() {
         * byte[] keyBytes = Base64.from(jwtSecret).decode();
         * return new SecretKeySpec(keyBytes, 0, keyBytes.length,
         * SecurityUtils.JWT_ALGORITHM.getName());
         * }
         */

        @Bean
        @Profile("!test")
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .headers(headers -> headers
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self' data:; style-src 'self' 'unsafe-inline';"))
                                                .frameOptions(frameOptionsConfig -> frameOptionsConfig.deny())
                                                .permissionsPolicyHeader(
                                                                permissionsPolicyConfig -> permissionsPolicyConfig
                                                                                .policy("fullscreen=(self), geolocation(), microphone(), camera()")))
                                .authorizeHttpRequests(authorization -> authorization
                                                .requestMatchers(HttpMethod.GET, "/players/**")
                                                .hasAuthority("ROLE_USER")
                                                .requestMatchers(HttpMethod.POST, "/players/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/players/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/players/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.GET, "/tournaments/**")
                                                .hasAuthority("ROLE_USER")
                                                .requestMatchers(HttpMethod.POST, "/tournaments/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/tournaments/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/tournaments/**")
                                                .hasAuthority("ROLE_ADMIN")
                                                .requestMatchers("/healthcheck/**").permitAll()
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                // Avant avec jwt : .requestMatchers("/accounts/login").permitAll()
                                                .requestMatchers("/accounts/token").permitAll()
                                                .requestMatchers("/actuator/**").permitAll()
                                                .anyRequest().authenticated()

                                )
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .oauth2ResourceServer(
                                                oauth2 -> oauth2.jwt(jwt -> jwt
                                                                .jwtAuthenticationConverter(keycloakTokenConverter)));

                return http.build();
        }

}
