package com.mcart.productservice.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.text.ParseException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private static final String JWKS_JSON = """
            {
                "keys": [
                    {
                        "alg": "RS256",
                        "e": "AQAB",
                        "kid": "BbF3511i9PxUURykQiV3AUkKDFV9LiCfIkDkabQaHN0=",
                        "kty": "RSA",
                        "n": "vw1njgzPalzyDcYrco33U5mbCFKVzmj2D4o5q8uHFl0ADyTo6J5UI_fIzoN08530dQHrGslLjIVZpKT810G1OwH5WC8e_yCRT5SiblDJmayOcF1R3l2IWMpZXS1csZdUno0O4-BYE513iFhRJ6SBBpjdxOe3V2eOcVUuFd8hDnmQaI4nbH-75ircCxUurQ3S3v3_ccv0VDWHpzW9ts4lve4nxANO5AfTgG8PqoM-4OnQe9tilhOqDyYFIYcjXKAzUnYfm2eg3FHqL2idHDlqT7MWlErrymEAvILJqVacKv8HCrd4M4YWLbvx8ysYDSWmsaLTBooUrKSNkRGckOw1Uw",
                        "use": "sig"
                    },
                    {
                        "alg": "RS256",
                        "e": "AQAB",
                        "kid": "7fBr4IdxCk5z/9j6q/WxTEPei5hG5XKtnlBJJ/tQYS8=",
                        "kty": "RSA",
                        "n": "0QmEK5GIduv9yvxSxcCQNXXTe7yndrq_LYzuED5IxCWr2-fAJiM5J8meZS7OOTwaQQO_WrpKjmszL1B67ouM-kEDCt-388rRDtxTPhIVNmkLS9IYdlXuB0iRGMegbtL_AsFk07xB7gDsdVUc_OY864XId7IZjUcCE6M8iUMHeDRmwI3b3Ep_ZLo6UNtJ02AfjOg4Q1CNMaS8_ccyLpBm1Qzm9uU-qvK8CBv3l2E--OSKBTnn36_voFgVCx7hcp0AFeyils_w0R-vYRjciV4tEmbqFsP9h5G9DaVbN8vjbVfdxZAhSLipgAjhpUTLSLY-QxEB1ZuzT0tTR3DfH5eATQ",
                        "use": "sig"
                    }
                ]
            }
            """;

    @Bean
    public JwtDecoder jwtDecoder() throws ParseException {
        // Parse hardcoded JWKS — no network call needed
        JWKSet jwkSet = JWKSet.parse(JWKS_JSON);
        ImmutableJWKSet<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet);

        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withJwkSetUri("https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_w11q1SuUr/.well-known/jwks.json")
                .build();

        // ✅ Skip "aud" validation — Cognito access tokens don't have it
        // ✅ Only validate issuer + expiry
        OAuth2TokenValidator<Jwt> validator = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                new JwtIssuerValidator("https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_w11q1SuUr")
        );
        decoder.setJwtValidator(validator);

        return decoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/version", "/health").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {
                            try {
                                jwt.decoder(jwtDecoder());
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        })
                );

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        converter.setPrincipalClaimName("sub");
        return converter;
    }
}