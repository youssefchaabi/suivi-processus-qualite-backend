package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Nomenclature;
import com.pfe.qualite.backend.service.NomenclatureService;
import com.pfe.qualite.backend.service.HistoriqueService;
import com.pfe.qualite.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des nomenclatures (ADMIN uniquement)
 * Utilise NomenclatureService pour la logique métier
 */
@RestController
@RequestMapping("/api/nomenclatures")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class NomenclatureController {

    private final NomenclatureService nomenclatureService;
    private final HistoriqueService historiqueService;
    private final JwtUtil jwtUtil;

    /**
     * Récupère toutes les nomenclatures
     */
    @GetMapping
    public ResponseEntity<List<Nomenclature>> getAll() {
        List<Nomenclature> nomenclatures = nomenclatureService.getAllNomenclatures();
        return ResponseEntity.ok(nomenclatures);
    }

    /**
     * Récupère une nomenclature par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Nomenclature> getById(@PathVariable String id) {
        return nomenclatureService.getNomenclatureById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les nomenclatures par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Nomenclature>> getByType(@PathVariable String type) {
        List<Nomenclature> nomenclatures = nomenclatureService.getNomenclaturesByType(type);
        return ResponseEntity.ok(nomenclatures);
    }

    /**
     * Récupère tous les types de nomenclatures distincts
     */
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllTypes() {
        List<String> types = nomenclatureService.getAllTypes();
        return ResponseEntity.ok(types);
    }

    /**
     * Crée une nouvelle nomenclature
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Nomenclature> create(
            @RequestBody Nomenclature nomenclature,
            HttpServletRequest request) {
        try {
            log.info("POST /api/nomenclatures - Création nomenclature: {} - {}", 
                nomenclature.getType(), nomenclature.getCode());
            
            Nomenclature savedNomenclature = nomenclatureService.createNomenclature(nomenclature);
            
            // Historique
            historiqueService.enregistrerAction(
                    "CREATION",
                    "NOMENCLATURE",
                    savedNomenclature.getId(),
                    jwtUtil.extractUserIdFromRequest(request),
                    "Création nomenclature: " + savedNomenclature.getType() + " - " + savedNomenclature.getLibelle(),
                    request
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNomenclature);
        } catch (IllegalArgumentException e) {
            log.error("Erreur validation nomenclature", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Met à jour une nomenclature existante
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Nomenclature> update(
            @PathVariable String id,
            @RequestBody Nomenclature updated,
            HttpServletRequest request) {
        try {
            log.info("PUT /api/nomenclatures/{} - Mise à jour nomenclature", id);
            
            Nomenclature updatedNomenclature = nomenclatureService.updateNomenclature(id, updated);
            
            // Historique
            historiqueService.enregistrerAction(
                    "MODIFICATION",
                    "NOMENCLATURE",
                    updatedNomenclature.getId(),
                    jwtUtil.extractUserIdFromRequest(request),
                    "Modification nomenclature: " + updatedNomenclature.getType() + " - " + updatedNomenclature.getLibelle(),
                    request
            );
            
            return ResponseEntity.ok(updatedNomenclature);
        } catch (IllegalArgumentException e) {
            log.error("Erreur validation nomenclature", e);
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            log.error("Nomenclature non trouvée", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprime une nomenclature
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            HttpServletRequest request) {
        try {
            log.info("DELETE /api/nomenclatures/{} - Suppression nomenclature", id);
            
            // Récupérer la nomenclature avant suppression pour l'historique
            String userId = jwtUtil.extractUserIdFromRequest(request);
            nomenclatureService.getNomenclatureById(id).ifPresent(nom -> {
                historiqueService.enregistrerAction(
                        "SUPPRESSION",
                        "NOMENCLATURE",
                        nom.getId(),
                        userId,
                        "Suppression nomenclature: " + nom.getType() + " - " + nom.getLibelle(),
                        request
                );
            });
            
            nomenclatureService.deleteNomenclature(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Erreur suppression nomenclature", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Compte le nombre total de nomenclatures
     */
    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        long count = nomenclatureService.countAllNomenclatures();
        return ResponseEntity.ok(count);
    }

    /**
     * Compte les nomenclatures par type
     */
    @GetMapping("/count/type/{type}")
    public ResponseEntity<Long> countByType(@PathVariable String type) {
        long count = nomenclatureService.countNomenclaturesByType(type);
        return ResponseEntity.ok(count);
    }

    /**
     * Initialise les nomenclatures par défaut
     */
    @PostMapping("/init-defaults")
    public ResponseEntity<Void> initializeDefaults() {
        nomenclatureService.initializeDefaultNomenclatures();
        return ResponseEntity.ok().build();
    }
}
