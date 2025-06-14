package com.pfe.qualite.backend.scheduler;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.repository.NotificationRepository;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    @Autowired
    private UtilisateurRepository utilisateurRepository;


    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Scheduled(fixedRate = 100000000) // chaque 60 secondes
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
    }}
