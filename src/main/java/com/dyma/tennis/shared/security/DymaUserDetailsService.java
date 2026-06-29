package com.dyma.tennis.shared.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dyma.tennis.features.accounts.db.RoleEntity;
import com.dyma.tennis.features.accounts.db.UserEntity;
import com.dyma.tennis.features.accounts.db.UserRepository;

/* Ce service n'est plus utile car Keycloak gère l'authentification et les utilisateurs, mais est conservé pour référence :
@Component
public class DymaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return userRepository.findOneWithRolesByLoginIgnoreCase(login)
                .map(this::createSecurityUser)
                .orElseThrow(() -> new UsernameNotFoundException("User with login " + login + " could not be found."));

    }

    private User createSecurityUser(UserEntity userEntity) {
        List<SimpleGrantedAuthority> grantedRoles = userEntity
                .getRoles()
                .stream()
                .map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new User(userEntity.getLogin(), userEntity.getPassword(), grantedRoles);
    }
}
*/
