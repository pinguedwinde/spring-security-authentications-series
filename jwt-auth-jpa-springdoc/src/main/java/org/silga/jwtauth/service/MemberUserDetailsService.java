package org.silga.jwtauth.service;

import org.jboss.logging.Logger;
import org.silga.jwtauth.model.Member;
import org.silga.jwtauth.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MemberUserDetailsService implements UserDetailsService {
    private final MemberRepository repository;
    private final Logger LOGGER = Logger.getLogger(MemberUserDetailsService.class);

    public MemberUserDetailsService(MemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = repository.findByUsername(username);
        if (member == null)
            throw new UsernameNotFoundException(username + " was not found");
        LOGGER.info("User found : " + member);
        return memberToUser(member);
    }

    private User memberToUser(Member member){
        Collection<GrantedAuthority> grantedAuthorities = member.getAuthorities().stream()
                .map(authority -> (GrantedAuthority) () -> authority)
                .collect(Collectors.toList());
        return new User(member.getUsername(), member.getPassword(), grantedAuthorities);
    }
}
