package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Notification;
import com.pfe.qualite.backend.repository.NotificationRepository;
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

}
