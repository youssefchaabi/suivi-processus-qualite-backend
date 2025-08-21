package com.pfe.qualite.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.qualite.backend.model.HistoriqueAction;
import com.pfe.qualite.backend.repository.HistoriqueActionRepository;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class HistoriqueService {

    @Autowired
    private HistoriqueActionRepository historiqueRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Enregistre une action dans l'historique
     */
    public void enregistrerAction(String action, String entite, String entiteId, String utilisateurId, 
                                 String details, Object anciennesValeurs, Object nouvellesValeurs, 
                                 HttpServletRequest request) {
        try {
            HistoriqueAction historique = HistoriqueAction.builder()
                    .action(action)
                    .entite(entite)
                    .entiteId(entiteId)
                    .utilisateurId(utilisateurId)
                    .utilisateurNom(getNomUtilisateur(utilisateurId))
                    .details(details)
                    .dateAction(new Date())
                    .anciennesValeurs(anciennesValeurs != null ? objectMapper.writeValueAsString(anciennesValeurs) : null)
                    .nouvellesValeurs(nouvellesValeurs != null ? objectMapper.writeValueAsString(nouvellesValeurs) : null)
                    .ipAdresse(getClientIpAddress(request))
                    .userAgent(request != null ? request.getHeader("User-Agent") : null)
                    .build();

            historiqueRepository.save(historique);
        } catch (JsonProcessingException e) {
            // Log l'erreur mais ne pas faire échouer l'opération principale
            System.err.println("Erreur lors de la sérialisation JSON pour l'historique: " + e.getMessage());
        }
    }

    /**
     * Enregistre une action simple
     */
    public void enregistrerAction(String action, String entite, String entiteId, String utilisateurId, 
                                 String details, HttpServletRequest request) {
        enregistrerAction(action, entite, entiteId, utilisateurId, details, null, null, request);
    }

    /**
     * Récupère l'historique d'un utilisateur
     */
    public List<HistoriqueAction> getHistoriqueUtilisateur(String utilisateurId) {
        return historiqueRepository.findByUtilisateurIdOrderByDateActionDesc(utilisateurId);
    }

    /**
     * Récupère l'historique d'une entité
     */
    public List<HistoriqueAction> getHistoriqueEntite(String entite, String entiteId) {
        return historiqueRepository.findByEntiteAndEntiteIdOrderByDateActionDesc(entite, entiteId);
    }

    /**
     * Récupère l'historique par période
     */
    public List<HistoriqueAction> getHistoriqueParPeriode(Date dateDebut, Date dateFin) {
        return historiqueRepository.findByDateActionBetweenOrderByDateActionDesc(dateDebut, dateFin);
    }

    /**
     * Récupère l'historique d'un utilisateur par période
     */
    public List<HistoriqueAction> getHistoriqueUtilisateurParPeriode(String utilisateurId, Date dateDebut, Date dateFin) {
        return historiqueRepository.findByUtilisateurIdAndDateActionBetweenOrderByDateActionDesc(utilisateurId, dateDebut, dateFin);
    }

    /**
     * Récupère l'historique d'une entité par période
     */
    public List<HistoriqueAction> getHistoriqueEntiteParPeriode(String entite, Date dateDebut, Date dateFin) {
        return historiqueRepository.findByEntiteAndDateActionBetweenOrderByDateActionDesc(entite, dateDebut, dateFin);
    }

    /**
     * Récupère les statistiques d'actions
     */
    public long getNombreActionsUtilisateur(String utilisateurId) {
        return historiqueRepository.countByUtilisateurId(utilisateurId);
    }

    public long getNombreActionsEntite(String entite) {
        return historiqueRepository.countByEntite(entite);
    }

    /**
     * Récupère le nom d'un utilisateur
     */
    private String getNomUtilisateur(String utilisateurId) {
        if (utilisateurId == null) return "Système";
        
        Optional<com.pfe.qualite.backend.model.Utilisateur> utilisateur = utilisateurRepository.findById(utilisateurId);
        return utilisateur.map(u -> u.getNom()).orElse("Utilisateur inconnu");
    }

    /**
     * Récupère l'adresse IP du client
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return "N/A";
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
} 