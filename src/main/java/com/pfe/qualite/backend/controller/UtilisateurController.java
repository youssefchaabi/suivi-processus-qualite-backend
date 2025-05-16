package com.pfe.qualite.backend.controller;

import com.pfe.qualite.backend.model.Utilisateur;
import com.pfe.qualite.backend.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

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
    public Utilisateur create(@RequestBody Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @PutMapping("/{id}")
    public Utilisateur update(@PathVariable String id, @RequestBody Utilisateur updatedUser) {
        return utilisateurRepository.findById(id).map(user -> {
            user.setNom(updatedUser.getNom());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            return utilisateurRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        utilisateurRepository.deleteById(id);
    }


}
