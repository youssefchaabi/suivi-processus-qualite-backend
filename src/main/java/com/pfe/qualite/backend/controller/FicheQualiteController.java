package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FicheQualite;
import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import com.pfe.qualite.backend.service.NotificationService;
import com.pfe.qualite.backend.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/fiches")
@CrossOrigin("*")
public class FicheQualiteController {

    @Autowired
    private FicheQualiteRepository ficheRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HistoriqueService historiqueService;

    // 🔹 GET all
    @GetMapping
    public List<FicheQualite> getAll() {
        return ficheRepository.findAll();
    }

    // 🔹 GET par ID
    @GetMapping("/{id}")
    public FicheQualite getById(@PathVariable String id) {
        return ficheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche non trouvée"));
    }

    // 🔹 POST : créer une fiche
    @PostMapping
    public FicheQualite create(@RequestBody FicheQualite fiche, HttpServletRequest request) {
        FicheQualite saved = ficheRepository.save(fiche);

        // 🛎️ Notification automatique
        notificationService.creerNotification(
                "Nouvelle fiche qualité ajoutée",
                fiche.getResponsable(),
                "FICHE_QUALITE",
                saved.getId()
        );

        // 📝 Enregistrer dans l'historique
        historiqueService.enregistrerAction(
                "CREATION",
                "FICHE_QUALITE",
                saved.getId(),
                fiche.getResponsable(),
                "Création de la fiche qualité : " + saved.getTitre(),
                request
        );

        return saved;
    }

    // 🔹 PUT : modifier une fiche
    @PutMapping("/{id}")
    public FicheQualite update(@PathVariable String id, @RequestBody FicheQualite updated, HttpServletRequest request) {
        return ficheRepository.findById(id).map(fiche -> {
            fiche.setTitre(updated.getTitre());
            fiche.setDescription(updated.getDescription());
            fiche.setTypeFiche(updated.getTypeFiche());
            fiche.setStatut(updated.getStatut());
            fiche.setResponsable(updated.getResponsable());
            fiche.setCommentaire(updated.getCommentaire());
            FicheQualite updatedFiche = ficheRepository.save(fiche);

            // 🛎️ Notification automatique
            notificationService.creerNotification(
                    "Fiche qualité mise à jour",
                    fiche.getResponsable(),
                    "FICHE_QUALITE",
                    fiche.getId()
            );

            // 📝 Enregistrer dans l'historique
            historiqueService.enregistrerAction(
                    "MODIFICATION",
                    "FICHE_QUALITE",
                    fiche.getId(),
                    fiche.getResponsable(),
                    "Modification de la fiche qualité : " + fiche.getTitre(),
                    request
            );

            return updatedFiche;
        }).orElseThrow(() -> new RuntimeException("Fiche non trouvée"));
    }

    // 🔹 DELETE : supprimer une fiche
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, HttpServletRequest request) {
        ficheRepository.findById(id).ifPresent(fiche -> {
            // 📝 Enregistrer dans l'historique avant suppression
            historiqueService.enregistrerAction(
                    "SUPPRESSION",
                    "FICHE_QUALITE",
                    fiche.getId(),
                    fiche.getResponsable(),
                    "Suppression de la fiche qualité : " + fiche.getTitre(),
                    request
            );
        });
        
        ficheRepository.deleteById(id);
    }
}
