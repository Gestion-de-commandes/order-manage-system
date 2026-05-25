package com.mauth.security;

import com.mauth.model.User;
import com.mauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security appelle loadUserByUsername() automatiquement
 * quand quelqu'un essaie de se connecter.
 *
 * Cette classe va chercher l'utilisateur dans PostgreSQL
 * et retourne un objet UserDetails que Spring Security comprend.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Charge un utilisateur depuis la BDD par son username.
     * Appelé automatiquement par Spring Security.
     *
     * @param username le nom d'utilisateur saisi au login
     * @return UserDetails contenant username, password hashé, et rôles
     * @throws UsernameNotFoundException si l'utilisateur n'existe pas
     */
    @Override
    @Transactional  // nécessaire pour charger les rôles (relation LAZY par défaut)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // 1. Cherche l'utilisateur en base
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilisateur non trouvé : " + username));

        // 2. Convertit les Role en GrantedAuthority
        // Spring Security travaille avec des GrantedAuthority, pas des Role
        // Ex: Role("ROLE_USER") → SimpleGrantedAuthority("ROLE_USER")
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        // 3. Retourne un UserDetails standard de Spring Security
        // Spring va comparer le password fourni avec user.getPassword() (hashé)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}