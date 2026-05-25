package com.mauth.dto;

import javax.validation.constraints.NotBlank;

/**
 * JSON attendu pour le login :
 * {
 *   "username": "alice",
 *   "password": "monMotDePasse"
 * }
 */
public class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public LoginRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}