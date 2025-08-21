package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.service.RapportKpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/rapports-kpi")
@CrossOrigin(origins = "*")
public class RapportKpiController {

    @Autowired
    private RapportKpiService rapportKpiService;

    /**
     * Générer un rapport KPI complet
     */
    @GetMapping("/complet")
    public ResponseEntity<Map<String, Object>> genererRapportKpiComplet() {
        Map<String, Object> rapport = rapportKpiService.genererRapportKpiComplet();
        return ResponseEntity.ok(rapport);
    }

    /**
     * Générer un rapport KPI par période
     */
    @GetMapping("/periode")
    public ResponseEntity<Map<String, Object>> genererRapportParPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        Map<String, Object> rapport = rapportKpiService.genererRapportParPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(rapport);
    }

    /**
     * Télécharger le rapport KPI en JSON
     */
    @GetMapping("/telecharger")
    public ResponseEntity<Map<String, Object>> telechargerRapportKpi() {
        Map<String, Object> rapport = rapportKpiService.genererRapportKpiComplet();
        return ResponseEntity.ok(rapport);
    }

    /**
     * Télécharger le rapport KPI par période en JSON
     */
    @GetMapping("/telecharger/periode")
    public ResponseEntity<Map<String, Object>> telechargerRapportParPeriode(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateDebut,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFin) {
        Map<String, Object> rapport = rapportKpiService.genererRapportParPeriode(dateDebut, dateFin);
        return ResponseEntity.ok(rapport);
    }
} 