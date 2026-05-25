package com.mpaiement.soap.security;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.springframework.ws.soap.security.wss4j2.callback.UsernameTokenPrincipalCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Spring-WS peut envoyer deux types de callbacks :
 *
 * 1. WSPasswordCallback → quand il veut VÉRIFIER le mot de passe
 *    Spring nous donne le username et attend qu'on lui donne
 *    le mot de passe attendu pour comparer
 *
 * 2. UsernameTokenPrincipalCallback → quand il nous informe
 *    que l'authentification a réussi (notification seulement)
 *    On n'a rien à faire ici, juste ignorer
 *
 * L'erreur venait du fait qu'on traitait les deux comme
 * WSPasswordCallback → ClassCastException
 */
public class SimplePasswordValidator implements CallbackHandler {

    @Override
    public void handle(Callback[] callbacks)
            throws IOException, UnsupportedCallbackException {

        for (Callback callback : callbacks) {

            if (callback instanceof WSPasswordCallback) {
                // Cas 1 : Spring demande le mot de passe attendu
                WSPasswordCallback pc = (WSPasswordCallback) callback;

                if ("hope".equals(pc.getIdentifier())) {
                    pc.setPassword("paiement2026");
                } else if ("admin".equals(pc.getIdentifier())) {
                    pc.setPassword("admin123");
                }

            } else if (callback instanceof UsernameTokenPrincipalCallback) {
                // Cas 2 : Spring informe que auth a réussi
                // Rien à faire — on ignore simplement
            } else {
                throw new UnsupportedCallbackException(callback,
                    "Type de callback non supporté");
            }
        }
    }
}
