package com.pfe.qualite.backend.scheduler;

import com.pfe.qualite.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final NotificationService notificationService;

    @Scheduled(fixedRate = 20000)
    public void envoyerNotificationQuotidienne() {
        System.out.println("📨 Tentative d'envoi à : " + java.time.LocalTime.now());

        notificationService.creerNotification(
                "🔔 Rappel quotidien : pensez à vérifier vos fiches qualité.",
                "admin",
                "RAPPEL",
                null
        );

        System.out.println("✅ Notification automatique créée.");
    }}
