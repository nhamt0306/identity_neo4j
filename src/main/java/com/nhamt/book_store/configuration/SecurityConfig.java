package com.nhamt.book_store.configuration;

import com.nhamt.book_store.enums.Role;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINT = {"/users/create", "/auth/token", "/auth/introspect","/auth/logout"};

    @Autowired
    CustomJwtDecoder customJwtDecoder;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request -> {
            request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll() //filter url -> passed
                    //.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()) //the same: hasAuthority("ROLE_ADMIN")
                    .anyRequest().authenticated(); //other -> denied
        });
        //register an authentication provider -> support for jwt token
        //<-> when we send a request with header is BEARER token -> validate jwt token
        httpSecurity.oauth2ResourceServer(oauth2 -> {
            oauth2.jwt(jwtConfigurer -> jwtConfigurer
                    .decoder(customJwtDecoder) //for decode jwt token to get info and verify
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())) // for customize prefix of authority/role
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint()); //redirect when error

        });

        httpSecurity.csrf(AbstractHttpConfigurer::disable); //disable CORS
        return httpSecurity.build();
    }

    //Custom prefix of authenticator in JWT
    //default is SCOPE_ -> replace to ROLE_
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    @Bean // can be injected to using @Autowired in other class
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);
    }
}

