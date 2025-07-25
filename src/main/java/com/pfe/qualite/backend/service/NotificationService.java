package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void creerNotification(String message, String utilisateurId, String type, String objetId) {
        // Vérification : l'utilisateurId doit être un ID MongoDB valide (24 caractères hexadécimaux)
        if (utilisateurId == null || !utilisateurId.matches("^[a-fA-F0-9]{24}$")) {
            logger.error("[NotificationService] utilisateurId invalide : '{}' (notification ignorée)", utilisateurId);
            return;
        }
        Notification notification = Notification.builder()
                .message(message)
                .utilisateurId(utilisateurId)
                .type(type)
                .objetId(objetId)
                .lu(false)
                .dateCreation(new java.util.Date())
                .build();

        notificationRepository.save(notification);
    }
}
