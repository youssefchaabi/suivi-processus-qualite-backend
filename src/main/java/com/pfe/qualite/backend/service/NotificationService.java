package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void creerNotification(String message, String utilisateurId, String type, String objetId) {
        Notification notification = Notification.builder()
                .message(message)
                .utilisateurId(utilisateurId)
                .type(type)
                .objetId(objetId)
                .lu(false)
                .dateCreation(new Date())
                .build();

        notificationRepository.save(notification);
    }
}
