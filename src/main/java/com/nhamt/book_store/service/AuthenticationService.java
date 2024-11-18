package com.nhamt.book_store.service;

import com.nhamt.book_store.dto.request.AuthenticationRequest;
import com.nhamt.book_store.dto.request.IntrospectRequest;
import com.nhamt.book_store.dto.request.LogoutRequest;
import com.nhamt.book_store.dto.request.RefreshRequest;
import com.nhamt.book_store.dto.response.AuthenticationResponse;
import com.nhamt.book_store.dto.response.IntrospectResponse;
import com.nhamt.book_store.entity.InvalidatedToken;
import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.exception.AppException;
import com.nhamt.book_store.exception.ErrorCode;
import com.nhamt.book_store.repository.InvalidatedTokenRepository;
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
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal // do not inject this variable to constructor
    @Value("${jwt.signerKey}") //Read SIGNER_KEY form application.yaml file
    protected String SIGNER_KEY;

    @NonFinal // do not inject this variable to constructor
    @Value("${jwt.valid-duration}")
    protected int VALID_DURATION;

    @NonFinal // do not inject this variable to constructor
    @Value("${jwt.refreshable-duration}")
    protected int REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isAuthenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!isAuthenticated){
            throw new AppException(ErrorCode.UNAUTHORIZED);
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
                        System.currentTimeMillis()+ VALID_DURATION*60*1000 // 60 minutes
                )) //expire time of token
                .claim("customField","Nha Mai")
                .claim("scope",buildScopeFromUser(user))
                .jwtID(UUID.randomUUID().toString())//id of jwt token, user for log out
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
        boolean isValid = true;
        try {
            parseVerifyToken(token, false);
        } catch (AppException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .isValid(isValid)
                .build();
    }

    private String buildScopeFromUser(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!user.getRoles().isEmpty()){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!role.getPermissions().isEmpty()){
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = parseVerifyToken(request.getToken(), true);
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(expireTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }


    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        SignedJWT signedJWT = parseVerifyToken(request.getToken(), true);
        // logout old token
        String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
        Date expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jwtId)
                .expiryTime(expireTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        // generate new token for user
        String username = signedJWT.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    private SignedJWT parseVerifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        // 1. Get verifier = algorithm using to sign jwsObject
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        //2. Parse token
        SignedJWT signedJWT = SignedJWT.parse(token);

        //3. Verify token
        Date expireTime;
        // if isRefresh -> use for refresh token (authenticate or introspect token)-> check expireTime
        if (isRefresh)
            expireTime = new Date (signedJWT.getJWTClaimsSet().getIssueTime().getSeconds() + REFRESHABLE_DURATION);
        else // else
            expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        //check token valid ~ sign is true and valid expire time
        if(!(verified && expireTime.after(new Date()))) // is valid => expire time > current time
        {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }
}
