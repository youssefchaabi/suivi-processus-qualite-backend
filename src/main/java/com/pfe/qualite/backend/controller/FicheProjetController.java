package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FicheProjet;
import com.pfe.qualite.backend.service.FicheProjetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.Instant;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/projets")
@CrossOrigin("*")
public class FicheProjetController {
    @Autowired
    private FicheProjetService ficheProjetService;

    @GetMapping
    public List<FicheProjet> getAll() {
        return ficheProjetService.getAll();
    }

    @GetMapping("/{id}")
    public FicheProjet getById(@PathVariable String id) {
        return ficheProjetService.getById(id).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
    }

    @PostMapping
    public FicheProjet create(@RequestBody FicheProjet ficheProjet) {
        // Correction robuste : convertir seulement si c'est une chaîne
        Object echeance = ficheProjet.getEcheance();
        if (echeance != null && echeance.getClass() == String.class) {
            try {
                ficheProjet.setEcheance(java.util.Date.from(Instant.parse((String) echeance)));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Format de date d'échéance invalide. Utilisez le format ISO (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ssZ)");
            }
        }
        return ficheProjetService.create(ficheProjet);
    }

    @PutMapping("/{id}")
    public FicheProjet update(@PathVariable String id, @RequestBody FicheProjet updated) {
        // Correction robuste : convertir seulement si c'est une chaîne
        Object echeance = updated.getEcheance();
        if (echeance != null && echeance.getClass() == String.class) {
            try {
                updated.setEcheance(java.util.Date.from(Instant.parse((String) echeance)));
            } catch (DateTimeParseException e) {
                throw new RuntimeException("Format de date d'échéance invalide. Utilisez le format ISO (yyyy-MM-dd ou yyyy-MM-ddTHH:mm:ssZ)");
            }
        }
        return ficheProjetService.update(id, updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        ficheProjetService.delete(id);
    }
} 