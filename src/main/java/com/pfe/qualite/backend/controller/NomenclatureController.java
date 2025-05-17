package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Nomenclature;
import com.pfe.qualite.backend.repository.NomenclatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nomenclatures")
@CrossOrigin("*")
public class NomenclatureController {

    @Autowired
    private NomenclatureRepository nomenclatureRepository;

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
    public Nomenclature create(@RequestBody Nomenclature nom) {
        return nomenclatureRepository.save(nom);
    }

    // 🔹 PUT : modifier une nomenclature
    @PutMapping("/{id}")
    public Nomenclature update(@PathVariable String id, @RequestBody Nomenclature updated) {
        return nomenclatureRepository.findById(id).map(nom -> {
            nom.setType(updated.getType());
            nom.setValeur(updated.getValeur());
            return nomenclatureRepository.save(nom);
        }).orElseThrow(() -> new RuntimeException("Nomenclature non trouvée"));
    }

    // 🔹 DELETE : supprimer
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        nomenclatureRepository.deleteById(id);
    }
}
