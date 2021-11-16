package org.silga.oauth2_social_jwt.service;

import org.silga.oauth2_social_jwt.dto.LocalUser;
import org.silga.oauth2_social_jwt.exception.ResourceNotFoundException;
import org.silga.oauth2_social_jwt.model.User;
import org.silga.oauth2_social_jwt.util.GeneralUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("localUserDetailService")
public class LocalUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public LocalUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public LocalUser loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User " + email + " was not found in the database");
        }
        return createLocalUser(user);
    }

    @Transactional
    public LocalUser loadUserById(Long id) {
        User user = userService.findUserById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return createLocalUser(user);
    }

    private LocalUser createLocalUser(User user) {
        return new LocalUser(user.getEmail(), user.getPassword(), user.isEnabled(), true, true, true, GeneralUtils.buildSimpleGrantedAuthorities(user.getRoles()), user);
    }
}
