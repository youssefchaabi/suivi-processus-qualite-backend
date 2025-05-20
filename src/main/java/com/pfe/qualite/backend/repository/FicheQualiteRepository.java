package com.pfe.qualite.backend.repository;

import com.pfe.qualite.backend.model.FicheQualite;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FicheQualiteRepository extends MongoRepository<FicheQualite, String> {

    // Exemple de méthode personnalisée : retrouver les fiches par utilisateur
    List<FicheQualite> findByCreePar(String creePar);

    // Retrouver les fiches par type (audit, amélioration, etc.)
    List<FicheQualite> findByTypeFiche(String typeFiche);

    // Retrouver les fiches par statut (en cours, validée, etc.)
    List<FicheQualite> findByStatut(String statut);
}
