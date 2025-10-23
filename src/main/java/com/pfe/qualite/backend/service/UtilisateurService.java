package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.model.Utilisateur;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    // Note: HistoriqueService sera ajouté au Sprint 2 pour la traçabilité avancée

    /**
     * Récupérer tous les utilisateurs
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Récupérer un utilisateur par ID
     */
    public Optional<Utilisateur> getUtilisateurById(String id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Récupérer un utilisateur par email
     */
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    /**
     * Créer un nouvel utilisateur (Admin uniquement)
     */
    public Utilisateur creerUtilisateur(Utilisateur utilisateur, String adminId) {
        log.info("Création d'un nouvel utilisateur: {}", utilisateur.getEmail());
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(utilisateur.getEmail()).isPresent()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Générer un mot de passe temporaire si non fourni
        String motDePasseTemporaire = utilisateur.getPassword();
        if (motDePasseTemporaire == null || motDePasseTemporaire.isEmpty()) {
            motDePasseTemporaire = genererMotDePasseTemporaire();
        }

        // Encoder le mot de passe
        utilisateur.setPassword(passwordEncoder.encode(motDePasseTemporaire));
        
        // Définir les valeurs par défaut
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateur.setCreePar(adminId);

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        // Envoyer notification email avec mot de passe temporaire
        try {
            notificationService.envoyerEmailBienvenue(
                saved.getEmail(),
                saved.getNom(),
                motDePasseTemporaire
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de bienvenue", e);
        }

        log.info("Utilisateur créé avec succès: {}", saved.getId());
        return saved;
    }

    /**
     * Mettre à jour un utilisateur
     */
    public Utilisateur updateUtilisateur(String id, Utilisateur utilisateurUpdate) {
        log.info("Mise à jour de l'utilisateur: {}", id);
        
        return utilisateurRepository.findById(id).map(user -> {
            user.setNom(utilisateurUpdate.getNom());
            user.setEmail(utilisateurUpdate.getEmail());
            user.setRole(utilisateurUpdate.getRole());
            user.setTelephone(utilisateurUpdate.getTelephone());
            
            // Ne pas modifier le mot de passe ici
            if (utilisateurUpdate.getActif() != null) {
                user.setActif(utilisateurUpdate.getActif());
            }
            
            user.setDateModification(LocalDateTime.now());
            
            return utilisateurRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id: " + id));
    }

    /**
     * Activer/Désactiver un utilisateur
     */
    public Utilisateur toggleActif(String id) {
        log.info("Toggle actif pour l'utilisateur: {}", id);
        
        return utilisateurRepository.findById(id).map(user -> {
            user.setActif(!user.getActif());
            user.setDateModification(LocalDateTime.now());
            
            Utilisateur saved = utilisateurRepository.save(user);
            
            // Envoyer notification
            String message = saved.getActif() ? "activé" : "désactivé";
            try {
                notificationService.envoyerNotification(
                    saved.getId(),
                    "Compte " + message,
                    "Votre compte a été " + message + " par l'administrateur.",
                    "INFO"
                );
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de notification", e);
            }
            
            return saved;
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Réinitialiser le mot de passe
     */
    public String resetMotDePasse(String id) {
        log.info("Réinitialisation du mot de passe pour l'utilisateur: {}", id);
        
        return utilisateurRepository.findById(id).map(user -> {
            String nouveauMotDePasse = genererMotDePasseTemporaire();
            user.setPassword(passwordEncoder.encode(nouveauMotDePasse));
            user.setDateModification(LocalDateTime.now());
            
            utilisateurRepository.save(user);
            
            // Envoyer email avec nouveau mot de passe
            try {
                notificationService.envoyerEmailResetPassword(
                    user.getEmail(),
                    user.getNom(),
                    nouveauMotDePasse
                );
            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de l'email de reset", e);
            }
            
            return nouveauMotDePasse;
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    /**
     * Supprimer un utilisateur
     */
    public void deleteUtilisateur(String id) {
        log.info("Suppression de l'utilisateur: {}", id);
        
        if (!utilisateurRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur non trouvé avec l'id: " + id);
        }
        
        utilisateurRepository.deleteById(id);
    }

    /**
     * Générer un mot de passe temporaire aléatoire
     */
    private String genererMotDePasseTemporaire() {
        return "Temp" + UUID.randomUUID().toString().substring(0, 8) + "!";
    }

    /**
     * Compter les utilisateurs par rôle
     */
    public long countByRole(String role) {
        return utilisateurRepository.findAll().stream()
            .filter(u -> role.equals(u.getRole()))
            .count();
    }

    /**
     * Compter les utilisateurs actifs
     */
    public long countActifs() {
        return utilisateurRepository.findAll().stream()
            .filter(u -> Boolean.TRUE.equals(u.getActif()))
            .count();
    }
}
