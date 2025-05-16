package com.pfe.qualite.backend.repository;

import com.pfe.qualite.backend.model.Utilisateur;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UtilisateurRepository extends MongoRepository<Utilisateur, String> {
    // tu pourras ajouter des méthodes personnalisées ici plus tard
}
