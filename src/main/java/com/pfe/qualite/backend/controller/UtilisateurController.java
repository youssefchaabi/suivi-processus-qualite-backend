package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Utilisateur;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import com.pfe.qualite.backend.service.HistoriqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private HistoriqueService historiqueService;

    @GetMapping
    public List<Utilisateur> getAll() {
        return utilisateurRepository.findAll();
    }

    @GetMapping("/{id}")
    public Utilisateur getById(@PathVariable String id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));
    }

    @PostMapping
    public Utilisateur create(@RequestBody Utilisateur utilisateur, HttpServletRequest request) {
        Utilisateur saved = utilisateurRepository.save(utilisateur);
        // Historique: création utilisateur
        historiqueService.enregistrerAction(
                "CREATION",
                "UTILISATEUR",
                saved.getId(),
                saved.getId(),
                "Création de l'utilisateur: " + saved.getNom(),
                request
        );
        return saved;
    }

    @PutMapping("/{id}")
    public Utilisateur update(@PathVariable String id, @RequestBody Utilisateur updatedUser, HttpServletRequest request) {
        return utilisateurRepository.findById(id).map(user -> {
            user.setNom(updatedUser.getNom());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            Utilisateur saved = utilisateurRepository.save(user);
            // Historique: modification utilisateur
            historiqueService.enregistrerAction(
                    "MODIFICATION",
                    "UTILISATEUR",
                    saved.getId(),
                    saved.getId(),
                    "Modification de l'utilisateur: " + saved.getNom(),
                    request
            );
            return saved;
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id, HttpServletRequest request) {
        utilisateurRepository.findById(id).ifPresent(user -> {
            // Historique: suppression utilisateur
            historiqueService.enregistrerAction(
                    "SUPPRESSION",
                    "UTILISATEUR",
                    user.getId(),
                    user.getId(),
                    "Suppression de l'utilisateur: " + user.getNom(),
                    request
            );
        });
        utilisateurRepository.deleteById(id);
    }
}
