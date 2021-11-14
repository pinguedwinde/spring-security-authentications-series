package org.silga.jwtauth.config;

import org.silga.jwtauth.model.Member;
import org.silga.jwtauth.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class MemberConfig implements CommandLineRunner {

    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    public MemberConfig(MemberRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        repository.saveAll(
                List.of(
                        new Member("monika", passwordEncoder.encode("123456"), "Monika", List.of("ROLE_ADMIN", "ROLE_USER")),
                        new Member("jack", passwordEncoder.encode("123456"), "Jack", List.of("ROLE_USER")),
                        new Member("peter", "123456", "Peter", List.of("ROLE_USER"))
                )
        );
    }
}
