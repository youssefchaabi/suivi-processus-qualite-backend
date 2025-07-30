package com.pfe.qualite.backend.service;

import com.pfe.qualite.backend.model.FicheQualite;
import com.pfe.qualite.backend.model.FicheSuivi;
import com.pfe.qualite.backend.model.FicheProjet;
import com.pfe.qualite.backend.repository.FicheQualiteRepository;
import com.pfe.qualite.backend.repository.FicheSuiviRepository;
import com.pfe.qualite.backend.repository.FicheProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiAnalyticsService {

    @Autowired
    private FicheQualiteRepository ficheQualiteRepository;

    @Autowired
    private FicheSuiviRepository ficheSuiviRepository;

    @Autowired
    private FicheProjetRepository ficheProjetRepository;

    // Analyse prédictive des risques
    public Map<String, Object> analyserRisques() {
        Map<String, Object> result = new HashMap<>();
        List<FicheQualite> fichesQualite = ficheQualiteRepository.findAll();
        List<FicheSuivi> fichesSuivi = ficheSuiviRepository.findAll();

        List<Map<String, Object>> predictions = new ArrayList<>();

        // Analyse du taux de conformité
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = fichesQualite.isEmpty() ? 0 : (double) fichesTerminees / fichesQualite.size() * 100;

        if (tauxConformite < 70) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("niveau", "CRITIQUE");
            prediction.put("probabilite", 0.85);
            prediction.put("description", "Taux de conformité très faible détecté");
            prediction.put("recommandations", Arrays.asList(
                "Réviser les processus qualité",
                "Former les équipes aux bonnes pratiques",
                "Mettre en place un suivi renforcé"
            ));
            prediction.put("impact", "Impact majeur sur la réputation et la conformité");
            predictions.add(prediction);
        } else if (tauxConformite < 85) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("niveau", "ÉLEVÉ");
            prediction.put("probabilite", 0.65);
            prediction.put("description", "Taux de conformité en dessous des objectifs");
            prediction.put("recommandations", Arrays.asList(
                "Identifier les causes de non-conformité",
                "Renforcer les contrôles qualité",
                "Améliorer la communication"
            ));
            prediction.put("impact", "Impact modéré sur l'efficacité");
            predictions.add(prediction);
        }

        // Analyse des fiches en retard
        long fichesEnRetard = fichesQualite.stream()
                .filter(f -> "EN_COURS".equals(f.getStatut()))
                .count();
        if (fichesEnRetard > fichesQualite.size() * 0.3) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("niveau", "ÉLEVÉ");
            prediction.put("probabilite", 0.75);
            prediction.put("description", "Trop de fiches en cours de traitement");
            prediction.put("recommandations", Arrays.asList(
                "Prioriser les fiches urgentes",
                "Allouer plus de ressources",
                "Optimiser les processus de traitement"
            ));
            prediction.put("impact", "Risque de retard généralisé");
            predictions.add(prediction);
        }

        result.put("predictions", predictions);
        result.put("tauxConformite", tauxConformite);
        result.put("fichesEnRetard", fichesEnRetard);

        return result;
    }

    // Génération de recommandations intelligentes
    public Map<String, Object> genererRecommandations() {
        Map<String, Object> result = new HashMap<>();
        List<FicheQualite> fichesQualite = ficheQualiteRepository.findAll();
        List<FicheSuivi> fichesSuivi = ficheSuiviRepository.findAll();
        List<FicheProjet> fichesProjet = ficheProjetRepository.findAll();

        List<Map<String, Object>> recommandations = new ArrayList<>();

        // Analyse des métriques
        long totalFiches = fichesQualite.size();
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = totalFiches > 0 ? (double) fichesTerminees / totalFiches * 100 : 0;

        // Recommandations basées sur le taux de conformité
        if (tauxConformite < 70) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "URGENT");
            recommandation.put("titre", "Amélioration critique du taux de conformité");
            recommandation.put("description", "Le taux de conformité est très faible et nécessite une action immédiate");
            recommandation.put("priorite", 1);
            recommandation.put("actions", Arrays.asList(
                "Audit complet des processus qualité",
                "Formation intensive des équipes",
                "Mise en place de contrôles renforcés",
                "Révision des procédures"
            ));
            recommandation.put("impactAttendu", "Amélioration de 20-30% du taux de conformité");
            recommandation.put("delaiEstime", "2-3 semaines");
            recommandations.add(recommandation);
        }

        // Recommandations basées sur les fiches de suivi
        if (fichesSuivi.isEmpty()) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "IMPORTANT");
            recommandation.put("titre", "Mise en place du suivi qualité");
            recommandation.put("description", "Aucune fiche de suivi n'existe, essentiel pour le contrôle qualité");
            recommandation.put("priorite", 2);
            recommandation.put("actions", Arrays.asList(
                "Créer des fiches de suivi pour toutes les fiches qualité",
                "Former les équipes au suivi qualité",
                "Établir des points de contrôle réguliers"
            ));
            recommandation.put("impactAttendu", "Amélioration du contrôle et de la traçabilité");
            recommandation.put("delaiEstime", "1-2 semaines");
            recommandations.add(recommandation);
        }

        // Recommandations d'optimisation
        if (totalFiches > 10) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("type", "SUGGESTION");
            recommandation.put("titre", "Optimisation des processus qualité");
            recommandation.put("description", "Opportunité d'améliorer l'efficacité des processus");
            recommandation.put("priorite", 3);
            recommandation.put("actions", Arrays.asList(
                "Automatiser les tâches répétitives",
                "Standardiser les procédures",
                "Mettre en place des indicateurs de performance"
            ));
            recommandation.put("impactAttendu", "Réduction de 15-25% du temps de traitement");
            recommandation.put("delaiEstime", "3-4 semaines");
            recommandations.add(recommandation);
        }

        result.put("recommandations", recommandations);
        result.put("totalFiches", totalFiches);
        result.put("tauxConformite", tauxConformite);

        return result;
    }

    // Analyse des tendances
    public Map<String, Object> analyserTendances() {
        Map<String, Object> result = new HashMap<>();
        List<FicheQualite> fichesQualite = ficheQualiteRepository.findAll();

        List<Map<String, Object>> tendances = new ArrayList<>();

        // Simulation d'analyse de tendances
        long totalFiches = fichesQualite.size();
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = totalFiches > 0 ? (double) fichesTerminees / totalFiches * 100 : 0;

        // Tendance du taux de conformité
        Map<String, Object> tendance = new HashMap<>();
        tendance.put("periode", "Ce mois");
        if (tauxConformite > 85) {
            tendance.put("tendance", "HAUSSE");
            tendance.put("variation", 5.2);
            tendance.put("explication", "Amélioration continue des processus qualité");
        } else if (tauxConformite < 70) {
            tendance.put("tendance", "BAISSE");
            tendance.put("variation", -8.5);
            tendance.put("explication", "Dégradation des performances qualité");
        } else {
            tendance.put("tendance", "STABLE");
            tendance.put("variation", 0.3);
            tendance.put("explication", "Performance stable mais amélioration possible");
        }
        tendance.put("valeur", tauxConformite);
        tendances.add(tendance);

        // Tendance du volume de travail
        if (totalFiches > 20) {
            Map<String, Object> tendanceVolume = new HashMap<>();
            tendanceVolume.put("periode", "Ce mois");
            tendanceVolume.put("tendance", "HAUSSE");
            tendanceVolume.put("valeur", totalFiches);
            tendanceVolume.put("variation", 15.0);
            tendanceVolume.put("explication", "Augmentation de l'activité qualité");
            tendances.add(tendanceVolume);
        }

        result.put("tendances", tendances);
        result.put("totalFiches", totalFiches);
        result.put("tauxConformite", tauxConformite);

        return result;
    }

    // Optimisation des processus
    public Map<String, Object> optimiserProcessus() {
        Map<String, Object> result = new HashMap<>();
        List<FicheQualite> fichesQualite = ficheQualiteRepository.findAll();
        List<FicheSuivi> fichesSuivi = ficheSuiviRepository.findAll();

        List<Map<String, Object>> optimisations = new ArrayList<>();

        // Analyse de l'efficacité des processus
        long totalFiches = fichesQualite.size();
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double efficaciteActuelle = totalFiches > 0 ? (double) fichesTerminees / totalFiches * 100 : 0;

        // Optimisation du processus de validation
        Map<String, Object> optimisation = new HashMap<>();
        optimisation.put("processus", "Validation des fiches qualité");
        optimisation.put("efficaciteActuelle", efficaciteActuelle);
        optimisation.put("efficaciteOptimale", 95.0);
        optimisation.put("gainsPotentiels", Arrays.asList(
            "Réduction de 30% du temps de validation",
            "Amélioration de 25% de la précision",
            "Réduction de 40% des erreurs"
        ));
        optimisation.put("actionsOptimisation", Arrays.asList(
            "Automatiser les contrôles de base",
            "Standardiser les critères de validation",
            "Former les validateurs aux nouvelles procédures",
            "Mettre en place un système de validation en cascade"
        ));
        optimisation.put("delaiImplementation", "4-6 semaines");
        optimisations.add(optimisation);

        // Optimisation du suivi qualité
        if (!fichesSuivi.isEmpty()) {
            Map<String, Object> optimisationSuivi = new HashMap<>();
            optimisationSuivi.put("processus", "Suivi qualité");
            optimisationSuivi.put("efficaciteActuelle", 75.0);
            optimisationSuivi.put("efficaciteOptimale", 90.0);
            optimisationSuivi.put("gainsPotentiels", Arrays.asList(
                "Amélioration de 20% de la traçabilité",
                "Réduction de 35% du temps de suivi",
                "Amélioration de 30% de la réactivité"
            ));
            optimisationSuivi.put("actionsOptimisation", Arrays.asList(
                "Mettre en place des alertes automatiques",
                "Créer des tableaux de bord temps réel",
                "Automatiser les rapports de suivi",
                "Former les pilotes qualité aux nouveaux outils"
            ));
            optimisationSuivi.put("delaiImplementation", "3-4 semaines");
            optimisations.add(optimisationSuivi);
        }

        result.put("optimisations", optimisations);
        result.put("efficaciteActuelle", efficaciteActuelle);

        return result;
    }

    // Génération de rapports IA
    public Map<String, Object> genererRapportIA() {
        Map<String, Object> rapport = new HashMap<>();
        List<FicheQualite> fichesQualite = ficheQualiteRepository.findAll();
        List<FicheSuivi> fichesSuivi = ficheSuiviRepository.findAll();
        List<FicheProjet> fichesProjet = ficheProjetRepository.findAll();

        // Résumé
        Map<String, Object> resume = new HashMap<>();
        resume.put("totalFiches", fichesQualite.size());
        resume.put("totalSuivis", fichesSuivi.size());
        resume.put("totalProjets", fichesProjet.size());
        
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = fichesQualite.isEmpty() ? 0 : (double) fichesTerminees / fichesQualite.size() * 100;
        resume.put("tauxConformite", tauxConformite);

        rapport.put("dateGeneration", new Date());
        rapport.put("resume", resume);
        rapport.put("alertes", genererAlertes(fichesQualite, fichesSuivi));
        rapport.put("predictions", genererPredictions(fichesQualite));
        rapport.put("recommandations", genererRecommandationsRapides(fichesQualite, fichesSuivi));

        return rapport;
    }

    private List<Map<String, Object>> genererAlertes(List<FicheQualite> fichesQualite, List<FicheSuivi> fichesSuivi) {
        List<Map<String, Object>> alertes = new ArrayList<>();
        
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = fichesQualite.isEmpty() ? 0 : (double) fichesTerminees / fichesQualite.size() * 100;
        
        if (tauxConformite < 70) {
            Map<String, Object> alerte = new HashMap<>();
            alerte.put("niveau", "CRITIQUE");
            alerte.put("message", "Taux de conformité critique");
            alerte.put("description", "Le taux de conformité est de " + String.format("%.1f", tauxConformite) + "%, en dessous du seuil de 70%");
            alertes.add(alerte);
        }

        long fichesEnRetard = fichesQualite.stream()
                .filter(f -> "EN_COURS".equals(f.getStatut()))
                .count();
        if (fichesEnRetard > fichesQualite.size() * 0.3) {
            Map<String, Object> alerte = new HashMap<>();
            alerte.put("niveau", "ATTENTION");
            alerte.put("message", "Trop de fiches en cours");
            alerte.put("description", fichesEnRetard + " fiches en cours de traitement");
            alertes.add(alerte);
        }

        return alertes;
    }

    private List<Map<String, Object>> genererPredictions(List<FicheQualite> fichesQualite) {
        List<Map<String, Object>> predictions = new ArrayList<>();
        
        long fichesTerminees = fichesQualite.stream()
                .filter(f -> "TERMINE".equals(f.getStatut()))
                .count();
        double tauxConformite = fichesQualite.isEmpty() ? 0 : (double) fichesTerminees / fichesQualite.size() * 100;
        
        if (tauxConformite < 80) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("type", "RISQUE");
            prediction.put("description", "Risque de dégradation du taux de conformité dans les 30 prochains jours");
            prediction.put("probabilite", 0.75);
            prediction.put("actions", Arrays.asList("Renforcer les contrôles", "Former les équipes", "Réviser les processus"));
            predictions.add(prediction);
        }

        return predictions;
    }

    private List<Map<String, Object>> genererRecommandationsRapides(List<FicheQualite> fichesQualite, List<FicheSuivi> fichesSuivi) {
        List<Map<String, Object>> recommandations = new ArrayList<>();
        
        if (fichesSuivi.isEmpty()) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("priorite", "HAUTE");
            recommandation.put("action", "Créer des fiches de suivi");
            recommandation.put("raison", "Aucune fiche de suivi n'existe");
            recommandations.add(recommandation);
        }

        long fichesEnCours = fichesQualite.stream()
                .filter(f -> "EN_COURS".equals(f.getStatut()))
                .count();
        if (fichesEnCours > 5) {
            Map<String, Object> recommandation = new HashMap<>();
            recommandation.put("priorite", "MOYENNE");
            recommandation.put("action", "Prioriser les fiches en cours");
            recommandation.put("raison", fichesEnCours + " fiches en cours nécessitent un suivi");
            recommandations.add(recommandation);
        }

        return recommandations;
    }
} 