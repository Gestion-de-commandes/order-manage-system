package com.mauth.controller;

import com.mauth.dto.JwtResponse;
import com.mauth.dto.LoginRequest;
import com.mauth.dto.RefreshTokenRequest;
import com.mauth.dto.RegisterRequest;
import com.mauth.model.Role;
import com.mauth.model.User;
import com.mauth.repository.RoleRepository;
import com.mauth.repository.UserRepository;
import com.mauth.security.JwtUtils;
import com.mauth.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    // ----------------------------------------------------------------
    // POST /auth/register
    // ----------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername()))
            return ResponseEntity.badRequest()
                    .body("Erreur : ce username est déjà pris !");

        if (userRepository.existsByEmail(request.getEmail()))
            return ResponseEntity.badRequest()
                    .body("Erreur : cet email est déjà utilisé !");

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        String roleName = (request.getRole() != null
                && request.getRole().equals("ROLE_ADMIN"))
                ? "ROLE_ADMIN" : "ROLE_USER";

        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(
                "Utilisateur enregistré avec succès ! Rôle : " + roleName);
    }

    // ----------------------------------------------------------------
    // POST /auth/login
    // ----------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken  = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        return ResponseEntity.ok(
                new JwtResponse(accessToken, refreshToken,
                        userDetails.getUsername(), role));
    }

    // ----------------------------------------------------------------
    // POST /auth/refresh
    // ----------------------------------------------------------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtUtils.validateToken(refreshToken))
            return ResponseEntity.status(401)
                    .body("Refresh token invalide ou expiré !");

        String username = jwtUtils.getUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(
                newAccessToken, refreshToken, username,
                userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(a -> a.getAuthority())
                        .orElse("ROLE_USER")
        ));
    }

    // ----------------------------------------------------------------
    // GET /auth/validate?token=xxx
    // Appelé par le Gateway pour valider chaque requête entrante.
    // ----------------------------------------------------------------
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam String token) {
        if (!jwtUtils.validateToken(token))
            return ResponseEntity.status(401).body("Token invalide ou expiré");

        String username = jwtUtils.getUsernameFromToken(token);
        return ResponseEntity.ok("Token valide pour : " + username);
    }

    // ----------------------------------------------------------------
    // GET /auth/me?token=xxx
    // Retourne username + rôle depuis le token.
    // ----------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam String token) {
        if (!jwtUtils.validateToken(token))
            return ResponseEntity.status(401).body("Token invalide");

        String username = jwtUtils.getUsernameFromToken(token);
        String role = jwtUtils.getRoleFromToken(token);

        return ResponseEntity.ok(
                "{ \"username\": \"" + username
                + "\", \"role\": \"" + role + "\" }");
    }

    // ----------------------------------------------------------------
    // GET /auth/admin/users?token=xxx
    //
    // DÉMONSTRATION DE L'AUTORISATION PAR RÔLE :
    // Cet endpoint est accessible UNIQUEMENT aux ROLE_ADMIN.
    // Un ROLE_USER reçoit 403 Forbidden.
    //
    // C'est ici qu'on voit la différence entre :
    //   - Authentification : "qui es-tu ?" → vérifié par le token JWT
    //   - Autorisation     : "as-tu le droit ?" → vérifié par le rôle
    // ----------------------------------------------------------------
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAllUsers(@RequestParam String token) {

        // 1. Vérifie que le token est valide
        if (!jwtUtils.validateToken(token))
            return ResponseEntity.status(401).body("Token invalide ou expiré");

        // 2. Extrait le rôle depuis le token (pas d'appel BDD)
        String role = jwtUtils.getRoleFromToken(token);

        // 3. Vérifie que c'est un admin
        if (!"ROLE_ADMIN".equals(role))
            return ResponseEntity.status(403).body(
                    "Accès refusé — cet endpoint est réservé aux administrateurs. "
                    + "Votre rôle : " + role);

        // 4. Retourne la liste de tous les utilisateurs
        List<String> users = userRepository.findAll().stream()
                .map(u -> "{ \"id\": " + u.getId()
                        + ", \"username\": \"" + u.getUsername()
                        + "\", \"email\": \"" + u.getEmail()
                        + "\", \"roles\": " + u.getRoles().stream()
                                .map(r -> "\"" + r.getName() + "\"")
                                .collect(Collectors.joining(", ", "[", "]"))
                        + " }")
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }
}