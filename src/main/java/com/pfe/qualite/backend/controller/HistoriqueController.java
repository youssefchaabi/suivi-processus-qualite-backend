package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.HistoriqueAction;
import com.pfe.qualite.backend.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/historique")
@CrossOrigin(origins = "*")
public class HistoriqueController {

    @Autowired
    private HistoriqueService historiqueService;

    /**
     * Récupère l'historique d'un utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueUtilisateur(@PathVariable String utilisateurId) {
        List<HistoriqueAction> historique = historiqueService.getHistoriqueUtilisateur(utilisateurId);
        return ResponseEntity.ok(historique);
    }

    /**
     * Récupère l'historique d'une entité
     */
    @GetMapping("/entite/{entite}/{entiteId}")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueEntite(
            @PathVariable String entite, 
            @PathVariable String entiteId) {
        List<HistoriqueAction> historique = historiqueService.getHistoriqueEntite(entite, entiteId);
        return ResponseEntity.ok(historique);
    }

    /**
     * Récupère l'historique par période
     */
    @GetMapping("/periode")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueParPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        List<HistoriqueAction> historique = historiqueService.getHistoriqueParPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(historique);
    }

    /**
     * Récupère l'historique d'un utilisateur par période
     */
    @GetMapping("/utilisateur/{utilisateurId}/periode")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueUtilisateurParPeriode(
            @PathVariable String utilisateurId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        List<HistoriqueAction> historique = historiqueService.getHistoriqueUtilisateurParPeriode(utilisateurId, dateDebut, dateFin);
        return ResponseEntity.ok(historique);
    }

    /**
     * Récupère l'historique d'une entité par période
     */
    @GetMapping("/entite/{entite}/periode")
    public ResponseEntity<List<HistoriqueAction>> getHistoriqueEntiteParPeriode(
            @PathVariable String entite,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        List<HistoriqueAction> historique = historiqueService.getHistoriqueEntiteParPeriode(entite, dateDebut, dateFin);
        return ResponseEntity.ok(historique);
    }

    /**
     * Récupère les statistiques d'actions d'un utilisateur
     */
    @GetMapping("/stats/utilisateur/{utilisateurId}")
    public ResponseEntity<Long> getNombreActionsUtilisateur(@PathVariable String utilisateurId) {
        long nombre = historiqueService.getNombreActionsUtilisateur(utilisateurId);
        return ResponseEntity.ok(nombre);
    }

    /**
     * Récupère les statistiques d'actions d'une entité
     */
    @GetMapping("/stats/entite/{entite}")
    public ResponseEntity<Long> getNombreActionsEntite(@PathVariable String entite) {
        long nombre = historiqueService.getNombreActionsEntite(entite);
        return ResponseEntity.ok(nombre);
    }
} 