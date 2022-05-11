package service.vaxapp.service;

import service.vaxapp.model.User;
import service.vaxapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        System.out.println("Trying to load user using email provided: " + email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            System.out.println("User is null because email is not found in db");
            throw new UsernameNotFoundException(email);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // for (Role role : user.getRoles()){
        // grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        // }

        System.out.println("Loading user by username now... User: " + user.getEmail() + ", " + user.getPassword());
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                grantedAuthorities);
    }
}