package com.dyma.tennis.features.accounts.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.dyma.tennis.shared.security.JwtService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Tag(name = "Accounts API")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String tokenIssuerUrl;

    @Value("${jwt.auth.client-id}")
    private String clientId;

    @Operation(summary = "Gets an access token", description = "Gets an access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token was provided.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserAuthentication.class)) }),
            @ApiResponse(responseCode = "400", description = "Login or password is not provided."),
            @ApiResponse(responseCode = "500", description = "An error occurred while asking for an access token.")
    })
    @PostMapping("/token")
    public ResponseEntity<UserAuthentication> getAccessToken(@RequestBody @Valid UserCredentials credentials) {
        String url = tokenIssuerUrl + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = Map.of(
                "username", credentials.login(),
                "password", credentials.password(),
                "grant_type", "password",
                "client_id", clientId).entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        String accessToken = (String) Objects.requireNonNull(response.getBody()).get("access_token");

        return ResponseEntity.ok(new UserAuthentication(credentials.login(), accessToken));
    }
}

/*
 * Avec Keycloak, les endpoints de login/logout locaux ne sont plus utilisés :
 * 
 * @Autowired
 * private AuthenticationManager authenticationManager;
 * 
 * @Autowired
 * private JwtService jwtService;
 * 
 * private final SecurityContextLogoutHandler securityContextLogoutHandler = new
 * SecurityContextLogoutHandler();
 * 
 * @PostMapping("/login")
 * public ResponseEntity<UserAuthentication> login(@RequestBody @Valid
 * UserCredentials credentials,
 * HttpServletRequest request,
 * HttpServletResponse response) {
 * UsernamePasswordAuthenticationToken authenticationToken = new
 * UsernamePasswordAuthenticationToken(
 * credentials.login(), credentials.password());
 * 
 * Authentication authentication =
 * authenticationManager.authenticate(authenticationToken);
 * 
 * String jwt = jwtService.createToken(authentication);
 * 
 * return new ResponseEntity<>(new UserAuthentication(authentication.getName(),
 * jwt), HttpStatus.OK);
 * 
 * }
 * // Plus utilisé avec les jwt mais possible d'en créer un
 * // @GetMapping("/logout")
 * // public void logout(Authentication authentication, HttpServletRequest
 * request,
 * // HttpServletResponse response) {
 * // securityContextLogoutHandler.logout(request, response, authentication);
 * // }
 */
