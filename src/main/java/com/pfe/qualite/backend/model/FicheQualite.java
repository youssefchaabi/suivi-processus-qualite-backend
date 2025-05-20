package com.pfe.qualite.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "fiches_qualite")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheQualite {

    @Id
    private String id;

    private String titre;

    private String description;

    private String typeFiche;  // ex : AUDIT, EVALUATION... (de nomenclature TYPE_FICHE)

    private String statut;     // ex : EN_COURS, VALIDEE (de nomenclature STATUT)

    private Date dateCreation;

    private String creePar;    // id ou nom de l'utilisateur
}
