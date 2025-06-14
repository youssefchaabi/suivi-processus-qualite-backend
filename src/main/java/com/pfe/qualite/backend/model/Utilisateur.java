package com.pfe.qualite.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {
    @Id
    private String id;
    private String email;
    private String password;
    private String nom;
    private String role; // ADMIN, CHEF_PROJET, PILOTE_QUALITE
}
