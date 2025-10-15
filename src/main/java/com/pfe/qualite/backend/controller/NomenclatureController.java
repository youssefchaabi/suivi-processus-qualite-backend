package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Nomenclature;
import com.pfe.qualite.backend.repository.NomenclatureRepository;
import com.pfe.qualite.backend.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/nomenclatures")
@CrossOrigin("*")
public class NomenclatureController {

    @Autowired
    private NomenclatureRepository nomenclatureRepository;

    @Autowired
    private HistoriqueService historiqueService;

    // 🔹 GET : récupérer toutes les nomenclatures
    @GetMapping
    public List<Nomenclature> getAll() {
        return nomenclatureRepository.findAll();
    }

    // 🔹 GET par type (ex: STATUT, TYPE_FICHE, etc.)
    @GetMapping("/type/{type}")
    public List<Nomenclature> getByType(@PathVariable String type) {
        return nomenclatureRepository.findByType(type);
    }

    // 🔹 POST : créer une nouvelle nomenclature
    @PostMapping
    public Nomenclature create(@RequestBody Nomenclature nom, HttpServletRequest request) {
        Nomenclature saved = nomenclatureRepository.save(nom);
        historiqueService.enregistrerAction(
                "CREATION",
                "NOMENCLATURE",
                saved.getId(),
                null,
                "Création de la nomenclature " + saved.getType() + ": " + saved.getValeur(),
                request
        );
        return saved;
    }

    // 🔹 PUT : modifier une nomenclature
    @PutMapping("/{id}")
    public Nomenclature update(@PathVariable String id, @RequestBody Nomenclature updated, HttpServletRequest request) {
        return nomenclatureRepository.findById(id).map(nom -> {
            nom.setType(updated.getType());
            nom.setValeur(updated.getValeur());
            Nomenclature saved = nomenclatureRepository.save(nom);
            historiqueService.enregistrerAction(
                    "MODIFICATION",
                    "NOMENCLATURE",
                    saved.getId(),
                    null,
                    "Modification de la nomenclature " + saved.getType() + ": " + saved.getValeur(),
                    request
            );
            return saved;
        }).orElseThrow(() -> new RuntimeException("Nomenclature non trouvée"));
    }

    // 🔹 DELETE : supprimer
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, HttpServletRequest request) {
        nomenclatureRepository.findById(id).ifPresent(nom -> {
            historiqueService.enregistrerAction(
                    "SUPPRESSION",
                    "NOMENCLATURE",
                    nom.getId(),
                    null,
                    "Suppression de la nomenclature " + nom.getType() + ": " + nom.getValeur(),
                    request
            );
        });
        nomenclatureRepository.deleteById(id);
    }
}
