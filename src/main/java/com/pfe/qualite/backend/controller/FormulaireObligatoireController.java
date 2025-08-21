package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.FormulaireObligatoire;
import com.pfe.qualite.backend.service.FormulaireObligatoireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formulaires-obligatoires")
@CrossOrigin(origins = "*")
public class FormulaireObligatoireController {

    @Autowired
    private FormulaireObligatoireService formulaireService;

    /**
     * Créer un nouveau formulaire obligatoire
     */
    @PostMapping
    public ResponseEntity<FormulaireObligatoire> creerFormulaireObligatoire(@RequestBody FormulaireObligatoire formulaire) {
        FormulaireObligatoire saved = formulaireService.creerFormulaireObligatoire(formulaire);
        return ResponseEntity.ok(saved);
    }

    /**
     * Récupérer tous les formulaires obligatoires
     */
    @GetMapping
    public ResponseEntity<List<FormulaireObligatoire>> getAllFormulairesObligatoires() {
        List<FormulaireObligatoire> formulaires = formulaireService.getAllFormulairesObligatoires();
        return ResponseEntity.ok(formulaires);
    }

    /**
     * Récupérer les formulaires d'un responsable
     */
    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesByResponsable(@PathVariable String responsableId) {
        List<FormulaireObligatoire> formulaires = formulaireService.getFormulairesByResponsable(responsableId);
        return ResponseEntity.ok(formulaires);
    }

    /**
     * Récupérer les formulaires d'un projet
     */
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesByProjet(@PathVariable String projetId) {
        List<FormulaireObligatoire> formulaires = formulaireService.getFormulairesByProjet(projetId);
        return ResponseEntity.ok(formulaires);
    }

    /**
     * Récupérer les formulaires en retard
     */
    @GetMapping("/retards")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesEnRetard() {
        List<FormulaireObligatoire> retards = formulaireService.getFormulairesEnRetard();
        return ResponseEntity.ok(retards);
    }

    /**
     * Récupérer les formulaires à échéance proche
     */
    @GetMapping("/echeances-proches")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesEcheanceProche() {
        List<FormulaireObligatoire> echeances = formulaireService.getFormulairesEcheanceProche();
        return ResponseEntity.ok(echeances);
    }

    /**
     * Récupérer les formulaires par statut
     */
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesByStatut(@PathVariable String statut) {
        List<FormulaireObligatoire> formulaires = formulaireService.getFormulairesByStatut(statut);
        return ResponseEntity.ok(formulaires);
    }

    /**
     * Récupérer les formulaires par priorité
     */
    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<List<FormulaireObligatoire>> getFormulairesByPriorite(@PathVariable String priorite) {
        List<FormulaireObligatoire> formulaires = formulaireService.getFormulairesByPriorite(priorite);
        return ResponseEntity.ok(formulaires);
    }

    /**
     * Marquer un formulaire comme soumis
     */
    @PutMapping("/{id}/soumis")
    public ResponseEntity<FormulaireObligatoire> marquerCommeSoumis(@PathVariable String id) {
        FormulaireObligatoire formulaire = formulaireService.marquerCommeSoumis(id);
        return ResponseEntity.ok(formulaire);
    }

    /**
     * Marquer un formulaire comme en retard
     */
    @PutMapping("/{id}/retard")
    public ResponseEntity<FormulaireObligatoire> marquerCommeEnRetard(@PathVariable String id) {
        FormulaireObligatoire formulaire = formulaireService.marquerCommeEnRetard(id);
        return ResponseEntity.ok(formulaire);
    }

    /**
     * Mettre à jour un formulaire obligatoire
     */
    @PutMapping("/{id}")
    public ResponseEntity<FormulaireObligatoire> updateFormulaireObligatoire(
            @PathVariable String id, 
            @RequestBody FormulaireObligatoire updated) {
        FormulaireObligatoire formulaire = formulaireService.updateFormulaireObligatoire(id, updated);
        return ResponseEntity.ok(formulaire);
    }

    /**
     * Supprimer un formulaire obligatoire
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormulaireObligatoire(@PathVariable String id) {
        formulaireService.deleteFormulaireObligatoire(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Vérifier et notifier les retards (appelé par le scheduler)
     */
    @PostMapping("/verifier-retards")
    public ResponseEntity<String> verifierEtNotifierRetards() {
        formulaireService.verifierEtNotifierRetards();
        return ResponseEntity.ok("Vérification des retards effectuée");
    }

    /**
     * Vérifier et notifier les échéances proches (appelé par le scheduler)
     */
    @PostMapping("/verifier-echeances")
    public ResponseEntity<String> verifierEtNotifierEcheancesProches() {
        formulaireService.verifierEtNotifierEcheancesProches();
        return ResponseEntity.ok("Vérification des échéances effectuée");
    }

    /**
     * Obtenir les statistiques
     */
    @GetMapping("/stats/retards")
    public ResponseEntity<Long> getNombreFormulairesEnRetard() {
        long nombre = formulaireService.getNombreFormulairesEnRetard();
        return ResponseEntity.ok(nombre);
    }

    @GetMapping("/stats/statut/{statut}")
    public ResponseEntity<Long> getNombreFormulairesByStatut(@PathVariable String statut) {
        long nombre = formulaireService.getNombreFormulairesByStatut(statut);
        return ResponseEntity.ok(nombre);
    }

    @GetMapping("/stats/responsable/{responsableId}")
    public ResponseEntity<Long> getNombreFormulairesByResponsable(@PathVariable String responsableId) {
        long nombre = formulaireService.getNombreFormulairesByResponsable(responsableId);
        return ResponseEntity.ok(nombre);
    }
} 