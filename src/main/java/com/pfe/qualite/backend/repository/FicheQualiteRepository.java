package com.pfe.qualite.backend.repository;

import com.pfe.qualite.backend.model.FicheQualite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FicheQualiteRepository extends MongoRepository<FicheQualite, String> {

    // ➕ méthodes utiles supplémentaires
    List<FicheQualite> findByCreePar(String creePar);
    List<FicheQualite> findByTypeFiche(String typeFiche);
    List<FicheQualite> findByStatut(String statut);
}
