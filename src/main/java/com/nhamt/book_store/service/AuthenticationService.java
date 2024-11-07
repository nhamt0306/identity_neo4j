package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.AuthenticationRequest;
import com.nhamt.book_store.dto.request.IntrospectRequest;
import com.nhamt.book_store.dto.response.AuthenticationResponse;
import com.nhamt.book_store.dto.response.IntrospectResponse;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.exception.ErrorCode;
import com.nhamt.book_store.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal // do not inject this variable to constructor
    @Value("${jwt.signerKey}") //Read SIGNER_KEY form application.yaml file
    protected String SIGNER_KEY;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isAuthenticated){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    private String generateToken(User user){
        //1. create header for jwt
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        //2　create payload for jwt
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername()) //username of user
                .issuer("rainy-global.com") //Token from
                .issueTime(new Date()) //Start time of token
                .expirationTime(new Date(
                        System.currentTimeMillis()+ 60*60*1000 // 60 minutes
                )) //expire time of token
                .claim("customField","Nha Mai")
                .claim("scope",buildScopeFromUser(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        //3. generate jwsObject = header + payload
        JWSObject jwsObject = new JWSObject(header, payload);

        //4. sign jwsObject to authen
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.INVALID_SIGN_JWT);
        }

    }

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();
        // 1. Get verifier = algorithm using to sign jwsObject
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        //2. Parse token
        SignedJWT signedJWT = SignedJWT.parse(token);

        //3. Verify token
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        boolean verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .isValid(verified && expireTime.after(new Date()))
                .build();
    }

    private String buildScopeFromUser(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!user.getRoles().isEmpty()){
            user.getRoles().forEach(s -> stringJoiner.add(s));
        }
        return stringJoiner.toString();
    }
}
