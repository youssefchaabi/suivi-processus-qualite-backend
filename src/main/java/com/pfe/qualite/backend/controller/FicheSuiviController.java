package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FicheQualite;
import com.pfe.qualite.backend.model.FicheSuivi;
import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import com.pfe.qualite.backend.repository.FicheSuiviRepository;
import com.pfe.qualite.backend.service.NotificationService;
import com.pfe.qualite.backend.service.MailService;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import com.pfe.qualite.backend.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/suivis")
@CrossOrigin("*")
public class FicheSuiviController {

    @Autowired
    private FicheSuiviRepository ficheSuiviRepository;

    @Autowired
    private FicheQualiteRepository ficheQualiteRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HistoriqueService historiqueService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    // 🔹 GET : toutes les fiches de suivi
    @GetMapping
    public List<FicheSuivi> getAll() {
        return ficheSuiviRepository.findAll();
    }

    // 🔹 GET : par ID
    @GetMapping("/{id}")
    public FicheSuivi getById(@PathVariable String id) {
        return ficheSuiviRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé"));
    }

    // 🔹 GET : par ID de fiche qualité
    @GetMapping("/fiche/{ficheId}")
    public List<FicheSuivi> getByFicheId(@PathVariable String ficheId) {
        return ficheSuiviRepository.findByFicheId(ficheId);
    }

    // 🔹 POST : créer une fiche de suivi
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FicheSuivi ficheSuivi, HttpServletRequest request) {
        // Vérification : la fiche qualité liée existe-t-elle ?
        Optional<FicheQualite> ficheQualite = ficheQualiteRepository.findById(ficheSuivi.getFicheId());

        if (ficheQualite.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : la fiche qualité avec l'ID " + ficheSuivi.getFicheId() + " n'existe pas.");
        }

        FicheSuivi saved = ficheSuiviRepository.save(ficheSuivi);

        // 🔔 Notification automatique
        notificationService.creerNotification(
                "Nouvelle fiche de suivi ajoutée",
                ficheSuivi.getAjoutePar(),     // ID utilisateur
                "FICHE_SUIVI",
                saved.getId()
        );

        // ✉️ Email automatique à l'auteur si email disponible
        try {
            if (ficheSuivi.getAjoutePar() != null) {
                var userOpt = utilisateurRepository.findById(ficheSuivi.getAjoutePar());
                if (userOpt.isPresent() && userOpt.get().getEmail() != null && !userOpt.get().getEmail().isBlank()) {
                    String email = userOpt.get().getEmail();
                    String subject = "Nouvelle fiche de suivi";
                    String body = "Une nouvelle fiche de suivi a été ajoutée (ID: " + saved.getId() + ")";
                    mailService.sendEmail(email, subject, body);
                }
            }
        } catch (Exception e) {
            // on ne bloque pas la création si l'email échoue
        }

        // 📝 Enregistrer dans l'historique
        historiqueService.enregistrerAction(
                "CREATION",
                "FICHE_SUIVI",
                saved.getId(),
                ficheSuivi.getAjoutePar(),
                "Création d'une fiche de suivi pour la fiche qualité : " + ficheSuivi.getFicheId(),
                request
        );

        return ResponseEntity.ok(saved);
    }

    // 🔹 PUT : modifier une fiche de suivi
    @PutMapping("/{id}")
    public FicheSuivi update(@PathVariable String id, @RequestBody FicheSuivi updated, HttpServletRequest request) {
        return ficheSuiviRepository.findById(id).map(fsuivi -> {
            fsuivi.setEtatAvancement(updated.getEtatAvancement());
            fsuivi.setProblemes(updated.getProblemes());
            fsuivi.setDecisions(updated.getDecisions());
            fsuivi.setIndicateursKpi(updated.getIndicateursKpi());
            fsuivi.setTauxConformite(updated.getTauxConformite());
            fsuivi.setDelaiTraitementJours(updated.getDelaiTraitementJours());
            fsuivi.setAjoutePar(updated.getAjoutePar());
            fsuivi.setDateSuivi(updated.getDateSuivi());
            
            FicheSuivi saved = ficheSuiviRepository.save(fsuivi);
            
            // 📝 Enregistrer dans l'historique
            historiqueService.enregistrerAction(
                    "MODIFICATION",
                    "FICHE_SUIVI",
                    saved.getId(),
                    saved.getAjoutePar(),
                    "Modification d'une fiche de suivi pour la fiche qualité : " + saved.getFicheId(),
                    request
            );
            
            return saved;
        }).orElseThrow(() -> new RuntimeException("Suivi non trouvé"));
    }

    // 🔹 DELETE : supprimer une fiche de suivi
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, HttpServletRequest request) {
        ficheSuiviRepository.findById(id).ifPresent(ficheSuivi -> {
            // 📝 Enregistrer dans l'historique avant suppression
            historiqueService.enregistrerAction(
                    "SUPPRESSION",
                    "FICHE_SUIVI",
                    ficheSuivi.getId(),
                    ficheSuivi.getAjoutePar(),
                    "Suppression d'une fiche de suivi pour la fiche qualité : " + ficheSuivi.getFicheId(),
                    request
            );
        });
        
        ficheSuiviRepository.deleteById(id);
    }
}
