package com.nhamt.book_store.configuration;

import com.nhamt.book_store.entity.User;
import com.nhamt.book_store.enums.Role;
import com.nhamt.book_store.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor //replace for @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //replace for @Autowired
@Slf4j //using for logging
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    // will be init when start application
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUsername("admin").isEmpty()){
                HashSet<String> adminRoles = new HashSet<>();
                adminRoles.add(Role.ADMIN.name());

                User user = User.builder()
                        .username("admin")
                        //.roles(adminRoles)
                        .password(passwordEncoder.encode("admin"))
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin. Please change it.");
            }
        };
    }
}
