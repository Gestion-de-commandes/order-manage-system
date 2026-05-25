package com.mauth.dto;

/**
 * Réponse après login réussi.
 * Contient maintenant deux tokens :
 *   - accessToken  : token JWT normal (24h) — envoyé à chaque requête
 *   - refreshToken : token longue durée (7 jours) — stocké côté client
 *                    utilisé UNIQUEMENT pour renouveler l'accessToken
 */
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private String role;

    public JwtResponse(String accessToken, String refreshToken,
                       String username, String role) {
        this.accessToken  = accessToken;
        this.refreshToken = refreshToken;
        this.username     = username;
        this.role         = role;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getType() { return type; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}