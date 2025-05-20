package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FicheQualite;
import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/fiches")
@CrossOrigin("*")
public class FicheQualiteController {

    @Autowired
    private FicheQualiteRepository ficheRepository;

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
    public FicheQualite create(@RequestBody FicheQualite fiche) {
        fiche.setDateCreation(new Date());
        return ficheRepository.save(fiche);
    }

    // 🔹 PUT : modifier
    @PutMapping("/{id}")
    public FicheQualite update(@PathVariable String id, @RequestBody FicheQualite updated) {
        return ficheRepository.findById(id).map(fiche -> {
            fiche.setTitre(updated.getTitre());
            fiche.setDescription(updated.getDescription());
            fiche.setTypeFiche(updated.getTypeFiche());
            fiche.setStatut(updated.getStatut());
            return ficheRepository.save(fiche);
        }).orElseThrow(() -> new RuntimeException("Fiche non trouvée"));
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        ficheRepository.deleteById(id);
    }
}
