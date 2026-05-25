package com.mauth.dto;

import javax.validation.constraints.NotBlank;

/**
 * Requête pour renouveler un token expiré.
 * Le client envoie son refresh token et reçoit un nouveau access token.
 *
 * Pourquoi un refresh token séparé ?
 * - L'access token (JWT) expire vite (24h) pour limiter les risques si volé
 * - Le refresh token dure plus longtemps (7 jours) et sert UNIQUEMENT
 *   à générer de nouveaux access tokens
 * - Si le refresh token est volé, on peut le révoquer en base
 */
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public RefreshTokenRequest() {}

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}