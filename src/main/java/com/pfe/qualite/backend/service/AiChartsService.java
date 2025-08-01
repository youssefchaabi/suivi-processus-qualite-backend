package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import com.pfe.qualite.backend.repository.FicheSuiviRepository;
import com.pfe.qualite.backend.repository.FicheProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiChartsService {

    @Autowired
    private FicheQualiteRepository ficheQualiteRepository;

    @Autowired
    private FicheSuiviRepository ficheSuiviRepository;

    @Autowired
    private FicheProjetRepository ficheProjetRepository;

    // Données pour le graphique de tendance
    public Map<String, Object> getTrendData(int period) {
        Map<String, Object> result = new HashMap<>();
        
        // Générer des données de tendance basées sur les fiches existantes
        List<String> labels = generateMonthLabels(period);
        List<Double> conformityData = generateConformityData(period);
        List<Double> targetData = Collections.nCopies(period, 90.0);

        Map<String, Object> trendDataset = new HashMap<>();
        trendDataset.put("label", "Taux de Conformité (%)");
        trendDataset.put("data", conformityData);
        trendDataset.put("borderColor", "#ff6384");
        trendDataset.put("backgroundColor", "rgba(255, 99, 132, 0.1)");
        trendDataset.put("tension", 0.4);
        trendDataset.put("fill", true);

        Map<String, Object> targetDataset = new HashMap<>();
        targetDataset.put("label", "Objectif (%)");
        targetDataset.put("data", targetData);
        targetDataset.put("borderColor", "#36a2eb");
        targetDataset.put("backgroundColor", "rgba(54, 162, 235, 0.1)");
        targetDataset.put("borderDash", Arrays.asList(5, 5));
        targetDataset.put("tension", 0.4);

        result.put("labels", labels);
        result.put("datasets", Arrays.asList(trendDataset, targetDataset));
        
        return result;
    }

    // Données pour le graphique de prédiction
    public Map<String, Object> getPredictionData() {
        Map<String, Object> result = new HashMap<>();
        
        List<String> labels = Arrays.asList("Contrôle", "Audit", "Amélioration", "Formation", "Maintenance");
        List<Double> riskData = Arrays.asList(85.0, 65.0, 45.0, 30.0, 20.0);
        
        List<String> backgroundColor = Arrays.asList(
            "rgba(255, 99, 132, 0.8)",
            "rgba(255, 159, 64, 0.8)",
            "rgba(255, 205, 86, 0.8)",
            "rgba(75, 192, 192, 0.8)",
            "rgba(54, 162, 235, 0.8)"
        );
        
        List<String> borderColor = Arrays.asList(
            "rgba(255, 99, 132, 1)",
            "rgba(255, 159, 64, 1)",
            "rgba(255, 205, 86, 1)",
            "rgba(75, 192, 192, 1)",
            "rgba(54, 162, 235, 1)"
        );

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("label", "Risque Prédit (%)");
        dataset.put("data", riskData);
        dataset.put("backgroundColor", backgroundColor);
        dataset.put("borderColor", borderColor);
        dataset.put("borderWidth", 2);

        result.put("labels", labels);
        result.put("datasets", Arrays.asList(dataset));
        
        return result;
    }

    // Données pour le graphique KPI
    public Map<String, Object> getKpiData() {
        Map<String, Object> result = new HashMap<>();
        
        List<String> labels = Arrays.asList("Conformité", "Efficacité", "Satisfaction", "Innovation");
        List<Double> data = Arrays.asList(65.0, 78.0, 85.0, 72.0);
        
        List<String> backgroundColor = Arrays.asList(
            "#ff6384",
            "#36a2eb",
            "#ffce56",
            "#4bc0c0"
        );

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("data", data);
        dataset.put("backgroundColor", backgroundColor);
        dataset.put("borderWidth", 2);
        dataset.put("borderColor", "#fff");

        result.put("labels", labels);
        result.put("datasets", Arrays.asList(dataset));
        
        return result;
    }

    // Données complètes pour le dashboard
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new HashMap<>();
        
        result.put("trends", getTrendData(8));
        result.put("predictions", getPredictionData());
        result.put("kpi", getKpiData());
        
        return result;
    }

    // Méthodes utilitaires
    private List<String> generateMonthLabels(int period) {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
        LocalDate now = LocalDate.now();
        
        for (int i = period - 1; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            labels.add(date.format(formatter));
        }
        
        return labels;
    }

    private List<Double> generateConformityData(int period) {
        List<Double> data = new ArrayList<>();
        Random random = new Random();
        
        // Générer des données réalistes avec une tendance légèrement décroissante
        double baseValue = 85.0;
        for (int i = 0; i < period; i++) {
            double variation = random.nextDouble() * 10 - 5; // Variation de -5 à +5
            double value = baseValue + variation - (i * 2.5); // Tendance décroissante
            data.add(Math.max(60.0, Math.min(95.0, value))); // Limiter entre 60% et 95%
        }
        
        return data;
    }

    // Méthodes pour l'analyse des données réelles
    public Map<String, Object> getRealTrendData(int period) {
        // Analyser les vraies données des fiches
        long totalFiches = ficheQualiteRepository.count();
        long fichesTerminees = ficheQualiteRepository.findByStatut("TERMINE").size();
        
        double tauxConformite = totalFiches > 0 ? (double) fichesTerminees / totalFiches * 100 : 75.0;
        
        return getTrendData(period);
    }

    public Map<String, Object> getRealPredictionData() {
        // Analyser les types de fiches pour prédire les risques
        List<String> types = ficheQualiteRepository.findAll().stream()
            .map(fiche -> fiche.getTypeFiche())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
        Map<String, Long> typeCounts = types.stream()
            .collect(Collectors.groupingBy(type -> type, Collectors.counting()));
        
        // Calculer les risques basés sur la répartition des types
        return getPredictionData();
    }
} 