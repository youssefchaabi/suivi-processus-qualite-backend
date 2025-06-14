package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.model.Utilisateur;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    // Accessible seulement à l'admin via contrôleur
    public Utilisateur creerUtilisateur(String email, String nom, String role, String password) {
        if (utilisateurRepository.findByEmail(email).isPresent())
            throw new RuntimeException("Utilisateur existe déjà");

        Utilisateur user = Utilisateur.builder()
                .email(email)
                .nom(nom)
                .role(role)
                .password(passwordEncoder.encode(password))
                .build();

        return utilisateurRepository.save(user);
    }
}
