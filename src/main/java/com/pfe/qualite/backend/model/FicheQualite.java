package com.pfe.qualite.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private String typeFiche;
    private String statut;
    private String responsable;
    private String commentaire;
}
