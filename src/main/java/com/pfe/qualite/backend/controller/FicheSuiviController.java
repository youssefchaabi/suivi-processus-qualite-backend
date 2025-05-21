package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FicheQualite;
import com.pfe.qualite.backend.model.FicheSuivi;
import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import com.pfe.qualite.backend.repository.FicheSuiviRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 🔹 GET toutes les fiches de suivi
    @GetMapping
    public List<FicheSuivi> getAll() {
        return ficheSuiviRepository.findAll();
    }

    // 🔹 GET suivi par ID
    @GetMapping("/{id}")
    public FicheSuivi getById(@PathVariable String id) {
        return ficheSuiviRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Suivi non trouvé"));
    }

    // 🔹 GET par fiche projet (ficheId)
    @GetMapping("/fiche/{ficheId}")
    public List<FicheSuivi> getByFicheId(@PathVariable String ficheId) {
        return ficheSuiviRepository.findByFicheId(ficheId);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody FicheSuivi ficheSuivi) {
        // Vérifier si la fiche qualité associée existe
        Optional<FicheQualite> ficheQualite = ficheQualiteRepository.findById(ficheSuivi.getFicheId());

        if (ficheQualite.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Erreur : la fiche qualité avec l'ID " + ficheSuivi.getFicheId() + " n'existe pas.");
        }

        // Si elle existe, enregistrer la fiche de suivi
        FicheSuivi saved = ficheSuiviRepository.save(ficheSuivi);
        return ResponseEntity.ok(saved);
    }


    // 🔹 PUT : modifier une fiche de suivi
    @PutMapping("/{id}")
    public FicheSuivi update(@PathVariable String id, @RequestBody FicheSuivi updated) {
        return ficheSuiviRepository.findById(id).map(fsuivi -> {
            fsuivi.setEtatAvancement(updated.getEtatAvancement());
            fsuivi.setProblemes(updated.getProblemes());
            fsuivi.setDecisions(updated.getDecisions());
            fsuivi.setIndicateursKpi(updated.getIndicateursKpi());
            fsuivi.setAjoutePar(updated.getAjoutePar());
            return ficheSuiviRepository.save(fsuivi);
        }).orElseThrow(() -> new RuntimeException("Suivi non trouvé"));
    }

    // 🔹 DELETE : supprimer une fiche de suivi
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        ficheSuiviRepository.deleteById(id);
    }
}
