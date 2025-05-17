package com.pfe.qualite.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nomenclatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nomenclature {
    @Id
    private String id;
    private String type;     // Exemple : STATUT, TYPE_FICHE
    private String valeur;   // Exemple : EN_COURS, VALIDEE, REJETEE
}
