package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.service.AiChartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai-charts")
@CrossOrigin(origins = "http://localhost:4200")
public class AiChartsController {

    @Autowired
    private AiChartsService aiChartsService;

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Object>> getTrendData(
            @RequestParam(defaultValue = "8") int period) {
        try {
            Map<String, Object> data = aiChartsService.getTrendData(period);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/predictions")
    public ResponseEntity<Map<String, Object>> getPredictionData() {
        try {
            Map<String, Object> data = aiChartsService.getPredictionData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/kpi")
    public ResponseEntity<Map<String, Object>> getKpiData() {
        try {
            Map<String, Object> data = aiChartsService.getKpiData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/dashboard-data")
    public ResponseEntity<Map<String, Object>> getDashboardData() {
        try {
            Map<String, Object> data = aiChartsService.getDashboardData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/real-trends")
    public ResponseEntity<Map<String, Object>> getRealTrendData(
            @RequestParam(defaultValue = "8") int period) {
        try {
            Map<String, Object> data = aiChartsService.getRealTrendData(period);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/real-predictions")
    public ResponseEntity<Map<String, Object>> getRealPredictionData() {
        try {
            Map<String, Object> data = aiChartsService.getRealPredictionData();
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 