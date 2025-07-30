package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.service.AiAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-analytics")
@CrossOrigin(origins = "*")
public class AiAnalyticsController {

    @Autowired
    private AiAnalyticsService aiAnalyticsService;

    @GetMapping("/risques")
    public ResponseEntity<Map<String, Object>> analyserRisques() {
        try {
            Map<String, Object> result = aiAnalyticsService.analyserRisques();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'analyse des risques: " + e.getMessage()));
        }
    }

    @GetMapping("/recommandations")
    public ResponseEntity<Map<String, Object>> genererRecommandations() {
        try {
            Map<String, Object> result = aiAnalyticsService.genererRecommandations();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de la génération des recommandations: " + e.getMessage()));
        }
    }

    @GetMapping("/tendances")
    public ResponseEntity<Map<String, Object>> analyserTendances() {
        try {
            Map<String, Object> result = aiAnalyticsService.analyserTendances();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'analyse des tendances: " + e.getMessage()));
        }
    }

    @GetMapping("/optimisations")
    public ResponseEntity<Map<String, Object>> optimiserProcessus() {
        try {
            Map<String, Object> result = aiAnalyticsService.optimiserProcessus();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de l'optimisation des processus: " + e.getMessage()));
        }
    }

    @GetMapping("/rapport")
    public ResponseEntity<Map<String, Object>> genererRapportIA() {
        try {
            Map<String, Object> result = aiAnalyticsService.genererRapportIA();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de la génération du rapport IA: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> dashboardData = Map.of(
                "risques", aiAnalyticsService.analyserRisques(),
                "recommandations", aiAnalyticsService.genererRecommandations(),
                "tendances", aiAnalyticsService.analyserTendances(),
                "optimisations", aiAnalyticsService.optimiserProcessus()
            );
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erreur lors de la récupération des données du dashboard: " + e.getMessage()));
        }
    }
} 