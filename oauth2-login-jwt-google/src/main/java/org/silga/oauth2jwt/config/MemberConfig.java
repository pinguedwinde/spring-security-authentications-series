package org.silga.oauth2jwt.config;

import org.silga.oauth2jwt.model.Member;
import org.silga.oauth2jwt.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MemberConfig implements CommandLineRunner {

    private final MemberRepository repository;

    public MemberConfig(MemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        repository.saveAll(
                List.of(
                        new Member("sliga.brice@gmail.com", "google", "Fabrice", List.of("ROLE_ADMIN", "ROLE_USER")),
                        new Member("jack@gmail.com", "google", "Jack", List.of("ROLE_USER")),
                        new Member("peter@gmail.com", "google", "Peter", List.of("ROLE_USER"))
                )
        );
    }
}
