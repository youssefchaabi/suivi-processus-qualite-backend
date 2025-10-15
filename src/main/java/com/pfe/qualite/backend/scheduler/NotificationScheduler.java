package com.pfe.qualite.backend.scheduler;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.model.FormulaireObligatoire;
import com.pfe.qualite.backend.repository.NotificationRepository;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import com.pfe.qualite.backend.repository.FormulaireObligatoireRepository;
import com.pfe.qualite.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private FormulaireObligatoireRepository formulaireObligatoireRepository;

    @Autowired
    private MailService mailService;

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 */15 * * * *") // toutes les 15 minutes
    public void envoyerEmailsNotifications() {
        log.info("⏰ Planificateur exécuté...");

        // ✅ Étape 1 : récupérer les utilisateurs avec des notifications non lues
        List<String> utilisateursAvecNotif = notificationRepository
                .findAll()
                .stream()
                .filter(notif -> !notif.isLu())
                .map(Notification::getUtilisateurId)
                .distinct()
                .collect(Collectors.toList());

        if (utilisateursAvecNotif.isEmpty()) {
            log.info("✅ Aucune notification non lue à envoyer.");
            return;
        }

        // ✅ Étape 2 : REMPLACE ta boucle par ce bloc :
        for (String userId : utilisateursAvecNotif) {
            List<Notification> notifsNonLues = notificationRepository.findByUtilisateurIdAndLuFalse(userId);

            // 🎯 Récupérer l’utilisateur depuis MongoDB
            utilisateurRepository.findById(userId).ifPresentOrElse(utilisateur -> {
                String email = utilisateur.getEmail();  // ✅ E-mail réel

                if (email == null || email.isBlank()) {
                    log.warn("⚠️ Utilisateur {} n’a pas d’e-mail défini. Notification ignorée.", userId);
                    return;
                }

                // 📨 Construire le contenu
                String contenu = notifsNonLues.stream()
                        .map(Notification::getMessage)
                        .collect(Collectors.joining("\n"));

                try {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setTo(email);
                    message.setSubject("📢 Notifications non lues");
                    message.setText("Bonjour,\n\nVous avez des notifications :\n\n" + contenu);
                    mailSender.send(message);

                    log.info("📧 Email envoyé à {}", email);

                    // ✅ Marquer comme lues
                    notifsNonLues.forEach(n -> {
                        n.setLu(true);
                        notificationRepository.save(n);
                    });

                } catch (Exception e) {
                    log.error("❌ Erreur d'envoi de mail pour utilisateur {}", userId, e);
                }

            }, () -> log.warn("❌ Aucun utilisateur trouvé avec l’ID {}", userId));
        }
    }

    /**
     * Vérifier les formulaires obligatoires en retard (toutes les heures)
     */
    @Scheduled(cron = "0 0 * * * *") // toutes les heures
    public void verifierFormulairesEnRetard() {
        log.info("⏰ Vérification des formulaires en retard...");
        
        Date maintenant = new Date();
        List<FormulaireObligatoire> formulairesEnRetard = formulaireObligatoireRepository
                .findByDateEcheanceBeforeAndStatutNotSoumis(maintenant);
        
        for (FormulaireObligatoire formulaire : formulairesEnRetard) {
            // Marquer comme en retard
            formulaire.setStatut("EN_RETARD");
            formulaireObligatoireRepository.save(formulaire);
            
            // Envoyer email de notification
            utilisateurRepository.findById(formulaire.getResponsableId()).ifPresent(utilisateur -> {
                if (utilisateur.getEmail() != null && !utilisateur.getEmail().isBlank()) {
                    try {
                        mailService.envoyerEmailRetard(
                            utilisateur.getEmail(),
                            formulaire.getNom(),
                            formulaire.getDateEcheance()
                        );
                        log.info("📧 Email de retard envoyé à {} pour le formulaire {}", 
                                utilisateur.getEmail(), formulaire.getNom());
                    } catch (Exception e) {
                        log.error("❌ Erreur d'envoi d'email de retard pour {}", utilisateur.getEmail(), e);
                    }
                }
            });
        }
        
        if (!formulairesEnRetard.isEmpty()) {
            log.info("⚠️ {} formulaires marqués comme en retard", formulairesEnRetard.size());
        }
    }

    /**
     * Vérifier les échéances proches (toutes les 6 heures)
     */
    @Scheduled(cron = "0 0 */6 * * *") // toutes les 6 heures
    public void verifierEcheancesProches() {
        log.info("⏰ Vérification des échéances proches...");
        
        Date maintenant = new Date();
        Date dans24h = new Date(maintenant.getTime() + 24 * 60 * 60 * 1000); // +24h
        
        List<FormulaireObligatoire> formulairesEcheanceProche = formulaireObligatoireRepository
                .findByDateEcheanceBetweenAndStatutEnAttente(maintenant, dans24h);
        
        for (FormulaireObligatoire formulaire : formulairesEcheanceProche) {
            utilisateurRepository.findById(formulaire.getResponsableId()).ifPresent(utilisateur -> {
                if (utilisateur.getEmail() != null && !utilisateur.getEmail().isBlank()) {
                    try {
                        SimpleMailMessage message = new SimpleMailMessage();
                        message.setTo(utilisateur.getEmail());
                        message.setSubject("⚠️ Échéance proche - " + formulaire.getNom());
                        message.setText(String.format(
                            "Bonjour,\n\n" +
                            "Le formulaire obligatoire '%s' arrive à échéance le %s.\n" +
                            "Veuillez le compléter dans les plus brefs délais.\n\n" +
                            "Cordialement,\n" +
                            "Système de Suivi Qualité",
                            formulaire.getNom(),
                            formulaire.getDateEcheance().toString()
                        ));
                        mailSender.send(message);
                        
                        log.info("📧 Email d'échéance proche envoyé à {} pour le formulaire {}", 
                                utilisateur.getEmail(), formulaire.getNom());
                    } catch (Exception e) {
                        log.error("❌ Erreur d'envoi d'email d'échéance pour {}", utilisateur.getEmail(), e);
                    }
                }
            });
        }
        
        if (!formulairesEcheanceProche.isEmpty()) {
            log.info("⚠️ {} formulaires avec échéance proche notifiés", formulairesEcheanceProche.size());
        }
    }
}
