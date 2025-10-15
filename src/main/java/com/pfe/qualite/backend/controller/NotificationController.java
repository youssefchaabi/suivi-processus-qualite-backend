package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.repository.NotificationRepository;
import com.pfe.qualite.backend.service.MailService;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        notification.setDateCreation(new Date()); // date de création auto
        return notificationRepository.save(notification);
    }

    // 🔹 GET : toutes les notifications
    @GetMapping
    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<Notification> getByUtilisateur(@PathVariable String utilisateurId) {
        return notificationRepository.findByUtilisateurId(utilisateurId);
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public List<Notification> getNonLues(@PathVariable String utilisateurId) {
        return notificationRepository.findByUtilisateurIdAndLuFalse(utilisateurId);
    }

    @PutMapping("/{id}/lire")
    public Notification marquerCommeLue(@PathVariable String id) {
        Optional<Notification> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            Notification n = notification.get();
            n.setLu(true);
            return notificationRepository.save(n);
        } else {
            throw new RuntimeException("Notification introuvable avec ID : " + id);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id) {
        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        notificationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/relancer")
    public ResponseEntity<String> relancer(@RequestBody RelanceRequest req) {
        var list = notificationRepository.findByUtilisateurId(req.utilisateurId);
        if (list.isEmpty()) return ResponseEntity.badRequest().body("Aucune notification pour cet utilisateur");

        var userOpt = utilisateurRepository.findById(req.utilisateurId);
        if (userOpt.isEmpty() || userOpt.get().getEmail() == null || userOpt.get().getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Utilisateur sans email");
        }
        var email = userOpt.get().getEmail();

        try {
            mailService.sendEmail(email, "Relance notification", req.message != null ? req.message : "Vous avez une notification en attente");
            return ResponseEntity.ok("Relance envoyée");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur d'envoi");
        }
    }

    public static class RelanceRequest {
        public String utilisateurId;
        public String notificationId;
        public String type;
        public String message;
    }
}
